#!/bin/bash

# Step 1: Update system and install dependencies
echo "Updating package list..."
sudo apt update -y && sudo apt upgrade -y || { echo "❌ Failed to update system"; exit 1; }

echo "Installing required tools (curl, unzip, git, docker.io, docker-compose, openjdk-21-jdk)..."
sudo apt install -y curl unzip git docker.io docker-compose openjdk-21-jdk || { echo "❌ Failed to install required tools"; exit 1; }

# Step 2: Install AWS CLI if not already installed
if ! command -v aws &> /dev/null; then
    echo "AWS CLI not found. Installing AWS CLI..."
    curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip" || { echo "❌ Failed to download AWS CLI"; exit 1; }
    unzip awscliv2.zip || { echo "❌ Failed to unzip AWS CLI package"; exit 1; }
    sudo ./aws/install || { echo "❌ Failed to install AWS CLI"; exit 1; }
    echo "✅ AWS CLI installed successfully."
else
    echo "✅ AWS CLI is already installed."
fi

# Step 3: Start Docker service and enable it
echo "Enabling Docker..."
sudo systemctl start docker
sudo systemctl enable docker

# Step 4: Clone Aspect-henaApp repo from GitHub
echo "Cloning Aspect-henaApp repository..."
cd /home/ubuntu || exit 1
git clone https://github.com/Sarah-Mazhar/Aspect-henaApp.git || { echo "❌ Failed to clone repository"; exit 1; }
cd Aspect-henaApp || exit 1

# Step 5: Start supporting services (MySQL, Redis, phpMyAdmin) using docker-compose
echo "Starting Docker containers using docker-compose..."
sudo docker-compose up -d || { echo "❌ Docker Compose failed"; exit 1; }

# Step 6: Fetch the public IP of the frontend instance from AWS (used for CORS config)
echo "Fetching frontend public IP using AWS CLI and instance tag in eu-west-1..."
frontend_public_ip=$(aws ec2 describe-instances \
  --region eu-west-1 \
  --filters "Name=tag:Name,Values=frontend" \
           "Name=instance-state-name,Values=running" \
  --query "Reservations[0].Instances[0].PublicIpAddress" \
  --output text)

# Fallback in case no IP is retrieved
if [ -z "$frontend_public_ip" ] || [ "$frontend_public_ip" == "None" ]; then
  frontend_public_ip="Unavailable"
fi

# Step 7: Patch backend SecurityConfig.java to allow CORS from actual frontend IP
echo "Replacing localhost with $frontend_public_ip in SecurityConfig.java"
sudo sed -i "s|http://localhost|http://$frontend_public_ip|g" src/main/java/com/example/hena/security/SecurityConfig.java

# Step 8: Build the Spring Boot JAR (skip tests to save time)
echo "Building Spring Boot JAR..."
chmod +x ./mvnw
export HOME=/home/ubuntu
./mvnw clean package -DskipTests || { echo "❌ Maven build failed"; exit 1; }

# Step 9: Find the generated JAR file inside target/
JAR_FILE=$(find target -name "*.jar" | head -n 1)
if [ -z "$JAR_FILE" ]; then
  echo "❌ JAR file not found in target directory."
  exit 1
fi

# Step 10: Create a systemd service unit to run the Spring Boot app on system boot
SERVICE_NAME=hena-backend
SERVICE_PATH=/etc/systemd/system/${SERVICE_NAME}.service

echo "Creating systemd service for Spring Boot backend..."
echo "[Unit]
Description=Spring Boot Backend Service
After=network.target docker.service
Requires=docker.service

[Service]
User=ubuntu
ExecStart=/usr/lib/jvm/java-21-openjdk-amd64/bin/java -jar /home/ubuntu/Aspect-henaApp/$JAR_FILE
SuccessExitStatus=143
Restart=always
RestartSec=5

[Install]
WantedBy=multi-user.target" | sudo tee "$SERVICE_PATH" > /dev/null

# Step 11: Reload systemd, enable the service to run at boot, and start it now
sudo systemctl daemon-reload
sudo systemctl enable "$SERVICE_NAME"
sudo systemctl start "$SERVICE_NAME"

# Step 12: Final deployment summary
echo "------------------------------------------------------------"
echo "✅ Spring Boot backend setup complete with MySQL, Redis, and phpMyAdmin via Docker."
echo "✅ Backend JAR built and running as a systemd service: $SERVICE_NAME"
echo "🚀 Ready to test your backend at port 8080!"
echo "🌐 Access your frontend at: http://$frontend_public_ip"
echo "------------------------------------------------------------"

#!/bin/bash

# Step 1: Update system and install dependencies
echo "Updating package list..."
sudo apt update -y && sudo apt upgrade -y || { echo "❌ Failed to update system"; exit 1; }

echo "Installing required tools (curl, unzip, git, docker.io, docker-compose, openjdk-21-jdk)..."
sudo apt install -y curl unzip git docker.io docker-compose openjdk-21-jdk || { echo "❌ Failed to install required tools"; exit 1; }

# Step 2: Start Docker service and enable it
echo "Enabling Docker..."
sudo systemctl start docker
sudo systemctl enable docker

# Step 3: Clone Aspect-henaApp repo
echo "Cloning Aspect-henaApp repository..."
cd /home/ubuntu || exit 1
git clone https://github.com/Sarah-Mazhar/Aspect-henaApp.git || { echo "❌ Failed to clone repository"; exit 1; }
cd Aspect-henaApp || exit 1

# Step 4: Start Docker containers
echo "Starting Docker containers using docker-compose..."
sudo docker-compose up -d || { echo "❌ Docker Compose failed"; exit 1; }

# Step 5: Build Spring Boot JAR
echo "Building Spring Boot JAR..."
chmod +x ./mvnw
export HOME=/home/ubuntu
./mvnw clean package -DskipTests || { echo "❌ Maven build failed"; exit 1; }

JAR_FILE=$(find target -name "*.jar" | head -n 1)

if [ -z "$JAR_FILE" ]; then
  echo "❌ JAR file not found in target directory."
  exit 1
fi

# Step 6: Create systemd service for the backend app
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

# Reload systemd and start service
sudo systemctl daemon-reload
sudo systemctl enable "$SERVICE_NAME"
sudo systemctl start "$SERVICE_NAME"

# Final summary
echo "------------------------------------------------------------"
echo "✅ Spring Boot backend setup complete with MySQL, Redis, and phpMyAdmin via Docker."
echo "✅ Backend JAR built and running as a systemd service: $SERVICE_NAME"
echo "🚀 Ready to test your backend at port 8080!"
echo "------------------------------------------------------------"

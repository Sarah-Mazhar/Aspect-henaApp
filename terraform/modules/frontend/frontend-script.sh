#!/bin/bash

# Step 0: Use Terraform-injected backend public IP
backend_public_ip="${backend_public_ip}"

# Step 1: Update package lists and install required tools
echo "Updating package list..."
sudo apt-get update -y || { echo "Failed to update package list"; exit 1; }

echo "Installing required tools (curl, unzip, git, npm, apache2)..."
sudo apt-get install -y curl unzip git npm apache2 || { echo "Failed to install required tools"; exit 1; }

# Install AWS CLI if not already installed
if ! command -v aws &> /dev/null; then
    echo "AWS CLI not found. Installing AWS CLI..."
    curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip" || { echo "Failed to download AWS CLI"; exit 1; }
    unzip awscliv2.zip || { echo "Failed to unzip AWS CLI package"; exit 1; }
    sudo ./aws/install || { echo "Failed to install AWS CLI"; exit 1; }
    echo "AWS CLI installed successfully."
else
    echo "AWS CLI is already installed."
fi

# Step 2: Clone the Aspect-henaApp repository
echo "Cloning the Aspect-henaApp repository..."
cd /home/ubuntu || { echo "Failed to navigate to home directory"; exit 1; }
git clone https://github.com/Sarah-Mazhar/Aspect-henaApp.git || { echo "Failed to clone repository"; exit 1; }
cd Aspect-henaApp || { echo "Failed to enter Aspect-henaApp directory"; exit 1; }

# Step 3: Replace localhost with backend public IP
echo "Replacing 'localhost' with backend IP: $backend_public_ip"
find ./frontend -type f -exec sed -i "s/localhost/$backend_public_ip/g" {} +

# Step 4: Install frontend dependencies and build
cd frontend || { echo "Failed to enter frontend directory"; exit 1; }

echo "Installing frontend Node.js dependencies..."
npm install || { echo "Failed to install Node.js dependencies"; exit 1; }
npm install react-icons
echo "Building the React frontend..."
npm run build || { echo "Failed to build frontend"; exit 1; }

# Step 5: Deploy frontend to Apache web root
echo "Deploying built frontend to Apache server..."
sudo cp -r dist/* /var/www/html/ || { echo "Failed to copy frontend files to Apache directory"; exit 1; }

# Step 6: Restart Apache to serve latest build
echo "Restarting Apache server..."
sudo systemctl restart apache2
sudo systemctl enable apache2

# Step 7: Fetch the public IP of the frontend instance using AWS CLI
echo "Fetching frontend public IP using AWS CLI and instance tag in eu-west-1..."
frontend_public_ip=$(aws ec2 describe-instances \
  --region eu-west-1 \
  --filters "Name=tag:Name,Values=frontend" \
           "Name=instance-state-name,Values=running" \
  --query "Reservations[0].Instances[0].PublicIpAddress" \
  --output text)

if [ -z "$frontend_public_ip" ] || [ "$frontend_public_ip" == "None" ]; then
  frontend_public_ip="Unavailable"
fi

# Step 8: Output the frontend public IP and backend private IP
echo "------------------------------------------------------------"
echo "🎯 Frontend deployment complete!"
echo ""
echo "✅ Aspect-henaApp frontend built and served via Apache"
echo "✅ Connected to backend at: http://$backend_public_ip:8080"
echo "🌐 Access your frontend at: http://$frontend_public_ip"
echo "------------------------------------------------------------"
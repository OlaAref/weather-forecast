#!/bin/bash

# Ensure the script itself is executable
chmod +x "$0"

#create mysql image
docker --debug build -t mysql-db .

# Check if image build was successful
if [ $? -ne 0 ]; then
  echo "Error building Docker image. Exiting."
  exit 1
fi

#create mysql container
docker --debug run -d --name mysql-container -p 3306:3306 mysql-db

# Check if container is running
if [ $? -eq 0 ]; then
  echo "Container is running successfully."
else
  echo "Error running the Docker container."
  exit 1
fi
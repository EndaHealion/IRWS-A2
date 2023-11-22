#!/bin/bash

# Install Java 21, Maven, and Makefile
echo "Installing Java, Maven, and Makefile..."
sudo apt update -y
sudo apt install openjdk-21-jdk -y
sudo apt install maven -y
sudo apt install make -y

# Install pip, gdown, and unzip
echo "Installing pip, gdown, and unzip..."
sudo apt install python3-pip -y
pip install gdown
sudo apt install unzip -y

# Add gdown to PATH
echo 'export PATH="$HOME/.local/bin:$PATH"' >> ~/.bashrc
source ~/.bashrc

# Download and setup dataset
echo "Downloading and setting up the dataset..."
chmod +x dataset.sh
./dataset.sh

# Run Makefile commands
echo "Building and running the project..."
make

# Script ends
echo "Setup and execution complete."

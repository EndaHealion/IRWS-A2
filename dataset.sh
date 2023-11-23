#!/bin/bash

# Google Drive file ID
FILE_ID="17KpMCaE34eLvdiTINqj1lmxSBSu8BtDP"
# The name of the file to download
FILE_NAME="Assignment Two.zip"
# The name of the directory that will be created after extraction
FOLDER_NAME="Assignment Two"
# The target directory where the contents will be moved
TARGET_DIR="./resources/dataset/"

# Install gdown if you don't have it (you may need sudo privileges)
# pip install gdown or sudo pip install gdown

# Download the file using gdown
gdown "https://drive.google.com/uc?id=${FILE_ID}" -O "${FILE_NAME}"

# Check if the file has been downloaded successfully
if [ -f "${FILE_NAME}" ]; then
    # Unzip the file
    unzip -q "${FILE_NAME}"

    # Check if the folder has been extracted successfully
    if [ -d "${FOLDER_NAME}" ]; then
        # Create the target directory if it does not exist
        mkdir -p "${TARGET_DIR}"
        # Move the files
        mv "${FOLDER_NAME}"/* "${TARGET_DIR}"
        # Remove the zip file and empty folder
        rm -rf "${FILE_NAME}" "${FOLDER_NAME}"
        echo "Files have been successfully downloaded and moved to ${TARGET_DIR}"
    else
        echo "Extraction failed, folder ${FOLDER_NAME} does not exist."
    fi
else
    echo "Download failed, file ${FILE_NAME} does not exist."
fi

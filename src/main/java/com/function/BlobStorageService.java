package com.function;

import com.azure.core.util.BinaryData;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;

public class BlobStorageService {
    private final BlobServiceClient blobServiceClient;

    public BlobStorageService(BlobServiceClient blobServiceClient) {
        this.blobServiceClient = blobServiceClient;
    }

    public boolean updateBlobIfExist(String containerName, String blobName, String data) {
        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);
        containerClient.createIfNotExists();

        BlobClient blobClient = containerClient.getBlobClient(blobName);

        // Check if the blob already exists
        if (blobClient.exists()) {
            // If it exists, update the blob with new data
            blobClient.upload(BinaryData.fromString(data), true); // Overwrite the existing blob
            return true; // Indicate that the update was successful
        } else {
            // Blob doesn't exist, hence create it
            blobClient.upload(BinaryData.fromString(data));
            return false; // Indicate that a new blob was created
        }
    }
}

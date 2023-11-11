package com.function;

import com.azure.core.util.BinaryData;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;


import java.time.LocalDateTime;
import java.util.Optional;


public class OrderItemsReserver {
    private final BlobStorageService blobStorageService;

    public OrderItemsReserver() {
        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                .connectionString(System.getenv("AZURE_STORAGE_CONNECTION_STRING"))
                .buildClient();
        this.blobStorageService = new BlobStorageService(blobServiceClient);
    }

    @FunctionName("UpdateShoppingCart")
    public HttpResponseMessage run(
            @HttpTrigger(
                    name = "req",
                    methods = {HttpMethod.POST},
                    authLevel = AuthorizationLevel.ANONYMOUS)
            HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {

        context.getLogger().info("OrderItemsReserver - Processing shopping cart update.");

        // Check if the request has a body (orderJSON payload)
        if (request.getBody().isPresent()) {
            String orderJSON = request.getBody().get(); // Get the orderJSON from the request body

            // Implement logic to process the orderJSON here
            context.getLogger().info("Received orderJSON: " + orderJSON);

            // Use the received orderJSON to update Blob Storage
            boolean updated = blobStorageService.updateBlobIfExist("order-details", "order-" + System.currentTimeMillis() + ".json", orderJSON);

            // Build the response based on the update status
            if (updated) {
                return request.createResponseBuilder(HttpStatus.OK).body("Shopping cart updated successfully.").build();
            } else {
                return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update shopping cart.").build();
            }
        } else {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("No orderJSON found in the request.").build();
        }
    }
}

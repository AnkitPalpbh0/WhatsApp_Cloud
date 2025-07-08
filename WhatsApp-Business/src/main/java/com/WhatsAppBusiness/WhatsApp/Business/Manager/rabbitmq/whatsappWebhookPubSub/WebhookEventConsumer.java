package com.WhatsAppBusiness.WhatsApp.Business.Manager.rabbitmq.whatsappWebhookPubSub;

import com.WhatsAppBusiness.WhatsApp.Business.Manager.rabbitmq.RabbitMQConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class WebhookEventConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebhookEventConsumer.class);

    private final ObjectMapper objectMapper;

    public WebhookEventConsumer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME, containerFactory = "rabbitListenerContainerFactory")
    public void receiveBulkProductData(String messageBody) {
//        ReadableBulkUploadRequest bulkUploadResponse = null;
//        try {
//            LOGGER.info("Received bulk upload message");
//            // Deserialize the JSON string to ReadableBulkUploadProductResponse
//            bulkUploadResponse = objectMapper.readValue(messageBody, ReadableBulkUploadRequest.class);
//            LOGGER.info("Successfully deserialized bulk upload request: {}", bulkUploadResponse);
//            processBulkUploadProducts(bulkUploadResponse);
//            LOGGER.info("Successfully processed bulk upload for modelId: {}, manufactureId: {}",
//                    bulkUploadResponse.getModelId(), bulkUploadResponse.getManufactureId());
//        } catch (Exception e) {
//            LOGGER.error("Error processing bulk upload data: {}", e.getMessage(), e);
//            if (bulkUploadResponse != null) {
//                LOGGER.error("Bulk upload processing failed. Response data could not be fully processed.");
//                // Optional: Implement any rollback logic here if needed
//            }
//        }
    }

}


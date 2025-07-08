package com.WhatsAppBusiness.WhatsApp.Business.Manager.rabbitmq.whatsappWebhookPubSub;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;


@Service
public class WenhookEventProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(WenhookEventProducer.class);

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public WenhookEventProducer(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }
//
//    public void sendBulkProductData(ReadableBulkUploadRequest request) {
//        try {
//            // Encode file data to Base64
//            String messageBody = objectMapper.writeValueAsString(request);
//            rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, RabbitMQConfig.ROUTING_KEY, messageBody);
//            LOGGER.info("Successfully sent bulk product data to RabbitMQ.");
//        } catch (Exception e) {
//            LOGGER.error("Error occurred while sending bulk product data to RabbitMQ. Error: {}", e.getMessage());
//            LOGGER.debug("Stack trace: ", e);
//        }
//    }
}

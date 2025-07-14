package com.WhatsAppBusiness.WhatsApp.Business.Manager.rabbitmq.whatsappmediapubsub;

import com.WhatsAppBusiness.WhatsApp.Business.DTOs.MediaRequest;
import com.WhatsAppBusiness.WhatsApp.Business.Manager.rabbitmq.RabbitMQConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class MediaEventProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(MediaEventProducer.class);

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public MediaEventProducer(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }
    public void sendMediaMessage(MediaRequest mediaRequest) {
        LOGGER.info("Sending media event to queue.");
        try {
            // Encode file data to Base64
            String messageBody = objectMapper.writeValueAsString(mediaRequest);
            rabbitTemplate.convertAndSend(RabbitMQConfig.MEDIA_EXCHANGE_NAME, RabbitMQConfig.MEDIA_ROUTING_KEY, messageBody);
            LOGGER.info("Successfully sent data to RabbitMQ.");
        } catch (Exception e) {
            LOGGER.error("Failed to send data to RabbitMQ.", e);
            LOGGER.debug("Stack trace: ", e);
        }
        LOGGER.info("Sent media event to queue.");
    }
}
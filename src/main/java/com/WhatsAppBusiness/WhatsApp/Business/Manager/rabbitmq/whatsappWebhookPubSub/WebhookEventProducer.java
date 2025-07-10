package com.WhatsAppBusiness.WhatsApp.Business.Manager.rabbitmq.whatsappWebhookPubSub;

import com.WhatsAppBusiness.WhatsApp.Business.DTOs.WebhookResponse;
import com.WhatsAppBusiness.WhatsApp.Business.Manager.rabbitmq.RabbitMQConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;


@Service
public class WebhookEventProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebhookEventProducer.class);

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public WebhookEventProducer(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    public void sendMessage(WebhookResponse webhookResponse) {
        try {
            // Encode file data to Base64
            String messageBody = objectMapper.writeValueAsString(webhookResponse);
            rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, RabbitMQConfig.ROUTING_KEY, messageBody);
            LOGGER.info("Successfully sent data to RabbitMQ.");
        } catch (Exception e) {
            LOGGER.error("Failed to send data to RabbitMQ.", e);
            LOGGER.debug("Stack trace: ", e);
        }
    }

}

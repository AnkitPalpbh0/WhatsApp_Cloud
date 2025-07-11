package com.WhatsAppBusiness.WhatsApp.Business.Manager.rabbitmq.whatsappWebhookPubSub;

import com.WhatsAppBusiness.WhatsApp.Business.DTOs.WebhookResponse;
import com.WhatsAppBusiness.WhatsApp.Business.Manager.rabbitmq.RabbitMQConfig;
import com.WhatsAppBusiness.WhatsApp.Business.Model.Chat;
import com.WhatsAppBusiness.WhatsApp.Business.Model.Message;
import com.WhatsAppBusiness.WhatsApp.Business.Model.Users;
import com.WhatsAppBusiness.WhatsApp.Business.Repository.ChatRepository;
import com.WhatsAppBusiness.WhatsApp.Business.Repository.MessageRepository;
import com.WhatsAppBusiness.WhatsApp.Business.Repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.checkerframework.checker.units.qual.A;
import org.checkerframework.checker.units.qual.N;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class WebhookEventConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebhookEventConsumer.class);

    private final ObjectMapper objectMapper;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private UserRepository userRepository;

    public WebhookEventConsumer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME, containerFactory = "rabbitListenerContainerFactory")
    public void receiveMessage(String messageBody) {
        WebhookResponse webhookResponse = null;
        try {
            LOGGER.info("Received webhook message");
            // Deserialize the JSON string to ReadableBulkUploadProductResponse
            webhookResponse = objectMapper.readValue(messageBody, WebhookResponse.class);
            LOGGER.info("Successfully deserialized webhook request: {}", webhookResponse);
            Message message = messageRepository.findByMessageId(webhookResponse.getMessageId());
            if (message != null) {
                LOGGER.info("Updating message status for messageId: {}", webhookResponse.getMessageId());
                message.setStatus(webhookResponse.getStatus());
                messageRepository.save(message);
            } else {

                Chat chat = chatRepository.findByChatNumberEndingWith(webhookResponse.getSenderNumber());
                LOGGER.info("Chat: {}", chat);
                if (chat == null) {
                    return;
                }
                Message newMessage = new Message();
                newMessage.setChat(chat);
                newMessage.setContent(webhookResponse.getContent());
                newMessage.setMessageId(webhookResponse.getMessageId());
                newMessage.setStatus(webhookResponse.getStatus());
                newMessage.setMessageType("text");
                newMessage.setTimestamp(LocalDateTime.now());
                messageRepository.save(newMessage);
                LOGGER.info("Successfully saved message for messageId: {}", webhookResponse.getMessageId());
            }
            LOGGER.info("Successfully processed webhook for messageId: {}, status: {}",
                    webhookResponse.getMessageId(), webhookResponse.getStatus());
        } catch (Exception e) {
            LOGGER.error("Error processing webhook data: {}", e.getMessage(), e);
            if (webhookResponse != null) {
                LOGGER.error("Webhook processing failed. Response data could not be fully processed.");
            }
        }
    }

}


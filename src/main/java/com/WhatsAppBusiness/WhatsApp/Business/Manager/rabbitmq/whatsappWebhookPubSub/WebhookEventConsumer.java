package com.WhatsAppBusiness.WhatsApp.Business.Manager.rabbitmq.whatsappWebhookPubSub;

import com.WhatsAppBusiness.WhatsApp.Business.DTOs.WebhookResponse;
import com.WhatsAppBusiness.WhatsApp.Business.Manager.rabbitmq.RabbitMQConfig;
import com.WhatsAppBusiness.WhatsApp.Business.Model.Chat;
import com.WhatsAppBusiness.WhatsApp.Business.Model.MediaMetadata;
import com.WhatsAppBusiness.WhatsApp.Business.Model.Message;
import com.WhatsAppBusiness.WhatsApp.Business.Model.Users;
import com.WhatsAppBusiness.WhatsApp.Business.Repository.ChatRepository;
import com.WhatsAppBusiness.WhatsApp.Business.Repository.MediaMetaDataRepository;
import com.WhatsAppBusiness.WhatsApp.Business.Repository.MessageRepository;
import com.WhatsAppBusiness.WhatsApp.Business.Repository.UserRepository;
import com.WhatsAppBusiness.WhatsApp.Business.Service.S3Service;
import com.WhatsAppBusiness.WhatsApp.Business.Utils.WhatsappSdkConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Collections;

import static com.WhatsAppBusiness.WhatsApp.Business.Constants.WhatsappCloudApisUrls.BASE_URL;

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

    @Autowired
    private MediaMetaDataRepository mediaMetaDataRepository;

    @Autowired
    private WhatsappSdkConfig whatsappSdkConfig;

    @Autowired
    private S3Service s3Service;

    public WebhookEventConsumer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME, containerFactory = "rabbitListenerContainerFactory")
    public void receiveMessage(String messageBody) {
        WebhookResponse webhookResponse = null;
        try {
            LOGGER.info("Received webhook message");
            webhookResponse = objectMapper.readValue(messageBody, WebhookResponse.class);
            LOGGER.info("Successfully deserialized webhook request: {}", webhookResponse);
            LOGGER.info("WebhookResponse values: mediaId={}, mimeType={}, type={}, caption={}",
                    webhookResponse.getMediaId(),
                    webhookResponse.getMimeType(),
                    webhookResponse.getType(),
                    webhookResponse.getCaption());
            // Check if message already exists
            Message message = messageRepository.findByMessageId(webhookResponse.getMessageId());
            if (message != null) {
                LOGGER.info("Updating message status for messageId: {}", webhookResponse.getMessageId());
                message.setStatus(webhookResponse.getStatus());
                messageRepository.save(message);
                return;
            }

            // Retrieve user and chat
            Users user = userRepository.findByPhone(webhookResponse.getRecipientBusinessNumber());
            if (user == null) {
                LOGGER.error("User not found for messageId: {}", webhookResponse.getMessageId());
                return;
            }

            Chat chat = chatRepository.findByChatNumberEndingWithAndUserId(webhookResponse.getSenderNumber(), user.getId());
            if (chat == null) {
                LOGGER.info("Creating new chat for messageId: {}", webhookResponse.getMessageId());
                chat = new Chat();
                chat.setChatNumber(webhookResponse.getSenderNumber());
                chat.setUser(user);
                chat = chatRepository.save(chat);
            }

            // Create new message entity
            Message newMessage = new Message();
            newMessage.setChat(chat);
            newMessage.setMessageId(webhookResponse.getMessageId());
            newMessage.setStatus(webhookResponse.getStatus());
            newMessage.setMessageType(webhookResponse.getType());
            newMessage.setTimestamp(LocalDateTime.now());

            // Text or caption content
            if ("text".equalsIgnoreCase(webhookResponse.getType())) {
                LOGGER.info("Received text message for messageId: {}", webhookResponse.getMessageId());
                newMessage.setContent(webhookResponse.getContent());
            } else if (webhookResponse.getCaption() != null) {
                LOGGER.info("Received caption message for messageId: {}", webhookResponse.getMessageId());
                newMessage.setContent(webhookResponse.getCaption());
            }

            // Handle media metadata
            if (webhookResponse.getMediaId() != null && webhookResponse.getMimeType() != null) {
                LOGGER.info("Received media message for messageId: {}", webhookResponse.getMessageId());
                MediaMetadata media = new MediaMetadata();
                media.setFileName(webhookResponse.getFileName() != null ? webhookResponse.getFileName() : webhookResponse.getMessageId());
                media.setMimeType(webhookResponse.getMimeType());
                byte[] mediaBytes = downloadMedia(webhookResponse.getMediaId());
                String s3MediaUrl = s3Service.uploadFile("bulkUpload", media.getFileName(), mediaBytes, media.getMimeType());
                media.setUrl(s3MediaUrl);
                media = mediaMetaDataRepository.save(media);
                LOGGER.info("Saved media metadata with ID: {}", media.getId());
                newMessage.setMedia(media);
            }

            messageRepository.save(newMessage);
            LOGGER.info("Saved message with ID: {}", webhookResponse.getMessageId());

        } catch (Exception e) {
            LOGGER.error("Error processing webhook data: {}", e.getMessage(), e);
            if (webhookResponse != null) {
                LOGGER.error("Webhook processing failed. Message ID: {}", webhookResponse.getMessageId());
            }
        }
    }

    private byte[] downloadMedia(String mediaId) {
        LOGGER.info("Downloading media with ID: {}", mediaId);
        String url = BASE_URL + mediaId;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(this.whatsappSdkConfig.getToken());
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_OCTET_STREAM));

        HttpEntity<String> entity = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<byte[]> response = restTemplate.exchange(url, HttpMethod.GET, entity, byte[].class);
        LOGGER.info("Downloaded media with ID: {}", mediaId);

        return response.getBody();
    }

}


package com.WhatsAppBusiness.WhatsApp.Business.Manager.rabbitmq.whatsappmediapubsub;

import com.WhatsAppBusiness.WhatsApp.Business.Common.Exceptions.UserException;
import com.WhatsAppBusiness.WhatsApp.Business.DTOs.MediaRequest;
import com.WhatsAppBusiness.WhatsApp.Business.Manager.rabbitmq.RabbitMQConfig;
import com.WhatsAppBusiness.WhatsApp.Business.Model.Chat;
import com.WhatsAppBusiness.WhatsApp.Business.Model.MediaMetadata;
import com.WhatsAppBusiness.WhatsApp.Business.Model.Message;
import com.WhatsAppBusiness.WhatsApp.Business.Model.Users;
import com.WhatsAppBusiness.WhatsApp.Business.Repository.ChatRepository;
import com.WhatsAppBusiness.WhatsApp.Business.Repository.MediaMetaDataRepository;
import com.WhatsAppBusiness.WhatsApp.Business.Repository.MessageRepository;
import com.WhatsAppBusiness.WhatsApp.Business.Service.S3Service;
import com.WhatsAppBusiness.WhatsApp.Business.Service.WhatsappService;
import com.WhatsAppBusiness.WhatsApp.Business.ServiceImpl.ChatServiceImpl;
import com.WhatsAppBusiness.WhatsApp.Business.ServiceImpl.MessageServiceImpl;
import com.WhatsAppBusiness.WhatsApp.Business.ServiceImpl.UserServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.whatsapp.api.domain.messages.response.MessageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;

@Service
public class MediaEventConsumer {

    @Autowired
    private S3Service s3Service;

    @Autowired
    private WhatsappService whatsappService;

    @Autowired
    private ChatServiceImpl chatService;

    @Autowired
    private MessageServiceImpl messageService;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private MediaMetaDataRepository mediaMetaDataRepository;

    private final ObjectMapper objectMapper;

    private static final Logger LOGGER = LoggerFactory.getLogger(MediaEventConsumer.class);

    public MediaEventConsumer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = RabbitMQConfig.MEDIA_QUEUE_NAME, containerFactory = "rabbitListenerContainerFactory")
    public void handleMediaRequest(String mediaRequestString) throws IOException, UserException {
        MediaRequest mediaRequest = objectMapper.readValue(mediaRequestString, MediaRequest.class);
        LOGGER.info("Received media request for file URL: {}", mediaRequest.getMediaUrl());
        // Download from S3, process, and send to WhatsApp Cloud API

        // Step 1: Download the file from the S3 URL
        File tempFile = s3Service.downloadFile(mediaRequest.getMediaUrl());
        if (tempFile == null) {
            LOGGER.error("Failed to download file from S3: {}", mediaRequest.getMediaUrl());
            return;
        }

        // Step 2: Upload file to WhatsApp Cloud
        String mediaId = whatsappService.uploadToWhatsApp(tempFile, mediaRequest.getMimeType());
        if (mediaId == null) {
            LOGGER.error("Failed to upload file to WhatsApp: {}", mediaRequest.getMediaUrl());
            return;
        }

        // Step 3: Send the media message to WhatsApp
        MessageResponse response = whatsappService.sendMediaMessage(mediaId, mediaRequest.getTo(), mediaRequest.getType(), mediaRequest.getCaption(), tempFile.getName());

        LOGGER.info("Media message sent to WhatsApp: {}", response);
        Users user = userService.findUserById(mediaRequest.getUserId());
        Chat chat = this.chatService.findByChatNumberAndUserId(mediaRequest.getTo(), mediaRequest.getUserId());

        if (chat == null) {
            LOGGER.info("Chat not found, creating new chat in side media event consumer");
            chat = new Chat();
            chat.setChatNumber(mediaRequest.getTo());
            chat.setUser(user);
            chat = this.chatRepository.save(chat);
        }

        // Save media metadata
        LOGGER.info("Saving media metadata");
        MediaMetadata media = new MediaMetadata();
        media.setFileName(tempFile.getName());
        media.setMimeType(mediaRequest.getMimeType());
        media.setUrl(mediaRequest.getMediaUrl());
        mediaMetaDataRepository.save(media);

        Message message = new Message();
        try {
            LOGGER.info("Saving message with media metadata for messageId: {}", response.messages().get(0).id());
            String messageId = response.messages().get(0).id();
            String status = response.messages().get(0).messageStatus();
            message.setMessageId(messageId);
            message.setChat(chat);
            message.setContent(mediaRequest.getCaption());
            message.setTimestamp(LocalDateTime.now());
            message.setMessageType(mediaRequest.getType());
            message.setStatus(status);
            message.setMedia(media);
            messageRepository.save(message);
        } catch (Exception e) {
            LOGGER.error("Error sending message", e);
            throw new RuntimeException("Failed to send message: " + e.getMessage());
        }
        // Step 4: Delete the temporary file
        tempFile.delete();
    }
}
package com.WhatsAppBusiness.WhatsApp.Business.ServiceImpl;

import com.WhatsAppBusiness.WhatsApp.Business.Constants.Constants;
import com.WhatsAppBusiness.WhatsApp.Business.DTOs.SendMessageRequest;
import com.WhatsAppBusiness.WhatsApp.Business.Service.WhatsappService;
import com.WhatsAppBusiness.WhatsApp.Business.Utils.WhatsappSdkConfig;
import com.whatsapp.api.domain.messages.*;
import com.whatsapp.api.domain.messages.response.MessageResponse;
import com.whatsapp.api.impl.WhatsappBusinessCloudApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.util.Map;

@Service
public class WhatsappServiceImpl implements WhatsappService {

    Logger LOGGER = LoggerFactory.getLogger(WhatsappServiceImpl.class);

    @Value("${whatsapp.token}")
    private String token;

    @Autowired
    private WhatsappSdkConfig whatsappSdkConfig;

    @Value("${whatsapp.phone-number-id}")
    private String phoneNumberId; // sender phone number id

    @Override
    public MessageResponse sendTextMessage(SendMessageRequest request) throws RuntimeException {
        try {
            // Build text message
            var message = Message.MessageBuilder.builder()
                    .setTo(request.getTo())
                    .buildTextMessage(new TextMessage()
                            .setBody(request.getContent())
                            .setPreviewUrl(false)); // optional: disable preview

            // Send message via SDK
            MessageResponse response = whatsappSdkConfig.whatsappBusinessCloudApi().sendMessage(phoneNumberId, message);

            LOGGER.info("Message sent. Message ID: {}", response.messages().get(0).id());
            return response;

        } catch (Exception e) {
            LOGGER.error("Error sending message", e);
            throw new RuntimeException("Failed to send message via WhatsApp SDK", e);
        }
    }

    @Override
    public String uploadToWhatsApp(File file, String mimeType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.setBearerAuth(token);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("messaging_product", "whatsapp");
        body.add("type", mimeType);
        body.add("file", new FileSystemResource(file));

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map> response = restTemplate.exchange(
                "https://graph.facebook.com/v18.0/" + phoneNumberId + "/media",
                HttpMethod.POST,
                requestEntity,
                Map.class
        );

        if (response.getStatusCode() != HttpStatus.OK) {
            LOGGER.error("Failed to upload file to WhatsApp");
            return null;
        }
        String mediaId = response.getBody().get("id").toString();
        LOGGER.info("Media ID: {}", mediaId);
        return mediaId;
    }

    @Override
    public MessageResponse sendMediaMessage(String mediaId, String to, String type, String caption, String fileName) {
        try {
            WhatsappBusinessCloudApi api = whatsappSdkConfig.whatsappBusinessCloudApi(); // ✅ already initialized

            Message message;

            switch (type.toLowerCase()) {
                case Constants.IMAGE -> {
                    LOGGER.info("Sending image message");
                    var imageMessage = new ImageMessage()
                            .setId(mediaId)
                            .setCaption(caption);
                    message = Message.MessageBuilder.builder()
                            .setTo(to)
                            .buildImageMessage(imageMessage);
                }
                case Constants.VIDEO -> {
                    LOGGER.info("Sending video message");
                    var videoMessage = new VideoMessage()
                            .setId(mediaId)
                            .setCaption(caption);
                    message = Message.MessageBuilder.builder()
                            .setTo(to)
                            .buildVideoMessage(videoMessage);
                }
                case Constants.DOCUMENT -> {
                    LOGGER.info("Sending document message");
                    var documentMessage = new DocumentMessage()
                            .setId(mediaId)
                            .setCaption(caption)
                            .setFileName(fileName);
                    message = Message.MessageBuilder.builder()
                            .setTo(to)
                            .buildDocumentMessage(documentMessage);
                }
                default -> throw new IllegalArgumentException("Unsupported media type: " + type);
            }

            MessageResponse response = api.sendMessage(phoneNumberId, message);
            if (response.messages() == null || response.messages().isEmpty()) {
                throw new RuntimeException("Failed to send media message via WhatsApp SDK");
            }
            LOGGER.info("Media message sent. Message ID: {}", response.messages().get(0).id());
            return response;

        } catch (IllegalArgumentException e) {
            LOGGER.error("Unsupported media type", e);
            throw new IllegalArgumentException("Failed to send media", e);
        } catch (Exception e) {
            LOGGER.error("Failed to send media message via WhatsApp SDK", e);
            throw new RuntimeException("Failed to send media", e);
        }
    }

}

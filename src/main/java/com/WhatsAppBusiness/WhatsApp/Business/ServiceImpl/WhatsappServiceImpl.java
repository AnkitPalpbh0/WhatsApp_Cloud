package com.WhatsAppBusiness.WhatsApp.Business.ServiceImpl;

import com.WhatsAppBusiness.WhatsApp.Business.DTOs.SendMessageRequest;
import com.WhatsAppBusiness.WhatsApp.Business.Service.WhatsappService;
import com.WhatsAppBusiness.WhatsApp.Business.Utils.WhatsappSdkConfig;
import com.whatsapp.api.domain.messages.Message;
import com.whatsapp.api.domain.messages.TextMessage;
import com.whatsapp.api.domain.messages.response.MessageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class WhatsappServiceImpl implements WhatsappService {

    Logger LOGGER = LoggerFactory.getLogger(WhatsappServiceImpl.class);

    @Autowired
    private WhatsappSdkConfig whatsappSdkConfig;

    @Value("${whatsapp.phone-number-id}")
    private String phoneNumberId; // sender phone number id

    @Override
    public void sendTextMessage(SendMessageRequest request) throws RuntimeException {
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

        } catch (Exception e) {
            LOGGER.error("Error sending message", e);
            throw new RuntimeException("Failed to send message via WhatsApp SDK", e);
        }
    }

}

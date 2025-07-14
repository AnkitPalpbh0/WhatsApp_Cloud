package com.WhatsAppBusiness.WhatsApp.Business.Service;

import com.WhatsAppBusiness.WhatsApp.Business.DTOs.SendMessageRequest;
import com.whatsapp.api.domain.messages.response.MessageResponse;

import java.io.File;

public interface WhatsappService {

    MessageResponse sendTextMessage(SendMessageRequest request);

    String uploadToWhatsApp(File tempFile, String mimeType);

    MessageResponse sendMediaMessage(String mediaId, String to, String type, String caption, String fileName);
}

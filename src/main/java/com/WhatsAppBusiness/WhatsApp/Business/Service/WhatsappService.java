package com.WhatsAppBusiness.WhatsApp.Business.Service;

import com.WhatsAppBusiness.WhatsApp.Business.DTOs.SendMessageRequest;
import com.whatsapp.api.domain.messages.response.MessageResponse;

public interface WhatsappService {

    MessageResponse sendTextMessage(SendMessageRequest request);

}

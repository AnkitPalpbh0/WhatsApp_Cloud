package com.WhatsAppBusiness.WhatsApp.Business.Service;

import com.WhatsAppBusiness.WhatsApp.Business.DTOs.SendMessageRequest;

public interface WhatsappService {

    void sendTextMessage(SendMessageRequest request);

}

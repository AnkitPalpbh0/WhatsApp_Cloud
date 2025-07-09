package com.WhatsAppBusiness.WhatsApp.Business.DTOs;

public class WebhookResponse {
    private String messageId;
    private String status;
    private String senderNumber;
    private String recipientBusinessNumber;
    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSenderNumber() {
        return senderNumber;
    }

    public void setSenderNumber(String senderNumber) {
        this.senderNumber = senderNumber;
    }

    public String getRecipientBusinessNumber() {
        return recipientBusinessNumber;
    }

    public void setRecipientBusinessNumber(String recipientBusinessNumber) {
        this.recipientBusinessNumber = recipientBusinessNumber;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

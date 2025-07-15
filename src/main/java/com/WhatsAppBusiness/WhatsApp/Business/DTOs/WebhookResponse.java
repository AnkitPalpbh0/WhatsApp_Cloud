package com.WhatsAppBusiness.WhatsApp.Business.DTOs;

public class WebhookResponse {
    private String messageId;
    private String status;
    private String senderNumber;
    private String recipientBusinessNumber;
    private String content;
    private String type;
    private String mediaId;
    private String caption;
    private String mimeType;
    private String fileName;
    private String mediaUrl;

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public String getMediaId() {
        return mediaId;
    }

    public String getCaption() {
        return caption;
    }

    public String getMimeType() {
        return mimeType;
    }

    public String getFileName() {
        return fileName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

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

    public void setMediaId(String id) {
        this.mediaId = id;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public void setMimeType(String s) {
        this.mimeType = s;
    }

    public void setFileName(String filename) {
        this.fileName = filename;
    }
}

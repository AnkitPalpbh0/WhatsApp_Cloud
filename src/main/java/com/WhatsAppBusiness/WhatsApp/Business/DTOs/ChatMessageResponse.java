package com.WhatsAppBusiness.WhatsApp.Business.DTOs;

import java.time.LocalDateTime;

public class ChatMessageResponse {

    private Integer id;
    private String messageId;
    private String messageType;
    private String content;
    private String mediaUrl;
    private LocalDateTime timestamp;
    private String status;

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Constructor
    public ChatMessageResponse(Integer id,String messageId, String messageType, String content,
                               String mediaUrl, LocalDateTime timestamp, String status) {
        this.id = id;
        this.messageId = messageId;
        this.messageType = messageType;
        this.content = content;
        this.mediaUrl = mediaUrl;
        this.timestamp = timestamp;
        this.status = status;
    }

    // Getters and Setters (or use Lombok @Data)
}


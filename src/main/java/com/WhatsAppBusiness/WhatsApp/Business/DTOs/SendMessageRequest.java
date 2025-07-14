package com.WhatsAppBusiness.WhatsApp.Business.DTOs;

import jakarta.validation.constraints.NotNull;

public class SendMessageRequest {
    @NotNull(message = "recipient number is required")
    private String to;      // recipient number with country code
    private Integer userId;
    private Integer chatId;
    @NotNull(message = "content is required")
    private String content;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getChatId() {
        return chatId;
    }

    public void setChatId(Integer chatId) {
        this.chatId = chatId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public SendMessageRequest() {
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }
}

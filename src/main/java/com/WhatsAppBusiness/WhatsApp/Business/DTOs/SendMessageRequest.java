package com.WhatsAppBusiness.WhatsApp.Business.DTOs;

import com.google.firebase.database.annotations.NotNull;

public class SendMessageRequest {
    @NotNull
    private String to;      // recipient number with country code
    private Integer userId;
    private Integer chatId;
    @NotNull
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

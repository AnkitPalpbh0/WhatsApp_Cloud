package com.WhatsAppBusiness.WhatsApp.Business.DTOs;

public class UserChatsResponse {
    private Integer id;
    private String chatId;

    public UserChatsResponse(Integer id, String chatId) {
        this.id = id;
        this.chatId = chatId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }
}

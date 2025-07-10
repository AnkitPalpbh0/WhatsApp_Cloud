package com.WhatsAppBusiness.WhatsApp.Business.DTOs;

public class LoginResponse {
    private String jwt;
    private boolean isAuth;
    private Integer userId;

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }

    public boolean isAuth() {
        return isAuth;
    }

    public void setAuth(boolean auth) {
        isAuth = auth;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}
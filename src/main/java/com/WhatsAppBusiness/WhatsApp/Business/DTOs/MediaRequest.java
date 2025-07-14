package com.WhatsAppBusiness.WhatsApp.Business.DTOs;

import jakarta.validation.constraints.NotNull;

public class MediaRequest {

    @NotNull(message = "Media URL must not be null")
    private String mediaUrl;

    @NotNull(message = "Recipient number (to) must not be null")
    private String to;

    @NotNull(message = "MIME type must not be null")
    private String mimeType;

    @NotNull(message = "Media type must not be null (e.g., image, video)")
    private String type;

    @NotNull(message = "Caption must not be null")
    private String caption;

    private Integer userId;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }
}
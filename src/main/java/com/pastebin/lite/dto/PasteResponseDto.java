package com.pastebin.lite.dto;

import java.time.LocalDateTime;

public class PasteResponseDto
{
    private String content;
    private Integer remaining_views;
    private LocalDateTime expires_at;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getRemaining_views() {
        return remaining_views;
    }

    public void setRemaining_views(Integer remaining_views) {
        this.remaining_views = remaining_views;
    }

    public LocalDateTime getExpires_at() {
        return expires_at;
    }

    public void setExpires_at(LocalDateTime expires_at) {
        this.expires_at = expires_at;
    }
}

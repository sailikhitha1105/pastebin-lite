package com.pastebin.lite.dto;

public class PastesDTO
{
    private String content;
    private Integer ttl_seconds;
    private Integer max_views;
    private String id;
    private Boolean isActive;
    private Integer viewCount;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getTtl_seconds() {
        return ttl_seconds;
    }

    public void setTtl_seconds(Integer ttl_seconds) {
        this.ttl_seconds = ttl_seconds;
    }

    public Integer getMax_views() {
        return max_views;
    }

    public void setMax_views(Integer max_views) {
        this.max_views = max_views;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public Integer getViewCount() {
        return viewCount;
    }

    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }
}

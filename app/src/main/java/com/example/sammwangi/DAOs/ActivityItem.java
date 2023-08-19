package com.example.sammwangi.DAOs;

public class ActivityItem {
    private String title;
    private String description;
    private String imageUrl;
    private String timestamp;

    public ActivityItem(String title, String description, String imageUrl, String timestamp) {
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.timestamp = timestamp;
    }

    public ActivityItem() {

    }

    // Add getters for all the fields
    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}


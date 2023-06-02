package com.example.sammwangi;

public class NotificationRequest {
    private String title;
    private String body;
    private String notificationType;

    public NotificationRequest(String title, String body, String notificationType) {
        this.title = title;
        this.body = body;
        this.notificationType = notificationType;
    }

    public NotificationRequest() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }
}

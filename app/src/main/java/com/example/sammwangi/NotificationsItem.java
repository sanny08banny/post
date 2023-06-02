package com.example.sammwangi;

public class NotificationsItem {
    private int id;
    private String title, body, dateReceived, dateSent, timeReceived, timeSent, notificationType;
    private int notificationImage;

    public NotificationsItem() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getDateReceived() {
        return dateReceived;
    }

    public void setDateReceived(String dateReceived) {
        this.dateReceived = dateReceived;
    }

    public String getDateSent() {
        return dateSent;
    }

    public void setDateSent(String dateSent) {
        this.dateSent = dateSent;
    }

    public String getTimeReceived() {
        return timeReceived;
    }

    public void setTimeReceived(String timeReceived) {
        this.timeReceived = timeReceived;
    }

    public String getTimeSent() {
        return timeSent;
    }

    public void setTimeSent(String timeSent) {
        this.timeSent = timeSent;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public int getNotificationImage() {
        return notificationImage;
    }

    public void setNotificationImage(int notificationImage) {
        this.notificationImage = notificationImage;
    }

    public NotificationsItem(int id, String title, String body, String dateReceived, String dateSent, String timeReceived, String timeSent, String notificationType, int notificationImage) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.dateReceived = dateReceived;
        this.dateSent = dateSent;
        this.timeReceived = timeReceived;
        this.timeSent = timeSent;
        this.notificationType = notificationType;
        this.notificationImage = notificationImage;
    }
}
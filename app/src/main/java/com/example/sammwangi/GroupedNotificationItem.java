package com.example.sammwangi;

import java.util.List;

public class GroupedNotificationItem {
    private String date;
    private List<NotificationsItem> notificationsItems;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<NotificationsItem> getNotificationsItems() {
        return notificationsItems;
    }

    public void setNotificationsItems(List<NotificationsItem> notificationsItems) {
        this.notificationsItems = notificationsItems;
    }

    public GroupedNotificationItem(String date, List<NotificationsItem> notificationsItems) {
        this.date = date;
        this.notificationsItems = notificationsItems;
    }
}

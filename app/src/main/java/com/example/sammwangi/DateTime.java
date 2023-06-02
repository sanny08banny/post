package com.example.sammwangi;

public class DateTime {
    private String datePosted, timePosted;

    public String getDatePosted() {
        return datePosted;
    }

    public void setDatePosted(String datePosted) {
        this.datePosted = datePosted;
    }

    public String getTimePosted() {
        return timePosted;
    }

    public void setTimePosted(String timePosted) {
        this.timePosted = timePosted;
    }

    public DateTime() {
    }

    public DateTime(String datePosted, String timePosted) {
        this.datePosted = datePosted;
        this.timePosted = timePosted;
    }
}

package com.example.sammwangi;

public class MyPostItem {
  private String fullName,transactionId,amountPosted,timePosted;
  int imageId;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getAmountPosted() {
        return amountPosted;
    }

    public void setAmountPosted(String amountPosted) {
        this.amountPosted = amountPosted;
    }

    public String getTimePosted() {
        return timePosted;
    }

    public void setTimePosted(String timePosted) {
        this.timePosted = timePosted;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public MyPostItem(String fullName, String transactionId, String amountPosted, String timePosted, int imageId) {
        this.fullName = fullName;
        this.transactionId = transactionId;
        this.amountPosted = amountPosted;
        this.timePosted = timePosted;
        this.imageId = imageId;
    }
}

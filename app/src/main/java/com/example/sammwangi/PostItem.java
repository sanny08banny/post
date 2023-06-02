package com.example.sammwangi;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class PostItem implements Parcelable{
    private Long id;
    private String postAmount;
    private String postType;
    private String datePosted;
    private String fullName,transactionId,transactionType,timePosted, token_id,dateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    private String referenceNumber;


    public PostItem() {

    }

    public String getPostAmount() {
        return postAmount;
    }

    public void setPostAmount(String postAmount) {
        this.postAmount = postAmount;
    }

    public String getPostType() {
        return postType;
    }

    public void setPostType(String postType) {
        this.postType = postType;
    }

    public String getDatePosted() {
        return datePosted;
    }

    public void setDatePosted(String datePosted) {
        this.datePosted = datePosted;
    }

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

    public String getTimePosted() {
        return timePosted;
    }

    public void setTimePosted(String timePosted) {
        this.timePosted = timePosted;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getToken_id() {
        return token_id;
    }

    public void setToken_id(String token_id) {
        this.token_id = token_id;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public PostItem(String postAmount, String post_type, String datePosted, String fullName, String transactionId, String transactionType, String timePosted, String tokenId, String dateTime, String referenceNumber) {
        this.postAmount = postAmount;
        this.postType = post_type;
        this.datePosted = datePosted;
        this.fullName = fullName;
        this.transactionId = transactionId;
        this.transactionType = transactionType;
        this.timePosted = timePosted;
        this.token_id = tokenId;
        this.dateTime = dateTime;
        this.referenceNumber = referenceNumber;
    }
    protected PostItem(Parcel in) {
        postAmount = in.readString();
        postType = in.readString();
        datePosted = in.readString();
        fullName = in.readString();
        transactionId = in.readString();
        transactionType = in.readString();
        timePosted = in.readString();
        token_id = in.readString();
        referenceNumber = in.readString();
    }


    public static final Parcelable.Creator<PostItem> CREATOR = new Parcelable.Creator<PostItem>() {
        @Override
        public PostItem createFromParcel(Parcel in) {
            return new PostItem(in);
        }

        @Override
        public PostItem[] newArray(int size) {
            return new PostItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
      dest.writeString(postAmount);
      dest.writeString(postType);
        dest.writeString(datePosted);
        dest.writeString(fullName);
        dest.writeString(transactionId);
        dest.writeString(transactionType);
        dest.writeString(timePosted);
        dest.writeString(token_id);
        dest.writeString(referenceNumber);
    }
}
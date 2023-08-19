package com.example.sammwangi.DAOs;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class ProfileAccount implements Parcelable {
    private int id;
    private String accountType;

    private String fullName,email,referenceNumber,subCounty,password,repeatPassword,userName,tokenId;
    private String filePath;
    private String timestamp;

    public ProfileAccount() {
    }

    public ProfileAccount(int id, String accountType, String fullName, String email,
                          String referenceNumber, String subCounty, String password, String repeatPassword, String userName,
                          String tokenId, String filePath, String timestamp) {
        this.id = id;
        this.accountType = accountType;
        this.fullName = fullName;
        this.email = email;
        this.referenceNumber = referenceNumber;
        this.subCounty = subCounty;
        this.password = password;
        this.repeatPassword = repeatPassword;
        this.userName = userName;
        this.tokenId = tokenId;
        this.filePath = filePath;
        this.timestamp = timestamp;
    }

    protected ProfileAccount(Parcel in) {
        id = in.readInt();
        accountType = in.readString();
        fullName = in.readString();
        email = in.readString();
        referenceNumber = in.readString();
        subCounty = in.readString();
        password = in.readString();
        repeatPassword = in.readString();
        userName = in.readString();
        tokenId = in.readString();
        filePath = in.readString();
        timestamp = in.readString();
    }



    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(accountType);
        dest.writeString(fullName);
        dest.writeString(email);
        dest.writeString(referenceNumber);
        dest.writeString(subCounty);
        dest.writeString(password);
        dest.writeString(repeatPassword);
        dest.writeString(userName);
        dest.writeString(tokenId);
        dest.writeString(filePath);
        dest.writeString(timestamp);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ProfileAccount> CREATOR = new Creator<ProfileAccount>() {
        @Override
        public ProfileAccount createFromParcel(Parcel in) {
            return new ProfileAccount(in);
        }

        @Override
        public ProfileAccount[] newArray(int size) {
            return new ProfileAccount[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public String getSubCounty() {
        return subCounty;
    }

    public void setSubCounty(String subCounty) {
        this.subCounty = subCounty;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRepeatPassword() {
        return repeatPassword;
    }

    public void setRepeatPassword(String repeatPassword) {
        this.repeatPassword = repeatPassword;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}

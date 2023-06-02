package com.example.sammwangi;

public class ProfileAccount {
    private int id;
    private String accountType;

    private String fullName,email,referenceNumber,subCounty,password,repeatPassword,userName,tokenId;

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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getSubCounty() {
        return subCounty;
    }

    public void setSubCounty(String subCounty) {
        this.subCounty = subCounty;
    }

    public ProfileAccount(int id, String profile_type, String userName, String fullName, String email, String referenceNumber, String subCounty, String password, String repeatPassword, String tokenId) {
        this.id = id;
        this.accountType = profile_type;
        this.fullName = fullName;
        this.email = email;
        this.referenceNumber = referenceNumber;
        this.subCounty = subCounty;
        this.password = password;
        this.repeatPassword = repeatPassword;
        this.userName = userName;
        this.tokenId = tokenId;
    }
}

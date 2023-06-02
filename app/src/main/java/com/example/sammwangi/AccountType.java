package com.example.sammwangi;

public class AccountType {
    private int image;
    private String account_type;

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getAccount_type() {
        return account_type;
    }

    public void setAccount_type(String account_type) {
        this.account_type = account_type;
    }

    public AccountType(int image, String account_type) {
        this.image = image;
        this.account_type = account_type;
    }
}

package com.example.sammwangi;

import com.example.sammwangi.DAOs.PostItem;

import java.util.List;

public interface VolleyCallback {
    void onSuccess(List<PostItem> postItems);
    void onError(Exception e);
}

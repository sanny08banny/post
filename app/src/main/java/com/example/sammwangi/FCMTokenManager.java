package com.example.sammwangi;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessaging;

public class FCMTokenManager {
    private static final String PREF_FILE_NAME = "FCMTokenPrefs";
    private static final String PREF_TOKEN_KEY = "FCMTokenKey";
    private static String tokenId;

    public static String getTokenId() {

            FirebaseMessaging.getInstance().getToken()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            tokenId = task.getResult();
                            Log.e("Tag", "Token Id: " + tokenId);
                        }else {
                            Log.e("Tag", "Error getting token: " + task.getException());
                        }
                    });

        return tokenId;
    }

    public static void saveToken(Context context, String token) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PREF_TOKEN_KEY, token);
        editor.apply();
    }

    public static String getToken(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(PREF_TOKEN_KEY, null);
    }
}

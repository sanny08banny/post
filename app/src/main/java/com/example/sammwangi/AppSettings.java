package com.example.sammwangi;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import java.util.Objects;

public class AppSettings extends AppCompatActivity {

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private static final String PREF_NOTIFICATIONS_ENABLED = "notifications_enabled";
    private static final String PREF_BUBBLING_NOTIFICATIONS_ENABLED = "bubbling_notifications_enabled";
    private static final String PREF_NAME = "app_preferences";

    private SwitchCompat switchNotifications;
    private SwitchCompat switchBubblingNotifications;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_settings);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("App Settings");

        switchNotifications = findViewById(R.id.switchNotifications);
        switchBubblingNotifications = findViewById(R.id.switchBubblingNotifications);
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        boolean notificationsEnabled = sharedPreferences.getBoolean(PREF_NOTIFICATIONS_ENABLED, true);
        boolean bubblingNotificationsEnabled = sharedPreferences.getBoolean(PREF_BUBBLING_NOTIFICATIONS_ENABLED, false);

        switchNotifications.setChecked(notificationsEnabled);
        switchBubblingNotifications.setChecked(bubblingNotificationsEnabled);

        switchNotifications.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    enableNotifications();
                } else {
                    disableNotifications();
                }
            }
        });

        switchBubblingNotifications.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    enableBubblingNotifications();
                } else {
                    disableBubblingNotifications();
                }
            }
        });
    }

    private void enableNotifications() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(PREF_NOTIFICATIONS_ENABLED, true);
        editor.apply();

        Toast.makeText(AppSettings.this, "Notifications enabled", Toast.LENGTH_SHORT).show();
    }

    private void disableNotifications() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(PREF_NOTIFICATIONS_ENABLED, false);
        editor.apply();

        Toast.makeText(AppSettings.this, "Notifications disabled", Toast.LENGTH_SHORT).show();
    }

    private void enableBubblingNotifications() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(PREF_BUBBLING_NOTIFICATIONS_ENABLED, true);
        editor.apply();

        Toast.makeText(AppSettings.this, "Bubbling notifications enabled", Toast.LENGTH_SHORT).show();
    }

    private void disableBubblingNotifications() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(PREF_BUBBLING_NOTIFICATIONS_ENABLED, false);
        editor.apply();

        Toast.makeText(AppSettings.this, "Bubbling notifications disabled", Toast.LENGTH_SHORT).show();
    }
}

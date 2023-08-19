package com.example.sammwangi.activities;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.example.sammwangi.R;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_settings);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("App Settings");

        getSupportFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        private static final String PREF_NOTIFICATIONS_ENABLED = "notifications_enabled";
        private static final String PREF_BUBBLING_NOTIFICATIONS_ENABLED = "bubbling_notifications_enabled";
        private static final String PREF_NAME = "app_preferences";
        private static final int PASSWORD_PREF = 38;

        private SharedPreferences sharedPreferences;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.preferences, rootKey);
            sharedPreferences = requireContext().getSharedPreferences(PREF_NAME, MODE_PRIVATE);

            // Retrieve and set the current state of the SwitchPreferences
            SwitchPreferenceCompat switchNotifications = findPreference("notifications_enabled");
            SwitchPreferenceCompat switchBubblingNotifications = findPreference("bubbling_notifications_enabled");
            Preference passwordPref = findPreference("password");

            // Add listeners to handle changes in SwitchPreference states
            switchNotifications.setOnPreferenceChangeListener((preference, newValue) -> {
                boolean isChecked = (boolean) newValue;
                if (isChecked) {
                    enableNotifications();
                } else {
                    disableNotifications();
                }
                return true;
            });

            switchBubblingNotifications.setOnPreferenceChangeListener((preference, newValue) -> {
                boolean isChecked = (boolean) newValue;
                if (isChecked) {
                    enableBubblingNotifications();
                } else {
                    disableBubblingNotifications();
                }
                return true;
            });

            passwordPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(@NonNull Preference preference) {
                    openPasswordActivity();
                    return true;
                }
            });
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == PASSWORD_PREF && resultCode == RESULT_OK){

            }
        }

        private void openPasswordActivity() {
            Intent intent = new Intent(requireContext(), PasswordActivity.class);
            startActivityForResult(intent,PASSWORD_PREF);
        }

        private void enableNotifications() {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(PREF_NOTIFICATIONS_ENABLED, true);
            editor.apply();

            Toast.makeText(requireContext(), "Notifications enabled", Toast.LENGTH_SHORT).show();
        }

        private void disableNotifications() {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(PREF_NOTIFICATIONS_ENABLED, false);
            editor.apply();

            Toast.makeText(requireContext(), "Notifications disabled", Toast.LENGTH_SHORT).show();
        }

        private void enableBubblingNotifications() {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(PREF_BUBBLING_NOTIFICATIONS_ENABLED, true);
            editor.apply();

            Toast.makeText(requireContext(), "Bubbling notifications enabled", Toast.LENGTH_SHORT).show();
        }

        private void disableBubblingNotifications() {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(PREF_BUBBLING_NOTIFICATIONS_ENABLED, false);
            editor.apply();

            Toast.makeText(requireContext(), "Bubbling notifications disabled", Toast.LENGTH_SHORT).show();
        }
    }
}
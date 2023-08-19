package com.example.sammwangi.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.sammwangi.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class PasswordActivity extends AppCompatActivity {

    private EditText etCurrentPassword, etNewPassword, etReenterNewPassword;
    private MaterialButton btnChangePassword;
    private String passwordPolicyMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        MaterialToolbar toolbar = findViewById(R.id.password_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Password");
        }

        etCurrentPassword = findViewById(R.id.et_current_password);
        etNewPassword = findViewById(R.id.et_new_password);
        etReenterNewPassword = findViewById(R.id.et_reenter_new_password);
        btnChangePassword = findViewById(R.id.btn_change_password);

        // Load the password policy message from resources
        passwordPolicyMessage = getString(R.string.password_policy_message);

        // Add text change listeners to the EditText fields
        etCurrentPassword.addTextChangedListener(textWatcher);
        etNewPassword.addTextChangedListener(textWatcher);
        etReenterNewPassword.addTextChangedListener(textWatcher);

        // Set a click listener for the "Change Password" button
        btnChangePassword.setOnClickListener(v -> changePassword());
    }

    // TextWatcher to enable/disable the button based on field emptiness
    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            // Enable the button only when all fields are not empty
            boolean enableButton =
                    !etCurrentPassword.getText().toString().isEmpty()
                            && !etNewPassword.getText().toString().isEmpty()
                            && !etReenterNewPassword.getText().toString().isEmpty();

            btnChangePassword.setEnabled(enableButton);
        }
    };

    // Method to change the password and validate the password policy
    private void changePassword() {
        String newPassword = etNewPassword.getText().toString();
        if (validatePasswordPolicy(newPassword)) {
            // Password meets the policy, you can proceed with the password change logic here.
            Toast.makeText(this, "Password changed successfully!", Toast.LENGTH_SHORT).show();
            // Add your password change logic here
        } else {
            // Password does not meet the policy, show an error message.
            Toast.makeText(this, "Password must follow the policy.", Toast.LENGTH_SHORT).show();
        }
    }

    // Method to validate the password policy
    private boolean validatePasswordPolicy(String password) {
        // Add your password policy validation logic here.
        // For this example, we'll check if the password has at least 8 characters,
        // contains letters, numbers, and special characters.
        boolean hasMinimumLength = password.length() >= 8;
        boolean hasLetters = password.matches(".*[a-zA-Z].*");
        boolean hasNumbers = password.matches(".*\\d.*");
        boolean hasSpecialCharacters = password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*");
        return hasMinimumLength && hasLetters && hasNumbers && hasSpecialCharacters;
    }
}

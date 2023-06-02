package com.example.sammwangi;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

public class SignInActivity extends AppCompatActivity {
    private TextInputEditText emailEditext,passwordEditext;
    private MaterialButton signInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Sign-in to account");

        emailEditext = findViewById(R.id.sign_in_email_textField);
        passwordEditext = findViewById(R.id.sign_in_password_textField);
        signInButton = findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = Objects.requireNonNull(emailEditext.getText()).toString();
                Log.e("SignInActivity","Email chosen: " + email);
                String password = Objects.requireNonNull(passwordEditext.getText()).toString();

                if (email.length() == 0|| password.length() == 0){
                    Toast.makeText(SignInActivity.this,"Please fill all the fields",Toast.LENGTH_SHORT).show();
                }else {
                    ProfileFetchRunnable profileFetchRunnable = new ProfileFetchRunnable(email,password,SignInActivity.this);

                    Thread thread = new Thread(profileFetchRunnable);
                    thread.start();
                }
            }
        });
    }
}
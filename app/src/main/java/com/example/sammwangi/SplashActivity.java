package com.example.sammwangi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

public class SplashActivity extends AppCompatActivity {
    private TextView pTextView,ostTextview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        pTextView = findViewById(R.id.splash_screen_P);
        ostTextview = findViewById(R.id.splash_screen_OST);

        // Load animation from XML
        Animation fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in);

        // Set animation listener for the first TextView
        fadeInAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // Fade in animation has ended, start fade-in animation for the second TextView
                ostTextview.startAnimation(fadeInAnimation);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        pTextView.startAnimation(fadeInAnimation);

        // Set a listener to detect when the fade-in animation ends for the second TextView
        fadeInAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // Fade in animation has ended, navigate to the next activity
                navigateToNextActivity();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }
    private void navigateToNextActivity() {
        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
    }
}
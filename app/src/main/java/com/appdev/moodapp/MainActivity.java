package com.appdev.moodapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private static final int SPLASH_SCREEN_TIME_OUT = 2000; // After completion of 2000 ms, the next activity will get started.

    private static final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Intent is used to switch from one activity to another.
                if (firebaseAuth.getCurrentUser() != null) {
                    Intent i = new Intent(MainActivity.this, CalenderViewActivity.class);
                    startActivity(i); // invoke the SecondActivity.
                    finish(); // the current activity will get finished.
                } else {
                    Intent i = new Intent(MainActivity.this, OnboardingActivity.class);
                    startActivity(i); // invoke the SecondActivity.
                    finish();
                }
            }
        }, SPLASH_SCREEN_TIME_OUT);
    }
}
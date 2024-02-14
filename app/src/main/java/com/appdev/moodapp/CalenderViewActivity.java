package com.appdev.moodapp;

import static com.appdev.moodapp.Fragments.settingsScreen.handleBiometricAuthentication;
import static com.appdev.moodapp.Fragments.settingsScreen.showToast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.appdev.moodapp.Fragments.BoardScreen;
import com.appdev.moodapp.Fragments.homePage;
import com.appdev.moodapp.Fragments.settingsScreen;
import com.appdev.moodapp.Utils.Utils;
import com.appdev.moodapp.databinding.ActivityCalenderViewBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Calendar;
import java.util.Objects;
import java.util.concurrent.Executor;

public class CalenderViewActivity extends AppCompatActivity {
    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;

    public ActivityCalenderViewBinding binding; // Declare binding object

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCalenderViewBinding.inflate(getLayoutInflater()); // Inflate the binding object
        setContentView(binding.getRoot()); // Set the root view of the layout
        Utils.applyTheme(CalenderViewActivity.this, Utils.getThemeMode(CalenderViewActivity.this));
        if (Utils.getBoolean(getApplicationContext())) {
            if (Utils.isFingerprintSensorAvailable(getApplicationContext())) {
                // Device has a fingerprint sensor
                if (Utils.hasEnrolledFingerprints(getApplicationContext())) {
                    executor = ContextCompat.getMainExecutor(getApplicationContext());
                    biometricPrompt = new
                            BiometricPrompt(CalenderViewActivity.this,
                            executor, new
                            BiometricPrompt.AuthenticationCallback() {
                                @Override
                                public void onAuthenticationError(int errorCode,
                                                                  @NonNull CharSequence errString) {
                                    super.onAuthenticationError(errorCode, errString);
                                    finish();
                                }

                                @Override
                                public void onAuthenticationSucceeded(
                                        @NonNull BiometricPrompt.AuthenticationResult
                                                result) {
                                    super.onAuthenticationSucceeded(result);
                                    Toast.makeText(getApplicationContext(),
                                            "Authentication succeeded!",
                                            Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onAuthenticationFailed() {
                                    super.onAuthenticationFailed();

                                }
                            });

                    promptInfo = new BiometricPrompt.PromptInfo.Builder()
                            .setTitle("Biometric login for my app")
                            .setSubtitle("Log in using your biometric credential")
                            .setNegativeButtonText("Cancel")
                            .build();

                    biometricPrompt.authenticate(promptInfo);
                } else {
                    showToast(getApplicationContext(), "You need to enroll/add at least one Fingerprint from Settings App.");
                    // Set switch state to false if there are no enrolled fingerprints
                }
            } else {
                showToast(getApplicationContext(), "Fingerprint Sensor for Biometric authentication is not supported on this device.");
                // Set switch state to false if the fingerprint sensor is not available
            }

        }


        Toolbar toolbar = binding.activityToolbar;
        setSupportActionBar(binding.activityToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        binding.fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();

                // Get current day, month, and year
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH) + 1; // Months start from 0 (January)
                int year = calendar.get(Calendar.YEAR);

                // Create an intent to start the AddDataActivity
                Intent intent = new Intent(CalenderViewActivity.this, AddDataActivity.class);

                // Put extra data (day, month, and year) into the intent
                intent.putExtra("day", day);
                intent.putExtra("month", month);
                intent.putExtra("year", year);

                // Start the activity with the intent
                startActivity(intent);
            }
        });

        binding.bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.Diary) {
                loadFragment(new homePage(), false);
            } else if (item.getItemId() == R.id.Category) {
//                    loadFragment(new HomeFragment(getApplicationContext()),false);
            } else if (item.getItemId() == R.id.Board) {
                loadFragment(new BoardScreen(), false);
            } else if (item.getItemId() == R.id.Settings) {
                loadFragment(new settingsScreen(), false);
            }
            return true;
        });
        loadFragment(new homePage(), true);


    }


    public void loadFragment(Fragment fragment, Boolean isAppInit) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (isAppInit) {
            fragmentTransaction.add(R.id.frame_layout, fragment);
        } else {
            fragmentTransaction.replace(R.id.frame_layout, fragment);
        }
        fragmentTransaction.commit();

    }
}
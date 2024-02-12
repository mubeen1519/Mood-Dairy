package com.appdev.moodapp.Fragments;

import static android.app.Activity.RESULT_OK;

import static androidx.biometric.BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.appdev.moodapp.R;
import com.appdev.moodapp.Utils.Utils;
import com.appdev.moodapp.databinding.FragmentSettingsScreenBinding;

import java.util.concurrent.Executor;


public class settingsScreen extends Fragment {

    private FragmentSettingsScreenBinding binding; // Declare the binding object

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment using view binding
        binding = FragmentSettingsScreenBinding.inflate(inflater, container, false);
        binding.fingerprintSwitch.setChecked(Utils.getBoolean(requireContext()));
        binding.fingerprintSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            boolean switchState = false; // Assume switch state is false initially

            if (Utils.isFingerprintSensorAvailable(requireContext())) {
                // Device has a fingerprint sensor
                if (Utils.hasEnrolledFingerprints(requireContext())) {
                    if (isChecked) {
                        int authState = handleBiometricAuthentication(requireContext());
                        if (authState == BiometricManager.BIOMETRIC_SUCCESS) {
                            Utils.saveBoolean(requireContext(), true);
                            switchState = true; // Set switch state to true if authentication is successful
                        }
                    } else {
                        Utils.editBoolean(requireContext(), false);
                    }
                } else {
                    showToast(requireContext(),"You need to enroll/add at least one Fingerprint from Settings App.");
                    // Set switch state to false if there are no enrolled fingerprints
                }
            } else {
                showToast(requireContext(),"Fingerprint Sensor for Biometric authentication is not supported on this device.");
                // Set switch state to false if the fingerprint sensor is not available
            }

            // Set the switch state
            binding.fingerprintSwitch.setChecked(switchState);
        });

        return binding.getRoot(); // Return the root view of the binding
    }

    public static int handleBiometricAuthentication(Context context) {
        BiometricManager biometricManager = BiometricManager.from(context);
        switch (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG | BiometricManager.Authenticators.DEVICE_CREDENTIAL)) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                showToast(context, "App can authenticate using biometrics.");
                return BiometricManager.BIOMETRIC_SUCCESS;
            case BIOMETRIC_ERROR_NO_HARDWARE:
                showToast(context, "No biometric features available on this device.");
                return BIOMETRIC_ERROR_NO_HARDWARE;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                showToast(context, "Biometric features are currently unavailable.");
                return BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                // Prompts the user to create credentials that your app accepts.
                final Intent enrollIntent;
                showToast(context, "It is not set yet! Let's see it.");
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                    enrollIntent = new Intent(Settings.ACTION_BIOMETRIC_ENROLL);
                    enrollIntent.putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED, BiometricManager.Authenticators.BIOMETRIC_STRONG | BiometricManager.Authenticators.DEVICE_CREDENTIAL);
                    ((Activity) context).startActivityForResult(enrollIntent, 123);
                    return BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED;
                } else {
                    showToast(context, "Android version must be greater than 10.");
                    return BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED;
                }
            case BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED:
                showToast(context, "Biometric security update required.");
                return BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED;
            case BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED:
                showToast(context, "Biometric authentication is not supported on this device.");
                return BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED;
            case BiometricManager.BIOMETRIC_STATUS_UNKNOWN:
                showToast(context, "Biometric status is unknown.");
                return BiometricManager.BIOMETRIC_STATUS_UNKNOWN;
            default:
                return -1; // Unknown state
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123) {
            if (resultCode == RESULT_OK) {
                // Enrollment successful
                showToast(requireContext(), "Biometric enrollment successful.");
            } else {
                // Enrollment failed or canceled
                showToast(requireContext(), "Biometric enrollment failed or canceled.");
            }
        }
    }

    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}


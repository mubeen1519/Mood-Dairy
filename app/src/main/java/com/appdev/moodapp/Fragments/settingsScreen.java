package com.appdev.moodapp.Fragments;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

import static androidx.biometric.BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.appdev.moodapp.AdjustWordSize;
import com.appdev.moodapp.Login_screen;
import com.appdev.moodapp.R;
import com.appdev.moodapp.ReminderSetter;
import com.appdev.moodapp.ThemeChange;
import com.appdev.moodapp.Utils.Utils;
import com.appdev.moodapp.databinding.FragmentSettingsScreenBinding;
import com.appdev.moodapp.timeline;
import com.appdev.moodapp.updateData;
import com.google.firebase.auth.FirebaseAuth;

import java.util.concurrent.Executor;


public class settingsScreen extends Fragment {
    private FragmentSettingsScreenBinding binding; // Declare the binding object
    String textSizeName;
    private FirebaseAuth firebaseAuth;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment using view binding
        binding = FragmentSettingsScreenBinding.inflate(inflater, container, false);
        if(Utils.isDarkModeActivated(requireActivity())){
            Utils.status_bar_dark(requireActivity(), R.color.black);
        } else{
            Utils.status_bar(requireActivity(), R.color.lig_bkg);
        }

        SharedPreferences preferences = requireContext().getSharedPreferences("text_size_prefs", Context.MODE_PRIVATE);
        textSizeName = preferences.getString("text_size", "");

        binding.themeChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requireContext().startActivity(new Intent(requireActivity(), ThemeChange.class));
                requireActivity().finish();
            }
        });

        binding.Logout.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(requireActivity(), Login_screen.class));

        });

        switch (textSizeName) {
            case "medium":
                forMedium();
                break;
            case "large":
                forLarge();
                break;
            default:
                forDefault();
                break;
        }

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

        binding.profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requireContext().startActivity(new Intent(requireActivity(), updateData.class));
            }
        });
        binding.reminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requireContext().startActivity(new Intent(requireActivity(), ReminderSetter.class));
            }
        });

        binding.AdjustText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requireContext().startActivity(new Intent(requireActivity(), AdjustWordSize.class));
            }
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

    public void forDefault() {
        binding.fp.setTextAppearance(R.style.SettingsWord);
        binding.rem.setTextAppearance(R.style.SettingsWord);
        binding.tc.setTextAppearance(R.style.SettingsWord);
        binding.pp.setTextAppearance(R.style.SettingsWord);
        binding.ats.setTextAppearance(R.style.SettingsWord);
        binding.ps.setTextAppearance(R.style.SettingsWord);
    }
    public void forMedium() {
        binding.fp.setTextAppearance(R.style.SettingsWordMedium);
        binding.rem.setTextAppearance(R.style.SettingsWordMedium);
        binding.tc.setTextAppearance(R.style.SettingsWordMedium);
        binding.pp.setTextAppearance(R.style.SettingsWordMedium);
        binding.ats.setTextAppearance(R.style.SettingsWordMedium);
        binding.ps.setTextAppearance(R.style.SettingsWordMedium);
    }

    public void forLarge() {
        binding.fp.setTextAppearance(R.style.SettingsWordLarge);
        binding.rem.setTextAppearance(R.style.SettingsWordLarge);
        binding.tc.setTextAppearance(R.style.SettingsWordLarge);
        binding.pp.setTextAppearance(R.style.SettingsWordLarge);
        binding.ats.setTextAppearance(R.style.SettingsWordLarge);
        binding.ps.setTextAppearance(R.style.SettingsWordLarge);
    }

    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}


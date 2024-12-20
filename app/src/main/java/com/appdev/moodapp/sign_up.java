package com.appdev.moodapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.appdev.moodapp.ModelClasses.UserProfile;
import com.appdev.moodapp.Utils.UserValidation;
import com.appdev.moodapp.Utils.Utils;
import com.appdev.moodapp.databinding.ActivitySignUpBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class sign_up extends AppCompatActivity {

    private ActivitySignUpBinding binding;

    private FirebaseAuth firebaseAuth;
    private final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Utils.status_bar(this, R.color.second_bkg);

        Animation my_anim = AnimationUtils.loadAnimation(this, R.anim.fadein);

        binding.mainLay2.setAnimation(my_anim);
        binding.btnReg.setAnimation(my_anim);
        binding.upLayout.setAnimation(my_anim);
        binding.regImage.setAnimation(my_anim);

        binding.mainLay2.setVisibility(View.VISIBLE);
        binding.btnReg.setVisibility(View.VISIBLE);
        binding.upLayout.setVisibility(View.VISIBLE);
        binding.regImage.setVisibility(View.VISIBLE);

        firebaseAuth = FirebaseAuth.getInstance();
        binding.btnReg.setOnClickListener(v -> {


            String email = binding.suMail.getText().toString();
            String userName = binding.suUser.getText().toString();
            String password = binding.suPass.getText().toString();
            String confirmPassword = binding.suCpass.getText().toString();

            String checkMessage = new UserValidation().newUserValidationCheck(email, password, confirmPassword);
            boolean userValidationReceived = new UserValidation().newUserConfirmValidationCheck(email, password, confirmPassword);

            if (userValidationReceived) {
                binding.pg.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.common), PorterDuff.Mode.SRC_IN);
                binding.pg.setVisibility(View.VISIBLE);
                firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        UserProfile userProfile = new UserProfile(userName,password,email);
                        firebaseDatabase.getReference().child("userProfiles")
                                .child(Objects.requireNonNull(firebaseAuth.getUid()))
                                .setValue(userProfile)
                                .addOnSuccessListener(aVoid -> {
                                    Utils.setAppTheme(sign_up.this);
                                    Intent intent = new Intent(this, CalenderViewActivity.class);
                                    startActivity(intent);
                                    finish();
                                });
                    } else {
                        binding.pg.setVisibility(View.GONE);
                        Toast.makeText(this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                binding.pg.setVisibility(View.GONE);
                Toast.makeText(this, checkMessage, Toast.LENGTH_SHORT).show();
            }

        });
    }
}
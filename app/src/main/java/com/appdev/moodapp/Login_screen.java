package com.appdev.moodapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.appdev.moodapp.Utils.UserValidation;
import com.appdev.moodapp.Utils.Utils;
import com.appdev.moodapp.databinding.ActivityLoginScreenBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class Login_screen extends AppCompatActivity {

    private ActivityLoginScreenBinding binding;
    private Handler handler;
    private Animation new_anim;

    private static final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private static final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Utils.status_bar(this, R.color.second_bkg);

        handler = new Handler();
        new_anim = AnimationUtils.loadAnimation(this, R.anim.fadein);
        handler.postDelayed(() -> {
            binding.mainLay.setAnimation(new_anim);
            binding.loginImage.setAnimation(new_anim);
            binding.btnLay.setAnimation(new_anim);
            binding.newId.setAnimation(new_anim);

            binding.loginImage.setVisibility(View.VISIBLE);
            binding.mainLay.setVisibility(View.VISIBLE);
            binding.btnLay.setVisibility(View.VISIBLE);
            binding.newId.setVisibility(View.VISIBLE);

        }, 500);

        if (firebaseAuth.getCurrentUser() != null) {
            Intent intent = new Intent(this, CalenderViewActivity.class);
            startActivity(intent);
            finish();
        }

        binding.newId.setOnClickListener(v -> {
            Intent intent = new Intent(this, sign_up.class);
            startActivity(intent);
        });

        binding.btnLogin.setOnClickListener(v -> {
            String email = binding.siMail.getText().toString().trim();
            String password = binding.siPass.getText().toString();

            String checkMessage = new UserValidation().userValidationCheck(email, password);
            boolean userValidationReceived = new UserValidation().userValidationCheckReturn(email, password);

            if (userValidationReceived) {
                binding.pg.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.common), PorterDuff.Mode.SRC_IN);
                binding.pg.setVisibility(View.VISIBLE);
                firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Intent intent = new Intent(this, CalenderViewActivity.class);
                        startActivity(intent);
                        finish();
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
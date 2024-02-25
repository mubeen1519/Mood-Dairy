package com.appdev.moodapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.appdev.moodapp.ModelClasses.UserProfile;
import com.appdev.moodapp.Utils.UserValidation;
import com.appdev.moodapp.Utils.Utils;
import com.appdev.moodapp.databinding.ActivityUpdateDataBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class updateData extends AppCompatActivity {

    private ActivityUpdateDataBinding binding;
    private FirebaseAuth firebaseAuth;
    private final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String userName;
    String email;
    String password;
    boolean emailUpdated = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUpdateDataBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if(Utils.isDarkModeActivated(updateData.this)){
            Utils.status_bar_dark(updateData.this, R.color.black);
        } else{
            Utils.status_bar(updateData.this, R.color.lig_bkg);
        }
        firebaseAuth = FirebaseAuth.getInstance();
        binding.pg.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.common), PorterDuff.Mode.SRC_IN);
        binding.loaderPg.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.common), PorterDuff.Mode.SRC_IN);
//        binding.loadingLayout.setVisibility(View.VISIBLE);
        String userId = firebaseAuth.getUid();
        if (userId != null) {
            DatabaseReference userProfileRef = firebaseDatabase.getReference().child("userProfiles").child(userId);
            userProfileRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                        // Now you can use the userProfile object
                        if (userProfile != null) {
                            userName = userProfile.getUserName();
                            email = userProfile.getUserEmail();
                            password = userProfile.getUserPassword();
                            binding.loadingLayout.setVisibility(View.GONE);
                            binding.suUser.setText(userName);
                            binding.suMail.setText(email);
                            binding.suPass.setText(password);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle error
                }
            });
        }
        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        binding.btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.pg.setVisibility(View.VISIBLE);

                String checkMessage = new UserValidation().userValidationCheck(Objects.requireNonNull(binding.suMail.getText()).toString(), Objects.requireNonNull(binding.suPass.getText()).toString());
                boolean userValidationReceived = new UserValidation().userValidationCheckReturn(Objects.requireNonNull(binding.suMail.getText()).toString(), Objects.requireNonNull(binding.suPass.getText()).toString());




                //---------------------------

                if (!Objects.requireNonNull(binding.suMail.getText()).toString().trim().equals(email.trim()) ) {
                    if (userValidationReceived && !binding.suMail.getText().toString().trim().isEmpty()) {
                        if (user != null) {
                            binding.pg.setVisibility(View.VISIBLE);
                            AuthCredential credential = EmailAuthProvider.getCredential(email, password);
                            user.reauthenticate(credential)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            user.updateEmail(binding.suMail.getText().toString())
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            if(Objects.requireNonNull(binding.suPass.getText()).toString().trim().equals(password.trim()) && Objects.requireNonNull(binding.suUser.getText()).toString().trim().equals(userName.trim()) ){
                                                                UserProfile userProfile = new UserProfile(binding.suUser.getText().toString(), binding.suPass.getText().toString(), binding.suMail.getText().toString());
                                                                firebaseDatabase.getReference().child("userProfiles")
                                                                        .child(Objects.requireNonNull(firebaseAuth.getUid()))
                                                                        .setValue(userProfile)
                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void aVoid) {
                                                                                // User profile data updated successfully
                                                                                finish();
                                                                            }
                                                                        })
                                                                        .addOnFailureListener(new OnFailureListener() {
                                                                            @Override
                                                                            public void onFailure(@NonNull Exception e) {
                                                                                // User profile data update failed
                                                                                binding.pg.setVisibility(View.GONE);
                                                                                Toast.makeText(updateData.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        });
                                                            }
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            // Email verification failed
                                                            binding.pg.setVisibility(View.GONE);
                                                            Toast.makeText(updateData.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Re-authentication failed
                                            binding.pg.setVisibility(View.GONE);
                                            Toast.makeText(updateData.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });

                        }

                    } else {
                        binding.pg.setVisibility(View.GONE);
                        if (binding.suUser.getText().toString().trim().isEmpty()) {
                            Toast.makeText(updateData.this, "Username must not be empty !", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(updateData.this, checkMessage, Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                //----------------------------------------------

                if (!Objects.requireNonNull(binding.suPass.getText()).toString().trim().equals(password.trim())) {
                    if (userValidationReceived && !binding.suPass.getText().toString().trim().isEmpty()) {
                        if (user != null) {

                            AuthCredential credential = EmailAuthProvider.getCredential(email, password);
                            user.reauthenticate(credential)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            user.updatePassword(binding.suPass.getText().toString())
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            if(Objects.requireNonNull(binding.suUser.getText()).toString().trim().equals(userName.trim()) ){
                                                                UserProfile userProfile = new UserProfile(binding.suUser.getText().toString(), binding.suPass.getText().toString(), binding.suMail.getText().toString());
                                                                firebaseDatabase.getReference().child("userProfiles")
                                                                        .child(Objects.requireNonNull(firebaseAuth.getUid()))
                                                                        .setValue(userProfile)
                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void aVoid) {
                                                                                // User profile data updated successfully
                                                                                finish();
                                                                            }
                                                                        })
                                                                        .addOnFailureListener(new OnFailureListener() {
                                                                            @Override
                                                                            public void onFailure(@NonNull Exception e) {
                                                                                // User profile data update failed
                                                                                binding.pg.setVisibility(View.GONE);
                                                                                Toast.makeText(updateData.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        });
                                                            }
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            // Password update failed
                                                            binding.pg.setVisibility(View.GONE);
                                                            Toast.makeText(updateData.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Re-authentication failed
                                            binding.pg.setVisibility(View.GONE);
                                            Toast.makeText(updateData.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }

                    } else {
                        binding.pg.setVisibility(View.GONE);
                        if (Objects.requireNonNull(binding.suUser.getText()).toString().trim().isEmpty()) {
                            Toast.makeText(updateData.this, "Username must not be empty !", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(updateData.this, checkMessage, Toast.LENGTH_SHORT).show();
                        }
                    }
                }


                //--------------------------------------
                if (!Objects.requireNonNull(binding.suUser.getText()).toString().trim().equals(userName.trim())) {
                    if (userValidationReceived && !binding.suUser.getText().toString().trim().isEmpty()) {
                        if (user != null) {

                            // Update user profile data
                            UserProfile userProfile = new UserProfile(binding.suUser.getText().toString(), binding.suPass.getText().toString(), binding.suMail.getText().toString());
                            firebaseDatabase.getReference().child("userProfiles")
                                    .child(Objects.requireNonNull(firebaseAuth.getUid()))
                                    .setValue(userProfile)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // User profile data updated successfully
                                            finish();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // User profile data update failed
                                            binding.pg.setVisibility(View.GONE);
                                            Toast.makeText(updateData.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });

                        }

                    } else {
                        binding.pg.setVisibility(View.GONE);
                        if (binding.suUser.getText().toString().trim().isEmpty()) {
                            Toast.makeText(updateData.this, "Username must not be empty !", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(updateData.this, checkMessage, Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    binding.pg.setVisibility(View.GONE);
                }

                if (Objects.requireNonNull(binding.suUser.getText()).toString().trim().equals(userName.trim()) &&
                        Objects.requireNonNull(binding.suMail.getText()).toString().trim().equals(email.trim()) &&
                        Objects.requireNonNull(binding.suPass.getText()).toString().trim().equals(password.trim())){
                    binding.pg.setVisibility(View.GONE);
                    Toast.makeText(updateData.this, "Make any changes to update !", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }
}
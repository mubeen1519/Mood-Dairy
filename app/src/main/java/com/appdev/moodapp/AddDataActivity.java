package com.appdev.moodapp;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.appdev.moodapp.Utils.DailyData;
import com.appdev.moodapp.Utils.ImagePagerAdapter;
import com.appdev.moodapp.databinding.ActivityAddDataBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class AddDataActivity extends AppCompatActivity {

    private ActivityAddDataBinding binding;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private String selectedEmoji = "Neutral";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddDataBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        int day = getIntent().getIntExtra("day", -1);
        int month = getIntent().getIntExtra("month", -1);
        int year = getIntent().getIntExtra("year", -1);


        List<Uri> selectedImageUris = new ArrayList<>();
        ImagePagerAdapter adapter = new ImagePagerAdapter(selectedImageUris, this);
        binding.viewPager.setAdapter(adapter);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        FirebaseDatabase database = FirebaseDatabase.getInstance();

        StorageReference storageReference;

        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();


        String dayStr = String.format(Locale.getDefault(), "%02d", day);
        String monthStr = String.format(Locale.getDefault(), "%02d", month);
        String yearStr = String.valueOf(year);

        String currentDate = yearStr + "-" + monthStr + "-" + dayStr;

        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            // Check if multiple images are selected
                            if (data.getClipData() != null) {
                                int count = data.getClipData().getItemCount();
                                for (int i = 0; i < count; i++) {
                                    Uri imageUri = data.getClipData().getItemAt(i).getUri();
                                    selectedImageUris.add(imageUri);
                                }
                            } else if (data.getData() != null) {
                                // Single image is selected
                                Uri imageUri = data.getData();
                                selectedImageUris.add(imageUri);
                            }
                            adapter.notifyDataSetChanged(); // Notify adapter about data changes
                        }
                    }
                });

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AddDataActivity.this, CalenderViewActivity.class));
                finish();
            }
        });

        binding.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String textualData = binding.writeData.getText().toString().trim();
                if (textualData.isEmpty()) {
                    Toast.makeText(AddDataActivity.this, "Write something about your day !", Toast.LENGTH_SHORT).show();
                } else {
                    DailyData dailyData;
                    List<String> imageUriStrings = new ArrayList<>();
                    for (Uri imageUri : selectedImageUris) {
                        String imageName = "image_" + System.currentTimeMillis(); // Generate unique image name
                        StorageReference imageRef = storageReference.child(imageName);

                        // Upload image to Firebase Storage
                        imageRef.putFile(imageUri)
                                .addOnSuccessListener(taskSnapshot -> {
                                    // Get the uploaded image URL
                                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                        String imageUrl = uri.toString();
                                        imageUriStrings.add(imageUrl);
                                    });
                                })
                                .addOnFailureListener(e -> {
                                    // Handle any errors during image upload
                                    Toast.makeText(AddDataActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                                });
                    }
                    if (selectedImageUris.isEmpty()) {
                        dailyData = new DailyData(currentDate, selectedEmoji, textualData);
                    } else {
                        dailyData = new DailyData(currentDate, selectedEmoji, textualData, imageUriStrings);
                    }

                    database.getReference().child("users").
                            child(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid()).
                            child(yearStr).child(monthStr).child("dailyData").child(dayStr).
                            setValue(dailyData).addOnSuccessListener(unused -> {
                                Toast.makeText(AddDataActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(AddDataActivity.this, CalenderViewActivity.class));
                                finish();
                            }).addOnFailureListener(e -> {
                                Toast.makeText(AddDataActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                            });
                }
            }
        });
        binding.imageSelectionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });

        binding.imageBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedEmoji = "Happy";
                toggleCircularFrame(binding.circularFrame1);
            }
        });

        binding.imageBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedEmoji = "Smile";
                toggleCircularFrame(binding.circularFrame2);
            }
        });

        binding.imageBtn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedEmoji = "Neutral";
                toggleCircularFrame(binding.circularFrame3);
            }
        });

        binding.imageBtn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedEmoji = "Sad";
                toggleCircularFrame(binding.circularFrame4);
            }
        });

        binding.imageBtn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedEmoji = "Cry";
                toggleCircularFrame(binding.circularFrame5);
            }
        });


    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true); // Enable multiple selection
        galleryLauncher.launch(intent);
    }

    private void toggleCircularFrame(FrameLayout circularFrame) {
        // Hide all circular frames
        binding.circularFrame1.setVisibility(View.INVISIBLE);
        binding.circularFrame2.setVisibility(View.INVISIBLE);
        binding.circularFrame3.setVisibility(View.INVISIBLE);
        binding.circularFrame4.setVisibility(View.INVISIBLE);
        binding.circularFrame5.setVisibility(View.INVISIBLE);

        // Show the circular frame associated with the clicked image button
        circularFrame.setVisibility(View.VISIBLE);
    }
}
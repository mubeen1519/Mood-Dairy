package com.appdev.moodapp;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.appdev.moodapp.Utils.AddImageAdapter;
import com.appdev.moodapp.Utils.DailyData;
import com.appdev.moodapp.Utils.Utils;
import com.appdev.moodapp.databinding.ActivityAddDataBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class AddDataActivity extends AppCompatActivity {

    private ActivityAddDataBinding binding;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private String selectedEmoji = "Neutral";
    int count = 0;
    Boolean isOld = false;
    AddImageAdapter adapter;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    FirebaseDatabase database = FirebaseDatabase.getInstance();

    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();

    StorageReference storageReference = firebaseStorage.getReference();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddDataBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        if(Utils.isDarkModeActivated(AddDataActivity.this)){
            Utils.status_bar_dark(AddDataActivity.this, R.color.black);
        } else{
            Utils.status_bar(AddDataActivity.this, R.color.lig_bkg);
        }

        int day = getIntent().getIntExtra("day", -1);
        int month = getIntent().getIntExtra("month", -1);
        int year = getIntent().getIntExtra("year", -1);
        DateFormatSymbols dfs = new DateFormatSymbols();
        String[] months = dfs.getMonths();
        String monthName = months[month - 1];
        String title = String.format(Locale.getDefault(), "%s %d", monthName, year);
        binding.titleMonth.setText(title);
        binding.pg.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.common), PorterDuff.Mode.SRC_IN);

        List<String> selectedImageString = new ArrayList<>();


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
                                    selectedImageString.add(imageUri.toString());
                                    isOld = true;
                                }
                            } else if (data.getData() != null) {
                                // Single image is selected
                                Uri imageUri = data.getData();
                                selectedImageString.add(imageUri.toString());
                                isOld = true;
                            }
                            adapter.notifyDataSetChanged(); // Notify adapter about data changes
                        }
                    }
                });


        adapter = new AddImageAdapter(selectedImageString, this);
        binding.RcImages.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.RcImages.setAdapter(adapter);
        binding.loadingLayout.setVisibility(View.VISIBLE);
        binding.myView.setText(R.string.loader);

        database.getReference().child("users")
                .child(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid())
                .child(yearStr).child(monthStr).child("dailyData").child(dayStr)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // Data exists, populate activity components with existing data
                            DailyData dailyData = dataSnapshot.getValue(DailyData.class);
                            if (dailyData != null) {
                                binding.writeData.setText(dailyData.getTextualData());
                                selectedEmoji = dailyData.getEmoji();
                                switch (dailyData.getEmoji()) {
                                    case "Happy":
                                        toggleCircularFrame(binding.circularFrame1);
                                        break;
                                    case "Smile":
                                        toggleCircularFrame(binding.circularFrame2);
                                        break;
                                    case "Sad":
                                        toggleCircularFrame(binding.circularFrame4);
                                        break;
                                    case "Cry":
                                        toggleCircularFrame(binding.circularFrame5);
                                        break;
                                    default:
                                        toggleCircularFrame(binding.circularFrame3);
                                        break;
                                }
                                if (dailyData.getImageUris() != null && !dailyData.getImageUris().isEmpty()) {
                                    selectedImageString.addAll(dailyData.getImageUris());
                                    adapter.notifyDataSetChanged();
                                }
                            }
                        }
                        binding.loadingLayout.setVisibility(View.GONE);
                        binding.myView.setText(R.string.saving);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        binding.loadingLayout.setVisibility(View.GONE);
                        binding.myView.setText(R.string.saving);
                    }
                });


        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        binding.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                binding.loadingLayout.setVisibility(View.VISIBLE);
                String textualData = binding.writeData.getText().toString().trim();
                if (textualData.isEmpty()) {
                    Toast.makeText(AddDataActivity.this, "Write something about your day !", Toast.LENGTH_SHORT).show();
                } else {
                    List<String> imageUriStrings = new ArrayList<>();
                    if (isOld) {
                        for (String imageUri : selectedImageString) {
                            String imageName = "image_" + System.currentTimeMillis(); // Generate unique image name
                            StorageReference imageRef = storageReference.child(imageName);

                            // Upload image to Firebase Storage
                            imageRef.putFile(Uri.parse(imageUri))
                                    .addOnSuccessListener(taskSnapshot -> {
                                        // Get the uploaded image URL
                                        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                            String imageUrl = uri.toString();
                                            imageUriStrings.add(imageUrl);
                                            count++;
                                            if (count == selectedImageString.size()) {
                                                saveDailyDataToDatabase(currentDate, selectedEmoji, textualData, imageUriStrings, yearStr, monthStr, dayStr);
                                            }
                                        });
                                    })
                                    .addOnFailureListener(e -> {
                                        // Handle any errors during image upload
                                        Toast.makeText(AddDataActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                        Toast.makeText(AddDataActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                                    });
                        }
                    }
                    else if (count == 0 && selectedImageString.isEmpty()) {
                        saveDailyDataToDatabase(currentDate, selectedEmoji, textualData, imageUriStrings, yearStr, monthStr, dayStr);
                    } else if (count == 0 && selectedImageString.size() > 0) {
                        saveDailyDataToDatabase(currentDate, selectedEmoji, textualData, selectedImageString, yearStr, monthStr, dayStr);
                    }
                }
            }
        });
        binding.imageSelectionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedImageString.clear();
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

    private void saveDailyDataToDatabase(String currentDate, String selectedEmoji, String textualData, List<String> imageUriStrings, String yearStr, String monthStr, String dayStr) {
        DailyData dailyData;
        if (imageUriStrings.isEmpty()) {
            dailyData = new DailyData(currentDate, selectedEmoji, textualData);
        } else {
            dailyData = new DailyData(currentDate, selectedEmoji, textualData, imageUriStrings);
        }

        database.getReference().child("users")
                .child(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid())
                .child(yearStr).child(monthStr).child("dailyData").child(dayStr)
                .setValue(dailyData)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(AddDataActivity.this, "Saved Successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(AddDataActivity.this, CalenderViewActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AddDataActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
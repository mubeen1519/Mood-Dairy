package com.appdev.moodapp;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
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

import java.io.File;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

public class AddDataActivity extends AppCompatActivity {

    private ActivityAddDataBinding binding;
    private ActivityResultLauncher<Void> requestLauncher;

    AlertDialog notificationGuidanceDialog;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private String selectedEmoji = "Neutral";
    int count = 0;
    Boolean isOld = false;
    AddImageAdapter adapter;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    FirebaseDatabase database = FirebaseDatabase.getInstance();

    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();

    StorageReference storageReference = firebaseStorage.getReference();
    String textSizeName;

    Dialog dialog;
    private static final int REQUEST_CODE_PERMISSIONS = 10;
    private static final String[] REQUIRED_PERMISSIONS = new String[]{Manifest.permission.CAMERA};
    List<String> selectedImageString = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddDataBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        if (Utils.isDarkModeActivated(AddDataActivity.this)) {
            Utils.status_bar_dark(AddDataActivity.this, R.color.black);
        } else {
            Utils.status_bar(AddDataActivity.this, R.color.lig_bkg);
        }
        dialog = new Dialog(this);


        SharedPreferences preferences = getSharedPreferences("text_size_prefs", Context.MODE_PRIVATE);
        textSizeName = preferences.getString("text_size", "");

        int day = getIntent().getIntExtra("day", -1);
        int month = getIntent().getIntExtra("month", -1);
        int year = getIntent().getIntExtra("year", -1);
        DateFormatSymbols dfs = new DateFormatSymbols();
        String[] months = dfs.getMonths();
        String monthName = months[month - 1];
        String title = String.format(Locale.getDefault(), "%s %d", monthName, year);
        binding.titleMonth.setText(title);
        binding.pg.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.common), PorterDuff.Mode.SRC_IN);




        String dayStr = String.format(Locale.getDefault(), "%02d", day);
        String monthStr = String.format(Locale.getDefault(), "%02d", month);
        String yearStr = String.valueOf(year);

        String currentDate = yearStr + "-" + monthStr + "-" + dayStr;


        // Retrieve the URI from the intent extras


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
        Log.d("FirebaseStorage","AT MAIN "+Utils.URI_IMAGE);




        adapter = new AddImageAdapter(selectedImageString, this);
        binding.RcImages.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.RcImages.setAdapter(adapter);
        binding.loadingLayout.setVisibility(View.VISIBLE);
        binding.myView.setText(R.string.loader);

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
                    binding.loadingLayout.setVisibility(View.GONE);
                    Toast.makeText(AddDataActivity.this, "Write something about your day !", Toast.LENGTH_SHORT).show();
                } else {

                    List<String> imageUriStrings = new ArrayList<>();

                    if (isOld) {
                        int size = selectedImageString.size();
                        for (String imageUri : selectedImageString) {
                            if (!imageUri.startsWith("https://")) {
                                // This URI is suitable for upload
                                String imageName = "image_" + System.currentTimeMillis(); // Generate unique image name
                                StorageReference imageRef = storageReference.child(imageName);

                                // Upload image to Firebase Storage
                                int finalSize = size;
                                imageRef.putFile(Uri.parse(imageUri))
                                        .addOnSuccessListener(taskSnapshot -> {
                                            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                                String imageUrl = uri.toString();
                                                imageUriStrings.add(imageUrl);
                                                count++;
                                                if (count == finalSize) {
                                                    saveDailyDataToDatabase(currentDate, selectedEmoji, textualData, imageUriStrings, yearStr, monthStr, dayStr);
                                                }
                                            });
                                        })
                                        .addOnFailureListener(e -> {
                                            // Handle any errors during image upload
                                            Log.d("FirebaseStorage", "Image upload failed: " + e.getMessage()); // Log the error message
                                            Toast.makeText(AddDataActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                            Toast.makeText(AddDataActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                                        });
                            }else{
                                size--;
                                imageUriStrings.add(imageUri);
                            }
                        }
                    } else if (count == 0 && selectedImageString.isEmpty()) {
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
                showDialog();
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


        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog);
        Button buttonClose = dialog.findViewById(R.id.close_btn);
        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                int totalPoints = sharedPreferences.getInt("totalPoints", 0);
                totalPoints += 100;
                editor.putInt("totalPoints", totalPoints);
                editor.apply();
                Toast.makeText(AddDataActivity.this, "Claimed Successfully", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(AddDataActivity.this, CalenderViewActivity.class));
                finish();
                dialog.dismiss();
            }
        });


    }

    @Override
    protected void onDestroy() {
        Utils.URI_IMAGE = "";
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        if (!Utils.URI_IMAGE.isEmpty()) {
            String uriStringToAdd = Utils.URI_IMAGE;

            // Check if the URI is not already present in the list
            boolean uriAlreadyExists = false;
            for (String uri : selectedImageString) {
                if (uri.equals(uriStringToAdd)) {
                    uriAlreadyExists = true;
                    break;
                }
            }

            // Add the URI to the list only if it's not already present
            if (!uriAlreadyExists) {
                selectedImageString.add(uriStringToAdd);
                isOld = true;
                adapter.notifyDataSetChanged();
            }
        }
        super.onResume();

    }

    private boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }


    private void showDialog() {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.bottomsheetlayout);

        LinearLayout galleryLayout = dialog.findViewById(R.id.layoutEdit);
        LinearLayout cameraLayout = dialog.findViewById(R.id.layoutShare);


        galleryLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                openGallery();
            }
        });

        cameraLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (allPermissionsGranted()) {
                    startActivity(new Intent(AddDataActivity.this, camerapreview.class));
                } else {
                    ActivityCompat.requestPermissions(AddDataActivity.this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
                }
            }
        });


        dialog.show();
        Objects.requireNonNull(dialog.getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

    }


    public void forDefault() {
        binding.howWas.setTextAppearance(R.style.WeekRow);
        binding.writeData.setTextAppearance(R.style.WeekRow);
        binding.writeAbout.setTextAppearance(R.style.WeekRow);
        binding.yourPhotos.setTextAppearance(R.style.WeekRow);
    }

    public void forMedium() {
        binding.howWas.setTextAppearance(R.style.WeekRowMedium);
        binding.writeData.setTextAppearance(R.style.WeekRowMedium);
        binding.writeAbout.setTextAppearance(R.style.WeekRowMedium);
        binding.yourPhotos.setTextAppearance(R.style.WeekRowMedium);
    }

    public void forLarge() {
        binding.howWas.setTextAppearance(R.style.WeekRowLarge);
        binding.writeData.setTextAppearance(R.style.WeekRowLarge);
        binding.writeAbout.setTextAppearance(R.style.WeekRowLarge);
        binding.yourPhotos.setTextAppearance(R.style.WeekRowLarge);
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
                    SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                    String lastStoredDate = sharedPreferences.getString("lastStoredDate", "");
                    String currentDates = getCurrentDate(); // Implement this method to get current date
                    if (!currentDates.equals(lastStoredDate)) {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("lastStoredDate", currentDates);
                        // Increment user's total points by 100
                        editor.apply();
                        dialog.show();
                    } else {
                        startActivity(new Intent(AddDataActivity.this, CalenderViewActivity.class));
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AddDataActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private String getCurrentDate() {
        // Create a SimpleDateFormat object with the desired date format
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        // Get the current date and format it using the SimpleDateFormat object
        return sdf.format(new Date());
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
    private int generateRandomRequestCode(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min + 1) + min;
    }
}
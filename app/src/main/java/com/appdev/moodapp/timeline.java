package com.appdev.moodapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.appdev.moodapp.Recyclerview.DailyDataAdapter;
import com.appdev.moodapp.Utils.DailyData;
import com.appdev.moodapp.Utils.Utils;
import com.appdev.moodapp.databinding.ActivityTimelineBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class timeline extends AppCompatActivity {

    private ActivityTimelineBinding binding;
    private DailyDataAdapter adapter;
    private List<DailyData> dataList;
    private List<DailyData> savedList;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private Boolean getAll = true;

    private boolean[] checkboxStates = {false, false, false, false, false}; // Initialize with all elements set to false


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTimelineBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Utils.status_bar(timeline.this, R.color.lig_bkg);
        firebaseAuth = FirebaseAuth.getInstance();

        binding.pg.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.common), PorterDuff.Mode.SRC_IN);
        binding.loadingLayout.setVisibility(View.VISIBLE);
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        YearMonth currentMonth = YearMonth.now();
        if (currentUser != null) {
            dataList = new ArrayList<>();
            savedList = new ArrayList<>();
            String yearStr = String.valueOf(currentMonth.getYear());
            String monthStr = currentMonth.format(DateTimeFormatter.ofPattern("MM"));

            databaseReference = FirebaseDatabase.getInstance().getReference().child("users")
                    .child(currentUser.getUid()).child(yearStr).child(monthStr).child("dailyData");

            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        dataList.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            DailyData dailyData = snapshot.getValue(DailyData.class);
                            if (dailyData != null) {
                                dataList.add(dailyData);
                                savedList.add(dailyData);
                            }
                        }
                        adapter.notifyDataSetChanged();
                        binding.loadingLayout.setVisibility(View.GONE);
                    } else {
                        // Hide the loadingLayout if there's no data
                        binding.loadingLayout.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle any errors
                }
            });

            adapter = new DailyDataAdapter(dataList);
            binding.ListRc.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));
            binding.ListRc.setAdapter(adapter);

        }
        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // Listen for radio button changes

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.filter_dialog);
        Button buttonClose = dialog.findViewById(R.id.close_btn);
        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                dataList.clear();
                if (getAll) {
                    dataList.addAll(savedList);
                    adapter.notifyDataSetChanged();
                } else {
                    filterDataAndUpdateRecyclerView();
                }
            }
        });

        RadioGroup radioGroup = dialog.findViewById(R.id.radioGroup);
        CheckBox checkBoxOption2 = dialog.findViewById(R.id.checkBoxOption2);
        CheckBox checkBoxOption3 = dialog.findViewById(R.id.checkBoxOption3);
        CheckBox checkBoxOption4 = dialog.findViewById(R.id.checkBoxOption4);
        CheckBox checkBoxOption5 = dialog.findViewById(R.id.checkBoxOption5);
        CheckBox checkBoxOption6 = dialog.findViewById(R.id.checkBoxOption6);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // Show/hide checkboxes based on radio button selection
                if (checkedId == R.id.radioButtonCustom) {
                    getAll = false;
                    checkBoxOption2.setVisibility(View.VISIBLE);
                    checkBoxOption3.setVisibility(View.VISIBLE);
                    checkBoxOption4.setVisibility(View.VISIBLE);
                    checkBoxOption5.setVisibility(View.VISIBLE);
                    checkBoxOption6.setVisibility(View.VISIBLE);
                } else {
                    getAll = true;
                    checkBoxOption2.setChecked(false);
                    checkBoxOption3.setChecked(false);
                    checkBoxOption4.setChecked(false);
                    checkBoxOption5.setChecked(false);
                    checkBoxOption6.setChecked(false);

                    checkBoxOption2.setVisibility(View.GONE);
                    checkBoxOption3.setVisibility(View.GONE);
                    checkBoxOption4.setVisibility(View.GONE);
                    checkBoxOption5.setVisibility(View.GONE);
                    checkBoxOption6.setVisibility(View.GONE);
                }
            }
        });
        binding.filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
            }
        });

        checkBoxOption2.setChecked(checkboxStates[0]);
        checkBoxOption3.setChecked(checkboxStates[1]);
        checkBoxOption4.setChecked(checkboxStates[2]);
        checkBoxOption5.setChecked(checkboxStates[3]);
        checkBoxOption6.setChecked(checkboxStates[4]);

        // Set listener for checkbox changes
        CompoundButton.OnCheckedChangeListener checkboxListener = (buttonView, isChecked) -> {
            // Update checkbox states
            checkboxStates[0] = checkBoxOption2.isChecked();
            checkboxStates[1] = checkBoxOption3.isChecked();
            checkboxStates[2] = checkBoxOption4.isChecked();
            checkboxStates[3] = checkBoxOption5.isChecked();
            checkboxStates[4] = checkBoxOption6.isChecked();
        };

        checkBoxOption2.setOnCheckedChangeListener(checkboxListener);
        checkBoxOption3.setOnCheckedChangeListener(checkboxListener);
        checkBoxOption4.setOnCheckedChangeListener(checkboxListener);
        checkBoxOption5.setOnCheckedChangeListener(checkboxListener);
        checkBoxOption6.setOnCheckedChangeListener(checkboxListener);
    }

    private void filterDataAndUpdateRecyclerView() {
        dataList.addAll(savedList);
        List<DailyData> filteredList = new ArrayList<>();

        // Filter dataList based on checkbox states
        for (DailyData data : dataList) {
            if ((checkboxStates[0] && data.getEmoji().equals("Happy")) ||
                    (checkboxStates[1] && data.getEmoji().equals("Smile")) ||
                    (checkboxStates[2] && data.getEmoji().equals("Neutral")) ||
                    (checkboxStates[3] && data.getEmoji().equals("Sad")) ||
                    (checkboxStates[4] && data.getEmoji().equals("Cry"))) {
                filteredList.add(data);
            }
        }
        dataList.clear();
        dataList.addAll(filteredList); // Add the filtered data
        adapter.notifyDataSetChanged();
    }

}
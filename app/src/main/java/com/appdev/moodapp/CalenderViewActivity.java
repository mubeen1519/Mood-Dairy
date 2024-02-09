package com.appdev.moodapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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

import com.appdev.moodapp.Fragments.homePage;
import com.appdev.moodapp.databinding.ActivityCalenderViewBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Calendar;
import java.util.Objects;

public class CalenderViewActivity extends AppCompatActivity {

    public ActivityCalenderViewBinding binding; // Declare binding object

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCalenderViewBinding.inflate(getLayoutInflater()); // Inflate the binding object
        setContentView(binding.getRoot()); // Set the root view of the layout

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
                loadFragment(new homePage(),false);
            }
            else if(item.getItemId() == R.id.Category) {
//                    loadFragment(new HomeFragment(getApplicationContext()),false);
            }
            else if (item.getItemId() == R.id.Board) {
//                    loadFragment(new SettingsFragment(),false);
            }
            else if(item.getItemId() == R.id.Settings) {
//                    loadFragment(new HomeFragment(getApplicationContext()),false);
            }
            return true;
        });
        loadFragment(new homePage(),true);


    }


    public void loadFragment(Fragment fragment, Boolean isAppInit){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if(isAppInit){
            fragmentTransaction.add(R.id.frame_layout,fragment);
        } else{
            fragmentTransaction.replace(R.id.frame_layout,fragment);
        }
        fragmentTransaction.commit();

    }
}
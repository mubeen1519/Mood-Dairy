package com.appdev.moodapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.appdev.moodapp.Utils.Utils;
import com.appdev.moodapp.databinding.ActivityThemeChangeBinding;

public class ThemeChange extends AppCompatActivity {


    private ActivityThemeChangeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityThemeChangeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Toast.makeText(ThemeChange.this,"Is Dark mode : " + Utils.isDarkModeActivated(ThemeChange.this), Toast.LENGTH_SHORT).show();
        binding.radioButtonNight.setChecked(Utils.isDarkModeActivated(ThemeChange.this));
        binding.radioButtonDay.setChecked(!Utils.isDarkModeActivated(ThemeChange.this));
        if (Utils.isDarkModeActivated(ThemeChange.this)) {
            Utils.status_bar_dark(ThemeChange.this, R.color.black);
        } else {
            Utils.status_bar(ThemeChange.this, R.color.lig_bkg);
        }

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ThemeChange.this, CalenderViewActivity.class));
                finish();
            }
        });

        binding.radioGroupCustom.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // Show/hide checkboxes based on radio button selection
                    Utils.toggleTheme(ThemeChange.this);
            }
        });

    }
}
package com.appdev.moodapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
        binding.radioButtonNight.setChecked(Utils.isDarkModeActivated(ThemeChange.this));
        binding.radioButtonDay.setChecked(!Utils.isDarkModeActivated(ThemeChange.this));

        SharedPreferences preferences = getSharedPreferences("text_size_prefs", Context.MODE_PRIVATE);


        String textSizeName = preferences.getString("text_size", "");
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
    public void forDefault() {
        binding.radioButtonDay.setTextAppearance(R.style.RadioSize);
        binding.radioButtonNight.setTextAppearance(R.style.RadioSize);
        binding.calendarDay.setTextAppearance(R.style.RadioSize);
        binding.calendarMonthDay.setTextAppearance(R.style.RadioSize);
        binding.textualDataDay.setTextAppearance(R.style.RadioSize);
        binding.calendarnight.setTextAppearance(R.style.RadioSize);
        binding.calendarMonth.setTextAppearance(R.style.RadioSize);
        binding.textualData.setTextAppearance(R.style.RadioSize);
    }

    public void forMedium() {
        binding.radioButtonDay.setTextAppearance(R.style.RadioSizeMedium);
        binding.calendarDay.setTextAppearance(R.style.RadioSizeMedium);
        binding.calendarMonthDay.setTextAppearance(R.style.RadioSizeMedium);
        binding.textualDataDay.setTextAppearance(R.style.RadioSizeMedium);
        binding.calendarnight.setTextAppearance(R.style.RadioSizeMedium);
        binding.calendarMonth.setTextAppearance(R.style.RadioSizeMedium);
        binding.textualData.setTextAppearance(R.style.RadioSizeMedium);
    }

    public void forLarge() {
        binding.radioButtonDay.setTextAppearance(R.style.RadioSizeLarge);
        binding.calendarDay.setTextAppearance(R.style.RadioSizeLarge);
        binding.calendarMonthDay.setTextAppearance(R.style.RadioSizeLarge);
        binding.textualDataDay.setTextAppearance(R.style.RadioSizeLarge);
        binding.calendarnight.setTextAppearance(R.style.RadioSizeLarge);
        binding.calendarMonth.setTextAppearance(R.style.RadioSizeLarge);
        binding.textualData.setTextAppearance(R.style.RadioSizeLarge);
    }
}
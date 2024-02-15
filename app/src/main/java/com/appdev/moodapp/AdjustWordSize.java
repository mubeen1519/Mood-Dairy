package com.appdev.moodapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;

import com.appdev.moodapp.Utils.Utils;
import com.appdev.moodapp.databinding.ActivityAdjustWordSizeBinding;

public class AdjustWordSize extends AppCompatActivity {

    private ActivityAdjustWordSizeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdjustWordSizeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        if(Utils.isDarkModeActivated(AdjustWordSize.this)){
            Utils.status_bar_dark(AdjustWordSize.this, R.color.black);
        } else{
            Utils.status_bar(AdjustWordSize.this, R.color.lig_bkg);
        }
        SharedPreferences preferences = getSharedPreferences("text_size_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        String textSizeName = preferences.getString("text_size", "");
        switch (textSizeName) {
            case "medium":
                forMedium();
                binding.radioButtonMedium.setChecked(true);
                break;
            case "large":
                forLarge();
                binding.radioButtonBig.setChecked(true);
                break;
            default:
                forDefault();
                binding.radioButtonDefault.setChecked(true);
                break;
        }

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        binding.radioGroupCustom.setOnCheckedChangeListener((group, checkedId) -> {
            String finalSize = "";
            if (checkedId == R.id.radioButtonDefault) {
                finalSize = "default";
                binding.title.setText("Default size text");
                forDefault();
            } else if (checkedId == R.id.radioButtonMedium) {
                finalSize = "medium";
                binding.title.setText("Medium size text");
                forMedium();
            } else if (checkedId == R.id.radioButtonBig) {
                finalSize = "large";
                binding.title.setText("Large size text");
                forLarge();
            }

            editor.putString("text_size", finalSize);
            editor.apply();
        });
    }

    public void forDefault() {
        binding.radioButtonDefault.setTextAppearance(R.style.RadioSize);
        binding.radioButtonMedium.setTextAppearance(R.style.RadioSize);
        binding.radioButtonBig.setTextAppearance(R.style.RadioSize);
        binding.calendarDay.setTextAppearance(R.style.RadioSize);
        binding.calendarMonthDay.setTextAppearance(R.style.RadioSize);
        binding.textualDataDay.setTextAppearance(R.style.RadioSize);
    }

    public void forMedium() {
        binding.radioButtonDefault.setTextAppearance(R.style.RadioSizeMedium);
        binding.radioButtonMedium.setTextAppearance(R.style.RadioSizeMedium);
        binding.radioButtonBig.setTextAppearance(R.style.RadioSizeMedium);
        binding.calendarDay.setTextAppearance(R.style.RadioSizeMedium);
        binding.calendarMonthDay.setTextAppearance(R.style.RadioSizeMedium);
        binding.textualDataDay.setTextAppearance(R.style.RadioSizeMedium);
    }

    public void forLarge() {
        binding.radioButtonDefault.setTextAppearance(R.style.RadioSizeLarge);
        binding.radioButtonMedium.setTextAppearance(R.style.RadioSizeLarge);
        binding.radioButtonBig.setTextAppearance(R.style.RadioSizeLarge);
        binding.calendarDay.setTextAppearance(R.style.RadioSizeLarge);
        binding.calendarMonthDay.setTextAppearance(R.style.RadioSizeLarge);
        binding.textualDataDay.setTextAppearance(R.style.RadioSizeLarge);
    }
}
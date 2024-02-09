package com.appdev.moodapp.Utils;

import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

public class DailyData {
    private String date;
    private String emoji;
    private String textualData;
    private List<String> imageUris; // Store Uri strings instead of Uri objects


    public  DailyData(){}
    public DailyData(String date, String emoji, String textualData) {
        this.date = date;
        this.emoji = emoji;
        this.textualData = textualData;
    }
    public DailyData(String date, String emoji, String textualData, List<String> imageUris) {
        this.date = date;
        this.emoji = emoji;
        this.textualData = textualData;
        this.imageUris = imageUris;
    }


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getEmoji() {
        return emoji;
    }

    public void setEmoji(String emoji) {
        this.emoji = emoji;
    }

    public String getTextualData() {
        return textualData;
    }

    public void setTextualData(String textualData) {
        this.textualData = textualData;
    }

    public List<String> getImageUris() {
        return imageUris;
    }

    public void setImageUris(List<String> imageUris) {
        this.imageUris = imageUris;
    }
}
package com.appdev.moodapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.ramotion.paperonboarding.PaperOnboardingFragment;
import com.ramotion.paperonboarding.PaperOnboardingPage;
import com.ramotion.paperonboarding.listeners.PaperOnboardingOnRightOutListener;

import java.util.ArrayList;

public class OnboardingActivity extends AppCompatActivity {
    private FragmentManager fragmentManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        fragmentManager = getSupportFragmentManager();

        final PaperOnboardingFragment paperOnboardingFragment = PaperOnboardingFragment.newInstance(getDataforOnboarding());
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.add(R.id.onboardLayout, paperOnboardingFragment);

        fragmentTransaction.commit();

        paperOnboardingFragment.setOnRightOutListener(new PaperOnboardingOnRightOutListener() {
            @Override
            public void onRightOut() {
                Intent i = new Intent(OnboardingActivity.this, Login_screen.class);
                startActivity(i);
                finish();
            }
        });

    }

    private ArrayList<PaperOnboardingPage> getDataforOnboarding() {

        // the first string is to show the main title ,
        // second is to show the message below the
        // title, then color of background is passed ,
        // then the image to show on the screen is passed
        // and at last icon to navigate from one screen to other
        PaperOnboardingPage source = new PaperOnboardingPage("Mood dairy", "Welcome to Mood Dairy app", Color.parseColor("#ffffff"),R.drawable.diary, R.drawable.dot_com);
        PaperOnboardingPage source1 = new PaperOnboardingPage("Add Modes", "Record Your each day with us", Color.parseColor("#ffffff"),R.drawable.mood, R.drawable.dot_com);

        // array list is used to store
        // data of onbaording screen
        ArrayList<PaperOnboardingPage> elements = new ArrayList<>();

        // all the sources(data to show on screens)
        // are added to array list
        elements.add(source);
        elements.add(source1);
        return elements;
    }
}
package com.appdev.moodapp.Fragments;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.appdev.moodapp.CoursesAdapter;
import com.appdev.moodapp.ModelClasses.Courses;
import com.appdev.moodapp.R;
import com.appdev.moodapp.databinding.FragmentCategoryBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class CategoryFragment extends Fragment {

    private FragmentCategoryBinding binding; // Declare binding object

    private CoursesAdapter adapter;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment using data binding
        binding = FragmentCategoryBinding.inflate(inflater, container, false);




        binding.ListRc.setLayoutManager(new LinearLayoutManager(getContext()));

        // Create a list of Courses objects
        List<Courses> coursesList = new ArrayList<>();
        coursesList.add(new Courses("Understanding mental disorders", "10 Courses","https://googlee.com"));
        coursesList.add(new Courses("Mindfulness", "1 Courses","https://google.com"));
        coursesList.add(new Courses("Relaxation", "2 Courses","https://google.com"));
        coursesList.add(new Courses("Self-confidence", "6 Courses","https://google.com"));

        // Create and set the adapter for the RecyclerView
        adapter = new CoursesAdapter(getContext(), coursesList);
        binding.ListRc.setAdapter(adapter);

        return binding.getRoot();
    }
}
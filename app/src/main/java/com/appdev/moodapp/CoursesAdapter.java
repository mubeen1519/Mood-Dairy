package com.appdev.moodapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.appdev.moodapp.ModelClasses.Courses;
import com.appdev.moodapp.databinding.CatCardBinding;

import java.util.List;

public class CoursesAdapter extends RecyclerView.Adapter<CoursesAdapter.ViewHolder> {

    private final Context context;
    private final List<Courses> coursesList;

    public CoursesAdapter(Context context, List<Courses> coursesList) {
        this.context = context;
        this.coursesList = coursesList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CatCardBinding binding = CatCardBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Courses course = coursesList.get(position);
        holder.bind(course);
    }

    @Override
    public int getItemCount() {
        return coursesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final CatCardBinding binding;

        public ViewHolder(CatCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Courses course) {
            binding.title.setText(course.getTitle());
            binding.courseBtn.setText(course.getCourses());

            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {openURL(course.getUrl() );
                }
            });
            binding.courseBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openURL(course.getUrl());
                }
            });
        }
    }
    private void openURL(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Add this flag to start the activity in a new task
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        } else {
            Toast.makeText(context, "No app can handle this request", Toast.LENGTH_SHORT).show();
        }
    }
}


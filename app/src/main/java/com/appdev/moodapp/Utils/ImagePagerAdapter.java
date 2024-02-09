package com.appdev.moodapp.Utils;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.appdev.moodapp.R;

import java.util.List;

public class ImagePagerAdapter extends PagerAdapter {

    private List<Uri> imageUris;
    private LayoutInflater layoutInflater;

    public ImagePagerAdapter(List<Uri> imageUris, Context context) {
        this.imageUris = imageUris;
        this.layoutInflater = LayoutInflater.from(context); // Initialize layoutInflater
    }

    @Override
    public int getCount() {
        return imageUris.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = layoutInflater.inflate(R.layout.image_sample, container, false);
        ImageView imageView = view.findViewById(R.id.imageView);
        imageView.setImageURI(imageUris.get(position));
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}



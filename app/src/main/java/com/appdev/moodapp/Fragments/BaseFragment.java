package com.appdev.moodapp.Fragments;

import androidx.annotation.LayoutRes;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.appdev.moodapp.CalenderViewActivity;
import com.appdev.moodapp.R;
import com.appdev.moodapp.Utils.Utils;


public abstract class BaseFragment extends Fragment {

    public interface HasToolbar {
        Toolbar getToolbar(); // Return null to hide the toolbar
    }

    public interface HasBackButton {
    }

    public BaseFragment(@LayoutRes int layoutRes) {
        super(layoutRes);

    }

    protected abstract Integer getTitleRes();

    private Toolbar getActivityToolbar() {
        return ((CalenderViewActivity) requireActivity()).binding.activityToolbar;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (this instanceof HasToolbar) {
            Utils.makeGone(getActivityToolbar());
            ((AppCompatActivity) requireActivity()).setSupportActionBar(((HasToolbar) this).getToolbar());
        }

        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            if (this instanceof HasBackButton) {
                Integer titleRes = getTitleRes();
                if (titleRes != null) {
                    actionBar.setTitle(getString(titleRes));
                } else {
                    actionBar.setTitle("");
                }
                actionBar.setDisplayHomeAsUpEnabled(true);
            } else {
                actionBar.setDisplayHomeAsUpEnabled(false);
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (this instanceof HasToolbar) {
            Utils.makeVisible(getActivityToolbar());
            ((AppCompatActivity) requireActivity()).setSupportActionBar(getActivityToolbar());
        }

        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        ActionBar actionBar = activity.getSupportActionBar();
        if(actionBar!=null){
            if (this instanceof HasBackButton) {
                actionBar.setTitle(getString(R.string.activity_title_view));
            }
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }
}

package com.example.a202sgi_assignment.items;

import android.content.Context;

import androidx.fragment.app.Fragment;


public class ViewPagerItem {
    private String _title;
    private Fragment _fragment;

    public String getTitle() {
        return _title;
    }

    public void setTitle(Context context, int titleResource) {
        _title = context.getString(titleResource);
    }

    public void setTitle(String title) {
        _title = title;
    }

    public Fragment getFragment() {
        return _fragment;
    }

    public void setFragment(Fragment fragment) {
        _fragment = fragment;
    }
}

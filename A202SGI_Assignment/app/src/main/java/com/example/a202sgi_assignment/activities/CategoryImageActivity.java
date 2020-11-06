package com.example.a202sgi_assignment.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.a202sgi_assignment.R;
import com.example.a202sgi_assignment.adapters.CategoryIconAdapter;
import com.example.a202sgi_assignment.cores.BaseActivity;
import com.example.a202sgi_assignment.fragments.CategoryImageFragment;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class CategoryImageActivity extends BaseActivity implements CategoryIconAdapter.OnActionListener {
    public static final String ICON_URL="icon-url";

    private Fragment _fragment;

    @Override
    protected int ContentView() {
        return R.layout.activity_logged_template;
    }

    @Override
    protected void AttemptSave() {

    }

    @Override
    protected void AttemptDelete() {

    }

    @Override
    protected void AttemptSearch() {

    }

    @Override
    protected void AttemptAdd() {
        ((CategoryImageFragment)_fragment).addIcon();
    }

    @Override
    protected void AttemptFilter() {

    }

    @Override
    protected void AttemptRefresh() {

    }

    @Override
    protected int MenuResource() {
        return R.menu.add_only;
    }

    @Override
    protected boolean DisableActionMenu() {
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        if (savedInstanceState == null) {
            _fragment = CategoryImageFragment.newInstance();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.frame_container, _fragment).commit();

        }
    }

    @Override
    public void returnIcon(String url) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(ICON_URL, url);
        setResult(RESULT_OK, returnIntent);
        finish();
    }
}

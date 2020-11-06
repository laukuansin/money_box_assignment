package com.example.a202sgi_assignment.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import com.example.a202sgi_assignment.R;
import com.example.a202sgi_assignment.cores.BaseActivity;
import com.example.a202sgi_assignment.fragments.CategoryDetailFragment;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class CategoryDetailActivity extends BaseActivity {
    public static final String CATEGORY_NAME="category-name";
    public static final String CATEGORY_TYPE="category-type";
    private String categoryName="";
    private String categoryType="";

    public Fragment _fragment;
    @Override
    protected int ContentView() {
        return R.layout.activity_logged_template;
    }

    @Override
    protected void AttemptSave() {

    }

    @Override
    protected void AttemptDelete() {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Warning");
        builder.setMessage("Delete this category will also delete all records in this category");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ((CategoryDetailFragment)_fragment).removeCategory();
                finish();
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.show();
    }

    @Override
    protected void AttemptSearch() {

    }

    @Override
    protected void AttemptAdd() {

    }

    @Override
    protected void AttemptFilter() {

    }

    @Override
    protected void AttemptRefresh() {

    }

    @Override
    protected int MenuResource() {
        return R.menu.delete_only;
    }

    @Override
    protected boolean DisableActionMenu() {
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            if(bundle.containsKey(CATEGORY_NAME))
            {
                categoryName=bundle.getString(CATEGORY_NAME);
            }
            if(bundle.containsKey(CATEGORY_TYPE))
            {
                categoryType=bundle.getString(CATEGORY_TYPE);
            }
        }
        if (savedInstanceState == null ) {
            _fragment = CategoryDetailFragment.newInstance(categoryName,categoryType);
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.frame_container, _fragment).commit();

        }
    }
}

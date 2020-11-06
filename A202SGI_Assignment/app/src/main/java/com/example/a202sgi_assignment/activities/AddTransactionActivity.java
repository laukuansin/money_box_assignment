package com.example.a202sgi_assignment.activities;

import android.os.Bundle;
import android.view.WindowManager;

import com.example.a202sgi_assignment.R;
import com.example.a202sgi_assignment.adapters.ImageAdapter;
import com.example.a202sgi_assignment.cores.BaseActivity;
import com.example.a202sgi_assignment.fragments.AddTransactionFragment;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class AddTransactionActivity extends BaseActivity  implements ImageAdapter.OnActionListener{
    private Fragment _fragment;

    @Override
    protected int ContentView() {
        return R.layout.activity_logged_template;
    }

    @Override
    protected void AttemptSave() {
        ((AddTransactionFragment)_fragment).saveTransaction();
        hideSoftKeyboard();
    }

    @Override
    protected void AttemptDelete() {

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
        return R.menu.save_only;
    }

    @Override
    protected boolean DisableActionMenu() {
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);

        if (savedInstanceState == null ) {
            _fragment = AddTransactionFragment.newInstance();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.frame_container, _fragment).commit();

        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        hideSoftKeyboard();
    }

    @Override
    public void removeImage(int position) {
        ((AddTransactionFragment)_fragment).removeImage(position);
    }
}

package com.example.a202sgi_assignment.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;

import com.example.a202sgi_assignment.R;
import com.example.a202sgi_assignment.adapters.SelectCategoryAdapter;
import com.example.a202sgi_assignment.adapters.ViewPagerAdapter;
import com.example.a202sgi_assignment.cores.BaseActivity;
import com.example.a202sgi_assignment.domains.Category;
import com.example.a202sgi_assignment.fragments.SelectCategoryFragment;
import com.example.a202sgi_assignment.hanlders.ViewPagerHandler;
import com.example.a202sgi_assignment.items.ViewPagerItem;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

public class SelectCategoryActivity extends BaseActivity implements com.example.a202sgi_assignment.interfaces.ViewPager, SelectCategoryAdapter.OnActionListener {
    public static final String ICON_URL="icon-url";
    public static final String CATEGORY_TYPE="category-type";
    public static final String CATEGORY_NAME="category-name";
    private ViewPager _viewPager;
    private TabLayout _viewPagerTabs;
    private List<ViewPagerItem> _viewPagerItems;
    private ViewPagerAdapter _viewPagerAdapter;
    private int _defaultIndex = 0;

    public SelectCategoryActivity() {
        _viewPagerItems=new ArrayList<>();
    }

    @Override
    protected int ContentView() {
        return R.layout.activity_view_pager_template;
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

    }

    @Override
    protected void AttemptFilter() {

    }

    @Override
    protected void AttemptRefresh() {

    }

    @Override
    protected int MenuResource() {
        return 0;
    }

    @Override
    protected boolean DisableActionMenu() {
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        ViewPagerHandler handler = getViewPagerHandler();
        if (handler != null && handler.getViewPagerItems() != null) {
            _viewPagerItems = handler.getViewPagerItems();
        }

        _viewPager = findViewById(R.id.viewpager);
        _viewPager.setOffscreenPageLimit(2);
        _viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), _viewPagerItems);
        _viewPager.setAdapter(_viewPagerAdapter);

        int defaultViewPagerPageSelectedPosition = defaultViewPagerPageSelectedPosition();
        if (defaultViewPagerPageSelectedPosition >= 0 &&
                defaultViewPagerPageSelectedPosition < _viewPagerItems.size()) {
            selectPage(defaultViewPagerPageSelectedPosition);
        }
        _viewPagerTabs =  findViewById(R.id.tabs);

        if (!_viewPagerItems.isEmpty()) showTabs(_viewPager);
    }

    @Override
    public ViewPagerHandler getViewPagerHandler() {
        ViewPagerHandler viewPagerHandler = new ViewPagerHandler(this);

        viewPagerHandler.addPage("Expense", SelectCategoryFragment.newInstance("Expense"));
        viewPagerHandler.addPage("Income",SelectCategoryFragment.newInstance("Income"));
        return viewPagerHandler;
    }
    @Override
    public void selectPage(int position) {
        _viewPager.setCurrentItem(position);
    }


    @Override
    public void updateNavigationDrawerTopHandler(ViewPagerHandler viewPagerHandler, int defaultViewPagerPageSelectedPosition) {
        if (viewPagerHandler == null) viewPagerHandler = new ViewPagerHandler(this);
        _viewPagerItems.clear();
        _viewPagerItems.addAll(viewPagerHandler.getViewPagerItems());
        _viewPagerAdapter.notifyDataSetChanged();

        selectPage(defaultViewPagerPageSelectedPosition);

        if (!_viewPagerItems.isEmpty()) showTabs(_viewPager);
    }

    @Override
    public int defaultViewPagerPageSelectedPosition() {
        return _defaultIndex;
    }
    private void showTabs(ViewPager pager) {

        _viewPagerTabs.setTabsFromPagerAdapter(_viewPagerAdapter);
        _viewPagerTabs.setupWithViewPager(pager);

    }

    @Override
    public void returnCategory(Category category) {
        Intent returnIntent=new Intent();
        returnIntent.putExtra(ICON_URL,category.getCategoryImage());
        returnIntent.putExtra(CATEGORY_NAME,category.getCategoryName());
        returnIntent.putExtra(CATEGORY_TYPE,category.getCategoryType());
        setResult(RESULT_OK,returnIntent);
        finish();
    }
}

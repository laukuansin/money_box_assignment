package com.example.a202sgi_assignment.interfaces;


import com.example.a202sgi_assignment.hanlders.ViewPagerHandler;

public interface ViewPager {
    ViewPagerHandler getViewPagerHandler();

    void selectPage(int position);

    void updateNavigationDrawerTopHandler(ViewPagerHandler viewPagerHandler, int defaultViewPagerPageSelectedPosition);

    int defaultViewPagerPageSelectedPosition();
}

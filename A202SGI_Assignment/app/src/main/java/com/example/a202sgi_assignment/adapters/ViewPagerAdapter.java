package com.example.a202sgi_assignment.adapters;



import com.example.a202sgi_assignment.items.ViewPagerItem;

import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;


public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    private List<ViewPagerItem> mViewPagerItems;

    public ViewPagerAdapter(FragmentManager fm, List<ViewPagerItem> viewPagerItems) {
        super(fm);

        mViewPagerItems = viewPagerItems;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mViewPagerItems.get(position).getTitle();
    }

    @Override
    public int getCount() {
        return mViewPagerItems.size();
    }

    @Override
    public Fragment getItem(int position) {
        return mViewPagerItems.get(position).getFragment();
    }

    @Override
    public int getItemPosition(Object object) {
        if (object instanceof OnViewPagerAdapterListener) {
            ((OnViewPagerAdapterListener)object).ReloadData();
        }

        return super.getItemPosition(object);
    }

    public interface OnViewPagerAdapterListener {
        void ReloadData();
    }
}

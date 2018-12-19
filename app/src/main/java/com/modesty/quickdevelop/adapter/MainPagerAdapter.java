package com.modesty.quickdevelop.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

/**
 * Created by congtaowang on 2018/3/20.
 */

public class MainPagerAdapter extends FragmentStatePagerAdapter {

    private final List<Fragment> fragments;

    public MainPagerAdapter(FragmentManager fm, List<Fragment> fragments ) {
        super( fm );
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position ) {
        return fragments.get( position );
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}

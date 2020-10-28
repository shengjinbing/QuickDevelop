package com.modesty.quickdevelop.adapter;


import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

/**
 * Created by lixiang on 2020/10/9
 * Describe:
 */
public class VideoRegionFragmentPagerAdapter extends FragmentStatePagerAdapter {
    private List<Fragment> mFragments;

    public VideoRegionFragmentPagerAdapter(@NonNull FragmentManager fm, List<Fragment> mFragments ) {
        super(fm);
        this.mFragments = mFragments;
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

/*
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
*/

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }
}

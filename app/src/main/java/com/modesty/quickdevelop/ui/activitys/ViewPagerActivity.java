package com.modesty.quickdevelop.ui.activitys;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;


import com.modesty.quickdevelop.R;
import com.modesty.quickdevelop.adapter.VideoRegionFragmentPagerAdapter;
import com.modesty.quickdevelop.ui.fragments.Fragment1;
import com.modesty.quickdevelop.ui.fragments.Fragment2;
import com.modesty.quickdevelop.ui.fragments.FragmentThress;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ViewPagerActivity extends AppCompatActivity {

    @BindView(R.id.vp)
    ViewPager vp;
    @BindView(R.id.btn_update)
    Button btnUpdate;
    @BindView(R.id.btn_fragment1)
    Button btnFragment1;
    @BindView(R.id.btn_fragment2)
    Button btnFragment2;

    private List<Fragment> mFragments;
    private VideoRegionFragmentPagerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pager);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        mFragments = new ArrayList();
        mFragments.add(Fragment1.newInstance());
        mFragments.add(Fragment2.newInstance());
        mAdapter = new VideoRegionFragmentPagerAdapter(getSupportFragmentManager(), mFragments);
        vp.setCurrentItem(0);
        vp.setAdapter(mAdapter);
    }

    @OnClick({R.id.btn_update, R.id.btn_fragment1, R.id.btn_fragment2})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_update:
                mFragments.add(FragmentThress.newInstance());
                mAdapter.notifyDataSetChanged();
                break;
            case R.id.btn_fragment1:
                Fragment1 fragment1 = (Fragment1) mFragments.get(0);
                fragment1.updateUI();
                break;
            case R.id.btn_fragment2:
                Fragment2 fragment2 = (Fragment2) mFragments.get(1);
                fragment2.updateUI();
                break;
            default:
                break;
        }
    }
}
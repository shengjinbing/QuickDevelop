package com.modesty.quickdevelop.ui.activitys;

import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.modesty.quickdevelop.R;
import com.modesty.quickdevelop.ui.fragments.FragmentOne;
import com.modesty.quickdevelop.ui.fragments.FragmentThress;
import com.modesty.quickdevelop.ui.fragments.FragmentTwo;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

/**
 * Toobar有app:layout_scrollFlags属性
 * 1.scroll: 所有想滚动出屏幕的view都需要设置这个flag， 没有设置这个flag的view将被固定在屏幕顶部。例如，TabLayout 没有设置这个值，将会停留在屏幕顶部。
 * 2.enterAlways: 设置这个flag时，向下的滚动都会导致该view变为可见，启用快速“返回模式”。
 * 3.enterAlwaysCollapsed: 当你的视图已经设置minHeight属性又使用此标志时，你的视图只能已最小高度进入，只有当滚动视图到达顶部时才扩大到完整高度。
 * 4.exitUntilCollapsed: 滚动退出屏幕，最后折叠在顶端。
 *
 * CoordinatorLayout包含的子视图中带有滚动属性的View需要设置app:layout_behavior属性。例如，示例中Viewpager设置了此属性。
 * app:layout_behavior="@string/appbar_scrolling_view_behavior"//系统默认属性
 */
public class CoordinatorLayoutActivity extends AppCompatActivity {

    private AppBarLayout mAppBarLayout;
    private Toolbar mToolbar;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    private List<String> mTitleList = new ArrayList<>();//页卡标题集合
    private List<Fragment> mFragmentList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coordinator_layout);
        mAppBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        initView();
        initData();
        initListener();
    }

    private void initView() {
        mTitleList.add("TAB1");
        mTitleList.add("TAB2");
        mTitleList.add("TAB3");

        mFragmentList.add(FragmentOne.newInstance());
        mFragmentList.add(FragmentTwo.newInstance());
        mFragmentList.add(FragmentThress.newInstance());

        MyPageAdapter myPageAdapter = new MyPageAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(myPageAdapter);
        mTabLayout.setupWithViewPager(mViewPager);//将TabLayout和ViewPager关联起来。
        mViewPager.setOffscreenPageLimit(4);
        mViewPager.setCurrentItem(0);
    }

    private void initData() {

    }

    private void initListener() {

    }

    /**
     * 将fragment都保存在内存中
     */
    public class MyPageAdapter extends FragmentPagerAdapter {

        public MyPageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return mTitleList.get(position);
        }
    }

}

package com.modesty.quickdevelop.ui.activitys;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import com.modesty.quickdevelop.R;
import com.modesty.quickdevelop.adapter.MainPagerAdapter;
import com.modesty.quickdevelop.nav.NavBar;
import com.modesty.quickdevelop.nav.NavItem;
import com.modesty.quickdevelop.ui.fragments.FragmentFour;
import com.modesty.quickdevelop.ui.fragments.FragmentOne;
import com.modesty.quickdevelop.ui.fragments.FragmentThress;
import com.modesty.quickdevelop.ui.fragments.FragmentTwo;
import com.modesty.quickdevelop.ui.view.NoScrollViewPager;

import java.util.ArrayList;
import java.util.List;

/**
 * //事务添加回退栈
 * //transaction.addToBackStack(null);
 * <p>
 * 1.detach()：将视图View和Fragment分离，视图View也会从ViewTree中删除，还会将Fragment从add的队列中删除，
 * 所以在调用isAdd方法的时候返回的是false，但实例对象本身是还存在的，通过FragmentManager的findFragmentByTag还可以获取到实例对象。
 * 2.attach()：通过fragment的onCreateView()的重建视图，并且被重新加入到add的队列中，并且处于队列头部。
 * <p>
 * commit() 需要在宿主 Activity 保存状态之前调用，否则会报错。
 * 这是因为如果 Activity 出现异常需要恢复状态，在保存状态之后的 commit() 将会丢失，这和调用的初衷不符，所以会报错。
 * <p>
 * 1.commit() 异步
 * 2.commitAllowingStateLoss()异步  允许在 Activity 保存状态之后调用，也就是说它遇到状态丢失不会报错。
 * 因此我们一般在界面状态出错是可以接受的情况下使用它。
 * 3.commitNow()同步
 * 4.commitNowAllowingStateLoss()同步
 */
public class FragmentActivity extends AppCompatActivity implements NavBar.OnNavItemClickCallback, FragmentFour.OnFragmentInteractionListener {
  /*  Fragment、FragmentManager、FragmentTransaction 关系
    Fragment
    其实是对 View 的封装，它持有 view, containerView, fragmentManager, childFragmentManager 等信息
    FragmentManager
    是一个抽象类，它定义了对一个 Activity/Fragment 中 添加进来的 Fragment 列表、Fragment 回退栈的操作、管理方法
            还定义了获取事务对象的方法具体实现在 FragmentImpl 中
    FragmentTransaction
    定义了对 Fragment 添加、替换、隐藏等操作，还有四种提交方法
    具体实现是在 BackStackRecord 中*/
    private static final int MAX_COUNT = 4;
    private static final String CURRENT_PAGE = "currentPage";
    private MainPagerAdapter mainPagerAdapter;
    private NoScrollViewPager mPager;
    private FrameLayout mFrameLayout;
    private NavBar mNavBar;
    private FragmentManager mFragmentManager;
    private List<Fragment> mFragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);
        mPager = (NoScrollViewPager) findViewById(R.id.vp_pagers);
        mFrameLayout = (FrameLayout) findViewById(R.id.fl);
        mNavBar = (NavBar) findViewById(R.id.nav_bar);
        //initViewPager(savedInstanceState);
        initViewFragment(savedInstanceState);

    }

    private void initViewPager(Bundle savedInstanceState) {
        mPager.setVisibility(View.VISIBLE);

        int position = NavItem.CAR.getPageIndex();
        //内存重启处理，fragment状态会被保存，直接恢复不需重新创建
        List<Fragment> fragments = new ArrayList<>();
        List<Fragment> cacheFragments = getSupportFragmentManager().getFragments();
        if (savedInstanceState == null || cacheFragments == null || cacheFragments.size() != MAX_COUNT) {
            fragments.add(FragmentOne.newInstance());
            fragments.add(FragmentTwo.newInstance());
            fragments.add(FragmentThress.newInstance());
            fragments.add(FragmentFour.newInstance(null, null));
        } else {
            fragments = cacheFragments;
            position = savedInstanceState.getInt(CURRENT_PAGE, NavItem.CAR.getPageIndex());
        }
        this.mainPagerAdapter = new MainPagerAdapter(getSupportFragmentManager(), fragments);
        mPager.setOffscreenPageLimit(MAX_COUNT);
        mPager.setAdapter(mainPagerAdapter);
        mNavBar.setOnNavItemClickCallback(this);
        mNavBar.setNavItemCount(MAX_COUNT);
        final View view = mNavBar.findNavItemBy(NavItem.getNav(position));
        if (view != null) {
            view.performClick();
        }
    }

    private void initViewFragment(Bundle savedInstanceState) {
        mFrameLayout.setVisibility(View.VISIBLE);

        //初始化fragment
        mFragments = new ArrayList<>();
        List<Fragment> cacheFragments = getSupportFragmentManager().getFragments();
        if (savedInstanceState == null || cacheFragments == null || cacheFragments.size() != MAX_COUNT) {
            mFragments.add(FragmentOne.newInstance());
            mFragments.add(FragmentTwo.newInstance());
            mFragments.add(FragmentThress.newInstance());
            mFragments.add(FragmentFour.newInstance(null, null));
        } else {
            mFragments = cacheFragments;
        }

        //获取fragment管理器，与片段交互
        mFragmentManager = getSupportFragmentManager();

        //第一次显示哪个fragment
        int position = NavItem.CAR.getPageIndex();
        mNavBar.setOnNavItemClickCallback(this);
        mNavBar.setNavItemCount(MAX_COUNT);
        final View view = mNavBar.findNavItemBy(NavItem.getNav(position));
        if (view != null) {
            view.performClick();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(CURRENT_PAGE, mPager.getCurrentItem());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onNavItemClicked(View lastClickedView, View view) {
        if (view != null && view.getTag(R.id.nav_item_type) instanceof NavItem) {
            final NavItem type = (NavItem) view.getTag(R.id.nav_item_type);
            if (mPager.getVisibility() == View.VISIBLE) {
                mPager.setCurrentItem(type.getPageIndex(), false);
            } else {
                //获取事务，一个事务只能提交一次
                FragmentTransaction transaction = mFragmentManager.beginTransaction();
                //显示或者隐藏fragment
                changeShow(mFragments.get(type.getPageIndex()), transaction, null);
            }
            //可以进行埋点分析
        }
    }


    /**
     * BackStackRecord 既是对 Fragment 进行操作的事务的真正实现，也是 FragmentManager 中的回退栈的实现
     *
     * @param showFrag
     * @param transaction
     * @param tag
     */
    private void changeShow(Fragment showFrag, FragmentTransaction transaction, String tag) {
        if (showFrag.isAdded()) {
            transaction.show(showFrag);
        } else {
            transaction.add(R.id.fl, showFrag, tag);
        }
        for (Fragment fragment : mFragments) {
            if (fragment != showFrag && fragment.isAdded()) {
                transaction.hide(fragment);
            }
        }
        transaction.commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public void startActivity(View view) {
        startActivity(new Intent(this, OkHttpActivity.class));
    }
}

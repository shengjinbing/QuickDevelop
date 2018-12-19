package com.modesty.quickdevelop.nav;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.modesty.quickdevelop.R;


/**
 * Created by congtaowang on 2017/12/28.
 */

public class NavBar extends FrameLayout {

    private View mLastSelectedView = null;

    LinearLayout mContainer;
    ImageView bkgCursor;

    private OnNavItemClickCallback mOnNavItemClickCallback;
    private int mNavItemCount = 3;

    public NavBar(Context context) {
        this(context, null);
    }

    public NavBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NavBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {

        LayoutInflater.from(context).inflate(R.layout.layout_nav_bar, this, true);
        mContainer=(LinearLayout) findViewById(R.id.ll_nav_item_container);
        bkgCursor=(ImageView) findViewById(R.id.zdc_id_bkg_cursor);

        LayoutParams params = ((LayoutParams) mContainer.getLayoutParams());
        params.height = BitmapHelper.dip2px(context, 50);
        mContainer.setLayoutParams(params);

        addNavItem(NavItem.CAR);
        addNavItem(NavItem.SMART_GUIDE);
        addNavItem(NavItem.SERVICES);
        addNavItem(NavItem.PERSONAL);
    }

    public NavBar addNavItem(NavItemDescriptor descriptor) {
        final View navItemView = getNavItem(descriptor, getContext());
        if (navItemView != null) {
            mContainer.addView(navItemView, getNavItemParams());
        }
        return this;
    }

    public void setNavItemCount(int navItemCount) {
        if (navItemCount < 1) {
            throw new IllegalArgumentException("nav bar item count can't be < 1");
        }
        this.mNavItemCount = navItemCount;
    }

    private LinearLayout.LayoutParams getNavItemParams() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.weight = 1f / mNavItemCount;
        return params;
    }

    private <T extends NavItemDescriptor> View getNavItem(T navItem, Context context) {

        if (navItem == null) {
            return null;
        }

        View itemView = LayoutInflater.from(context).inflate(R.layout.layout_nav_item, null);
        final ImageView navIcon = itemView.findViewById(R.id.iv_nav_icon);
        final TextView navName = itemView.findViewById(R.id.tv_nav_name);
//        final View navItemView = itemView.findViewById( R.id.ll_nav_item );
        navIcon.setImageResource(navItem.getNavItemIconRes());
        navName.setText(navItem.getNavItemNameRes());

        itemView.setId(navItem.getIdRes());
        itemView.setTag(R.id.nav_item_type, navItem);
        itemView.setOnClickListener(new OnNavItemClickListener());

        return itemView;
    }

    public View findNavItemBy(NavItemDescriptor descriptor) {
        if (descriptor == null) {
            return null;
        }
        final View navItemView = findViewById(descriptor.getIdRes());
        return navItemView;
    }

    public void performItemClick(NavItem navItem) {
        final View view = findNavItemBy(navItem);
        if (view != null) {
            view.performClick();
        }
    }

    public <T> void setOnNavItemClickCallback(OnNavItemClickCallback onNavItemClickCallback) {
        this.mOnNavItemClickCallback = onNavItemClickCallback;
    }

    private class OnNavItemClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            if (mLastSelectedView == v) {
                return;
            }
            if (mOnNavItemClickCallback == null) {
                return;
            }

            markSelectedView(v);
            mOnNavItemClickCallback.onNavItemClicked(mLastSelectedView, v);
            mLastSelectedView = v;
        }
    }

    private void markSelectedView(View view) {

        if (view == null || mLastSelectedView == view) {
            return;
        }

        if (mContainer == null) {
            return;
        }
        for (int i = 0; i < mContainer.getChildCount(); i++) {
            final View childView = mContainer.getChildAt(i);
            if (childView == null) {
                continue;
            }
            final ImageView navIcon = childView.findViewById(R.id.iv_nav_icon);
            final TextView navName = childView.findViewById(R.id.tv_nav_name);
            if (view == childView) {
                navIcon.setSelected(true);
                navName.setSelected(true);
            } else {
                navIcon.setSelected(false);
                navName.setSelected(false);
            }
        }

//        if (view.getTag(R.id.nav_item_type) == NavItem.SMART_GUIDE) {
//            bkgCursor.setVisibility(View.VISIBLE);
//        } else {
//            bkgCursor.setVisibility(View.GONE);
//        }

//        moveCursor( view );
    }

    private void moveCursor(final View view) {
        if (mLastSelectedView == view || view == null) {
            return;
        }
        view.post(new Runnable() {
            @Override
            public void run() {
                LayoutParams params = ((LayoutParams) bkgCursor.getLayoutParams());
                params.leftMargin = view.getLeft() + (view.getMeasuredWidth() - bkgCursor.getMeasuredWidth()) / 2;
                bkgCursor.setLayoutParams(params);
            }
        });
    }

    public <T> T getCurrentNavItem() {
        if (mLastSelectedView == null) {
            return null;
        }
        return (T) mLastSelectedView.getTag(R.id.nav_item_type);
    }

    public interface OnNavItemClickCallback {

        void onNavItemClicked(View lastClickedView, View view);
    }

}

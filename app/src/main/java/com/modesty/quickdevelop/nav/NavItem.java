package com.modesty.quickdevelop.nav;

import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.StringRes;

import com.modesty.quickdevelop.R;


/**
 * Created by congtaowang on 2018/2/1.
 */

public enum NavItem implements NavItemDescriptor {

    CAR(R.drawable.nav_item_car, R.string.str_nav_item_car, R.id.nav_item_car, 0),
    SMART_GUIDE(R.drawable.nav_item_smart_guide, R.string.str_nav_item_smart_guide, R.id.nav_item_smart_guide, 1),
    SERVICES(R.drawable.nav_item_services, R.string.str_nav_item_services, R.id.nav_item_services, 2),
    PERSONAL(R.drawable.nav_item_personal, R.string.str_nav_item_personal, R.id.nav_item_personal, 3);

    @DrawableRes
    private int navItemIconRes;

    @StringRes
    private int navItemNameRes;

    @IdRes
    private int idRes;

    private int pageIndex;

    NavItem(int navItemIconRes, int navItemNameRes, int idRes, int pageIndex) {
        this.navItemIconRes = navItemIconRes;
        this.navItemNameRes = navItemNameRes;
        this.idRes = idRes;
        this.pageIndex = pageIndex;
    }

    @Override
    public int getPageIndex() {
        return pageIndex;
    }

    @Override
    public int getIdRes() {
        return idRes;
    }

    @Override
    public int getNavItemIconRes() {
        return navItemIconRes;
    }

    @Override
    public int getNavItemNameRes() {
        return navItemNameRes;
    }

    public static NavItem getNav(int position) {
        if (position == SMART_GUIDE.getPageIndex()) {
            return SMART_GUIDE;
        }
        if (position == CAR.getPageIndex()) {
            return CAR;
        }
        if (position == SERVICES.getPageIndex()) {
            return SERVICES;
        }
        if (position == PERSONAL.getPageIndex()) {
            return PERSONAL;
        }
        return null;
    }
}

package com.modesty.quickdevelop.network.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.modesty.logger.simplelog.Logger;


/**
 * @author wangzhiyuan
 * @since 2017/5/10
 */

public class ProgressDialog {

    public static final String LOADING_DIALOG_TAG = "loading_dialog_tag";

    private ProgressDialogFragment mProgressDialogFragment;
    private FragmentManager mFragmentManager;

    public void showLoadingDialog(@NonNull Context context, String loadingMassage) {
        showLoadingDialog(context, loadingMassage, true);
    }

    public void showLoadingDialog(@NonNull Context context, String loadingMassage, boolean cancelable) {
        showLoadingDialog(context, loadingMassage, cancelable, false);
    }

    public void showLoadingDialog(Context context, String loadingMassage, boolean cancelable, boolean cancelableOnTouchOutSide) {
        if (context != null && context instanceof FragmentActivity) {
            mFragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
        }

        if (mFragmentManager == null) {
            return;
        }

        mProgressDialogFragment = new ProgressDialogFragment();
        mProgressDialogFragment.setContent(loadingMassage, cancelable, cancelableOnTouchOutSide);

        try {
            FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
            fragmentTransaction.add(mProgressDialogFragment, LOADING_DIALOG_TAG);
            fragmentTransaction.commitAllowingStateLoss();
        } catch (IllegalStateException e) {
            Logger.e(e, e.getMessage());
            mFragmentManager = null;
            mProgressDialogFragment = null;
        }
    }

    public void removeLoadingDialog() {
        if (mProgressDialogFragment != null) {
            try {
                mProgressDialogFragment.dismissAllowingStateLoss();
            } catch (Exception e) {
                Logger.e("dialog 取消异常");
            } finally {
                mProgressDialogFragment = null;
                mFragmentManager = null;
            }
        }
    }

}


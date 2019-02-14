package com.modesty.quickdevelop.ui.activitys;

import android.content.Context;
import android.graphics.PixelFormat;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.modesty.quickdevelop.R;
import com.modesty.quickdevelop.utils.permissiongen.PermissionChecker;
import com.modesty.quickdevelop.utils.permissiongen.PermissionFail;
import com.modesty.quickdevelop.utils.permissiongen.PermissionGen;
import com.modesty.quickdevelop.utils.permissiongen.PermissionSetup;
import com.modesty.quickdevelop.utils.permissiongen.PermissionSuccess;

import java.util.List;

public class WindowManagerTestActivity extends AppCompatActivity implements View.OnTouchListener {

    private static final String TAG = "TestActivity";

    private Button mCreateWindowButton;

    private Button mFloatingButton;
    private WindowManager.LayoutParams mLayoutParams;
    private WindowManager mWindowManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_window_manager_test);
        initView();
    }
    private void initView() {
        mCreateWindowButton = (Button) findViewById(R.id.button1);
        mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
    }

    public void onButtonClick(View v) {
        if (v == mCreateWindowButton) {
            PermissionSetup.requestPermission(this, PermissionChecker.Permissions.WINDOWS);
        }
        AddButtonToWindow();
    }

    private void AddButtonToWindow() {
        mFloatingButton = new Button(this);
        mFloatingButton.setText("click me");
        mLayoutParams = new WindowManager.LayoutParams();
        mLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED;
       // mLayoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        mLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mLayoutParams.gravity = Gravity.LEFT | Gravity.TOP;
        mLayoutParams.x = 100;
        mLayoutParams.y = 300;
        mFloatingButton.setOnTouchListener(this);
        mWindowManager.addView(mFloatingButton, mLayoutParams);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int rawX = (int) event.getRawX();
        int rawY = (int) event.getRawY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                int x = (int) event.getX();
                int y = (int) event.getY();
                mLayoutParams.x = rawX;
                mLayoutParams.y = rawY;
                mWindowManager.updateViewLayout(mFloatingButton, mLayoutParams);
                break;
            }
            case MotionEvent.ACTION_UP: {
                break;
            }
            default:
                break;
        }

        return false;
    }

    @Override
    protected void onDestroy() {
        try {
            mWindowManager.removeView(mFloatingButton);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }


    @PermissionSuccess
    public void onPermissionSuccess(int requestCode) {
        Toast.makeText(getApplicationContext(),"获取权限成功",Toast.LENGTH_SHORT).show();
        AddButtonToWindow();
    }

    @PermissionFail
    public void onPermissionFailed(int requestCode, List<String> deniedInfo) {
        Toast.makeText(getApplicationContext(),"获取权限失败",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionGen.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }
}

package com.modesty.quickdevelop.ui.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.modesty.quickdevelop.R;
import com.modesty.quickdevelop.ui.activitys.frame.LeakCanaryActivity;

public class FrameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frame);
    }

    public void leakCanary(View view) {
        startActivity(new Intent(this, LeakCanaryActivity.class));
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d(LeakCanaryActivity.TAG,LeakCanaryActivity.referenceQueue.poll()+"");
            }
        },5000);
    }
}
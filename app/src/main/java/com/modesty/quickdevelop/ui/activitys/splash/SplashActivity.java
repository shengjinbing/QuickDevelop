package com.modesty.quickdevelop.ui.activitys.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.modesty.quickdevelop.MainActivity;
import com.modesty.quickdevelop.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SplashActivity extends AppCompatActivity {

    @BindView(R.id.btn_count_down)
    Button btnCountDown;

    int sumTime = 3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        CountDownTimer start = new CountDownTimer(3000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                btnCountDown.setText("跳过" + (--sumTime));
            }

            @Override
            public void onFinish() {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
            }
        }.start();
        btnCountDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start.cancel();
                startActivity(new Intent(SplashActivity.this,MainActivity.class));
            }
        });
    }
}

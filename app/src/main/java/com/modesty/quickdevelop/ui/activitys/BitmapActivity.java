package com.modesty.quickdevelop.ui.activitys;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.modesty.quickdevelop.R;
import com.modesty.quickdevelop.utils.image.ImageCompressUtil;

public class BitmapActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bitmap);
        initView();
    }

    private void initView() {
        ImageView image1 = (ImageView) findViewById(R.id.image1);
        TextView tv1 = (TextView) findViewById(R.id.tv);



    }
}

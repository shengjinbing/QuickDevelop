package com.modesty.quickdevelop.ui.activitys;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;

import com.modesty.quickdevelop.R;
import com.modesty.quickdevelop.ui.view.HorizontalScrollViewEx;

public class ScrollActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scroll);
        initView();
    }

    private void initView() {
        LayoutInflater inflater = getLayoutInflater();
        HorizontalScrollViewEx scrollViewEx = (HorizontalScrollViewEx) findViewById(R.id.container);

    }


}

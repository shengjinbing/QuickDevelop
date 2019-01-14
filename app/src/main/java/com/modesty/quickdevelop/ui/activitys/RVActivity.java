package com.modesty.quickdevelop.ui.activitys;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.modesty.quickdevelop.R;

public class RVActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rv);
    }

    public void singleRV(View view) {
        startActivity(new Intent(this, RecyclerViewActivity.class));
    }

    public void mulRV(View view) {
        startActivity(new Intent(this, MultiItemRvActivity.class));
    }

    public void refresh(View view) {
        startActivity(new Intent(this, RefreshRecyclerViewActivity.class));
    }

    public void mulheadRV(View view) {

    }


}

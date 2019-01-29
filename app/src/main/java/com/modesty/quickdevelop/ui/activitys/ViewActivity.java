package com.modesty.quickdevelop.ui.activitys;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.modesty.quickdevelop.R;

public class ViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
    }

    public void recyclerview(View view) {
       startActivity(new Intent(this,RVActivity.class));
    }

    public void listview(View view) {
        startActivity(new Intent(this,ListViewActivity.class));
    }

    public void coordinatorLayout(View view) {
        startActivity(new Intent(this,CoordinatorLayoutActivity.class));
    }

    public void collapsing(View view) {
        startActivity(new Intent(this,CollapsingActivity.class));
    }

    public void behavior(View view) {
        startActivity(new Intent(this,BehaviorActivity.class));
    }

    public void touchevent(View view) {
        startActivity(new Intent(this,DispatchActivity.class));
    }

    public void scroll(View view) {
        startActivity(new Intent(this,ScrollActivity.class));
    }

    public void Window(View view) {
        startActivity(new Intent(this,WindowMSActivity.class));
    }

    public void GlideImage(View view) {
        startActivity(new Intent(this,ImageActivity.class));
    }

    public void Bitmap(View view) {
        startActivity(new Intent(this,BitmapActivity.class));
    }

    public void optimization(View view) {
        startActivity(new Intent(this,OptimizationActivity.class));
    }

    public void customView(View view) {
        startActivity(new Intent(this,CustomViewActivity.class));

    }
}

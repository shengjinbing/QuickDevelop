package com.modesty.quickdevelop;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.modesty.quickdevelop.ui.activitys.FragmentActivity;
import com.modesty.quickdevelop.ui.activitys.HandlerActivity;
import com.modesty.quickdevelop.ui.activitys.OkHttpActivity;
import com.modesty.quickdevelop.ui.activitys.RxJavaActivity;
import com.modesty.quickdevelop.ui.activitys.ViewActivity;
import com.modesty.quickdevelop.ui.activitys.VolleyActivity;
import com.modesty.quickdevelop.volley.VolleyManage;

import org.reactivestreams.Subscription;



import okhttp3.logging.HttpLoggingInterceptor;

/**
 * @author 1
 * /*
 * api：跟2.x版本的 compile完全相同
 * provided（compileOnly）
   只在编译时有效，不会参与打包
   可以在自己的moudle中使用该方式依赖一些比如com.android.support，gson这些使用者常用的库，避免冲突。
 *
 * 总结起来：如果api依赖，一个module发生变化，这条依赖链上所有的module都需要重新编译；而implemention，只有直接依赖这个module需要重新编译。
 * 如果都是本地依赖，implementation相比api，主要优势在于减少build time
 *
 * 全部远程依赖模式下，无论是api还是implemention都起不到依赖隔离的作用
 *
 */
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * RecyclerView和ListView
     *
     * @param view
     */
    public void viewmain(View view) {
        Intent intent = new Intent(this, ViewActivity.class);
        startActivity(intent);
    }

    /**
     * Okhttp
     *
     * @param view
     */
    public void okhttpmain(View view) {
        startActivity(new Intent(this, OkHttpActivity.class));
    }
    /**
     * rxJava
     *
     * @param view
     */
    public void rxjavamain(View view) {
        startActivity(new Intent(this, RxJavaActivity.class));
    }


    public void volley(View view) {
        startActivity(new Intent(this, VolleyActivity.class));
    }

    public void fragment(View view) {
        startActivity(new Intent(this, FragmentActivity.class));
    }

    public void Handler(View view) {
        startActivity(new Intent(this, HandlerActivity.class));
    }
}

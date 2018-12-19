package com.modesty.quickdevelop.ui.activitys;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.modesty.quickdevelop.R;
import com.modesty.quickdevelop.volley.VolleyManage;

import java.util.HashMap;
import java.util.Map;

/**
 * Volley是Google官方出的一套小而巧的异步请求库，该框架封装的扩展性很强，支持HttpClient、HttpUrlConnection，
 *  甚至支持OkHttp，而且Volley里面也封装了ImageLoader，所以如果你愿意你甚至不需要使用图片加载框架，
 * 不过这块功能没有一些专门的图片加载框架强大，对于简单的需求可以使用，
 * 稍复杂点的需求还是需要用到专门的图片加载框架。Volley也有缺陷，比如不支持post大数据，所以不适合上传文件。
 * 不过Volley设计的初衷本身也就是为频繁的、数据量小的网络请求而生。
 */
public class VolleyActivity extends AppCompatActivity {
    public static final String TAG = "Volley_LOG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volley);

    }

    /**
     * @param view
     */
    public void StringRequest(View view) {
        VolleyManage instance = VolleyManage.getInstance(this);
        String url = "https://api.ekchange.com/common/searchList";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG,response);
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG,"That didn't work!");
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {  //设置头信息
                Map<String, String> map = new HashMap<String, String>();
                map.put("Content-Type", "application/x-www-form-urldecoded");
                return map;
            }
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {  //设置参数
                Map<String, String> map = new HashMap<String, String>();
                map.put("name", "张立");
                return map;
            }
        };
        stringRequest.setTag("StringRequest");
        instance.addToRequestQueue(stringRequest);
    }

    @Override
    protected void onStop() {
        super.onStop();
        VolleyManage.getInstance(this).cancelAll("StringRequest");
    }
}

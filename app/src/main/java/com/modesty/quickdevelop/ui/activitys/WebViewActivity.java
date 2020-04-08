package com.modesty.quickdevelop.ui.activitys;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.Toast;

import com.modesty.quickdevelop.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnPageChange;

public class WebViewActivity extends AppCompatActivity {

    @BindView(R.id.et_content)
    EditText mEtContent;
    @BindView(R.id.wv)
    WebView mWv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        ButterKnife.bind(this);
        initWebViewSetting();
        initWebListener();
        initView();
    }

    private void initView() {
        //加载本地html文件
        mWv.loadUrl("file:///android_asset/JavaAndJavaScriptCall.html");
        mWv.addJavascriptInterface(new Jsinterface(), "Android");
    }

    class Jsinterface {
        /**
         * 1.如果混淆了，@JavascriptInterface注解的方法可能就没了，结果是，JS就没办法知己调用对应的方法，
         * 导致通信失败
         * 2.4.2以后，WebView会禁止JS调用没有添加@JavascriptInterface方法, 解决了安全漏洞，而且很少
         * APP兼容到4.2以前，安全问题可以忽略。
         * 3.JavascriptInterface注入的方法被js调用时，可以看做是一个同步调用，虽然两者位于不同线程，但是应该存在
         * 一个等待通知的机制来保证，所以Native中被回调的方法里尽量不要处理耗时操作
         */
        @JavascriptInterface
        public void showToast(String arg) {
            Toast.makeText(WebViewActivity.this, arg, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * java调用js
     *
     * @param view
     */
    public void javacalljs(View view) {
        String content = mEtContent.getText().toString().trim();
        mWv.loadUrl("javascript:javaCallJs(" + "'" + content + "'" + ")");
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void initWebViewSetting() {
        WebSettings settings = mWv.getSettings();
// 存储(storage)
// 启用HTML5 DOM storage API，默认值 false
        settings.setDomStorageEnabled(true);
// 启用Web SQL Database API，这个设置会影响同一进程内的所有WebView，默认值 false
// 此API已不推荐使用，参考：https://www.w3.org/TR/webdatabase/
        settings.setDatabaseEnabled(true);
// 启用Application Caches API，必需设置有效的缓存路径才能生效，默认值 false
// 此API已废弃，参考：https://developer.mozilla.org/zh-CN/docs/Web/HTML/Using_the_application_cache
        settings.setAppCacheEnabled(true);
        settings.setAppCachePath(getCacheDir().getAbsolutePath());
// 定位(location)
        settings.setGeolocationEnabled(true);
// 是否保存表单数据
        settings.setSaveFormData(true);
// 是否当webview调用requestFocus时为页面的某个元素设置焦点，默认值 true
        settings.setNeedInitialFocus(true);
// 是否支持viewport属性，默认值 false
// 页面通过`<meta name="viewport" ... />`自适应手机屏幕
        settings.setUseWideViewPort(true);
// 是否使用overview mode加载页面，默认值 false
// 当页面宽度大于WebView宽度时，缩小使页面宽度等于WebView宽度
        settings.setLoadWithOverviewMode(true);
// 布局算法
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
// 是否支持Javascript，默认值false
        settings.setJavaScriptEnabled(true);
// 是否支持多窗口，默认值false
        settings.setSupportMultipleWindows(false);
// 是否可用Javascript(window.open)打开窗口，默认值 false
        settings.setJavaScriptCanOpenWindowsAutomatically(false);
// 资源访问
        settings.setAllowContentAccess(true); // 是否可访问Content Provider的资源，默认值 true
        settings.setAllowFileAccess(true);    // 是否可访问本地文件，默认值 true
// 是否允许通过file url加载的Javascript读取本地文件，默认值 false
        settings.setAllowFileAccessFromFileURLs(false);
// 是否允许通过file url加载的Javascript读取全部资源(包括文件,http,https)，默认值 false
        settings.setAllowUniversalAccessFromFileURLs(false);
// 资源加载
        settings.setLoadsImagesAutomatically(true); // 是否自动加载图片
        settings.setBlockNetworkImage(false);       // 禁止加载网络图片
        settings.setBlockNetworkLoads(false);       // 禁止加载所有网络资源
// 缩放(zoom)
        settings.setSupportZoom(true);          // 是否支持缩放
        settings.setBuiltInZoomControls(false); // 是否使用内置缩放机制
        settings.setDisplayZoomControls(true);  // 是否显示内置缩放控件
// 默认文本编码，默认值 "UTF-8"
        settings.setDefaultTextEncodingName("UTF-8");
        settings.setDefaultFontSize(16);        // 默认文字尺寸，默认值16，取值范围1-72
        settings.setDefaultFixedFontSize(16);   // 默认等宽字体尺寸，默认值16
        settings.setMinimumFontSize(8);         // 最小文字尺寸，默认值 8
        settings.setMinimumLogicalFontSize(8);  // 最小文字逻辑尺寸，默认值 8
        settings.setTextZoom(100);              // 文字缩放百分比，默认值 100
// 字体
        settings.setStandardFontFamily("sans-serif");   // 标准字体，默认值 "sans-serif"
        settings.setSerifFontFamily("serif");           // 衬线字体，默认值 "serif"
        settings.setSansSerifFontFamily("sans-serif");  // 无衬线字体，默认值 "sans-serif"
        settings.setFixedFontFamily("monospace");       // 等宽字体，默认值 "monospace"
        settings.setCursiveFontFamily("cursive");       // 手写体(草书)，默认值 "cursive"
        settings.setFantasyFontFamily("fantasy");       // 幻想体，默认值 "fantasy"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 用户是否需要通过手势播放媒体(不会自动播放)，默认值 true
            settings.setMediaPlaybackRequiresUserGesture(true);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // 5.0以上允许加载http和https混合的页面(5.0以下默认允许，5.0+默认禁止)
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 是否在离开屏幕时光栅化(会增加内存消耗)，默认值 false
            settings.setOffscreenPreRaster(false);
        }
      /*  if (isNetworkConnected(context)) {
            // 根据cache-control决定是否从网络上取数据
            settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        }*/
        else {
            // 没网，离线加载，优先加载缓存(即使已经过期)
            settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }
    }

    private void initWebListener() {
        mWv.setWebViewClient(new WebViewClient() {
            /**
             * @param view
             * @param request
             * @return true-->不加载，false--->从新加载
             */
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.getUrl();
                return super.shouldOverrideUrlLoading(view, request);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }

        });

        /**
         * 在js调用​window.alert​，​window.confirm​，​window.prompt​时，​会调用WebChromeClient​对应方法，
         * 可以此为入口，作为消息传递通道，考虑到开发习惯，一般不会选择alert跟confirm，​通常会选promopt作为入口
         *
         *
         *
         * Native通知Js
         * webview可以通过loadUrl()的方法直接调用。在4.4以上还可以通过evaluateJavascript()方法获取js方法的返回值。
         * 4.4以前，如果想获取方法的返回值，就需要通过上面的对webview信息冒泡传递拦截的方式来实现。
         *
         */
        mWv.setWebChromeClient(new WebChromeClient() {
            /**
             * @param view
             * @param newProgress 加载进度
             */
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
            }

            public boolean onJsAlert(WebView view, String url, String message,
                                     JsResult result) {
                return false;
            }

            public boolean onJsConfirm(WebView view, String url, String message,
                                       JsResult result) {
                return false;
            }


            /**
             *
             * Prompt提示
             * 1、在UI线程执行，所以尽量不要做耗时操作，可以借助Handler灵活处理。
             *
             * 2、第一步：js线程在执行prompt时被挂起，
             * 3、第二部 ：UI线程被调度，恰好销毁了Webview，调用了 （webview的detroy），detroy之后，导致 onJsPrompt
             *   不会被回调，prompt一直等着，js线程就一直阻塞，导致所有webview打不开，一旦出现可能需要杀进程才能解决
             *
             * @return
             */
            public boolean onJsPrompt(WebView view, String url, String message,
                                      String defaultValue, JsPromptResult result) {
                return false;
            }

        });
    }


    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
    }
}

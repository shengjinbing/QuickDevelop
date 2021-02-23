package com.modesty.quickdevelop.ui.activitys;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.webkit.JavascriptInterface;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.modesty.quickdevelop.R;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnPageChange;

/**
 * https://www.jianshu.com/p/3c94ae673e2a
 * 1.销毁Webview
 * 在关闭了Activity时，如果Webview的音乐或视频，还在播放。就必须销毁Webview但是注意：webview调用destory时,webview仍绑定在Activity上
 * 这是由于自定义webview构建时传入了该Activity的context对象因此需要先从父容器中移除webview,然后再销毁webview:
 * rootLayout.removeView(webView);
 * webView.destroy();
 *
 * 2.如何避免WebView内存泄露？
 * (1)不在xml中定义 Webview ，而是在需要的时候在Activity中创建，并且Context使用 getApplicationgContext()
 * LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
 * mWebView = new WebView(getApplicationContext());
 * mWebView.setLayoutParams(params);
 * mLayout.addView(mWebView);
 * (2)在 Activity 销毁（ WebView ）的时候，先让 WebView 加载null内容，然后移除 WebView，再销毁 WebView，最后置空。
 *
 * 作者：Carson_Ho
 * 链接：https://www.jianshu.com/p/3c94ae673e2a
 * https://www.jianshu.com/p/345f4d8a5cfa
 * 来源：简书
 * 著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。
 *
 * 2.清除缓存数据
 * 清除网页访问留下的缓存
 * 由于内核缓存是全局的因此这个方法不仅仅针对webview而是针对整个应用程序.
 * Webview.clearCache(true);
 * 清除当前webview访问的历史记录
 * 只会webview访问历史记录里的所有记录除了当前访问记录
 * Webview.clearHistory()；
 * 这个api仅仅清除自动完成填充的表单数据，并不会清除WebView存储到本地的数据
 * Webview.clearFormData()；
 * <p>
 * <p>
 * 3.WebView中的漏洞分为三类：
 * 任意代码执行漏洞
 * 密码明文存储漏洞
 * 域控制不严格漏洞
 */
public class WebViewActivity extends AppCompatActivity {
    public static final String TAG = "webview_TAG";

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
        initWebViewClient();
        initWebChromeClient();
        initView();
    }

    private void initView() {
        //加载本地html文件
        mWv.loadUrl("file:///android_asset/JavaAndJavaScriptCall.html");
        jsCallAndroid();
    }

    class Jsinterface {
        /**
         * 1.如果混淆了，@JavascriptInterface注解的方法可能就没了，结果是，JS就没办法知己调用对应的方法，
         * 导致通信失败
         * 2.4.2以后，WebView会禁止JS调用没有添加@JavascriptInterface方法, 解决了安全漏洞，而且很少
         * APP兼容到4.2以前(需要采用拦截prompt（）的方式进行漏洞修复)，安全问题可以忽略。
         * 3.JavascriptInterface注入的方法被js调用时，可以看做是一个同步调用，虽然两者位于不同线程，但是应该存在
         * 一个等待通知的机制来保证，所以Native中被回调的方法里尽量不要处理耗时操作
         */
        @JavascriptInterface
        public void showToast(String arg) {
            Toast.makeText(WebViewActivity.this, arg, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 对于JS调用Android代码的方法有3种：
     * 1.通过WebView的addJavascriptInterface（）进行对象映射
     * 使用简单，但是存在严重漏洞
     * 漏洞产生原因是：当JS拿到Android这个对象后，就可以调用这个Android对象中所有的方法，包括系统类（java.lang.Runtime 类），从而进行任意代码执行
     * 2.通过 WebViewClient 的shouldOverrideUrlLoading ()方法回调拦截 url
     * 优点：不存在方式1的漏洞；
     * 缺点：JS获取Android方法的返回值复杂。
     * 如果JS想要得到Android方法的返回值，只能通过 WebView 的 loadUrl （）去执行 JS 方法把返回值传递回去
     * 3.通过 WebChromeClient 的onJsAlert()、onJsConfirm()、onJsPrompt（）方法回调拦截JS对话框alert()、confirm()、prompt（） 消息
     * 常用的拦截是：拦截 JS的输入框（即prompt（）方法）
     * 因为只有prompt（）可以返回任意类型的值，操作最全面方便、更加灵活；而alert（）对话框没有返回值；confirm（）对话框只能返回两种状态（确定 / 取消）两个值
     */
    public void jsCallAndroid() {
        mWv.addJavascriptInterface(new Jsinterface(), "Android");
    }

    /**
     * java调用js,2中方式
     * 1.通过WebView的loadUrl（）
     * 2.通过WebView的evaluateJavascript（）
     * 因为该方法的执行不会使页面刷新，而第一种方法（loadUrl ）的执行则会。
     * Android 4.4 后才可使用
     * <p>
     * 特别注意：
     * JS代码调用一定要在 onPageFinished（） 回调之后才能调用，否则不会调用。
     * onPageFinished()属于WebViewClient类的方法，主要在页面加载结束时调用
     *
     * @param view
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void javacalljs(View view) {
        String content = mEtContent.getText().toString().trim();
        mWv.post(() -> {
            //mWv.loadUrl("javascript:javaCallJs(" + "'" + content + "'" + ")");
            mWv.evaluateJavascript("javascript:javaCallJs(" + "'" + content + "'" + ")", new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String value) {
                    //js的返回结果
                }
            });
        });
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void initWebViewSetting() {
        WebSettings settings = mWv.getSettings();
        //如果访问的页面中要与Javascript交互，则webview必须设置支持Javascript
        // 若加载的 html 里有JS 在执行动画等操作，会造成资源浪费（CPU、电量）
        // 在 onStop 和 onResume 里分别把 setJavaScriptEnabled() 给设置成 false 和 true 即可
        settings.setJavaScriptEnabled(true);

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
// 是否支持多窗口，默认值false
        settings.setSupportMultipleWindows(false);
// 是否可用Javascript(window.open)打开窗口，默认值 false
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
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
/*// 默认文本编码，默认值 "UTF-8"
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
        settings.setFantasyFontFamily("fantasy");       // 幻想体，默认值 "fantasy"*/
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

    /**
     * Android WebView自带的缓存机制有5种：     链接：https://www.jianshu.com/p/5e7075f4875f
     * 浏览器 缓存机制
     * Application Cache 缓存机制
     * Dom Storage 缓存机制
     * Web SQL Database 缓存机制
     * Indexed Database 缓存机制
     * File System 缓存机制（H5页面新加入的缓存机制，虽然Android WebView暂时不支持，但会进行简单介绍）
     */
    public void initCache() {

    }


    public void setAppcache() {
        // 通过设置WebView的settings来实现
        WebSettings settings = mWv.getSettings();

        String cacheDirPath = getFilesDir().getAbsolutePath() + "cache/";
        // 1. 设置缓存路径
        settings.setAppCachePath(cacheDirPath);

        // 2. 设置缓存大小
        settings.setAppCacheMaxSize(20 * 1024 * 1024);

        settings.setAppCacheEnabled(true);
        // 3. 开启Application Cache存储机制

        // 特别注意
        // 每个 Application 只调用一次 WebSettings.setAppCachePath() 和
        //WebSettings.setAppCacheMaxSize();
    }

    public void setDomStorage() {

    }

    private void initWebViewClient() {
        mWv.setWebViewClient(new WebViewClient() {
            /**
             * @param view
             * @param url
             * @return true-->不加载，false--->从新加载
             */
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // 步骤2：根据协议的参数，判断是否是所需要的url
                // 一般根据scheme（协议格式） & authority（协议名）判断（前两个参数）
                //假定传入进来的 url = "js://webview?arg1=111&arg2=222"（同时也是约定好的需要拦截的）

                Uri uri = Uri.parse(url);
                // 如果url的协议 = 预先约定的 js 协议
                // 就解析往下解析参数
                if (uri.getScheme().equals("js")) {

                    // 如果 authority  = 预先约定协议里的 webview，即代表都符合约定的协议
                    // 所以拦截url,下面JS开始调用Android需要的方法
                    if (uri.getAuthority().equals("webview")) {

                        //  步骤3：
                        // 执行JS所需要调用的逻辑
                        Toast.makeText(getApplicationContext(), "js调用了Android的方法", Toast.LENGTH_LONG).show();
                        // 可以在协议上带有参数并传递到Android上
                        HashMap<String, String> params = new HashMap<>();
                        Set<String> collection = uri.getQueryParameterNames();

                    }

                    return true;
                }
                return super.shouldOverrideUrlLoading(view, url);

            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }

            /**
             * 在加载页面资源时会调用，每一个资源（比如图片）的加载都会调用一次。
             */
            @Override
            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);
            }

            /**
             * 加载页面的服务器出现错误时（如404）调用。
             */
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
            }

            // 重写 WebViewClient  的  shouldInterceptRequest （）
            // API 21 以下用shouldInterceptRequest(WebView view, String url)
            // API 21 以上用shouldInterceptRequest(WebView view, WebResourceRequest request)
            // 下面会详细说明

            // API 21 以下用shouldInterceptRequest(WebView view, String url)
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {

                // 步骤1:判断拦截资源的条件，即判断url里的图片资源的文件名
                if (url.contains("logo.gif")) {
                    // 假设网页里该图片资源的地址为：http://abc.com/imgage/logo.gif
                    // 图片的资源文件名为:logo.gif

                    InputStream is = null;
                    // 步骤2:创建一个输入流

                    try {
                        is = getApplicationContext().getAssets().open("images/abc.png");
                        // 步骤3:获得需要替换的资源(存放在assets文件夹里)
                        // a. 先在app/src/main下创建一个assets文件夹
                        // b. 在assets文件夹里再创建一个images文件夹
                        // c. 在images文件夹放上需要替换的资源（此处替换的是abc.png图片）

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    // 步骤4:替换资源
                    WebResourceResponse response = new WebResourceResponse("image/png",
                            "utf-8", is);
                    // 参数1：http请求里该图片的Content-Type,此处图片为image/png
                    // 参数2：编码类型
                    // 参数3：存放着替换资源的输入流（上面创建的那个）
                    return response;
                }

                return super.shouldInterceptRequest(view, url);
            }


            // API 21 以上用shouldInterceptRequest(WebView view, WebResourceRequest request)
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {

                // 步骤1:判断拦截资源的条件，即判断url里的图片资源的文件名
                if (request.getUrl().toString().contains("logo.gif")) {
                    // 假设网页里该图片资源的地址为：http://abc.com/imgage/logo.gif
                    // 图片的资源文件名为:logo.gif

                    InputStream is = null;
                    // 步骤2:创建一个输入流

                    try {
                        is = getApplicationContext().getAssets().open("images/abc.png");
                        // 步骤3:获得需要替换的资源(存放在assets文件夹里)
                        // a. 先在app/src/main下创建一个assets文件夹
                        // b. 在assets文件夹里再创建一个images文件夹
                        // c. 在images文件夹放上需要替换的资源（此处替换的是abc.png图片

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    // 步骤4:替换资源
                    WebResourceResponse response = new WebResourceResponse("image/png",
                            "utf-8", is);
                    // 参数1：http请求里该图片的Content-Type,此处图片为image/png
                    // 参数2：编码类型
                    // 参数3：存放着替换资源的输入流（上面创建的那个）
                    return response;
                }
                return super.shouldInterceptRequest(view, request);
            }

        });
    }

    private void initWebChromeClient() {
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

            /**
             *作用：支持javascript的警告框
             */
            public boolean onJsAlert(WebView view, String url, String message,
                                     JsResult result) {
                Log.d(TAG, "onJsAlert");
                AlertDialog.Builder b = new AlertDialog.Builder(WebViewActivity.this);
                b.setTitle("Alert");
                b.setMessage(message);
                b.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        result.confirm();
                    }
                });
                b.setCancelable(false);
                b.create().show();
                return true;
            }

            /**
             *作用：支持javascript的确认框
             */
            public boolean onJsConfirm(WebView view, String url, String message,
                                       JsResult result) {
                Log.d(TAG, "onJsConfirm");
                return false;
            }

            /**
             * 作用：支持javascript输入框
             * 点击确认返回输入框中的值，点击取消返回 null。
             * Prompt提示
             * 1、在UI线程执行，所以尽量不要做耗时操作，可以借助Handler灵活处理。
             * 2、第一步：js线程在执行prompt时被挂起，
             * 3、第二部 ：UI线程被调度，恰好销毁了Webview，调用了 （webview的detroy），detroy之后，导致 onJsPrompt
             *   不会被回调，prompt一直等着，js线程就一直阻塞，导致所有webview打不开，一旦出现可能需要杀进程才能解决
             *
             * @return
             */
            // 拦截输入框(原理同方式2)
            // 参数message:代表promt（）的内容（不是url）
            // 参数result:代表输入框的返回值
            public boolean onJsPrompt(WebView view, String url, String message,
                                      String defaultValue, JsPromptResult result) {
                Log.d(TAG, "onJsPrompt");
                // 根据协议的参数，判断是否是所需要的url(原理同方式2)
                // 一般根据scheme（协议格式） & authority（协议名）判断（前两个参数）
                //假定传入进来的 url = "js://webview?arg1=111&arg2=222"（同时也是约定好的需要拦截的）

                Uri uri = Uri.parse(message);
                // 如果url的协议 = 预先约定的 js 协议
                // 就解析往下解析参数
                if (uri.getScheme().equals("js")) {

                    // 如果 authority  = 预先约定协议里的 webview，即代表都符合约定的协议
                    // 所以拦截url,下面JS开始调用Android需要的方法
                    if (uri.getAuthority().equals("webview")) {

                        //
                        // 执行JS所需要调用的逻辑
                        System.out.println("js调用了Android的方法");
                        // 可以在协议上带有参数并传递到Android上
                        HashMap<String, String> params = new HashMap<>();
                        Set<String> collection = uri.getQueryParameterNames();

                        //参数result:代表消息框的返回值(输入值)
                        result.confirm("js调用了Android的方法成功啦");
                    }
                    return true;
                }
                return super.onJsPrompt(view, url, message, defaultValue, result);
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


    @Override
    protected void onDestroy() {
        if (mWv != null) {
            mWv.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);

            ViewParent parent = mWv.getParent();
            if (parent != null){
                /**
                 * 原文里说的webview引起的内存泄漏主要是因为org.chromium.android_webview.AwContents 类中注册了component callbacks，但是未正常反注册而导致的。
                 * org.chromium.android_webview.AwContents 类中有这两个方法 onAttachedToWindow 和 onDetachedFromWindow；系统会在attach和detach处进行注册和反注册component callback；
                 * 在onDetachedFromWindow() 方法的第一行中：
                 * if (isDestroyed()) return;，
                 * 如果 isDestroyed() 返回 true 的话，那么后续的逻辑就不能正常走到，所以就不会执行unregister的操作；
                 * 我们的activity退出的时候，都会主动调用 WebView.destroy() 方法，这会导致 isDestroyed() 返回
                 * true；destroy()的执行时间又在onDetachedFromWindow之前，所以就会导致不能正常进行unregister()。
                 * 然后解决方法就是：让onDetachedFromWindow先走，在主动调用destroy()之前，把webview从它的parent上面移除掉
                 */
                ((ViewGroup)parent).removeView(mWv);
            }
            mWv.stopLoading();
            // 退出时调用此方法，移除绑定的服务，否则某些特定系统会报错
            mWv.getSettings().setJavaScriptEnabled(false);
            mWv.clearHistory();
            mWv.removeAllViews();
            mWv.destroy();
            mWv = null;
        }
        super.onDestroy();
    }
}

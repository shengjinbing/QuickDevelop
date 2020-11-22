package com.modesty.quickdevelop.ui.activitys;

import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.modesty.logger.simplelog.Logger;
import com.modesty.quickdevelop.R;
import com.modesty.quickdevelop.network.provider.OkHttpFactory;
import com.modesty.quickdevelop.network.provider.OkHttpProvider;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import okhttp3.Authenticator;
import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Handshake;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.Route;
import okhttp3.internal.Util;
import okio.BufferedSink;

/**
 * OKhttp框架流程
 * 基本的执行流程如下：
 * OKhttpClient->Request->RealCall->Dispatcher->interceptors(RetryAndFollow->Bridge->Cache->Connect->CallServer)
 * <p>
 * 1.新建的连接connection会存放到一个缓存池connectionpool中。网络连接完成后不会立即释放，而是存活一段时间。
 * 网络连接存活状态下，如果有相同的目标连接，则复用该连接，用它来进行写入写出流操作。
 * 2.统计每个connection上发起网络请求的次数，若次数为0，则一段时间后释放该连接。
 * 3.每个网络请求对应一个stream，connection，connectionpool等数据，将它封装为StreamAllocation对象。
 * <p>
 * 1.支持Http2/SPDY,对一台机器的所有请求共享同一个socket。
 * 2.默认启用长连接，使用连接池管理（内置连接池，支持连接复用，减少延迟 ），支持Cache(目前仅支持GET请求的缓存)。
 * 3.路由节点管理，提升访问速度。
 * 4.透明的Gzip处理，节省网络流量。
 * 5.灵活的拦截器，行为类似Java EE的Filter或者函数编程中的Map。
 */
public class OkHttpActivity extends AppCompatActivity {

    private static final String TAG = "OkHttpActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ok_http);
    }

    /**
     * 同步
     * 1.在new OkHttpClient()内部使用构造器模式初始化了一些配置信息：
     * 支持协议、任务分发器（其内部包含一个线程池，执行异步请求）、连接池(其内部包含一个线程池，维护connection)、连接/读/写超时时长等信息。
     *
     * @param view
     */
    public void GETtongbu(View view) {
        String url = "https://wwww.baidu.com";
        OkHttpClient okHttpClient = OkHttpFactory.getInstance();
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = okHttpClient.newCall(request);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Response response = call.execute();
                    Logger.d("run: " + response.body().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
         /*
    OkHttpClient构造函数中创建Builder（） 所有的配置
    public Builder() {
      dispatcher = new Dispatcher(); //任务分发器,多线程管理
      protocols = DEFAULT_PROTOCOLS;//默认协议 Protocol.HTTP_2, Protocol.HTTP_1_1
      connectionSpecs = DEFAULT_CONNECTION_SPECS;
      eventListenerFactory = EventListener.factory(EventListener.NONE);
      proxySelector = ProxySelector.getDefault();
      cookieJar = CookieJar.NO_COOKIES;
      socketFactory = SocketFactory.getDefault();
      hostnameVerifier = OkHostnameVerifier.INSTANCE;
      certificatePinner = CertificatePinner.DEFAULT;
      proxyAuthenticator = Authenticator.NONE;
      authenticator = Authenticator.NONE;
      connectionPool = new ConnectionPool();//连接池
      dns = Dns.SYSTEM;
      followSslRedirects = true;
      followRedirects = true;
      retryOnConnectionFailure = true;
      connectTimeout = 10_000;//超时时间
      readTimeout = 10_000;
      writeTimeout = 10_000;
      pingInterval = 0;
    }

    //任务分发器
    public final class Dispatcher {
    private int maxRequests = 64; //最大请求数量
    private int maxRequestsPerHost = 5; //每台主机最大的请求数量
    private @Nullable Runnable idleCallback;

    /** Executes calls. Created lazily.
    private @Nullable ExecutorService executorService; //线程池

    /** Ready async calls in the order they'll be run.
     准备执行请求的任务队列
    private final Deque<AsyncCall> readyAsyncCalls = new ArrayDeque<>();

    /** Running asynchronous calls. Includes canceled calls that haven't finished yet.
     正在运行的任务队列
    private final Deque<AsyncCall> runningAsyncCalls = new ArrayDeque<>();

    /** Running synchronous calls. Includes canceled calls that haven't finished yet.
    正在运行的同步请求队列
    private final Deque<RealCall> runningSyncCalls = new ArrayDeque<>();

     1.这个线程池没有核心线程，线程数量没有限制，空闲60s就会回收
     2.这个线程池跟Android中的CachedThreadPool非常类似，这种类型的线程池，适用于大量的耗时较短的异步任务
    public synchronized ExecutorService executorService() {
        if (executorService == null) {
            executorService = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60, TimeUnit.SECONDS,
                    new SynchronousQueue<Runnable>(), Util.threadFactory("OkHttp Dispatcher", false));
        }
        return executorService;
    }

    //执行的地方
    synchronized void enqueue(AsyncCall call) {
    //正在执行的任务数量小于最大值（64），并且此任务所属主机的正在执行任务小于最大值（5）
    if (runningAsyncCalls.size() < maxRequests && runningCallsForHost(call) < maxRequestsPerHost) {
        runningAsyncCalls.add(call);//加入到运行队列
        executorService().execute(call);
    } else {
        readyAsyncCalls.add(call);//加入到准备队列
    }
    现在回头看看 AsyncCall ，它继承自 NamedRunnable，而 NamedRunnable实现了 Runnable 接口，它的作用有2个：
    ①采用模板方法的设计模式，让子类将具体的操作放在 execute()方法中;
    ②给线程指定一个名字，比如传入模块名称，方便监控线程的活动状态

}

}

    Request{
      getResponseWithInterceptorChain //可以查看拦截器的顺序 核心（所以网络请求都在里面）

      //通过拦截器的添加执行顺序可以得知整个流程
      从上面的请求流程图可以看出，OkHttp的拦截器链可谓是其整个框架的精髓，用户可传入的 interceptor 分为两类：
     ①一类是全局的 interceptor，该类 interceptor 在整个拦截器链中最早被调用，通过 OkHttpClient.Builder#addInterceptor(
     Interceptor) 传入；
     ②另外一类是非网页请求的 interceptor ，这类拦截器只会在非网页请求中被调用，并且是在组装完请求之后，
     真正发起网络请求前被调用，所有的 interceptor 被保存在 List<Interceptor> interceptors 集合中，
     按照添加顺序来逐个调用，具体可参考 RealCall#getResponseWithInterceptorChain() 方法。
     通过 OkHttpClient.Builder#addNetworkInterceptor(Interceptor) 传入；

     /* Response getResponseWithInterceptorChain() throws IOException {
    // Build MVPActivityModelImpl full stack of interceptors.
    List<Interceptor> interceptors = new ArrayList<>(); //这是一个List，是有序的
    interceptors.addAll(client.interceptors());//首先添加的是用户添加的全局拦截器
    interceptors.add(retryAndFollowUpInterceptor); //错误、重定向拦截器
   //桥接拦截器，桥接应用层与网络层，添加必要的头、帮助我们把没有添加的信息填上
    interceptors.add(new BridgeInterceptor(client.cookieJar()));
    //缓存处理，Last-Modified、ETag、DiskLruCache等,如果命中缓存直接返回
    interceptors.add(new CacheInterceptor(client.internalCache()));
    //连接拦截器，这里是重点进行Socket连接
    interceptors.add(new ConnectInterceptor(client));
    //从这就知道，通过okHttpClient.Builder#addNetworkInterceptor()传进来的拦截器只对非网页的请求生效
    if (!forWebSocket) {
      interceptors.addAll(client.networkInterceptors());//网络调试用
    }
    //真正访问服务器的拦截器,返回Response
    interceptors.add(new CallServerInterceptor(forWebSocket));

    Interceptor.Chain chain = new RealInterceptorChain(interceptors, null, null, null, 0,
        originalRequest, this, eventListener, client.connectTimeoutMillis(),
        client.readTimeoutMillis(), client.writeTimeoutMillis());

       return chain.proceed(originalRequest);
    }
    }
    */


  /*      public final class Response implements Closeable {
            //网络请求的信息
            private final Request request;

            //网路协议，OkHttp3支持"http/1.0","http/1.1","h2"和"spdy/3.1"
            private final Protocol protocol;

            //返回状态码，包括404(Not found),200(OK),504(Gateway timeout)...
            private final int code;

            //状态信息，与状态码对应
            private final String message;

            //TLS(传输层安全协议)的握手信息（包含协议版本，密码套件(https://en.wikipedia.org/wiki/Cipher_suite)，证书列表
            private final Handshake handshake;

            //相应的头信息，格式与请求的头信息相同。
            private final Headers headers;

            //数据内容在ResponseBody中
            private final ResponseBody body;

            //网络返回的原声数据(如果未使用网络，则为null)
            private final Response networkResponse;

            //从cache中读取的网络原生数据
            private final Response cacheResponse;

            //网络重定向后的，存储的上一次网络请求返回的数据。
            private final Response priorResponse;

            //发起请求的时间轴
            private final long sentRequestAtMillis;

            //收到返回数据时的时间轴
            private final long receivedResponseAtMillis;

            //缓存控制指令，由服务端返回数据的中的Header信息指定，或者客户端发器请求的Header信息指定。key："Cache-Control"
            //详见<MVPActivityModelImpl href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9">RFC 2616,14.9</MVPActivityModelImpl>
            private volatile CacheControl cacheControl; // Lazily initialized.

            //各种附值函数和Builder类型          ...
        }*/


    /**
     * 异步
     *
     * @param view
     */
    public void GETyibu(View view) {
        String url = "https://appdev7.hexindai.com/v5/integral?goods_id=96";
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Logger.d("请求失败");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Logger.d("onResponse: " + response.body().toString());
            }
        });
    }

    public void PostString(View view) {
        MediaType mediaType = MediaType.parse("text/x-markdown; charset=utf-8");
        String requestBody = "I am Jdqm.";
        Request request = new Request.Builder()
                .url("https://api.github.com/markdown/raw")
                .post(RequestBody.create(mediaType, requestBody))
                .build();
        OkHttpClient okHttpClient = new OkHttpClient.Builder().authenticator(new Authenticator() {
            /**
             * 认证失败再次发
             *
             * @param route
             * @param response
             * @return
             * @throws IOException
             */
            @Override
            public Request authenticate(Route route, Response response) throws IOException {
                return null;
            }
        }).build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "onFailure: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d(TAG, response.protocol() + " " + response.code() + " " + response.message());
                Headers headers = response.headers();
                for (int i = 0; i < headers.size(); i++) {
                    Log.d(TAG, headers.name(i) + ":" + headers.value(i));
                }
                Log.d(TAG, "onResponse: " + response.body().string());
            }
        });

    }

    public void PostStream(View view) {
        RequestBody requestBody = new RequestBody() {
            @Nullable
            @Override
            public MediaType contentType() {
                return MediaType.parse("text/x-markdown; charset=utf-8");
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                sink.writeUtf8("I am Jdqm.");
            }
        };

        Request request = new Request.Builder()
                .url("https://api.github.com/markdown/raw")
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "onFailure: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d(TAG, response.protocol() + " " + response.code() + " " + response.message());
                Headers headers = response.headers();
                for (int i = 0; i < headers.size(); i++) {
                    Log.d(TAG, headers.name(i) + ":" + headers.value(i));
                }
                Log.d(TAG, "onResponse: " + response.body().string());
            }
        });

    }

    public void PostFile(View view) {
        MediaType mediaType = MediaType.parse("text/x-markdown; charset=utf-8");
        OkHttpClient okHttpClient = new OkHttpClient();
        File file = new File("test.md");
        Request request = new Request.Builder()
                .url("https://api.github.com/markdown/raw")
                .post(RequestBody.create(mediaType, file))
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "onFailure: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d(TAG, response.protocol() + " " + response.code() + " " + response.message());
                Headers headers = response.headers();
                for (int i = 0; i < headers.size(); i++) {
                    Log.d(TAG, headers.name(i) + ":" + headers.value(i));
                }
                Log.d(TAG, "onResponse: " + response.body().string());
            }
        });

    }

    /**
     * 提交表单时，使用 RequestBody 的实现类FormBody来描述请求体，它可以携带一些经过编码的 key-value 请求体，
     * 键值对存储在下面两个集合中：
     * private final List<String> encodedNames;
     * private final List<String> encodedValues;
     *
     * @param view
     */
    public void PostForm(View view) {
        OkHttpClient okHttpClient = OkHttpFactory.getInstance();
        RequestBody requestBody = new FormBody.Builder()
                .add("name", "张立")
                .build();
        Request request = new Request.Builder()
                .url("https://api.ekchange.com/common/searchList")
                .post(requestBody)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "onFailure: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d(TAG, response.protocol() + " " + response.code() + " " + response.message());
                Headers headers = response.headers();
                for (int i = 0; i < headers.size(); i++) {
                    Log.d(TAG, headers.name(i) + ":" + headers.value(i));
                }
                Log.d(TAG, "onResponse: " + response.body().string());
            }
        });

    }

    /**
     * MultipartBody 可以构建复杂的请求体，与HTML文件上传形式兼容。多块请求体中每块请求都是一个请求体，
     * 可以定义自己的请求头。这些请求头可以用来描述这块请求，例如它的 Content-Disposition 。
     * 如果 Content-Length 和 Content-Type 可用的话，他们会被自动添加到请求头中。
     *
     * @param view
     */
    private static final String IMGUR_CLIENT_ID = "...";
    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");

    public void PostMulti(View view) {
        OkHttpClient client = new OkHttpClient();

        File file = new File("website/static/logo-square.png");
        RequestBody requestBody = RequestBody.create(MEDIA_TYPE_PNG, file);
        // Use the imgur image upload API as documented at https://api.imgur.com/endpoints/image
        MultipartBody body = new MultipartBody.Builder("AaB03x")
                .setType(MultipartBody.FORM)
                .addPart(
                        Headers.of("Content-Disposition", "form-data; name=\"title\""),
                        RequestBody.create(null, "Square Logo"))
                .addPart(
                        Headers.of("Content-Disposition", "form-data; name=\"image\""),
                        requestBody)
                .addFormDataPart("key", "abc")
                .addFormDataPart("file", file.getName(), requestBody)
                .build();

        Request request = new Request.Builder()
                .header("Authorization", "Client-ID " + IMGUR_CLIENT_ID)
                .url("https://api.imgur.com/3/image")
                .post(body)
                .build();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                System.out.println(response.body().string());

            }

        });

    }
}

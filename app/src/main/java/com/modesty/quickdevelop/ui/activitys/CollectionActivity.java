package com.modesty.quickdevelop.ui.activitys;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import android.util.SparseLongArray;
import android.view.View;

import com.modesty.quickdevelop.R;
import com.modesty.quickdevelop.bean.User;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 测试EvnetBus相关
 */
public class CollectionActivity extends AppCompatActivity {
    public static final String TAG = "COLLECTION_LOG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);
        handlerThread();
        arraymap();
        sparseArray();
        pair();
        copyOnWriteArrayList();
        EventBus.getDefault().register(this);
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /**
     *在一些开发场景下，既需要已键值的方式存储数据列表，还需要在输出的时候保持和插入数据的顺序不变。
     * HashMap满足前者，ArrayList则满足后者，这时候就可以选择Android提供的一种工具类：Pair（搭配ArrayList）。
     当然，如果仅仅是为了保证数据的插入和输出顺序一致的话, 可以选择使用LinkedHashMap<K,V>,
     但我们知道， Map要求Key不能重复， 如果我们还准许Key可以重复的话， 就只能选择使用"Pair（搭配ArrayList）"这种结构了.
     */
    private void pair() {
        Pair pair = new Pair(1, "3");
        //pair.first;
        //pair.second;
        Pair pair1 = Pair.create("1", 3);
        List<Pair<String, String>> list = new ArrayList<Pair<String, String>>();
    }

    /**
     * 稀疏数组：时间换空间。
     * 1.数据量不大，最好在千级以内
     * 2.key必须为int类型，这中情况下的HashMap能够用SparseArray取代
     * 3.会使用二分查找法和之前的key比較当前我们加入的元素的key的大小
     * 4.以int作为Key。避免了HashMap的装箱拆箱操作,性能更高且int的存储开销远远小于Integer;
     *
     * DELETED ，static final 的一个静态Object实例，当一个键值对被remove后，会在对应key的value下放置该对象，标记该
     * 元素已经被删除（延迟删除，等下具体介绍）；
     * mGarbage  , 当值为true，标志数据结构中有元素被删除，可以触发gc对无效数据进行回收（真正删除）
     * 初始化容量10
     * mSize的大小等于数组中mValues的值等于非DELETED的元素个数
     *
     * 总结
     *
     * SparseArray采用了延迟删除的机制，通过将删除KEY的Value设置DELETED，方便之后对该下标的存储进行复用；
     * 使用二分查找，时间复杂度为O(LogN)，如果没有查找到，那么取反返回左边界，再取反后，左边界即为应该插入的数组下标；
     * 如果无法直接插入，则根据mGarbage标识（是否有潜在延迟删除的无效数据），进行数据清除，再通过System.arraycopy进行数组后移，
     * 将目标元素插入二分查找左边界对应的下标；
     * mSize 小于等于keys.length，小于的部分为空数据或者是gc后前移的数据的原数据（也是无效数据），因此二分查找的右边界以
     * mSize为准；mSize包含了延迟删除后的元素个数；
     * 如果遇到频繁删除，不会触发gc机制，导致mSize 远大于有效数组长度,造成性能损耗;
     * 根据源码,可能触发gc操作的方法有(1、put；2、与index有关的所有操作，setValueAt()等；3、size()方法；)
     * mGarbage为true不一定有无效元素，因为可能被删除的元素恰好被新添加的元素覆盖；
     * 根据SparseArray的这些特点。我们能分析出其使用场景：
     *
     * key为整型；
     * 不需要频繁的删除；
     * 元素个数相对较少；
     */
    private void sparseArray() {
        SparseBooleanArray sparseBooleanArray = new SparseBooleanArray();
        SparseIntArray sparseIntArray = new SparseIntArray();
        //SparseLongArray sparseLongArray = new SparseLongArray();
        SparseArray<String> array = new SparseArray<>();
        array.put(1, "daas");
        array.get(1);
        array.keyAt(1);
        array.valueAt(1);

    }

    /**
     * 1.内存占有问题:很明显，两个数组同时驻扎在内存中，如果实际应用中，数据比较多，而且比较大的情况下，占用内存会比较大，
     * 针对这个其实可以用ConcurrentHashMap来代替。
     * 2.数据一致性:CopyOnWrite容器只能保证数据的最终一致性，不能保证数据的实时一致性。所以如果你希望写入的的数据，
     * 马上能读到，请不要使用CopyOnWrite容器
     */
    private void copyOnWriteArrayList() {
        CopyOnWriteArrayList copyOnWriteArrayList = new CopyOnWriteArrayList();
    }

    /**
     * 1.实现多线程在工作线程中执行任务，如 耗时任务异步通信、消息传递
     * 2. 实现工作线程 & 主线程（UI线程）之间的通信，即：将工作线程的执行结果传递给主线程，从而在主线程中执行相关的UI操作
     */
    private void handlerThread() {
        // 步骤1：创建HandlerThread实例对象
// 传入参数 = 线程名字，作用 = 标记该线程
        HandlerThread mHandlerThread = new HandlerThread("handlerThread");

// 步骤2：启动线程
        mHandlerThread.start();

// 步骤3：创建工作线程Handler & 复写handleMessage（）
// 作用：关联HandlerThread的Looper对象、实现消息处理操作 & 与其他线程进行通信
// 注：消息处理操作（HandlerMessage（））的执行线程 = mHandlerThread所创建的工作线程中执行
        Handler workHandler = new Handler(mHandlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
            }
        };

// 步骤4：使用工作线程Handler向工作线程的消息队列发送消息
// 在工作线程中，当消息循环时取出对应消息 & 在工作线程执行相关操作
        // MVPActivityModelImpl. 定义要发送的消息
        Message msg = Message.obtain();
        msg.what = 2; //消息的标识
        msg.obj = "B"; // 消息的存放
        // b. 通过Handler发送消息到其绑定的消息队列
        workHandler.sendMessage(msg);

// 步骤5：结束线程，即停止线程的消息循环
        mHandlerThread.quit();
    }

    /**
     * 这个时候，我们就可以用ArrayMap替代HashMap。ArrayMap相比传统的HashMap速度要慢，因为查找方法是二分法，
     * 并且当你删除或者添加数据时，会对空间重新调整，在使用大量数据时，效率低于50%。
     * 可以说ArrayMap是牺牲了时间换区空间。但在写手机app时，适时的使用ArrayMap，会给内存使用带来可观的提升
     * ArrayMap用的是copy数据，所以效率相对要高。
     * ArrayMap提供了数组收缩的功能，在clear或remove后，会重新收缩数组，释放空间
     * ArrayMap采用二分法查找；
     * 存储结构，两个数组存储，一个存key的hash，一个存key和value（设计是最棒的）
     * 数组缓存设计（那2个大魔头弄懂就行）
     * 删除元素时的数组容量及时收缩
     * 删除元素时的下界控制，防止抖动
     *
     * ArrayMap优点：
     * 在数据量少时，内存利用率高，及时的空间压缩机制
     * 迭代效率高，可以使用索引来迭代（keyAt()方法以及valueAt() 方法），相比于HashMap迭代使用迭代器模式，效率要高很多
     * ArrayMap缺点：
     * 存取复杂度高，花费大
     * 二分查找的O(log n )时间复杂度远远小于HashMap
     * ArrayMap没有实现Serializable，不利于在Android中借助Bundle传输。
     */
    final ArrayMap<String, Boolean> mBlackFirstFrame = new ArrayMap<>();
    private void arraymap() {
        ArrayMap<String, String> map = new ArrayMap<>();
        map.put("a","s");
        Log.d("BBBBB",map.get("a"));
        mBlackFirstFrame.put("1",false);
        boolean flag = getFlag("1");
        Log.d("BBBBB",flag+"");


    }

    public boolean getFlag(String key) {
        return mBlackFirstFrame.get(key);
    }

    /**
     * 1.EventBus双重枷锁的单列模式
     * @param view
     */
    public void send(View view) {
        User user = new User();
        user.setAge(12);
        user.setName("张三");
        EventBus.getDefault().post(user);
    }

    /**
     * 1.threadMode方法执行在什么线程。
     * 2.sticky event，中文名为粘性事件。普通事件是先注册，然后发送事件才能收到；而粘性事件，在发送事件之后再订阅该事件
     *   也能收到。此外，粘性事件会保存在内存中，每次进入都会去内存中查找获取最新的粘性事件，除非你手动解除注册。
     * 3.priority优先级默认为0。
     * 4.BACKGROUND和ASYNC有什么区别呢？
     *   BACKGROUND中的任务是一个接着一个的去调用，而ASYNC则会即时异步运行，具体
     * 5.采用双重校验并加锁的单例模式生成EventBus实例
     * 6.private static final Map<Class<?>, List<SubscriberMethod>> METHOD_CACHE = new ConcurrentHashMap<>();
     *   使用map数据结构缓存所以的订阅方法。
     * 7.通过反射的方式，将方法名，threadMode，优先级，是否为sticky方法封装为SubscriberMethod对象，添加到subscriberMethods列表中。
     * 8.方法的命名并没有任何要求，只是加上@Subscribe注解即可！同时事件的命名也没有任何要求！
     * 9.接下来，就是添加newSubscription，它属于Subscription类，里面包含着subscriber和subscriberMethod等信息，
     *   同时这里有一个优先级的判断，说明它是按照优先级添加的。优先级越高，会插到在当前List靠前面的位置；
     */
    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true,priority = 10)
    public void getEventBus(User user){
        Log.d(TAG,user.toString());
    }

}

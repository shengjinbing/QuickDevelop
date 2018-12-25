package com.modesty.quickdevelop.ui.activitys;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Pair;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import android.util.SparseLongArray;

import com.modesty.quickdevelop.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CollectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);
        handlerThread();
        arraymap();
        sparseArray();
        pair();
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
     * 4.SparseArray是android.util包中提供的类，用于建立整数对对象的映射，比HashMap性能更佳，因为它避免了自动装箱并且内部数据结构不依赖额外实体对象。
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
        // a. 定义要发送的消息
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
     */
    private void arraymap() {
        Map<String, String> map = new ArrayMap<>();

    }
}

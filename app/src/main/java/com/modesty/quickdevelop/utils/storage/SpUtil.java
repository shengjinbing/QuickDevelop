package com.modesty.quickdevelop.utils.storage;

import android.content.Context;
import android.content.SharedPreferences;

import com.tencent.mmkv.MMKV;

/**
 * 一些结论：
 * 1.SharedPreferences 是线程安全的. 内部由大量 synchronized 关键字保障
 * 2.SharedPreferences 不是进程安全的
 * 3.第一次 getSharedPreferences 会读取磁盘文件, 后续的 getSharedPreferences 会从内存缓存中获取.
 *   如果第一次调用 getSharedPreferences 时还没从磁盘加载完毕就调用 getXxx/putXxx ,
 *   则 getXxx/putXxx 操作会卡主, 直到数据从磁盘加载完毕后返回
 * 4.所有的 getXxx 都是从内存中取的数据
 * 5.apply 是同步回写内存, 然后把异步回写磁盘的任务放到一个单线程的队列中等待调度. commit 和前者一样,
 *   只不过要等待异步磁盘任务结束后才返回
 * 6.MODE_MULTI_PROCESS 是在每次 getSharedPreferences 时检查磁盘上配置文件上次修改时间和文件大小,
 *   一旦所有修改则会重新从磁盘加载文件. 所以并不能保证多进程数据的实时同步
 * 7.从 Android N 开始, 不再支持 MODE_WORLD_READABLE & MODE_WORLD_WRITEABLE. 一旦指定, 会抛异常
 * 8.每次 apply / commit 都会把全部的数据一次性写入磁盘, 所以单个的配置文件不应该过大, 影响整体性能
 *
 *
 *
 *
 * 1.跨进程不安全。由于没有跨进程得锁，就算使用MODE_MULTI_PROCESS在跨进程进行频繁的读写有可能导致数据全部
 *   丢失；根据线上统计SharedPreferences有万分之一的损坏率
 * 2.加载缓慢。SharedPreferences加载使用异步线程，而且加载线程没有设置优先级，如果这个时候主线程读取数据
 *   就需要等待文件加载线程得结束。这就导致会出现主线程等待低优先级线程锁得问题，比如读一个100kbSP文件需要
 *   50ms-100ms，我建议使用异步线程预加启动过程中用到的SP文件。
 * 3.全量写入。无论是commit()还是apply()，即使我们只修改其中一个条目，都会把整个内容全部写到文件，及时我们多次写
 *   同一个同一个文件，SP也没有将多次修改为一次，这也是性能差的主要原因之一。
 * Created by lixiang on 2019/7/12
 */
public class SpUtil {

    public static void put(Context context,String name,String key, String value){
        SharedPreferences preferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit().putString(key, value);
        //异步提交
        editor.apply();
    }


    public static String put(Context context,String name,String key){
        SharedPreferences preferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        return preferences.getString(name,key);
    }


    public static void encode(String key, String value){
        MMKV.defaultMMKV().encode(key,value);
    }


    public static String decode(String key){
        return MMKV.defaultMMKV().decodeString(key);
    }
}

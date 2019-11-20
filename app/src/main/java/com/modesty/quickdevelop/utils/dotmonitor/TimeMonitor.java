package com.modesty.quickdevelop.utils.dotmonitor;

/**
 * 代码打点（函数插桩）
 *
 * 可以写一个统计耗时的工具类来记录整个过程的耗时情况。其中需要注意的有：
 *
 * 在上传数据到服务器时建议根据用户ID的尾号来抽样上报。
 * 在项目中核心基类的关键回调函数和核心方法中加入打点。
 *
 */

import android.util.Log;

import java.util.HashMap;

/**
 * 耗时监视器对象，记录整个过程的耗时情况，可以用在很多需要统计的地方，比如Activity的启动耗时和Fragment的启动耗时。
 *
 * Created by lixiang on 2019-11-13
 */
public class TimeMonitor {
    private final String TAG = TimeMonitor.class.getSimpleName();
    private int mMonitorId = -1;

    // 保存一个耗时统计模块的各种耗时，tag对应某一个阶段的时间
    private HashMap<String, Long> mTimeTag = new HashMap<>();
    private long mStartTime = 0;

    public TimeMonitor(int mMonitorId) {
        Log.d(TAG, "init TimeMonitor id: " + mMonitorId);
        this.mMonitorId = mMonitorId;
    }

    public int getMonitorId() {
        return mMonitorId;
    }

    public void startMonitor() {
        // 每次重新启动都把前面的数据清除，避免统计错误的数据
        if (mTimeTag.size() > 0) {
            mTimeTag.clear();
        }
        mStartTime = System.currentTimeMillis();
    }

    /**
     * 每打一次点，记录某个tag的耗时
     */
    public void recordingTimeTag(String tag) {
        // 若保存过相同的tag，先清除
        if (mTimeTag.get(tag) != null) {
            mTimeTag.remove(tag);
        }
        long time = System.currentTimeMillis() - mStartTime;
        Log.d(TAG, tag + ": " + time);
        mTimeTag.put(tag, time);
    }

    /**
     *
     * @param tag
     * @param writeLog 是否本地保存
     */
    public void end(String tag, boolean writeLog) {
        recordingTimeTag(tag);
        end(writeLog);
    }

    public void end(boolean writeLog) {
        if (writeLog) {
            //写入到本地文件
        }
    }

    public HashMap<String, Long> getTimeTags() {
        return mTimeTag;
    }
}

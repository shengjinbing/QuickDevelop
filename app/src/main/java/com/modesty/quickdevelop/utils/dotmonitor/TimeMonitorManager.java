package com.modesty.quickdevelop.utils.dotmonitor;

import java.util.HashMap;

/**
 * Created by lixiang on 2019-11-13
 *
 *
 * 因为，耗时统计可能会在多个模块和类中需要打点，所以需要一个单例类来管理各个耗时统计的数据
 *
 * 主要在以下几个方面需要打点：
 * 应用程序的生命周期节点。
 * 启动时需要初始化的重要方法，如数据库初始化，读取本地的一些数据。
 * 其他耗时的一些算法。
 *
 * 采用单例管理各个耗时统计的数据。
 */
public class TimeMonitorManager {
    private static TimeMonitorManager mTimeMonitorManager = null;
    private HashMap<Integer, TimeMonitor> mTimeMonitorMap = null;

    public synchronized static TimeMonitorManager getInstance() {
        if (mTimeMonitorManager == null) {
            mTimeMonitorManager = new TimeMonitorManager();
        }
        return mTimeMonitorManager;
    }

    public TimeMonitorManager() {
        this.mTimeMonitorMap = new HashMap<Integer, TimeMonitor>();
    }

    /**
     * 初始化打点模块
     */
    public void resetTimeMonitor(int id) {
        if (mTimeMonitorMap.get(id) != null) {
            mTimeMonitorMap.remove(id);
        }
        getTimeMonitor(id);
    }

    /**
     * 获取打点器
     */
    public TimeMonitor getTimeMonitor(int id) {
        TimeMonitor monitor = mTimeMonitorMap.get(id);
        if (monitor == null) {
            monitor = new TimeMonitor(id);
            mTimeMonitorMap.put(id, monitor);
        }
        return monitor;
    }

}

package com.modesty.utils;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.os.Process;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class ProcessUtil {

    public static boolean isMainProcess(Context context) {
        try {
            ActivityManager activityManager = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE));
            if(activityManager == null){return false;}
            List<RunningAppProcessInfo> raps = activityManager.getRunningAppProcesses();
            if (raps == null || raps.size() <= 0) {return false;}
            int pid = Process.myPid();
            String packageName = context.getPackageName();
            for (RunningAppProcessInfo info : raps) {
                if (packageName.equals(info.processName)) {
                    return pid == info.pid;
                }
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public static String getPackageName() {
        String packageName = null;
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream("/proc/" + Process.myPid() + "/cmdline")));
            packageName = reader.readLine().trim();
        } catch (Exception e){
            e.printStackTrace();
            packageName = String.valueOf(Process.myPid());
        } finally {
            if(reader != null) try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return packageName;
    }

    /**
     * 获取进程号对应的进程名
     *
     * @param pid 进程号
     * @return 进程名
     */
    private static String getProcessName(int pid) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("/proc/" + pid + "/cmdline"));
            String processName = reader.readLine();
            if (!TextUtils.isEmpty(processName)) {
                processName = processName.trim();
            }
            return processName;
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            IOUtils.closeSilently(reader);
        }
        return null;
    }
}
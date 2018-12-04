package com.modesty.logger.file;

import android.os.Environment;
import android.os.Process;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ${lixiang} on 2018/8/20.
 */

class Utils {
    Utils() {
    }

    public static File getLogFileDirectory() {
        String logDirStr = getFilesDirectoryPath(getPackageName()) + "/log/";
        File logDir = new File(logDirStr);
        if(!logDir.exists()) {
            logDir.mkdirs();
        }

        return logDir;
    }

    private static String getFilesDirectoryPath(String packageName) {
        String dataDir = Environment.getDataDirectory().getAbsolutePath();
        return dataDir + "/data/" + packageName + "/files";
    }

    public static File getAppRootDir(String packageName) {
        String dataDir = Environment.getDataDirectory().getAbsolutePath();
        return new File(dataDir, "/data/" + packageName);
    }

    public static String getPackageName() {
        String packageName = null;
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream("/proc/" + Process.myPid() + "/cmdline")));
            packageName = reader.readLine().trim();
        } catch (Exception var11) {
            var11.printStackTrace();
            packageName = String.valueOf(Process.myPid());
        } finally {
            if(reader != null) {
                try {
                    reader.close();
                } catch (IOException var10) {
                    var10.printStackTrace();
                }
            }

        }

        return packageName;
    }

    public static void closeSilently(Closeable c) {
        if(c != null) {
            try {
                c.close();
                c = null;
            } catch (Throwable var2) {
                var2.printStackTrace();
            }

        }
    }

    public static Map<Object, Object> toMap(Object... args) {
        if(args == null) {
            return null;
        } else {
            Map<Object, Object> map = new HashMap();

            for(int i = 0; i < args.length - 1; i += 2) {
                map.put(args[i], args[i + 1]);
            }

            return map;
        }
    }

    public static int getTotalBytes(File file) {
        InputStream in = null;
        int total = 0;

        try {
            in = new FileInputStream(file);
            total = in.available();
        } catch (Exception var7) {
            var7.printStackTrace();
        } finally {
            closeSilently(in);
        }

        return total;
    }

    public static <T> boolean isEmpty(T[] array) {
        return array == null || array.length == 0;
    }
}


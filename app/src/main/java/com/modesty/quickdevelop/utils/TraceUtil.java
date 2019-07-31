package com.modesty.quickdevelop.utils;

import android.os.Trace;

/**
 * Created by lixiang on 2019/7/10
 */
public class TraceUtil{

    public static void i(String tag) {
        Trace.beginSection(tag);
    }


    public static void o() {
        Trace.endSection();
    }

}

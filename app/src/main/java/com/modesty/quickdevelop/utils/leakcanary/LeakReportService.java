package com.modesty.quickdevelop.utils.leakcanary;

import android.support.annotation.NonNull;

import com.squareup.leakcanary.AnalysisResult;
import com.squareup.leakcanary.DisplayLeakService;
import com.squareup.leakcanary.HeapDump;

/**
 * Created by lixiang on 2019-09-04
 */
public class LeakReportService extends DisplayLeakService {
    @SuppressWarnings("ThrowableNotThrown")
    @Override
    protected void afterDefaultHandling(@NonNull HeapDump heapDump, @NonNull AnalysisResult result, @NonNull String leakInfo) {
        if (!result.leakFound || result.excludedLeak) {
            return;
        }
        try {
            Exception exception = new Exception("Memory Leak from LeakCanary");
            exception.setStackTrace(result.leakTraceAsFakeException().getStackTrace());
            //Sentry.capture(exception);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

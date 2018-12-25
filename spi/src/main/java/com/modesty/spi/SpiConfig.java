package com.modesty.spi;

import android.app.Application;
import android.content.Context;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by lixiang
 * on 2018/12/20
 */
public class SpiConfig {
    private Set<String> bizIds;
    private Context appContext;

    private SpiConfig() {
        this.bizIds = new HashSet();
    }

    public static SpiConfig getInstance() {
        return SpiConfig.SingletonHolder.INSTANCE;
    }

    public Set<String> getBizIds() {
        return this.bizIds;
    }

    public Context getAppContext() {
        return this.appContext;
    }

    public SpiConfig context(Context context) {
        if(context instanceof Application) {
            this.appContext = context;
        } else {
            this.appContext = context.getApplicationContext();
        }

        return this;
    }

    public SpiConfig bizIds(Set<String> ids) {
        this.bizIds.addAll(ids);
        return this;
    }

    public SpiConfig bizIds(String... ids) {
        Collections.addAll(this.bizIds, ids);
        return this;
    }

    private void checkParams() {
        if(this.appContext == null) {
            throw new SpiConfig.SpiActivateException("you should call context(Context context) method to set application context first.");
        } else if(this.bizIds != null && this.bizIds.isEmpty()) {
            throw new SpiConfig.SpiActivateException("there is no business id, please set ids first.");
        }
    }

    private static class SpiActivateException extends RuntimeException {
        private SpiActivateException(String message) {
            super(message);
        }
    }

    private static final class SingletonHolder {
        private static final SpiConfig INSTANCE = new SpiConfig();

        private SingletonHolder() {
        }
    }
}

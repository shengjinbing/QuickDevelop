package com.modesty.socket;

import android.support.annotation.NonNull;

/**
 * @author wangzhiyuan
 * @since 2018/6/25
 */

public interface Callback {

    /**
     * Callback's method invoked when message is received by client
     *
     * @param manager callback manager.
     * @param message message.
     */
    void update(@NonNull CallbackManager manager, @NonNull String message);

}

package com.modesty.logger.file;

/**
 * Created by ${lixiang} on 2018/8/20.
 */

abstract class Callback {
    Callback() {
    }

    void onStart() {
    }

    void onError(Throwable e) {
    }

    void onSuccess(String json) {
    }

    void onFailure() {
    }
}

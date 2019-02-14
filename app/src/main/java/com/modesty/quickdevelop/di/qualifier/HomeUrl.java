package com.modesty.quickdevelop.di.qualifier;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;

import javax.inject.Qualifier;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by Administrator on 2017/11/22 0022.
 */

@Qualifier
@Documented
@Retention(RUNTIME)
public @interface HomeUrl {
}

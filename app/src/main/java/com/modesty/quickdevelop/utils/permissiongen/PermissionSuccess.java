package com.modesty.quickdevelop.utils.permissiongen;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by namee on 2015. 11. 17..
 * Register MVPActivityModelImpl method invoked when permission requests are succeeded.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PermissionSuccess {
}

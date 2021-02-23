package com.apt_annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.CLASS)//Class注解的应用
@Target(ElementType.FIELD)
public @interface BindViewC {
    int value();
}

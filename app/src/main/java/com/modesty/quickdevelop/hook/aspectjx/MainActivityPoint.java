package com.modesty.quickdevelop.hook.aspectjx;

import android.util.Log;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

/**
 * 1、无侵入性
 * 2、修改方便
 *
 * JoinPoint一般定位在如下位置
 *
 * 1、函数调用
 * 2、获取、设置变量
 * 3、类初始化
 *
 * 1、Before：PointCut之前执行
 * 2、After：PointCut之后执行
 * 3、Around：PointCut之前、之后分别执行
 *
 * 当Action为Before、After时，方法入参为JoinPoint。
 * 当Action为Around时，方法入参为ProceedingPoint。
 *
 * Around和Before、After的最大区别:
 *
 * ProceedingPoint不同于JoinPoint，其提供了proceed方法执行目标方法。
 */
@Aspect
public class MainActivityPoint {
    /**
     * 在execution中的是一个匹配规则，第一个*代表匹配任意的方法返回值，后面的语法代码匹配所有Activity中on开头的方法。
     * @param joinPoint
     * @throws Throwable
     */
    @Before("execution(* android.app.Activity.on**(..))")
    public void onActivityCalled(JoinPoint joinPoint) throws Throwable {
    }


    @Around("call (* com.json.chao.application.BaseApplication.**(..))")
    public void getTime(ProceedingJoinPoint joinPoint) {
        Signature signature = joinPoint.getSignature();
        String name = signature.toShortString();
        long time = System.currentTimeMillis();
        try {
            joinPoint.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        Log.i("BBBBB", name + " cost" +     (System.currentTimeMillis() - time));
    }
}

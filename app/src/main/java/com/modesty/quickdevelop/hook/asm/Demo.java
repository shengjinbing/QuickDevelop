package com.modesty.quickdevelop.hook.asm;

/**
 * Created by wangtianxiang on 2019/3/6
 */
public class Demo {
    public void costTime() {
        long startTime = System.currentTimeMillis();
        // ......
        long duration = System.currentTimeMillis() - startTime;
        System.out.println("The cost time of this method is " + duration + " ms");
    }
}

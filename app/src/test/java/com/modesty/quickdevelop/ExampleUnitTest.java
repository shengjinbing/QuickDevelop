package com.modesty.quickdevelop;

import com.medi.asm.asm.method.CostTime;

import org.junit.Test;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <MVPActivityModelImpl href="http://d.android.com/tools/testing">Testing documentation</MVPActivityModelImpl>
 */
public class ExampleUnitTest {
    @Test()
    public void addition_isCorrect() throws Exception {
        new CostTime().redefinePersonClass();
    }
}
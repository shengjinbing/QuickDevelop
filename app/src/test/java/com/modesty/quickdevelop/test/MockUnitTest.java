package com.modesty.quickdevelop.test;

import android.content.Context;

import com.modesty.quickdevelop.R;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

/**
 * 如果要测试的单元依赖了Android框架，比如用到了Android中的Context类的一些方法，本地JVM将无法提供这样的环境，
 * 这时候模拟框架[Mockito][1]就派上用场了。
 */
@RunWith(MockitoJUnitRunner.class)
public class MockUnitTest {
    private static final String FAKE_STRING = "AndroidUnitTest";

    @Mock
    Context mMockContext;

    /**
     * 通过模拟框架[Mockito][1]，指定调用context.getString(int)方法的返回值，达到了隔离依赖的目的，
     * 其中[Mockito][1]使用的是[cglib][2]动态代理技术
     */
    @Test
    public void readStringFromContext_LocalizedString() {
        //模拟方法调用的返回值，隔离对Android系统的依赖
        when(mMockContext.getString(R.string.app_name)).thenReturn(FAKE_STRING);
        assertThat(mMockContext.getString(R.string.app_name), is(FAKE_STRING));

        when(mMockContext.getPackageName()).thenReturn("com.jdqm.androidunittest");
        System.out.println(mMockContext.getPackageName());
    }
}

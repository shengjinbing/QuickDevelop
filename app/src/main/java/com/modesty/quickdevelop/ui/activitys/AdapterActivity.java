package com.modesty.quickdevelop.ui.activitys;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.modesty.quickdevelop.R;
import com.modesty.quickdevelop.utils.permissiongen.internal.Utils;
import com.modesty.utils.DisplayUtils;

/**
 * 1.屏幕尺寸：含义：手机对角线的物理尺寸
 *           单位：英寸（inch），1英寸=2.54cm
 * 2.屏幕分辨率： 1080x1920，即宽度方向上有1080个像素点，在高度方向上有1920个像素点
 * 3.屏幕像素密度：含义：每英寸的像素点数
 *              单位：dpi（dots per ich）
 * 4.屏幕尺寸、分辨率、像素密度三者关系: dpi = ....
 *
 * android中的dp在渲染前会将dp转为px，计算公式：
 *
 * px = density * dp;
 *
 * density = dpi / 160;
 *
 * px = dp * (dpi / 160);
 *
 *
 *
 *
 * 密度无关像素:
 * 含义：density-independent pixel，叫dp或dip，与终端上的实际物理像素点无关。
 * 单位：dp，可以保证在不同屏幕像素密度的设备上显示相同的效果
 * 1.Android开发时用dp而不是px单位设置图片大小，是Android特有的单位
 * 2.场景：假如同样都是画一条长度是屏幕一半的线，如果使用px作为计量单位，
 * 那么在480x800分辨率手机上设置应为240px；在320x480的手机上应设置为160px，
 * 二者设置就不同了；如果使用dp为单位，在这两种分辨率下，160dp都显示为屏幕一半的长度。
 */
public class AdapterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //必须setContentView之前调用
        DisplayUtils.setCustomDensity(this,getApplication());
        setContentView(R.layout.activity_adapter);
    }

}

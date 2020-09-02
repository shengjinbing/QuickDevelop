package com.modesty.quickdevelop.ui.activitys;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.modesty.quickdevelop.R;
import com.modesty.quickdevelop.utils.image.ImageCompressUtil;

/**
 * 1.大图片加载BitmapRegionDecoder
 * 2.图片的裁剪
 *   createBitmap：具体如何转换可以到BitmapFactory中查找。将转换了的bitmap对象传入方法中，x为从横轴上开始裁剪的开始
 *   位置，y为从纵轴上开始裁剪的开始位置，width为需要裁剪的宽度，height为需要裁剪的高度。然后这个方法返回的就是你裁剪的图片
 * 3.质量压缩不会减少图片的像素，它是在保持像素的前提下改变图片的位深及透明度，来达到压缩图片的目的，图片的长，宽，像素都不
 *   会改变，那么bitmap所占内存大小是不会变的。但是我们看到bytes.length是随着quality变小而变小的。这样适合去传递二进制
 *   的图片数据，比如微信分享图片，要传入二进制数据过去，限制32kb之内。
 * 4.采样率压缩其原理其实也是缩放bitamp的尺寸
 * 5.通过压缩像素占用的内存来达到压缩的效果
 *
 *
 * 怎么计算一个View在屏幕可见部分的百分比？
 *
 */
public class BitmapActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bitmap);
        initView();
    }

    private void initView() {
        ImageView image1 = (ImageView) findViewById(R.id.image1);
        TextView tv1 = (TextView) findViewById(R.id.tv);

        TextView tvView = (TextView) findViewById(R.id.tv_view);
        tvView.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(),getVisibilityPercents(tvView)+"",Toast.LENGTH_LONG).show();
            }
        });
    }

    public void loadBigImage(View view) {
        startActivity(new Intent(this,BigImageActivity.class));
    }

    /**
     * 获取view当前占据屏幕的百分比
     * View.getGlobalVisibleRect(rect); //以屏幕 左上角 为参考系的
     * View.getLocalVisibleRect(rect); //以目标 View 左上角 为参考系
     */
    private int getVisibilityPercents(View currentView) {
        int percents = 100;
        Rect rect = new Rect();
        //防止出现视频已不在可见得范围之内仍然返回100（完全可见）
        boolean isVisible = currentView.getLocalVisibleRect(rect);
        if (isVisible) { //可见时做百分比的计算
            int height = currentView.getHeight();
            int width = currentView.getWidth();
            if(viewIsPartiallyHiddenTop(rect)){
                // view is partially hidden behind the top edge
                percents = (height - rect.top) * 100 / height;
            } else if(viewIsPartiallyHiddenBottom(rect, height)){
                percents = rect.bottom * 100 / height;
            } else if (viewIsPartiallyHiddenLeft(rect)){
                percents = (width - rect.right)*100 /width;
            } else if (viewIsPartiallyHiddenRight(rect,width)){
            }
        }else { //View已经不可见
            percents = 0;
        }
        return percents;
    }

    //view底部部分不可见
    private boolean viewIsPartiallyHiddenBottom(Rect rect, int height) {
        return rect.bottom > 0 && rect.bottom < height;
    }

    //view顶部部分不可见
    private boolean viewIsPartiallyHiddenTop(Rect rect) {
        return rect.top > 0;
    }
    //view顶部部分不可见
    private boolean viewIsPartiallyHiddenLeft(Rect rect) {
        return rect.left < 0;
    }

    //view顶部部分不可见
    private boolean viewIsPartiallyHiddenRight(Rect rect,int width) {
        return rect.right < width ;
    }

}

package com.modesty.quickdevelop.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.RegionIterator;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

public class RegionView extends View {

    private Paint paint;
    private Paint paintGreen;
    public RegionView(Context context) {
        this(context,null);
    }

    public RegionView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public RegionView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
    }



    private void initPaint() {
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.RED);

        paintGreen = new Paint();
        paintGreen.setStyle(Paint.Style.FILL);
        paintGreen.setColor(Color.GREEN);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        opMethod(canvas);
    }

    /**
     * 除了union函数合并指定矩形操作以外，Region还提供了更加灵活得操作函数
     *         DIFFERENCE(0),  region1与region2不同区域
     *         INTERSECT(1),  region1与region2相交区域
     *         UNION(2),      region1与region2组合在一起得区域
     *         XOR(3),        region1与region2相交之外得区域
     *         REVERSE_DIFFERENCE(4),  region2与region1不同得区域
     *         REPLACE(5);          最终区域为region2
     * @param canvas
     */
    private void opMethod(Canvas canvas){
        Rect rect1 = new Rect(100, 100, 400, 200);
        Rect rect2 = new Rect(200, 0, 300, 300);

        Region region1 = new Region(rect1);
        Region region2 = new Region(rect2);
        region1.op(region2, Region.Op.XOR);
        drawRegion(canvas,region1,paintGreen);
    }

    private void opRegionMethod(Canvas canvas){
        Rect rect1 = new Rect(100, 100, 400, 200);
        Rect rect2 = new Rect(200, 0, 300, 300);

        Region region1 = new Region(rect1);
        Region region2 = new Region(rect2);
        Region region = new Region();
        //这里将region1和region2相交得结果赋给region
        region.op(region1,region2, Region.Op.INTERSECT);
        drawRegion(canvas,region,paintGreen);
    }

    private void setUnionMethod(Canvas canvas){
        Region region = new Region(10, 10, 200, 100);
        region.union(new Rect(10,10,50,300));
        drawRegion(canvas,region,paint);
    }

    private void setPathMethod(Canvas canvas){
        //RectF用于path
        Path ovalpath = new Path();
        RectF rectF = new RectF(50, 50, 200, 500);
        //1、Path.Direction.CCW：是counter-clockwise缩写，指创建逆时针方向的矩形路径；
        //2、Path.Direction.CW：是clockwise的缩写，指创建顺时针方向的矩形路径；
        ovalpath.addOval(rectF, Path.Direction.CCW);


        Region region = new Region(new Rect(50,50,200,200));
        //根据路径得区域与某区域得交集构造出新得区域
        region.setPath(ovalpath,region);
        drawRegion(canvas,region,paint);
    }

    private void drawRegion(Canvas canvas, Region region,Paint paint) {
        RegionIterator iter = new RegionIterator(region);
        Rect rect = new Rect();
        while (iter.next(rect)) {
            canvas.drawRect(rect,paint);
        }
    }
}

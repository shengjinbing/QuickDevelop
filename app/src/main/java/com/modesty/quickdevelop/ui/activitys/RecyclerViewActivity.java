package com.modesty.quickdevelop.ui.activitys;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.modesty.quickdevelop.R;

import java.util.ArrayList;
import java.util.Collections;

/**
 * 1.首先没有见过特别详细的RecyclerView的源码分析系列，所有关于RecyclerView都是停留在使用或者少数进阶使用的博客
 * <p>
 * 2.RecyclerView，LayoutManager，Adapter，ViewHolder，ItemDecoration这些和RecycleView使用息息相关的类到底是什么关系
 * <p>
 * 3.RecyclerView作为列表，绘制流程到底什么样的
 * <p>
 * 4.RecyclerView有什么不常用的进阶使用方式，但是却很适合RecyclerView作为很“重”的组件的优化，像setRecyclerPool用处到底是什么
 * <p>
 * 5.大家都只要要使用RecyclerView替代ListView和GridView，好用，都在用，但是都没有追究到底这背后的原因到底是什么，
 * RecyclerView到底比ListView好在哪里，到底该不该替换，性能到底提升多少。
 */
public class RecyclerViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view);
    }
    /***********************第一阶段 对于RecyclerView的绘制流程其实我们有了一个大体的了解*******************************/
    //onMeasure和onLayout进行分析
//    1.RecyclerView是将绘制流程交给LayoutManager处理，如果没有设置不会测量子View。
//    2.绘制流程是区分正向绘制和倒置绘制。
//    3.绘制是先确定锚点，然后向上绘制，向下绘制，fill()至少会执行两次，如果绘制完还有剩余空间，则会再执行一次fill()方法。
//    4.LayoutManager获得View是从RecyclerView中的Recycler.next()方法获得，涉及到RecyclerView的缓存策略，如果缓存没有拿到，则走我们自己重写的onCreateView方法。
//    5.如果RecyclerView宽高没有写死，onMeasure就会执行完子View的measure和Layout方法，onLayout仅仅是重置一些参数，如果写死，子View的measure和layout会延后到onLayout中执行。

    /***********************第二阶段 RecyclerView的缓存机制*******************************/

    //从ListView的RecycleBin到RecyclerView的Recycler
    /*public final class Recycler {
        final ArrayList<ViewHolder> mAttachedScrap = new ArrayList<>();
        ArrayList<ViewHolder> mChangedScrap = null;

        final ArrayList<ViewHolder> mCachedViews = new ArrayList<ViewHolder>();

        private final List<ViewHolder>
                mUnmodifiableAttachedScrap = Collections.unmodifiableList(mAttachedScrap);

        private int mRequestedCacheMax = DEFAULT_CACHE_SIZE;
        int mViewCacheMax = DEFAULT_CACHE_SIZE;

        RecycledViewPool mRecyclerPool;

        private ViewCacheExtension mViewCacheExtension;

        static final int DEFAULT_CACHE_SIZE = 2;
    ...
    }
    类的结构也比较清楚，这里可以清楚的看到我们后面讲到的四级缓存机制所用到的类都在这里可以看到
    * 1.一级缓存：mAttachedScrap
    * 2.二级缓存：mCacheViews
    * 3.三级缓存：mViewCacheExtension
    * 4.四级缓存：mRecyclerPool
    /*
 1.mCachedViews 优先级高于 RecyclerViewPool，回收时，最新的 ViewHolder 都是往 mCachedViews 里放，
  如果它满了，那就移出一个扔到 ViewPool 里好空出位置来缓存最新的 ViewHolder。
 2.复用时，也是先到 mCachedViews 里找 ViewHolder，但需要各种匹配条件，概括一下就是只有原来位置的
 卡位可以复用存在 mCachedViews 里的 ViewHolder，如果 mCachedViews 里没有，那么才去 ViewPool 里找。
 3.在 ViewPool 里的 ViewHolder 都是跟全新的 ViewHolder 一样，只要 type 一样，有找到，就可以拿出来复用，重新绑定下数据即可。

    1.RecyclerView内部大体可以分为四级缓存：mAttachedScrap,mCacheViews,ViewCacheExtension,RecycledViewPool.
    2.mAttachedScrap,mCacheViews只是对View的复用，并且不区分type，ViewCacheExtension,RecycledViewPool是对于ViewHolder的复用，而且区分type。
    3.如果缓存ViewHolder时发现超过了mCachedView的限制，会将最老的ViewHolder(也就是mCachedView缓存队列的第一个ViewHolder)移到RecycledViewPool中
     */
}

package com.modesty.quickdevelop.ui.activitys;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.modesty.quickdevelop.R;
import com.modesty.quickdevelop.adapter.recyclerview.CommonAdapter;
import com.modesty.quickdevelop.adapter.recyclerview.base.ViewHolder;
import com.modesty.quickdevelop.adapter.recyclerview.wrapper.EmptyWrapper;
import com.modesty.quickdevelop.adapter.recyclerview.wrapper.HeaderAndFooterWrapper;
import com.modesty.quickdevelop.adapter.recyclerview.wrapper.LoadMoreWrapper;
import com.modesty.quickdevelop.adapter.rv.DividerItemDecoration;
import com.yanzhenjie.recyclerview.swipe.widget.DefaultItemDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

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

    @BindView(R.id.rv)
    RecyclerView mRv;

    private List<String> mDatas = new ArrayList<>();
    private CommonAdapter<String> mAdapter;
    private HeaderAndFooterWrapper mHeaderAndFooterWrapper;
    private EmptyWrapper mEmptyWrapper;
    private LoadMoreWrapper mLoadMoreWrapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view);
        ButterKnife.bind(this);
        initView();
        initData();
        initListener();
    }

    /**
     * 1.布局管理器分为LinearLayoutManager,GridLayoutManager,StaggeredGridLayoutManager
     * 2.viewHolder规范化，不再需要像 ListView 那样自己调用 setTag
     * 3.ListView 提供了 setEmptyView 这个 API 来让我们处理 Adapter 中数据为空的情况，只需轻轻一 set 就能搞定一切。
     * 4.局部刷新动画方法和添加item动画,但是需要自己定义下划线
     * 5.ListView可以有添加HeaderView 和 FooterView
     * 6.ItemTouchHelper 是系统为我们提供的一个用于滑动和删除 RecyclerView 条目的工具类
     * 7.嵌套滚动机制NestedScrollingChild 和 NestedScrollingParent实现和AppBar的联动
     */
    private void initView() {
//      mRv.setHasFixedSize(true);
//        mRv.setLayoutManager(new LinearLayoutManager(this));
        mRv.setLayoutManager(new GridLayoutManager(this, 2));
//      mRv.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        mRv.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));

        mAdapter = new CommonAdapter<String>(this, R.layout.item_list, mDatas) {
            @Override
            protected void convert(ViewHolder holder, String s, int position) {
                holder.setText(R.id.id_item_list_title, s + " : " + holder.getAdapterPosition() + " , " + holder.getLayoutPosition());
            }
        };

        initHeaderAndFooter();

        //initEmptyView();

        initLoadMore();


        mRv.setAdapter(mLoadMoreWrapper);
        mAdapter.setOnItemClickListener(new CommonAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                Toast.makeText(RecyclerViewActivity.this, "pos = " + position, Toast.LENGTH_SHORT).show();
                mAdapter.notifyItemRemoved(position);
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });
    }


    private void initData() {
        for (int i = 'A'; i <= 'z'; i++) {
            mDatas.add((char) i + "");
        }
    }

    private void initListener() {

    }

    /**
     * 初始化头和尾部
     */
    private void initHeaderAndFooter() {
        mHeaderAndFooterWrapper = new HeaderAndFooterWrapper(mAdapter);

        TextView t1 = new TextView(this);
        t1.setText("Header 1");
        TextView t2 = new TextView(this);
        t2.setText("Header 2");
        mHeaderAndFooterWrapper.addHeaderView(t1);
        mHeaderAndFooterWrapper.addHeaderView(t2);
    }

    /**
     * 加载更多
     */
    private void initLoadMore() {
        mLoadMoreWrapper = new LoadMoreWrapper(mHeaderAndFooterWrapper);
        mLoadMoreWrapper.setLoadMoreView(R.layout.default_loading);
        mLoadMoreWrapper.setOnLoadMoreListener(new LoadMoreWrapper.OnLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < 10; i++) {
                            mDatas.add("Add:" + i);
                        }
                        mLoadMoreWrapper.notifyDataSetChanged();

                    }
                }, 3000);
            }
        });
    }

    /**
     * 初始化添加空布局
     */
    private void initEmptyView() {
        mEmptyWrapper = new EmptyWrapper(mAdapter);
        mEmptyWrapper.setEmptyView(LayoutInflater.from(this).inflate(R.layout.empty_view, mRv, false));
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
    2.mAttachedScrap,mCacheViews只是对View的复用，并且不区分type，ViewCacheExtension,RecycledViewPool是对于ViewHolde
    r的复用，而且区分type。
    3.如果缓存ViewHolder时发现超过了mCachedView的限制，会将最老的ViewHolder(也就是mCachedView缓存队列的第一个ViewHolder)
    移到RecycledViewPool中
     */
}

package com.modesty.quickdevelop.ui.activitys;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.recyclerview.extensions.AsyncListDiffer;
import android.support.v7.util.AdapterListUpdateCallback;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.modesty.quickdevelop.R;
import com.modesty.quickdevelop.adapter.DemoAdapter;
import com.modesty.quickdevelop.adapter.recyclerview.CommonAdapter;
import com.modesty.quickdevelop.adapter.recyclerview.base.ViewHolder;
import com.modesty.quickdevelop.adapter.recyclerview.wrapper.EmptyWrapper;
import com.modesty.quickdevelop.adapter.recyclerview.wrapper.HeaderAndFooterWrapper;
import com.modesty.quickdevelop.adapter.recyclerview.wrapper.LoadMoreWrapper;
import com.modesty.quickdevelop.adapter.rv.DividerItemDecoration;
import com.modesty.quickdevelop.bean.RvTestBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 字节4轮面试，3轮都问了RecyclerView 链接：https://www.jianshu.com/p/b2d504fa53ce
 * Android深入理解RecyclerView的缓存机制 https://blog.csdn.net/Listron/article/details/107952703
 * 你是从哪些方面优化RecyclerView的？
 * 我总结了几点，主要可以从以下几个方面对RecyclerView进行优化：
 * 1.尽量将复杂的数据处理操作放到异步中完成。RecyclerView需要展示的数据经常是从远端服务器上请求获取，但是在网络请求拿到数据之后，需要将数据做扁平化操作，尽量将最优质的数据格式返回给UI线程。
 * 2.优化RecyclerView的布局，避免将其与ConstraintLayout使用
 * 3.针对快速滑动事件，可以使用addOnScrollListener添加对快速滑动的监听，当用户快速滑动时，停止加载数据操作。
 * 4.如果ItemView的高度固定，可以使用setHasFixSize(true)。这样RecyclerView在onMeasure阶段可以直接计算出高度，不
 *   需要多次计算子ItemView的高度，这种情况对于垂直RecyclerView中嵌套横向RecyclerView效果非常显著。
 * 5.当UI是Tab feed流时，可以考虑使用RecycledViewPool来实现多个RecyclerView的缓存共享。
 *
 * 1.首先没有见过特别详细的RecyclerView的源码分析系列，所有关于RecyclerView都是停留在使用或者少数进阶使用的博客
 * 2.RecyclerView，LayoutManager，Adapter，ViewHolder，ItemDecoration这些和RecycleView使用息息相关的类到底是什么关系
 * 3.RecyclerView作为列表，绘制流程到底什么样的
 * 4.RecyclerView有什么不常用的进阶使用方式，但是却很适合RecyclerView作为很“重”的组件的优化，像setRecyclerPool用处到底是什么
 * 5.大家都只要要使用RecyclerView替代ListView和GridView，好用，都在用，但是都没有追究到底这背后的原因到底是什么，
 * RecyclerView到底比ListView好在哪里，到底该不该替换，性能到底提升多少。
 *
 *
 * RecyclerView 动画原理 | 换个姿势看源码（pre-layout）https://juejin.cn/post/6890288761783975950
 * 1.RecyclerView第一次layout时，会发生预布局pre-layout吗？
 * 答：第一次布局时，并不会触发pre-layout。pre-layout只会在每次notify change时才会被触发，目的是通过saveOldPosition方法将屏幕中各位置上的ViewHolder
 * 的坐标记录下来，并在重新布局之后，通过对比实现Item的动画效果
 * 2.如果自定义LayoutManager需要注意什么？（重点）
 * 重点这个supportsPredictiveItemAnimations方法的复写，主要是pre-layoyt；
 * 例如：​ 比如下图中点击item2将其删除，调用notifyItemRemoved后，在pre-layout之前item5并没有被添加到RecyclerView中，
 * 而经过pre-layout之后，item5经过布局会被填充到RecyclerView中当item移出屏幕之后，item5会随同item3和item4一起向上移动
 * 如果自定义LayoutManager并没有实现pre-layout，或者实现不合理，则当item2移出屏幕时，只会将item3和item4进行平滑移动，而item5只是单纯的appear到屏幕中
 * 3.ViewHolder何时被缓存到RecycledViewPool中？
 * 主要有以下2种情况：
 * (1)当ItemView被滑动出屏幕时，并且CachedView已满，则ViewHolder会被缓存到RecycledViewPool中
 * (2)当数据发生变动时，执行完disappearrance的ViewHolder会被缓存到RecycledViewPool中
 *
 *1.RecyclerView为了实现表项动画，进行了 2 次布局，第一次预布局，第二次正真的布局，在源码上表现为LayoutManager.onLayoutChildren()被调用 2 次
 *2. mState.mInPreLayout的值标记了预布局的生命周期。预布局的过程始于RecyclerView.dispatchLayoutStep1()，终于RecyclerView.dispatchLayoutStep2()。
 * 两次调用LayoutManager.onLayoutChildren()会因为这个标记位的不同而执行不同的逻辑分支。
 *3.在预布局阶段，循环填充表项时，若遇到被移除的表项，则会忽略它占用的空间，多余空间被用来加载额外的表项，这些表项在屏幕之外，本来不会被加载。
 *
 * RecyclerView性能问题
 * 1.每次数据变化都全量刷新整个列表是很奢侈的，不仅整个列表会闪烁一下，而且所有可见表项都会重新执行一遍onBindViewHolder()并重绘列表（即便它并不需要刷新）。若表项视图复杂，会显著影响列表性能。
 * 2.onCreateViewHolder和onBindViewHolder对时间非常敏感
 */
public class RecyclerViewActivity extends AppCompatActivity {

    @BindView(R.id.rv)
    RecyclerView mRv;

    private List<String> mDatas = new ArrayList<>();
    private DemoAdapter mAdapter;

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
        mRv.setLayoutManager(new LinearLayoutManager(this));
//        mRv.setLayoutManager(new GridLayoutManager(this, 2));
//      mRv.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
//        mRv.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        mAdapter = new DemoAdapter();
        mRv.setAdapter(mAdapter);
        mAdapter.notifyItemChanged(1);
        //viewType类型为TYPE_SPECIAL时，设置四级缓存池RecyclerPool不存储对应类型的数据(因为对于类型的缓存数值最大为0相当于不缓存)
        // 因为需要开发者自行缓存
        mRv.getRecycledViewPool().setMaxRecycledViews(DemoAdapter.TYPE_SPECIAL, 0);
        //自定义缓存，ViewCacheExtension适用场景：ViewHolder位置固定、内容固定、数量有限时使用
        //ViewCacheExtension使用举例：
        //比如在position=0时展示的是一个广告，位置不变，内容不变，来看看如何实现：
        mRv.setViewCacheExtension(new RecyclerView.ViewCacheExtension() {

            @Override
            public View getViewForPositionAndType(RecyclerView.Recycler recycler, int position, int viewType) {
                //如果viewType为TYPE_SPECIAL,使用自己缓存的View去构建ViewHolder
                // 否则返回null，会使用系统RecyclerPool缓存或者从新通过onCreateViewHolder构建View及ViewHolder
                return viewType == DemoAdapter.TYPE_SPECIAL ? mAdapter.caches.get(position) : null;
            }
        });
    }


    private void initData() {
        //https://mp.weixin.qq.com/s/ZvII9kkWD7jFONB-w9Ob2g
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new callback());
        diffResult.dispatchUpdatesTo(mAdapter);
        //AsyncListDiffer<RvTestBean> rvTestBeanAsyncListDiffer = new AsyncListDiffer<RvTestBean>(new AdapterListUpdateCallback(mAdapter),new callback());
    }

    private void initListener() {

    }


    class callback extends DiffUtil.Callback{
        private List<RvTestBean> oldList = new ArrayList<>();
        private List<RvTestBean> newList = new ArrayList<>();


        @Override
        public int getOldListSize() {
            return oldList.size();
        }

        @Override
        public int getNewListSize() {
            return newList.size();
        }

        /**
         * 是否是同一个数据
         * @return
         */
        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            // 分别获取新老列表中对应位置的元素
            RvTestBean oldItem = oldList.get(oldItemPosition);
            RvTestBean newItem = newList.get(oldItemPosition);
            // 定义什么情况下新老元素是同一个对象（通常是业务id）
            return oldItem.getId() == newItem.getId();
        }

        /**
         * 当areItemsTheSame()返回true时才会被调用
         * @return
         */
        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            RvTestBean oldItem = oldList.get(oldItemPosition);
            RvTestBean newItem = newList.get(oldItemPosition);
            // 定义什么情况下同一对象内容是否相同 (由业务逻辑决定)
            return oldItem.getAge() == newItem.getAge();
        }

        /**
         * 若同一数据的具体内容不同，则找出不同点：对应getChangePayload()（当areContentsTheSame()返回false时才会被调用）
         * @return
         */
        @Nullable
        @Override
        public Object getChangePayload(int oldItemPosition, int newItemPosition) {
            RvTestBean oldItem = oldList.get(oldItemPosition);
            RvTestBean newItem = newList.get(oldItemPosition);
            // 具体定义同一对象内容是如何地不同 (返回值会作为payloads传入onBindViewHoder())
            return super.getChangePayload(oldItemPosition, newItemPosition);
        }
    }


    /*********************** 对于ListView的绘制流程其实我们有了一个大体的了解*******************************/
    /**
     *
     *     1.ActiveView 活跃view
     *     2.ScrapView  废弃View
     */
/*    class RecycleBin {
        private View[] mActiveViews = new View[0];

        private ArrayList<View>[] mScrapViews;

        private int mViewTypeCount;

        private ArrayList<View> mCurrentScrap;

        private ArrayList<View> mSkippedScrap;

        private SparseArray<View> mTransientStateViews;
        private LongSparseArray<View> mTransientStateViewsById;*/


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

缓存到CachedView中的ViewHolder并不会清理相关信息(比如position、state等)，因此刚移出屏幕的ViewHolder，再次被移回屏幕时，
只要从CachedView中查找并显示即可，不需要重新绑定(bindViewHolder)。
        final ArrayList<ViewHolder> mCachedViews = new ArrayList<ViewHolder>();默认大小2

        private final List<ViewHolder>
                mUnmodifiableAttachedScrap = Collections.unmodifiableList(mAttachedScrap);

        private int mRequestedCacheMax = DEFAULT_CACHE_SIZE;
        int mViewCacheMax = DEFAULT_CACHE_SIZE;

而缓存到RecycledViewPool中的ViewHolder会被清理状态和位置信息，因此从RecycledViewPool查找到ViewHolder，
需要重新调用bindViewHolder绑定数据。
        RecycledViewPool mRecyclerPool;默认大小是5

        private ViewCacheExtension mViewCacheExtension;

        static final int DEFAULT_CACHE_SIZE = 2;
    ...
    }
    类的结构也比较清楚，这里可以清楚的看到我们后面讲到的四级缓存机制所用到的类都在这里可以看到
     1.一级缓存：mAttachedScrap
        (Scrap)碎片,废弃，显示在屏幕内的缓存，通过postions获取直接复用不需要走onBindViewHolder，
        只关心position不关心view type，用于notify***等方法
     2.二级缓存：mCacheViews 移除屏幕的缓存，通过postions获取直接复用不需要走onBindViewHolder，
     3.三级缓存：mViewCacheExtension
          1.来看看Recycler中的其他缓存，其中mAttachedScrap用来处理可见屏幕的缓存；
          2.mCachedViews里存储的数据虽然是根据position来缓存，但是里面的数据随时可能会被替换的；
          3.再来看mRecyclerPool，mRecyclerPool里按viewType去存储ArrayList< ViewHolder>，所以mRecyclerPool并
            不能按position去存储ViewHolder，而且从mRecyclerPool取出的View每次都要去走Adapter#onBindViewHolder去重新绑定数据。

     三级缓存使用的场景：假如我现在需要在一个特定的位置(比如position=0位置)一直展示某个View，且里面的内容是不变的，那么最好的情况就是在特定
     位置时，既不需要每次重新创建View，也不需要每次都去重新绑定数据，上面的几种缓存显然都是不适用的，这种情况该怎么办呢？
     可以通过自定义缓存ViewCacheExtension实现上述需求。
     4.四级缓存：mRecyclerPool 脏数据缓存，只关心view type，都需要重新绑定，使用的是SparseArray，存储key为viewType，
     value是ArrayList<ViewHolder>,默认每个ArrayList最多放5个元素
 1.mCachedViews 优先级高于 RecyclerViewPool，回收时，最新的 ViewHolder 都是往 mCachedViews 里放，
  如果它满了，那就移出一个扔到 ViewPool 里好空出位置来缓存最新的 ViewHolder。
 2.复用时，也是先到 mCachedViews 里找 ViewHolder，但需要各种匹配条件，概括一下就是只有原来位置的
 卡位可以复用存在 mCachedViews 里的 ViewHolder，如果 mCachedViews 里没有，那么才去 ViewPool 里找。
 3.在 ViewPool 里的 ViewHolder 都是跟全新的 ViewHolder 一样，只要 type 一样，有找到，就可以拿出来复用，
   重新绑定下数据即可(走onBindViewHolder)。

    1.RecyclerView内部大体可以分为四级缓存：mAttachedScrap,mCacheViews,ViewCacheExtension,RecycledViewPool.
    2.mAttachedScrap,mCacheViews只是对View的复用，并且不区分type，ViewCacheExtension,RecycledViewPool是对于ViewHolde
    r的复用，而且区分type。
    3.如果缓存ViewHolder时发现超过了mCachedView的限制，会将最老的ViewHolder(也就是mCachedView缓存队列的第一个ViewHolder)
    移到RecycledViewPool中

    recycView的性能优化？
    1.在onBindViewHolder里面设置item点击监听
      可以在onCreateViewHolder里面设置
    3.mRv.setHasFixedSize(true)
    4.共用缓存池
    5.DiffUtilWechatIMG6350.png


    itemDecoration的作用有哪些?
    1.画分割线
    2. Recylerview的item是 ImageView 和  TextView构成，当数据改变时，我们会调用 notifyDataSetChanged，这个时候
       列表会刷新，为了使 url 没变的 ImageView 不重新加载（图片会一闪），我们可以用setHasStableIds(true);
    使用这个，相当于给ImageView加了一个tag，tag不变的话，不用重新加载图片。但是加了这句话，会使得 列表的 数据项 重复！！
    我们需要在我们的Adapter里面重写 getItemId就好了。
    @Override
    public long getItemId(int position) {
        return position;
    }*/
}

package com.modesty.quickdevelop.ui.activitys;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.modesty.quickdevelop.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 1.RecycleBin机制，这个机制也是ListView能够实现成百上千条数据都不会OOM最重要的一个原因。
 * 其实RecycleBin的代码并不多，只有300行左右，它是写在AbsListView中的一个内部类
 *
 * 2.谷歌官方不推荐我们使用ScrollView嵌套ListView。ScrollView的高度是由它的子View决定的，
 * 而且它的子View的测量模式为MeasureSpec.EXACTLY模式。
 *
 * 3.第一种方法重写ListView中的onMeasure()方法直接MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE>>2,MeasureSpec.AT_MOST));
 * 4.第二种方式手动去计算ListView中每个Item的高度，然后将这个高度传给ListView,让ScrollView知道自己的子布局有多高，
 *   这样的话ListView就会正常显示了，这个方法在ListView.setAdapter()后调用即可。
 *
 */
public class ListViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);
        List<String> data = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            data.add("我是第"+i+"个数据");
        }
        ListView listView = (ListView) findViewById(R.id.listview);
        listView.setAdapter(new ListAdapter(this,data));
    }


    class ListAdapter extends BaseAdapter{
        private Context mContext;
        private List<String> data;

        public ListAdapter(Context context,List<String> data) {
            mContext = context;
            this.data = data;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        if (convertView == null){
            Log.d("BBBBB","创建了"+position+"个");
            view = LayoutInflater.from(mContext).inflate(R.layout.item_listview, null);//实例化一个对象
            TextView name = (TextView)view.findViewById(R.id.name);
            name.setText(data.get(position));
            view.setTag(view);
        }else {
            Log.d("BBBBB","复用了"+position+"个");
            view = (View) convertView.getTag();
        }

        return view;
    }
}
    /************************************************/
   /* 一、RecycleBin缓存机制

    RecycleBin是写在AbsListView中的，而ListView继承于AbsListView，也自然继承了这个机制。

    变量

 1、private View[] mActiveViews : 缓存屏幕上可见的view

 2、private int mViewTypeCount : ListView中子布局不同类型的类型总数

 3、ArrayList<View>[] mScrapViews : ListView中所有的废弃缓存。这是一个数组，每一种布局类型的view都有一个自己的arraylist缓存。

 4、ArrayList<View> mCurrentScrap : 当前childView布局类型下的缓存

  方法

 1、 void fillActiveViews() : 此方法将listview中指定元素存放到mActiveViews中

 2、View getActiveView() : 从mActiveViews中取出指定元素，取出view后，该位置元素将被置空。

 3、addScrapView() : 将一个废弃view置入缓存。如果布局类型只有一项，则直接存入mCurrentScrap中，如果有多项，从mScrapViews中找到对应废弃缓存并存储。

 4、View getScrapView() : 与addScrapView对应，从相应类型的缓存中，取出view。

 5、setViewTypeCount() : 为mViewTypeCount设置布局类型总数，同时为每一种布局类型启动一个RecycleBin机制。

    RecycleBin缓存机制原理，当一个view滑出界面时，会调用addScrapView方法，将view缓存，当一个view进入页面时，会调用getScrapView方法获取一个view。adpater中的getView方法里的convertView正是取出来的view，如果为空就说明缓存中已经没有view了，需要我们inflate一个。之后就是对holder的操作。
/*/
}

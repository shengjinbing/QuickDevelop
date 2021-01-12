package com.modesty.quickdevelop.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.modesty.quickdevelop.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lixiang on 2021/1/11
 * Describe:
 */
public class DemoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    //viewType类型 TYPE_COMMON代表普通类型 TYPE_SPECIAL代表特殊类型(此处的View和数据一直不变)
    public static final int TYPE_COMMON = 1;
    public static final int TYPE_SPECIAL = 101;

    public SparseArray<View> caches = new SparseArray<>();//开发者自行维护的缓存

    private List<String> mDatas = new ArrayList<>();

    public DemoAdapter() {
        initData();
    }

    private void initData() {
        for (int i = 0; i < 50; i++) {
            if (i == 0) {
                mDatas.add("我是一条特殊的数据，我的位置固定、内容不会变");
            } else {
                mDatas.add("这是第" + (i + 1) + "条数据");
            }
        }
    }

    public List<String> getData() {
        return mDatas;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Log.e("TTT", "-----onCreateViewHolder:" + "viewType is " + viewType + "-----");
        Context context = viewGroup.getContext();
        if (viewType == TYPE_SPECIAL) {
            View view = LayoutInflater.from(context)
                    .inflate(R.layout.item_special_layout, viewGroup, false);
            return new SpecialHolder(view);
        } else {
            View view = LayoutInflater.from(context)
                    .inflate(R.layout.item_common_layout, viewGroup, false);
            return new CommonHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Log.e("TTT", "-----onBindViewHolder:" + "position is " + position + "-----");
        if (holder instanceof SpecialHolder) {
            SpecialHolder sHolder = (SpecialHolder) holder;
            sHolder.tv_ad.setText(mDatas.get(position));
            //这里是重点，根据position将View放到自定义缓存中
            caches.put(position, sHolder.itemView);
        } else if (holder instanceof CommonHolder) {
            CommonHolder cHolder = (CommonHolder) holder;
            cHolder.tv_textName.setText(mDatas.get(position));
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_SPECIAL;//第一个位置View和数据固定
        } else {
            return TYPE_COMMON;
        }
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    class SpecialHolder extends RecyclerView.ViewHolder {
        TextView tv_ad;

        public SpecialHolder(@NonNull View itemView) {
            super(itemView);
            tv_ad = itemView.findViewById(R.id.tv_special_ad);
        }
    }

    class CommonHolder extends RecyclerView.ViewHolder {

        TextView tv_textName;

        public CommonHolder(@NonNull View itemView) {
            super(itemView);
            tv_textName = itemView.findViewById(R.id.tv_text);
        }
    }
}

package com.modesty.quickdevelop.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.modesty.quickdevelop.R;

import java.util.List;

/**
 * Created by lixiang
 * on 2018/12/14
 */
public class CoordinatorAdapter extends RecyclerView.Adapter<CoordinatorAdapter.MyViewHold> {
    private List<String> data;
    private Context mContext;

    public CoordinatorAdapter(Context context, List<String> data) {
        this.data = data;
        mContext = context;
    }

    @NonNull
    @Override
    public MyViewHold onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_coor, parent, false);
        return new MyViewHold(view);
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHold holder, int position) {
        holder.mTextView.setText(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class MyViewHold extends RecyclerView.ViewHolder {

        private final TextView mTextView;

        public MyViewHold(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView.findViewById(R.id.text);
        }
    }
}

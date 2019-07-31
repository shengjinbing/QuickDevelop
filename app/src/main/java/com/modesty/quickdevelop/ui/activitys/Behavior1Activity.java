package com.modesty.quickdevelop.ui.activitys;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.modesty.quickdevelop.R;
import com.modesty.quickdevelop.adapter.recyclerview.CommonAdapter;
import com.modesty.quickdevelop.adapter.recyclerview.base.ViewHolder;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Behavior1Activity extends AppCompatActivity {

    @BindView(R.id.tv_header)
    TextView tvHeader;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_behavior1);
        ButterKnife.bind(this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerview.setLayoutManager(linearLayoutManager);

        ArrayList<String> strings = new ArrayList<>();
        for (int i = 0; i <30; i++) {
            strings.add("我是第"+i+"条");
        }
        recyclerview.setAdapter(new CommonAdapter(this,R.layout.item,strings) {
            @Override
            protected void convert(ViewHolder holder, Object o, int position) {
                holder.setText(R.id.id_info,strings.get(position));
            }
        });
    }
}

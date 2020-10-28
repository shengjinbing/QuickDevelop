package com.modesty.quickdevelop.ui.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;



import com.modesty.quickdevelop.Constants;
import com.modesty.quickdevelop.R;
import com.modesty.quickdevelop.adapter.CoordinatorAdapter;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment2 extends Fragment {

    private View mView;
    private ArrayList<String> objects;
    private CoordinatorAdapter adapter;
    public static Fragment2 newInstance() {
        Fragment2 fragment = new Fragment2();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public Fragment2() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        Log.d(Constants.FRAGMENTTAG,getClass().getName()+"--------------onAttach");
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(Constants.FRAGMENTTAG,getClass().getName()+"--------------onCreate");
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(Constants.FRAGMENTTAG,getClass().getName()+"--------------onCreateView");
        mView = inflater.inflate(R.layout.fragment_fragment_two, container, false);
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d(Constants.FRAGMENTTAG,getClass().getName()+"--------------onActivityCreated");
        super.onActivityCreated(savedInstanceState);
        initView(mView);
    }
    private void initView(View view) {
        RecyclerView recyclerView = (RecyclerView) mView.findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        objects = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            objects.add("我是第"+i+"条目");
        }
        adapter = new CoordinatorAdapter(getContext(),objects);
        recyclerView.setAdapter(adapter);
    }
    @Override
    public void onStart() {
        Log.d(Constants.FRAGMENTTAG,getClass().getName()+"--------------onStart");
        super.onStart();
    }

    @Override
    public void onResume() {
        Log.d(Constants.FRAGMENTTAG,getClass().getName()+"--------------onResume");
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.d(Constants.FRAGMENTTAG,getClass().getName()+"--------------onPause");
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.d(Constants.FRAGMENTTAG,getClass().getName()+"--------------onStop");
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        Log.d(Constants.FRAGMENTTAG,getClass().getName()+"--------------onDestroyView");
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        Log.d(Constants.FRAGMENTTAG,getClass().getName()+"--------------onDestroy");
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        Log.d(Constants.FRAGMENTTAG,getClass().getName()+"--------------onDetach");
        super.onDetach();
    }

    public void updateUI(){
        objects.remove(0);

        adapter.notifyDataSetChanged();
    }
}

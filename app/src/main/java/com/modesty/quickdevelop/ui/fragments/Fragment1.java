package com.modesty.quickdevelop.ui.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.modesty.quickdevelop.Constants;
import com.modesty.quickdevelop.R;

import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment1 extends Fragment {

    private View mView;
    private TextView content;
    public static Fragment1 newInstance() {
        Fragment1 fragment = new Fragment1();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }
    public Fragment1() {
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
        mView = inflater.inflate(R.layout.fragment_fragment_one, container, false);
        content = (TextView) mView.findViewById(R.id.tv_content);
        return mView;
    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d(Constants.FRAGMENTTAG,getClass().getName()+"--------------onActivityCreated");
        super.onActivityCreated(savedInstanceState);
        initView(mView);
    }
    private void initView(View view) {
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
        content.setText(content.getText().toString()+new Random().nextInt(1000));
    }
}

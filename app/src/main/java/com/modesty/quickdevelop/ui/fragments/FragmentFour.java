package com.modesty.quickdevelop.ui.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.modesty.quickdevelop.Constants;
import com.modesty.quickdevelop.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentFour.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentFour#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentFour extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public FragmentFour() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create MVPActivityModelImpl new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentFour.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentFour newInstance(String param1, String param2) {
        FragmentFour fragment = new FragmentFour();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        Log.d(Constants.FRAGMENTTAG,getClass().getName()+"--------------onAttach");
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(Constants.FRAGMENTTAG,getClass().getName()+"--------------onCreate");
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(Constants.FRAGMENTTAG,getClass().getName()+"--------------onCreateView");
        return inflater.inflate(R.layout.fragment_fragment_four, container, false);
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <MVPActivityModelImpl href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</MVPActivityModelImpl> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d(Constants.FRAGMENTTAG,getClass().getName()+"--------------onActivityCreated");
        super.onActivityCreated(savedInstanceState);
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
        mListener = null;
    }
}

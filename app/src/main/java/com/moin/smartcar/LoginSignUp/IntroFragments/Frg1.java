package com.moin.smartcar.LoginSignUp.IntroFragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.moin.smartcar.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class Frg1 extends Fragment {


    public Frg1() {
        // Required empty public constructor
    }

    public static Frg1 getInstance(){
        return (new Frg1());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_frg1, container, false);
    }


}

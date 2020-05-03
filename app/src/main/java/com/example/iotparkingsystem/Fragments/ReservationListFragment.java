package com.example.iotparkingsystem.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.iotparkingsystem.R;


public class ReservationListFragment extends Fragment {

    private View view;
    private Button backButton;

    public ReservationListFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_reservation_list, container, false);
        inicialize();
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStackImmediate("MN",0);
                getActivity().onBackPressed();
            }
        });
        return this.view;
    }

    private void inicialize(){
        backButton = this.view.findViewById(R.id.backButtonRL);
    }


}

package com.example.iotparkingadmin.Activity.Fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.iotparkingadmin.Activity.Objects.DateCheck;
import com.example.iotparkingadmin.R;
import com.google.firebase.database.DatabaseReference;


public class SpotFragment extends DialogFragment {

    private String spotId;
    private View view;
    private TextView spotName;

    public SpotFragment(String spotId) {
        this.spotId = spotId;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_spot,container,false);
        inicialize();
        spotName.setText(spotId);

        return this.view;
    }


    public void inicialize() {

    }



}

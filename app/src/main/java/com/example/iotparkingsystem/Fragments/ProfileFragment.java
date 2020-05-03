package com.example.iotparkingsystem.Fragments;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.iotparkingsystem.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


public class ProfileFragment extends Fragment {

    private View view;
    private FirebaseAuth firebaseAuth;
    private EditText nameEditText,emailEditText,licensPlateEditText;
    private DatabaseReference mDatabaseReference;
    private String name,email,licensPlate;
    private Button backButton,saveButton;

    public ProfileFragment(FirebaseAuth firebaseAuth) {
        this.firebaseAuth = firebaseAuth;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_profile, container, false);
        inicialize();
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStackImmediate("MN",0);
                getActivity().onBackPressed();
            }
        });
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveButtonClick();
            }
        });
        return this.view;
    }

    public void inicialize(){
        this.name = "";
        this.email = "";
        this.licensPlate = "";
        nameEditText = view.findViewById(R.id.nameEditTextPR);
        emailEditText = view.findViewById(R.id.emialEditTextPR);
        licensPlateEditText = view.findViewById(R.id.licensplateEditTextPR);
        backButton = view.findViewById(R.id.backButton);
        saveButton = view.findViewById(R.id.saveButton);
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("IoTSystem");

        fillData();
    }

    public void setName(String name) {
        this.name = name;
        if (this.name.equals("")){
            nameEditText.setHint("Set your name");
            nameEditText.setHintTextColor(Color.RED);
        }else{
            nameEditText.setText(name);
        }

    }

    public void setEmail(String email) {
        this.email = email;
        if (this.email.equals("")){
            emailEditText.setHint("Set your email");
            emailEditText.setHintTextColor(Color.RED);
        }else{
            emailEditText.setText(email);
        }
    }

    public void setLicensPalte(String licensPlate) {
        this.licensPlate = licensPlate;
        if (this.licensPlate.equals("")){
            licensPlateEditText.setHint("Set your licens plate");
            licensPlateEditText.setHintTextColor(Color.RED);
        }else{
            licensPlateEditText.setText(licensPlate);
        }
    }

    private void fillData(){
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("IoTSystem");
        Query query = mDatabaseReference.child("Users").child(firebaseAuth.getUid().toString());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name="", email="", licensplate="";
                for (DataSnapshot child:dataSnapshot.getChildren()){
                    if (child.getKey().equals("name")){
                        name = child.getValue().toString();
                    }
                    if (child.getKey().equals("email")){
                        email = child.getValue().toString();
                    }
                    if (child.getKey().equals("licensPlate")){
                         licensplate = child.getValue().toString();
                    }
                    setName(name);
                    setEmail(email);
                    setLicensPalte(licensplate);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void saveButtonClick(){
        String uid = firebaseAuth.getUid();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("IoTSystem");
        if (!nameEditText.getText().toString().equals("")){
            mDatabaseReference.child("Users").child(uid).child("name").setValue(nameEditText.getText().toString());
        }
        if (!emailEditText.getText().toString().equals("")){
            mDatabaseReference.child("Users").child(uid).child("email").setValue(emailEditText.getText().toString());
        }
        if (!licensPlateEditText.getText().toString().equals("")){
            mDatabaseReference.child("Users").child(uid).child("licensPlate").setValue(licensPlateEditText.getText().toString());
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}

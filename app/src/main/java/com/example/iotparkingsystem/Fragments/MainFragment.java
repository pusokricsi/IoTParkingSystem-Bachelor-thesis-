package com.example.iotparkingsystem.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.iotparkingsystem.Activitys.MainActivity;
import com.example.iotparkingsystem.Objects.GoogleMaps;
import com.example.iotparkingsystem.R;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


public class MainFragment extends Fragment{

    private View view;
    private Button parkingSpot;
    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private Query query;
    private FragmentManager fragmentManager;
    private FreeSpotFragment freeSpotFragment;
    GoogleMaps googleMap;




    public MainFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_main,container,false);
        inicialize();
        main();


        return this.view;
    }


    private void inicialize(){
        ((MainActivity) getActivity()).setActionBarTitle(getResources().getString(R.string.AcctionBarTitle));
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.map);
        googleMap = new GoogleMaps(this.view,mapFragment);

    }

    private void main(){
        Log.i("IoT","Main entered");
        mDatabaseReference = firebaseDatabase.getInstance().getReference("IoTSystem");
        query = mDatabaseReference.child("Modules");
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                parkingSpotSet2(dataSnapshot);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                parkingSpotSet2(dataSnapshot);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot child:dataSnapshot.getChildren()) {
                    parkingSpotSet(child);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void getClickedButton(View v){
        Button button = view.findViewById(v.getId());
        String buttonName = button.getTag().toString();
        fragmentManager = ((AppCompatActivity)view.getContext()).getSupportFragmentManager();
        freeSpotFragment = new FreeSpotFragment(buttonName);
        freeSpotFragment.show(fragmentManager,"fragment_free_spot");

        Log.i("IoT","MainFragment: Button "+buttonName+" has been pressed!");
    }

    public void parkingSpotSet(DataSnapshot dataSnapshot){
        LatLng pos1=null,pos2=null,pos3=null,pos4 = null;
        double lat=0,lng=0;
        String spotName=null;
        Boolean spotStatus=true;
        for (DataSnapshot child:dataSnapshot.getChildren()){
            Log.i("IoT",child.getKey()+"  "+child.getValue());
            if (child.getKey().equals("spotName")){
                spotName = String.valueOf(child.getValue());
                Log.i("IoT","MainFragment: "+child.getValue()+" spot has found by tag!");
            }
            if (child.getKey().equals("spotStatus")){
                Log.i("IoT","MainFragment: Color set in progress...");
                if (child.getValue().equals("true")){
                    spotStatus = true;
                    Log.i("IoT","MainFragment: Color set!");
                }else if (child.getValue().equals("false")){
                    Log.i("IoT","MainFragment: Color set!");
                    spotStatus = false;
                }
            }
            if (child.getKey().equals("pos1")){
                lat = (double) child.child("lat").getValue();
                lng = (double) child.child("lng").getValue();
                pos1 = new LatLng(lat,lng);
            }

            if (child.getKey().equals("pos2")){
                lat = (double) child.child("lat").getValue();
                lng = (double) child.child("lng").getValue();
                pos2 = new LatLng(lat,lng);
            }

            if (child.getKey().equals("pos3")){
                lat = (double) child.child("lat").getValue();
                lng = (double) child.child("lng").getValue();
                pos3 = new LatLng(lat,lng);
            }

            if (child.getKey().equals("pos4")){
                lat = (double) child.child("lat").getValue();
                lng = (double) child.child("lng").getValue();
                pos4 = new LatLng(lat,lng);
            }

        }
        Log.i("IoT","MainFragment: Position:"+pos1.toString()+pos2.toString()+pos3.toString()+pos4.toString());
        if (pos1==null || pos2==null || pos3==null || pos4==null || spotStatus==null){
            Log.i("IoT","MainFragment: Position is not correct!");
        }else {
            googleMap.createPolygon(pos1, pos2, pos3, pos4,spotName,spotStatus);
        }
    }


    public void parkingSpotSet2(DataSnapshot dataSnapshot){
        String spotName=null;
        Boolean spotStatus=true;
        for (DataSnapshot child:dataSnapshot.getChildren()){
            Log.i("IoT",child.getKey()+"  "+child.getValue());
            if (child.getKey().equals("spotName")){
                spotName = String.valueOf(child.getValue());
                Log.i("IoT","MainFragment: "+child.getValue()+" spot has found by tag!");
            }
            if (child.getKey().equals("spotStatus")){
                Log.i("IoT","MainFragment: Color set in progress...");
                if (child.getValue().equals("true")){
                    spotStatus = true;
                    Log.i("IoT","MainFragment: Color set!");
                }else if (child.getValue().equals("false")){
                    Log.i("IoT","MainFragment: Color set!");
                    spotStatus = false;
                }
            }
        }
        Log.i("IoT","MainFragment: Modify:"+spotName);
        if (spotStatus==null || spotName==null){
            Log.i("IoT","MainFragment: Position is not correct!");
        }else {
            googleMap.modifyPolygon(spotName,spotStatus);
        }
    }

}

package com.example.iotparkingsystem.Fragments;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.iotparkingsystem.Activitys.MainActivity;
import com.example.iotparkingsystem.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.maps.android.data.Feature;
import com.google.maps.android.data.kml.KmlContainer;
import com.google.maps.android.data.kml.KmlLayer;

import android.location.Location;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;


public class MainFragment extends Fragment implements OnMapReadyCallback{

    private View view;
    private TextView nameTextView;
    private Button parkingSpot;
    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private Query query;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private FreeSpotFragment freeSpotFragment;
    private GoogleMap mMap;
    private boolean mLocationPermissionGranted;

    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final int DEFAULT_ZOOM = 15;
    private static final int  PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION =1;

    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mLastKnownLocation;
    private KmlLayer kmlLayer;


    public MainFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_main,container,false);
        inicialize();
        main();
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        return this.view;
    }


    private void inicialize(){
        ((MainActivity) getActivity()).setActionBarTitle("Reservations");
        nameTextView = view.findViewById(R.id.nameTextView);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this.view.getContext());

    }

    private void main(){
        Log.i("IoT","Main entered");
        mDatabaseReference = firebaseDatabase.getInstance().getReference("IoTSystem");
        query = mDatabaseReference.child("Modules");
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                parkingSpotSet(dataSnapshot);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                parkingSpotSet(dataSnapshot);
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

        nameTextView.setText(firebaseUser.getEmail());
    }

    public void getClickedButton(View v){
        Button button = view.findViewById(v.getId());
        String buttonName = button.getTag().toString();
        fragmentManager = ((AppCompatActivity)view.getContext()).getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        freeSpotFragment = new FreeSpotFragment(buttonName);
        freeSpotFragment.show(fragmentManager,"fragment_free_spot");

        Log.i("IoT","MainFragment: Button "+buttonName+" has been pressed!");
    }

    public void parkingSpotSet(DataSnapshot dataSnapshot){
        for (DataSnapshot child:dataSnapshot.getChildren()){
            Log.i("IoT",child.getKey()+"  "+child.getValue());
            if (child.getKey().equals("spotName")){
                parkingSpot = view.findViewWithTag(child.getValue());
                Log.i("IoT","MainFragment: "+child.getValue()+" button has found by tag!");

            }
            if (child.getKey().equals("spotStatus")){
                Log.i("IoT","MainFragment: Color set in progress...");
                if (child.getValue().equals("true")){
                    parkingSpot.setBackgroundColor(getResources().getColor(R.color.green));
                    Log.i("IoT","MainFragment: Color set!");
                }else if (child.getValue().equals("false")){
                    parkingSpot.setBackgroundColor(getResources().getColor(R.color.red));
                    Log.i("IoT","MainFragment: Color set!");
                }
            }
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(view.getContext(), "Map is Ready", Toast.LENGTH_SHORT).show();
        mMap = googleMap;
        updateLocationUI();
        getDeviceLocation();
        setKmlLayer();
    }

    private void setKmlLayer(){
        try {
            kmlLayer = new KmlLayer(mMap,R.raw.parkolo,view.getContext());
            kmlLayer.addLayerToMap();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        kmlLayer.setOnFeatureClickListener(new KmlLayer.OnFeatureClickListener() {
            @Override
            public void onFeatureClick(Feature feature) {
                String name;

                name = feature.getProperty("name");
                Log.i("IoT","MainFragment: kmlClick: "+feature.getGeometry());
                for (KmlContainer container : kmlLayer.getContainers()) {
                    if (container.hasProperty("name")) {
                        Log.i("IoT","MainFragment: kmlClickContainer: "+container.getProperty("name"));
                    }
                }
            }
        });
    }






    private void getLocationPermission() {  //Elohoz egy ablakot, ahol keri az eszkoz poziciojat
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getActivity().getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this.getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override                               //UJRAHIVAS, az engedelykerelemre
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        Log.i("IoT","MainFragment: Map is ready!");
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                //mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {

                Task locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = (Location) task.getResult();
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(46.52,24.6), DEFAULT_ZOOM));
                            mMap.addMarker(new MarkerOptions().position(new LatLng(mLastKnownLocation.getLatitude(),mLastKnownLocation.getLongitude())));

                        } else {
                            Log.i("IoT", "Current location is null. Using defaults.");
                            Log.e("IoT", "Exception: %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch(SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }


}

package com.example.iotparkingsystem.Objects;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import com.example.iotparkingsystem.Fragments.FreeSpotFragment;
import com.example.iotparkingsystem.Fragments.MainFragment;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.PolyUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executor;

public class GoogleMaps implements OnMapReadyCallback {

    private GoogleMap mMap;
    private android.view.View view;
    private SupportMapFragment supportMapFragment;
    private FragmentManager fragmentManager;
    private FreeSpotFragment freeSpotFragment;
    private Polygon polygon;
    private DatabaseReference mDatabaseReference;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DateCheck mDateCheck;
    private boolean haveReservation;
    private boolean isOnCheckpoint;



    private final LatLng mDefaultLocation = new LatLng(46.52,24.6);
    private static final int DEFAULT_ZOOM = 20;


    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;
    private Location mLastKnownLocation;

    ArrayList<Polygon> polygons = new ArrayList<>();

    private FusedLocationProviderClient mFusedLocationProviderClient;


    public GoogleMaps(android.view.View view, SupportMapFragment supportMapFragment) {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(view.getContext());
        this.view = view;
        this.supportMapFragment = supportMapFragment;
        this.supportMapFragment.getMapAsync(this);
        this.mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(view.getContext());
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("IoTSystem");
        mDateCheck = new DateCheck();

    }

    public boolean ismLocationPermissionGranted() {
        return mLocationPermissionGranted;
    }

    public void setmLocationPermissionGranted(boolean mLocationPermissionGranted) {
        this.mLocationPermissionGranted = mLocationPermissionGranted;
    }

    public GoogleMap getmMap() {
        return mMap;
    }

    public Location getmLastKnownLocation() {
        return mLastKnownLocation;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(view.getContext(), "Map is Ready", Toast.LENGTH_SHORT).show();
        Log.i("IoT","GoogleMaps: Map is ready!");
        this.mMap = googleMap;
        //setCamera();
        if (mLocationPermissionGranted){
            mMap.setMyLocationEnabled(true);
        }
        //mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

        mMap.setOnPolygonClickListener(new GoogleMap.OnPolygonClickListener() {
            @Override
            public void onPolygonClick(Polygon polygon) {
                Log.i("IoT","GoogleMaps: "+polygon.getTag());
                //polygon.setFillColor(Color.BLUE);
                fragmentManager = ((AppCompatActivity)view.getContext()).getSupportFragmentManager();
                freeSpotFragment = new FreeSpotFragment(String.valueOf(polygon.getTag()));
                freeSpotFragment.show(fragmentManager,"fragment_free_spot");
            }
        });
        getLocationPermission();
        getLocation();
        addGatePolygon();
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(46.5232705,24.5980264), DEFAULT_ZOOM));
    }

    public void setCamera() {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
    }

    public void setCamera(LatLng latLng){
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));
    }

    public void createPolygon(LatLng pos1,LatLng pos2, LatLng pos3, LatLng pos4,String spotName,Boolean spotStatus){
        PolygonOptions polygonOptions = new PolygonOptions()
                .add(pos1)
                .add(pos2)
                .add(pos3)
                .add(pos4)
                .strokeWidth(2)
                .zIndex(10);
        if (spotStatus == true){
            polygonOptions.fillColor(Color.argb(50,51,255,51));
        }else{
            polygonOptions.fillColor(Color.argb(50,255,51,51));
        }
        polygon = mMap.addPolygon(polygonOptions);
        polygon.setClickable(true);
        polygon.setTag(spotName);
        polygons.add(polygon);
        Log.i("IoT","GoogleMaps: Position:"+pos1.toString()+pos2.toString()+pos3.toString()+pos4.toString()+polygon.getTag());
    }

    public void modifyPolygon(String spotName,Boolean spotStatus){
        for (Polygon polygon:polygons){
            if (polygon.getTag().equals(spotName)){
                if (spotStatus == true){
                    polygon.setFillColor(Color.argb(50,51,255,51));
                }else{
                    polygon.setFillColor(Color.argb(50,255,51,51));
                }
            }
        }
    }

    public void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.view.getContext().getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions((Activity) view.getContext(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }


    public void getLocation(){
        LocationRequest mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(500);
        mLocationRequest.setFastestInterval(400);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationCallback mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                Location location = locationResult.getLastLocation();
                //mMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(),location.getLongitude())));
                Log.i("IoT","Device location: "+location.getLongitude()+"  "+location.getLatitude());
                if (!isOnCheckpoint) {
                    //checkIsDeviceInGateZone(new LatLng(location.getLatitude(), location.getLongitude()));
                }


            }
        };
        LocationServices.getFusedLocationProviderClient(this.view.getContext()).requestLocationUpdates(mLocationRequest, mLocationCallback, null);
    }

    public void addGatePolygon(){
            PolygonOptions polygonOptions = new PolygonOptions()
                    .add(new LatLng(47.162549, 23.053623))
                    .add(new LatLng(47.162511, 23.053923))
                    .add(new LatLng(47.162376, 23.053904))
                    .add(new LatLng(47.162411, 23.053590))
                    .strokeWidth(2)
                    .zIndex(10);
            polygonOptions.fillColor(Color.argb(50,255,51,51));
            polygon = mMap.addPolygon(polygonOptions);
            polygon.setClickable(true);
            polygon.setTag("Test");

        }

        private void checkIsDeviceInGateZone(LatLng location){
            LatLngBounds latLngBounds = new LatLngBounds(new LatLng(47.162376, 23.053904),new LatLng(47.162549, 23.053623));
            if (latLngBounds.contains(location)){
                Log.i("IoT","GoogleMaps: Entered in the gate zone!");
                Toast.makeText(this.view.getContext(),"You entered!",Toast.LENGTH_LONG).show();
                isOnCheckpoint = true;
                checkDateIsFree();
            }
        }

    private void checkDateIsFree(){
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        Query query = mDatabaseReference.child("Reservations").orderByChild("userId");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot idChild : dataSnapshot.getChildren()) {
                    String startTime = null;
                    String uId = null;
                    for (DataSnapshot child : idChild.getChildren()) {
                        if (child.getKey().equals("startTime")){
                            startTime = child.getValue().toString();
                        }
                        if (child.getKey().equals("userId")){
                            uId = child.getValue().toString();
                        }
                    }
                    if (uId!=null && startTime!=null){
                        mDateCheck.setPresentDate();
                        if (mDateCheck.checkReservationDate(startTime)){
                            Log.i("IoT","DateCheck: "+mFirebaseAuth.getUid()+" have reservations in 20minutes interval");
                            haveReservation = true;
                        }
                    }
                }
                alertDIalogShow();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void alertDIalogShow(){
        if (haveReservation){
            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this.view.getContext());
            builder.setMessage("You have reservation, do you want to open?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.i("IoT","GoogleMaps/Gate Open");
                            haveReservation = false;
                            isOnCheckpoint = false;
                            mDatabaseReference.child("Modules").child("9009").child("gateStatus").setValue(true);
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.i("IoT","GoogleMaps/Gate Close");
                            haveReservation = false;
                            isOnCheckpoint = false;
                        }
                    });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }else{
            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this.view.getContext());
            builder.setMessage("You doesn't have reservation, please make one and go back!")
                    .setCancelable(false)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.i("IoT","GoogleMaps/Gate Blocked, didn't have reservation");
                            haveReservation = false;
                            isOnCheckpoint = false;
                        }
                    });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }

    }




































    /*private void setKmlLayer(){
        try {
            kmlLayer = new KmlLayer(mMap, R.raw.parkolo,view.getContext());
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
    }*/


}

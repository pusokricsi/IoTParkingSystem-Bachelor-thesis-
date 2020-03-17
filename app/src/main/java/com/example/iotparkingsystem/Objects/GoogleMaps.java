package com.example.iotparkingsystem.Objects;

import android.graphics.Color;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.example.iotparkingsystem.Fragments.FreeSpotFragment;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.maps.android.data.kml.KmlLayer;

import java.util.ArrayList;

public class GoogleMaps implements OnMapReadyCallback {

    private GoogleMap mMap;
    private android.view.View view;
    private SupportMapFragment supportMapFragment;
    private FragmentManager fragmentManager;
    private FreeSpotFragment freeSpotFragment;
    private Polygon polygon;

    private final LatLng mDefaultLocation = new LatLng(46.52,24.6);
    private static final int DEFAULT_ZOOM = 15;

    ArrayList<Polygon> polygons = new ArrayList<>();

    private FusedLocationProviderClient mFusedLocationProviderClient;


    public GoogleMaps(android.view.View view, SupportMapFragment supportMapFragment) {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(view.getContext());
        this.view = view;
        this.supportMapFragment = supportMapFragment;
        this.supportMapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(view.getContext(), "Map is Ready", Toast.LENGTH_SHORT).show();
        Log.i("IoT","GoogleMaps: Map is ready!");
        this.mMap = googleMap;
        setCamera();
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
    }

    public void setCamera() {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
    }

    public void createPolygon1(){
        PolygonOptions polygonOptions = new PolygonOptions()
                .add(new LatLng(46.5227157,24.5977417))
                .add(new LatLng(46.5226912,24.5977525))
                .add(new LatLng(46.5227032,24.5978186))
                .add(new LatLng(46.5227272,24.5978082))
                .fillColor(Color.RED);
        Polygon polygon = mMap.addPolygon(polygonOptions);
        polygon.setClickable(true);
    }

    public void createPolygon2(){
        PolygonOptions polygonOptions = new PolygonOptions()
                .add(new LatLng(46.5226912,24.5977525))
                .add(new LatLng(46.522667,24.5977626))
                .add(new LatLng(46.5226797,24.5978287))
                .add(new LatLng(46.5227032,24.5978186))
                .fillColor(Color.RED);
        Polygon polygon = mMap.addPolygon(polygonOptions);
        polygon.setClickable(true);
    }

    public void createPolygon(LatLng pos1,LatLng pos2, LatLng pos3, LatLng pos4,String spotName,Boolean spotStatus){
        PolygonOptions polygonOptions = new PolygonOptions()
                .add(pos1)
                .add(pos2)
                .add(pos3)
                .add(pos4)
                .strokeWidth(2);
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

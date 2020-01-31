package com.brainybrains.mapapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.FragmentManager;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private TextView locationTV;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private double latitude;
    private double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        locationTV=findViewById(R.id.textView);


        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.map,mapFragment);
        fragmentTransaction.commit();
        mapFragment.getMapAsync(this);
        getDevicrLocation();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


//        LatLng Janata_Tower = new LatLng(23.7540926, 90.3910788);
//        mMap.addMarker(new MarkerOptions().position(Janata_Tower).title("Marker in Janata Tower"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Janata_Tower,16));
        mapBackgroundChange(mMap);
        locationPermitionRequrst();
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);


    }

    private void getDevicrLocation() {
        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(this);
        locationRequest=LocationRequest.create().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(3000).setFastestInterval(1000);

        locationCallback=new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);


                for (Location location:locationResult.getLocations()){
                    latitude=location.getLatitude();
                    longitude=location.getLongitude();
                    LatLng latLng=new LatLng(latitude,longitude);
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,16));
                    try {
                        List<Address> addresses= new Geocoder(MainActivity.this).getFromLocation(latitude,longitude,1);
                        String myLocation=addresses.get(0).getAddressLine(0);
                       // Toast.makeText(MainActivity.this, " "+myLocation, Toast.LENGTH_SHORT).show();
                        locationTV.setText(myLocation);
                      //  mMap.addMarker(new MarkerOptions().position(latLng).title(myLocation));


                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    /*  Toast.makeText(MainActivity.this, "ur location is"+latLng, Toast.LENGTH_SHORT).show();
               */ }
            }
        };

        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},1);
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback,null);

    }

    private void locationPermitionRequrst() {

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){

            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_DENIED){
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            }
        }
    }
    @Override
    public void onRequestPermissionsResult (int requestCode, @NonNull String [] permissions,@NonNull int[] grantResults){
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        if (requestCode==1){
        if (grantResults[0]==PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this, "PERMISSION_GRANTED", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this, "PERMISSION_NOT_GRANTED", Toast.LENGTH_SHORT).show();
        }
    }}
    private void mapBackgroundChange(GoogleMap mMap) {

        try {
            boolean success=mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this,R.raw.changemapdesignapi));
            if (!success){
                Log.e("MainActivity","Style parch faild");
            }

        }
        catch (Resources.NotFoundException e){
            Log.e("MainActivity","can't fing style",e);
        }


    }
}

package com.moin.smartcar;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.vignesh_iopex.confirmdialog.Confirm;
import com.github.vignesh_iopex.confirmdialog.Dialog;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.moin.smartcar.Network.MyApplication;
import com.moin.smartcar.ReportBreakdown.BreakdownEntering;
import com.moin.smartcar.SingeltonData.DataSingelton;

public class BreakdownGoogleMap extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap googleMap1;
    Marker mMarker;
    int cameraZoom = 0;

    double myLatitude = 0.0;
    double myLongitude = 0.0;
    int locationChanged = 0;

    private Button sendButton;


    private DataSingelton mySingelton = DataSingelton.getMy_SingeltonData_Reference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_breakdown_google_map);
        sendButton = (Button) findViewById(R.id.sendLocationButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateUserLocationSending();
            }
        });

        mySingelton.myLat = 0.0;
        mySingelton.myLong = 0.0;
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap1 = googleMap;
            googleMap.getUiSettings().setAllGesturesEnabled(true);
            // Add a marker in Sydney and move the camera
            LatLng UserPosition = new LatLng(18.9750, 72.8258);
            mMarker = googleMap.addMarker(new MarkerOptions().position(UserPosition).title("User Location"));

            googleMap.setMyLocationEnabled(true);
            Location string = googleMap.getMyLocation();
            googleMap.setOnMyLocationChangeListener(myLocationChangeListener);

            CameraPosition cameraPosition = new CameraPosition.Builder().target(UserPosition).zoom(15.0f).build();
            CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
            googleMap.moveCamera(cameraUpdate);

            getCurrentLocation();


            googleMap1.clear();
            LatLng loc = UserPosition;
            if (mMarker != null) {
                mMarker.remove();
            }
            mMarker = googleMap1.addMarker(new MarkerOptions().position(loc));
            mMarker.setTitle("User Location");
            mMarker.setDraggable(true);


            if (googleMap1 != null) {
                googleMap1.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 16.0f));
            }

            googleMap1.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                @Override
                public void onMarkerDragStart(Marker marker) {
                    locationChanged = 1;
                    mySingelton.myLat = marker.getPosition().latitude;
                    mySingelton.myLong = marker.getPosition().longitude;
                }

                @Override
                public void onMarkerDrag(Marker marker) {

                }

                @Override
                public void onMarkerDragEnd(Marker marker) {
                    googleMap1.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 16.0f));
                }
            });
        }

    }

    private GoogleMap.OnMyLocationChangeListener myLocationChangeListener = new GoogleMap.OnMyLocationChangeListener() {
        @Override
        public void onMyLocationChange(Location location) {
            if (locationChanged == 0) {
                LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
                if (mMarker != null) {
                    mMarker.remove();
                }
                mMarker = googleMap1.addMarker(new MarkerOptions().position(loc));
                mMarker.setDraggable(true);
                mMarker.setTitle("User Location");
                myLatitude = location.getLatitude();
                myLongitude = location.getLongitude();

                mySingelton.myLat = myLatitude;
                mySingelton.myLong = myLongitude;

                if (googleMap1 != null && cameraZoom == 0) {
                    cameraZoom = 1;
                    googleMap1.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 16.0f));
                }
            }
        }
    };

    void getCurrentLocation() {
        Location myLocation = googleMap1.getMyLocation();
        if (myLocation != null) {
            double dLatitude = myLocation.getLatitude();
            double dLongitude = myLocation.getLongitude();
            googleMap1.addMarker(new MarkerOptions().position(
                    new LatLng(dLatitude, dLongitude)).title("My Location").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));

            googleMap1.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(dLatitude, dLongitude), 8));

        } else {
//            Toast.makeText(this, "Unable to fetch the current location", Toast.LENGTH_SHORT).show();
        }
    }

    private void validateUserLocationSending() {

        if (mySingelton.myLat == 0.0) {

        } else {
//            Confirm.using(this).ask("This is for customer assistance ," + getResources().getString(R.string.app_name) + " is not involved in this service \nAll payments should be handled by the user directly").onPositive("Yes", new Dialog.OnClickListener() {
//                @Override
//                public void onClick(Dialog dialog, int which) {
//                    Toast.makeText(MyApplication.getAppContext(), "Start Sending User Location", Toast.LENGTH_LONG).show();
//                }
//            }).onNegative("NO", new Dialog.OnClickListener() {
//                @Override
//                public void onClick(Dialog dialog, int which) {
//
//                }
//            }).build().show();

            TextView textView = new TextView(this);
            textView.setPadding(10, 40, 10, 40);
            textView.setText(getResources().getString(R.string.mapInfo));
            textView.setGravity(Gravity.CENTER);
            textView.setTextSize(20);
            Confirm.using(this).askView(textView).onPositive("Ok", new Dialog.OnClickListener() {
                @Override
                public void onClick(Dialog dialog, int which) {
                        startActivity(new Intent(BreakdownGoogleMap.this, BreakdownEntering.class));
                    overridePendingTransition(R.anim.activity_slide_right_in,R.anim.scalereduce);
                }
            }).onNegative("Cancel", new Dialog.OnClickListener() {
                @Override
                public void onClick(Dialog dialog, int which) {

                }
            }).build().show();
        }
    }



    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.scaleincrease, R.anim.slide_right_out);
    }
}

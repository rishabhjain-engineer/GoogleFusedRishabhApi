package com.example.rishabh.googlefusedapi;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private ArrayList<LatLng> mTravellingCoordinates = new ArrayList<>();
    private ArrayList<LatLng> mPlottingCoordinates = new ArrayList<>();
    private GoogleMap mMap;
    private int count = 1;
    private double mCurrentLatitude, mCurrentLongitude;
    private DatabaseReference mFirebaseDataBaseRef;
    private String mDeviceID;
    private static final String NEXUS_DEVICE_ID = "353627071638772";
    private Map<String, List<Coordinate>> mHashMapList = new HashMap();
    private List<String> mDeviceIdList = new ArrayList<>();
    private List<Integer> mColoList = new ArrayList<>();
    private List<Coordinate> mCoordinateList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mColoList.add(Color.RED);
        mColoList.add(Color.GREEN);
        mColoList.add(Color.YELLOW);

        askRunTimePermissions();
        getDeviceID();
        mFirebaseDataBaseRef = FirebaseDatabase.getInstance().getReference("HealthScion");

        mGoogleApiClient = new GoogleApiClient.Builder(MapsActivity.this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(MapsActivity.this)
                .addOnConnectionFailedListener(MapsActivity.this)
                .build();

        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                //.setSmallestDisplacement(15)        // distance in meters
                .setInterval(10 * 1000)        // 15 seconds, in milliseconds
                .setFastestInterval(5 * 1000); // 5 second, in milliseconds


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(MapsActivity.this);

    }


    private void setUpMap() {
        LatLng healthScion = new LatLng(28.583932, 77.323109);
        // mMap.addMarker(new MarkerOptions().position(healthScion).title("HealthScion Pvt Ltd."));
        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("HealthScion Pvt Ltd."));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(healthScion));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
    }

    private void askRunTimePermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.INTERNET, Manifest.permission.READ_PHONE_STATE},
                MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //        Log.e("Rishabh", "Permission Granted");
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.


                } else {
              //      Log.e("Rishabh", "Permission Not Granted");
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }


    private void getDeviceID() {
        TelephonyManager tManager = (TelephonyManager) MapsActivity.this.getSystemService(Context.TELEPHONY_SERVICE);
        mDeviceID = tManager.getDeviceId();
     //   Log.e("Rishabh", "device id := " + mDeviceID);

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
    //    Log.e("Rishabh", "Location services connected.");
        Toast.makeText(MapsActivity.this, "Connected", Toast.LENGTH_SHORT).show();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Toast.makeText(MapsActivity.this, "Permissin not granted", Toast.LENGTH_SHORT).show();
            return;
        }


        if (NEXUS_DEVICE_ID.equalsIgnoreCase(mDeviceID)) {
            Toast.makeText(MapsActivity.this, " You have MASTER device, to track employee", Toast.LENGTH_LONG).show();
        } else {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

            /*Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (location == null) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            }
            else {
                Log.e("Rishabh","On handle location called from coonected method");
                Toast.makeText(MapsActivity.this,"loc not null",Toast.LENGTH_SHORT).show();
                handleNewLocation(location,count);
            }*/


        }


    }

    private void handleNewLocation(Location location, int counter) {
        int c = counter;
        Toast.makeText(MapsActivity.this, "New Loc :" + c + " changed =" + location.toString(), Toast.LENGTH_SHORT).show();
        mCurrentLatitude = location.getLatitude();
        mCurrentLongitude = location.getLongitude();
        LatLng latLng = new LatLng(mCurrentLatitude, mCurrentLongitude);
        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .title("I am going here!");
        mTravellingCoordinates.add(latLng); //added
       // mMap.addMarker(options);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
        redDrawLine(Color.BLUE);
        sendCoordinatesToFirebaseDatabase();

    }

    private void redDrawLine(int color) {

        //mMap.clear();  //clears all Markers and Polylines
        PolylineOptions options = new PolylineOptions().width(5).color(color).geodesic(true);
        for (int i = 0; i < mTravellingCoordinates.size(); i++) {
            LatLng point = mTravellingCoordinates.get(i);
            options.add(point);
        }
        //    addMarker(); //add Marker in current position
        //     line = mMap.addPolyline(options); //add Polyline
        mMap.addPolyline(options);
    }

    private void drawGraphForAdmin(int color) {
        PolylineOptions options = new PolylineOptions().width(5).color(color).geodesic(true);
        for (int i = 0; i < mPlottingCoordinates.size(); i++) {
            LatLng point = mPlottingCoordinates.get(i);
            options.add(point);
        }
        //    addMarker(); //add Marker in current position
        //     line = mMap.addPolyline(options); //add Polyline
        mMap.addPolyline(options);
    }


    private void sendCoordinatesToFirebaseDatabase() {

      //  Log.e("Rishabh", "sending Data to Firebase := " + mDeviceID + " " + mCurrentLatitude + " " + mCurrentLongitude);

        mFirebaseDataBaseRef.child("deviceId").setValue(mDeviceID);
        mFirebaseDataBaseRef.child("coordinates").child("Latitude").setValue(mCurrentLatitude);
        mFirebaseDataBaseRef.child("coordinates").child("Longitude").setValue(mCurrentLongitude);

    }


    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(MapsActivity.this, "Connection Suspended", Toast.LENGTH_SHORT).show();
       // Log.e("Rishabh", "Location services suspended. Please reconnect");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(MapsActivity.this, "Connection Fail", Toast.LENGTH_SHORT).show();
      //  Log.i("Rishabh", "Location services connection failed with code " + connectionResult.getErrorCode());
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (NEXUS_DEVICE_ID.equalsIgnoreCase(mDeviceID)) {

            mFirebaseDataBaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    Log.e("Rishabh", "JSON" + dataSnapshot.getValue().toString());


                    // JSON :=  {deviceId=862512030120555, coordinates={Longitude=77.3220119, Latitude=28.5818988}}

                    String latitude = String.valueOf(dataSnapshot.child("coordinates").child("Latitude").getValue());
                    String longitude = String.valueOf(dataSnapshot.child("coordinates").child("Longitude").getValue());
                    String deviceId = String.valueOf(dataSnapshot.child("deviceId").getValue());

              //      Log.e("Rishabh", "User Location Changed := " + "Latitude :" + latitude + " " + " Longitude : " + longitude);

                    Toast.makeText(MapsActivity.this, "Latitude : " + latitude + " " + " Longitude : " + longitude, Toast.LENGTH_SHORT).show();

                    double l1 = Double.parseDouble(latitude);

                    double l2 = Double.parseDouble(longitude);

                    List<Coordinate> coordinatesList = null;
                    if (!mDeviceIdList.contains(deviceId)) {
                        mDeviceIdList.add(deviceId);
                        coordinatesList = new ArrayList<Coordinate>();
                    } else {
                        coordinatesList = mHashMapList.get(deviceId);
                    }

                    Coordinate coordinate = new Coordinate();
                    coordinate.setLongitude(l2);
                    coordinate.setLatitude(l1);
                    coordinatesList.add(coordinate);

                    int position = mDeviceIdList.indexOf(deviceId);
                    int color = mColoList.get(position);

                    mHashMapList.put(deviceId, coordinatesList);

                    mMap.clear();

                    for (int i = 0; i < coordinatesList.size(); i++) {
                        Coordinate coordinate1 = coordinatesList.get(i);
                        showMapOnMasterDevice(coordinate1.getLatitude(), coordinate1.getLongitude(), color);
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });

        }


    }

    private void showMapOnMasterDevice(double a, double b, int color) {



      //  Log.e("Rishabh", "latitude in master deveice := " + a);
      //  Log.e("Rishabh", "longitude in master deveice := " + b);

        LatLng latLng = new LatLng(a, b);
        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .title("I am going here!");
        mPlottingCoordinates.add(latLng); //added

     //   mMap.addMarker(options);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
        drawGraphForAdmin(color);


    }



    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mTravellingCoordinates.clear();
        mPlottingCoordinates.clear();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onLocationChanged(Location location) {
     //   Log.e("Rishabh", "onLocationChange called");

        if (!location.hasAccuracy()) {
            return;
        }
        if (location.getAccuracy() > 3) {
            count = count + 1;
            handleNewLocation(location, count);
        }


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        setUpMap();
    }
}

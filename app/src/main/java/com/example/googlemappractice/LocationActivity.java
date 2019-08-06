package com.example.googlemappractice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LocationActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnCameraMoveListener,
        GoogleMap.OnCameraMoveCanceledListener,
        GoogleMap.OnCameraIdleListener,
        GoogleMap.OnMarkerClickListener {
    @BindView(R.id.location_map)
    MapView mMapView;
    @BindView(R.id.input_latitude)
    EditText editText_lat;
    @BindView(R.id.input_longitude)
    EditText editText_long;
    private LatLngBounds mMapBoundary;
    private GoogleMap mGoogleMap;
    double latitude;
    double longitude;
    public static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    private Marker mMarker;
    private UserLocation userLocation;
    private String userId;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        ButterKnife.bind(this);
        mAuth=FirebaseAuth.getInstance();
        userId=mAuth.getCurrentUser().getUid();
        userLocation=new UserLocation();
        Intent intent=new Intent();
        Log.d("OnCreate:","create");
        Bundle b = getIntent().getExtras();
//        double result = b.getDouble("key");
        if(b!=null){
            latitude=b.getDouble("latitude");
            longitude=b.getDouble("longitude");
            Log.d("OnCreate: ",""+latitude+" "+longitude);
        }
        initGoogleMap(savedInstanceState);
    }

    /**
     * Determines the view boundary then sets the camera
     * Sets the view
     */
    private void setCameraView() {

        // Set a boundary to start
        double bottomBoundary = latitude - .1;
        double leftBoundary = longitude - .1;
        double topBoundary = latitude + .1;
        double rightBoundary = longitude + .1;

        mMapBoundary = new LatLngBounds(
                new LatLng(bottomBoundary, leftBoundary),
                new LatLng(topBoundary, rightBoundary)
        );

        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(mMapBoundary, 0));
    }

    private void initGoogleMap(Bundle savedInstanceState){
        // *** IMPORTANT ***
        // MapView requires that the Bundle you pass contain _ONLY_ MapView SDK
        // objects or sub-Bundles.
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }

        mMapView.onCreate(mapViewBundle);

        mMapView.getMapAsync(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }

        mMapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mMapView.onStop();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        map.addMarker(new MarkerOptions().position(new LatLng(latitude,longitude)).title("Marker"));
        if (ActivityCompat.checkSelfPermission(LocationActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(LocationActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        map.setMyLocationEnabled(true);
        //Toast.makeText(UserListFragment.this, "LatLang:"+map.getMyLocation(), Toast.LENGTH_SHORT).show();
        mGoogleMap = map;
        setCameraView();
        try {
            Geocoder geo = new Geocoder(LocationActivity.this.getApplicationContext(), Locale.getDefault());
            List<Address> addresses = geo.getFromLocation(latitude, longitude, 1);
            if (addresses.isEmpty()) {
                mMarker = mGoogleMap.addMarker(new MarkerOptions().position(mGoogleMap.getCameraPosition().target).anchor(0.5f, .05f).title("Unknown"));
                mMarker.showInfoWindow();

                //yourtextfieldname.setText("Waiting for Location");
            }
            else {
                addresses.size();
                mMarker = mGoogleMap.addMarker(new MarkerOptions().position(mGoogleMap.getCameraPosition().target).anchor(0.5f, .05f).title(addresses.get(0).getFeatureName() + ", " + addresses.get(0).getLocality() +", " + addresses.get(0).getAdminArea() + ", " + addresses.get(0).getCountryName()));
                //Toast.makeText(getApplicationContext(), "Address:- " + addresses.get(0).getFeatureName() + addresses.get(0).getAdminArea() + addresses.get(0).getLocality(), Toast.LENGTH_LONG).show();
                mMarker.showInfoWindow();
            }
        }
        catch (Exception e) {
            e.printStackTrace(); // getFromLocation() may sometimes fail
        }
        mGoogleMap.setOnCameraIdleListener(this);
        //m_map.setOnCameraMoveStartedListener(this);
        mGoogleMap.setOnMarkerClickListener(this);
        mGoogleMap.setOnCameraMoveListener(this);
        mGoogleMap.setOnCameraMoveCanceledListener(this);
    }

    @Override
    public void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onCameraIdle() {

        try {
            Geocoder geo = new Geocoder(LocationActivity.this.getApplicationContext(), Locale.getDefault());
            List<Address> addresses = geo.getFromLocation(mMarker.getPosition().latitude, mMarker.getPosition().longitude, 1);
            if (addresses.isEmpty()) {
                mMarker = mGoogleMap.addMarker(new MarkerOptions().position(mGoogleMap.getCameraPosition().target).anchor(0.5f, .05f).title("Unknown"));
                mMarker.showInfoWindow();

               //yourtextfieldname.setText("Waiting for Location");
            }
            else {
                addresses.size();
                mMarker = mGoogleMap.addMarker(new MarkerOptions().position(mGoogleMap.getCameraPosition().target).anchor(0.5f, .05f).title(addresses.get(0).getFeatureName() + ", " + addresses.get(0).getLocality() +", " + addresses.get(0).getAdminArea() + ", " + addresses.get(0).getCountryName()));
                //Toast.makeText(getApplicationContext(), "Address:- " + addresses.get(0).getFeatureName() + addresses.get(0).getAdminArea() + addresses.get(0).getLocality(), Toast.LENGTH_LONG).show();
                mMarker.showInfoWindow();
            }
        }
        catch (Exception e) {
            e.printStackTrace(); // getFromLocation() may sometimes fail
        }

        double latitude=mMarker.getPosition().latitude;
        double longitude=mMarker.getPosition().longitude;
        editText_lat.setText(String.valueOf(latitude));
        editText_long.setText(String.valueOf(longitude));
        GeoPoint geoPoint=new GeoPoint(latitude,longitude);
        userLocation.setGeoPoint(geoPoint);
        userLocation.setTimesTamp(null);
        userLocation.setUserId(userId);


    }

    @Override
    public void onCameraMoveCanceled() {

    }

    @Override
    public void onCameraMove() {
        //Remove previous center if it exists
        if (mMarker != null) {
            mMarker.remove();
            mMarker.setVisible(false);
        }
        mGoogleMap.clear();
        CameraPosition test =mGoogleMap.getCameraPosition();
        //Assign mCenterMarker reference:
        mMarker = mGoogleMap.addMarker(new MarkerOptions().position(mGoogleMap.getCameraPosition().target).anchor(0f, 0f));
//        mMarker.showInfoWindow();


    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        //Toast.makeText(this, "latitude: "+latitude+" longitude: "+langitude, Toast.LENGTH_SHORT).show();
        return false;
    }

    @OnClick(R.id.save_btn)
    void saveLocation(){
        saveUserLocation();
    }

    private void saveUserLocation(){
        FirebaseFirestore mDb=FirebaseFirestore.getInstance();
        String id = mDb.collection("User Locations").document().getId();
        if(userLocation != null){
            DocumentReference locationRef = mDb
                    .collection("User Locations")
                    .document(id);

            locationRef.set(userLocation).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(LocationActivity.this, "Save locations", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}

package com.example.googlemappractice;

import androidx.appcompat.app.AppCompatActivity;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearachActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    @BindView(R.id.input_search1)
    AutoCompleteTextView searchEditTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searach);
        ButterKnife.bind(this);
        //searchEditTxt=(EditText)findViewById(R.id.input_search1);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void init(){
        Log.d("Initialization:","Init");
        searchEditTxt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                Log.d("get:","get");
                if(i== EditorInfo.IME_ACTION_SEARCH || i==EditorInfo.IME_ACTION_DONE||i==EditorInfo.IME_ACTION_NEXT
                    || keyEvent.getAction()==KeyEvent.ACTION_DOWN
                    || keyEvent.getAction()==KeyEvent.KEYCODE_ENTER){

                    //execute methode for search
                    geoLocation();

                }
                //geoLocation();
                return false;
            }
        });
        hideKeybord();
    }

    private void geoLocation(){
        Log.d("Search: ","Geolocation");

        String searchString=searchEditTxt.getText().toString();
        Geocoder geocoder=new Geocoder(SearachActivity.this);
        List<Address> list=new ArrayList<>();
        try{
            list=geocoder.getFromLocationName(searchString,1);
        }catch (IOException e){
            Log.e("Error: ","GeoLocation IOException "+e.getMessage());
        }
        if(list.size()>0){
            Address address=list.get(0);
            Log.d("Result:","Found location: "+address.toString());
            moveCamera(new LatLng(address.getLatitude(),address.getLongitude()),14,address.getAddressLine(0));
        }

    }

    private void moveCamera(LatLng latLng,float zoom,String title){
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom));
        MarkerOptions markerOptions=new MarkerOptions()
                .position(latLng)
                .title(title);
        mMap.addMarker(markerOptions);
        hideKeybord();
    }

    private void hideKeybord(){
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney, Australia, and move the camera.
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        init();
    }
}

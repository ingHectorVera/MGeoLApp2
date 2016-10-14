package com.example.hectorvera.mgeolapp;

import android.app.Dialog;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {


    private GoogleMap mMap;
    private static final int GS_REQUEST_CODE = 9001;
    private static final float DEFAULTZOOM = 18;
    private RadioGroup rgTypeMap;
    private SupportMapFragment mapFragment;
    private int count = 0;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        if (servicesOK()) {
            setContentView(R.layout.activity_main);
            if (initMap()) {
                Toast.makeText(this, "Ready to map", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Map not available", Toast.LENGTH_SHORT).show();
            }
        } else {
            //setContentView(R.layout.activity_main);
        }

        rgTypeMap = ((RadioGroup) findViewById(R.id.rgTypeMap));
        rgTypeMap.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rSat:
                        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                        break;
                    case R.id.rNormal:
                        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        break;
                    case R.id.rHybrid:
                        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                        break;
                    case R.id.rTerrain:
                        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                        break;
                    case R.id.rNone:
                        mMap.setMapType(GoogleMap.MAP_TYPE_NONE);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        MapStateManager mgr = new MapStateManager(this);
        mgr.saveMapState(mMap);
    }

    protected void onRestart() {
        super.onRestart();
        MapStateManager mgr = new MapStateManager(this);
        CameraPosition position = mgr.getSavedCameraPosition();
        if (position != null) {
            CameraUpdate update = CameraUpdateFactory.newCameraPosition(position);
            mMap.moveCamera(update);
        }
    }

    private void gotoLocation(double lat, double lng, float zoom, String locality) {
        LatLng ll = new LatLng(lat, lng);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, zoom);
        mMap.addMarker(new MarkerOptions().position(ll).title(locality));
        mMap.moveCamera(update);
    }

    private void gotoLocation(double lat, double lng, float zoom ,GoogleMap googleMap){
        //mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng mexico = new LatLng(lat, lng);
        mMap.addMarker(new MarkerOptions().position(mexico).title("Marker in Mexico"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(mexico));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mexico,zoom));

    }

    public void geoLocate(View view) throws IOException {
        hideSoftKeyboard(view);

        EditText et = ((EditText) findViewById(R.id.editText1));
        String location = et.getText().toString();

        Geocoder gc = new Geocoder(this);
        List<Address> list = gc.getFromLocationName(location, 1);

        Address add = list.get(0);

        String locality = add.getLocality();

        Toast.makeText(this, locality, Toast.LENGTH_SHORT).show();

        double lat = add.getLatitude();
        double lng = add.getLongitude();
        gotoLocation(lat, lng, DEFAULTZOOM, locality);
    }

    private void hideSoftKeyboard(View v) {
        InputMethodManager imm = ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE));
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }


    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
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
        gotoLocation(19.491903, -99.135972, DEFAULTZOOM, mMap);
    }

    public boolean servicesOK(){
        GoogleApiAvailability gaa = GoogleApiAvailability.getInstance();
        int isAvailable =  gaa.isGooglePlayServicesAvailable(this);

        if(isAvailable == ConnectionResult.SUCCESS){
            return true;
        }else if(gaa.isUserResolvableError(isAvailable)) {
            Dialog dialog = gaa.getErrorDialog(this,isAvailable,  GS_REQUEST_CODE);
            dialog.show();
        }else {
            Toast.makeText(this, "Can't connect to Google Play services", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private boolean initMap(){
        if(mapFragment == null){
            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }
        return (mapFragment != null);
    }

}

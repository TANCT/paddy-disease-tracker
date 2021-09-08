package com.example.paddy_disease_tracker;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.paddy_disease_tracker.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    String query;
    Statement st;
    ResultSet rs;
    Toast toast;
    String disease,status;
    float lat,lon;
    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    LatLng centerLocation,place;
    Connection connect;
    ArrayList<LatLng> places=new ArrayList<LatLng>();
    ArrayList<String> dis=new ArrayList<String>();
    Intent intent;
    float lati=0,longi=0;
    int focus=6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Intent intent=this.getIntent();
        if(intent!=null){
            lati=intent.getFloatExtra("lat",0);
            longi=intent.getFloatExtra("lon",0);

        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        if(lati!=0&&longi!=0){
            centerLocation=new LatLng(lati,longi);
            focus=12;
        }else{
            centerLocation=new LatLng(6.0,101);
        }

        try {
            ConnectionHelper connectionHelper = new ConnectionHelper();
            connect = connectionHelper.connectionclass();

            if (connect != null) {
                query = "Select * from Disease";
                st = connect.createStatement();
                rs = st.executeQuery(query);
                while (rs.next()) {
                    status=rs.getString(7);
                    if(status.equals("Found")){
                        disease=rs.getString(3);
                        lat=rs.getFloat(4);
                        lon=rs.getFloat(5);
                        places.add(new LatLng(lat,lon));
                        dis.add(disease);
                }
                }
            } else {
                toast=Toast.makeText(getApplicationContext(),"Check Connection",Toast.LENGTH_LONG);
                toast.show();
            }
        }catch(Exception e){
            toast=Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG);
            toast.show();
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        enableMyLocation();
        // Add a marker in Sydney and move the camera
        for(int i=0; i<places.size();i++){
            mMap.addMarker(new MarkerOptions().position(places.get(i)).title(dis.get(i)+" found"));
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(centerLocation,focus));
    }

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (mMap != null) {
                mMap.setMyLocationEnabled(true);
            }
        } else {
            String []perms={"android.permission.ACCESS_FINE_LOCATION"};
            // Permission to access the location is missing. Show rationale and request permission
            ActivityCompat.requestPermissions(this,perms,200);

        }
    }
}

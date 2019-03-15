package com.example.mikiswahn.vehiclematch;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Consat.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Consat office and move the camera
        LatLng consat = new LatLng(57.707001, 11.941134);
        mMap.addMarker(new MarkerOptions().position(consat).title("Marker by Consat"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(consat, 15));
    }

    /*
    * Värt att testa hur bra masp aktivitten är på at updatera my location
    *
    * The ACCESS_COARSE/FINE_LOCATION permissions are not required to use
    * Google Maps Android API v2, but you must specify either coarse or fine
    * location permissions for the 'MyLocation' functionality.
    * */
}

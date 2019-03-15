package com.example.mikiswahn.vehiclematch;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.os.Bundle;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.TextView;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.model.LatLng;

import static com.example.mikiswahn.vehiclematch.AppDatabase.getAppDatabase;

public class LocationActivity extends FragmentActivity{

    private LocationCallback locationCallback;
    private LocationRequest locationRequest;
    private FusedLocationProviderClient fusedLocationClient;
    private ArrayList<TextView> textUI = new ArrayList<>();
    int count = 1;

    // Bryt ut db & UI update ur denna logik. skapa egen aktivitet för det.
    @Override
    protected void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        addUIComponents();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    printPassengerLocation(location);
                }
            };
        };
        createLocationRequest();
    }

    protected void createLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        //se public LocationRequest setMaxWaitTime (long millis)
        //se public LocationRequest setNumUpdates (int numUpdates)
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        //detta fungerar.
        //men för komersiellt bruk ska du egentligen kolla att användaren gett din app alla permissions osv.
        //"Next check whether the current location settings are satisfied:"
        //SettingsClient client =...
        //Task<LocationSettingsResponse> task = ...
        //task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
        //task.addOnFailureListener(this, new OnFailureListener() {
        //https://developer.android.com/training/location/change-location-settings.html
    }

    protected void onResume() {
        super.onResume();
        //if (requestingLocationUpdates){... där rLU är en bool basetat på bundle savedinstancestate
        startLocationUpdates();
    }

    private void startLocationUpdates() {
        try {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null /* Looper */);
        }
        catch (SecurityException e){
            Log.i("security", "not connected to Google PLay Services Client");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Stop location updates while paused?
        //fusedLocationClient.removeLocationUpdates(locationCallback);
        //Just nu prenumererar den inte om appen är pausad ändå
    }

    //Bundle extra = location.getExtras();
    //information such as K/V-pair: satellites - the number of satellites used to derive the fix
    //String provider = location.getProvider();
    //Returns the name of the provider that generated this fix (dvs.location). typ gps antar jag?
    public void printPassengerLocation(Location location){
        final double lat = location.getLatitude();
        final double lng = location.getLongitude();
        //String lngs = lng.toString();
        //String lats = lat.toString();
        String coordinate = "(" + lat + ",  " + lng + ")";
        float speedms = location.getSpeed();
        final float speedkmh = speedms * 3.6f;
        String speed = Float.toString(speedkmh);
        if (speed.length() > 5 ){
            speed = (speed.substring(0,5));
        }
        final float bearing = location.getBearing();

        // TODO Return the UTC time of this fix, in milliseconds since January 1, 1970.
        long timestamp = location.getTime();
        Date date = new java.util.Date(timestamp*1000L);
        SimpleDateFormat sdf = new java.text.SimpleDateFormat("h:mm a");
        sdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT+1"));
        String formattedDate = sdf.format(date);
        // TODO tiden blir jättefel. vaför?
        float elapsedTime = location.getElapsedRealtimeNanos();
        String time=String.valueOf(elapsedTime);
        /*
        public Long dateToTimestamp(Date date) {
            if (date == null) {
                return null;
            } else {
                return date.getTime();
            }
        }*/

        Context context = LocationActivity.this;
        final AppDatabase passengerDb = getAppDatabase(context);

        //Always use a Thread or AsyncTask for db calls, or the app will crash
        new Thread(new Runnable() {
            @Override
            public void run() {
                Passenger passenger = new Passenger();
                passenger.setPid(0); //från 0 till 120 st typ, så har man 2 min data
                //passenger.setLng(lat);
                //passenger.setLat(lng);
                //passenger.setVelocity(speedkmh);
                //passenger.setBearing(bearing);
                passengerDb.passengerDao().insert(passenger);
            }
        }) .start();


        String snapshot =  String.join("  |  ", coordinate, (speed + " km/h"),
                Float.toString(bearing));
        if (count == 36) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
        else{
            TextView text = textUI.get(count-1);
            text.setText(snapshot);
            count ++;
        }
    }


    private void addUIComponents(){
        //gör en tabell istället
        /*textUI.add(findViewById(R.id.textView1)); //Buggar*/
        TextView t1 = findViewById(R.id.textView1);
        TextView t2 = findViewById(R.id.textView2);
        TextView t3 = findViewById(R.id.textView3);
        TextView t4 = findViewById(R.id.textView4);
        TextView t5 = findViewById(R.id.textView5);
        TextView t6 = findViewById(R.id.textView6);
        TextView t7 = findViewById(R.id.textView7);
        TextView t8 = findViewById(R.id.textView8);
        TextView t9 = findViewById(R.id.textView9);
        TextView t10 = findViewById(R.id.textView10);
        TextView t11 = findViewById(R.id.textView11);
        TextView t12 = findViewById(R.id.textView12);
        TextView t13 = findViewById(R.id.textView13);
        TextView t14 = findViewById(R.id.textView14);
        TextView t15 = findViewById(R.id.textView15);
        TextView t16 = findViewById(R.id.textView16);
        TextView t17 = findViewById(R.id.textView17);
        TextView t18 = findViewById(R.id.textView18);
        TextView t19 = findViewById(R.id.textView19);
        TextView t20 = findViewById(R.id.textView20);
        TextView t21 = findViewById(R.id.textView21);
        TextView t22 = findViewById(R.id.textView22);
        TextView t23 = findViewById(R.id.textView23);
        TextView t24 = findViewById(R.id.textView24);
        TextView t25 = findViewById(R.id.textView25);
        TextView t26 = findViewById(R.id.textView26);
        TextView t27 = findViewById(R.id.textView27);
        TextView t28 = findViewById(R.id.textView28);
        TextView t29 = findViewById(R.id.textView29);
        TextView t30 = findViewById(R.id.textView30);
        TextView t31 = findViewById(R.id.textView31);
        TextView t32 = findViewById(R.id.textView32);
        TextView t33 = findViewById(R.id.textView33);
        TextView t34 = findViewById(R.id.textView34);
        TextView t35 = findViewById(R.id.textView35);
        textUI.add(t1);
        textUI.add(t2);
        textUI.add(t3);
        textUI.add(t4);
        textUI.add(t5);
        textUI.add(t6);
        textUI.add(t7);
        textUI.add(t8);
        textUI.add(t9);
        textUI.add(t10);
        textUI.add(t11);
        textUI.add(t12);
        textUI.add(t13);
        textUI.add(t14);
        textUI.add(t15);
        textUI.add(t16);
        textUI.add(t17);
        textUI.add(t18);
        textUI.add(t19);
        textUI.add(t20);
        textUI.add(t21);
        textUI.add(t22);
        textUI.add(t23);
        textUI.add(t24);
        textUI.add(t25);
        textUI.add(t26);
        textUI.add(t27);
        textUI.add(t28);
        textUI.add(t29);
        textUI.add(t30);
        textUI.add(t31);
        textUI.add(t32);
        textUI.add(t33);
        textUI.add(t34);
        textUI.add(t35);
    }


}

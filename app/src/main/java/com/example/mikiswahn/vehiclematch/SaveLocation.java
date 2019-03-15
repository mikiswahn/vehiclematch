package com.example.mikiswahn.vehiclematch;

import java.text.SimpleDateFormat;
import java.util.Date;
import android.content.Context;
import android.location.Location;
import android.widget.TextView;
import static com.example.mikiswahn.vehiclematch.AppDatabase.getAppDatabase;

//TODO: har börjat refaktorera men pallar kanske inte.
public class SaveLocation {

    int count = 1;

/*
    public void savePassengerLocation(Location location){

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



        Context context = LocationActivity.this;
        final AppDatabase passengerDb = getAppDatabase(context);

        //Always use a Thread or AsyncTask for db calls, or the app will crash
        new Thread(new Runnable() {
            @Override
            public void run() {
                Passenger passenger = new Passenger();
                passenger.setPid(0); //från 0 till 120 st typ, så har man 2 min data
                passenger.setLng(lat);
                passenger.setLat(lng);
                passenger.setVelocity(speedkmh);
                passenger.setBearing(bearing);
                passengerDb.passengerDao().insert(passenger);
            }
        }) .start();
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

/*
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

*/

}

package com.example.mikiswahn.vehiclematch;

//TODO: Readmefil som påpekar alla buggar pga inte kommersiell produkt


import java.util.ArrayList;
import java.lang.Math;
import java.util.Collections;
import java.util.Date;
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




/** Class for collecting location data about a passenger. */

public class LocationActivity extends FragmentActivity implements AsyncResponse{

    //Half the side length of bounding box
    //=The radius in meters around a passenger where their vehicle is assumed to be within
    //D in report, in meters, based on GPS error and max vehicle length
    private double MAX_DISTANCE_VEHICLE_PASSENGER = 60;
    //private double MAX_DISTANCE_VEHICLE_PASSENGER = 320; //For testing at lindholmen office
    private double EARTH_RADIUS = 6363000; //meters. Västra Götaland region
    long ONESECOND = 1000;
    long THREESECONDS = 3000;
    //How often the passenger location will update
    long locationIntervall = THREESECONDS;
    // the first 18(?) locations will only be recorded in the txt file but never used for matching. (empirical tests showed that no vehicle data was retrieved for initial positions)
    Integer DISCARD_INITIAL_LOCATIONS = 8;
    Integer discardedCounter = 1;
    private int countSnapshots = 1;
    int maxNrOfSnapshots = 120; //about two minutes of data, if collected every second

    private LocationCallback locationCallback;
    private LocationRequest locationRequest;
    private FusedLocationProviderClient fusedLocationClient;
    public ArrayList<TextView> textUI = new ArrayList<>();
    private LocationSaver locationSaver;

    // The passengerSnapshotID is a unique ID for each passenger snapshot (snapshot = time and location)
    // It is used for pairing* an API vehicle response with the right passenger position initiating the query
    private Integer passengerSnapshotIdCount = 1;
    //*A PassengerVehiclePairing is a passenger snapshot and a list of the vehicles nearby at that time
    private ArrayList<PassengerVehiclePairing> pairings = new ArrayList<>();
    //The top candidates for which vehicle the passenger is onboard
    ArrayList<Vehicle> topCandidates = new ArrayList<>();




    @Override
    protected void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        addUIComponents();
        locationSaver = new LocationSaver(textUI, this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    final Integer passengerSnapshotId = passengerSnapshotIdCount++;
                    savePassengerLocation(location, passengerSnapshotId);
                    if (discardedCounter > DISCARD_INITIAL_LOCATIONS){
                        final double lat = location.getLatitude();
                        final double lng = location.getLongitude();
                        Log.e("****LOCATION:", "passenger is at (" + lat + ", " +  lng + ")");
                        Date dateUTC = new Date(location.getTime());
                        String date = dateUTC.toString(); //-> "Thu Mar 21 16:03:20 GMT+01:00 2019"
                        String time = date.substring(11, 19); //-> 16:03:20
                        pairings.add(new PassengerVehiclePairing(passengerSnapshotId, time, lat, lng));
                        Integer[] boundingBox = getBoundingBox(lat, lng);
                        Log.e("****Location:", "BB lower left: (" + boundingBox[2] + ", " +  boundingBox[0] + ")");
                        Log.e("****Location:", "BB upper right: (" + boundingBox[3] + ", " +  boundingBox[1] + ")");
                        fetchVehicles(passengerSnapshotId, boundingBox);
                    }
                    discardedCounter++;
                }
            }
        };
        createLocationRequest();
    }


    /*******************************Get Nearby Vehicles********************************************/

    void fetchVehicles(Integer passengerSnapshotId, Integer[] boundingBox){
        GetNearbyVehicles getNearbyVehiclesTask = new GetNearbyVehicles();
        //this to make the middleman/interface feed result of AsyncTask back to this class
        getNearbyVehiclesTask.middleman = this;
        getNearbyVehiclesTask.execute(passengerSnapshotId, boundingBox[0], boundingBox[1], boundingBox[2], boundingBox[3]);
    }

    // The vehicle API in getNearbyVehicles takes two longitudes and two latitudes WGS84*1000000,and returns all vehicles in the enclosed area.
    // Below is an algorithm for offsetting a WGS84 position some meters along latitude or longitude
    // https://stackoverflow.com/questions/2839533/adding-distance-to-a-gps-coordinate
    private Integer[] getBoundingBox(double lat, double lng){
        final double offset_meters = MAX_DISTANCE_VEHICLE_PASSENGER / 2;
        Integer[] boundingBox = {0,0,0,0};
        Double x1 = lng - (180/Math.PI) * (offset_meters/EARTH_RADIUS) / Math.cos(lat);
        Double x2 = lng + (180/Math.PI) * (offset_meters/EARTH_RADIUS) / Math.cos(lat);
        Double y1 = lat - (180/Math.PI) * (offset_meters/EARTH_RADIUS);
        Double y2 = lat + (180/Math.PI) * (offset_meters/EARTH_RADIUS);
        boundingBox[0] = (int) Math.round(x1*1000000);
        boundingBox[1] = (int) Math.round(x2*1000000);
        boundingBox[2] = (int) Math.round(y1*1000000);
        boundingBox[3] = (int) Math.round(y2*1000000);
        return boundingBox;
    }

    /************From received vehicles, determine which one the passenger is onboard**************/

    //Receives the result from AsyncTask class (GetNearbyVehicles), onPostExecute method.
    @Override
    public void onAPIResponse(ArrayList<Vehicle> vehicles){
        //TODO BRYT UT SOM EN EGEN METOD ATT PARA IHOP SNAPSHOTS OCH SKAPA KANDIDATLISTA PÅ SNAPEN

        int lastIndex = pairings.size()-1;
        for (int i = lastIndex; i >= 0; i--){
            PassengerVehiclePairing pairing = pairings.get(i);
            if (pairing.passengerSnapshotId.equals(vehicles.get(0).passengerSnapshotId)){
                //We have found the passenger snapshot from which the vehicle query originated
                boolean doneYet = pairing.setVehicles(vehicles);
                while (!doneYet){
                    //wait. (This in not the nicest way to control concurrency, but it's really simple)
                }
                if (pairing.candidates != null){
                    for (Vehicle newVehicle : pairing.candidates){
                        for (Vehicle recordedVehicle : topCandidates){
                            if (newVehicle.gid == recordedVehicle.gid){
                                recordedVehicle.points = recordedVehicle.points + newVehicle.points;
                                break;
                            }
                        }
                        topCandidates.add(newVehicle);
                    }
                }
                else{
                    //no vehicle candidates returned, nothing to add to toplist
                }
                break;
            }
        }

        locationSaver.printVehicles (vehicles);
        locationSaver.saveVehicles (vehicles);

        // TODO BTYT UT SOM EGEN METOD ATT MAINTAINA TOPLISTAN

        if (!topCandidates.isEmpty()){ //TODO, läs i pvp
            Collections.sort(topCandidates);
            Integer minNrIterations = 10;
            Integer iterationCouner = 1;
            if (iterationCouner++ <= minNrIterations){
                // sort out all vehicles that aren't likely candidates.
                if (topCandidates.get(0) != null){
                    Integer highestScore = topCandidates.get(0).points;
                    if (highestScore <= 15){ //Top candidate should have at least 15 points before it can be deemed to be superior
                        for (Vehicle v : topCandidates){
                            if (v.points < (0.75*highestScore)){
                                topCandidates.remove(v);
                            }
                        }
                    }
                }
            }

            String topList = "";
            for (Vehicle v : topCandidates){
                topList = topList + "( " + v.name + ", " + v.points +" )" ;

            }
            Log.e("**** TOP CANDIDATES **** ", "" + topList + "!! ");
        }
    }


    /***************************Subscribe to passenger Location************************************/

    protected void createLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(locationIntervall);
        locationRequest.setFastestInterval(locationIntervall);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        //detta fungerar, men för kommesriellt bruk...*
    }

    private void startLocationUpdates() {
        try {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null /* Looper */);
        }
        catch (SecurityException e){
            Log.e("**** security", "not connected to Google Play Services Client");
        }
    }

    public void savePassengerLocation(Location location, int passengerSnapshotIdCount){
        locationSaver.savePassengerLocation(location, passengerSnapshotIdCount);
        locationSaver.printPassengerLocation(location, passengerSnapshotIdCount);
        if (countSnapshots == maxNrOfSnapshots) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
            //you can also use: public LocationRequest setNumUpdates (int numUpdates)
        }
        countSnapshots ++;
    }

    /**************************************Android Activity ***************************************/

    protected void onResume() {
        super.onResume();
        //egentilgen borde du först kolla if (requestingLocationUpdates){... där rLU är en bool basetat på bundle savedinstancestate. android guide
        startLocationUpdates();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Stop location updates while paused?
        //fusedLocationClient.removeLocationUpdates(locationCallback);
        //Just nu prenumererar den inte på ny platse om appen är pausad ändå
    }

    /**************************************UI******************************************************/

    private void addUIComponents(){
        //gör en tabell istället för 36 textviews
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




// *
//detta fungerar.
//men för komersiellt bruk ska du egentligen kolla att användaren gett din app alla permissions osv.
//"Next check whether the current location settings are satisfied:"
//SettingsClient client =...
//Task<LocationSettingsResponse> task = ...
//task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
//task.addOnFailureListener(this, new OnFailureListener() {
//https://developer.android.com/training/location/change-location-settings.html
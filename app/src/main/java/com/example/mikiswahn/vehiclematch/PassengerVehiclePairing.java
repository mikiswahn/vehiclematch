package com.example.mikiswahn.vehiclematch;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


/** Class for a pairing of one passenger location with all vehicles in the vicinity of the passenger at that time*/

public class PassengerVehiclePairing {

    public Integer passengerSnapshotId;
    public String passengerTime;
    public double passengerLat;
    public double passengerLng;
    //double passengerBearing;
    public ArrayList<Vehicle> vehicles;
    public ArrayList<Vehicle> candidates = new ArrayList<>();


    PassengerVehiclePairing(Integer passengerSnapshotId, String time, double lat, double lng){
        this.passengerSnapshotId = passengerSnapshotId;
        passengerTime = time;
        passengerLat = lat;
        passengerLng = lng;
    }

    public ArrayList<Vehicle> sortOut(ArrayList<Vehicle> vehicles){
        return vehicles;
    }

    //before calling this method you must have checked that the id of <vehicles> is the same as this.id
    public boolean setVehicles(ArrayList<Vehicle> vehicles){
        this.vehicles = vehicles;
        boolean doneYet = computeTopCandidate();
        return true;
    }

    // keep track of vehicle gid. on main thread.
    public boolean computeTopCandidate(){
        //(bus vehicle length 20 m) + (low gps error 5 m)
        double HIGHEST_POINT_THRESHOLD = 30;
        //(tram vehicle length 30 m) + (low gps error 5 m)
        double NEXT_HIGHEST_POINT_THRESHOLD = 40;
        //(max vehicle length 30 m) + (high gps error passenger 8 m) + (high gps error vehicle 8 m)
        double LOWEST_POINT_THRESHOLD = 46;
        //right now i query a square with sides of 60*2, which is much larger than a radius of 46.
        //double LOWEST_POINT_THRESHOLD = 320; //For testing at lindholmen office

        if (vehicles.get(0).getName().equals("No vehicles nearby")){
            //don't add any vehicles, candidates.isEmpty() will be true
        }
        else {
            String vehicleTime = vehicles.get(0).snapshot.getTime();
            timeDelayOk(vehicleTime);
            for (Vehicle v : vehicles){
                double distance = computeDistance(v.getSnapshot().getLat(), v.getSnapshot().getLng(), v);
                if (distance <= HIGHEST_POINT_THRESHOLD){
                    v.setPoints(5);
                    candidates.add(v);
                }
                else if (distance <= NEXT_HIGHEST_POINT_THRESHOLD){
                    v.setPoints(4);
                    candidates.add(v);
                }
                else if (distance <= LOWEST_POINT_THRESHOLD){
                    v.setPoints(1);
                    candidates.add(v);
                }
                //else v gets no points and is not on top list
            }
            if (candidates != null ){
                //Collections.sort(candidates);
                for (Vehicle v : candidates) {
                    Log.e("****candidate", v.getName() + ", " + v.getPoints() + " points, " + v.getSnapshot().getDistanceToPassenger()+ "meters");
                }
            }
        }
        return true;
    }

    public double computeDistance(double vLat, double vLng, Vehicle vehicle){
        double EARTH_RADIUS = 6363000; //meters
        double distY = Math.abs(vLat-passengerLat)*(Math.PI/180)*EARTH_RADIUS;
        double distX = Math.abs(vLng-passengerLng)*(Math.PI/180)*EARTH_RADIUS*Math.cos(vLat);
        double distance = Math.sqrt(Math.pow(distY, 2) + Math.pow(distX, 2));
        vehicle.getSnapshot().setDistanceToPassenger(distance);
        return distance;
    }

    public boolean timeDelayOk(String vehicleTime){
        return true;
        /*
        Integer MAX_TIME_DELAY = 7;//in seconds
        Integer psec = Integer.parseInt(passengerTime.substring(6,8));
        Integer pmin = Integer.parseInt(passengerTime.substring(3,5));
        Integer phou = Integer.parseInt(passengerTime.substring(0,2));
        Integer vsec = Integer.parseInt(vehicleTime.substring(6,8));
        Integer vmin = Integer.parseInt(vehicleTime.substring(3,5));
        Integer vhou = Integer.parseInt(vehicleTime.substring(0,2));
        Log.e("****Timedlays", "(s,m,h) p:" + psec + ", " + pmin + ", " + phou + ", v: " + vsec + ", " + vmin + ", " + vhou);
        // Will not bother with this since empirical results show that time delay is never av issue
        // also the vehicle time is the sever time when query was made, and not when gps position was logged locally on vehicle.
        Integer timeDelay = 0;
        if (timeDelay < MAX_TIME_DELAY){
            return true;
        }
        */
    }


}

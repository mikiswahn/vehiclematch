package com.example.mikiswahn.vehiclematch;

import java.util.ArrayList;

public class PassengerVehiclePairing {

    Integer passengerSnapshotId;
    String passengerTime;
    double passengerLat;
    double passengerLng;
    double passengerBearing;
    ArrayList<Vehicle> vehicles;
    ArrayList<Vehicle> topCandidates;


    PassengerVehiclePairing(Integer passengerSnapshotId, String time, double lat, double lng){
        this.passengerSnapshotId = passengerSnapshotId;
        passengerTime = time;
        passengerLat = lat;
        passengerLng = lng;
    }

    //before calling this method you must have checked that the id of <vehicles> is the same as this.id
    public void setVehicles(ArrayList<Vehicle> vehicles){
        this.vehicles = vehicles;
        computeTopCandidate();
    }

    // keep track of vehicle gid.
    public void computeTopCandidate(){
        //Find the (top 3?) vehicle(s) that are most likely to be the one the passenger is onboard.
    }


}

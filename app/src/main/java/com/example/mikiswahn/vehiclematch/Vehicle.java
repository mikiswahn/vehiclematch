package com.example.mikiswahn.vehiclematch;

import android.util.Log;
import java.util.Comparator;
import java.util.Random;


/** Class for a vehicle at a particular time and location (snapshot). */

public class Vehicle implements Comparable<Vehicle> {
    //From which passenger snapshot this vehicle was queried
    public Integer passengerSnapshotId;
    // Service GID, typ en specifik körning, mellan ändhållplatser typ. Är alltså unik för linje, tid och riktning. ex. 9015014535800075
    public long gid;
    // The name of the line
    public String name;
    // A list of snapshots in time with location info
    public Snapshot snapshot;
    // The points this vehicle got in the iteration, used for the voting system for top candidates
    public Integer points;
    public String lColor;
    public String bColor;
    //String prodClass; //kan användas ihop med name för att lista ut längden på fordonet. användbart för gps.


    public Vehicle (Integer passengerSnapshotId, long gid, String name, double lat, double lng, String lColor, String bColor){
        this.passengerSnapshotId = passengerSnapshotId;
        this.gid = gid;
        this.name = name;
        snapshot = new Snapshot(lat, lng);
        this.lColor = lColor;
        this.bColor = bColor;
    }

    //make copy
    public Vehicle (Vehicle v){
        this.passengerSnapshotId = v.passengerSnapshotId;
        this.gid = v.gid;
        this.name = v.name;
        this.snapshot =  new Snapshot(v.snapshot);
        this.points = v.points;
        this.lColor = v.lColor;
        this.bColor = v.bColor;
    }

    //"empty vehicle" constructor
    public Vehicle (Integer passengerSnapshotId){
        this.passengerSnapshotId = passengerSnapshotId;
        this.name = "No vehicles nearby"; //This should be a string resource in the android project, but whatever
        //this.name ="@string/no_vehicles_nearby";
        this.gid = getFakeId();
        snapshot = new Snapshot();
        this.lColor = "#102d64";
        this.bColor = "#dcd135";
    }

    public void addTime(String time){
        if (snapshot != null){
          snapshot.setTime(time);
        }
        else{
            Log.e("*****SNAPSHOT","You can't set the time since the vehicle snapshot is null");
        }
    }

    public void setPoints(Integer points){
        this.points = points;
    }

    @Override
    public int compareTo(Vehicle v2) {
        if (this.points <= v2.points) {
            return -1;
        }
        else if (this.points.equals(v2.points)) {
            return 0;
        }
        else if (this.points >= v2.points) {
            return 1;
        }
        return 2;
    }

    public long getFakeId(){
        //To generate fake gids for empty vehicles, but so that they don't appear as top candidates due to frequency
        Random randomGenerator = new Random();
        long id = randomGenerator.nextLong();
        return id;
    }
}

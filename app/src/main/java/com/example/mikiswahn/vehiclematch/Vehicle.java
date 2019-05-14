package com.example.mikiswahn.vehiclematch;

import android.util.Log;
import java.util.Comparator;


/** Class for a vehicle at a particular time and location (snapshot). */

public class Vehicle implements Comparable<Vehicle> {
    //From which passenger snapshot this vehicle was queried
    Integer passengerSnapshotId;
    // Service GID, typ en specifik körning, mellan ändhållplatser typ. Är alltså unik för linje, tid och riktning. ex. 9015014535800075
    long gid;
    // The name of the line
    String name;
    // A list of snapshots in time with location info
    Snapshot snapshot;
    // The points this vehicle got in the iteration, used for the voting system for top candidates
    Integer points;
    // "lcolor": "string",
    //  "prodClass": "string", //kan användas ihop med name för att lista ut längden på fordonet. användbart för gps.
    //  "bcolor": "string",
    //  "delay": 0,
    //To generate fake gids for empty vehicles, but so sthat they don't appear as top candidates due to frequency
    public long fakeGidGenerator = 0;


    public Vehicle (Integer passengerSnapshotId, long gid, String name, double lat, double lng){
        this.passengerSnapshotId = passengerSnapshotId;
        this.gid = gid;
        this.name = name;
        snapshot = new Snapshot(lat, lng);
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

    //"empty vehicle" constructor
    public Vehicle (Integer passengerSnapshotId){
        this.passengerSnapshotId = passengerSnapshotId;
        this.name = "No vehicles nearby"; //This should be a string resource in the android project, but whatever
        this.gid = fakeGidGenerator++;
        snapshot = new Snapshot();

    }

}

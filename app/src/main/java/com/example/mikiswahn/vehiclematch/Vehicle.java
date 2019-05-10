package com.example.mikiswahn.vehiclematch;

import android.util.Log;
import java.util.ArrayList;


/* Class for a vehicle for a particular time and location. */

public class Vehicle {
    //From which passenger snapshot this vehicle was queried
    Integer passengerSnapshotId;
    // Service GID, typ en specifik körning, mellan ändhållplatser typ. Är alltså unik för linje, tid och riktning. ex. 9015014535800075
    long gid;
    // The name of the line
    String name;
    // A list of snapshots in time with location info
    Snapshot snapshot;
    // "lcolor": "string",
    //  "prodClass": "string", //kan användas ihop med name för att lista ut längden på fordonet. användbart för gps.
    //  "bcolor": "string",
    //  "delay": 0,


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
            Log.e("*****SNAPSHOT","Yo can't set the time since the vehicle snapshot is null");
        }
    }

    //"empty vehicle" constructor
    public Vehicle (Integer passengerSnapshotId){
        this.passengerSnapshotId = passengerSnapshotId;
        this.name = "No vehicles nearby"; //This should be a string resource in the android project, but whatever
        this.gid = 0;
        snapshot = new Snapshot();

    }


}

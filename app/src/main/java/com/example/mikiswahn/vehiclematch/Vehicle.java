package com.example.mikiswahn.vehiclematch;

import android.util.Log;
import java.util.ArrayList;


/* Class for a vehicle for a particular time and location. */

public class Vehicle {
    Integer passengerSnapshotId;
    // Service GID, typ en specifik körning, mellan ändhållplatser typ. Är alltså unik för linje, tid och riktning. ex. 9015014535800075
    long gid;
    // The name of the line
    String name;
    // A list of snapshots in time with location info
    Snapshot snapshot;
    // "lcolor": "string",
    //  "prodClass": "string", //kan användas ihop med name för att lista ut längden
    //  "bcolor": "string",
    //  "delay": 0,


    public Vehicle (Integer passengerSnapshotId, long gid, String name, double lat, double lng, int bearing){
        this.passengerSnapshotId = passengerSnapshotId;
        this.gid = gid;
        this.name = name;
        snapshot = new Snapshot(lat, lng, bearing);
    }

    public void addTime(String time){
        if (snapshot != null){
          snapshot.setTime(time);
        }
        else{
            Log.e("*****SNAPSHOT","This action cannot be performed since the vehicle has no snapshots");
        }
    }


}

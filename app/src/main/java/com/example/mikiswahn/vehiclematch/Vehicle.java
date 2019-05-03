package com.example.mikiswahn.vehiclematch;

import android.util.Log;
import java.util.ArrayList;


/* Class for a vehicle. */

public class Vehicle {
    // Service GID, typ en specifik körning mellan ändhållplatser. Är alltså unik för linje, tid och riktning. ex. 9015014535800075
    long gid;
    // The name of the line
    String name;
    // A list of snapshots in time with location info
    ArrayList<Snapshot> snapshots = new ArrayList<>();
    // "lcolor": "string",
    //  "prodClass": "string", //kan användas ihop med name för att lista ut längden
    //  "bcolor": "string",
    //  "delay": 0,


    public Vehicle (long gid, String name, double lat, double lng, int bearing){
        this.gid = gid;
        this.name = name;
        snapshots.add(new Snapshot(lat, lng, bearing));
    }

    public void addTime(String time){
        if (snapshots.size() == 1){
          snapshots.get(0).setTime(time);
        }
        else{
            Log.e("*****SNAPSHOT","This action cannot be performed since the vehicle has multiple snapshots, or none");
        }
    }


}

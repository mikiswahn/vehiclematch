package com.example.mikiswahn.vehiclematch;

import java.util.Date;

public class Vehicle {
    String time;
    //Date date; //time
    String name; //vehicles.name
    double lat; //vehicles.y
    double lng; //vehicles.x
    int bearing; //vehicles.direction
    //final float speedkmh; //-
    // "lcolor": "string",
    //  "prodClass": "string",
    //  "bcolor": "string",
    //  "gid": "string",
    //  "delay": 0,


    public Vehicle (String name, double lat, double lng, int bearing){
        //this.date = date;
        // The name of the line
        this.name = name;
        this.lat = lat;
        this.lng = lng;
        // Bearing is an integer compass angle from 0 to 31
        // N:8, E:16, S: 24, W:0 and 31 is W-ish
        this.bearing = bearing;
    }

    public void setTime(String time) {
        this.time = time;
    }

    /*
    public void setDate(Date date) {
        this.date = date;
    }
    */
}

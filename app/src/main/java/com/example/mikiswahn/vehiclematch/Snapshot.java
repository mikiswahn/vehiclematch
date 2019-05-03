package com.example.mikiswahn.vehiclematch;


/* Class for a snapshot in time, along with a location data */

public class Snapshot {
    private String time;
    private double lat;
    private double lng;
    // Bearing is an integer compass angle from 0 to 31
    // N:8, E:16, S: 24, W:0 and ish 31
    private int bearing;
    // The distance to the pasenger at tihis point in time.
    private double distanceToPassenger; //TODO


    public Snapshot (String time, double lat, double lng, int bearing){
        this.time = time;
        this.lat = lat;
        this.lng = lng;
        this.bearing = bearing;
    }

    public Snapshot (double lat, double lng, int bearing){
        this.lat = lat;
        this.lng = lng;
        this.bearing = bearing;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTime(){
        return time;
    }




    //passenger:
    public Snapshot (String time, double lat, double lng){
        this.time = time;
        this.lat = lat;
        this.lng = lng;
        this.bearing = -1; //exceptions, gött //TODO beräkna passagerares rikting utifrån föregående gps, typ plocka två punkter snabbt?
        //float speedkmh = speedms * 3.6f;
    }


}

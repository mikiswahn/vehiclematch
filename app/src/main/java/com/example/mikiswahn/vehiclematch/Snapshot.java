package com.example.mikiswahn.vehiclematch;


/** Class for a snapshot, which is a point in time and location data */

public class Snapshot {
    private String time;
    public double lat;
    public double lng;
    public double distanceToPassenger;


    public Snapshot (double lat, double lng){
        this.lat = lat;
        this.lng = lng;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setDistanceToPassenger(double distance) {
        this.distanceToPassenger = distance;
    }

    public String getTime(){ return time; }
    public double getLat(){ return lat; }
    public double getLng(){ return lng; }


    public Snapshot (){
        this.time = "";
        this.lat = 0;
        this.lng = 0;
        this.distanceToPassenger = 9000;
    }



    /*
    //passenger:
    public Snapshot (String time, double lat, double lng){
        this.time = time;
        this.lat = lat;
        this.lng = lng;
        this.bearing = -1; //exceptions //beräkna passagerares rikting utifrån föregående gps, typ plocka två punkter snabbt?
        //float speedkmh = speedms * 3.6f;
    }
    */


}

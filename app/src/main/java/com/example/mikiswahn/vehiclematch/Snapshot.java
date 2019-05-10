package com.example.mikiswahn.vehiclematch;


/* Class for a snapshot in time, along with a location data */

public class Snapshot {
    private String time;
    private double lat;
    private double lng;
    private double distanceToPassenger; //TODO


    public Snapshot (double lat, double lng){
        this.lat = lat;
        this.lng = lng;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTime(){
        return time;
    }


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

package com.example.mikiswahn.vehiclematch;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.Date;

//INTE PASSENGERS I DB UTAN SNAPSHOT I DB SÅ BYT NAMN HALLÅ DETTA BLEV KONSTIGT
//ELELR?
@Entity
public class Passenger {
    @NonNull
    @PrimaryKey //(autoGenerate = true)
    public int pid;

    //@ColumnInfo(name = "timestamp")
    //public Date timestamp;
    // error: Cannot figure out how to save this field(Date) into database. You can consider adding a type converter for it.

    @ColumnInfo(name = "longitude")
    public double lng;

    @ColumnInfo(name = "latitude")
    public double lat;

    @ColumnInfo(name = "velocity")
    public float velocity;

    @ColumnInfo(name = "bearing")
    public float bearing;

    public int getPid() { return pid; }

    public void setPid(int pid) { this.pid = pid; }

    //public Date getTime() { return timestamp; }

    //public void setTime(Date timestamp) { this.timestamp = timestamp; }

    public double getLng() { return lng; }

    public void setLng(int lng) { this.lng = lng; }

    public double getLat() { return lat; }

    public void setLat(int lat) { this.lat = lat; }

    public float getVelocity() { return velocity; }

    public void setVelocity(int velocity) { this.velocity = velocity; }

    public float getBearing() { return bearing; }

    public void setBearing(int bearing) { this.bearing = bearing; }

    // .setPid(); .setLng(); .setLat(); .setVelocity(); .setBearing();
}


//Room creates a table for each class annotated with @Entity;
//the fields in the class correspond to columns in the table.
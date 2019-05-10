package com.example.mikiswahn.vehiclematch;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import android.location.Location;
import android.media.MediaScannerConnection;
import android.util.Log;
import android.widget.TextView;
import android.os.Environment;
import android.content.Context;



/* Class for updating txt-file & UI with location data. */


public class LocationSaver {

    public ArrayList<TextView> textRows;
    private Integer MAX_ROW_INDEX = 34; //there are 35 text rows
    private Integer rowCursor = 1; //begins at one since index 0 is reserved:
    private TextView firstRow;
    private File filePassenger;
    private File fileVehicles;


    public LocationSaver(ArrayList<TextView> textRows, Context context) {
        this.textRows = textRows;
        firstRow = textRows.get(0);
        if (isExternalStorageWritable()) {
            getPublicAlbumStorageDir("vehiclematch", context);
        }
    }

    public void printVehicles(ArrayList<Vehicle> vehicles) {
        if (vehicles.get(0).name.equals("No Vehicles returned for this request")){
            // Nah, don't waste UI space with empty vehicle sets
        }
        else {
            String outprint = vehicleListPrettyPrint(vehicles);
            if (rowCursor <= MAX_ROW_INDEX) {
                TextView row = textRows.get(rowCursor);
                row.setText(outprint);
                rowCursor++;
            }
        }
    }

    public void printPassengerLocation(Location location, int passnpID) {
        String coordinate = coordinatePrettyPrint(location);
        String speed = speedPrettyPrint(location);
        String time = timePrettyPrint(location);
        String snapshot = ("Last: " + coordinate + " | " + speed + " km/h | " + time + " | " + passnpID);
        firstRow.setText(snapshot);
    }
    //Bundle extra = location.getExtras(); -> information in K/V-pairs, such as satellites - the number of satellites used to derive the fix
    //String provider = location.getProvider(); ->Returns the name of the provider that generated this fix (dvs.location fix). typ gps antar jag?

    public void saveVehicles(ArrayList<Vehicle> vehicles){
        String outprint = "";
        if (vehicles.get(0).name.equals("No vehicles nearby")){
            String passnpID = "" + vehicles.get(0).passengerSnapshotId;
            String time = vehicles.get(0).snapshot.getTime();
            outprint = ("No vehicles nearby" + " | " + time + " | " + passnpID + "\n");
        }
        else {
            outprint = vehicleListPrettyPrint(vehicles);
        }
        writeToFile(fileVehicles, outprint);
    }

    public void savePassengerLocation(Location location, int passnpID){
        String coordinate = coordinatePrettyPrint (location);
        String speed = speedPrettyPrint(location);
        String time = timePrettyPrint(location);
        String snapshot = (coordinate + " | " + speed + " | " + time + " | " + passnpID + "\n");
        writeToFile(filePassenger, snapshot);
    }

    public void writeToFile(File file, String text){
        if(isExternalStorageWritable()){
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
                writer.append(text);
                writer.close();
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("**** filesystem", "failed to write to file");
            }
        }
    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public void getPublicAlbumStorageDir(String albumName, Context context) {
        File pathExternalStorage = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        File path = new File(pathExternalStorage, albumName);
        path.mkdirs();
        MediaScannerConnection.scanFile(context, new String[] {path.toString()}, null, null);
        String fullFilenameP = fileNameWithDate(true);
        String fullFilenameV = fileNameWithDate(false);
        filePassenger = new File(path, fullFilenameP);
        fileVehicles = new File(path, fullFilenameV);
    }

    public String coordinatePrettyPrint (Location location){
        final double lat = location.getLatitude();
        final double lng = location.getLongitude();
        return "(" + lat + ", " + lng + ")";
    }

    public String speedPrettyPrint (Location location){
        float speedms = location.getSpeed();
        final float speedkmh = speedms * 3.6f;
        String speed = Float.toString(speedkmh);
        if (speed.length() > 5 ){
            speed = (speed.substring(0,5));
        }
        return speed;
    }

    public String timePrettyPrint(Location location){
        long timestamp = location.getTime();
        Date date = new Date(timestamp);
        String longDate = date.toString(); // -> "Thu Mar 21 16:03:20 GMT+01:00 2019"
        return longDate.substring(11, 19);
    }


    public String vehicleListPrettyPrint(ArrayList<Vehicle> vehicles) {
        String passnpID = "" + vehicles.get(0).passengerSnapshotId;
        String time = vehicles.get(0).snapshot.getTime();
        String vehicleNames = "";
        for (Vehicle v : vehicles) {
            vehicleNames = vehicleNames + v.name + ", ";
        }
        String outprint = vehicleNames + "| " + time + " | " + passnpID;
        return outprint;
    }

    public String fileNameWithDate(Boolean isSnapshot){
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("Europe/Stockholm"));
        Date dateO = cal.getTime();
        String dateS = dateO.toString(); //-> "Thu Mar 21 16:03:20 GMT+01:00 2019"
        String date = dateS.substring(0, 13) + dateS.substring(14, 16); //filename mus be without ':'
        if (isSnapshot){
            return date + " passenger" + ".txt";
        }
        else{
            return date + " vehicles" + ".txt";
        }
    }


}
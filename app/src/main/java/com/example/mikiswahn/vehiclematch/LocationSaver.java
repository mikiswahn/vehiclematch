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



/** Class for updating txt-file & UI with location data. */

//TODO: rename to UI/DB/View since it doesn't just save locations
public class LocationSaver {

    public ArrayList<TextView> textRows;
    private TextView firstRow;
    private Integer vehicleRowCursor = 1; //begins at one since firstRow is reserved
    private Integer V_MAX_ROW_INDEX = 7;
    private Integer candidateRowCursor = 8;
    private Integer C_MAX_ROW_INDEX = 34; //there are 35 text rows
    private File filePassenger;
    private File fileVehicles;
    private File fileCandidates;


    public LocationSaver(ArrayList<TextView> textRows, Context context) {
        this.textRows = textRows;
        firstRow = textRows.get(0);
        if (isExternalStorageWritable()) {
            getPublicAlbumStorageDir("vehiclematch", context);
        }
    }

    /****************************PRINT ON SCREEN***************************************************/

    public void printVehicles(ArrayList<Vehicle> vehicles) {
        if (vehicles.get(0).name.equals("No vehicles nearby")){
            //don't waste UI space with empty vehicle sets
        }
        else {
            String outprint = vehicleListPrettyPrint(vehicles);
            if (vehicleRowCursor <= V_MAX_ROW_INDEX) {
                TextView row = textRows.get(vehicleRowCursor);
                row.setText(outprint);
                vehicleRowCursor++;
                if (vehicleRowCursor == V_MAX_ROW_INDEX+1){
                    vehicleRowCursor=1; //modulo 7 is not useful since these rows begin indexing at 1 not 0
                }
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


    public void printTopCandidates (ArrayList<Vehicle> topCandidates){
        String outprint = TopCandidatesPrettyPrint(topCandidates);
        if (candidateRowCursor <= C_MAX_ROW_INDEX) {
            TextView row = textRows.get(candidateRowCursor);
            row.setText(outprint);
            candidateRowCursor++;
        }
    }


    /****************************SAVE TO LOCAL STORAGE*********************************************/

    public void saveVehicles(ArrayList<Vehicle> vehicles){
        String outprint = "";
        if (vehicles.get(0).name.equals("No vehicles nearby")){
            String passnpID = "" + vehicles.get(0).passengerSnapshotId;
            String time = vehicles.get(0).snapshot.getTime();
            outprint = ( time + " | " + passnpID + " | " + "No vehicles nearby" + "\n" );
        }
        else {
            outprint = vehicleListPrettyPrint(vehicles) + "\n";
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

    public void saveTopCandidates (ArrayList<Vehicle> topCandidates){
        String outprint = TopCandidatesPrettyPrint(topCandidates) + "\n";
        writeToFile(fileCandidates, outprint);
        Log.e("**** TOP CANDIDATES **** ", outprint + "!! ");
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


    /****************************HELPER************************************************************/

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
        int indexOfComma = vehicleNames.length() - 2 ;
        vehicleNames = vehicleNames.substring(0, indexOfComma);
        return ( time + " | " + passnpID + " | " + vehicleNames );
    }

    public String TopCandidatesPrettyPrint(ArrayList<Vehicle> topCandidates){
        String topList = "";
        for (Vehicle v : topCandidates){
            topList = topList + "(" + v.name + ", " + v.points +"p.)";
        }
        return topList;
    }

    public void getPublicAlbumStorageDir(String albumName, Context context) {
        File pathExternalStorage = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        File path = new File(pathExternalStorage, albumName);
        path.mkdirs();
        MediaScannerConnection.scanFile(context, new String[] {path.toString()}, null, null);
        String fullFilenamePass = fileNameWithDate(" passenger");
        String fullFilenameVeh = fileNameWithDate(" vehicles");
        String fullFilenameCand = fileNameWithDate(" candidates");
        filePassenger = new File(path, fullFilenamePass);
        fileVehicles = new File(path, fullFilenameVeh);
        fileCandidates = new File(path, fullFilenameCand);
    }

    public String fileNameWithDate(String name){
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("Europe/Stockholm"));
        Date dateO = cal.getTime();
        String dateS = dateO.toString(); //-> "Thu Mar 21 16:03:20 GMT+01:00 2019"
        String date = dateS.substring(0, 13) + dateS.substring(14, 16); //filename mus be without ':'
        return date + name + ".txt";
    }


}
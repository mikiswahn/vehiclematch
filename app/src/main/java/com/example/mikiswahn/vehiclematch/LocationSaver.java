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



/* Class for updating UI/Database/File with location data. */

public class LocationSaver {

    public ArrayList<TextView> textUI;
    private File file;

    private Integer vecicleRow = 20;

    public LocationSaver(ArrayList<TextView> textUI, Context context){
        this.textUI = textUI;
        if(isExternalStorageWritable()){
            getPublicAlbumStorageDir("vehiclematch", context);
        }
    }

    public void printVehicles (String vehicleNames){
        int nrOfTextRowsVeh = 36;
        if (vecicleRow < nrOfTextRowsVeh) {
            TextView text = textUI.get(vecicleRow-1);
            text.setText(vehicleNames);
            vecicleRow++;
        }
    }

    //Bundle extra = location.getExtras();
    //information in K/V-pairs, such as satellites - the number of satellites used to derive the fix
    //String provider = location.getProvider();
    //Returns the name of the provider that generated this fix (dvs.location fix). typ gps antar jag?
    public void printPassengerLocation(Location location, int count){
        int nrOfTextRows = 20;
        String coordinate = coordinatePrettyPrint (location);
        String speed = speedPrettyPrint(location);
        final float bearing = location.getBearing();
        String time = timePrettyPrint(location);

        String snapshot = (coordinate + " | " + speed + " km/h | " + Float.toString(bearing) + " | " + time);
        if (count < nrOfTextRows) {
            TextView text = textUI.get(count-1);
            text.setText(snapshot);
        }
    }

    //TODO:Skapa passenger objekt ?

    public void savePassengerLocation(Location location){
        String coordinate = coordinatePrettyPrint (location);
        String speed = speedPrettyPrint(location);
        final float bearing = location.getBearing();
        String time = timePrettyPrint(location);
        String snapshot = (coordinate + " | " + speed + " | " + Float.toString(bearing) + " | " + time + "\n");

        if(isExternalStorageWritable()){
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
                writer.append(snapshot);
                writer.close();
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("**** filesystem", "failed to write to file");
            }
        }
        //eller Skriv till databas: *
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
        String fullFilename = fileNameWithDate();
        file = new File(path, fullFilename);
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
        String longDate = date.toString();
        return longDate.substring(11, 19);
    }
    //date.toString() -> "Thu Mar 21 16:03:20 GMT+01:00 2019"

    public String fileNameWithDate(){
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("Europe/Stockholm"));
        Date dateO = cal.getTime();
        String dateS = dateO.toString();
        String date = dateS.substring(0, 13) + dateS.substring(14, 16); //filename without ':'
        return "snapshots_" + date + ".txt";
    }


}










        /* Skriv till databas:
        // lägg till: import static com.example.mikiswahn.vehiclematch.AppDatabase.getAppDatabase;
        Context context = LocationActivity.this;
        final AppDatabase passengerDb = getAppDatabase(context);

        //Always use a Thread or AsyncTask for db calls, or the app will crash
        new Thread(new Runnable() {
            @Override
            public void run() {
                Passenger passenger = new Passenger();
                passenger.setPid(0); //från 0 till 120 st typ, så har man 2 min data
                passenger.setLng(lat);
                passenger.setLat(lng);
                passenger.setVelocity(speedkmh);
                passenger.setBearing(bearing);
                passengerDb.passengerDao().insert(passenger);
                //testa metoden List<Passenger> getAll(); från passengerdao för att posta data från db
            }
        }) .start();
        // läs mer på
        // https://developer.android.com/training/data-storage/room
        // https://codelabs.developers.google.com/codelabs/android-room-with-a-view/#11
        // https://medium.com/androiddevelopers/7-steps-to-room-27a5fe5f99b2
        // https://medium.freecodecamp.org/room-sqlite-beginner-tutorial-2e725e47bfab
        */
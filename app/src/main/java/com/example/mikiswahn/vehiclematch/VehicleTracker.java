package com.example.mikiswahn.vehiclematch;

import android.os.AsyncTask;
import android.util.JsonReader;
import android.util.Log;
import java.util.ArrayList;
import java.util.Base64;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;



/* Class for collecting location data from vehicles. */

public class VehicleTracker {

    private String apiKey = "gUcw2uf9fQJT9JMyyf43gWpyl9Ma";
    private String apiSecret = "Ay6or6sJCgDod_D102Sjio_Pt5ca";
    //client-/device id, Bara om appen distribueras behöver användarna unika scopes
    public final String id = "device_1";
    private String tokenType = "Bearer";
    private String accessToken;

    //enclose Brunnssparken
    //longitude
    int x1 = 11966699;
    int x2 = 11971377;
    //latitude
    int y1 = 57706108;
    int y2 = 57709250;

    //The radius in meters around a passenger where their vehicle is assumed to be within
    int raduis = 50; //= variable D in alg.
    //which translates to the following addition to a GPS coordinate in WGS84 * 1000000
    int gpsRadius;


    public VehicleTracker(){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                String token = generateToken();
                Log.e("***** AccessToken is", token);
                accessToken = token;
            }
        });
    }


        // TODO : JÄTTEVIKGIT ATT KOLLA ATT TOKEN GILITG!!!!!!!!!!!! CHANSA ITE PÅ EXEKVERINGSTID !!!!
        // TODO 1. getNearbyVehicles ,1.1 kolla om tokn finns, annars generera,



    public String generateToken (){
        String token = "";
        HttpsURLConnection connection = null;
        try {
            URL url = new URL("https://api.vasttrafik.se/token");
            connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            byte[] myData = (apiKey + ":" + apiSecret).getBytes("utf-8");
            String keySecret64 = "Basic " + Base64.getEncoder().encodeToString(myData);
            connection.setRequestProperty ("Authorization", keySecret64); //header
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("grant_type", "client_credentials");
            connection.setRequestProperty("scope", id);
            connection.setDoOutput(true);

            DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
            outputStream.writeBytes("grant_type=client_credentials&scope=device_1");
            outputStream.flush();
            outputStream.close();
            if (connection.getResponseCode() == 200 ){
                InputStream responseBody = connection.getInputStream();
                InputStreamReader responseBodyReader = new InputStreamReader(responseBody, "UTF-8");
                JsonReader jsonReader = new JsonReader(responseBodyReader);
                jsonReader.beginObject();
                while (jsonReader.hasNext()) {
                    String key = jsonReader.nextName();
                    if (key.equals("token_type")) {
                        String value = jsonReader.nextString();
                        tokenType = value;
                    } else if (key.equals("expires_in")) {
                        String value = jsonReader.nextString();
                        //TODO save expirty time, check time and use expiresin to get time
                    } else if (key.equals("access_token")) {
                        String value = jsonReader.nextString();
                        token = value;
                    } else {
                        String value = jsonReader.nextString();
                    }
                }
                jsonReader.close();
            } else{
                Log.e("****ERROR code", connection.getResponseMessage());
                //401 - Unauthorized, Token has been denied
                //403 - Forbidden, Missing or invalid request parameters
            }
            connection.disconnect();
        }catch (Exception e) {
            Log.e("****HTTPS","The https url connection failed. protocol- or IO exception");
        }
        return token;
    }


    //TODO ADD BEARING AND TIME (ETC?) TO COMPARE WITH, FROM PASSENGER
    //@param: Longitudes (x) and latitudes (y) to query vehicles within bounding box
    //minx = left border, maxx = right border, miny = lower border, maxy = upper boarder, all in WGS84*1000000
    public void getNearbyVehicles (final int minx, final int maxx, final int miny, final int maxy){
        //TODO you should check that the token hasn't expired and if so call generateToken()

        Log.e("**** fetching vehicles inside:" , minx + " " + maxx + " " + miny + " " + maxy);
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                HttpsURLConnection connection = null;
                try {
                    String endpoint = "https://api.vasttrafik.se/bin/rest.exe/v2/livemap";
                    String query = "?minx=" + minx + "&maxx=" + maxx + "&miny=" + miny + "&maxy=" + maxy + "&onlyRealtime=yes";
                    //onlyRealtime=yes means only positions based on realtime data, no would include positions from planned timetable.
                    URL url = new URL(endpoint + query);
                    connection = (HttpsURLConnection) url.openConnection();
                    connection.setRequestProperty("Authorization", tokenType + " " + accessToken);
                    connection.setRequestProperty("Accept-Charset", "UTF-8");

                    if (connection.getResponseCode() == 200 ){
                        InputStream responseBody = connection.getInputStream();
                        InputStreamReader responseBodyReader = new InputStreamReader(responseBody, StandardCharsets.UTF_8);
                        JsonReader jsonReader = new JsonReader(responseBodyReader);
                        ArrayList<Vehicle> vehicles = parseVehicles(jsonReader);
                        for (Vehicle v : vehicles) {
                            Log.e("****VEHICLE", "\n"+ v.name +", "+ v.gid + "\n" ); //("+ v.lng +", "+ v.lat +"), "+ v.bearing +", "+ v.time);
                        }
                        //TODO Return the list of vehicles to add to the toplist
                        jsonReader.close();
                    } else{
                        Log.e("****Failed to connect", "" );
                        Log.e("****ERROR code",connection.getResponseCode() + "");
                    }
                    connection.disconnect();
                }
                catch (Exception e) {
                    Log.e("****HTTPS ERROR","The https url connection failed for some unexpected reason. ");
                    //(ProtocolException, MalformedURLException, UnsupportedEncodingException IllegalStateException or IOException)...
                    Log.e("****HTTPS ERROR", e.toString());
                }
            }
        }); //assync task!
    }

    public ArrayList<Vehicle> parseVehicles (JsonReader jsonReader){
        ArrayList<Vehicle> vehicles = new ArrayList<>();
        String time = null;
        try {
            jsonReader.beginObject();
            while(jsonReader.hasNext()){
                String nameMap = jsonReader.nextName();
                if (nameMap.equals("livemap")){
                    jsonReader.beginObject();
                    while(jsonReader.hasNext()){
                        String nameVeh = jsonReader.nextName();
                        if (nameVeh.equals("vehicles")){
                            jsonReader.beginArray();
                            Log.e("****", "parsing vehicles, if any");
                            while (jsonReader.hasNext()) {
                                jsonReader.beginObject();
                                vehicles.add(readVehicle(jsonReader));
                                jsonReader.endObject();
                            }
                            jsonReader.endArray();
                        }else if (nameVeh.equals("time")) {
                            time = jsonReader.nextString();
                        } else {
                            jsonReader.skipValue();
                        }
                    }
                    jsonReader.endObject();
                }
                else {
                    Log.e("****API ERROR","JSON object not a livemap object");
                }

            }
            jsonReader.endObject();
        } catch (IOException e) {
            Log.e("****JSON ERROR", "IO Exception");
        } catch (Exception e) {
            Log.e("****JSON ERROR", "unknown outer");
            Log.e("****JSON ERROR", e.toString());
        }
        for (Vehicle v : vehicles) {
            v.addTime(time);
        }
        return vehicles;
        //* If a value may be null, you should first check using peek(). Null literals can be consumed using either nextNull() or skipValue().
        //ex. else if (name.equals("geo") && reader.peek() != JsonToken.NULL) {
        //    geo = readDoublesArray(reader);
    }


    public Vehicle readVehicle (JsonReader reader) {
        long gid = -1;
        String vehicleName = null; //vehicles.name
        double lat = -1; //vehicles.y
        double lng = -1; //vehicles.x
        int bearing = -1; //vehicles.direction
        try {
            while(reader.hasNext()){
                String name = reader.nextName();
                if (name.equals("x")) {
                    String x = reader.nextString();
                    lng = Double.parseDouble(x.substring(0,2) + "." + x.substring(2));
                } else if (name.equals("y")) {
                    String y = reader.nextString();
                    lat = Double.parseDouble(y.substring(0,2) + "." + y.substring(2));
                } else if (name.equals ("name")){
                    vehicleName  = reader.nextString();
                } else if (name.equals ("gid")){
                    gid  = Long.parseLong(reader.nextString());
                } else if (name.equals ("direction")){
                    bearing  = Integer.parseInt(reader.nextString());
                } else {
                    reader.skipValue();
                }
            }
        } catch (IOException e) {
            Log.e("****JSON ERROR", "unknown inner");
            Log.e("****JSON ERROR", e.toString());
        }
        Vehicle vehicle= new Vehicle (gid, vehicleName, lat, lng, bearing);
        return vehicle;
    }



}

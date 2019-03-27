package com.example.mikiswahn.vehiclematch;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.widget.TextView;
import android.os.AsyncTask;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import javax.net.ssl.HttpsURLConnection;
import java.net.URL;
import java.util.Base64;




/* Class for collecting location data from vehicles. */

public class VehicleActivity extends FragmentActivity{

    private TextView t1;
    private TextView t2;
    private TextView t3;
    private TextView t4;
    private TextView t5;

    private String apiKey = "gUcw2uf9fQJT9JMyyf43gWpyl9Ma";
    private String apiSecret = "Ay6or6sJCgDod_D102Sjio_Pt5ca";
    public final String id = "device_1"; //client-/device id
    private String scope = "device_1"; //Bara om appen distribueras behöver användarna unika scopes
    private String tokenType = "Bearer";
    private String accessToken;

    double lng = 57.7042458;
    double lat = 11.9636235;
    //x determined by longitude
    //y determined by latitude
    // Remember to convert to api format, ie. lng * 1000000 ish
    double maxx;
    double minx;
    double maxy;
    double miny;


    @Override
    protected void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle);
        t1 = findViewById(R.id.textView01);
        t2 = findViewById(R.id.textView02);
        t3 = findViewById(R.id.textView03);
        t4 = findViewById(R.id.textView04);
        t5 = findViewById(R.id.textView05);

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                String token = generateToken();
                //TODO WAIT FOR ASSYNK TAST TO COMPLETE, THE NPROCEED:
                Log.e("***** AccessToken is", token);
                accessToken = token;
                useToken ();
            }
        });
    }


    public String generateToken (){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                HttpsURLConnection connection = null;
                try {
                    URL url = new URL("https://api.vasttrafik.se/token");
                    //perhaps HTTP instead since API specified "HTTP/1.1"?
                    connection = (HttpsURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    byte[] myData = (apiKey + ":" + apiSecret).getBytes("utf-8");
                    String keySecret64 = "Basic " + Base64.getEncoder().encodeToString(myData);
                    connection.setRequestProperty ("Authorization", keySecret64);
                    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    connection.setRequestProperty("grant_type", "client_credentials"); //ta bort
                    connection.setRequestProperty("scope", id); //ta bort
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
                            if (key.equals("scope")) {
                                String value = jsonReader.nextString();
                                Log.e("RESPONSE_scope:", value);
                            } else if (key.equals("token_type")) {
                                String value = jsonReader.nextString();
                                Log.e("RESPONSE_token_type:", value);
                            } else if (key.equals("expires_in")) {
                                String value = jsonReader.nextString();
                                Log.e("RESPONSE_expires_in:", value);
                            } else if (key.equals("refresh_token")) {
                                String value = jsonReader.nextString();
                                Log.e("RESPONSE_refresh_token:", value);
                            } else if (key.equals("access_token")) {
                                String value = jsonReader.nextString();
                                Log.e("RESPONSE_access_token:", value);
                                Log.e("RESPONSE_access_token*", "bbf2d518-8397-3d99-95e2-7d9dc234c4da");
                            } else {
                                String value = jsonReader.nextString();
                                Log.e("RESPONSE_SURPRISE KEY:", value);
                            }
                        }
                        jsonReader.close();
                    } else{
                        Log.e("****ERROR code", connection.getResponseMessage());
                        //401 - unauthorized, token denied
                        //403 - Forbidden
                    }
                    connection.disconnect();
                }catch (Exception e) {
                    Log.e("****HTTPS","The https url connection failed. protocol- or IO exception");
                }
            }
        }); //assync task!
        //TODO WAIT FOR ASSYNK TASK (OR THREAD) TO FINISH AND THEN RETURN REAL ACCESS TOEKN
        return "bbf2d518-8397-3d99-95e2-7d9dc234c4da";

    }



    public void useToken (){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                HttpsURLConnection connection = null;
                try {
                    URL url = new URL("https://api.vasttrafik.se/bin/rest.exe/v2/location.name?input=ols&format=json");
                    //perhaps HTTP instead since API specified "HTTP/1.1"?
                    connection = (HttpsURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setRequestProperty ("Authorization", tokenType + " " + accessToken);
                    connection.setDoOutput(true);

                    DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
                    outputStream.writeBytes("input=ols&format=json");
                    outputStream.flush();
                    outputStream.close();
                    if (connection.getResponseCode() == 200 ){
                        InputStream responseBody = connection.getInputStream();
                        InputStreamReader responseBodyReader = new InputStreamReader(responseBody, "UTF-8");
                        JsonReader jsonReader = new JsonReader(responseBodyReader);
                        jsonReader.beginObject();
                        while (jsonReader.hasNext()) {
                            Log.e("_________", jsonReader.nextString());
                        }
                    } else{
                        Log.e("****ERROR code", connection.getResponseMessage());
                    }
                    connection.disconnect();


                }catch (Exception e) {
                    Log.e("****HTTPS","The https url connection failed. protocol- or IO exception");
                }
            }
        }); //assync task!








        /*
        URL url = new URL("https://api.vasttrafik.se/bin/rest.exe/v2" + key);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

        connection.addRequestProperty("client_id", id);
        connection.addRequestProperty("client_secret", secret);
        connection.setRequestProperty("Authorization", "OAuth " + token);

        //If the request returns an HTTP error code of 401, then your token has been denied
        */
    }




    public void makeRequest (){
        int minx = 11955000;
        int maxx = 11954000;
        int miny = 57721000;
        int maxy = 57720000;
        // WGS84 * 1000000 är typ x = 11954385, y=57720743
        String onlyRealtime = "no";
        //"yes" or "no" returns either positions based on realtime data only or positions from planned timetable data as well.
        String cgiBinPath ="api.vasttrafik.se/bin/rest.exe/v2"; //?
        try{
            URL url = new URL("https://" + cgiBinPath +
                    "/help.exe/eny?tpl=livemap&L=vs_livemap&minx=" + minx + "&maxx=" + maxx +
                    "&miny=" + miny + "&maxy=" + maxy + "&onlyRealtime=" + onlyRealtime );
        }catch (Exception e){
            Log.e("****","):");
        }
    }
}

package com.example.mikiswahn.vehiclematch;


import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.os.AsyncTask;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;
//import java.util.Base64.Encoder;




/* Class for collecting location data from vehicles. */

public class VehicleActivity extends FragmentActivity{

    private TextView t1;
    private TextView t2;
    private TextView t3;
    private TextView t4;
    private TextView t5;

    private String apiKey = "gUcw2uf9fQJT9JMyyf43gWpyl9Ma";
    private String apiSecret = "Ay6or6sJCgDod_D102Sjio_Pt5ca";
    public String id = "device_1"; //client_or_device_id
    private String scope = "device_1"; //Bara om appen distribueras behöver användarna unika scopes
    //encode w. base64 (google)
    //generate token with vasttrafik oauth2 guide step 1
    //elelr är detta vasttraffik access ens samma sak som i guden på tutsplus??

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
                String accessToken = generateToken (apiKey, apiSecret, id);
                useToken (apiKey, apiSecret, accessToken);
            }
        });
    }


    public String generateToken (String key, String secret, String id){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                HttpsURLConnection connection;
                try {
                    URL url = new URL("https://api.vasttrafik.se/token");
                    connection = (HttpsURLConnection) url.openConnection();


                    connection.setRequestMethod("POST");
                    connection.setDoOutput(true);
                    //base64 encode:
                    String myData = "message= apiKey ++ apiSecret encoded";
                    //or ?:
                    //connection.setRequestProperty(apiKey,apiSecret); //??
                    connection.getOutputStream().write(myData.getBytes());

                    /*
                     * POST https://api.vasttrafik.se/token HTTP/1.1
                     * Content-Type: application/x-www-form-urlencoded
                     * Authorization: Basic <nyckel:hemlighet_som_base64_encodats> //auth code
                     * grant_type=client_credentials&scope=<device_1>
                     * */


                }catch (Exception e) {
                    Log.e("****HTTPS","The https url connection failed. protocol- or IO exception");
                }
            }
        });
        return "not implemented";
    }


    public void useToken (String key, String secret, String token){
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

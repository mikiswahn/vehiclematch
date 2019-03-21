package com.example.mikiswahn.vehiclematch;


import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.os.AsyncTask;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;
//import java.util.Base64.Encoder;




/* Class for collecting location data from vehicles. */

public class VehicleActivity extends FragmentActivity{

    TextView t1;
    TextView t2;
    TextView t3;
    TextView t4;
    TextView t5;

    String apiKey = "gUcw2uf9fQJT9JMyyf43gWpyl9Ma";
    String apiSecret = "Ay6or6sJCgDod_D102Sjio_Pt5ca";
    String id = "client_or_device_id";
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
        /*
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                String accessToken = generateToken (apiKey, apiSecret, id);
                useToken (apiKey, apiSecret, accessToken);
            }
        });
        */
    }

    //
    // Har kört fast här. förstår inte hur jag ska kunna föra API requests till västtraffik från min app.
    //
    // kod ska ej kunna kompilera eller köras atm.
    //

    public String generateToken (String key, String secret, String id){
        /*
        URL url = new URL("https://api.vasttrafik.se/bin/rest.exe/v2");
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        //base64 encode
        String myData = "message= apiKey ++ apiSecret encoded";
        connection.getOutputStream().write(myData.getBytes());

        /*
        * POST https://api.vasttrafik.se/token HTTP/1.1
        * Content-Type: application/x-www-form-urlencoded
        * Authorization: Basic <nyckel:hemlighet_som_base64_encodats>
        * grant_type=client_credentials&scope=<device_id_unik_för_användare_enstring_typ_device_123>
        * */
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
}

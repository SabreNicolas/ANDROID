package com.example.mylife;

import android.app.Application;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class GlobalState extends Application {

    private static final String CAT = "IME4";
    public SharedPreferences settings;
    private final String URL = "http://10.0.2.2:8888/API_ANDROID/";

    @Override
    public void onCreate() {
        super.onCreate();
        settings = PreferenceManager.getDefaultSharedPreferences(this);


    }

    public static String jsonToPrettyFormat(String jsonString) {
        JSONObject json = null;
        try {
            json = new JSONObject(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Gson gson = new GsonBuilder()
                .serializeNulls()
                .disableHtmlEscaping()
                .setPrettyPrinting()
                .create();

        return gson.toJson(json);
    }



    private String convertStreamToString(InputStream in) throws IOException {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            return sb.toString();
        }finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void alerter(String s) {

        Log.i(CAT,s);
        Toast t = Toast.makeText(this,s,Toast.LENGTH_LONG);
        t.show();
    }

    public boolean verifReseau()
    {
        // On vérifie si le réseau est disponible,
        // si oui on change le statut du bouton de connexion
        ConnectivityManager cnMngr = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo netInfo;
        netInfo = cnMngr.getActiveNetworkInfo();

        String sType = "Aucun réseau détecté";
        Boolean bStatut = false;
        if (netInfo != null)
        {

            NetworkInfo.State netState = netInfo.getState();

            if (netState.compareTo(NetworkInfo.State.CONNECTED) == 0)
            {
                bStatut = true;
                int netType= netInfo.getType();
                switch (netType)
                {
                    case ConnectivityManager.TYPE_MOBILE :
                        sType = "Réseau mobile détecté"; break;
                    case ConnectivityManager.TYPE_WIFI :
                        sType = "Réseau wifi détecté"; break;
                }

            }
        }

        this.alerter(sType);
        return bStatut;
    }

    public String requete(String qs) {
        if (qs != null)
        {
            String urlData = settings.getString("urlData","http://10.0.2.2:8888/API_ANDROID/");

            try {
                URL url = new URL(urlData + qs);
                Log.i(CAT,"url utilisée : " + url.toString());
                HttpURLConnection urlConnection = null;
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = null;
                in = new BufferedInputStream(urlConnection.getInputStream());
                String txtReponse = convertStreamToString(in);
                urlConnection.disconnect();
                return txtReponse;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        return "";
    }

    public String sendGet(String requete) throws Exception {
        if (requete != null) {

            String urlData = URL + requete;

            URL obj = new URL(urlData);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            //optional default is GET
            con.setRequestMethod("GET");

            //add request header
            con.setRequestProperty("connexion", requete);

            int responseCode = con.getResponseCode();
            System.out.println("\nSending 'GET' request to URL : " + urlData);
            System.out.println("Response Code : " + responseCode);

            InputStream in = null;
            in = new BufferedInputStream(con.getInputStream());
            String txtReponse = convertStreamToString(in);
            con.disconnect();
            System.out.println(txtReponse);
            return txtReponse;

        }
        return "";

    }
}
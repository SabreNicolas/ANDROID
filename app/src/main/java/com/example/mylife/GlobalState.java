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
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import classBDD.Espace;
import classBDD.Indicateur;
import classBDD.User;

public class GlobalState extends Application {

    private static final String CAT = "IME4";
    public SharedPreferences settings;
    //private final String URL = "http://10.0.2.2:8888/API_ANDROID/";
    //pc worldline
    private final String URL = "http://10.0.2.2/API_ANDROID/";
    private User user = null;
    private Espace espace = null ;
    private Indicateur indicateur = null;

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


    public String requete(String requete, String type, JSONObject reqBody) throws Exception {
        if (requete != null) {
            URL obj = new URL(URL + requete);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            //add reuqest header
            con.setRequestMethod(type);
            con.setRequestProperty("requete", requete);
            //con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

            if(type.equals("POST") || type.equals("DELETE") || type.equals("PUT")){
                con.setDoOutput(true);
            }

            //PUT request
            if (reqBody != null){
                OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
                wr.write(reqBody.toString());
                wr.flush();
            }

            int responseCode = con.getResponseCode();
            System.out.println("\nSending '"+type+"' request to URL : " + obj);
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

    public void deleteEspace(){
        this.espace = null;
    }

    public void deleteIndicateur(){
        this.indicateur = null;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Espace getEspace() {
        return espace;
    }

    public void setEspace(Espace espace) {
        this.espace = espace;
    }

    public Indicateur getIndicateur() {
        return indicateur;
    }

    public void setIndicateur(Indicateur indicateur) {
        this.indicateur = indicateur;
    }
}
package com.example.mylife;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import classBDD.User;

import static java.lang.System.exit;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String CAT = "IME";
    private EditText editTextLogin;
    private EditText editTextPasswd;
    private Button btnConnexion;
    private Button btnInscription;
    private boolean isCo = false;
    private final String URL = "http://10.0.2.2:8888/API_ANDROID/";
    private String nom;
    private String prenom;



    class JSONAsyncTask extends AsyncTask<String, Void, JSONObject> {
        // Params, Progress, Result

        @Override
        protected void onPreExecute() { // S’exécute dans l’UI Thread
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(String... qs) {
            // pas d'interaction avec l'UI Thread ici
            // On exécute la requete
            //String res = MainActivity.this.gs.requete(qs[0]);
            String res = null;
            try {
                res = MainActivity.this.gs.sendGet(qs[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                JSONArray jsonTab = new JSONArray(res);
                //System.out.println("je récupère : " + jsonTab);
                JSONObject json = jsonTab.getJSONObject(0);
                isCo = true;
                return json;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(JSONObject json) { // S’exécute dans l’UI Thread
            if (json != null) {
                super.onPostExecute(json);

                try {
                    nom = json.getString("nom");
                    prenom= json.getString("prenom");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String s = json.toString();
                //MainActivity.this.gs.alerter(MainActivity.this.gs.jsonToPrettyFormat(s));

                Gson gson = new GsonBuilder()
                        .serializeNulls()
                        .disableHtmlEscaping()
                        .setPrettyPrinting()
                        .create();

                User u = gson.fromJson(s, User.class);
                MainActivity.this.gs.alerter(u.toString());
            }
        }
    }
    GlobalState gs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editTextLogin = (EditText) findViewById(R.id.editTextLogin);
        editTextLogin.setOnClickListener(this);
        editTextPasswd = (EditText) findViewById(R.id.editTextMDP);
        editTextPasswd.setOnClickListener(this);
        btnConnexion = (Button) findViewById(R.id.buttonConnexion);
        btnConnexion.setOnClickListener(this);
        btnInscription = (Button) findViewById(R.id.buttonInscription);
        btnInscription.setOnClickListener(this);
        gs = (GlobalState) getApplication();
    }



    @Override
    protected void onStart() {
        super.onStart();
        //String login = settings.getString("login","");
        //String passe = settings.getString("passe","");
        //edtLogin.setText(login);
        //edtPasse.setText(passe);

        // verif état du réseau pour activer les btns
        btnConnexion.setEnabled(gs.verifReseau());
        btnInscription.setEnabled(gs.verifReseau());
    }



    private void alerter(String s) {
        Log.i(CAT, s);
        Toast myToast = Toast.makeText(this, s, Toast.LENGTH_SHORT);
        myToast.show();

    }




    @Override
    public void onClick(View v) {
        // méthode appellé lors du click sur editPseudo ou idbutton
        switch (v.getId()) {
            case R.id.buttonConnexion:
                //recuperer le peudo saisi , s'il n'est pas vide changer d'activité
                //pour afficher la 2nd activité en passant la valeur saisie
                String login = editTextLogin.getText().toString();
                String passwd = editTextPasswd.getText().toString();
                if(login.isEmpty()){
                    alerter("Saisir votre login");
                    return;
                }
                if(passwd.isEmpty()){
                    alerter("Saisir votre mot de passe");
                    return;
                }
                JSONAsyncTask jsAT = new JSONAsyncTask();
                jsAT.execute("/users/" + login+"/"+passwd);

                if (isCo == true){
                    Bundle myBundle = new Bundle();
                    myBundle.putString("nom",nom);
                    myBundle.putString("prenom",prenom);

                    Intent versAcceuil= new Intent(this,ListEspaces.class);
                    versAcceuil.putExtras(myBundle);
                    startActivity(versAcceuil);
                }
                else alerter("identifiants incorrectes");

                break;
            case R.id.buttonInscription:
                Intent versSubscribe= new Intent(this,inscription.class);
                startActivity(versSubscribe);
                break;
            default:    alerter("cas non prévu");
        }
    }
}
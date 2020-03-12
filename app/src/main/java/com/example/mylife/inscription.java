package com.example.mylife;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import classBDD.User;

public class inscription extends AppCompatActivity implements View.OnClickListener{
    private static final String CAT = "IME";
    private EditText editTextNom;
    private EditText editTextPrenom;
    private EditText editTextLogin;
    private EditText editTextPasswd;
    private EditText editTextPasswdConfirm;
    private Button btnSubscibe;
    private User u;

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
            String res = null;
            try {
                System.out.println("********** dans le try");
                res = inscription.this.gs.sendPost(qs[0]);
                System.out.println("******"+res);
                System.out.println("********** après le sendPOST");

            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                JSONObject json = new JSONObject(res);
                return json;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(JSONObject json) { // S’exécute dans l’UI Thread
            if (json != null) {
                //super.onPostExecute(json);

                JSONArray jsa = null;
                try {
                    jsa = json.getJSONArray("user");
                    JSONObject user = jsa.getJSONObject(0);
                    String s = user.toString();

                    Gson gson = new GsonBuilder()
                            .serializeNulls()
                            .disableHtmlEscaping()
                            .setPrettyPrinting()
                            .create();

                    u = gson.fromJson(s, User.class);

                    Bundle myBundle = new Bundle();
                    myBundle.putParcelable("user", u);

                    Intent versAcceuil= new Intent(gs,ListEspaces.class);
                    versAcceuil.putExtras(myBundle);
                    startActivity(versAcceuil);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    GlobalState gs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inscription);
        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new
                    StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        editTextNom = (EditText) findViewById(R.id.editTextNom);
        editTextNom.setOnClickListener(this);
        editTextPrenom = (EditText) findViewById(R.id.editTextPrenom);
        editTextPrenom.setOnClickListener(this);
        editTextLogin = (EditText) findViewById(R.id.editTextLogin);
        editTextLogin.setOnClickListener(this);
        editTextPasswd = (EditText) findViewById(R.id.editTextPasswd);
        editTextPasswd.setOnClickListener(this);
        editTextPasswdConfirm = (EditText) findViewById(R.id.editTextPasswd2);
        editTextPasswdConfirm.setOnClickListener(this);
        btnSubscibe = (Button) findViewById(R.id.buttonSubscribe);
        btnSubscibe.setOnClickListener(this);
        gs = (GlobalState) getApplication();
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
            case R.id.buttonSubscribe:
                //recupere infos pour inscription et passage à la suite si pas vide
                String nom = editTextNom.getText().toString();
                String prenom = editTextPrenom.getText().toString();
                String login = editTextLogin.getText().toString();
                String passwd = editTextPasswd.getText().toString();
                String passwdConfirm = editTextPasswdConfirm.getText().toString();
                if(nom.isEmpty()){
                    alerter("Saisir votre nom");
                    return;
                }
                if(prenom.isEmpty()){
                    alerter("Saisir votre prenom");
                    return;
                }
                if(login.isEmpty()){
                    alerter("Saisir votre login");
                    return;
                }
                if(passwd.isEmpty()){
                    alerter("Saisir votre mot de passe");
                    return;
                }
                if(passwdConfirm.isEmpty()){
                    alerter("Saisir confirmation de mot de passe");
                    return;
                }
                if(!passwd.equals(passwdConfirm)){
                    alerter("Les mots de passe sont différents");
                    return;
                }

                JSONAsyncTask jsAT = new JSONAsyncTask();
                jsAT.execute("/users?nom=" +nom+"&prenom="+prenom+"&login="+login+"&passwd="+passwd);
                break;
            default:    alerter("cas non prévu");
        }
    }
}

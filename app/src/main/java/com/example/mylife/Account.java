package com.example.mylife;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import classBDD.User;

public class Account extends AppCompatActivity implements View.OnClickListener{
    private static final String CAT = "IME";
    private String nom;
    private String prenom;
    private Integer id;
    private EditText editTextLogin;
    private EditText editTextPasswd;
    private EditText editTextPasswdConfirm;
    private Button btnSubscibe;
    private User u;

    //TODO : alert pour confirmation si delete account + check si delete aussi espaces et indicateurs
    // changer mot de passe + verif si nouveau OK pour connexion + user de gs change mdp
    // voir si possible de stocker login et MDP dans settings pour compléter automatiquement pour connexion
    // via gs.getUser pour cela ??? (voir ce qui a était fait en TD avec bourdo)

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
                res = Account.this.gs.requete(qs[0],"POST",null);

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

                    gs.setUser(u);

                    Intent versAcceuil= new Intent(gs,ListEspaces.class);
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
        setContentView(R.layout.activity_account);
        gs = (GlobalState) getApplication();

        u = gs.getUser();
        nom = u.getNom();
        prenom = u.getPrenom();
        id = u.getId();
        TextView userInfos = (TextView) findViewById(R.id.infosUser);
        userInfos.setText(nom.toUpperCase() + " "+ prenom);

        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new
                    StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        editTextLogin = (EditText) findViewById(R.id.editTextLogin);
        editTextLogin.setOnClickListener(this);
        editTextPasswd = (EditText) findViewById(R.id.editTextPasswd);
        editTextPasswd.setOnClickListener(this);
        editTextPasswdConfirm = (EditText) findViewById(R.id.editTextPasswd2);
        editTextPasswdConfirm.setOnClickListener(this);
        //btnSubscibe = (Button) findViewById(R.id.buttonSubscribe);
        //btnSubscibe.setOnClickListener(this);
        gs = (GlobalState) getApplication();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // affichage des boutons du menu
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id) {
            case R.id.action_historique :
                // affiche de l'activité historique
                Intent versPrefs = new Intent(this, Calendar.class);
                startActivity(versPrefs);
                break;
            case R.id.addIndicateur :
                // affiche de l'activité add Indicateur
                Intent versaddIndicateur = new Intent(this, AddIndicateur.class);
                startActivity(versaddIndicateur);
                break;
            case R.id.listIndicateur :
                // affiche la liste des indicateurs de l'user
                Intent listIndicateurs= new Intent(this,ListIndicateurs.class);
                startActivity(listIndicateurs);
                break;
            case R.id.addEspace :
                // affiche de l'activité add Espace
                Intent versaddEspace = new Intent(this, AddEspace.class);
                startActivity(versaddEspace);
                break;
            case R.id.listEspace:
                // affiche la liste des espaces de l'user
                Intent listEspaces= new Intent(this,ListEspaces.class);
                startActivity(listEspaces);
                break;
            case R.id.action_settings :
                Intent setting = new Intent(this,SettingsActivity.class);
                startActivity(setting);
                break;
            case R.id.action_account :
                Intent account = new Intent(this,Account.class);
                startActivity(account);
                break;
        }
        return super.onOptionsItemSelected(item);
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
                String login = editTextLogin.getText().toString();
                String passwd = editTextPasswd.getText().toString();
                String passwdConfirm = editTextPasswdConfirm.getText().toString();

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

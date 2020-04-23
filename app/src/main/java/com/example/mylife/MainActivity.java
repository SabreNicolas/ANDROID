package com.example.mylife;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
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


import classBDD.User;



public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String CAT = "IME";
    private EditText editTextLogin;
    private EditText editTextPasswd;
    private Button btnConnexion;
    private Button btnInscription;
    private User u;
    private boolean isCo = false;



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
                System.out.println("envoie de la requête");
                res = MainActivity.this.gs.requete(qs[0],"GET",null);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                JSONArray jsonTab = new JSONArray(res);
                JSONObject json = jsonTab.getJSONObject(0);
                return json;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(JSONObject json) { // S’exécute dans l’UI Thread
            if (json != null) {
                isCo = true;
                super.onPostExecute(json);

                String s = json.toString();
                //MainActivity.this.gs.alerter(MainActivity.this.gs.jsonToPrettyFormat(s));

                Gson gson = new GsonBuilder()
                        .serializeNulls()
                        .disableHtmlEscaping()
                        .setPrettyPrinting()
                        .create();

                u = gson.fromJson(s, User.class);
                gs.setUser(u);
                Intent versAcceuil= new Intent(gs,ListEspaces.class);
                startActivity(versAcceuil);

            }
            else alerter("identifiants incorrectes");
        }
    }
    GlobalState gs;
    SharedPreferences settings;

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
        settings = PreferenceManager.getDefaultSharedPreferences(this);
        gs = (GlobalState) getApplication();
    }



    @Override
    protected void onStart() {
        super.onStart();
        String login = settings.getString("login","");
        String passwd = settings.getString("passe","");
        editTextLogin.setText(login);
        editTextPasswd.setText(passwd);

        // verif état du réseau pour activer les btns
        btnConnexion.setEnabled(gs.verifReseau());
        btnInscription.setEnabled(gs.verifReseau());

        if(!gs.verifReseau()){
            alerter("Vous devez avoir une connexion internet");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // affichage des boutons du menu
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);
        return true;
    }

    //TODO : voir pour récupèrer la nouvelle value de l'url passé dans les préférences
    // quand on veut se connecter et que l'on change la value de l'url
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id) {
            case R.id.action_settings :
                Intent setting = new Intent(this,SettingsActivity.class);
                startActivity(setting);
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

                // sauvegarde du contenu des champs login, passwd et URL API
                // dans les préférences
                SharedPreferences.Editor editor = settings.edit();
                editor.clear();
                editor.putString("login",login);
                editor.putString("passe",passwd);
                editor.commit();

                JSONAsyncTask jsAT = new JSONAsyncTask();
                jsAT.execute("/users/" + login+"/"+passwd);
                break;
            case R.id.buttonInscription:
                Intent versSubscribe= new Intent(this, Inscription.class);
                startActivity(versSubscribe);
                break;
            default:    alerter("cas non prévu");
        }
    }
}
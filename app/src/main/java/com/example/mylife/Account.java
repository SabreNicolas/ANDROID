package com.example.mylife;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import androidx.preference.PreferenceManager;

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
    private Button btnUpdate;
    private Button btnDelete;
    private User u;
    private String httpType;
    private JSONObject reqBody;


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
                res = Account.this.gs.requete(qs[0],httpType,reqBody);

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
                if(httpType.equals("PUT")){
                    alerter("Mot de passe mis à jour avec succès !");
                    u.setPasswd(editTextPasswd.getText().toString());
                    gs.setUser(u);
                    editTextPasswd.setText("");
                    editTextPasswdConfirm.setText("");
                }
                else {
                    Intent connexion = new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(connexion);
                }

            }
        }
    }
    GlobalState gs;
    SharedPreferences settings;
    DialogInterface.OnClickListener dialogClickListener;


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

        editTextLogin = (EditText) findViewById(R.id.editTextLogin);
        editTextLogin.setText(u.getLogin());
        editTextLogin.setEnabled(false);
        editTextPasswd = (EditText) findViewById(R.id.editTextPasswd);
        editTextPasswdConfirm = (EditText) findViewById(R.id.editTextPasswd2);
        btnUpdate = (Button) findViewById(R.id.buttonChangeMDP);
        btnUpdate.setOnClickListener(this);
        btnDelete = (Button) findViewById(R.id.buttonDeleteAccount);
        btnDelete.setOnClickListener(this);
        settings = PreferenceManager.getDefaultSharedPreferences(this);

        dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        JSONAsyncTask jsaDelete = new JSONAsyncTask();
                        httpType = "DELETE";
                        reqBody = null;

                        SharedPreferences.Editor editorDelete = settings.edit();
                        editorDelete.clear();

                        jsaDelete.execute("/users/"+id);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        alerter("Le compte n'a pas été supprimé");
                        break;
                }
            }
        };
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
            case R.id.deconnexion :
                gs.setUser(null);
                Intent connexion = new Intent(this,MainActivity.class);
                startActivity(connexion);
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
        switch (v.getId()) {
            case R.id.buttonChangeMDP:
                String passwd = editTextPasswd.getText().toString();
                String passwdConfirm = editTextPasswdConfirm.getText().toString();

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
                httpType = "PUT";
                reqBody = new JSONObject();
                try {
                    reqBody.put("passwd", passwd);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // sauvegarde du nouveau mot de passe
                SharedPreferences.Editor editor = settings.edit();
                editor.clear();
                editor.putString("login",u.getLogin());
                editor.putString("passe",passwd);
                editor.putString("urlData",gs.getURL());
                editor.commit();

                jsAT.execute("/users/"+id);
                break;

            case R.id.buttonDeleteAccount:
                AlertDialog.Builder ab = new AlertDialog.Builder(this);
                ab.setMessage("Voulez-vous supprimer votre compte et vos données ?").setPositiveButton("OUI", dialogClickListener)
                        .setNegativeButton("NON", dialogClickListener).show();
                break;
            default:    alerter("cas non prévu");
        }
    }
}

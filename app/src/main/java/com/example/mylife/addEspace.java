package com.example.mylife;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

import classBDD.Espace;
import classBDD.User;

public class addEspace extends AppCompatActivity implements View.OnClickListener{

    private static final String CAT = "IME";
    private EditText editTextName;
    private Button btnTerminer;
    private User user;
    private Espace espace;
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
                res = addEspace.this.gs.requete(qs[0],httpType,reqBody);

            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                JSONObject json;
                if(espace == null){
                    JSONArray jsonTab = new JSONArray(res);
                    json = jsonTab.getJSONObject(0);
                }
                else{
                    json = new JSONObject(res);
                }
                return json;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(JSONObject json) { // S’exécute dans l’UI Thread
            if (json != null) {
                if(espace == null){
                    alerter("Espace ajouté avec succès !");
                    editTextName.setText("");
                }
                else {
                    alerter("Espace mis à jour avec succès !");
                }

            }
        }
    }
    GlobalState gs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_espace);
        editTextName = (EditText) findViewById(R.id.editTextNameEspace);
        editTextName.setOnClickListener(this);
        btnTerminer = (Button) findViewById(R.id.buttonTerminerAddEspace);
        btnTerminer.setOnClickListener(this);
        gs = (GlobalState) getApplication();
        user = gs.getUser();
        espace = gs.getEspace();
        gs.deleteEspace();
        if(this.espace != null){
            editTextName.setText(espace.getNomEspace());
        }
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
                Intent versaddIndicateur = new Intent(this, addIndicateur.class);
                startActivity(versaddIndicateur);
                break;
            case R.id.listIndicateur :
                // affiche la liste des indicateurs de l'user
                Intent listIndicateurs= new Intent(this,ListIndicateurs.class);
                startActivity(listIndicateurs);
                break;
            case R.id.addEspace :
                // affiche de l'activité add Espace
                Intent versaddEspace = new Intent(this, addEspace.class);
                startActivity(versaddEspace);
                break;
            case R.id.listEspace:
                // affiche la liste des indicateurs de l'user
                Intent listEspaces= new Intent(this,ListEspaces.class);
                startActivity(listEspaces);
                break;
            case R.id.action_settings : break;
            case R.id.action_account : break;
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
            case R.id.buttonTerminerAddEspace:
                String name = editTextName.getText().toString();
                if(name.isEmpty()){
                    alerter("Saisir un nom pour l'espace");
                    return;
                }
                JSONAsyncTask jsAT = new JSONAsyncTask();

                if(this.espace == null){
                    this.httpType = "POST";
                    this.reqBody = null;
                    jsAT.execute("/espaces/?nomEspace=" + name+"&idUser="+ user.getId());
                }
                else {
                    this.httpType = "PUT";
                    reqBody = new JSONObject();
                    try {
                        reqBody.put("nomEspace", name);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    jsAT.execute("/espaces/"+espace.getId());
                }

                break;
            default:    alerter("cas non prévu");
        }
    }
}

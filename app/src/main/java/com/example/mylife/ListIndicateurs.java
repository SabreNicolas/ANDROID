package com.example.mylife;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import classBDD.Indicateur;
import classBDD.User;

public class ListIndicateurs extends AppCompatActivity {
    private Integer id;
    private User u;
    private ArrayList<Indicateur> listIndicateurs;
    private Button btnColEdit;
    private Button btnColDelete;
    private ListView lv;


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
                res = ListIndicateurs.this.gs.requete(qs[0],"GET",null);
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
                super.onPostExecute(json);

                Gson gson = new GsonBuilder()
                        .serializeNulls()
                        .disableHtmlEscaping()
                        .setPrettyPrinting()
                        .create();

                try {
                    JSONArray jsa = json.getJSONArray("indicateurs");
                    for (int i = 0; i < jsa.length(); i++) {
                        JSONObject indicateur = jsa.getJSONObject(i);

                        String s = indicateur.toString();
                        Indicateur e  = gson.fromJson(s, Indicateur.class);
                        listIndicateurs.add(e);
                    }
                    showIndicateurs();

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
        setContentView(R.layout.activity_list_indicateurs);
        gs = (GlobalState) getApplication();


        u = gs.getUser();
        id = u.getId();


        listIndicateurs = new ArrayList<Indicateur>();

        ListIndicateurs.JSONAsyncTask jsAT = new JSONAsyncTask();
        jsAT.execute("indicateursUser/" + id+"/indicateurs");

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
                // affiche la liste des indicateurs de l'user
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

    public void showIndicateurs() {
        System.out.println("j'ai fini mon traitement et je viens de créer la liste d'indicateurs : "+listIndicateurs);
        afficherIndicateurs();
    }

    public void afficherIndicateurs(){
        lv = (ListView) findViewById(R.id.lvIndicateurs);
        MyCustomAdapter adapterNext = new MyCustomAdapter(listIndicateurs, this,gs);
        lv.setAdapter(adapterNext);
    }

}

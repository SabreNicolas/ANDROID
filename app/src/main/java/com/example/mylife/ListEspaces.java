package com.example.mylife;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.MatrixCursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import classBDD.Espace;
import classBDD.User;

public class ListEspaces extends AppCompatActivity {
    private String nom;
    private String prenom;
    private Integer id;
    private User u;
    private List<Espace> listEspaces;
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
                res = ListEspaces.this.gs.sendGet(qs[0]);
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
                    JSONArray jsa = json.getJSONArray("espaces");
                    for (int i = 0; i < jsa.length(); i++) {
                        JSONObject espace = jsa.getJSONObject(i);

                        String s = espace.toString();
                        Espace e  = gson.fromJson(s, Espace.class);
                        listEspaces.add(e);
                    }
                    showEspaces();

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
        setContentView(R.layout.activity_list_espaces);
        gs = (GlobalState) getApplication();

        u = gs.getUser();
        nom = u.getNom();
        prenom = u.getPrenom();
        id = u.getId();
        System.out.println(u.toString());
        TextView editTextLogin = (TextView) findViewById(R.id.login);
        editTextLogin.setText(nom.toUpperCase() + " "+ prenom);


        listEspaces = new ArrayList<Espace>();

        ListEspaces.JSONAsyncTask jsAT = new JSONAsyncTask();
        jsAT.execute("espacesUser/" + id+"/espaces");

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

    public void showEspaces() {
        System.out.println("j'ai fini mon traitement et je viens de créer la liste d'espace : "+listEspaces);
        afficherEspaces();
    }

    public void afficherEspaces(){
        String[] columns = new String[] { "_id", "nomEspaces", "edit" , "delete"};
        MatrixCursor matrixCursor= new MatrixCursor(columns);
        startManagingCursor(matrixCursor);
        for(Espace e:listEspaces){
            matrixCursor.addRow(new Object[] {e.getId(), e.getNomEspace(),"EDIT","DELETE"});
        }
        String[] from = new String[] {"nomEspaces", "edit","delete"};
        int[] to = new int[] { R.id.textBtnCol1, R.id.textBtnCol2,R.id.textBtnCol3};
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.row_item, matrixCursor, from, to, 0);
        lv = (ListView) findViewById(R.id.lv);
        lv.setAdapter(adapter);

        //Gestion des clics sur les lignes
        AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View container, int position, long id) {
                System.out.println("click sur : " + id);
                System.out.println("position : " + position);
            }

        };
        lv.setOnItemClickListener(itemClickListener);
    }

}

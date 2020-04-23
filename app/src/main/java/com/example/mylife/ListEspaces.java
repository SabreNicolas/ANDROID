package com.example.mylife;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import classBDD.Espace;
import classBDD.User;

public class ListEspaces extends AppCompatActivity {
    private String nom;
    private String prenom;
    private Integer id;
    private User u;
    private ArrayList<Espace> listEspaces;
    private ListView lv;
    private Date dateData;
    private String dateDataFormatted;
    private TextView date;
    private DatePickerDialog datePickerDialog;


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
                res = ListEspaces.this.gs.requete(qs[0],"GET",null);
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
        TextView editTextLogin = (TextView) findViewById(R.id.login);
        editTextLogin.setText(nom.toUpperCase() + " "+ prenom);
        date = (TextView) findViewById(R.id.dateAdd);
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final java.util.Calendar c = java.util.Calendar.getInstance();
                int mYear = c.get(java.util.Calendar.YEAR); // current year
                int mMonth = c.get(java.util.Calendar.MONTH); // current month
                int mDay = c.get(java.util.Calendar.DAY_OF_MONTH); // current day
                // date picker dialog
                datePickerDialog = new DatePickerDialog(ListEspaces.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                date.setText(String.format("%02d", year) + "-"
                                        + String.format("%02d", (monthOfYear + 1)) + "-" + String.format("%02d", dayOfMonth));
                                dateDataFormatted = date.getText().toString();
                                gs.setDate(dateDataFormatted);
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });
        listEspaces = new ArrayList<Espace>();

        ListEspaces.JSONAsyncTask jsAT = new JSONAsyncTask();
        jsAT.execute("espacesUser/" + id+"/espaces");

        setDate();

    }

    private void setDate(){
        dateData = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        dateDataFormatted = sdf.format(dateData);
        date.setText(dateDataFormatted);
        gs.setDate(dateDataFormatted);
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

    public void showEspaces() {
        System.out.println("j'ai fini mon traitement et je viens de créer la liste d'espace : "+listEspaces);
        afficherEspaces();
    }

    public void afficherEspaces(){
        lv = (ListView) findViewById(R.id.lv);
        MyCustomAdapter adapterNext = new MyCustomAdapter(listEspaces, this,gs,"Espace");
        lv.setAdapter(adapterNext);
    }

}

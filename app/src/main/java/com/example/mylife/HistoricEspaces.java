package com.example.mylife;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.MatrixCursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import classBDD.Espace;
import classBDD.User;

public class HistoricEspaces extends AppCompatActivity {

    private static final String CAT = "Historic";
    private TextView editTextDate;
    private DatePickerDialog datePickerDialog;
    private ArrayList<Espace> listEspaces;
    private String dateHistoric;
    private ListView lv;
    private Integer id;
    private User u;

    class JSONAsyncTask extends AsyncTask<String, Void, JSONObject> {
        // Params, Progress, Result

        @Override
        protected void onPreExecute() { // S’exécute dans l’UI Thread
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(String... qs) {
            String res = null;
            try {
                res = HistoricEspaces.this.gs.requete(qs[0],"GET",null);
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
                    JSONArray jsa = json.getJSONArray("espacesHistorique");
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
        setContentView(R.layout.activity_historic_espaces);
        editTextDate = (TextView) findViewById(R.id.dateHistoric);
        gs = (GlobalState) getApplication();
        u = gs.getUser();
        id = u.getId();
        listEspaces = new ArrayList<Espace>();

        final java.util.Calendar c = java.util.Calendar.getInstance();
        int mYear = c.get(java.util.Calendar.YEAR); // current year
        int mMonth = c.get(java.util.Calendar.MONTH); // current month
        int mDay = c.get(java.util.Calendar.DAY_OF_MONTH); // current day
        // date picker dialog
        datePickerDialog = new DatePickerDialog(HistoricEspaces.this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        dateHistoric =(String.format("%02d", year) + "-"
                                + String.format("%02d", (monthOfYear + 1)) + "-" + String.format("%02d", dayOfMonth));
                        gs.setDate(dateHistoric);
                        editTextDate.setText("Historique du " + String.format("%02d", dayOfMonth)+"/"+String.format("%02d", (monthOfYear + 1))+"/"+String.format("%02d", year)+" :");
                        HistoricEspaces.JSONAsyncTask jsAT = new JSONAsyncTask();
                        jsAT.execute("activites/" +id+"/"+dateHistoric+"/historic");
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();

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
                finish();
                Intent versHistoric = new Intent(this, HistoricEspaces.class);
                startActivity(versHistoric);
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

    private void alerter(String s) {
        Log.i(CAT, s);
        Toast myToast = Toast.makeText(this, s, Toast.LENGTH_SHORT);
        myToast.show();
    }

    public Object getItem(long id) {
        Espace espaceSelect = null;
        for(Espace espace : listEspaces){
            if (espace.getId() == id){
                espaceSelect = espace;
            }
        }
        return espaceSelect;
    }

    public void showEspaces() {
        System.out.println("j'ai fini mon traitement et je viens de récupèrer l'historique : "+listEspaces);
        afficherEspaces();
    }

    public void afficherEspaces(){
        String[] columns = new String[] { "_id", "col1" };
        MatrixCursor matrixCursor= new MatrixCursor(columns);
        startManagingCursor(matrixCursor);
        for(Espace espace : listEspaces){
            matrixCursor.addRow(new Object[] { espace.getId(),espace.getNomEspace()});
        }
        String[] from = new String[] {"col1"};
        int[] to = new int[] { R.id.textViewCol1};
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.row_item_historic, matrixCursor, from, to, 0);
        lv = (ListView) findViewById(R.id.lvHistoric);
        lv.setAdapter(adapter);

        // Gestion des clics sur les lignes
        AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View container, int position, long id) {
                gs.setEspace((Espace) getItem(id));
                System.out.println("espace cliqué pour historique :"+gs.getEspace());
                Intent versHistoric= new Intent(getApplicationContext(), AddDataEspace.class);
                startActivity(versHistoric);
            }
        };
        lv.setOnItemClickListener(itemClickListener);
    }

}

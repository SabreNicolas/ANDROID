package com.example.mylife;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import classBDD.Indicateur;
import classBDD.User;

public class AddIndicateur extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private static final String CAT = "IME";
    private EditText editTextName;
    private Spinner selectType;
    private Button btnTerminer;
    private Spinner nbValues;
    private LinearLayout zoneAddNbValues;
    private LinearLayout zoneAddValues;
    private Integer nbVal = 0;
    private User user;
    private Indicateur indicateur;
    private String httpType;
    private JSONObject reqBody;
    private String typeSelected;
    private String listValues[];

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
                res = AddIndicateur.this.gs.requete(qs[0],httpType,reqBody);

            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                JSONObject json;
                if(indicateur == null){
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
                if(indicateur == null){
                    alerter("Indicateur ajouté avec succès !");
                    editTextName.setText("");
                    zoneAddValues.removeAllViews();
                    zoneAddNbValues.removeAllViews();
                    nbVal=0;
                    for (int i = 0; i < selectType.getCount(); i++) {
                        if (selectType.getItemAtPosition(i).toString().equals("Champ de saisie")) {
                            selectType.setSelection(i);
                            break;
                        }
                    }
                }
                else {
                    alerter("Indicateur mis à jour avec succès !");
                    Intent listIndicateurs= new Intent(getApplicationContext(),ListIndicateurs.class);
                    startActivity(listIndicateurs);
                }

            }
        }
    }

    GlobalState gs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_indicateur);

        editTextName = findViewById(R.id.Indicateur);
        editTextName.setOnClickListener(this);
        selectType = findViewById(R.id.SpinnerTypeIndicateur);
        selectType.setOnItemSelectedListener(this);
        zoneAddNbValues = (LinearLayout) findViewById(R.id.dynamicNbValues);
        zoneAddValues = (LinearLayout) findViewById(R.id.dynamicValues);
        btnTerminer = findViewById(R.id.buttonTerminerAddIndicateur);
        btnTerminer.setOnClickListener(this);

        gs = (GlobalState) getApplication();
        user = gs.getUser();
        indicateur = gs.getIndicateur();
        gs.deleteIndicateur();

        if(this.indicateur != null){
            listValues = indicateur.getValeurInit().split(";");
            editTextName.setText(indicateur.getNomIndicateur());

            for (int i = 0; i < selectType.getCount(); i++) {
                if (selectType.getItemAtPosition(i).toString().equals(indicateur.getType())) {
                    selectType.setSelection(i);
                    break;
                }
            }
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
                Intent versHistoric = new Intent(this, HistoricEspaces.class);
                startActivity(versHistoric);
                break;
            case R.id.addIndicateur :
                // affiche de l'activité add Indicateur
                finish();
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
            case R.id.buttonTerminerAddIndicateur:
                String name = editTextName.getText().toString();
                if(name.isEmpty()){
                    alerter("Saisir un nom pour l'espace");
                    return;
                }

                String valeurInit="";
                for (int i=1; i<=nbVal; i++){
                    EditText val = findViewById(R.id.dynamicValues+i);
                    String value = val.getText().toString();
                    if(value.isEmpty()){
                        alerter("Il y a des valeurs vides");
                        return;
                    }
                    else{
                        valeurInit = valeurInit + val.getText() + ";";
                    }
                }

                if (valeurInit.isEmpty()){
                    valeurInit ="0";
                }

                JSONAsyncTask jsAT = new JSONAsyncTask();

                if(this.indicateur == null){
                    this.httpType = "POST";
                    this.reqBody = null;
                    jsAT.execute("/indicateurs/?type="+typeSelected+"&valeurInit="+valeurInit+"&nomIndicateur="+name+"&idUser="+user.getId());
                }
                else {
                    this.httpType = "PUT";
                    reqBody = new JSONObject();
                    try {
                        reqBody.put("type", typeSelected);
                        reqBody.put("valeurInit", valeurInit);
                        reqBody.put("nomIndicateur", name);
                        reqBody.put("idUser", user.getId());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    jsAT.execute("/indicateurs/"+indicateur.getId());
                }

                break;
            default:    alerter("cas non prévu");
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // An item was selected. You can retrieve the selected item using
        String selectedItem = parent.getItemAtPosition(position).toString();
        nbVal = 0;

        if(selectedItem.equals("Menu déroulant") || selectedItem.equals("Champ de saisie") || selectedItem.equals("Oui ou Non") || selectedItem.equals("Curseur")){
            zoneAddNbValues.removeAllViews();
            typeSelected = selectedItem;

            if(selectedItem.equals("Menu déroulant"))
            {
                TextView libelleNbValues = new TextView(getApplicationContext());
                libelleNbValues.setText("Nombre de valeurs possibles :");
                libelleNbValues.setTextSize(25);
                zoneAddNbValues.addView(libelleNbValues);
                nbValues = new Spinner(getApplicationContext());
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.listNbValues, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                nbValues.setAdapter(adapter);
                nbValues.setOnItemSelectedListener(this);
                zoneAddNbValues.addView(nbValues);

                if(this.indicateur != null){

                    for (int i = 0; i < nbValues.getCount(); i++) {
                        if (nbValues.getItemAtPosition(i).toString().equals(String.valueOf(listValues.length))) {
                            nbValues.setSelection(i);
                            break;
                        }
                    }
                }

            }
            else {
                zoneAddValues.removeAllViews();
            }
        }
        else{
            zoneAddValues.removeAllViews();
            nbVal = Integer.parseInt(selectedItem);
            for (int i=1; i<=nbVal; i++){
                EditText edit = new EditText(getApplicationContext());
                edit.setId(R.id.dynamicValues+i);
                zoneAddValues.addView(edit);
            }

            if(this.indicateur != null){
                Integer limite;
                if (listValues.length > nbVal){
                    limite = nbVal;
                }
                else {
                    limite = listValues.length;
                }
                for (int i = 0; i < limite; i++) {
                    EditText editUpdate = findViewById(R.id.dynamicValues+(i+1));
                        editUpdate.setText(listValues[i]);
                }
            }
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

}

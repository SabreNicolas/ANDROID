package com.example.mylife;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewParent;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Calendar;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import classBDD.Espace;
import classBDD.Indicateur;
import classBDD.User;

import static java.lang.Thread.sleep;

public class addDataEspace extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener{

    private static final String CAT = "IME";
    private TextView name;
    private TextView date;
    private DatePickerDialog datePickerDialog;
    private Button btnTerminer;
    private Spinner nbIndicateur;
    private LinearLayout zoneAddIndicateur;
    private Espace espace;
    private Date dateData;
    private String dateDataFormatted;
    private User u;
    private ArrayList<Indicateur> listIndicateursRecup = new ArrayList<Indicateur>();
    private ArrayList<Indicateur> listAllIndicateurs = new ArrayList<Indicateur>();
    private Map<Indicateur,String> mapValueGetForDate  = new HashMap<>();
    private String httpType;
    private JSONObject reqBody;
    private int nbVal;
    private Map<LinearLayout,Indicateur> mapIndicateur = new HashMap<>();
    private Map<LinearLayout,String> mapValueIndicateur = new HashMap<>();
    private boolean isUpdate = false;

    private boolean isAllIndicateurs = false;


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
                res = addDataEspace.this.gs.requete(qs[0],httpType,reqBody);
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
                    JSONArray jsa = null;
                    if ( json.has("indicateurs")){
                        jsa = json.getJSONArray("indicateurs");
                        for (int i = 0; i < jsa.length(); i++) {
                            JSONObject indicateur = jsa.getJSONObject(i);
                            String s = indicateur.toString();
                            Indicateur e  = gson.fromJson(s, Indicateur.class);
                            if(isAllIndicateurs == true) {
                                listAllIndicateurs.add(e);
                            }
                            else{
                                listIndicateursRecup.add(e);
                            }
                        }
                        isAllIndicateurs = true;
                        System.out.println("tout les indicateurs en BDD sont : " + listAllIndicateurs);
                        System.out.println("les indicateurs connus sont : " + listIndicateursRecup);

                        if (!listIndicateursRecup.isEmpty()){
                            setNbIndicateur();
                        }
                    }
                    else if (json.has("indicateur")){
                        alerter("Ajout réussi !");
                        finish();
                        Intent listEspaces= new Intent(getApplicationContext(),ListEspaces.class);
                        startActivity(listEspaces);
                    }
                    else if (json.has("indicateursDate")){
                        jsa = json.getJSONArray("indicateursDate");
                        for (int i = 0; i < jsa.length(); i++) {
                            JSONObject indicateur = jsa.getJSONObject(i);
                            String s = indicateur.toString();
                            Indicateur e  = gson.fromJson(s, Indicateur.class);
                            mapValueGetForDate.put(e,indicateur.get("valeur").toString());
                        }
                        if (!mapValueGetForDate.isEmpty()){
                            setValueIndicateur();
                        }
                    }


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
        setContentView(R.layout.activity_add_data_espace);

        name = (TextView) findViewById(R.id.EspaceName);
        date = (TextView) findViewById(R.id.dateAdd);
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                // date picker dialog
                datePickerDialog = new DatePickerDialog(addDataEspace.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                date.setText(year + "-"
                                        + (monthOfYear + 1) + "-" + dayOfMonth);
                                    System.out.println("changement de la date, je check si j'ai des valeurs");
                                    mapValueGetForDate.clear();
                                    setNbIndicateur();
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });
        btnTerminer = (Button) findViewById(R.id.buttonTerminerAddData);
        btnTerminer.setOnClickListener(this);
        nbIndicateur = findViewById(R.id.SpinnerNbIndicateur);
        nbIndicateur.setOnItemSelectedListener(this);
        zoneAddIndicateur = (LinearLayout) findViewById(R.id.data);

        gs = (GlobalState) getApplication();
        u = gs.getUser();
        espace = gs.getEspace();
        gs.deleteEspace();
        name.setText(espace.getNomEspace());
        setDate();

        addDataEspace.JSONAsyncTask jsAT = new JSONAsyncTask();
        reqBody = null;
        httpType = "GET";
        jsAT.execute("activites/" + espace.getId()+"/indicateurs");
        addDataEspace.JSONAsyncTask jsATall = new JSONAsyncTask();
        jsATall.execute("indicateursUser/" +u.getId()+"/indicateurs");
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
                // affiche la liste des espaces de l'user
                finish();
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

    private void setDate(){
        dateData = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        dateDataFormatted = sdf.format(dateData);
        date.setText(dateDataFormatted);
    }

    private void setNbIndicateur(){
        for (int i = 0; i < nbIndicateur.getCount(); i++) {
            if (nbIndicateur.getItemAtPosition(i).toString().equals(((String.valueOf(listIndicateursRecup.size()))))) {
                nbIndicateur.setSelection(i);
                nbIndicateur.setEnabled(false);
                break;
            }
        }

        addDataEspace.JSONAsyncTask jsATDate = new JSONAsyncTask();
        jsATDate.execute("activites/" +espace.getId()+"/"+date.getText()+"/indicateurs");


        //TODO : si map pas vide, remplir value en fonction du type de l'indicateur
    }

    private void setIndicateurs(Spinner indicateur){
        for (int j = 0; j < listIndicateursRecup.size(); j++) {
            for (int k = 0; k < indicateur.getCount(); k++) {
                if (indicateur.getItemAtPosition(k).toString().equals((listIndicateursRecup.get(j).getNomIndicateur()))) {
                    indicateur.setSelection(k);
                    indicateur.setEnabled(false);
                    listIndicateursRecup.remove(j);
                    return;
                }
            }
        }
    }

    private void setValueIndicateur(){
        System.out.println("**************** j'ai des values a mettre pour cette date");
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonTerminerAddData:
                String value = "";
                for (int i=1; i<=mapIndicateur.size(); i++){
                    LinearLayout ll = findViewById(R.id.Indicateur+i);
                    Indicateur indicateur = mapIndicateur.get(ll);
                    if (indicateur != null){
                        if (indicateur.getType().equals("Champ de saisie")){
                            EditText valueView = ll.findViewById(R.id.valueOfIndicateur);
                            value = String.valueOf(valueView.getText());
                        }
                        else if (indicateur.getType().equals("Menu déroulant")){
                            value = mapValueIndicateur.get(ll);
                        }
                        else if (indicateur.getType().equals("Oui ou Non")){
                            RadioGroup valueView = ll.findViewById(R.id.valueOfIndicateur);
                            value = String.valueOf(valueView.getCheckedRadioButtonId());
                        }
                        else if (indicateur.getType().equals("Curseur")){
                            SeekBar valueView = ll.findViewById(R.id.valueOfIndicateur);
                            value = String.valueOf(valueView.getProgress());
                        }

                        if(value.equals("")){
                            alerter("Il y a au moins une valeur non renseignée");
                            return;
                        }

                        JSONAsyncTask jsAT = new JSONAsyncTask();

                        if(isUpdate == false){
                            this.httpType = "POST";
                            this.reqBody = null;
                            jsAT.execute("/activites/?idEspace="+espace.getId()+"&idIndicateur="+indicateur.getId()+"&valeur="+value+"&date="+date.getText());
                        }
                    }

                }
                break;
            default:    alerter("cas non prévu");
        }
    }

    @SuppressLint("ResourceType")
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String selectedItem = parent.getItemAtPosition(position).toString();
        Indicateur selectedIndicateur = null;
        LinearLayout viewParent = null;

        if(selectedItem.equals("1") || selectedItem.equals("2") || selectedItem.equals("3") || selectedItem.equals("4") || selectedItem.equals("5")) {
            zoneAddIndicateur.removeAllViewsInLayout();

            nbVal = Integer.parseInt(selectedItem);
            for (int i=1; i<=nbVal; i++){
                LinearLayout ll = new LinearLayout(getApplicationContext());
                ll.setId(R.id.Indicateur+i);
                Spinner indicateur = new Spinner(getApplicationContext());
                ArrayAdapter<Indicateur> adapter = new ArrayAdapter<Indicateur>(getApplicationContext(),  android.R.layout.simple_spinner_dropdown_item, listAllIndicateurs);
                adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);
                indicateur.setAdapter(adapter);
                indicateur.setOnItemSelectedListener(this);
                indicateur.setId(R.id.data+i);
                ll.addView(indicateur);
                zoneAddIndicateur.addView(ll);

                if (!listIndicateursRecup.isEmpty()){
                    setIndicateurs(indicateur);
                }
            }
        }
        else {
            viewParent = (LinearLayout) parent.getParent();
            if(parent.getItemAtPosition(position) instanceof Indicateur){
                selectedIndicateur = (Indicateur) parent.getItemAtPosition(position);
                String type = selectedIndicateur.getType();
                if (type.equals("Champ de saisie") || type.equals("Menu déroulant") || type.equals("Oui ou Non") || type.equals("Curseur")) {
                    String valueInit = selectedIndicateur.getValeurInit();
                    String listValues[] = selectedIndicateur.getValeurInit().split(";");
                    LinearLayout.LayoutParams layoutParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                    View valueOfIndicateur = viewParent.findViewById(R.id.valueOfIndicateur);
                    View pourcentCurseur = viewParent.findViewById(R.id.pourcentCurseur);
                    viewParent.removeView(valueOfIndicateur);
                    viewParent.removeView(pourcentCurseur);

                    mapIndicateur.remove(viewParent);
                    mapIndicateur.put(viewParent, selectedIndicateur);


                    //TODO : eviter de choisir plusieurs fois le même indicateur

                    if (type.equals("Champ de saisie")) {
                        EditText champSaisie = new EditText(getApplicationContext());
                        champSaisie.setId(R.id.valueOfIndicateur);
                        viewParent.addView(champSaisie, layoutParam);
                    } else if (type.equals("Menu déroulant")) {
                        Spinner menuDeroulant = new Spinner(getApplicationContext());
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, listValues);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        menuDeroulant.setAdapter(adapter);
                        menuDeroulant.setOnItemSelectedListener(this);
                        menuDeroulant.setId(R.id.valueOfIndicateur);
                        viewParent.addView(menuDeroulant, layoutParam);

                    } else if (type.equals("Oui ou Non")) {
                        RadioGroup radioGroup = new RadioGroup(getApplicationContext());
                        RadioButton yes = new RadioButton(getApplicationContext());
                        RadioButton no = new RadioButton(getApplicationContext());
                        yes.setText("Oui");
                        no.setText("Non");
                        yes.setId(1);
                        no.setId(0);
                        radioGroup.addView(yes);
                        radioGroup.addView(no);
                        radioGroup.setId(R.id.valueOfIndicateur);
                        viewParent.addView(radioGroup, layoutParam);

                    } else if (type.equals("Curseur")) {
                        final TextView textCurseur = new TextView(getApplicationContext());
                        final ProgressBar progressCurseur = new ProgressBar(getApplicationContext());
                        SeekBar curseur = new SeekBar(getApplicationContext());
                        curseur.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                progressCurseur.setProgress(progress);
                                textCurseur.setText("" + progress + "%");
                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {

                            }
                        });
                        curseur.setId(R.id.valueOfIndicateur);
                        textCurseur.setId(R.id.pourcentCurseur);
                        viewParent.addView(textCurseur);
                        viewParent.addView(curseur, layoutParam);
                    }
                }
            }
            else {
                mapValueIndicateur.remove(viewParent);
                mapValueIndicateur.put(viewParent,selectedItem);
            }
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

}

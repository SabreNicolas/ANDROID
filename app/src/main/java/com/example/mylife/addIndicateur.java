package com.example.mylife;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONObject;

import classBDD.Indicateur;
import classBDD.User;

public class addIndicateur extends AppCompatActivity implements View.OnClickListener {

    private static final String CAT = "IME";
    private EditText editTextName;
    private Spinner selectType;
    private Button btnTerminer;
    private TextView tv;
    private Spinner nbValues;
    private ScrollView ScrollView;
    private User user;
    private Indicateur indicateur;
    private String valueInit;
    private String httpType;
    private JSONObject reqBody;

    GlobalState gs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_indicateur);

        editTextName = findViewById(R.id.Indicateur);
        editTextName.setOnClickListener(this);
        tv = findViewById(R.id.nbValues);
        ScrollView = findViewById(R.id.ScrollViewValues);
        nbValues = findViewById(R.id.values);
        selectType = findViewById(R.id.SpinnerTypeIndicateur);

        btnTerminer = findViewById(R.id.buttonTerminerAddIndicateur);
        btnTerminer.setOnClickListener(this);

        gs = (GlobalState) getApplication();
        user = gs.getUser();
        indicateur = gs.getIndicateur();
        gs.deleteIndicateur();
        if(this.indicateur != null){
            editTextName.setText(indicateur.getNomIndicateur());
        }
    }

    //TODO pas mettre en dur le 2e select, le générer à la volée
    @Override
    public void onResume() {
        super.onResume();
        selectType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                String selectedItem = parent.getItemAtPosition(position).toString();
                if(selectedItem.equals("Case à cocher") || selectedItem.equals("Menu déroulant"))
                {
                    nbValues.setVisibility(View.VISIBLE);
                    tv.setVisibility(View.VISIBLE);
                }
                else {
                    nbValues.setVisibility(View.INVISIBLE);
                    tv.setVisibility(View.INVISIBLE);
                }
            } // to close the onItemSelected
            public void onNothingSelected(AdapterView<?> parent)
            {
                System.out.println("rien n'est selectionné ...");
                nbValues.setVisibility(View.INVISIBLE);
                tv.setVisibility(View.INVISIBLE);
            }
        });

        nbValues.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                System.out.println("click");
                String selectedItem = parent.getItemAtPosition(position).toString();
                Integer nbVal = Integer.parseInt(selectedItem);
                LinearLayout ll = (LinearLayout) findViewById(R.id.dynamic);

                // TODO car 2 est selectionné de base et supprimer les lignes en trop
                // exemple : 5 puis 2, il ajoute 2 aux 5 déjà présentes => vider d'abord la scroll view ?
                /*if(nbVal == 2){
                    sv.setVisibility(View.INVISIBLE);
                    for (int i=1; i<=nbVal; i++){
                        EditText edit = new EditText(getApplicationContext());
                        edit.setId(R.id.values+i);
                        ll.addView(edit);
                        EditText editUpdate = findViewById(R.id.values+i);
                        editUpdate.setText("test"+i);
                    }
                }*/


                ScrollView.setVisibility(View.VISIBLE);
                for (int i=1; i<=nbVal; i++){
                    EditText edit = new EditText(getApplicationContext());
                    edit.setId(R.id.values+i);
                    ll.addView(edit);
                    EditText editUpdate = findViewById(R.id.values+i);
                    editUpdate.setText("test"+i);
                }

                //else sv.setVisibility(View.INVISIBLE);
            } // to close the onItemSelected
            public void onNothingSelected(AdapterView<?> parent)
            {
                System.out.println("rien n'est selectionné ...");
                ScrollView.setVisibility(View.INVISIBLE);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // affichage des boutons du menu
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.SpinnerTypeIndicateur:
                //String type = selectType;

                break;
            default:
                System.out.println("cas non prévu");
        }

    }
}

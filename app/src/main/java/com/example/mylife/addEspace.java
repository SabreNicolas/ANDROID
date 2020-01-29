package com.example.mylife;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class addEspace extends AppCompatActivity implements View.OnClickListener{

    private static final String CAT = "IME";
    private EditText editTextName;
    private Button btnTerminer;
    private Button btnNouvelIndicateur;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_espace);
        editTextName = (EditText) findViewById(R.id.editTextNameEspace);
        editTextName.setOnClickListener(this);
        btnTerminer = (Button) findViewById(R.id.buttonNouvelIndicateur);
        btnTerminer.setOnClickListener(this);
        btnNouvelIndicateur = (Button) findViewById(R.id.buttonTerminerAddEspace);
        btnNouvelIndicateur.setOnClickListener(this);
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
                //recuperer les infos , s'il n'est pas vide changer d'activité
                //pour afficher la 2nd activité en passant les valeurs saisies
                String name = editTextName.getText().toString();
                if(name.isEmpty()){
                    alerter("Saisir un nom pour l'espace");
                    return;
                }
                Bundle myBundle = new Bundle();
                myBundle.putString("name",name);

                //Intent versAcceuil= new Intent(this,ListEspaces.class);
                //versAcceuil.putExtras(myBundle);
                //startActivity(versAcceuil);
                break;
            case R.id.buttonNouvelIndicateur:
                Intent versAddIndicateur= new Intent(this,addIndicateur.class);
                startActivity(versAddIndicateur);
                break;
            default:    alerter("cas non prévu");
        }
    }
}

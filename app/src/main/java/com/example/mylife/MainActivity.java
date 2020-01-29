package com.example.mylife;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String CAT = "IME";
    private EditText editTextLogin;
    private EditText editTextPasswd;
    private Button btnConnexion;
    private Button btnInscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editTextLogin = (EditText) findViewById(R.id.editTextLogin);
        editTextLogin.setOnClickListener(this);
        editTextPasswd = (EditText) findViewById(R.id.editTextMDP);
        editTextPasswd.setOnClickListener(this);
        btnConnexion = (Button) findViewById(R.id.buttonConnexion);
        btnConnexion.setOnClickListener(this);
        btnInscription = (Button) findViewById(R.id.buttonInscription);
        btnInscription.setOnClickListener(this);
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
            case R.id.buttonConnexion:
                //recuperer le peudo saisi , s'il n'est pas vide changer d'activité
                //pour afficher la 2nd activité en passant la valeur saisie
                String login = editTextLogin.getText().toString();
                String passwd = editTextPasswd.getText().toString();
                if(login.isEmpty()){
                    alerter("Saisir votre login");
                    return;
                }
                if(passwd.isEmpty()){
                    alerter("Saisir votre mot de passe");
                    return;
                }
                Bundle myBundle = new Bundle();
                myBundle.putString("login",login);
                myBundle.putString("passwd",passwd);

                Intent versAcceuil= new Intent(this,ListEspaces.class);
                versAcceuil.putExtras(myBundle);
                startActivity(versAcceuil);
                break;
            case R.id.buttonInscription:
                Intent versSubscribe= new Intent(this,inscription.class);
                startActivity(versSubscribe);
                break;
            default:    alerter("cas non prévu");
        }
    }
}
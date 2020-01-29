package com.example.mylife;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class inscription extends AppCompatActivity implements View.OnClickListener{
    private static final String CAT = "IME";
    private EditText editTextNom;
    private EditText editTextPrenom;
    private EditText editTextLogin;
    private EditText editTextPasswd;
    private EditText editTextPasswdConfirm;
    private Button btnSubscibe;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inscription);
        editTextNom = (EditText) findViewById(R.id.editTextNom);
        editTextNom.setOnClickListener(this);
        editTextPrenom = (EditText) findViewById(R.id.editTextPrenom);
        editTextPrenom.setOnClickListener(this);
        editTextLogin = (EditText) findViewById(R.id.editTextLogin);
        editTextLogin.setOnClickListener(this);
        editTextPasswd = (EditText) findViewById(R.id.editTextPasswd);
        editTextPasswd.setOnClickListener(this);
        editTextPasswdConfirm = (EditText) findViewById(R.id.editTextPasswd2);
        editTextPasswdConfirm.setOnClickListener(this);
        btnSubscibe = (Button) findViewById(R.id.buttonSubscribe);
        btnSubscibe.setOnClickListener(this);
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
            case R.id.buttonSubscribe:
                //recupere infos pour inscription et passage à la suite si pas vide
                String nom = editTextNom.getText().toString();
                String prenom = editTextPrenom.getText().toString();
                String login = editTextLogin.getText().toString();
                String passwd = editTextPasswd.getText().toString();
                String passwdConfirm = editTextPasswdConfirm.getText().toString();
                if(nom.isEmpty()){
                    alerter("Saisir votre nom");
                    return;
                }
                if(prenom.isEmpty()){
                    alerter("Saisir votre prenom");
                    return;
                }
                if(login.isEmpty()){
                    alerter("Saisir votre login");
                    return;
                }
                if(passwd.isEmpty()){
                    alerter("Saisir votre mot de passe");
                    return;
                }
                if(passwdConfirm.isEmpty()){
                    alerter("Saisir confirmation de mot de passe");
                    return;
                }
                if(!passwd.equals(passwdConfirm)){
                    alerter("Les mots de passe sont différents");
                    return;
                }
                Bundle myBundle = new Bundle();
                myBundle.putString("nom",nom);
                myBundle.putString("prenom",prenom);
                myBundle.putString("login",login);
                myBundle.putString("passwd",passwd);
                myBundle.putString("passwdConfirm",passwdConfirm);

                Intent versAcceuil= new Intent(this,ListEspaces.class);
                versAcceuil.putExtras(myBundle);
                startActivity(versAcceuil);
                break;
            default:    alerter("cas non prévu");
        }
    }
}

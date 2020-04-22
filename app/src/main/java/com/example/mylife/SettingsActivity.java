package com.example.mylife;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editTextURL;
    private Button btnChangeURL;
    GlobalState gs;

    //TODO : voir si possible de stocker login et MDP dans settings pour compléter automatiquement pour connexion
    // via gs.getUser pour cela ??? (voir ce qui a était fait en TD avec bourdo)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        gs = (GlobalState) getApplication();

        editTextURL = (EditText) findViewById(R.id.url);
        editTextURL.setOnClickListener(this);
        btnChangeURL = (Button) findViewById(R.id.buttonTerminerSettings);
        btnChangeURL.setOnClickListener(this);
        editTextURL.setText(gs.getURL());
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
        switch (id) {
            case R.id.action_historique:
                // affiche de l'activité historique
                Intent versPrefs = new Intent(this, Calendar.class);
                startActivity(versPrefs);
                break;
            case R.id.addIndicateur:
                // affiche de l'activité add Indicateur
                Intent versaddIndicateur = new Intent(this, AddIndicateur.class);
                startActivity(versaddIndicateur);
                break;
            case R.id.listIndicateur:
                // affiche la liste des indicateurs de l'user
                Intent listIndicateurs = new Intent(this, ListIndicateurs.class);
                startActivity(listIndicateurs);
                break;
            case R.id.addEspace:
                // affiche de l'activité add Espace
                Intent versaddEspace = new Intent(this, AddEspace.class);
                startActivity(versaddEspace);
                break;
            case R.id.listEspace:
                // affiche la liste des indicateurs de l'user
                Intent listEspaces = new Intent(this, ListEspaces.class);
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
        }
        return super.onOptionsItemSelected(item);
    }

    private void alerter(String s) {
        Toast myToast = Toast.makeText(this, s, Toast.LENGTH_SHORT);
        myToast.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonTerminerSettings:
                String url = editTextURL.getText().toString();
                if (url.isEmpty()) {
                    alerter("Saisir un URL valide");
                    return;
                }
                gs.setURL(url);
                alerter("URL mis à jour");
                break;
            default:
                alerter("cas non prévu");
        }
    }
}
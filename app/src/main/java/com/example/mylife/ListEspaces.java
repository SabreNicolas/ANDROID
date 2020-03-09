package com.example.mylife;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class ListEspaces extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_espaces);

        Bundle b = this.getIntent().getExtras();
        String loginRecup = b.getString("login");
        Toast t = Toast.makeText(this,"infos récupérés = "+ loginRecup ,Toast.LENGTH_SHORT);
        t.show();
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
            case R.id.gestionIndicateur :
                // affiche de l'activité liste Indicateur
                Intent verslistIndicateurs = new Intent(this, ListIndicateurs.class);
                startActivity(verslistIndicateurs);
                break;
            case R.id.addEspace :
                // affiche de l'activité add Espace
                Intent versaddEspace = new Intent(this, addEspace.class);
                startActivity(versaddEspace);
                break;
            case R.id.action_settings : break;
            case R.id.action_account : break;
        }
        return super.onOptionsItemSelected(item);
    }
}

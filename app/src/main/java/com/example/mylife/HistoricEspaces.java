package com.example.mylife;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

public class HistoricEspaces extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historic_espaces);

        Bundle b = this.getIntent().getExtras();
        String dateRecup = b.getString("date");
        Toast t = Toast.makeText(this,"date = "+ dateRecup,Toast.LENGTH_SHORT);
        t.show();
    }
}

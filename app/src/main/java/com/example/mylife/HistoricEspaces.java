package com.example.mylife;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class HistoricEspaces extends AppCompatActivity {

    private static final String CAT = "DateRecup";
    private TextView editTextDate;
    private String dateRecup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historic_espaces);
        editTextDate = (TextView) findViewById(R.id.dateHistoric);

        Bundle b = this.getIntent().getExtras();
        dateRecup = b.getString("date");
        editTextDate.setText(dateRecup);
    }

}

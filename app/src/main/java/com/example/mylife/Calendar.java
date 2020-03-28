package com.example.mylife;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;

import java.text.SimpleDateFormat;
import java.util.Date;


public class Calendar extends AppCompatActivity implements View.OnClickListener{

    private static final String CAT = "CALENDAR";
    private CalendarView mCalendarView;
    private Button btnHistoric;

    private String dateClicked;
    private String dateFormatted;
    private Date dateJour;
    private String dateJourFormatted;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        mCalendarView = findViewById(R.id.calendarView);
        btnHistoric = (Button) findViewById(R.id.btnHistoric);
        btnHistoric.setOnClickListener(this);

        getDateJour();
        dateFormatted = dateJourFormatted;

        mCalendarView.setOnDayClickListener(new OnDayClickListener() {

            @Override
            public void onDayClick(EventDay eventDay) {
             java.util.Calendar clicked = eventDay.getCalendar();
             dateClicked = clicked.getTime().toString();

             Date date = new Date(dateClicked);
             SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
             dateFormatted = sdf.format(date);
             btnHistoric.setText("Historique du : " + dateFormatted);
            }
        });
    }

    private void alerter(String s) {
        Log.i(CAT, s);
        Toast myToast = Toast.makeText(this, s, Toast.LENGTH_SHORT);
        myToast.show();

    }

    private void getDateJour(){
        dateJour = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        dateJourFormatted = sdf.format(dateJour);
        btnHistoric.setText("Historique du : " + dateJourFormatted);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnHistoric:
                //recuperer le jour sélectionné saisi , s'il n'est pas vide changer d'activité
                //et afficher historique de ce jour

                Bundle myBundle = new Bundle();
                myBundle.putString("date",dateFormatted);

                Intent versHistoric= new Intent(this,HistoricEspaces.class);
                versHistoric.putExtras(myBundle);
                startActivity(versHistoric);
                break;
            default:    alerter("cas non prévu");
        }

    }
}

package com.example.mylife;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;


public class Calendar extends AppCompatActivity{

    public static final String RESULT = "result";
    public static final String EVENT = "event";
    private static final int ADD_NOTE = 44;

    private CalendarView mCalendarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        mCalendarView = (CalendarView) findViewById(R.id.calendarView);



        mCalendarView.setOnDayClickListener(new OnDayClickListener() {

            @Override
            public void onDayClick(EventDay eventDay) {
             java.util.Calendar clicked = eventDay.getCalendar();
                System.out.println("ok click");
                System.out.println(clicked.getTime());
            }
        });
    }

}

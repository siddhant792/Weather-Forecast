package com.sagar.weatherforecast.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Spinner;
import android.widget.Toast;

import com.sagar.weatherforecast.Activity.City_Weather;
import com.sagar.weatherforecast.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Month;
import java.util.Calendar;
import java.util.Date;

public class Calender extends Fragment{

    CalendarView calendarView;
    Spinner drop_down;
    Context context;
    String[] city = { "Delhi", "Mumbai", "Noida"};
    String[] latitude = {"28.7041","19.0760","28.5355"};
    String[] longitude = {"77.1025","72.8777","77.3910"};
    Button btn_report;
    String epoch = "";
    int pos = 0;
    String day_in = "";

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calender, container, false);
        context = getContext();
        calendarView = view.findViewById(R.id.calender);
        btn_report = view.findViewById(R.id.btn_report);
        drop_down = view.findViewById(R.id.drop_down);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 7);
        long endOfMonth = calendar.getTimeInMillis();
        calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -4);
        long startOfMonth = calendar.getTimeInMillis();
        Calendar c = Calendar.getInstance();
        Date d = c.getTime();
        DateFormat dateFormat = new SimpleDateFormat("dd");
        day_in = dateFormat.format(d);
        calendarView.setMaxDate(endOfMonth);
        calendarView.setMinDate(startOfMonth);
        epoch = String.valueOf(calendarView.getDate()).substring(0,10);
        calendarView.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {
            String str = Month.of(month + 1) + " " + dayOfMonth + " " + year;
            SimpleDateFormat df = new SimpleDateFormat("MMM dd yyyy");
            day_in = dayOfMonth + "";
            Date date = null;
            try {
                date = df.parse(str);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            epoch = String.valueOf(date.getTime()/1000L);
        });
        setSpinner();
        btn_report.setOnClickListener(v -> {
            Intent intent = new Intent(context, City_Weather.class);
            intent.putExtra("lat",latitude[pos]);
            intent.putExtra("lon",longitude[pos]);
            intent.putExtra("date",epoch);
            intent.putExtra("city",city[pos]);
            intent.putExtra("day",day_in);
            startActivity(intent);
        });
        return view;
    }

    public void setSpinner(){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, city);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        drop_down.setAdapter(adapter);
        drop_down.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                pos = position;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });
    }
}
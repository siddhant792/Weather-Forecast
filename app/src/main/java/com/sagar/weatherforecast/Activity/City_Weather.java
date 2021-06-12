package com.sagar.weatherforecast.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sagar.weatherforecast.Adapter.AdapterReport;
import com.sagar.weatherforecast.Constants.ApiConstant;
import com.sagar.weatherforecast.Model.ModelReport;
import com.sagar.weatherforecast.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class City_Weather extends AppCompatActivity {

    Context context;
    TextView tv_username,tv_day,tv_temp,tv_unit,tv_city,tv_min_temp,tv_max_temp,tv_pressure,tv_humidity,tv_wind;
    RelativeLayout progress_circular;
    SharedPreferences units;
    String cur_unit = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_weather);
        context = this;

        units = context.getSharedPreferences("units", MODE_PRIVATE);
        //getLastLocation();
        tv_day = findViewById(R.id.tv_day);
        tv_temp = findViewById(R.id.tv_temp);
        tv_unit = findViewById(R.id.tv_unit);
        tv_city = findViewById(R.id.tv_city);
        tv_min_temp = findViewById(R.id.tv_min_temp);
        tv_max_temp = findViewById(R.id.tv_max_temp);
        tv_pressure = findViewById(R.id.tv_pressure);
        tv_humidity = findViewById(R.id.tv_humidity);
        progress_circular = findViewById(R.id.progress_circular);
        tv_wind = findViewById(R.id.tv_wind);
        String lat = getIntent().getStringExtra("lat");
        String lon = getIntent().getStringExtra("lon");
        String date = getIntent().getStringExtra("date");
        String city = getIntent().getStringExtra("city");
        String day = getIntent().getStringExtra("day");
        Calendar calendar = Calendar.getInstance();
        Date d = calendar.getTime();
        DateFormat dateFormat = new SimpleDateFormat("dd");
        int getDay = Integer.parseInt(day);
        int myDay = Integer.parseInt(dateFormat.format(d));
        if(getDay < myDay){
            networkCall(lat, lon, date, city);
        }else{
            networkCallFuture(lat, lon, getDay - myDay, city);
        }
    }

    public void networkCall(String lat, String lon, String dt, String city){
        progress_circular.setVisibility(View.VISIBLE);
        Date expiry = new Date(Long.parseLong(dt) * 1000);
        DateFormat dateFormat = new SimpleDateFormat("dd");

        tv_day.setText(new SimpleDateFormat("EE", Locale.ENGLISH).format(expiry.getTime()) + ", " + new SimpleDateFormat("MMM").format(expiry.getTime()) + " " + dateFormat.format(expiry) + "th" + " " + (expiry.getYear() + 1900));
        // metric -> celsius
        // imperial -> Fahrenheit
        cur_unit = units.getString("unit","metric");
        System.out.println("UNit : " + cur_unit);
        String un = units.getString("unit","metric").equals("metric") ? "°C" : "°F";
        tv_unit.setText(un);
        String LAT_LONG = "https://api.openweathermap.org/data/2.5/onecall/timemachine?lat=" + lat + "&lon=" + lon + "&dt=" + dt + "&appid=" + ApiConstant.API_KEY + "&units=" + units.getString("unit","metric");
        StringRequest stringRequest = new StringRequest(Request.Method.GET,LAT_LONG,new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println(response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject main = jsonObject.getJSONObject("current");
                    String wind_speed = main.getString("wind_speed");
                    String temp = main.getString("temp");
                    String pressure = main.getString("pressure");
                    String humidity = main.getString("humidity");

                    JSONArray jsonArray = main.getJSONArray("weather");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObjectData = jsonArray.getJSONObject(i);
                        String desc = jsonObjectData.getString("main");
                        tv_max_temp.setText("Description : "+desc);
                    }

                    tv_city.setText(city);
                    tv_wind.setText("Wind Speed : " + wind_speed + (units.getString("unit","metric").equals("metric") ? " meter/sec" : " miles/sec"));
                    tv_temp.setText(temp);
                    tv_pressure.setText("Pressure : "+pressure + " hPa");
                    tv_humidity.setText("Humidity : "+humidity + " %");

                    progress_circular.setVisibility(View.INVISIBLE);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        }, error -> Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show());
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    public void networkCallFuture(String LATITUDE, String LONGITUDE, int day, String city){
        progress_circular.setVisibility(View.VISIBLE);
        cur_unit = units.getString("unit","metric");
        String un = units.getString("unit","metric").equals("metric") ? "°C" : "°F";
//        tv_unit.setText(un);
        String JSON_URL = "https://api.openweathermap.org/data/2.5/onecall?lat=" + LATITUDE + "&lon="+ LONGITUDE + "&exclude=hourly,minutely" + "&appid=" + ApiConstant.API_KEY + "&units=" + units.getString("unit","metric");
        StringRequest stringRequest = new StringRequest(Request.Method.GET,JSON_URL,new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("Adv call : " + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("daily");
                    JSONObject jsonObjectData = jsonArray.getJSONObject(day);
                    JSONObject main = jsonObjectData.getJSONObject("temp");
                    String temp = main.getString("day");
                    String temp_min = "Min Temp : " + main.getString("min") + " " + un;;
                    String temp_max = "Max Temp : " + main.getString("max") + " " + un;;
                    String humidity = "Humidity : " + jsonObjectData.getString("humidity") + " %";
                    String wind_speed = "Wind Speed : " + jsonObjectData.getString("wind_speed") + (units.getString("unit","metric").equals("metric") ? " meter/sec" : " miles/sec");

                    Calendar calendar = Calendar.getInstance();
                    calendar.add(Calendar.DAY_OF_YEAR,day);
                    Date date = calendar.getTime();
                    DateFormat dateFormat = new SimpleDateFormat("dd");
                    String day = new SimpleDateFormat("EE", Locale.ENGLISH).format(date.getTime()) + ", " + new SimpleDateFormat("MMM").format(calendar.getTime()) + " " + dateFormat.format(date) + "th" + " " + (date.getYear() + 1900);
                    tv_day.setText(day);
                    tv_max_temp.setText(temp_max);
                    tv_city.setText(city);
                    tv_wind.setText(wind_speed);
                    tv_temp.setText(temp);
                    tv_pressure.setText(temp_min);
                    tv_humidity.setText(humidity);

                    progress_circular.setVisibility(View.INVISIBLE);
                    } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        }, error -> Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show());
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

}
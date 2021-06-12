package com.sagar.weatherforecast.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sagar.weatherforecast.Constants.ApiConstant;
import com.sagar.weatherforecast.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

public class Forecast extends Fragment {
    Context context;
    TextView tv_username,tv_day,tv_temp,tv_unit,tv_city,tv_min_temp,tv_max_temp,tv_pressure,tv_humidity,tv_wind;
    RelativeLayout progress_circular;
    SharedPreferences units;
    String cur_unit = "";
    String LONGITUDE, LATITUDE;

    public Forecast(String LATITUDE, String LONGITUDE){
        this.LONGITUDE = LONGITUDE;
        this.LATITUDE = LATITUDE;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forecast, container, false);
        context = getContext();

        units = context.getSharedPreferences("units", MODE_PRIVATE);
        //getLastLocation();
        tv_username = view.findViewById(R.id.tv_username);
        tv_day = view.findViewById(R.id.tv_day);
        tv_temp = view.findViewById(R.id.tv_temp);
        tv_unit = view.findViewById(R.id.tv_unit);
        tv_city = view.findViewById(R.id.tv_city);
        tv_min_temp = view.findViewById(R.id.tv_min_temp);
        tv_max_temp = view.findViewById(R.id.tv_max_temp);
        tv_pressure = view.findViewById(R.id.tv_pressure);
        tv_humidity = view.findViewById(R.id.tv_humidity);
        progress_circular = view.findViewById(R.id.progress_circular);
        tv_wind = view.findViewById(R.id.tv_wind);
        networkCall();
        return view;
    }

    public void networkCall(){
        progress_circular.setVisibility(View.VISIBLE);
        tv_username.setText(units.getString("name", "USER 001"));
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        DateFormat dateFormat = new SimpleDateFormat("dd");
        tv_day.setText(new SimpleDateFormat("EE", Locale.ENGLISH).format(date.getTime()) + ", " + new SimpleDateFormat("MMM").format(calendar.getTime()) + " " + dateFormat.format(date) + "th" + " " + (date.getYear() + 1900));
        // metric -> celsius
        // imperial -> Fahrenheit
        cur_unit = units.getString("unit","metric");
        System.out.println("UNit : " + cur_unit);
        String un = units.getString("unit","metric").equals("metric") ? "°C" : "°F";
        tv_unit.setText(un);
        String LAT_LONG = "https://api.openweathermap.org/data/2.5/weather?lat=" + LATITUDE + "&lon=" + LONGITUDE + "&appid=" + ApiConstant.API_KEY + "&units=" + units.getString("unit","metric");
        StringRequest stringRequest = new StringRequest(Request.Method.GET,LAT_LONG,new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println(response);
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    String city = jsonObject.getString("name");
                    JSONObject wind = jsonObject.getJSONObject("wind");
                    String wind_speed = wind.getString("speed");
                    JSONObject main = jsonObject.getJSONObject("main");
                    String temp = main.getString("temp");
                    String temp_min = main.getString("temp_min");
                    String temp_max = main.getString("temp_max");
                    String pressure = main.getString("pressure");
                    String humidity = main.getString("humidity");

                    tv_city.setText(city);
                    tv_wind.setText("Wind Speed : " + wind_speed + (units.getString("unit","metric").equals("metric") ? " meter/sec" : " miles/sec"));
                    tv_temp.setText(temp);
                    tv_min_temp.setText("Minimum Temperature : "+temp_min + " " + un);
                    tv_max_temp.setText("Maximum Temperature : "+temp_max+ " " + un);
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



    @Override
    public void onResume() {
        super.onResume();
    }

    public void getActivityCall(){
        if(!units.getString("unit","").equals(cur_unit)){
            networkCall();
        }
    }
}
package com.sagar.weatherforecast.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
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
import com.sagar.weatherforecast.Adapter.AdapterReport;
import com.sagar.weatherforecast.Constants.ApiConstant;
import com.sagar.weatherforecast.Model.ModelReport;
import com.sagar.weatherforecast.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

public class Report extends Fragment {
    String LONGITUDE, LATITUDE;
    TextView tv_username;
    Context context;
    RelativeLayout progress_circular;
    SharedPreferences units;
    String cur_unit = "";
    AdapterReport adapter;
    RecyclerView rv_report;
    List<ModelReport> list = new ArrayList<>();
    public Report(String LATITUDE, String LONGITUDE){
        this.LONGITUDE = LONGITUDE;
        this.LATITUDE = LATITUDE;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_report, container, false);
        context = getContext();
        units = context.getSharedPreferences("units", MODE_PRIVATE);

        tv_username = view.findViewById(R.id.tv_username);
        progress_circular = view.findViewById(R.id.progress_circular);
        rv_report = view.findViewById(R.id.rv_report);
        networkCall();
        return view;
    }

    public void networkCall(){
        progress_circular.setVisibility(View.VISIBLE);
        tv_username.setText(units.getString("name", "USER 001"));
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        cur_unit = units.getString("unit","metric");
        DateFormat dateFormat = new SimpleDateFormat("dd");
//        tv_day.setText(new SimpleDateFormat("EE", Locale.ENGLISH).format(date.getTime()) + ", " + new SimpleDateFormat("MMM").format(calendar.getTime()) + " " + dateFormat.format(date) + "th" + " " + (date.getYear() + 1900));
//        // metric -> celsius
//        // imperial -> Fahrenheit
//        cur_unit = units.getString("unit","metric");
//        System.out.println("UNit : " + cur_unit);
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
                    if (jsonArray.length() > 1) {
                        list.clear();
                        for (int i = 1; i < jsonArray.length(); i++) {
                            JSONObject jsonObjectData = jsonArray.getJSONObject(i);
                            JSONObject main = jsonObjectData.getJSONObject("temp");
                            String temp = "Avg Temp : " + main.getString("day")  + " " + un;
                            String temp_min = "Min Temp : " + main.getString("min") + " " + un;;
                            String temp_max = "Max Temp : " + main.getString("max") + " " + un;;
                            String humidity = "Humidity : " + jsonObjectData.getString("humidity") + " %";
                            String wind_speed = "Wind Speed : " + jsonObjectData.getString("wind_speed") + (units.getString("unit","metric").equals("metric") ? " meter/sec" : " miles/sec");

                            Calendar calendar = Calendar.getInstance();
                            calendar.add(Calendar.DAY_OF_YEAR,i);
                            Date date = calendar.getTime();
                            DateFormat dateFormat = new SimpleDateFormat("dd");
                            String day = new SimpleDateFormat("EE", Locale.ENGLISH).format(date.getTime()) + ", " + new SimpleDateFormat("MMM").format(calendar.getTime()) + " " + dateFormat.format(date) + "th" + " " + (date.getYear() + 1900);

                            list.add(new ModelReport(temp, temp_min,temp_max,humidity,wind_speed,day));
                        }


                        adapter = new AdapterReport(list,context);
                        rv_report.setLayoutManager(new GridLayoutManager(context,1, GridLayoutManager.VERTICAL,false));
                        rv_report.setAdapter(adapter);
                        rv_report.setBackgroundColor(getResources().getColor(R.color.back_rv));
                    }
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

    public void getActivityCall(){
        if(!units.getString("unit","").equals(cur_unit)){
            networkCall();
        }
    }
}
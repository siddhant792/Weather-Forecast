package com.sagar.weatherforecast.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.text.Editable;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.sagar.weatherforecast.Fragment.Calender;
import com.sagar.weatherforecast.Fragment.Forecast;
import com.sagar.weatherforecast.Fragment.Report;
import com.sagar.weatherforecast.Fragment.Setting;
import com.sagar.weatherforecast.R;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    
    private BottomNavigationView bottom_nav;
    private FragmentManager fragmentManager;
    private FrameLayout frag;
    private Calender cal;
    private Forecast forecast;
    private Report report;
    private Setting setting = new Setting();
    int PERMISSION_ID = 10;
    Context context;
    SharedPreferences units;
    FusedLocationProviderClient mFusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        bottom_nav = findViewById(R.id.bottom_nav);
        frag = findViewById(R.id.frag);
        units = context.getSharedPreferences("units", MODE_PRIVATE);
        if(units.getString("name","").length() == 0){
            getUserName();
        }else{
            getLastLocation();
        }


        bottom_nav.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.item_current:
                    fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .show(forecast)
                            .hide(cal)
                            .hide(report)
                            .hide(setting)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .commit();
                    forecast.getActivityCall();
                    return true;
                case R.id.item_date:
                    fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .show(cal)
                            .hide(forecast)
                            .hide(report)
                            .hide(setting)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .commit();
                    return true;
                case R.id.item_report:
                    fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .show(report)
                            .hide(forecast)
                            .hide(cal)
                            .hide(setting)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .commit();
                    report.getActivityCall();
                    return true;
                case R.id.item_setting:
                    fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .show(setting)
                            .hide(forecast)
                            .hide(cal)
                            .hide(report)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .commit();
                    return true;
            }
            return false;
        });


    }

    public void requestPermissions() {
        ActivityCompat.requestPermissions((Activity) context, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
    }

    public boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void
    onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_ID) {
            getLastLocation();
        }
    }

    @SuppressLint("MissingPermission")
    public void getLastLocation() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        if(checkPermissions()){
            if (isLocationEnabled()) {
                mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        if (location == null) {
                            requestNewLocationData();
                        } else {
                            forecast = new Forecast(location.getLatitude() + "",location.getLongitude() + "");
                            cal = new Calender();
                            report = new Report(location.getLatitude() + "",location.getLongitude() + "");
                            fragmentManager = getSupportFragmentManager();
                            fragmentManager.beginTransaction()
                                    .add(R.id.frag,cal)
                                    .add(R.id.frag,forecast)
                                    .add(R.id.frag,report)
                                    .add(R.id.frag,setting)
                                    .show(forecast)
                                    .hide(cal)
                                    .hide(report)
                                    .hide(setting)
                                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                    .commit();
                        }
                    }
                });
            }else {
                Toast.makeText(context, "Please turn on" + " your location...", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                context.startActivity(intent);
            }
        }else {
            requestPermissions();
        }
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    private LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location location = locationResult.getLastLocation();
            System.out.println("SHHRFD  " + location.getLatitude() + " " + location.getLongitude());
            forecast = new Forecast(location.getLatitude() + "",location.getLongitude() + "");
            cal = new Calender();
            report = new Report(location.getLatitude() + "",location.getLongitude() + "");
            fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .add(R.id.frag,cal)
                    .add(R.id.frag,forecast)
                    .add(R.id.frag,report)
                    .add(R.id.frag,setting)
                    .show(forecast)
                    .hide(cal)
                    .hide(report)
                    .hide(setting)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit();
        }
    };

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private void getUserName(){
        final EditText edittext = new EditText(context);
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Please enter your name");

        alert.setView(edittext);

        alert.setPositiveButton("Save", (dialog, whichButton) -> {
            String dt = edittext.getText().toString();
            units.edit().putString("name",dt).apply();
            units.edit().putString("unit","metric").apply();
            getLastLocation();
        });
        alert.setCancelable(false);
        alert.show();
    }

}
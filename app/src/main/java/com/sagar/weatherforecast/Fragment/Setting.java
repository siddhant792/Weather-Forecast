package com.sagar.weatherforecast.Fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.sagar.weatherforecast.R;

import static android.content.Context.MODE_PRIVATE;

public class Setting extends Fragment {

    CardView cv_unit;
    TextView tv_units;
    Context context;
    SharedPreferences units;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        context = getContext();
        cv_unit = view.findViewById(R.id.cv_unit);
        tv_units = view.findViewById(R.id.tv_units);
        units = context.getSharedPreferences("units", MODE_PRIVATE);
        tv_units.setText(units.getString("unit","").equals("metric") ? "Celsius" : "Fahrenheit");
        cv_unit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog();
            }
        });
        return view;
    }

    private void showAlertDialog() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
        mBuilder.setTitle("Temperature Unit");
        String[] listItems = {"Celsius","Fahrenheit"};
        int checked = units.getString("unit","").equals("metric") ? 0 : 1;
        mBuilder.setSingleChoiceItems(listItems, checked, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(listItems[i].equals("Celsius")){
                    units.edit().putString("unit","metric").apply();
                    tv_units.setText(listItems[i]);
                }else{
                    units.edit().putString("unit","imperial").apply();
                    tv_units.setText(listItems[i]);
                }
                dialogInterface.dismiss();
            }
        });

        AlertDialog mDialog = mBuilder.create();
        mDialog.setCanceledOnTouchOutside(true);
        mDialog.show();
    }
}
package com.sagar.weatherforecast.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sagar.weatherforecast.Model.ModelReport;
import com.sagar.weatherforecast.R;

import java.util.List;

public class AdapterReport extends RecyclerView.Adapter<AdapterReport.Holder>{
    private List<ModelReport> list;
    private Context context;

    public AdapterReport(List<ModelReport> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_report,parent,false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.tv_day.setText(list.get(position).getDay());
        holder.tv_humidity.setText(list.get(position).getHumidity());
        holder.tv_temp.setText(list.get(position).getTemp());
        holder.tv_temp_max.setText(list.get(position).getTemp_max());
        holder.tv_temp_min.setText(list.get(position).getTemp_min());
        holder.tv_windspeed.setText(list.get(position).getWindspeed());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Holder extends RecyclerView.ViewHolder {

        TextView tv_temp,tv_temp_min,tv_temp_max,tv_humidity,tv_windspeed,tv_day;

        public Holder(@NonNull View itemView) {
            super(itemView);
            tv_temp = itemView.findViewById(R.id.tv_temp);
            tv_day = itemView.findViewById(R.id.tv_day);
            tv_temp_min = itemView.findViewById(R.id.tv_temp_min);
            tv_temp_max = itemView.findViewById(R.id.tv_temp_max);
            tv_humidity = itemView.findViewById(R.id.tv_humidity);
            tv_windspeed = itemView.findViewById(R.id.tv_windspeed);
        }
    }
}

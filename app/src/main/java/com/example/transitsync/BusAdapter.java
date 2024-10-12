package com.example.transitsync;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class BusAdapter extends RecyclerView.Adapter<BusAdapter.BusViewHolder> {

    private ArrayList<Bus> busList;
    private Context context;

    public BusAdapter(ArrayList<Bus> busList, Context context) {
        this.busList = busList;
        this.context = context;
    }

    @NonNull
    @Override
    public BusViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_bus_details, parent, false);
        return new BusViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BusViewHolder holder, int position) {
        Bus currentBus = busList.get(position);
        holder.busImage.setImageResource(currentBus.getImageResId());
        holder.busRouteName.setText(currentBus.getRouteName());
        holder.busNumber.setText(currentBus.getBusNumber());

        holder.busRouteName.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra("imageResId", currentBus.getImageResId());
            intent.putExtra("routeName", currentBus.getRouteName());
            intent.putExtra("busNumber", currentBus.getBusNumber());
            intent.putExtra("busTimings", currentBus.getBusTimings());
            intent.putExtra("distanceCovered", currentBus.getDistanceCovered());
            intent.putExtra("nearbyStops", currentBus.getNearbyStops());
            context.startActivity(intent);
        });

        holder.busImage.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra("imageResId", currentBus.getImageResId());
            intent.putExtra("routeName", currentBus.getRouteName());
            intent.putExtra("busNumber", currentBus.getBusNumber());
            intent.putExtra("busTimings", currentBus.getBusTimings());
            intent.putExtra("distanceCovered", currentBus.getDistanceCovered());
            intent.putExtra("nearbyStops", currentBus.getNearbyStops());
            context.startActivity(intent);
        });

        holder.busNumber.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra("imageResId", currentBus.getImageResId());
            intent.putExtra("routeName", currentBus.getRouteName());
            intent.putExtra("busNumber", currentBus.getBusNumber());
            intent.putExtra("busTimings", currentBus.getBusTimings());
            intent.putExtra("distanceCovered", currentBus.getDistanceCovered());
            intent.putExtra("nearbyStops", currentBus.getNearbyStops());
            context.startActivity(intent);
        });


    }

    @Override
    public int getItemCount() {
        return busList.size();
    }

    public static class BusViewHolder extends RecyclerView.ViewHolder {
        ImageView busImage;
        TextView busRouteName, busNumber;

        public BusViewHolder(@NonNull View itemView) {
            super(itemView);
            busImage = itemView.findViewById(R.id.bus_image);
            busRouteName = itemView.findViewById(R.id.bus_route_name);
            busNumber = itemView.findViewById(R.id.bus_number);
        }
    }
}
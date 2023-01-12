package com.example.poolrdriver.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.poolrdriver.R;
import com.example.poolrdriver.classes.other.Schedule;

import java.util.ArrayList;


public class MyScheduleAdapter extends RecyclerView.Adapter<MyScheduleAdapter.HolderView> {
    ArrayList<Schedule> schedules;
    Context mContext;

    public MyScheduleAdapter(ArrayList<Schedule> schedules, Context mContext) {
        this.schedules=schedules;
        this.mContext=mContext;
    }

    @NonNull
    @Override
    public HolderView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflating the schedules card
        LayoutInflater inflater=LayoutInflater.from(mContext);
        return new HolderView(inflater.inflate(R.layout.scheduled_trips, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull HolderView holder, int position) {
        //setting texts on views
        holder.destinations.setText(new StringBuilder().append(schedules.get(position).getSourceAddress()).append(" to ").append(schedules.get(position).getDestinationAddress()).toString());
        holder.timing.setText(new StringBuilder().append(schedules.get(position).getLeavingTime()).append(" to ").append(schedules.get(position).getDestinationTime()).toString());
    }

    @Override
    public int getItemCount() {
        return schedules.size();
    }

    public  class HolderView extends RecyclerView.ViewHolder {
        TextView destinations,timing;
        public HolderView(@NonNull View itemView) {
            super(itemView);

            //initializations
            destinations=itemView.findViewById(R.id.destination);
            timing=itemView.findViewById(R.id.txtNetworkName);
        }
    }
}

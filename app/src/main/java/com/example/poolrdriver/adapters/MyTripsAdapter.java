package com.example.poolrdriver.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.poolrdriver.My_trips_expanded;
import com.example.poolrdriver.R;
import com.example.poolrdriver.models.TripModel;


import java.util.List;

public class MyTripsAdapter extends RecyclerView.Adapter<MyTripsAdapter.holderView> {
    private List<TripModel> tripsList;
    private Context mContext;
    private Activity activity;
    public MyTripsAdapter(List<TripModel> tripsList, Context mContext, Activity activity) {
        this.tripsList=tripsList;
        this.mContext=mContext;
        this.activity=activity;
    }

    @NonNull
    @Override
    public holderView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(mContext);
        View view=inflater.inflate(R.layout.upcoming_trips_card_layout,parent,false);
        return new holderView(view);
    }

    @Override
    public void onBindViewHolder(@NonNull holderView holder, int position) {
        setViewsText(tripsList.get(position), holder);
        setListeners(holder,tripsList.get(position));
    }

    private void setListeners(holderView holder,TripModel tripModel) {


        holder.viewMore.setOnClickListener(v -> {

            Intent myTrips=new Intent(activity, My_trips_expanded.class);
            Log.d("tag", "setUpTripObject: "+tripModel.getTripID());
            myTrips.putExtra("chosen_trip",tripModel);
            activity.startActivity(myTrips);

        });
    }

    private void setViewsText(TripModel trip,holderView holder) {
        holder.seatsOffered.setText(String.valueOf(trip.getSeats()));
        holder.driverSource.setText(trip.getDriverSource());
        holder.driverDestination.setText(trip.getDriverDestination());
        holder.tripPrice.setText(String.valueOf(trip.getTripPrice()));

        if (!trip.isPrivacy())
            holder.tripPrivacy.setText("network");
        else
            holder.tripPrivacy.setText("Everyone");
    }

    @Override
    public int getItemCount() {
        return tripsList.size();
    }

    public class holderView extends RecyclerView.ViewHolder {
        TextView seatsOffered,driverDestination,driverSource,driverSourceTime,tripPrivacy,tripPrice;
        Button viewMore;
        public holderView(@NonNull View itemView) {
            super(itemView);
            setViews(itemView);
        }

        private void setViews(View itemView) {
            //declarations
            seatsOffered=itemView.findViewById(R.id.seats);
            driverSource=itemView.findViewById(R.id.TripSource);
            driverSourceTime=itemView.findViewById(R.id.TimeToDepart);
            driverDestination=itemView.findViewById(R.id.TripDestination);
            tripPrivacy=itemView.findViewById(R.id.privacy_setting);
            tripPrice=itemView.findViewById(R.id.CashPaid);
            viewMore=itemView.findViewById(R.id.btn_view_more);
        }

    }
}

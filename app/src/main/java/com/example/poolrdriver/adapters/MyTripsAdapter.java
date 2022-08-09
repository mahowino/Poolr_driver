package com.example.poolrdriver.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.poolrdriver.R;
import com.example.poolrdriver.classes.Trips;


import java.util.List;

public class MyTripsAdapter extends RecyclerView.Adapter<MyTripsAdapter.holderView> {
    private List<Trips> tripsList;
    private Context mContext;

    public MyTripsAdapter(List<Trips> tripsList, Context mContext) {
        this.tripsList=tripsList;
        this.mContext=mContext;
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
        setListeners(holder);
    }

    private void setListeners(holderView holder) {

        holder.actionButton.setOnClickListener(v -> {
            /*

             *Intent myTrips=new Intent(mContext, My_trips_expanded.class);
             *myTrips.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
             *mContext.startActivity(myTrips);

             */
        });
    }

    private void setViewsText(Trips trip,holderView holder) {
        holder.seatsOffered.setText(trip.getSeats());
        holder.driverSource.setText(trip.getDriverSource());
        holder.driverDestination.setText(trip.getDriverDestination());
        holder.tripPrice.setText(trip.getTripPrice());
        holder.tripPrivacy.setText(trip.getPrivacy());
    }

    @Override
    public int getItemCount() {
        return tripsList.size();
    }

    public class holderView extends RecyclerView.ViewHolder {
        TextView seatsOffered,driverDestination,driverSource,driverSourceTime,tripPrivacy,actionButton,tripPrice;
        public holderView(@NonNull View itemView) {
            super(itemView);
            setViews(itemView);
        }

        private void setViews(View itemView) {
            //declarations
            seatsOffered=itemView.findViewById(R.id.seats_booked);
            driverSource=itemView.findViewById(R.id.TripSource);
            driverSourceTime=itemView.findViewById(R.id.TimeToDepart);
            driverDestination=itemView.findViewById(R.id.TripDestination);
            tripPrivacy=itemView.findViewById(R.id.privacy_setting);
            tripPrice=itemView.findViewById(R.id.CashPaid);
            actionButton=itemView.findViewById(R.id.viewDetailsOnTrip);
        }

    }
}

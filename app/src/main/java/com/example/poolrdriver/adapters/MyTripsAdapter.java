package com.example.poolrdriver.adapters;

import android.annotation.SuppressLint;
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

import com.example.poolrdriver.ui.activities.other.My_trips_expanded;
import com.example.poolrdriver.R;
import com.example.poolrdriver.ui.activities.other.TripSummary;
import com.example.poolrdriver.classes.models.TimePickerObject;
import com.example.poolrdriver.classes.models.TripModel;


import java.util.List;

public class MyTripsAdapter extends RecyclerView.Adapter<MyTripsAdapter.holderView> {
    private List<TripModel> tripsList;
    private Context mContext;
    private Activity activity;
    private boolean isRoute;
    public MyTripsAdapter(List<TripModel> tripsList, Context mContext, Activity activity,boolean isRoute) {
        this.tripsList=tripsList;
        this.mContext=mContext;
        this.activity=activity;
        this.isRoute=isRoute;
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

            if (!isRoute){
                TimePickerObject object=tripModel.getTimePickerObject();
                Intent myTrips=new Intent(activity, My_trips_expanded.class);
                Log.d("tag", "setUpTripObject: "+tripModel.getTripID());
                myTrips.putExtra("chosen_trip",tripModel);
                myTrips.putExtra("trip_time",object);
                activity.startActivity(myTrips);
            }
            else {
                TimePickerObject object=tripModel.getTimePickerObject();
                Intent myTrips=new Intent(activity, TripSummary.class);
                Log.d("tag", "setUpTripObject: "+tripModel.getTripID());
                myTrips.putExtra("chosen_trip",tripModel);
                myTrips.putExtra("trip_time",object);
                activity.startActivity(myTrips);
            }


        });
    }

    @SuppressLint("SetTextI18n")
    private void setViewsText(TripModel trip, holderView holder) {
        holder.seatsOffered.setText(trip.getSeats()+" Seats");
        holder.driverSource.setText(trip.getDriverSource());
        holder.driverDestination.setText(trip.getDriverDestination());
        holder.tripPrice.setText("KSH " + trip.getTripPrice()+" per seat");
        TimePickerObject object=trip.getTimePickerObject();
        int day=object.getDay();
        int month=object.getMonth();
        int year=object.getYear();
        int hour=object.getHour();
        int minute= object.getMinute();
        holder.driverSourceTime.setText(day+"/"+month+"/"+year+" at "+hour+":"+minute+" hours");

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

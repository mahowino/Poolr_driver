package com.example.poolrdriver.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.poolrdriver.PassengerProfile;
import com.example.poolrdriver.R;
import com.example.poolrdriver.classes.Passenger;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class PassengersAdapter extends RecyclerView.Adapter<PassengersAdapter.HolderView> {
    Context context;
    List<Passenger> passengers;
    private final String PASSENGER="passenger";
    public PassengersAdapter(Context context, List<Passenger> passengers) {
        this.context=context;
        this.passengers=passengers;
    }

    @NonNull
    @Override
    public PassengersAdapter.HolderView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HolderView(LayoutInflater.from(context).inflate(R.layout.booked_passenger_card,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull PassengersAdapter.HolderView holder, int position) {
        Passenger passenger=passengers.get(position);
        setTexts(passenger,holder);
        setListener(passenger,holder);
    }

    private void setListener(Passenger passenger, HolderView holder) {
        holder.callPassenger.setOnClickListener(v -> callPassenger(passenger));
        holder.passengerCard.setOnClickListener(v -> openPassengerProfile(passenger));
    }

    private void openPassengerProfile(Passenger passenger) {
        Intent intent=new Intent(context.getApplicationContext(), PassengerProfile.class);
        intent.putExtra(PASSENGER,passenger);
        context.startActivity(intent);
    }

    private void callPassenger(Passenger passenger) {
        String number=passenger.getPhoneNumber();
        Intent callIntent = new Intent(Intent.ACTION_DIAL,Uri.parse("tel:"+number));
        context.startActivity(callIntent);
    }

    private void setTexts(Passenger passenger,HolderView holderView) {
        holderView.passengerName.setText(passenger.getNames());
        holderView.passengerRating.setText(String.valueOf(passenger.getRating()));
        Glide.with(context).load(passenger.getProfilePic()).into(holderView.passengerDisplayPicture);
    }

    @Override
    public int getItemCount() {
        return passengers.size();
    }

    public class HolderView extends RecyclerView.ViewHolder {
        TextView passengerName,passengerRating,passengerSource,passengerDestination;
        ImageView passengerDisplayPicture;
        FloatingActionButton callPassenger;
        CardView passengerCard;
        public HolderView(@NonNull View itemView) {
            super(itemView);
            initializeViews(itemView);
        }
        private void initializeViews(View itemView) {
            passengerName=itemView.findViewById(R.id.passenger_name);
            passengerRating=itemView.findViewById(R.id.passenger_rating);
            passengerSource=itemView.findViewById(R.id.PassengerTripSource);
            passengerDestination=itemView.findViewById(R.id.PassengerTripDestination);
            passengerDisplayPicture=itemView.findViewById(R.id.passenger_DP);
            callPassenger=itemView.findViewById(R.id.btn_call_passenger);
            passengerCard=itemView.findViewById(R.id.call_card);
        }
    }


}

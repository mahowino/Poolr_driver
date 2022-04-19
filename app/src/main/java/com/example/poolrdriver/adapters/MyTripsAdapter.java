package com.example.poolrdriver.adapters;

import android.content.Context;
import android.content.Intent;
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
        //setting texts on views
        //setup of views
        holder.driverName.setText(tripsList.get(position).getDriverName());
        holder.driverSource.setText(tripsList.get(position).getRouteSource());
        holder.driverDestination.setText(tripsList.get(position).getRouteDestination());

        holder.actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*

               *Intent myTrips=new Intent(mContext, My_trips_expanded.class);
               *myTrips.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
               *mContext.startActivity(myTrips);

                 */

            }
        });



    }

    @Override
    public int getItemCount() {
        return tripsList.size();
    }

    public class holderView extends RecyclerView.ViewHolder {
        TextView driverName,driverDestination,driverSource,driverSourceTime,driverDestionationTime,actionButton,tripPrice;
        public holderView(@NonNull View itemView) {
            super(itemView);

            //declarations
            driverName=itemView.findViewById(R.id.TripDriverName_myTrips);
            driverSource=itemView.findViewById(R.id.TripSource);
            driverSourceTime=itemView.findViewById(R.id.TimeToDepart);
            driverDestination=itemView.findViewById(R.id.TripDestination);
            driverDestionationTime=itemView.findViewById(R.id.TimeToArrive);
            tripPrice=itemView.findViewById(R.id.CashPaid);
            actionButton=itemView.findViewById(R.id.viewDetailsOnTrip);

        }
    }
}

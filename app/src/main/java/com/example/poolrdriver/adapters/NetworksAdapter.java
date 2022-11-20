package com.example.poolrdriver.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.poolrdriver.NetworkMainPage;
import com.example.poolrdriver.NetworkTrips;
import com.example.poolrdriver.R;
import com.example.poolrdriver.models.Network;

import java.util.List;

public class NetworksAdapter extends RecyclerView.Adapter<NetworksAdapter.HolderView> {
    List<Network> networks;
    Context mContext;
    Activity activity;

    public NetworksAdapter(List<Network> networks, Context mContext, Activity activity) {
        this.networks=networks;
        this.mContext=mContext;
        this.activity=activity;
    }

    @NonNull
    @Override
    public NetworksAdapter.HolderView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflating the schedules card
        LayoutInflater inflater=LayoutInflater.from(mContext);
        return new NetworksAdapter.HolderView(inflater.inflate(R.layout.scheduled_trips, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull NetworksAdapter.HolderView holder, int position) {
        //setting texts on views
        holder.network_name.setText(networks.get(position).getNetworkName());
        holder.networkCard.setOnClickListener(v -> openNetworkTrips(networks.get(position)));

    }

    private void openNetworkTrips(Network network) {
        Intent intent=new Intent(mContext, NetworkMainPage.class);
        intent.putExtra("Network",network);
        activity.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return networks.size();
    }

    public  class HolderView extends RecyclerView.ViewHolder {
        TextView network_name,timing;
        CardView networkCard;
        public HolderView(@NonNull View itemView) {
            super(itemView);

            //initializations

            network_name=itemView.findViewById(R.id.txtNetworkName);
            networkCard=itemView.findViewById(R.id.card_network);
        }
    }
}

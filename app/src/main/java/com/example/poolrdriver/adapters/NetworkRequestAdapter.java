package com.example.poolrdriver.adapters;

import static com.example.poolrdriver.ui.activities.other.NetworkUserProfile.NETWORK_FROM;
import static com.example.poolrdriver.ui.activities.other.NetworkUserProfile.NETWORK_PASSENGER;
import static com.example.poolrdriver.classes.other.Passenger.getProfilePicture;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.poolrdriver.Abstract.Callback;
import com.example.poolrdriver.ui.activities.other.NetworkUserProfile;
import com.example.poolrdriver.R;
import com.example.poolrdriver.classes.other.Passenger;
import com.example.poolrdriver.classes.models.Network;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import java.util.List;

public class NetworkRequestAdapter extends RecyclerView.Adapter<NetworkRequestAdapter.HolderView> {
    List<Passenger> passengers;
    Context mContext;
    Activity activity;
    Network network;

    public NetworkRequestAdapter(List<Passenger> passengers, Network network, Context mContext, Activity activity) {
        this.passengers = passengers;
        this.mContext = mContext;
        this.activity=activity;
        this.network=network;   }

    @NonNull
    @Override
    public HolderView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HolderView(LayoutInflater.from(mContext).inflate(R.layout.network_member_card,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull HolderView holder, int position) {
    holder.names.setText(passengers.get(position).getNames());

    if (passengers.get(position).isNetworkAdmin())
        holder.viewUser.setVisibility(View.INVISIBLE);

        getProfilePicture(passengers.get(position).getUsername(),new Callback() {
            @Override
            public void onSuccess(Object object) {
                Uri uri=(Uri)object;
                Picasso.with(mContext)
                        .load(uri)
                        .into(holder.profilePic);
            }

            @Override
            public void onError(Object object) {

            }
        });

        holder.viewUser.setOnClickListener(v -> redirect(activity, NetworkUserProfile.class,passengers.get(position)));

    }

    private void redirect(Activity activity, Class<NetworkUserProfile> networkUserProfileClass,Passenger passenger) {
        Intent intent=new Intent(activity,networkUserProfileClass);
        intent.putExtra(NETWORK_PASSENGER,passenger);
        intent.putExtra(NETWORK_FROM,network);
        activity.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return passengers.size();
    }

    public class HolderView extends RecyclerView.ViewHolder {
        ImageView profilePic;
        TextView names;
        FloatingActionButton viewUser;

        public HolderView(@NonNull View itemView) {
            super(itemView);
            profilePic=itemView.findViewById(R.id.networkMemberProfilePic);
            names=itemView.findViewById(R.id.networkMemberName);
            viewUser=itemView.findViewById(R.id.btnViewNetworkMember);
        }
    }
}

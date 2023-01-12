package com.example.poolrdriver.ui.activities.other;

import static com.example.poolrdriver.ui.activities.other.My_trips.FROM_NETWORK;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.poolrdriver.Helpers.NetworksHelper;
import com.example.poolrdriver.R;
import com.example.poolrdriver.adapters.NetworkRequestAdapter;
import com.example.poolrdriver.classes.other.Passenger;
import com.example.poolrdriver.classes.models.Network;

import java.util.List;

public class NetworkViewMembers extends AppCompatActivity {
    RecyclerView memberList;

    Network network;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_view_members);

        initializeData();
        getNetworkMembers();
    }

    private void getNetworkMembers() {
        NetworksHelper.getNetworkMembers(network, this::setUpAdapter);
    }

    private void setUpAdapter(List<Passenger> passengers) {
        NetworkRequestAdapter adapter=new NetworkRequestAdapter(passengers,network,this,NetworkViewMembers.this);
        memberList.setAdapter(adapter);
        memberList.setLayoutManager(new LinearLayoutManager(this,RecyclerView.VERTICAL,false));
    }

    private void initializeData() {
        memberList=findViewById(R.id.recyclerViewMembers);
        network=getIntent().getParcelableExtra(FROM_NETWORK);
    }
}
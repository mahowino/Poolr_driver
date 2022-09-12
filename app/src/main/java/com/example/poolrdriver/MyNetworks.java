package com.example.poolrdriver;

import static com.example.poolrdriver.Firebase.FirebaseRepository.createCollectionReference;
import static com.example.poolrdriver.Firebase.FirebaseRepository.createDocumentReference;
import static com.example.poolrdriver.Firebase.FirebaseRepository.getDocumentsInCollection;
import static com.example.poolrdriver.util.AppSystem.displayError;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.poolrdriver.Firebase.Callback;
import com.example.poolrdriver.Firebase.FirebaseConstants;
import com.example.poolrdriver.Firebase.FirebaseFields;
import com.example.poolrdriver.Firebase.User;
import com.example.poolrdriver.adapters.NetworksAdapter;
import com.example.poolrdriver.classes.Network;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MyNetworks extends AppCompatActivity {

    RecyclerView recyclerView;
    String path;
    User user;
    List<Network> networks;
    ImageView noNetworks;
    TextView txtNoNetworks;
    boolean isNetworksThere;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_networks);
        initializeVariables();
    }
    private void initializeVariables() {
        recyclerView=findViewById(R.id.schedulesRecyclerView);
        user=new User();
        isNetworksThere=false;
        path= FirebaseConstants.PASSENGERS+"/"+user.getUID()+"/"+FirebaseConstants.NETWORKS;
        getNetworks(path);
        noNetworks=findViewById(R.id.img_no_networks);
        txtNoNetworks=findViewById(R.id.txt_no_networks);
        recyclerView=findViewById(R.id.recyclerViewNetworks);

    }
    private void diplayAdapter() {
        noNetworks.setVisibility(View.INVISIBLE);
        txtNoNetworks.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.VISIBLE);

        NetworksAdapter adapter=new NetworksAdapter(networks,getApplicationContext(),MyNetworks.this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL,false));

    }
    private void displayNoNetworks(){
        noNetworks.setVisibility(View.VISIBLE);
        txtNoNetworks.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.INVISIBLE);
    }

    private void getNetworks(String path) {
        networks=new ArrayList<>();

        getDocumentsInCollection(createCollectionReference(path), new Callback() {
            @Override
            public void onSuccess(Object object) {displaySchedule(((Task<QuerySnapshot>)object));}
            @Override
            public void onError(Object object) {displayError(MyNetworks.this, getApplicationContext(),(Exception)object);}
        });
    }

    private void displaySchedule(Task<QuerySnapshot> task) {

        for (DocumentSnapshot snapshot:task.getResult())
        {
            Network network=new Network();
            network.setNetworkName(String.valueOf(snapshot.get(FirebaseFields.NETWORK_NAME)));
            network.setNetworkUID(String.valueOf(snapshot.get(FirebaseFields.NETWORK_UID)));
            network.setNetworkTravelAdminUID(String.valueOf(snapshot.get(FirebaseFields.NETWORK_TRAVEL_ADMIN)));
            networks.add(network);
            isNetworksThere=true;

        }
        if (isNetworksThere)
            diplayAdapter();
        else
            displayNoNetworks();
    }
}
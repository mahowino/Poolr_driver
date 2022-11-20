package com.example.poolrdriver;

import static com.example.poolrdriver.util.AppSystem.redirectActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class NetworksActivity extends AppCompatActivity {

    Button createNetwork;
    TextView joinNetwork;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_networks);

        initializeViews();
        setListeners();
    }

    private void setListeners() {
        createNetwork.setOnClickListener(v -> redirectActivity(NetworksActivity.this,NetworkInformationScreen.class));
        joinNetwork.setOnClickListener(v -> redirectActivity(NetworksActivity.this,JoinNetworkActivity.class));
    }

    private void initializeViews() {
        createNetwork=findViewById(R.id.btn_CreateNetwork);
        joinNetwork=findViewById(R.id.txtJoinNetwork);
    }
}
package com.example.poolrdriver;

import static com.example.poolrdriver.NetworkInformationScreen.NETWORK_CODE;
import static com.example.poolrdriver.util.AppSystem.redirectActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.poolrdriver.util.AppSystem;

public class CreateNetworkSuccess extends AppCompatActivity {
    String code;
    TextView txtCode;
    Button finish;
    ImageView share;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_network_success);

        initializeData();
        setTexts();
        setListeners();

    }

    private void setListeners() {
        share.setOnClickListener(v -> AppSystem.shareCode(code,CreateNetworkSuccess.this));
        finish.setOnClickListener(v -> redirectActivity(CreateNetworkSuccess.this,MapsActivity.class));
    }

    private void setTexts() {
        txtCode.setText(code);
    }

    private void initializeData() {
        txtCode=findViewById(R.id.txtCodeNetwork);
        code=getIntent().getExtras().getString(NETWORK_CODE);
        share=findViewById(R.id.imgShareBtn);
        finish=findViewById(R.id.btnFinishNetworkCreation);
    }
}
package com.example.poolrdriver.ui.activities.networks;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.example.poolrdriver.Abstract.Callback;
import com.example.poolrdriver.Helpers.NetworksHelper;
import com.example.poolrdriver.R;

public class JoinNetworkActivity extends AppCompatActivity {

    AutoCompleteTextView txtCode;
    Button joinNetworkBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_network);

        initializeViews();
        setListeners();

    }

    private void setListeners() {
        joinNetworkBtn.setOnClickListener(v -> requestJoinToNetwork());
    }

    private void requestJoinToNetwork() {
        String networkCode=txtCode.getText().toString();
        NetworksHelper.joinNetworkUsingCode(networkCode, new Callback() {
            @Override
            public void onSuccess(Object object) {
                //joined successfully
                Toast.makeText(JoinNetworkActivity.this, "joined ", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Object object) {

                //error in joinint
            }
        });
    }

    private void initializeViews() {
        txtCode=findViewById(R.id.txtNetworkCode);
        joinNetworkBtn=findViewById(R.id.btnJoinNetwork);
    }
}
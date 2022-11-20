package com.example.poolrdriver;

import static com.example.poolrdriver.Firebase.GoogleMaps.getLocationFromAddress;
import static com.example.poolrdriver.util.AppSystem.redirectActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.poolrdriver.Firebase.Callback;
import com.example.poolrdriver.Firebase.User;
import com.example.poolrdriver.Helpers.NetworksHelper;
import com.example.poolrdriver.adapters.AutoSuggestionsAdapter;
import com.example.poolrdriver.models.Network;
import com.example.poolrdriver.util.AppSystem;
import com.example.poolrdriver.util.LoadingDialog;
import com.google.android.gms.maps.model.LatLng;

public class NetworkInformationScreen extends AppCompatActivity {

    AutoCompleteTextView networkName,networkHomeLocation,networkWorkLocation;
    Spinner networkRules;
    Button createNetwork;
    private LatLng sourcePoint,destinationPoint;
    LoadingDialog loadingDialog;
    public static String NETWORK_CODE="networkCode";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_information_screen);

        initializeData();
        setListeners();
        setAdapters();
    }

    private void setAdapters() {
        networkHomeLocation.setAdapter(new AutoSuggestionsAdapter(getApplicationContext(), android.R.layout.simple_list_item_1));
        networkWorkLocation.setAdapter(new AutoSuggestionsAdapter(getApplicationContext(), android.R.layout.simple_list_item_1));
    }

    private void setListeners() {
        createNetwork.setOnClickListener(v -> validateInput());
    }


    private void initializeData() {
        networkName=findViewById(R.id.txtNetworkName);
        networkHomeLocation=findViewById(R.id.txtNetworkLocationFrom);
        networkWorkLocation=findViewById(R.id.txtNetworkLocationTo);
        networkRules=findViewById(R.id.spinnerNetworkRules);
        createNetwork=findViewById(R.id.btnCreateNetworkFinish);
        loadingDialog=new LoadingDialog(this);
    }
    private void validateInput() {
        loadingDialog.startLoadingAlertDialog();
        setLatLngValues();
        String code=AppSystem.getRandomString(6);

        checkIfCodeExists(code, new Callback() {
            @Override
            public void onSuccess(Object object) {
                Network network=new Network();
                network.setNetworkName(networkName.getText().toString());
                network.setNetworkTravelAdminUID(new User().getUID());
                network.setHomeLocation(sourcePoint);
                network.setWorkLocation(destinationPoint);
                network.setNetworkCode(code);
                if (networkRules.getSelectedItemPosition()==0)
                    network.setNetworkAcceptOnCode(true);
                else network.setNetworkAcceptOnCode(false);
                createNetworkInDb(network);
            }

            @Override
            public void onError(Object object) {

                loadingDialog.dismissDialog();
            }
        });

    }

    private void createNetworkInDb(Network network) {
        NetworksHelper.createNetworkInDatabase(network, new Callback() {
            @Override
            public void onSuccess(Object object) {
                loadingDialog.dismissDialog();

                Intent intent=new Intent(getApplicationContext(),CreateNetworkSuccess.class);
                intent.putExtra(NETWORK_CODE,network.getNetworkCode());
                startActivity(intent);

            }

            @Override
            public void onError(Object object) {

            }
        });
    }

    private void checkIfCodeExists(String code,Callback callback) {
        NetworksHelper.searchNetworkByCode(code, new Callback() {
            @Override
            public void onSuccess(Object object) {
                //document exists hence a failure
                callback.onError(object);
            }

            @Override
            public void onError(Object object) {
                //document does not exist unless a db failure.
                String message=(String)object;
                if (message==null) callback.onSuccess(object);
                else callback.onError(object);
            }
        });
    }

    private void setLatLngValues() {
        try {

            sourcePoint = getLocationFromAddress(networkHomeLocation.getText().toString(), getApplicationContext());
            destinationPoint = getLocationFromAddress(networkWorkLocation.getText().toString(), getApplicationContext());
        }  catch (Exception e){

            e.printStackTrace();
            Toast.makeText(this, "write a valid address", Toast.LENGTH_SHORT).show();
            loadingDialog.dismissDialog();
        }
    }

}
package com.example.poolrdriver;

import static com.example.poolrdriver.Firebase.FirebaseRepository.createCollectionReference;
import static com.example.poolrdriver.Firebase.FirebaseRepository.getDocumentsInCollection;

import static com.example.poolrdriver.util.AppSystem.redirectActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.example.poolrdriver.Firebase.Callback;
import com.example.poolrdriver.Firebase.FirebaseConstants;
import com.example.poolrdriver.Firebase.FirebaseFields;
import com.example.poolrdriver.Firebase.User;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


public class fragment_where_to extends Fragment {
    private EditText whereTo;
    private ConstraintLayout wallet_view;
    private FloatingActionButton requests, wallet, trips;
    TextView amount;

    public fragment_where_to() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
            View view=inflater.inflate(R.layout.fragment_where_to, container, false);
            initializeVariables(view);
            getWalletData();
            setListeners();
        // Inflate the layout for this fragment




        return view;
    }
    private void getWalletData(){
        String path= FirebaseConstants.PASSENGERS+"/"+new User().getUID()+ "/"+FirebaseConstants.DRIVER_WALLET;
        getDocumentsInCollection(createCollectionReference(path), new Callback() {
            @Override
            public void onSuccess(Object object) {

                Task<QuerySnapshot> task=(Task<QuerySnapshot>) object;
                for (DocumentSnapshot snapshot:task.getResult()){
                    String cash= String.valueOf(snapshot.get(FirebaseFields.CASH));
                    amount.setText(String.format("KSH %s", cash));

                }
            }

            @Override
            public void onError(Object object) {
                Log.d(TAG.TAG, "onFailure: failure "+((Exception)object).getMessage());}
        });
    }


    private void setListeners() {
        whereTo.setOnClickListener(v -> {
            //todo:get date of travel
            redirectActivity(getActivity(),onLocationPressedActivity.class);
            requireActivity().overridePendingTransition( R.anim.slide_in_up, R.anim.slide_out_up );
        });

        wallet_view.setOnClickListener(v -> redirectActivity(getActivity(),wallet.class));

        requests.setOnClickListener(v -> redirectActivity(getActivity(),NotificationsActivity.class));

        wallet.setOnClickListener(v -> redirectActivity(getActivity(),wallet.class));

        trips.setOnClickListener(v -> redirectActivity(getActivity(),My_trips.class));
    }

    private void initializeVariables(View view) {

        //initializations
        requests =  view.findViewById(R.id.requests);
        wallet = view.findViewById(R.id.wallet);
        trips =  view.findViewById(R.id.trips);
        whereTo= view.findViewById(R.id.location_to_editText);
        wallet_view= view.findViewById(R.id.profile);
        amount=view.findViewById(R.id.amount_in_wallet);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }


}
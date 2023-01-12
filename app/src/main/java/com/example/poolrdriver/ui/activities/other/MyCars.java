package com.example.poolrdriver.ui.activities.other;

import static com.example.poolrdriver.Firebase.FirebaseRepository.createCollectionReference;
import static com.example.poolrdriver.Firebase.FirebaseRepository.getDocumentsInCollection;
import static com.example.poolrdriver.ui.activities.other.onLocationPressedActivity.CAR_FOR_TRIP;
import static com.example.poolrdriver.util.AppSystem.displayError;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.poolrdriver.Abstract.Callback;
import com.example.poolrdriver.Abstract.FirebaseConstants;
import com.example.poolrdriver.Abstract.Constants.FirebaseFields;
import com.example.poolrdriver.Firebase.User;
import com.example.poolrdriver.R;
import com.example.poolrdriver.adapters.CarTypeAdapter;
import com.example.poolrdriver.classes.models.CarTypes;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class MyCars extends AppCompatActivity {
    RecyclerView recyclerView;
    User user;
    boolean isCarThere;
    String path;
    ImageView noCars;
    TextView txtNoCars;
    ArrayList<CarTypes> cars;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_cars);
        initializeVariables();
    }
    private void initializeVariables() {
        recyclerView=findViewById(R.id.schedulesRecyclerView);
        user=new User();
        isCarThere=false;
        path= FirebaseConstants.DRIVERS+"/"+user.getUID()+"/"+FirebaseConstants.CARS;
        getNetworks(path);
        noCars=findViewById(R.id.img_no_cars);
        txtNoCars=findViewById(R.id.txtNoCars);
        recyclerView=findViewById(R.id.recyclerViewCars);

    }
    private void diplayAdapter() {
        noCars.setVisibility(View.INVISIBLE);
        txtNoCars.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.VISIBLE);

        CarTypeAdapter adapter=new CarTypeAdapter(getApplicationContext(), cars, position -> {
            Intent intent=new Intent(getApplicationContext(),MyRoutes.class);
            intent.putExtra(CAR_FOR_TRIP,cars.get(position));
            startActivity(intent);
            overridePendingTransition( R.anim.slide_in_up, R.anim.slide_out_up );
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL,false));

    }
    private void displayNoNetworks(){
        noCars.setVisibility(View.VISIBLE);
        txtNoCars.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.INVISIBLE);
    }

    private void getNetworks(String path) {
        cars=new ArrayList<>();

        getDocumentsInCollection(createCollectionReference(path), new Callback() {
            @Override
            public void onSuccess(Object object) {displaySchedule(((Task<QuerySnapshot>)object));}
            @Override
            public void onError(Object object) {displayError(MyCars.this, getApplicationContext(),(Exception)object);}
        });
    }

    private void displaySchedule(Task<QuerySnapshot> task) {

        for (DocumentSnapshot snapshot:task.getResult()){

            if (snapshot.exists()){


                CarTypes carTypes=new CarTypes(snapshot.getString(FirebaseFields.MAKE));
                carTypes.setNumberplate(snapshot.getString(FirebaseFields.NUMBER_PLATE));
                carTypes.setColor(snapshot.getString(FirebaseFields.COLOR));
                carTypes.setModel(snapshot.getString(FirebaseFields.MODEL));
                carTypes.setYear(snapshot.getString(FirebaseFields.CAR_YEAR));


                isCarThere=true;
                cars.add(carTypes);
            }
        }

        if (isCarThere)
            diplayAdapter();
        else
            displayNoNetworks();
    }

}
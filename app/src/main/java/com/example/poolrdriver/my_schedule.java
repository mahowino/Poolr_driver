package com.example.poolrdriver;

import static com.example.poolrdriver.Firebase.FirebaseRepository.createDocumentReference;
import static com.example.poolrdriver.Firebase.FirebaseRepository.getDocument;
import static com.example.poolrdriver.util.AppSystem.displayError;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.poolrdriver.Firebase.Callback;
import com.example.poolrdriver.Firebase.Constants.FirebaseConstants;
import com.example.poolrdriver.Firebase.Constants.FirebaseFields;
import com.example.poolrdriver.Firebase.User;
import com.example.poolrdriver.adapters.MyScheduleAdapter;
import com.example.poolrdriver.classes.Schedule;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Objects;


public class my_schedule extends Fragment {
    User user;
    String path;
    ArrayList<Schedule> schedules;
    private Context mContext;
    RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_my_schedule, container, false);
        initializeVariables(view);
        getSchedule(path);

        return view;
    }

    private void diplayAdapter() {
        MyScheduleAdapter adapter=new MyScheduleAdapter(schedules,mContext);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false));

    }

    private void getSchedule(String path) {
        schedules=new ArrayList<>();

        getDocument(createDocumentReference(path), new Callback() {
            @Override
            public void onSuccess(Object object) {displaySchedule(((Task<DocumentSnapshot>)object));}
            @Override
            public void onError(Object object) {displayError(getActivity(), getContext(),(Exception)object);}
        });
    }

    private void displaySchedule(Task<DocumentSnapshot> task) {

        DocumentSnapshot snapshot=task.getResult();
            schedules.add(new Schedule(
                    Objects.requireNonNull(String.valueOf(snapshot.get(FirebaseFields.HOME_ADRESS))),
                    Objects.requireNonNull(String.valueOf(snapshot.get(FirebaseFields.WORK_ADRESS))),
                    "7:30 am",
                    "8:30 am"
            ));

        schedules.add(new Schedule(
                Objects.requireNonNull(String.valueOf(snapshot.get(FirebaseFields.WORK_ADRESS))),
                Objects.requireNonNull(String.valueOf(snapshot.get(FirebaseFields.HOME_ADRESS))),
                "7:30 am",
                "8:30 am"
        ));
        diplayAdapter();
    }

    private void initializeVariables(View view) {
        recyclerView=view.findViewById(R.id.schedulesRecyclerView);
        user=new User();
        mContext=getContext();
        path= FirebaseConstants.PASSENGERS+"/"+user.getUID()+"/";
    }
}
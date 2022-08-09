package com.example.poolrdriver;

import static com.example.poolrdriver.Firebase.FirebaseRepository.createCollectionReference;
import static com.example.poolrdriver.Firebase.FirebaseRepository.getDocumentsInCollection;


import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.poolrdriver.Firebase.Callback;
import com.example.poolrdriver.Firebase.FirebaseConstants;
import com.example.poolrdriver.Firebase.FirebaseFields;
import com.example.poolrdriver.Firebase.User;
import com.example.poolrdriver.adapters.NotificationsAdapter;
import com.example.poolrdriver.classes.Notifications;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class NotificationsActivity extends AppCompatActivity {

    RecyclerView notificationsView;
    private List<Notifications> notifications;
    ImageView no_notifications_image;
    TextView no_notification_text;
    boolean is_notification_available;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests);
        initializeVariables();
        getNotificationFromFirebase();
    }

    private void initializeVariables() {
        //initialization
        notifications=new ArrayList<>();
        notificationsView=findViewById(R.id.notifications_recycler);
        no_notifications_image=findViewById(R.id.no_notification_image);
        no_notification_text=findViewById(R.id.no_notification_text);
        is_notification_available=false;
    }



    private void getNotificationFromFirebase() {
        String path=FirebaseConstants.PASSENGERS+"/"+new User().getUID()+"/"+FirebaseConstants.REQUESTS;

        getDocumentsInCollection(createCollectionReference(path), new Callback() {
            @Override
            public void onSuccess(Object object) {
                Task<QuerySnapshot> task=(Task<QuerySnapshot>)object;
                for (DocumentSnapshot snapshot:task.getResult())
                    if (snapshot.exists()){createNotification(snapshot);is_notification_available=true;}

                if (is_notification_available)initializeRecyclerView();
                else initializeNoNotificationView();

            }

            @Override
            public void onError(Object object) {
                Log.d(TAG.TAG, "onFailure: "+((Exception)object).getMessage());
                Toast.makeText(getApplicationContext(),"error getting notifications",Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void initializeNoNotificationView() {
        no_notifications_image.setVisibility(View.VISIBLE);
        no_notification_text.setVisibility(View.VISIBLE);
        notificationsView.setVisibility(View.INVISIBLE);
    }

    private void createNotification(DocumentSnapshot snapshot) {
        String message=snapshot.getString(FirebaseFields.MESSAGE);
        int type=Integer.parseInt((String) snapshot.get(FirebaseFields.TYPE));
        Notifications notification=new Notifications(message,type);

        //if notification requires picture, get from database
        if (snapshot.getString(FirebaseFields.IMAGE_URI)!=null)
            notification.setImageUri(Uri.parse(snapshot.getString(FirebaseFields.IMAGE_URI)));

        notifications.add(notification);
    }

    private void initializeRecyclerView() {
        no_notifications_image.setVisibility(View.INVISIBLE);
        no_notification_text.setVisibility(View.INVISIBLE);
        notificationsView.setVisibility(View.VISIBLE);

        //RecyclerView initializations
        NotificationsAdapter adapter=new NotificationsAdapter(getApplicationContext(),notifications);
        notificationsView.setAdapter(adapter);
        notificationsView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    }
}
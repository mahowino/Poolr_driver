package com.example.poolrdriver;

import static com.example.poolrdriver.SignUpScreen.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.poolrdriver.Firebase.FirebaseConstants;
import com.example.poolrdriver.Firebase.FirebaseFields;
import com.example.poolrdriver.classes.LoadingDialog;
import com.example.poolrdriver.classes.SignedUpDriver;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class wallet extends AppCompatActivity {
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private TextView amount,withdraw,driverName;
    private String driverUid;
    private ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);
        db=FirebaseFirestore.getInstance();
        mAuth=FirebaseAuth.getInstance();
        amount=(TextView) findViewById(R.id.cash_in_wallet);
        withdraw=(TextView)findViewById(R.id.action_withdraw_money);
        driverName=(TextView)findViewById(R.id.wallet_user_name);
        back=(ImageView)findViewById(R.id.imageViewBack);

        driverName.setText(SignedUpDriver.getNames());

        db.collection(FirebaseConstants.DRIVERS)
                .document(mAuth.getCurrentUser().getUid())
                .collection(FirebaseConstants.WALLET)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()){
                            for (DocumentSnapshot snapshot:task.getResult()){
                                String amount_in_wallet=snapshot.get(FirebaseFields.CASH).toString();
                                String accountCode=snapshot.getId();
                                amount.setText("KSH "+amount_in_wallet);
                                //set up details on wallet


                            }
                        }
                        else {

                            Toast.makeText(getApplicationContext(),"Error in getting wallet data",Toast.LENGTH_LONG).show();
                            Intent intent=new Intent(getApplicationContext(),HomePageActivity.class);
                            startActivity(intent);
                            finish();

                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: failure "+e.getMessage());
                    }
                });

        withdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                db.collection(FirebaseConstants.DRIVER_WITHDRAWALS)
                        .whereEqualTo("user_uid",mAuth.getUid())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                if (task.isSuccessful()){
                                    //active withdrawal already being made
                                    Toast.makeText(getApplicationContext(),"active withdrawal request alreasy made",Toast.LENGTH_LONG).show();

                                }
                                else {

                                    new AlertDialog.Builder(getApplicationContext())
                                            .setTitle("Withdraw request")
                                            .setMessage("Are you sure you want to send a withdrawal request")

                                            // Specifying a listener allows you to take an action before dismissing the dialog.
                                            // The dialog is automatically dismissed when a dialog button is clicked.

                                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {

                                                    LoadingDialog dialog1=new LoadingDialog(getParent());
                                                    dialog1.startLoadingAlertDialog();


                                                    Map<String,Object> withdrawalRequestsMap=new HashMap<>();
                                                    withdrawalRequestsMap.put("email",mAuth.getCurrentUser().getEmail());
                                                    withdrawalRequestsMap.put("user_uid",mAuth.getUid());
                                                    withdrawalRequestsMap.put("name",SignedUpDriver.getNames());
                                                    withdrawalRequestsMap.put("phoneNumber",SignedUpDriver.getPhoneNumber());
                                                    withdrawalRequestsMap.put("initializationDate",new Date());


                                                    db.collection(FirebaseConstants.DRIVER_WITHDRAWALS)
                                                            .document()
                                                            .set(withdrawalRequestsMap)
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    dialog1.dismissDialog();

                                                                    Toast.makeText(getApplicationContext(),"Withdrawal request received, wait for update in less than 10 minutes",Toast.LENGTH_LONG).show();
                                                                    Intent intent=new Intent(getApplicationContext(),HomePageActivity.class);
                                                                    startActivity(intent);
                                                                    finish();

                                                                }
                                                            })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    Log.d(TAG, "onFailure: "+e.getMessage());
                                                                    dialog1.dismissDialog();
                                                                }
                                                            });
                                                }
                                            })

                                            // A null listener allows the button to dismiss the dialog and take no further action.
                                            .setNegativeButton(android.R.string.no, null)
                                            .setIcon(android.R.drawable.ic_dialog_alert)
                                            .show();
                                }
                            }
                        });

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



    }

}
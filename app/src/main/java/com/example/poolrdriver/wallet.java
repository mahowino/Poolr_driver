package com.example.poolrdriver;

import static com.example.poolrdriver.Firebase.FirebaseRepository.createCollectionReference;
import static com.example.poolrdriver.Firebase.FirebaseRepository.createDocumentReference;
import static com.example.poolrdriver.Firebase.FirebaseRepository.createQuery;
import static com.example.poolrdriver.Firebase.FirebaseRepository.getDocument;
import static com.example.poolrdriver.Firebase.FirebaseRepository.getDocumentsFromQueryInCollection;
import static com.example.poolrdriver.Firebase.FirebaseRepository.getDocumentsInCollection;
import static com.example.poolrdriver.Firebase.FirebaseRepository.setDocument;

import static com.example.poolrdriver.util.AppSystem.redirectActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.poolrdriver.Firebase.Callback;
import com.example.poolrdriver.Firebase.FirebaseConstants;
import com.example.poolrdriver.Firebase.FirebaseFields;
import com.example.poolrdriver.Firebase.User;
import com.example.poolrdriver.classes.SignedUpDriver;
import com.example.poolrdriver.util.LoadingDialog;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class wallet extends AppCompatActivity {
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private TextView amount;
    private TextView withdraw,driverName;
    private ImageView back;
    Double wallet_balance=0.00;
    private Double withdrawal_allowed_amount=1000.00;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);
        initializeVariables();
        getWalletData();
        setListeners();

    }

    private void  initializeVariables(){
        db=FirebaseFirestore.getInstance();
        mAuth=FirebaseAuth.getInstance();
        amount=findViewById(R.id.cash_in_wallet);
        withdraw=findViewById(R.id.action_withdraw_money);
        driverName =  findViewById(R.id.wallet_user_name);
        back=findViewById(R.id.imageViewBack);

        driverName.setText(new User().getName());

    }

    private void getWalletData(){
        String path=FirebaseConstants.PASSENGERS+"/"+new User().getUID()+ "/"+FirebaseConstants.DRIVER_WALLET;
        getDocumentsInCollection(createCollectionReference(path), new Callback() {
            @Override
            public void onSuccess(Object object) {

                Task<QuerySnapshot> task=(Task<QuerySnapshot>) object;
                for (DocumentSnapshot snapshot:task.getResult()){
                    String cash= String.valueOf(snapshot.get(FirebaseFields.CASH));
                    wallet_balance=Double.valueOf(cash);
                    amount.setText(String.format("KSH %s", cash));

                }
            }

            @Override
            public void onError(Object object) {
                Log.d(TAG.TAG, "onFailure: failure "+((Exception)object).getMessage());}
        });
    }

    private void setListeners() {


        withdraw.setOnClickListener(v -> { processWithdrawal();});

        back.setOnClickListener(v -> finish());


    }

    private void processWithdrawal() {
        String path=FirebaseConstants.DRIVER_WITHDRAWALS;

        getDocumentsFromQueryInCollection(createQuery(createCollectionReference(path), "user_uid",
                new User().getUID()), new Callback() {
                    @Override
                    public void onSuccess(Object object) {
                        //checkIfWithdrawalExists
                        boolean doesWithdrawalRequestExist=false;
                        Task<QuerySnapshot> snapshotTask=(Task<QuerySnapshot>) object;

                        for (DocumentSnapshot snapshot:snapshotTask.getResult())
                            if (snapshot.exists()){doesWithdrawalRequestExist=true;break;}

                        if (doesWithdrawalRequestExist)Toast.makeText(getApplicationContext(),"You have a pending withdrawal request",Toast.LENGTH_LONG).show();
                        else checkWalletBalance();
                    }

                    @Override
                    public void onError(Object object) {}
                });
    }

    private void checkWalletBalance() {
        if (wallet_balance>withdrawal_allowed_amount )requestWithdrawal();
        else Toast.makeText(getApplicationContext(),"You need a balance of "+withdrawal_allowed_amount+" to request withdrawal",Toast.LENGTH_LONG).show();

    }

    private void requestWithdrawal() {

        new AlertDialog.Builder(getApplicationContext())
                .setTitle("Withdraw request")
                .setMessage("Are you sure you want to send a withdrawal request")

                .setPositiveButton(android.R.string.yes, (dialog, which) -> {

                    LoadingDialog dialog1=new LoadingDialog(getParent());
                    dialog1.startLoadingAlertDialog();
                    sendWithdrawalRequestToDatabase(dialog1);

                })

                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void sendWithdrawalRequestToDatabase(LoadingDialog dialog) {
        setDocument(createWithdrawalRequest(), createDocumentReference(FirebaseConstants.DRIVER_WITHDRAWALS), new Callback() {
            @Override
            public void onSuccess(Object object) {

                dialog.dismissDialog();
                Toast.makeText(getApplicationContext(),"Withdrawal request received, wait for update ",Toast.LENGTH_LONG).show();
                redirectActivity(wallet.this,HomePageActivity.class);finish();
            }

            @Override
            public void onError(Object object) {
                Log.d(TAG.TAG, "onFailure: "+((Exception)object).getMessage());dialog.dismissDialog();
            }
        });
    }

    private Map createWithdrawalRequest() {
        Map<String,Object> withdrawalRequestsMap=new HashMap<>();

        withdrawalRequestsMap.put("email",mAuth.getCurrentUser().getEmail());
        withdrawalRequestsMap.put("user_uid",mAuth.getUid());
        withdrawalRequestsMap.put("name",SignedUpDriver.getNames());
        withdrawalRequestsMap.put("phoneNumber",SignedUpDriver.getPhoneNumber());
        withdrawalRequestsMap.put("initializationDate",new Date());

        return withdrawalRequestsMap;
    }


}
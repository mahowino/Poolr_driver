package com.example.poolrdriver;

import static com.example.poolrdriver.Firebase.FirebaseRepository.createCollectionReference;
import static com.example.poolrdriver.Firebase.FirebaseRepository.createDocumentReference;
import static com.example.poolrdriver.Firebase.FirebaseRepository.createQuery;
import static com.example.poolrdriver.Firebase.FirebaseRepository.getDocumentsFromQueryInCollection;
import static com.example.poolrdriver.Firebase.FirebaseRepository.getDocumentsInCollection;
import static com.example.poolrdriver.Firebase.FirebaseRepository.setDocument;

import static com.example.poolrdriver.util.AppSystem.redirectActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.poolrdriver.Firebase.Callback;
import com.example.poolrdriver.Firebase.FirebaseConstants;
import com.example.poolrdriver.Firebase.FirebaseFields;
import com.example.poolrdriver.Firebase.User;
import com.example.poolrdriver.classes.Notifications;
import com.example.poolrdriver.classes.SignedUpDriver;
import com.example.poolrdriver.models.Requests;
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
    private Button addPromo;
    private EditText promoEditText;
    private TextView withdraw,driverName;
    private ImageView back;
    private String walletUid;
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
        
        addPromo=findViewById(R.id.btn_ApplyPromotion);
        promoEditText=findViewById(R.id.editTextPromoCode);

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
        
        addPromo.setOnClickListener(v -> getPromo());
    }

    private void getPromo() {
        String path=FirebaseConstants.PROMOTIONS;
        validatePromoCode(path,getPromoCode());

    }

    private void validatePromoCode(String paths, String promoCode) {
        getDocumentsFromQueryInCollection(
                createQuery(
                        createCollectionReference(paths),
                        FirebaseFields.PROMOTION_CODE,promoCode), new Callback() {
                    @Override
                    public void onSuccess(Object object) {
                        Task<QuerySnapshot> task=(Task<QuerySnapshot>)object;
                        boolean promoExists=false;
                        boolean isPromoUsed=true;
                        String promoID="";
                        double amount_to_give=0.0;
                        for (DocumentSnapshot snapshot:task.getResult()){
                            promoExists=true;
                            isPromoUsed= !Boolean.FALSE.equals(snapshot.getBoolean(FirebaseFields.IS_PROMOTION_USED));
                            promoID=snapshot.getId();
                            amount_to_give=snapshot.getDouble(FirebaseFields.PROMOTION_AMOUNT);
                        }

                        if (promoExists && !isPromoUsed){
                            double finalAmount_to_give = amount_to_give;
                            String finalPromoID = promoID;
                            new android.app.AlertDialog.Builder(wallet.this)
                                    .setTitle("Confirmation")
                                    .setIcon(R.drawable.icons8_bus_ticket_20px)
                                    .setMessage("Are you sure you would like to redeem "+amount_to_give+" Ksh to your wallet?")
                                    .setPositiveButton("yes", (dialog1, which) -> {
                                        topUpCash(finalAmount_to_give);
                                        updatePromoCode(finalPromoID, finalAmount_to_give,promoCode);
                                        leavePromotionFootprint(finalAmount_to_give,promoCode);
                                        createNotification();
                                        Toast.makeText(getApplicationContext(), "Promo code successfully input", Toast.LENGTH_SHORT).show();
                                    })
                                    .setNegativeButton("no",(dialog1,which)->{

                                    })
                                    .show();
                        }
                        else if (promoExists)
                            Toast.makeText(getApplicationContext(), "Promo code has been used before", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(getApplicationContext(), "Write a valid promo code", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Object object) {
                        Toast.makeText(getApplicationContext(), "Write a valid promo code", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private Map<String, Object> createNotification() {
        Map<String,Object> map=new HashMap<>();
        map.put(FirebaseFields.MESSAGE,"you have successfully redeemed a promo code");
        map.put(FirebaseFields.TYPE,1);
        map.put(FirebaseFields.TITLE,"promo code redeemed");
        return map;
    }

    private void updatePromoCode(String promoID,double amount_updated,String promoCode) {
        String path=FirebaseConstants.PROMOTIONS +"/"+promoID ;
        setDocument(updatePromotion(amount_updated,promoCode), createDocumentReference(path), new Callback() {
            @Override
            public void onSuccess(Object object) {


            }

            @Override
            public void onError(Object object) {

            }
        });
    }

    private void leavePromotionFootprint(double amount_updated,String promoCode) {
        String path=FirebaseConstants.PASSENGERS +"/"+new User().getUID()+ "/"+FirebaseConstants.PROMOTIONS+"/";
        setDocument(createPromotionFootprint(amount_updated,promoCode), createCollectionReference(path), new Callback() {
            @Override
            public void onSuccess(Object object) {


            }

            @Override
            public void onError(Object object) {

            }
        });
    }

    private Map createPromotionFootprint(double amount_updated,String promoCode) {
        Map<String,Object> promotionFootprint=new HashMap<>();

        promotionFootprint.put(FirebaseFields.PROMOTION_CODE,promoCode);
        promotionFootprint.put(FirebaseFields.PROMOTION_AMOUNT,amount_updated);
        promotionFootprint.put(FirebaseFields.UPDATE_TIME,new Date());
        return promotionFootprint;
    }
    private Map updatePromotion(double amount_updated,String promoCode) {
        Map<String,Object> promotionFootprint=new HashMap<>();

        promotionFootprint.put(FirebaseFields.PROMOTION_CODE,promoCode);
        promotionFootprint.put(FirebaseFields.PROMOTION_AMOUNT,amount_updated);
        promotionFootprint.put(FirebaseFields.IS_PROMOTION_USED,true);
        return promotionFootprint;
    }
    private void showNotification(Requests requests, Notifications notification ) {
        String path=FirebaseConstants.PASSENGERS+"/"+requests.getPassengerUID()+"/"+FirebaseConstants.NOTIFICATIONS;
        setDocument(createNotification(), createCollectionReference(path), new Callback() {
            @Override
            public void onSuccess(Object object) {


            }

            @Override
            public void onError(Object object) {

            }
        });
    }



    private void topUpCash(double amount_to_give) {
        String path=FirebaseConstants.PASSENGERS+"/"+new User().getUID()+ "/"+FirebaseConstants.DRIVER_WALLET+"/"+walletUid;
        setDocument(createWallet(amount_to_give+wallet_balance), createDocumentReference(path), new Callback() {
            @Override
            public void onSuccess(Object object) {

            }

            @Override
            public void onError(Object object) {

            }
        });
    }

    private Map createWallet(double amountToUpdate) {
        Map<String,Object> wallet_update_object=new HashMap<>();

        wallet_update_object.put(FirebaseFields.CASH,amountToUpdate);
        wallet_update_object.put(FirebaseFields.UPDATE_TIME,new Date());
        return wallet_update_object;

    }

    private String getPromoCode() {
        String promoCodeInput=promoEditText.getText().toString().trim();
        return promoCodeInput;
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
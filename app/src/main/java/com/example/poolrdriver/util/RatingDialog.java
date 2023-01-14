package com.example.poolrdriver.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.poolrdriver.Abstract.Callback;
import com.example.poolrdriver.Abstract.FirebaseConstants;
import com.example.poolrdriver.Abstract.Constants.FirebaseFields;
import com.example.poolrdriver.Firebase.FirebaseRepository;
import com.example.poolrdriver.Firebase.User;
import com.example.poolrdriver.R;
import com.example.poolrdriver.classes.other.Passenger;
import com.example.poolrdriver.classes.other.Reviews;
import com.example.poolrdriver.ui.activities.other.MapsActivity;
import com.example.poolrdriver.ui.activities.other.OngoingTrip;

import java.util.HashMap;
import java.util.Map;

public class RatingDialog {
    Activity activity;
    AlertDialog dialog;
    TextView name;
    RatingBar ratingBar;
    EditText ratingDescription;
    Passenger passenger;
    String tripId;
    float rating;
    Button submitRating;
    CheckBox instantBookListCheckBox;
    boolean isTripDone;

    public RatingDialog(Activity activity, Passenger passenger, String tripID,boolean isTripDone) {
        this.tripId=tripID;
        this.passenger=passenger;
        this.activity = activity;
        this.isTripDone=isTripDone;
        rating=0;
    }
    public void startRatingAlertDialog(){

        AlertDialog.Builder builder=new AlertDialog.Builder(activity);
        LayoutInflater inflater=activity.getLayoutInflater();
        View view=inflater.inflate(R.layout.rating_dialog,null);

        builder.setView(setViewTexts(view,passenger.getNames(),tripId));
        setListeners();
        builder.setCancelable(true);

        dialog=builder.create();
        dialog.show();
    }

    private void setListeners() {
        ratingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> RatingDialog.this.rating=rating);

        submitRating.setOnClickListener(v->postRating());
    }

    private void postRating() {
        if (instantBookListCheckBox.isChecked())
            addPassengerToDriverList();
        Reviews reviews=new Reviews();
        reviews.setReview(ratingDescription.getText().toString());
        reviews.setRating(rating);
        reviews.setReviewerName(new User().getName());
        String path= FirebaseConstants.PASSENGERS+"/"+passenger.getUsername()+"/"+FirebaseConstants.REVIEWS;
        FirebaseRepository.setDocument(setReview(reviews), FirebaseRepository.createCollectionReference(path), new Callback() {
            @Override
            public void onSuccess(Object object) {
                Toast.makeText(activity, "rating posted", Toast.LENGTH_SHORT).show();
                dismissDialog();

                if (isTripDone){
                    Toast.makeText(activity, "Trip successfully ended", Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(activity, MapsActivity.class);
                    activity.startActivity(intent);
                    activity.finish();
                }
            }

            @Override
            public void onError(Object object) {

            }
        });

    }

    private void addPassengerToDriverList() {
        String path= FirebaseConstants.PASSENGERS+"/"+new User().getUID()+"/"+FirebaseConstants.INSTANT_BOOK_LIST+"/"+passenger.getUsername();
        String path2= FirebaseConstants.PASSENGERS+"/"+passenger.getUsername()+"/"+FirebaseConstants.INSTANT_BOOK_LIST+"/"+new User().getUID();
        FirebaseRepository.setDocument(setDriverList(), FirebaseRepository.createDocumentReference(path), new Callback() {
            @Override
            public void onSuccess(Object object) {

            }

            @Override
            public void onError(Object object) {
                Exception e=(Exception) object;
                Toast.makeText(activity, "error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        FirebaseRepository.setDocument(setDriverList(), FirebaseRepository.createDocumentReference(path2), new Callback() {
            @Override
            public void onSuccess(Object object) {

            }

            @Override
            public void onError(Object object) {
                Exception e=(Exception) object;
                Toast.makeText(activity, "error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private View setViewTexts(View view, String passengerName, String tripId) {
        name=view.findViewById(R.id.txtPassengerNameRatingCard);
        ratingBar=view.findViewById(R.id.ratingBarPassenger);
        ratingDescription=view.findViewById(R.id.ratingDescriptionBar);
        submitRating=view.findViewById(R.id.btnSubmitRating);
        instantBookListCheckBox=view.findViewById(R.id.chkBxInstantBookList);
        name.setText(passengerName);
        ratingBar.setRating(0);
        return view;
    }

    public void dismissDialog(){
        //UPLOAD RATING

        dialog.dismiss();
    }
    private Map<String,Object> setReview(Reviews reviews){
        Map<String,Object> map=new HashMap<>();
        map.put(FirebaseFields.REVIEWER_NAME,reviews.getReviewerName());
        map.put(FirebaseFields.REVIEW_DESCRIPTION,reviews.getReview());
        map.put(FirebaseFields.REVIEW_RATING,reviews.getRating());
        map.put(FirebaseFields.REVIEWER_UID,new User().getUID());
        return map;

    }

    private Map<String,Object> setDriverList(){
        Map<String,Object> map=new HashMap<>();
        map.put(FirebaseFields.FULL_NAMES,passenger.getNames());
        return map;

    }
}

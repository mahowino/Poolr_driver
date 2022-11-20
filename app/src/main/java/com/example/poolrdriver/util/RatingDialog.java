package com.example.poolrdriver.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.poolrdriver.Firebase.Callback;
import com.example.poolrdriver.Firebase.Constants.FirebaseConstants;
import com.example.poolrdriver.Firebase.Constants.FirebaseFields;
import com.example.poolrdriver.Firebase.FirebaseRepository;
import com.example.poolrdriver.Firebase.User;
import com.example.poolrdriver.R;
import com.example.poolrdriver.classes.Passenger;
import com.example.poolrdriver.classes.Reviews;

import java.util.HashMap;
import java.util.Map;

public class RatingDialog {
    Activity activity;
    AlertDialog dialog;
    TextView name;
    RatingBar ratingBar;
    EditText ratingDescription;
    Passenger passenger;
    String tripId,userDescription;
    float rating;
    Button submitRating;
    public RatingDialog(Activity activity, Passenger passenger, String tripID) {
        this.tripId=tripID;
        this.passenger=passenger;
        this.activity = activity;
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
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
               RatingDialog.this.rating=rating;
            }
        });

        submitRating.setOnClickListener(v->postRating());
    }

    private void postRating() {
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
            }

            @Override
            public void onError(Object object) {

            }
        });

    }

    private View setViewTexts(View view, String passengerName, String tripId) {
        name=view.findViewById(R.id.txtPassengerNameRatingCard);
        ratingBar=view.findViewById(R.id.ratingBarPassenger);
        ratingDescription=view.findViewById(R.id.ratingDescriptionBar);
        submitRating=view.findViewById(R.id.btnSubmitRating);

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
}

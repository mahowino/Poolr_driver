package com.example.poolrdriver.ui.activities.other;

import static android.content.ContentValues.TAG;

import static com.example.poolrdriver.util.AppSystem.redirectActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.poolrdriver.Abstract.Callback;
import com.example.poolrdriver.Abstract.FirebaseConstants;
import com.example.poolrdriver.Abstract.Constants.FirebaseFields;
import com.example.poolrdriver.Firebase.FirebaseRepository;
import com.example.poolrdriver.Firebase.User;
import com.example.poolrdriver.R;
import com.example.poolrdriver.ui.activities.userRegistrationJourney.AddBio;
import com.example.poolrdriver.ui.activities.userRegistrationJourney.ChangeProfilePicture;
import com.example.poolrdriver.ui.activities.userRegistrationJourney.HomeAndWorkAdress;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class MyProfile extends AppCompatActivity {
    ImageView profilepicture;
    User user;

    private static final int PICK_IMAGE = 100;
    TextView names,email,phoneNumber,homeAdress,workAdress,bio,editBio;

    @Override
    public void onStart() {
        super.onStart();
        setProfilePicture();
    }

    @Override
    public void onResume() {
        super.onResume();
        setProfilePicture();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        initializeVariables();
        updateUserData();
        setOnclickListeners();


    }

    private void setOnclickListeners() {
        profilepicture.setOnClickListener(v -> redirectActivity(MyProfile.this, ChangeProfilePicture.class));
        homeAdress.setOnClickListener(v -> redirectActivity(MyProfile.this, HomeAndWorkAdress.class));
        workAdress.setOnClickListener(v -> redirectActivity(MyProfile.this,HomeAndWorkAdress.class));
        editBio.setOnClickListener(v -> redirectActivity(MyProfile.this, AddBio.class));
    }

    private void updateUserData() {
        names.setText(user.getName());

        email.setText(user.getEmail());
        getHomeAndWorkAddressFromFirebase();
        bio.setVisibility(View.VISIBLE);
        bio.setText(user.getBio());
        phoneNumber.setText(user.getPhoneNumber());
        setProfilePicture();
    }

    private void setProfilePicture() {
        if(user.getProfilePic()!=null){

        Picasso.with(this).load(user.getProfilePic())
                .resize(100, 100)
                .into(profilepicture, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        Bitmap imageBitmap = ((BitmapDrawable) profilepicture.getDrawable()).getBitmap();
                        RoundedBitmapDrawable imageDrawable = RoundedBitmapDrawableFactory.create(getResources(), imageBitmap);
                        imageDrawable.setCircular(true);
                        imageDrawable.setCornerRadius(Math.max(imageBitmap.getWidth(), imageBitmap.getHeight()) / 2.0f);
                        profilepicture.setImageDrawable(imageDrawable);
                    }

                    @Override
                    public void onError() {
                        profilepicture.setImageResource(R.drawable.user_male);
                    }

                });
    }}

    private void initializeVariables() {
        //declarations
        user=new User();
        profilepicture = findViewById(R.id.imgDriverProfilePicture);
        names= findViewById(R.id.full_names_profile_page);
        email= findViewById(R.id.email_profile_page);
        bio= findViewById(R.id.txtBioProfile);
        homeAdress= findViewById(R.id.home_location_profile_page);
        workAdress= findViewById(R.id.work_location_profile_page);
        phoneNumber= findViewById(R.id.phone_number_profile_page);
        editBio=findViewById(R.id.txtBtnEditBio);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE){
            Uri imageUri = data.getData();
            Bitmap bitmap ;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                profilepicture.setImageBitmap(bitmap);
                handleUpload(bitmap);
            } catch (IOException e) {e.printStackTrace();}

        }
    }

    private void handleUpload(Bitmap bitmap) {

        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);

        //firebase storage setup and initialization
        StorageReference firebaseStorage= FirebaseStorage.getInstance().getReference()
                .child("profileImages")
                .child(user.getUID()+".jpeg");

        firebaseStorage.putBytes(byteArrayOutputStream.toByteArray())
                .addOnSuccessListener(taskSnapshot -> getDownloadUrl(firebaseStorage))
                .addOnFailureListener(e -> Log.e(TAG, "onFailure: ",e.getCause()));


    }
    private void getDownloadUrl(StorageReference reference){
        reference.getDownloadUrl()
                .addOnSuccessListener(uri -> {
                    Log.d(TAG, "onSuccess: "+uri);setUserProfileUrl(uri);})
                .addOnFailureListener(e -> Log.e(TAG, "onFailure: ",e.getCause()));
    }

    private void setUserProfileUrl(Uri uri) {user.setProfilePic(uri);}
    private void getHomeAndWorkAddressFromFirebase() {
        String path= FirebaseConstants.PASSENGERS+"/"+user.getUID();
        FirebaseRepository.getDocument(FirebaseRepository.createDocumentReference(path), new Callback() {
            @Override
            public void onSuccess(Object object) {

                Task<DocumentSnapshot> task = (Task<DocumentSnapshot>) object;


                DocumentSnapshot snapshot = task.getResult();
                if (snapshot.exists()) setUIForAddresses(snapshot);
                else setUiForNoAddresses();

            }

            @Override
            public void onError(Object object) {
                //Toast.makeText(activity.getApplicationContext(),((Exception)object).getMessage(),Toast.LENGTH_SHORT).show();}
            }});
    }
    private void setUiForNoAddresses() { }

    private void setUIForAddresses(DocumentSnapshot snapshot) {
        homeAdress.setText(snapshot.getString(FirebaseFields.HOME_ADRESS));
        workAdress.setText(snapshot.getString(FirebaseFields.WORK_ADRESS));
        bio.setText(snapshot.getString(FirebaseFields.BIO));
    }

}
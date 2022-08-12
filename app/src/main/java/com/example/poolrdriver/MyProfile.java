package com.example.poolrdriver;

import static android.content.ContentValues.TAG;

import static com.example.poolrdriver.util.AppSystem.openGallery;
import static com.example.poolrdriver.util.AppSystem.redirectActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.poolrdriver.Firebase.Callback;
import com.example.poolrdriver.Firebase.FirebaseConstants;
import com.example.poolrdriver.Firebase.FirebaseFields;
import com.example.poolrdriver.Firebase.FirebaseRepository;
import com.example.poolrdriver.Firebase.User;
import com.example.poolrdriver.userRegistrationJourney.HomeAndWorkAdress;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class MyProfile extends AppCompatActivity {
    ImageView profilepicture;
    User user;

    private static final int PICK_IMAGE = 100;
    TextView names,email,phoneNumber,homeAdress,workAdress,bio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        initializeVariables();
        updateUserData();
        setOnclickListeners();


    }

    private void setOnclickListeners() {
        profilepicture.setOnClickListener(v -> openGallery(MyProfile.this));
        homeAdress.setOnClickListener(v -> redirectActivity(MyProfile.this, HomeAndWorkAdress.class));
        workAdress.setOnClickListener(v -> redirectActivity(MyProfile.this,HomeAndWorkAdress.class));
    }

    private void updateUserData() {
        names.setText(user.getName());
        bio.setText(user.getBio());
        email.setText(user.getEmail());
        getHomeAndWorkAddressFromFirebase();
        phoneNumber.setText(user.getPhoneNumber());
        setProfilePicture();
    }

    private void setProfilePicture() {if(user.getProfilePic()!=null){
        Glide.with(this).load(user.getProfilePic()).into(profilepicture);}}

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
    }

}
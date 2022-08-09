package com.example.poolrdriver.classes;

import static com.example.poolrdriver.Firebase.FirebaseRepository.*;

import android.hardware.lights.LightsManager;
import android.net.Uri;

import com.example.poolrdriver.Firebase.Callback;
import com.example.poolrdriver.Firebase.FirebaseConstants;
import com.example.poolrdriver.Firebase.FirebaseFields;
import com.example.poolrdriver.Firebase.FirebaseRepository;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class Passenger {
    private  String username;
    private Uri profilePic;
    private ArrayList<Reviews> reviews;
    private double rating;
    DocumentSnapshot snapshot;
    QuerySnapshot reviewSnapshot;
    StorageReference reference;
    String userDetailsPath,reviewsPath;


     public Uri getProfilePic() {return profilePic;}

     public void setProfilePic(Uri profilePic) {
         this.profilePic = profilePic;
     }

    public double getRating() {return Double.parseDouble(String.valueOf(snapshot.get(FirebaseFields.RATING)));}

    public Passenger(String username) {
         initializeVariables();
         getUserInformation();
     }
     public Passenger(DocumentSnapshot snapshot){
         this.snapshot=snapshot;
         username=String.valueOf(snapshot.get(FirebaseFields.UID));
         initializeVariables();
         getReviewsFromFirebase();
         getProfilePictureFromFirestore();
     }

    private void getUserInformation() {
         getSnapshot();
         getReviewsFromFirebase();
         getProfilePictureFromFirestore();

    }

    private void getSnapshot() {
        getDocument(createDocumentReference(userDetailsPath), new Callback() {
            @Override
            public void onSuccess(Object object) { snapshot=(DocumentSnapshot) object;}
            @Override
            public void onError(Object object) {}
        });

    }

    private void getProfilePictureFromFirestore() {
        FirebaseRepository.getPictureFromFirebaseStorage(reference, new Callback() {
            @Override
            public void onSuccess(Object object) {profilePic=(Uri)object; }

            @Override
            public void onError(Object object) {}
        });

    }

    private void getReviewsFromFirebase() {
        FirebaseRepository.getDocumentsInCollection(createCollectionReference(reviewsPath), new Callback() {
            @Override
            public void onSuccess(Object object) {reviewSnapshot=(QuerySnapshot) object;}

            @Override
            public void onError(Object object) {}
        });
    }

    private void initializeVariables() {
        userDetailsPath= FirebaseConstants.PASSENGERS+"/"+username+"/";
        reviewsPath=FirebaseConstants.PASSENGERS+"/"+username+"/"+FirebaseFields.REVIEWS;
        //firebase storage setup and initialization
        reference= FirebaseStorage.getInstance().getReference()
                .child("profileImages")
                .child(username+".jpeg");

    }

    public List<Reviews> getReviews() {
         List<DocumentSnapshot> reviewSnapshotDocuments =reviewSnapshot.getDocuments();
         List<Reviews> reviews=new ArrayList<>();
         for (DocumentSnapshot snapshot:reviewSnapshotDocuments){
          reviews.add(snapshot.toObject(Reviews.class));
         }
        return reviews;
    }

    public void setReviews(ArrayList<Reviews> reviews) {
        this.reviews = reviews;
    }

    public  String getNames() {return String.valueOf(snapshot.get(FirebaseFields.FULL_NAMES));}

    public  String getEmail() {return String.valueOf(snapshot.get(FirebaseFields.EMAIL));}

    public  String getPhoneNumber() {return String.valueOf(snapshot.get(FirebaseFields.PHONE_NUMBER));}

}

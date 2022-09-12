package com.example.poolrdriver.classes;

import static com.example.poolrdriver.Firebase.FirebaseRepository.*;

import android.hardware.lights.LightsManager;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.example.poolrdriver.Firebase.Callback;
import com.example.poolrdriver.Firebase.FirebaseConstants;
import com.example.poolrdriver.Firebase.FirebaseFields;
import com.example.poolrdriver.Firebase.FirebaseRepository;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class Passenger implements Parcelable {
    private  String username;
    private Uri profilePic;
    private ArrayList<Reviews> reviews;
    private double rating;
    DocumentSnapshot snapshot;
    Task<QuerySnapshot> reviewSnapshot;
    StorageReference reference;
    String userDetailsPath,reviewsPath;


    public String getUsername() {
        return username;
    }

    protected Passenger(Parcel in) {
        username = in.readString();
        profilePic = in.readParcelable(Uri.class.getClassLoader());
        rating = in.readDouble();
        userDetailsPath = in.readString();
        reviewsPath = in.readString();
    }

    public static final Creator<Passenger> CREATOR = new Creator<Passenger>() {
        @Override
        public Passenger createFromParcel(Parcel in) {
            return new Passenger(in);
        }

        @Override
        public Passenger[] newArray(int size) {
            return new Passenger[size];
        }
    };

    public Uri getProfilePic() {return profilePic;}

     public void setProfilePic(Uri profilePic) {
         this.profilePic = profilePic;
     }

    public double getRating() {return 5.00;
        //return Double.parseDouble(String.valueOf(snapshot.get(FirebaseFields.RATING)));
    }

    public Passenger(String username) {
         initializeVariables();
         getUserInformation();
     }
     public Passenger(DocumentSnapshot snapshot){
         this.snapshot=snapshot;
         username=snapshot.getId();
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
            public void onSuccess(Object object) {reviewSnapshot=(Task<QuerySnapshot>) object;}

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
         List<DocumentSnapshot> reviewSnapshotDocuments =reviewSnapshot.getResult().getDocuments();
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(username);
        dest.writeParcelable(profilePic, flags);
        dest.writeDouble(rating);
        dest.writeString(userDetailsPath);
        dest.writeString(reviewsPath);
    }

    public String getBio() {return String.valueOf(snapshot.get(FirebaseFields.BIO));}
}

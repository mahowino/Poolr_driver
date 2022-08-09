package com.example.poolrdriver.classes;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.poolrdriver.Firebase.FirebaseFields;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.type.LatLng;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;

public class Trips implements Parcelable {

    private int tripPrice,seats;
    private ArrayList<Passenger> passengers;
    private ArrayList<Requests> requests;
    private String driverSource,driverDestination,driverUid,privacy;
    private ArrayList<LatLng> driverRoute;
    private Date date;
    DocumentSnapshot snapshot;

    public Trips(DocumentSnapshot snapshot) {
     this.snapshot=snapshot;
    }

    protected Trips(Parcel in) {
        tripPrice = in.readInt();
        seats = in.readInt();
        driverSource = in.readString();
        driverDestination = in.readString();
        driverUid = in.readString();
        privacy = in.readString();
    }

    public Date getDate() {
        return snapshot.getDate(FirebaseFields.DEPARTURETIME);
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public static final Creator<Trips> CREATOR = new Creator<Trips>() {
        @Override
        public Trips createFromParcel(Parcel in) {
            return new Trips(in);
        }

        @Override
        public Trips[] newArray(int size) {
            return new Trips[size];
        }
    };

    public String getPrivacy() {
        return String.valueOf(snapshot.get(FirebaseFields.PRIVACY));
    }

    public void setPrivacy(String privacy) {
        this.privacy = privacy;
    }

    public int getSeats() {
        return seats;
    }

    public void setSeats(int seats) {
        this.seats = seats;
    }

    public int getTripPrice() {
        return tripPrice;
    }

    public void setTripPrice(int tripPrice) {
        this.tripPrice = tripPrice;
    }

    public ArrayList<Passenger> getPassengers() {
        return passengers;
    }
    public int getNumberOfPassengers(){return passengers.size();}
    public int getNumberOfRequests(){return requests.size();}

    public void setPassengers(ArrayList<Passenger> passengers) {
        this.passengers = passengers;
    }

    public ArrayList<Requests> getRequests() {
        return requests;
    }

    public void setRequests(ArrayList<Requests> requests) {
        this.requests = requests;
    }

    public String getDriverSource() {
        return driverSource;
    }

    public void setDriverSource(String driverSource) {
        this.driverSource = driverSource;
    }

    public String getDriverDestination() {
        return driverDestination;
    }

    public void setDriverDestination(String driverDestination) {
        this.driverDestination = driverDestination;
    }

    public String getDriverUid() {
        return driverUid;
    }

    public void setDriverUid(String driverUid) {
        this.driverUid = driverUid;
    }

    public ArrayList<LatLng> getDriverRoute() {
        return driverRoute;
    }

    public void setDriverRoute(ArrayList<LatLng> driverRoute) {
        this.driverRoute = driverRoute;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(tripPrice);
        dest.writeInt(seats);
        dest.writeString(driverSource);
        dest.writeString(driverDestination);
        dest.writeString(driverUid);
        dest.writeString(privacy);
    }
}

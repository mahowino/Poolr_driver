package com.example.poolrdriver.classes.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.GeoPoint;

import java.util.Date;

public class Requests implements Parcelable {
    LatLng source,destination;
    GeoPoint sourceGeopoint,destinationGeopoint;

    protected Requests(Parcel in) {
        source = in.readParcelable(LatLng.class.getClassLoader());
        destination = in.readParcelable(LatLng.class.getClassLoader());
        LocationFrom = in.readString();
        LocationTo = in.readString();
        passengerUID = in.readString();
        tripUID = in.readString();
        requestID = in.readString();
        seats = in.readInt();
        userSource = in.readString();
        userDestination = in.readString();
        distanceFromSource = in.readDouble();
        distanceToDestination = in.readDouble();
        tripPrice = in.readDouble();
    }

    public static final Creator<Requests> CREATOR = new Creator<Requests>() {
        @Override
        public Requests createFromParcel(Parcel in) {
            return new Requests(in);
        }

        @Override
        public Requests[] newArray(int size) {
            return new Requests[size];
        }
    };

    public GeoPoint getSourceGeopoint() {
        return sourceGeopoint;
    }

    public GeoPoint getDestinationGeopoint() {
        return destinationGeopoint;
    }

    public void setSourceGeopoint(GeoPoint sourceGeopoint) {
        this.sourceGeopoint = sourceGeopoint;
    }

    public void setDestinationGeopoint(GeoPoint destinationGeopoint) {
        this.destinationGeopoint = destinationGeopoint;
    }

    String LocationFrom,LocationTo;
    String passengerUID,tripUID,requestID;
    Date tripDate;
    int seats;
    String userSource,userDestination;
    double distanceFromSource,distanceToDestination;
    double tripPrice;

    public String getRequestID() {
        return requestID;
    }

    public void setRequestID(String requestID) {
        this.requestID = requestID;
    }

    public Requests() {
    }

    public int getSeats() {
        return seats;
    }

    public void setSeats(int seats) {
        this.seats = seats;
    }

    public LatLng getSource() {
        return source;
    }

    public void setSource(LatLng source) {
        this.source = source;
    }

    public LatLng getDestination() {
        return destination;
    }

    public void setDestination(LatLng destination) {
        this.destination = destination;
    }

    public String getLocationFrom() {
        return LocationFrom;
    }

    public void setLocationFrom(String driverLocationFrom) {
        this.LocationFrom = driverLocationFrom;
    }

    public String getLocationTo() {
        return LocationTo;
    }

    public void setLocationTo(String driverLocationTo) {
        this.LocationTo = driverLocationTo;
    }

    public String getPassengerUID() {
        return passengerUID;
    }

    public void setPassengerUID(String driverUID) {
        this.passengerUID = driverUID;
    }

    public String getTripUID() {
        return tripUID;
    }

    public void setTripUID(String tripUID) {
        this.tripUID = tripUID;
    }

    public Date getTripDate() {
        return tripDate;
    }

    public void setTripDate(Date tripDate) {
        this.tripDate = tripDate;
    }

    public String getUserSource() {
        return userSource;
    }

    public void setUserSource(String userSource) {
        this.userSource = userSource;
    }

    public String getUserDestination() {
        return userDestination;
    }

    public void setUserDestination(String userDestination) {
        this.userDestination = userDestination;
    }

    public double getDistanceFromSource() {
        return distanceFromSource;
    }

    public void setDistanceFromSource(double distanceFromSource) {
        this.distanceFromSource = distanceFromSource;
    }

    public double getDistanceToDestination() {
        return distanceToDestination;
    }

    public void setDistanceToDestination(double distanceToDestination) {
        this.distanceToDestination = distanceToDestination;
    }

    public double getTripPrice() {
        return tripPrice;
    }

    public void setTripPrice(double tripPrice) {
        this.tripPrice = tripPrice;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(source, flags);
        dest.writeParcelable(destination, flags);
        dest.writeString(LocationFrom);
        dest.writeString(LocationTo);
        dest.writeString(passengerUID);
        dest.writeString(tripUID);
        dest.writeString(requestID);
        dest.writeInt(seats);
        dest.writeString(userSource);
        dest.writeString(userDestination);
        dest.writeDouble(distanceFromSource);
        dest.writeDouble(distanceToDestination);
        dest.writeDouble(tripPrice);
    }
}

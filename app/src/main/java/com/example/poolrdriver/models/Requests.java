package com.example.poolrdriver.models;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.GeoPoint;

import java.util.Date;

public class Requests {
    LatLng source,destination;
    String LocationFrom,LocationTo;
    String driverUID,tripUID;
    Date tripDate;
    int seats;
    String userSource,userDestination;
    double distanceFromSource,distanceToDestination;
    double tripPrice;

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
        return driverUID;
    }

    public void setPassengerUID(String driverUID) {
        this.driverUID = driverUID;
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
}

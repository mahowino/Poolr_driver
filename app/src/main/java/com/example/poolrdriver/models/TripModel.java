package com.example.poolrdriver.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.poolrdriver.classes.Network;
import com.example.poolrdriver.classes.Passenger;
import com.example.poolrdriver.classes.Requests;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.List;

public class TripModel implements Parcelable {
    private int seats;
    private Long tripPrice;
    private ArrayList<Passenger> passengers;
    private ArrayList<Requests> requests;
    private ArrayList<Network> networks;
    private String driverSource,driverDestination,driverUid;
    private boolean privacy;
    private PolylineOptions driverRoute;
    private TimePickerObject timePickerObject;
    private String networkId,tripID;
    private List<LatLng> driverRouteList;
    private LatLng sourceGeopoint,destinationGeopoint;

    public TripModel(){}

    public LatLng getSourcePoint() {
        return sourceGeopoint;
    }

    public void setSourcepoint(LatLng sourcePoint) {
        this.sourceGeopoint = sourcePoint;
    }

    public LatLng getDestinationpoint() {
        return destinationGeopoint;
    }

    public void setDestinationpoint(LatLng destinationPoint) {
        this.destinationGeopoint = destinationPoint;
    }

    protected TripModel(Parcel in) {
        seats = in.readInt();
        if (in.readByte() == 0) {
            tripPrice = null;
        } else {
            tripPrice = in.readLong();
        }
        passengers = in.createTypedArrayList(Passenger.CREATOR);
        driverSource = in.readString();
        driverDestination = in.readString();
        driverUid = in.readString();
        privacy = in.readByte() != 0;
        driverRoute = in.readParcelable(PolylineOptions.class.getClassLoader());
        networkId = in.readString();
        driverRouteList = in.createTypedArrayList(LatLng.CREATOR);
    }

    public static final Creator<TripModel> CREATOR = new Creator<TripModel>() {
        @Override
        public TripModel createFromParcel(Parcel in) {
            return new TripModel(in);
        }

        @Override
        public TripModel[] newArray(int size) {
            return new TripModel[size];
        }
    };

    public Long getTripPrice() {
        return tripPrice;
    }

    public void setTripPrice(Long tripPrice) {
        this.tripPrice = tripPrice;
    }

    public int getSeats() {
        return seats;
    }

    public void setSeats(int seats) {
        this.seats = seats;
    }

    public ArrayList<Passenger> getPassengers() {
        return passengers;
    }

    public void setPassengers(ArrayList<Passenger> passengers) {
        this.passengers = passengers;
    }

    public ArrayList<Requests> getRequests() {
        return requests;
    }

    public void setRequests(ArrayList<Requests> requests) {
        this.requests = requests;
    }

    public ArrayList<Network> getNetworks() {
        return networks;
    }

    public void setNetworks(ArrayList<Network> networks) {
        this.networks = networks;
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

    public boolean isPrivacy() {
        return privacy;
    }

    public void setPrivacy(boolean privacy) {
        this.privacy = privacy;
    }

    public PolylineOptions getDriverRoute() {
        return driverRoute;
    }

    public void setDriverRoute(PolylineOptions driverRoute) {
        this.driverRoute = driverRoute;
    }

    public TimePickerObject getTimePickerObject() {
        return timePickerObject;
    }

    public void setTimePickerObject(TimePickerObject timePickerObject) {
        this.timePickerObject = timePickerObject;
    }

    public String getNetworkId() {
        return networkId;
    }

    public void setNetworkId(String networkId) {
        this.networkId = networkId;
    }

    public List<LatLng> getDriverRouteList() {
        return driverRouteList;
    }

    public void setDriverRouteList(List<LatLng> driverRouteList) {
        this.driverRouteList = driverRouteList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(seats);
        if (tripPrice == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(tripPrice);
        }
        dest.writeTypedList(passengers);
        dest.writeString(driverSource);
        dest.writeString(driverDestination);
        dest.writeString(driverUid);
        dest.writeByte((byte) (privacy ? 1 : 0));
        dest.writeParcelable(driverRoute, flags);
        dest.writeString(networkId);
        dest.writeTypedList(driverRouteList);
    }


    public String getTripID() {
        return tripID;
    }

    public void setTripID(String tripID) {
        this.tripID = tripID;
    }
}

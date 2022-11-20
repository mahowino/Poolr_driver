package com.example.poolrdriver.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.poolrdriver.classes.Requests;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

public class TripModel implements Parcelable {
    private int seats;
    private Long tripPrice,passengerBookingFee;
    private ArrayList<Requests> requests;
    private ArrayList<Network> networks;
    private String driverSource,driverDestination,driverUid;
    private boolean privacy;
    private String luggage;
    private PolylineOptions driverRoute;
    private TimePickerObject timePickerObject;
    private String networkId,tripID;
    private LatLng sourceGeopoint,destinationGeopoint;
    private LatLng startingPoint,endingPoint;

    public LatLng getStartingPoint() {
        return startingPoint;
    }

    public void setStartingPoint(LatLng startingPoint) {
        this.startingPoint = startingPoint;
    }

    public LatLng getEndingPoint() {
        return endingPoint;
    }

    public void setEndingPoint(LatLng endingPoint) {
        this.endingPoint = endingPoint;
    }

    public TripModel(){}

    protected TripModel(Parcel in) {
        seats = in.readInt();
        if (in.readByte() == 0) {
            tripPrice = null;
        } else {
            tripPrice = in.readLong();
        }
        if (in.readByte() == 0) {
            passengerBookingFee = null;
        } else {
            passengerBookingFee = in.readLong();
        }
        networks = in.createTypedArrayList(Network.CREATOR);
        driverSource = in.readString();
        driverDestination = in.readString();
        driverUid = in.readString();
        privacy = in.readByte() != 0;
        luggage = in.readString();
        driverRoute = in.readParcelable(PolylineOptions.class.getClassLoader());
        networkId = in.readString();
        tripID = in.readString();
        sourceGeopoint = in.readParcelable(LatLng.class.getClassLoader());
        destinationGeopoint = in.readParcelable(LatLng.class.getClassLoader());
        startingPoint = in.readParcelable(LatLng.class.getClassLoader());
        endingPoint = in.readParcelable(LatLng.class.getClassLoader());
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

    public Long getPassengerBookingFee() {
        return passengerBookingFee;
    }

    public void setPassengerBookingFee(Long passengerBookingFee) {
        this.passengerBookingFee = passengerBookingFee;
    }

    public String getLuggage() {
        return luggage;
    }

    public void setLuggage(String luggage) {
        this.luggage = luggage;
    }

    public LatLng getSourceGeopoint() {
        return sourceGeopoint;
    }

    public void setSourceGeopoint(LatLng sourceGeopoint) {
        this.sourceGeopoint = sourceGeopoint;
    }

    public LatLng getDestinationGeopoint() {
        return destinationGeopoint;
    }

    public void setDestinationGeopoint(LatLng destinationGeopoint) {
        this.destinationGeopoint = destinationGeopoint;
    }


  ;

    public LatLng getSourcePoint() {
        return sourceGeopoint;
    }

    public void setSourcePoint(LatLng sourcePoint) {
        this.sourceGeopoint = sourcePoint;
    }

    public LatLng getDestinationpoint() {
        return destinationGeopoint;
    }

    public void setDestinationpoint(LatLng destinationPoint) {
        this.destinationGeopoint = destinationPoint;
    }


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



    public String getTripID() {
        return tripID;
    }

    public void setTripID(String tripID) {
        this.tripID = tripID;
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
        if (passengerBookingFee == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(passengerBookingFee);
        }
        dest.writeTypedList(networks);
        dest.writeString(driverSource);
        dest.writeString(driverDestination);
        dest.writeString(driverUid);
        dest.writeByte((byte) (privacy ? 1 : 0));
        dest.writeString(luggage);
        dest.writeParcelable(driverRoute, flags);
        dest.writeString(networkId);
        dest.writeString(tripID);
        dest.writeParcelable(sourceGeopoint, flags);
        dest.writeParcelable(destinationGeopoint, flags);
        dest.writeParcelable(startingPoint, flags);
        dest.writeParcelable(endingPoint, flags);
    }
}

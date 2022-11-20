package com.example.poolrdriver.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.GeoPoint;

public class Network implements Parcelable {
    String NetworkName,NetworkTravelAdminUID,NetworkUID,networkCode;
    int NetworkRating;
    LatLng homeLocation,workLocation;
    boolean isNetworkAcceptOnCode;


    protected Network(Parcel in) {
        NetworkName = in.readString();
        NetworkTravelAdminUID = in.readString();
        NetworkUID = in.readString();
        networkCode = in.readString();
        NetworkRating = in.readInt();
        homeLocation = in.readParcelable(LatLng.class.getClassLoader());
        workLocation = in.readParcelable(LatLng.class.getClassLoader());
        isNetworkAcceptOnCode = in.readByte() != 0;
    }

    public static final Creator<Network> CREATOR = new Creator<Network>() {
        @Override
        public Network createFromParcel(Parcel in) {
            return new Network(in);
        }

        @Override
        public Network[] newArray(int size) {
            return new Network[size];
        }
    };

    public boolean isNetworkAcceptOnCode() {
        return isNetworkAcceptOnCode;
    }

    public void setNetworkAcceptOnCode(boolean networkAcceptOnCode) {
        isNetworkAcceptOnCode = networkAcceptOnCode;
    }
;

    public String getNetworkCode() {
        return networkCode;
    }

    public void setNetworkCode(String networkCode) {
        this.networkCode = networkCode;
    }

    public LatLng getHomeLocation() {
        return homeLocation;
    }

    public void setHomeLocation(LatLng homeLocation) {
        this.homeLocation = homeLocation;
    }

    public LatLng getWorkLocation() {
        return workLocation;
    }

    public void setWorkLocation(LatLng workLocation) {
        this.workLocation = workLocation;
    }

    public Network() {
    }


    public String getNetworkName() {
        return NetworkName;
    }

    public void setNetworkName(String networkName) {
        NetworkName = networkName;
    }

    public String getNetworkTravelAdminUID() {
        return NetworkTravelAdminUID;
    }

    public void setNetworkTravelAdminUID(String networkTravelAdminUID) {
        NetworkTravelAdminUID = networkTravelAdminUID;
    }

    public String getNetworkUID() {
        return NetworkUID;
    }

    public void setNetworkUID(String networkUID) {
        NetworkUID = networkUID;
    }

    public int getNetworkRating() {
        return NetworkRating;
    }

    public void setNetworkRating(int networkRating) {
        NetworkRating = networkRating;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(NetworkName);
        dest.writeString(NetworkTravelAdminUID);
        dest.writeString(NetworkUID);
        dest.writeString(networkCode);
        dest.writeInt(NetworkRating);
        dest.writeParcelable(homeLocation, flags);
        dest.writeParcelable(workLocation, flags);
        dest.writeByte((byte) (isNetworkAcceptOnCode ? 1 : 0));
    }
}

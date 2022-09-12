package com.example.poolrdriver.classes;

import android.os.Parcel;
import android.os.Parcelable;

public class Network implements Parcelable {
    String NetworkName,NetworkTravelAdminUID,NetworkUID;
    int NetworkRating;

    public Network() {
    }

    protected Network(Parcel in) {
        NetworkName = in.readString();
        NetworkTravelAdminUID = in.readString();
        NetworkUID = in.readString();
        NetworkRating = in.readInt();
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
        dest.writeInt(NetworkRating);
    }
}

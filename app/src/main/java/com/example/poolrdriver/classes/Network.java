package com.example.poolrdriver.classes;

public class Network {
    String NetworkName,NetworkTravelAdminUID,NetworkUID;
    int NetworkRating;

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
}

package com.example.poolrdriver.classes;

public class Requests {
    private int tripPrice,passengerProfilePicture;
    private String passengerName;

    public Requests(String routeSource, String routeDestination, int tripPrice, int passegerProfilePicture, String passengerName) {
        this.passengerName=passengerName;
        this.passengerProfilePicture=passegerProfilePicture;
        this.tripPrice=tripPrice;
    }

    public int getTripPrice() {
        return tripPrice;
    }

    public void setTripPrice(int tripPrice) {
        this.tripPrice = tripPrice;
    }

    public int getDriverProfilePicture() {
        return passengerProfilePicture;
    }

    public void setDriverProfilePicture(int driverProfilePicture) {
        this.passengerProfilePicture = driverProfilePicture;
    }

    public String getDriverName() {
        return passengerName;
    }

    public void setDriverName(String driverName) {
        this.passengerName = driverName;
    }
}

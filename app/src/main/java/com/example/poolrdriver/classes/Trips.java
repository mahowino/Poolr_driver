package com.example.poolrdriver.classes;

public class Trips extends Route {

    private int tripPrice,passengerProfilePicture;
    private String passengerName;

    public Trips(String routeSource, String routeDestination, String userSource, String userDestination, int tripPrice, int driverProfilePicture, String driverName) {
        super(routeSource, routeDestination, userSource, userDestination);
        this.passengerName=driverName;
        this.passengerProfilePicture=driverProfilePicture;
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

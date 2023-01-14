package com.example.poolrdriver.Interfaces;

import com.example.poolrdriver.classes.other.Passenger;

public interface OnTripEndedListener {
    void onTripEnded(double driverCharge, double adminCharge, double valueToTopPassengerWallet,Passenger passenger);

}

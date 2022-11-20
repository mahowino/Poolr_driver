package com.example.poolrdriver.Interfaces;

import com.example.poolrdriver.classes.Passenger;

import java.util.List;

public interface PassengerRetriever {
     void onSuccess(List<Passenger> passengers);
}

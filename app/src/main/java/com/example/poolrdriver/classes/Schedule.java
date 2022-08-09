package com.example.poolrdriver.classes;

public class Schedule {
    String sourceAddress,destinationAddress,leavingTime,destinationTime;

    public Schedule(String sourceAddress, String destinationAddress, String leavingTime, String destinationTime) {
        this.sourceAddress=sourceAddress;
        this.destinationAddress=destinationAddress;
        this.leavingTime=leavingTime;
        this.destinationTime=destinationTime;

    }

    public String getSourceAddress() {
        return sourceAddress;
    }

    public void setSourceAddress(String sourceAddress) {
        this.sourceAddress = sourceAddress;
    }

    public String getDestinationAddress() {
        return destinationAddress;
    }

    public void setDestinationAddress(String destinationAddress) {
        this.destinationAddress = destinationAddress;
    }

    public String getLeavingTime() {
        return leavingTime;
    }

    public void setLeavingTime(String leavingTime) {
        this.leavingTime = leavingTime;
    }

    public String getDestinationTime() {
        return destinationTime;
    }

    public void setDestinationTime(String destinationTime) {
        this.destinationTime = destinationTime;
    }
}

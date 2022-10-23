package com.example.poolrdriver.models;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class CarTypes implements Parcelable {

    String carType,model,numberplate,color;
    String year;

    public CarTypes(String carType) {

        this.carType = carType;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getNumberplate() {
        return numberplate;
    }

    public void setNumberplate(String numberplate) {
        this.numberplate = numberplate;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    protected CarTypes(Parcel in) {
        carType = in.readString();
        year=in.readString();
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public static final Creator<CarTypes> CREATOR = new Creator<CarTypes>() {
        @Override
        public CarTypes createFromParcel(Parcel in) {
            return new CarTypes(in);
        }

        @Override
        public CarTypes[] newArray(int size) {
            return new CarTypes[size];
        }
    };

    public String getCarType() {
        return carType;
    }

    public void setCarType(String carType) {
        this.carType = carType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(carType);
        dest.writeString(year);
    }
}

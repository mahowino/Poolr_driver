package com.example.poolrdriver.models;


import android.os.Parcel;
import android.os.Parcelable;

public class CarTypes implements Parcelable {

    String carType,model,numberplate,color;
    String year;

    public CarTypes(String carType) {

        this.carType = carType;
    }

    protected CarTypes(Parcel in) {
        carType = in.readString();
        model = in.readString();
        numberplate = in.readString();
        color = in.readString();
        year = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(carType);
        dest.writeString(model);
        dest.writeString(numberplate);
        dest.writeString(color);
        dest.writeString(year);
    }

    @Override
    public int describeContents() {
        return 0;
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

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getCarType() {
        return carType;
    }

    public void setCarType(String carType) {
        this.carType = carType;
    }

}

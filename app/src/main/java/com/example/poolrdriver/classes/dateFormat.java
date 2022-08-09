package com.example.poolrdriver.classes;

import android.os.Parcel;
import android.os.Parcelable;

public class dateFormat implements Parcelable {
    String Year,Month,day,Hour,Minute;
    int Year_int,Month_int,day_int,Hour_int,Minute_int;
    public dateFormat(){}

    public int getYear_int() {
        return Year_int;
    }

    public void setYear_int(int year_int) {
        Year_int = year_int;
    }

    public int getMonth_int() {
        return Month_int;
    }

    public void setMonth_int(int month_int) {
        Month_int = month_int;
    }

    public int getDay_int() {
        return day_int;
    }

    public void setDay_int(int day_int) {
        this.day_int = day_int;
    }

    public int getHour_int() {
        return Hour_int;
    }

    public void setHour_int(int hour_int) {
        Hour_int = hour_int;
    }

    public int getMinute_int() {
        return Minute_int;
    }

    public void setMinute_int(int minute_int) {
        Minute_int = minute_int;
    }

    public dateFormat(String year, String month, String day, String hour, String minute) {
        Year = year;
        Month = month;
        this.day = day;
        Hour = hour;
        Minute = minute;
    }

    protected dateFormat(Parcel in) {
        Year = in.readString();
        Month = in.readString();
        day = in.readString();
        Hour = in.readString();
        Minute = in.readString();
    }
    public dateFormat(int driverYear, int driverMonth, int driverDay, int driverHour, int driverMinute) {
        Year_int=driverYear;
        Month_int=driverMonth;
        day_int=driverDay;
        Hour_int=driverHour;
        Minute_int=driverMinute;
    }
    public static final Creator<dateFormat> CREATOR = new Creator<dateFormat>() {
        @Override
        public dateFormat createFromParcel(Parcel in) {
            return new dateFormat(in);
        }

        @Override
        public dateFormat[] newArray(int size) {
            return new dateFormat[size];
        }
    };



    public String getYear() {
        return Year;
    }

    public void setYear(String year) {
        Year = year;
    }

    public String getMonth() {
        return Month;
    }

    public void setMonth(String month) {
        Month = month;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getHour() {
        return Hour;
    }

    public void setHour(String hour) {
        Hour = hour;
    }

    public String getMinute() {
        return Minute;
    }

    public void setMinute(String minute) {
        Minute = minute;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(Year);
        dest.writeString(Month);
        dest.writeString(day);
        dest.writeString(Hour);
        dest.writeString(Minute);
    }
}

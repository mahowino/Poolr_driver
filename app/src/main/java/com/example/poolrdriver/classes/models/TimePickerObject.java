package com.example.poolrdriver.classes.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Calendar;
import java.util.Date;

public class TimePickerObject implements Parcelable {
    int hour,minute,day,month,year;

    public TimePickerObject() {
    }

    public TimePickerObject(int hour,int minute,int day, int month, int year) {
        this.hour=hour;
        this.minute=minute;
        this.day=day;
        this.month=month;
        this.year=year;
    }
    protected TimePickerObject(Parcel in) {
        hour = in.readInt();
        minute = in.readInt();
        day = in.readInt();
        month = in.readInt();
        year = in.readInt();
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public int getDay() {
        return day;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    public static final Creator<TimePickerObject> CREATOR = new Creator<TimePickerObject>() {
        @Override
        public TimePickerObject createFromParcel(Parcel in) {
            return new TimePickerObject(in);
        }

        @Override
        public TimePickerObject[] newArray(int size) {
            return new TimePickerObject[size];
        }
    };

    public void setCalendarDate(int day, int month, int year){
        this.day=day;
        this.month=month;
        this.year=year;
    }
    public void setCalendarTime(int hour,int minute){
        this.minute=minute;
        this.hour=hour;
    }
    public void setCalendarDate(String day,String month, String year){
        this.day=Integer.parseInt(day);
        this.month=Integer.parseInt(month);
        this.year=Integer.parseInt(year);
    }
    public void setCalendarTime(String hour ,String minute){
        this.minute=Integer.parseInt(minute);
        this.hour=Integer.parseInt(hour);
    }
    public Date getDate(){
        Calendar calendar=Calendar.getInstance();
        calendar.set(Calendar.MINUTE,minute);
        calendar.set(Calendar.HOUR_OF_DAY,hour);
        calendar.set(Calendar.DAY_OF_MONTH,day);
        calendar.set(Calendar.MONTH,month);
        calendar.set(Calendar.YEAR,year);
        return calendar.getTime();
    }

    public Date getDate(boolean bool){
        return new Date((year-1900),month,day,hour,minute,0);
    }

    public void setDefaultCalendarDateAndTime() {
        Calendar calendar=Calendar.getInstance();
        this.minute=calendar.get(Calendar.MINUTE);
        this.hour=calendar.get(Calendar.HOUR_OF_DAY);
        this.day=calendar.get(Calendar.DAY_OF_MONTH);
        this.month=calendar.get(Calendar.MONTH);
        this.year=calendar.get(Calendar.YEAR);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(hour);
        dest.writeInt(minute);
        dest.writeInt(day);
        dest.writeInt(month);
        dest.writeInt(year);
    }
}

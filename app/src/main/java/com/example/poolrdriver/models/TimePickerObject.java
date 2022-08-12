package com.example.poolrdriver.models;

import java.util.Calendar;
import java.util.Date;

public class TimePickerObject {
    int hour,minute,day,month,year;

    public TimePickerObject() {
    }

    public void setCalendarDate(int day,int month, int year){
        this.day=day;
        this.month=month;
        this.year=year;
    }
    public void setCalendarTime(int minute,int hour){
        this.minute=minute;
        this.hour=hour;
    }
    public void setCalendarDate(String day,String month, String year){
        this.day=Integer.parseInt(day);
        this.month=Integer.parseInt(month);
        this.year=Integer.parseInt(year);
    }
    public void setCalendarTime(String minute,String hour){
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

    public void setDefaultCalendarDateAndTime() {
        Calendar calendar=Calendar.getInstance();
        this.minute=calendar.get(Calendar.MINUTE);
        this.hour=calendar.get(Calendar.HOUR_OF_DAY);
        this.day=calendar.get(Calendar.DAY_OF_MONTH);
        this.month=calendar.get(Calendar.MONTH);
        this.year=calendar.get(Calendar.YEAR);
    }
}

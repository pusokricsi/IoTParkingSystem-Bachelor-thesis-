package com.example.iotparkingsystem.Objects;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateCheck {
    private String cDate, cStartDate, cEndDate;  //c = choosed
    private String pDate, pTime;
    private SimpleDateFormat dateFormat;
    private SimpleDateFormat timeFormat;
    private SimpleDateFormat dateTimeFormat;

    public DateCheck() {
    }

    public DateCheck(String cDate, String cStartDate, String cEndDate) {
        this.cDate = cDate;
        this.cStartDate = cStartDate;
        this.cEndDate = cEndDate;
        setPresentDate();
    }

    public void setPresentDate() {
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        timeFormat = new SimpleDateFormat("HH-mm");
        dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm");
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        this.pDate = year + "-" + month + "-" + day;
        this.pTime = hour + "-" + minute;
    }

    public String getcDate() {
        return cDate;
    }

    public void setcDate(String cDate) {
        this.cDate = cDate;
    }

    public String getcStartDate() {
        return cStartDate;
    }

    public void setcStartDate(String cStartDate) {
        this.cStartDate = cStartDate;
    }

    public String getcEndDate() {
        return cEndDate;
    }

    public void setcEndDate(String cEndDate) {
        this.cEndDate = cEndDate;
    }

    public boolean checkIsEmpty() {
        if (!cDate.isEmpty() && !cEndDate.isEmpty() && !cStartDate.isEmpty()) {
            Log.i("IoT", "DataCheck: Data field is ok!");
            return false;
        }
        Log.i("IoT", "DataCheck: Data field is empty!");
        return true;
    }

    public boolean checkOutDatedDate() {  // check the date is outdated
        Date presentDate = null, startDate = null, endDate = null;
        try {
            presentDate = dateTimeFormat.parse(pDate + " " + pTime);
            startDate = dateTimeFormat.parse(cDate + " " + cStartDate);
            endDate = dateTimeFormat.parse(cDate + " " + cEndDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (startDate.compareTo(presentDate) <= 0 || endDate.compareTo(presentDate) <= 0) {
            Log.i("IoT", "DateCheck: Date is out of date!");
            return true;
        }
        Log.i("IoT", "DateCheck: Date is up to date!");
        return false;
    }

    public boolean check15Minutes(){
        //TODO
        return true;
    }

    public boolean checkOneDateIsFree(String sDate, String eDate) {
        Date rStartDate = null, rEndDate = null; // r = reserved
        Date startDate = null, endDate = null;
        try {
            rStartDate = dateTimeFormat.parse(sDate);
            rEndDate = dateTimeFormat.parse(eDate);
            startDate = dateTimeFormat.parse(cDate + " " + cStartDate);
            endDate = dateTimeFormat.parse(cDate + " " + cEndDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (endDate.compareTo(rStartDate) > 0 && endDate.compareTo(rEndDate) < 0) { //paper 1'st test
            Log.i("IoT", "DateCheck: Date is reserved!  1");
            return false;
        }
        if (startDate.compareTo(rStartDate) > 0 && startDate.compareTo(rEndDate) < 0) { //paper 2'nd test
            Log.i("IoT", "DateCheck: Date is reserved!  2");
            return false;
        }
        if (startDate.compareTo(rStartDate)>0 && endDate.compareTo(rEndDate)<0){ //paper 3'rd test
            Log.i("IoT", "DateCheck: Date is reserved!  3");
            return false;
        }
        if (startDate.compareTo(rStartDate)<0 && endDate.compareTo(rEndDate)>0){  //paper 4 test
            Log.i("IoT", "DateCheck: Date is reserved!  4");
            return false;
        }
        return true;
    }

    public boolean checkReservationDate(String startTime){
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        if (minute+20>59){
            minute = minute-40;
        }else{
            minute=minute+20;
        }
        String newTime = hour + "-" + minute;

        Date date = null;
        Date start = null;
        Date tendate = null;
        try {
            date = dateTimeFormat.parse(this.pDate+" "+this.pTime);
            start = dateTimeFormat.parse(startTime);
            tendate = dateTimeFormat.parse(this.pDate+" "+newTime);
            if (start.compareTo(date)>0 && start.compareTo(tendate)<=0){
                Log.i("IoT","DateCheck: user have reservations in 20minutes interval");
                return true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

}

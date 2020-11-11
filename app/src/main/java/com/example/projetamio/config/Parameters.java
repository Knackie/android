package com.example.projetamio.config;

import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;

public class Parameters {
    public static final String URLData =  "http://iotlab.telecomnancy.eu:8080/iotlab/rest/data/1/temperature-light2/last";
    public static final String PrefName = "iotlabDreyerMillardet";
    public static final String DefaultEmail = "jzkaejlka@cnxjkcsnjcks.com";
    public static final int[] Week = { Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY, Calendar.THURSDAY, Calendar.FRIDAY};
    public static final int[] WeekEnd = { Calendar.SATURDAY, Calendar.SUNDAY};

    public static boolean isWeekDay(int day){
        int nb = Week.length;
        for(int i= 0; i<nb; i++){
            if (day == Week[i]){
                return true;
            }
        }
        return false;
    }

    public static boolean isWeekEndDay(int day){
        int nb = Week.length;
        for(int i= 0; i<nb; i++){
            if (day == WeekEnd[i]){
                return true;
            }
        }
        return false;
    }


}

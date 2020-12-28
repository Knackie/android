package com.example.projetamio.config;

import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Classe contenant les paramètres de l'application
 */
public class Parameters {

    /**
     * URL du serveur contenant les données
     */
    public static final String URLData =  "http://iotlab.telecomnancy.eu:8080/iotlab/rest/data/1/temperature-light2-light1-battery_indicator-humidity/last";

    /**
     * Nom des préférences de l'application
     */
    public static final String PrefName = "iotlabDreyerMillardet";

    /**
     * Adresse email par défaut de l'application
     */
    public static String DefaultEmail = "Projet.amio@gmail.com";

    /**
     * Jour de la semaine
     */
    public static final int[] Week = { Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY, Calendar.THURSDAY, Calendar.FRIDAY};

    /**
     * Jour du Weekend
     */
    public static final int[] WeekEnd = { Calendar.SATURDAY, Calendar.SUNDAY};

    /**
     * Fonction indiquant si le jour donné est un jour de la semaine
     * @param day Jour à tester
     * @return True si jour de la semaine, else sinon
     */
    public static boolean isWeekDay(int day){
        int nb = Week.length;
        for(int i= 0; i<nb; i++){
            if (day == Week[i]){
                return true;
            }
        }
        return false;
    }

    /**
     * Fonction indiquant si le jour donné est un jour du Weekend
     * @param day Jour à tester
     * @return True si jour de Weekend, else sinon
     */
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

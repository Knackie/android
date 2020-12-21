package com.example.projetamio.services;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.projetamio.config.Parameters;

/**
 * Service de notification des applications
 */
public class NotifyChoiceService extends Service {

    /**
     * Action définie au bind du service
     * @param intent Intent appelant
     * @return ???
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Fonction appelée à la création du service
     */
    @Override
    public void onCreate() {
        super.onCreate();
        if (!this.inHourNotification()){
            Intent service = new Intent(this, SendEmailService.class);
            startService(service);
        }
        else {
            Log.d(this.getClass().getName(), "Je suis là");
        }
        this.stopSelf();
    }

    /**
     * Fonction permettant de tester si l'on est dans les heures envoyant des notifications
     */
    private boolean inHourNotification() {
        //String PREFS_NAME = Parameters.PrefName;
        //TODO : Programmer le système de basculement des notifications en fonction de l'heure.
        return false;
    }

}

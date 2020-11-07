package com.example.projetamio.services;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.projetamio.config.Parameters;

public class NotifyChoiceService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

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

    private boolean inHourNotification() {
        //String PREFS_NAME = Parameters.PrefName;
        //TODO : Programmer le système de basculement des notifications en fonction de l'heure.
        return false;
    }

}

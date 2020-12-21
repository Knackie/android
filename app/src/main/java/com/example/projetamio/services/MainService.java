package com.example.projetamio.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.util.Timer;

/**
 * Service principal de l'application lançant la récupération périodique des données
 */
public class MainService extends Service {

    /**
     * Indique si le service est lancé ou non
     */
    public static boolean active;

    /**
     * Constructeur de la classe
     */
    public MainService() {
    }

    /**
     * Fonction permettant de définir des actions lorsque le service est Bind
     * @param intent Intent lançant le service
     * @return ???
     */
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Fonction appelée lors de la création du service
     */
    @Override
    public void onCreate() {
        LogEveryMoment log = new LogEveryMoment();
        log.setContext(this);
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(log, 0, 30*1000);
        MainService.active = true;
    }

    /**
     * Fonction appelée à la destruction du service
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        MainService.active = false;
    }
}
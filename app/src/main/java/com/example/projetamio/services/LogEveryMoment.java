package com.example.projetamio.services;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import java.util.TimerTask;

/**
 * Fonction peremttant l'execution à interval régulier de la mis à jour des données
 */
public class LogEveryMoment extends TimerTask {

    /**
     * Indique le numéro auquel commence le comptage des recherches
     */
    private static int number = 0;

    /**
     * Context dans lequel est lancé la tache
     */
    private Context context;

    /**
     * Fonction définissant le context
     * @param con context récupéré
     */
    public void setContext(Context con){
        this.context = con;
    }

    /**
     * Fonction lancée à chaque fois que le temps est atteint
     */
    @Override
    public void run() {
        LogEveryMoment.number++;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(new Intent(context, DatareceiverFromServerService.class));
        } else {
            context.startService(new Intent(context, DatareceiverFromServerService.class));
        }

    }
}

package com.example.projetamio.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import com.example.projetamio.services.DatareceiverFromServerService;

import static com.example.projetamio.activity.MainActivity.PREFS_NAME;


/**
 * Class permettant la gestion du démarrage de l'application lorsque
 */
public class MyBootBroadcastReceiver extends BroadcastReceiver {

    /**
     * Constructeur de la classe
     */
    public MyBootBroadcastReceiver(){
        super();
    }

    /**
     * Fonction appelée lorsque le Boradcast est appelée
     * @param context Context d'appel de la méthode
     * @param intent Intent Intent appelant la fonction
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("MyBootBroadcastReceiver", "Le broadcast est bien arrivé");
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        if ( settings.getBoolean("startAtBoot", false)){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(new Intent(context, DatareceiverFromServerService.class));
            } else {
                context.startService(new Intent(context, DatareceiverFromServerService.class));
            }
        }
    }
}

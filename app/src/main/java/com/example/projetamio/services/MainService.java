package com.example.projetamio.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.util.Timer;

public class MainService extends Service {

    public static boolean active;

    public MainService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Function create manually
     */
    @Override
    public void onCreate() {
        LogEveryMoment log = new LogEveryMoment();
        log.setContext(this);
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(log, 0, 30*1000);
        MainService.active = true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MainService.active = false;
    }
}
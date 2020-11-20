package com.example.projetamio.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.util.Timer;

public class MainService extends Service {

    public static boolean create;
    private boolean downloading = false;
//    private NetworkFragment networkFragment;

    public MainService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
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
        MainService.create = true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MainService.create = false;
    }
}
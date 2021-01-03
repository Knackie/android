package com.example.projetamio.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.opencsv.CSVReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Service principal de l'application lançant la récupération périodique des données
 */
public class ClosestMoteService extends Service {

    private static final String TAG = "BOOMBOOMTESTGPS";
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 1000;
    private static final float LOCATION_DISTANCE = 10f;
    private static String ClosestMote;
    private static String ClosestMoteSimple;
    private AssetManager assetManager;
    private InputStream is;
    private BufferedReader br = null;
    private ArrayList<Emplacement> listEmp;

    private class LocationListener implements android.location.LocationListener {


        Location mLastLocation;


        public LocationListener(String provider) {
            mLastLocation = new Location(provider);
            mLastLocation.set(mLastLocation);

        }

        @Override
        public void onLocationChanged(Location location) {
            executeCalcul(location);

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // Do nothing because it's useless on our project now
        }

        @Override
        public void onProviderDisabled(String provider) {
            // Do nothing because it's useless on our project now
        }

        @Override
        public void onProviderEnabled(String provider) {
            // Do nothing because it's useless on our project now
        }

        private void executeCalcul(Location location){
            float distance_min = -1, distance;
            String moteName = "", nomSimple = "";
            float[] res = new float[1];;
            for (Emplacement e: listEmp) {
                Location.distanceBetween(location.getLatitude(), location.getLongitude(), e.Latutude, e.Longitude, res );
                distance =  res[0];
                if (distance < distance_min || distance_min == -1){
                    distance_min = distance;
                    moteName = e.nomMote;
                    nomSimple = e.nomMoteSimple;
                }
            }
            ClosestMoteService.ClosestMote = moteName;
            ClosestMoteService.ClosestMoteSimple = nomSimple;
        }
    }

    LocationListener[] mLocationListeners = new LocationListener[]{
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        assetManager = getAssets();
        try {
            is = assetManager.open("motes_information.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }
        br = new BufferedReader(new InputStreamReader(is));
        listEmp = new ArrayList<>();
        if (br != null) {
            String numMote;
            Emplacement emp;
            try {
                CSVReader reader = new CSVReader(br);
                String[] nextLine;
                while ((nextLine = reader.readNext()) != null) {
                    // nextLine[] is an array of values from the line
                    emp = new Emplacement();
                    emp.Longitude = Float.valueOf(nextLine[1]).floatValue();
                    emp.Latutude = Float.valueOf(nextLine[0]).floatValue();
                    numMote = nextLine[2];
                    emp.nomMote = numMote;
                    emp.nomMoteSimple = nextLine[3];
                    listEmp.add(emp);
                }
            } catch (IOException e) {

            }
        }
        Log.e(TAG, "onCreate");
        initializeLocationManager();
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[1]);


        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[0]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");
        super.onDestroy();
        if (mLocationManager != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    mLocationManager.removeUpdates(mLocationListeners[i]);
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listners, ignore", ex);
                }
            }
        }
    }

    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }
    private class Emplacement {
        public String nomMote;
        public String nomMoteSimple;
        public Float Longitude;
        public Float Latutude;

        @Override
        public String toString() {
            return "Emplacement{" +
                    "Longitude='" + Longitude + '\'' +
                    ", Latutude='" + Latutude + '\'' +
                    '}';
        }
    }

    public static String getClosestMote(){
        return ClosestMoteService.ClosestMote;
    }
    public static String getClosestMoteNomSimple(){
        return ClosestMoteService.ClosestMoteSimple;
    }
}
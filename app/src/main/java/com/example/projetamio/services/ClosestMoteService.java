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
 * Service permettant de récupérer les informations GPS de l'application
 */
public class ClosestMoteService extends Service {

    /**
     * Tag utilisé pour les logs
     */
    private static final String TAG = ClosestMoteService.class.getName();

    /**
     * Emplacement actuel
     */
    private LocationManager mLocationManager = null;

    /**
     * Interval auquel la localisation est actualisée
     */
    private static final int LOCATION_INTERVAL = 1000;

    /**
     * Distance de localisation en Hexadécimal
     */
    private static final float LOCATION_DISTANCE = 10f;

    /**
     * Mote la plus proche avec son numéro de mote
     */
    private static String closestMote;

    /**
     * Mote la plus proche avec son nom simplifié
     */
    private static String closestMoteSimple;

    /**
     * Gestionnaire de fichiers extérieurs au projet
     */
    private AssetManager assetManager;

    /**
     * Input stream contenant les informations sur les motes
     */
    private InputStream is;

    /**
     * Buffer contenant les informations extrais du CSV des motes
     */
    private BufferedReader br = null;

    /**
     * Sauvegarde des emplacements récupérées du fichier CSV
     */
    private ArrayList<Emplacement> listEmp;

    /**
     * Classe appelée lors d'un évènement sur la localisation
     */
    private class LocationListener implements android.location.LocationListener {

        /**
         * Emplacement actuel
         */
        Location mLastLocation;

        /**
         * Constructeur de la méthode
         * @param provider Nom donné à la localisation actuelle
         */
        public LocationListener(String provider) {
            mLastLocation = new Location(provider);
            mLastLocation.set(mLastLocation);

        }

        /**
         * Méthode éxecutée lorsqu'un changement de localisation ce passe
         * @param location Données de localisation
         */
        @Override
        public void onLocationChanged(Location location) {
            executeCalcul(location);

        }

        /**
         * Méthode appelé lors d'un changement de statu du GPS
         * @param provider Nom de la localisation
         * @param status Nouveau statu
         * @param extras Données supplémentaires
         */
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // Do nothing because it's useless on our project now
        }

        /**
         * Méthode appelée lorsque la localisation est désactivée
         * @param provider Nom de la localisation
         */
        @Override
        public void onProviderDisabled(String provider) {
            // Do nothing because it's useless on our project now
        }
        /**
         * Méthode appelée lorsque la localisation est activée
         * @param provider Nom de la localisation
         */
        @Override
        public void onProviderEnabled(String provider) {
            // Do nothing because it's useless on our project now
        }

        /**
         * Fonction calculant la distance entre la position actuelle et celle des motes
         * @param location Localisation actuelle
         */
        private void executeCalcul(Location location){
            float distanceMin = -1;
            float distance;
            String moteName = "";
            String nomSimple = "";
            float[] res = new float[1];
            for (Emplacement e: listEmp) {
                Location.distanceBetween(location.getLatitude(), location.getLongitude(), e.latitude, e.longitude, res );
                distance =  res[0];
                if (distance < distanceMin || distanceMin == -1){
                    distanceMin = distance;
                    moteName = e.nomMote;
                    nomSimple = e.nomMoteSimple;
                }
            }
            ClosestMoteService.setClosestMote(moteName);
            ClosestMoteService.setClosestMoteSimple(nomSimple);
        }
    }

    /**
     * Création des listener se basant sur la localisation GPS ou par réseau
     */
    LocationListener[] mLocationListeners = new LocationListener[]{
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };

    /**
     * Méthode appelée lorsque le service est bind
     * @param arg0 Intent d'appel
     * @return ???
     */
    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    /**
     * Méthode appelée lorsque le service est crée et permettant d'extraire les données issues deu
     * fichier CSV fourni
     */
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

        // Extraction des données du CSV
        String numMote;
        Emplacement emp;
        try {
            try(CSVReader reader = new CSVReader(br)) {
                String[] nextLine;
                while ((nextLine = reader.readNext()) != null) {
                    // nextLine[] is an array of values from the line
                    emp = new Emplacement();
                    emp.longitude = Float.parseFloat(nextLine[1]);
                    emp.latitude = Float.parseFloat(nextLine[0]);
                    numMote = nextLine[2];
                    emp.nomMote = numMote;
                    emp.nomMoteSimple = nextLine[3];
                    listEmp.add(emp);
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "Extraction impossible des données du XML");
        }
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

    /**
     * Méthode appelée lorsque le service est arrêté
     */
    @Override
    public void onDestroy() {
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

    /**
     * Initialise la localisation en définissant le context
     */
    private void initializeLocationManager() {
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }

    /**
     * Classe permettant de stocker facilement les données extraies des fichiers CSV
     */
    private class Emplacement {
        /**
         * Peremt de conserver le nom de la mote
         */
        public String nomMote;

        /**
         * Permet de conserver la salle de la mote
         */
        public String nomMoteSimple;

        /**
         * Permet de conserver la Longitude de la mote
         */
        public Float longitude;

        /**
         * Permet de conserver la Latitude de la mote
         */
        public Float latitude;

        /**
         * Méthode permettant d'afficher les données contenues dans la classe
         * @return Les données de la classe sous forme de string
         */
        @Override
        public String toString() {
            return "Emplacement{" +
                    "Longitude='" + longitude + '\'' +
                    ", Latutude='" + latitude + '\'' +
                    '}';
        }
    }

    /**
     * Méthode permettant de récupérer la mote la plus proche
     * @return Numéro de la mote la plus proche
     */
    public static synchronized String getClosestMote(){
        return ClosestMoteService.closestMote;
    }

    /**
     * Méthode permettant de récupérer l'emplacement de la mote la plus proche
     * @return Emplacement de la mote la plus proche
     */
    public static synchronized String getClosestMoteNomSimple(){
        return ClosestMoteService.closestMoteSimple;
    }

    private static synchronized void setClosestMote(String nomMote){
        closestMote = nomMote;
    }

    private static synchronized void setClosestMoteSimple(String nomMote){
        closestMoteSimple = nomMote;
    }
}
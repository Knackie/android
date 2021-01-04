package com.example.projetamio.activity;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.projetamio.R;
import com.example.projetamio.config.Parameters;
import com.example.projetamio.services.ClosestMoteService;
import com.example.projetamio.services.DatareceiverFromServerService;
import com.example.projetamio.services.MainService;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.text.DateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    public static final String PREFS_NAME = Parameters.PrefName;

    /**
     * Indique si le téléchargement est en cours
     */
    private boolean downloading = false;

    /**
     * Variable utilisée pour actualiser la date de dernière mise à jour réussite
     */
    private Handler handler = new Handler();

    /**
     * Intent gérant la partie localisation des motes
     */
    private Intent servicelocatlisation;


    private Runnable run = new Runnable() {
        @Override
        public void run() {
            TextView lastDate = findViewById(R.id.dataLastUpdateMA);
            Date lastDownald = DatareceiverFromServerService.getLastDateDownload();
            // Gestion de la date de derinère mise à jour

            if (lastDownald != null ){
                if (lastDate != null) {
                    lastDate.setText(DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(lastDownald));
                }
            }
            else{
                if (lastDate != null) {
                    lastDate.setText(getString(R.string.newer));
                }
            }
            handler.postDelayed(this, 1000);
        }
    };

    private Runnable updateLocationMote = new Runnable() {
        @Override
        public void run() {
            TextView lastDate = findViewById(R.id.valueMoteSoon);
            lastDate.setText(ClosestMoteService.getClosestMoteNomSimple());
            handler.postDelayed(this, 1000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * Fonction permettant la création du menu
     * @param menu Menu que l'on souhaite afficher
     * @return Indique si l'on a utilisé la fonction
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * Fonction permettant de définir les actions à effectuer lorsque l'on clique sur un bouton du menu
     * @param item Element sur lequel l'utilisateur a appuyé
     * @return la fonction onOptionsItemSelected du niveau supérieur
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here
        int id = item.getItemId();
        Intent preferencesIntent = new Intent(this, SettingsActivity.class);
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(preferencesIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Fonction appelée à la création de la vue
     */
    @Override
    public void onStart() {
        super.onStart();
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        Task<LocationSettingsResponse> result =
                LocationServices.getSettingsClient(this).checkLocationSettings(builder.build());

        MainActivity save = this;



        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                try {
                    Log.d(this.getClass().getName(), "Avant");
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    Log.d(this.getClass().getName(), "Après");
                    // All location settings are satisfied. The client can initialize location
                    // requests here.
                } catch (ApiException exception) {
                    Log.d(this.getClass().getName(), "Exception");
                    switch (exception.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied. But could be fixed by showing the
                            // user a dialog.
                            try {
                                // Cast to a resolvable exception.
                                ResolvableApiException resolvable = (ResolvableApiException) exception;
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                resolvable.startResolutionForResult(
                                        MainActivity.this,
                                        LocationRequest.PRIORITY_HIGH_ACCURACY);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            } catch (ClassCastException e) {
                                // Ignore, should be an impossible error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. However, we have no way to fix the
                            // settings so we won't show the dialog.
                            break;
                    }
                }
            }
        });
        //Gestion de l'affichage de l'état au démarrage de l'app

        Intent service = new Intent(this, MainService.class);
        servicelocatlisation = new Intent(this, ClosestMoteService.class);
        startService(servicelocatlisation);
        ImageView status = findViewById(R.id.imageDownloadStatus);
        if (MainService.active){
            status.setImageResource(R.drawable.power_on);
        }
        else{
            status.setImageResource(R.drawable.power_off);
        }

        // Gestion du bouton on/off
        Button on = findViewById(R.id.toggle);
        on.setOnClickListener(v -> {
            if (!MainService.active) {
                status.setImageResource(R.drawable.power_on);
                startService(service);
            } else {
                status.setImageResource(R.drawable.power_off);
                stopService(service);
            }
            downloading = !downloading;
        });

        // Gestion du bouton d'afffichage des Motes

        Button showMote = findViewById(R.id.viewMoteButton);
        showMote.setOnClickListener(v -> {
            Intent intentApp = new Intent(MainActivity.this,
                    ListMoteActivity.class);
            MainActivity.this.startActivity(intentApp);
        });

        handler.post(run);
        handler.post(updateLocationMote);

        // Gestion de la case à cocher permettant le lancement de l'app au démarrage du téléphone

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(this.servicelocatlisation);
        handler.removeCallbacks(run);
        handler.removeCallbacks(updateLocationMote);
    }
}
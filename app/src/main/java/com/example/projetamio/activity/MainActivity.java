package com.example.projetamio.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.projetamio.R;
import com.example.projetamio.config.Parameters;
import com.example.projetamio.services.ClosestMoteService;
import com.example.projetamio.services.DatareceiverFromServerService;
import com.example.projetamio.services.MainService;

import java.text.DateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    public static final String PREFS_NAME = Parameters.PrefName;
    private static final int PERMISSION_REQUEST_CODE = 1000;

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

    /**
     * Variable permettant de gérer la demande de localisation
     */
    private Boolean locationEnble = false;

    /*private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new RequestPermission(), isGranted -> {
                if (isGranted) {
                    // Permission is granted. Continue the action or workflow in your
                    // app.
                } else {
                    // Explain to the user that the feature is unavailable because the
                    // features requires a permission that the user has denied. At the
                    // same time, respect the user's decision. Don't link to system
                    // settings in an effort to convince the user to change their
                    // decision.
                }
            });*/





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
            if (!locationEnble){

                locationEnble = true;
            }
            TextView lastDate = findViewById(R.id.valueMoteSoon);
            String text = ClosestMoteService.getClosestMoteNomSimple();
            if (text == null){
                lastDate.setText(getString(R.string.newer));
            }
            else{
                lastDate.setText(text);
            }

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
        servicelocatlisation = new Intent(this, ClosestMoteService.class);
        int permissionCheck = ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                showExplanation(getString(R.string.title_add_location), getString(R.string.text_add_location), Manifest.permission.ACCESS_FINE_LOCATION, PERMISSION_REQUEST_CODE);
            } else {
                requestPermission(Manifest.permission.ACCESS_FINE_LOCATION, PERMISSION_REQUEST_CODE);
            }
        } else {
            startService(servicelocatlisation);
            handler.post(updateLocationMote);
        }
        //Gestion de l'affichage de l'état au démarrage de l'app

        Intent service = new Intent(this, MainService.class);
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
    public void onRequestPermissionsResult(
            int requestCode,
            String permissions[],
            int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Toast.makeText(MainActivity.this, "Permission Granted!", Toast.LENGTH_SHORT).show();
                    startService(servicelocatlisation);
                    handler.post(updateLocationMote);

                } else {
                    Toast.makeText(MainActivity.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
                }
        }
    }


    private void showExplanation(String title,
                                 String message,
                                 final String permission,
                                 final int permissionRequestCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        requestPermission(permission, permissionRequestCode);
                    }
                });
        builder.create().show();
    }

    private void requestPermission(String permissionName, int permissionRequestCode) {
        ActivityCompat.requestPermissions(this,
                new String[]{permissionName}, permissionRequestCode);
    }
}
package com.example.projetamio.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projetamio.R;
import com.example.projetamio.config.Parameters;
import com.example.projetamio.services.DatareceiverFromServerService;
import com.example.projetamio.services.MainService;

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





        // Gestion de la case à cocher permettant le lancement de l'app au démarrage du téléphone

        CheckBox startAtBoot = findViewById(R.id.startAtBoot);
        startAtBoot.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("start At Boot", isChecked);
            editor.apply();
        });
    }
}
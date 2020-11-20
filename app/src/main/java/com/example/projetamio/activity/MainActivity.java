package com.example.projetamio.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.example.projetamio.services.MainService;
import com.example.projetamio.R;
import com.example.projetamio.config.Parameters;

public class MainActivity extends AppCompatActivity {

    public static final String PREFS_NAME = Parameters.PrefName;

    // Boolean telling us whether a download is in progress
    private boolean downloading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

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

    @Override
    public void onStart() {
        super.onStart();

        Intent service = new Intent(this, MainService.class);

        ImageView status = findViewById(R.id.imageDownloadStatus);
        if (MainService.active){
            status.setImageResource(R.drawable.power_on);
        }
        else{
            status.setImageResource(R.drawable.power_off);
        }

        // Button on management
        Button on = findViewById(R.id.toggle);
        on.setOnClickListener(v -> {
            if (downloading) {
                status.setImageResource(R.drawable.power_on);
                startService(service);
            } else {
                status.setImageResource(R.drawable.power_off);
                stopService(service);
            }
            downloading = !downloading;
        });

        Button showMote = findViewById(R.id.viewMoteButton);
        showMote.setOnClickListener(v -> {
            Intent intentApp = new Intent(MainActivity.this,
                    ListMoteActivity.class);
            MainActivity.this.startActivity(intentApp);
        });

        CheckBox startAtBoot = findViewById(R.id.startAtBoot);
        startAtBoot.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("start At Boot", isChecked);
            editor.apply();
        });
    }
}
package com.example.projetamio.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.example.projetamio.datamanagement.ListLampe;
import com.example.projetamio.services.MainService;
import com.example.projetamio.R;
import com.example.projetamio.config.Parameters;

public class MainActivity extends AppCompatActivity{

    private static int status = 0;
    public static final String PREFS_NAME = Parameters.PrefName;
    // Keep a reference to the NetworkFragment, which owns the AsyncTask object
    // that is used to execute network ops.
//    private NetworkFragment networkFragment;

    // Boolean telling us whether a download is in progress, so we don't trigger overlapping
    // downloads with consecutive button clicks.
    private boolean downloading = false;

    private ListLampe listLampe;

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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Intent preferencesIntent = new Intent(this, Settings.class);
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

        // Gestion du bouton toggle

        Button on = findViewById(R.id.toggle);
        on.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 ImageView status = findViewById(R.id.TV2);
                switch (MainActivity.status){
                    case 0 :
                        status.setImageResource(R.drawable.power_on);
                        startService(service);
                        MainActivity.status = 1;
                        downloading = true;
                        break;
                    case 1 :
                        status.setImageResource(R.drawable.power_off);
                        stopService(service);
                        MainActivity.status = 0;
                        break;
                    default:
                        MainActivity.status = 10;
                }
            }
        });

            Button showMote = findViewById(R.id.viewMoteButton);
            showMote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // doStuff
                    Intent intentApp = new Intent(MainActivity.this,
                            ListMoteActivity.class);

                    MainActivity.this.startActivity(intentApp);

                }
        });

        CheckBox startAtBoot = findViewById(R.id.startAtBoot);
        startAtBoot.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String etat = "";
                if (isChecked){
                    etat = "coché";
                }
                else{
                    etat = "décoché";
                }
                SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("start At Boot", isChecked);
                editor.commit();
            }
        });
    }
}
package com.example.projetamio.services;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.example.projetamio.config.Parameters;
import com.example.projetamio.R;

public class SendEmailService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Intent emailIntent = new Intent();
        emailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        emailIntent.setAction(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        String PREFS_NAME = Parameters.PrefName;
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        String destEmail = settings.getString("emailString", Parameters.DefaultEmail);
        emailIntent.putExtra(Intent.EXTRA_EMAIL, destEmail);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_light_subject));
        emailIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.email_light_content));
        emailIntent.setType("text/plain");
        Intent chooserIntent = Intent.createChooser(emailIntent,"Envoyer à ");
        chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(chooserIntent);
        this.stopSelf();
    }

}

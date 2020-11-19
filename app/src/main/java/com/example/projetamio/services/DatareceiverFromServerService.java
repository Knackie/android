package com.example.projetamio.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.JsonReader;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.example.projetamio.datamanagement.DonneesLampe;
import com.example.projetamio.datamanagement.ListLampe;
import com.example.projetamio.config.Parameters;
import com.example.projetamio.R;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.InterruptedByTimeoutException;
import java.util.Calendar;

public class DatareceiverFromServerService extends Service implements DownloadCallback {

    private static final String PREFS_NAME = Parameters.PrefName;
    private boolean downloading;
//    private NetworkFragment networkFragment;
    private ListLampe listLampe;

    private static DatareceiverFromServerService instance = null;
    private DownloadTask downloadTask;
    private DownloadCallback<String> callback;

    public DatareceiverFromServerService(){
        super();
        this.listLampe = ListLampe.getInstance();
        callback = (DownloadCallback<String>) this;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Function create manually
     */
    @Override
    public void onCreate()  {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startMyOwnForeground();
        else
            startForeground(1, new Notification());
        this.startDownload();
        this.stopSelf();
    }

    private void startMyOwnForeground(){
        String NOTIFICATION_CHANNEL_ID = "com.example.simpleapp";
        String channelName = "My Background Service";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("App is running in background")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(2, notification);
    }

    /**
     * Start non-blocking execution of DownloadTask.
     */
    public void startDownload() {
        downloadTask = new DownloadTask(callback);
        downloadTask.execute(Parameters.URLData);
        Log.d(this.getClass().getName(), "Téléchargement lancé");
    }


    /**
     * {
     *       "timestamp": 1603588279000,
     *       "label": "temperature",
     *       "value": -32.76,
     *       "mote": "53.105"
     *     }
     * @param result
     */
    @Override
    public void updateFromDownload(Object result){
        boolean changement = false, res;
        if (result instanceof String) {
            String resultString = (String)result;
            Log.d(this.getClass().getName(), resultString);
            if (resultString.contains("HTTP error code:") || resultString.contains("no protocol:")) {
                CharSequence text = "Erreur lors du chargement des données";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(this.getBaseContext(), text, duration);
                toast.show();
            }
            else{
                JsonReader reader = null;
                try {
                    reader = new JsonReader(new InputStreamReader(new ByteArrayInputStream(resultString.getBytes()), "UTF-8"));
                    reader.beginObject();
                    DonneesLampe lampe = null;
                    TempInfoMote tmpMoteInfo = null;
                    while (reader.hasNext()) {
                        if (reader.nextName().equals("data")) {
                            reader.beginArray();
                            while (reader.hasNext()) {
                                reader.beginObject();
                                tmpMoteInfo = new TempInfoMote();
                                while (reader.hasNext()) {
                                    String name = reader.nextName();
                                    if (name.equals("mote")) {
                                        tmpMoteInfo.name = reader.nextString();
                                    } else if (name.equals("timestamp")) {
                                        tmpMoteInfo.timestamp = reader.nextLong();
                                    } else if (name.equals("value")) {
                                        tmpMoteInfo.value = reader.nextDouble();
                                    } else if (name.equals("label")) {
                                        tmpMoteInfo.label = reader.nextString();
                                    }else {
                                        reader.skipValue();
                                    }
                                }
                                reader.endObject();
                                lampe = this.listLampe.getLampe(tmpMoteInfo.name);
                                boolean prevState = lampe.isEtat();
                                lampe.addEtat(tmpMoteInfo.value, tmpMoteInfo.label, tmpMoteInfo.timestamp);
                                if (lampe.isEtat() != prevState && lampe.isEtat()){
                                    changement = true;
                                }
                            }
                            reader.endArray();
                        }
                        else {
                            reader.skipValue();
                        }
                    }
                    reader.endObject();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {

                } finally {
                    assert reader != null;
                    try {
                        reader.close();
                        Log.d(this.getClass().getName(), "Chargement réussi");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        if (changement){
            Log.d(this.getClass().getName(), "Changement detecté");
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                //deprecated in API 26
                v.vibrate(500);
            }

            if (this.isInEmailHour()){
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
            }
            if  (this.isNotificationHour()){
                // The id of the channel.
                String id = "my_channel_01";
                Notification.Builder notificationBuilder = new Notification.Builder(this, id);

                notificationBuilder.setAutoCancel(true)
                        .setWhen(System.currentTimeMillis())
                        .setSmallIcon(R.drawable.power_on)
                        .setTicker("projetAMIO")
                        .setContentTitle(getString(R.string.notification_title))
                        .setContentText(getString(R.string.notification_desc));

                NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

// The user-visible name of the channel.
                CharSequence name = getString(R.string.channel_name);

// The user-visible description of the channel.
                String description = getString(R.string.channel_description);

                int importance = NotificationManager.IMPORTANCE_LOW;

                NotificationChannel mChannel = new NotificationChannel(id, name,importance);

// Configure the notification channel.
                mChannel.setDescription(description);

                mChannel.enableLights(true);
// Sets the notification light color for notifications posted to this
// channel, if the device supports this feature.
                mChannel.setLightColor(Color.RED);

                mChannel.enableVibration(true);
                mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});

                notificationManager.createNotificationChannel(mChannel);
                notificationManager.notify(1, notificationBuilder.build());
            }

        }


    }

    private boolean isNotificationHour() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);

        // On vérifie que les notifications n'ont pas été desactivées par l'utilisateur
        if (settings.getBoolean("notification_switch_enable", false)){

            if  (Parameters.isWeekDay(Calendar.getInstance().get(Calendar.DAY_OF_WEEK))){

                // On récupère les heures et dates en string, il faut donc les convertir en Int

                String[] stringDebut = settings.getString("hour_begin_notification", "00:00").split(":");
                String[] stringEnd = settings.getString("hour_end_notification", "00:00").split(":");
                int hoursBegin = Integer.parseInt(stringDebut[0]), minuteBegin = Integer.parseInt(stringDebut[1]);
                int hoursEnd = Integer.parseInt(stringEnd[0]), minuteEnd = Integer.parseInt(stringEnd[1]);
                int hnow = Calendar.getInstance().get(Calendar.HOUR_OF_DAY), mnow =  Calendar.getInstance().get(Calendar.MINUTE);
                if (hoursBegin < hnow && hnow < hoursEnd){
                    return true;
                }
                else if (hoursBegin == hnow){
                    if (minuteBegin <= mnow){
                        return true;
                    }
                }else if (hoursEnd == hnow){
                    if (mnow <= minuteEnd){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean isInEmailHour() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);

        // On vérifie que les notifications n'ont pas été desactivées par l'utilisateur
        if (settings.getBoolean("email_switch_enable", false)){

            if  (Parameters.isWeekDay(Calendar.getInstance().get(Calendar.DAY_OF_WEEK))){

                // On récupère les heures et dates en string, il faut donc les convertir en Int

                String[] stringDebut = settings.getString("hour_begin_email", "00:00").split(":");
                String[] stringEnd = settings.getString("hour_end_email", "00:00").split(":");
                int hoursBegin = Integer.parseInt(stringDebut[0]), minuteBegin = Integer.parseInt(stringDebut[1]);
                int hoursEnd = Integer.parseInt(stringEnd[0]), minuteEnd = Integer.parseInt(stringEnd[1]);
                int hnow = Calendar.getInstance().get(Calendar.HOUR_OF_DAY), mnow =  Calendar.getInstance().get(Calendar.MINUTE);
                if (hoursBegin < hnow && hnow < hoursEnd){
                    return true;
                }
                else if (hoursBegin == hnow){
                    if (minuteBegin <= mnow){
                        return true;
                    }
                }else if (hoursEnd == hnow){
                    if (mnow <= minuteEnd){
                        return true;
                    }
                }
            }
            else if (Parameters.isWeekEndDay(Calendar.getInstance().get(Calendar.DAY_OF_WEEK))){
                String[] stringDebut = settings.getString("hour_begin_email_weekend", "00:00").split(":");
                String[] stringEnd = settings.getString("hour_end_email_weekend", "00:00").split(":");
                int hoursBegin = Integer.parseInt(stringDebut[0]), minuteBegin = Integer.parseInt(stringDebut[1]);
                int hoursEnd = Integer.parseInt(stringEnd[0]), minuteEnd = Integer.parseInt(stringEnd[1]);
                int hnow = Calendar.getInstance().get(Calendar.HOUR_OF_DAY), mnow =  Calendar.getInstance().get(Calendar.MINUTE);
                if (hoursBegin < hnow && hnow < hoursEnd){
                    return true;
                }
                else if (hoursBegin == hnow){
                    if (minuteBegin <= mnow){
                        return true;
                    }
                }else if (hoursEnd == hnow){
                    if (mnow <= minuteEnd){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public NetworkInfo getActiveNetworkInfo() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo;
    }

    @Override
    public void onProgressUpdate(int progressCode, int percentComplete) {
        switch(progressCode) {
            // You can add UI behavior for progress updates here.
            case DownloadCallback.Progress.ERROR:
                /**CharSequence text = "Erreur lors du chargement des données";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(this.getBaseContext(), text, duration);
                toast.show();*/
                Log.e(this.getClass().getName(), "Erreur lors du téléchargement");
                break;
            case DownloadCallback.Progress.CONNECT_SUCCESS:
                break;
            case DownloadCallback.Progress.GET_INPUT_STREAM_SUCCESS:
                break;
            case DownloadCallback.Progress.PROCESS_INPUT_STREAM_IN_PROGRESS:
                break;
            case DownloadCallback.Progress.PROCESS_INPUT_STREAM_SUCCESS:
                break;
        }
    }

    @Override
    public void finishDownloading() {
        downloading = false;
        this.cancelDownload();
        Log.d(this.getClass().getName(), "Données : " + this.listLampe.toString());
    }
    /**
     * Cancel (and interrupt if necessary) any ongoing DownloadTask execution.
     */
    public void cancelDownload() {
        if (downloadTask != null) {
            downloadTask.cancel(true);
        }
    }

    /**
     * Implementation of AsyncTask designed to fetch data from the network.
     */
    private static class DownloadTask extends AsyncTask<String, Integer, DownloadTask.Result> {

        private DownloadCallback<String> callback;

        DownloadTask(DownloadCallback<String> callback) {
            setCallback(callback);
        }

        void setCallback(DownloadCallback<String> callback) {
            this.callback = callback;
        }

        /**
         * Wrapper class that serves as a union of a result value and an exception. When the download
         * task has completed, either the result value or exception can be a non-null value.
         * This allows you to pass exceptions to the UI thread that were thrown during doInBackground().
         */
        class Result {
            public String resultValue;
            public Exception exception;
            public Result(String resultValue) {
                this.resultValue = resultValue;
            }
            public Result(Exception exception) {
                this.exception = exception;
            }
        }

        /**
         * Cancel background network operation if we do not have network connectivity.
         */
        @Override
        protected void onPreExecute() {
            if (callback != null) {
                NetworkInfo networkInfo = callback.getActiveNetworkInfo();
                if (networkInfo == null || !networkInfo.isConnected() ||
                        (networkInfo.getType() != ConnectivityManager.TYPE_WIFI
                                && networkInfo.getType() != ConnectivityManager.TYPE_MOBILE)) {
                    // If no connectivity, cancel task and update Callback with null data.
                    callback.updateFromDownload(null);
                    cancel(true);
                }
            }
        }

        /**
         * Defines work to perform on the background thread.
         */
        @Override
        protected DownloadTask.Result doInBackground(String... urls) {
            Result result = null;
            if (!isCancelled() && urls != null && urls.length > 0) {
                String urlString = urls[0];
                try {
                    URL url = new URL(urlString);
                    String resultString = downloadUrl(url);
                    if (resultString != null) {
                        result = new Result(resultString);
                    } else {
                        throw new IOException("No response received.");
                    }
                } catch(Exception e) {
                    result = new Result(e);
                }
            }
            return result;
        }

        /**
         * Updates the DownloadCallback with the result.
         */
        @Override
        protected void onPostExecute(Result result) {
            if (result != null && callback != null) {
                if (result.exception != null) {
                    callback.updateFromDownload(result.exception.getMessage());
                } else if (result.resultValue != null) {
                    callback.updateFromDownload(result.resultValue);
                }
                callback.finishDownloading();
            }
        }

        /**
         * Override to add special behavior for cancelled AsyncTask.
         */
        @Override
        protected void onCancelled(Result result) {
        }

        /**
         * Given a URL, sets up a connection and gets the HTTP response body from the server.
         * If the network request is successful, it returns the response body in String form. Otherwise,
         * it will throw an IOException.
         */
        private String downloadUrl(URL url) throws IOException {
            InputStream stream = null;
            HttpURLConnection connection = null;
            String result = null;
            try {
                connection = (HttpURLConnection) url.openConnection();
                // Timeout for reading InputStream arbitrarily set to 3000ms.
                connection.setReadTimeout(3000);
                // Timeout for connection.connect() arbitrarily set to 3000ms.
                connection.setConnectTimeout(3000);
                // For this use case, set HTTP method to GET.
                connection.setRequestMethod("GET");
                // Already true by default but setting just in case; needs to be true since this request
                // is carrying an input (response) body.
                connection.setDoInput(true);
                // Open communications link (network traffic occurs here).
                connection.connect();
                publishProgress(DownloadCallback.Progress.CONNECT_SUCCESS);
                int responseCode = connection.getResponseCode();
                if (responseCode != HttpURLConnection.HTTP_OK) {
                    throw new IOException("HTTP error code: " + responseCode);
                }
                // Retrieve the response body as an InputStream.
                stream = connection.getInputStream();
                publishProgress(DownloadCallback.Progress.GET_INPUT_STREAM_SUCCESS, 0);
                if (stream != null) {
                    // Converts Stream to String with max length of 500.
                    result = readStream(stream, 100000);
                }
            } finally {
                // Close Stream and disconnect HTTPS connection.
                if (stream != null) {
                    stream.close();
                }
                if (connection != null) {
                    connection.disconnect();
                }
            }
            return result;
        }
        /**
         * Converts the contents of an InputStream to a String.
         */
        public String readStream(InputStream stream, int maxReadSize)
                throws IOException, UnsupportedEncodingException {
            Reader reader = null;
            reader = new InputStreamReader(stream, "UTF-8");
            char[] rawBuffer = new char[maxReadSize];
            int readSize;
            StringBuffer buffer = new StringBuffer();
            while (((readSize = reader.read(rawBuffer)) != -1) && maxReadSize > 0) {
                if (readSize > maxReadSize) {
                    readSize = maxReadSize;
                }
                buffer.append(rawBuffer, 0, readSize);
                maxReadSize -= readSize;
            }
            return buffer.toString();
        }
    }

    private class TempInfoMote{
        public String name;
        public double value;
        public String label;
        public long timestamp;
    }
}

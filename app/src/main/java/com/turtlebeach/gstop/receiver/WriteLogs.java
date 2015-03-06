package com.turtlebeach.gstop.receiver;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.text.format.Time;
import android.util.Log;

import com.turtlebeach.gstop.R;
import com.turtlebeach.gstop.headsets.Constants;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by emezias on 3/5/15.
 */
public class WriteLogs extends IntentService {
    public static final String TAG = "WriteLogs";

    public WriteLogs() {
        super(TAG);
    }

    /**
     * send a broadcast with extras to indicate there was an error
     */
    void sendBroadcast(boolean error) {
        final Intent tnt = new Intent(TAG);
        if(error) {
            tnt.putExtra(TAG, true);
        }

        LocalBroadcastManager.getInstance(this).sendBroadcast(tnt);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //set a file
        final Time t = new Time();
        t.setToNow();

        //TODO change to sd card
        final File file = new File (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                t.format3339(true)+"_tbeach.txt");
        file.setReadable(true);
        file.setWritable(true);
        //clears any existing file
        if(file.exists()){
            file.delete();
        }

        //write log to file, using root gets the complete log buffer output to the file
        try {
            //pre-pend device data into the file
            final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            //Begin the file with the data from the admin page
            final StringBuilder result = new StringBuilder()
                    .append(Constants.STORE_NUMBER).append(": ")
                    .append(prefs.getString(Constants.STORE_NUMBER, Constants.STORE_NUMBER)).append("\n")
                    .append(Constants.STORE_ZIP).append(": ")
                    .append(prefs.getString(Constants.STORE_ZIP, Constants.STORE_ZIP)).append("\n")
                    .append(getString(R.string.adminManLabel)).append(": ").append(Build.MANUFACTURER).append("\n")
                    .append(getString(R.string.adminVerLabel)).append(": ").append(Build.VERSION.RELEASE).append("\n\n");

            final Process process = Runtime.getRuntime().exec("su -c logcat -d -v long");
            final BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String currentLine = null;

            while ((currentLine = reader.readLine()) != null) {
                result.append(currentLine).append("\n");
            }

            FileWriter out = new FileWriter(file);
            out.write(result.toString());
            out.close();
            Log.i(TAG, "log service complete, file closed");
            sendBroadcast(false);
            //false means no errors
        } catch (IOException e) {
            Log.e(TAG, "error in log service: " + e.getMessage());
            sendBroadcast(true);
        }
    }
}

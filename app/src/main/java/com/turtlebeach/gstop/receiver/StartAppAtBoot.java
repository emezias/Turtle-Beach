package com.turtlebeach.gstop.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.turtlebeach.gstop.activities.SplashScreenActivity;

public class StartAppAtBoot extends BroadcastReceiver {
	private static final String TAG = "StartAppAtBoot";
	
    @Override
    public void onReceive(Context context, Intent intent) {
    	
		final SharedPreferences peace = PreferenceManager
				.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = peace.edit();
		editor.putBoolean("REBOOT", true);
		editor.commit();
    	
    	//Start Kiosk app
    	Intent tnt = new Intent(context, SplashScreenActivity.class);
    	tnt.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(tnt);
        Log.v(TAG, "send report, startActivity after reboot");
    }
}

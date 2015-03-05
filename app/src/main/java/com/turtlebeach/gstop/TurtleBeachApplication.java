package com.turtlebeach.gstop;

import java.io.File;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.format.Time;
import android.util.Log;

import com.turtlebeach.gstop.activities.DemoVideo;
import com.turtlebeach.gstop.activities.SplashScreenActivity;
import com.turtlebeach.gstop.headsets.Constants;
import com.turtlebeach.gstop.headsets.model.HeadsetManager;
import com.turtlebeach.gstop.headsets.ui.BaseActivity;
import com.turtlebeach.gstop.headsets.update.UpdateService;
import com.turtlebeach.gstop.receiver.PingReportReceiver;

//clearing out cruft with content update
public class TurtleBeachApplication extends Application {
	/**
	 * Tag used for logging. statics removed
	 * the following can be set for Localytics use across activities
	 * public static String sAppVersion, sAppPkg;
	 */
	public static final String TAG = "TurtleBeachApplication";
	
	//Array used to initialize the headset manager with the correct catalog headsets, both special edition and catalog
	//featured headsets sold separately :)
	private static final int[] sBBYHeadsets = { 
		HeadsetManager.HEADSET_ID_NLA, HeadsetManager.HEADSET_ID_P11, HeadsetManager.HEADSET_ID_P12, HeadsetManager.HEADSET_ID_PX4,
		HeadsetManager.HEADSET_ID_PLA, HeadsetManager.HEADSET_ID_PX22, HeadsetManager.HEADSET_ID_STEALTH400, HeadsetManager.HEADSET_ID_STEALTH500P,
		HeadsetManager.HEADSET_ID_X12, HeadsetManager.HEADSET_ID_X32, HeadsetManager.HEADSET_ID_XL1, HeadsetManager.HEADSET_ID_STEALTH500X,
		HeadsetManager.HEADSET_ID_XO4, HeadsetManager.HEADSET_ID_XO1, HeadsetManager.HEADSET_ID_XO7, HeadsetManager.HEADSET_ID_ELITE800,
		HeadsetManager.HEADSET_ID_Z11, HeadsetManager.HEADSET_ID_Z60, HeadsetManager.HEADSET_ID_RECON320, HeadsetManager.HEADSET_ID_COD_PRESTIGE,
		HeadsetManager.HEADSET_ID_HOTS_HEROESOFTHESTORM, HeadsetManager.HEADSET_ID_MARVEL_DISNEYSUPERHEROES, HeadsetManager.HEADSET_ID_SENTINELTASKFORCEPS4,
		HeadsetManager.HEADSET_ID_SENTINELTASKFORCEX1, HeadsetManager.HEADSET_ID_TITANFALL_ATLAS, HeadsetManager.HEADSET_ID_STARWARS
	};

	@SuppressLint("DefaultLocale")
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void onCreate() {
		super.onCreate();
		

		final SharedPreferences peace = PreferenceManager
				.getDefaultSharedPreferences(this);
		HashMap<String, String> attrs = new HashMap<String, String>();
		
		if (peace.contains("REBOOT")) {
			SharedPreferences.Editor editor = peace.edit();
			editor.remove("REBOOT");
			editor.commit();
		}
		
		if (peace.contains("FIRST_RUN")) {
			attrs.put("first_time", "false");
		} else {
			SharedPreferences.Editor editor = peace.edit();
			editor.putBoolean("FIRST_RUN", true);
			editor.commit();
			attrs.put("first_time", "true");
		}

		//Without the array of headsets all headsets are instantiated, this saves space in the heap
		HeadsetManager.init(this, sBBYHeadsets);
		BaseActivity.mDemoListener = (Class) DemoVideo.class;
		// This sets up the demo to play video after 45s
		
		// Only launch an update if it makes sense.  Also, set the flag early so we catch 
		// the update in the Splash activity.  Ideally, update would be triggered from splash...
		File file = new File(Constants.getRootDir());
		if (Constants.isRemoteSDAvailable() && !file.exists()) {
			final Intent intent = new Intent(this, UpdateService.class);
	        intent.setAction(UpdateService.ACTION_UPDATE_CONTENT_FROM_SDCARD);
			startService(intent);
		}
		
		//Run the ping report daily at approximately 11 p.m.
		final Time t = new Time();
		t.setToNow();
		t.hour = 23;
		t.minute = 0;
		t.normalize(false);
		//The broadcast receiver makes a daily call to the IntentService and sends a Kiosk identification event to Localytics
		((AlarmManager) getSystemService(Context.ALARM_SERVICE)).setInexactRepeating(AlarmManager.RTC_WAKEUP, t.toMillis(false),
		        AlarmManager.INTERVAL_DAY, 
		        PendingIntent.getBroadcast(this, 0, new Intent(this, PingReportReceiver.class), 0));
		

		//This code restarts the app in case of errors
		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler(){
        	//ACRA is gone, do we keep the app restart?
            @Override
            public void uncaughtException(final Thread thread, final Throwable ex){
                Log.e(TAG, "CRASH", ex);
                scheduleRestartApp(getBaseContext());
                System.exit(2);
            }
        });
	}
	
	//This is going to restart the app with an uncaught exception (see above)
	public void scheduleRestartApp(Context aContext){
		// Cancel the ping intent.  It will be rescheduled on restart.
		((AlarmManager) getSystemService(Context.ALARM_SERVICE)).cancel(PendingIntent.getBroadcast(this, 0, new Intent(this, PingReportReceiver.class), 0));
		
        final Intent intent = new Intent(aContext, SplashScreenActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        final PendingIntent pendIntent = PendingIntent.getActivity(aContext, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        final AlarmManager mgr = (AlarmManager) aContext.getSystemService(Context.ALARM_SERVICE);
        final long triggerAtTime = System.currentTimeMillis() + 10000; //10s
        mgr.cancel(pendIntent);
        mgr.set(AlarmManager.RTC, triggerAtTime, pendIntent);

        Log.v(TAG, "scheduleRestartApp, currentTime: " + System.currentTimeMillis() + " trigger in 10s " + triggerAtTime);
    }
}

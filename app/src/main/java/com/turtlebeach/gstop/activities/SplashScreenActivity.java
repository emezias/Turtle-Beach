package com.turtlebeach.gstop.activities;

import java.io.IOException;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.turtlebeach.gstop.headsets.Constants;
import com.turtlebeach.gstop.headsets.update.UpdateService;
import com.turtlebeach.gstop.R;
import com.turtlebeach.gstop.headsets.ui.BaseActivity;

/**
 * This activity will show once at app launch and will disable the nav bar
 * https://github.com/ppareit/HideBar/blob/more-devices/src/be/ppareit/hidebar/BackgroundService.java
 * http://android.serverbox.ch/?p=306
 * Refer also to code in the RegistrationDialogFragment
 */
public class SplashScreenActivity extends BaseActivity {
	private static final String TAG = "SplashScreenActivity";
	//public static boolean mUpdate = false;
	
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        try {
            // In lieu of a proper kiosk mode, get the existing environment, run superuser
            // kill the nav bar
            // hard coded value for Acer/Asus, works on both 4.1 and 4.2

            //42 on 4.1.1, maybe 79 on 4.2.2

            // REQUIRES ROOT
            final Process proc = Runtime
                    .getRuntime()
                    .exec(new String[] { "su", "-c",
                            "service call activity 42 s16 com.android.systemui" });
            proc.waitFor();

        } catch (IOException e) {
            Log.e(TAG, "Exception running as root: " + e.getMessage());
            e.printStackTrace();
        } catch (InterruptedException e) {
            Log.e(TAG, "Interrupted: " + e.getMessage());
            e.printStackTrace();
        } //end nav bar hide

        /* Why?
        final Bundle b = getIntent().getExtras();
        if(b != null && !b.containsKey(Admin.TAG)) { }*/
        //normal startup will set the content view, first start will bring up the Admin screen
        if(!PreferenceManager.getDefaultSharedPreferences(this).contains(Constants.STORE_ZIP)) {
        	startActivity(new Intent(this, Admin.class));
        	Log.v(TAG, "first install, starting Admin activity");
        	finish();
        	return;
        }       
        setContentView(R.layout.splash_screen);
        
        //This is a bit of a race, using bool to get update dialog onscreen more quickly, registration and broadcast is too slow
        if(UpdateService.mUpdating) {
        	createContentUpdateProgress();
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event){
    	//The base activity sets a touch listener that gets overridden here
    	super.onTouch(v, event);
    	splashAction();
        return false;
    }  
    
    void splashAction() {
        //no back stack, nothing to clear... intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(new Intent(SplashScreenActivity.this, FeaturedHeadsets.class));
        Log.v(TAG,"start HeadsetSelectorActivity from SplashScreeActivity");
        finish();
    }
    
    public void splashButton(View btn) {
    	//set in XML
    	splashAction();
    }
  
}
    
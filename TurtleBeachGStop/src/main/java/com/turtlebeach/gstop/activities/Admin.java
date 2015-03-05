package com.turtlebeach.gstop.activities;

import java.io.IOException;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.turtlebeach.gstop.R;
import com.turtlebeach.gstop.internet.NWBaseActivity;

/**
 * This activity will show after the user opens the password dialog 
 * It allows users to exit the app and update it or perform maintenance on the tablet
 */
public class Admin extends NWBaseActivity {
	static final String TAG = "Admin";
	public static final String PWD = "062819682014";
	private static SharedPreferences sPrefs;
	//07-11 16:13:53.400: D/MenuActivity(6623): pos click get text is 67434211771

	private OnEditorActionListener done = new OnEditorActionListener() {
	    @Override
	    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
	        boolean handled = false;
	        if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_NULL) {
	            ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).
	            	hideSoftInputFromWindow(v.getWindowToken(), 0);
	            handled = true;
	        }
	        return handled;
	    }
	};
	
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_screen);
        ((TextView)findViewById(R.id.adminManTV)).setText(Build.MANUFACTURER.toUpperCase());
        ((TextView)findViewById(R.id.adminVerTV)).setText(Build.VERSION.RELEASE);
        
        sPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        if(sPrefs.contains(STORE_ZIP)) {
        	//Pre load the EditText fields with the saved values when possible
        	((EditText)findViewById(R.id.adminZip)).setText(
        			sPrefs.getString(STORE_ZIP, ""));
        	((EditText)findViewById(R.id.adminNumber)).setText(
        			sPrefs.getString(STORE_NUMBER, ""));	
        }
        ((EditText)findViewById(R.id.adminZip)).setOnEditorActionListener(done);
        ((EditText)findViewById(R.id.adminNumber)).setOnEditorActionListener(done);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        
        sScreenName = "Admin";

    }
    
    @Override
	protected void onResume() {
		// This is supposed to prevent the app from timing out to the demo video
		super.onResume();
		findViewById(android.R.id.content).setOnTouchListener(new OnTouchListener() {
			// This overrides the touch listener in the BaseActivity which starts the time out to demo.
			// It is not sufficient to just clear mFinishRunnable from the callbaacks since any touch will
			// post the callback.  Note there is no super.onTouch()!
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return false;
			}
		});
		mHandler.removeCallbacks(mFinishRunnable);
	}
    
    @Override
    protected void onPause() {
    	super.onPause();
    }

    @Override
	public boolean onTouch(View v, MotionEvent event) {
    	//do not call super, do not time out to the demo
    	return false;
    }

	public void adminButton(View btn) {
    	switch(btn.getId()) {
    	case R.id.adminCancel:
            //This action is TBD
    		if(!sPrefs.contains(STORE_ZIP)) {
    			//logEvent(this, EVENT_CANCEL_EXIT, null);
    			exitKiosk();
    		} else {
    			//Forget it, go back to the app
    			backToApp("Cancel from AdminActivity");
    		}
    		break;
    	case R.id.adminExit:
			//logEvent(this, EVENT_APP_EXIT, null);
    		exitKiosk();
    		break;
    	case R.id.adminSave:
    		String tmp = ((EditText)findViewById(R.id.adminZip)).getText().toString();
            SharedPreferences.Editor ed = sPrefs.edit();
            ed.putString(STORE_ZIP, tmp);
            ed.putString(STORE_NUMBER,
                    ((EditText)findViewById(R.id.adminNumber)).getText().toString());
            ed.commit();
            updateCustomDimensions(this);
            //logEvent(this, EVENT_SAVED_DATA, null);
            backToApp("Saving store data");
    		break;
        case R.id.adminLog:
            break;
    	}
    	
    }
	
	void backToApp(String logMessage) {
		startActivity(new Intent(Admin.this, SplashScreenActivity.class));
        Log.i(TAG, logMessage);
        finish();
	}
    
    void exitKiosk() {
    	//Exit app selected, every activity that is started must "finish" if this is going to work
    	if(mSession != null) {
			mSession.close();
		    mSession.upload();
		    mSession = null;
		}
		try {	
			// REQUIRES ROOT
			// Restarts the system nav bar
        	Log.i(TAG, "running command to get task bar back");
        	
			((Process) Runtime.getRuntime().exec
					("am startservice --user 0 -n com.android.systemui/.SystemUIService")).waitFor();
							//"su -c am startservice -n com.android.systemui/.SystemUIService")).waitFor();
		} catch (IOException e) {
			Log.e(TAG, "Exception running as root: " + e.getMessage());
			e.printStackTrace();
		} catch (InterruptedException e) {
			Log.e(TAG, "Interrupted: " + e.getMessage());
			e.printStackTrace();
		}
		finish();
    }
  
}
    
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
import com.turtlebeach.gstop.headsets.Constants;
import com.turtlebeach.gstop.R;
import com.turtlebeach.gstop.headsets.ui.BaseActivity;
import com.turtlebeach.gstop.headsets.update.UpdateService;
import com.turtlebeach.gstop.receiver.WriteLogs;

/**
 * This activity will show after the user opens the password dialog 
 * It allows users to exit the app and update it or perform maintenance on the tablet
 */
public class Admin extends BaseActivity {
	static final String TAG = "Admin";
	public static final String PWD = "062819682014";
	private static SharedPreferences sPrefs;
	private static boolean created = false;
    //don't kill the navigation bar every time...

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
        if(!created) {
            hideNavBar();
        }
        //This is a bit of a race, using bool to get update dialog onscreen more quickly, registration and broadcast is too slow
        if(UpdateService.mUpdating) {
            createContentUpdateProgress();
        }

        sPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        if(sPrefs.contains(Constants.STORE_ZIP) && getIntent().getExtras() == null) {
            //this is a reboot, or other app restart - set Extras when exiting the app
            startActivity(new Intent(this, MainScreen.class));
            finish();
            return;
        }
        //check for existing preferences and fill in the fields
        if(sPrefs.contains(Constants.STORE_ZIP)) {
        	//Pre load the EditText fields with the saved values when possible
        	((EditText)findViewById(R.id.adminZip)).setText(
        			sPrefs.getString(Constants.STORE_ZIP, ""));
        	((EditText)findViewById(R.id.adminNumber)).setText(
        			sPrefs.getString(Constants.STORE_NUMBER, ""));
        }
        ((EditText)findViewById(R.id.adminZip)).setOnEditorActionListener(done);
        ((EditText)findViewById(R.id.adminNumber)).setOnEditorActionListener(done);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

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

    void hideNavBar() {
        try {
            // In lieu of a proper kiosk mode, get the existing environment, run superuser
            // kill the nav bar
            // hard coded value for Acer/Asus, works on both 4.1 and 4.2
            //pid maybe 79 on 4.2.2, set to 42 for 4.1 here
            Log.w(TAG, "hiding navigation bar");
            // REQUIRES ROOT
            final Process proc = Runtime
                    .getRuntime()
                    .exec(new String[] { "su", "-c",
                            "service call activity 42 s16 com.android.systemui" });
            proc.waitFor();
            created = true;
        } catch (IOException e) {
            Log.e(TAG, "Exception running as root: " + e.getMessage());
            e.printStackTrace();
        } catch (InterruptedException e) {
            Log.e(TAG, "Interrupted: " + e.getMessage());
            e.printStackTrace();
        } //end nav bar hide
    }

	public void adminButton(View btn) {
    	switch(btn.getId()) {
    	case R.id.adminCancel:
            //This action is TBD
    		if(!sPrefs.contains(Constants.STORE_ZIP)) {
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
            if(tmp.isEmpty()) {
                //Display error to the user
                if(((EditText)findViewById(R.id.adminNumber)).getText().toString().isEmpty()) {
                    Toast.makeText(this, R.string.noText, Toast.LENGTH_LONG).show();
                } else {
                    //user has number, did not set name
                    Toast.makeText(this, R.string.noName, Toast.LENGTH_LONG).show();
                }
            } else {
                //name has a value, check for location
                if(((EditText)findViewById(R.id.adminNumber)).getText().toString().isEmpty()) {
                    Toast.makeText(this, R.string.noNumber, Toast.LENGTH_LONG).show();
                } else {
                    //both fields ready for save
                    SharedPreferences.Editor ed = sPrefs.edit();
                    ed.putString(Constants.STORE_ZIP, tmp);
                    ed.putString(Constants.STORE_NUMBER,
                            ((EditText)findViewById(R.id.adminNumber)).getText().toString());
                    ed.commit();

                    backToApp("Saving store data");
                }
            }
    		break;
        case R.id.adminLog:
            startService(new Intent(this, WriteLogs.class));
            Log.i(TAG, "starting log service");
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
        created = false;
		finish();
    }
  
}
    
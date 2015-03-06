package com.turtlebeach.gstop.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
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
import com.turtlebeach.gstop.headsets.Constants;
import com.turtlebeach.gstop.headsets.ui.BaseActivity;
import com.turtlebeach.gstop.receiver.WriteLogs;

import java.io.IOException;

/**
 * This activity will show after the user opens the password dialog 
 * It allows users to exit the app and update it or perform maintenance on the tablet
 */
public class Admin extends BaseActivity {
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
        if(sPrefs.contains(Constants.STORE_ZIP)) {
        	//Pre load the EditText fields with the saved values when possible
        	((EditText)findViewById(R.id.adminZip)).setText(
        			sPrefs.getString(Constants.STORE_ZIP, ""));
        	((EditText)findViewById(R.id.adminNumber)).setText(
        			sPrefs.getString(Constants.STORE_NUMBER, ""));
            findViewById(R.id.adminCancel).setEnabled(true);
            findViewById(R.id.adminLog).setEnabled(true);
        } else {
            //preferences are not yet saved, disable cancel and log features
            findViewById(R.id.adminCancel).setEnabled(false);
            findViewById(R.id.adminLog).setEnabled(false);
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
            if(!Constants.isRemoteSDAvailable()){
                //show the error dialog, no sd card, user must press get logs button again
                final AlertDialog dog = showAlert(this, getString(R.string.admin_dogSD), getString(R.string.admin_dogSDtext));
                dog.setButton(DialogInterface.BUTTON_NEUTRAL, getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User completed the dialog
                        dialog.dismiss();
                    }
                });
                dog.show();
            } else {
                //sd card is available, confirm log download
                final AlertDialog dog = showAlert(this, getString(R.string.admin_dogDL), getString(R.string.admin_dogDLtext));
                dog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(android.R.string.no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        dialog.dismiss();
                    }
                });
                dog.setButton(DialogInterface.BUTTON_POSITIVE, getString(android.R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        dialog.dismiss();
                        LocalBroadcastManager.getInstance(Admin.this).registerReceiver(logListener,
                                new IntentFilter(WriteLogs.TAG));
                        startService(new Intent(Admin.this, WriteLogs.class));
                        Log.i(TAG, "starting log service");
                    }
                });
                dog.show();
            }

            break;
    	}
    	
    }
	
	void backToApp(String logMessage) {
		startActivity(new Intent(Admin.this, SplashScreenActivity.class));
        Log.i(TAG, logMessage);
        LocalBroadcastManager.getInstance(Admin.this).unregisterReceiver(logListener);
        finish();
	}
    
    void exitKiosk() {
    	//Exit app selected, every activity that is started must "finish" if this is going to work
        LocalBroadcastManager.getInstance(Admin.this).unregisterReceiver(logListener);
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

    public static AlertDialog showAlert(Activity ctx, String title, String message) {
        // Use an AlertDialog.Builder to put up an interactive message, set the buttons after
        return new AlertDialog.Builder(ctx)
                .setCancelable(false)
                .setMessage(message)
                .setTitle(title).create();
    }

    BroadcastReceiver logListener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(Admin.this != null && !Admin.this.isFinishing()) {
                Admin.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final AlertDialog dog = showAlert(Admin.this,
                                getString(R.string.admin_dogDone), getString(R.string.admin_dogDonetext));
                        dog.setButton(DialogInterface.BUTTON_NEUTRAL, getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                                dialog.dismiss();
                                LocalBroadcastManager.getInstance(Admin.this).unregisterReceiver(logListener);
                            }
                        });
                        dog.show();
                    }
                });
            }
        }
    };
  
}
    
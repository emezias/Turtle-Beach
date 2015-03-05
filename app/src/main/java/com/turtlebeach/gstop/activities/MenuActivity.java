package com.turtlebeach.gstop.activities;


import java.io.File;
import java.util.HashMap;
import java.util.Map;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.turtlebeach.gstop.R;
import com.turtlebeach.gstop.headsets.Analytics;
import com.turtlebeach.gstop.headsets.Constants;
import com.turtlebeach.gstop.headsets.model.VideoManager;
import com.turtlebeach.gstop.headsets.ui.BaseActivity;

public class MenuActivity extends BaseActivity implements View.OnClickListener {
	private static final String TAG = "MenuActivity";
	public static final int MENUCODE = 99;
	private static long mTouchStart = -1;
	Dialog mPwdDialog = null;
	EditText mPwdEditTxt = null;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.menu_activity);
		setVersion();
		Log.d(TAG, "on create");
		/*
		 * For some reason setting xml onClick in the menu layout is not working!
		 * tried with clickable true, still no go, thus setting Activity to listen
		 */
		((Button) findViewById(R.id.mmFeatured)).setOnClickListener(this);
		((Button) findViewById(R.id.mmWhy)).setOnClickListener(this);
		((Button) findViewById(R.id.mmSound)).setOnClickListener(this);
		((Button) findViewById(R.id.mmTrailers)).setOnClickListener(this);
		((Button) findViewById(R.id.mmSpecial)).setOnClickListener(this);
		((Button) findViewById(R.id.mmCatalog)).setOnClickListener(this);
		((Button) findViewById(R.id.mmSelector)).setOnClickListener(this);
		((Button) findViewById(R.id.mmPC)).setOnClickListener(this);
		//this code will eventually trigger the password dialog to show
		findViewById(R.id.mmVersion).setOnTouchListener(this);

	}
	

	@Override
	public void onClick(View v) {
		//This handles all the menu buttons
		switch(v.getId()) {
		case R.id.mmFeatured:
			Log.i(TAG, "menu item selected, Featured Headsets");
			startActivity(new Intent(this, FeaturedHeadsets.class));
			//featured headset can come from the splash screen, special treatment
			finish();
			return;
		case R.id.mmWhy:
			Log.i(TAG, "menu item selected, Why Turtle Beach");
			final Intent intent = new Intent(this, VideoPlaybackActivity.class);
	    	intent.putExtra(VideoPlaybackActivity.EXTRA_DEMO_FLAG, true);
	    	//demo video needs to return to the menu, don't finish this time
	    	startActivity(intent);
			return;
		case R.id.mmSound:
			Log.i(TAG, "menu item selected, Sound Experiences");
		return;
		case R.id.mmTrailers:
			Log.i(TAG, "Game trailer menu item clicked");
			final Intent tnt = new Intent(this, VideoPlaybackActivity.class);
	    	tnt.putExtra(VideoPlaybackActivity.EXTRA_VIDEO_FILE, 
	    			Constants.getVideo() + File.separator + "cod.mp4");
	    	//these hardcoded values are the first in the Gametrailer array, played by default
	    	tnt.putExtra(VideoPlaybackActivity.EXTRA_TITLE, 
	    			getResources().getStringArray(R.array.trailerTitles)[0]);
	    	tnt.putExtra(VideoPlaybackActivity.EXTRA_LIST_TYPE, VideoManager.GAMEVIDEOS);
	    	tnt.putExtra(VideoPlaybackActivity.EXTRA_SCREEN_TAG, "Trailers");
	    	startActivity(tnt);
			return;
		case R.id.mmCatalog:
			Log.i(TAG, "menu item selected, Headset Catalog");
			startActivity(new Intent(this, Catalog.class));
			return;
		case R.id.mmSpecial:
			Log.i(TAG, "menu item selected, Special Edition");
			return;
		case R.id.mmSelector:
			Log.i(TAG, "menu item selected, Headset Selector");
			break;
		case R.id.mmPC:
			Log.i(TAG, "menu item selected, PC Headsets");
			break;
		}
	}
	
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		//This can bring up the exit dialog, executes only if the version text is touched
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN: {
				mTouchStart = System.currentTimeMillis();
				Log.d(TAG, "start countdown");
				return true;
	
			}
			case MotionEvent.ACTION_UP: {
				// user must hold for 10s
				if (mTouchStart > 0
						&& System.currentTimeMillis() - mTouchStart > 10000) {
					// Reset start, show dialog
					mTouchStart = -1;
					showExitDialog();
					return false;
				}
			}// end action up
		} //end switch
		
		return super.onTouch(v, event);
	} //end onTouch
	
	/**
	 * Stop the demo and show the admin password dialog for maintenance purposes
	 * Feature is now set on version text instead of random point at the top left
	 */
	private void showExitDialog() {
		
		mHandler.removeCallbacks(mFinishRunnable);
		//don't timeout if the admin is typing in the password
		final View content = LayoutInflater.from(MenuActivity.this).inflate(R.layout.pwd_input_panel, null);
		
		// This needs to go away in a production build!!!!
		mPwdEditTxt = (EditText) content.findViewById(R.id.pwd);  
		//temp
        mPwdEditTxt.setText(Admin.PWD);

		mPwdDialog = new Dialog(this, R.style.Theme_CustomDialog);
		mPwdDialog.setContentView(content);
		mPwdDialog.show();
	}

	/**
	 * This listener is set in the XML, it processes button press as input to the EditText
	 * It simulates keypresses on all the numeric buttons in the panel
	 * It also handles the cancel/okay buttons 
	 * @param v - the button that was pressed
	 */
	public void pwdNumButton(View v) {
		Log.i(TAG, "button click");
		switch(v.getId()) {
		case R.id.pwdCancel:
			mPwdDialog.dismiss();
			Log.i(TAG, "cancel click");
			mHandler.postDelayed(mFinishRunnable, mIdleAfterTime);
			return;
		case R.id.pwdOkay:
			if(mPwdEditTxt.getText().toString()
					.equals(Admin.PWD)) {
				Log.i(TAG, "password correct, going to Admin screen");
				//trick to dismiss the keyboard
				startActivity(new Intent(this, Admin.class));
				mPwdDialog.dismiss();
				finish();
			} else {
				Toast.makeText(this, "Bad Password", Toast.LENGTH_LONG).show();
				Map<String, String> attributes = new HashMap<String, String>();
				String password = mPwdEditTxt.getText().toString();
				if(password == null || password.isEmpty()) {
					password = "none entered";
				}
				attributes.put(Analytics.ATTRIBUTE_PASSCODE_USED, password);
			}
			return;
		case R.id.pwd0:
			//programming both an action down and action up event simulates a key press
			//this creates a specific animation in the password panel edit text that we want to see 
			mPwdEditTxt.dispatchKeyEvent(new KeyEvent(0, 0, KeyEvent.ACTION_DOWN,
				    KeyEvent.KEYCODE_0, 0));
			mPwdEditTxt.dispatchKeyEvent(new KeyEvent(0, 0, KeyEvent.ACTION_UP,
				    KeyEvent.KEYCODE_0, 0));

			return;
		case R.id.pwd1:
			mPwdEditTxt.dispatchKeyEvent(new KeyEvent(0, 0, KeyEvent.ACTION_DOWN,
				    KeyEvent.KEYCODE_1, 0));
			mPwdEditTxt.dispatchKeyEvent(new KeyEvent(0, 0, KeyEvent.ACTION_UP,
				    KeyEvent.KEYCODE_1, 0));

			return;
		case R.id.pwd2:
			mPwdEditTxt.dispatchKeyEvent(new KeyEvent(0, 0, KeyEvent.ACTION_DOWN,
				    KeyEvent.KEYCODE_2, 0));
			mPwdEditTxt.dispatchKeyEvent(new KeyEvent(0, 0, KeyEvent.ACTION_UP,
				    KeyEvent.KEYCODE_2, 0));

			return;
		case R.id.pwd3:
			mPwdEditTxt.dispatchKeyEvent(new KeyEvent(0, 0, KeyEvent.ACTION_DOWN,
				    KeyEvent.KEYCODE_3, 0));
			mPwdEditTxt.dispatchKeyEvent(new KeyEvent(0, 0, KeyEvent.ACTION_UP,
				    KeyEvent.KEYCODE_3, 0));

			return;
		case R.id.pwd4:
			mPwdEditTxt.dispatchKeyEvent(new KeyEvent(0, 0, KeyEvent.ACTION_DOWN,
				    KeyEvent.KEYCODE_4, 0));
			mPwdEditTxt.dispatchKeyEvent(new KeyEvent(0, 0, KeyEvent.ACTION_UP,
				    KeyEvent.KEYCODE_4, 0));

			return;
		case R.id.pwd5:
			mPwdEditTxt.dispatchKeyEvent(new KeyEvent(0, 0, KeyEvent.ACTION_DOWN,
				    KeyEvent.KEYCODE_5, 0));
			mPwdEditTxt.dispatchKeyEvent(new KeyEvent(0, 0, KeyEvent.ACTION_UP,
				    KeyEvent.KEYCODE_5, 0));

			return;
		case R.id.pwd6:
			mPwdEditTxt.dispatchKeyEvent(new KeyEvent(0, 0, KeyEvent.ACTION_DOWN,
				    KeyEvent.KEYCODE_6, 0));
			mPwdEditTxt.dispatchKeyEvent(new KeyEvent(0, 0, KeyEvent.ACTION_UP,
				    KeyEvent.KEYCODE_6, 0));

			return;
		case R.id.pwd7:
			mPwdEditTxt.dispatchKeyEvent(new KeyEvent(0, 0, KeyEvent.ACTION_DOWN,
				    KeyEvent.KEYCODE_7, 0));
			mPwdEditTxt.dispatchKeyEvent(new KeyEvent(0, 0, KeyEvent.ACTION_UP,
				    KeyEvent.KEYCODE_7, 0));

			return;
		case R.id.pwd8:
			mPwdEditTxt.dispatchKeyEvent(new KeyEvent(0, 0, KeyEvent.ACTION_DOWN,
				    KeyEvent.KEYCODE_8, 0));
			mPwdEditTxt.dispatchKeyEvent(new KeyEvent(0, 0, KeyEvent.ACTION_UP,
				    KeyEvent.KEYCODE_8, 0));

			return;
		case R.id.pwd9:
			mPwdEditTxt.dispatchKeyEvent(new KeyEvent(0, 0, KeyEvent.ACTION_DOWN,
				    KeyEvent.KEYCODE_9, 0));
			mPwdEditTxt.dispatchKeyEvent(new KeyEvent(0, 0, KeyEvent.ACTION_UP,
				    KeyEvent.KEYCODE_9, 0));

			return;
			
		case R.id.pwdback:
			mPwdEditTxt.dispatchKeyEvent(new KeyEvent(0, 0, KeyEvent.ACTION_DOWN,
				    KeyEvent.KEYCODE_DEL, 0));
			mPwdEditTxt.dispatchKeyEvent(new KeyEvent(0, 0, KeyEvent.ACTION_UP,
				    KeyEvent.KEYCODE_DEL, 0));

			return;
		} //end switch
		Log.e(TAG, "Invalid Input: error processing button press in password panel");
	}//end button listener
	
	void setVersion() {
    	try{
    		final PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            ((TextView)findViewById(R.id.mmVersion)).setText(pInfo.versionName);
        } catch(PackageManager.NameNotFoundException exc){
        	Log.e(TAG, "problem getting version");
        	Log.e(TAG, exc.getMessage());
        }
    }
	
}
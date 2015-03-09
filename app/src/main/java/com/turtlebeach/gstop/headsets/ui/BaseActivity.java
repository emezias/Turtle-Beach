package com.turtlebeach.gstop.headsets.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.turtlebeach.gstop.R;
import com.turtlebeach.gstop.activities.DemoVideo;
import com.turtlebeach.gstop.headsets.model.HeadsetManager;
import com.turtlebeach.gstop.headsets.update.UpdateService;

public abstract class BaseActivity extends Activity implements OnTouchListener {
	//deprecated IdleListener, using instead a simple TouchListener class
	private static final String TAG = "BaseActivity";

    protected Handler mHandler;
    protected int mIdleAfterTime;
    
    //This variable gets set at launch by the Application class so the touch timeout will work
    //refer to TurtleBeachApplication.java

    //Receiver code and wait dialog can move to the splash screen instead of being part of every screen
    public static ProgressDialog mUpdateProgress;
    
    @Override
	protected void onCreate(Bundle savedInstanceState) {
    	//The Window manager is called so that the device doesn't go to sleep
		super.onCreate(savedInstanceState);
		final Window w = getWindow();
		w.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		//Remove title bar
	    w.requestFeature(Window.FEATURE_NO_TITLE);
	    //Remove notification bar
	    w.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		//hide the system bar and drawer on top
		mIdleAfterTime = getResources().getInteger(R.integer.idleAfter)*1000;
        //mIdleAfterTime = getResources().getInteger(R.integer.idleAfter);
        mHandler = new Handler();
	}

    @Override
    protected void onResume() {
        super.onResume();
        //setup listeners on Update Svc broadcasts, this may not be needed for all activities
	    Log.i(TAG, "registering for update service broadcasts");
	    final IntentFilter updateFilter = new IntentFilter(UpdateService.ACTION_UPDATE_STARTED);
	    updateFilter.addAction(UpdateService.ACTION_UPDATE_FINISHED);
	    updateFilter.addAction(UpdateService.ACTION_FILE_LOADING_STARTED);
	    registerReceiver(mUpdateSvcReceiver, updateFilter);
         		
        if(UpdateService.mUpdating) {
            Log.v(TAG, "onResume starting update progress dialog.");
            createContentUpdateProgress();
        } else if(!BaseActivity.this.getClass().getSimpleName().equals("VideoPlaybackActivity")) {
            //don't set idle time to play video demo during a video playback
            ((ViewGroup) findViewById(android.R.id.content)).setOnTouchListener(this);
            mHandler.postDelayed(mFinishRunnable, mIdleAfterTime);
            Log.v(TAG, "onResume startIdleTimer " + BaseActivity.this.getClass().getSimpleName());
        }
    }

    @Override
	protected void onPause() {
	    super.onPause();
	    unregisterReceiver(mUpdateSvcReceiver);
	    mHandler.removeCallbacks(mFinishRunnable);
	}
    
    /*using both dispatch and onTouch will set the Runnable into the queue twice

    @Override
    public boolean dispatchTouchEvent (MotionEvent ev) {
    	handleTouchEvent(ev);
    	return super.dispatchTouchEvent(ev);
    }*/
    
    @Override
	public boolean onTouch(View v, MotionEvent event) {
        //ignore calls from the VideoPlayback Activity, that activity sets the mFinishRunnable on its own, based on video state
        if(BaseActivity.this.getClass().getSimpleName().equals("VideoPlaybackActivity")) {
            //Log.i(TAG, "ignore touch in BaseActivity, video playback");
            return false;
        }
        if(event.getAction() == MotionEvent.ACTION_UP ||
                event.getAction() == MotionEvent.ACTION_CANCEL) {
            //reset the timer, touch is over, set new timer
            mHandler.postDelayed(mFinishRunnable, mIdleAfterTime);
            Log.v(TAG, "startIdleTimer " + BaseActivity.this.getClass().getSimpleName());
        } else if(event.getAction() == MotionEvent.ACTION_DOWN) {
            //reset the timer, remove old since a touch started
            mHandler.removeCallbacks(mFinishRunnable);
        }
        //this never actually processes the touch event, it is background, always return false
        return false;
	}
    

    /**
     * sets up the progress dialog shown during updates
     * This can probably move to the Admin activity
     */
    protected void createContentUpdateProgress() {
//    	if(!this.hasWindowFocus()) return;
        if (mUpdateProgress == null) {
        	mHandler.removeCallbacks(mFinishRunnable);
            mUpdateProgress = new ProgressDialog(this);
            mUpdateProgress.setMessage(getString(R.string.updateContentMessage));
            mUpdateProgress.setTitle(getString(R.string.updateContentTitle));
            mUpdateProgress.setCancelable(false);
            mUpdateProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mUpdateProgress.setCanceledOnTouchOutside(false);
            mUpdateProgress.setIndeterminate(true);
        }
        
        if (!mUpdateProgress.isShowing()) {
            mUpdateProgress.show();
        }
    }
    
    //This runnable implements the 45s inactivity timeout
    protected Runnable mFinishRunnable = new Runnable() {
    	//this code starts the demo mode after enough idle time
	    @Override
	    public void run() {
	    	//cancel future calls that could interrupt the video
	    	mHandler.removeCallbacks(mFinishRunnable);
	    	if(UpdateService.mUpdating) return;
			if(Looper.myLooper() == Looper.getMainLooper()) {
				//this check verifies we are on the main thread
				//mDemoListener must be set! Put it in the application class and never change it
				//no back stack, no need for flags intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				Log.v(TAG, "starting demo");
				startActivity(new Intent(BaseActivity.this, DemoVideo.class));
				//end the running activity when it's time to launch the demo
				finish();
			} 
	    }
	};

    BroadcastReceiver mUpdateSvcReceiver = new BroadcastReceiver() {

        private int mCurrent;
        private int mTotal;

        @Override
        public void onReceive(Context aContext, Intent aIntent) {
            String action = aIntent.getAction();
            if (UpdateService.ACTION_UPDATE_STARTED.equals(action)) {
                mHandler.removeCallbacks(mFinishRunnable);

                createContentUpdateProgress();
                mTotal += aIntent.getIntExtra(UpdateService.sXtraTotal, 0);
                if(mUpdateProgress != null) {
                	mUpdateProgress.setMax(mTotal);
                }
                Log.v(TAG, "ACTION_UPDATE_STARTED");
            } else if (UpdateService.ACTION_UPDATE_FINISHED.equals(action)) {
            	
                if((mUpdateProgress != null) && (mUpdateProgress.isShowing())) {
                	mUpdateProgress.dismiss();
                    Log.v(TAG, "ACTION_UPDATE_FINISHED: dismissed progress, init headsets");
                } else {
                    Log.e(TAG, "ACTION_UPDATE_FINISHED: no progress to dismiss?");
                }
                mUpdateProgress = null;
                HeadsetManager.init(aContext);

                if(mHandler != null  && BaseActivity.this != null
                        && Looper.myLooper() == Looper.getMainLooper()) {
                    mHandler.postDelayed(mFinishRunnable, mIdleAfterTime);
                    Log.v(TAG, "startIdleTimer from update receiver " + BaseActivity.this.getClass().getSimpleName());
                    //set touch listener so activity will timeout to demo video
                    ((ViewGroup) findViewById(android.R.id.content)).setOnTouchListener(BaseActivity.this);
                } else {
                	Log.e(TAG, "update finished, could not post demo timer");
                }
                	
                mCurrent = 0;
                mTotal = 0;

            } else if(UpdateService.ACTION_FILE_LOADING_STARTED.equals(action)) {
                Log.v(TAG, "ACTION_FILE_LOADING_STARTED");
                if (mUpdateProgress == null) {
                	//When an update from SD Card is called from the App class this class misses the 
                    //UpdateService.ACTION_UPDATE_APP broadcast, thus we listen for the files
                	createContentUpdateProgress();
                	mTotal += aIntent.getIntExtra(UpdateService.sXtraTotal, 0);
                	Log.i(TAG, "file loading started, null progress");
                    return;
                }
                mCurrent += aIntent.getIntExtra(UpdateService.sXtraFileNum, 0);
                mUpdateProgress.setProgress(mCurrent);

                String dirName = aIntent.getStringExtra(UpdateService.sXtraFileName);
                mUpdateProgress.setMessage(
                        String.format(getString(R.string.contentString1) + " %1s ", dirName));
            } 
            
        } 

    };    
    
}

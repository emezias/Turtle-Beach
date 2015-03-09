package com.turtlebeach.gstop.activities;

import java.io.File;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.VideoView;

import com.turtlebeach.gstop.R;
import com.turtlebeach.gstop.headsets.Constants;
import com.turtlebeach.gstop.headsets.ui.BaseActivity;

/**
 * 
 * This activity kicks in after 45s of idle time to play a demo video in a loop
 * If the tablet is not plugged into the charger, the video loop will exit
 * It was originally built to play multiple videos found in the _demo directory
 * The code to trigger the demo is a touchlistener in the Base Activity
 */
public class DemoVideo extends BaseActivity {
	private static final String TAG = "DemoModeActivity";
	public static final String DEMOFILE = "Turtle Beach Demo.mp4";
	public static final float VOL_PCT = 0.75f;
	
	/**
	 * Intent key for the extra determining if the demo should run regardless of other conditions.
	 */
	public static final String EXTRA_RUN_VALUE = "EXTRA_RUN_VALUE";    
    /**
     * Flag to let the activity know that it is running from the main menu, and should ignore 
     * canRunNow() and return to the main menu after closing.
     */
    private VideoView mVideoView;

	public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo);
        Log.v(TAG, "demo start");
        
        final File demoDir = new File(Constants.getDemoDir(), DEMOFILE);
        if(!demoDir.exists()){
            Toast.makeText(this, "Failed to start demo video: TurtleBeachDemo not under _demo directory",
                    Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        mVideoView = (VideoView) findViewById(R.id.demo_vidview);
        final AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        final int bgood = Double.valueOf(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) * VOL_PCT).intValue();
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, bgood, 0);
        //small detail, erase volume settings from the last user
        VideoPlaybackActivity.sMuted = false;
        VideoPlaybackActivity.sVolumeSaver = -1;
        mVideoView.setKeepScreenOn(true);
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener(){

            @Override
            public void onPrepared(MediaPlayer aMediaPlayer){
                aMediaPlayer.setScreenOnWhilePlaying(true);
            }
        });
        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener(){

            @Override
            public void onCompletion(MediaPlayer aMediaPlayer){
            	mVideoView.stopPlayback();
            	Log.v(TAG, "completion listener, demo restart");
                //restart and keep playing
                mVideoView.setVideoPath(demoDir.getAbsolutePath());
                mVideoView.start();
                
            }
        });
        
    }

    @Override
    protected void onResume(){
        super.onResume();
        //need to override base activity touch listener
        //The Base activity will auto launch this activity, notice no call to super
        ((ViewGroup) findViewById(android.R.id.content)).setOnTouchListener(new View.OnTouchListener(){

            @Override
            public boolean onTouch(View aView, MotionEvent aMotionEvent){

                if(aMotionEvent.getAction() == MotionEvent.ACTION_UP){
                    mVideoView.stopPlayback();
                    finish();
                }
                return true;

            }
        });
        
        mVideoView.setVideoPath(Constants.getDemoDir() + File.separator + DEMOFILE);
        mVideoView.start();
        mHandler.removeCallbacks(mFinishRunnable);
    }

   
    @Override
	public void finish(){
	    super.finish();
	    overridePendingTransition(0, 0);
	}

	   

}
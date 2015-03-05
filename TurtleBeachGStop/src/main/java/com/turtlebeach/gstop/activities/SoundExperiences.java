package com.turtlebeach.gstop.activities;

import java.io.File;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.clickbrand.turtlebeach.Constants;
import com.clickbrand.turtlebeach.model.VideoManager;
import com.turtlebeach.gstop.R;
import com.turtlebeach.gstop.internet.NWBaseActivity;

public class SoundExperiences extends NWBaseActivity { 
	private static final String TAG = "SoundExperiences";
	private static String[] mVideos, mTitles;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sound_activity);
		if(mVideos == null) {
			mVideos = getResources().getStringArray(R.array.soundVideos);
			mTitles = getResources().getStringArray(R.array.soundTitles);
		}
		/*
		 * DEBUG code
		 * DisplayMetrics metrics = new DisplayMetrics();
		 getWindowManager().getDefaultDisplay().getMetrics(metrics);
		 Log.i(TAG, "metrics width = " + metrics.widthPixels);*/
	}

	/**
	 * Every play button has this method set in xml
	 * @param v - the button that was pressed, the video to play
	 */
	public void playVideo(View v) {
		switch(v.getId()) {
		case R.id.sndPlayAction:
			pushPlay(mVideos[0], mTitles[0]);
			break;
		case R.id.sndPlayRacing:
			pushPlay(mVideos[1], mTitles[1]);
			break;
		case R.id.sndPlayRPG:
			pushPlay(mVideos[2], mTitles[2]);
			break;
			
		case R.id.sndPlaySport:
			pushPlay(mVideos[3], mTitles[3]);
			break;
		}
	}
	
	//all the buttons have the same action...
	void pushPlay(String filename, String title) {
		Log.i(TAG, "Sound video selected: " + title);
		final Intent intent = new Intent(this, VideoPlaybackActivity.class);
    	intent.putExtra(VideoPlaybackActivity.EXTRA_VIDEO_FILE, 
    			Constants.getVideo() + File.separator + filename);
    	
    	intent.putExtra(VideoPlaybackActivity.EXTRA_TITLE, title);
    	intent.putExtra(VideoPlaybackActivity.EXTRA_LIST_TYPE, VideoManager.SOUNDVIDEOS);
    	startActivityForResult(intent, 999);
    	//request code doesn't matter
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// This will receive a signal from the video playback to go to the main menu
		super.onActivityResult(requestCode, resultCode, data);
		Log.i(TAG,"result code is: " + resultCode);
		if(resultCode == MenuActivity.MENUCODE) {
			Log.i(TAG, "get to main menu");
			mMenu(null);
		}
	}

	public void mMenu(View v) {
        finish();
	}
	
}

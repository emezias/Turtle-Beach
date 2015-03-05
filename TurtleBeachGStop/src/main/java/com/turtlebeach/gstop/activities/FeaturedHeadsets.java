package com.turtlebeach.gstop.activities;

import java.io.File;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.clickbrand.turtlebeach.Constants;
import com.clickbrand.turtlebeach.model.HeadsetManager;
import com.turtlebeach.gstop.R;
import com.turtlebeach.gstop.internet.NWBaseActivity;

public class FeaturedHeadsets extends NWBaseActivity { 
	private static final String TAG = "FeaturedHeadsets";

	private boolean mPlayPressed;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.featured_headsets);
		//The array order in the library for getFeaturedButton is alphabetical 
		//HEADSET_ID_P12, HEADSET_ID_PX22, HEADSET_ID_X12, HEADSET_ID_XO4
		((ImageView) findViewById(R.id.featHeadset1)).setImageDrawable(HeadsetManager.sFeatured[0]);
		//The HeadsetManager will construct stateful buttons from the two images on the SD Card
		
		((ImageView) findViewById(R.id.featHeadset2))
			.setImageDrawable(HeadsetManager.sFeatured[1]);
		
		((ImageView) findViewById(R.id.featHeadset3))
			.setImageDrawable(HeadsetManager.sFeatured[2]);
		
		((ImageView) findViewById(R.id.featHeadset4))
			.setImageDrawable(HeadsetManager.sFeatured[3]);
		//drop shadow on title text is set in the layout
		
		mPlayPressed = false;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		mPlayPressed = false;
	}

	/**
	 * TODO use the array
	 * This onClick is set in xml on all the "play video" buttons
	 * @param v
	 */
	public void playVideo(View v) {
		final String[] titles = getResources().getStringArray(R.array.featuredTitles);
		final String[] movies = getResources().getStringArray(R.array.featuredVideo);
		switch(v.getId()) {
		case R.id.featbtn1:
		case R.id.featHeadset1:
			pushPlay(movies[0], titles[0]);
			break;
			
		case R.id.featbtn2:
		case R.id.featHeadset2:
			pushPlay(movies[1], titles[1]);
			break;
			
		case R.id.featbtn3:
		case R.id.featHeadset3:
			pushPlay(movies[2], titles[2]);
			break;
			
			
		case R.id.featbtn4:
		case R.id.featHeadset4:
			pushPlay(movies[3], titles[3]);
			break;
		}
	}
	
	void pushPlay(String filename, String title) {
		if(!mPlayPressed) {
			Log.i(TAG, "Featured headset selected: " + title);
			final Intent intent = new Intent(this, VideoPlaybackActivity.class);
			intent.putExtra(VideoPlaybackActivity.EXTRA_VIDEO_FILE, 
    			Constants.getFeatureVideo() + File.separator + filename);
    	
			intent.putExtra(VideoPlaybackActivity.EXTRA_TITLE, title);
			startActivityForResult(intent, 99);
			//request code doesn't matter
			mPlayPressed = true;
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// This will receive a signal from the video playback to go to the main menu
		super.onActivityResult(requestCode, resultCode, data);
		mPlayPressed = true;
		Log.i(TAG,"result code is: " + resultCode);
		if(resultCode == MenuActivity.MENUCODE) {
			Log.i(TAG, "get to main menu");
			mMenu(null);
		}
	}

	public void mMenu(View v) {
        startActivity(new Intent(this, MenuActivity.class));
		//special treatment for two starting points
        finish();
	}
	
}

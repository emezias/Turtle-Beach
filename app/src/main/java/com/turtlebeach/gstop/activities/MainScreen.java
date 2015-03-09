package com.turtlebeach.gstop.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.turtlebeach.gstop.R;
import com.turtlebeach.gstop.headsets.Constants;
import com.turtlebeach.gstop.headsets.model.HeadsetManager;
import com.turtlebeach.gstop.headsets.model.VideoManager;
import com.turtlebeach.gstop.headsets.ui.BaseActivity;

import java.io.File;

public class MainScreen extends BaseActivity {
	private static final String TAG = "MainScreen";

    //The array order in the library for getFeaturedButton is alphabetical
    //HEADSET_ID_P12, HEADSET_ID_PX22, HEADSET_ID_X12, HEADSET_ID_XO4
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

        //set up the included layouts
        RelativeLayout tmp = (RelativeLayout) findViewById(R.id.menuHS1);
        tmp.setTag(0);
        ((ImageView) tmp.findViewById(R.id.mainHS_Set)).setImageDrawable(HeadsetManager.sFeatured[0]);
		//The HeadsetManager will construct stateful buttons from the two images on the SD Card
        ((ImageView) tmp.findViewById(R.id.mainHS_Logo)).setImageResource(R.drawable.img_featured_logo_stealth500p);
        tmp.invalidate();

        tmp = (RelativeLayout) findViewById(R.id.menuHS2);
        tmp.setTag(1);
		((ImageView) findViewById(R.id.mainHS_Set))
			.setImageDrawable(HeadsetManager.sFeatured[1]);
        ((ImageView) tmp.findViewById(R.id.mainHS_Logo)).setImageResource(R.drawable.img_featured_logo_stealth500x);
        tmp.invalidate();

        tmp = (RelativeLayout) findViewById(R.id.menuHS3);
        tmp.setTag(2);
		((ImageView) findViewById(R.id.mainHS_Set))
			.setImageDrawable(HeadsetManager.sFeatured[2]);
        ((ImageView) tmp.findViewById(R.id.mainHS_Logo)).setImageResource(R.drawable.img_featured_logo_xoseven);
        tmp.invalidate();

        tmp = (RelativeLayout) findViewById(R.id.menuHS4);
        tmp.setTag(3);
		((ImageView) findViewById(R.id.mainHS_Set))
			.setImageDrawable(HeadsetManager.sFeatured[3]);
        ((ImageView) tmp.findViewById(R.id.mainHS_Logo)).setImageResource(R.drawable.img_featured_logo_elite800);
        tmp.invalidate();

	}
	
	@Override
	public void onResume() {
		super.onResume();
	}

	/**
	 * TODO use the array
	 * This onClick is set in xml on all the "play video" buttons
	 * @param v
	 */
	public void playVideo(View v) {
		final String[] titles = getResources().getStringArray(R.array.featuredTitles);
		final String[] movies = getResources().getStringArray(R.array.featuredVideo);
        final int dex = (Integer) ((RelativeLayout) v.getParent()).getTag();
		switch(dex) {
		case 0:
			pushPlay(movies[0], titles[0]);
			break;
			
		case 1:
			pushPlay(movies[1], titles[1]);
			break;
			
		case 2:
			pushPlay(movies[2], titles[2]);
			break;

		case 3:
			pushPlay(movies[3], titles[3]);
			break;
		}
	}

    public void learnMore(View v) {
        final int dex = (Integer) ((RelativeLayout) v.getParent()).getTag();
        switch (dex) {
            case 0:
                break;

            case 1:
                break;

            case 2:
                break;

            case 3:
                break;
        }
    }
	
	void pushPlay(String filename, String title) {
        Log.i(TAG, "Featured headset selected: " + title);
        final Intent intent = new Intent(this, VideoPlaybackActivity.class);
        intent.putExtra(VideoPlaybackActivity.EXTRA_VIDEO_FILE,
                Constants.getFeatureVideo() + File.separator + filename);

        intent.putExtra(VideoPlaybackActivity.EXTRA_TITLE, title);
        startActivityForResult(intent, 99);
	}

	public void mMenu(View v) {
		//this is a call to bring up a new activity that will return here
        switch(v.getId()) {
            case R.id.menuCatalog:
                Log.i(TAG, "menu item selected, Headset Catalog");
                startActivity(new Intent(this, Catalog.class));
                break;
            case R.id.menuWhy:
                Log.i(TAG, "menu item selected, Why Turtle Beach");
                final Intent intent = new Intent(this, VideoPlaybackActivity.class);
                intent.putExtra(VideoPlaybackActivity.EXTRA_DEMO_FLAG, true);
                //demo video needs to return to the menu, don't finish this time
                startActivity(intent);
                break;
            case R.id.menuGames:
                Log.i(TAG, "Game trailer menu item clicked");
                final Intent tnt = new Intent(this, VideoPlaybackActivity.class);
                tnt.putExtra(VideoPlaybackActivity.EXTRA_LIST_TYPE, VideoManager.GAMEVIDEOS);
                tnt.putExtra(VideoPlaybackActivity.EXTRA_SCREEN_TAG, "Trailers");
                startActivity(tnt);
                break;
        }

	}
	
}

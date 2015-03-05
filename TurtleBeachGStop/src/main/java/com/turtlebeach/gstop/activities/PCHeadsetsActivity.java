package com.turtlebeach.gstop.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.clickbrand.turtlebeach.model.HeadsetManager;
import com.turtlebeach.gstop.R;
import com.turtlebeach.gstop.internet.NWBaseActivity;

/*
 * This is just a place holder so the main menu can be completed...
 */
public class PCHeadsetsActivity extends NWBaseActivity {
	private int[] headsets = { HeadsetManager.HEADSET_ID_Z11, HeadsetManager.HEADSET_ID_Z60, HeadsetManager.HEADSET_ID_RECON320 };
	
	private static final String TAG="PCHeadsets";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.setContentView(R.layout.pc_headsets);
		
		((ImageView) findViewById(R.id.pcHeadset1))
			.setImageDrawable(HeadsetManager.getCatalogButton(getResources(), headsets[0]));
		((ImageView) findViewById(R.id.pcHeadset2))
			.setImageDrawable(HeadsetManager.getCatalogButton(getResources(), headsets[1]));
		((ImageView) findViewById(R.id.pcHeadset3))
			.setImageDrawable(HeadsetManager.getCatalogButton(getResources(), headsets[2]));
		
		sScreenName = "PCHeadsets";
	}
    
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// This is where the back/main menu button clicks from the slide panel end up
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == Activity.RESULT_OK) {
			gotoMainMenu(null);
		} else {
			Log.i(TAG, "Back Button");
		}
	}
	
	public void gotoSlider(View v) {
    	//This is where we open up the slider panel
		int index = 0;
		if (v.getId() == R.id.pcHeadset1) {
			index = 0;
		} else if (v.getId() == R.id.pcHeadset2) {
			index = 1;
		} else if (v.getId() == R.id.pcHeadset3) {
			index = 2;
		}
    	final Intent tnt = new Intent(this, CatalogSlider.class);
		tnt.putExtra(CatalogSlider.TAG, headsets[index]);
		tnt.putExtra(CatalogSlider.GROUP, headsets);
		tnt.putExtra(CatalogSlider.TITLE, getString(R.string.pcTitle));
		startActivityForResult(tnt, 99);
	}
	
	public void gotoMainMenu(View view) {
		finish();
	}
}

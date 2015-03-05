package com.turtlebeach.gstop.activities;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.clickbrand.turtlebeach.model.Headset;
import com.clickbrand.turtlebeach.model.HeadsetManager;
import com.clickbrand.turtlebeach.ui.TrueTypeTextView;
import com.turtlebeach.gstop.R;
import com.turtlebeach.gstop.internet.NWBaseActivity;

/**
 * This is a detail screen that opens when the user touches a headset in a
 * catalog screen
 */
public class LearnMore extends NWBaseActivity implements View.OnClickListener {
	public static final String TAG = "LearnMoreActivity";

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.learn_more);
		
		sScreenName = "Details";
		
		final Headset h = HeadsetManager.getHeadsetById(getIntent().getExtras().getInt(TAG));
		final LinearLayout ll = (LinearLayout) findViewById(R.id.learnBullets);
		findViewById(R.id.rootView).setOnClickListener(this);
		
		final String[] detailText = getResources().getStringArray(h.getArrayResource(true));
		final int sz = detailText.length;
		TrueTypeTextView line;
		/*
		 * The detailText array contains a header followed by explanatory text
		 * This could be separated into two arrays, seemed cleaner to put them together
		 */
		
		//entry zero... heading description
		if(!detailText[0].isEmpty()) {
			line = (TrueTypeTextView) LayoutInflater.from(this).inflate(
					R.layout.learn_text, null);
			line.setFont("dharma.otf");
			line.setText(detailText[0]);
			ll.addView(line);
			line = (TrueTypeTextView) LayoutInflater.from(this).inflate(
					R.layout.learn_text, null);
			line.setText(" ");
			ll.addView(line);
		}
		
		//add each heading/detail text bullet to the Linear Layout inside the scrollview 
		for(int dex = 1; dex < sz; dex++) {
			//Switch to set the correct layout and font, size is set in XML
			if(dex%2!=0) {
				line = (TrueTypeTextView) LayoutInflater.from(this).inflate(
						R.layout.learn_text, null);
				line.setFont("dharma.otf");
			} else {
				line = (TrueTypeTextView) LayoutInflater.from(this).inflate(
						R.layout.learn_text2, null);
				line.setFont("gothamBook.otf");
			}
			
			line.setText(detailText[dex]);
			ll.addView(line);
		}
		
		
		final Bitmap hImage = HeadsetManager.getDetailsImage(h.deviceID);
		if(hImage != null) {
			((ImageView)findViewById(R.id.learnImage)).setImageBitmap(hImage);
			line = (TrueTypeTextView) findViewById(R.id.disclaimer);
			line.setFont("gothamMedium.otf");
			line.setWidth(hImage.getWidth());
		} 
		
	}

	@Override
	public void onClick(View v) {
		// This is what happens when the user touches anything on the white background
		finish();
	}
}

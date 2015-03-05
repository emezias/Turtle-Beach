package com.clickbrand.turtlebeach.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class TrueTypeTextView extends TextView {

	public String mFontFile = "bankgothicbtmedium.ttf";
			
	public TrueTypeTextView(Context context) {
		super(context);
		init(context);
	}

	public TrueTypeTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public TrueTypeTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}
	
	void init(Context ctx) {
		if(mFontFile == null) {
			mFontFile = "bankgothicmedium.ttf";
		}
		setTypeface(Typeface.createFromAsset(
			    ctx.getAssets(), mFontFile));
	}
	
	public void setFont(String font) {
		mFontFile = font;
		setTypeface(Typeface.createFromAsset(
			    getContext().getAssets(), font));
	}

}

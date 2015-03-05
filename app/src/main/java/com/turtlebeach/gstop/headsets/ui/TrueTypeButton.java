package com.turtlebeach.gstop.headsets.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

public class TrueTypeButton extends Button {

	public String mFontFile = "bankgothicbtmedium.ttf";
			
	public TrueTypeButton(Context context) {
		super(context);
		init(context);
	}

	public TrueTypeButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public TrueTypeButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}
	
	void init(Context ctx) {
		if(mFontFile == null) {
			mFontFile = "bankgothicmedium.ttf";
		}
		Typeface t = Typeface.createFromAsset(
			    ctx.getAssets(), mFontFile);
		setTypeface(t);
	}
	
	public void setFont(String font) {
		mFontFile = font;
		setTypeface(Typeface.createFromAsset(
			    getContext().getAssets(), font));
	}

}

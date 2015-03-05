package com.turtlebeach.gstop.headsets.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

public class TrueTypeEditText extends EditText {

	public String mFontFile = "bankgothicbtmedium.ttf";
			
	public TrueTypeEditText(Context context) {
		super(context);
		init(context);
	}

	public TrueTypeEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public TrueTypeEditText(Context context, AttributeSet attrs, int defStyle) {
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

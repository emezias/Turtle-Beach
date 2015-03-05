package com.clickbrand.turtlebeach.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

public class PwdEditText extends EditText {

    public PwdEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override 
    public boolean onCheckIsTextEditor() {
        return false;
    }       
}
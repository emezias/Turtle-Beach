package com.turtlebeach.gstop.test.assertions;

import junit.framework.AssertionFailedError;
import android.view.View;
import android.widget.TextView;

import com.google.android.apps.common.testing.ui.espresso.NoMatchingViewException;
import com.google.android.apps.common.testing.ui.espresso.ViewAssertion;
import com.google.common.base.Optional;

public class TextIsCorrect implements ViewAssertion {
	
	private String mTextToCheck;
	
	public TextIsCorrect(String textToCheck) {
		mTextToCheck = textToCheck;
	}
	
	@Override
	public void check(Optional<View> view, Optional<NoMatchingViewException> noViewFoundException) {
		try {
			TextView textView = (TextView) view.get();
			if(!textView.getText().equals(mTextToCheck)) {
				String errorMessage = "Text does not match. View's text is \"" + textView.getText() + "\"";
		        throw new AssertionFailedError(errorMessage);
			}
		}catch(ClassCastException e) {
			String errorMessage = "View was not of type TextView.";
	        throw new AssertionFailedError(errorMessage);
		}
	}

}

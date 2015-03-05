package com.turtlebeach.gstop.test.assertions;

import com.google.android.apps.common.testing.ui.espresso.ViewAssertion;

public class TBAssertions {

	public static ViewAssertion correctText(String text) {
		return new TextIsCorrect(text);
	}
}

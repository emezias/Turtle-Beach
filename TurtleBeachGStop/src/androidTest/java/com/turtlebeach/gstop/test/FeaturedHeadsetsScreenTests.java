package com.turtlebeach.gstop.test;

import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.click;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.matches;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withId;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withText;
import android.test.ActivityInstrumentationTestCase2;

import com.turtlebeach.gstop.activities.SplashScreenActivity;

public class FeaturedHeadsetsScreenTests extends ActivityInstrumentationTestCase2<SplashScreenActivity> {

    public FeaturedHeadsetsScreenTests() {
        super(SplashScreenActivity.class);
    }


    @Override
    protected void setUp() throws Exception {
        super.setUp();
        getActivity();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
    
    public void testClick() {
        onView(withId(com.turtlebeach.gstop.R.id.splashTitleText))
        .check(matches(withText(com.turtlebeach.gstop.R.string.splashTitle)));

//        onView(withId(R.id.button_main))
//        .perform(click());
//        
//        onView(withId(R.id.textview_hello))
//        .check(matches(withText("42")));
        
    }
    
}

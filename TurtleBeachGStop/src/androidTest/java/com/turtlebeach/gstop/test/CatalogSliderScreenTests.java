package com.turtlebeach.gstop.test;

import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.click;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.matches;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withId;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withText;
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;

import com.clickbrand.turtlebeach.model.HeadsetManager;
import com.turtlebeach.gstop.R;
import com.turtlebeach.gstop.activities.Catalog;
import com.turtlebeach.gstop.activities.CatalogSlider;

public class CatalogSliderScreenTests extends ActivityInstrumentationTestCase2<CatalogSlider> {

    public CatalogSliderScreenTests() {
        super(CatalogSlider.class);
    }


    @Override
    protected void setUp() throws Exception {
        super.setUp();
        final Intent tnt = new Intent();
        tnt.putExtra(CatalogSlider.TAG, HeadsetManager.HEADSET_ID_ELITE800);
		tnt.putExtra(CatalogSlider.GROUP, Catalog.S2_PS4);
        setActivityIntent(tnt);
        getActivity();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
    
    public void testClick() {
        onView(withId(com.turtlebeach.gstop.R.id.catTitleText))
        .check(matches(withText(com.turtlebeach.gstop.R.string.hcTitle)));

//        onView(withId(R.id.button_main))
//        .perform(click());
//        
//        onView(withId(R.id.textview_hello))
//        .check(matches(withText("42")));
        
    }
    
    public void test80048628() {
    	onView(withId(com.turtlebeach.gstop.R.id.catDesignText)).check(matches(withText("DESIGNED FOR PS4\u2122, PS3\u2122 \u0026 MOBILE")));
    }
    
}

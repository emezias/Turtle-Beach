package com.turtlebeach.gstop.test;

import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.turtlebeach.gstop.test.assertions.TBAssertions.correctText;
import android.test.ActivityInstrumentationTestCase2;

import com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers;
import com.turtlebeach.gstop.activities.SpecialEdition;

public class SpecialEditionScreenTests extends ActivityInstrumentationTestCase2<SpecialEdition> {

    public SpecialEditionScreenTests() {
        super(SpecialEdition.class);
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
    
    public void testGroupTitles() {
    	onView(ViewMatchers.withText("CALL OF DUTY\u00AE")).check(correctText("CALL OF DUTY\u00AE"));
    	onView(ViewMatchers.withText("BLIZZARD\u0026")).check(correctText("BLIZZARD\u0026"));
    	onView(ViewMatchers.withText("MARVEL")).check(correctText("MARVEL"));
    	onView(ViewMatchers.withText("STAR WARS\u0026")).check(correctText("STAR WARS\u0026"));
    	onView(ViewMatchers.withText("TITANFALL\u0026")).check(correctText("TITANFALL\u0026"));
    }
    
}

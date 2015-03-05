package com.turtlebeach.gstop.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.clickbrand.turtlebeach.ui.TrueTypeTextView;
import com.turtlebeach.gstop.R;
import com.turtlebeach.gstop.fragment.SliderFragment;
import com.turtlebeach.gstop.internet.NWBaseActivity;

/**
 * This activity will show the slider panel from the special edition screen
 * this activity was started for a result so it will finish and return to the caller (special edition)
 */
public class SpecialSlider extends NWBaseActivity {
	public static final String TAG = "SpecialSlider";
	public static final String GROUP = "group";
	public static final String TITLE = "title";
	public static final String BACKGROUND = "background";
	
	//scratch variables to re use through out the activity
	//we can reuse the same block of space in the heap instead of allocating and tossing out transactions
	FragmentManager mMgr;
	FragmentTransaction mXaction;
	SliderFragment mCF;
	//the fragment container that is animated
	View mHeadsetFragment;
	//the simple gesture detector that implements fling/swipe interaction
	GestureDetector mDetector;
	//the index to the mHeadsets array of the currently visible headset
	int mSelected = -1;
	//headset ids for the group that is being displayed
	int[] mHeadsets;
	
	//The following are declared static so their values will be retained and ready each time this screen is called
	//animation duration and the translation distance
    static int mDuration, mWidth = -1;
    //animations for the sliding part of this panel
    ObjectAnimator moveLeft, moveLeft2, moveRight, moveRight2;
	
		
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newslide_panel);
        
        sScreenName = "Slider";
        //hit out of memory error here, setting large heap size in the manifest
        findViewById(R.id.rootView).setBackgroundResource(getIntent().getExtras().getInt(BACKGROUND));
        
        mHeadsetFragment = findViewById(R.id.newcatContainer);
        mMgr = getFragmentManager();
        //Swipe listener
        mDetector = new GestureDetector(this, new GD());
        //the id is passed in from the Headset catalog
        final int headsetId = getIntent().getExtras().getInt(TAG);
        //find the headset to display and set a tag on the menu for the frag to read
        findViewById(R.id.rootView).setTag(headsetId);
        
        if (getIntent().hasExtra(TITLE)) {
        	((TrueTypeTextView)findViewById(R.id.catTitleText)).setText(getIntent().getStringExtra(TITLE));
        }
        
        // This is where we get the second Extra, the group of headsets for navigation
        mHeadsets = getIntent().getExtras().getIntArray(GROUP);
		for(int dex = 0; dex  < mHeadsets.length; dex++) {
			if(mHeadsets[dex] == headsetId) {
				//initialize mSelected
				mSelected = dex;
				break;
			}
		}
		Log.i(TAG, "group size " + mHeadsets.length + " selected value is " + mSelected);
		//cannot retain the manager for the activity after finishing
		//thus, the fragment cache is only for the given section
    	mXaction = mMgr.beginTransaction();
    	mCF = new SliderFragment();
		mXaction.add(R.id.newcatContainer, mCF, headsetId +"");
		//put it on the backstack so the Fragment manager will cache it
		mXaction.addToBackStack(null);
    	mXaction.commit();
    	Log.i(TAG, "committed transaction");
    	
    	((TrueTypeTextView)findViewById(R.id.catTitleText)).setText(R.string.specialTitle);

    }
    
    
    @Override 
    public boolean onTouchEvent(MotionEvent event){ 
    	//first look for a swipe, then run the stuff from the base activity
        mDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }


    /**
     * This method will instantiate the fragment transition animation
     * It sets a listener that will create and add fragment that not been seen before OR
     * find and show a fragment the the FragmentManager already has hanging around
     */
    void leftArrow() {
    	moveLeft = ObjectAnimator.ofFloat(mHeadsetFragment, "translationX", 0f, -mWidth);
		moveLeft.setDuration(mDuration);
		//instantiate the second animation here, instead of everytime the listener runs
		moveLeft2 = ObjectAnimator.ofFloat(mHeadsetFragment, "translationX", mWidth, 0);
		moveLeft2.setDuration(mDuration);
		
    	moveLeft.addListener(new AnimatorListenerAdapter() {
    		@Override
    	    public void onAnimationEnd(Animator animation) {
    			//after animating current fragment offscreen, hide it
    			setFragment();
    	    	//now animate the new fragment on to the screen
    	    	moveLeft2.start();
    	    	
    		}
    		
    	});    			
    }
    
    /**
     * same as above, except for the other side
     * TRIED mXaction.setCustomAnimations(R.animator.out_left, R.animator.in_left);
     * as well as mXaction.setCustomAnimations(enter, exit, popEnter, popExit);
     * the fragment transaction custom animations were not working as expected
     * enter would only run on add, not on show
     * results were better when the code set manual animations on the view
     */
    void rightArrow() {    	
    	moveRight = ObjectAnimator.ofFloat(mHeadsetFragment, "translationX", 0, 2*mWidth);
		moveRight.setDuration(mDuration);
		moveRight2 = ObjectAnimator.ofFloat(mHeadsetFragment, "translationX", -mWidth, 0);
		moveRight2.setDuration(mDuration);
		
    	moveRight.addListener(new AnimatorListenerAdapter() {
    		@Override
    	    public void onAnimationEnd(Animator animation) {
    			setFragment();
    	    	moveRight2.start();
    		}
    		
    	});
    	
    }
    
    /**
     * short cut for code that is common to both animation listeners
     */
    void setFragment() {
    	mXaction = mMgr.beginTransaction(); 
		mXaction.hide(mCF);
    	mXaction.commit();
    	        	    	
    	mXaction = mMgr.beginTransaction();
    	
    	mCF = (SliderFragment) mMgr.findFragmentByTag(mHeadsets[mSelected]+"");
    	if(mCF == null) {
    		Log.e(TAG, "current fragment not found?!?");
    		mCF = new SliderFragment();
    		//always add to backstack so that the Fragment Manager keeps state
    		mXaction.add(R.id.newcatContainer, mCF, mHeadsets[mSelected] +"");
    		mXaction.addToBackStack(null);
    	} else {
    		Log.d(TAG, "found fragment to show");
    		mXaction.show(mCF);
    		((TrueTypeTextView)findViewById(R.id.catDesignText)).setText((String)
            		((TrueTypeTextView)mCF.getView().findViewById(R.id.slideDescription)).getTag());
    	}
    	mXaction.commit();
    }

    /**
     * This click listener is set in the xml
     * It is the logic to navigate through the set of headsets in that catalog section
     * Navigation is circular, users can go through left and right
     * Right and Left arrows hide the current fragment view and set the next one
     * @param arrow
     */
	public void arrowButton(View arrow) {
		//width and animation duration are needed if the user navigates to another headset
		//the variables are static, and only need to be defined once
    	if(mWidth < 0) {
    		//this is static and needs to be set only once
    		mWidth = mHeadsetFragment.getWidth();
    		mDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);
    	}
    	//selected is going to be the animation, left or right
    	final ObjectAnimator selected;
    	if(arrow.getId() == R.id.catBtnRight) {
    		mSelected++;
    		if(mSelected == mHeadsets.length) {
    			mSelected = 0;
    		}
    		//instantiate if this is our first call
    		if(moveLeft == null) {
    			leftArrow();
    		}
    		selected = moveLeft;
    	} else {
    		mSelected--;
    		if(mSelected < 0) {
    			mSelected = mHeadsets.length-1;
    		} 
    		
    		//instantiate if this is our first call
    		if(moveRight == null) {
    			rightArrow();
    		}
    		selected = moveRight;
    	} //mSelected is now reset and in range
    	Log.d(TAG, "new mSelected " + mSelected + " : " + mHeadsets[mSelected] + " current fragment id= " + mHeadsets[mSelected]);
    	//the headset id must be set as a tag for the fragment to read when creating the view
    	
    	findViewById(R.id.rootView).setTag(mHeadsets[mSelected]);
		selected.start();
		//The "design for" text field is set here to match the new headset

    }
    
	/**
	 * Click listener set in xml to respond to button clicks
	 * @param btn
	 */
    public void mMenu(View btn) {
    	//set in XML
    	switch(btn.getId()) {
    	case R.id.catBack:
    		finish();
    		return;
    		//these two return to the headset catalog activity
    		//the catalog needs to "finish" - the logic of the Admin Console requires it
    	case R.id.catMenu:
    		setResult(Activity.RESULT_OK);
            finish();
            return;
    	case R.id.slideLearn:
    		final Intent tnt = new Intent(this, LearnMore.class);
    		//tnt.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP), nothing to clear, no back stack
    		tnt.putExtra(LearnMore.TAG, (Integer) btn.getTag());
    		startActivity(tnt);
    	}
    	
    }
    
    /******************************/
    class GD extends GestureDetector.SimpleOnGestureListener {
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
    	
    	@Override
        public boolean onDown(MotionEvent event) { 
    		// This is not used, returning true so that we get the rest of the gesture
    		// This gesture detector return won't stop the super from executing, it just goes first
    		return true;
        }
    	
		@Override
		public boolean onFling (MotionEvent start, MotionEvent finish, float velocityX, float velocityY) {
    		super.onFling(start, finish, velocityX, velocityY);
    		if(Math.abs(velocityX) < SWIPE_THRESHOLD_VELOCITY) {
    			return false;
    		}
    		if (start.getRawX() < finish.getRawX()) {
    			//swipe is going from left to right 
    			arrowButton(findViewById(R.id.catBtnLeft));
    		} else {
    			//swipe is from right to left
    			arrowButton(findViewById(R.id.catBtnRight));
    		}
    		return true;
    	}
    	
    }
  
}
    
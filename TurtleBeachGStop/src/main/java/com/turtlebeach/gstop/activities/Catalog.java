package com.turtlebeach.gstop.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.clickbrand.turtlebeach.model.HeadsetManager;
import com.turtlebeach.gstop.R;
import com.turtlebeach.gstop.internet.NWBaseActivity;

/**
 * This activity allows users to browse through the store's selection of headsets
 * Integer arrays of headset ids set up the headsets for each catalog section, set in order
 * They are used to create arrays of drawables that are set into imageview objects on the page
 */
public class Catalog extends NWBaseActivity{
	private static final String TAG = "HeadsetCatalog";
	
	//section 2, s2, top right
	public static final int[] S2_PS4 = { HeadsetManager.HEADSET_ID_ELITE800,
		HeadsetManager.HEADSET_ID_STEALTH500P,
		HeadsetManager.HEADSET_ID_STEALTH400, 
		HeadsetManager.HEADSET_ID_PX4, 
		HeadsetManager.HEADSET_ID_P12
		};
	//section 4, s4, lower right
	public static final int[] S4_PS3 = { HeadsetManager.HEADSET_ID_ELITE800,
		HeadsetManager.HEADSET_ID_STEALTH500P,
		HeadsetManager.HEADSET_ID_STEALTH400, 
		HeadsetManager.HEADSET_ID_PX4,
		HeadsetManager.HEADSET_ID_PX22,
		HeadsetManager.HEADSET_ID_P11
		};
	//section 1, s1, top left
	public static final int[] S1_XBOX1 = { HeadsetManager.HEADSET_ID_STEALTH500X,
		HeadsetManager.HEADSET_ID_XO7,
		HeadsetManager.HEADSET_ID_XO4, 
		HeadsetManager.HEADSET_ID_XO1
	};
	//section 3, s3, lower left
	public static final int[] S3_XBOX360 = { HeadsetManager.HEADSET_ID_X32, 
		HeadsetManager.HEADSET_ID_X12, 
		HeadsetManager.HEADSET_ID_XL1
	};
	//images for each section
	static Drawable[] s1, s2, s3, s4 = null;
	//indices to track which headset the user is viewing in each section
	int mDex1 = 0, mDex2 = 0, mDex3 = 0, mDex4 = 0;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.headset_catalog);
        
        sScreenName = "HeadsetCatalog";
        
        if(s4 == null) {
        	//this only has to run once no matter how many times we go through onCreate
        	s1 = HeadsetManager.fetchHeadsetSection(S1_XBOX1);
        	s2 = HeadsetManager.fetchHeadsetSection(S2_PS4);
        	s3 = HeadsetManager.fetchHeadsetSection(S3_XBOX360);
        	s4 = HeadsetManager.fetchHeadsetSection(S4_PS3);
        }
        //this code sets the image drawable and puts the headset ID on as a tag
        //A second tag is set on the imageview object to send the correct headset ids into the slider panel
        setImageButton(R.id.headset1, 0, s1[0].getConstantState().newDrawable(), S1_XBOX1);
        setImageButton(R.id.headset2, 1, s1[1].getConstantState().newDrawable(), S1_XBOX1);

        setImageButton(R.id.headset3, 0, s2[0].getConstantState().newDrawable(), S2_PS4);
        setImageButton(R.id.headset4, 1, s2[1].getConstantState().newDrawable(), S2_PS4);

        setImageButton(R.id.headset5, 0, s3[0].getConstantState().newDrawable(), S3_XBOX360);
        setImageButton(R.id.headset6, 1, s3[1].getConstantState().newDrawable(), S3_XBOX360);

        setImageButton(R.id.headset7, 0, s4[0].getConstantState().newDrawable(), S4_PS3);
        setImageButton(R.id.headset8, 1, s4[1].getConstantState().newDrawable(), S4_PS3);        
        
        //disable the left arrow in code since the xml property is not working...
        ((ImageButton)findViewById(R.id.hcS1BtnLeft)).setEnabled(false);
        ((ImageButton)findViewById(R.id.hcS2BtnLeft)).setEnabled(false);
        ((ImageButton)findViewById(R.id.hcS3BtnLeft)).setEnabled(false);
        ((ImageButton)findViewById(R.id.hcS4BtnLeft)).setEnabled(false);
    }    
    
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// This is where the back/main menu button clicks from the slide panel end up
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == Activity.RESULT_OK) {
			gotoMainMenu(null);
		} else {
			Log.i(TAG, "Back Button");
		}
	}


    void setImageButton(int btn_resource, int index, Drawable image, int[] sectionArray) {
    	final ImageButton tmp = (ImageButton) findViewById(btn_resource);
        tmp.setImageDrawable(image);
        Log.i(TAG, "Setting button and tags, index is: " + index);
        tmp.setTag(R.layout.catalog_slide_content, sectionArray[index]);
        tmp.setTag(R.layout.catalog_slide_panel, sectionArray);
    	
    }
    //xml button listener methods follow
    //there is one method for headset clicks and four identical methods for the navigation of each section
    
	/**
     * User has clicked an arrow button
     * The method increments/decrements the index for that section
     * The index value tracks which headset is showing in the leftmost spot for that section
     * Following the math, the method checks to see if the left/right arrows need to be enabled or disabled
     * @param arrowButton - the button that was clicked, increment or decrement
     */
    public void hcS1Click(View arrowButton) {
    	Log.v(TAG, "xbox1/s1 click, index is:" + mDex1);
    	switch(arrowButton.getId()) {
    	case R.id.hcS1BtnLeft:
    		mDex1--;
    		break;
    	case R.id.hcS1BtnRight:
    		mDex1++;
    		break;
    	}
    	//check the index value, if it is at max, disable the right arrow button
    	if(mDex1 == s1.length-2) {
			((ImageButton)findViewById(R.id.hcS1BtnRight)).setEnabled(false);
		} else {
			if(mDex1 == s1.length-3){
				((ImageButton)findViewById(R.id.hcS1BtnRight)).setEnabled(true);
			}
		}
    	//disable or enable the left arrow button
    	switch(mDex1) {
    	case 0:
    		((ImageButton)findViewById(R.id.hcS1BtnLeft)).setEnabled(false);
    		break;
    	case 1:
    		((ImageButton)findViewById(R.id.hcS1BtnLeft)).setEnabled(true);
    		break;
    	}
    	//set the new images into place
        //setImageButton(int btn_resource, int index, Drawable image, int[] sectionArray)

    	setImageButton(R.id.headset1, mDex1, s1[mDex1].getConstantState().newDrawable(), S1_XBOX1);
    	setImageButton(R.id.headset2, mDex1+1, s1[mDex1+1].getConstantState().newDrawable(), S1_XBOX1);
    }
    
    //Same as above method, index and button variables are adjusted for section 2
    public void hcS2Click(View arrowButton) {
    	Log.v(TAG, "ps4/s2 click, index is:" + mDex2);
    	switch(arrowButton.getId()) {
    	case R.id.hcS2BtnLeft:
    		mDex2--;
    		break;
    	case R.id.hcS2BtnRight:
    		mDex2++;
    		break;
    	}
    	
    	if(mDex2 == s2.length-2) {
			((ImageButton)findViewById(R.id.hcS2BtnRight)).setEnabled(false);
		} else {
			if(mDex2 == s2.length-3){
				((ImageButton)findViewById(R.id.hcS2BtnRight)).setEnabled(true);
			}
		}
    	
    	switch(mDex2) {
    	case 0:
    		((ImageButton)findViewById(R.id.hcS2BtnLeft)).setEnabled(false);
    		break;
    	case 1:
    		((ImageButton)findViewById(R.id.hcS2BtnLeft)).setEnabled(true);
    		break;
    	}
    	
    	setImageButton(R.id.headset3, mDex2, s2[mDex2].getConstantState().newDrawable(), S2_PS4);
    	setImageButton(R.id.headset4, mDex2+1, s2[mDex2+1].getConstantState().newDrawable(), S2_PS4);
    }
    
    //Same logic as hcS1Click, index and button variables are adjusted for section 3
    public void hcS3Click(View arrowButton) {
    	Log.v(TAG, "xbox360/s3 click, index is:" + mDex3);
    	switch(arrowButton.getId()) {
    	case R.id.hcS3BtnLeft:
    		mDex3--;
    		break;
    	case R.id.hcS3BtnRight:
    		mDex3++;
    		break;
    	}
    	
    	if(mDex3 == s3.length-2) {
			((ImageButton)findViewById(R.id.hcS3BtnRight)).setEnabled(false);
		} else {
			if(mDex3 == s3.length-3){
				((ImageButton)findViewById(R.id.hcS3BtnRight)).setEnabled(true);
			}
		}
    	
    	switch(mDex3) {
    	case 0:
    		((ImageButton)findViewById(R.id.hcS3BtnLeft)).setEnabled(false);
    		break;
    	case 1:
    		((ImageButton)findViewById(R.id.hcS3BtnLeft)).setEnabled(true);
    		break;
    	}
    	
    	setImageButton(R.id.headset5, mDex3, s3[mDex3].getConstantState().newDrawable(), S3_XBOX360);
    	setImageButton(R.id.headset6, mDex3+1, s3[mDex3+1].getConstantState().newDrawable(), S3_XBOX360);
    }
    
    // Index and button variables are set for section 4
    public void hcS4Click(View arrowButton) {
    	Log.v(TAG, "ps3/s4 click, index is:" + mDex4);
    	switch(arrowButton.getId()) {
    	case R.id.hcS4BtnLeft:
    		mDex4--;
    		break;
    	case R.id.hcS4BtnRight:
    		mDex4++;
    		break;
    	}
    	
    	if(mDex4 == s4.length-2) {
			((ImageButton)findViewById(R.id.hcS4BtnRight)).setEnabled(false);
		} else {
			if(mDex4 == s4.length-3){
				((ImageButton)findViewById(R.id.hcS4BtnRight)).setEnabled(true);
			}
		}
    	
    	switch(mDex4) {
    	case 0:
    		((ImageButton)findViewById(R.id.hcS4BtnLeft)).setEnabled(false);
    		break;
    	case 1:
    		((ImageButton)findViewById(R.id.hcS4BtnLeft)).setEnabled(true);
    		break;
    	}
    	
    	setImageButton(R.id.headset7, mDex4, s4[mDex4].getConstantState().newDrawable(), S4_PS3);
    	setImageButton(R.id.headset8, mDex4+1, s4[mDex4+1].getConstantState().newDrawable(), S4_PS3);
    }
    
    public void headsetClick(View v) {
    	//This is where we open up the slider panel
    	final Intent tnt = new Intent(this, CatalogSlider.class);
		tnt.putExtra(CatalogSlider.TAG, (Integer) v.getTag(R.layout.catalog_slide_content));
		Log.v(TAG, "tags on View?" + v.getTag(R.layout.catalog_slide_content));
		tnt.putExtra(CatalogSlider.GROUP, (int[]) v.getTag(R.layout.catalog_slide_panel));
		startActivityForResult(tnt, 99);
    }
    
    public void gotoMainMenu(View btn) {
    	//set in XML, call the menu
        finish();
    }
  
}
    
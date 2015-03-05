package com.clickbrand.turtlebeach.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.util.Log;

import com.clickbrand.turtlebeach.Constants;
import com.clickbrand.turtlebeach.R;

/**
 * Updating class for content updates
 * switching out some headsets, adding new and retiring old
 * Headset IDs are for the main arrays, tracking the various resources in arrays.xml
 * Featured headsets need not have a constant and be in the list
 * 		since there are no slider/details for featured
 */
public class HeadsetManager {
    private static String LOG_TAG = "HeadsetManager";
    public static Drawable[] sFeatured;
    //scratchpad arrays for constructing stateful drawable button graphics
    private static String[] nState = null, pState = null;
    static Headset[] sHeadsetList = null;
    
    /**
     * The group titles for special editions.
     */
    public static String[] sSpecialEditionGroupTitles;

	//headset ids are the index value for the headset resource arrays
	public static final int HEADSET_ID_NLA = 0;

	public static final int HEADSET_ID_P11 = 1;
	public static final int HEADSET_ID_P12 = 2;
	public static final int HEADSET_ID_P4C = 3;
	public static final int HEADSET_ID_PLA = 4;

	public static final int HEADSET_ID_PX3 = 5;
	public static final int HEADSET_ID_PX4 = 6;
	public static final int HEADSET_ID_PX22 = 7;
	public static final int HEADSET_ID_PX22W = 8;
	public static final int HEADSET_ID_PX51 = 9;
	public static final int HEADSET_ID_SHADOW = 10;
	public static final int HEADSET_ID_SPECTRE = 11;
	public static final int HEADSET_ID_STEALTH400 = 12;
	public static final int HEADSET_ID_STEALTH500P = 13;
	public static final int HEADSET_ID_STEALTH500X = 14;
	public static final int HEADSET_ID_TITAN = 15;
	public static final int HEADSET_ID_X12 = 16;
	public static final int HEADSET_ID_X12W = 17;
	public static final int HEADSET_ID_X32 = 18;
	public static final int HEADSET_ID_X42 = 19;
	public static final int HEADSET_ID_XL1 = 20;
	public static final int HEADSET_ID_XLA = 21;
	public static final int HEADSET_ID_XP510 = 22;
	public static final int HEADSET_ID_XP7 = 23;
	public static final int HEADSET_ID_XO4 = 24;
	public static final int HEADSET_ID_XO1 = 25;
	public static final int HEADSET_ID_XO7 = 26;
	//Adding in the new PC headsets
	public static final int HEADSET_ID_Z11 = 27;
	public static final int HEADSET_ID_Z22 = 28;
	public static final int HEADSET_ID_Z300 = 29;
	public static final int HEADSET_ID_Z60 = 30;
	public static final int HEADSET_ID_RECON320 = 31;
	//Another new one
	public static final int HEADSET_ID_ELITE800 = 32;
	public static final int HEADSET_ID_COD_PRESTIGE = 33;
	public static final int HEADSET_ID_HOTS_HEROESOFTHESTORM = 34;
	public static final int HEADSET_ID_MARVEL_DISNEYSUPERHEROES = 35;
	public static final int HEADSET_ID_SENTINELTASKFORCEPS4 = 36;
	public static final int HEADSET_ID_SENTINELTASKFORCEX1 = 37;
	public static final int HEADSET_ID_TITANFALL_ATLAS = 38;
	public static final int HEADSET_ID_STARWARS = 39;

	/**
	 * Used by the headset selector.  Headsets are configured with different traits, 
	 * and the headset selector code whittles down the headsets based on the traits 
	 * that the user selects.
	 * 
	 * May be cleaner and faster to create an array of headset ids for each trait
	 */
	public enum Trait {
		MULTI_PLATFORM,
		PC,
		PS3,
		PS4,
		STEREO_SOUND,
		SURROUND_SOUND,
		WIRED,
		WIRELESS,
		XBOX_360,
		XBOX_ONE
	}
	
	/**
     * Resource ids that are mapped to traits.
     */
	public static int[] sTrtLabels = new int[] {
		R.string.hsTraitMultiPlatform,
		R.string.hsTraitPC,
		R.string.hsTraitPS3,
		R.string.hsTraitPS4,
		R.string.hsTraitStereo,
		R.string.hsTraitSurroundSound,
		R.string.hsTraitWired,
		R.string.hsTraitWireless,
		R.string.hsTraitXbox360,
		R.string.hsTraitXboxOne
	};
	
	/*
	 * sTraitLabels = new HashMap<Trait, Integer>();
		replaced with calls to ordinal and a parallel integer array
	 */
	
	/**
	 * An array of Trait arrays, one for each headset.
	 */
	private static final Trait[][] sHeadsetTraits = new Trait[][]{
		{},//HEADSET_ID_NLA = 0;
		{Trait.PS3, Trait.WIRED},	//HEADSET_ID_P11 = 1;
		{Trait.PS4, Trait.WIRED},  //HEADSET_ID_P12 = 2;
		{},//HEADSET_ID_P4C = 3;
		{},//HEADSET_ID_PLA = 4;
		{},//HEADSET_ID_PX3 = 5;
		{Trait.XBOX_360, Trait.WIRELESS, Trait.PS3, Trait.PS4, Trait.MULTI_PLATFORM, Trait.SURROUND_SOUND},	//HEADSET_ID_PX4 = 6;
		{Trait.XBOX_360, Trait.WIRED, Trait.PS3, Trait.MULTI_PLATFORM},	//HEADSET_ID_PX22 = 7;
		{},	//HEADSET_ID_PX22W = 8;
		{},	//HEADSET_ID_PX51 = 9;
		{},	//HEADSET_ID_SHADOW = 10;
		{},	//HEADSET_ID_SPECTRE = 11;
		{Trait.PS3, Trait.WIRELESS, Trait.PS4, Trait.STEREO_SOUND},	//HEADSET_ID_STEALTH400 = 12;
		{Trait.PS3, Trait.WIRELESS, Trait.PS4, Trait.SURROUND_SOUND},	//HEADSET_ID_STEALTH500P = 13;
		{Trait.XBOX_ONE, Trait.WIRELESS},	//HEADSET_ID_STEALTH500X = 14;
		{},	//HEADSET_ID_TITAN = 15;
		{Trait.XBOX_360, Trait.WIRED},	//HEADSET_ID_X12 = 16;
		{},	//HEADSET_ID_X12W = 17;
		{Trait.XBOX_360, Trait.WIRELESS, Trait.STEREO_SOUND},	//HEADSET_ID_X32 = 18;
		{},	//HEADSET_ID_X42 = 19;
		{Trait.XBOX_360, Trait.WIRED},	//HEADSET_ID_XL1 = 20;
		{},	//HEADSET_ID_XLA = 21;
		{},	//HEADSET_ID_XP510 = 22;
		{},	//HEADSET_ID_XP7 = 23;
		{Trait.XBOX_ONE, Trait.WIRED},	//HEADSET_ID_XO4 = 24;
		{Trait.XBOX_ONE, Trait.WIRED},	//HEADSET_ID_XO1 = 25;
		{Trait.XBOX_ONE, Trait.WIRED},	//HEADSET_ID_XO7 = 26;
		{Trait.PC, Trait.WIRED, Trait.STEREO_SOUND},	//HEADSET_ID_Z11 = 27;
		{},	//HEADSET_ID_Z22 = 28;
		{},	//HEADSET_ID_Z300 = 29;
		{Trait.PC, Trait.WIRED, Trait.SURROUND_SOUND},	//HEADSET_ID_Z60 = 30;
		{Trait.PC, Trait.WIRED, Trait.SURROUND_SOUND},	//HEADSET_ID_RECON320 = 31;
		{Trait.PS3, Trait.WIRELESS, Trait.PS4, Trait.SURROUND_SOUND},	//HEADSET_ID_ELITE800 = 32;
		{},	//HEADSET_ID_COD_PRESTIGE = 33;
		{Trait.PC, Trait.WIRED, Trait.STEREO_SOUND},	//HEADSET_ID_HOTS_HEROESOFTHESTORM = 34;
		{},	//HEADSET_ID_MARVEL_DISNEYSUPERHEROES = 35;
		{},	//HEADSET_ID_SENTINELTASKFORCEPS4 = 36;
		{},	//HEADSET_ID_SENTINELTASKFORCEX1 = 37;
		{Trait.PC, Trait.XBOX_360, Trait.WIRED, Trait.XBOX_ONE, Trait.STEREO_SOUND},	//HEADSET_ID_TITANFALL_ATLAS = 38;
		{Trait.PC, Trait.WIRED, Trait.STEREO_SOUND}	//HEADSET_ID_STARWARS = 39;
	};
    
    public static Drawable[] fetchHeadsetSection(int[] sectionIDs) {
    	ArrayList<Drawable> list = new ArrayList<Drawable>();
    	
    	for(int dex = 0; dex < sectionIDs.length; dex++) {
    		int index = sectionIDs[dex];
    		
    		if ((index < sHeadsetList.length) && (sHeadsetList[index] != null)) {
    			//null pointer reported in Crashlytics 
    			//dropped .getConstantState().newDrawable() - code can just return the catalogThumb
    			list.add(sHeadsetList[sectionIDs[dex]].catalogThumb);
    		} else {
    			Log.e(LOG_TAG, "No headset for headset ID: " + index);
    		}
    	}
    	return list.toArray(new Drawable[list.size()]);
    }
    
    
    /**
     * @param id, headset id constant, array index
     * @return the Headset object
     */
    public static Headset getHeadsetById(int id) {
    	if(sHeadsetList != null && id < sHeadsetList.length) {
    		return sHeadsetList[id];
    	} else {
    		Log.e(LOG_TAG, "Error finding headset ID: " + id);
    	}
    	
    	return null;
    }
    
	public static void init(Context ctx) {
		
		// all headsets from the spreadsheet now are added using the arrays
		// init the headsets and add them to the list
		
    	Headset headset;
		final Resources res = ctx.getResources();
		sSpecialEditionGroupTitles = res.getStringArray(R.array.special_edition_group_labels);
		final String[] details = res.getStringArray(R.array.detailsOnSDCard);
		final String[] slider = res.getStringArray(R.array.sliderImages);
		final String[] names = res.getStringArray(R.array.headsetNames);
		final String[] sliderDescription = res.getStringArray(R.array.sliderHType);
		final String[] sliderDesign = res.getStringArray(R.array.sliderDesign);
		final String[] sliderLogos = res.getStringArray(R.array.sliderLogos);
		final String[] selectorLogos = res.getStringArray(R.array.selectorLogos);
    	sHeadsetList = new Headset[names.length];
		// set up the list
		for(int index = 0; index < names.length; index++) {
			headset = new Headset();
			//The device id should equal the dex value
			headset.deviceID = index;
			headset.deviceName = names[headset.deviceID];
			headset.sliderImage = slider[headset.deviceID];
			headset.deviceDetails = details[headset.deviceID];
			headset.sliderText = sliderDescription[headset.deviceID];
			headset.sliderDesign = sliderDesign[headset.deviceID];
			headset.sliderLogo = sliderLogos[headset.deviceID];
			// Construct a stateful drawable from bitmaps on the SD Card
			headset.catalogThumb = getCatalogButton(res, headset.deviceID);
			headset.thumb = nState[headset.deviceID];
			headset.thumbPress = pState[headset.deviceID];
			headset.traits = sHeadsetTraits[headset.deviceID];
			headset.selectorLogo = selectorLogos[headset.deviceID];
			sHeadsetList[index] = headset;
			Log.i(LOG_TAG, "initialize " + headset.deviceName + " id " + headset.deviceID);
		}
		initFeatured(ctx);

	}
	
	public static void init(Context ctx, int[] headsetIds) {
		//The headsetIds map specifies which headsets are needed for this store 
		//init the headsets for this store and add them to the list
    	Headset headset;
		final Resources res = ctx.getResources();
		sSpecialEditionGroupTitles = res.getStringArray(R.array.special_edition_group_labels); 
		final String[] details = res.getStringArray(R.array.detailsOnSDCard);
		final String[] slider = res.getStringArray(R.array.sliderImages);
		final String[] names = res.getStringArray(R.array.headsetNames);
		final String[] sliderDescription = res.getStringArray(R.array.sliderHType);
		final String[] sliderDesign = res.getStringArray(R.array.sliderDesign);
		final String[] sliderLogos = res.getStringArray(R.array.sliderLogos);
		final String[] selectorLogos = res.getStringArray(R.array.selectorLogos);
		sHeadsetList = new Headset[names.length];
		Arrays.fill(sHeadsetList, null);
		// set up the list
		for(int i = 0; i < headsetIds.length; i++) {
			if (headsetIds[i] < names.length) {
				headset = new Headset();
				//The device id should equal the dex value
				headset.deviceID = headsetIds[i];
				headset.deviceName = names[headset.deviceID];
				headset.sliderImage = slider[headset.deviceID];
				headset.deviceDetails = details[headset.deviceID];
				headset.sliderText = sliderDescription[headset.deviceID];
				headset.sliderDesign = sliderDesign[headset.deviceID];
				headset.sliderLogo = sliderLogos[headset.deviceID];
				// Construct a stateful drawable from bitmaps on the SD Card
				headset.catalogThumb = getCatalogButton(res, headset.deviceID);
				headset.thumb = nState[headset.deviceID];
				headset.thumbPress = pState[headset.deviceID];
				headset.traits = sHeadsetTraits[headset.deviceID];
				headset.selectorLogo = selectorLogos[headset.deviceID];
				sHeadsetList[headsetIds[i]] = headset;
				Log.i(LOG_TAG, "initialize " + headset.deviceName + " id " + headset.deviceID);
			}
		}
		initFeatured(ctx);
		
	}
     
	public static void initFeatured(Context ctx) {
		if(sFeatured == null) {
			sFeatured = new Drawable[4];
		}
		
		final Resources res = ctx.getResources();
		final String[] thumbFile = res.getStringArray(R.array.featuredButton);
		final String[] pressFile = res.getStringArray(R.array.featuredButtonPress);
		Bitmap n, p;
    	File tmp;
    	StateListDrawable states;
    	//Populate state list drawable, start with the normal state
    	for(int position = 0; position < 4; position++) {
    		tmp = new File(Constants.getButtonDir(), thumbFile[position]);
    		if(tmp.exists()) {
    			n = BitmapFactory.decodeFile(tmp.getAbsolutePath());
    		} else {
    			sFeatured[position] = null;
    			break;
    		}
    		//now the pressed state
    		tmp = new File(Constants.getButtonDir(), pressFile[position]);
    		if(tmp.exists()) {
    			p = BitmapFactory.decodeFile(tmp.getAbsolutePath());
    		} else {
    			sFeatured[position] = null;
    			break;
    		}
    		states = new StateListDrawable();
        	
    		states.addState(new int[]{android.R.attr.state_pressed}, new BitmapDrawable(res, p));
    		states.addState(new int[]{}, new BitmapDrawable(res, n));
    		sFeatured[position] = states;
    	}
	    	
	}
    
	//The Featured headsets for the original Target release are (alphabetically):
    //HEADSET_ID_P12, HEADSET_ID_PX22, HEADSET_ID_X12, HEADSET_ID_XO7
	//The BBY release has stealth 500P, stealth 500X, xo7 and elite 800
    /**
     * The Featured Headsets in this library can be tailored to the project. 
     * Arrays that describe the normal and pressed states of the featured headset are found in arrays.xml
     * Actual images are found on the SD Card with all the other catalog assets
     * @param ctx, Context needed to fetch resources and create drawables
     * @param position, which featured headset
     * @return The stateful drawable to set in the Featured Headset Catalog
     */
    public static Headset findHeadsetById(int id, Context ctx) {
    	if (sHeadsetList == null) {
            init(ctx);
        }
        
        for(Headset headset : sHeadsetList) {
            if(headset != null && headset.deviceID == id) {
            	Log.i(LOG_TAG, "headset found: " + id);
                return headset;
            }
        } //end for loop, no more in list!
        Log.e(LOG_TAG, "headset not found: " + id);
        return null;
    }
    
    
    /**
     * Given app resources and the headset id, return the headset button for the catalog - a stateful drawable
     * @param res
     * @param position
     * @return the Catalog button, headset images
     */
    public static Drawable getCatalogButton(Resources res, int position) {
    	if(nState == null) {
    		nState = res.getStringArray(R.array.thumbs);
        	pState = res.getStringArray(R.array.thumbsPress);	
        	Log.d(LOG_TAG, "initialize arrays: " + nState[0] + " " + pState[0]);
    	}
    	
    	Bitmap n, p;
    	File tmp;
    	
    	//Populate state list drawable, start with the normal state
		tmp = new File(Constants.getButtonDir(), nState[position]);
		if(tmp.exists()) {
			n = BitmapFactory.decodeFile(tmp.getAbsolutePath());
		} else {
			Log.e(LOG_TAG, "Can't find normal file: " + tmp.getAbsolutePath());
			return null;
		}
		//now the pressed state
		tmp = new File(Constants.getButtonDir(), pState[position]);
		if(tmp.exists()) {
			p = BitmapFactory.decodeFile(tmp.getAbsolutePath());
		} else {
			Log.e(LOG_TAG, "Can't find pressed file: " + tmp.getAbsolutePath());
			return null;
		}
    	
    	final StateListDrawable states = new StateListDrawable();
    	
		states.addState(new int[]{android.R.attr.state_pressed}, new BitmapDrawable(res, p));
		states.addState(new int[]{}, new BitmapDrawable(res, n));
		return states;
    }
    
    /**
     * Given a headset id, return the slide panel image for that headset
     * @param position
     * @return the slide panel headset image bitmap 
     */
    public static Bitmap getSlidePanel(int position, boolean logo) { 
    	//position is the headset id    	
    	File tmp;
    	if(logo) {
    		tmp = new File(Constants.getSlidePanels(), sHeadsetList[position].sliderLogo);
    	} else {
    		tmp = new File(Constants.getSlidePanels(), sHeadsetList[position].sliderImage);
    	}
    	//Error check
		if(tmp.exists()) {
			return BitmapFactory.decodeFile(tmp.getAbsolutePath());
		} else {
			return null;
		}
    	
    }
    
    public static Bitmap getSelectorLogo(int position) { 
    	//position is the headset id    	
    	File tmp;
    	tmp = new File(Constants.getSlidePanels(), sHeadsetList[position].selectorLogo);
    	//Error check
		if(tmp.exists()) {
			return BitmapFactory.decodeFile(tmp.getAbsolutePath());
		} else {
			return null;
		}
    	
    }
    
    public static Bitmap getDetailsImage(int position) { 
    	//position is the headset id    	
    	File tmp = new File(Constants.getDetailsDir(), sHeadsetList[position].deviceDetails);
    	
    	//Error check
		if(tmp.exists()) {
			return BitmapFactory.decodeFile(tmp.getAbsolutePath());
		} else {
			return null;
		}
    	
    }
    
    //TODO change from a collection to an array
    public static List<Headset> getFilteredHeadsets(List<Trait> filterTraits) {
    	List<Headset> filteredHeadsets = new ArrayList<Headset>();
    	for(Headset headset : sHeadsetList) {
    		if(headset != null && headset.traits != null) {
    			boolean hasAllTraits = true;
    			
    			for(Trait filterTrait : filterTraits) {
    				boolean found = false;
    				for(Trait headsetTrait : headset.traits) {
    					if(filterTrait == headsetTrait) {
    						found = true;
    						break;
       					}
    				}
    				if(!found) {
    					hasAllTraits = false;
    					break;
    				}
    			}

    			if(hasAllTraits) {
    				Log.d("TK","Adding headset " + headset.deviceName);
    				filteredHeadsets.add(headset);
    			}
    		}
    	}
    	return filteredHeadsets;
    }
        
    
    
}

package com.clickbrand.turtlebeach.model;

import android.graphics.drawable.Drawable;
import android.os.Parcel;

import com.clickbrand.turtlebeach.R;
import com.clickbrand.turtlebeach.model.HeadsetManager.Trait;

/**
 * This data structure holds arrays of assets used to display French/English
 * Other assets are found on the SD Card
 */
public class Headset {
		
	public int deviceID; //this is the array index for the headset
    public String deviceName;
    String thumb;
    String thumbPress;
    public String catalogBG;
    public String sliderImage;
    public String deviceDetails;
    public String sliderText;
    public String sliderDesign;
    public String sliderLogo;
    public String selectorLogo;
    public Drawable catalogThumb;
    public Trait[] traits;

    /*@Override
    public void writeToParcel(Parcel aParcel, int id) {
        aParcel.writeInt(deviceID);
        aParcel.writeString(deviceName);
        aParcel.writeString(thumb);
        aParcel.writeString(thumbPress);
        aParcel.writeString(catalogBG);
        aParcel.writeString(sliderImage);
        aParcel.writeString(deviceDetails);
        aParcel.writeString(sliderText);
        aParcel.writeString(sliderDesign);
        aParcel.writeString(sliderLogo);
        aParcel.writeString(selectorLogo);
    }*/

    public Headset() { }

    public Headset(Parcel aIn) {
        deviceID = aIn.readInt();
        deviceName = aIn.readString();
        thumb = aIn.readString();
        thumbPress = aIn.readString();
        catalogBG = aIn.readString();
        sliderImage = aIn.readString();
        deviceDetails = aIn.readString();
        sliderText = aIn.readString();
        sliderDesign = aIn.readString();
        sliderLogo = aIn.readString();
        selectorLogo = aIn.readString();
    }

    public int getArrayResource(boolean details) {
    	if(details) {
    		return sDetailBullets[deviceID];
    	} else {
    		return sSliderBullets[deviceID];
    	}
    }

	//This array holds pointers to the array of bullet points for each headset's learn more display
	//The headsets are in alphabetical order
	public static final int[] sDetailBullets = new int[] {
		R.array.nlaDetails, 
		R.array.p11Details,
		R.array.p12Details,
		R.array.p4cDetails, 
		R.array.plaDetails, -1, //px3 
		R.array.pxDetails, 
		R.array.px22Details,
		R.array.px22Details, -1, -1, -1, //px51, shadow, spectre
		R.array.stealth400Details,
		R.array.stealth500PDetails,
		R.array.stealth500XDetails, -1, //titan
		R.array.x12Details,
		R.array.x12Details,
		R.array.x32Details, -1, //x42
		R.array.xl1Details, -1, -1, -1, //xla, xp510, xp7
		R.array.xofourDetails,
		R.array.xooneDetails,
		R.array.xosevenDetails,
		R.array.z11Details, -1, -1, //z22, z300
		R.array.z60Details,
		R.array.recon320Details,
		R.array.elite800Details,
		R.array.prestigeDetails,
		R.array.blizzardDetails,
		R.array.marvelDetails,
		R.array.taskForcePsDetails,
		R.array.taskForceXboxDetails,
		R.array.titanfallAtlasDetails,
		R.array.starwarsDetails
	};
	
	//This array holds pointers to the array of bullet points for each headset's slider fragment
	public static final int[] sSliderBullets = new int[] {
		R.array.nlaBullets,
		R.array.p11Bullets,
		R.array.p12Bullets,
		R.array.p4cBullets, 
		R.array.plaBullets, -1, //px3
		R.array.px4Bullets,
		R.array.px22Bullets,
		R.array.px22Bullets, -1, -1, -1, //px51, shadow, spectre
		R.array.stealth400Bullets,
		R.array.stealth500PBullets,
		R.array.stealth500XBullets, -1, //titan
		R.array.x12Bullets,
		R.array.x12Bullets,
		R.array.x32Bullets, -1, //x42
		R.array.xl1Bullets, -1, -1, -1, //xla, xp510, xp7
		R.array.xofourBullets,
		R.array.xooneBullets,
		R.array.xosevenBullets,
		R.array.z11Bullets, -1, -1, //z22, z300
		R.array.z60Bullets,
		R.array.recon320Bullets,
		R.array.elite800Bullets,
		R.array.prestigeBullets,
		R.array.blizzardBullets,
		R.array.marvelBullets,
		R.array.taskForcePsBullets,
		R.array.taskForceXboxBullets,
		R.array.titanfallAtlasBullets,
		R.array.starwarsBullets
	};
	
	/**
	 * Defines the logos used at the end of the bullet list on the slider screen. 
	 * Each entry in the first dimension represents a headset, while the second 
	 * dimension represents the logos to display for each headset.
	 */
	public static final int[][] sSliderLogos = new int[][] {
		new int[]{R.drawable.img_logo_nintendoseal},
		new int[]{-1},
		new int[]{-1},
		new int[]{-1}, 
		new int[]{-1}, //px3, px4
		new int[]{-1}, //px3, px4
		new int[]{R.drawable.img_logo_dolbydigital}, //px4
		new int[]{R.drawable.img_logo_mlg},
		new int[]{R.drawable.img_logo_mlg},
		new int[]{-1}, 
		new int[]{-1}, 
		new int[]{-1},
		new int[]{-1},
		new int[]{R.drawable.img_logo_dts},
		new int[]{R.drawable.img_logo_designed_for_xbox, R.drawable.img_logo_dts}, //display two for stealth 500x
		new int[]{-1}, //titan
		new int[]{-1},
		new int[]{-1},
		new int[]{R.drawable.img_logo_5ghzwifi}, 
		new int[]{-1}, //x42
		new int[]{-1}, 
		new int[]{-1}, 
		new int[]{-1}, 
		new int[]{-1}, //xla, xp510, xp7
		new int[]{R.drawable.img_logo_designed_for_xbox},
		new int[]{R.drawable.img_logo_designed_for_xbox},
		new int[]{R.drawable.img_logo_designed_for_xbox}, 
		new int[]{-1}, 
		new int[]{-1}, 
		new int[]{-1}, //z11, z22, z300
		new int[]{R.drawable.img_logo_dts},
		new int[]{R.drawable.img_logo_dolby_headphones},
		new int[]{R.drawable.img_logo_dts, R.drawable.img_logo_bluetooth},
		new int[]{R.drawable.img_logo_activision_rv, R.drawable.img_logo_sledgehammer_rv, R.drawable.img_logo_designed_for_xbox}, //prestige
		new int[]{R.drawable.img_logo_licblizzard},
		new int[]{R.drawable.img_logo_disney_infinite, R.drawable.img_logo_marvel},
		new int[]{R.drawable.img_logo_activision_rv, R.drawable.img_logo_sledgehammer_rv}, //taskforce PS4
		new int[]{R.drawable.img_logo_activision_rv, R.drawable.img_logo_sledgehammer_rv, R.drawable.img_logo_designed_for_xbox}, //taskforce xbox
		new int[]{R.drawable.img_logo_designed_for_xbox, R.drawable.img_logo_respawn, R.drawable.img_logo_ea},
		new int[]{-1}

	};
}

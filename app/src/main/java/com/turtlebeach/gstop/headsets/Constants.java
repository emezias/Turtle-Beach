package com.turtlebeach.gstop.headsets;

import java.io.File;

import android.os.Build;
import android.os.Environment;
import android.util.Log;

/**
 * Directory access, file finding
 * External directories are manufacturer specific and hard-coded here
 * Code looks for an "a" in the manufacturer name, Asus and Acer work with the getExternalStorageDirectory
 * Lynx is coded to /mnt/extsd
 * Additional manufacturer logic goes into getRootDir and getUpdateDir methods
 */
public class Constants {
	private static final String TAG = "Constants";

	public static final int MASTER_TIME_RESTART = 10 * 60; // in sec
	public static String ASUS_EXTERNAL_SD = "/Removable/MicroSD/";
	public static final String ACER_EXT_SD = "mnt/external_sd/";
	public static final String CONTENT_DIR = "turtle_beach";
	public static final String LYNX_ROOT = "/mnt/extsd/";

    public static final String STORE_NAME = "Store Name";
    public static final String STORE_NUMBER = "Store Number";
    public static final String STORE_TYPE = "Store Type";
    public static final String STORE_ZIP = "Store Zip";
    public static final String APP_VERSION = "App Version";
	
	public static String getRootDir() {
		// The data directory will not work for Lynx, leaving data on emulated drive
		if (Build.MANUFACTURER.toLowerCase().contains("a")) {
            Log.d(TAG, "getting dir: " + Environment.getExternalStorageDirectory() + File.separator
                    + Constants.CONTENT_DIR);
			return Environment.getExternalStorageDirectory() + File.separator
					+ Constants.CONTENT_DIR;
		} else {
			return LYNX_ROOT + Constants.CONTENT_DIR;
		}

	}

	public static String getUpdateDir() {
		// TODO
		return ACER_EXT_SD + CONTENT_DIR;
	}

	public static String getFeatureVideo() {
		return getRootDir() + File.separator + "_selectheadset";
	}

	public static String getVideo() {
		return getRootDir() + File.separator + "video";
	}

	public static String getDemoDir() {
		Log.d(TAG, "get demo dir" + getRootDir() + File.separator + "_demo");
		return getRootDir() + File.separator + "_demo";
	}

	public static String getDetailsDir() {
		return getRootDir() + File.separator + "details";
	}

	public static String getButtonDir() {
		return getRootDir() + File.separator + "catalog";
	}

	public static String getSlidePanels() {
		return getRootDir() + File.separator + "slidePanel";
	}

	public static boolean isRemoteSDAvailable() {
		final File dir = new File(ACER_EXT_SD, CONTENT_DIR);
		if (dir.exists() && dir.isDirectory() && dir.list() != null
				&& dir.list().length > 0) {
			return true;
		} else {
			return false;
		}
	}

		// might be nice to use this...
		/*
		 * if(Environment.isExternalStorageRemovable()) { final File dir = new
		 * File(Environment.getExternalStorageDirectory(), CONTENT_DIR); return
		 * dir.exists() && dir.isDirectory() && dir.list() != null &&
		 * dir.list().length > 0; } else { return false; }
		 */

}

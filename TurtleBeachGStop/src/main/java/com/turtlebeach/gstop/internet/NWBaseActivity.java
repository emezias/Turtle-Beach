package com.turtlebeach.gstop.internet;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;

import com.clickbrand.turtlebeach.Analytics;
import com.clickbrand.turtlebeach.ui.BaseActivity;
import com.localytics.android.LocalyticsSession;
import com.turtlebeach.gstop.R;

/**
 * This class builds from the library BaseActivity to implement Localytics and use the internet
 * Base reporting of app events is only in the BBY app since those devices have wifi
 * It will do some error checking, report a charger unplugged or failed attempts to get to the Console
 * @author emezias
 *
 */
public class NWBaseActivity extends BaseActivity {
	private static final String TAG = "NWBaseActivity";
    protected static LocalyticsSession mSession = null;
	public static final String UNK = "unknown";
	public static String sStoreName, sStoreNumber;
	protected String sScreenName = getClass().getSimpleName();
	
	public static final String TYPE = "Best Buy";
	
	public static final String STORE_NAME = "Store Name";
	public static final String STORE_NUMBER = "Store Number";
	public static final String STORE_TYPE = "Store Type";
	public static final String STORE_ZIP = "Store Zip";
	public static final String APP_VERSION = "App Version";

	public static final String EVENT_APP_START = "app_start";
	public static final String EVENT_DISK_FAILURE = "disk_access_failure";
	public static final String EVENT_NO_INTERNET = "internet_access_failure";
	public static final String EVENT_PING = "ping";
	public static final String EVENT_CHARGE = "charger_event";
	public static final String EVENT_REBOOT = "reboot";
	public static final String EVENT_SAVED_DATA = "saved_data";
	public static final String EVENT_PWD = "password_shown";
	public static final String EVENT_BAD_PWD = "password_incorrect";
	public static final String EVENT_APP_EXIT = "exit_quit";
	public static final String EVENT_CANCEL_EXIT = "exit_cancel";
	
	// Localytics Methods
	public static void startLocalyticsSession(Context ctx) {
		if (mSession != null) {
			mSession.close();
		}
		
		LocalyticsSession.setSessionExpiration(0);
		mSession = new LocalyticsSession(ctx);
		updateCustomDimensions(ctx);
		mSession.open();
		localyticsUploadWithCheck(ctx);
	}
	
	public static void stopLocalyticsSession(Context ctx) {
		if (mSession != null) {
			mSession.close();
			localyticsUploadWithCheck(ctx);
			mSession = null;
		}
	}
	
	public static void updateCustomDimensions(Context ctx) {
		if (mSession != null) {
			final SharedPreferences peace = PreferenceManager
					.getDefaultSharedPreferences(ctx);
			mSession.setCustomDimension(0, peace.getString(STORE_NUMBER, NWBaseActivity.UNK));
			mSession.setCustomDimension(1, peace.getString(STORE_ZIP, NWBaseActivity.UNK));
			mSession.setCustomDimension(2, peace.getString(STORE_NAME, NWBaseActivity.UNK));
			mSession.setCustomDimension(3, peace.getString(STORE_TYPE, TYPE));
		}
	}
	
	public static void localyticsUploadWithCheck(Context ctx) {
		if (mSession != null) {
			if (isNetworkAvailable(ctx)) {
				mSession.upload();
			} else {
	        	Map<String, String> tmp = new HashMap<String, String>();
	        	tmp.put(Analytics.ATTRIBUTE_TIME, getLocalDateTimeString(ctx));
	        	mSession.tagEvent(NWBaseActivity.EVENT_NO_INTERNET, tmp);
			}
		}
	}
    
    public static void logEvent(Context ctx, String action, Map<String, String> attrs) {
    	if(mSession != null && action != null) {
        	Map<String, String> tmp = attrs;
        	if (tmp == null) {
        		tmp = new HashMap<String, String>();
        	}
        	
        	tmp.put(Analytics.ATTRIBUTE_TIME, getLocalDateTimeString(ctx));
        	mSession.tagEvent(action, tmp);
			localyticsUploadWithCheck(ctx);
    	}
    }
	
	public static String getLocalDateTimeString(Context ctx) {
		final SimpleDateFormat format = new SimpleDateFormat(ctx.getString(R.string.dateTimeFormat));
		return format.format(new Date());
	}
	
	
	public static void setGPSValue(Context ctx, Location loc) {
		final Geocoder geocoder =
                new Geocoder(ctx, Locale.getDefault());
        
        try {
            /*
             * Return 1 address.
             */
            final List<Address> addresses = geocoder.getFromLocation(loc.getLatitude(),
                    loc.getLongitude(), 1);
            if (addresses != null && addresses.size() > 0) {
                // Get the zip from the first address
                final String zip = addresses.get(0).getPostalCode(); 
                final SharedPreferences.Editor ed = PreferenceManager.getDefaultSharedPreferences(ctx).edit();
                if(zip == null || zip.isEmpty()) {
                	ed.putString(STORE_ZIP, UNK);
                	Log.v(TAG, "(setting) Location of Kiosk is: " + UNK + " for lat/long " + loc.getLatitude() + "/" + loc.getLongitude());
                } else {
                	ed.putString(STORE_ZIP, zip);
                	Log.v(TAG, "(setting) Location of Kiosk is: " + zip + " for lat/long " + loc.getLatitude() + "/" + loc.getLongitude());
                }               
        		ed.commit();
        		
            } else {
                Log.e(TAG, "No address found");
                
            }

        } catch (IOException e1) {
	        Log.e("LocationSampleActivity",
	                "IO Exception in getFromLocation()");
	        e1.printStackTrace();	        
        } catch (IllegalArgumentException e2) {
        // Error message to post in the log
	        Log.e("LocationSampleActivity", "Illegal arguments " +
	                Double.toString(loc.getLatitude()) +
	                " , " +
	                Double.toString(loc.getLongitude()) +
	                " passed to address service");
	        e2.printStackTrace();
        
        }
	}

	/**
	 * This will check before creating Localytics data
	 * @param context - the activity
	 * @return
	 */
	private static NetworkInfo sNetwork;
	//keep this to a single allocation rather than allocate and cleanup many times
    public static boolean isNetworkAvailable(Context context) {
		sNetwork = ((ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE))
				.getActiveNetworkInfo();
		if (sNetwork != null && sNetwork.isConnected()) {
			return true;
		}
		
		return false;
	}

	@Override
    protected void onResume() {
        super.onResume();
        
        if (mSession != null && sScreenName != null) {
     		mSession.tagScreen(sScreenName);
       		mSession.upload();	
        } //else? shouldn't this be allocated if null?
	}
	
	public static void setGPSForLocalytics(final Context ctx) {
    	//Note lastKnown location could be out-of-date, for example if the device was turned off and moved to another location
    	//Device can do a check for location if the data is more than one week old
    	final LocationManager locationManager = (LocationManager) ctx.getSystemService(LOCATION_SERVICE);
		Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if(location == null || location.getTime()+604800000 < System.currentTimeMillis()) {
			location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			
		} 
		Log.v(TAG, "got location from network? " + (location != null));
		//Try another provider if the location is stale
		if(location == null || location.getTime()+604800000 < System.currentTimeMillis()) {
			//If no last known or last known is over a week old
			Criteria c = new Criteria();
			c.setAccuracy(Criteria.ACCURACY_FINE);
			final LocationListener gpsListener = new LocationListener() {

				@Override
				public void onLocationChanged(Location location) {
					// This will make sure the right location is reported, even if the Kiosk moves
					setGPSValue(ctx, location);
					Log.i(TAG, "location changed, reset preference");
				}

				@Override
				public void onProviderDisabled(String provider) { }

				@Override
				public void onProviderEnabled(String provider) { }

				@Override
				public void onStatusChanged(String provider, int status, Bundle extras) { }
		    	
		    };
		    //request a single update and set preference for localytics events using that data
		    locationManager.requestSingleUpdate(c, gpsListener, Looper.myLooper());
		    return;
		} else if(location != null) {
		//location retrieved, setting preference
		    setGPSValue(ctx, location);
			Log.v(TAG, "existing location found");
			return;
		} 
    }
}

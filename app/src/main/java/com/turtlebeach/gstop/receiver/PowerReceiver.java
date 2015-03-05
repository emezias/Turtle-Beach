
package com.turtlebeach.gstop.receiver;

import java.util.HashMap;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;

public class PowerReceiver extends BroadcastReceiver {
	//private static final String TAG = "PowerReceiver";

	@Override
	public void onReceive(Context ctx, Intent intent) {
		// This receiver will report an error to Turtle Beach through Localytics if the network is available
		String action = intent.getAction();
        //Send an event when unplugged or when plugged, otherwise skip
        if(action.equals(Intent.ACTION_POWER_CONNECTED) || action.equals(Intent.ACTION_POWER_DISCONNECTED)) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            int batteryPct = (level * 10) / scale;  // 0 - 9% = 0, 10 - 19% = 1 etc            
            
            HashMap<String, String> attrs = new HashMap<String, String>();
            
            attrs.put("power_level", String.valueOf(batteryPct));
            if (action.equals(Intent.ACTION_POWER_CONNECTED)) {
                attrs.put("charge_type", "plugged");
            } else {
                attrs.put("charge_type", "unplugged");
            }
        }
	}
}

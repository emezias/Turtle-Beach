package com.turtlebeach.gstop.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class PingReportReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context ctx, Intent arg1) {
		/*
		// This receiver will call the intent service to send a quick report into Localytics
		final Intent tnt = new Intent(ctx, LocalyticsErrorSvc.class);
        tnt.putExtra(LocalyticsErrorSvc.EVENT, LocalyticsErrorSvc.EVENT_PING);
    	ctx.startService(tnt);
    	*/
	}
}

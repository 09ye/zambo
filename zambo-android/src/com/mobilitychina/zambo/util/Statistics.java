package com.mobilitychina.zambo.util;

import com.google.analytics.tracking.android.EasyTracker;

import android.app.Activity;
import android.util.Log;



public class Statistics {
	public static void activityStart(Activity t) {
		EasyTracker.getInstance().activityStart(t);
	}
	
	public static void activityStop(Activity t) {
		
		EasyTracker.getInstance().activityStop(t);
	}
	
	
	
	public static void sendEvent(String category, String action, String label, long value) {
		EasyTracker.getTracker().sendEvent(category, action, label, value);
		Log.d("ga", category+"-"+action + "-"+label+"-"+String.valueOf(value));
	}
}

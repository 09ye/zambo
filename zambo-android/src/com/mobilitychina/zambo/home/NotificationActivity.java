package com.mobilitychina.zambo.home;

import android.app.Activity;
import android.os.Bundle;

import com.mobilitychina.zambo.util.SiemensStatusLogo;

public class NotificationActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SiemensStatusLogo.instance().unregisterNotification();
		finish();
	}
}

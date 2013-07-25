package com.mobilitychina.zambo.service.message;

import com.mobilitychina.util.Log;
import com.mobilitychina.zambo.checkin.CheckInOfflineManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class NetworkReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i("NetworkReceiver","Network state changed");
		if(isNetworkAvailable(context)){
//			CheckInManager.instance().uploadAll();
			Log.i("NetworkReceiver","Network state is available");
		}else{
//			Toast.makeText(context, "network disconnected!", Toast.LENGTH_SHORT).show();
		}
	}
	
	public static boolean isNetworkAvailable(Context context){
		ConnectivityManager mgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo[] infos = mgr.getAllNetworkInfo();
		if(infos != null){
			for(NetworkInfo info : infos){
				if(info.isConnected()){
					return true;
				}
			}
		}
		return false;
	}

}

package com.mobilitychina.zambo.service.location;

import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.mobilitychina.zambo.app.ZamboApplication;


public class LocationInfoManager implements LocationListener {
	public static final String INTENT_ACTION_LOCATIONCHANGED = "locationChanged";//地址变化广播
	public static final String INTENT_ACTION_LOCATIONSTATECHANGED = "locationStateChanged";//地址状态变化后广播

	private final static LocationInfoManager mInstance = new LocationInfoManager();
	private LocationState mState = LocationState.None;
	private String mLocationProvider = LocationManager.GPS_PROVIDER;
	private TimerOut mTime ;
	private TimerOutLocationInfo mTimeOutLocation;
	public static  final int START_HANDER = 1;
	private Handler hander = new Handler(){
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			
			case LocationInfoManager.START_HANDER :
				 LocationInfoManager.this._start();
				break;
			}
	}
	};
	public String getLocationPrivider(){
		return mLocationProvider;
	}
	private Calendar updatedate = Calendar.getInstance();
	private class TimerOutLocationInfo extends AsyncTask<Void,Void,Void>{

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			int timeout = 600;
			try {
				while (true){
					Thread.sleep(timeout*1000);//10分钟超时
					Calendar cal = Calendar.getInstance();
					cal.add(Calendar.SECOND, -timeout);
					if(cal.after(updatedate)){
						//LocationInfoManager.this.setState(LocationState.Fail);
						if(LocationInfoManager.this.getState() == LocationState.Done){
							LocationInfoManager.this.start();
						}
						
					}
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				
			}
			return null;
		}
	
	}
	private class TimerOut extends AsyncTask<Void,Void,Void>{

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			try {
				Thread.sleep(30000);
				if(LocationInfoManager.this.getState() != LocationState.Done){//定位未成功
					if(LocationInfoManager.this.mLocationProvider.equalsIgnoreCase(LocationManager.GPS_PROVIDER)){
						mLocationProvider = LocationManager.NETWORK_PROVIDER;
						start();
					}else{
						mLocationProvider = LocationManager.GPS_PROVIDER;
						LocationInfoManager.this.setState(LocationState.Fail);//失败了
					}
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				
			}
			return null;
		}
		
	}
	private void setState(LocationState state) {
		if (mState != state) {
			mState = state;
			Intent intent = new Intent();
			intent.setAction(INTENT_ACTION_LOCATIONSTATECHANGED);
			ZamboApplication.getInstance().getApplicationContext()
					.sendBroadcast(intent);
		}
	}

	public LocationState getState() {
		return mState;
	}

	public static LocationInfoManager getInstance() {
		return mInstance;
	}

	private Location mlocation = null;

	public Location getLocation() {
		return mlocation;
	}
	public void stop(){
		LocationManager locationManager;
		String contextService = Context.LOCATION_SERVICE;
		// 通过系统服务，取得LocationManager对象
		locationManager = (LocationManager) ZamboApplication.getInstance()
				.getApplicationContext().getSystemService(contextService);
		locationManager.removeUpdates(this);
		this.setState(LocationState.None);
	}
	public void start(){
		hander.sendEmptyMessage(START_HANDER);
	}
	public void _start() {
		this.setState(LocationState.Try);
		LocationManager locationManager;
		String contextService = Context.LOCATION_SERVICE;
		// 通过系统服务，取得LocationManager对象
		locationManager = (LocationManager) ZamboApplication.getInstance()
				.getApplicationContext().getSystemService(contextService);

		mTime = new TimerOut();
		mTime.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);//定时器
		if(mTimeOutLocation != null){
			mTimeOutLocation.cancel(true);
		}
		mTimeOutLocation = new TimerOutLocationInfo();
		mTimeOutLocation.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		locationManager.removeUpdates(this);
		if(locationManager.isProviderEnabled(mLocationProvider)){
			locationManager.requestLocationUpdates(mLocationProvider, 0, 0, this);
		}
		// 获得最后一次变化的位置
	}

	public LocationInfoManager() {
		//this.start();
		
	}

	@Override
	public void onLocationChanged(Location arg0) {
		// TODO Auto-generated method stub
		if (arg0 != null) {
			mlocation = arg0;
			updatedate = Calendar.getInstance();
			if(this.getState() != LocationState.Done){
				this.setState(LocationState.Done);
			}
			if(mTime != null){
				mTime.cancel(true);
				mTime = null;
			}
			Intent intent = new Intent();
			intent.setAction(INTENT_ACTION_LOCATIONCHANGED);
			ZamboApplication.getInstance().getApplicationContext()
					.sendBroadcast(intent);
		}else{
			int i = 0;
			i = 1;
			int  j = i;
			i = j;
		}
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

		//this.setState(LocationState.Fail);
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}
}

package com.mobilitychina.zambo.checkin;

import java.util.ArrayList;

import android.content.Context;

import com.mobilitychina.intf.Task;
import com.mobilitychina.localcontent.ContentManager;
import com.mobilitychina.zambo.app.ZamboApplication;
import com.mobilitychina.zambo.checkin.CheckInOfflineManager.CheckInMsg;
import com.mobilitychina.zambo.service.SoapService;

public class CheckInOfflineManager extends ContentManager<CheckInMsg> {

	public static class CheckInMsg extends ContentManager.Content {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		private String custId;
		private String datelineId;
		private String longitude;
		private String latitude;
		private String accuracy;
		private String datetime;
		private int checkInNumPerDay;

		public CheckInMsg(String custId, String datelineId, String longitude, String latitude, String accuracy,
				String datetime) {
			this.custId = custId;
			this.datelineId = datelineId;
			this.longitude = longitude;
			this.latitude = latitude;
			this.accuracy = accuracy;
			this.datetime = datetime;
		}

		public String custId() {
			return custId;
		}

		public String datelineId() {
			return datelineId;
		}

		public String longitude() {
			return longitude;
		}

		public String latitude() {
			return latitude;
		}

		public String accuracy() {
			return accuracy;
		}

		public String datetime() {
			return datetime;
		}
		
		public void setCheckInNumPerDay(int num){
			checkInNumPerDay = num;
		}
		
		public int checkInNumPerDay(){
			return checkInNumPerDay;
		}

		@Override
		public String toString() {
			return custId + ">" + datelineId + ">" + longitude + ">" + latitude + ">" + accuracy + ">" + datetime + ">"
					+ checkInNumPerDay;
		}
	}

	private static CheckInOfflineManager instance;

	public static void init() {
		instance();
	}

	public static CheckInOfflineManager instance() {
		if (instance == null) {
			instance = new CheckInOfflineManager(ZamboApplication.getInstance());
		}
		return instance;
	}

	private CheckInOfflineManager(Context context) {
		super(context);
	}

	@Override
	protected String ContentFileName() {
		return "checkinoffline";
	}

	@Override
	protected String logTag() {
		return CheckInOfflineManager.class.getSimpleName();
	}

	/**
	 * 返回未上传的msg
	 */
	@Override
	public CheckInMsg[] getContents() {
		ArrayList<CheckInMsg> list = new ArrayList<CheckInOfflineManager.CheckInMsg>();
		for (CheckInMsg msg : mUploadItems) {
			if (!msg.isUploaded()) {
				list.add(msg);
			}
		}
		return list.toArray(new CheckInMsg[0]);
	}

	@Override
	public Task createTask(CheckInMsg content) {
		return SoapService.getOfflineCheckInTask(content.custId(), content.datelineId(), content.longitude(),
				content.latitude(), content.accuracy(), content.datetime());
	}

}

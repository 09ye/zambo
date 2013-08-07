package com.mobilitychina.zambo.business.today;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ListFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.mobilitychina.intf.ITaskListener;
import com.mobilitychina.intf.Task;
import com.mobilitychina.log.McLogger;
import com.mobilitychina.net.HttpPostTask;
import com.mobilitychina.net.SoapTask;
import com.mobilitychina.util.Log;
import com.mobilitychina.util.NetObject;
import com.mobilitychina.zambo.R;
import com.mobilitychina.zambo.app.BaseActivity;
import com.mobilitychina.zambo.app.BaseTitlebar;
import com.mobilitychina.zambo.app.ZamboApplication;
import com.mobilitychina.zambo.business.customer.CustomerDetailActivity;
import com.mobilitychina.zambo.business.customer.data.CustomerInfo;
import com.mobilitychina.zambo.business.plan.data.GridViewTitleAdapter;
import com.mobilitychina.zambo.business.plan.data.PlanInfo;
import com.mobilitychina.zambo.business.record.FollowupDetailActivity;
import com.mobilitychina.zambo.checkin.CheckInDBHelper;
import com.mobilitychina.zambo.checkin.CheckInOfflineManager;
import com.mobilitychina.zambo.checkin.CheckInValidataionHelper;
import com.mobilitychina.zambo.service.CustomerInfoManager;
import com.mobilitychina.zambo.service.HttpPostService;
import com.mobilitychina.zambo.service.SoapService;
import com.mobilitychina.zambo.service.location.LocationInfoManager;
import com.mobilitychina.zambo.service.resps.RespFactory;
import com.mobilitychina.zambo.util.CommonUtil;
import com.mobilitychina.zambo.util.ConfigDefinition;
import com.mobilitychina.zambo.util.ConfigHelper;
import com.mobilitychina.zambo.util.MsLogType;
import com.mobilitychina.zambo.util.Statistics;
import com.mobilitychina.zambo.widget.PlanItem;
import com.mobilitychina.zambo.widget.ProgressBar;
import com.mobilitychina.zambo.widget.calendar.CalendarGridView;
import com.mobilitychina.zambo.widget.calendar.WeekGridViewAdapter;

public class TodayFragment extends ListFragment implements ITaskListener,
		OnTouchListener, BDLocationListener { 
	private HttpPostTask mTaskPlanlist;
	private HttpPostTask mTaskCheckIn;
	private HttpPostTask mTaskCheckInUpload;//上传新gps
	private List<PlanInfo> mPlanInfoList = new ArrayList<PlanInfo>();
	private MyAdapter mAdatper;
	private BaseTitlebar mTitleBar;
	private CalendarGridView mCalendarView;
	private WeekGridViewAdapter gWeekAdapter;
	private LinearLayout mLayoutCalendar;
	private Calendar calSelected = Calendar.getInstance();
	private final static int MESSAGE_GETLOCATION = 1;
	private final static int MESSAGE_GETLOCATION_CHECKIN= 2;
	private final static int MESSAGE_GETLOCATION_LOCATION= 3;
	private final static int MESSAGE_GETLOCATION_GPS_STATE= 4;
	private CheckInOfflineManager.CheckInMsg checkInMsg; // 保存签到失败的消息

	private Boolean mIsAbortNeedCheckIn = true;
	private PlanInfo checkInPlanInfo = null;
	private LocationClient mLocationClient;
	private boolean  checkinSuccessed = false;

	private Handler handler = new Handler() {

		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case MESSAGE_GETLOCATION_CHECKIN:
				if (!mIsAbortNeedCheckIn) {
					mIsAbortNeedCheckIn = true;
					((BaseActivity) (TodayFragment.this.getActivity())).dismissDialog();
					CheckIn((Map<String, String>) msg.obj);
				}

				break;
			case MESSAGE_GETLOCATION:
				checkInPlanInfo = (PlanInfo) msg.obj;

				mIsAbortNeedCheckIn = false;
				((BaseActivity) (TodayFragment.this.getActivity()))
				.showProgressDialog(
						"正在定位...","取消", 0,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								dialog.dismiss();
								mIsAbortNeedCheckIn = true;
							    Statistics.sendEvent("visit", "checkin", "cancel", (long) 0);                
							}
						}, false);
		
				Message msg2 = new Message();
				msg2.what = MESSAGE_GETLOCATION_LOCATION;
		//		mLocationPrivider = LocationManager.GPS_PROVIDER;
				handler.sendMessage(msg2);
				break;
			case MESSAGE_GETLOCATION_LOCATION: {
				((BaseActivity) (TodayFragment.this.getActivity())).editDialog("正在定位");
					TodayFragment.this.getLocation();
				}
				break;
			case MESSAGE_GETLOCATION_GPS_STATE:
			{
				break;
			}
			}
		}
	};
	private void getLocation()
	{
		mLocationClient.requestLocation();
	}
	private void CheckIn( Map<String, String> location){
		final String latitude = location.get("latitude");
		final String longitude = location.get("longitude");
		final String accuracy  =  location.get("accuracy");
		
		String datelineId;
		String custId;
		datelineId = String.valueOf(checkInPlanInfo.getId());
		custId = String.valueOf(checkInPlanInfo.getCustDetailId());
		
		String locationtype = location.get("type");
		String locationReason= "";
//		61 ： GPS定位结果
//		62 ： 扫描整合定位依据失败。此时定位结果无效。
//		63 ： 网络异常，没有成功向服务器发起请求。此时定位结果无效。
//		65 ： 定位缓存的结果。
//		66 ： 离线定位结果。通过requestOfflineLocaiton调用时对应的返回结果
//		67 ： 离线定位失败。通过requestOfflineLocaiton调用时对应的返回结果
//		68 ： 网络连接失败时，查找本地离线定位时对应的返回结果
//		161： 表示网络定位结果
//		162~167： 服务端定位失败。
		if(locationtype.equalsIgnoreCase("61"))
		{
			locationReason = "come from GPS";
			
		}else if(locationtype.equalsIgnoreCase("62"))
		{
			locationReason = "扫描整合定位依据失败。此时定位结果无效"; 
		}else if(locationtype.equalsIgnoreCase("63"))
		{
			locationReason = "网络异常，没有成功向服务器发起请求。此时定位结果无效"; 
		}else if(locationtype.equalsIgnoreCase("65"))
		{
			locationReason = "定位缓存的结果"; 
		}else if(locationtype.equalsIgnoreCase("161"))
		{
			locationReason = "come from network "; 
		}else if(locationtype.equalsIgnoreCase("162")||locationtype.equalsIgnoreCase("163")||locationtype.equalsIgnoreCase("164")||locationtype.equalsIgnoreCase("164")||locationtype.equalsIgnoreCase("165")||locationtype.equalsIgnoreCase("166")||locationtype.equalsIgnoreCase("167"))
		{
			locationReason = "Baidu SDK inner ERROR<"+ locationtype+">"; 
		}
//	    Statistics.sendEvent("visit", "checkin", "notsuccessful", (long) 1);  
	    Statistics.sendEvent("visit", "checkin", "locationtype"+locationtype, (long) 0); 
	    McLogger.getInstance().addLog(MsLogType.TYPE_SYS,MsLogType.ACT_LOCATION,"定位结果:"+ locationtype+";CID:"+checkInPlanInfo.getId());
	    McLogger.getInstance().addLog(MsLogType.TYPE_SYS,MsLogType.ACT_LOCATION,"CID:"+checkInPlanInfo.getId()+";CID:"+checkInPlanInfo.getId());

	    McLogger.getInstance().addLog(MsLogType.TYPE_SYS,MsLogType.ACT_LOCATION,"lat:"+latitude+";lng:"+longitude+";CID:"+checkInPlanInfo.getId());

		checkInMsg = new CheckInOfflineManager.CheckInMsg(custId, datelineId, longitude, latitude, accuracy,
				CommonUtil.getCurrentTime("yyyy-MM-dd HH:mm:ss"));
		if(!CheckInValidataionHelper.validate(getActivity(),checkInMsg)){
			return;
		}
		try {
			
			McLogger.getInstance().addLog(MsLogType.TYPE_EVT,MsLogType.ACT_EVT_OTHER,CommonUtil.getNetWorkInfo(this.getActivity()));
			McLogger.getInstance().addLog(MsLogType.TYPE_EVT,MsLogType.ACT_EVT_OTHER,"CellInfo:"+CommonUtil.getCellInfo(this.getActivity()));
			McLogger.getInstance().addLog(MsLogType.TYPE_EVT,MsLogType.ACT_EVT_OTHER,"CellNearbyInfo:"+CommonUtil.getNearbyCellInfo(this.getActivity()));
			WifiManager wifiManager = (WifiManager) this.getActivity().getSystemService(this.getActivity().WIFI_SERVICE);
			WifiInfo wifiInfo = wifiManager.getConnectionInfo();
			McLogger.getInstance().addLog(MsLogType.TYPE_EVT,MsLogType.ACT_EVT_OTHER,"WifiInfo:"+wifiInfo.toString());
			StringBuilder sb = new StringBuilder();
			BDLocation tempLocation = mLocationClient.getLastKnownLocation();
			if(tempLocation!=null){
				sb.append("Location:"+tempLocation.getLatitude()+","+tempLocation.getLongitude());
				sb.append("SatelliteNumber:"+tempLocation.getSatelliteNumber());
				McLogger.getInstance().addLog(MsLogType.TYPE_EVT,MsLogType.ACT_EVT_OTHER,sb.toString());
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		if(latitude.equalsIgnoreCase("1")){
			Builder builder = new Builder(
					TodayFragment.this.getActivity());
			builder.setTitle("提示");
			builder.setMessage("获取您当前的位置信息失败, 请您稍后再试");
			builder.setNegativeButton("确定", null);
			builder.show();
		    Statistics.sendEvent("visit", "checkin", "fail", (long) 0);
		    McLogger.getInstance().addLog(MsLogType.TYPE_SYS,MsLogType.ACT_LOCATION,"定位失败"+";CID:"+checkInPlanInfo.getId());

		}else{
			CustomerInfo info = CustomerInfoManager.getInstance().getCustomerById(String.valueOf(checkInPlanInfo.getCustDetailId()));
			double distance = 0;
			try{
					distance = this.getDistance(info.getLatitude(),
					info.getLongitude(), Double.parseDouble(latitude),
					Double.parseDouble(longitude));
			}catch (Exception e) {
				// TODO: handle exception
				
				Builder builder = new Builder(
						TodayFragment.this.getActivity());
				builder.setTitle("提示");

				builder.setMessage("获取您当前的位置信息失败, 请您稍后再试");

				builder.setNegativeButton("取消",null);
				builder.show();
				McLogger.getInstance().addLog(MsLogType.TYPE_SYS,MsLogType.ACT_LOCATION,"POI经纬度错误"+";CID:"+checkInPlanInfo.getId(),true);
				return ;
			}
			
			if ((distance - Double.parseDouble(accuracy)) <= ConfigHelper
					.getInstance().getMaxDistance()) {
					//正常签到
						CheckIn(latitude, longitude,accuracy);
					      Statistics.sendEvent("visit", "checkin", "successful", (long) 1);   
					      McLogger.getInstance().addLog(MsLogType.TYPE_SYS,MsLogType.ACT_LOCATION,"定位成功:正常签到"+";CID:"+checkInPlanInfo.getId());
			} else {
						
				if (!info.getIsLocked()) {//允许更新gps
					Builder builder = new Builder(
							TodayFragment.this.getActivity());
					McLogger.getInstance().addLog(MsLogType.TYPE_SYS,MsLogType.ACT_LOCATION,"定位成功:距离超出,弹出询问"+";CID:"+checkInPlanInfo.getId());
					builder.setTitle("提示");
					builder.setMessage("您当前的位置信息显示您离客户距离较远。\n如果您确已经到达客户所在地，可上传目前的GPS信息以供人工审核，或者稍后再试"+ (CommonUtil.isMainServer(this.getActivity()) == 0? "":"\n<目标:("+String.valueOf(info.getLatitude())+","+String.valueOf(info.getLongitude())+")\n定位:("+latitude+","+longitude+")\n精度:"+accuracy+"\n相距大约:"+(int)distance/1000+"km>\n"+locationReason));
					builder.setNegativeButton("取消", null);
					builder.setPositiveButton("上传",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									McLogger.getInstance().addLog(MsLogType.TYPE_USER,MsLogType.ACT_CHECKIN,"选择更新的POI的GPS"+";CID:"+checkInPlanInfo.getId());
									CheckIn(latitude, longitude,accuracy);
//									CheckInAndUpdateLocation(latitude, longitude,accuracy);
									Statistics.sendEvent("visit", "checkin", "uploadgps", (long) 0);  
								}
							});
					builder.show();
				} else {//不可更新gps
					McLogger.getInstance().addLog(MsLogType.TYPE_SYS,MsLogType.ACT_CHECKIN,"不可更新POI的GPS");
					
					Builder builder = new Builder(
							TodayFragment.this.getActivity());
					builder.setTitle("提示");
					builder.setMessage("系统显示您当前的位置与客户距离超出允许范围，请检查您的位置或稍后再试。"+ (CommonUtil.isMainServer(this.getActivity()) == 0? "":"\n<目标:("+String.valueOf(info.getLatitude())+","+String.valueOf(info.getLongitude())+")\n定位:("+latitude+","+longitude+")\n精度:"+accuracy+"\n相距大约:"+(int)distance/1000+"km>\n"+locationReason));
					builder.setNegativeButton("取消",null);
					builder.show();
					Statistics.sendEvent("visit", "checkin", "faraway", (long) 0);  
				}
			}
		}
		
		
	}
	
	private void CheckIn(String latitude, String longitude, String accuracy) {
		String datelineId;
		String custId;
		datelineId = String.valueOf(checkInPlanInfo.getId());
		custId = String.valueOf(checkInPlanInfo.getCustDetailId());
		checkinSuccessed = false;
		checkInMsg = new CheckInOfflineManager.CheckInMsg(custId, datelineId, longitude, latitude, accuracy,
				CommonUtil.getCurrentTime("yyyy-MM-dd HH:mm:ss"));
		mTaskCheckIn = new HttpPostTask(ZamboApplication.getInstance().getApplicationContext());
		mTaskCheckIn.setUrl(HttpPostService.SOAP_URL+"create_visit_sign_in" );
		mTaskCheckIn.getTaskArgs().put("emp_id", 3);
		mTaskCheckIn.getTaskArgs().put("visit_plan_id", datelineId);
		mTaskCheckIn.getTaskArgs().put("latitude",latitude);
		mTaskCheckIn.getTaskArgs().put("longitude",longitude);
		mTaskCheckIn.getTaskArgs().put("accuracy",accuracy);
		mTaskCheckIn.getTaskArgs().put("client_time","'"+CommonUtil.getCurrentTime("yyyy-MM-dd HH:mm:ss")+"'");
		mTaskCheckIn.setListener(TodayFragment.this);
		mTaskCheckIn.start();
		((BaseActivity) getActivity()).showProgressDialog("正在签到...");
	}
	/**
	 * 
	 * @param latitude
	 * @param longitude
	 * @param accuracy
	 */
	private void CheckInAndUpdateLocation(String latitude, String longitude, String accuracy) {
		String datelineId;
		String custId;
		datelineId = String.valueOf(checkInPlanInfo.getId());
		custId = String.valueOf(checkInPlanInfo.getCustDetailId());
		checkinSuccessed = false;
		checkInMsg = new CheckInOfflineManager.CheckInMsg(custId, datelineId, longitude, latitude, accuracy,
				CommonUtil.getCurrentTime("yyyy-MM-dd HH:mm:ss"));
//		mTaskCheckInUpload = SoapService.getCheckInAndUpdateLocationTask(custId, datelineId, longitude, latitude, accuracy);
		mTaskCheckInUpload.setListener(TodayFragment.this);
		mTaskCheckInUpload.start();
		((BaseActivity) getActivity()).showProgressDialog("正在签到...");
	}
	
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equalsIgnoreCase(
					ConfigDefinition.INTENT_ACTION_ADDPLANCOMPLETE)
					|| intent.getAction().equalsIgnoreCase(
							ConfigDefinition.INTENT_ACTION_SUBMITFLUP)
					|| intent.getAction().equalsIgnoreCase(
							ConfigDefinition.INTENT_ACTION_SUBMITITEM)
					|| intent.getAction().equalsIgnoreCase(
							ConfigDefinition.INTENT_ACTION_DELETEPLANCOMPLETE)) {
				initPlanList();
			}
		}
	};
	
	public double getDistance(double lat1, double lon1, double lat2, double lon2) {  
	    float[] results = new float[3];  
	    Location.distanceBetween(lat1 , lon1 , lat2 , lon2, results);  
	    //AggLog.d(TAG, "haidistance lat1=" + lat1 + " lon1=" + lon1 + " lat2=" + lat2 + " lon2=" + lon2 + " dis=" + results[0]);  
	    return results[0];  
	}  
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mReceiver != null) {
			this.getActivity().unregisterReceiver(mReceiver);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater
				.inflate(R.layout.fragment_active, container, false);
		mTitleBar = (BaseTitlebar) view.findViewById(R.id.title_bar);
		mTitleBar.setTitle("我的拜访");
		mLayoutCalendar = (LinearLayout) view.findViewById(R.id.layoutCalendar);
		mLayoutCalendar.addView(this.getWeekTitleView(), new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		super.onActivityCreated(savedInstanceState);
		this.onCreateCalendarView();
		IntentFilter intentFilter = new IntentFilter(
				ConfigDefinition.INTENT_ACTION_ADDPLANCOMPLETE);
		intentFilter.addAction(ConfigDefinition.INTENT_ACTION_DELETEPLANCOMPLETE);
		intentFilter.addAction(ConfigDefinition.INTENT_ACTION_SUBMITFLUP);
		intentFilter.addAction(ConfigDefinition.INTENT_ACTION_SUBMITITEM);
		intentFilter.addAction(LocationInfoManager.INTENT_ACTION_LOCATIONSTATECHANGED);
		this.getActivity().registerReceiver(mReceiver, intentFilter);
		mLocationClient = new LocationClient(this.getActivity());
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);				//打开gps
		option.setCoorType("bd09ll");		//设置坐标类型
		option.setPriority(LocationClientOption.GpsFirst);     
		option.setPoiExtraInfo(false);	
		option.setProdName("sheely"); 
		option.setScanSpan(5000);
		option.disableCache(true);
		mLocationClient.setLocOption(option);
		mLocationClient.registerLocationListener(this);
		this.initPlanList();
	}

	private void onCreateCalendarView() {

		Calendar calStartDate = Calendar.getInstance();
		mCalendarView = new CalendarGridView(getActivity());
		mCalendarView.setOnTouchListener(this);
		gWeekAdapter = new WeekGridViewAdapter(this.getActivity(),
				calStartDate, null);
		mCalendarView.setAdapter(gWeekAdapter);
		mLayoutCalendar.addView(mCalendarView, new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		//this.initPlanList();
	}
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
//		this.initPlanList();
		mLocationClient.start();
		mLocationClient.requestLocation();
	}
	
	private boolean isFirstRequestPlanList = true;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(savedInstanceState != null){
			isFirstRequestPlanList = savedInstanceState.getBoolean("isFirstRequestPlanList");
		}
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean("isFirstRequestPlanList", isFirstRequestPlanList);
	}
	
	public void onPause()
	{
		super.onPause();
		mLocationClient.stop();
		mIsAbortNeedCheckIn = true;
	}
	// 初始化计划
	private void initPlanList() {
		if(isFirstRequestPlanList){
			((BaseActivity) this.getActivity()).showProgressDialog("正在获取任务列表...",false);
		}else{
			((BaseActivity) this.getActivity()).showProgressDialog("正在更新任务列表...",false);
		}
		if(mTaskPlanlist != null){
			mTaskPlanlist.setListener(null);
		}
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		mTaskPlanlist = new HttpPostTask(ZamboApplication.getInstance().getApplicationContext());
		mTaskPlanlist.setUrl(HttpPostService.SOAP_URL+"get_visit_plan__list" );
		mTaskPlanlist.getTaskArgs().put("emp_id", 3);
		mTaskPlanlist.getTaskArgs().put("plan_status", "A");
		mTaskPlanlist.getTaskArgs().put("plan_start_date", df.format(gWeekAdapter.getCalendarFirstDate()));
		mTaskPlanlist.getTaskArgs().put("plan_end_date", df.format(gWeekAdapter.getCalendarLastDate()));
		mTaskPlanlist.setListener(this);
		mTaskPlanlist.setMaxTryCount(5);
		mTaskPlanlist.start();
		McLogger.getInstance().addLog(MsLogType.TYPE_SYS,MsLogType.ACT_VISITE,"获取列表");
	}

	@Override
	public void onTaskFailed(Task task) {
		((BaseActivity) this.getActivity()).dismissDialog();
		if (task == mTaskPlanlist) {
			if(isFirstRequestPlanList){
				((BaseActivity) this.getActivity()).showDialog("提示", "获取任务列表失败，请稍后再试..", null);
			}else{
				((BaseActivity) this.getActivity()).showDialog("提示", "更新任务列表失败，请稍后再试..", null);
			}
			mTaskPlanlist = null;
			isFirstRequestPlanList = false;
		} else if (task == mTaskCheckIn) {
			((BaseActivity) this.getActivity()).showDialog("提示", "提交［签到/上传］信息失败,请确保您的网络通畅并稍侯再试..", null);
			mTaskCheckIn = null;
//			CheckInManager.instance().addContent(checkInMsg);
//			CheckInDBHelper.instance().insertOrUpdate(checkInMsg);
//			checkInMsg = null;
//			((BaseActivity) this.getActivity()).showDialog("提示", "因为系统或网络延迟等原因，提交的签到请求尚未发送成功。" +
//					"系统会在网络连接通畅时自动重新发送签到。", null);
		}
	}

	@Override
	public void onTaskFinished(Task task) {
		// TODO Auto-generated method stub
		((BaseActivity) this.getActivity()).dismissDialog();

		if (task == mTaskPlanlist) {
			NetObject result = ((HttpPostTask)task).getResult();
			List<NetObject> listNet = result.listForKey("data");
			Log.i("HttpPostTask",task.getResult().toString());
			if(mPlanInfoList!=null){
				mPlanInfoList.clear();
			}
			for (NetObject netObject : listNet) {
				PlanInfo plan = new PlanInfo();
//			    CustomerInfo CustomerInfo = CustomerInfoManager.getInstance().getCustomerById(custId);
				plan.setId(Integer.valueOf((netObject.stringForKey("id"))));
			    plan.setCustDetailId((Integer) netObject.arrayForKey("cust_id").get(0));
			    plan.setCustName((String) netObject.arrayForKey("cust_id").get(1));
			    plan.setModify_date(netObject.stringForKey("plan_visit_date"));
			    plan.setPlanStatus(netObject.stringForKey("plan_type"));
			    plan.setVisited("Y");
				mPlanInfoList.add(plan);
			}
			McLogger.getInstance().addLog(MsLogType.TYPE_SYS,MsLogType.ACT_VISITE,"获取列表成功");
			
//			mPlanInfoList = RespFactory.getService().fromResp(PlanInfo.class,
//					task.getResult());
			if (mPlanInfoList == null) {
				return;
			}
			if (mAdatper == null) {
				mAdatper = new MyAdapter(this.getActivity());
			}
			List<String> list = new ArrayList<String>();
			Calendar calendar = Calendar.getInstance();
			int month = calendar.get(Calendar.MONTH)+1;
			String m = String.valueOf(calendar.get(Calendar.MONTH)+1);
			if(month<10){
				StringBuffer sb = new StringBuffer(m);
				sb.insert(0, "0");
				m = sb.toString();
			}
			if (mPlanInfoList != null) {
				for (PlanInfo info : mPlanInfoList) {
					if (!list.contains(info.getModify_date())) {
						list.add(info.getModify_date());
					}
					if(info.getModify_date().split("-")[1].startsWith(m)){
						CustomerInfo cs  = CustomerInfoManager.getInstance().getCustomerById(String.valueOf(info.getCustDetailId()));
						if(cs != null){
							cs.setPlanVisitNum(info.getVisitNum());
							cs.setVisitedNum(info.getVisitedNum());
						}
					}
				}
			}
			gWeekAdapter.setListMark(list);
			gWeekAdapter.notifyDataSetChanged();
			mAdatper.mListPlanInfo = mPlanInfoList;
			mAdatper.mListPlanInfo = PlanInfo.getListByData(mPlanInfoList,
					calSelected.getTime());
			this.setListAdapter(mAdatper);

			mTaskPlanlist = null;
			isFirstRequestPlanList = false;
		} else if (task == mTaskCheckIn || task == mTaskCheckInUpload) {
			try{
				NetObject result = ((HttpPostTask)task).getResult();
				Log.i("HttpPostTask",task.getResult().toString());
//				final int siginId = result.jsonObjectForKey("data").getInt("id");
				String retMsg = result.stringForKey("message").replaceAll("[^\u4E00-\u9FA5]", "");;
				int retCode = Integer.valueOf(result.stringForKey("code"));
//				if(retCode == 1){
//					retMsg = "您当前使用的版本过低，请升级到最新版本.";
//				}else if(retCode == 2){
//					retMsg = "现在距您上次签到时间较短，系统暂不允许您进行签到，请稍后再试.";
//				}else if(retCode == 3){
//					retMsg = "今天尚有计划未跟进，请先完成跟进后再做签到！";
//				}else if(retCode == 4){
//					retMsg = "客户关联已取消.";
				if(retCode==0){
					checkinSuccessed = true;
					Builder builder = new Builder(
							TodayFragment.this.getActivity());
					builder.setTitle("提示");
					if(task == mTaskCheckInUpload){
						retMsg= "上传成功，请在今天完成本次拜访的“跟进”操作";
					}
					if(task == mTaskCheckIn){
						retMsg= "签到成功，请在今天完成本次拜访的“跟进”操作";
					}
				}
				if(!TextUtils.isEmpty(retMsg)){
					((BaseActivity) this.getActivity()).showDialog("提示",retMsg, new DialogInterface.OnClickListener(){
						public void onClick(DialogInterface dialog, int which) 
						{
							if(checkinSuccessed){
							int	custId = checkInPlanInfo.getCustDetailId();
								for (PlanInfo planInfo : mPlanInfoList) {
									if (planInfo.getCustDetailId()==custId) {
										planInfo.setVisited("Y");
									}
								}
								mAdatper.notifyDataSetChanged();
//								TodayFragment.this.initPlanList();
								CheckInDBHelper.instance().insertOrUpdate(checkInMsg);
							}
						};
						}
					);
				}
				McLogger.getInstance().addLog(MsLogType.TYPE_SYS,MsLogType.ACT_VISITE,retMsg);

			}catch(Exception e){
				try{
					if("true".equalsIgnoreCase(task.getResult().toString())){
						checkinSuccessed = true;
					}
				}catch(Exception ex){
				}
			}finally{
				
			}
			
			mTaskCheckIn = null;
			checkInMsg = null;
		}

	}

	@Override
	public void onTaskUpdateProgress(Task arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	private View getWeekTitleView() {
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		GridView gridView = new GridView(this.getActivity());
		gridView.setLayoutParams(params);
		gridView.setNumColumns(7);// 设置每行列数
		gridView.setGravity(Gravity.CENTER_VERTICAL);// 位置居中
		gridView.setVerticalSpacing(1);// 垂直间隔
		gridView.setHorizontalSpacing(1);// 水平间隔
		gridView.setBackgroundColor(getResources().getColor(R.color.white));

		WindowManager windowManager = this.getActivity().getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		int i = display.getWidth() / 7;
		int j = display.getWidth() - (i * 7);
		int x = j / 2;
		gridView.setPadding(x, 0, 0, 0);// 居中
		gridView.setLayoutParams(new LinearLayout.LayoutParams(
				android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		gridView.setVerticalSpacing(0);// 垂直间隔
		gridView.setHorizontalSpacing(0);// 水平间隔
		GridViewTitleAdapter titleAdapter = new GridViewTitleAdapter(
				this.getActivity());
		gridView.setAdapter(titleAdapter);// 设置菜单Adapter

		return gridView;
	}

	private void itemOnClick(int position) {
		final PlanInfo planInfo = (PlanInfo) mAdatper.mListPlanInfo
				.get(position);
		final Long info = Long.valueOf(planInfo.getCustDetailId());

		if (0l != info) {

			new AlertDialog.Builder(this.getActivity())
					.setTitle("提示")
					.setMessage("请选择一种操作")
					.setPositiveButton(
							"随访备注",
							new android.content.DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									Statistics.sendEvent("visit", "followup", "", (long) 0);
									Intent intent = new Intent(
											TodayFragment.this.getActivity(),
											FollowupDetailActivity.class);
									intent.putExtra(
											FollowupDetailActivity.INTENT_STRING_PLAN_ID,
											planInfo.getId());
									intent.putExtra(
											FollowupDetailActivity.INTENT_INT_CUSTOMER_ID,
											String.valueOf(planInfo
													.getCustDetailId()));
									intent.putExtra(
											FollowupDetailActivity.INTENT_BOOL_EDIT,
											true);

									TodayFragment.this.getActivity()
											.startActivity(intent);
									McLogger.getInstance().addLog(MsLogType.TYPE_SYS,MsLogType.ACT_CHECKIN,"点击随访");
								}
							})

					.setNeutralButton(
							"项目跟进",
							new android.content.DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									Statistics.sendEvent("visit", "project", "", (long) 0);
									Intent intent = new Intent(
											TodayFragment.this.getActivity(),
											CustomerDetailActivity.class);
									intent.putExtra("customerId",
											String.valueOf(planInfo
													.getCustDetailId()));
									intent.putExtra("planId",
											String.valueOf(planInfo.getId()));
									startActivity(intent);
									McLogger.getInstance().addLog(MsLogType.TYPE_SYS,MsLogType.ACT_CHECKIN,"点击备注");
								}
							}).setNegativeButton("取消", null).show();
		}
	}

	class MyAdapter extends BaseAdapter {
		// private List<PlanInfo> data;
		private LayoutInflater layoutInflater;
		private List<PlanInfo> mListPlanInfo = new ArrayList<PlanInfo>();

		public MyAdapter(Context context) {
			this.layoutInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return mListPlanInfo.size();
		}

		/**
		 * 获取某一位置的数据
		 */
		@Override
		public PlanInfo getItem(int position) {

			return mListPlanInfo.get(position);

		}

		/**
		 * 获取唯一标识
		 */
		@Override
		public long getItemId(int position) {
			return position;
		}

		/**
		 * android绘制每一列的时候，都会调用这个方法
		 */
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (position < mListPlanInfo.size()) {
				return this.getViewForPlanList(position, convertView, parent);
			}
			return convertView;

		}

		public View getViewForPlanList(int position, View convertView,
				ViewGroup parent) {
			PlanItem planitem = null;
			final int index = position;
			if (convertView == null) {
				planitem = new PlanItem();
				// 获取组件布局
				convertView = layoutInflater.inflate(R.layout.item_plan, null);
				// planitem.txtSection = (TextView) convertView
				// .findViewById(R.id.txtSection);
				planitem.mProgressbar = (ProgressBar) convertView
						.findViewById(R.id.progressVisitNum);
				planitem.titleView = (TextView) convertView
						.findViewById(R.id.custName);
				planitem.visitedNumView = (TextView) convertView
						.findViewById(R.id.visited_num);
				planitem.modifyDateView = (TextView) convertView
						.findViewById(R.id.modify_date);
				// planitem.visitNumView = (TextView) convertView
				// .findViewById(R.id.visitNum);
				planitem.locationView = (Button) convertView
						.findViewById(R.id.location);
				// 这里要注意，是使用的tag来存储数据的。
				convertView.setTag(planitem);
			} else {
				planitem = (PlanItem) convertView.getTag();
			}
			if (this.getItem(position).getVisitNum() > 0) {
				planitem.mProgressbar.setProgress((float) this
						.getItem(position).getVisitedNum()
						/ this.getItem(position).getVisitNum());
			}
			// if (position == 0) {
			// planitem.txtSection.setVisibility(View.VISIBLE);
			// planitem.txtSection.setText("今日拜访计划");
			// } else {
			// planitem.txtSection.setVisibility(View.GONE);
			// }

			// 绑定数据、以及事件触发
			if (mListPlanInfo.get(position).getCustDetailId() != 0) {
				// 绑定数据、以及事件触发
				if ("T".equals(mListPlanInfo.get(position).getPlanStatus())) {
					planitem.titleView
							.setText(position + 1 + "-" + mListPlanInfo.size()
									+ " "
									+ mListPlanInfo.get(position).getCustName()
									+ "(临)");
				} else {
					planitem.titleView.setText(position + 1 + "-"
							+ mListPlanInfo.size() + " "
							+ mListPlanInfo.get(position).getCustName());
				}
				// planitem.visitNumView.setText(/* (int) */"当月拜访数：" +
				// data.get(position).getVisitNum());
				planitem.visitedNumView.setText(mListPlanInfo.get(position)
						.getVisitedNum()
						+ "/"
						+ mListPlanInfo.get(position).getVisitNum());
				if (null != mListPlanInfo.get(position).getVisitDate()
						&& !"".equals(mListPlanInfo.get(position)
								.getVisitDate())) {
					planitem.modifyDateView.setText("上次拜访时间："
							+ mListPlanInfo.get(position).getVisitDate()
									.substring(0, 10));
				} else {
					planitem.modifyDateView.setText("");
				}
				planitem.locationView.setTextSize(16);
				planitem.locationView.setOnClickListener(null);
				if (!"Y".equals(mListPlanInfo.get(position).getVisited())) {
					planitem.locationView.setText(R.string.qiandao);
					planitem.locationView.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View v) {
									Builder builder = new Builder(
											TodayFragment.this.getActivity());
									builder.setTitle("提示");
									builder.setMessage("请尽量在室外完成签到,提高获取您位置信息的准确性. 确认现在签到吗？");
									builder.setPositiveButton(
											"确定",
											new DialogInterface.OnClickListener() {

												@Override
												public void onClick(
														DialogInterface dialog,
														int which) {
													
													LocationManager locationManager = (LocationManager)TodayFragment.this.getActivity().
															getSystemService(Context.LOCATION_SERVICE);
													if( !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
														((BaseActivity)TodayFragment.this.getActivity()).showDialog("提示", "\"Google的位置服务\"或\"GPS卫星\"功能未开启，是否前往设置？","取消","设置", new DialogInterface.OnClickListener(){
															public  void onClick(android.content.DialogInterface arg0, int arg1){
																if(arg1 == DialogInterface.BUTTON_NEGATIVE){
																     Intent intent = new Intent();
																        intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
																        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
																 
																      TodayFragment.this.startActivity(intent);
																      Statistics.sendEvent("visit", "checkin", "setting", (long) 0);                
																}
															}
														});
													} else {
														McLogger.getInstance()
																.addLog(MsLogType.TYPE_USER,
																		MsLogType.ACT_CHECKIN,
																		"点击确认");
														PlanInfo info = mListPlanInfo
																.get(index);
														String datelineId = String.valueOf(info
																.getId());
														String custId = String.valueOf(info
																.getCustDetailId());
														checkInMsg = new CheckInOfflineManager.CheckInMsg(
																custId,
																datelineId,
																"0",
																"0",
																"0",
																CommonUtil
																		.getCurrentTime("yyyy-MM-dd HH:mm:ss"));
														if (!CheckInValidataionHelper
																.validate(
																		getActivity(),
																		checkInMsg)) {
															Statistics
																	.sendEvent(
																			"visit",
																			"checkin",
																			"签到间隔审查未通过",
																			(long) 0);
															McLogger.getInstance()
																	.addLog(MsLogType.TYPE_SYS,
																			MsLogType.ACT_CHECKIN,
																			"签到间隔审查未通过");
															return;
														}

														if (!CheckInValidataionHelper
																.validateCheckInAndReport(TodayFragment.this.mPlanInfoList)) {
															((BaseActivity) TodayFragment.this
																	.getActivity())
																	.showDialog(
																			"提示",
																			"今天尚有计划未跟进，请先完成跟进后再做签到",
																			"确定",
																			null,
																			null);
															Statistics
																	.sendEvent(
																			"visit",
																			"checkin",
																			"未跟进",
																			(long) 0);
															McLogger.getInstance()
																	.addLog(MsLogType.TYPE_SYS,
																			MsLogType.ACT_CHECKIN,
																			"计划跟进审查未通过");
															return;
														}
														McLogger.getInstance()
																.addLog(MsLogType.TYPE_SYS,
																		MsLogType.ACT_CHECKIN,
																		"开始定位");
														Message msg = new Message();
														msg.what = MESSAGE_GETLOCATION;
														msg.obj = info;
														handler.sendMessage(msg);

													}
												}
											});
									builder.setNegativeButton(
											"取消",
											new DialogInterface.OnClickListener() {

												@Override
												public void onClick(
														DialogInterface dialog,
														int which) {
													dialog.cancel();
													McLogger.getInstance().addLog(MsLogType.TYPE_USER,MsLogType.ACT_CHECKIN,"点击取消");
												}
											});
									builder.show();
								}
							});
					planitem.locationView.setVisibility(View.VISIBLE);
				} else {
					planitem.locationView.setText(R.string.follow_up);
					planitem.locationView
							.setOnClickListener(new View.OnClickListener() {

								@Override
								public void onClick(View v) {
									// TODO Auto-generated method stub
									itemOnClick(index);
								}
							});
				}

			} else {
				planitem.titleView.setText(mListPlanInfo.get(position)
						.getCustName());
				// planitem.visitNumView.setText("");
				planitem.visitedNumView.setText("");
				planitem.modifyDateView.setText("");
				planitem.locationView.setVisibility(View.INVISIBLE);
			}
			return convertView;
		}
	}

	

	@Override
	public boolean onTouch(View v, MotionEvent e) {
		// TODO Auto-generated method stub
		int pos = mCalendarView.pointToPosition((int) e.getX(), (int) e.getY());
		LinearLayout txtDay = (LinearLayout) mCalendarView
				.findViewById(pos + 5000);
		if (txtDay != null) {
			if (txtDay.getTag() != null) {
				Date date = (Date) txtDay.getTag();

				calSelected.setTime(date);

				gWeekAdapter.setSelectedDate(calSelected);
				gWeekAdapter.notifyDataSetChanged();
				if(mAdatper != null){
					mAdatper.mListPlanInfo = PlanInfo.getListByData(mPlanInfoList,
							date);
					mAdatper.notifyDataSetChanged();
				}
				
			}

		}
		return true;
	}

	@Override
	public void onReceiveLocation(BDLocation arg0) {
		// TODO Auto-generated method stub
		if(mIsAbortNeedCheckIn){
			return;
		}

		HashMap<String, String> map = new HashMap<String, String>();
		if (arg0 != null && arg0.getLatitude() != 0
				&& arg0.getLongitude() != 0 && (arg0.getLocType() == 61 || arg0.getLocType() == 161 ||  arg0.getLocType() == 65)) {
			map.put("latitude", String.valueOf(arg0.getLatitude()));
			map.put("longitude", String.valueOf(arg0.getLongitude()));
			map.put("accuracy", String.valueOf(arg0.getRadius()));
		}else{
			map.put("latitude", "1");
			map.put("longitude", "1");
			map.put("accuracy", "0");
		}
		map.put("type", String.valueOf(arg0.getLocType()));
		Message msg2 = new Message();
		msg2.what = MESSAGE_GETLOCATION_CHECKIN;
		msg2.obj = map;
		handler.sendMessage(msg2);
	}
	@Override
	public void onReceivePoi(BDLocation arg0) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onTaskTry(Task task) {
		if(task == this.mTaskPlanlist ){
			if(this.getActivity() != null){
				((BaseActivity)this.getActivity()).editDialog("您的网络连接不稳定，正在重试...\n第"+this.mTaskPlanlist.getCurTryCount()+"次");
			}
		}
	}

}

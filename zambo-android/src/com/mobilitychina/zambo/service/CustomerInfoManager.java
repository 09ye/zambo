package com.mobilitychina.zambo.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.serialization.SoapObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;

import com.mobilitychina.intf.ITaskListener;
import com.mobilitychina.intf.Task;
import com.mobilitychina.log.McLogger;
import com.mobilitychina.net.HttpPostTask;
import com.mobilitychina.net.SoapTask;
import com.mobilitychina.util.CacheType;
import com.mobilitychina.util.Log;
import com.mobilitychina.util.NetObject;
import com.mobilitychina.zambo.app.ZamboApplication;
import com.mobilitychina.zambo.business.customer.data.CustomerInfo;
import com.mobilitychina.zambo.business.customer.data.CustomerType;
import com.mobilitychina.zambo.service.message.BaseMsgInfo;
import com.mobilitychina.zambo.service.message.MsgDefine;
import com.mobilitychina.zambo.service.message.MsgSenderInfo;
import com.mobilitychina.zambo.service.message.NotificationService;
import com.mobilitychina.zambo.service.message.bus.BusDelegate;
import com.mobilitychina.zambo.util.CommonUtil;
import com.mobilitychina.zambo.util.ConfigDefinition;
import com.mobilitychina.zambo.util.MsLogType;

/**
 * 客户信息管理,包括客户列表和客户类型列表
 * 
 * @author chenwang
 * 
 */
public class CustomerInfoManager implements ITaskListener,BusDelegate {
	private static final String TAG = "CustomerInfoManager";

	public enum CustomerLoadStatus {
		LOADING, SUCCESS, FAILED,
	};

	private final String SELECTED_FAVORITE = "收藏";
	private List<CustomerInfo> mCustomerinfoList;
	private Map<String, List<CustomerInfo>> mGroupCustomerMap;
	private List<CustomerType> mCustomerTypeList;

	private HttpPostTask getCustomerListTask;
	private SoapTask getCustomerTypeTask;

	private static CustomerInfoManager mIntance;

	private final int MAX_RETRY_COUNT = 3;
	private int retryCount = 0;
	private int customerTypeRetryCount = 0;

	private CustomerLoadStatus status;
	private CustomerLoadStatus customerTypeStatus;
	
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equalsIgnoreCase(
					ConfigDefinition.INTENT_ACTION_SERVICE_SETUP)		) {//订阅
				NotificationService.instance().addBusDelegate(CustomerInfoManager.this);
				MsgSenderInfo msg = new MsgSenderInfo();
				msg.setClientId(UserInfoManager.getInstance().getUserId());
				msg.setMsgId(MsgDefine.MSG_CUSTEMER_UPDATE);
				NotificationService.instance().sendMsg(msg);
			}
		}
	};
	
	

	private CustomerInfoManager() {
		mCustomerinfoList = new ArrayList<CustomerInfo>();
		mCustomerTypeList = new ArrayList<CustomerType>();
//		mFaoriteList = new ArrayList<CustomerInfo>();

		mGroupCustomerMap = new HashMap<String, List<CustomerInfo>>();
		
		
		
	}
	public static synchronized CustomerInfoManager getInstance() {
		if (mIntance == null) {
			mIntance = new CustomerInfoManager();
		}
		return mIntance;
	}

	public void start() {
		mCustomerinfoList.clear();
		mCustomerTypeList.clear();
		mGroupCustomerMap.clear();
		
		status = CustomerLoadStatus.LOADING;
		getCustomerListTask = new HttpPostTask(ZamboApplication.getInstance().getApplicationContext());
		getCustomerListTask.setUrl(HttpPostService.SOAP_URL + "get_customer");
		getCustomerListTask.getTaskArgs().put("emp_id", "3");
		getCustomerListTask.setListener(this);
		getCustomerListTask.start();
//		getCustomerListTask.setCacheType(CacheType.NOTKEYBUSSINESS);
		customerTypeStatus = CustomerLoadStatus.LOADING;
		getCustomerTypeTask = SoapService.getSiemensCustType("I");
		getCustomerTypeTask.setListener(this);
		getCustomerTypeTask.start();
		McLogger.getInstance().addLog(MsLogType.TYPE_SYS,MsLogType.ACT_CUSTOMER,"获取列表");
		IntentFilter intentFilter = new IntentFilter(
				ConfigDefinition.INTENT_ACTION_SERVICE_SETUP);
		ZamboApplication.getInstance().getApplicationContext().registerReceiver(mReceiver,intentFilter);
	}

	public CustomerLoadStatus getStatus() {
		return status;
	}

	public CustomerLoadStatus getCustomerTypeStatus() {
		return customerTypeStatus;
	}
	
	public boolean isCustomersLoaded(){
		return (status == CustomerLoadStatus.SUCCESS) && (customerTypeStatus == CustomerLoadStatus.SUCCESS);
	}
	
	public boolean isCustomerLoading(){
		return (status == CustomerLoadStatus.LOADING) || (customerTypeStatus == CustomerLoadStatus.LOADING);
	}

	public List<CustomerType> getCustomerTypeList() {
		return mCustomerTypeList;
	}

	public List<CustomerInfo> parseCustomerInfoList(NetObject result) {
		if (result == null) {
			return null;
		}
		List<NetObject> list = result.listForKey("data");
		List<CustomerInfo> custInfoList = new ArrayList<CustomerInfo>(list.size());
		for (NetObject netObject : list) {
			CustomerInfo customerInfo = new CustomerInfo();
			customerInfo.setId(netObject.stringForKey("id"));
			customerInfo.setCustName(netObject.stringForKey("custName"));
			customerInfo.setCustType(" ");
			List<Object> type =  netObject.arrayForKey("custType");
			if(type!=null){
				customerInfo.setCustType((String) type.get(1));
			}
			custInfoList.add(customerInfo);
		}
		return custInfoList;
	}

	/*private CustomerInfo processCustomer(String msg){
		CustomerInfo cust = new CustomerInfo();
		
		String element = msg;
		cust.setId(CommonUtil.getStringElement(element, "custId"));
		cust.setCustName(CommonUtil.getStringElement(element, "custNameEn"));
		cust.setCustShortName(CommonUtil.getStringElement(element, "custNameShort"));
		cust.setContactName(CommonUtil.getStringElement(element, "corporateDeputy"));
		cust.setContactPhone(CommonUtil.getStringElement(element, "corporateDeputy"));
		cust.setCustAddress(CommonUtil.getStringElement(element, "custAddress"));
		cust.setMobilePhone(CommonUtil.getStringElement(element, "custAddress"));
		cust.setEmpName(CommonUtil.getStringElement(element, "custAddress"));
		String tag = CommonUtil.getStringElement(element, "locked");
		if(tag != null && tag.equalsIgnoreCase("true")){
			cust.setIsLocked(true);
		}
		try {
			if (CommonUtil.getStringElement(element, "latitude") != null
					&& !CommonUtil.getStringElement(element, "latitude")
							.isEmpty()) {

				cust.setLatitude(Double.parseDouble(CommonUtil
						.getStringElement(element, "latitude")));
			}
		} catch (NumberFormatException e) {
			cust.setLatitude(0L);
		}
		try {
			if (CommonUtil.getStringElement(element, "longitude") != null
					&& !CommonUtil.getStringElement(element, "longitude")
							.isEmpty()) {
				cust.setLongitude(Double.parseDouble(CommonUtil
						.getStringElement(element, "longitude")));
			}
		} catch (NumberFormatException e) {
			cust.setLongitude(0L);
		}
		try {
			cust.setPosId(Long.parseLong(CommonUtil.getStringElement(element, "custAddress")));
		} catch (NumberFormatException e) {
			cust.setPosId(0L);
		}
		try {
			cust.setPlanVisitNum(Integer.parseInt(CommonUtil.getStringElement(element, "visitNum")));
		} catch (NumberFormatException e) {
			cust.setPlanVisitNum(0);
		}
		try {
			cust.setVisitedNum(Integer.parseInt(CommonUtil.getStringElement(element, "visitedNum")));
		} catch (NumberFormatException e) {
			cust.setVisitedNum(0);
		}
		try {
			cust.setCustDetailId(Long.parseLong(CommonUtil.getStringElement(element, "custDetailId")));
		} catch (NumberFormatException e) {
			cust.setCustDetailId(0L);
		}
		String visiteDate = CommonUtil.getStringElement(element, "visitDate");
		if (visiteDate.length() >= 10) {
			cust.setVisitDate(visiteDate.substring(0, 10));
		}
		cust.setEmpPwd(CommonUtil.getStringElement(element, "empPwd"));
		cust.setProType(CommonUtil.getStringElement(element, "proType"));
		cust.setProStatus(CommonUtil.getStringElement(element, "proType"));
		if (CommonUtil.getStringElement(element, "custStatus").equals("")) {
			cust.setCustStatus("未知");
		} else {
			cust.setCustStatus(CommonUtil.getStringElement(element, "custStatus"));
		}
		cust.setCustType(CommonUtil.getStringElement(element, "custType"));
		try {
			cust.setTotalVisitNum(Integer.parseInt(CommonUtil.getStringElement(element, "totalVisitNum")));
		}
		catch(NumberFormatException e){
			cust.setTotalVisitNum(0);
		}
		
		cust.setLastPlanVisitDate(CommonUtil.getStringElement(element, "lastPlanVisitDate"));
		cust.setNextPlanVisitDate(CommonUtil.getStringElement(element, "nextPlanVisitDate"));
		return cust;
	}*/
	public List<CustomerType> parseCustomerType(SoapObject result) {
		if (result == null) {
			return null;
		}
		int n = result.getPropertyCount();
		List<CustomerType> custTypeList = new ArrayList<CustomerType>(n);
		for (int i = 0; i < n; i++) {
			CustomerType custType = new CustomerType();
			String element = result.getProperty(i).toString();
			custType.setId(CommonUtil.getStringElement(element, "itemId"));
			custType.setName(CommonUtil.getStringElement(element, "itemName"));
			custType.setDescription(CommonUtil.getStringElement(element, "value"));
			custType.setCustomerCount(Integer.parseInt(CommonUtil.getStringElement(element, "totalCount")));

			custTypeList.add(custType);
		}
		// 确保“全部类型”排列在第一位,"S"类型排在第二位
		{
			CustomerType totalType = null;
			CustomerType sType = null;
			for (CustomerType ct : custTypeList) {
				if ("-1".equalsIgnoreCase(ct.getName())) {
					totalType = ct;
				} else if ("S".equalsIgnoreCase(ct.getName())) {
					sType = ct;
				}
			}
			if (sType != null) {
				if (custTypeList.indexOf(sType) > 0) {
					custTypeList.remove(sType);
					custTypeList.add(0, sType);
				}
			}
			if (totalType != null) {
				if (custTypeList.indexOf(totalType) > 0) {
					custTypeList.remove(totalType);
					custTypeList.add(0, totalType);
				}
			}
			
		}
		return custTypeList;
	}

	public List<CustomerInfo> getCustomerList() {
		return mCustomerinfoList;
	}

	public Map<String, List<CustomerInfo>> getGroupCustomerList() {
		return mGroupCustomerMap;
	}

	public CustomerInfo getCustomerById(String id) {
		for (CustomerInfo cust : mCustomerinfoList) {
			if (cust.getId().equalsIgnoreCase(id)) {
				return cust;
			}
		}
		return null;
	}
	public void editCustomer(CustomerInfo cust){
		for (int i = 0; i < mCustomerinfoList.size(); i++) {
			CustomerInfo c = mCustomerinfoList.get(i);
			if (c.getId().equalsIgnoreCase(cust.getId())) {
				if (cust.getChannelType() != null
						&& cust.getChannelType().length() > 0) {
					c.setChannelType(cust.getChannelType());
				}
				if (cust.getPlanVisitNum() > 0) {
					c.setPlanVisitNum(cust.getPlanVisitNum());
				}
				if (cust.getVisitedNum() > 0) {
					c.setVisitedNum(cust.getVisitedNum());
				}
				if (cust.getVisitDate() != null
						&& cust.getVisitDate().length() > 0) {
					c.setVisitDate(cust.getVisitDate());
				}

			}
		}
	}
	public void addCustomer(CustomerInfo cust) {
		if(this.mCustomerinfoList.contains(cust)){
			this.mCustomerinfoList.remove(cust);
			this.addCustomer(cust);
		}else{
			this.mCustomerinfoList.add(cust);
		}
	}

	public void addCustomer(int index, CustomerInfo cust) {
		this.mCustomerinfoList.add(index, cust);
	}

	public void deleteCustomer(String id) {
		CustomerInfo cust = this.getCustomerById(id);
		if (cust != null) {
			this.mCustomerinfoList.remove(cust);
		}
	}
	private void processtype(){
		for (CustomerInfo customerInfo : mCustomerinfoList) {
			String custType = customerInfo.getCustType();
			if (custType == null || custType.length() == 0) {
				continue;
			}
			List<CustomerInfo> arr = mGroupCustomerMap.get(custType);
			if (arr == null) {
				arr = new ArrayList<CustomerInfo>();
				mGroupCustomerMap.put(custType, arr);
			}
			arr.add(customerInfo);
		}
		String ids = UserInfoManager.getInstance().getFavoriteId();
		List<CustomerInfo> favoriteCustomerList = new ArrayList<CustomerInfo>();
		if(ids!=null){
			String[] ids2 = ids.split(",");
			for(String temp:ids2){
				if(CustomerInfoManager.getInstance().getCustomerById(temp)!=null){
					favoriteCustomerList.add(CustomerInfoManager.getInstance().getCustomerById(temp));
				}
			}
		}
		mGroupCustomerMap.put(SELECTED_FAVORITE, favoriteCustomerList);
		
	}
	@Override
	public void onTaskFinished(Task task) {
		if (task == getCustomerListTask) {
			McLogger.getInstance().addLog(MsLogType.TYPE_SYS,MsLogType.ACT_CUSTOMER,"获取列表成功");
			NetObject result = ((HttpPostTask)task).getResult();
			Log.i("HttpPostTask",task.getResult().toString());
			List<CustomerInfo> custInfoList = this.parseCustomerInfoList(result);
			if (custInfoList != null) {
				this.mCustomerinfoList.clear();
				this.mCustomerinfoList.addAll(custInfoList);
			}
			
			getCustomerListTask = null;
			processtype();
			status = CustomerLoadStatus.SUCCESS;
			Intent intent = new Intent();
			intent.setAction(ConfigDefinition.INTENT_ACTION_GETCUSTOMERCOMPLETE);
			ZamboApplication.getInstance().getApplicationContext().sendBroadcast(intent);
		} else if (task == getCustomerTypeTask) {
			SoapObject result = (SoapObject) task.getResult();
			List<CustomerType> custTypeList = this.parseCustomerType(result);
			if (custTypeList != null) {
				this.mCustomerTypeList.addAll(custTypeList);
			}
			getCustomerTypeTask = null;

			customerTypeStatus = CustomerLoadStatus.SUCCESS;
			Intent intent = new Intent();
			intent.setAction(ConfigDefinition.INTENT_ACTION_GETCUSTOMERTYPECOMPLETE);
			ZamboApplication.getInstance().getApplicationContext().sendBroadcast(intent);
		}
	}

	@Override
	public void onTaskFailed(Task task) {
		if (task == getCustomerListTask) {
			getCustomerListTask = null;

			if (retryCount < MAX_RETRY_COUNT) {
				// 1秒后自动重试
				new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
					@Override
					public void run() {
						retryCount++;
						getCustomerListTask = new HttpPostTask(ZamboApplication.getInstance().getApplicationContext());
						getCustomerListTask.setUrl(HttpPostService.SOAP_URL+"/get_customer");
//						getCustomerListTask.getTaskArgs().put(key, value);
						getCustomerListTask.setListener(CustomerInfoManager.this);
						getCustomerListTask.start();
					}
				}, 1000);
			} else {
				status = CustomerLoadStatus.FAILED;
				Intent intent = new Intent();
				intent.setAction(ConfigDefinition.INTENT_ACTION_GETCUSTOMERCOMPLETE);
				ZamboApplication.getInstance().getApplicationContext().sendBroadcast(intent);
			}
		} else if (task == getCustomerTypeTask) {
			getCustomerTypeTask = null;
			if (customerTypeRetryCount < MAX_RETRY_COUNT) {
				// 1秒后自动重试
				new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
					@Override
					public void run() {
						customerTypeRetryCount++;
						getCustomerTypeTask = SoapService.getSiemensCustType("I");
						getCustomerTypeTask.setListener(CustomerInfoManager.this);
						getCustomerTypeTask.start();
					}
				}, 1000);
			} else {
				customerTypeStatus = CustomerLoadStatus.FAILED;
				Intent intent = new Intent();
				intent.setAction(ConfigDefinition.INTENT_ACTION_GETCUSTOMERTYPECOMPLETE);
				ZamboApplication.getInstance().getApplicationContext().sendBroadcast(intent);
			}
		}
	}

	@Override
	public void onTaskUpdateProgress(Task task, int count, int total) {
	}

	@Override
	public Boolean processMsg(BaseMsgInfo msg) {
		// TODO Auto-generated method stub
		/*if(msg.getMsgId() == MsgDefine.MSG_CUSTEMER_UPDATE_RES_SUBSCRIB){
			if(msg.getMsgContent() != null){
				CustomerInfo info = processCustomer(msg.getMsgContent() );
				for (List<CustomerInfo> iterable_element : mGroupCustomerMap.values()) {
					if(iterable_element.contains(info))
					{
						iterable_element.remove(info);
					}
				}
				
				this.addCustomer(info);
				String custType = info.getCustType();
				if (custType != null && custType.length() != 0) {
					List<CustomerInfo> arr = mGroupCustomerMap.get(custType);
					if (arr == null) {
						arr = new ArrayList<CustomerInfo>();
						mGroupCustomerMap.put(custType, arr);
					}
					arr.add(info);
				}
			
				
				Intent intent = new Intent();
				intent.setAction(ConfigDefinition.INTENT_ACTION_GETCUSTOMERREFRESH);
				ZamboApplication.getInstance().getApplicationContext().sendBroadcast(intent);
			}
			
			return true;
		}*/
		return false;
	}

	@Override
	public void onTaskTry(Task task) {
		// TODO Auto-generated method stub
		
	}
	
	
}

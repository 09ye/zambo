package com.mobilitychina.zambo.service;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

import com.mobilitychina.net.SoapTask;
import com.mobilitychina.util.Log;
import com.mobilitychina.zambo.app.ZamboApplication;
import com.mobilitychina.zambo.util.CommonUtil;
import com.mobilitychina.zambo.util.ConfigDefinition;

public class SoapService {
	private static final String MAIN_OPENAPIURL = ConfigDefinition.URL_MAIN_OPENAPI;
	private static final String TEST_OPENAPIURL = ConfigDefinition.URL_TEST_OPENAPI;

	private static final String OPENAPI_PATH = "/openApiPlatform/Service/siemensService";

	public static String SOAP_URL = MAIN_OPENAPIURL + OPENAPI_PATH;
	public static final String SOAP_NAMESPACE = "http://siemens.api.openApiPlatform.nfsq.com/";

	public static final String LOGIN_METHOD = "siemensLogin";
	public static final String GET_ALL_SIMEMENS_CUSTOMERS_METHOD = "getAllSiemensCustomersV2";
	public static final String CREATE_VISIT_PLAN_METHOD = "createVisitPlan";
	public static final String DELETE_VISIT_PLAN_METHOD = "deleteVisitPlan";
	public static final String UPLOADGPS = "uploadGPSV2ReturnString";
	public static final String UPLOADGPSV2NEW = "uploadGPSV2NewReturnString";
	public static final String GET_SIEMENS_CUST_PRO_STATUS = "getSiemensCustProStatus";
	public static final String GET_SIEMENS_DICT = "getSiemensDict";

	public static final String GET_SENDWAITMESS_METHOD = "getSendWaitMessNew";
	public static final String GET_SENDWAITMESS_METHOD_V2 = "getSendWaitMessV2";
	public static final String GET_CALL_RATE = "getVistCustNum";
	public static final String RESP_MESS_RESULT_METHOD = "recordSendWaitMess";

	public static final String INSERT_SIEMENS_UPLOAD = "insertSiemensUpLoadV2Detail";

	public static final String GET_VISTPLAN_METHOD = "getVisitSiemensCustomers";

	public static final String UPDATE_SIEMENS_EMP = "updateSiemensEmp";

	public static final String GET_SIEMENS_EMP = "getVisitCustRate";

	public static final String GET_SIEMENS_PEMP = "getTheLevelVisitCustRateByPos";

	public static final String GET_VISTPLAN_EMP_METHOD = "getVisitSiemensCustomersByEmp";
	public static final String INSTRUCTION_TO_LOWERLEVEL = "instructionToLowerLevel";
	public static final String VISTPLAN_MAIL_REQUEST = "vistPlanMailRequest";
	public static final String GET_SENDWAItMESSREPLY = "getSendWaitMessReply";

	public static final String GET_SIEMENS_PRO_MESS = "getSiemensProMess";
	public static final String GETSIEMENSPROPROGRESSMESS = "getSiemensProProgressMess";

	public static final String GET_SIEMENS_CUSTVISITED_PRP_MESS = "getSiemensCustvisitedProMessV2";
	public static final String GET_SIEMENS_PROJECT_DETAIL = "getSiemensCustvisitedProDetailsMessV2Detail";
	public static final String GET_SIEMENS_CUST_KPI = "getKpiCountMess";
	public static final String GET_LAST_VISIT_DATE = "getLastVisitDate";
	public static final String GET_PLAN_VISIT_LOGS = "getPlanVisitLogsV2Detail";
	public static final String GET_FOLLOW_UP_LOGS_BY_VISIT ="getFollowUpDetailByDatelineId";
	public static final String GET_REPORT_EMPS = "getParentMobile";
	public static final String GET_LOWER_TEAM = "getLowerTeamV2";
	public static final String GET_MESSAGE_UNREAD_NUM = "getMessageUnReadNum";
	public static final String INSTRUCTION_TO_LOWERLEVELV2 = "instructionToLowerLevelV2";
	public static final String GET_LOWER_USER = "getLowerUser";
	public static final String DELETE_SENDER_WAIT_MESS = "deleteSendWaitMess";
	public static final String SEND_MSGINFO = "senderMessage";
	public static final String GET_KESHI_MESS = "getSiemensKeShiMess";
	public static final String GET_POSITION_MESS = "getSiemensPositionMess";
	public static final String GET_LATENT_MESS = "getSiemensLatentDemandMess";
	public static final String GET_NOTICE_MESS = "getSiemensNoticeContentMess";
	public static final String GER_RECORD_SENDEWAIT_MESS_BY_TYPE = "recordSendWaitMessByType";
	
	public static void switchServer(int isMainServer) {
		switch(isMainServer){
		case 0:
			SOAP_URL = MAIN_OPENAPIURL + OPENAPI_PATH;
			break;
		case 1:
			SOAP_URL = TEST_OPENAPIURL + OPENAPI_PATH;
			break;
		case 2:
			UserInfoManager.getInstance().sync(ZamboApplication.getInstance().getApplicationContext(), false);
			SOAP_URL = UserInfoManager.getInstance().getDefinitUrl();
//			Log.d("SoapActivity", UserInfoManager.getInstance().getDefinitUrl());
			break;
		
		}
		
	}

	/**
	 * 登录Task
	 * 
	 * @param context
	 * @param phone
	 * @param password
	 * @param deviceid
	 * @return
	 */
	public static SoapTask getLoginTask(Context context, String name, String password, String deviceid) {
		SoapTask task = new SoapTask(context);
		System.out.println(SoapService.SOAP_URL+"/login");
		task.setUrl(SoapService.SOAP_URL+"/login");
		JSONObject json = new JSONObject();
		JSONObject message = new JSONObject();
		try {
			message.put("type", "basic");
			message.put("name", name);
			message.put("password", password);
			json.put("identication", message);
			json.put("data", new JSONObject().put("imei", deviceid));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		task.setPostString(json.toString());
		return task;
	}

	/**
	 * 修改密码
	 * 
	 * @param oldPassword
	 * @param newPassword
	 * @return
	 */
	public static SoapTask getModifyPasswordTask(Context context, String phone, String oldPassword, String newPassword) {
		SoapTask task = new SoapTask(context);
		task.setUrl(SoapService.SOAP_URL);
		task.setSoapNamespace(SoapService.SOAP_NAMESPACE);
		task.setSoapMethod(SoapService.UPDATE_SIEMENS_EMP);

		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("arg0", phone));
		params.add(new BasicNameValuePair("arg1", oldPassword));
		params.add(new BasicNameValuePair("arg2", newPassword));
		task.setParams(params);

		return task;
	}

	/**
	 * 获取所有的客户列表
	 * 
	 * @param type
	 *            "A"=我的团队,"I"=我的
	 * @return 客户列表对象
	 */
	public static SoapTask getAllZamboCustomersTask(Context context, String type) {
		SoapTask task = new SoapTask(context);
		JSONObject json = new JSONObject();
		JSONObject message = new JSONObject();
		UserInfoManager.getInstance().sync(ZamboApplication.getInstance().getApplicationContext(), false);
		try {
			message.put("type", "basic");
			message.put("name", UserInfoManager.getInstance().getPhone());
			message.put("password", UserInfoManager.getInstance().getPassword());
			json.put("identication", message);
			json.put("data", new JSONObject());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		task.setUrl(SoapService.SOAP_URL+"/get_customer");
		task.setPostString(json.toString());
		return task;
	}

	/**
	 * 获取客户类型
	 * 
	 * @param type
	 *            "A"=我的团队,"I"=我的
	 * @return 客户类型列表对象
	 */
	public static SoapTask getSiemensCustType(String type) {
		SoapTask task = new SoapTask(ZamboApplication.getInstance().getApplicationContext());
		task.setUrl(SoapService.SOAP_URL);
		task.setSoapNamespace(SoapService.SOAP_NAMESPACE);
		task.setSoapMethod("getSiemensCustType");
		//task.setCacheType(CacheType.NOTKEYBUSSINESS);
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("arg0", "visits@type"));
		params.add(new BasicNameValuePair("arg1", UserInfoManager.getInstance().getPhone()));
		params.add(new BasicNameValuePair("arg2", UserInfoManager.getInstance().getPassword()));
		params.add(new BasicNameValuePair("arg3", type));
		task.setParams(params);

		return task;
	}

	/**
	 * 获取客户类型的拜访覆盖率
	 * 
	 * @param custType
	 *            客户类型ID,{C1, C2, C3, S, -1}
	 * @param type
	 *            "A"=我的团队,"I"=我的
	 * @return "已拜访的客户数，客户总数"
	 */
	public static SoapTask getCoverRate(String custType, String type) {
		SoapTask task = new SoapTask(ZamboApplication.getInstance().getApplicationContext());
		task.setUrl(SoapService.SOAP_URL);
		task.setSoapNamespace(SoapService.SOAP_NAMESPACE);
		task.setSoapMethod("getCoverRate");

		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("arg0", UserInfoManager.getInstance().getPhone()));
		params.add(new BasicNameValuePair("arg1", UserInfoManager.getInstance().getPassword()));
		params.add(new BasicNameValuePair("arg2", custType));
		params.add(new BasicNameValuePair("arg3", "-1"));
		params.add(new BasicNameValuePair("arg4", type));// A,I
		task.setParams(params);
		return task;
	}
	/**
	 * 消息推送测试接口
	 * 
	
	 */
	public static SoapTask insertMessage(String client, String msgid, String version, String content,String tag){
		SoapTask task = new SoapTask(ZamboApplication.getInstance().getApplicationContext());
		task.setUrl(SoapService.SOAP_URL);
		task.setSoapNamespace(SoapService.SOAP_NAMESPACE);
		task.setSoapMethod("insertMessage");

		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("arg0", client));
		params.add(new BasicNameValuePair("arg1", msgid));
		params.add(new BasicNameValuePair("arg2", version));
		params.add(new BasicNameValuePair("arg3", content));
		params.add(new BasicNameValuePair("arg4", tag));// A,I
		task.setParams(params);
		return task;
	}

	
	/**
	 * 获取累计拜访率
	 * 
	 * @param custType
	 *            客户类型ID
	 * @param custId
	 *            客户ID，为“-1”表示某种类型的所有客户
	 * @param type
	 *            "A"=我的团队,"I"=我的
	 * @return “期望次数，计划次数，实际次数”
	 */
	public static SoapTask getKpiCountMess(String custType, String custId, String type) {
		SoapTask task = new SoapTask(ZamboApplication.getInstance().getApplicationContext());
		task.setUrl(SoapService.SOAP_URL);
		task.setSoapNamespace(SoapService.SOAP_NAMESPACE);
		task.setSoapMethod("getKpiCountMess");

		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("arg0", UserInfoManager.getInstance().getPhone()));
		params.add(new BasicNameValuePair("arg1", UserInfoManager.getInstance().getPassword()));
		params.add(new BasicNameValuePair("arg2", custType));
		params.add(new BasicNameValuePair("arg3", custId));
		params.add(new BasicNameValuePair("arg4", type));// A,I
		task.setParams(params);
		return task;
	}

	/**
	 * 获取计划列表
	 * 
	 * @param startDay
	 *            开始日起
	 * 
	 * @param endDay
	 *            结束日期
	 * 
	 * @return 获取计划列表
	 */
	public static SoapTask getPlanlistTask(String startDay, String endDay) {
		SoapTask task = new SoapTask(ZamboApplication.getInstance().getApplicationContext());
		task.setUrl(SoapService.SOAP_URL);
		task.setSoapNamespace(SoapService.SOAP_NAMESPACE);
		task.setSoapMethod(SoapService.GET_VISTPLAN_METHOD);

		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("arg0", startDay));
		params.add(new BasicNameValuePair("arg1", endDay));
		params.add(new BasicNameValuePair("arg2", UserInfoManager.getInstance().getPhone()));
		params.add(new BasicNameValuePair("arg3", UserInfoManager.getInstance().getPassword()));
		task.setParams(params);
		return task;
	}
	/**
	 * 获取下级计划列表接口
	 * @param startDay
	 * @param endDay
	 * @param empid 下级id
	 * @return
	 */
	public static SoapTask getPlanlistByEmployeeTask(String startDay, String endDay,String empid) {
		SoapTask task = new SoapTask(ZamboApplication.getInstance().getApplicationContext());
		task.setUrl(SoapService.SOAP_URL);
		task.setSoapNamespace(SoapService.SOAP_NAMESPACE);
		task.setSoapMethod(SoapService.GET_VISTPLAN_EMP_METHOD);

		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("arg0", startDay));
		params.add(new BasicNameValuePair("arg1", endDay));
		params.add(new BasicNameValuePair("arg2", UserInfoManager.getInstance().getPhone()));
		params.add(new BasicNameValuePair("arg3", UserInfoManager.getInstance().getPassword()));
		params.add(new BasicNameValuePair("arg4", empid));
		task.setParams(params);
		return task;
	}
	/**
	 * 请求消息
	 * 
	 * @param context
	 * @param isOnlyToday
	 *            是否仅仅请求当天的
	 * @return
	 */
	public static SoapTask getMessageTask(Boolean isOnlyToday) {
		SoapTask task = new SoapTask(ZamboApplication.getInstance().getApplicationContext());
		task.setUrl(SoapService.SOAP_URL);
		task.setSoapNamespace(SoapService.SOAP_NAMESPACE);

		task.setSoapMethod(SoapService.GET_SENDWAITMESS_METHOD);

		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("arg0", UserInfoManager.getInstance().getPhone()));
		params.add(new BasicNameValuePair("arg1", UserInfoManager.getInstance().getPassword()));
		params.add(new BasicNameValuePair("arg2", ""));

		params.add(new BasicNameValuePair("arg3", isOnlyToday ? "Y" : "N"));

		task.setParams(params);
		return task;
	}

	/**
	 * 获取项目跟进记录
	 * 
	 * @param customerId
	 *            客户ID
	 * @return
	 */
	public static SoapTask getProjectRecordListTask(String customerId) {
		SoapTask task = new SoapTask(ZamboApplication.getInstance().getApplicationContext());
		task.setUrl(SoapService.SOAP_URL);
		task.setSoapNamespace(SoapService.SOAP_NAMESPACE);
		task.setSoapMethod(SoapService.GET_SIEMENS_CUSTVISITED_PRP_MESS);
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("arg0", customerId));
		params.add(new BasicNameValuePair("arg1", UserInfoManager.getInstance().getPhone()));
		params.add(new BasicNameValuePair("arg2", UserInfoManager.getInstance().getPassword()));
		task.setParams(params);
		return task;
	}

	/**
	 * 获取客户随访记录
	 * 
	 * @param customerId
	 *            客户ID
	 * @return
	 */
	public static SoapTask getFollowupListTask(String customerId) {
		SoapTask task = new SoapTask(ZamboApplication.getInstance().getApplicationContext());
		task.setUrl(SoapService.SOAP_URL);
		task.setSoapNamespace(SoapService.SOAP_NAMESPACE);
		task.setSoapMethod(SoapService.GET_PLAN_VISIT_LOGS);
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("arg0", customerId));
		params.add(new BasicNameValuePair("arg1", UserInfoManager.getInstance().getPhone()));
		params.add(new BasicNameValuePair("arg2", UserInfoManager.getInstance().getPassword()));
		task.setParams(params);
		return task;
	}
	
	/**
	 * @param planVisitID  计划ID
	 * @return
	 */
	public static SoapTask getFollowupListForCurrVisitTask(String planVisitID) {
		SoapTask task = new SoapTask(ZamboApplication.getInstance().getApplicationContext());
		task.setUrl(SoapService.SOAP_URL);
		task.setSoapNamespace(SoapService.SOAP_NAMESPACE);
		task.setSoapMethod(SoapService.GET_FOLLOW_UP_LOGS_BY_VISIT);
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("arg0", planVisitID));
		params.add(new BasicNameValuePair("arg1", UserInfoManager.getInstance().getPhone()));
		params.add(new BasicNameValuePair("arg2", UserInfoManager.getInstance().getPassword()));
		task.setParams(params);
		return task;
	}

	/**
	 * 上传信息
	 * 
	 * @param report
	 * @param detail
	 * @param share
	 * @param dateLineId
	 * @param detailCustId
	 * @param type
	 * @param reportObject
	 * @param id
	 * @param remark
	 * @return
	 */
	public static SoapTask insertSiemensUpload(String report, String detail, String share, String dateLineId,
			String detailCustId, String type, String reportObject, String id, String remark) {
		SoapTask task = new SoapTask(ZamboApplication.getInstance().getApplicationContext());
		task.setUrl(SoapService.SOAP_URL);
		task.setSoapNamespace(SoapService.SOAP_NAMESPACE);
		task.setSoapMethod(SoapService.INSERT_SIEMENS_UPLOAD);
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("arg0", report));
		params.add(new BasicNameValuePair("arg1", detail));
		params.add(new BasicNameValuePair("arg2", share));
		params.add(new BasicNameValuePair("arg3", dateLineId));
		params.add(new BasicNameValuePair("arg4", detailCustId));
		params.add(new BasicNameValuePair("arg5", type));
		params.add(new BasicNameValuePair("arg6", UserInfoManager.getInstance().getPhone()));
		params.add(new BasicNameValuePair("arg7", UserInfoManager.getInstance().getPassword()));
		params.add(new BasicNameValuePair("arg8", reportObject));
		params.add(new BasicNameValuePair("arg9", id));
		params.add(new BasicNameValuePair("arg10", remark));
		
		

		task.setParams(params);
		return task;
	}

	/**
	 * 签到
	 * 
	 * @param context
	 * @param custId
	 * @param datelineId
	 * @param longitude
	 * @param latitude
	 * @return
	 */
	public static SoapTask getCheckInTask(String custId, String datelineId, String longitude, String latitude,String accuracy){
		SoapTask task = new SoapTask(ZamboApplication.getInstance().getApplicationContext());
		PackageManager manager = ZamboApplication.getInstance().getApplicationContext().getPackageManager();
		task.setUrl(SoapService.SOAP_URL);
		task.setSoapNamespace(SoapService.SOAP_NAMESPACE);
		task.setSoapMethod(SoapService.UPLOADGPS);
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("arg0", UserInfoManager.getInstance().getPhone()));
		params.add(new BasicNameValuePair("arg1", UserInfoManager.getInstance().getPassword()));
		params.add(new BasicNameValuePair("arg2", datelineId));
		params.add(new BasicNameValuePair("arg3", longitude));
		params.add(new BasicNameValuePair("arg4", latitude));
		params.add(new BasicNameValuePair("arg5", accuracy));
		params.add(new BasicNameValuePair("arg6", CommonUtil.getCurrentTime("yyyy-MM-dd HH:mm:ss")));
		try {
			PackageInfo info = manager.getPackageInfo(ZamboApplication.getInstance().getApplicationContext().getPackageName(), 0);
			String versioncode = info.versionName.replaceAll("\\.", "");
			params.add(new BasicNameValuePair("arg7", versioncode));
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			params.add(new BasicNameValuePair("arg7", "200"));
		}
		task.setParams(params);
		return task;

	}
	/**
	 * 签到
	 * 
	 * @param context
	 * @param custId
	 * @param datelineId
	 * @param longitude
	 * @param latitude
	 * @return
	 */
	public static SoapTask getCheckInAndUpdateLocationTask(String custId, String datelineId, String longitude, String latitude,String accuracy) {
		SoapTask task = new SoapTask(ZamboApplication.getInstance().getApplicationContext());
		PackageManager manager = ZamboApplication.getInstance().getApplicationContext().getPackageManager();
		task.setUrl(SoapService.SOAP_URL);
		task.setSoapNamespace(SoapService.SOAP_NAMESPACE);
		task.setSoapMethod(SoapService.UPLOADGPSV2NEW);
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("arg0", UserInfoManager.getInstance().getPhone()));
		params.add(new BasicNameValuePair("arg1", UserInfoManager.getInstance().getPassword()));
		params.add(new BasicNameValuePair("arg2", datelineId));
		params.add(new BasicNameValuePair("arg3", longitude));
		params.add(new BasicNameValuePair("arg4", latitude));
		params.add(new BasicNameValuePair("arg5", accuracy));
		params.add(new BasicNameValuePair("arg6", CommonUtil.getCurrentTime("yyyy-MM-dd HH:mm:ss")));
		try {
			PackageInfo info = manager.getPackageInfo(ZamboApplication.getInstance().getApplicationContext().getPackageName(), 0);
			String versioncode = info.versionName.replaceAll("\\.", "");
			params.add(new BasicNameValuePair("arg7", versioncode));
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			params.add(new BasicNameValuePair("arg7", "200"));
		}
		
		task.setParams(params);
		return task;

	}
	
	public static SoapTask getOfflineCheckInTask(String custId, String datelineId, String longitude, String latitude,String accuracy,String datetime){
		SoapTask task = new SoapTask(ZamboApplication.getInstance().getApplicationContext());
		task.setUrl(SoapService.SOAP_URL);
		task.setSoapNamespace(SoapService.SOAP_NAMESPACE);
		task.setSoapMethod(SoapService.UPLOADGPS);
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("arg0", UserInfoManager.getInstance().getPhone()));
		params.add(new BasicNameValuePair("arg1", UserInfoManager.getInstance().getPassword()));
		params.add(new BasicNameValuePair("arg2", datelineId));
		params.add(new BasicNameValuePair("arg3", longitude));
		params.add(new BasicNameValuePair("arg4", latitude));
		params.add(new BasicNameValuePair("arg5", accuracy));
		params.add(new BasicNameValuePair("arg6", datetime));
		task.setParams(params);
		return task;

	}
	/**
	 * 获取下级
	 * 
	 * @param context
	 * @param posId
	 *            传 “-1” 表示 直属下级。
	 * 
	 * @return
	 */
	public static SoapTask getEmpListTask(String posId) {
		SoapTask task = new SoapTask(ZamboApplication.getInstance().getApplicationContext());
		task.setUrl(SoapService.SOAP_URL);
		task.setSoapNamespace(SoapService.SOAP_NAMESPACE);
		task.setSoapMethod(SoapService.GET_SIEMENS_EMP);
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("arg0", UserInfoManager.getInstance().getPhone()));
		params.add(new BasicNameValuePair("arg1", UserInfoManager.getInstance().getPassword()));
		params.add(new BasicNameValuePair("arg2", posId));
		task.setParams(params);
		return task;

	}

	// load 父类
	public static SoapTask getParentEmpListTask(String posId) {
		SoapTask task = new SoapTask(ZamboApplication.getInstance().getApplicationContext());
		task.setUrl(SoapService.SOAP_URL);
		task.setSoapNamespace(SoapService.SOAP_NAMESPACE);
		task.setSoapMethod(SoapService.GET_SIEMENS_PEMP);
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("arg0", UserInfoManager.getInstance().getPhone()));
		params.add(new BasicNameValuePair("arg1", UserInfoManager.getInstance().getPassword()));
		params.add(new BasicNameValuePair("arg2", posId));
		return task;
	}

	/**
	 * 获取项目进度详情
	 * 
	 * @param customerId
	 *            客户ID
	 * @param proNumber
	 *            项目编号
	 * @return
	 */
	public static SoapTask getSiemensProjectDetailTask(String customerId, String proNumber) {
		SoapTask task = new SoapTask(ZamboApplication.getInstance().getApplicationContext());
		task.setUrl(SoapService.SOAP_URL);
		task.setSoapNamespace(SoapService.SOAP_NAMESPACE);
		task.setSoapMethod(SoapService.GET_SIEMENS_PROJECT_DETAIL);
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("arg0", customerId));
		params.add(new BasicNameValuePair("arg1", proNumber));
		params.add(new BasicNameValuePair("arg2", UserInfoManager.getInstance().getPhone()));
		params.add(new BasicNameValuePair("arg3", UserInfoManager.getInstance().getPassword()));
		task.setParams(params);
		return task;
	}

	/**
	 * 获取项目列表
	 * 
	 * @return
	 */
	public static SoapTask getSiemensProjectListTask() {
		SoapTask task = new SoapTask(ZamboApplication.getInstance().getApplicationContext());
		task.setUrl(SoapService.SOAP_URL);
		task.setSoapNamespace(SoapService.SOAP_NAMESPACE);
		task.setSoapMethod(SoapService.GET_SIEMENS_DICT);
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("arg0", "pro@siemens"));
		params.add(new BasicNameValuePair("arg1", UserInfoManager.getInstance().getPhone()));
		params.add(new BasicNameValuePair("arg2", UserInfoManager.getInstance().getPassword()));
		task.setParams(params);
		return task;
	}

	/**
	 * 获取项目状态列表
	 * 
	 * @param projectId
	 * @return
	 */
	public static SoapTask getSiemensProjectStatusTask() {
		SoapTask task = new SoapTask(ZamboApplication.getInstance().getApplicationContext());
		task.setUrl(SoapService.SOAP_URL);
		task.setSoapNamespace(SoapService.SOAP_NAMESPACE);
		task.setSoapMethod(SoapService.GETSIEMENSPROPROGRESSMESS);
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("arg0", "3252"));
		params.add(new BasicNameValuePair("arg1", UserInfoManager.getInstance().getPhone()));
		params.add(new BasicNameValuePair("arg2", UserInfoManager.getInstance().getPassword()));
		task.setParams(params);
		return task;
	}

	/**
	 * 消息发送
	 * 
	 * @return
	 */
	public static SoapTask getSendMessageTask(String smscontent, String meg) {
		SoapTask task = new SoapTask(ZamboApplication.getInstance().getApplicationContext());
		task.setUrl(SoapService.SOAP_URL);
		task.setSoapNamespace(SoapService.SOAP_NAMESPACE);
		task.setSoapMethod(SoapService.INSTRUCTION_TO_LOWERLEVEL);
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("arg0", UserInfoManager.getInstance().getPhone()));
		params.add(new BasicNameValuePair("arg1", UserInfoManager.getInstance().getPassword()));
		params.add(new BasicNameValuePair("arg2", smscontent));
		params.add(new BasicNameValuePair("arg3", smscontent));
		task.setParams(params);
		return task;
	}

	/**
	 * 创建拜访计划
	 * 
	 * @param planInfo
	 * @return
	 */
	public static SoapTask createVisitPlanTask(String custIds, String date) {
		SoapTask task = new SoapTask(ZamboApplication.getInstance().getApplicationContext());
		task.setUrl(SoapService.SOAP_URL);
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("arg0", custIds));
		params.add(new BasicNameValuePair("arg1", date));
		params.add(new BasicNameValuePair("arg2", UserInfoManager.getInstance().getPhone()));
		params.add(new BasicNameValuePair("arg3", UserInfoManager.getInstance().getPassword()));
		task.setParams(params);
		return task;
	}

	/**
	 * 获取下级团队。 posid 职位id
	 * 
	 * @return
	 */
	public static SoapTask getLowerTeamTask(String level) {
		SoapTask task = new SoapTask(ZamboApplication.getInstance().getApplicationContext());
		task.setUrl(SoapService.SOAP_URL);
		task.setSoapNamespace(SoapService.SOAP_NAMESPACE);
		task.setSoapMethod(SoapService.GET_LOWER_TEAM);
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("arg0", UserInfoManager.getInstance().getPhone()));
		params.add(new BasicNameValuePair("arg1", UserInfoManager.getInstance().getPassword()));
		params.add(new BasicNameValuePair("arg2", level));

		task.setParams(params);
		return task;
	}

	/**
	 * 获取未读消息条目
	 * 
	 * 
	 * @return
	 */
	public static SoapTask getMessageUnReadNum() {
		SoapTask task = new SoapTask(ZamboApplication.getInstance().getApplicationContext());
		task.setUrl(SoapService.SOAP_URL);
		task.setSoapNamespace(SoapService.SOAP_NAMESPACE);
		task.setSoapMethod(SoapService.GET_MESSAGE_UNREAD_NUM);
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("arg0", UserInfoManager.getInstance().getPhone()));
		params.add(new BasicNameValuePair("arg1", UserInfoManager.getInstance().getPassword()));

		task.setParams(params);
		return task;
	}

	/**
	 * 请求消息
	 * 
	 * @param context
	 * @param isOnlyToday
	 *            是否仅仅请求当天的
	 * @return
	 */
	public static SoapTask getMessageTask(String status, String type, Boolean isOnlyToday) {
		SoapTask task = new SoapTask(ZamboApplication.getInstance().getApplicationContext());
		task.setUrl(SoapService.SOAP_URL);
		task.setSoapNamespace(SoapService.SOAP_NAMESPACE);

		task.setSoapMethod(SoapService.GET_SENDWAITMESS_METHOD_V2);

		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("arg0", UserInfoManager.getInstance().getPhone()));
		params.add(new BasicNameValuePair("arg1", UserInfoManager.getInstance().getPassword()));
		params.add(new BasicNameValuePair("arg2", status));
		params.add(new BasicNameValuePair("arg3", type));
		params.add(new BasicNameValuePair("arg4", isOnlyToday ? "Y" : "N"));

		task.setParams(params);
		return task;
	}

	/**
	 * 获取收件人信息
	 * 
	 * @param smsId
	 * @return
	 */
	public static SoapTask getSendWaitMessReplyTask(String smsId) {
		SoapTask task = new SoapTask(ZamboApplication.getInstance().getApplicationContext());
		task.setUrl(SoapService.SOAP_URL);
		task.setSoapNamespace(SoapService.SOAP_NAMESPACE);

		task.setSoapMethod(SoapService.GET_SENDWAItMESSREPLY);

		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("arg0", UserInfoManager.getInstance().getPhone()));
		params.add(new BasicNameValuePair("arg1", UserInfoManager.getInstance().getPassword()));
		params.add(new BasicNameValuePair("arg2", smsId));
		task.setParams(params);
		return task;
	}

	/*
	 * 高层下达指令V2
	 * 
	 * @param mobile
	 * 
	 * @param pwd
	 * 
	 * @param smscontent
	 * 
	 * @param title
	 * 
	 * @param smstype
	 * 
	 * @return
	 */

	public static SoapTask instructionToLowerLevelV2(String smscontent, String teamPosId, String userPosId) {
		SoapTask task = new SoapTask(ZamboApplication.getInstance().getApplicationContext());
		task.setUrl(SoapService.SOAP_URL);
		task.setSoapNamespace(SoapService.SOAP_NAMESPACE);

		task.setSoapMethod(SoapService.INSTRUCTION_TO_LOWERLEVELV2);

		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("arg0", UserInfoManager.getInstance().getPhone()));
		params.add(new BasicNameValuePair("arg1", UserInfoManager.getInstance().getPassword()));
		params.add(new BasicNameValuePair("arg2", smscontent));
		params.add(new BasicNameValuePair("arg3", teamPosId));
		params.add(new BasicNameValuePair("arg4", userPosId));

		task.setParams(params);
		return task;
	}

	/**
	 * 获取下级
	 * 
	 * @param posId
	 * @return
	 */
	public static SoapTask getLowerUser() {
		SoapTask task = new SoapTask(ZamboApplication.getInstance().getApplicationContext());
		task.setUrl(SoapService.SOAP_URL);
		task.setSoapNamespace(SoapService.SOAP_NAMESPACE);

		task.setSoapMethod(SoapService.GET_LOWER_USER);

		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("arg0", UserInfoManager.getInstance().getPhone()));
		params.add(new BasicNameValuePair("arg1", UserInfoManager.getInstance().getPassword()));
		params.add(new BasicNameValuePair("arg2", ""));
		task.setParams(params);
		return task;
	}

	/**
	 * 消息反馈接口，如已读
	 * 
	 * @param messId
	 * @param result
	 * @return
	 */
	public static SoapTask getSenderMessageReplayInfoTask(String messId, String result) {
		// TODO Auto-generated method stub

		SoapTask task = new SoapTask(ZamboApplication.getInstance().getApplicationContext());
		task.setUrl(SoapService.SOAP_URL);
		task.setSoapNamespace(SoapService.SOAP_NAMESPACE);

		task.setSoapMethod(SoapService.RESP_MESS_RESULT_METHOD);

		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("arg0", messId));
		params.add(new BasicNameValuePair("arg1", UserInfoManager.getInstance().getPhone()));
		params.add(new BasicNameValuePair("arg2", result));
		params.add(new BasicNameValuePair("arg3", "Y"));
		params.add(new BasicNameValuePair("arg4", UserInfoManager.getInstance().getPassword()));
		task.setParams(params);
		return task;
	}

	/**
	 * 发送月报
	 * 
	 * @param visitDate
	 *            格式：yyyy-MM
	 * @return
	 */
	public static SoapTask getVistPlanMailRequestTask(String visitDate) {
		SoapTask task = new SoapTask(ZamboApplication.getInstance().getApplicationContext());
		task.setUrl(SoapService.SOAP_URL);
		task.setSoapNamespace(SoapService.SOAP_NAMESPACE);
		task.setSoapMethod("vistPlanMailRequestByMonth");

		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("arg0", UserInfoManager.getInstance().getPhone()));
		params.add(new BasicNameValuePair("arg1", UserInfoManager.getInstance().getPassword()));
		params.add(new BasicNameValuePair("arg2", visitDate));

		task.setParams(params);
		return task;
	}

	/**
	 * 删除计划
	 * 
	 * @param datelineId
	 * 
	 * @return
	 */
	public static SoapTask getDeleteVisitPlan(String datelineId) {
		// TODO Auto-generated method stub

		SoapTask task = new SoapTask(ZamboApplication.getInstance().getApplicationContext());
		task.setUrl(SoapService.SOAP_URL);
		task.setSoapNamespace(SoapService.SOAP_NAMESPACE);
		task.setSoapMethod(SoapService.DELETE_VISIT_PLAN_METHOD);
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("arg0", datelineId));
		params.add(new BasicNameValuePair("arg1", UserInfoManager.getInstance().getPhone()));
		params.add(new BasicNameValuePair("arg2", UserInfoManager.getInstance().getPassword()));
		task.setParams(params);
		return task;
	}

	/**
	 * 获取汇报对象(结果格式：test1&11111/test2&1111111)
	 * 
	 * @return
	 */
	public static SoapTask getReportEmpsTask() {
		SoapTask task = new SoapTask(ZamboApplication.getInstance().getApplicationContext());
		task.setUrl(SoapService.SOAP_URL);
		task.setSoapNamespace(SoapService.SOAP_NAMESPACE);
		task.setSoapMethod(SoapService.GET_REPORT_EMPS);
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("arg0", UserInfoManager.getInstance().getPhone()));
		params.add(new BasicNameValuePair("arg1", UserInfoManager.getInstance().getPassword()));
		task.setParams(params);
		return task;
	}
	/**
	 * 获取汇报对象(结果格式：test1&11111/test2&1111111)
	 * 
	 * @return
	 */
	public static SoapTask getRecordSendWaitMessbyTypeTask(String smstype) {
		SoapTask task = new SoapTask(ZamboApplication.getInstance().getApplicationContext());
		task.setUrl(SoapService.SOAP_URL);
		task.setSoapNamespace(SoapService.SOAP_NAMESPACE);
		task.setSoapMethod(SoapService.GER_RECORD_SENDEWAIT_MESS_BY_TYPE);
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("arg0", UserInfoManager.getInstance().getPhone()));
		params.add(new BasicNameValuePair("arg1", UserInfoManager.getInstance().getPassword()));
		params.add(new BasicNameValuePair("arg2",smstype));
		task.setParams(params);
		return task;
	}
	/**
	 * 删除消息
	 * 
	 * @return
	 */
	public static SoapTask getDeleteSendMessage(String id) {
		SoapTask task = new SoapTask(ZamboApplication.getInstance().getApplicationContext());
		task.setUrl(SoapService.SOAP_URL);
		task.setSoapNamespace(SoapService.SOAP_NAMESPACE);
		task.setSoapMethod(SoapService.DELETE_SENDER_WAIT_MESS);
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("arg0", UserInfoManager.getInstance().getPhone()));
		params.add(new BasicNameValuePair("arg1", UserInfoManager.getInstance().getPassword()));
		params.add(new BasicNameValuePair("arg2",id));
		task.setParams(params);
		return task;
	}
	/**
	 * 删除消息
	 * 
	 * @return
	 */
	public static SoapTask sendMsgInfo(String mClientId,String mMsgId, String mMsgContent) {
		SoapTask task = new SoapTask(ZamboApplication.getInstance().getApplicationContext());
		task.setUrl(ConfigDefinition.URL_PUSH + OPENAPI_PATH);
		task.setSoapNamespace(SoapService.SOAP_NAMESPACE);
		task.setSoapMethod(SoapService.SEND_MSGINFO);
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("arg0", mClientId));
		params.add(new BasicNameValuePair("arg1", mMsgId));
		params.add(new BasicNameValuePair("arg2",mMsgContent));
		task.setParams(params);
		return task;
	}
	
	/**
	 * 获取科室列表
	 * 
	 * @return
	 */
	public static SoapTask getDepartmentListTask() {
		SoapTask task = new SoapTask(ZamboApplication.getInstance().getApplicationContext());
		
		task.setUrl(SoapService.SOAP_URL);
		task.setSoapNamespace(SoapService.SOAP_NAMESPACE);
		task.setSoapMethod(SoapService.GET_KESHI_MESS);
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("arg0", "324"));
		params.add(new BasicNameValuePair("arg1", UserInfoManager.getInstance().getPhone()));
		params.add(new BasicNameValuePair("arg2", UserInfoManager.getInstance().getPassword()));

		task.setParams(params);
		return task;
	}
	
	/**
	 * 获取职位列表
	 * 
	 * @return
	 */
	public static SoapTask getJobTitleListTask() {
		SoapTask task = new SoapTask(ZamboApplication.getInstance().getApplicationContext());
		task.setUrl(SoapService.SOAP_URL);
		task.setSoapNamespace(SoapService.SOAP_NAMESPACE);
		task.setSoapMethod(SoapService.GET_POSITION_MESS);
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("arg0", "325"));
		params.add(new BasicNameValuePair("arg1", UserInfoManager.getInstance().getPhone()));
		params.add(new BasicNameValuePair("arg2", UserInfoManager.getInstance().getPassword()));

		task.setParams(params);
		return task;
	}
	
	/**
	 * 获取设备产品列表
	 * 
	 * @return
	 */
	public static SoapTask getFacilityListTask() {
		SoapTask task = new SoapTask(ZamboApplication.getInstance().getApplicationContext());
		task.setUrl(SoapService.SOAP_URL);
		task.setSoapNamespace(SoapService.SOAP_NAMESPACE);
		task.setSoapMethod(SoapService.GET_LATENT_MESS);
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("arg0", "pro@siemens"));
		params.add(new BasicNameValuePair("arg1", UserInfoManager.getInstance().getPhone()));
		params.add(new BasicNameValuePair("arg2", UserInfoManager.getInstance().getPassword()));

		task.setParams(params);
		return task;
	}
	
	/**
	 * 获取通知团队列表
	 * 
	 * @return
	 */
	public static SoapTask getNotifyTeamListTask() {
		SoapTask task = new SoapTask(ZamboApplication.getInstance().getApplicationContext());
		task.setUrl(SoapService.SOAP_URL);
		task.setSoapNamespace(SoapService.SOAP_NAMESPACE);
		task.setSoapMethod(SoapService.GET_NOTICE_MESS);
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("arg0", "326"));
		params.add(new BasicNameValuePair("arg1", UserInfoManager.getInstance().getPhone()));
		params.add(new BasicNameValuePair("arg2", UserInfoManager.getInstance().getPassword()));
		task.setParams(params);
		return task;
	}
}

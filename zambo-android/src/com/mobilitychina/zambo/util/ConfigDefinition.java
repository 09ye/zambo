package com.mobilitychina.zambo.util;

public class ConfigDefinition {
//	public final static boolean isDebug = true;
	//升级地址
	public final static String URL_APP_UPDATE = "http://update1siemens.mobilitychina.com/test/update.xml";
	public final static String URL_APP_UPDATE2 = "http://update2siemens.mobilitychina.com/test/update.xml";
	public final static String URL_CRASH = "http://upload-siemens.mobilitychina.com:80";
	public final static String URL_LOG = "http://upload-siemens.mobilitychina.com:80";
	public final static String URL_PUSH="http://upload-siemens.mobilitychina.com:80";
	//主站地址
	public final static String URL_MAIN_OPENAPI = "http://s-h-mobi-p.mobilitychina.com:8080";
	//测试站地址
    public final static String URL_TEST_OPENAPI = "http://s-h-mobi-t.mobilitychina.com:8080";
//    public final static String URL_TEST_OPENAPI = "http://test.mobilitychina.com:80";
//    public final static String URL_TEST_OPENAPI = "http://202.91.240.88:8080";
  //public final static String URL_TEST_OPENAPI = "http://192.168.11.134:8080";

	//本地上线时间
	public final static String DATE_APP_DISTRIBUTE = "2013-06-03";
	//config持久化数据
	public final static String FILE_APP_UPDATE= "/data/data/com.mobilitychina.siemens/update.xml";
	
	
	
	public final static int TAB_WDKH = 0;
	public final static int TAB_WDJH = 1;
	public final static int TAB_WDBF = 2;
	public final static int TAB_WDZX = 3;
	public final static int TAB_WDJX = 4;
	
	
	
	public static final String PREFS_DATA = "zambodata";
	public static final String PREFS_LASTPHONE = "salesphone";
	public static final String PREFS_LASTPW = "salespasswd";
	public static final String PREFS_NAME = "salesname";
	public static final String PREFS_PEMPNAME = "pEmpName";
	public static final String PREFS_IMEI = "imei";
	public static final String PREFS_PSWSHOW = "pswShow";
	public static final String PREFS_PLAN_VISTDATE = "prefs_plan_vistdate";
	public static final String REPORT_MESS = "reportMess";
	public static final String PREFS_CHECKED = "-1";
//	Broadcast定义 
	public static final String INTENT_ACTION_GETCUSTOMERCOMPLETE = "getCustomerComplete";
	public static final String INTENT_ACTION_GETCUSTOMERREFRESH = "getCustomerRefresh";
	public static final String INTENT_ACTION_GETCUSTOMERTYPECOMPLETE = "getCustomerTypeComplete";
	public static final String INTENT_ACTION_GETPRODUCTCOMPLETE = "getProductComplete";
	public static final String INTENT_ACTION_DELETEPLANCOMPLETE= "deletePlanComplete"; //添加计划成功后广播
	public static final String INTENT_ACTION_ADDPLANCOMPLETE= "addPlanComplete"; //添加计划成功后广播
	public static final String INTENT_ACTION_SUBMITFLUP = "submitflup"; 
	public static final String INTENT_ACTION_SUBMITITEM = "submititem";
	public static final String INTENT_ACTION_SERVICE_SETUP = "servicesetup";//服务启动
	//public static final String INTENT_ACTION_SUBMITFLUP = "submitflup"; 
	public static final String INTENT_ACTION_TABCHANGE = "tabChange";
	public static final String ZL_MESS = "zl_mess";
	
	public static final String INTENT_ACTION_GETDEPARTMENTCOMPLETE = "getDepartmentComplete";
	public static final String INTENT_ACTION_GETJOBTITLECOMPLETE = "getJobTitleComplete";
	public static final String INTENT_ACTION_GETFACILITYCOMPLETE = "getFacilityComplete";
	public static final String INTENT_ACTION_GETNOTIFYTEAMCOMPLETE = "getNotifyTeamComplete";
	public static final String INTENT_ACTION_GETPRODUCTSTATUSCOMPLETE = "getProductStatusComplete";
}

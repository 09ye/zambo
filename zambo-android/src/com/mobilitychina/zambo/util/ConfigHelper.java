package com.mobilitychina.zambo.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import android.os.AsyncTask;
import android.util.Log;


public class ConfigHelper extends BaseHelper {
	//private final static String UPDATE_URL = ConfigDefinition.URL_APP_UPDATE;
	private final static ConfigHelper instance = new ConfigHelper();
	private ArrayList<String> updatexmlurlList;
	private Version minVersion;// 最小
	private Version newVersion;// 最新
	private String updateContent;// 升级提示
	private String updateDate;// 更新时间
	private ArrayList<String> updateapkUrllist;//apk省级包
	private Boolean isMaintenanceMode = false;
	private String pushNotice = "";
	private Boolean hasPushNotice = false;
	private int maxDistance = 2000;
	private int maxAccuracy = 500; 
	private int checkinCount = 3;//间隔次数
	private int checkinTime = 120;//间隔时间：分钟
	private boolean isValidateCheckin = true; //是否限制签到
	private boolean isPush = false;
	private ConfigState state = ConfigState.Done;
	public ConfigState getState()
	{
		return state;
		
	}
	public int getCheckInTime(){
		return checkinTime;
	}
	public int getCheckInCount(){
		return checkinCount;
	}
	public boolean isValidateCheckin(){
		return isValidateCheckin;
	}
	public boolean getIsPush(){
		return isPush;
	}
	public int getMaxAccuracy()
	{
		return maxAccuracy;
	}
	public int getMaxDistance()
	{
		return maxDistance;
	}
	//持久化数据文件
	private File fileUpdate = new File(ConfigDefinition.FILE_APP_UPDATE);

	public ConfigHelper() {
		//初始化
		this.initial();
		//加载本地数据
		this.readlocalData();
		//http请求
		
	}
	public void refresh(){
		state = ConfigState.Try;
		configHttpGet = new ConfigHttpGet();
		configHttpGet.execute();
	}
	private void readlocalData() {
		try {	
			if (fileUpdate.exists()) {
				FileInputStream inStream = new FileInputStream(fileUpdate);
				BufferedReader buffReader = new BufferedReader(
						new InputStreamReader(inStream));
				StringBuffer strBuff = new StringBuffer();
				String result = null;
				while ((result = buffReader.readLine()) != null) {
					strBuff.append(result);
				}
				String xml = strBuff.toString();
				buffReader.close();
				if(xml != null && xml.length()>0){
					doResponse(xml);
					
				}
			}else{
				fileUpdate.createNewFile();
			
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void initial() {
		minVersion = new Version("0.0.0");
		newVersion = new Version("0.0.0");
		updateDate = "2012-10-29";
		updatexmlurlList = new ArrayList<String>();
	    Random rnd = new Random();
	    int p = rnd.nextInt(2);
	   
	    if(p == 0){
	    	updatexmlurlList.add(ConfigDefinition.URL_APP_UPDATE);
			updatexmlurlList.add(ConfigDefinition.URL_APP_UPDATE2);
	    }else{
	    	updatexmlurlList.add(ConfigDefinition.URL_APP_UPDATE2);
			updatexmlurlList.add(ConfigDefinition.URL_APP_UPDATE);
	    }
		
		updateapkUrllist = new ArrayList<String>();
	}

	public ArrayList<String> getUpdateUrl() {
		return updateapkUrllist;
	}

	public String getUpdateContent() {
		return updateContent;
	}

	public Boolean getIsMaintenanceMode() {
		return isMaintenanceMode;
	}
	public Boolean getHasPushNotice() {
		return hasPushNotice;
	}
	public String getPushNotice() {
		return pushNotice;
	}
	public Version getMinVersion() {
		return minVersion;
	}
	public Version getNewVersion() {
		return newVersion;
	}

	public String getUpdateDate() {
		return updateDate;
	}

	public static ConfigHelper getInstance() {
		return instance;
	}

	private ConfigHttpGet configHttpGet = null;

	/**
	 * 后台线程
	 * 
	 * @author zywang
	 * 
	 */
	private class ConfigHttpGet extends AsyncTask<String, String, Object> {

		@Override
		protected Object doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			for (String string : updatexmlurlList) {
				if(state != ConfigState.Done){
					doGet(string);
				}else{
					break;
				}
			}
			ConfigHelper.this.onEventHandler();
			return null;
		}
	}

	/**
	 * 请求
	 * 
	 * @param url
	 */
	public void doGet(String url) {
		HttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, 30000);
		HttpConnectionParams.setSoTimeout(httpParams, 30000);
		HttpClient httpClient = new DefaultHttpClient(httpParams);
		// GET
		HttpGet httpGet = new HttpGet(url);
		try {
			HttpResponse response = httpClient.execute(httpGet);
			// 成功
			if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
				Log.i("GET", "Bad Request!");
				state = ConfigState.Fail;
			} else {
				HttpEntity entity = response.getEntity();
				BufferedReader buffReader = new BufferedReader(
						new InputStreamReader(entity.getContent()));
				StringBuffer strBuff = new StringBuffer();
				String result = null;
				while ((result = buffReader.readLine()) != null) {
					strBuff.append(result);
				}
				// 处理xml文件
				String xml = strBuff.toString();
				if(xml  != null && xml.length()>0){
					this.doResponse(xml);
					this.writeLocalData(xml);
					// 通知
					state = ConfigState.Done;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			state = ConfigState.Fail;
		}
	}
	/**
	 * 响应
	 * 
	 * @param xmlStr
	 */
	private void doResponse(String xmlStr) {
		if (xmlStr == null || xmlStr.length() == 0) {
			return;
		}
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document dom = builder.parse(new ByteArrayInputStream(
					xmlStr.getBytes()));
			Element root = dom.getDocumentElement();//
			NodeList personNodes;
			personNodes = root.getElementsByTagName("update");
			doUpdate(personNodes);// 处理升级模块
			personNodes = root.getElementsByTagName("config");
			doConfig(personNodes);// 处理配置表的更新；
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 写本地数据
	 * @param xml
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private void writeLocalData(String xml) throws FileNotFoundException,
			IOException {
		if(fileUpdate.exists()){
			FileOutputStream fos = new FileOutputStream(fileUpdate);
			fos.write(xml.getBytes());
			fos.flush();
			fos.close();
		}
	}
	
	/**
	 * 读取配置
	 */
	private void doConfig(NodeList xml) {
		if (xml.getLength() > 0) {
			Node nodeUpdate = xml.item(0);
			NodeList listChild = nodeUpdate.getChildNodes();
			for (int i = 0; i < listChild.getLength(); i++) {
				Node node = listChild.item(i);
				// 解析
				if (node.getNodeName().equalsIgnoreCase("maxdistance")) {
					maxDistance = Integer.parseInt(node.getTextContent().trim());
				} else if (node.getNodeName().equalsIgnoreCase("accuracy")) {
					maxAccuracy = Integer.parseInt(node.getTextContent().trim());
				} else if (node.getNodeName().equalsIgnoreCase("checkincount")) {
					checkinCount = Integer.parseInt(node.getTextContent().trim());
				} else if (node.getNodeName().equalsIgnoreCase("checkintime")) {
					checkinTime = Integer.parseInt(node.getTextContent().trim());
				} else if (node.getNodeName().equalsIgnoreCase("push")) {
					isPush = node.getTextContent().trim().equalsIgnoreCase("true")?true:false;
				} else if (node.getNodeName().equalsIgnoreCase("validateCheckin")) {
					isValidateCheckin = node.getTextContent().trim().equalsIgnoreCase("true")?true:false;
				}
			}
		}
	}
	/**
	 * 读升级信息
	 * @param xml
	 */
	private void doUpdate(NodeList xml) {
		if (xml.getLength() > 0) {
			Node nodeUpdate = xml.item(0);
			NodeList listChild = nodeUpdate.getChildNodes();
			for (int i = 0; i < listChild.getLength(); i++) {
				Node node = listChild.item(i);
				// 解析
				if (node.getNodeName().equalsIgnoreCase("newVersion")) {
					newVersion = new Version(node.getTextContent().trim());
				} else if (node.getNodeName().equalsIgnoreCase("minVersion")) {
					minVersion = new Version(node.getTextContent().trim());
				} else if (node.getNodeName().equalsIgnoreCase("content")) {
					updateContent = node.getTextContent().trim();
				} else if (node.getNodeName().equalsIgnoreCase("updateDate")) {
					updateDate = node.getTextContent().trim();
				} else if (node.getNodeName().equalsIgnoreCase("updateURL")) {
					NodeList updateUrls = node.getChildNodes();
					updateapkUrllist.clear();
					for (int j = 0; j < updateUrls.getLength(); j++) {
						Node url = updateUrls.item(j);
						if (url.getTextContent() != null
								&& url.getTextContent().trim().length() > 0) {
							updateapkUrllist.add(url.getTextContent().trim());
						}
					}
				} else if (node.getNodeName().equalsIgnoreCase("hasPushNotice")) {
					if( node.getTextContent().trim().equalsIgnoreCase("true")){
						this.hasPushNotice = true;
					}else{
						this.hasPushNotice = false;
					}
				} else if (node.getNodeName().equalsIgnoreCase("pushNotice")) {
					this.pushNotice = node.getTextContent().trim();
				} else if (node.getNodeName().equalsIgnoreCase("isMaintenanceMode")) {
					if( node.getTextContent().trim().equalsIgnoreCase("true")){
						this.isMaintenanceMode = true;
					}else{
						this.isMaintenanceMode = false;
					}				}
			}
			
		}
	}
}

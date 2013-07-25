package com.mobilitychina.zambo.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ksoap2.serialization.SoapObject;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.mobilitychina.intf.ITaskListener;
import com.mobilitychina.intf.Task;
import com.mobilitychina.net.SoapTask;
import com.mobilitychina.util.CacheType;
import com.mobilitychina.util.Log;
import com.mobilitychina.zambo.app.ZamboApplication;
import com.mobilitychina.zambo.business.record.data.ProjectInfo;
import com.mobilitychina.zambo.util.CommonUtil;
import com.mobilitychina.zambo.util.ConfigDefinition;

public class SiemensProductManager implements ITaskListener {
	public enum ProductLoadStatus {
		LOADING, SUCCESS, FAILED,
	};
	
	public static final String ALL_CATEGORY = "全部";
	//public static final String COMMEN_USE = "常用";
	private static SiemensProductManager mIntance;
	private Map<String, List<ProjectInfo>> mProductMap;
	private List<String> mCategoryList;
	private SoapTask getProductTask;

	private ProductLoadStatus status;
	
	private SiemensProductManager() {
		mCategoryList = new ArrayList<String>();
		mProductMap = new HashMap<String, List<ProjectInfo>>();
	}

	public static synchronized SiemensProductManager getInstance() {
		if (mIntance == null) {
			mIntance = new SiemensProductManager();
		}
		return mIntance;
	}

	public List<String> getProductCategoryList() {
		return mCategoryList;
	}

	public List<ProjectInfo> getProductListByCategory(String category) {
		return mProductMap.get(category);
	}
	
	public List<ProjectInfo> getprInfo(String category){
		return mProductMap.get(category);
		
	}
	
	public ProductLoadStatus getStatus() {
		return status;
	}
	

	public void start() {
		mCategoryList.clear();
		mProductMap.clear();
		
		if (getProductTask != null) {
			getProductTask.cancel(true);
			getProductTask = null;
		}
		getProductTask = SoapService.getSiemensProjectListTask();
		getProductTask.setCacheType(CacheType.NOTKEYBUSSINESS);
		getProductTask.setListener(this);
		getProductTask.start();
	}

	@Override
	public void onTaskFailed(Task task) {
		getProductTask = null;
		// 1秒后自动重试
		new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
			@Override
			public void run() {
				getProductTask = SoapService.getSiemensProjectListTask();
				getProductTask.setListener(SiemensProductManager.this);
			}
		}, 1000);
	}

	@Override
	public void onTaskFinished(Task task) {
		SoapObject result = (SoapObject) task.getResult();
		if (result == null) {
			// 1秒后自动重试
			new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
				@Override
				public void run() {
					getProductTask = SoapService.getSiemensProjectListTask();
					getProductTask.setListener(SiemensProductManager.this);
				}
			}, 1000);
			return;
		}
		
		int n = result.getPropertyCount();
		
		for (int i = 0; i < n; i++) {
			String element = result.getProperty(i).toString();
			Log.i("SiemensProductManager",element);
			String itemId = CommonUtil.getStringElement(element, "itemId");
			String itemName = CommonUtil.getStringElement(element, "itemName");

			//List<ProjectInfo> allProjectList = mProductMap.get(COMMEN_USE);
//			if (allProjectList == null) {
//				allProjectList = new ArrayList<ProjectInfo>();
//				mProductMap.put(COMMEN_USE, allProjectList);
//				mCategoryList.add(COMMEN_USE);
//			}
			ProjectInfo project = new ProjectInfo();
			project.setId(itemId);
			project.setName(itemName);
		//	allProjectList.add(project);

			String[] temp = itemName.split("/");
			if (temp.length > 1) {
				String category = temp[0];
				String projectName = itemName;
				
				List<ProjectInfo> projectList = mProductMap.get(category);
				if (projectList == null) {
					projectList = new ArrayList<ProjectInfo>();
					mProductMap.put(category, projectList);
					mCategoryList.add(category);
				}
				ProjectInfo projectInfo = new ProjectInfo();
				projectInfo.setId(itemId);
				projectInfo.setName(projectName);
				projectList.add(projectInfo);
			}
		}
		status = ProductLoadStatus.SUCCESS;
		Intent intent = new Intent();
		intent.setAction(ConfigDefinition.INTENT_ACTION_GETPRODUCTCOMPLETE);
		ZamboApplication.getInstance().getApplicationContext().sendBroadcast(intent);
		getProductTask = null;
	}

	@Override
	public void onTaskUpdateProgress(Task arg0, int arg1, int arg2) {
	}

	@Override
	public void onTaskTry(Task task) {
		// TODO Auto-generated method stub
		
	}
	public ProjectInfo getProjectById(String value) {
		if (TextUtils.isEmpty(value)) {
			return null;
		}
		for (String category : mCategoryList)
			for (ProjectInfo fl : mProductMap.get(category)) {
				if (value.equalsIgnoreCase(fl.getId())) {
					return fl;
				}
			}
		
		return null;
	}

}

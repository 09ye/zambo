package com.mobilitychina.zambo.business.facility.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.mobilitychina.intf.ITaskListener;
import com.mobilitychina.intf.Task;
import com.mobilitychina.net.SoapTask;
import com.mobilitychina.util.CacheType;
import com.mobilitychina.zambo.business.departments.data.Department;
import com.mobilitychina.zambo.service.SoapService;
import com.mobilitychina.zambo.service.resps.RespFactory;

public class FacilityManager implements ITaskListener {

	public enum LoadStatus {
		Loading, Success, Failed
	}

	public static final String TAG = "FacilityManager";
	public static final String ALL_CATEGORY = "全部";

	private static FacilityManager mInstance;
	private Map<String, List<Facility>> mFacilityMap;
	private List<String> mCategoryList;
	private SoapTask getFacilityTask;

	private LoadStatus status;

	private FacilityManager() {
		mCategoryList = new ArrayList<String>();
		mFacilityMap = new HashMap<String, List<Facility>>();
		
		//mockList();
	}

	public static synchronized FacilityManager getInstance() {
		if (mInstance == null) {
			mInstance = new FacilityManager();
		}
		return mInstance;
	}

	public List<String> getCategoryList() {
		return mCategoryList;
	}

	public List<Facility> getListByCategory(String category) {
		return mFacilityMap.get(category);
	}

	public Facility getFacilityById(String deptId) {
		if (TextUtils.isEmpty(deptId)) {
			return null;
		}
		for (String category : mCategoryList) {
			for (Facility Facility : getListByCategory(category)) {
				if (deptId.equalsIgnoreCase(Facility.getId())) {
					return Facility;
				}
			}
		}
		return null;
	}
	public Facility getFacilityByValue(String value) {
		if (TextUtils.isEmpty(value)) {
			return null;
		}
		for (String category : mCategoryList) {
			for (Facility Facility : getListByCategory(category)) {
				if (value.equalsIgnoreCase(Facility.getName())) {
					return Facility;
				}
			}
		}
		return null;
	}
	public String getFacilityByIds(String value) {
		StringBuilder sb = new StringBuilder();
		if (TextUtils.isEmpty(value)) {
			return null;
		}
		String []array = value.split(",");
		for (String string : array) {
			for (String category : mCategoryList) {
				for (Facility Facility : getListByCategory(category)) {
					if (string.equalsIgnoreCase(Facility.getId())) {
						sb.append(Facility.getName()+",");
						break;
					}
				}
			}
		}
		if(sb.length()>0){
			sb.deleteCharAt(sb.length()-1);
		}
		return sb.toString();
	}
	public LoadStatus getLoadStatus() {
		return status;
	}

	public void start() {
		if (getFacilityTask != null) {
			getFacilityTask.cancel(true);
			getFacilityTask = null;
		}

		 getFacilityTask = SoapService.getFacilityListTask();
		 getFacilityTask.setListener(this);
		 getFacilityTask.setCacheType(CacheType.NOTKEYBUSSINESS);
		 getFacilityTask.start();
		 status = LoadStatus.Loading;
	}

	@Override
	public void onTaskFinished(Task task) {
		getFacilityTask = null;
		status = LoadStatus.Success;
		mCategoryList.add("产品大类名称");
		List<Facility> list = new ArrayList<Facility>();
		list = RespFactory.getService().fromResp(Facility.class,
				task.getResult());
		mFacilityMap.put("产品大类名称", list);
	}

	@Override
	public void onTaskFailed(Task task) {
		getFacilityTask = null;
		status = LoadStatus.Failed;
		new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {

			@Override
			public void run() {
				start();
			}
		}, 1000);
	}

	@Override
	public void onTaskUpdateProgress(Task task, int count, int total) {

	}

	@Override
	public void onTaskTry(Task task) {

	}



}

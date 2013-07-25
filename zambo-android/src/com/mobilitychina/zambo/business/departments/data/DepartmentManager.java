package com.mobilitychina.zambo.business.departments.data;

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
import com.mobilitychina.zambo.business.plan.data.PlanInfo;
import com.mobilitychina.zambo.service.SoapService;
import com.mobilitychina.zambo.service.resps.RespFactory;

public class DepartmentManager implements ITaskListener {

	public enum LoadStatus {
		Loading, Success, Failed
	}

	public static final String TAG = "DepartmentManager";
	public static final String ALL_CATEGORY = "全部";

	private static DepartmentManager mInstance;
	private Map<String, List<Department>> mDepartmentMap;
	private List<String> mCategoryList;
	private SoapTask getDepartmentTask;

	private LoadStatus status;

	private DepartmentManager() {
		mCategoryList = new ArrayList<String>();
		mDepartmentMap = new HashMap<String, List<Department>>();
		
		//mockList();
	}

	public static synchronized DepartmentManager getInstance() {
		if (mInstance == null) {
			mInstance = new DepartmentManager();
		}
		return mInstance;
	}

	public List<String> getCategoryList() {
		return mCategoryList;
	}

	public List<Department> getListByCategory(String category) {
		return mDepartmentMap.get(category);
	}

	public Department getDepartmentById(String deptId) {
		if (TextUtils.isEmpty(deptId)) {
			return null;
		}
		for (String category : mCategoryList) {
			for (Department department : getListByCategory(category)) {
				if (deptId.equalsIgnoreCase(department.getId())) {
					return department;
				}
			}
		}
		return null;
	}

	public LoadStatus getLoadStatus() {
		return status;
	}

	public void start() {
		if (getDepartmentTask != null) {
			getDepartmentTask.cancel(true);
			getDepartmentTask = null;
		}

		 getDepartmentTask = SoapService.getDepartmentListTask();
		 getDepartmentTask.setListener(this);
		 getDepartmentTask.setCacheType(CacheType.NOTKEYBUSSINESS);
		 getDepartmentTask.start();
		 status = LoadStatus.Loading;
	}

	@Override
	public void onTaskFinished(Task task) {
		getDepartmentTask = null;
		mCategoryList.add("科室名称");
		List<Department> list = new ArrayList<Department>();
		list = RespFactory.getService().fromResp(Department.class,
				task.getResult());
		mDepartmentMap.put("科室名称", list);
		status = LoadStatus.Success;
	}

	@Override
	public void onTaskFailed(Task task) {
		getDepartmentTask = null;
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

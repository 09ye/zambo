package com.mobilitychina.zambo.business.jobtitle.data;

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

public class JobTitleManager implements ITaskListener {

	public enum LoadStatus {
		Loading, Success, Failed
	}

	public static final String TAG = "JobTitleManager";
	public static final String ALL_CATEGORY = "全部";

	private static JobTitleManager mInstance;
	private Map<String, List<JobTitle>> mJobTitleMap;
	private List<String> mCategoryList;
	private SoapTask getJobTitleTask;

	private LoadStatus status;

	private JobTitleManager() {
		mCategoryList = new ArrayList<String>();
		mJobTitleMap = new HashMap<String, List<JobTitle>>();
		
		//mockList();
	}

	public static synchronized JobTitleManager getInstance() {
		if (mInstance == null) {
			mInstance = new JobTitleManager();
		}
		return mInstance;
	}

	public List<String> getCategoryList() {
		return mCategoryList;
	}

	public List<JobTitle> getListByCategory(String category) {
		return mJobTitleMap.get(category);
	}

	public JobTitle getJobTitleById(String jobid) {
		if (TextUtils.isEmpty(jobid)) {
			return null;
		}
		for (String category : mCategoryList) {
			for (JobTitle JobTitle : getListByCategory(category)) {
				if (jobid.equalsIgnoreCase(JobTitle.getId())) {
					return JobTitle;
				}
			}
		}
		return null;
	}

	public LoadStatus getLoadStatus() {
		return status;
	}

	public void start() {
		if (getJobTitleTask != null) {
			getJobTitleTask.cancel(true);
			getJobTitleTask = null;
		}

		 getJobTitleTask = SoapService.getJobTitleListTask();
		 getJobTitleTask.setCacheType(CacheType.NOTKEYBUSSINESS);
		 getJobTitleTask.setListener(this);
		 getJobTitleTask.start();
		 status = LoadStatus.Loading;
	}

	@Override
	public void onTaskFinished(Task task) {
		getJobTitleTask = null;
		status = LoadStatus.Success;
		mCategoryList.add("职位名称");
		List<JobTitle> list = new ArrayList<JobTitle>();
		list = RespFactory.getService().fromResp(JobTitle.class,
				task.getResult());
		mJobTitleMap.put("职位名称", list);
	}
	
	@Override
	public void onTaskFailed(Task task) {
		getJobTitleTask = null;
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

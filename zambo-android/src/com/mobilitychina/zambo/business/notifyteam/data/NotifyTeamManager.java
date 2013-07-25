package com.mobilitychina.zambo.business.notifyteam.data;

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
import com.mobilitychina.zambo.business.facility.data.Facility;
import com.mobilitychina.zambo.service.SoapService;
import com.mobilitychina.zambo.service.resps.RespFactory;

public class NotifyTeamManager implements ITaskListener {

	public enum LoadStatus {
		Loading, Success, Failed
	}

	public static final String TAG = "NotifyTeamManager";
	public static final String ALL_CATEGORY = "全部";

	private static NotifyTeamManager mInstance;
	private Map<String, List<NotifyTeam>> mNotifyTeamMap;
	private List<String> mCategoryList;
	private SoapTask getNotifyTeamTask;

	private LoadStatus status;

	private NotifyTeamManager() {
		mCategoryList = new ArrayList<String>();
		mNotifyTeamMap = new HashMap<String, List<NotifyTeam>>();
		
		//mockList();
	}

	public static synchronized NotifyTeamManager getInstance() {
		if (mInstance == null) {
			mInstance = new NotifyTeamManager();
		}
		return mInstance;
	}

	public List<String> getCategoryList() {
		return mCategoryList;
	}

	public List<NotifyTeam> getListByCategory(String category) {
		return mNotifyTeamMap.get(category);
	}

	public NotifyTeam getNotifyTeamById(String notifyid) {
		if (TextUtils.isEmpty(notifyid)) {
			return null;
		}
		for (String category : mCategoryList) {
			for (NotifyTeam NotifyTeam : getListByCategory(category)) {
				if (notifyid.equalsIgnoreCase(NotifyTeam.getId())) {
					return NotifyTeam;
				}
			}
		}
		return null;
	}

	public LoadStatus getLoadStatus() {
		return status;
	}

	public void start() {
		if (getNotifyTeamTask != null) {
			getNotifyTeamTask.cancel(true);
			getNotifyTeamTask = null;
		}

		 getNotifyTeamTask = SoapService.getNotifyTeamListTask();
		 getNotifyTeamTask.setListener(this);
		 getNotifyTeamTask.setCacheType(CacheType.NOTKEYBUSSINESS);
		 getNotifyTeamTask.start();
		 status = LoadStatus.Loading;
	}

	@Override
	public void onTaskFinished(Task task) {
		getNotifyTeamTask = null;
		status = LoadStatus.Success;
		mCategoryList.add("通知内容");
		List<NotifyTeam> list = new ArrayList<NotifyTeam>();
		list = RespFactory.getService().fromResp(NotifyTeam.class,
				task.getResult());
		mNotifyTeamMap.put("通知内容", list);
	}

	@Override
	public void onTaskFailed(Task task) {
		getNotifyTeamTask = null;
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

	// ===mock
//	private void mockList() {
//		mCategoryList.add("通知内容");
//		List<NotifyTeam> list = new ArrayList<NotifyTeam>();
//
//		NotifyTeam NotifyTeam1 = new NotifyTeam();
//		NotifyTeam1.setId("notify1");
//		NotifyTeam1.setName("新建大楼");
//		list.add(NotifyTeam1);
//
//		NotifyTeam NotifyTeam2 = new NotifyTeam();
//		NotifyTeam2.setId("notify2");
//		NotifyTeam2.setName("新建实验楼");
//		list.add(NotifyTeam2);
//
//		NotifyTeam NotifyTeam3 = new NotifyTeam();
//		NotifyTeam3.setId("notify3");
//		NotifyTeam3.setName("设备更换");
//		list.add(NotifyTeam3);
//
//		mNotifyTeamMap.put("通知内容", list);
//		status = LoadStatus.Success;
//	}

}

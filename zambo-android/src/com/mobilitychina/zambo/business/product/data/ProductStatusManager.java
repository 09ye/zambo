package com.mobilitychina.zambo.business.product.data;

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
import com.mobilitychina.zambo.business.jobtitle.data.JobTitle;
import com.mobilitychina.zambo.service.SoapService;
import com.mobilitychina.zambo.service.resps.RespFactory;

public class ProductStatusManager implements ITaskListener {

	public enum LoadStatus {
		Loading, Success, Failed
	}

	public static final String TAG = "ProductStatusManager";
	public static final String ALL_CATEGORY = "全部";

	private static ProductStatusManager mInstance;
	private Map<String, List<ProductStatus>> mProductStatusMap;
	private List<String> mCategoryList;
	private SoapTask getProductStatusTask;

	private LoadStatus status;

	private ProductStatusManager() {
		mCategoryList = new ArrayList<String>();
		mProductStatusMap = new HashMap<String, List<ProductStatus>>();
	}

	public static synchronized ProductStatusManager getInstance() {
		if (mInstance == null) {
			mInstance = new ProductStatusManager();
		}
		return mInstance;
	}

	public List<String> getCategoryList() {
		return mCategoryList;
	}

	public List<ProductStatus> getListByCategory(String category) {
		return mProductStatusMap.get(category);
	}

	public ProductStatus getProductStatusById(String notifyid) {
		if (TextUtils.isEmpty(notifyid)) {
			return null;
		}
		for (String category : mCategoryList) {
			for (ProductStatus ProductStatus : getListByCategory(category)) {
				if (notifyid.equalsIgnoreCase(ProductStatus.getId())) {
					return ProductStatus;
				}
			}
		}
		return null;
	}

	public LoadStatus getLoadStatus() {
		return status;
	}

	public void start() {
		if (getProductStatusTask != null) {
			getProductStatusTask.cancel(true);
			getProductStatusTask = null;
		}
		 getProductStatusTask = SoapService.getSiemensProjectStatusTask();
		 getProductStatusTask.setListener(this);
		 getProductStatusTask.start();
		 status = LoadStatus.Loading;
	}

	@Override
	public void onTaskFinished(Task task) {
		getProductStatusTask = null;
		status = LoadStatus.Success;
		mCategoryList.add("项目状态");
		List<ProductStatus> list = new ArrayList<ProductStatus>();
		list = RespFactory.getService().fromResp(ProductStatus.class,
				task.getResult());
		mProductStatusMap.put("项目状态", list);
	}

	@Override
	public void onTaskFailed(Task task) {
		getProductStatusTask = null;
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
//		mCategoryList.add("项目状态");
//		List<ProductStatus> list = new ArrayList<ProductStatus>();
//
//		ProductStatus ProductStatus1 = new ProductStatus();
//		ProductStatus1.setId("notify1");
//		ProductStatus1.setName("项目启动");
//		list.add(ProductStatus1);
//
//		ProductStatus ProductStatus2 = new ProductStatus();
//		ProductStatus2.setId("notify2");
//		ProductStatus2.setName("考察和产品介绍");
//		list.add(ProductStatus2);
//
//		ProductStatus ProductStatus3 = new ProductStatus();
//		ProductStatus3.setId("notify3");
//		ProductStatus3.setName("合同部署");
//		list.add(ProductStatus3);
//		
//		ProductStatus ProductStatus4 = new ProductStatus();
//		ProductStatus4.setId("notify4");
//		ProductStatus4.setName("项目结束");
//		list.add(ProductStatus4);
//
//		mProductStatusMap.put("项目状态", list);
//		status = LoadStatus.Success;
//	}

}

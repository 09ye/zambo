package com.mobilitychina.zambo.business.record;

import java.util.ArrayList;
import java.util.List;

import org.ksoap2.serialization.SoapObject;

import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.autonavi.mapapi.PoiSearch;

import com.mobilitychina.intf.ITaskListener;
import com.mobilitychina.intf.Task;
import com.mobilitychina.net.SoapTask;
import com.mobilitychina.util.Log;
import com.mobilitychina.zambo.R;
import com.mobilitychina.zambo.business.record.data.FollowupInfo;
import com.mobilitychina.zambo.service.SoapService;
import com.mobilitychina.zambo.util.CommonUtil;
import com.mobilitychina.zambo.util.Statistics;
import com.mobilitychina.zambo.widget.FollowupListItem;

/**
 * 客户随访记录
 * 
 * @author chenwang
 * 
 */
public class FollowupListFragment extends ListFragment implements ITaskListener {
	private List<FollowupInfo> mFollowupList;
	private String customerId;
	private SoapTask mTask;
	private Adapter mAdapter;
	private boolean isError;

	public void setCustomerId(String id) {
		this.customerId = id;
	}
	public void requestRecordList() {
		if (mTask != null) {
			mTask.cancel(true);
			mTask = null;
		}
		isError = false;
		mTask = SoapService.getFollowupListTask(this.customerId);
		mTask.setListener(this);
		mTask.start();
	}

	public void refresh() {
		mFollowupList.clear();
		if (mAdapter != null) {
			mAdapter.notifyDataSetChanged();
		}
		this.requestRecordList();
	}

	@Override
	public void onTaskFailed(Task task) {
		mTask = null;
		isError = true;
		if (mAdapter != null) {
			mAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onTaskFinished(Task task) {
		if (task == mTask) {
			SoapObject result = (SoapObject) task.getResult();
			List<FollowupInfo> followupInfoList = this.parse(result);
			if (followupInfoList != null) {
				mFollowupList.addAll(followupInfoList);
			}
			mTask = null;
			isError = false;
			if (mAdapter != null) {
				mAdapter.notifyDataSetChanged();
			}
		}
	}

	private List<FollowupInfo> parse(SoapObject result) {
		if (result == null) {
			return null;
		}
		int n = result.getPropertyCount();
		List<FollowupInfo> followupList = new ArrayList<FollowupInfo>(n);
		for (int i = 0; i < n; i++) {
			FollowupInfo followup = new FollowupInfo();
			String element = result.getProperty(i).toString();
			Log.i("FollowupListFragment",element);
			followup.setRequireContent(CommonUtil.getStringElement(element, "require_content"));
			followup.setIfSend(CommonUtil.getStringElement(element, "if_send"));
			followup.setProductdalei(CommonUtil.getStringElement(element, "productdalei_all"));
			followup.setKzr(CommonUtil.getStringElement(element, "kzr_all"));
			followup.setId(CommonUtil.getStringElement(element, "id"));
			followup.setDatelineId(CommonUtil.getStringElement(element, "datelineId"));
			followup.setType(CommonUtil.getStringElement(element, "type"));
			followup.setVisitDate(CommonUtil.getStringElement(element, "visitDate"));
			followup.setRemark(CommonUtil.getStringElement(element, "remark"));
			followupList.add(followup);
		}
		return followupList;
	}

	@Override
	public void onTaskUpdateProgress(Task task, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_simple_list, container, false);
		mFollowupList = new ArrayList<FollowupInfo>();
		requestRecordList();
		mAdapter = new Adapter();
		this.setListAdapter(mAdapter);
		
		return view;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mTask != null) {
			mTask.cancel(true);
			mTask = null;
		}
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		if (mFollowupList.size() > position) {
			FollowupInfo followupInfo = mFollowupList.get(position);
			if (followupInfo != null) {
				Intent intent = new Intent(this.getActivity(), FollowupDetailActivity.class);
				intent.putExtra("followupId", followupInfo.getId());
				intent.putExtra("remark", followupInfo.getRemark());
				intent.putExtra("kzr_all", followupInfo.getKzr());
				intent.putExtra("productdalei_all", followupInfo.getProductdalei());
				intent.putExtra("if_send", followupInfo.getIfSend());
				intent.putExtra("require_content", followupInfo.getRequireContent());
				
				Statistics.sendEvent("followup" , "followup_itemclick","",(long) 0);
				//System.out.println("具体随访内容查看-------》》");
				
				this.getActivity().startActivity(intent);
			}
		}
	}

	class Adapter extends BaseAdapter {

		@Override
		public int getCount() {
			if (mFollowupList.size() == 0) {
				return 1;
			} else {
				return (mFollowupList.size());
			}
		}

		@Override
		public FollowupInfo getItem(int position) {
			
			if (mFollowupList.size() == 0) {
				return null;
			} else {
				return mFollowupList.get(position);
			}
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (mFollowupList.size() == 0) {
				Context context = FollowupListFragment.this.getActivity();
				if (isError) {
					return CommonUtil.buildListRetryItemView(context, convertView, "网络错误，请稍后重试", new OnClickListener() {
						@Override
						public void onClick(View v) {
							FollowupListFragment.this.refresh();
						}
					});
				}
				if (mTask == null) {
					return CommonUtil.buildListSimpleMsgItemView(context, convertView, "暂无数据");
				} else {
					return CommonUtil.buildListLoadingItemView(context, convertView, null);
				}
			}
			FollowupListItem view;
			if (convertView == null || !(convertView instanceof FollowupListItem)) {
				view = (FollowupListItem) LayoutInflater.from(FollowupListFragment.this.getActivity()).inflate(
						R.layout.item_followup_list, null);
			} else {
				view = (FollowupListItem) convertView;
			}
			view.show(this.getItem(position));
			return view;
		}

	}

	@Override
	public void onTaskTry(Task task) {
		// TODO Auto-generated method stub
		
	}
}

package com.mobilitychina.zambo.business.facility;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.mobilitychina.zambo.R;
import com.mobilitychina.zambo.app.BaseDetailListActivity;
import com.mobilitychina.zambo.business.facility.data.Facility;
import com.mobilitychina.zambo.business.facility.data.FacilityManager;
import com.mobilitychina.zambo.business.facility.data.FacilityManager.LoadStatus;
import com.mobilitychina.zambo.business.record.FollowupDetailActivity;
import com.mobilitychina.zambo.util.ConfigDefinition;
import com.mobilitychina.zambo.widget.CommonListItem;
import com.mobilitychina.zambo.widget.CustomPopupWindow;
import com.mobilitychina.zambo.widget.SectionListAdapter;

public class FacilityListActivity extends BaseDetailListActivity {

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			dismissDialog();
			updateList(FacilityManager.ALL_CATEGORY);
		}
	};;

	private Facility selectFacility;
	private SectionListAdapter mSectionListAdapter;

	private Button btnCategory;

	private final String SEARCH_RESULT_TYPE = "搜索结果";
	private EditText etProjectSearch;
	private ImageButton btnClear;
	
	private ArrayList<Facility> facilities = new ArrayList<Facility>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_project_list);
		String selectedValue = this.getIntent().getStringExtra("ids");
		if(selectedValue!= null && selectedValue.length() > 0){
			String [] values =  selectedValue.split("&~");
			for (String string : values) {
				Facility fl = FacilityManager.getInstance().getFacilityById(string);
				if(fl!=null){
					facilities.add(fl);
					selectFacility = fl;
				}
			}
		}
		this.setTitle("选择产品大类");
		this.getTitlebar().setRightButton("确定", new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (facilities.size() != 0) {
					Intent intent = new Intent(FacilityListActivity.this, FollowupDetailActivity.class);
					intent.putExtra("ids", getSelectedFacilityIds());
					intent.putExtra("names", getSelectedFacilityNames());
					setResult(0, intent);
					finish();
				} else {
					showDialog("提示", "请先选择一个产品大类", null);
				}
			}
		});

		btnCategory = (Button) this.findViewById(R.id.btnCatetory);
		btnCategory.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				List<String> categoryList = FacilityManager.getInstance().getCategoryList();
				if (categoryList == null || categoryList.size() == 0) {
					return;
				}
				List<String> data = new ArrayList<String>();
				data.add(FacilityManager.ALL_CATEGORY);
				for (String category : categoryList) {
					data.add(category);
				}
				CustomPopupWindow popup = new CustomPopupWindow(FacilityListActivity.this, data);
				popup.setOnItemClickListener(new CustomPopupWindow.OnItemClickListener() {
					@Override
					public void onItemClick(int position, String data) {
						selectFacility = null;
						facilities.clear();
						btnCategory.setText(data);
						String keyword = etProjectSearch.getText().toString();
						if (keyword.length() > 0) {
							updateSearchList(keyword);
						} else {
							updateList(data);
						}
					}
				});
				popup.show(btnCategory, 0, 0);
			}
		});

		etProjectSearch = (EditText) findViewById(R.id.etProjectSearch);
		etProjectSearch.setEnabled(false);
		etProjectSearch.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				String keyword = etProjectSearch.getText().toString();
				if (keyword.length() == 0) {
					btnClear.setVisibility(View.GONE);
					updateList(btnCategory.getText().toString());
				} else {
					btnClear.setVisibility(View.VISIBLE);
					updateSearchList(keyword);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});

		btnClear = (ImageButton) findViewById(R.id.btnClear);
		btnClear.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				etProjectSearch.setText(null);
				updateList(btnCategory.getText().toString());
			}
		});

		registerReceiver();

		if (FacilityManager.getInstance().getLoadStatus() == LoadStatus.Loading) {
			this.showProgressDialog("正在加载...");
		} else {
			this.updateList(FacilityManager.ALL_CATEGORY);
		}
	}

	private void registerReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(ConfigDefinition.INTENT_ACTION_GETFACILITYCOMPLETE);
		this.registerReceiver(mReceiver, filter);
	}

	private void updateList(String aCategory) {
		int totalCount = 0;
		List<String> categoryList = FacilityManager.getInstance().getCategoryList();
		for (String category : categoryList) {
			if (aCategory.equalsIgnoreCase(FacilityManager.ALL_CATEGORY) || category.equalsIgnoreCase(aCategory)) {
				List<Facility> projectList = FacilityManager.getInstance().getListByCategory(category);
				totalCount += projectList.size();
			}
		}
		if (totalCount > 0) {
			etProjectSearch.setHint("搜索" + totalCount + "个项目");
		} else {
			etProjectSearch.setHint("搜索项目");
		}
		etProjectSearch.setEnabled(true);
		mSectionListAdapter = new SectionListAdapter(this);
		for (String category : categoryList) {
			if (aCategory.equalsIgnoreCase(FacilityManager.ALL_CATEGORY) || category.equalsIgnoreCase(aCategory)) {
				List<Facility> projectList = FacilityManager.getInstance().getListByCategory(category);
				ProjectListAdapter listAdapter = new ProjectListAdapter();
				listAdapter.setList(projectList);
				mSectionListAdapter.addSection(category, listAdapter);
			}
		}

		this.setListAdapter(mSectionListAdapter);
		this.mSectionListAdapter.notifyDataSetChanged();
	}

	private void updateSearchList(String keyword) {
		String proType = btnCategory.getText().toString();

		List<Facility> searchProjectList = new ArrayList<Facility>();

		List<String> categoryList = FacilityManager.getInstance().getCategoryList();
		for (String category : categoryList) {
			if (proType.equalsIgnoreCase(FacilityManager.ALL_CATEGORY) || category.equalsIgnoreCase(proType)) {
				List<Facility> projectList = FacilityManager.getInstance().getListByCategory(category);
				for (Facility Facility : projectList) {
					if (Facility.getName().contains(keyword.trim())) {
						searchProjectList.add(Facility);
						break;
					}
				}
			}
		}

		mSectionListAdapter = new SectionListAdapter(this);
		ProjectListAdapter listAdapter = new ProjectListAdapter();
		listAdapter.setList(searchProjectList);
		mSectionListAdapter.addSection(SEARCH_RESULT_TYPE + "(" + searchProjectList.size() + ")", listAdapter);
		this.setListAdapter(mSectionListAdapter);
		this.mSectionListAdapter.notifyDataSetChanged();
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
			if(this.mSectionListAdapter.getItem(position) instanceof Facility){
				selectFacility = (Facility) this.mSectionListAdapter.getItem(position);
				if(facilities.contains(selectFacility)){
					facilities.remove(selectFacility);
				}else{
					facilities.add(selectFacility);
				}
				this.mSectionListAdapter.notifyDataSetChanged();
		}
	}
	
	private boolean isSelected(Facility facility){
		return facilities.contains(facility);
	}
	
	private String getSelectedFacilityNames(){
		StringBuffer buffer = new StringBuffer();
		for(Facility facility : facilities){
			buffer.append(facility.getName()).append("&~");
		}
		if(buffer.length()>0){
			buffer.delete(buffer.length()-2,buffer.length());
		}
		return buffer.toString();
	}
	
	private String getSelectedFacilityIds(){
		StringBuffer buffer = new StringBuffer();
		for(Facility facility : facilities){
			buffer.append(facility.getId()).append("&~");
		}
		if(buffer.length()>0){
			buffer.delete(buffer.length()-2,buffer.length());
		}
		return buffer.toString();
	}

	public void onDestroy() {
		super.onDestroy();
		if (mReceiver != null) {
			this.unregisterReceiver(mReceiver);
		}
	}

	public class ProjectListAdapter extends BaseAdapter {
		private List<Facility> mProjectList;

		public void setList(List<Facility> projectList) {
			mProjectList = projectList;
		}

		@Override
		public int getCount() {
			return mProjectList.size();
		}

		@Override
		public Facility getItem(int position) {
			return mProjectList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			CommonListItem view;
			if (convertView == null) {
				view = (CommonListItem) LayoutInflater.from(FacilityListActivity.this).inflate(
						R.layout.common_list_item, null);
				view.setMultiSelect();
			} else {
				view = (CommonListItem) convertView;
			}
			boolean isSelected = isSelected(this.getItem(position));
			view.show(this.getItem(position).getName(), isSelected);
			view.showSeperator(position < this.getCount() - 1);
			return view;
		}

	}
}

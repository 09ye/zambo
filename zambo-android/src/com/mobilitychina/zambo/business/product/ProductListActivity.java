package com.mobilitychina.zambo.business.product;

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
import com.mobilitychina.zambo.business.record.ProjectDetailActivity;
import com.mobilitychina.zambo.business.record.data.ProjectInfo;
import com.mobilitychina.zambo.service.SiemensProductManager;
import com.mobilitychina.zambo.service.SiemensProductManager.ProductLoadStatus;
import com.mobilitychina.zambo.util.ConfigDefinition;
import com.mobilitychina.zambo.widget.CustomPopupWindow;
import com.mobilitychina.zambo.widget.ProductListItem;
import com.mobilitychina.zambo.widget.SectionListAdapter;

/**
 * 项目列表
 * 
 * @author chenwang
 * 
 */
public class ProductListActivity extends BaseDetailListActivity {

	private BroadcastReceiver mReceiver;

	private ProjectInfo selectedProjectInfo;
	private SectionListAdapter mSectionListAdapter;

	private Button btnCategory;

	private final String SEARCH_RESULT_TYPE = "搜索结果";
	private EditText etProjectSearch;
	private ImageButton btnClear;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_project_list);
		if(this.getIntent() != null){
		String projectid = this.getIntent().getStringExtra("projectId");
			if(projectid != null){
				selectedProjectInfo = SiemensProductManager.getInstance().getProjectById(projectid);
			}
		}
		this.setTitle("选择项目");
		this.getTitlebar().setRightButton("确定", new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (selectedProjectInfo != null) {
					Intent intent = new Intent(ProductListActivity.this, ProjectDetailActivity.class);
					intent.putExtra("projectName", selectedProjectInfo.getName());
					intent.putExtra("projectId", selectedProjectInfo.getId());
					setResult(0, intent);
					finish();
				} else {
					showDialog("提示", "请先选择一个项目", null);
				}
			}
		});

		btnCategory = (Button) this.findViewById(R.id.btnCatetory);
		btnCategory.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				List<String> categoryList = SiemensProductManager.getInstance().getProductCategoryList();
				if (categoryList == null || categoryList.size() == 0) {
					return;
				}
				List<String> data = new ArrayList<String>();
				data.add(SiemensProductManager.ALL_CATEGORY);
				for (String category : categoryList) {
					data.add(category);
				}
				CustomPopupWindow popup = new CustomPopupWindow(ProductListActivity.this, data);
				popup.setOnItemClickListener(new CustomPopupWindow.OnItemClickListener() {
					@Override
					public void onItemClick(int position, String data) {
						selectedProjectInfo = null;
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

		btnClear = (ImageButton)findViewById(R.id.btnClear);
		btnClear.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				etProjectSearch.setText(null);
				updateList(btnCategory.getText().toString());
			}
		});
		if (SiemensProductManager.getInstance().getStatus() == ProductLoadStatus.LOADING) {
			this.showProgressDialog("正在加载...");
			if (mReceiver == null) {
				mReceiver = new BroadcastReceiver() {
					@Override
					public void onReceive(Context context, Intent intent) {
						dismissDialog();
						updateList(SiemensProductManager.ALL_CATEGORY);
					}
				};
				IntentFilter filter = new IntentFilter();
				filter.addAction(ConfigDefinition.INTENT_ACTION_GETCUSTOMERCOMPLETE);
				this.registerReceiver(mReceiver, filter);
			}
		} else {
			this.updateList(SiemensProductManager.ALL_CATEGORY);
		}
	}

	private void updateList(String aCategory) {
		int totalCount = 0;
		List<String> categoryList = SiemensProductManager.getInstance().getProductCategoryList();
		for (String category : categoryList) {
			if (aCategory.equalsIgnoreCase(SiemensProductManager.ALL_CATEGORY) || category.equalsIgnoreCase(aCategory)) {
				List<ProjectInfo> projectList = SiemensProductManager.getInstance().getProductListByCategory(category);
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
			if (aCategory.equalsIgnoreCase(SiemensProductManager.ALL_CATEGORY) || category.equalsIgnoreCase(aCategory)) {
				List<ProjectInfo> projectList = SiemensProductManager.getInstance().getProductListByCategory(category);
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
		
		List<ProjectInfo> searchProjectList = new ArrayList<ProjectInfo>();
		
		List<String> categoryList = SiemensProductManager.getInstance().getProductCategoryList();
		for (String category : categoryList) {
			if (proType.equalsIgnoreCase(SiemensProductManager.ALL_CATEGORY) || category.equalsIgnoreCase(proType)) {
				List<ProjectInfo> projectList = SiemensProductManager.getInstance().getProductListByCategory(category);
				for (ProjectInfo projectInfo : projectList) {
					if (projectInfo.getName().contains(keyword.trim())) {
						searchProjectList.add(projectInfo);
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
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);

		selectedProjectInfo = (ProjectInfo) this.mSectionListAdapter.getItem(position);
		this.mSectionListAdapter.notifyDataSetChanged();
	}

	public void onDestroy() {
		super.onDestroy();
		if (mReceiver != null) {
			this.unregisterReceiver(mReceiver);
		}
	}

	public class ProjectListAdapter extends BaseAdapter {
		private List<ProjectInfo> mProjectList;

		public void setList(List<ProjectInfo> projectList) {
			mProjectList = projectList;
		}

		@Override
		public int getCount() {
			return mProjectList.size();
		}

		@Override
		public ProjectInfo getItem(int position) {
			return mProjectList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ProductListItem view;
			if (convertView == null) {
				view = (ProductListItem) LayoutInflater.from(ProductListActivity.this).inflate(
						R.layout.item_product_list, null);
			} else {
				view = (ProductListItem) convertView;
			}
			boolean isSelected = (selectedProjectInfo != null && this.getItem(position).getId() == selectedProjectInfo
					.getId());
			view.show(this.getItem(position), isSelected);
			view.showSeperator(position < this.getCount() - 1);
			return view;
		}

	}
}

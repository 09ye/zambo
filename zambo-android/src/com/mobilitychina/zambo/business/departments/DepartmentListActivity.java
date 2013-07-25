package com.mobilitychina.zambo.business.departments;

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
import com.mobilitychina.zambo.business.departments.data.Department;
import com.mobilitychina.zambo.business.departments.data.DepartmentManager;
import com.mobilitychina.zambo.business.departments.data.DepartmentManager.LoadStatus;
import com.mobilitychina.zambo.business.record.FollowupDetailActivity;
import com.mobilitychina.zambo.util.ConfigDefinition;
import com.mobilitychina.zambo.widget.CommonListItem;
import com.mobilitychina.zambo.widget.CustomPopupWindow;
import com.mobilitychina.zambo.widget.SectionListAdapter;

public class DepartmentListActivity extends BaseDetailListActivity {

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			dismissDialog();
			updateList(DepartmentManager.ALL_CATEGORY);
		}
	};;

	private Department selectDept;
	private SectionListAdapter mSectionListAdapter;

	private Button btnCategory;

	private final String SEARCH_RESULT_TYPE = "搜索结果";
	private EditText etProjectSearch;
	private ImageButton btnClear;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_project_list);

		this.setTitle("选择科室");
		String  deptid = this.getIntent().getStringExtra("deptid");
		if(deptid != null && deptid.length()>0){
			selectDept = DepartmentManager.getInstance().getDepartmentById(deptid);
		}
		this.getTitlebar().setRightButton("确定", new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (selectDept != null) {
					Intent intent = new Intent(DepartmentListActivity.this, FollowupDetailActivity.class);
					intent.putExtra("deptid", selectDept.getId());
					setResult(0, intent);
					finish();
				} else {
					showDialog("提示", "请先选择一个科室", null);
				}
			}
		});

		btnCategory = (Button) this.findViewById(R.id.btnCatetory);
		btnCategory.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				List<String> categoryList = DepartmentManager.getInstance().getCategoryList();
				if (categoryList == null || categoryList.size() == 0) {
					return;
				}
				List<String> data = new ArrayList<String>();
				data.add(DepartmentManager.ALL_CATEGORY);
				for (String category : categoryList) {
					data.add(category);
				}
				CustomPopupWindow popup = new CustomPopupWindow(DepartmentListActivity.this, data);
				popup.setOnItemClickListener(new CustomPopupWindow.OnItemClickListener() {
					@Override
					public void onItemClick(int position, String data) {
						selectDept = null;
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

		if (DepartmentManager.getInstance().getLoadStatus() == LoadStatus.Loading) {
			this.showProgressDialog("正在加载...");
		} else {
			this.updateList(DepartmentManager.ALL_CATEGORY);
		}
	}

	private void registerReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(ConfigDefinition.INTENT_ACTION_GETDEPARTMENTCOMPLETE);
		this.registerReceiver(mReceiver, filter);
	}

	private void updateList(String aCategory) {
		int totalCount = 0;
		List<String> categoryList = DepartmentManager.getInstance().getCategoryList();
		for (String category : categoryList) {
			if (aCategory.equalsIgnoreCase(DepartmentManager.ALL_CATEGORY) || category.equalsIgnoreCase(aCategory)) {
				List<Department> projectList = DepartmentManager.getInstance().getListByCategory(category);
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
			if (aCategory.equalsIgnoreCase(DepartmentManager.ALL_CATEGORY) || category.equalsIgnoreCase(aCategory)) {
				List<Department> projectList = DepartmentManager.getInstance().getListByCategory(category);
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

		List<Department> searchProjectList = new ArrayList<Department>();

		List<String> categoryList = DepartmentManager.getInstance().getCategoryList();
		for (String category : categoryList) {
			if (proType.equalsIgnoreCase(DepartmentManager.ALL_CATEGORY) || category.equalsIgnoreCase(proType)) {
				List<Department> projectList = DepartmentManager.getInstance().getListByCategory(category);
				for (Department Department : projectList) {
					if (Department.getName().contains(keyword.trim())) {
						searchProjectList.add(Department);
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
		selectDept = (Department) this.mSectionListAdapter.getItem(position);
		this.mSectionListAdapter.notifyDataSetChanged();
	}

	public void onDestroy() {
		super.onDestroy();
		if (mReceiver != null) {
			this.unregisterReceiver(mReceiver);
		}
	}

	public class ProjectListAdapter extends BaseAdapter {
		private List<Department> mProjectList;

		public void setList(List<Department> projectList) {
			mProjectList = projectList;
		}

		@Override
		public int getCount() {
			return mProjectList.size();
		}

		@Override
		public Department getItem(int position) {
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
				view = (CommonListItem) LayoutInflater.from(DepartmentListActivity.this).inflate(
						R.layout.common_list_item, null);
			} else {
				view = (CommonListItem) convertView;
			}
			boolean isSelected = (selectDept != null && this.getItem(position).getId() == selectDept.getId());
			view.show(this.getItem(position).getName(), isSelected);
			view.showSeperator(position < this.getCount() - 1);
			return view;
		}

	}
}

package com.mobilitychina.zambo.business.customer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.app.ListFragment;
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
import android.widget.ImageView;
import android.widget.ListView;

import com.mobilitychina.zambo.R;
import com.mobilitychina.zambo.app.BaseActivity;
import com.mobilitychina.zambo.app.BaseTitlebar;
import com.mobilitychina.zambo.business.customer.data.CustomerInfo;
import com.mobilitychina.zambo.service.CustomerInfoManager;
import com.mobilitychina.zambo.service.UserInfoManager;
import com.mobilitychina.zambo.util.ConfigDefinition;
import com.mobilitychina.zambo.util.Statistics;
import com.mobilitychina.zambo.widget.CustomPopupWindow;
import com.mobilitychina.zambo.widget.CustomerListItem;
import com.mobilitychina.zambo.widget.SectionListAdapter;

public class CustomerFragment extends ListFragment {
	private SectionListAdapter mSectionListAdapter;
	private BaseTitlebar mTitleBar;
	private Button mBtnCategory;
	private EditText etCustsearch;
	private ImageButton btnClear;
	public static final String ALL_CUST_TYPE = "全部";
	private final String SEARCH_RESULT_TYPE = "搜索结果";
	private final String SELECTED_RESULT_TYPE = "您选择的客户";
	private final String SELECTED_FAVORITE = "收藏";
	private boolean supportMultiSelect = false;
	private List<CustomerInfo> selectedCustomerList = new ArrayList<CustomerInfo>();
	private List<CustomerInfo> favoriteCustomerList = new ArrayList<CustomerInfo>();

	private Map<String, List<CustomerInfo>> groupCustomerList;
	private List<CustomerInfo> allCustomerList;

	private String ownerType; // "I"表示自己，“A”表示团队
	
	private final BroadcastReceiver  mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			((BaseActivity)getActivity()).dismissDialog();
			updateListView(ALL_CUST_TYPE);
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		IntentFilter filter = new IntentFilter();
		filter.addAction(ConfigDefinition.INTENT_ACTION_GETCUSTOMERCOMPLETE);
		filter.addAction(ConfigDefinition.INTENT_ACTION_GETCUSTOMERREFRESH);
		//filter.addAction(ConfigDefinition.INTENT_ACTION_GETCUSTOMERTYPECOMPLETE);
		this.getActivity().registerReceiver(mReceiver, filter);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_customer, container, false);
		mTitleBar = (BaseTitlebar) view.findViewById(R.id.title_bar);
		mTitleBar.setTitle("我的客户");
		mTitleBar.setRightButton(R.drawable.refresh, new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				((BaseActivity)getActivity()).showProgressDialog("正在请求客户列表...",false);
				CustomerInfoManager.getInstance().start();
			}
		});
		mBtnCategory = (Button) view.findViewById(R.id.btnCategory);
		btnClear = (ImageButton) view.findViewById(R.id.btnClear);
		etCustsearch = (EditText) view.findViewById(R.id.etCustsearch);
		etCustsearch.setEnabled(false);
		etCustsearch.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				String keyword = etCustsearch.getText().toString();
				if (keyword.length() == 0) {
					btnClear.setVisibility(View.GONE);
					updateListView(mBtnCategory.getText().toString());
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
	

		btnClear.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				etCustsearch.setText(null);
				updateListView(mBtnCategory.getText().toString());
			}
		});
		mBtnCategory.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				List<String> custyTypeList = getCustTypeList();
				if (custyTypeList == null || custyTypeList.size() == 0) {
					return;
				}
				List<String> data = new ArrayList<String>();
				data.add(ALL_CUST_TYPE);
				for (String custyType : custyTypeList) {
					data.add(custyType);
				}
				CustomPopupWindow popup = new CustomPopupWindow(CustomerFragment.this.getActivity(), data);
				popup.setOnItemClickListener(new CustomPopupWindow.OnItemClickListener() {
					@Override
					public void onItemClick(int position, String data) {
						mBtnCategory.setText(data);
						String keyword = etCustsearch.getText().toString();
						if (keyword.length() > 0) {
							updateSearchList(keyword);
						} else {
							updateListView(data);
							Statistics.sendEvent("customer" , "type","",(long) 0);
//							System.out.println("点击类型------》》");
						}
					}
				});
				popup.show(mBtnCategory, 0, 0);
			}
		});
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		favoriteCustomerList = readFavorite();
		resume();
	}

	private void resume() {
		this.getListView().setDivider(null);
		final BaseActivity activity = (BaseActivity) getActivity();
		if (CustomerInfoManager.getInstance().isCustomerLoading()) {
			activity.showProgressDialog("正在加载...");
		} else {
			this.updateListView(ALL_CUST_TYPE);
		}
	}
	


	@Override
	public void onResume() {
		super.onResume();
		resume();
	}

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
		if (mReceiver != null) {
			this.getActivity().unregisterReceiver(mReceiver);
		}
	}

	/**
	 * 隐藏Fragment的标题栏
	 */
	public void hideTitlebar() {
		mTitleBar.setVisibility(View.GONE);
	}
	

	/**
	 * 设置是否支持多选
	 * 
	 * @param supportMultiSelect
	 */
	public void setMultiSelect(boolean supportMultiSelect) {
		this.supportMultiSelect = supportMultiSelect;
		if (supportMultiSelect) {
			selectedCustomerList = new ArrayList<CustomerInfo>();
		}
	}

	/**
	 * 获取选中的客户列表
	 * 
	 * @return
	 */
	public List<CustomerInfo> getSelectedCustomerList() {
		return selectedCustomerList;
	}


	public Map<String, List<CustomerInfo>> getGroupCustomerList() {
		//if (groupCustomerList == null) {
			return CustomerInfoManager.getInstance().getGroupCustomerList();
//		} else {
//			return groupCustomerList;
//		}
	}

	public List<CustomerInfo> getAllCustomerList() {
		if (allCustomerList == null) {
			return CustomerInfoManager.getInstance().getCustomerList();
		} else {
			return allCustomerList;
		}
	}

	public void setGroupCustomerList(Map<String, List<CustomerInfo>> groupCustomerList) {
		this.groupCustomerList = groupCustomerList;
	}

	public void setAllCustomerList(List<CustomerInfo> allCustomerList) {
		this.allCustomerList = allCustomerList;
	}

	private List<String> getCustTypeList() {
		List<String> custTypeList = new ArrayList<String>();
		
		Iterator<String> it = this.getGroupCustomerList().keySet().iterator();
		while (it.hasNext()) {
			String custType = it.next();
			custTypeList.add(custType);
		}
		Collections.sort(custTypeList, new Comparator<String>() {
			@Override
			public int compare(String lhs, String rhs) {
				// TODO Auto-generated method stub
				return lhs.compareToIgnoreCase(rhs);
			}
		});
		// 将S类型置为首要位置
		for (String string : custTypeList) {
			if (string.equalsIgnoreCase("S")) {
				custTypeList.remove(string);
				custTypeList.add(0, "S");
				break;
			}
		}
		for (String string : custTypeList) {
			if (string.equalsIgnoreCase(SELECTED_FAVORITE)) {
				custTypeList.remove(string);
				custTypeList.add(0, SELECTED_FAVORITE);
				break;
			}
		}
		return custTypeList;
	}

	/**
	 * 显示已选择的客户
	 */
	private void addSelectedCustomerListView() {
		if (supportMultiSelect && selectedCustomerList != null) {
			CustomerListAdapter listAdapter = new CustomerListAdapter();
			listAdapter.setList(selectedCustomerList);
			mSectionListAdapter.addSection(SELECTED_RESULT_TYPE, listAdapter);
		}
	}

	public void updateListView(String aCustType) {
		int totalCount = 0;
		mSectionListAdapter = new SectionListAdapter(getActivity());
		if (supportMultiSelect && selectedCustomerList != null) { // 显示已选择的客户
			this.addSelectedCustomerListView();
		}

		
		List<String> custTypeList = this.getCustTypeList();
		for (String custType : custTypeList) {
			if (aCustType.equalsIgnoreCase(ALL_CUST_TYPE) || custType.equalsIgnoreCase(aCustType)) {
				List<CustomerInfo> custInfoList = this.getGroupCustomerList().get(custType);
				if(!custType.equalsIgnoreCase(SELECTED_FAVORITE)){
					totalCount += custInfoList.size();
				}
//				if(custType.equalsIgnoreCase(SELECTED_FAVORITE)){
//					custInfoList =getFavoriteList();
//				}
				CustomerListAdapter listAdapter = new CustomerListAdapter();
				listAdapter.setList(custInfoList);
				mSectionListAdapter.addSection(custType, listAdapter);
			}
		}
		
		this.setListAdapter(mSectionListAdapter);
		if (mSectionListAdapter != null) {
			this.mSectionListAdapter.notifyDataSetChanged();
		}

		
		if (totalCount > 0) {
			etCustsearch.setHint("搜索" + totalCount + "位客户");
		} else {
			etCustsearch.setHint("搜索客户");
		}
		if(aCustType.equalsIgnoreCase(SELECTED_FAVORITE)){
			etCustsearch.setHint("搜索" + this.getGroupCustomerList().get(SELECTED_FAVORITE).size() + "位客户");
		}
		etCustsearch.setEnabled(true);
	}

	private void updateSearchList(String keyword) {
		String custType = mBtnCategory.getText().toString();
		List<CustomerInfo> custList = null;
		if (custType.equalsIgnoreCase(ALL_CUST_TYPE)) {
			custList = this.getAllCustomerList();
		} else {
			custList = this.getGroupCustomerList().get(custType);
		}
		if (custList == null || keyword == null || keyword.length() == 0) {
			return;
		}

		List<CustomerInfo> searchCustomerInfo = new ArrayList<CustomerInfo>();
		for (CustomerInfo customerInfo : custList) {
			if (customerInfo.getCustName().contains(keyword.trim())) {
				searchCustomerInfo.add(customerInfo);
			}
		}
		if (selectedCustomerList != null) {
			searchCustomerInfo.removeAll(selectedCustomerList);
		}

		mSectionListAdapter = new SectionListAdapter(getActivity());

		this.addSelectedCustomerListView();

		CustomerListAdapter listAdapter = new CustomerListAdapter();
		listAdapter.setList(searchCustomerInfo);
		mSectionListAdapter.addSection(SEARCH_RESULT_TYPE + "(" + searchCustomerInfo.size() + ")", listAdapter);
		this.setListAdapter(mSectionListAdapter);
		if (mSectionListAdapter != null) {
			this.mSectionListAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		if (supportMultiSelect) {
			CustomerInfo custInfo = (CustomerInfo) this.mSectionListAdapter.getItem(position);
			if (custInfo != null) {
				if (selectedCustomerList == null) {
					selectedCustomerList = new ArrayList<CustomerInfo>();
				}
				boolean isExists = false;
				for (CustomerInfo temp : selectedCustomerList) {
					if (temp.getId().equalsIgnoreCase(custInfo.getId())) {
						isExists = true;
						selectedCustomerList.remove(temp);
						
						addCustomerInfoToAdapter(temp);
						break;
					}
				}
				if (!isExists) {
					selectedCustomerList.add(custInfo);
					removeCustomerInfoFromAdapter(custInfo);
				}
				
				if(mSectionListAdapter != null){
					this.mSectionListAdapter.notifyDataSetChanged();
				}
				
//				String keyword = etCustsearch.getText().toString();
//				if (keyword.length() == 0) {
//					btnClear.setVisibility(View.GONE);
//					updateListView(mBtnCategory.getText().toString());
//				} else {
//					btnClear.setVisibility(View.VISIBLE);
//					updateSearchList(keyword);
//				}
			}
		} else {
			
			CustomerInfo custInfo = (CustomerInfo) this.mSectionListAdapter.getItem(position);
			if (custInfo != null) {
				Intent intent = new Intent(this.getActivity(), CustomerDetailActivity.class);
				intent.putExtra("customerId", custInfo.getId());
				startActivity(intent);
				
				Statistics.sendEvent("customer" , "hospital_itemclick","",(long) 0);
//				System.out.println("点击一个-------》》");
				
			}
		}
	}
	
	private void removeCustomerInfoFromAdapter(CustomerInfo customerInfo){
		List<String> custTypeList = this.getCustTypeList();
		for (String custType : custTypeList) {
			if (custType.equalsIgnoreCase(customerInfo.getCustType())) {
				List<CustomerInfo> custInfoList = this.getGroupCustomerList().get(custType);
				custInfoList.remove(customerInfo);
				List<CustomerInfo> custInfoList1 = this.getGroupCustomerList().get(SELECTED_FAVORITE);
				if(custInfoList1.contains(customerInfo)){
					custInfoList1.remove(customerInfo);
				}
				break;
			}
		}
	}
	private void addCustomerInfoToAdapter(CustomerInfo customerInfo){
		List<String> custTypeList = this.getCustTypeList();
		for (String custType : custTypeList) {
			if (custType.equalsIgnoreCase(customerInfo.getCustType())) {
				List<CustomerInfo> custInfoList = this.getGroupCustomerList().get(custType);
				if(!custInfoList.contains(customerInfo)){
					custInfoList.add(customerInfo);
				}
				List<CustomerInfo> custInfoList1 = this.getGroupCustomerList().get(SELECTED_FAVORITE);
				List<CustomerInfo> list = readFavorite();
				
				if(list.contains(customerInfo)){
					custInfoList1.add(customerInfo);
				}
				break;
			}
		}
	}
	private void removeFavoriteCustomerInfoFromAdapter(CustomerInfo customerInfo){
		List<CustomerInfo> list = readFavorite();
		list.remove(customerInfo);
		writeFavorite(list);
		List<CustomerInfo> custInfoList = this.getGroupCustomerList().get(SELECTED_FAVORITE);
		custInfoList.remove(customerInfo);
	}
	private void addFavoriteCustomerInfoToAdapter(CustomerInfo customerInfo){
		List<CustomerInfo> list = readFavorite();
		if(!list.contains(customerInfo)){
			list.add(customerInfo);
			writeFavorite(list);
		}
		List<CustomerInfo> custInfoList = this.getGroupCustomerList().get(SELECTED_FAVORITE);
				if(!custInfoList.contains(customerInfo)){
					custInfoList.add(customerInfo);
				}
	}
	
	private void restoreCustomerInfo(){
		if(selectedCustomerList == null){
			return;
		}
		for(CustomerInfo customerInfo : selectedCustomerList){
			addCustomerInfoToAdapter(customerInfo);
		}
	}
	
	
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		restoreCustomerInfo();
	}

	public class CustomerListAdapter extends BaseAdapter {
		private List<CustomerInfo> mCustomerList;

		public void setList(List<CustomerInfo> custInfoList) {
			mCustomerList = custInfoList;
		}

		@Override
		public int getCount() {
			return mCustomerList.size();
		}

		@Override
		public CustomerInfo getItem(int position) {
			return mCustomerList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			CustomerListItem view;
			if (convertView == null) {
				view = (CustomerListItem) LayoutInflater.from(CustomerFragment.this.getActivity()).inflate(
						R.layout.item_customer_list, null);
			} else {
				view = (CustomerListItem) convertView;
			}
			final ImageView ivFavoritView = (ImageView) view.findViewById(R.id.ivFavorite);
			
			CustomerInfo customerInfo = this.getItem(position);
			view.show(customerInfo);
			view.showSeperator(position < this.getCount() - 1);
			view.showCheckbox(supportMultiSelect);
			
			if(getGroupCustomerList().get(SELECTED_FAVORITE) != null){
				boolean select = false;
				for(CustomerInfo temp: getGroupCustomerList().get(SELECTED_FAVORITE)){
					if(temp.getId().equalsIgnoreCase(customerInfo.getId())){
						select = true;
						break;
					}
				}
					view.setFavoriteSelect(select);
			}
			if (supportMultiSelect && selectedCustomerList != null) {
				boolean selected = false;
//				boolean select = false;
				for (CustomerInfo temp : selectedCustomerList) {
					if (temp.getId().equalsIgnoreCase(customerInfo.getId())) {
						selected = true;
						if(readFavorite().contains(temp)){
							view.setFavoriteSelect(true);
						}
						break;
					}
				}
				view.setSelected(selected);
			}
             ivFavoritView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					CustomerInfo custInfo = (CustomerInfo) getItem(position);
					if (custInfo != null) {
						if (favoriteCustomerList == null) {
							favoriteCustomerList = new ArrayList<CustomerInfo>();
						}
						boolean isExists = false;
						for(CustomerInfo temp : favoriteCustomerList){
							if (temp.getId().equalsIgnoreCase(custInfo.getId())) {
								isExists = true;
								favoriteCustomerList.remove(custInfo);							
								removeFavoriteCustomerInfoFromAdapter(custInfo);
//								ivFavoritView.startAnimation(AnimationUtils.loadAnimation(SiemensApplication.getInstance(), R.anim.slide_down));
								break;
							}
						}
					
						
						if (!isExists) {
							favoriteCustomerList.add(custInfo);							
							addFavoriteCustomerInfoToAdapter(custInfo);
//							ivFavoritView.startAnimation(AnimationUtils.loadAnimation(SiemensApplication.getInstance(), R.anim.slide_up));
						}	
						
						if(mSectionListAdapter != null){
							mSectionListAdapter.notifyDataSetChanged();
						}
				}
					
				}
			});
			return view;
		}
		

	}
	public void writeFavorite(List<CustomerInfo> list){
		StringBuffer sb  = new StringBuffer("");
		for(int i=0 ;i<list.size();i++){
			sb.append(list.get(i).getId()).append(",");
		}
		if(sb.length()!=0){
			sb.deleteCharAt(sb.length()-1);
		}
	
		UserInfoManager.getInstance().setFavoriteId(sb.toString());
		UserInfoManager.getInstance().sync(this.getActivity(), true);
		
	}
	public List<CustomerInfo> readFavorite(){
		String ids = UserInfoManager.getInstance().getFavoriteId();
		List<CustomerInfo> favoriteList = new ArrayList<CustomerInfo>();
		if(ids!=null){
			String[] ids2 = ids.split(",");
			for(String temp:ids2){
				if(CustomerInfoManager.getInstance().getCustomerById(temp)!=null){
					favoriteList.add(CustomerInfoManager.getInstance().getCustomerById(temp));
				}
			}
		}
		return favoriteList;
		
	}
	
	
}


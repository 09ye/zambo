package com.mobilitychina.zambo.business.plan;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.ListFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Display;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;


import com.mobilitychina.intf.ITaskListener;
import com.mobilitychina.intf.Task;
import com.mobilitychina.log.McLogger;
import com.mobilitychina.net.HttpPostTask;
import com.mobilitychina.net.SoapTask;
import com.mobilitychina.util.Log;
import com.mobilitychina.util.NetObject;
import com.mobilitychina.zambo.R;
import com.mobilitychina.zambo.app.BaseActivity;
import com.mobilitychina.zambo.app.BaseDetailActivity;
import com.mobilitychina.zambo.app.BaseTitlebar;
import com.mobilitychina.zambo.app.ZamboApplication;
import com.mobilitychina.zambo.business.customer.data.CustomerInfo;
import com.mobilitychina.zambo.business.plan.data.GridViewTitleAdapter;
import com.mobilitychina.zambo.business.plan.data.PlanInfo;
import com.mobilitychina.zambo.service.CustomerInfoManager;
import com.mobilitychina.zambo.service.HttpPostService;
import com.mobilitychina.zambo.service.SoapService;
import com.mobilitychina.zambo.util.CommonUtil;
import com.mobilitychina.zambo.util.ConfigDefinition;
import com.mobilitychina.zambo.util.MsLogType;
import com.mobilitychina.zambo.util.Statistics;
import com.mobilitychina.zambo.widget.PlanItem;
import com.mobilitychina.zambo.widget.ProgressBar;
import com.mobilitychina.zambo.widget.calendar.CalendarGridView;
import com.mobilitychina.zambo.widget.calendar.CalendarGridViewAdapter;
import com.mobilitychina.zambo.widget.calendar.WeekGridViewAdapter;

public class PlanCenterFragment extends ListFragment implements OnTouchListener, ITaskListener, OnClickListener, OnItemLongClickListener {

	private static final int SWIPE_MIN_DISTANCE = 120;
	private static final int SWIPE_MAX_OFF_PATH = 250;
	private static final int SWIPE_THRESHOLD_VELOCITY = 200;
	public static final String INTENT_EMP_ID = "intent_emp_id";
	public static final String INTENT_EMP_NAME = "intent_emp_name";
	private BaseTitlebar mTitleBar;
	private LinearLayout mLayoutCalendar;
	private TextView tvDate;
	private Animation slideLeftIn;
	private Animation slideLeftOut;
	private Animation slideRightIn;
	private Animation slideRightOut;
	private ViewFlipper viewFlipperCalendar;
	private GestureDetector mGesture;
	private CalendarGridView mCalendarView;
	private CalendarGridViewAdapter gMouthAdapter;
	private WeekGridViewAdapter gWeekAdapter;
	private MyAdapter mListAdapter;
	private Calendar calSelected = Calendar.getInstance();
	private Calendar calStartDate = Calendar.getInstance();
	private int currentMonth = 0; // 当前视图月
	private int currentYear = 0; // 当前视图年
	private int currentWeek = 0;
	private HttpPostTask mTaskPlanlist;
	private SoapTask mTaskPlanDelete;
	private List<PlanInfo> mPlanInfoList = new ArrayList<PlanInfo>();
	private Button mBtnChangeCalendarStyle;
	private Button mBtnPre;
	private Button mBtnNext;
	private Button mBtnAddPlan;
	private int AddPlanRequestCode = 0;
	private boolean mIsMouthMode = true;//是否月度模式
	private String  mEmployeeId ="";
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equalsIgnoreCase(
					ConfigDefinition.INTENT_ACTION_ADDPLANCOMPLETE)
					|| intent.getAction().equalsIgnoreCase(ConfigDefinition.INTENT_ACTION_SUBMITFLUP)
					|| intent.getAction().equalsIgnoreCase(ConfigDefinition.INTENT_ACTION_SUBMITITEM)
			) {		
				initPlanList();
			}
		}
	};
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_plan, container, false);
		
		mBtnChangeCalendarStyle = (Button) view.findViewById(R.id.btnChangeCalendarStyle);
		mBtnChangeCalendarStyle.setOnClickListener(this);
		mBtnPre = (Button) view.findViewById(R.id.btnPrevMonth);
		mBtnNext = (Button) view.findViewById(R.id.btnNextMonth);
		mBtnAddPlan = (Button) view.findViewById(R.id.btnAddPlan);
		//mBtnAddPlan.setEnabled(true);
		mBtnAddPlan.setOnClickListener(this);
		calSelected.setFirstDayOfWeek(Calendar.MONDAY);
		calSelected.setMinimalDaysInFirstWeek(7);
//		Date date = new Date();
//		date.setYear(2013);
//		date.setMonth(3);
//		date.setDate(13);
		calSelected.add(Calendar.DAY_OF_YEAR,1);
		calSelected.add(Calendar.DAY_OF_YEAR,-1);
		calSelected.setFirstDayOfWeek(Calendar.MONDAY);
		calSelected.setMinimalDaysInFirstWeek(7);
		calStartDate.setFirstDayOfWeek(Calendar.MONDAY);
		calStartDate.setMinimalDaysInFirstWeek(7);
		
		IntentFilter intentFilter = new IntentFilter(ConfigDefinition.INTENT_ACTION_ADDPLANCOMPLETE);
		intentFilter.addAction(ConfigDefinition.INTENT_ACTION_SUBMITFLUP);
		intentFilter.addAction(ConfigDefinition.INTENT_ACTION_SUBMITITEM);
		
		this.getActivity().registerReceiver(mReceiver, intentFilter);
		this.initView(view);
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if(this.getActivity().getIntent() != null){
			if(this.getActivity().getIntent().getStringExtra(INTENT_EMP_ID) !=null){
				mEmployeeId = this.getActivity().getIntent().getStringExtra(INTENT_EMP_ID) ;
			}
		}
		this.mTitleBar.setTitle("计划中心");
		this.mLayoutCalendar.addView(this.getWeekTitleView(), new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT));
		viewFlipperCalendar = new ViewFlipper(this.getActivity());
		mLayoutCalendar.addView(viewFlipperCalendar, new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		mGesture = new GestureDetector(this.getActivity(), new GestureListener());
		mListAdapter = new MyAdapter(this.getActivity(), new ArrayList<PlanInfo>());
		if(mEmployeeId.equalsIgnoreCase("")){
			this.getListView().setOnItemLongClickListener(this);
		}else{
			mBtnAddPlan.setVisibility(View.GONE);
		}
		this.setListAdapter(mListAdapter);
		this.onCreateCalendarView();
	}
	
	@Override
   public void onResume(){
	   super.onResume();
//	   this.initPlanList();
   }
	
	private boolean isFirstRequestPlanList = true;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(savedInstanceState != null){
			isFirstRequestPlanList = savedInstanceState.getBoolean("isFirstRequestPlanList");
		}
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean("isFirstRequestPlanList", isFirstRequestPlanList);
	}
	
	// 初始化计划
	private void initPlanList() {
		if(isFirstRequestPlanList){
			((BaseActivity)this.getActivity()).showProgressDialog("正在获取计划列表..",false);
		}
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		if(mTaskPlanlist != null){
			mTaskPlanlist.setListener(null);
		}
		mTaskPlanlist = new HttpPostTask(ZamboApplication.getInstance().getApplicationContext());
		mTaskPlanlist.setUrl(HttpPostService.SOAP_URL+"get_visit_plan__list" );
		if(isMonthMode()){
			mTaskPlanlist.getTaskArgs().put("emp_id", "3");
			mTaskPlanlist.getTaskArgs().put("plan_status", "A");
			mTaskPlanlist.getTaskArgs().put("plan_start_date", format.format(gMouthAdapter.getCalendarFirstDate().getTime()));
			mTaskPlanlist.getTaskArgs().put("plan_end_date", format.format(gMouthAdapter.getCalendarLastDate().getTime()));
		}else{
			mTaskPlanlist.getTaskArgs().put("emp_id", "3");
			mTaskPlanlist.getTaskArgs().put("plan_status", "A");
			mTaskPlanlist.getTaskArgs().put("plan_start_date", format.format(gWeekAdapter.getCalendarFirstDate().getTime()));
			mTaskPlanlist.getTaskArgs().put("plan_end_date", format.format(gWeekAdapter.getCalendarLastDate().getTime()));
		}
		
		McLogger.getInstance().addLog(MsLogType.TYPE_SYS,MsLogType.ACT_PLANCENTER,"获取列表");
		
		mTaskPlanlist.setMaxTryCount(3);
		mTaskPlanlist.setListener(this);
		mTaskPlanlist.start();
	}

	private void initView(View view) {
		mTitleBar = (BaseTitlebar) view.findViewById(R.id.title_bar);
		if(this.getActivity() instanceof BaseDetailActivity){
			mTitleBar.setVisibility(View.GONE);
		}
		mLayoutCalendar = (LinearLayout) view.findViewById(R.id.layoutCalendar);
		tvDate = (TextView) view.findViewById(R.id.tvDate);

		slideLeftIn = AnimationUtils.loadAnimation(ZamboApplication.getInstance(), R.anim.slide_left_in);
		slideLeftOut = AnimationUtils.loadAnimation(ZamboApplication.getInstance(), R.anim.slide_left_out);
		slideRightIn = AnimationUtils.loadAnimation(ZamboApplication.getInstance(), R.anim.slide_right_in);
		slideRightOut = AnimationUtils.loadAnimation(ZamboApplication.getInstance(), R.anim.slide_right_out);

		slideLeftIn.setAnimationListener(animationListener);
		slideLeftOut.setAnimationListener(animationListener);
		slideRightIn.setAnimationListener(animationListener);
		slideRightOut.setAnimationListener(animationListener);

		((Button) view.findViewById(R.id.btnPrevMonth)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				preAction();
			}
		});
		((Button) view.findViewById(R.id.btnNextMonth)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				nextAction();
			}
		});
	}

	private View getWeekTitleView() {
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		GridView gridView = new GridView(this.getActivity());
		gridView.setLayoutParams(params);
		gridView.setNumColumns(7);// 设置每行列数
		gridView.setGravity(Gravity.CENTER_VERTICAL);// 位置居中
		gridView.setVerticalSpacing(1);// 垂直间隔
		gridView.setHorizontalSpacing(1);// 水平间隔
		gridView.setBackgroundColor(getResources().getColor(R.color.white));

		WindowManager windowManager = this.getActivity().getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		int i = display.getWidth() / 7;
		int j = display.getWidth() - (i * 7);
		int x = j / 2;
		gridView.setPadding(x, 0, 0, 0);// 居中
		gridView.setLayoutParams(new LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		gridView.setVerticalSpacing(0);// 垂直间隔
		gridView.setHorizontalSpacing(0);// 水平间隔
		GridViewTitleAdapter titleAdapter = new GridViewTitleAdapter(this.getActivity());
		gridView.setAdapter(titleAdapter);// 设置菜单Adapter
		return gridView;
	}

	
	private boolean isMonthMode(){
		return mIsMouthMode;
	}
	/**
	 * 上月
	 */
	private void preAction() {
		if (this.isMonthMode()) {
			currentMonth--;// 当前选择月--
			// 如果当前月为负数的话显示上一年
			if (currentMonth == -1) {
				currentMonth = 11;
				currentYear--;
			}
			calStartDate.set(Calendar.DAY_OF_MONTH, 1); // 设置日为当月1日
			calStartDate.set(Calendar.MONTH, currentMonth); // 设置月
			calStartDate.set(Calendar.YEAR, currentYear); // 设置年
			Statistics.sendEvent("plan", "preMonth", "", (long) 0);
		} else {
			currentWeek--;// 当前选择月--
			if (currentWeek == 1) {
				currentYear--;
				Calendar c = Calendar.getInstance();
				c.set(currentYear, Calendar.DECEMBER, 31, 23, 59, 59);
				Calendar c1 = Calendar.getInstance();
				c1.setFirstDayOfWeek(Calendar.MONDAY);
				c1.setMinimalDaysInFirstWeek(7);
				c1.setTime(c.getTime());
				currentWeek = c1.get(Calendar.WEEK_OF_YEAR);
				calStartDate.set(Calendar.MONTH, 12);
			}
			calStartDate.set(Calendar.WEEK_OF_YEAR, currentWeek); // 设置日为当月1日
			calStartDate.set(Calendar.YEAR, currentYear); // 设置年
			Statistics.sendEvent("plan", "preweek", "按周查看", (long) 0);

		}
		viewFlipperCalendar.setInAnimation(slideRightIn);
		viewFlipperCalendar.setOutAnimation(slideRightOut);
		viewFlipperCalendar.showPrevious();
	}

	/**
	 * 下月
	 */
	private void nextAction() {
		if (isMonthMode()) {
			currentMonth++;
			if (currentMonth == 12) {
				currentMonth = 0;
				currentYear++;
			}
			calStartDate.set(Calendar.DAY_OF_MONTH, 1);
			calStartDate.set(Calendar.MONTH, currentMonth);
			calStartDate.set(Calendar.YEAR, currentYear);
			Statistics.sendEvent("plan", "nextMonth", "", (long) 0);
		} else {
			currentWeek++;
			Calendar c = Calendar.getInstance();
			c.set(currentYear, Calendar.DECEMBER, 31, 23, 59, 59);
			Calendar c1 =  Calendar.getInstance();
			c1.setFirstDayOfWeek(Calendar.MONDAY);
			c1.setMinimalDaysInFirstWeek(7);
			c1.setTime(c.getTime());
			int iMaxWeek = c1.get(Calendar.WEEK_OF_YEAR);
			if (currentWeek == iMaxWeek) {
				currentWeek = 1;
				currentYear++;
			}
			calStartDate.set(Calendar.WEEK_OF_YEAR, currentWeek);
			calStartDate.set(Calendar.YEAR, currentYear);
			Statistics.sendEvent("plan", "nextWeek", "", (long) 0);
		}

		viewFlipperCalendar.setInAnimation(slideLeftIn);
		viewFlipperCalendar.setOutAnimation(slideLeftOut);
		viewFlipperCalendar.showNext();
	}

	/**
	 * 动画事件监听
	 */
	private AnimationListener animationListener = new AnimationListener() {
		@Override
		public void onAnimationStart(Animation animation) {
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
		}

		@Override
		public void onAnimationEnd(Animation animation) {
			onCreateCalendarView();
		}
	};

	/**
	 * 构建日历界面
	 */
	private void onCreateCalendarView() {
		currentMonth = calStartDate.get(Calendar.MONTH);
		currentYear = calStartDate.get(Calendar.YEAR);
		currentWeek = calStartDate.get(Calendar.WEEK_OF_YEAR);
		mCalendarView = new CalendarGridView(getActivity());
		if(gMouthAdapter == null){
			gMouthAdapter = new CalendarGridViewAdapter(this.getActivity(), calStartDate, null);
		}else{
			gMouthAdapter.updateDate(calStartDate);
		}
		if(gWeekAdapter == null){
			gWeekAdapter = new WeekGridViewAdapter(this.getActivity(), calStartDate, null);
		}else{
			gWeekAdapter.updateDate(calStartDate);
		}
		if(isMonthMode()){
			mCalendarView.setAdapter(gMouthAdapter);// 设置菜单Adapter
		}else{
			mCalendarView.setAdapter(gWeekAdapter);
		}
		mCalendarView.setOnTouchListener(this);
		if (viewFlipperCalendar.getChildCount() != 0) {
			viewFlipperCalendar.removeAllViews();
		}
		viewFlipperCalendar.addView(mCalendarView, new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		this.reFreshTitle();
		this.initPlanList();
	}

	private void reFreshTitle() {
		String s;
		if(isMonthMode()){
			s = calStartDate.get(Calendar.YEAR) + "-"
				+ CommonUtil.formatNumber(calStartDate.get(Calendar.MONTH) + 1, 2);
		}else{
	
			Calendar c1 =  Calendar.getInstance();
			c1.setFirstDayOfWeek(Calendar.MONDAY);
			c1.setMinimalDaysInFirstWeek(7);	
			c1.set(Calendar.WEEK_OF_YEAR, currentWeek);
			c1.set(Calendar.YEAR, currentYear);
			s = c1.get(Calendar.YEAR) + "-第" +CommonUtil.formatNumber(c1.get(Calendar.WEEK_OF_YEAR)+1 , 2) + "周";
		}
		tvDate.setText(s);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return mGesture.onTouchEvent(event);
	}

	class GestureListener extends SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			try {
				if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
					return false;
				// right to left swipe
				if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					nextAction();
					return true;

				} else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					preAction();
					return true;

				}
			} catch (Exception e) {
				// nothing
			}
			return false;
		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			// 得到当前选中的是第几个单元格
			int pos = mCalendarView.pointToPosition((int) e.getX(), (int) e.getY());
			LinearLayout txtDay = (LinearLayout) mCalendarView.findViewById(pos + 5000);
			if (txtDay != null) {
				if (txtDay.getTag() != null) {
					Date date = (Date) txtDay.getTag();
					calSelected.setTime(date);
					checkAddMode();
					
					mListAdapter.data =  PlanInfo.getListByData(mPlanInfoList,date);
					mListAdapter.notifyDataSetChanged();
					Statistics.sendEvent("plan", "dayselected", "", (long) 0);
				}

			}
			return true;
		}

		private void checkAddMode() {
			if(getWeekState() == WeekState.NextWeek){
				mBtnAddPlan.setEnabled(true);
				mBtnAddPlan.setText("添加正常计划");
				Statistics.sendEvent("plan", "addplan", "正常计划", (long) 0);
			}else if (getWeekState() == WeekState.SameWeek){
				mBtnAddPlan.setEnabled(true);
				mBtnAddPlan.setText("添加临时计划");
				Statistics.sendEvent("plan", "addplan", "临时计划", (long) 0);
			}else{
				mBtnAddPlan.setEnabled(false);
			}
			if(mCalendarView.getAdapter() == gMouthAdapter){
				gMouthAdapter.setSelectedDate(calSelected);
				gMouthAdapter.notifyDataSetChanged();	
				
			}else{
				gWeekAdapter.setSelectedDate(calSelected);
				gWeekAdapter.notifyDataSetChanged();
			}
		}
		
		
	}
	private WeekState getWeekState(){	
		
		
	    Calendar today = Calendar.getInstance();
	    today.setFirstDayOfWeek(Calendar.MONDAY);
	    today.setMinimalDaysInFirstWeek(7);
		  
	    today.add(Calendar.DAY_OF_YEAR, -1);
	    Calendar calendarToday = Calendar.getInstance();
		calendarToday.setFirstDayOfWeek(Calendar.MONDAY);
		if(calendarToday.get(Calendar.DAY_OF_WEEK) != 1){
			 calendarToday.add(Calendar.DAY_OF_WEEK, 7-calendarToday.get(Calendar.DAY_OF_WEEK) + 1);
		}	
	   
	    
	    if(calSelected.after(calendarToday)){
	    	return WeekState.NextWeek;
	    }else if (calSelected.before(calendarToday) && calSelected.after(today)){
	    	return WeekState.SameWeek;
	    }else{
	    	return WeekState.PreWeek;
	    }
	}
	private enum WeekState
	{
		PreWeek,
		SameWeek,
		NextWeek;
	}
	private class MyAdapter extends BaseAdapter {
		private List<PlanInfo> data;
		private LayoutInflater layoutInflater ;

		public MyAdapter(Context context, List<PlanInfo> data) {
			if(data != null){
				this.data = data;
			}else{
				this.data = new ArrayList<PlanInfo>(); 
			}			
			this.layoutInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return data.size();
		}

		/**
		 * 获取某一位置的数据
		 */
		@Override
		public Object getItem(int position) {
			return data.get(position);
		}

		/**
		 * 获取唯一标识
		 */
		@Override
		public long getItemId(int position) {
			return position;
		}

		/**
		 * android绘制每一列的时候，都会调用这个方法
		 */
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			PlanItem planitem = null;
			if (convertView == null) {
				planitem = new PlanItem();
				// 获取组件布局
				convertView = layoutInflater.inflate(R.layout.item_plan, null);
				planitem.titleView = (TextView) convertView.findViewById(R.id.custName);
				planitem.mProgressbar = (ProgressBar)convertView.findViewById(R.id.progressVisitNum);
				// planitem.visitNumView = (TextView)
				// convertView.findViewById(R.id.visitNum);
				planitem.visitedNumView = (TextView) convertView.findViewById(R.id.visited_num);
				planitem.modifyDateView = (TextView) convertView.findViewById(R.id.modify_date);
				planitem.locationView = (Button) convertView.findViewById(R.id.location);
				// 这里要注意，是使用的tag来存储数据的。
				convertView.setTag(planitem);
			} else {
				planitem = (PlanItem) convertView.getTag();
			}
			if (data.get(position).getVisitNum() > 0) {
				planitem.mProgressbar.setProgress((float) data.get(position).getVisitedNum() / data.get(position).getVisitNum());
			}
			if (data.get(position).getCustDetailId() != 0) {
				// final int a = position;
				// 绑定数据、以及事件触发
				if ("T".equals(data.get(position).getPlanStatus())) {
					planitem.titleView.setText(position + 1 + "-" + data.size() + " "
							+ data.get(position).getCustName() + "(临)");
				} else {
					planitem.titleView.setText(position + 1 + "-" + data.size() + " "
							+ data.get(position).getCustName());
				}
				// planitem.visitNumView.setText(/* (int) */"当月拜访数：" +
				// data.get(position).getVisitNum());
				planitem.visitedNumView.setText(data.get(position).getVisitedNum() + "/"
						+ data.get(position).getVisitNum());
				if (null != data.get(position).getVisitDate() && !"".equals(data.get(position).getVisitDate())) {
					planitem.modifyDateView.setText("上次拜访时间：" + data.get(position).getVisitDate().substring(0, 10));
				} else {
					planitem.modifyDateView.setText("");
				}
				planitem.locationView.setTextSize(16);
				planitem.locationView.setText("  签到");
				planitem.locationView.setVisibility(View.GONE);// 签到不用在计划页面显示
			
			} else {
				planitem.titleView.setText(data.get(position).getCustName());
				// planitem.visitNumView.setText("");
				planitem.visitedNumView.setText("");
				planitem.modifyDateView.setText("");
				planitem.locationView.setVisibility(View.INVISIBLE);
			}
			return convertView;
		}
	}



	@Override
	public void onTaskFailed(Task arg0) {
		if(this.getActivity() instanceof BaseActivity){
			((BaseActivity)this.getActivity()).dismissDialog();
		}
		if(arg0 == mTaskPlanlist){
			if(isFirstRequestPlanList){
				((BaseActivity) this.getActivity()).showDialog("提示", "获取信息失败，请稍后再试..", null);
				mTaskPlanlist = null;
				isFirstRequestPlanList = false;
			}
		}else {
			((BaseActivity) this.getActivity()).showDialog("提示", "获取信息失败，请稍后再试..", null);
		}
	}

	@Override
	public void onTaskFinished(Task arg0) {
		// TODO Auto-generated method stub
		if (mTaskPlanDelete == arg0) {
			Log.i("HttpPostTask","删除计划:" +arg0.getResult().toString());
			if (("2").equals(arg0.getResult().toString())){	
				this.initPlanList();
				Intent intent = new Intent();
				intent.setAction(ConfigDefinition.INTENT_ACTION_DELETEPLANCOMPLETE);
				this.getActivity().sendBroadcast(intent);
				Statistics.sendEvent("plan", "delete_result", "删除成功", (long) 0);
			}else 	if (("0").equals(arg0.getResult().toString())){
				Statistics.sendEvent("plan", "delete_result", "删除失败", (long) 0);
				((BaseActivity) this.getActivity()).showDialog("提示", "删除失败", null);
			}else 	if (("1").equals(arg0.getResult().toString())){
				((BaseActivity) this.getActivity()).showDialog("提示", "本周正常计划不能删除", null);
				Statistics.sendEvent("plan", "delete_result", "本周正常计划不能删除", (long) 0);
			}else 	if (("3").equals(arg0.getResult().toString())){
				((BaseActivity) this.getActivity()).showDialog("提示", "已经签到的临时计划不能删除", null);
				Statistics.sendEvent("plan", "delete_result", "已经签到的临时计划不能删除除", (long) 0);
			}
		} else {
			if (this.getActivity() instanceof BaseActivity) {
				((BaseActivity) this.getActivity()).dismissDialog();
			}
			NetObject result = ((HttpPostTask)arg0).getResult();
			Log.i("HttpPostTask", "计划中心:"+result.toString());
			String code = result.stringForKey("code");
			String message = result.stringForKey("message");
			if(!code.equals("0")){
					((BaseActivity) this.getActivity()).showDialog("提示", message, null);
					mTaskPlanlist = null;
					isFirstRequestPlanList = false;
					return;
			}
			List<NetObject> listNet = result.listForKey("data");
			if(mPlanInfoList!=null){
				mPlanInfoList.clear();
			}
			for (NetObject netObject : listNet) {
				PlanInfo plan = new PlanInfo();
//			    CustomerInfo CustomerInfo = CustomerInfoManager.getInstance().getCustomerById(custId);
				plan.setId(Integer.valueOf((netObject.stringForKey("id"))));
			    plan.setCustDetailId((Integer) netObject.arrayForKey("cust_id").get(0));
			    plan.setCustName((String) netObject.arrayForKey("cust_id").get(1));
			    plan.setModify_date(netObject.stringForKey("plan_visit_date"));
			    plan.setPlanStatus(netObject.stringForKey("plan_type"));
				mPlanInfoList.add(plan);
			}
			
			McLogger.getInstance().addLog(MsLogType.TYPE_SYS,MsLogType.ACT_PLANCENTER,"获取列表完成");
//			mPlanInfoList = RespFactory.getService().fromResp(CustomerInfo.class,
//					arg0.getResult());
			if(this.getActivity()==null){
				System.out.println("PlanCenter==null");
			}else{
				this.mListAdapter = new MyAdapter(this.getActivity(), mPlanInfoList);
			}
			mListAdapter.data = PlanInfo.getListByData(mPlanInfoList,
					calSelected.getTime());
			if (gMouthAdapter != null) {
				List<String> list = new ArrayList<String>();
				
				Calendar calendar = Calendar.getInstance();
				int month = calendar.get(Calendar.MONTH)+1;
				String m = String.valueOf(calendar.get(Calendar.MONTH)+1);
				if(month<10){
					StringBuffer sb = new StringBuffer(m);
					sb.insert(0, "0");
					m = sb.toString();
				}
				
				if (mPlanInfoList != null) {
					for (PlanInfo info : mPlanInfoList) {
						if (!list.contains(info.getModify_date())) {
							list.add(info.getModify_date());
						}
						if(info.getModify_date().split("-")[1].startsWith(m)){
							CustomerInfo cs  = CustomerInfoManager.getInstance().getCustomerById(String.valueOf(info.getCustDetailId()));
							if(cs != null){
								cs.setPlanVisitNum(info.getVisitNum());
								cs.setVisitedNum(info.getVisitedNum());
							}
						}
					}
					
				}

				gMouthAdapter.setListMark(list);
				gWeekAdapter.setListMark(list);
				if (isMonthMode()) {
					gMouthAdapter.notifyDataSetChanged();
				} else {
					gWeekAdapter.notifyDataSetChanged();
				}
			}
			this.getListView().setAdapter(this.mListAdapter);
			isFirstRequestPlanList = false;
		}
	}

	@Override
	public void onTaskUpdateProgress(Task arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		if (view == mBtnChangeCalendarStyle) {
			calStartDate = Calendar.getInstance(calSelected.getTimeZone());
			calStartDate.setFirstDayOfWeek(Calendar.MONDAY);
			calStartDate.setMinimalDaysInFirstWeek(7);
			if (mIsMouthMode) {
				currentWeek = calSelected.get(Calendar.WEEK_OF_YEAR);
				mBtnChangeCalendarStyle.setText("按月查看");
				Statistics.sendEvent("plan", "displaytype", "按月查看", (long) 0);;
				mBtnPre.setBackgroundResource(R.drawable.btn_previousweek);
				mBtnNext.setBackgroundResource(R.drawable.btn_nextweek);
				//gWeekAdapter = new WeekGridViewAdapter(this.getActivity(), calSelected, null);
				gWeekAdapter.updateDate(calSelected);
				gWeekAdapter.setSelectedDate(calSelected);
				
				mCalendarView.setAdapter(gWeekAdapter);
				mIsMouthMode = false;
			} else {
				mBtnChangeCalendarStyle.setText("按周查看");
				Statistics.sendEvent("plan", "displaytype", "按周查看", (long) 0);
				mBtnPre.setBackgroundResource(R.drawable.btn_previousmonth);
				mBtnNext.setBackgroundResource(R.drawable.btn_nextmonth);
				//gMouthAdapter = new CalendarGridViewAdapter(this.getActivity(), calSelected, null);
				gMouthAdapter.updateDate(calSelected);
				gMouthAdapter.setSelectedDate(calSelected);
				mCalendarView.setAdapter(gMouthAdapter);
				mIsMouthMode = true;
				this.initPlanList();
			}
			
			this.reFreshTitle();
		} else if (mBtnAddPlan == view) { // 添加计划
			Intent intent = new Intent(this.getActivity(), AddPlanActivity.class);
			WeekState state = getWeekState();
			if(state == WeekState.NextWeek){
				intent.putExtra("type", AddPlanActivity.AddPlanTypeNormal);
			}else if (state == WeekState.SameWeek){
				intent.putExtra("type", AddPlanActivity.AddPlanTypeTemp);
			}else{
				((BaseActivity)this.getActivity()).showDialog("提示", "您的选择的日期已经过期，请选择今日往后的日期", null);
				return;
			}
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			intent.putExtra("planDate", df.format(calSelected.getTime()));
			this.getActivity().startActivityForResult(intent, AddPlanRequestCode);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
	}


	public void onPause() {
		super.onDestroy();
		
	}
	public void onDestroy() {
		super.onDestroy();
		if (mReceiver != null) {
			this.getActivity().unregisterReceiver(mReceiver);
		}
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
		// TODO Auto-generated method stub
		final int arg2index = arg2;
		((BaseActivity)this.getActivity()).showDialog("提示","确认删除计划？", "确认","取消",new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				if(which == -1){
					if(mListAdapter.data.size()>arg2index){
						PlanInfo info = mListAdapter.data.get(arg2index);
						mTaskPlanDelete = SoapService.getDeleteVisitPlan(String.valueOf(info.getId()));
						mTaskPlanDelete.setListener(PlanCenterFragment.this);
						mTaskPlanDelete.start();
						Statistics.sendEvent("plan", "delete_result", "删除失败", (long) 0);
					}
				}
			}
		});
		
		return true;
	}

	@Override
	public void onTaskTry(Task task) {
		// TODO Auto-generated method stub
		((BaseActivity)this.getActivity()).editDialog("您的网络连接不稳定，正在重试...\n第"+this.mTaskPlanlist.getCurTryCount()+"次");

	}


}

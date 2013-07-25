package com.mobilitychina.zambo.widget.calendar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mobilitychina.zambo.R;
import com.mobilitychina.zambo.business.plan.data.PlanInfo;

public class CalendarGridViewAdapter extends BaseAdapter {

	private Calendar calStartDate = Calendar.getInstance();
	private Calendar calSelected = Calendar.getInstance();
	private String empId;

	public void setSelectedDate(Calendar cal) {
		calSelected = cal;
	}
	private List<String>mListMark;
	public void setListMark(List<String> list){
		mListMark = list;
	}
	private Calendar calToday = Calendar.getInstance();
	private int iMonthViewCurrentMonth = 0;
	private ArrayList<java.util.Date> alArrayList;
	private void UpdateStartDateForMonth() {
		calStartDate.set(Calendar.DATE, 1);

		/*
		 * SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd"); String
		 * plan_visit_date = df.format(calStartDate.getTime());
		 */

		iMonthViewCurrentMonth = calStartDate.get(Calendar.MONTH);

		int iDay = 0;
		int iFirstDayOfWeek = Calendar.TUESDAY;
		int iStartDay = iFirstDayOfWeek;
		if (iStartDay == Calendar.TUESDAY) {
			iDay = calStartDate.get(Calendar.DAY_OF_WEEK) - Calendar.TUESDAY;
			if (calStartDate.get(Calendar.DAY_OF_WEEK) == 1) {
				iDay = 5;
			} else if (iDay < 0)
				iDay = 6;

		}
		calStartDate.add(Calendar.DAY_OF_WEEK, -iDay);
		calStartDate.add(Calendar.DAY_OF_MONTH, -1);

	}

	ArrayList<java.util.Date> titles;

	public Date getCalendarFirstDate(){
		if(alArrayList != null){
			return alArrayList.get(0);
		}
		return null;
	}
	public Date getCalendarLastDate(){
		if(alArrayList != null){
			return alArrayList.get(alArrayList.size() -1 );
		}
		return null;
	}
	private ArrayList<java.util.Date> getDates() {

		UpdateStartDateForMonth();

		alArrayList = new ArrayList<java.util.Date>();

		for (int i = 1; i <= 42; i++) {
			alArrayList.add(calStartDate.getTime());
			calStartDate.add(Calendar.DAY_OF_MONTH, 1);
		}

		return alArrayList;
	}

	private Activity activity;
	Resources resources;

	// construct
	public CalendarGridViewAdapter(Activity a, Calendar cal, String empId) {
		activity = a;
		updateDate(cal);
	}
	public void updateDate(Calendar cal){
		calStartDate = Calendar.getInstance(cal.getTimeZone());
		calStartDate.setTimeInMillis(cal.getTimeInMillis());
		
		resources = activity.getResources();
		titles = getDates();
		this.empId = empId;
	}
	public CalendarGridViewAdapter(Activity a) {
		activity = a;
		resources = activity.getResources();
	}

	@Override
	public int getCount() {
		return titles.size();
	}

	@Override
	public Object getItem(int position) {
		return titles.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		RelativeLayout fl = new RelativeLayout(activity);
		LinearLayout iv = new LinearLayout(activity);
		iv.setId(position + 5000);
		iv.setGravity(Gravity.CENTER);
		iv.setOrientation(1);
		Date myDate = (Date) getItem(position);
		
		Calendar calCalendar = Calendar.getInstance();
		calCalendar.setTime(myDate);

		final int iMonth = calCalendar.get(Calendar.MONTH);
		final int iDay = calCalendar.get(Calendar.DAY_OF_WEEK);

		iv.setBackgroundColor(resources.getColor(R.color.calendar_item_background));
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
		String date = df.format(myDate);
		TextView txtToDay = new TextView(activity);
		txtToDay.setGravity(Gravity.CENTER_HORIZONTAL);
		txtToDay.setTextSize(9);
		if (equalsDate(calToday.getTime(), myDate)) {
			iv.setBackgroundColor(resources.getColor(R.color.event_center));
			txtToDay.setTextColor(Color.DKGRAY);
			txtToDay.setText("今日");
		}

		if (equalsDate(calSelected.getTime(), myDate)) {
			//
			iv.setBackgroundColor(resources.getColor(R.color.selection));
		} else {
			if (equalsDate(calToday.getTime(), myDate)) {
				iv.setBackgroundColor(resources.getColor(R.color.calendar_zhe_day));
			}
		}

		TextView txtDay = new TextView(activity);
		txtDay.setGravity(Gravity.CENTER_HORIZONTAL);
		if (iMonth == iMonthViewCurrentMonth) {
			txtDay.setTextColor(resources.getColor(R.color.Text));
		} else {
			txtDay.setTextColor(resources.getColor(R.color.noMonth));
		}
		int day = myDate.getDate();

		txtDay.setText(String.valueOf(day));
		txtDay.setId(position + 500);
		iv.setTag(myDate);
	
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		iv.addView(txtDay, lp);
		iv.addView(txtToDay, lp);
		fl.addView(iv,lp);
		//标记
		if(mListMark != null && mListMark.contains(date)){
			ImageView image = new ImageView(activity);
			image.setImageResource(R.drawable.icon_calendar_mark);
			RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(new LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
			lp1.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			image.setLayoutParams(lp1);
			fl.addView(image,lp1);
		}
		return fl;
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
	}

	private Boolean equalsDate(Date date1, Date date2) {

		if (date1.getYear() == date2.getYear() && date1.getMonth() == date2.getMonth()
				&& date1.getDate() == date2.getDate()) {
			return true;
		} else {
			return false;
		}

	}

	public String getEmpId() {
		return empId;
	}

	public void setEmpId(String empId) {
		this.empId = empId;
	}

}

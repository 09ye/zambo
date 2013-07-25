package com.mobilitychina.common.calendar;

import java.util.ArrayList;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * 日历gridview的adapter
 * 
 * @author cc
 * 
 */
public class CalendarViewAdapter extends BaseAdapter {
	ArrayList<CalendarItemObject> contentList = new ArrayList<CalendarItemObject>();
	Context context;
	int width;
	int height;

	public CalendarViewAdapter(Context context, int width, int height,
			ArrayList<CalendarItemObject> contentList) {
		super();
		this.context = context;
		this.contentList = contentList;
		this.width = width;
		this.height = height;
	}

	@Override
	public int getCount() {
		return contentList.size();
	}

	@Override
	public Object getItem(int position) {
		return contentList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		CalendarItemView calendarItemView = new CalendarItemView(context,
				contentList.get(position), width, height, position);

		return calendarItemView;
	}

}

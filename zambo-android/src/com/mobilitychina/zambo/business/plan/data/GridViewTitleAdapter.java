package com.mobilitychina.zambo.business.plan.data;

import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mobilitychina.zambo.R;

/**
 * 星期的Title
 * 
 * @author sheely
 * 
 */
public class GridViewTitleAdapter extends BaseAdapter {

	int[] titles = new int[] { R.string.Mon, R.string.Tue, R.string.Wed, R.string.Thu, R.string.Fri, R.string.Sat,
			R.string.Sun, };

	private Activity activity;

	// construct
	public GridViewTitleAdapter(Activity a) {
		activity = a;
	}

	@Override
	public int getCount() {
		return titles.length;
	}

	@Override
	public Object getItem(int position) {
		return titles[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LinearLayout iv = new LinearLayout(activity);
		TextView txtDay = new TextView(activity);
		txtDay.setPadding(0, 5, 0, 5);
		txtDay.setFocusable(false);
		iv.setOrientation(1);

		txtDay.setGravity(Gravity.CENTER);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.MATCH_PARENT);
		txtDay.setBackgroundColor(Color.WHITE);
		txtDay.setTextColor(activity.getResources().getColor(R.color.calendar_title_background));
		txtDay.setText((Integer) getItem(position));

		iv.addView(txtDay, lp);

		return iv;
	}
}
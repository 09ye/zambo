package com.mobilitychina.zambo.widget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;

import com.mobilitychina.zambo.R;

public class CustomPopupWindow implements OnItemClickListener {
	public interface OnItemClickListener {
		public void onItemClick(int position, String data);
	}

	private static final int WINDOW_WIDTH = 330;
	private static final int WINDOW_HEIGHT = 315;
	private Context context;
	private List<String> data;
	private PopupWindow popupWindow;
	private View popupView;
	private CustomPopupWindow.OnItemClickListener listener;

	public CustomPopupWindow(Context context, List<String> data) {
		this.context = context;
		this.data = data;

		LayoutInflater inflater = LayoutInflater.from(this.context);
		popupView = inflater.inflate(R.layout.popup_listview, null);

		SimpleAdapter adapter = new SimpleAdapter(this.context, this.getData(), R.layout.item_popup_list,
				new String[] { "text" }, new int[] { R.id.item });
		ListView listView = (ListView) popupView.findViewById(R.id.listview);
		listView.setOnItemClickListener(this);
		listView.setAdapter(adapter);

		popupWindow = new PopupWindow(popupView, WINDOW_WIDTH, WINDOW_HEIGHT);
		popupWindow.setBackgroundDrawable(this.context.getResources().getDrawable(R.drawable.bg_dropdownlistbox));
		popupWindow.setOutsideTouchable(true);
		popupWindow.setAnimationStyle(android.R.style.Animation_Activity);
		popupWindow.update();
		popupWindow.setTouchable(true);
		popupWindow.setFocusable(true);
	}

	public void setOnItemClickListener(CustomPopupWindow.OnItemClickListener listener) {
		this.listener = listener;
	}

	public void show(View anchor, int xoff, int yoff) {
		if (!popupWindow.isShowing()) {
			xoff -= (popupWindow.getWidth() - 35);
			if (xoff < 0) {
				xoff = 0;
			}
			if (xoff < 35) {
				popupView.setBackgroundResource(R.drawable.bg_dropdownlistbox_left);
				popupWindow.setBackgroundDrawable(this.context.getResources().getDrawable(
						R.drawable.bg_dropdownlistbox_left));
			} else {
				popupView.setBackgroundResource(R.drawable.bg_dropdownlistbox);
				popupWindow.setBackgroundDrawable(this.context.getResources()
						.getDrawable(R.drawable.bg_dropdownlistbox));
			}
			popupWindow.showAsDropDown(anchor, xoff, yoff);
		}
	}

	private List<Map<String, String>> getData() {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();

		for (String value : data) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("text", value);
			list.add(map);
		}
		return list;
	}

	@Override
	public void onItemClick(AdapterView<?> apdaterView, View view, int position, long id) {
		if (this.listener != null) {
			this.listener.onItemClick(position, data.get(position));
		}
		popupWindow.dismiss();
	}
}

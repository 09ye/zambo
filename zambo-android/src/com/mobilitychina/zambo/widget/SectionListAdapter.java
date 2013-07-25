package com.mobilitychina.zambo.widget;

import java.util.LinkedHashMap;
import java.util.Map;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;

import com.mobilitychina.zambo.R;

/**
 * 分段列表的适配器
 * 
 * @author chenwang
 * 
 */
public class SectionListAdapter extends BaseAdapter {

	private final Map<String, Adapter> sections = new LinkedHashMap<String, Adapter>();
	private ArrayAdapter<String> headers;
	private BaseAdapter complexHeaders; // 复杂的Section Header，支持自定义
	private boolean hiddenHeader; // 隐藏Header
	public final static int TYPE_SECTION_HEADER = 0;

	/**
	 * 使用默认header布局
	 * 
	 * @param context
	 */
	public SectionListAdapter(Context context) {
		headers = new ArrayAdapter<String>(context, R.layout.list_header);
	}

	/**
	 * 增加是否需要隐藏Header的判别
	 * 
	 * @param context
	 * @param hiddenHeader
	 */
	public SectionListAdapter(Context context, boolean hiddenHeader) {
		this.hiddenHeader = hiddenHeader;
		if (!hiddenHeader) {
			headers = new ArrayAdapter<String>(context, R.layout.list_header);
		}
	}

	/**
	 * 可指定header的布局，仅支持String文本的显示
	 * 
	 * @param context
	 * @param layoutId
	 */
	public SectionListAdapter(Context context, int layoutId) {
		headers = new ArrayAdapter<String>(context, layoutId);
	}

	/**
	 * 使用自定义的Header，Header样式可自定义
	 * 
	 * @param context
	 * @param headerAdpater
	 */
	public SectionListAdapter(Context context, BaseAdapter headerAdpater) {
		this.complexHeaders = headerAdpater;
	}

	public void addSection(String section, Adapter adapter) {
		if (this.headers != null) {
			this.headers.add(section);
		}
		this.sections.put(section, adapter);
	}

	public Object getItem(int position) {
		for (Object section : this.sections.keySet()) {
			Adapter adapter = sections.get(section);
			int size = adapter.getCount() + (this.hiddenHeader ? 0 : 1);

			if (this.hiddenHeader) {
				if (position < size)
					return adapter.getItem(position);
			} else {
				// check if position inside this section
				if (position == 0)
					return section;
				if (position < size)
					return adapter.getItem(position - 1);
			}
			// otherwise jump into next section
			position -= size;
		}
		return null;
	}

	public int getCount() {
		// total together all sections, plus one for each section header
		int total = 0;
		for (Adapter adapter : this.sections.values())
			total += adapter.getCount() + (this.hiddenHeader ? 0 : 1);
		return total;
	}

	public int getViewTypeCount() {
		// assume that headers count as one, then total all sections
		int total = (this.hiddenHeader ? 0 : 1);
		for (Adapter adapter : this.sections.values())
			total += adapter.getViewTypeCount();
		return total;
	}

	public int getItemViewType(int position) {
		int type = (this.hiddenHeader ? 0 : 1);
		for (Object section : this.sections.keySet()) {
			Adapter adapter = sections.get(section);
			int size = adapter.getCount() + (this.hiddenHeader ? 0 : 1);
			if (this.hiddenHeader) {
				if (position < size)
					return type + adapter.getItemViewType(position);
			} else {
				// check if position inside this section
				if (position == 0)
					return TYPE_SECTION_HEADER;
				if (position < size)
					return type + adapter.getItemViewType(position - 1);
			}
			// otherwise jump into next section
			position -= size;
			type += adapter.getViewTypeCount();
		}
		return -1;
	}

	public boolean areAllItemsSelectable() {
		return false;
	}

	public boolean isEnabled(int position) {
		if (this.hiddenHeader) {
			return true;
		} else {
			return (getItemViewType(position) != TYPE_SECTION_HEADER);
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		int sectionnum = 0;
		for (Object section : this.sections.keySet()) {
			Adapter adapter = sections.get(section);
			int size = adapter.getCount() + (this.hiddenHeader ? 0 : 1);
			if (this.hiddenHeader) {
				if (position < size)
					return adapter.getView(position, convertView, parent);
			} else {
				if (position == 0) {
					if (headers != null) {
						String sectionName = headers.getItem(sectionnum);
						View  headerView = headers.getView(sectionnum, convertView, parent);
						if("您选择的客户".equalsIgnoreCase(sectionName)){
							headerView.setBackgroundDrawable(new ColorDrawable(Color.argb(0xFF,0x52,0xB6,0xC0)));
						}
						return headerView;
					} else {
						return this.complexHeaders.getView(sectionnum, convertView, parent);
					}
				} else if (position < size) {
					return adapter.getView(position - 1, convertView, parent);
				}
			}
			position -= size;
			sectionnum++;
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

}

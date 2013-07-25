package com.mobilitychina.common.calendar;

import java.util.ArrayList;
import java.util.Date;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CalendarView extends LinearLayout {
	Context context;
	String[] weekStrs = { "星期天", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六" };
	String[] weekStrsE = { "Sun", "Mon", "Tues", "Wed", "Thur", "Fri", "Sat" };
	LinearLayout linearLayout;// 列标头
	GridView gridView;// 日期gv
	ArrayList<CalendarItemObject> contentList = new ArrayList<CalendarItemObject>();
	CalendarViewAdapter mAdapter;
	int width = LinearLayout.LayoutParams.MATCH_PARENT;// 控件宽
	int height = LinearLayout.LayoutParams.MATCH_PARENT;// 控件高
	int divider = 1;// 分割线宽
	int dividerColor = Color.WHITE;// 分割线颜色
	int titleColor = Color.rgb(97, 97, 97);// title背景
	Drawable titleBackgroundDrawable;// title背景
	int titleBackgroundResource;// title背景
	boolean EnBo = false;// 是否英文
	int titleHeight = 21;// title高
	int titleHeightS = 0;// 自定义title高
	int titleTextColor = Color.WHITE;// title字体颜色
	int titleTextSize = 10;// title字体大小
	int itemW = 0;// 子项宽
	int itemH = 0;// 子项高
	int gvRow = 5;// gv行数
	int dValueX = 0;// 行差
	int dValueY = 0;// 列差
	int gvBottom = 0;// gv高
	int childLeft = 0;// 子控件宽
	int childRight = 0;// 子控件高
	Drawable clickBgDrawable = null;// 被点击子项背景图
	int oldPosition = -1;// 前一个被点击的项
	Drawable oldDrawable = null;// 前一个被点击的项背景图
	int sPosition = -1; // 特殊子项
	Drawable sClickBgDrawable = null;// 特殊子项被点击子项背景图
	onCalendarClickListener interface1;
	TextView weekTv_0;
	TextView weekTv_1;
	TextView weekTv_2;
	TextView weekTv_3;
	TextView weekTv_4;
	TextView weekTv_5;
	TextView weekTv_6;
	TextView[] weekTvs = { weekTv_0, weekTv_1, weekTv_2, weekTv_3, weekTv_4,
			weekTv_5, weekTv_6 };

	/**
	 * 接口
	 * 
	 * @param interface1
	 */
	public void SetOnCalendarClickListener(onCalendarClickListener interface1) {
		this.interface1 = interface1;
	}

	/**
	 * 创建CalendarView方法
	 * 
	 * @param context
	 * @param contentList日期子项数据
	 * @param width宽
	 * @param height高
	 */
	public CalendarView(Context context,
			ArrayList<CalendarItemObject> contentList, int width, int height) {
		super(context);
		this.width = width;
		this.height = height;
		creatView(context, contentList);
	}

	/**
	 * 创建CalendarView方法
	 * 
	 * @param context
	 * @param contentList日期子项数据
	 */
	public CalendarView(Context context, Date date) {
		super(context);

		creatView(context, contentList);
	}

	/**
	 * 创建CalendarView方法
	 * 
	 * @param context
	 * @param contentList日期子项数据
	 */
	public CalendarView(Context context,
			ArrayList<CalendarItemObject> contentList) {
		super(context);

		creatView(context, contentList);
	}

	/**
	 * 创建CalendarView方法
	 * 
	 * @param context
	 * @param attrs
	 */
	public CalendarView(Context context, AttributeSet attrs) {
		super(context, attrs);
		creatView(context, contentList);
	}

	/**
	 * 创建view
	 * 
	 * @param context
	 * @param contentList
	 */
	protected void creatView(final Context context,
			final ArrayList<CalendarItemObject> contentList_) {
		this.context = context;
		this.contentList.clear();
		for (int i = 0; i < contentList_.size(); i++) {
			this.contentList.add(new CalendarItemObject(contentList_.get(i)));
		}

		setOrientation(LinearLayout.VERTICAL);
		linearLayout = new LinearLayout(context);
		linearLayout.setOrientation(LinearLayout.HORIZONTAL);
		linearLayout.setGravity(Gravity.CENTER_VERTICAL);
		linearLayout.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT));
		this.addView(linearLayout);
		for (int i = 0; i < 7; i++) {
			weekTvs[i] = new TextView(context);
			weekTvs[i].setGravity(Gravity.CENTER);
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.MATCH_PARENT);
			layoutParams.weight = 1;
			linearLayout.addView(weekTvs[i], layoutParams);
		}
		gridView = new GridView(context);
		gridView.setGravity(Gravity.CENTER);
		gridView.setNumColumns(7);
		gridView.setSelector(android.R.color.transparent);
		this.addView(gridView);
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				int nowMonth = contentList.get(7).getDate().getMonth();
				int month = contentList.get(arg2).getDate().getMonth();
				if (month == nowMonth) {
					if (oldPosition != arg2) {
						if (oldPosition != -1) {
							contentList.get(oldPosition).setBackgroundDrawable(
									oldDrawable);
						}
						oldDrawable = ((CalendarItemView) arg1).getBackground();
						oldPosition = arg2;
					}
					if (arg2 != sPosition) {
						if (clickBgDrawable != null) {
							contentList.get(oldPosition).setBackgroundDrawable(
									clickBgDrawable);
						}
					} else {
						if (sClickBgDrawable != null) {
							contentList.get(oldPosition).setBackgroundDrawable(
									sClickBgDrawable);
						} else if (sClickBgDrawable == null
								&& clickBgDrawable != null) {
							contentList.get(oldPosition).setBackgroundDrawable(
									clickBgDrawable);
						}
					}

					if (interface1 != null) {
						interface1.onCalendarClick(arg2,
								((CalendarItemView) arg1).getDate());
					}
					mAdapter = new CalendarViewAdapter(context, itemW, itemH,
							contentList);
					gridView.setAdapter(mAdapter);
				}

			}
		});
	}

	/**
	 * 设置自定义的项
	 */
	protected void lauoutChange() {
		if (titleColor != 0) {
			linearLayout.setBackgroundColor(titleColor);
		}
		if (titleBackgroundDrawable != null) {
			linearLayout.setBackgroundDrawable(titleBackgroundDrawable);
		}
		if (titleBackgroundResource != 0) {
			linearLayout.setBackgroundResource(titleBackgroundResource);
		}
		gridView.setVerticalSpacing(divider);
		gridView.setHorizontalSpacing(divider);
		gridView.setBackgroundColor(dividerColor);
		for (int i = 0; i < 7; i++) {
			if (EnBo) {
				weekTvs[i].setText(weekStrsE[i]);
			} else {
				weekTvs[i].setText(weekStrs[i]);
			}
			weekTvs[i].setTextColor(titleTextColor);
			weekTvs[i].setTextSize(titleTextSize);
		}

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if (width < 0) {
			width = MeasureSpec.getSize(widthMeasureSpec);
		}
		if (height < 0) {
			height = MeasureSpec.getSize(heightMeasureSpec);
		}
		if (titleHeightS != 0) {
			titleHeight = titleHeightS;
		}
		gvRow = contentList.size() / 7;
		dValueX = (width - (6 * divider)) % 7;
		dValueY = (height - titleHeight - ((gvRow - 1) * divider)) % gvRow;
		itemW = (width - (6 * divider)) / 7;
		itemH = (height - titleHeight - ((gvRow - 1) * divider)) / gvRow;
		gvBottom = height - dValueY;
		childLeft = (dValueX / 2);
		childRight = width - dValueX + (dValueX / 2);
		setMeasuredDimension(width, height);
		linearLayout.getLayoutParams().width = width - dValueX;
		gridView.getLayoutParams().width = width - dValueX;
		gridView.getLayoutParams().height = height - titleHeight - dValueY;
		mAdapter = new CalendarViewAdapter(context, itemW, itemH, contentList);
		gridView.setAdapter(mAdapter);
		lauoutChange();
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		for (int i = 0; i < getChildCount(); i++) {
			View child = this.getChildAt(i);
			if (child == linearLayout) {
				child.layout(childLeft, 0, childRight, titleHeight);
			} else if (child == gridView) {
				child.layout(childLeft, titleHeight, childRight, gvBottom);

			}

		}
	}

	/**
	 * 设置分割线颜色
	 * 
	 * @param color
	 */
	public void setGridViewLineColor(int color) {
		this.dividerColor = color;
		this.requestLayout();
	}

	/**
	 * 设置分割线宽度
	 * 
	 * @param divider
	 */
	public void setGridViewLineW(int divider) {
		this.divider = divider;
		this.requestLayout();
	}

	/**
	 * 设置控件宽
	 * 
	 * @param width
	 */
	public void setWidth(int width) {
		this.width = width;
		this.requestLayout();
	}

	/**
	 * 设置控件高
	 * 
	 * @param height
	 */
	public void setHeight(int height) {
		this.height = height;
		this.requestLayout();
	}

	/**
	 * 设置title高度
	 * 
	 * @param size
	 */
	public void setTitleHeight(int height) {
		this.titleHeightS = height;
		this.requestLayout();

	}

	/**
	 * 设置title背景
	 * 
	 * @param color
	 */
	public void setTitleBackgroundColor(int color) {
		this.titleColor = color;
		this.titleBackgroundDrawable = null;
		this.titleBackgroundResource = 0;
		this.requestLayout();
	}

	/**
	 * 设置title背景
	 * 
	 * @param drawable
	 */
	public void setTitleBackgroundDrawable(Drawable drawable) {
		this.titleColor = 0;
		this.titleBackgroundDrawable = drawable;
		this.titleBackgroundResource = 0;
		this.requestLayout();
	}

	/**
	 * 设置title背景
	 * 
	 * @param resource
	 */
	public void setTitleBackgroundResource(int resource) {
		this.titleColor = 0;
		this.titleBackgroundDrawable = null;
		this.titleBackgroundResource = resource;
		this.requestLayout();
	}

	/**
	 * 设置title中英文
	 * 
	 * @param EnBo
	 */
	public void setTitleStyle(boolean EnBo) {
		this.EnBo = EnBo;
		this.requestLayout();
	}

	/**
	 * 设置title字体颜色
	 * 
	 * @param color
	 */
	public void setTitleTextColor(int color) {
		this.titleTextColor = color;
		this.requestLayout();
	}

	/**
	 * 设置title字体大小
	 * 
	 * @param size
	 */
	public void setTitleTextSize(int size) {
		this.titleTextSize = size;
		this.requestLayout();
	}

	/**
	 * 更改某项数据
	 * 
	 * @param position
	 *            ,calenderItemObject
	 */
	public void setCalendarItemObject(int position,
			CalendarItemObject calendarItemObject) {
		this.contentList.get(position).addParameters(calendarItemObject);
		this.requestLayout();
	}

	/**
	 * 更改某项数据
	 * 
	 * @param position
	 *            ,calenderItemObject
	 */
	public void setCalendarItemObject(Date date,
			CalendarItemObject calendarItemObject) {
		this.contentList.get(getPosition(date)).addParameters(
				calendarItemObject);
		this.requestLayout();
	}

	/**
	 * 设置子项sign图片所占全控件比例
	 * 
	 * @param scale
	 */
	public void setItemSignScale(float scale) {
		for (int i = 0; i < contentList.size(); i++) {
			this.contentList.get(i).setItemSignScale(scale);
		}
		this.requestLayout();
	}

	/**
	 * 设置子项点击后背景图
	 * 
	 * @param res
	 */
	public void setClickBgId(Context context, int res) {
		this.clickBgDrawable = context.getResources().getDrawable(res);
		this.requestLayout();
	}

	/**
	 * 设置子项点击后背景图
	 * 
	 * @param res
	 */
	public void setClickBgDrawable(Drawable drawable) {
		this.clickBgDrawable = drawable;
		this.requestLayout();
	}

	/**
	 * 设置特殊子项点击后背景图
	 * 
	 * @param res
	 */
	public void setSpecialClickBgId(Context context, int position, int res) {
		this.sPosition = position;
		this.sClickBgDrawable = context.getResources().getDrawable(res);
		this.requestLayout();
	}

	/**
	 * 设置特殊子项点击后背景图
	 * 
	 * @param res
	 */
	public void setSpecialClickBgDrawable(int position, Drawable drawable) {
		this.sPosition = position;
		this.sClickBgDrawable = drawable;
		this.requestLayout();
	}

	/**
	 * 设置特殊日期点击后背景图
	 * 
	 * @param res
	 */
	public void setSpecialClickBgId(Context context, Date date, int res) {
		this.sPosition = getPosition(date);
		this.sClickBgDrawable = context.getResources().getDrawable(res);
		this.requestLayout();
	}

	/**
	 * 设置特殊日期点击后背景图
	 * 
	 * @param res
	 */
	public void setSpecialClickBgDrawable(Date date, Drawable drawable) {
		this.sPosition = getPosition(date);
		this.sClickBgDrawable = drawable;
		this.requestLayout();
	}

	/**
	 * 获取对应天的position
	 * 
	 * @param date
	 */
	public int getPosition(Date date) {
		DateManager dateManager = new DateManager();
		int position = -1;
		for (int i = 0; i < contentList.size(); i++) {
			if (dateManager.compareTO(date, (contentList.get(i).getDate()))) {
				position = i;
			}
		}
		return position;

	}
}

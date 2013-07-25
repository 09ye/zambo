package com.mobilitychina.common.calendar;

import java.util.Date;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 日历gridview子项view
 * 
 * @author cc
 * 
 */
public class CalendarItemView extends RelativeLayout {
	Context context;
	CalendarItemObject content;// 内容对象
	float scale;
	float scaleXToY = 0;
	int signW = RelativeLayout.LayoutParams.WRAP_CONTENT;
	int signH = RelativeLayout.LayoutParams.WRAP_CONTENT;

	/**
	 * 日历gridview子项view创建方法
	 * 
	 * @param context
	 * @param content
	 * @param width
	 * @param height
	 * @param id
	 */
	public CalendarItemView(Context context, CalendarItemObject content,
			int width, int height, int id) {
		super(context);
		this.context = context;
		this.content = content;
		scale = content.getItemSignScale();
		if (scale < 0) {
			scale = ((float) 1) / 3;
		}
		setId(id);
		if (content.getBackgroundDrawable() != null) {
			setBackgroundDrawable(content.getBackgroundDrawable());
		} else if (content.getBackgroundId() != 0) {
			setBackgroundResource((int) content.getBackgroundId());
		} else {
			setBackgroundColor(Color.WHITE);
		}
		android.widget.AbsListView.LayoutParams layoutParams = new android.widget.AbsListView.LayoutParams(
				width, height);
		setLayoutParams(layoutParams);
		TextView textView = new TextView(context);
		textView.setText(content.getDate().getDate() + "");
		textView.setTextColor(content.getTextColor());
		textView.setTextSize(content.getTextSize());
		RelativeLayout.LayoutParams tvOflayoutParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		tvOflayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		addView(textView, tvOflayoutParams);
		ImageView imageView = new ImageView(context);
		if (content.getSignDrawable() != null) {
			scaleXToY = getScaleXToY(content.getSignDrawable());
			imageView.setBackgroundDrawable(content.getSignDrawable());
			signW = (int) (width * scale);
			signH = (int) (signW / scaleXToY);
		} else if (content.getSignId() != 0) {
			Drawable drawable = getResources().getDrawable(content.getSignId());
			scaleXToY = getScaleXToY(drawable);
			imageView.setBackgroundResource((int) content.getSignId());
			signW = (int) (width * scale);
			signH = (int) (signW / scaleXToY);
		}
		RelativeLayout.LayoutParams imgOflayoutParams = new RelativeLayout.LayoutParams(
				signW, signH);
		imgOflayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		imgOflayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		addView(imageView, imgOflayoutParams);
	}

	/**
	 * 获取date
	 * 
	 * @return
	 */
	public Date getDate() {
		return content.getDate();

	}

	/**
	 * 获得图片宽高比
	 * 
	 * @param drawable
	 * @return scale
	 */
	protected float getScaleXToY(Drawable drawable) {
		float scale = ((float) drawable.getIntrinsicWidth())
				/ drawable.getIntrinsicHeight();
		return scale;

	}
}

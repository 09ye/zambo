package com.mobilitychina.common.calendar;

import java.util.Date;
import android.graphics.drawable.Drawable;

/**
 * 日历子项Object类
 * 
 * @author cc
 * 
 */
public class CalendarItemObject extends Object {
	Date date;// 当前日期
	int backgroundId;// 背景图片id
	int signId;// 标识图片id
	int textSize;// 日期字体大小
	int textColor;// 日期字体颜色
	Drawable backgroundDrawable;// 背景图片Drawable
	Drawable signDrawable;// 标识图片Drawable
	float scale = (float) -1;// 标识sign所占子项比例

	public CalendarItemObject() {
	}

	/**
	 * 创建Object方法
	 * 
	 * @param date当前日期
	 * @param backgroundId背景图片id
	 * @param signId标识图片id
	 * @param textSize日期字体大小
	 * @param textColor日期字体颜色
	 */
	public CalendarItemObject(Date date, int backgroundId, int signId,
			int textSize, int textColor) {
		this.date = date;
		this.backgroundId = backgroundId;
		this.signId = signId;
		this.textColor = textColor;
		this.textSize = textSize;
		this.backgroundDrawable = null;
		this.signDrawable = null;
	}

	/**
	 * 创建Object方法
	 * 
	 * @param date当前日期
	 * @param backgroundDrawable背景图片Drawable
	 * @param signId标识图片id
	 * @param textSize日期字体大小
	 * @param textColor日期字体颜色
	 */
	public CalendarItemObject(Date date, Drawable backgroundDrawable,
			int signId, int textSize, int textColor) {
		this.date = date;
		this.backgroundDrawable = backgroundDrawable;
		this.signId = signId;
		this.textColor = textColor;
		this.textSize = textSize;
		this.backgroundId = 0;
		this.signDrawable = null;
	}

	/**
	 * 创建Object方法
	 * 
	 * @param date当前日期
	 * @param backgroundDrawable背景图片Drawable
	 * @param signDrawable标识图片Drawable
	 * @param textSize日期字体大小
	 * @param textColor日期字体颜色
	 */
	public CalendarItemObject(Date date, Drawable backgroundDrawable,
			Drawable signDrawable, int textSize, int textColor) {
		this.date = date;
		this.backgroundDrawable = backgroundDrawable;
		this.signDrawable = signDrawable;
		this.textColor = textColor;
		this.textSize = textSize;
		this.backgroundId = 0;
		this.signId = 0;
	}

	/**
	 * 创建Object方法
	 * 
	 * @param date当前日期
	 * @param backgroundId背景图片id
	 * @param signDrawable标识图片Drawable
	 * @param textSize日期字体大小
	 * @param textColor日期字体颜色
	 */
	public CalendarItemObject(Date date, int backgroundId,
			Drawable signDrawable, int textSize, int textColor) {
		this.date = date;
		this.backgroundId = backgroundId;
		this.signDrawable = signDrawable;
		this.textColor = textColor;
		this.textSize = textSize;
		this.backgroundDrawable = null;
		this.signId = 0;
	}

	/**
	 * 创建Object方法
	 * 
	 * @param date当前日期
	 * @param backgroundId背景图片id
	 * @param signId标识图片id
	 * @param textSize日期字体大小
	 * @param textColor日期字体颜色
	 * @param scale标识比例
	 */
	public CalendarItemObject(Date date, int backgroundId, int signId,
			int textSize, int textColor, float scale) {
		this.date = date;
		this.backgroundId = backgroundId;
		this.signId = signId;
		this.textColor = textColor;
		this.textSize = textSize;
		this.backgroundDrawable = null;
		this.signDrawable = null;
		this.scale = scale;
		try {
			
		} catch (Exception e) {
			// TODO: handle exception
			
		}
	}

	/**
	 * 创建Object方法
	 * 
	 * @param date当前日期
	 * @param backgroundDrawable背景图片Drawable
	 * @param signId标识图片id
	 * @param textSize日期字体大小
	 * @param textColor日期字体颜色
	 * @param scale标识比例
	 */
	public CalendarItemObject(Date date, Drawable backgroundDrawable,
			int signId, int textSize, int textColor, float scale) {
		this.date = date;
		this.backgroundDrawable = backgroundDrawable;
		this.signId = signId;
		this.textColor = textColor;
		this.textSize = textSize;
		this.backgroundId = 0;
		this.signDrawable = null;
		this.scale = scale;
	}

	/**
	 * 创建Object方法
	 * 
	 * @param date当前日期
	 * @param backgroundDrawable背景图片Drawable
	 * @param signDrawable标识图片Drawable
	 * @param textSize日期字体大小
	 * @param textColor日期字体颜色
	 * @param scale标识比例
	 */
	public CalendarItemObject(Date date, Drawable backgroundDrawable,
			Drawable signDrawable, int textSize, int textColor, float scale) {
		this.date = date;
		this.backgroundDrawable = backgroundDrawable;
		this.signDrawable = signDrawable;
		this.textColor = textColor;
		this.textSize = textSize;
		this.backgroundId = 0;
		this.signId = 0;
		this.scale = scale;
	}

	/**
	 * 创建Object方法
	 * 
	 * @param date当前日期
	 * @param backgroundId背景图片id
	 * @param signDrawable标识图片Drawable
	 * @param textSize日期字体大小
	 * @param textColor日期字体颜色
	 * @param scale标识比例
	 */
	public CalendarItemObject(Date date, int backgroundId,
			Drawable signDrawable, int textSize, int textColor, float scale) {
		this.date = date;
		this.backgroundId = backgroundId;
		this.signDrawable = signDrawable;
		this.textColor = textColor;
		this.textSize = textSize;
		this.backgroundDrawable = null;
		this.signId = 0;
		this.scale = scale;
	}

	/**
	 * 创建Object方法
	 * 
	 * @param calendarItemObject
	 */
	public CalendarItemObject(CalendarItemObject calendarItemObject) {
		this.date = calendarItemObject.getDate();
		this.backgroundId = calendarItemObject.getBackgroundId();
		this.signId = calendarItemObject.getSignId();
		this.textColor = calendarItemObject.getTextColor();
		this.textSize = calendarItemObject.getTextSize();
		this.backgroundDrawable = calendarItemObject.getBackgroundDrawable();
		this.signDrawable = calendarItemObject.getSignDrawable();
	}

	/**
	 * 设置日期
	 * 
	 * @param date
	 */
	public void setDate(Date date) {
		this.date = date;
	}

	/**
	 * 获取日期
	 * 
	 * @return date
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * 设置背景图片id
	 * 
	 * @param backgroundId
	 */
	public void setBackgroundId(int backgroundId) {
		this.backgroundId = backgroundId;
		this.backgroundDrawable = null;
	}

	/**
	 * 获取背景图片id
	 * 
	 * @return backgroundId
	 */
	public int getBackgroundId() {
		return backgroundId;
	}

	/**
	 * 设置标志图片id
	 * 
	 * @param signId
	 */
	public void setSignId(int signId) {
		this.signId = signId;
		this.signDrawable = null;
	}

	/**
	 * 获取标志图片id
	 * 
	 * @return signId
	 */
	public int getSignId() {
		return signId;
	}

	/**
	 * 设置日期字体大小
	 * 
	 * @param textSize
	 */
	public void setTextSize(int textSize) {
		this.textSize = textSize;
	}

	/**
	 * 获取日期字体大小
	 * 
	 * @return textSize
	 */
	public int getTextSize() {
		return textSize;
	}

	/**
	 * 设置日期字体颜色
	 * 
	 * @param textColor
	 */
	public void setTextColor(int textColor) {
		this.textColor = textColor;
	}

	/**
	 * 获取日期字体颜色
	 * 
	 * @return textColor
	 */
	public int getTextColor() {
		return textColor;
	}

	/**
	 * 设置背景图片Drawable
	 * 
	 * @param backgroundDrawable
	 */
	public void setBackgroundDrawable(Drawable backgroundDrawable) {
		this.backgroundDrawable = backgroundDrawable;
		this.backgroundId = 0;
	}

	/**
	 * 获取背景图片Drawable
	 * 
	 * @return backgroundDrawable
	 */
	public Drawable getBackgroundDrawable() {
		return backgroundDrawable;
	}

	/**
	 * 设置标识图片Drawable
	 * 
	 * @param signDrawable
	 */
	public void setSignDrawablee(Drawable signDrawable) {
		this.signDrawable = signDrawable;
		this.signId = 0;
	}

	/**
	 * 获取标识图片Drawable
	 * 
	 * @return signDrawable
	 */
	public Drawable getSignDrawable() {
		return signDrawable;
	}

	/**
	 * 设置标识图片所占子项比例
	 * 
	 * @param scale
	 */
	public void setItemSignScale(float scale) {
		this.scale = scale;
	}

	/**
	 * 获取标识图片所占子项比例
	 * 
	 * @return signDrawable
	 */
	public float getItemSignScale() {
		return scale;
	}

	/**
	 * 合并参数
	 * 
	 * @return calendarItemObject
	 */
	public CalendarItemObject addParameters(
			CalendarItemObject calendarItemObject) {
		if (calendarItemObject.getDate() != null) {
			this.date = calendarItemObject.getDate();
		}
		if (calendarItemObject.getBackgroundId() != 0) {
			this.backgroundId = calendarItemObject.getBackgroundId();
		}
		if (calendarItemObject.getSignId() != 0) {
			this.signId = calendarItemObject.getSignId();
		}
		if (calendarItemObject.getTextColor() != 0) {
			this.textColor = calendarItemObject.getTextColor();
		}
		if (textSize != 0) {
			this.textSize = calendarItemObject.getTextSize();
		}
		if (calendarItemObject.getBackgroundDrawable() != null) {
			this.backgroundDrawable = calendarItemObject
					.getBackgroundDrawable();
		}
		if (calendarItemObject.getSignDrawable() != null) {
			this.signDrawable = calendarItemObject.getSignDrawable();
		}
		if (calendarItemObject.getItemSignScale() != -1) {
			this.scale = calendarItemObject.getItemSignScale();
		}
		return calendarItemObject;
	}
}

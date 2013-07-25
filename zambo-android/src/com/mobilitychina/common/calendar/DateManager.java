package com.mobilitychina.common.calendar;

import java.util.ArrayList;
import java.util.Date;

/**
 * date处理类
 * 
 * @author cc
 * 
 */
public class DateManager {
	long msOfDay = 1000 * 3600 * 24;// 一天的毫秒数

	public DateManager() {
	}

	/**
	 * 获取date所在月的第一天
	 * 
	 * @param date
	 * @return firstDate
	 */
	public Date getFirstDayOfMonth(Date date) {
		Date firstDate = (Date) date.clone();
		firstDate.setDate(1);
		return firstDate;
	}

	/**
	 * 获取date所在月的最后一天
	 * 
	 * @param date
	 * @return lastDate
	 */
	public Date getLastDayOfMonth(Date date) {
		Date lastDate = (Date) date.clone();
		lastDate.setDate(1);
		if (lastDate.getMonth() == 11) {
			lastDate.setYear(lastDate.getYear() + 1);
			lastDate.setMonth(0);
		} else {
			lastDate.setMonth(lastDate.getMonth() + 1);
		}
		long millisecond = lastDate.getTime() - msOfDay;
		lastDate = new Date(millisecond);
		return lastDate;
	}

	/**
	 * 获取date所在星期的头一天
	 * 
	 * @param date
	 * @return firstDate
	 */
	public Date getFirstDayOfWeek(Date date) {
		Date firstDate = (Date) date.clone();
		int week = firstDate.getDay();
		int dayDValue = week - 0;
		long millisecond = firstDate.getTime() - (dayDValue * msOfDay);
		firstDate = new Date(millisecond);
		return firstDate;

	}

	/**
	 * 获取date所在星期的最后一天
	 * 
	 * @param date
	 * @return lastDate
	 */
	public Date getLastDayOfWeek(Date date) {
		Date lastDate = (Date) date.clone();
		;
		int week = lastDate.getDay();
		int dayDValue = 6 - week;
		long millisecond = lastDate.getTime() + (dayDValue * msOfDay);
		lastDate = new Date(millisecond);
		return lastDate;

	}

	/**
	 * 获取date的前一天
	 * 
	 * @param date
	 * @return PrevDate
	 */
	public Date getPrevDate(Date date) {
		long millisecond = ((Date) date.clone()).getTime() - msOfDay;
		Date PrevDate = new Date(millisecond);
		return PrevDate;

	}

	/**
	 * 获取date的后一天
	 * 
	 * @param date
	 * @return nextDate
	 */
	public Date getNextDate(Date date) {
		long millisecond = ((Date) date.clone()).getTime() + msOfDay;
		Date nextDate = new Date(millisecond);
		return nextDate;
	}

	/**
	 * 判断是否在一天之内
	 * 
	 * @param date
	 * @return nextDate
	 */
	public boolean compareTO(Date date_1, Date date_2) {
		if (date_1.getYear() == date_2.getYear()
				&& date_1.getMonth() == date_2.getMonth()
				&& date_1.getDate() == date_2.getDate()) {
			return true;
		} else {
			return false;
		}

	}
	/**
	 * 默认list创建
	 * 
	 * @param date
	 * @param textSize
	 * @param bgId
	 * @param textColor
	 * @param nowDateBgId
	 * @param nowDateTextColor
	 * @param outDateBgId
	 * @param outDateTextColor
	 * @return
	 */
	public ArrayList<CalendarItemObject> getContentList(Date date,
			int textSize, int bgId, int textColor, int nowDateBgId,
			int nowDateTextColor, int outDateBgId, int outDateTextColor) {
		DateManager dateManager = new DateManager();
		ArrayList<CalendarItemObject> arrayList = new ArrayList<CalendarItemObject>();
		Date firstDateOfMonth = dateManager.getFirstDayOfMonth(date);
		Date fistDateOfWeek = dateManager.getFirstDayOfWeek(firstDateOfMonth);
		Date lastDateOfMonth = dateManager.getLastDayOfMonth(date);
		Date lastDateOfWeek = dateManager.getLastDayOfWeek(lastDateOfMonth);
		Date itemDate = (Date) fistDateOfWeek.clone();
		for (int i = 0; i > -1; i++) {
			CalendarItemObject calenderItemObject = null;
			if (itemDate.equals(date)) {
				calenderItemObject = new CalendarItemObject(itemDate,
						nowDateBgId, 0, textSize, nowDateTextColor);
			} else if (itemDate.before(firstDateOfMonth)
					|| itemDate.after(lastDateOfMonth)) {
				calenderItemObject = new CalendarItemObject(itemDate,
						outDateBgId, 0, textSize, outDateTextColor);
			} else {
				calenderItemObject = new CalendarItemObject(itemDate, bgId, 0,
						textSize, textColor);
			}
			arrayList.add(calenderItemObject);
			if (itemDate.equals(lastDateOfWeek)) {
				break;
			} else {
				itemDate = dateManager.getNextDate(itemDate);
			}
		}

		return arrayList;

	}
}

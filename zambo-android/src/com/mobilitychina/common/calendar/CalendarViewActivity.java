//package com.mobilitychina.common.calendar;
//
//import java.util.ArrayList;
//import java.util.Date;
//
//import cc.CalendarView.R;
//import cc.CalendarView.R.drawable;
//import cc.CalendarView.R.id;
//import cc.CalendarView.R.layout;
//import android.app.Activity;
//import android.graphics.Color;
//import android.graphics.drawable.Drawable;
//import android.os.Bundle;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.widget.Button;
//import android.widget.LinearLayout;
//import android.widget.Toast;
//
//public class CalendarViewActivity extends Activity implements
//		onCalendarClickListener, OnClickListener {
//	ArrayList<CalendarItemObject> list = new ArrayList<CalendarItemObject>();
//	DateManager dateManager = new DateManager();
//
//	CalendarView calendarView;
//	Button button_01, button_02, button_03, button_04, button_05, button_06,
//			button_07, button_08, button_09, button_10, button_11, button_12,
//			button_13, btn;
//	Button buttons[] = { button_01, button_02, button_03, button_04, button_05,
//			button_06, button_07, button_08, button_09, button_10, button_11,
//			button_12, button_13 };
//	int buttonIds[] = { R.id.btn_01, R.id.btn_02, R.id.btn_03, R.id.btn_04,
//			R.id.btn_05, R.id.btn_06, R.id.btn_07, R.id.btn_08, R.id.btn_09,
//			R.id.btn_10, R.id.btn_11, R.id.btn_12, R.id.btn_13 };
//	String btnStr[] = { "改变分割线颜色", "改变分割线宽", "改变控件宽", "改变控件高", "改变title高",
//			"改变title为英文", "改变title颜色", "改变title字体大小", "改变子项标识大小", "改变某个项标识大小",
//			"改变title颜色", "改变title背景(ID)", "改变title背景(drawable)" };
//	Date nowDate;
//	LinearLayout layout;
//
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		nowDate = new Date(System.currentTimeMillis());
//		setContentView(R.layout.main);
//		for (int i = 0; i < buttons.length; i++) {
//			buttons[i] = (Button) findViewById(buttonIds[i]);
//			buttons[i].setOnClickListener(this);
//			buttons[i].setText(btnStr[i]);
//		}
//		btn = (Button) findViewById(R.id.btn);
//		layout = (LinearLayout) findViewById(R.id.ly);
//		creatList();
//
//		btn.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				creatList();
//			}
//		});
//	}
//
//	public void creatList() {
//		layout.removeView(calendarView);
//		list = dateManager.getContentList(nowDate, 15, R.drawable.bj,
//				Color.BLACK, R.drawable.bg, Color.RED, R.drawable.bj,
//				Color.GRAY);
//		for (int i = 0; i < list.size(); i++) {
//			if ((list.get(i).getDate()
//					.before(dateManager.getFirstDayOfMonth(nowDate)) || list
//					.get(i).getDate()
//					.after(dateManager.getLastDayOfMonth(nowDate)))) {
//			} else {
//				list.get(i).setSignId(R.drawable.paiban);
//			}
//		}
//		calendarView = new CalendarView(this, list);
//		calendarView.setClickBgId(this, R.drawable.bg);
//		int nowP = calendarView.getPosition(nowDate);
//		calendarView.setSpecialClickBgId(this, nowP, R.drawable.bj);
//		layout.addView(calendarView);
//		calendarView.SetOnCalendarClickListener(this);
//	}
//
//	@Override
//	public void onCalendarClick(int position, Date date) {
//		Toast.makeText(this, date.toString(), 100).show();
//	}
//
//	@Override
//	public void onClick(View v) {
//		switch (v.getId()) {
//		case R.id.btn_01:
//			calendarView.setGridViewLineColor(Color.BLUE);
//			break;
//		case R.id.btn_02:
//			calendarView.setGridViewLineW(5);
//			break;
//		case R.id.btn_03:
//			calendarView.setWidth(200);
//			break;
//		case R.id.btn_04:
//			calendarView.setHeight(200);
//			break;
//		case R.id.btn_05:
//			calendarView.setTitleHeight(30);
//			break;
//		case R.id.btn_06:
//			calendarView.setTitleStyle(true);
//			break;
//		case R.id.btn_07:
//			calendarView.setTitleTextColor(Color.RED);
//			break;
//		case R.id.btn_08:
//			calendarView.setTitleTextSize(5);
//			break;
//		case R.id.btn_09:
//			calendarView.setItemSignScale((float) 0.1);
//			break;
//		case R.id.btn_10:
//			CalendarItemObject calendarItemObject = new CalendarItemObject();
//			calendarItemObject.setItemSignScale((float) 0.5);
//			calendarView.setCalendarItemObject(8, calendarItemObject);
//			break;
//		case R.id.btn_11:
//			calendarView.setTitleBackgroundColor(Color.RED);
//			break;
//		case R.id.btn_12:
//			calendarView.setTitleBackgroundResource(R.drawable.bg);
//			break;
//		case R.id.btn_13:
//			Drawable drawable = getResources().getDrawable(R.drawable.paiban);
//			calendarView.setTitleBackgroundDrawable(drawable);
//			break;
//
//		default:
//			break;
//		}
//
//	}
//}
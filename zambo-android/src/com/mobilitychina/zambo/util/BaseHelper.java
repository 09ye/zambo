package com.mobilitychina.zambo.util;

import java.util.ArrayList;


public class BaseHelper     implements EventHandler{
	private ArrayList<EventListener> listListener = new ArrayList<EventListener>();
	/**
	 * 监听
	 */
	@Override
	public void addListener(EventListener listener) {
		// TODO Auto-generated method stub
		listListener.add(listener);
	}
	/**
	 * 移除
	 */
	@Override
	public void removeListener(EventListener listener) {
		// TODO Auto-generated method stub
		listListener.remove(listener);
		}

	@Override
	public void onEventHandler() {
		// TODO Auto-generated method stub
		for (int i= 0;i< listListener.size();i++)
		{
			EventListener listener  = listListener.get(i);
			listener.onListenerRespond(this,new Object());
		}
	}
}

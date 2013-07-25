package com.mobilitychina.image;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.content.Context;

import com.mobilitychina.intf.Task;

/**
 * 图片
 * 
 * @author chenwang
 * 
 */
public class ImageTask extends Task {

	private static ThreadPoolExecutor executor;
	static {
		executor = new ThreadPoolExecutor(3, 6, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
	}

	public static final int THUMBNAIL = 0x01;
	public static final int PHOTO = 0x02;

	private int imageType;

	public int getImageType() {
		return imageType;
	}

	public void setImageType(int imageType) {
		this.imageType = imageType;
	}
	
	public ImageTask(Context context) {
		super(context);
	}

	@Override
	protected Object doInBackground() {
		return null;
	}

	@Override
	protected ThreadPoolExecutor getThreadPoolExecutor() {
		return executor;
	}
}

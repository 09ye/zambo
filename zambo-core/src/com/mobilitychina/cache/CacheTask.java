package com.mobilitychina.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.content.Context;

import com.mobilitychina.intf.Task;
import com.mobilitychina.util.CacheDatabaseHelper;
import com.mobilitychina.util.CacheType;

/**
 * 缓存任务
 * 
 * @author chenwang
 * 
 */
public class CacheTask extends Task {
	private static ThreadPoolExecutor executor;
	static {
		executor = new ThreadPoolExecutor(2, 2, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
	}

	private CacheType cacheType;

	public enum CacheMethod {
		/**
		 * 获取缓存内容
		 */
		GET,
		/**
		 * 存储缓存内容
		 */
		PUT,
		/**
		 * 清除一条缓存记录
		 */
		REMOVE,
		/**
		 * 清除所有缓存内容
		 */
		CLEAR
	}

	private CacheMethod cacheMethod;

	/**
	 * 存储的缓存内容
	 */
	private Object cacheData;

	private static Map<String, CacheDatabaseHelper> cacheHelperMap;
	static {
		cacheHelperMap = new HashMap<String, CacheDatabaseHelper>();
	}
	private CacheDatabaseHelper currentCacheHelper;
	private boolean shouldCacheTime; // 是否需要缓存时间

	public CacheTask(Context context, CacheType cacheType) {
		super(context);
		this.cacheType = cacheType;
		this.cacheMethod = CacheMethod.GET;
		String cacheName = null;
		if (this.cacheType == CacheType.NORMAL) {
			this.shouldCacheTime = true;
			cacheName = "normal";
		} else if (this.cacheType == CacheType.PERSISTENT) {
			this.shouldCacheTime = true;
			cacheName = "persistent";
		} else if (this.cacheType == CacheType.THUMBNAIL) {
			cacheName = "thumbnail";
		} else if (this.cacheType == CacheType.PHOTO) {
			cacheName = "photo";
		}else if (this.cacheType == CacheType.NOTKEYBUSSINESS) {
			this.shouldCacheTime = true;
			cacheName = "keybussiness";
		}
		if (cacheName != null) {
			String cachePath = context.getCacheDir().getAbsolutePath();
			this.currentCacheHelper = cacheHelperMap.get(cachePath + cacheName);
			if (this.currentCacheHelper == null) {
				this.currentCacheHelper = new CacheDatabaseHelper(cachePath, cacheName);
				cacheHelperMap.put(cachePath + cacheName, this.currentCacheHelper);
			}
		}
	}

	@Override
	protected Object doInBackground() {
		if (this.currentCacheHelper == null) {
			this.taskStatus = TaskStatus.FAILED;
			return null;
		}
		boolean isSuccess = true;
		Object result = null;
		if (this.getCacheMethod() == CacheMethod.GET) {
			result = this.currentCacheHelper.get(this.getUrl(), this.shouldCacheTime);
			isSuccess = (result != null);

		} else if (this.getCacheMethod() == CacheMethod.PUT) {
			isSuccess = (this.currentCacheHelper.put(this.getUrl(), this.cacheData));
		} else if (this.getCacheMethod() == CacheMethod.CLEAR) {
			this.currentCacheHelper.clear();
		} else if (this.getCacheMethod() == CacheMethod.REMOVE) {
			this.currentCacheHelper.remove(this.getUrl());
		}
		if (isSuccess) {
			this.taskStatus = TaskStatus.FINISHED;
		} else {
			this.taskStatus = TaskStatus.FAILED;
		}
		return result;
	}

	@Override
	protected ThreadPoolExecutor getThreadPoolExecutor() {
		return executor;
	}

	public CacheMethod getCacheMethod() {
		return cacheMethod;
	}

	public void setCacheMethod(CacheMethod cacheMethod) {
		this.cacheMethod = cacheMethod;
	}

	public Object getCacheData() {
		return cacheData;
	}

	public void setCacheData(Object cacheData) {
		this.cacheData = cacheData;
	}

	public CacheType getCacheType() {
		return cacheType;
	}
}

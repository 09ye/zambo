package com.mobilitychina.intf;

import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ThreadPoolExecutor;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.mobilitychina.util.Log;

/**
 * 所有任务的基类
 * 
 * @author chenwang
 * 
 */
public abstract class Task {
	private static final String TAG = "Task";
	protected int maxTryCount = 0;//最大重试次数;
	protected int curTryCount = 0;
	public void setMaxTryCount(int max){
		maxTryCount = max;
	}
	public int getMaxTryCount(){
		return maxTryCount;
	}
	public int getCurTryCount(){
		return curTryCount;
	}
	public enum TaskStatus {
		FINISHED,
		FAILED
	}
	
	protected String url;
	protected Object result;
	protected Object error;
	protected TaskStatus taskStatus;
	public TaskStatus Status(){
		return taskStatus;
	}
	
	protected ITaskListener listener;

	protected static final int MESSAGE_POST_RESULT = 0x1;
	protected static final int MESSAGE_POST_PROGRESS = 0x2;
	protected static final int MESSAGE_POST_RETRY = 0x3;

	protected final Handler handler = new Handler(Looper.getMainLooper()) {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			Object result = msg.obj;
			switch (msg.what) {
			case MESSAGE_POST_RESULT:
				// There is only one result
				Task.this.result = result;
				Task.this.taskResult();
				break;
			case MESSAGE_POST_PROGRESS:
				break;
			case MESSAGE_POST_RETRY:
				curTryCount++;
				if (Task.this.listener != null) {
					Task.this.listener.onTaskTry(Task.this);
					Task.this.future = null;
					Task.this.start();
				}
				break;
			}
		}
	};

	protected FutureTask<Object> future;
	protected Context context;
	protected void taskResult()
	{
		if (Task.this.listener != null) {
			if (Task.this.taskStatus == TaskStatus.FAILED) {
				//Task.this.listener.onTaskFailed(Task.this);
				if(maxTryCount <= curTryCount){
					Task.this.listener.onTaskFailed(Task.this);
				}else{
					handler.sendEmptyMessageDelayed(MESSAGE_POST_RETRY,3000);
				}
			} else {
				Task.this.listener.onTaskFinished(Task.this);
			}
		}
	}
	public Task(Context context) {
		super();
		this.context = context;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Object getResult() {
		return result;
	}

	public ITaskListener getListener() {
		return listener;
	}

	public void setListener(ITaskListener listener) {
		this.listener = listener;
	}

	public Object getError() {
		return error;
	}

	/**
	 * 启动Task，异步方式
	 * 
	 * @return 是否成功启动
	 */
	public boolean start() {
		return innerStart();
	}
	
	protected boolean innerStart() {
		ThreadPoolExecutor executor = this.getThreadPoolExecutor();
		if (executor == null) {
			Log.w(TAG, "Task 异步启动需要依赖线程池");
			return false;
		}
		if (future != null) {
			Log.w(TAG, "Task 不能重复启动");
			return false;
		}
		future = new FutureTask<Object>(new Callable<Object>() {
			@Override
			public Object call() throws Exception {
				// TODO Auto-generated method stub
				return doInBackground();
			}
		}) {
			@Override
			protected void done() {
				try {
					final Object result = get();
					sendResult(result);
				} catch (InterruptedException e) {
				} catch (ExecutionException e) {
					throw new RuntimeException(e);
				} catch (CancellationException e) {
					sendResult(null);
				} catch (Throwable t) {
					throw new RuntimeException(t);
				}
			}

			private void sendResult(Object result) {
				Message message = handler.obtainMessage(MESSAGE_POST_RESULT, result);
				message.sendToTarget();
			}
		};

		executor.execute(future);
		return true;
	}

	/**
	 * 启动Task,同步方式
	 * 
	 * @return 是否成功启动
	 */
	public Object startSync() {
		return doInBackground();
	}

	/**
	 * 取消或停止Task
	 * 
	 * @return 是否成功取消
	 */
	public boolean cancel(boolean mayInterruptIfRunning) {
		this.listener = null;
		if (future == null) {
			Log.w(TAG, "Task 没有启动");
			return false;
		}
		return future.cancel(mayInterruptIfRunning);
	}

	protected abstract Object doInBackground();

	protected abstract ThreadPoolExecutor getThreadPoolExecutor();
}

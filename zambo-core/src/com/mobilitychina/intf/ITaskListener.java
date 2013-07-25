package com.mobilitychina.intf;


public interface ITaskListener {
	/**
	 * 任务完成时回调
	 * @param task
	 */
	public void onTaskFinished(Task task);

	public void onTaskFailed(Task task);

	public void onTaskUpdateProgress(Task task, int count, int total);
	public void onTaskTry(Task task);
}

package com.mobilitychina.zambo.service.message;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import com.mobilitychina.intf.ITaskListener;
import com.mobilitychina.intf.Task;
import com.mobilitychina.zambo.service.SoapService;
import com.mobilitychina.zambo.service.UserInfoManager;
import com.mobilitychina.zambo.service.message.bus.BusDelegate;
import com.mobilitychina.zambo.service.message.bus.ReceiveBus;
import com.mobilitychina.zambo.service.message.bus.SendBus;
import com.mobilitychina.zambo.service.resps.RespFactory;
import com.mobilitychina.zambo.util.ConfigDefinition;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;

/**
 * 消息通知中心
 * @author zywang
 *
 */
public class NotificationService extends Service implements BusDelegate, ITaskListener {
	private static NotificationService mInstance;
	private IBinder binder = new NotificationServiceBinder();
	private SendBus mSender = new SendBus();
	private ReceiveBus mRecevie = new ReceiveBus();
	private Task mtask ;
	private HeartAsyncTask heartTask = new HeartAsyncTask();
	//private Boolean isNeedReSubscribe = false;
	private int mHeartTime = 60000;
	private int mTimeOut = mHeartTime*2;
	private long lastSendTime = Calendar.getInstance().getTimeInMillis();
	public static  NotificationService instance(){
		return mInstance;
	}
	private List<BaseMsgInfo> mSubscribe =  new ArrayList<BaseMsgInfo>();
	private class HeartAsyncTask extends AsyncTask<Void, Void,Void >{

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			while (true){
				MsgSenderInfo msg = new MsgSenderInfo();
				msg.setClientId(UserInfoManager.getInstance().getUserId());
				msg.setMsgId(MsgDefine.MSG_MESSAGE_HEART);
				mSender.addMsg(msg);
				try {
					Thread.sleep(mHeartTime);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					break;
				}
			}
			return null;
			
		}
	
	}
	@Override
	public void onCreate(){
		super.onCreate();
		mInstance = this;
		mSender.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		mSender.addDeleagte(this);
		mRecevie.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		heartTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		Intent intent = new Intent();
		intent.setAction(ConfigDefinition.INTENT_ACTION_SERVICE_SETUP);
		sendBroadcast(intent);
	}
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return binder;
	}
	public class NotificationServiceBinder extends Binder{
		
		NotificationServiceBinder getService(){
			return NotificationServiceBinder.this;
		}
	}
	public int  onStartCommand(Intent intent, int flags, int startId){
		return super.onStartCommand(intent, flags, startId);
	}
	public void sendMsg(BaseMsgInfo msg){
		mSender.addMsg(msg);
		if(((msg.getMsgId()>>8) & MsgDefine.TYPE_SUBSCRIBE) ==MsgDefine.TYPE_SUBSCRIBE){
			mSubscribe.add(msg);
		}
	}
	public void addBusDelegate(BusDelegate delegate){
//		if(!mRecevie.contains(delegate)){
//			mRecevie.add(delegate);
//		}
		mRecevie.addDeleagte(delegate);
	}
	public void removeBusDelegate(BusDelegate delegate){
		mRecevie.removeDeleagte(delegate);
	}
	//处理消息
	@Override
	public Boolean processMsg(BaseMsgInfo msg) {
		// TODO Auto-generated method stub
		mtask = SoapService.sendMsgInfo(msg.getClient(),String.valueOf( msg.getMsgId()), msg.getMsgContent()); 
		mtask.setListener(this);
		mtask.start();
		return true;
	}
	@Override
	public void onTaskFinished(Task task) {
		// TODO Auto-generated method stub
		long time = lastSendTime;
		lastSendTime = Calendar.getInstance().getTimeInMillis();
		if (task != null && task.getResult() != null) {
			List<MsgResInfo> msgInfo = RespFactory.getService().fromResp(
					MsgResInfo.class, task.getResult());

			for (int i = 0; i < msgInfo.size(); i++) {
				mRecevie.addMsg(msgInfo.get(i));
			}

		}
		if(Calendar.getInstance().getTimeInMillis() - time > mTimeOut){
			//超时了。且成功连上。补充订阅。
			for (BaseMsgInfo info : mSubscribe) {
				mSender.addMsg(info);
			}
		}
	}
	@Override
	public void onTaskFailed(Task task) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onTaskUpdateProgress(Task task, int count, int total) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onTaskTry(Task task) {
		// TODO Auto-generated method stub
		
	}

}

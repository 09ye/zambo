package com.mobilitychina.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.NameValuePair;
import org.kobjects.xml.XmlReader;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.kxml2.io.KXmlParser;
import org.kxml2.kdom.Document;
import org.kxml2.kdom.Element;
import org.kxml2.kdom.Node;
import org.xmlpull.v1.XmlPullParser;

import android.content.Context;

import com.mobilitychina.cache.CacheTask;
import com.mobilitychina.cache.CacheTask.CacheMethod;
import com.mobilitychina.intf.ITaskListener;
import com.mobilitychina.intf.Task;
import com.mobilitychina.intf.Task.TaskStatus;
import com.mobilitychina.util.CacheDatabaseHelper.CacheData;
import com.mobilitychina.util.CacheType;
import com.mobilitychina.util.Environment;
import com.mobilitychina.util.Log;

/**
 * 处理Soap任务
 * 
 * @author chenwang
 * 
 */
public class SoapTask extends Task {
	private static final String TAG = "SoapTask";

	private static final int TIME_OUT = 30000;
	private static ThreadPoolExecutor executor;
	static {
		executor = new ThreadPoolExecutor(3, 6, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
	}

	private List<NameValuePair> params;

	private String soapNamespace;
	private String soapMethod;

	private CacheType cacheType;

	public SoapTask(Context context) {
		super(context);
		this.cacheType = CacheType.DISABLE;
		params = new ArrayList<NameValuePair>();
	}

	protected ThreadPoolExecutor getThreadPoolExecutor() {
		return executor;
	}

	public List<NameValuePair> getParams() {
		return params;
	}

	public void setParams(List<NameValuePair> params) {
		this.params = params;
	}

	public String getSoapNamespace() {
		return soapNamespace;
	}

	public void setSoapNamespace(String soapNamespace) {
		this.soapNamespace = soapNamespace;
	}

	public String getSoapMethod() {
		return soapMethod;
	}

	public void setSoapMethod(String soapMethod) {
		this.soapMethod = soapMethod;
	}

	public CacheType getCacheType() {
		return cacheType;
	}

	public void setCacheType(CacheType cacheType) {
		this.cacheType = cacheType;
		if (this.cacheType != CacheType.NORMAL && this.cacheType != CacheType.PERSISTENT && this.cacheType != CacheType.NOTKEYBUSSINESS) {
			this.cacheType = CacheType.DISABLE;
		}
	}

	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		// TODO Auto-generated method stub
		return super.cancel(mayInterruptIfRunning);
	}

	private String generateCacheKey() {
		StringBuffer sb = new StringBuffer();
		sb.append(this.url).append("/").append(this.soapNamespace).append("/").append(this.soapMethod);
		for (NameValuePair param : this.params) {
			sb.append("/").append(param.getName()).append("=").append(param.getValue());
		}
		return sb.toString();
	}
	protected void taskResult()
	{
		if (this.listener != null) {
			if (this.taskStatus == TaskStatus.FAILED) {
				//Task.this.listener.onTaskFailed(Task.this);
				if(maxTryCount <= curTryCount){
					if(this.getCacheType() == CacheType.NOTKEYBUSSINESS){
						CacheTask cacheTask = new CacheTask(this.context, this.cacheType);
						cacheTask.setUrl(this.generateCacheKey());
						cacheTask.setListener(new ITaskListener() {

							@Override
							public void onTaskUpdateProgress(Task task, int count, int total) {
								// TODO Auto-generated method stub

							}

							@Override
							public void onTaskFinished(Task task) {
								Object cacheData = task.getResult();
								if (cacheData != null && cacheData instanceof CacheData) {
									byte[] blob = ((CacheData) cacheData).getBlob();
									if (blob != null) {								
											String value  = new String(blob);
											Object obj = SoapTask.this.xmlToSoapObject(value);
											SoapTask.this.result = obj;
											SoapTask.this.listener.onTaskFinished(SoapTask.this);
											return;
										
									}
								}
								SoapTask.this.listener.onTaskFailed(task);
							}

							@Override
							public void onTaskFailed(Task task) {
								// TODO Auto-generated method stub
								SoapTask.this.listener.onTaskFailed(task);
							}

							@Override
							public void onTaskTry(Task task) {
								// TODO Auto-generated method stub
								
							}
						});
						cacheTask.start();	
					}else{
						this.listener.onTaskFailed(this);
					}
				}else{
					handler.sendEmptyMessageDelayed(MESSAGE_POST_RETRY,3000);
				}
			} else {
				this.listener.onTaskFinished(this);
			}
		}
	}
	@Override
	public boolean start() {
		// TODO Auto-generated method stub
		if (this.getCacheType() == CacheType.NORMAL || this.getCacheType() == CacheType.PERSISTENT) {
			CacheTask cacheTask = new CacheTask(this.context, this.cacheType);
			cacheTask.setUrl(this.generateCacheKey());
			cacheTask.setListener(new ITaskListener() {

				@Override
				public void onTaskUpdateProgress(Task task, int count, int total) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onTaskFinished(Task task) {
					Object cacheData = task.getResult();
					if (cacheData != null && cacheData instanceof CacheData) {
						long time = ((CacheData) cacheData).getTime();
						byte[] blob = ((CacheData) cacheData).getBlob();
						if (blob != null) {
							double offset = System.currentTimeMillis() - time;
							if (SoapTask.this.getCacheType() == CacheType.NORMAL
									&& (offset > 5 * 60 * 1000 || offset < 0)) { // Normal缓存已失效
								// 清除缓存
								CacheTask deleteCacheTask = new CacheTask(SoapTask.this.context,
										SoapTask.this.cacheType);
								deleteCacheTask.setUrl(SoapTask.this.generateCacheKey());
								deleteCacheTask.setCacheMethod(CacheMethod.REMOVE);
								deleteCacheTask.start(); // 异步处理
							} else if (offset > 24 * 60 * 60 * 1000 || offset < 0) { // 持久缓存已到期，刷新数据
								SoapTask.this.result = new String(blob);
								SoapTask.this.listener.onTaskFinished(SoapTask.this);
								return;
							} else {
								SoapTask.this.result = new String(blob);
								SoapTask.this.listener.onTaskFinished(SoapTask.this);
								return;
							}
						}
					}
					SoapTask.this.innerStart();
				}

				@Override
				public void onTaskFailed(Task task) {
					// TODO Auto-generated method stub
					SoapTask.this.innerStart();
				}

				@Override
				public void onTaskTry(Task task) {
					// TODO Auto-generated method stub
					
				}
			});
			cacheTask.start();
			return true;
		} else {
			return super.start();
		}
	}

	protected Object doInBackground() {
		HttpTransportSE ht = getHttpTransportSE(url);
		ht.debug = true;
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

		//envelope.dotNet = true;
		SoapObject rpc = new SoapObject(soapNamespace, soapMethod);
		for (NameValuePair param : this.params) {
			rpc.addProperty(param.getName(), param.getValue());
		}
		envelope.bodyOut = rpc;
		Element[] header = new Element[1];
		header[0] = new Element().createElement(soapNamespace, "SoapHeader");

		Element version = new Element().createElement(soapNamespace, "version");
		version.addChild(Node.TEXT, Environment.getInstance().getVersion());
		Element token = new Element().createElement(soapNamespace, "token");
		token.addChild(Node.TEXT, Environment.getInstance().getToken());
		header[0].addChild(Node.ELEMENT, version);
		header[0].addChild(Node.ELEMENT, token);
		envelope.headerOut = header;
		Log.i(TAG, rpc.toString());

		try {
			ht.call(null, envelope);
			//ht.call(soapNamespace + "/" + soapMethod, envelope);
		} catch (Exception e) {
			this.error = e;
			this.taskStatus = TaskStatus.FAILED;
			return null;
		}
		Object result = null;
		try {
		//	SoapObject result1 = (SoapObject) envelope.bodyIn;
        //    String name = result1.getProperty(0).toString();
			
			result = envelope.getResponse();
			if (result != null) {
				String temp = result.toString();
				Log.i(TAG, temp.substring(0, temp.length() > 256 ? 256 : temp.length() - 1));
			} else {
				Log.e(TAG, "空返回");
			}
		} catch (SoapFault e) {
			this.error = e;
			this.taskStatus = TaskStatus.FAILED;
			Log.e(TAG, e.toString());
			return null;
		} finally {
		}
		this.taskStatus = TaskStatus.FINISHED;

		if (result != null && this.cacheType != CacheType.DISABLE) {
			CacheTask addCacheTask = new CacheTask(this.context, this.cacheType);
			addCacheTask.setUrl(this.generateCacheKey());
			addCacheTask.setCacheMethod(CacheMethod.PUT);
			//SoapFormatter sf = 
			//SoapObject on = new SoapObject("", "");
			StringBuilder sb = new StringBuilder();
			if(result.getClass() == SoapObject.class){
				
				soapObjectToXml(null,(SoapObject)result,sb);
			}
			addCacheTask.setCacheData(sb.toString().getBytes());
			addCacheTask.start();
		}

		return result;
	}

	private void xmlToSpapObject(Element nd,SoapObject so){
		for (int i = 0; i < nd.getChildCount(); i++) {
			Object ob = nd.getChild(i);
			if(ob.getClass() == String.class){
				return ;
			}
			Element el = (Element)ob;
			PropertyInfo pi = new PropertyInfo();
			pi.setName(el.getAttributeValue(0));
			if(el.getName().equalsIgnoreCase("SoapObject")){
				SoapObject sojb = new SoapObject("", "");
				pi.setValue(sojb);
				xmlToSpapObject(el,sojb);
			}else{
				SoapPrimitive sp = new SoapPrimitive("", "anyType", el.getAttributeValue(1));
				pi.setValue(sp);
			}
			so.addProperty(pi);
		}
	}
	private Object xmlToSoapObject(String value) {
		StringReader sr = new StringReader(value);
		try {

			// 找到xml，并加载文档
			KXmlParser parser = new KXmlParser();
			parser.setInput(new StringReader(value));
			Document document = new Document();
			document.parse(parser);
			// 找到根Element
			Element root = document.getRootElement();
			SoapObject so = new SoapObject("", "");
			xmlToSpapObject(root,so);
			return so;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	private void soapPrimitiveToXml(PropertyInfo po,SoapPrimitive sp,StringBuilder sb ){
	
		sb.append("<SoapPrimitive type=\""+po.getName()+  "\"  value=\""+sp.toString()+"\">");
		sb.append("</SoapPrimitive >");
	}
	private void soapObjectToXml(PropertyInfo p,SoapObject so,StringBuilder sb ){
		sb.append("<SoapObject type=\""+((p == null||p.getName()==null) ? "":p.getName())+"\">");
		for (int i = 0; i < so.getPropertyCount(); i++) {
			PropertyInfo po = new PropertyInfo();
			so.getPropertyInfo(i, po);
			if(po != null ){
				Object ob = po.getValue();
				if(ob.getClass() == SoapObject.class){
					SoapObject sobject= (SoapObject)ob;
					soapObjectToXml(po,sobject,sb);
				}else if (ob.getClass() == SoapPrimitive.class){
					soapPrimitiveToXml(po,(SoapPrimitive) ob, sb);
				}
			}
			
		}
		sb.append("</SoapObject>");
		
	}
	public boolean isUsingProxy() {
		String proxyHost = android.net.Proxy.getDefaultHost();
		return proxyHost != null;
	}

	public HttpTransportSE getHttpTransportSE(String url) {
		HttpTransportSE ht;
		if (isUsingProxy()) {
			java.net.Proxy p = new java.net.Proxy(java.net.Proxy.Type.HTTP, new InetSocketAddress(
					android.net.Proxy.getDefaultHost(), android.net.Proxy.getDefaultPort()));
			ht = new HttpTransportSE(p, url, TIME_OUT);
		} else {
			ht = new HttpTransportSE(url, TIME_OUT);
		}
		return ht;
	}
}

package com.mobilitychina.net;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import android.content.Context;

import com.mobilitychina.intf.Task;

/**
 * 处理Http任务
 * 
 * @author chenwang
 * 
 */
public class HttpTask extends Task {
	public static final String GET = HttpGet.METHOD_NAME;
	public static final String POST = HttpPost.METHOD_NAME;

	private static ThreadPoolExecutor executor;
	static {
		executor = new ThreadPoolExecutor(2, 6, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
	}

	private String method;
	private List<NameValuePair> requestHeaders;
	private List<NameValuePair> responseHeaders;
	private InputStream postBody;
	private int statusCode;
	private static final int TIME_OUT = 30000;
	private HttpUriRequest httpRequest;

	public HttpTask(Context context) {
		super(context);
	}
	
	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public List<NameValuePair> getRequestHeaders() {
		return requestHeaders;
	}

	public void setRequestHeaders(List<NameValuePair> requestHeaders) {
		this.requestHeaders = requestHeaders;
	}

	public List<NameValuePair> getResponseHeaders() {
		return responseHeaders;
	}

	public InputStream getPostBody() {
		return postBody;
	}

	public void setPostBody(InputStream postBody) {
		this.postBody = postBody;
	}

	public int getStatusCode() {
		return statusCode;
	}

	@Override
	protected Object doInBackground() {
		HttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, TIME_OUT);
		HttpConnectionParams.setSoTimeout(httpParams, TIME_OUT);
		HttpClient httpClient = new DefaultHttpClient(httpParams);
		try {
			this.httpRequest = this.getUriRequest();
			HttpResponse response = httpClient.execute(this.httpRequest);
			this.statusCode = response.getStatusLine().getStatusCode();
			HttpEntity entity = response.getEntity();
			this.result = EntityUtils.toByteArray(entity);
			entity.consumeContent();

			this.responseHeaders = new ArrayList<NameValuePair>(8);
			for (Header h : response.getAllHeaders()) {
				this.responseHeaders.add(new BasicNameValuePair(h.getName(), h.getValue()));
			}
		} catch (Exception e) {
			this.error = e;
			this.taskStatus = TaskStatus.FAILED;
		}
		this.taskStatus = TaskStatus.FINISHED;
		return this.result;
	}

	protected HttpUriRequest getUriRequest() throws Exception {
		HttpUriRequest request;
		if (HttpTask.GET.equals(this.getMethod())) {
			request = new HttpGet(this.getUrl());
		} else if (HttpTask.POST.equals(this.getMethod())) {
			HttpPost post = new HttpPost(this.getUrl());
			InputStream ins = this.getPostBody();
			if (ins != null) {
				post.setEntity(new InputStreamEntity(ins, ins.available()));
			}
			request = post;
		} else {
			throw new IllegalArgumentException("unknown http method " + this.method);
		}
		if (this.getRequestHeaders() != null) {
			for (NameValuePair e : this.getRequestHeaders()) {
				request.setHeader(e.getName(), e.getValue());
			}
		}
		// 暂不支持代理
		// ConnRouteParams.setDefaultProxy(request.getParams(), proxy);
		return request;
	}

	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		if (super.cancel(mayInterruptIfRunning)) {
			this.httpRequest.abort();
			return true;
		}
		return false;
	}

	@Override
	protected ThreadPoolExecutor getThreadPoolExecutor() {
		return executor;
	}
}

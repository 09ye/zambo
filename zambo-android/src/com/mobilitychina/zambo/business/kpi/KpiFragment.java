package com.mobilitychina.zambo.business.kpi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.mobilitychina.zambo.R;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class KpiFragment extends Fragment {
	public interface OnKpiListener {
		/**
		 * KPI加载完成
		 */
		void onKpiLoadComplete();

		/**
		 * 加载失败
		 */
		void onKpiLoadFailed();
	}

	private OnKpiListener mListener;
	private WebView mWebView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_kpi, container, false);
		this.mWebView = (WebView) view.findViewById(R.id.webViewKpi);
		return view;
	}

	public void setOnKpiListener(OnKpiListener listener) {
		this.mListener = listener;
	}

	public void showKpi(int expect, int plan, int actual, String showTitle) {
		String data = "window.chartdata = { " + "title: '" + showTitle + "'," + "expect: " + expect + "," + "plan: "
				+ plan + "," + "actual:" + actual + "," + "};";
		File jsFile = new File("/data/data/com.mobilitychina.siemens/data01.js");
		if (jsFile.exists()) {
			jsFile.delete();
		}
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(jsFile);
			fos.write(data.getBytes());
			fos.flush();
			fos.close();
		} catch (Exception e) {
			if (this.mListener != null) {
				this.mListener.onKpiLoadFailed();
			}
			return;
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
				}
			}
		}

		// 显示内容
		mWebView.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				if (mListener != null) {
					mListener.onKpiLoadComplete();
				}
			}

			@Override
			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
				// TODO Auto-generated method stub
				super.onReceivedError(view, errorCode, description, failingUrl);
				if (mListener != null) {
					mListener.onKpiLoadFailed();
				}
			}

		});
		mWebView.setEnabled(false);
		mWebView.setBackgroundColor(0x00000000);
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		mWebView.clearCache(true);
		mWebView.loadUrl("file:///android_asset/chart01.html");
	}
}

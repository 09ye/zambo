package com.mobilitychina.zambo.service;

import com.mobilitychina.util.Log;
import com.mobilitychina.zambo.app.ZamboApplication;
import com.mobilitychina.zambo.util.ConfigDefinition;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 用户信息管理
 * 
 * @author chenwang
 * 
 */
public class UserInfoManager {
	/**
	 * 手机号，当前将手机号作为帐号处理
	 */
	private String phone;
	/**
	 * 用户名称
	 */
	private String name;
	/**
	 * 用户密码，需要加密存储
	 */
	private String password;

	/**
	 * 上次登录时间
	 */
	private long lastLoginTime;

	/**
	 * 职位ID
	 */
	private String posId;
	
	/**
	 * 是否有下属
	 */
	private boolean isLeader;
	/**
	 * 收藏的客户
	 */
	private String favoriteId;
	
	private static UserInfoManager _instance;

	public static synchronized UserInfoManager getInstance() {
		if (_instance == null) {
			_instance = new UserInfoManager();
			_instance.sync(ZamboApplication.getInstance(),false);
		}
		return _instance;
	}

	/**
	 * 同步数据
	 * 
	 * @param context
	 * @param isWrite
	 */
	public void sync(Context context, boolean isWrite) {
		SharedPreferences pref = context.getSharedPreferences(ConfigDefinition.PREFS_DATA, Context.MODE_PRIVATE);
		if (isWrite) {
			SharedPreferences.Editor editor = pref.edit();
			if (phone != null) {
				editor.putString("phone", phone);
			}
			if (name != null) {
				editor.putString("name", name);
			}
			if (password != null) {
				editor.putString("password", password);
			}
			editor.putLong("lastlogintime", lastLoginTime);
			if (posId != null) {
				editor.putString("posid", posId);
			}
			if (favoriteId != null) {
				editor.putString("favoriteId", favoriteId);
			}
			editor.commit();
		} else {
			this.phone = pref.getString("phone", null);
			this.name = pref.getString("name", null);
			this.password = pref.getString("password", null);
			this.lastLoginTime = pref.getLong("lastlogintime", 0);
			this.posId = pref.getString("posid", null);
			this.favoriteId = pref.getString("favoriteId", null);
		}
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public long getLastLoginTime() {
		return lastLoginTime;
	}

	public void setLastLoginTime(long lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}

	public String getPosId() {
		return posId;
	}

	public void setPosId(String posId) {
		this.posId = posId;
	}

	public boolean isLeader() {
		return isLeader;
	}

	public void setLeader(boolean isLeader) {
		this.isLeader = isLeader;
	}
	
	
	public String getFavoriteId() {
		return favoriteId;
	}

	public void setFavoriteId(String favoriteId) {
		this.favoriteId = favoriteId;
	}

	public void print() {
		Log.i("userInfo","phone="+phone+" pwd="+password+" name="+name+" posid="+posId+" isLeader="+isLeader);
	}
	
}

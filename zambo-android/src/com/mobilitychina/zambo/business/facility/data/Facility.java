package com.mobilitychina.zambo.business.facility.data;

import java.io.Serializable;

import android.text.TextUtils;

import com.mobilitychina.zambo.service.resps.KSoapRespField;

public class Facility implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@KSoapRespField(name = "itemId")
	private String id;
	@KSoapRespField(name = "itemName")
	private String name;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int hashCode() {
		int hascode = 101;
		if (!TextUtils.isEmpty(id)) {
			hascode += id.hashCode();
		}
		return hascode;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		if (!(o instanceof Facility)) {
			return false;
		}
		if (TextUtils.isEmpty(id) || TextUtils.isEmpty(((Facility) o).getId())) {
			return false;
		}
		if (id.equals(((Facility) o).getId())) {
			return true;
		}
		return super.equals(o);
	}

}

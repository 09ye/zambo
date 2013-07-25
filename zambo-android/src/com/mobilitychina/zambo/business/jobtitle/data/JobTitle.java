package com.mobilitychina.zambo.business.jobtitle.data;

import java.io.Serializable;

import com.mobilitychina.zambo.service.resps.KSoapRespField;

public class JobTitle implements Serializable {

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

}

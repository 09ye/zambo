package com.mobilitychina.zambo.business.customer.data;

import java.io.Serializable;

/**
 * 客户类型
 * 
 * @author chenwang
 * 
 */
public class CustomerType implements Serializable {
	private static final long serialVersionUID = 8222467701578677370L;

	/**
	 * 类型ID
	 */
	protected String id;

	/**
	 * 类型名称
	 */
	protected String name;

	/**
	 * 类型描述
	 */
	protected String description;

	/**
	 * 类型下的客户总数
	 */
	protected int customerCount;

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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getCustomerCount() {
		return customerCount;
	}

	public void setCustomerCount(int customerCount) {
		this.customerCount = customerCount;
	}

	/**
	 * 是否为全部类型
	 * @return
	 */
	public boolean isAllType() {
		return this.getName().equalsIgnoreCase("-1");
	}
}

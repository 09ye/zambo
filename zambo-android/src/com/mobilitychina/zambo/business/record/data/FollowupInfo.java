package com.mobilitychina.zambo.business.record.data;

/**
 * 客户随访信息
 * 
 * @author chenwang
 * 
 */
public class FollowupInfo {
	protected String id;

	protected String datelineId;

	protected String type;

	protected String visitDate;

	protected String remark;
	
	protected String kzr_all;
	protected String if_send;

	protected String require_content;
	public void setRequireContent(String value){
		require_content = value;
	}
	public String getRequireContent()
	{
		return require_content;
	}
	public void setIfSend(String value){
		if_send = value;
	}
	public String getIfSend()
	{
		return if_send;
	}
	public void setKzr(String value){
		kzr_all = value;
	}
	public String getKzr()
	{
		return kzr_all;
	}
	protected String productdalei_all;
	
	public void setProductdalei(String value){
		productdalei_all = value;
	}
	public String getProductdalei()
	{
		return productdalei_all;
	}
	
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDatelineId() {
		return datelineId;
	}

	public void setDatelineId(String datelineId) {
		this.datelineId = datelineId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getVisitDate() {
		return visitDate;
	}

	public void setVisitDate(String visitDate) {
		this.visitDate = visitDate;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

}

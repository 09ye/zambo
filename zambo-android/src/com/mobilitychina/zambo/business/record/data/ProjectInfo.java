package com.mobilitychina.zambo.business.record.data;

import java.io.Serializable;

public class ProjectInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// 项目ID
	protected String id;

	// 客户ID
	protected String customerId;

	// 项目名称
	protected String name;

	// 项目编号
	protected String proNumber;

	// 项目类型
	protected String proType;

	// 拜访时间
	protected String visitDate;

	// 项目备注
	protected String remark;

	// 项目状态
	protected String status;

	// 项目状态ID
	protected String statusId;

	// 联系人名称
	protected String contactName;

	// 联系人电话
	protected String contactPhone;

	// OPPTYID
	protected String opptyid;

	// 不明字段
	protected String proBigType;

	// 项目状态信息
	protected String statusText;

	// 项目状态值(0至100的值)
	protected int statusValue;

	protected String kzr_all;
	public String getKzrAll(){
		return kzr_all;
	}
	public void setKzrAll(String value)	{
		kzr_all = value;
	}
	//anyType{item=anyType{addmenu_date=2013-06-15T00:00:00+08:00; custId=22268; kzr_all=3526,3520,gy;; proNumber=3198; proStatus=谈判招标; proStatusId=3225; proType=3249; pro_status_id=5648; project_budget=1; visitDate=2013-06-15 17:03:55; }; }
	protected String addmenu_date ;
	public void setAddMenuDate(String value){
		addmenu_date = value;
	}
	public String getAddMenuDate(){
		return addmenu_date;	
	}
	protected String project_budget;
	public String getProjectBudget(){
		return project_budget;
	}
	public void setProjectedBudget(String value){
		project_budget = value;
	}
	protected String share_id;
	public String getShareId(){
		return share_id;
	}
	public void setShareId(String value){
		share_id = value;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getProNumber() {
		return proNumber;
	}

	public void setProNumber(String proNumber) {
		this.proNumber = proNumber;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getContactName() {
		return contactName;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	public String getContactPhone() {
		return contactPhone;
	}

	public void setContactPhone(String contactPhone) {
		this.contactPhone = contactPhone;
	}

	public String getOpptyid() {
		return opptyid;
	}

	public void setOpptyid(String opptyid) {
		this.opptyid = opptyid;
	}

	public String getProBigType() {
		return proBigType;
	}

	public void setProBigType(String proBigType) {
		this.proBigType = proBigType;
	}

	public String getStatusId() {
		return statusId;
	}

	public void setStatusId(String statusId) {
		this.statusId = statusId;
	}

	public String getProType() {
		return proType;
	}

	public void setProType(String proType) {
		this.proType = proType;
	}

	public String getStatusText() {
		return statusText;
	}

	public void setStatusText(String statusText) {
		this.statusText = statusText;
	}

	public int getStatusValue() {
		return statusValue;
	}

	public void setStatusValue(int statusValue) {
		this.statusValue = statusValue;
	}

}

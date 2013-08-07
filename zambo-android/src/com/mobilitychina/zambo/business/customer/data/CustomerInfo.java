package com.mobilitychina.zambo.business.customer.data;


import com.mobilitychina.zambo.service.resps.KSoapRespField;

public class CustomerInfo {
	// 客户id
	@KSoapRespField(name = "custId")
	protected String id;

	// 客户名称
	protected String custName;
	// 客户简称
	protected String custShortName;
	// 联系人
	protected String contactName;
	// 联系人电话
	protected String contactPhone;
	// 联系人手机
	protected String contactMobile;
	// 渠道类型
	protected String channelType;
	// cust_number
	protected String custNumber;

	// cust_address
	protected String custAddress;
	// mobile_phone
	protected String mobilePhone;
	// 纬度
	protected double latitude;
	// 经度
	protected double longitude;
	// emp_id
	protected Long empId;
	// emp_name
	protected String empName;
	// pos_id
	protected Long posId;
	// 当月计划拜访次数
	protected int planVisitNum;
	// 当月已拜访次数
	protected int visitedNum;
	// cust_detail_id
	protected Long custDetailId;
	// visit_date
	protected String visitDate;
	// emp_pwd
	protected String empPwd;
	// pro_type
	protected String proType;
	// pro_status
	protected String proStatus;
	// cust_status
	protected String custStatus;

	// check
	protected int custCheck;

	// 医院类型
	protected String custType = "S";

	// 医院类型
	protected int totalVisitNum;
	
	// 最近一次的计划拜访时间
	protected String lastPlanVisitDate;

	protected String nextPlanVisitDate;
	protected Boolean isLocked = false;
	
	public Boolean getIsLocked() {
		return isLocked;
	}
	public void setIsLocked(Boolean value){
		this.isLocked = value;
	}
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCustName() {
		return custName;
	}

	public void setCustName(String custName) {
		this.custName = custName;
	}

	public String getCustShortName() {
		return custShortName;
	}

	public void setCustShortName(String custShortName) {
		this.custShortName = custShortName;
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

	public String getContactMobile() {
		return contactMobile;
	}

	public void setContactMobile(String contactMobile) {
		this.contactMobile = contactMobile;
	}

	public String getChannelType() {
		return channelType;
	}

	public void setChannelType(String channelType) {
		this.channelType = channelType;
	}

	public String getCustNumber() {
		return custNumber;
	}

	public void setCustNumber(String custNumber) {
		this.custNumber = custNumber;
	}

	public String getCustAddress() {
		return custAddress;
	}

	public void setCustAddress(String custAddress) {
		this.custAddress = custAddress;
	}

	public String getMobilePhone() {
		return mobilePhone;
	}

	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public Long getEmpId() {
		return empId;
	}

	public void setEmpId(Long empId) {
		this.empId = empId;
	}

	public String getEmpName() {
		return empName;
	}

	public void setEmpName(String empName) {
		this.empName = empName;
	}

	public Long getPosId() {
		return posId;
	}

	public void setPosId(Long posId) {
		this.posId = posId;
	}

	public int getPlanVisitNum() {
		return planVisitNum;
	}

	public void setPlanVisitNum(int planVisitNum) {
		this.planVisitNum = planVisitNum;
	}

	public int getVisitedNum() {
		return visitedNum;
	}

	public void setVisitedNum(int visitedNum) {
		this.visitedNum = visitedNum;
	}

	public Long getCustDetailId() {
		return custDetailId;
	}

	public void setCustDetailId(Long custDetailId) {
		this.custDetailId = custDetailId;
	}

	public String getVisitDate() {
		return visitDate;
	}

	public void setVisitDate(String visitDate) {
		this.visitDate = visitDate;
	}

	public String getEmpPwd() {
		return empPwd;
	}

	public void setEmpPwd(String empPwd) {
		this.empPwd = empPwd;
	}

	public String getProType() {
		return proType;
	}

	public void setProType(String proType) {
		this.proType = proType;
	}

	public String getProStatus() {
		return proStatus;
	}

	public void setProStatus(String proStatus) {
		this.proStatus = proStatus;
	}

	public String getCustStatus() {
		return custStatus;
	}

	public void setCustStatus(String custStatus) {
		this.custStatus = custStatus;
	}

	public int getCustCheck() {
		return custCheck;
	}

	public void setCustCheck(int custCheck) {
		this.custCheck = custCheck;
	}

	public String getCustType() {
		return custType;
	}

	public void setCustType(String custType) {
		this.custType = custType;
	}

	public int getTotalVisitNum() {
		return totalVisitNum;
	}

	public void setTotalVisitNum(int totalVisitNum) {
		this.totalVisitNum = totalVisitNum;
	}

	public String getLastPlanVisitDate() {
		return lastPlanVisitDate;
	}

	public void setLastPlanVisitDate(String lastPlanVisitDate) {
		this.lastPlanVisitDate = lastPlanVisitDate;
	}
	public void setNextPlanVisitDate(String nextPlanVisitDate) {
		this.nextPlanVisitDate = nextPlanVisitDate;
	}
	public String getNextPlanVisitDate() {
		return this.nextPlanVisitDate;
	}
	@Override
	public boolean equals(Object o){
		if(super.equals(o)){
			return true;
		}
		if(o instanceof  CustomerInfo){
			if(((CustomerInfo)o).getId().equalsIgnoreCase(this.getId())){
				return true;
			}
			return false;
		}
		return false;
	}
}

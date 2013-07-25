package com.mobilitychina.zambo.business.plan.data;

import java.io.Serializable;


import com.mobilitychina.zambo.service.resps.KSoapRespField;

public class BasePlanInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@KSoapRespField(name = "datelineId")
	protected int id; // planID

	@KSoapRespField(name = "custNameEn")
	protected String custName; // custName

	@KSoapRespField(name = "custDetailId")
	protected int custDetailId; // custID

	@KSoapRespField(name = "planVisitDate")
	protected String modify_date; // modify_date
	@KSoapRespField(name = "signDate")
	protected String signDate; // modify_date
	
	protected Long pos_id; // 职位ID
	
	protected String cv_flg; // cv_flg

	protected String phone; // 电话号码

	@KSoapRespField(name = "visitNum")
	protected int visitNum; // visited_num

	@KSoapRespField(name = "visitedNum")
	protected int visitedNum; // visited_num
	
	@KSoapRespField(name = "visitDate")
	protected String visitDate; // visited_num

	protected String empId;

	@KSoapRespField(name = "visited")
	protected String visited; // visited

	@KSoapRespField(name = "planStatus")
	protected String planStatus; // visited_num

	public String getSignDate() {
		return signDate;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCustName() {
		return custName;
	}

	public void setCustName(String custName) {
		this.custName = custName;
	}

	public int getCustDetailId() {
		return custDetailId;
	}

	public void setCustDetailId(int custDetailId) {
		this.custDetailId = custDetailId;
	}

	public String getModify_date() {
		return modify_date;
	}

	public void setModify_date(String modify_date) {
		this.modify_date = modify_date;
	}

	public Long getPos_id() {
		return pos_id;
	}

	public void setPos_id(Long pos_id) {
		this.pos_id = pos_id;
	}

	public String getCv_flg() {
		return cv_flg;
	}

	public void setCv_flg(String cv_flg) {
		this.cv_flg = cv_flg;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public int getVisitedNum() {
		return visitedNum;
	}

	public void setVisitedNum(int visitedNum) {
		this.visitedNum = visitedNum;
	}

	public String getEmpId() {
		return empId;
	}

	public void setEmpId(String empId) {
		this.empId = empId;
	}

	public int getVisitNum() {
		return visitNum;
	}

	public void setVisitNum(int visitNum) {
		this.visitNum = visitNum;
	}

	public String getVisitDate() {
		return visitDate;
	}

	public void setVisitDate(String visitDate) {
		this.visitDate = visitDate;
	}

	public String getVisited() {
		return visited;
	}

	public void setVisited(String visited) {
		this.visited = visited;
	}

	public String getPlanStatus() {
		return planStatus;
	}

	public void setPlanStatus(String planStatus) {
		this.planStatus = planStatus;
	}
}

package com.mobilitychina.zambo.business.message.data;

import java.io.Serializable;

import com.mobilitychina.zambo.service.resps.KSoapRespField;

public class BaseSiemensEmpInfo implements Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = 4054988878879986457L;


	@KSoapRespField(name = "posId")
	protected String id;

	
	@KSoapRespField(name = "empId")
	protected String empId;
	@KSoapRespField(name = "level")
	protected String level;

	@KSoapRespField(name = "empName")
	protected String empName;


	@KSoapRespField(name = "visitNum")
	protected int visitNum;

	@KSoapRespField(name = "visitedNum")
	protected int visitedNum;

	protected String pposId;// parent

	protected String hposId;

	@KSoapRespField(name = "pempName")
	protected String pempName;// parent

	protected String phone;

	@KSoapRespField(name = "parentPosId")
	protected String parentPos;

	@KSoapRespField(name = "parentEName")
	protected String parentEName;


	protected String checkFalg;
	
	@KSoapRespField(name = "isParent")
	protected String isParent;
	
	@KSoapRespField(name= "posName")
	protected String posName;


	public String getId() {
		return id;
	}
	public String getLevel() {
		 return level;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getEmpName() {
		return empName;
	}

	public void setEmpName(String empName) {
		this.empName = empName;
	}

	public int getVisitNum() {
		return visitNum;
	}

	public void setVisitNum(int visitNum) {
		this.visitNum = visitNum;
	}

	public int getVisitedNum() {
		return visitedNum;
	}

	public void setVisitedNum(int visitedNum) {
		this.visitedNum = visitedNum;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getPposId() {
		return pposId;
	}

	public void setPposId(String pposId) {
		this.pposId = pposId;
	}

	public String getHposId() {
		return hposId;
	}

	public void setHposId(String hposId) {
		this.hposId = hposId;
	}

	public String getPempName() {
		return pempName;
	}

	public void setPempName(String pempName) {
		this.pempName = pempName;
	}

	public String getEmpId() {
		return empId;
	}

	public void setEmpId(String empId) {
		this.empId = empId;
	}

	public String getParentPos() {
		return parentPos;
	}

	public void setParentPos(String parentPos) {
		this.parentPos = parentPos;
	}

	public String getParentEName() {
		return parentEName;
	}

	public void setParentEName(String parentEName) {
		this.parentEName = parentEName;
	}

	public String getCheckFalg() {
		return checkFalg;
	}

	public void setCheckFalg(String checkFalg) {
		this.checkFalg = checkFalg;
	}

	public String getIsParent() {
		return isParent;
	}
	
	public boolean isParent() {
		return ("Y".equals(this.getIsParent()));
	}

	public void setIsParent(String isParent) {
		this.isParent = isParent;
	}

	public String getPosName() {
		return posName;
	}

	public void setPosName(String posName) {
		this.posName = posName;
	}

	
}

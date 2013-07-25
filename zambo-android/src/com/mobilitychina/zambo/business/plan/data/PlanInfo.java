package com.mobilitychina.zambo.business.plan.data;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PlanInfo extends BasePlanInfo {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PlanInfo() {

	}
    public static  List<PlanInfo>getListByData(List<PlanInfo> list, Date date){
    	List<PlanInfo> newlist = new ArrayList<PlanInfo>(); 
    	if (list == null) {
    		return newlist;
    	}
    	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
    	for (PlanInfo planInfo : list) {
			if(planInfo.getModify_date()!=null){
				if(planInfo.getModify_date().equalsIgnoreCase(df.format(date))){
					newlist.add(planInfo);
				}
			}
		}
    	return newlist;
    }
	public PlanInfo(int id, String custName, int custDetailId, String modify_date, Long pos_id, String cv_flg,
			String phone, String empId, int visitedNum, int visitNum, String visitDate, String visited,
			String planStatus) {

		this.id = id;
		this.custName = custName;
		this.custDetailId = custDetailId;
		this.modify_date = modify_date;
		this.pos_id = pos_id;
		this.cv_flg = cv_flg;
		this.phone = phone;
		this.empId = empId;
		this.visitedNum = visitedNum;
		this.visitNum = visitNum;
		this.visitDate = visitDate;
		this.visited = visited;
		this.planStatus = planStatus;
	}


}

package com.mobilitychina.zambo.business.message.data;

import java.io.Serializable;

public class SiemensEmpInfo extends BaseSiemensEmpInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7731585069259543043L;

	public boolean equals(Object object){
		if(super.equals(object)){
			return true;
		}
		if(object instanceof SiemensEmpInfo ){
			SiemensEmpInfo obj = (SiemensEmpInfo)object;
			if(obj.id != null && this.id!= null && obj.id.equals(this.id)){
				return true;
			}
			return false;
		}else{
			return false;
		}
		
	}
}

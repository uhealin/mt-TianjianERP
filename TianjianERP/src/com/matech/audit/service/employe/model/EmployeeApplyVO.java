package com.matech.audit.service.employe.model;

import com.matech.framework.pub.db.Table;

@Table(name="hr_employee_apply",pk="uuid")
public class EmployeeApplyVO {
	 protected String uuid ;
	 protected String employee_id ;
	 protected String remark ;
	 protected String departmentid ;
	 protected String state ;
	 protected String userid ;
     protected String temp_userid;
     protected String assign_departmentid;
     
     

	 public String getAssign_departmentid() {
		return assign_departmentid;
	}
	public void setAssign_departmentid(String assign_departmentid) {
		this.assign_departmentid = assign_departmentid;
	}
	public String getUuid(){ return this.uuid; }
	 public void setUuid(String uuid){ this.uuid=uuid; }

	 
	 
	 public String getEmployee_id() {
		return employee_id;
	}
	public void setEmployee_id(String employee_id) {
		this.employee_id = employee_id;
	}
	public String getRemark(){ return this.remark; }
	 public void setRemark(String remark){ this.remark=remark; }
	 public String getDepartmentid(){ return this.departmentid; }
	 public void setDepartmentid(String departmentid){ this.departmentid=departmentid; }
	 public String getState(){ return this.state; }
	 public void setState(String state){ this.state=state; }
	 public String getUserid(){ return this.userid; }
	 public void setUserid(String userid){ this.userid=userid; }
	public String getTemp_userid() {
		return temp_userid;
	}
	public void setTemp_userid(String temp_userid) {
		this.temp_userid = temp_userid;
	}
	 
}

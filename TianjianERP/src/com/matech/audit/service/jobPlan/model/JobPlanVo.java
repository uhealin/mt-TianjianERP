package com.matech.audit.service.jobPlan.model;

import com.matech.framework.pub.db.Table;

@Table(name="k_jobPlan",pk="uuid")
public class JobPlanVo {
	 protected String uuid ;
	 protected String areaid ;
	 protected String departmentid ;
	 protected String applyTime ;
	 protected String property ;


	 public String getUuid(){ return this.uuid; }
	 public void setUuid(String uuid){ this.uuid=uuid; }
	 public String getAreaid(){ return this.areaid; }
	 public void setAreaid(String areaid){ this.areaid=areaid; }
	 public String getDepartmentid(){ return this.departmentid; }
	 public void setDepartmentid(String departmentid){ this.departmentid=departmentid; }
	 public String getApplyTime(){ return this.applyTime; }
	 public void setApplyTime(String applyTime){ this.applyTime=applyTime; }
	 public String getProperty(){ return this.property; }
	 public void setProperty(String property){ this.property=property; }
}

package com.matech.audit.service.groupVindicate;

import com.matech.framework.pub.db.Table;

@Table(name="k_Vindicate_group",pk="uuid")
public class GroupVindicateVO {
	protected String uuid ;
	 protected Integer group_id ;
	 protected String group_name ;
	 protected String group_headman ;
	 protected String group_member ;
	 protected String group_departmentid ;
	 protected String group_property ;
	 protected String group_department_name ;
	 protected String group_member_name ;
	 protected String departmentid ;
     protected Double percent;

	 public String getUuid(){ return this.uuid; }
	 public void setUuid(String uuid){ this.uuid=uuid; }
	 public Integer getGroup_id(){ return this.group_id; }
	 public void setGroup_id(Integer group_id){ this.group_id=group_id; }
	 public String getGroup_name(){ return this.group_name; }
	 public void setGroup_name(String group_name){ this.group_name=group_name; }
	 public String getGroup_headman(){ return this.group_headman; }
	 public void setGroup_headman(String group_headman){ this.group_headman=group_headman; }
	 public String getGroup_member(){ return this.group_member; }
	 public void setGroup_member(String group_member){ this.group_member=group_member; }
	 public String getGroup_departmentid(){ return this.group_departmentid; }
	 public void setGroup_departmentid(String group_departmentid){ this.group_departmentid=group_departmentid; }
	 public String getGroup_property(){ return this.group_property; }
	 public void setGroup_property(String group_property){ this.group_property=group_property; }
	 public String getGroup_department_name(){ return this.group_department_name; }
	 public void setGroup_department_name(String group_department_name){ this.group_department_name=group_department_name; }
	 public String getGroup_member_name(){ return this.group_member_name; }
	 public void setGroup_member_name(String group_member_name){ this.group_member_name=group_member_name; }
	 public String getDepartmentid(){ return this.departmentid; }
	 public void setDepartmentid(String departmentid){ this.departmentid=departmentid; }
	public Double getPercent() {
		return percent;
	}
	public void setPercent(Double percent) {
		this.percent = percent;
	}
	
	 
}

package com.matech.audit.service.car.model;

import com.matech.framework.pub.db.Table;

@Table(name="c_apply",pk="uuid")
public class CarApplyVO {
	 protected String uuid ;
	 protected String people ;
	 protected String deptid ;
	 protected String begintime ;
	 protected String endtime ;
	 protected String phone ;
	 protected String termini ;
	 protected String peoplenumber ;
	 protected String motormanid ;
	 protected String reason ;
	 protected String motormanid1 ;
	 protected String motormanphone ;
	 protected String registration ;
	 protected String color ;
	 protected String btime ;
	 protected String etime ;
	 protected String Auditing ;
	 protected String create_time ;
	 protected Integer people_uid ;
	 protected String departmentid ;


	 public String getUuid(){ return this.uuid; }
	 public void setUuid(String uuid){ this.uuid=uuid; }
	 public String getPeople(){ return this.people; }
	 public void setPeople(String people){ this.people=people; }
	 public String getDeptid(){ return this.deptid; }
	 public void setDeptid(String deptid){ this.deptid=deptid; }
	 public String getBegintime(){ return this.begintime; }
	 public void setBegintime(String begintime){ this.begintime=begintime; }
	 public String getEndtime(){ return this.endtime; }
	 public void setEndtime(String endtime){ this.endtime=endtime; }
	 public String getPhone(){ return this.phone; }
	 public void setPhone(String phone){ this.phone=phone; }
	 public String getTermini(){ return this.termini; }
	 public void setTermini(String termini){ this.termini=termini; }
	 public String getPeoplenumber(){ return this.peoplenumber; }
	 public void setPeoplenumber(String peoplenumber){ this.peoplenumber=peoplenumber; }
	 public String getMotormanid(){ return this.motormanid; }
	 public void setMotormanid(String motormanid){ this.motormanid=motormanid; }
	 public String getReason(){ return this.reason; }
	 public void setReason(String reason){ this.reason=reason; }
	 public String getMotormanid1(){ return this.motormanid1; }
	 public void setMotormanid1(String motormanid1){ this.motormanid1=motormanid1; }
	 public String getMotormanphone(){ return this.motormanphone; }
	 public void setMotormanphone(String motormanphone){ this.motormanphone=motormanphone; }
	 public String getRegistration(){ return this.registration; }
	 public void setRegistration(String registration){ this.registration=registration; }
	 public String getColor(){ return this.color; }
	 public void setColor(String color){ this.color=color; }
	 public String getBtime(){ return this.btime; }
	 public void setBtime(String btime){ this.btime=btime; }
	 public String getEtime(){ return this.etime; }
	 public void setEtime(String etime){ this.etime=etime; }
	 public String getAuditing(){ return this.Auditing; }
	 public void setAuditing(String Auditing){ this.Auditing=Auditing; }
	 public String getCreate_time(){ return this.create_time; }
	 public void setCreate_time(String create_time){ this.create_time=create_time; }
	 public Integer getPeople_uid(){ return this.people_uid; }
	 public void setPeople_uid(Integer people_uid){ this.people_uid=people_uid; }
	 public String getDepartmentid(){ return this.departmentid; }
	 public void setDepartmentid(String departmentid){ this.departmentid=departmentid; }
}

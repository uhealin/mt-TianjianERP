package com.matech.audit.service.jobPlan.model;

import com.matech.framework.pub.db.Table;

@Table(name="k_jobPlan_item",pk="uuid")
public class JobPlanItemVo {
	
	 protected String uuid ;
	 protected String mainformid ;
	 protected String jobname ;
	 protected String peoplecount ;
	 protected String qualifications ;
	 protected String typeid ;
	 protected String type ;
	 protected String education ;
	 protected String certificate ;
	 protected String toworktime ;
	 protected String city ;
	 protected String working ;
	 protected String reason ;
	 protected String remark ;
	 protected String state ;
	 protected String property ;


	 public String getUuid(){ return this.uuid; }
	 public void setUuid(String uuid){ this.uuid=uuid; }
	 public String getMainformid(){ return this.mainformid; }
	 public void setMainformid(String mainformid){ this.mainformid=mainformid; }
	 public String getJobname(){ return this.jobname; }
	 public void setJobname(String jobname){ this.jobname=jobname; }
	 public String getPeoplecount(){ return this.peoplecount; }
	 public void setPeoplecount(String peoplecount){ this.peoplecount=peoplecount; }
	 public String getQualifications(){ return this.qualifications; }
	 public void setQualifications(String qualifications){ this.qualifications=qualifications; }
	 public String getTypeid(){ return this.typeid; }
	 public void setTypeid(String typeid){ this.typeid=typeid; }
	 public String getType(){ return this.type; }
	 public void setType(String type){ this.type=type; }
	 public String getEducation(){ return this.education; }
	 public void setEducation(String education){ this.education=education; }
	 public String getCertificate(){ return this.certificate; }
	 public void setCertificate(String certificate){ this.certificate=certificate; }
	 public String getToworktime(){ return this.toworktime; }
	 public void setToworktime(String toworktime){ this.toworktime=toworktime; }
	 public String getCity(){ return this.city; }
	 public void setCity(String city){ this.city=city; }
	 public String getWorking(){ return this.working; }
	 public void setWorking(String working){ this.working=working; }
	 public String getReason(){ return this.reason; }
	 public void setReason(String reason){ this.reason=reason; }
	 public String getRemark(){ return this.remark; }
	 public void setRemark(String remark){ this.remark=remark; }
	 public String getState(){ return this.state; }
	 public void setState(String state){ this.state=state; }
	 public String getProperty(){ return this.property; }
	 public void setProperty(String property){ this.property=property; }

}

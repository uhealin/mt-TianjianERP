package com.matech.audit.service.official.model;

import com.matech.framework.pub.db.Table;

@Table(name="hr_employee_official",pk="uuid")
public class Official {
	protected String uuid ;
	 protected String name ;
	 protected String cardNum ;
	 protected String birthday ;
	 protected String sex ;
	 protected String departmentId ;
	 protected String joinTime ;
	 protected String entryTime ;
	 protected String pactlimit ;
	 protected String education ;
	 protected String school ;
	 protected String specialty ;
	 protected String state ;
	 protected String evaluate ;
	 protected String result ;
	 protected String suggest ;
	 protected String list_state ;
	 protected String rankId ;
	 protected String pactlimitEnd ;
	 protected String remark;
	 protected String zz_date;


	 public String getUuid(){ return this.uuid; }
	 public void setUuid(String uuid){ this.uuid=uuid; }
	 public String getName(){ return this.name; }
	 public void setName(String name){ this.name=name; }
	 public String getCardNum(){ return this.cardNum; }
	 public void setCardNum(String cardNum){ this.cardNum=cardNum; }
	 public String getBirthday(){ return this.birthday; }
	 public void setBirthday(String birthday){ this.birthday=birthday; }
	 public String getSex(){ return this.sex; }
	 public void setSex(String sex){ this.sex=sex; }
	 public String getDepartmentId(){ return this.departmentId; }
	 public void setDepartmentId(String departmentId){ this.departmentId=departmentId; }
	 public String getJoinTime(){ return this.joinTime; }
	 public void setJoinTime(String joinTime){ this.joinTime=joinTime; }
	 public String getEntryTime(){ return this.entryTime; }
	 public void setEntryTime(String entryTime){ this.entryTime=entryTime; }
	 public String getPactlimit(){ return this.pactlimit; }
	 public void setPactlimit(String pactlimit){ this.pactlimit=pactlimit; }
	 public String getEducation(){ return this.education; }
	 public void setEducation(String education){ this.education=education; }
	 public String getSchool(){ return this.school; }
	 public void setSchool(String school){ this.school=school; }
	 public String getSpecialty(){ return this.specialty; }
	 public void setSpecialty(String specialty){ this.specialty=specialty; }
	 public String getState(){ return this.state; }
	 public void setState(String state){ this.state=state; }
	 public String getEvaluate(){ return this.evaluate; }
	 public void setEvaluate(String evaluate){ this.evaluate=evaluate; }
	 public String getResult(){ return this.result; }
	 public void setResult(String result){ this.result=result; }
	 public String getSuggest(){ return this.suggest; }
	 public void setSuggest(String suggest){ this.suggest=suggest; }
	 public String getList_state(){ return this.list_state; }
	 public void setList_state(String list_state){ this.list_state=list_state; }
	 public String getRankId(){ return this.rankId; }
	 public void setRankId(String rankId){ this.rankId=rankId; }
	 public String getPactlimitEnd(){ return this.pactlimitEnd; }
	 public void setPactlimitEnd(String pactlimitEnd){ this.pactlimitEnd=pactlimitEnd; }
	 public String getRemark(){ return this.remark; }
	 public void setRemark(String remark){ this.remark=remark; }
	 public String getZz_date(){ return this.zz_date; }
	 public void setZz_date(String zz_date){ this.zz_date=zz_date; }

}

package com.matech.audit.service.cadet.model;

import com.matech.framework.pub.db.Table;

@Table(name="oa_practice_check",pk="uuid")
public class CadetCheckVO {
	
	
	protected String uuid ;
	 protected String practiceId ;
	 protected String p_startTime ;
	 protected String p_endTime ;
	 protected String report ;
	 protected String register ;
	 protected String project_case ;
	 protected String situation ;
	 protected String suggestion ;
	 protected String name ;
	 protected String departmentId ;
	 protected String sex ;
	 protected String school ;
	 protected String education ;
	 protected String profession ;
	 protected String p_real_start_time ;
	 protected String p_real_end_time ;
	 protected String userid ;
	 protected String director_opinion ;
	 protected String flag;


	 public String getUuid(){ return this.uuid; }
	 public void setUuid(String uuid){ this.uuid=uuid; }
	 public String getPracticeId(){ return this.practiceId; }
	 public void setPracticeId(String practiceId){ this.practiceId=practiceId; }
	 public String getP_startTime(){ return this.p_startTime; }
	 public void setP_startTime(String p_startTime){ this.p_startTime=p_startTime; }
	 public String getP_endTime(){ return this.p_endTime; }
	 public void setP_endTime(String p_endTime){ this.p_endTime=p_endTime; }
	 public String getReport(){ return this.report; }
	 public void setReport(String report){ this.report=report; }
	 public String getRegister(){ return this.register; }
	 public void setRegister(String register){ this.register=register; }
	 public String getProject_case(){ return this.project_case; }
	 public void setProject_case(String project_case){ this.project_case=project_case; }
	 public String getSituation(){ return this.situation; }
	 public void setSituation(String situation){ this.situation=situation; }
	 public String getSuggestion(){ return this.suggestion; }
	 public void setSuggestion(String suggestion){ this.suggestion=suggestion; }
	 public String getName(){ return this.name; }
	 public void setName(String name){ this.name=name; }
	 public String getDepartmentId(){ return this.departmentId; }
	 public void setDepartmentId(String departmentId){ this.departmentId=departmentId; }
	 public String getSex(){ return this.sex; }
	 public void setSex(String sex){ this.sex=sex; }
	 public String getSchool(){ return this.school; }
	 public void setSchool(String school){ this.school=school; }
	 public String getEducation(){ return this.education; }
	 public void setEducation(String education){ this.education=education; }
	 public String getProfession(){ return this.profession; }
	 public void setProfession(String profession){ this.profession=profession; }
	 public String getP_real_start_time(){ return this.p_real_start_time; }
	 public void setP_real_start_time(String p_real_start_time){ this.p_real_start_time=p_real_start_time; }
	 public String getP_real_end_time(){ return this.p_real_end_time; }
	 public void setP_real_end_time(String p_real_end_time){ this.p_real_end_time=p_real_end_time; }
	 public String getUserid(){ return this.userid; }
	 public void setUserid(String userid){ this.userid=userid; }
	 public String getDirector_opinion(){ return this.director_opinion; }
	 public void setDirector_opinion(String director_opinion){ this.director_opinion=director_opinion; }
	 public String getFlag(){ return this.flag; }
	 public void setFlag(String flag){ this.flag=flag; }


}

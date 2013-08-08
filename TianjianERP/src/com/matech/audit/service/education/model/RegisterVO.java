package com.matech.audit.service.education.model;

import com.matech.framework.pub.db.Table;

@Table(name="k_educationregdetail",pk="id")
public class RegisterVO {
	
	 protected Integer id ;
	 protected Integer educationId ;
	 protected Integer userId ;
	 protected String time ;
	 protected String evaResult ;


	 public Integer getId(){ return this.id; }
	 public void setId(Integer id){ this.id=id; }
	 public Integer getEducationId(){ return this.educationId; }
	 public void setEducationId(Integer educationId){ this.educationId=educationId; }
	 public Integer getUserId(){ return this.userId; }
	 public void setUserId(Integer userId){ this.userId=userId; }
	 public String getTime(){ return this.time; }
	 public void setTime(String time){ this.time=time; }
	 public String getEvaResult(){ return this.evaResult; }
	 public void setEvaResult(String evaResult){ this.evaResult=evaResult; }

}

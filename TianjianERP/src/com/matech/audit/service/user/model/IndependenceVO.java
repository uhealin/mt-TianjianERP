package com.matech.audit.service.user.model;

import com.matech.framework.pub.db.Table;

@Table(name="k_indepandence",pk="uuid")
public class IndependenceVO {
	 protected String uuid ;
	 protected String userid ;
	 protected String departmentid ;
	 protected String jarr ;
	 protected String modify_date ;
	 protected String remark ;
	 protected String year ;


	 public String getUuid(){ return this.uuid; }
	 public void setUuid(String uuid){ this.uuid=uuid; }
	 public String getUserid(){ return this.userid; }
	 public void setUserid(String userid){ this.userid=userid; }
	 public String getDepartmentid(){ return this.departmentid; }
	 public void setDepartmentid(String departmentid){ this.departmentid=departmentid; }
	 public String getJarr(){ return this.jarr; }
	 public void setJarr(String jarr){ this.jarr=jarr; }
	 public String getModify_date(){ return this.modify_date; }
	 public void setModify_date(String modify_date){ this.modify_date=modify_date; }
	 public String getRemark(){ return this.remark; }
	 public void setRemark(String remark){ this.remark=remark; }
	 public String getYear(){ return this.year; }
	 public void setYear(String year){ this.year=year; }

}

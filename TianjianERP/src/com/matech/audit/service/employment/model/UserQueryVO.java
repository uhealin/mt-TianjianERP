package com.matech.audit.service.employment.model;

import com.matech.framework.pub.db.Table;

@Table(name="oa_user_query",pk="uuid")
public class UserQueryVO {
	 protected String uuid ;
	 protected String name ;
	 protected String userid ;
	 protected String departmentid ;
	 protected String qry_where ;
	 protected String qry_join ;
	 protected String context ;
	 protected String emtype ;


	 public String getUuid(){ return this.uuid; }
	 public void setUuid(String uuid){ this.uuid=uuid; }
	 public String getName(){ return this.name; }
	 public void setName(String name){ this.name=name; }
	 public String getUserid(){ return this.userid; }
	 public void setUserid(String userid){ this.userid=userid; }
	 public String getDepartmentid(){ return this.departmentid; }
	 public void setDepartmentid(String departmentid){ this.departmentid=departmentid; }
	 public String getQry_where(){ return this.qry_where; }
	 public void setQry_where(String qry_where){ this.qry_where=qry_where; }
	 public String getQry_join(){ return this.qry_join; }
	 public void setQry_join(String qry_join){ this.qry_join=qry_join; }
	 public String getContext(){ return this.context; }
	 public void setContext(String context){ this.context=context; }
	 public String getEmtype(){ return this.emtype; }
	 public void setEmtype(String emtype){ this.emtype=emtype; }
}

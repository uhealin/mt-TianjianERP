package com.matech.audit.service.userpopedom.model;

import com.matech.framework.pub.db.Table;

@Table(name="k_userpopedom",pk="autoid",insertPk=false)
public class UserPeopedomVO {
	
		 protected Integer autoid ;
		 protected String userid ;
		 protected String menuid ;
		 protected String departmentid ;
		 protected String property ;


		 public Integer getAutoid(){ return this.autoid; }
		 public void setAutoid(Integer autoid){ this.autoid=autoid; }
		 public String getUserid(){ return this.userid; }
		 public void setUserid(String userid){ this.userid=userid; }
		 public String getMenuid(){ return this.menuid; }
		 public void setMenuid(String menuid){ this.menuid=menuid; }
		 public String getDepartmentid(){ return this.departmentid; }
		 public void setDepartmentid(String departmentid){ this.departmentid=departmentid; }
		 public String getProperty(){ return this.property; }
		 public void setProperty(String property){ this.property=property; }


}

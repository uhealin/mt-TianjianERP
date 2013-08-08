package com.matech.audit.service.employeeedit1.model;

import com.matech.framework.pub.db.Table;

@Table(name="hr_edit1",pk="uuid")
public class EmployeeeditVO1 {

	 protected String uuid ;
	 protected String content ;


	 public String getUuid(){ return this.uuid; }
	 public void setUuid(String uuid){ this.uuid=uuid; }
	 public String getContent(){ return this.content; }
	 public void setContent(String content){ this.content=content; }
}

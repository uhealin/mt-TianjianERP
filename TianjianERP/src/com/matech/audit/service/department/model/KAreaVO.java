package com.matech.audit.service.department.model;

import com.matech.framework.pub.db.Table;

@Table(name="k_area",pk="autoid",insertPk=false)
public class KAreaVO {

	
	 protected Integer autoid ;
	 protected String name ;
	 protected String organid ;
	 protected String managers ;
	 protected String orderid ;
	 protected String property ;
	 protected String short_name ;
     protected String doc_code;

	 public Integer getAutoid(){ return this.autoid; }
	 public void setAutoid(Integer autoid){ this.autoid=autoid; }
	 public String getName(){ return this.name; }
	 public void setName(String name){ this.name=name; }
	 public String getOrganid(){ return this.organid; }
	 public void setOrganid(String organid){ this.organid=organid; }
	 public String getManagers(){ return this.managers; }
	 public void setManagers(String managers){ this.managers=managers; }
	 public String getOrderid(){ return this.orderid; }
	 public void setOrderid(String orderid){ this.orderid=orderid; }
	 public String getProperty(){ return this.property; }
	 public void setProperty(String property){ this.property=property; }
	 public String getShort_name(){ return this.short_name; }
	 public void setShort_name(String short_name){ this.short_name=short_name; }
	public String getDoc_code() {
		return doc_code;
	}
	public void setDoc_code(String doc_code) {
		this.doc_code = doc_code;
	}

}

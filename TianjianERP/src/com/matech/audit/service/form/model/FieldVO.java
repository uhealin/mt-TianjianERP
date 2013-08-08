package com.matech.audit.service.form.model;

import com.matech.framework.pub.db.Table;

@Table(name="mt_com_form_field",pk="UUID",excludeColumns={"formName"})
public class FieldVO {
	 protected String UUID ;
	 protected String NAME ;
	 protected String ENNAME ;
	 protected String FORMID ;
	 protected Integer ORDERID ;
	 protected String MATECHEXT ;
	 protected String PROPERTY ;
	 protected String PARENTFORMID ;
     protected String formName;

	 public String getUUID(){ return this.UUID; }
	 public void setUUID(String UUID){ this.UUID=UUID; }
	 public String getNAME(){ return this.NAME; }
	 public void setNAME(String NAME){ this.NAME=NAME; }
	 public String getENNAME(){ return this.ENNAME; }
	 public void setENNAME(String ENNAME){ this.ENNAME=ENNAME; }
	 public String getFORMID(){ return this.FORMID; }
	 public void setFORMID(String FORMID){ this.FORMID=FORMID; }
	 public Integer getORDERID(){ return this.ORDERID; }
	 public void setORDERID(Integer ORDERID){ this.ORDERID=ORDERID; }
	 public String getMATECHEXT(){ return this.MATECHEXT; }
	 public void setMATECHEXT(String MATECHEXT){ this.MATECHEXT=MATECHEXT; }
	 public String getPROPERTY(){ return this.PROPERTY; }
	 public void setPROPERTY(String PROPERTY){ this.PROPERTY=PROPERTY; }
	 public String getPARENTFORMID(){ return this.PARENTFORMID; }
	 public void setPARENTFORMID(String PARENTFORMID){ this.PARENTFORMID=PARENTFORMID; }
	public String getFormName() {
		return formName;
	}
	public void setFormName(String formName) {
		this.formName = formName;
	}
	 
	 
}

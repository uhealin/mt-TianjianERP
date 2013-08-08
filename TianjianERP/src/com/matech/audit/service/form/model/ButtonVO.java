package com.matech.audit.service.form.model;

import com.matech.framework.pub.db.Table;

@Table(name="mt_com_form_button",pk="UUID")
public class ButtonVO {
	
	 protected String UUID ;
	 protected String NAME ;
	 protected String ENNAME ;
	 protected String FORMID ;
	 protected Integer ORDERID ;
	 protected String ICON ;
	 protected Integer AFTERGROUP ;
	 protected String ONCLICK ;
	 protected String EXTJS ;
	 protected String PROPERTY ;
	 protected String CLASSNAME ;
	 protected String SQL ;
	 protected String HANDLETYPE ;
	 protected String BUTTONTYPE ;
	 protected String BEFORECLICK ;
	 protected String BEFORECLICKJS ;
	 protected String AFTERCLICK ;
	 protected String AFTERCLICKJS ;


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
	 public String getICON(){ return this.ICON; }
	 public void setICON(String ICON){ this.ICON=ICON; }
	 public Integer getAFTERGROUP(){ return this.AFTERGROUP; }
	 public void setAFTERGROUP(Integer AFTERGROUP){ this.AFTERGROUP=AFTERGROUP; }
	 public String getONCLICK(){ return this.ONCLICK; }
	 public void setONCLICK(String ONCLICK){ this.ONCLICK=ONCLICK; }
	 public String getEXTJS(){ return this.EXTJS; }
	 public void setEXTJS(String EXTJS){ this.EXTJS=EXTJS; }
	 public String getPROPERTY(){ return this.PROPERTY; }
	 public void setPROPERTY(String PROPERTY){ this.PROPERTY=PROPERTY; }
	 public String getCLASSNAME(){ return this.CLASSNAME; }
	 public void setCLASSNAME(String CLASSNAME){ this.CLASSNAME=CLASSNAME; }
	 public String getSQL(){ return this.SQL; }
	 public void setSQL(String SQL){ this.SQL=SQL; }
	 public String getHANDLETYPE(){ return this.HANDLETYPE; }
	 public void setHANDLETYPE(String HANDLETYPE){ this.HANDLETYPE=HANDLETYPE; }
	 public String getBUTTONTYPE(){ return this.BUTTONTYPE; }
	 public void setBUTTONTYPE(String BUTTONTYPE){ this.BUTTONTYPE=BUTTONTYPE; }
	 public String getBEFORECLICK(){ return this.BEFORECLICK; }
	 public void setBEFORECLICK(String BEFORECLICK){ this.BEFORECLICK=BEFORECLICK; }
	 public String getBEFORECLICKJS(){ return this.BEFORECLICKJS; }
	 public void setBEFORECLICKJS(String BEFORECLICKJS){ this.BEFORECLICKJS=BEFORECLICKJS; }
	 public String getAFTERCLICK(){ return this.AFTERCLICK; }
	 public void setAFTERCLICK(String AFTERCLICK){ this.AFTERCLICK=AFTERCLICK; }
	 public String getAFTERCLICKJS(){ return this.AFTERCLICKJS; }
	 public void setAFTERCLICKJS(String AFTERCLICKJS){ this.AFTERCLICKJS=AFTERCLICKJS; }
}

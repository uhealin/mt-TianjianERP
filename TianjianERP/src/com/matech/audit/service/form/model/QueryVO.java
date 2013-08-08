package com.matech.audit.service.form.model;

import com.matech.framework.pub.db.Table;

@Table(name="mt_com_form_query",pk="UUID",excludeColumns={"table_name","table_name_cn",})
public class QueryVO {
	 protected String UUID ;
	 protected String NAME ;
	 protected String ENNAME ;
	 protected String FORMID ;
	 protected Integer ORDERID ;
	 protected String BTYPE ;
	 protected String PROPERTY ;
	 protected Integer BSHOW ;
	 protected Integer BHIDDENROW ;
	 protected Integer BORDER ;
	 protected String ROWFLAG ;
	 protected String WIDTH ;

	 protected String table_name,table_name_cn;

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
	 public String getBTYPE(){ return this.BTYPE; }
	 public void setBTYPE(String BTYPE){ this.BTYPE=BTYPE; }
	 public String getPROPERTY(){ return this.PROPERTY; }
	 public void setPROPERTY(String PROPERTY){ this.PROPERTY=PROPERTY; }
	 public Integer getBSHOW(){ return this.BSHOW; }
	 public void setBSHOW(Integer BSHOW){ this.BSHOW=BSHOW; }
	 public Integer getBHIDDENROW(){ return this.BHIDDENROW; }
	 public void setBHIDDENROW(Integer BHIDDENROW){ this.BHIDDENROW=BHIDDENROW; }
	 public Integer getBORDER(){ return this.BORDER; }
	 public void setBORDER(Integer BORDER){ this.BORDER=BORDER; }
	 public String getROWFLAG(){ return this.ROWFLAG; }
	 public void setROWFLAG(String ROWFLAG){ this.ROWFLAG=ROWFLAG; }
	 public String getWIDTH(){ return this.WIDTH; }
	 public void setWIDTH(String WIDTH){ this.WIDTH=WIDTH; }
	public String getTable_name() {
		return table_name;
	}
	public void setTable_name(String table_name) {
		this.table_name = table_name;
	}
	public String getTable_name_cn() {
		return table_name_cn;
	}
	public void setTable_name_cn(String table_name_cn) {
		this.table_name_cn = table_name_cn;
	}
	 
}

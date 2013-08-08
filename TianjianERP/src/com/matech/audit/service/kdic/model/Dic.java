package com.matech.audit.service.kdic.model;

import com.matech.framework.pub.db.Table;

@Table(name="k_dic",pk="autoId",insertPk=false)
public class Dic {
	
	private String autoId = "" ;
	private String name = "" ;
	private String value = "" ;
	private String ctype = "" ;
	private String userdata = "" ;
	private String property = "" ;
	
	 protected String ext_str1 ;
	 protected String ext_str2 ;
	 protected String ext_str3 ;
	 protected Integer ext_int1 ;
	 protected Integer ext_int2 ;
	 protected Integer ext_int3 ;
	
	public String getProperty() {
		return property;
	}
	public void setProperty(String property) {
		this.property = property;
	}
	public String getUserdata() {
		return userdata;
	}
	public void setUserdata(String userdata) {
		this.userdata = userdata;
	}
	public String getAutoId() {
		return autoId;
	}
	public void setAutoId(String autoId) {
		this.autoId = autoId;
	}
	public String getCtype() {
		return ctype;
	}
	public void setCtype(String ctype) {
		this.ctype = ctype;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getExt_str1() {
		return ext_str1;
	}
	public void setExt_str1(String ext_str1) {
		this.ext_str1 = ext_str1;
	}
	public String getExt_str2() {
		return ext_str2;
	}
	public void setExt_str2(String ext_str2) {
		this.ext_str2 = ext_str2;
	}
	public String getExt_str3() {
		return ext_str3;
	}
	public void setExt_str3(String ext_str3) {
		this.ext_str3 = ext_str3;
	}
	public Integer getExt_int1() {
		return ext_int1;
	}
	public void setExt_int1(Integer ext_int1) {
		this.ext_int1 = ext_int1;
	}
	public Integer getExt_int2() {
		return ext_int2;
	}
	public void setExt_int2(Integer ext_int2) {
		this.ext_int2 = ext_int2;
	}
	public Integer getExt_int3() {
		return ext_int3;
	}
	public void setExt_int3(Integer ext_int3) {
		this.ext_int3 = ext_int3;
	}

}

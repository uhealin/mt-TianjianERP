package com.matech.audit.service.userdef;

public class Userdef {
	private int id;//	编号
	private String contrastid;//	对应ID
	private String name;//	名称
	private String value;//	值
	private String property;//	属性 user 用户,cust　客户,depart　部门,com　单位　
	private String dictype; //字典类型值，设置了这个就会自动出现下拉
	private String selectid;//
	public String getSelectid() {
		return selectid;
	}
	public void setSelectid(String selectid) {
		this.selectid = selectid;
	}
	public String getDictype() {
		return dictype;
	}
	public void setDictype(String dictype) {
		this.dictype = dictype;
	}
		
	public String getContrastid() {
		return contrastid;
	}
	public void setContrastid(String contrastid) {
		this.contrastid = contrastid;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getProperty() {
		return property;
	}
	public void setProperty(String property) {
		this.property = property;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
}

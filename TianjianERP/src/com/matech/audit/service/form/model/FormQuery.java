package com.matech.audit.service.form.model;

public class FormQuery {

	private String uuid;
	private String name; // 字段名
	private String enname; // 字段英文名
	private String formid; // 表单ID
	private int orderid; // 字段显示顺序
	private int bshow; // 0不显示1显示在列表
	private int bhiddenrow; // 是否放到trproperty,0不放1放
	private int border; // 按abs(数值)顺序排序，其中大于零正序小于零降序
	private String btype; // 货币\\字符串左对齐\\字符串居中对齐
	private String property;
	private String rowFlag;
	private String width ;
	private String summaryType;
	
	public String getWidth() {
		return width;
	}
	public void setWidth(String width) {
		this.width = width;
	}
	public String getRowFlag() {
		return rowFlag;
	}
	public void setRowFlag(String rowFlag) {
		this.rowFlag = rowFlag;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEnname() {
		return enname;
	}
	public void setEnname(String enname) {
		this.enname = enname;
	}
	public String getFormid() {
		return formid;
	}
	public void setFormid(String formid) {
		this.formid = formid;
	}
	public int getOrderid() {
		return orderid;
	}
	public void setOrderid(int orderid) {
		this.orderid = orderid;
	}
	public int getBshow() {
		return bshow;
	}
	public void setBshow(int bshow) {
		this.bshow = bshow;
	}
	public int getBhiddenrow() {
		return bhiddenrow;
	}
	public void setBhiddenrow(int bhiddenrow) {
		this.bhiddenrow = bhiddenrow;
	}
	public int getBorder() {
		return border;
	}
	public void setBorder(int border) {
		this.border = border;
	}
	public String getBtype() {
		return btype;
	}
	public void setBtype(String btype) {
		this.btype = btype;
	}
	public String getProperty() {
		return property;
	}
	public void setProperty(String property) {
		this.property = property;
	}
	public String getSummaryType() {
		return summaryType;
	}
	public void setSummaryType(String summaryType) {
		this.summaryType = summaryType;
	}

	
}

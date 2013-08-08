package com.matech.audit.service.form.model;

import com.matech.framework.pub.db.Table;

@Table(name="mt_com_form_field",pk="uuid")
public class FormField {
	private String uuid;
	private String name; // 字段名
	private String enname; // 字段英文名
	private String formid; // 表单ID
	private int orderid; // 字段显示顺序
	private String matechext;
	private String property;


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

	public String getMatechext() {
		return matechext;
	}

	public void setMatechext(String matechext) {
		this.matechext = matechext;
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}
}

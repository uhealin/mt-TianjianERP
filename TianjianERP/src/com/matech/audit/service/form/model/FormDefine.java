package com.matech.audit.service.form.model;

import com.matech.framework.pub.db.Table;


public class FormDefine {
	private String uuid; // uuid
	private String name;// 中文名
	private String enname; // 英文名
	private String tableName;//真正的表名
	private String definestr;// 自定义字符串
	private String extclass;// 维护时的扩展类
	private String udate;// 最后修改时间
	private String uname;// 最后修改人
	private String selecttype;// 单选或多选
	private String property;// 备用
	private String tableType;//创建表类型（0  自动创建表 ，  1  手工创建表)
	private String formType;//表单类型
	private String listSql;
	private String listHtml;
	private String thead;

	public String getListSql() {
		return listSql;
	}

	public void setListSql(String listSql) {
		this.listSql = listSql;
	}

	public String getListHtml() {
		return listHtml;
	}

	public void setListHtml(String listHtml) {
		this.listHtml = listHtml;
	}

	public String getFormType() {
		return formType;
	}

	public void setFormType(String formType) {
		this.formType = formType;
	}

	public String getTableType() {
		return tableType;
	}

	public void setTableType(String tableType) {
		this.tableType = tableType;
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

	public String getDefinestr() {
		return definestr;
	}

	public void setDefinestr(String definestr) {
		this.definestr = definestr;
	}

	public String getExtclass() {
		return extclass;
	}

	public void setExtclass(String extclass) {
		this.extclass = extclass;
	}

	public String getUdate() {
		return udate;
	}

	public void setUdate(String udate) {
		this.udate = udate;
	}

	public String getUname() {
		return uname;
	}

	public void setUname(String uname) {
		this.uname = uname;
	}

	public String getSelecttype() {
		return selecttype;
	}

	public void setSelecttype(String selecttype) {
		this.selecttype = selecttype;
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public String toString() {
		return this.uuid + "&" + this.name + "&" + this.enname + "&"
				+ this.definestr + "&" + this.extclass + "&" + this.udate + "&"
				+ this.uname + "&" + this.selecttype + "&" + this.property;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getThead() {
		return thead;
	}

	public void setThead(String thead) {
		this.thead = thead;
	}
	
	
}

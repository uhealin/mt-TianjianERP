package com.matech.audit.service.form.model;

public class FormButton {
	private String uuid;
	private String name; // 中文名
	private String enname; // 英文名
	private String formid; // 所属表单
	private int orderid; // 排序编号
	private String icon; // 图标
	private int aftergroup; // 0或1，1的话会在按钮后显示|分隔
	private String onclick; // 点击调用的JS函数
	private String extjs; // 扩展的JS函数
	private String property; // 备用
	private String className; // 对应处理类
	private String sql; // 对应处理SQL
	private String handleType; // 处理类型 0 JS, 1 类,
	private String buttonType; // 按钮类型 0 默认 , 1自定义

	private String beforeClick;
	private String beforeClickJs;

	public String getBeforeClick() {
		return beforeClick;
	}

	public void setBeforeClick(String beforeClick) {
		this.beforeClick = beforeClick;
	}

	public String getBeforeClickJs() {
		return beforeClickJs;
	}

	public void setBeforeClickJs(String beforeClickJs) {
		this.beforeClickJs = beforeClickJs;
	}

	public String getAfterClickJs() {
		return afterClickJs;
	}

	public void setAfterClickJs(String afterClickJs) {
		this.afterClickJs = afterClickJs;
	}

	public String getAfterClick() {
		return afterClick;
	}

	public void setAfterClick(String afterClick) {
		this.afterClick = afterClick;
	}

	private String afterClickJs;
	private String afterClick;

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

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public int getAftergroup() {
		return aftergroup;
	}

	public void setAftergroup(int aftergroup) {
		this.aftergroup = aftergroup;
	}

	public String getOnclick() {
		return onclick;
	}

	public void setOnclick(String onclick) {
		this.onclick = onclick;
	}

	public String getExtjs() {
		return extjs;
	}

	public void setExtjs(String extjs) {
		this.extjs = extjs;
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public String getHandleType() {
		return handleType;
	}

	public void setHandleType(String handleType) {
		this.handleType = handleType;
	}

	public String getButtonType() {
		return buttonType;
	}

	public void setButtonType(String buttonType) {
		this.buttonType = buttonType;
	}

}

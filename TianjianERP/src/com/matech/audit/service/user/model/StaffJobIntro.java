package com.matech.audit.service.user.model;

/**
 * 员工报到(学习、工作简历介绍)
 * @author Ymm
 * k_staffjobintro
 */
public class StaffJobIntro {
	private String autoId; //int(11) NOT NULL
	private String id ;//(100) NULL外键
	private String ctype ;//(100) NULL类型
	private String startDate ;//(100) NULL开始时间
	private String endDate ;//(100) NULL结束时间
	private String content;// text NULL学习、工作单位及职位描述
	private String property ;//(200) NULL备用
	private String createDate;// varchar(150) NULL创建日期
	private String createUser;// varchar(100) NULL创建人
	private String createDepartment;// varchar(100) NULL创建部门
	
	public String getCreateDate() {
		return createDate;
	}
	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}
	public String getCreateUser() {
		return createUser;
	}
	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}
	public String getCreateDepartment() {
		return createDepartment;
	}
	public void setCreateDepartment(String createDepartment) {
		this.createDepartment = createDepartment;
	}
	public String getAutoId() {
		return autoId;
	}
	public void setAutoId(String autoId) {
		this.autoId = autoId;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCtype() {
		return ctype;
	}
	public void setCtype(String ctype) {
		this.ctype = ctype;
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getProperty() {
		return property;
	}
	public void setProperty(String property) {
		this.property = property;
	}
	
	
}

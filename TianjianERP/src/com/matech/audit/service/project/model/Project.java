package com.matech.audit.service.project.model;

/**
 * <p>Title: 项目信息</p>
 * <p>Description: 项目信息</p>
 * <p>Copyright: Copyright (c) 2006, 2008 MaTech Corporation.All rights reserved. </p>
 * <p>Company: Matech  广州铭太信息科技有限公司</p>
 *
 * 本程序及其相关的所有资源均为铭太科技公司研发中心审计开发组所有，
 * 版本发行及解释权归研发中心，公司网站为：http://www.matech.cn
 *
 * 贡献者团队:
 *     铭太科技 - 研发中心，审计开发组
 *
 * @author void
 * 2007-7-30
 */
public class Project {
	
	private String projectId;	//项目编号
	private String auditDept;	//主审单位编号
	private String customerId;	//被审单位编号
	private String accPackageId;	//账套编号
	private String auditType;	//模版类型
	private String auditPara;	//审计类型
	private String projectName;	//项目名称
	private String projectCreated;	//项目创建日期
	private String projectEnd;	//项目结束日期
	private String state;	//项目状态
	private String auditPeople;	//审计人员
	private String property;	//项目属性
	private String auditTimeBegin;	//审计区间开始时间
	private String auditTimeEnd;	//审计区间结束时间
	private String realStartDate;	//计划开始时间
	private String realEndDate;	//计划结束时间
	private String TemplateType = "" ; //模板类型
	private String departmentId;
	private String standbyName;
	private String groupName;
	private String groupProjectName;
	private String groupProjectId;
	private String projectType;
	private String createTime;
	private String createUser;
	private String projectYear;
	private String shortName;
	private String postil;
	
	public String getPostil() {
		return postil;
	}
	public void setPostil(String postil) {
		this.postil = postil;
	}
	public String getProjectYear() {
		return projectYear;
	}
	public void setProjectYear(String projectYear) {
		this.projectYear = projectYear;
	}
	public String getDepartmentId() {
		return departmentId;
	}
	public void setDepartmentId(String departmentId) {
		this.departmentId = departmentId;
	}
	public String getStandbyName() {
		return standbyName;
	}
	public void setStandbyName(String standbyName) {
		this.standbyName = standbyName;
	}
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public String getGroupProjectName() {
		return groupProjectName;
	}
	public void setGroupProjectName(String groupProjectName) {
		this.groupProjectName = groupProjectName;
	}
	public String getGroupProjectId() {
		return groupProjectId;
	}
	public void setGroupProjectId(String groupProjectId) {
		this.groupProjectId = groupProjectId;
	}
	public String getProjectType() {
		return projectType;
	}
	public void setProjectType(String projectType) {
		this.projectType = projectType;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getCreateUser() {
		return createUser;
	}
	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}
	public String getAccPackageId() {
		return accPackageId;
	}
	/**
	 * @return the projectId
	 */
	public String getProjectId() {
		return projectId;
	}
	/**
	 * @param projectId the projectId to set
	 */
	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}
	/**
	 * @return the auditDept
	 */
	public String getAuditDept() {
		return auditDept;
	}
	/**
	 * @param auditDept the auditDept to set
	 */
	public void setAuditDept(String auditDept) {
		this.auditDept = auditDept;
	}
	/**
	 * @return the customerId
	 */
	public String getCustomerId() {
		return customerId;
	}
	/**
	 * @param customerId the customerId to set
	 */
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	
	/**
	 * @param accPackageId the accPackageId to set
	 */
	public void setAccPackageId(String accPackageId) {
		this.accPackageId = accPackageId;
	}
	/**
	 * @return the auditType
	 */
	public String getAuditType() {
		return auditType;
	}
	/**
	 * @param auditType the auditType to set
	 */
	public void setAuditType(String auditType) {
		this.auditType = auditType;
	}
	/**
	 * @return the auditPara
	 */
	public String getAuditPara() {
		return auditPara;
	}
	/**
	 * @param auditPara the auditPara to set
	 */
	public void setAuditPara(String auditPara) {
		this.auditPara = auditPara;
	}
	/**
	 * @return the projectName
	 */
	public String getProjectName() {
		return projectName;
	}
	/**
	 * @param projectName the projectName to set
	 */
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	/**
	 * @return the projectCreated
	 */
	public String getProjectCreated() {
		return projectCreated;
	}
	/**
	 * @param projectCreated the projectCreated to set
	 */
	public void setProjectCreated(String projectCreated) {
		this.projectCreated = projectCreated;
	}
	/**
	 * @return the projectEnd
	 */
	public String getProjectEnd() {
		return projectEnd;
	}
	/**
	 * @param projectEnd the projectEnd to set
	 */
	public void setProjectEnd(String projectEnd) {
		this.projectEnd = projectEnd;
	}
	/**
	 * @return the state
	 */
	public String getState() {
		return state;
	}
	/**
	 * @param state the state to set
	 */
	public void setState(String state) {
		this.state = state;
	}
	/**
	 * @return the auditPeople
	 */
	public String getAuditPeople() {
		return auditPeople;
	}
	/**
	 * @param auditPeople the auditPeople to set
	 */
	public void setAuditPeople(String auditPeople) {
		this.auditPeople = auditPeople;
	}
	/**
	 * @return the property
	 */
	public String getProperty() {
		return property;
	}
	/**
	 * @param property the property to set
	 */
	public void setProperty(String property) {
		this.property = property;
	}
	/**
	 * @return the auditTimeBegin
	 */
	public String getAuditTimeBegin() {
		return auditTimeBegin;
	}
	/**
	 * @param auditTimeBegin the auditTimeBegin to set
	 */
	public void setAuditTimeBegin(String auditTimeBegin) {
		this.auditTimeBegin = auditTimeBegin;
	}
	/**
	 * @return the auditTimeEnd
	 */
	public String getAuditTimeEnd() {
		return auditTimeEnd;
	}
	/**
	 * @param auditTimeEnd the auditTimeEnd to set
	 */
	public void setAuditTimeEnd(String auditTimeEnd) {
		this.auditTimeEnd = auditTimeEnd;
	}
	/**
	 * @return the realStartDate
	 */
	public String getRealStartDate() {
		return realStartDate;
	}
	/**
	 * @param realStartDate the realStartDate to set
	 */
	public void setRealStartDate(String realStartDate) {
		this.realStartDate = realStartDate;
	}
	/**
	 * @return the realEndDate
	 */
	public String getRealEndDate() {
		return realEndDate;
	}
	/**
	 * @param realEndDate the realEndDate to set
	 */
	public void setRealEndDate(String realEndDate) {
		this.realEndDate = realEndDate;
	}
	public String getTemplateType() {
		return TemplateType;
	}
	public void setTemplateType(String templateType) {
		TemplateType = templateType;
	}
	public String getShortName() {
		return shortName;
	}
	public void setShortName(String shortName) {
		this.shortName = shortName;
	}


}

package com.matech.audit.service.project.model;

/**
 * 
 * <p>
 * Title:集团项目
 * </p>
 * <p>
 * Description: 集团项目
 * </p>
 * <p>
 * Copyright: Copyright (c) 2006, 2008 MaTech Corporation.All rights reserved.
 * </p>
 * <p>
 * Company: Matech 广州铭太信息科技有限公司
 * </p>
 * 
 * 本程序及其相关的所有资源均为铭太科技公司研发中心审计开发组所有， 版本发行及解释权归研发中心，公司网站为：http://www.matech.cn
 * 
 * 贡献者团队: 铭太科技 - 研发中心，审计开发组
 * 
 * @author void 2008-6-27
 */
public class GroupProject {

	private String parentProjectId;// 上级项目编号
	private String projectId;// 项目编号
	private String auditDept;// 审计单位
	private String customerId;// 被审计单位
	private String accPackageId;// 帐套编号
	private String auditType; // 审计类型
	private String auditPara;// 审计类型
	private String projectName;// 项目名称
	private String projectCreated;// 项目创建时间
	private String projectEnd;// 项目结束时间
	private String state;// 项目状态

	private String auditPeople;// 审计人员

	private String property;// 属性

	private String auditTimeBegin;// 项目审计区间开始时间

	private String auditTimeEnd;// 项目审计区间结束时间

	private String realStartDate;// 项目真正开始时间

	private String realEndDate;// 项目真正结束时间

	private String standbyName;// 单位备用名

	private String systemId;// 体系编号,合并报表使用

	public String getAccPackageId() {
		return accPackageId;
	}

	public String getAuditDept() {
		return auditDept;
	}

	public String getAuditPara() {
		return auditPara;
	}

	public String getAuditPeople() {
		return auditPeople;
	}

	public String getAuditTimeBegin() {
		return auditTimeBegin;
	}

	public String getAuditTimeEnd() {
		return auditTimeEnd;
	}

	public String getAuditType() {
		return auditType;
	}

	public String getCustomerId() {
		return customerId;
	}

	public String getParentProjectId() {
		return parentProjectId;
	}

	public String getProjectCreated() {
		return projectCreated;
	}

	public String getProjectEnd() {
		return projectEnd;
	}

	public String getProjectId() {
		return projectId;
	}

	public String getProjectName() {
		return projectName;
	}

	public String getProperty() {
		return property;
	}

	public String getRealEndDate() {
		return realEndDate;
	}

	public String getRealStartDate() {
		return realStartDate;
	}

	public String getStandbyName() {
		return standbyName;
	}

	public String getState() {
		return state;
	}

	public String getSystemId() {
		return systemId;
	}

	public void setAccPackageId(String accPackageId) {
		this.accPackageId = accPackageId;
	}

	public void setAuditDept(String auditDept) {
		this.auditDept = auditDept;
	}

	public void setAuditPara(String auditPara) {
		this.auditPara = auditPara;
	}

	public void setAuditPeople(String auditPeople) {
		this.auditPeople = auditPeople;
	}

	public void setAuditTimeBegin(String auditTimeBegin) {
		this.auditTimeBegin = auditTimeBegin;
	}

	public void setAuditTimeEnd(String auditTimeEnd) {
		this.auditTimeEnd = auditTimeEnd;
	}

	public void setAuditType(String auditType) {
		this.auditType = auditType;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public void setParentProjectId(String parentProjectId) {
		this.parentProjectId = parentProjectId;
	}

	public void setProjectCreated(String projectCreated) {
		this.projectCreated = projectCreated;
	}

	public void setProjectEnd(String projectEnd) {
		this.projectEnd = projectEnd;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public void setRealEndDate(String realEndDate) {
		this.realEndDate = realEndDate;
	}

	public void setRealStartDate(String realStartDate) {
		this.realStartDate = realStartDate;
	}

	public void setStandbyName(String standbyName) {
		this.standbyName = standbyName;
	}

	public void setState(String state) {
		this.state = state;
	}

	public void setSystemId(String systemId) {
		this.systemId = systemId;
	}

}

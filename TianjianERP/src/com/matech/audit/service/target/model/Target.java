package com.matech.audit.service.target.model;

import java.util.Set;

/**
 * <p>Title: 审计目标类</p>
 * <p>Description: 审计目标类</p>
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
 * 2007-7-11
 */
public class Target {
	private String autoID;		//自动编号

	private String projectID;	//项目编号

	private String taskId;	//任务编号

	private String executeIt;	//是否完成

	private String state;	//目标状态

	private String defineId;	//目标自定义号,在风险导向里,改编号其实就是taskcode

	private String auditTarget;	//审计目标

	private String correlationExeProcedure;	//相关审计程序

	private String remark;	//备注

	private String cognizance;	//认定

	private String property;	//属性

	private int proCount;	//统计审计程序的数量

	private Set auditProcedures;	//审计目标下的所有审计程序

	public Set getAuditProcedures() {
		return auditProcedures;
	}

	public void setAuditProcedures(Set auditProcedures) {
		this.auditProcedures = auditProcedures;
	}

	public String getAuditTarget() {
		return auditTarget;
	}

	public void setAuditTarget(String auditTarget) {
		this.auditTarget = auditTarget;
	}

	public String getAutoID() {
		return autoID;
	}

	public void setAutoID(String autoID) {
		this.autoID = autoID;
	}

	public String getCorrelationExeProcedure() {
		return correlationExeProcedure;
	}

	public void setCorrelationExeProcedure(String correlationExeProcedure) {
		this.correlationExeProcedure = correlationExeProcedure;
	}

	public String getDefineId() {
		return defineId;
	}

	public void setDefineId(String defineId) {
		this.defineId = defineId;
	}

	public String getExecuteIt() {
		return executeIt;
	}

	public void setExecuteIt(String executeIt) {
		this.executeIt = executeIt;
	}

	public String getProjectID() {
		return projectID;
	}

	public void setProjectID(String projectID) {
		this.projectID = projectID;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public int getProCount() {
		return proCount;
	}

	public void setProCount(int proCount) {
		this.proCount = proCount;
	}

	public String getCognizance() {
		return cognizance;
	}

	public void setCognizance(String cognizance) {
		this.cognizance = cognizance;
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

}

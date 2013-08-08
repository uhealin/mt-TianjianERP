package com.matech.audit.service.procedure.model;

import java.util.List;

/**
 * <p>
 * Title: 审计程序
 * </p>
 * <p>
 * Description: 审计程序
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
 * @author void 2008-8-14
 */
public class Procedure {
	private String autoId;

	private String projectId;

	private String taskId;

	private String state;

	private String defineId;

	private String auditProcedure;

	private String manuScript;

	private String executor;

	private String remark;

	private String cognizance;

	private String manuLinks;

	private String parentId;

	private int level0;

	private String fullpath;

	private String property;

	private List childProcedureList;

	private String notApplicableMan;

	private String approvalMan;

	private String notApplicableDate;

	private String approvalDate;

	private String reDefineid;

	private String reAuditProcedure;
	
	private String reManuscript;
	
	private String reCognizance;

	private String reExecutor;
	
	private String reManuLinks;

	public String getReManuLinks() {
		return reManuLinks;
	}

	public void setReManuLinks(String reManuLinks) {
		this.reManuLinks = reManuLinks;
	}

	public String getReExecutor() {
		return reExecutor;
	}

	public void setReExecutor(String reExecutor) {
		this.reExecutor = reExecutor;
	}

	public String getReDefineid() {
		return reDefineid;
	}

	public void setReDefineid(String reDefineid) {
		this.reDefineid = reDefineid;
	}

	public String getReAuditProcedure() {
		return reAuditProcedure;
	}

	public void setReAuditProcedure(String reAuditProcedure) {
		this.reAuditProcedure = reAuditProcedure;
	}

	public String getReManuscript() {
		return reManuscript;
	}

	public void setReManuscript(String reManuscript) {
		this.reManuscript = reManuscript;
	}

	public String getReCognizance() {
		return reCognizance;
	}

	public void setReCognizance(String reCognizance) {
		this.reCognizance = reCognizance;
	}

	public String getApprovalMan() {
		return approvalMan;
	}

	public void setApprovalMan(String approvalMan) {
		this.approvalMan = approvalMan;
	}

	public String getNotApplicableMan() {
		return notApplicableMan;
	}

	public void setNotApplicableMan(String notApplicableMan) {
		this.notApplicableMan = notApplicableMan;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public int getLevel0() {
		return level0;
	}

	public void setLevel0(int level0) {
		this.level0 = level0;
	}

	public String getFullpath() {
		return fullpath;
	}

	public void setFullpath(String fullpath) {
		this.fullpath = fullpath;
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public String getManuLinks() {
		return manuLinks;
	}

	public void setManuLinks(String manuLinks) {
		this.manuLinks = manuLinks;
	}

	public String getCognizance() {
		return cognizance;
	}

	public void setCognizance(String cognizance) {
		this.cognizance = cognizance;
	}

	public String getAuditProcedure() {
		return auditProcedure;
	}

	public void setAuditProcedure(String auditProcedure) {
		this.auditProcedure = auditProcedure;
	}

	public String getAutoId() {
		return autoId;
	}

	public void setAutoId(String autoId) {
		this.autoId = autoId;
	}

	public String getDefineId() {
		return defineId;
	}

	public void setDefineId(String defineId) {
		this.defineId = defineId;
	}

	public String getExecutor() {
		return executor;
	}

	public void setExecutor(String executor) {
		this.executor = executor;
	}

	public String getManuScript() {
		return manuScript;
	}

	public void setManuScript(String manuScript) {
		this.manuScript = manuScript;
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
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

	public List getChildProcedureList() {
		return childProcedureList;
	}

	public void setChildProcedureList(List childProcedureList) {
		this.childProcedureList = childProcedureList;
	}

	public String getApprovalDate() {
		return approvalDate;
	}

	public void setApprovalDate(String approvalDate) {
		this.approvalDate = approvalDate;
	}

	public String getNotApplicableDate() {
		return notApplicableDate;
	}

	public void setNotApplicableDate(String notApplicableDate) {
		this.notApplicableDate = notApplicableDate;
	}
}

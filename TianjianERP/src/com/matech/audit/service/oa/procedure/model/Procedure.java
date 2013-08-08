package com.matech.audit.service.oa.procedure.model;

import java.util.List;

/**
 * <p>
 * Title: 程序
 * </p>
 * <p>
 * Description: 程序
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
}

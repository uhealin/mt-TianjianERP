package com.matech.audit.service.audittypetemplate.model;

import java.util.List;

public class AuditTemplateTask {

	private String taskId; // 任务编号

	private String taskCode; // 索引号

	private String taskName; // 任务名称

	private String taskContent; // 任务概述

	private String description; // 备注

	private String parentTaskId; // 上级任务ID

	private String typeId; // 归属模板

	private String manuId; // 底稿ID

	private String manuTemplateId; // 底稿模板ID

	private String property; // 属性

	private String fullPath; // 父子全路径

	private String createdate; // 创建日期

	private String subjectName; // 科目名称

	private String orderId; // 排序编号

	private int isLeaf; // 是否叶子

	private int level; // 层次

	private int ismust; // 是否必做

	private List sheetList;	//表页

	private String auditproperty; //是否需要二级复核或三级复核

	public String getAuditproperty() {
		return auditproperty;
	}

	public void setAuditproperty(String auditproperty) {
		this.auditproperty = auditproperty;
	}

	public String getCreatedate() {
		return createdate;
	}

	public void setCreatedate(String createdate) {
		this.createdate = createdate;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getFullPath() {
		return fullPath;
	}

	public void setFullPath(String fullPath) {
		this.fullPath = fullPath;
	}

	public int getIsLeaf() {
		return isLeaf;
	}

	public void setIsLeaf(int isLeaf) {
		this.isLeaf = isLeaf;
	}

	public int getIsmust() {
		return ismust;
	}

	public void setIsmust(int ismust) {
		this.ismust = ismust;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public String getManuId() {
		return manuId;
	}

	public void setManuId(String manuId) {
		this.manuId = manuId;
	}

	public String getManuTemplateId() {
		return manuTemplateId;
	}

	public void setManuTemplateId(String manuTemplateId) {
		this.manuTemplateId = manuTemplateId;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getParentTaskId() {
		return parentTaskId;
	}

	public void setParentTaskId(String parentTaskId) {
		this.parentTaskId = parentTaskId;
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public String getSubjectName() {
		return subjectName;
	}

	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
	}

	public String getTaskCode() {
		return taskCode;
	}

	public void setTaskCode(String taskCode) {
		this.taskCode = taskCode;
	}

	public String getTaskContent() {
		return taskContent;
	}

	public void setTaskContent(String taskContent) {
		this.taskContent = taskContent;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public List getSheetList() {
		return sheetList;
	}

	public void setSheetList(List sheetList) {
		this.sheetList = sheetList;
	}

	public String getTypeId() {
		return typeId;
	}

	public void setTypeId(String typeId) {
		this.typeId = typeId;
	}
}

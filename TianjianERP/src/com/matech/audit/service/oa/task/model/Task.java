package com.matech.audit.service.oa.task.model;

import java.util.List;

public class Task {

	private String taskId; // 任务编号

	private String taskCode; // 索引号

	private String taskName; // 任务名称

	private String taskContent; // 任务概述

	private String description; // 备注

	private String parentTaskId; // 上级任务ID

	private String projectId; // 归属项目

	private String manuId; // 底稿ID

	private String manuTemplateId; // 底稿模板ID

	private String property; // 属性

	private String fullPath; // 父子全路径

	private String user0; // 分工人

	private String user1; // 编制人

	private String user2; // 二级复核

	private String user3; // 三级复核

	private String user4; // 退回人

	private String user5; // 一级复核

	private String date1; // 编制时间

	private String date2; // 二级复核时间

	private String date3; // 三级复核时间

	private String date4; // 退回时间

	private String date5; // 一级复核时间

	private String createdate; // 创建日期

	private String subjectName; // 科目名称

	private String orderId; // 排序编号

	private int isLeaf; // 是否叶子

	private int level; // 层次

	private int ismust; // 是否必做

	private List sheetList;	//表页

	public String getCreatedate() {
		return createdate;
	}

	public void setCreatedate(String createdate) {
		this.createdate = createdate;
	}

	public String getDate1() {
		return date1;
	}

	public void setDate1(String date1) {
		this.date1 = date1;
	}

	public String getDate2() {
		return date2;
	}

	public void setDate2(String date2) {
		this.date2 = date2;
	}

	public String getDate3() {
		return date3;
	}

	public void setDate3(String date3) {
		this.date3 = date3;
	}

	public String getDate4() {
		return date4;
	}

	public void setDate4(String date4) {
		this.date4 = date4;
	}

	public String getDate5() {
		return date5;
	}

	public void setDate5(String date5) {
		this.date5 = date5;
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

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
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

	public String getUser0() {
		return user0;
	}

	public void setUser0(String user0) {
		this.user0 = user0;
	}

	public String getUser1() {
		return user1;
	}

	public void setUser1(String user1) {
		this.user1 = user1;
	}

	public String getUser2() {
		return user2;
	}

	public void setUser2(String user2) {
		this.user2 = user2;
	}

	public String getUser3() {
		return user3;
	}

	public void setUser3(String user3) {
		this.user3 = user3;
	}

	public String getUser4() {
		return user4;
	}

	public void setUser4(String user4) {
		this.user4 = user4;
	}

	public String getUser5() {
		return user5;
	}

	public void setUser5(String user5) {
		this.user5 = user5;
	}

	public List getSheetList() {
		return sheetList;
	}

	public void setSheetList(List sheetList) {
		this.sheetList = sheetList;
	}
}

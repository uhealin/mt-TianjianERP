package com.matech.audit.service.task.model;

/**
 * <p>Title: 表页</p>
 * <p>Description: 表页</p>
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
 * 2008-5-19
 */
public class SheetTask {

	private String projectId;		//项目编号
	private String taskId;			//主表的
	private String sheetTaskCode;	//表页的
	private String taskCode;		//主表的
	private String sheetName;		//表页名
	private String property;		//属性

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getSheetTaskCode() {
		return sheetTaskCode;
	}

	public void setSheetTaskCode(String sheetTaskCode) {
		this.sheetTaskCode = sheetTaskCode;
	}

	public String getTaskCode() {
		return taskCode;
	}

	public void setTaskCode(String taskCode) {
		this.taskCode = taskCode;
	}

	public String getSheetName() {
		return sheetName;
	}

	public void setSheetName(String sheetName) {
		this.sheetName = sheetName;
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}
}

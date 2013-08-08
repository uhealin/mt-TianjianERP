package com.matech.audit.service.task.model;

import com.matech.framework.pub.model.TreeNode;

public class TaskTreeNode extends TreeNode {
	private String icon;
	private String taskName;
	private String taskCode;
	private String taskId;

	private String helpname;

	private String groupId = "";
	
	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getHelpname() {
		return helpname;
	}

	public void setHelpname(String helpname) {
		this.helpname = helpname;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public String getTaskCode() {
		return taskCode;
	}

	public void setTaskCode(String taskCode) {
		this.taskCode = taskCode;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
}

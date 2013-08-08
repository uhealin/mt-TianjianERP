package com.matech.audit.service.task.model;

public class Tache {
	private String taskId;
	private String taskCode;
	private String taskName;
	private String executor;
	private int isAdd;

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getTaskCode() {
		return taskCode;
	}

	public void setTaskCode(String taskCode) {
		this.taskCode = taskCode;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public int getIsAdd() {
		return isAdd;
	}

	public void setIsAdd(int isAdd) {
		this.isAdd = isAdd;
	}

	public String getExecutor() {
		return executor;
	}

	public void setExecutor(String executor) {
		this.executor = executor;
	}
}

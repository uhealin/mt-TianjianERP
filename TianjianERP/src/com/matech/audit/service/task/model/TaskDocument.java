package com.matech.audit.service.task.model;

public class TaskDocument {
	private int autoId;     //唯一的编号
	
	private int projectId;   //  项目编号       
	
	private int taskId;      //任务编号    
	
	private String docName;  //文档名称   
	
	private String memo ;    //备注      
	
	private String manuscript;   //相关底稿    
	
	private String status ;      // 状态   

	public int getautoId() {
		return autoId;
	}

	public void setautoId(int autoId) {
		this.autoId = autoId;
	}

	public int getprojectId() {
		return projectId;
	}

	public void setprojectId(int projectId) {
		this.projectId = projectId;
	}

	public int gettaskId() {
		return taskId;
	}

	public void settaskId(int taskId) {
		this.taskId = taskId;
	}

	public String getdocName() {
		return docName;
	}

	public void setdocName(String docName) {
		this.docName = docName;
	}

	public String getmemo() {
		return memo;
	}

	public void setmemo(String memo) {
		this.memo = memo;
	}

	public String getmanuscript() {
		return manuscript;
	}

	public void setmanuscript(String manuscript) {
		this.manuscript = manuscript;
	}

	public String getstatus() {
		return status;
	}

	public void setstatus(String status) {
		this.status = status;
	}



}

package com.matech.audit.service.taskAttach.model;

/**
 * 底稿附件
 * 
 * @author yuanquan
 * 
 */
public class TaskAttachTable {
	private String attachId;

	private String attachCode;

	private String projectid;

	private String taskid;

	private String filename;

	private String savedate;

	private String userId;

	private String orderid;

	private String property;

	private String filetempname;

	public String getFiletempname() {
		return filetempname;
	}

	public void setFiletempname(String filetempname) {
		this.filetempname = filetempname;
	}

	public String getAttachCode() {
		return attachCode;
	}

	public void setAttachCode(String attachCode) {
		this.attachCode = attachCode;
	}

	public String getAttachId() {
		return attachId;
	}

	public void setAttachId(String attachId) {
		this.attachId = attachId;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getOrderid() {
		return orderid;
	}

	public void setOrderid(String orderid) {
		this.orderid = orderid;
	}

	public String getProjectid() {
		return projectid;
	}

	public void setProjectid(String projectid) {
		this.projectid = projectid;
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public String getSavedate() {
		return savedate;
	}

	public void setSavedate(String savedate) {
		this.savedate = savedate;
	}

	public String getTaskid() {
		return taskid;
	}

	public void setTaskid(String taskid) {
		this.taskid = taskid;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

}

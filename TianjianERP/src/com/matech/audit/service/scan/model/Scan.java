package com.matech.audit.service.scan.model;

public class Scan {
	private int id;
	private String scanName;
	private String remark;
	private String attachment;
	private String userId;
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getScanName() {
		return scanName;
	}
	public void setScanName(String scanName) {
		this.scanName = scanName;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getAttachment() {
		return attachment;
	}
	public void setAttachment(String attachment) {
		this.attachment = attachment;
	}
}

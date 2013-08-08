package com.matech.audit.service.enterpriseQualification.model;

public class EnterpriseQualification {
	private String uuid;
	private String title;
	private String uploadUserId;
	private String maintainUser;
	private String uploadTime;
	private String attachFileId;
	private String property;
	
	public String getMaintainUser() {
		return maintainUser;
	}
	public void setMaintainUser(String maintainUser) {
		this.maintainUser = maintainUser;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getUploadUserId() {
		return uploadUserId;
	}
	public void setUploadUserId(String uploadUserId) {
		this.uploadUserId = uploadUserId;
	}
	public String getUploadTime() {
		return uploadTime;
	}
	public void setUploadTime(String uploadTime) {
		this.uploadTime = uploadTime;
	}
	public String getAttachFileId() {
		return attachFileId;
	}
	public void setAttachFileId(String attachFileId) {
		this.attachFileId = attachFileId;
	}
	public String getProperty() {
		return property;
	}
	public void setProperty(String property) {
		this.property = property;
	}
	
}

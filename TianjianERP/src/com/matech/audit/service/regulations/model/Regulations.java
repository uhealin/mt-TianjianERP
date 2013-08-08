package com.matech.audit.service.regulations.model;

public class Regulations {
	private String autoId;
	private String title;
	private String contents;
	private String updateTime;
	private String publishUserId;
	private String attachmentId;
	private String memo;
	private String property;
	private String ctype;
	private String lookUser; //查看人员
	private String lookRole; //查看角色
	

	public String getLookUser() {
		return lookUser;
	}

	public void setLookUser(String lookUser) {
		this.lookUser = lookUser;
	}

	public String getLookRole() {
		return lookRole;
	}

	public void setLookRole(String lookRole) {
		this.lookRole = lookRole;
	}

	public String getCtype() {
		return ctype;
	}

	public void setCtype(String ctype) {
		this.ctype = ctype;
	}

	public String getAutoId() {
		return autoId;
	}

	public void setAutoId(String autoId) {
		this.autoId = autoId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContents() {
		return contents;
	}

	public void setContents(String contents) {
		this.contents = contents;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	public String getPublishUserId() {
		return publishUserId;
	}

	public void setPublishUserId(String publishUserId) {
		this.publishUserId = publishUserId;
	}

	public String getAttachmentId() {
		return attachmentId;
	}

	public void setAttachmentId(String attachmentId) {
		this.attachmentId = attachmentId;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}
}

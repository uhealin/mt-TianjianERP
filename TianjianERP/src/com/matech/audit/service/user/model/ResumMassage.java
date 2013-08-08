package com.matech.audit.service.user.model;

public class ResumMassage {
	private String title;
	private String content;
	private String type;
	private String updateTime;
	private String autoid;
	public String getAutoid() {
		return autoid;
	}
	public void setAutoid(String autoid) {
		this.autoid = autoid;
	}
	public String getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	

}

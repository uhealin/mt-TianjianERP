package com.matech.audit.service.educationRegDetail.model;

public class EducationRegDetail {
	private int id;
	private int educationId;
	private int userid;
	private String time;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getEducationId() {
		return educationId;
	}
	public void setEducationId(int educationId) {
		this.educationId = educationId;
	}
	public int getUserid() {
		return userid;
	}
	public void setUserid(int userid) {
		this.userid = userid;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
}

package com.matech.audit.service.employment.model;

public class Education {
	private String id;
	private String schoolType;
	private String schoolName;
	private String startTime;
	private String endTime;
	private String major;
	private String degreeAndDiploma;
	private String linkUserId;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getSchoolType() {
		return schoolType;
	}
	public void setSchoolType(String schoolType) {
		this.schoolType = schoolType;
	}
	public String getSchoolName() {
		return schoolName;
	}
	public void setSchoolName(String schoolName) {
		this.schoolName = schoolName;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public String getMajor() {
		return major;
	}
	public void setMajor(String major) {
		this.major = major;
	}
	public String getDegreeAndDiploma() {
		return degreeAndDiploma;
	}
	public void setDegreeAndDiploma(String degreeAndDiploma) {
		this.degreeAndDiploma = degreeAndDiploma;
	}
	public String getLinkUserId() {
		return linkUserId;
	}
	public void setLinkUserId(String linkUserId) {
		this.linkUserId = linkUserId;
	}
}

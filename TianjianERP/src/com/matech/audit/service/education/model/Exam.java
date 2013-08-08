package com.matech.audit.service.education.model;

public class Exam {
	private String id;
	private String examType;
	private String examSubject;
	private String registrationStartTime;
	private String registrationEndTime;
	private String qualifications;
	private String examTime;
	private String remark;
	private String createUser;
	private String createTime;
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getCreateUser() {
		return createUser;
	}
	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getExamType() {
		return examType;
	}
	public void setExamType(String examType) {
		this.examType = examType;
	}
	public String getExamSubject() {
		return examSubject;
	}
	public void setExamSubject(String examSubject) {
		this.examSubject = examSubject;
	}
	public String getRegistrationStartTime() {
		return registrationStartTime;
	}
	public void setRegistrationStartTime(String registrationStartTime) {
		this.registrationStartTime = registrationStartTime;
	}
	public String getRegistrationEndTime() {
		return registrationEndTime;
	}
	public void setRegistrationEndTime(String registrationEndTime) {
		this.registrationEndTime = registrationEndTime;
	}
	public String getQualifications() {
		return qualifications;
	}
	public void setQualifications(String qualifications) {
		this.qualifications = qualifications;
	}
	public String getExamTime() {
		return examTime;
	}
	public void setExamTime(String examTime) {
		this.examTime = examTime;
	}
}

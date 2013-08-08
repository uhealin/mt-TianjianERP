package com.matech.audit.service.employment.model;

public class WorkHistory {
	private String id;
	private String startTime;
	private String endTime;
	private String company;
	private String position;
	private String major;
	private String supervisor;
	private String salary;
	private String leaveReasons;
	private String linkUserId;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
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
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	public String getPosition() {
		return position;
	}
	public void setPosition(String position) {
		this.position = position;
	}
	public String getMajor() {
		return major;
	}
	public void setMajor(String major) {
		this.major = major;
	}
	public String getSupervisor() {
		return supervisor;
	}
	public void setSupervisor(String supervisor) {
		this.supervisor = supervisor;
	}
	public String getSalary() {
		return salary;
	}
	public void setSalary(String salary) {
		this.salary = salary;
	}
	public String getLeaveReasons() {
		return leaveReasons;
	}
	public void setLeaveReasons(String leaveReasons) {
		this.leaveReasons = leaveReasons;
	}
	public String getLinkUserId() {
		return linkUserId;
	}
	public void setLinkUserId(String linkUserId) {
		this.linkUserId = linkUserId;
	}
}

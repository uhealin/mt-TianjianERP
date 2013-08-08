package com.matech.audit.service.auditPeople.model;

import com.matech.audit.service.user.model.User;

public class AuditPeople {
	private User user;
	private String role;
	private String isAudit;
	private String isTarAndPro;
	private String departmentId;
	private String userId;
	private String appointdate ;

	public String getAppointdate() {
		return appointdate;
	}

	public void setAppointdate(String appointdate) {
		this.appointdate = appointdate;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getIsAudit() {
		return isAudit;
	}

	public void setIsAudit(String isAudit) {
		this.isAudit = isAudit;
	}

	public String getIsTarAndPro() {
		return isTarAndPro;
	}

	public void setIsTarAndPro(String isTarAndPro) {
		this.isTarAndPro = isTarAndPro;
	}

	public String getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(String departmentId) {
		this.departmentId = departmentId;
	}
}

package com.matech.audit.service.group.model;

public class Group {
	private String groupId;
	private String groupName;
	private String parentId;
	private String level;
	private String fullpath;
	private String property;
	private String departmentid;
	
	public String getFullpath() {
		return fullpath;
	}
	public void setFullpath(String fullpath) {
		this.fullpath = fullpath;
	}
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
 
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
	public String getParentId() {
		return parentId;
	}
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	public String getProperty() {
		return property;
	}
	public void setProperty(String property) {
		this.property = property;
	}
	public String getGroupId() {
		return groupId;
	}
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	public String getDepartmentid() {
		return departmentid;
	}
	public void setDepartmentid(String departmentid) {
		this.departmentid = departmentid;
	}
}

package com.matech.audit.service.department.model;

import java.util.List;

import com.matech.framework.pub.db.Table;

@Table(name="k_department",pk="autoId",insertPk=false,excludeColumns={"userList"})
public class DepartmentVO {
	
	private int autoId;
	private String departmentName;
	private int parentId;
	private String popedom;
	private String property;
	private String url;
	private List userList;
	private String typeid;
	private String level0 ;
	private String fullpath ;
	private String postalcode;
	private String address;
	private String ltype ;
	
	
	public String getLtype() {
		return ltype;
	}
	public void setLtype(String ltype) {
		this.ltype = ltype;
	}
	public int getAutoId() {
		return autoId;
	}
	public void setAutoId(int autoId) {
		this.autoId = autoId;
	}
	public String getDepartmentName() {
		return departmentName;
	}
	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}
	public int getParentId() {
		return parentId;
	}
	public void setParentId(int parentId) {
		this.parentId = parentId;
	}
	public String getPopedom() {
		return popedom;
	}
	public void setPopedom(String popedom) {
		this.popedom = popedom;
	}
	public String getProperty() {
		return property;
	}
	public void setProperty(String property) {
		this.property = property;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public List getUserList() {
		return userList;
	}
	public void setUserList(List userList) {
		this.userList = userList;
	}
	public String getTypeid() {
		return typeid;
	}
	public void setTypeid(String typeid) {
		this.typeid = typeid;
	}
	public String getLevel0() {
		return level0;
	}
	public void setLevel0(String level0) {
		this.level0 = level0;
	}
	public String getFullpath() {
		return fullpath;
	}
	public void setFullpath(String fullpath) {
		this.fullpath = fullpath;
	}
	public String getPostalcode() {
		return postalcode;
	}
	public void setPostalcode(String postalcode) {
		this.postalcode = postalcode;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	
	
}

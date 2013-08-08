package com.matech.audit.service.role.model;

public class RoleTable {

	private String id; 
	private String rolename; 
	private String rolevalue; 
	private String Popedom;
	private String property;
	private String ltype;
	private String innername;
	public String getLtype() {
		return ltype;
	}
	public void setLtype(String ltype) {
		this.ltype = ltype;
	}
	public String getInnername() {
		return innername;
	}
	public void setInnername(String innername) {
		this.innername = innername;
	}
	public RoleTable() {
		// TODO Auto-generated constructor stub
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getPopedom() {
		return Popedom;
	}
	public void setPopedom(String popedom) {
		Popedom = popedom;
	}
	public String getProperty() {
		return property;
	}
	public void setProperty(String property) {
		this.property = property;
	}
	public String getRolename() {
		return rolename;
	}
	public void setRolename(String rolename) {
		this.rolename = rolename;
	}
	public String getRolevalue() {
		return rolevalue;
	}
	public void setRolevalue(String rolevalue) {
		this.rolevalue = rolevalue;
	}

}

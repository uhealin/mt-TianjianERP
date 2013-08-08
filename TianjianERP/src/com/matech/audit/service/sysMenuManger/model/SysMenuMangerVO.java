package com.matech.audit.service.sysMenuManger.model;



public class SysMenuMangerVO {
	private int ID ;
	private String menu_id = "";
	private String parentid = "";
	private int depth ;
	private String type = "";
	private String name = "";
	private String act = "";
	private String target = "";
	private String helpact = "";
	private String ActiveX_method = "";
	private String power; //权限(用来获取人员的部门授权)
	

	public String getPower() {
		return power;
	}
	public void setPower(String power) {
		this.power = power;
	}
	public String getMenu_id()
	{
		return this.menu_id;
	}
	public void setMenu_id(String menu_id)
	{
		this.menu_id = menu_id;
	}
	public String getParentid()
	{
		return this.parentid;
	}
	public void setParentid(String parentid)
	{
		this.parentid = parentid;
	}
	
	public String getType()
	{
		return this.type;
	}
	public void setType(String type)
	{
		this.type = type;
	}
	public String getName()
	{
		return this.name;
	}
	public void setName(String name)
	{
		this.name = name;
	}
	public String getAct()
	{
		return this.act;
	}
	public String getHelpact()
	{
		return this.helpact;
	}
	public void setAct(String act)
	{
		this.act = act;
	}
	public void setHelpact(String helpact)
	{
		this.helpact = helpact;
	}
	public String getTarget()
	{
		return this.target;
	}
	public void setTarget(String target)
	{
		this.target = target;
	}
	
	public int getDepth() {
		return depth;
	}
	public int getID() {
		return ID;
	}
	public void setID(int id) {
		ID = id;
	}
	public void setDepth(int depth) {
		this.depth = depth;
	}
	public String getActiveX_method() {
		return ActiveX_method;
	}
	public void setActiveX_method(String activeX_method) {
		ActiveX_method = activeX_method;
	}
}

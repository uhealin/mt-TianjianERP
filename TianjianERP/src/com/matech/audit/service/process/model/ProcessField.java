/**
 * 
 */
package com.matech.audit.service.process.model;

/**
 * @author bill
 *
 */
public class ProcessField {
	
	private String uuid;
	private String name; // 字段名
	private String enname; // 字段英文名
	private String formid; // 表单ID
	
	private String isHide ; // 是否隐藏
	private String isReadOnly ; //是否可写
	private String isProcessVariable ; // 是否放流程变量
	private String type ; //主表字段 子表 或 子表字段
	private String tableName ;
	
	private String processKey ; //对应流程
	private String nodeName ;   //对应流程节点名称
	private String property ; //备用
	
	
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getProperty() {
		return property;
	}
	public void setProperty(String property) {
		this.property = property;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEnname() {
		return enname;
	}
	public void setEnname(String enname) {
		this.enname = enname;
	}
	public String getFormid() {
		return formid;
	}
	public void setFormid(String formid) {
		this.formid = formid;
	}
	public String getIsHide() {
		return isHide;
	}
	public void setIsHide(String isHide) {
		this.isHide = isHide;
	}
	public String getIsReadOnly() {
		return isReadOnly;
	}
	public void setIsReadOnly(String isReadOnly) {
		this.isReadOnly = isReadOnly;
	}
	public String getProcessKey() {
		return processKey;
	}
	public void setProcessKey(String processKey) {
		this.processKey = processKey;
	}
	public String getNodeName() {
		return nodeName;
	}
	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}
	public String getIsProcessVariable() {
		return isProcessVariable;
	}
	public void setIsProcessVariable(String isProcessVariable) {
		this.isProcessVariable = isProcessVariable;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
}

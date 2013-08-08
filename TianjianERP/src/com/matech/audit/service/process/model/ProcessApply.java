/**
 * 
 */
package com.matech.audit.service.process.model;

public class ProcessApply {
	private String id ;
	private String pkey ;
	private String pid ;
	private String foreignId ;
	private String pname ;
	private String applyTime ;
	private String applyUserId ;
	private String property ;
	private String processDes ;
	
	public String getProcessDes() {
		return processDes;
	}
	public void setProcessDes(String processDes) {
		this.processDes = processDes;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getPkey() {
		return pkey;
	}
	public void setPkey(String pkey) {
		this.pkey = pkey;
	}
	public String getPid() {
		return pid;
	}
	public void setPid(String pid) {
		this.pid = pid;
	}
	public String getForeignId() {
		return foreignId;
	}
	public void setForeignId(String foreignId) {
		this.foreignId = foreignId;
	}
	public String getPname() {
		return pname;
	}
	public void setPname(String pname) {
		this.pname = pname;
	}
	public String getApplyTime() {
		return applyTime;
	}
	public void setApplyTime(String applyTime) {
		this.applyTime = applyTime;
	}
	public String getApplyUserId() {
		return applyUserId;
	}
	public void setApplyUserId(String applyUserId) {
		this.applyUserId = applyUserId;
	}
	public String getProperty() {
		return property;
	}
	public void setProperty(String property) {
		this.property = property;
	}
	
	
}

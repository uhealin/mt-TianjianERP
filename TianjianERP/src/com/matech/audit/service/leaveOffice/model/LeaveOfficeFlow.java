package com.matech.audit.service.leaveOffice.model;

/**
 * @author Administrator
 *请假
 */
public class LeaveOfficeFlow {
	

	private String processInstanceId ;//(50) ,    #流程实例ID
	private String uuid ;//(100) ,                        外键
	private String applyuser ;//(50) ,                    #用户ID
	private String applyDate    ;//(50) ,                 #生成 时间
	private String state  ;//(100) ,                       #状态
	private String ctype ; //类型(请假/销假)
	private String property    ;//(50)                     #权限
	
	
	
	public String getCtype() {
		return ctype;
	}
	public void setCtype(String ctype) {
		this.ctype = ctype;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getProcessInstanceId() {
		return processInstanceId;
	}
	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}
	 
	public String getApplyuser() {
		return applyuser;
	}
	public void setApplyuser(String applyuser) {
		this.applyuser = applyuser;
	}
	public String getApplyDate() {
		return applyDate;
	}
	public void setApplyDate(String applyDate) {
		this.applyDate = applyDate;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getProperty() {
		return property;
	}
	public void setProperty(String property) {
		this.property = property;
	}
	  
	  
}

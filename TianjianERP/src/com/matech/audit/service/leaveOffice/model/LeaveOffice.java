package com.matech.audit.service.leaveOffice.model;

/**
 * @author Administrator
 *离职申请
 */
public class LeaveOffice {
	
	  private String uuid ;   //VARCHAR(100) NOT NULL COMMENT 'uuid',
	  private String userId ; //VARCHAR(30) DEFAULT NULL COMMENT '离职人',
	  private String applyDate ; //VARCHAR(30) DEFAULT NULL COMMENT '申请日期',
	  private String predictLeaveDate ; //VARCHAR(30) DEFAULT NULL COMMENT '预计离职日期',
	  private String reason;  //MEDIUMTEXT COMMENT '离职原因',
	  private String property ; //VARCHAR(300) DEFAULT NULL COMMENT '备用',
	  private String status ; 
	  
	  
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getApplyDate() {
		return applyDate;
	}
	public void setApplyDate(String applyDate) {
		this.applyDate = applyDate;
	}
	public String getPredictLeaveDate() {
		return predictLeaveDate;
	}
	public void setPredictLeaveDate(String predictLeaveDate) {
		this.predictLeaveDate = predictLeaveDate;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public String getProperty() {
		return property;
	}
	public void setProperty(String property) {
		this.property = property;
	}
	  
	  
}

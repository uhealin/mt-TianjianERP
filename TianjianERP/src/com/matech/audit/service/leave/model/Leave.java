package com.matech.audit.service.leave.model;

/**
 * @author Administrator
 *请假
 */
public class Leave {
	
    private String uuid ;  //VARCHAR(100) NOT NULL COMMENT 'uuid',
    private String userId ;  //VARCHAR(30) DEFAULT NULL COMMENT '用户编号',
    private String applyDate ;  //VARCHAR(30) DEFAULT NULL COMMENT '申日日期',
    private String leaveTypeId ;  //VARCHAR(20) DEFAULT NULL COMMENT '请假类型编号',
    private String leaveStartTime ;  //VARCHAR(50) DEFAULT NULL COMMENT '请假开始时间',
    private String leaveEndTime ;  //VARCHAR(50) DEFAULT NULL COMMENT '请假结束时间',
    private String leaveHourCount ;  //VARCHAR(50) DEFAULT NULL COMMENT '请假共小时',
    private String destroyStartTime ;  //VARCHAR(50) DEFAULT NULL COMMENT '销假开始时间',
    private String destroyEndTime ;  //VARCHAR(50) DEFAULT NULL COMMENT '销假结束时间',
    private String destroyHourCount ;  //VARCHAR(50) DEFAULT NULL COMMENT '销假共小时',
    private String RealStartTime ;  //VARCHAR(50) DEFAULT NULL COMMENT '只读开始时间',
    private String RealEndTime ;  //VARCHAR(50) DEFAULT NULL COMMENT '只读结束时间',
    private String  realHourCount ;  //VARCHAR(50) DEFAULT NULL COMMENT '只读共小时',
    private String memo;  // MEDIUMTEXT COMMENT '原因',
    private String status ; //状态
    private String property;  // MEDIUMTEXT COMMENT '备注',
	  
    
    
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
	public String getLeaveTypeId() {
		return leaveTypeId;
	}
	public void setLeaveTypeId(String leaveTypeId) {
		this.leaveTypeId = leaveTypeId;
	}
	public String getLeaveStartTime() {
		return leaveStartTime;
	}
	public void setLeaveStartTime(String leaveStartTime) {
		this.leaveStartTime = leaveStartTime;
	}
	public String getLeaveEndTime() {
		return leaveEndTime;
	}
	public void setLeaveEndTime(String leaveEndTime) {
		this.leaveEndTime = leaveEndTime;
	}
	public String getLeaveHourCount() {
		return leaveHourCount;
	}
	public void setLeaveHourCount(String leaveHourCount) {
		this.leaveHourCount = leaveHourCount;
	}
	public String getDestroyStartTime() {
		return destroyStartTime;
	}
	public void setDestroyStartTime(String destroyStartTime) {
		this.destroyStartTime = destroyStartTime;
	}
	public String getDestroyEndTime() {
		return destroyEndTime;
	}
	public void setDestroyEndTime(String destroyEndTime) {
		this.destroyEndTime = destroyEndTime;
	}
	public String getDestroyHourCount() {
		return destroyHourCount;
	}
	public void setDestroyHourCount(String destroyHourCount) {
		this.destroyHourCount = destroyHourCount;
	}
	public String getRealStartTime() {
		return RealStartTime;
	}
	public void setRealStartTime(String realStartTime) {
		RealStartTime = realStartTime;
	}
	public String getRealEndTime() {
		return RealEndTime;
	}
	public void setRealEndTime(String realEndTime) {
		RealEndTime = realEndTime;
	}
	public String getRealHourCount() {
		return realHourCount;
	}
	public void setRealHourCount(String realHourCount) {
		this.realHourCount = realHourCount;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	public String getProperty() {
		return property;
	}
	public void setProperty(String property) {
		this.property = property;
	}
	  
	  
}

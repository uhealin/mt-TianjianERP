package com.matech.audit.service.leaveType.model;

/**
 * @author Administrator
 *请假类型设置
 */
public class LeaveType {
	
	  private String autoId; // INT(11) NOT NULL AUTO_INCREMENT,
	  private String name ; //VARCHAR(200) DEFAULT NULL COMMENT '名称',
	  private String applyLimit ; //VARCHAR(20) DEFAULT NULL COMMENT '提前申请限制',
	  private String yearDayLimit ; //VARCHAR(20) DEFAULT NULL COMMENT '每年天数上限',
	  private String yearCountLimit ; //VARCHAR(20) DEFAULT NULL COMMENT '每年累计上限(次数)',
	  private String monthDayLimit ; //VARCHAR(20) DEFAULT NULL COMMENT '每月天数上限',
	  private String monthCountLimit ; //VARCHAR(20) DEFAULT NULL COMMENT '每月累计上限',
	  private String deductMoney ; //VARCHAR(20) DEFAULT NULL COMMENT '扣工资金额',
	  private String minTime ; //VARCHAR(20) DEFAULT NULL COMMENT '最小时间(起开始扣工资)',
	  private String memo;  // TEXT COMMENT '备注',
	  
	public String getAutoId() {
		return autoId;
	}
	public void setAutoId(String autoId) {
		this.autoId = autoId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getApplyLimit() {
		return applyLimit;
	}
	public void setApplyLimit(String applyLimit) {
		this.applyLimit = applyLimit;
	}
	public String getYearDayLimit() {
		return yearDayLimit;
	}
	public void setYearDayLimit(String yearDayLimit) {
		this.yearDayLimit = yearDayLimit;
	}
	public String getYearCountLimit() {
		return yearCountLimit;
	}
	public void setYearCountLimit(String yearCountLimit) {
		this.yearCountLimit = yearCountLimit;
	}
	public String getMonthDayLimit() {
		return monthDayLimit;
	}
	public void setMonthDayLimit(String monthDayLimit) {
		this.monthDayLimit = monthDayLimit;
	}
	public String getMonthCountLimit() {
		return monthCountLimit;
	}
	public void setMonthCountLimit(String monthCountLimit) {
		this.monthCountLimit = monthCountLimit;
	}
	public String getDeductMoney() {
		return deductMoney;
	}
	public void setDeductMoney(String deductMoney) {
		this.deductMoney = deductMoney;
	}
	public String getMinTime() {
		return minTime;
	}
	public void setMinTime(String minTime) {
		this.minTime = minTime;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
}

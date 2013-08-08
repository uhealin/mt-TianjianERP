package com.matech.audit.service.meetingConsumable.model;

public class MeetingConsumable {
	private String uuid;//
	private String meetingOrderId;// 会议编号
	private String names;//耗材名称
	private String counts;//数量
	private String moneys;//金额
	private String recordUser;//记录人
	private String recordTime;//记录时间
	private String batchNumber;//批次号
	private String property;
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getMeetingOrderId() {
		return meetingOrderId;
	}
	public void setMeetingOrderId(String meetingOrderId) {
		this.meetingOrderId = meetingOrderId;
	}
	public String getNames() {
		return names;
	}
	public void setNames(String names) {
		this.names = names;
	}
	public String getCounts() {
		return counts;
	}
	public void setCounts(String counts) {
		this.counts = counts;
	}
	public String getMoneys() {
		return moneys;
	}
	public void setMoneys(String moneys) {
		this.moneys = moneys;
	}
	public String getRecordUser() {
		return recordUser;
	}
	public void setRecordUser(String recordUser) {
		this.recordUser = recordUser;
	}
	public String getRecordTime() {
		return recordTime;
	}
	public void setRecordTime(String recordTime) {
		this.recordTime = recordTime;
	}
	public String getBatchNumber() {
		return batchNumber;
	}
	public void setBatchNumber(String batchNumber) {
		this.batchNumber = batchNumber;
	}
	public String getProperty() {
		return property;
	}
	public void setProperty(String property) {
		this.property = property;
	}
	
}

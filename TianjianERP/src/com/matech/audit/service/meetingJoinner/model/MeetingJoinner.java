package com.matech.audit.service.meetingJoinner.model;

public class MeetingJoinner {
	private String uuid;
	private String meetingOrderId;//会议预约Id
	private String waitingUserId;//等待参与人Id
	private String initUserId;// 初始化参与人Id
	private String actualUserId;// 实际参与人Id
	private String answerUserId;// 答复与人Id
	private String answerTime;// 答复参与时间
	private String historyUserId;//历史参与人Id
	private String batchNumber;//批次号
	private String optStatus;//操作状态：同意、不同意、更换
	private String reason;//原因
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
	public String getWaitingUserId() {
		return waitingUserId;
	}
	public void setWaitingUserId(String waitingUserId) {
		this.waitingUserId = waitingUserId;
	}
	public String getInitUserId() {
		return initUserId;
	}
	public void setInitUserId(String initUserId) {
		this.initUserId = initUserId;
	}
	public String getActualUserId() {
		return actualUserId;
	}
	public void setActualUserId(String actualUserId) {
		this.actualUserId = actualUserId;
	}
	public String getAnswerUserId() {
		return answerUserId;
	}
	public void setAnswerUserId(String answerUserId) {
		this.answerUserId = answerUserId;
	}
	public String getAnswerTime() {
		return answerTime;
	}
	public void setAnswerTime(String answerTime) {
		this.answerTime = answerTime;
	}
	public String getHistoryUserId() {
		return historyUserId;
	}
	public void setHistoryUserId(String historyUserId) {
		this.historyUserId = historyUserId;
	}
	public String getBatchNumber() {
		return batchNumber;
	}
	public void setBatchNumber(String batchNumber) {
		this.batchNumber = batchNumber;
	}
	public String getOptStatus() {
		return optStatus;
	}
	public void setOptStatus(String optStatus) {
		this.optStatus = optStatus;
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

package com.matech.audit.service.employment.model;

public class Train {
	private String id;
	private String training;
	private String trainingName;
	private String trainStartTime;
	private String trainEndTime;
	private String certificate;
	private String remarks;
	private String linkUserId;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTraining() {
		return training;
	}
	public void setTraining(String training) {
		this.training = training;
	}
	public String getTrainingName() {
		return trainingName;
	}
	public void setTrainingName(String trainingName) {
		this.trainingName = trainingName;
	}
	public String getTrainStartTime() {
		return trainStartTime;
	}
	public void setTrainStartTime(String trainStartTime) {
		this.trainStartTime = trainStartTime;
	}
	public String getTrainEndTime() {
		return trainEndTime;
	}
	public void setTrainEndTime(String trainEndTime) {
		this.trainEndTime = trainEndTime;
	}
	public String getCertificate() {
		return certificate;
	}
	public void setCertificate(String certificate) {
		this.certificate = certificate;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public String getLinkUserId() {
		return linkUserId;
	}
	public void setLinkUserId(String linkUserId) {
		this.linkUserId = linkUserId;
	}
}

package com.matech.audit.service.OAexamine.model;

public class CustomerExamineTable {

	private String examineName;//指标名称
	private String objectiveValue;//客观值
	private String systemScore;//系统分数��ĿID
	private String userScore;//用户分数��ID
	private String examineMome;//说明������
	
	public String getExamineName() {
		return examineName;
	}
	public void setExamineName(String examineName) {
		this.examineName = examineName;
	}
	public String getObjectiveValue() {
		return objectiveValue;
	}
	public void setObjectiveValue(String objectiveValue) {
		this.objectiveValue = objectiveValue;
	}
	public String getSystemScore() {
		return systemScore;
	}
	public void setSystemScore(String systemScore) {
		this.systemScore = systemScore;
	}
	public String getUserScore() {
		return userScore;
	}
	public void setUserScore(String userScore) {
		this.userScore = userScore;
	}
	public String getExamineMome() {
		return examineMome;
	}
	public void setExamineMome(String examineMome) {
		this.examineMome = examineMome;
	}	
}

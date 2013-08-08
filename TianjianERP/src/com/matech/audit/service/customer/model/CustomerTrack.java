package com.matech.audit.service.customer.model;

public class CustomerTrack {
	private String autoid;//追踪记录编号
	private String companyName;//单位全称
	private String linkman;//单位联系人
	private String projectName;//项目名称
	private String telPhone;//联系人电话
	private String linkmanQQ;//联系人ＱＱ
	private String giveCall;//来致电
	private String callTopic;//致电主题
	private String fixedQuestion;//已解决问题
	private String unfixQuestion;//未解决的问题
	private String fixedInstance;//完成情况
	private String recoder;//记录人
	private String recodeTime;//记录时间
	private String userid;
		
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getAutoid() {
		return autoid;
	}
	public void setAutoid(String autoid) {
		this.autoid = autoid;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public String getLinkman() {
		return linkman;
	}
	public void setLinkman(String linkman) {
		this.linkman = linkman;
	}
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	public String getTelPhone() {
		return telPhone;
	}
	public void setTelPhone(String telPhone) {
		this.telPhone = telPhone;
	}
	public String getLinkmanQQ() {
		return linkmanQQ;
	}
	public void setLinkmanQQ(String linkmanQQ) {
		this.linkmanQQ = linkmanQQ;
	}
	public String getGiveCall() {
		return giveCall;
	}
	public void setGiveCall(String giveCall) {
		this.giveCall = giveCall;
	}
	public String getCallTopic() {
		return callTopic;
	}
	public void setCallTopic(String callTopic) {
		this.callTopic = callTopic;
	}
	public String getFixedQuestion() {
		return fixedQuestion;
	}
	public void setFixedQuestion(String fixedQuestion) {
		this.fixedQuestion = fixedQuestion;
	}
	public String getUnfixQuestion() {
		return unfixQuestion;
	}
	public void setUnfixQuestion(String unfixQuestion) {
		this.unfixQuestion = unfixQuestion;
	}
	public String getFixedInstance() {
		return fixedInstance;
	}
	public void setFixedInstance(String fixedInstance) {
		this.fixedInstance = fixedInstance;
	}
	public String getRecoder() {
		return recoder;
	}
	public void setRecoder(String recoder) {
		this.recoder = recoder;
	}
	public String getRecodeTime() {
		return recodeTime;
	}
	public void setRecodeTime(String recodeTime) {
		this.recodeTime = recodeTime;
	}	
}

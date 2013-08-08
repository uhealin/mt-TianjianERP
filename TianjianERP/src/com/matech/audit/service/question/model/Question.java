package com.matech.audit.service.question.model;

public class Question {
	// id ctype typeId titile question userId createDate state fullPath
	private String id;
	private String ctype;
	private String typeId;
	private String title;
	private String question;
	private String userId;
	private String userName;
	private String createDate;
	private String state;
	private String fullPath;
	private String rewardMark;
	private String explan;
	private String explanDate;
	private String answerCount ;

	public String getExplanDate() {
		return explanDate;
	}

	public void setExplanDate(String explanDate) {
		this.explanDate = explanDate;
	}

	public String getRewardMark() {
		return rewardMark;
	}

	public void setRewardMark(String rewardMark) {
		this.rewardMark = rewardMark;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCtype() {
		return ctype;
	}

	public void setCtype(String ctype) {
		this.ctype = ctype;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getFullPath() {
		return fullPath;
	}

	public void setFullPath(String fullPath) {
		this.fullPath = fullPath;
	}

	public String getTypeId() {
		return typeId;
	}

	public void setTypeId(String typeId) {
		this.typeId = typeId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getExplan() {
		return explan;
	}

	public void setExplan(String explan) {
		this.explan = explan;
	}

	public String getAnswerCount() {
		return answerCount;
	}

	public void setAnswerCount(String answerCount) {
		this.answerCount = answerCount;
	}

}

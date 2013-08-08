package com.matech.audit.service.question.model;

public class Answer {
	// id questionId answer userId answerDate bestAnswer
	private String id;
	private String questionId;
	private String answer;
	private String userId;
	private String userName;
	private String date;
	private String isBest;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getQuestionId() {
		return questionId;
	}

	public void setQuestionId(String questionId) {
		this.questionId = questionId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getIsBest() {
		return isBest;
	}

	public void setIsBest(String isBest) {
		this.isBest = isBest;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

}

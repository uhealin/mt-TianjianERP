package com.matech.audit.service.question.model;

public class QuestionTable {
	private int id;

	private int QuestionType;
	private String GreateDate;

	private String title;

	private String context;

	private String author;

	private String keyValue;

	private int orderId;

	private int viewCount = 0;

	public QuestionTable() {
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public void setKeyValue(String keyValue) {
		this.keyValue = keyValue;
	}

	public void setViewCount(int viewCount) {
		this.viewCount = viewCount;
	}

	public int getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public String getContext() {
		return context;
	}

	public String getAuthor() {
		return author;
	}

	public String getKeyValue() {
		return keyValue;
	}

	public int getViewCount() {
		return viewCount;
	}

	public int getOrderId() {
		return orderId;
	}

	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}

	public String getGreateDate() {
		return GreateDate;
	}

	public void setGreateDate(String greateDate) {
		GreateDate = greateDate;
	}

	public int getQuestionType() {
		return QuestionType;
	}

	public void setQuestionType(int questionType) {
		QuestionType = questionType;
	}
}

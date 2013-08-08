package com.matech.audit.service.questionType.model;

public class QuestionTypeTable {

	private int id = 0;
	private int ParentID = 0;
	private int Policy_DB = 1;
	private int Question_DB = 1;
	private int IsLeaf = 1;
	private String TypeName = "";

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getIsLeaf() {
		return IsLeaf;
	}

	public void setIsLeaf(int isLeaf) {
		IsLeaf = isLeaf;
	}

	public int getParentID() {
		return ParentID;
	}

	public void setParentID(int parentID) {
		ParentID = parentID;
	}

	public int getPolicy_DB() {
		return Policy_DB;
	}

	public void setPolicy_DB(int policy_DB) {
		Policy_DB = policy_DB;
	}

	public String getTypeName() {
		return TypeName;
	}

	public void setTypeName(String typeName) {
		TypeName = typeName;
	}

	public int getQuestion_DB() {
		return Question_DB;
	}

	public void setQuestion_DB(int question_DB) {
		Question_DB = question_DB;
	}
}
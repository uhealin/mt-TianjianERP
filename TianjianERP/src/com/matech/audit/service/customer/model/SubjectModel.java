package com.matech.audit.service.customer.model;

public class SubjectModel {
	
	private String subjectName;
	
	private String subjectID;
	
	private String associateName;

	private String assItemID;

	public String getAssItemID() {
		return assItemID;
	}

	public void setAssItemID(String assItemID) {
		this.assItemID = assItemID;
	}

	public String getAssociateName() {
		return associateName;
	}

	public void setAssociateName(String associateName) {
		this.associateName = associateName;
	}

	public String getSubjectID() {
		return subjectID;
	}

	public void setSubjectID(String subjectID) {
		this.subjectID = subjectID;
	}

	public String getSubjectName() {
		return subjectName;
	}

	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
	}
	
	
}

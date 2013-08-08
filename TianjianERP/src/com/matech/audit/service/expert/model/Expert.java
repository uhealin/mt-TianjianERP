package com.matech.audit.service.expert.model;

public class Expert {
	private String loginId = null;
	private int resource = 0;
	private int expert = 0;
	private String gradeType = null;

	public String getGradeType() {
		return gradeType;
	}

	public void setGradeType(String gradeType) {
		this.gradeType = gradeType;
	}

	public String getLoginId() {
		return loginId;
	}

	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}

	public int getResource() {
		return resource;
	}

	public void setResource(int resource) {
		this.resource = resource;
	}

	public int getExpert() {
		return expert;
	}

	public void setExpert(int expert) {
		this.expert = expert;
	}
}

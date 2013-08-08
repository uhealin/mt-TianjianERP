package com.matech.audit.work.analyse;

public class Analyse {
	private String customerID;
	private String endYear;
	private String endMonth;
	private String direction;
	private String subjectID;
	
	private String sdate; //区间
	
	private String[] choose_slist;
	
	private String[] choose_alist;
	
	
	private String[] creditPeriod;		//信用期
	private String[] subjects;			//科目列表
	
	private String contrast;
	
	public String[] getSubjects() {
		return subjects;
	}

	public void setSubjects(String[] subjects) {
		this.subjects = subjects;
	}

	public String[] getCreditPeriod() {
		return creditPeriod;
	}

	public void setCreditPeriod(String[] creditPeriod) {
		this.creditPeriod = creditPeriod;
	}

	public String[] getChoose_slist() {
		return choose_slist;
	}

	public void setChoose_slist(String[] choose_slist) {
		this.choose_slist = choose_slist;
	}

	public String getCustomerID() {
		return customerID;
	}

	public void setCustomerID(String customerID) {
		this.customerID = customerID;
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public String getEndMonth() {
		return endMonth;
	}

	public void setEndMonth(String endMonth) {
		this.endMonth = endMonth;
	}

	public String getEndYear() {
		return endYear;
	}

	public void setEndYear(String endYear) {
		this.endYear = endYear;
	}

	public String getSubjectID() {
		return subjectID;
	}

	public void setSubjectID(String subjectID) {
		this.subjectID = subjectID;
	}

	public String getSdate() {
		return sdate;
	}

	public void setSdate(String sdate) {
		this.sdate = sdate;
	}

	public String[] getChoose_alist() {
		return choose_alist;
	}

	public void setChoose_alist(String[] choose_alist) {
		this.choose_alist = choose_alist;
	}

	public String getContrast() {
		return contrast;
	}

	public void setContrast(String contrast) {
		this.contrast = contrast;
	}
	
	
}

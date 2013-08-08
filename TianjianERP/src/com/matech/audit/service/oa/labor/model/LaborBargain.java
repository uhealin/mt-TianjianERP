package com.matech.audit.service.oa.labor.model;

public class LaborBargain {

	private String bargainID;

	private String bargainperson;

	private String endorsedate;

	private String emolument;

	private String ineffecttime;

	private String other;

	private String checkinperson;

	private String checkindate;

	private String userid;

	private String autoid;
	
	private String fileNames = "" ; //附件名
	
	private String fileRondomNames = "" ;//附件随机名字
	
	private String bargaintype = "" ;
	private String trialtime = "" ;

	public String getBargaintype() {
		return bargaintype;
	}

	public void setBargaintype(String bargaintype) {
		this.bargaintype = bargaintype;
	}

	public String getTrialtime() {
		return trialtime;
	}

	public void setTrialtime(String trialtime) {
		this.trialtime = trialtime;
	}

	public String getAutoid() {
		return autoid;
	}

	public void setAutoid(String autoid) {
		this.autoid = autoid;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getBargainID() {
		return bargainID;
	}

	public void setBargainID(String bargainID) {
		this.bargainID = bargainID;
	}

	public String getBargainperson() {
		return bargainperson;
	}

	public void setBargainperson(String bargainperson) {
		this.bargainperson = bargainperson;
	}

	public String getCheckindate() {
		return checkindate;
	}

	public void setCheckindate(String checkindate) {
		this.checkindate = checkindate;
	}

	public String getCheckinperson() {
		return checkinperson;
	}

	public void setCheckinperson(String checkinperson) {
		this.checkinperson = checkinperson;
	}

	public String getEmolument() {
		return emolument;
	}

	public void setEmolument(String emolument) {
		this.emolument = emolument;
	}

	public String getEndorsedate() {
		return endorsedate;
	}

	public void setEndorsedate(String endorsedate) {
		this.endorsedate = endorsedate;
	}

	public String getIneffecttime() {
		return ineffecttime;
	}

	public void setIneffecttime(String ineffecttime) {
		this.ineffecttime = ineffecttime;
	}

	public String getOther() {
		return other;
	}

	public void setOther(String other) {
		this.other = other;
	}

	public String getFileNames() {
		return fileNames;
	}

	public void setFileNames(String fileNames) {
		this.fileNames = fileNames;
	}

	public String getFileRondomNames() {
		return fileRondomNames;
	}

	public void setFileRondomNames(String fileRondomNames) {
		this.fileRondomNames = fileRondomNames;
	}

}

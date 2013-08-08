package com.matech.audit.service.oa.learncircs.model;

/**
 * 员工培训情况管理oa_learncircs
 * 
 * @author Administrator
 * 
 */
public class LearncircsTable {
	private int autoid = 0;

	private String userid = "";

	private String startlearndate = "";

	private String endlearndate = "";

	private String learncontent = "";

	private String learncertificate = "";

	private String learnachievement = "";

	private String remark = "";

	private String learnframework = "";

	private String learnlocus = "";

	private String checkinperson = "";

	private String checkindate = "";

	private String property = "";
	
	private String fileNames = "" ; //附件名
	
	private String fileRondomNames = "" ;//附件随机名字
	
	private String learntype = "" ;
	private String learnperiod = "" ;
	
	public String getLearntype() {
		return learntype;
	}

	public void setLearntype(String learntype) {
		this.learntype = learntype;
	}

	public String getLearnperiod() {
		return learnperiod;
	}

	public void setLearnperiod(String learnperiod) {
		this.learnperiod = learnperiod;
	}

	public int getAutoid() {
		return autoid;
	}

	public void setAutoid(int autoid) {
		this.autoid = autoid;
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

	public String getEndlearndate() {
		return endlearndate;
	}

	public void setEndlearndate(String endlearndate) {
		this.endlearndate = endlearndate;
	}

	public String getLearnachievement() {
		return learnachievement;
	}

	public void setLearnachievement(String learnachievement) {
		this.learnachievement = learnachievement;
	}

	public String getLearncertificate() {
		return learncertificate;
	}

	public void setLearncertificate(String learncertificate) {
		this.learncertificate = learncertificate;
	}

	public String getLearncontent() {
		return learncontent;
	}

	public void setLearncontent(String learncontent) {
		this.learncontent = learncontent;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getLearnframework() {
		return learnframework;
	}

	public void setLearnframework(String learnframework) {
		this.learnframework = learnframework;
	}

	public String getLearnlocus() {
		return learnlocus;
	}

	public void setLearnlocus(String learnlocus) {
		this.learnlocus = learnlocus;
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getStartlearndate() {
		return startlearndate;
	}

	public void setStartlearndate(String startlearndate) {
		this.startlearndate = startlearndate;
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

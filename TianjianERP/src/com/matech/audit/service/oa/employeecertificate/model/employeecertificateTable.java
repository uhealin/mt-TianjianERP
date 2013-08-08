package com.matech.audit.service.oa.employeecertificate.model;

/**
 * 员工证件登记oa_employeecertificate
 * 
 * @author Administrator
 * 
 */
public class employeecertificateTable {
	private int autoid = 0;

	private String certificatetype = "";

	private String certificateid = "";

	private String hairdepartment = "";

	private String hairtime = "";

	private String availabilitytime = "";

	private String remark = "";

	private String userid = "";

	private String property = "";
	
	private String fileNames = "" ; //附件名
	
	private String fileRondomNames = "" ;//附件随机名字

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

	public int getAutoid() {
		return autoid;
	}

	public void setAutoid(int autoid) {
		this.autoid = autoid;
	}

	public String getAvailabilitytime() {
		return availabilitytime;
	}

	public void setAvailabilitytime(String availabilitytime) {
		this.availabilitytime = availabilitytime;
	}

	public String getCertificateid() {
		return certificateid;
	}

	public void setCertificateid(String certificateid) {
		this.certificateid = certificateid;
	}

	public String getCertificatetype() {
		return certificatetype;
	}

	public void setCertificatetype(String certificatetype) {
		this.certificatetype = certificatetype;
	}

	public String getHairdepartment() {
		return hairdepartment;
	}

	public void setHairdepartment(String hairdepartment) {
		this.hairdepartment = hairdepartment;
	}

	public String getHairtime() {
		return hairtime;
	}

	public void setHairtime(String hairtime) {
		this.hairtime = hairtime;
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

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

}

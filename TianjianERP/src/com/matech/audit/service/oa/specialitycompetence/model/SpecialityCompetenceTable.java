package com.matech.audit.service.oa.specialitycompetence.model;

/**
 * 专业资格登记oa_specialitycompetence
 * 
 * @author Administrator
 * 
 */
public class SpecialityCompetenceTable {
	private int autoid = 0;

	private String certificate = "";

	private String certificateid = "";

	private String certificatedepartment = "";

	private String certificatetime = "";

	private String availabilitytime = "";

	private String ifera = "";

	private String remark = "";

	private String userid = "";

	private String property = "";
	
	private String fileNames = "" ;
	
	private String fileRondomNames = "" ;

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

	public String getCertificate() {
		return certificate;
	}

	public void setCertificate(String certificate) {
		this.certificate = certificate;
	}

	public String getCertificatedepartment() {
		return certificatedepartment;
	}

	public void setCertificatedepartment(String certificatedepartment) {
		this.certificatedepartment = certificatedepartment;
	}

	public String getCertificateid() {
		return certificateid;
	}

	public void setCertificateid(String certificateid) {
		this.certificateid = certificateid;
	}

	public String getCertificatetime() {
		return certificatetime;
	}

	public void setCertificatetime(String certificatetime) {
		this.certificatetime = certificatetime;
	}

	public String getIfera() {
		return ifera;
	}

	public void setIfera(String ifera) {
		this.ifera = ifera;
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

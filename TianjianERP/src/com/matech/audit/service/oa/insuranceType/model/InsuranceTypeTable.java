package com.matech.audit.service.oa.insuranceType.model;

/**
 * 险种oa_insurancetype
 * 
 * @author Administrator
 * 
 */
public class InsuranceTypeTable {
	private int autoid = 0;

	private String ctype = "";

	private String carea = "";

	private String ctime = "";

	private String cmoney = "";

	private String insurance = "";

	private String property = "";
	
	private String fileNames = "" ; //附件名
	
	private String fileRondomNames = "" ;//附件随机名字

	public int getAutoid() {
		return autoid;
	}

	public void setAutoid(int autoid) {
		this.autoid = autoid;
	}

	public String getCarea() {
		return carea;
	}

	public void setCarea(String carea) {
		this.carea = carea;
	}

	public String getCmoney() {
		return cmoney;
	}

	public void setCmoney(String cmoney) {
		this.cmoney = cmoney;
	}

	public String getCtime() {
		return ctime;
	}

	public void setCtime(String ctime) {
		this.ctime = ctime;
	}

	public String getCtype() {
		return ctype;
	}

	public void setCtype(String ctype) {
		this.ctype = ctype;
	}

	public String getInsurance() {
		return insurance;
	}

	public void setInsurance(String insurance) {
		this.insurance = insurance;
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
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

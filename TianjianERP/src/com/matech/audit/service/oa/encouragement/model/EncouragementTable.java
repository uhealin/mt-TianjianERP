package com.matech.audit.service.oa.encouragement.model;

/**
 * 员工奖惩记录管理oa_encouragement
 * 
 * @author Administrator
 * 
 */
public class EncouragementTable {
	private int autoid = 0;

	private String userid = "";

	private String pricedate = "";

	private String pricetype = "";

	private String whys = "";

	private String result = "";

	private String remark = "";

	private String checkinperson = "";

	private String checkindate = "";

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

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getPricedate() {
		return pricedate;
	}

	public void setPricedate(String pricedate) {
		this.pricedate = pricedate;
	}

	public String getPricetype() {
		return pricetype;
	}

	public void setPricetype(String pricetype) {
		this.pricetype = pricetype;
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

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getWhys() {
		return whys;
	}

	public void setWhys(String whys) {
		this.whys = whys;
	}

}

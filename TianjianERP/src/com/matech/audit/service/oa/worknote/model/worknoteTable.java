package com.matech.audit.service.oa.worknote.model;

/**
 * 工作记录登记oa_worknote
 * 
 * @author Administrator
 * 
 */
public class worknoteTable {
	private int autoid = 0;

	private String starttime = "";

	private String endtime = "";

	private String workunit = "";

	private String job = "";

	private String proveman = "";

	private String workcircs = "";

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

	public String getEndtime() {
		return endtime;
	}

	public void setEndtime(String endtime) {
		this.endtime = endtime;
	}

	public String getJob() {
		return job;
	}

	public void setJob(String job) {
		this.job = job;
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public String getProveman() {
		return proveman;
	}

	public void setProveman(String proveman) {
		this.proveman = proveman;
	}

	public String getStarttime() {
		return starttime;
	}

	public void setStarttime(String starttime) {
		this.starttime = starttime;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getWorkcircs() {
		return workcircs;
	}

	public void setWorkcircs(String workcircs) {
		this.workcircs = workcircs;
	}

	public String getWorkunit() {
		return workunit;
	}

	public void setWorkunit(String workunit) {
		this.workunit = workunit;
	}

}

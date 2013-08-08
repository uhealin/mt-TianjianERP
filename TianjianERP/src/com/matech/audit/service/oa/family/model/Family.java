package com.matech.audit.service.oa.family.model;
/**
 * 家庭人员信息
 * @author Administrator
 *
 */
public class Family {

	private String compellation;// 姓名

	private String footing; // 关系

	private String workunit; // 工作单位

	private String phone; // 联系电话

	private String government;// 政治面貌

	private String autoid;// 自增编号

	private String property;// 属性

	private String userid;// 人员ID
	
	private String fileNames = "" ; //附件名
	
	private String fileRondomNames = "" ;//附件随机名字

	public String getCompellation() {
		return compellation;
	}

	public void setCompellation(String compellation) {
		this.compellation = compellation;
	}

	public String getFooting() {
		return footing;
	}

	public void setFooting(String footing) {
		this.footing = footing;
	}

	public String getGovernment() {
		return government;
	}

	public void setGovernment(String government) {
		this.government = government;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getWorkunit() {
		return workunit;
	}

	public void setWorkunit(String workunit) {
		this.workunit = workunit;
	}

	public String getAutoid() {
		return autoid;
	}

	public void setAutoid(String autoid) {
		this.autoid = autoid;
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
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

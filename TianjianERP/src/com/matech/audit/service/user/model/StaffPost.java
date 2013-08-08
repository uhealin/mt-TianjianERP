package com.matech.audit.service.user.model;

/**
 *  员工报到(执业信息 职称表)
 * @author Ymm
 * k_staffpost
 */
public class StaffPost {
	
	private String autoId ; // int(11) NULL
	private String id ;//(100) NULL
	private String series ;//(100) NULL职称系列
	private String rankName ;//(50) NULL职称
	private String rankGrade ;//(50) NULL职称等级
	private String getDate ;//(100) NULL获取时间
	private String property ;//(300) NULL备用
	private String createDate;// varchar(150) NULL创建日期
	private String createUser;// varchar(100) NULL创建人员
	private String createDepartment;// varchar(100) NULL创建部门
	public String getCreateDate() {
		return createDate;
	}
	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}
	public String getCreateUser() {
		return createUser;
	}
	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}
	public String getCreateDepartment() {
		return createDepartment;
	}
	public void setCreateDepartment(String createDepartment) {
		this.createDepartment = createDepartment;
	}
	public String getAutoId() {
		return autoId;
	}
	public void setAutoId(String autoId) {
		this.autoId = autoId;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getSeries() {
		return series;
	}
	public void setSeries(String series) {
		this.series = series;
	}
	public String getRankName() {
		return rankName;
	}
	public void setRankName(String rankName) {
		this.rankName = rankName;
	}
	public String getRankGrade() {
		return rankGrade;
	}
	public void setRankGrade(String rankGrade) {
		this.rankGrade = rankGrade;
	}
	public String getGetDate() {
		return getDate;
	}
	public void setGetDate(String getDate) {
		this.getDate = getDate;
	}
	public String getProperty() {
		return property;
	}
	public void setProperty(String property) {
		this.property = property;
	}
	
}

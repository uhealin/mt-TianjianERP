package com.matech.audit.service.attach.model;

public class Attach {
	
	private String unid = ""; //唯一ID
	private String typeId; //分类ID
	private String title;	//标题	
	private String content;	//内容
	private String udate;	//创建时间
	
	private String lastDate;	//最后修改时间
	private String lastPerson;	//最后修改人ID	
	private String orderId;	//
	private int viewCount = 0;	//查看次数
	private String filename; //原文件名
	
	private String edate; //有效期
	private String mime; //原文件类型
	private String departid;	//单位
	private String property = ""; //属性
	private String projectid = ""; //项目ID
	private String releasedate = "";//资料日期
	

	public String getReleasedate() {
		return releasedate;
	}


	public void setReleasedate(String releasedate) {
		this.releasedate = releasedate;
	}


	public Attach() {
	}


	public void setTitle(String title) {
		this.title = title;
	}


	public void setViewCount(int viewCount) {
		this.viewCount = viewCount;
	}



	public String getTitle() {
		return title;
	}


	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getLastDate() {
		return lastDate;
	}

	public void setLastDate(String lastDate) {
		this.lastDate = lastDate;
	}

	public String getLastPerson() {
		return lastPerson;
	}

	public void setLastPerson(String lastPerson) {
		this.lastPerson = lastPerson;
	}

	public String getTypeId() {
		return typeId;
	}

	public void setTypeId(String typeId) {
		this.typeId = typeId;
	}

	public String getUdate() {
		return udate;
	}

	public void setUdate(String udate) {
		this.udate = udate;
	}

	public String getUnid() {
		return unid;
	}

	public void setUnid(String unid) {
		this.unid = unid;
	}

	public int getViewCount() {
		return viewCount;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getDepartid() {
		return departid;
	}

	public void setDepartid(String departid) {
		this.departid = departid;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getMime() {
		return mime;
	}

	public void setMime(String mime) {
		this.mime = mime;
	}

	public String getProjectid() {
		return projectid;
	}

	public void setProjectid(String projectid) {
		this.projectid = projectid;
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}


	public String getEdate() {
		return edate;
	}


	public void setEdate(String edate) {
		this.edate = edate;
	}
	
}

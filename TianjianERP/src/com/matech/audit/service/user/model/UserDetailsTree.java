package com.matech.audit.service.user.model;

import com.matech.framework.pub.db.Table;

@Table(name="k_userdetailstree",insertPk=false,pk="autoId")
public class UserDetailsTree {
	private String autoId;//
	private String id;//id
	private String text;//名称
	private String url;//点击调用的url
	private String orderby;//排序
	private String isShow;//1显示0隐藏
	private String createDate;//创建日期
	private String createUser;//创建人
	private String property; //备用
	
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
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getOrderby() {
		return orderby;
	}
	public void setOrderby(String orderby) {
		this.orderby = orderby;
	}
	public String getIsShow() {
		return isShow;
	}
	public void setIsShow(String isShow) {
		this.isShow = isShow;
	}
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
	public String getProperty() {
		return property;
	}
	public void setProperty(String property) {
		this.property = property;
	}
	
}

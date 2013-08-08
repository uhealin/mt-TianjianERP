package com.matech.audit.service.rule.model;
/**
 * 指标设置的ＶＯ，表名为k_rule
 * @author Administrator
 *
 */
public class RuleTable {

	private int autoid;

	private String title;

	private String type;

	private String memo;

	private String content;

	private double orderid;

	private String property;
	
	private String refer1;
	
	private String refer2;
	
	private String refer0;
	

	public RuleTable() {
		// TODO Auto-generated constructor stub
	}

	public int getAutoid() {
		return autoid;
	}

	public void setAutoid(int autoid) {
		this.autoid = autoid;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	
	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}



	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	public double getOrderid() {
		return orderid;
	}

	public void setOrderid(double orderid) {
		this.orderid = orderid;
	}



	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public String getRefer1() {
		return refer1;
	}

	public void setRefer1(String refer1) {
		this.refer1 = refer1;
	}

	public String getRefer2() {
		return refer2;
	}

	public void setRefer2(String refer2) {
		this.refer2 = refer2;
	}

	public String getRefer0() {
		return refer0;
	}

	public void setRefer0(String refer0) {
		this.refer0 = refer0;
	}

}

package com.matech.audit.service.customer.model;

public class Stockholder {
	
	private String name;
	
	private String totalFund;
	
	private String registerFund;
	
	private String percentOfFund;
	
	private String factFund;
	
	private String percentage;

	public String getFactFund() {
		return factFund;
	}

	public void setFactFund(String factFund) {
		this.factFund = factFund;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPercentage() {
		return percentage;
	}

	public void setPercentage(String percentage) {
		this.percentage = percentage;
	}

	public String getPercentOfFund() {
		return percentOfFund;
	}

	public void setPercentOfFund(String percentOfFund) {
		this.percentOfFund = percentOfFund;
	}

	public String getRegisterFund() {
		return registerFund;
	}

	public void setRegisterFund(String registerFund) {
		this.registerFund = registerFund;
	}

	public String getTotalFund() {
		return totalFund;
	}

	public void setTotalFund(String totalFund) {
		this.totalFund = totalFund;
	}

}

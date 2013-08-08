package com.matech.audit.service.investManage.model;

public class InvestManage {
	private String autoId;
	private String loginId;
	private String loginName;
	private String setTime;
	private String investId;
	private String userName;//录入姓名
	private String relations;//选择自己是事务所谁的XXX关系
	private String answer;//声明，我是不是有进行股票买卖 回答是否
	private String ssstockNum;//深市A股票帐号
	private String ssstockNum2;//深市B股票帐号
	private String hsstockNum;//沪市A股票帐号
	private String hsstockNum2;//沪市B股票帐号
	private String gsstockNum;//港市股票帐号
	private String stockCode;//股票代码
	private String stockName;//股票名称
	private String stockCount;//股数
	private String stockInDate;//首次买入日期
	private String stockOutDate;//最后卖出日期
	private String property;// 备注
	public String getAutoId() {
		return autoId;
	}
	public void setAutoId(String autoId) {
		this.autoId = autoId;
	}
	public String getLoginId() {
		return loginId;
	}
	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}
	public String getLoginName() {
		return loginName;
	}
	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}
	public String getSetTime() {
		return setTime;
	}
	public void setSetTime(String setTime) {
		this.setTime = setTime;
	}
	public String getInvestId() {
		return investId;
	}
	public void setInvestId(String investId) {
		this.investId = investId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getRelations() {
		return relations;
	}
	public void setRelations(String relations) {
		this.relations = relations;
	}
	public String getAnswer() {
		return answer;
	}
	public void setAnswer(String answer) {
		this.answer = answer;
	}
	public String getSsstockNum() {
		return ssstockNum;
	}
	public void setSsstockNum(String ssstockNum) {
		this.ssstockNum = ssstockNum;
	}
	public String getSsstockNum2() {
		return ssstockNum2;
	}
	public void setSsstockNum2(String ssstockNum2) {
		this.ssstockNum2 = ssstockNum2;
	}
	public String getHsstockNum() {
		return hsstockNum;
	}
	public void setHsstockNum(String hsstockNum) {
		this.hsstockNum = hsstockNum;
	}
	public String getHsstockNum2() {
		return hsstockNum2;
	}
	public void setHsstockNum2(String hsstockNum2) {
		this.hsstockNum2 = hsstockNum2;
	}
	public String getGsstockNum() {
		return gsstockNum;
	}
	public void setGsstockNum(String gsstockNum) {
		this.gsstockNum = gsstockNum;
	}
	public String getStockCode() {
		return stockCode;
	}
	public void setStockCode(String stockCode) {
		this.stockCode = stockCode;
	}
	public String getStockName() {
		return stockName;
	}
	public void setStockName(String stockName) {
		this.stockName = stockName;
	}
	public String getStockCount() {
		return stockCount;
	}
	public void setStockCount(String stockCount) {
		this.stockCount = stockCount;
	}
	public String getStockInDate() {
		return stockInDate;
	}
	public void setStockInDate(String stockInDate) {
		this.stockInDate = stockInDate;
	}
	public String getStockOutDate() {
		return stockOutDate;
	}
	public void setStockOutDate(String stockOutDate) {
		this.stockOutDate = stockOutDate;
	}
	public String getProperty() {
		return property;
	}
	public void setProperty(String property) {
		this.property = property;
	}
	
}

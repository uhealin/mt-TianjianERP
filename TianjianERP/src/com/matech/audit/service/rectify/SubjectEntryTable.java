package com.matech.audit.service.rectify;

public class SubjectEntryTable {
	private int autoid; // ��� �Զ����

	private String accpackageid; // ���ױ�� ��session �� request �еõ�

	private String projectID;

	private int voucherid; // ƾ֤������ AutoId ���(c_ Voucher)

	private String typeid;

	private String vchdate; // ��������

	private int serail; // ��¼��ƾ֤�е���� ����

	private String summary; // ժҪ ����

	private String subjectid; // ԭ��Ŀ��� ����

	private int dirction; // ������ ���� 1���跽��-1����

	private double occurvalue; // ������ ���� >0��ƾ֤��<0���ֳ���ƾ֤��

	private double currrate; // ���� null

	private double currvalue; // ��ҽ�� null

	private String currency; // ���� null

	private double quantity; // �� null

	private double unitprice; // ���� null

	private String bankid; // ���ж����˺� null

	private String property; // ���� 3(����) 4(�ط���) 5(����δ��)

	private String itemtype;// 固定资产类型

	private String inventorytype;// 存货类型
	
	private String DebitExpressions;// 借调整公式

	private String LenderExpressions;// 贷调整公式

	public String getInventorytype() {
		return inventorytype;
	}

	public void setInventorytype(String inventorytype) {
		this.inventorytype = inventorytype;
	}

	public String getItemtype() {
		return itemtype;
	}

	public void setItemtype(String itemtype) {
		this.itemtype = itemtype;
	}

	public SubjectEntryTable() {
	}

	public String getTypeid() {
		return typeid;
	}

	public void setTypeid(String typeid) {
		this.typeid = typeid;
	}

	public String getAccpackageid() {
		return accpackageid;
	}

	public void setAccpackageid(String accpackageid) {
		this.accpackageid = accpackageid;
	}

	public int getAutoid() {
		return autoid;
	}

	public void setAutoid(int autoid) {
		this.autoid = autoid;
	}

	public String getBankid() {
		return bankid;
	}

	public void setBankid(String bankid) {
		this.bankid = bankid;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public double getCurrrate() {
		return currrate;
	}

	public void setCurrrate(double currrate) {
		this.currrate = currrate;
	}

	public double getCurrvalue() {
		return currvalue;
	}

	public void setCurrvalue(double currvalue) {
		this.currvalue = currvalue;
	}

	public int getDirction() {
		return dirction;
	}

	public void setDirction(int dirction) {
		this.dirction = dirction;
	}

	public double getOccurvalue() {
		return occurvalue;
	}

	public void setOccurvalue(double occurvalue) {
		this.occurvalue = occurvalue;
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public double getQuantity() {
		return quantity;
	}

	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}

	public int getSerail() {
		return serail;
	}

	public void setSerail(int serail) {
		this.serail = serail;
	}

	public String getSubjectid() {
		return subjectid;
	}

	public void setSubjectid(String subjectid) {
		this.subjectid = subjectid;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public double getUnitprice() {
		return unitprice;
	}

	public void setUnitprice(double unitprice) {
		this.unitprice = unitprice;
	}

	public int getVoucherid() {
		return voucherid;
	}

	public void setVoucherid(int voucherid) {
		this.voucherid = voucherid;
	}

	public String getVchdate() {
		return vchdate;
	}

	public String getProjectID() {
		return projectID;
	}

	public void setVchdate(String vchdate) {
		this.vchdate = vchdate;
	}

	public void setProjectID(String projectID) {
		this.projectID = projectID;
	}

	public String getDebitExpressions() {
		return DebitExpressions;
	}

	public void setDebitExpressions(String debitExpressions) {
		DebitExpressions = debitExpressions;
	}

	public String getLenderExpressions() {
		return LenderExpressions;
	}

	public void setLenderExpressions(String lenderExpressions) {
		LenderExpressions = lenderExpressions;
	}

}

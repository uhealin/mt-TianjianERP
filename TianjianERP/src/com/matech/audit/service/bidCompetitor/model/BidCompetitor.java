package com.matech.audit.service.bidCompetitor.model;

public class BidCompetitor {
	private String uuid;//主键
	private String bidCompetitor;// 竞争对手名
	private String bidProjectId;// 招投标项目表外键
	private String bidCompetitorPrice;// 竞争对手报价
	private String bidMemberSuperiority;// 竞争对手优势分析
	private String bidMemberDisadvantaged;// 劣势分析
	private String property;// 备用字段

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getBidCompetitor() {
		return bidCompetitor;
	}

	public void setBidCompetitor(String bidCompetitor) {
		this.bidCompetitor = bidCompetitor;
	}

	public String getBidProjectId() {
		return bidProjectId;
	}

	public void setBidProjectId(String bidProjectId) {
		this.bidProjectId = bidProjectId;
	}

	public String getBidCompetitorPrice() {
		return bidCompetitorPrice;
	}

	public void setBidCompetitorPrice(String bidCompetitorPrice) {
		this.bidCompetitorPrice = bidCompetitorPrice;
	}

	public String getBidMemberSuperiority() {
		return bidMemberSuperiority;
	}

	public void setBidMemberSuperiority(String bidMemberSuperiority) {
		this.bidMemberSuperiority = bidMemberSuperiority;
	}

	public String getBidMemberDisadvantaged() {
		return bidMemberDisadvantaged;
	}

	public void setBidMemberDisadvantaged(String bidMemberDisadvantaged) {
		this.bidMemberDisadvantaged = bidMemberDisadvantaged;
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

}

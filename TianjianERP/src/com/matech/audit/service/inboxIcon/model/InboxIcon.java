package com.matech.audit.service.inboxIcon.model;

public class InboxIcon {
	private String id;
	private String name;
	private String sex;
	private String nation;
	private String born;
	private String address;
	private String cardNo;
	private String police;
	private String activity;
	private byte[] getPhotoBuffer;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public String getNation() {
		return nation;
	}
	public void setNation(String nation) {
		this.nation = nation;
	}
	public String getBorn() {
		return born;
	}
	public void setBorn(String born) {
		this.born = born;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getCardNo() {
		return cardNo;
	}
	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}
	public String getPolice() {
		return police;
	}
	public void setPolice(String police) {
		this.police = police;
	}
	public String getActivity() {
		return activity;
	}
	public void setActivity(String activity) {
		this.activity = activity;
	}
	public byte[] getGetPhotoBuffer() {
		return getPhotoBuffer;
	}
	public void setGetPhotoBuffer(byte[] getPhotoBuffer) {
		this.getPhotoBuffer = getPhotoBuffer;
	}
}

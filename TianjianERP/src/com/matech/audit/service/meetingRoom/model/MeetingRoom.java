package com.matech.audit.service.meetingRoom.model;

public class MeetingRoom {
	private String uuid;
	private String name;//名称
	private String organ;//所属机构
	private String containPerson;//可容纳人数
	private String device;//设备情况
	private String place;//地点
	private String describes;//描述
	private String creatorId;//创建人
	private String createTime;// 创建时间
	private String property;//备注
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getOrgan() {
		return organ;
	}
	public void setOrgan(String organ) {
		this.organ = organ;
	}
	public String getContainPerson() {
		return containPerson;
	}
	public void setContainPerson(String containPerson) {
		this.containPerson = containPerson;
	}
	public String getDevice() {
		return device;
	}
	public void setDevice(String device) {
		this.device = device;
	}
	public String getPlace() {
		return place;
	}
	public void setPlace(String place) {
		this.place = place;
	}
	public String getDescribes() {
		return describes;
	}
	public void setDescribes(String describes) {
		this.describes = describes;
	}
	public String getCreatorId() {
		return creatorId;
	}
	public void setCreatorId(String creatorId) {
		this.creatorId = creatorId;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getProperty() {
		return property;
	}
	public void setProperty(String property) {
		this.property = property;
	}
	
	
	
}

package com.matech.audit.service.meetingOrder.model;

import com.matech.framework.pub.db.Table;

@Table(name="k_meetingorder",pk="uuid")
public class MeetingOrder {
	protected String uuid ;
	 protected String title ;
	 protected String name ;
	 protected String event ;
	 protected String meetingRoomId ;
	 protected String startTime ;
	 protected String endTime ;
	 protected String describe1 ;
	 protected String describe2 ;
	 protected String describe ;
	 protected String requirements ;
	 protected String equipment ;
	 protected String departmentId ;
	 protected String describes ;
	 protected String attachFileId ;
	 protected String createDate ;
	 protected String createUserId ;
	 protected String auditDate ;
	 protected String auditUserId ;
	 protected String status ;
	 protected String reason ;
	 protected String propertys ;
	 protected String status1 ;
	 protected String userid ;


	 public String getUuid(){ return this.uuid; }
	 public void setUuid(String uuid){ this.uuid=uuid; }
	 public String getTitle(){ return this.title; }
	 public void setTitle(String title){ this.title=title; }
	 public String getName(){ return this.name; }
	 public void setName(String name){ this.name=name; }
	 public String getEvent(){ return this.event; }
	 public void setEvent(String event){ this.event=event; }
	 public String getMeetingRoomId(){ return this.meetingRoomId; }
	 public void setMeetingRoomId(String meetingRoomId){ this.meetingRoomId=meetingRoomId; }
	 public String getStartTime(){ return this.startTime; }
	 public void setStartTime(String startTime){ this.startTime=startTime; }
	 public String getEndTime(){ return this.endTime; }
	 public void setEndTime(String endTime){ this.endTime=endTime; }
	 public String getDescribe1(){ return this.describe1; }
	 public void setDescribe1(String describe1){ this.describe1=describe1; }
	 public String getDescribe2(){ return this.describe2; }
	 public void setDescribe2(String describe2){ this.describe2=describe2; }
	 public String getDescribe(){ return this.describe; }
	 public void setDescribe(String describe){ this.describe=describe; }
	 public String getRequirements(){ return this.requirements; }
	 public void setRequirements(String requirements){ this.requirements=requirements; }
	 public String getEquipment(){ return this.equipment; }
	 public void setEquipment(String equipment){ this.equipment=equipment; }
	 public String getDepartmentId(){ return this.departmentId; }
	 public void setDepartmentId(String departmentId){ this.departmentId=departmentId; }
	 public String getDescribes(){ return this.describes; }
	 public void setDescribes(String describes){ this.describes=describes; }
	 public String getAttachFileId(){ return this.attachFileId; }
	 public void setAttachFileId(String attachFileId){ this.attachFileId=attachFileId; }
	 public String getCreateDate(){ return this.createDate; }
	 public void setCreateDate(String createDate){ this.createDate=createDate; }
	 public String getCreateUserId(){ return this.createUserId; }
	 public void setCreateUserId(String createUserId){ this.createUserId=createUserId; }
	 public String getAuditDate(){ return this.auditDate; }
	 public void setAuditDate(String auditDate){ this.auditDate=auditDate; }
	 public String getAuditUserId(){ return this.auditUserId; }
	 public void setAuditUserId(String auditUserId){ this.auditUserId=auditUserId; }
	 public String getStatus(){ return this.status; }
	 public void setStatus(String status){ this.status=status; }
	 public String getReason(){ return this.reason; }
	 public void setReason(String reason){ this.reason=reason; }
	 public String getPropertys(){ return this.propertys; }
	 public void setPropertys(String propertys){ this.propertys=propertys; }
	 public String getStatus1(){ return this.status1; }
	 public void setStatus1(String status1){ this.status1=status1; }
	 public String getUserid(){ return this.userid; }
	 public void setUserid(String userid){ this.userid=userid; }

	
}

package com.matech.audit.service.club.model;

import com.matech.framework.pub.db.Table;

@Table(name="oa_club_event",pk="uuid")
public class ClubEventVO {
	
	 protected String uuid ;
	 protected String event_time ;
	 protected String member_ids ;
	 protected String member_names ;
	 protected String descp ;
	 protected String att_id ;
	 protected String userid ;
	 protected String departmentid ;


	 public String getUuid(){ return this.uuid; }
	 public void setUuid(String uuid){ this.uuid=uuid; }
	 public String getEvent_time(){ return this.event_time; }
	 public void setEvent_time(String event_time){ this.event_time=event_time; }
	 public String getMember_ids(){ return this.member_ids; }
	 public void setMember_ids(String member_ids){ this.member_ids=member_ids; }
	 public String getMember_names(){ return this.member_names; }
	 public void setMember_names(String member_names){ this.member_names=member_names; }
	 public String getDescp(){ return this.descp; }
	 public void setDescp(String descp){ this.descp=descp; }
	 public String getAtt_id(){ return this.att_id; }
	 public void setAtt_id(String att_id){ this.att_id=att_id; }
	 public String getUserid(){ return this.userid; }
	 public void setUserid(String userid){ this.userid=userid; }
	 public String getDepartmentid(){ return this.departmentid; }
	 public void setDepartmentid(String departmentid){ this.departmentid=departmentid; }

}

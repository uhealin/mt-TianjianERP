package com.matech.audit.service.club.model;

import com.matech.framework.pub.db.Table;

@Table(name="oa_club_apply",pk="uuid")
public class ClubApplyVO {
	
	 protected String uuid ;
	 protected String departmentid ;
	 protected String userid ;
	 protected String club_id ;
	 protected String state ;
	 protected String create_date;


	 public String getUuid(){ return this.uuid; }
	 public void setUuid(String uuid){ this.uuid=uuid; }
	 public String getDepartmentid(){ return this.departmentid; }
	 public void setDepartmentid(String departmentid){ this.departmentid=departmentid; }
	 public String getUserid(){ return this.userid; }
	 public void setUserid(String userid){ this.userid=userid; }
	 public String getClub_id(){ return this.club_id; }
	 public void setClub_id(String club_id){ this.club_id=club_id; }
	 public String getState(){ return this.state; }
	 public void setState(String state){ this.state=state; }
	public String getCreate_date() {
		return create_date;
	}
	public void setCreate_date(String create_date) {
		this.create_date = create_date;
	}
	 
}

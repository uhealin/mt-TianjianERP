package com.matech.audit.service.club.model;

import com.matech.framework.pub.db.Table;

@Table(name="oa_club_finance",pk="uuid")
public class ClubFinanceVO {	 protected String uuid ;
protected String userid ;
protected String department ;
protected String ftype ;
protected Double amount ;
protected String create_date ;
protected String event_id ;
protected String member_ids ;
protected String member_names ;
protected String payer_ids ;
protected String payer_names ;
protected String club_id ;
protected Double rest_amount ;
protected String descp;



public String getUuid(){ return this.uuid; }
public void setUuid(String uuid){ this.uuid=uuid; }
public String getUserid(){ return this.userid; }
public void setUserid(String userid){ this.userid=userid; }
public String getDepartment(){ return this.department; }
public void setDepartment(String department){ this.department=department; }
public String getFtype(){ return this.ftype; }
public void setFtype(String ftype){ this.ftype=ftype; }
public Double getAmount(){ return this.amount; }
public void setAmount(Double amount){ this.amount=amount; }
public String getCreate_date(){ return this.create_date; }
public void setCreate_date(String create_date){ this.create_date=create_date; }
public String getEvent_id(){ return this.event_id; }
public void setEvent_id(String event_id){ this.event_id=event_id; }
public String getMember_ids(){ return this.member_ids; }
public void setMember_ids(String member_ids){ this.member_ids=member_ids; }
public String getMember_names(){ return this.member_names; }
public void setMember_names(String member_names){ this.member_names=member_names; }

public String getPayer_ids() {
	return payer_ids;
}
public void setPayer_ids(String payer_ids) {
	this.payer_ids = payer_ids;
}
public String getPayer_names() {
	return payer_names;
}
public void setPayer_names(String payer_names) {
	this.payer_names = payer_names;
}
public String getClub_id(){ return this.club_id; }
public void setClub_id(String club_id){ this.club_id=club_id; }
public Double getRest_amount(){ return this.rest_amount; }
public void setRest_amount(Double rest_amount){ this.rest_amount=rest_amount; }
public String getDescp() {
	return descp;
}
public void setDescp(String descp) {
	this.descp = descp;
}


}

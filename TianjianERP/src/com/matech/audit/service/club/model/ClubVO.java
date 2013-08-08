package com.matech.audit.service.club.model;

import com.matech.framework.pub.db.Table;

@Table(name="oa_club",pk="uuid")
public class ClubVO {
	
	 protected String uuid ;
	 protected String name ;
	 protected String create_time ;
	 protected Integer member_count ;
	 protected String timeout_time ;
	 protected String descp ;
	 protected String chairman_master_id ;
	 protected String chairman_slave_id ;
	 protected String finance_id ;
	 protected String publicity_id ;
	 protected String organ_id ;
	 protected String qq_group ;
	 protected Double fee_amount ;
	 protected String departmentid ;
	 protected String state ;
	 protected String userid ;


	 public String getUuid(){ return this.uuid; }
	 public void setUuid(String uuid){ this.uuid=uuid; }
	 public String getName(){ return this.name; }
	 public void setName(String name){ this.name=name; }
	 public String getCreate_time(){ return this.create_time; }
	 public void setCreate_time(String create_time){ this.create_time=create_time; }
	 public Integer getMember_count(){ return this.member_count; }
	 public void setMember_count(Integer member_count){ this.member_count=member_count; }
	 public String getTimeout_time(){ return this.timeout_time; }
	 public void setTimeout_time(String timeout_time){ this.timeout_time=timeout_time; }
	 public String getDescp(){ return this.descp; }
	 public void setDescp(String descp){ this.descp=descp; }
	 public String getChairman_master_id(){ return this.chairman_master_id; }
	 public void setChairman_master_id(String chairman_master_id){ this.chairman_master_id=chairman_master_id; }
	 public String getChairman_slave_id(){ return this.chairman_slave_id; }
	 public void setChairman_slave_id(String chairman_slave_id){ this.chairman_slave_id=chairman_slave_id; }
	 public String getFinance_id(){ return this.finance_id; }
	 public void setFinance_id(String finance_id){ this.finance_id=finance_id; }
	 public String getPublicity_id(){ return this.publicity_id; }
	 public void setPublicity_id(String publicity_id){ this.publicity_id=publicity_id; }
	 public String getOrgan_id(){ return this.organ_id; }
	 public void setOrgan_id(String organ_id){ this.organ_id=organ_id; }
	 public String getQq_group(){ return this.qq_group; }
	 public void setQq_group(String qq_group){ this.qq_group=qq_group; }
	 public Double getFee_amount(){ return this.fee_amount; }
	 public void setFee_amount(Double fee_amount){ this.fee_amount=fee_amount; }
	 public String getDepartmentid(){ return this.departmentid; }
	 public void setDepartmentid(String departmentid){ this.departmentid=departmentid; }
	 public String getState(){ return this.state; }
	 public void setState(String state){ this.state=state; }
	 public String getUserid(){ return this.userid; }
	 public void setUserid(String userid){ this.userid=userid; }
}

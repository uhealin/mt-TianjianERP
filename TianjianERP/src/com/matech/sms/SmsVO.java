package com.matech.sms;

import com.matech.framework.pub.db.Table;

@Table(name="s_sms",pk="uuid")
public class SmsVO {protected String uuid ;
protected String client_num ;
protected String mobile ;
protected String context ;
protected String send_date ;
protected String remark ;
protected String send_result ;
protected String un_key ;
protected String send_time ;

protected String create_time ;
protected String state ;
protected String host_ip ;
protected String reach_time;


public String getUuid(){ return this.uuid; }
public void setUuid(String uuid){ this.uuid=uuid; }
public String getClient_num(){ return this.client_num; }
public void setClient_num(String client_num){ this.client_num=client_num; }
public String getMobile(){ return this.mobile; }
public void setMobile(String mobile){ this.mobile=mobile; }
public String getContext(){ return this.context; }
public void setContext(String context){ this.context=context; }
public String getSend_date(){ return this.send_date; }
public void setSend_date(String send_date){ this.send_date=send_date; }
public String getRemark(){ return this.remark; }
public void setRemark(String remark){ this.remark=remark; }
public String getSend_result(){ return this.send_result; }
public void setSend_result(String send_result){ this.send_result=send_result; }
public String getUn_key(){ return this.un_key; }
public void setUn_key(String un_key){ this.un_key=un_key; }
public String getSend_time(){ return this.send_time; }
public void setSend_time(String send_time){ this.send_time=send_time; }

public String getCreate_time(){ return this.create_time; }
public void setCreate_time(String create_time){ this.create_time=create_time; }
public String getState(){ return this.state; }
public void setState(String state){ this.state=state; }
public String getHost_ip(){ return this.host_ip; }
public void setHost_ip(String host_ip){ this.host_ip=host_ip; }
public String getReach_time() {
	return reach_time;
}
public void setReach_time(String reach_time) {
	this.reach_time = reach_time;
}

}

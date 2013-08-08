package com.matech.sms;

import com.matech.framework.pub.db.Table;


@Table(name="s_sms_config",pk="code")
public class SmsConfigVO {

	
	 protected String code ;
	 protected String name ;
	 protected String service_url ;
	 protected String unit_code ;
	 protected String client_no ;
	 protected String subject_code ;
	 protected String send_type ;
	 protected String context_subfix ;
	 protected String test_phone ;
	 protected String start_time ;
	 protected String end_time ;
	 protected String allow_send_ind ;


	 public String getCode(){ return this.code; }
	 public void setCode(String code){ this.code=code; }
	 public String getName(){ return this.name; }
	 public void setName(String name){ this.name=name; }
	 public String getService_url(){ return this.service_url; }
	 public void setService_url(String service_url){ this.service_url=service_url; }
	 public String getUnit_code(){ return this.unit_code; }
	 public void setUnit_code(String unit_code){ this.unit_code=unit_code; }
	 public String getClient_no(){ return this.client_no; }
	 public void setClient_no(String client_no){ this.client_no=client_no; }
	 public String getSubject_code(){ return this.subject_code; }
	 public void setSubject_code(String subject_code){ this.subject_code=subject_code; }
	 public String getSend_type(){ return this.send_type; }
	 public void setSend_type(String send_type){ this.send_type=send_type; }
	 public String getContext_subfix(){ return this.context_subfix; }
	 public void setContext_subfix(String context_subfix){ this.context_subfix=context_subfix; }
	 public String getTest_phone(){ return this.test_phone; }
	 public void setTest_phone(String test_phone){ this.test_phone=test_phone; }
	 public String getStart_time(){ return this.start_time; }
	 public void setStart_time(String start_time){ this.start_time=start_time; }
	 public String getEnd_time(){ return this.end_time; }
	 public void setEnd_time(String end_time){ this.end_time=end_time; }
	 public String getAllow_send_ind(){ return this.allow_send_ind; }
	 public void setAllow_send_ind(String allow_send_ind){ this.allow_send_ind=allow_send_ind; }


}

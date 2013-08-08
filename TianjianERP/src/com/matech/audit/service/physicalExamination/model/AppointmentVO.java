package com.matech.audit.service.physicalExamination.model;

import com.matech.framework.pub.db.Table;

@Table(name="pe_appointment",pk="uuid")
public class AppointmentVO {
	
	 protected String uuid ;
	 protected Integer choose_batch ;
	 protected String user_id ;
	 protected String examination_get ;
	 protected String Results_get ;
	 protected String inform_uuid ;
	 protected String appointment_time ;


	 public String getUuid(){ return this.uuid; }
	 public void setUuid(String uuid){ this.uuid=uuid; }
	 public Integer getChoose_batch(){ return this.choose_batch; }
	 public void setChoose_batch(Integer choose_batch){ this.choose_batch=choose_batch; }
	 public String getUser_id(){ return this.user_id; }
	 public void setUser_id(String user_id){ this.user_id=user_id; }
	 public String getExamination_get(){ return this.examination_get; }
	 public void setExamination_get(String examination_get){ this.examination_get=examination_get; }
	 public String getResults_get(){ return this.Results_get; }
	 public void setResults_get(String Results_get){ this.Results_get=Results_get; }
	 public String getInform_uuid(){ return this.inform_uuid; }
	 public void setInform_uuid(String infrom_uuid){ this.inform_uuid=infrom_uuid; }
	 public String getAppointment_time(){ return this.appointment_time; }
	 public void setAppointment_time(String appointment_time){ this.appointment_time=appointment_time; }

}

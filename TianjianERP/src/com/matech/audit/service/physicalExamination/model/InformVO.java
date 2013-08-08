package com.matech.audit.service.physicalExamination.model;

import com.matech.framework.pub.db.Table;

@Table(name="pe_inform",pk="uuid")
public class InformVO {
	 protected String uuid ;
	 protected String notice_title ;
	 protected String detailed_description ;
	 protected Integer batch_number ;
	 protected Integer number_limit ;
	 protected String closing_date ;
	 protected Integer reality_number ;
	 protected Integer get_physicalList_count ;
	 protected Integer get_result_count ;
	 protected String batch_time_1 ;
	 protected String batch_time_2 ;
	 protected String batch_time_3 ;
	 protected String batch_time_4 ;
	 protected String batch_time_5 ;
	 protected String batch_time_6 ;
	 protected String batch_time_7 ;
	 protected String batch_time_8 ;
	 protected String person_select_ids ;
	 protected String person_select_names ;
	 protected String attachment_id ;
	 protected String release_date ;
	 protected String release_department ;


	 public String getUuid(){ return this.uuid; }
	 public void setUuid(String uuid){ this.uuid=uuid; }
	 public String getNotice_title(){ return this.notice_title; }
	 public void setNotice_title(String notice_title){ this.notice_title=notice_title; }
	 public String getDetailed_description(){ return this.detailed_description; }
	 public void setDetailed_description(String detailed_description){ this.detailed_description=detailed_description; }
	 public Integer getBatch_number(){ return this.batch_number; }
	 public void setBatch_number(Integer batch_number){ this.batch_number=batch_number; }
	 public Integer getNumber_limit(){ return this.number_limit; }
	 public void setNumber_limit(Integer number_limit){ this.number_limit=number_limit; }
	 public String getClosing_date(){ return this.closing_date; }
	 public void setClosing_date(String closing_date){ this.closing_date=closing_date; }
	 public Integer getReality_number(){ return this.reality_number; }
	 public void setReality_number(Integer reality_number){ this.reality_number=reality_number; }
	 public Integer getGet_physicalList_count(){ return this.get_physicalList_count; }
	 public void setGet_physicalList_count(Integer get_physicalList_count){ this.get_physicalList_count=get_physicalList_count; }
	 public Integer getGet_result_count(){ return this.get_result_count; }
	 public void setGet_result_count(Integer get_result_count){ this.get_result_count=get_result_count; }
	 public String getBatch_time_1(){ return this.batch_time_1; }
	 public void setBatch_time_1(String batch_time_1){ this.batch_time_1=batch_time_1; }
	 public String getBatch_time_2(){ return this.batch_time_2; }
	 public void setBatch_time_2(String batch_time_2){ this.batch_time_2=batch_time_2; }
	 public String getBatch_time_3(){ return this.batch_time_3; }
	 public void setBatch_time_3(String batch_time_3){ this.batch_time_3=batch_time_3; }
	 public String getBatch_time_4(){ return this.batch_time_4; }
	 public void setBatch_time_4(String batch_time_4){ this.batch_time_4=batch_time_4; }
	 public String getBatch_time_5(){ return this.batch_time_5; }
	 public void setBatch_time_5(String batch_time_5){ this.batch_time_5=batch_time_5; }
	 public String getBatch_time_6(){ return this.batch_time_6; }
	 public void setBatch_time_6(String batch_time_6){ this.batch_time_6=batch_time_6; }
	 public String getBatch_time_7(){ return this.batch_time_7; }
	 public void setBatch_time_7(String batch_time_7){ this.batch_time_7=batch_time_7; }
	 public String getBatch_time_8(){ return this.batch_time_8; }
	 public void setBatch_time_8(String batch_time_8){ this.batch_time_8=batch_time_8; }
	 public String getPerson_select_ids(){ return this.person_select_ids; }
	 public void setPerson_select_ids(String person_select_ids){ this.person_select_ids=person_select_ids; }
	 public String getPerson_select_names(){ return this.person_select_names; }
	 public void setPerson_select_names(String person_select_names){ this.person_select_names=person_select_names; }
	 public String getAttachment_id(){ return this.attachment_id; }
	 public void setAttachment_id(String attachment_id){ this.attachment_id=attachment_id; }
	 public String getRelease_date(){ return this.release_date; }
	 public void setRelease_date(String release_date){ this.release_date=release_date; }
	 public String getRelease_department(){ return this.release_department; }
	 public void setRelease_department(String release_department){ this.release_department=release_department; }
}

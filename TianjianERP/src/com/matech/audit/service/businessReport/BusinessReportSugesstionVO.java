package com.matech.audit.service.businessReport;

import com.matech.framework.pub.db.Table;

@Table(name="k_business_report_suggestions",pk="uuid")
public class BusinessReportSugesstionVO {
	 protected String uuid ;
	 protected String suggestion_id ;
	 protected String kr_uuid ;
	 protected String suggestion_time ;
	 protected String suggestion_context ;
	 protected String suggestion_user ;
	 protected String suggestion_help ;


	 public String getUuid(){ return this.uuid; }
	 public void setUuid(String uuid){ this.uuid=uuid; }
	 public String getSuggestion_id(){ return this.suggestion_id; }
	 public void setSuggestion_id(String suggestion_id){ this.suggestion_id=suggestion_id; }
	 public String getKr_uuid(){ return this.kr_uuid; }
	 public void setKr_uuid(String kr_uuid){ this.kr_uuid=kr_uuid; }
	 public String getSuggestion_time(){ return this.suggestion_time; }
	 public void setSuggestion_time(String suggestion_time){ this.suggestion_time=suggestion_time; }
	 public String getSuggestion_context(){ return this.suggestion_context; }
	 public void setSuggestion_context(String suggestion_context){ this.suggestion_context=suggestion_context; }
	 public String getSuggestion_user(){ return this.suggestion_user; }
	 public void setSuggestion_user(String suggestion_user){ this.suggestion_user=suggestion_user; }
	 public String getSuggestion_help(){ return this.suggestion_help; }
	 public void setSuggestion_help(String suggestion_help){ this.suggestion_help=suggestion_help; }


}

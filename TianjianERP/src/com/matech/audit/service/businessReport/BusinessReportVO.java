package com.matech.audit.service.businessReport;

import com.matech.framework.pub.db.Table;

@Table(name="k_business_report",pk="uuid")
public class BusinessReportVO {
	 protected String uuid ;
	 protected Integer audit_departmentid ;
	 protected Integer apply_userid ;
	 protected String audit_groupname ;
	 protected String company_Name ;
	 protected String report_type ;
	 protected String company_type ;
	 protected String report_BeginTime ;
	 protected String report_EndTime ;
	 protected String major_Business ;
	 protected String report_Suggestion_Type ;
	 protected String stockholder ;
	 protected String report_use ;
	 protected String group_major_part_ind ;
	 protected String company_first_report ;
	 protected String unusual_deal ;
	 protected String merge_report_ind ;
	 protected Integer audit_person ;
	 protected Integer merge_count ;
	 protected Integer reaudit_person ;
	 protected String assets_totalamount ;
	 protected String income ;
	 protected String net_profit ;
	 protected String reaudit_time ;
	 protected String except_complete_time ;
	 protected String state ;
	 protected Integer appoint_human ;
	 protected String remarks ;
	 protected String property ;
	 protected String cancelstate ;
	 protected String project_reaudit_person ;
	 protected String department_reaudit_person ;
	 protected String tj_report_type ;
	 protected String report_target ;
	 protected String report_moreinfo ;
	 protected String report_validate_type ;
	 protected String report_pay_type ;
	 protected String report_audit_amount ;
	 protected String report_validate_amount_date ;
	 protected String report_data_receive_ind ;
	 protected String report_data_receive_time ;
	 protected String appoint_ind ;
	 protected String issue_partner ;
	 protected String reaudit_time_ampm;
	 protected String except_complete_time_ampm;
	 protected String report_level ;


	 public String getReport_level() {
		return report_level;
	}
	public void setReport_level(String report_level) {
		this.report_level = report_level;
	}
	public String getReaudit_time_ampm() {
		return reaudit_time_ampm;
	}
	public void setReaudit_time_ampm(String reaudit_time_ampm) {
		this.reaudit_time_ampm = reaudit_time_ampm;
	}
	public String getExcept_complete_time_ampm() {
		return except_complete_time_ampm;
	}
	public void setExcept_complete_time_ampm(String except_complete_time_ampm) {
		this.except_complete_time_ampm = except_complete_time_ampm;
	}
	public String getUuid(){ return this.uuid; }
	 public void setUuid(String uuid){ this.uuid=uuid; }
	 public Integer getAudit_departmentid(){ return this.audit_departmentid; }
	 public void setAudit_departmentid(Integer audit_departmentid){ this.audit_departmentid=audit_departmentid; }
	 public Integer getApply_userid(){ return this.apply_userid; }
	 public void setApply_userid(Integer apply_userid){ this.apply_userid=apply_userid; }
	 public String getAudit_groupname(){ return this.audit_groupname; }
	 public void setAudit_groupname(String audit_groupname){ this.audit_groupname=audit_groupname; }
	 public String getCompany_Name(){ return this.company_Name; }
	 public void setCompany_Name(String company_Name){ this.company_Name=company_Name; }
	 public String getReport_type(){ return this.report_type; }
	 public void setReport_type(String report_type){ this.report_type=report_type; }
	 public String getCompany_type(){ return this.company_type; }
	 public void setCompany_type(String company_type){ this.company_type=company_type; }
	 public String getReport_BeginTime(){ return this.report_BeginTime; }
	 public void setReport_BeginTime(String report_BeginTime){ this.report_BeginTime=report_BeginTime; }
	 public String getReport_EndTime(){ return this.report_EndTime; }
	 public void setReport_EndTime(String report_EndTime){ this.report_EndTime=report_EndTime; }
	 public String getMajor_Business(){ return this.major_Business; }
	 public void setMajor_Business(String major_Business){ this.major_Business=major_Business; }
	 public String getReport_Suggestion_Type(){ return this.report_Suggestion_Type; }
	 public void setReport_Suggestion_Type(String report_Suggestion_Type){ this.report_Suggestion_Type=report_Suggestion_Type; }
	 public String getStockholder(){ return this.stockholder; }
	 public void setStockholder(String stockholder){ this.stockholder=stockholder; }
	 public String getReport_use(){ return this.report_use; }
	 public void setReport_use(String report_use){ this.report_use=report_use; }
	 public String getGroup_major_part_ind(){ return this.group_major_part_ind; }
	 public void setGroup_major_part_ind(String group_major_part_ind){ this.group_major_part_ind=group_major_part_ind; }
	 public String getCompany_first_report(){ return this.company_first_report; }
	 public void setCompany_first_report(String company_first_report){ this.company_first_report=company_first_report; }
	 public String getUnusual_deal(){ return this.unusual_deal; }
	 public void setUnusual_deal(String unusual_deal){ this.unusual_deal=unusual_deal; }
	 public String getMerge_report_ind(){ return this.merge_report_ind; }
	 public void setMerge_report_ind(String merge_report_ind){ this.merge_report_ind=merge_report_ind; }
	 public Integer getAudit_person(){ return this.audit_person; }
	 public void setAudit_person(Integer audit_person){ this.audit_person=audit_person; }
	 public Integer getMerge_count(){ return this.merge_count; }
	 public void setMerge_count(Integer merge_count){ this.merge_count=merge_count; }
	 public Integer getReaudit_person(){ return this.reaudit_person; }
	 public void setReaudit_person(Integer reaudit_person){ this.reaudit_person=reaudit_person; }
	 public String getAssets_totalamount(){ return this.assets_totalamount; }
	 public void setAssets_totalamount(String assets_totalamount){ this.assets_totalamount=assets_totalamount; }
	 public String getIncome(){ return this.income; }
	 public void setIncome(String income){ this.income=income; }
	 public String getNet_profit(){ return this.net_profit; }
	 public void setNet_profit(String net_profit){ this.net_profit=net_profit; }
	 public String getReaudit_time(){ return this.reaudit_time; }
	 public void setReaudit_time(String reaudit_time){ this.reaudit_time=reaudit_time; }
	 public String getExcept_complete_time(){ return this.except_complete_time; }
	 public void setExcept_complete_time(String except_complete_time){ this.except_complete_time=except_complete_time; }
	 public String getState(){ return this.state; }
	 public void setState(String state){ this.state=state; }
	 public Integer getAppoint_human(){ return this.appoint_human; }
	 public void setAppoint_human(Integer appoint_human){ this.appoint_human=appoint_human; }
	 public String getRemarks(){ return this.remarks; }
	 public void setRemarks(String remarks){ this.remarks=remarks; }
	 public String getProperty(){ return this.property; }
	 public void setProperty(String property){ this.property=property; }
	 public String getCancelstate(){ return this.cancelstate; }
	 public void setCancelstate(String cancelstate){ this.cancelstate=cancelstate; }
	 public String getProject_reaudit_person(){ return this.project_reaudit_person; }
	 public void setProject_reaudit_person(String project_reaudit_person){ this.project_reaudit_person=project_reaudit_person; }
	 public String getDepartment_reaudit_person(){ return this.department_reaudit_person; }
	 public void setDepartment_reaudit_person(String department_reaudit_person){ this.department_reaudit_person=department_reaudit_person; }
	 public String getTj_report_type(){ return this.tj_report_type; }
	 public void setTj_report_type(String tj_report_type){ this.tj_report_type=tj_report_type; }
	 public String getReport_target(){ return this.report_target; }
	 public void setReport_target(String report_target){ this.report_target=report_target; }
	 public String getReport_moreinfo(){ return this.report_moreinfo; }
	 public void setReport_moreinfo(String report_moreinfo){ this.report_moreinfo=report_moreinfo; }
	 public String getReport_validate_type(){ return this.report_validate_type; }
	 public void setReport_validate_type(String report_validate_type){ this.report_validate_type=report_validate_type; }
	 public String getReport_pay_type(){ return this.report_pay_type; }
	 public void setReport_pay_type(String report_pay_type){ this.report_pay_type=report_pay_type; }
	 public String getReport_audit_amount(){ return this.report_audit_amount; }
	 public void setReport_audit_amount(String report_audit_amount){ this.report_audit_amount=report_audit_amount; }
	 public String getReport_validate_amount_date(){ return this.report_validate_amount_date; }
	 public void setReport_validate_amount_date(String report_validate_amount_date){ this.report_validate_amount_date=report_validate_amount_date; }
	 public String getReport_data_receive_ind(){ return this.report_data_receive_ind; }
	 public void setReport_data_receive_ind(String report_data_receive_ind){ this.report_data_receive_ind=report_data_receive_ind; }
	 public String getReport_data_receive_time(){ return this.report_data_receive_time; }
	 public void setReport_data_receive_time(String report_data_receive_time){ this.report_data_receive_time=report_data_receive_time; }
	 public String getAppoint_ind(){ return this.appoint_ind; }
	 public void setAppoint_ind(String appoint_ind){ this.appoint_ind=appoint_ind; }
	 public String getIssue_partner(){ return this.issue_partner; }
	 public void setIssue_partner(String issue_partner){ this.issue_partner=issue_partner; }

}

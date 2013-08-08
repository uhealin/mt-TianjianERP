package com.matech.audit.service.doc.model;

import com.matech.framework.pub.db.Table;

@Table(name="oa_doc_post_file",pk="uuid")
public class DocPostFileVO {
	
	
			 protected String uuid ;
			 protected String userid ;
			 protected String departmentid ;
			 protected String modify_date ;
			 protected String post_addr ;
			 protected String beaccount_addr ;
			 protected String title ;
			 protected String hq_id_1 ;
			 protected String hq_id_2 ;
			 protected String hq_id_3 ;
			 protected String hq_id_4 ;
			 protected String qf_id ;
			 protected String hy_id ;
			 protected String creater_id ;
			 protected String en_type ;
			 protected String report_date ;
			 protected String seal_date ;
			 protected Double fin_amount ;
			 protected String fin_amount_remark ;
			 protected Double amount ;
			 protected String amount_remark ;
			 protected String remark ;
			 protected String project_checker_id ;
			 protected String doc_no ;
			 protected String doc_id ;
			 protected String doc_type ;
			 protected Integer file_count ;
			 protected String verify_code ;
			 protected String project_leader_id ;
             protected String doc_year;
             protected String doc_seqno;
             protected Integer del_ind;
             protected String cancel_state;
             protected String cancel_reason;
             protected String cancel_date;

			 public String getUuid(){ return this.uuid; }
			 public void setUuid(String uuid){ this.uuid=uuid; }
			 public String getUserid(){ return this.userid; }
			 public void setUserid(String userid){ this.userid=userid; }
			 public String getDepartmentid(){ return this.departmentid; }
			 public void setDepartmentid(String departmentid){ this.departmentid=departmentid; }
			 public String getModify_date(){ return this.modify_date; }
			 public void setModify_date(String modify_date){ this.modify_date=modify_date; }
			 public String getPost_addr(){ return this.post_addr; }
			 public void setPost_addr(String post_addr){ this.post_addr=post_addr; }
			 public String getBeaccount_addr(){ return this.beaccount_addr; }
			 public void setBeaccount_addr(String beaccount_addr){ this.beaccount_addr=beaccount_addr; }
			 public String getTitle(){ return this.title; }
			 public void setTitle(String title){ this.title=title; }
			 public String getHq_id_1(){ return this.hq_id_1; }
			 public void setHq_id_1(String hq_id_1){ this.hq_id_1=hq_id_1; }
			 public String getHq_id_2(){ return this.hq_id_2; }
			 public void setHq_id_2(String hq_id_2){ this.hq_id_2=hq_id_2; }
			 public String getHq_id_3(){ return this.hq_id_3; }
			 public void setHq_id_3(String hq_id_3){ this.hq_id_3=hq_id_3; }
			 public String getHq_id_4(){ return this.hq_id_4; }
			 public void setHq_id_4(String hq_id_4){ this.hq_id_4=hq_id_4; }
			 public String getQf_id(){ return this.qf_id; }
			 public void setQf_id(String qf_id){ this.qf_id=qf_id; }
			 public String getHy_id(){ return this.hy_id; }
			 public void setHy_id(String hy_id){ this.hy_id=hy_id; }
			 public String getCreater_id(){ return this.creater_id; }
			 public void setCreater_id(String creater_id){ this.creater_id=creater_id; }
			 public String getEn_type(){ return this.en_type; }
			 public void setEn_type(String en_type){ this.en_type=en_type; }
			 public String getReport_date(){ return this.report_date; }
			 public void setReport_date(String report_date){ this.report_date=report_date; }
			 public String getSeal_date(){ return this.seal_date; }
			 public void setSeal_date(String seal_date){ this.seal_date=seal_date; }
			 public Double getFin_amount(){ return this.fin_amount; }
			 public void setFin_amount(Double fin_amount){ this.fin_amount=fin_amount; }
			 public String getFin_amount_remark(){ return this.fin_amount_remark; }
			 public void setFin_amount_remark(String fin_amount_remark){ this.fin_amount_remark=fin_amount_remark; }
			 public Double getAmount(){ return this.amount; }
			 public void setAmount(Double amount){ this.amount=amount; }
			 public String getAmount_remark(){ return this.amount_remark; }
			 public void setAmount_remark(String amount_remark){ this.amount_remark=amount_remark; }
			 public String getRemark(){ return this.remark; }
			 public void setRemark(String remark){ this.remark=remark; }
			 public String getProject_checker_id(){ return this.project_checker_id; }
			 public void setProject_checker_id(String project_checker_id){ this.project_checker_id=project_checker_id; }
			 public String getDoc_no(){ return this.doc_no; }
			 public void setDoc_no(String doc_no){ this.doc_no=doc_no; }
			 public String getDoc_id(){ return this.doc_id; }
			 public void setDoc_id(String doc_id){ this.doc_id=doc_id; }
			 public String getDoc_type(){ return this.doc_type; }
			 public void setDoc_type(String doc_type){ this.doc_type=doc_type; }
			 public Integer getFile_count(){ return this.file_count; }
			 public void setFile_count(Integer file_count){ this.file_count=file_count; }
			 public String getVerify_code(){ return this.verify_code; }
			 public void setVerify_code(String verify_code){ this.verify_code=verify_code; }
			 public String getProject_leader_id(){ return this.project_leader_id; }
			 public void setProject_leader_id(String project_leader_id){ this.project_leader_id=project_leader_id; }
			public String getDoc_year() {
				return doc_year;
			}
			public void setDoc_year(String doc_year) {
				this.doc_year = doc_year;
			}
			public String getDoc_seqno() {
				return doc_seqno;
			}
			public void setDoc_seqno(String doc_seqno) {
				this.doc_seqno = doc_seqno;
			}
			public Integer getDel_ind() {
				return del_ind;
			}
			public void setDel_ind(Integer del_ind) {
				this.del_ind = del_ind;
			}
			public String getCancel_state() {
				return cancel_state;
			}
			public void setCancel_state(String cancel_state) {
				this.cancel_state = cancel_state;
			}
			public String getCancel_reason() {
				return cancel_reason;
			}
			public void setCancel_reason(String cancel_reason) {
				this.cancel_reason = cancel_reason;
			}
			public String getCancel_date() {
				return cancel_date;
			}
			public void setCancel_date(String cancel_date) {
				this.cancel_date = cancel_date;
			}


}

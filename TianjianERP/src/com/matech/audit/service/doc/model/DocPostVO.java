package com.matech.audit.service.doc.model;

import com.matech.audit.service.doc.DocPostService.Node;
import com.matech.framework.pub.db.Table;

@Table(name="oa_doc_post",pk="uuid")
public class DocPostVO {
	 protected String uuid ;
	 protected String post_addr_names ;
	 protected String copy_addr_names ;
	 protected String title ;
	 protected String doc_type ;
	 protected String creater_names ;
	 protected String creater_phones ;
	 protected String countersigner_names ;
	 protected String countersigner_phones ;
	 protected String signissuer_names ;
	 protected String signissuer_phones ;
	 protected String checker_names ;
	 protected String checker_phones ;
	 protected String signissue_date ;
	 protected String timeout_date ;
	 protected String file_has_appendix_ind ;
	 protected Integer file_hardcover_count ;
	 protected Integer file_simple_count ;
	 protected Integer file_delivery_count ;
	 protected Integer file_saved_count ;
	 protected Integer file_total_count ;
	 protected String handle_state ;
	 protected String dep_id ;
	 protected String doc_no ;
	 protected String post_addr_ids ;
	 protected String copy_addr_ids ;
	 protected String creater_ids ;
	 protected String countersigner_ids ;
	 protected String checker_ids ;
	 protected Integer doc_seq ;
	 protected String signissuer_ids ;
	 protected String create_date ;
	 protected String node_code ;
	 protected String handler_ids ;
	 protected String handler_names ;
	 protected String departmentid ;
	 protected String userid ;
	 protected String countersign_date ;
	 protected String node_remark ;
	 protected String ctype ;
	 protected String project_member_ids ;
	 protected String project_member_names ;
	 protected String countersign_info ;
	 protected String signissuer_info ;
	 protected String check_info ;
	 protected String project_member_sign_info ;
     protected String apply_type;
     protected String attach_id;
     protected String dep_leader_ids;
     protected String dep_leader_names;
     protected Integer del_ind;
     protected String cur_hq_id;
	 protected String cancel_state;
	 protected String cancel_date;
	 protected String cancel_reason;
	 protected String beaccount_addr;
	 protected String pccpa_docid;
     
	 public String getUuid(){ return this.uuid; }
	 public void setUuid(String uuid){ this.uuid=uuid; }
	 public String getPost_addr_names(){ return this.post_addr_names; }
	 public void setPost_addr_names(String post_addr_names){ this.post_addr_names=post_addr_names; }
	 public String getCopy_addr_names(){ return this.copy_addr_names; }
	 public void setCopy_addr_names(String copy_addr_names){ this.copy_addr_names=copy_addr_names; }
	 public String getTitle(){ return this.title; }
	 public void setTitle(String title){ this.title=title; }
	 public String getDoc_type(){ return this.doc_type; }
	 public void setDoc_type(String doc_type){ this.doc_type=doc_type; }
	 public String getCreater_names(){ return this.creater_names; }
	 public void setCreater_names(String creater_names){ this.creater_names=creater_names; }
	 public String getCreater_phones(){ return this.creater_phones; }
	 public void setCreater_phones(String creater_phones){ this.creater_phones=creater_phones; }
	 public String getCountersigner_names(){ return this.countersigner_names; }
	 public void setCountersigner_names(String countersigner_names){ this.countersigner_names=countersigner_names; }
	 public String getCountersigner_phones(){ return this.countersigner_phones; }
	 public void setCountersigner_phones(String countersigner_phones){ this.countersigner_phones=countersigner_phones; }
	 public String getSignissuer_names(){ return this.signissuer_names; }
	 public void setSignissuer_names(String signissuer_names){ this.signissuer_names=signissuer_names; }
	 public String getSignissuer_phones(){ return this.signissuer_phones; }
	 public void setSignissuer_phones(String signissuer_phones){ this.signissuer_phones=signissuer_phones; }
	 public String getChecker_names(){ return this.checker_names; }
	 public void setChecker_names(String checker_names){ this.checker_names=checker_names; }
	 public String getChecker_phones(){ return this.checker_phones; }
	 public void setChecker_phones(String checker_phones){ this.checker_phones=checker_phones; }
	 public String getSignissue_date(){ return this.signissue_date; }
	 public void setSignissue_date(String signissue_date){ this.signissue_date=signissue_date; }
	 public String getTimeout_date(){ return this.timeout_date; }
	 public void setTimeout_date(String timeout_date){ this.timeout_date=timeout_date; }
	 public String getFile_has_appendix_ind(){ return this.file_has_appendix_ind; }
	 public void setFile_has_appendix_ind(String file_has_appendix_ind){ this.file_has_appendix_ind=file_has_appendix_ind; }
	 public Integer getFile_hardcover_count(){ return this.file_hardcover_count; }
	 public void setFile_hardcover_count(Integer file_hardcover_count){ this.file_hardcover_count=file_hardcover_count; }
	 public Integer getFile_simple_count(){ return this.file_simple_count; }
	 public void setFile_simple_count(Integer file_simple_count){ this.file_simple_count=file_simple_count; }
	 public Integer getFile_delivery_count(){ return this.file_delivery_count; }
	 public void setFile_delivery_count(Integer file_delivery_count){ this.file_delivery_count=file_delivery_count; }
	 public Integer getFile_saved_count(){ return this.file_saved_count; }
	 public void setFile_saved_count(Integer file_saved_count){ this.file_saved_count=file_saved_count; }
	 public Integer getFile_total_count(){ return this.file_total_count; }
	 public void setFile_total_count(Integer file_total_count){ this.file_total_count=file_total_count; }
	 public String getHandle_state(){ return this.handle_state; }
	 public void setHandle_state(String handle_state){ this.handle_state=handle_state; }
	 public String getDep_id(){ return this.dep_id; }
	 public void setDep_id(String dep_id){ this.dep_id=dep_id; }
	 public String getDoc_no(){ return this.doc_no; }
	 public void setDoc_no(String doc_no){ this.doc_no=doc_no; }
	 public String getPost_addr_ids(){ return this.post_addr_ids; }
	 public void setPost_addr_ids(String post_addr_ids){ this.post_addr_ids=post_addr_ids; }
	 public String getCopy_addr_ids(){ return this.copy_addr_ids; }
	 public void setCopy_addr_ids(String copy_addr_ids){ this.copy_addr_ids=copy_addr_ids; }
	 public String getCreater_ids(){ return this.creater_ids; }
	 public void setCreater_ids(String creater_ids){ this.creater_ids=creater_ids; }
	 public String getCountersigner_ids(){ return this.countersigner_ids; }
	 public void setCountersigner_ids(String countersigner_ids){ this.countersigner_ids=countersigner_ids; }
	 public String getChecker_ids(){ return this.checker_ids; }
	 public void setChecker_ids(String checker_ids){ this.checker_ids=checker_ids; }
	 public Integer getDoc_seq(){ return this.doc_seq; }
	 public void setDoc_seq(Integer doc_seq){ this.doc_seq=doc_seq; }
	 public String getSignissuer_ids(){ return this.signissuer_ids; }
	 public void setSignissuer_ids(String signissuer_ids){ this.signissuer_ids=signissuer_ids; }
	 public String getCreate_date(){ return this.create_date; }
	 public void setCreate_date(String create_date){ this.create_date=create_date; }
	 public String getNode_code(){ return this.node_code; }
	 public void setNode_code(String node_code){ this.node_code=node_code; }
	 public String getHandler_ids(){ return this.handler_ids; }
	 public void setHandler_ids(String handler_ids){ this.handler_ids=handler_ids; }
	 public String getHandler_names(){ return this.handler_names; }
	 public void setHandler_names(String handler_names){ this.handler_names=handler_names; }
	 public String getDepartmentid(){ return this.departmentid; }
	 public void setDepartmentid(String departmentid){ this.departmentid=departmentid; }
	 public String getUserid(){ return this.userid; }
	 public void setUserid(String userid){ this.userid=userid; }
	 public String getCountersign_date(){ return this.countersign_date; }
	 public void setCountersign_date(String countersign_date){ this.countersign_date=countersign_date; }
	 public String getNode_remark(){ return this.node_remark; }
	 public void setNode_remark(String node_remark){ this.node_remark=node_remark; }
	 public String getCtype(){ return this.ctype; }
	 public void setCtype(String ctype){ this.ctype=ctype; }
	 public String getProject_member_ids(){ return this.project_member_ids; }
	 public void setProject_member_ids(String project_member_ids){ this.project_member_ids=project_member_ids; }
	 public String getProject_member_names(){ return this.project_member_names; }
	 public void setProject_member_names(String project_member_names){ this.project_member_names=project_member_names; }
	 public String getCountersign_info(){ return this.countersign_info; }
	 public void setCountersign_info(String countersign_info){ this.countersign_info=countersign_info; }
	 public String getSignissuer_info(){ return this.signissuer_info; }
	 public void setSignissuer_info(String signissuer_info){ this.signissuer_info=signissuer_info; }
	 public String getCheck_info(){ return this.check_info; }
	 public void setCheck_info(String check_info){ this.check_info=check_info; }
	public String getProject_member_sign_info() {
		return project_member_sign_info;
	}
	public void setProject_member_sign_info(String project_member_sign_info) {
		this.project_member_sign_info = project_member_sign_info;
	}
	public String getApply_type() {
		return apply_type;
	}
	public void setApply_type(String apply_type) {
		this.apply_type = apply_type;
	}
	public String getAttach_id() {
		return attach_id;
	}
	public void setAttach_id(String attach_id) {
		this.attach_id = attach_id;
	}
	public String getDep_leader_ids() {
		return dep_leader_ids;
	}
	public void setDep_leader_ids(String dep_leader_ids) {
		this.dep_leader_ids = dep_leader_ids;
	}
	public String getDep_leader_names() {
		return dep_leader_names;
	}
	public void setDep_leader_names(String dep_leader_names) {
		this.dep_leader_names = dep_leader_names;
	}
	public Integer getDel_ind() {
		return del_ind;
	}
	public void setDel_ind(Integer del_ind) {
		this.del_ind = del_ind;
	}
	public String getCur_hq_id() {
		return cur_hq_id;
	}
	public void setCur_hq_id(String cur_hq_id) {
		this.cur_hq_id = cur_hq_id;
	}
	public String getCancel_state() {
		return cancel_state;
	}
	public void setCancel_state(String cancel_state) {
		this.cancel_state = cancel_state;
	}
	public String getCancel_date() {
		return cancel_date;
	}
	public void setCancel_date(String cancel_date) {
		this.cancel_date = cancel_date;
	}
	public String getCancel_reason() {
		return cancel_reason;
	}
	public void setCancel_reason(String cancel_reason) {
		this.cancel_reason = cancel_reason;
	}
	public String getBeaccount_addr() {
		return beaccount_addr;
	}
	public void setBeaccount_addr(String beaccount_addr) {
		this.beaccount_addr = beaccount_addr;
	}
	public String getPccpa_docid() {
		return pccpa_docid;
	}
	public void setPccpa_docid(String pccpa_docid) {
		this.pccpa_docid = pccpa_docid;
	}



}

package com.matech.audit.service.doc.model;

import com.matech.framework.pub.db.Table;



@Table(name="oa_doc_rec",pk="uuid",excludeColumns="assigner_id")
public class DocRecVO {

	 protected String uuid ;
	 protected String post_doc_no ;
	 protected String file_name ;
	 protected String file_source ;
	 protected String handle_type ;
	 protected String rec_doc_no ;
	 protected String rec_date ;
	 protected String timeout_date ;
	 protected String roam_range_ids ;
	 protected String state ;
	 protected String handle_date ;
	 protected String handle_remark ;
	 protected String has_appendfix_ind ;
	 protected Integer handle_hour ;
	 protected String file_upload ;
	 protected String check_state ;
	 protected String public_ind ;
	 protected String roam_range_names ;
	 protected String handler_ids ;
	 protected String handler_names ;
	 protected String departmentid ;
	 protected Integer doc_seq ;
	 protected String creater_id ;
	 protected String creater_name ;
     protected String post_organ;
     protected String remark;
   
	 
	 public String getUuid(){ return this.uuid; }
	 public void setUuid(String uuid){ this.uuid=uuid; }
	 public String getPost_doc_no(){ return this.post_doc_no; }
	 public void setPost_doc_no(String post_doc_no){ this.post_doc_no=post_doc_no; }
	 public String getFile_name(){ return this.file_name; }
	 public void setFile_name(String file_name){ this.file_name=file_name; }
	 public String getFile_source(){ return this.file_source; }
	 public void setFile_source(String file_source){ this.file_source=file_source; }
	 public String getHandle_type(){ return this.handle_type; }
	 public void setHandle_type(String handle_type){ this.handle_type=handle_type; }
	 public String getRec_doc_no(){ return this.rec_doc_no; }
	 public void setRec_doc_no(String rec_doc_no){ this.rec_doc_no=rec_doc_no; }
	 public String getRec_date(){ return this.rec_date; }
	 public void setRec_date(String rec_date){ this.rec_date=rec_date; }
	 public String getTimeout_date(){ return this.timeout_date; }
	 public void setTimeout_date(String timeout_date){ this.timeout_date=timeout_date; }
	 public String getRoam_range_ids(){ return this.roam_range_ids; }
	 public void setRoam_range_ids(String roam_range_ids){ this.roam_range_ids=roam_range_ids; }
	 public String getState(){ return this.state; }
	 public void setState(String state){ this.state=state; }
	 public String getHandle_date(){ return this.handle_date; }
	 public void setHandle_date(String handle_date){ this.handle_date=handle_date; }
	 public String getHandle_remark(){ return this.handle_remark; }
	 public void setHandle_remark(String handle_remark){ this.handle_remark=handle_remark; }
	 public String getHas_appendfix_ind(){ return this.has_appendfix_ind; }
	 public void setHas_appendfix_ind(String has_appendfix_ind){ this.has_appendfix_ind=has_appendfix_ind; }
	 public Integer getHandle_hour(){ return this.handle_hour; }
	 public void setHandle_hour(Integer handle_hour){ this.handle_hour=handle_hour; }
	 public String getFile_upload(){ return this.file_upload; }
	 public void setFile_upload(String file_upload){ this.file_upload=file_upload; }
	 public String getCheck_state(){ return this.check_state; }
	 public void setCheck_state(String check_state){ this.check_state=check_state; }
	 public String getPublic_ind(){ return this.public_ind; }
	 public void setPublic_ind(String public_ind){ this.public_ind=public_ind; }
	 public String getRoam_range_names(){ return this.roam_range_names; }
	 public void setRoam_range_names(String roam_range_names){ this.roam_range_names=roam_range_names; }
	 public String getHandler_ids(){ return this.handler_ids; }
	 public void setHandler_ids(String handler_ids){ this.handler_ids=handler_ids; }
	 public String getHandler_names(){ return this.handler_names; }
	 public void setHandler_names(String handler_names){ this.handler_names=handler_names; }
	 public String getDepartmentid(){ return this.departmentid; }
	 public void setDepartmentid(String departmentid){ this.departmentid=departmentid; }
	 public Integer getDoc_seq(){ return this.doc_seq; }
	 public void setDoc_seq(Integer doc_seq){ this.doc_seq=doc_seq; }
	 public String getCreater_id(){ return this.creater_id; }
	 public void setCreater_id(String creater_id){ this.creater_id=creater_id; }
	 public String getCreater_name(){ return this.creater_name; }
	 public void setCreater_name(String creater_name){ this.creater_name=creater_name; }
	public String getPost_organ() {
		return post_organ;
	}
	public void setPost_organ(String post_organ) {
		this.post_organ = post_organ;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}

	



}

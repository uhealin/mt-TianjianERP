package com.matech.audit.service.pccpa.model;

import com.matech.framework.pub.db.Table;

@Table(name="tj_word",pk="id")
public class WordVO {
	 protected String id ;
	 protected String file_name ;
	 protected String post_addr ;
	 protected String copy_addr ;
	 protected String beaccount_addr ;
	 protected String doc_no ;
	 protected String doc_type_name ;
	 protected String apply_type_name ;
	 protected String doc_template ;
	 protected String title ;
	 protected String create_date ;
	 protected String hq_names ;
	 protected String hq_phones ;
	 protected String has_hq_names ;
	 protected String qf_names ;
	 protected String qf_phones ;
	 protected Integer qf_state ;
	 protected String hy_names ;
	 protected String hy_phones ;
	 protected Integer hy_state ;
	 protected String hy_date ;
	 protected String qf_date ;
	 protected String timeout_date ;
	 protected String creater_name ;
	 protected String dep_id ;
	 protected String dep_name ;
	 protected Integer attach ;
	 protected String area_name ;
	 protected String area_id ;
	 protected String file_counts ;
	 protected String handler_names ;
	 protected String hq_info ;
	 protected String qf_info ;
	 protected Integer ISQF ;
	 protected String hy_info ;
	 protected Integer IsHY ;
	 protected String SHTime ;


	 public String getId(){ return this.id; }
	 public void setId(String id){ this.id=id; }
	 public String getFile_name(){ return this.file_name; }
	 public void setFile_name(String file_name){ this.file_name=file_name; }
	 public String getPost_addr(){ return this.post_addr; }
	 public void setPost_addr(String post_addr){ this.post_addr=post_addr; }
	 public String getCopy_addr(){ return this.copy_addr; }
	 public void setCopy_addr(String copy_addr){ this.copy_addr=copy_addr; }
	 public String getBeaccount_addr(){ return this.beaccount_addr; }
	 public void setBeaccount_addr(String beaccount_addr){ this.beaccount_addr=beaccount_addr; }
	 public String getDoc_no(){ return this.doc_no; }
	 public void setDoc_no(String doc_no){ this.doc_no=doc_no; }
	 public String getDoc_type_name(){ return this.doc_type_name; }
	 public void setDoc_type_name(String doc_type_name){ this.doc_type_name=doc_type_name; }
	 public String getApply_type_name(){ return this.apply_type_name; }
	 public void setApply_type_name(String apply_type_name){ this.apply_type_name=apply_type_name; }
	 public String getDoc_template(){ return this.doc_template; }
	 public void setDoc_template(String doc_template){ this.doc_template=doc_template; }
	 public String getTitle(){ return this.title; }
	 public void setTitle(String title){ this.title=title; }
	 public String getCreate_date(){ return this.create_date; }
	 public void setCreate_date(String create_date){ this.create_date=create_date; }
	 public String getHq_names(){ return this.hq_names; }
	 public void setHq_names(String hq_names){ this.hq_names=hq_names; }
	 public String getHq_phones(){ return this.hq_phones; }
	 public void setHq_phones(String hq_phones){ this.hq_phones=hq_phones; }
	 public String getHas_hq_names(){ return this.has_hq_names; }
	 public void setHas_hq_names(String has_hq_names){ this.has_hq_names=has_hq_names; }
	 public String getQf_names(){ return this.qf_names; }
	 public void setQf_names(String qf_names){ this.qf_names=qf_names; }
	 public String getQf_phones(){ return this.qf_phones; }
	 public void setQf_phones(String qf_phones){ this.qf_phones=qf_phones; }
	 public Integer getQf_state(){ return this.qf_state; }
	 public void setQf_state(Integer qf_state){ this.qf_state=qf_state; }
	 public String getHy_names(){ return this.hy_names; }
	 public void setHy_names(String hy_names){ this.hy_names=hy_names; }
	 public String getHy_phones(){ return this.hy_phones; }
	 public void setHy_phones(String hy_phones){ this.hy_phones=hy_phones; }
	 public Integer getHy_state(){ return this.hy_state; }
	 public void setHy_state(Integer hy_state){ this.hy_state=hy_state; }
	 public String getHy_date(){ return this.hy_date; }
	 public void setHy_date(String hy_date){ this.hy_date=hy_date; }
	 public String getQf_date(){ return this.qf_date; }
	 public void setQf_date(String qf_date){ this.qf_date=qf_date; }
	 public String getTimeout_date(){ return this.timeout_date; }
	 public void setTimeout_date(String timeout_date){ this.timeout_date=timeout_date; }
	 public String getCreater_name(){ return this.creater_name; }
	 public void setCreater_name(String creater_name){ this.creater_name=creater_name; }
	 public String getDep_id(){ return this.dep_id; }
	 public void setDep_id(String dep_id){ this.dep_id=dep_id; }
	 public String getDep_name(){ return this.dep_name; }
	 public void setDep_name(String dep_name){ this.dep_name=dep_name; }
	 public Integer getAttach(){ return this.attach; }
	 public void setAttach(Integer attach){ this.attach=attach; }
	 public String getArea_name(){ return this.area_name; }
	 public void setArea_name(String area_name){ this.area_name=area_name; }
	 public String getArea_id(){ return this.area_id; }
	 public void setArea_id(String area_id){ this.area_id=area_id; }
	 public String getFile_counts(){ return this.file_counts; }
	 public void setFile_counts(String file_counts){ this.file_counts=file_counts; }
	 public String getHandler_names(){ return this.handler_names; }
	 public void setHandler_names(String handler_names){ this.handler_names=handler_names; }
	 public String getHq_info(){ return this.hq_info; }
	 public void setHq_info(String hq_info){ this.hq_info=hq_info; }
	 public String getQf_info(){ return this.qf_info; }
	 public void setQf_info(String qf_info){ this.qf_info=qf_info; }
	 public Integer getISQF(){ return this.ISQF; }
	 public void setISQF(Integer ISQF){ this.ISQF=ISQF; }
	 public String getHy_info(){ return this.hy_info; }
	 public void setHy_info(String hy_info){ this.hy_info=hy_info; }
	 public Integer getIsHY(){ return this.IsHY; }
	 public void setIsHY(Integer IsHY){ this.IsHY=IsHY; }
	 public String getSHTime(){ return this.SHTime; }
	 public void setSHTime(String SHTime){ this.SHTime=SHTime; }
}

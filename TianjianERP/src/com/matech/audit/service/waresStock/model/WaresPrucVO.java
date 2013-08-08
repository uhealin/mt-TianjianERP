package com.matech.audit.service.waresStock.model;

import com.matech.framework.pub.db.Table;

@Table(name="k_wares_purchasing",pk="uuid")
public class WaresPrucVO {
	
	 protected String uuid ;
	 protected String ware_code ;
	 protected String ware_id ;
	 protected String ware_name ;
	 protected String ware_type ;
	 protected Integer expect_quantity ;
	 protected Double expect_amount ;
	 protected Integer real_quantity ;
	 protected String applyer_id ;
	 protected String applyer_name ;
	 protected String check_state ;
	 protected String checker_id ;
	 protected String checker_name ;
	 protected String eff_remark ;
	 protected String seq_no ;
	 protected String a_time ;
	 protected String state ;


	 public String getUuid(){ return this.uuid; }
	 public void setUuid(String uuid){ this.uuid=uuid; }
	 public String getWare_code(){ return this.ware_code; }
	 public void setWare_code(String ware_code){ this.ware_code=ware_code; }
	 public String getWare_id(){ return this.ware_id; }
	 public void setWare_id(String ware_id){ this.ware_id=ware_id; }
	 public String getWare_name(){ return this.ware_name; }
	 public void setWare_name(String ware_name){ this.ware_name=ware_name; }
	 public String getWare_type(){ return this.ware_type; }
	 public void setWare_type(String ware_type){ this.ware_type=ware_type; }
	 public Integer getExpect_quantity(){ return this.expect_quantity; }
	 public void setExpect_quantity(Integer expect_quantity){ this.expect_quantity=expect_quantity; }
	 public Double getExpect_amount(){ return this.expect_amount; }
	 public void setExpect_amount(Double expect_amount){ this.expect_amount=expect_amount; }
	 public Integer getReal_quantity(){ return this.real_quantity; }
	 public void setReal_quantity(Integer real_quantity){ this.real_quantity=real_quantity; }
	 public String getApplyer_id(){ return this.applyer_id; }
	 public void setApplyer_id(String applyer_id){ this.applyer_id=applyer_id; }
	 public String getApplyer_name(){ return this.applyer_name; }
	 public void setApplyer_name(String applyer_name){ this.applyer_name=applyer_name; }
	 public String getCheck_state(){ return this.check_state; }
	 public void setCheck_state(String check_state){ this.check_state=check_state; }
	 public String getChecker_id(){ return this.checker_id; }
	 public void setChecker_id(String checker_id){ this.checker_id=checker_id; }
	 public String getChecker_name(){ return this.checker_name; }
	 public void setChecker_name(String checker_name){ this.checker_name=checker_name; }
	 public String getEff_remark(){ return this.eff_remark; }
	 public void setEff_remark(String eff_remark){ this.eff_remark=eff_remark; }
	 public String getSeq_no(){ return this.seq_no; }
	 public void setSeq_no(String seq_no){ this.seq_no=seq_no; }
	 public String getA_time(){ return this.a_time; }
	 public void setA_time(String a_time){ this.a_time=a_time; }
	 public String getState(){ return this.state; }
	 public void setState(String state){ this.state=state; }

}

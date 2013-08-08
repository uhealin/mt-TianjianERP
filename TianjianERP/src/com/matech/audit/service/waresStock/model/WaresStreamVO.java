package com.matech.audit.service.waresStock.model;

import com.matech.framework.pub.db.Table;

@Table(name="k_waresstream",pk="uuid")
public class WaresStreamVO {
	
		 protected String uuid ;
		 protected String waresStockId ;
		 protected String userId ;
		 protected String quantity ;
		 protected String applyDate ;
		 protected String applyReason ;
		 protected String approveUserId ;
		 protected String approveDate ;
		 protected String approveQuantity ;
		 protected String approveIdea ;
		 protected String status ;
		 protected String pro_type ;
		 protected String return_state ;
		 protected String return_real_time ;
		 protected String return_expect_time ;
		 protected String project_name ;
		 protected String project_owner_name ;
		 protected String check_state ;
		 protected String ware_name ;
		 protected String receive_state ;
		 protected String receive_time ;
		 protected String project_id ;
		 protected String project_owner_id ;
		 protected String dep_name ;
		 protected String user_name ;
		 protected Integer dep_id ;
		 protected String ware_code ;
		 protected String mainformid ;
		 protected String user_type ;
		 protected String remark ;


		 public String getUuid(){ return this.uuid; }
		 public void setUuid(String uuid){ this.uuid=uuid; }
		 public String getWaresStockId(){ return this.waresStockId; }
		 public void setWaresStockId(String waresStockId){ this.waresStockId=waresStockId; }
		 public String getUserId(){ return this.userId; }
		 public void setUserId(String userId){ this.userId=userId; }
		 public String getQuantity(){ return this.quantity; }
		 public void setQuantity(String quantity){ this.quantity=quantity; }
		 public String getApplyDate(){ return this.applyDate; }
		 public void setApplyDate(String applyDate){ this.applyDate=applyDate; }
		 public String getApplyReason(){ return this.applyReason; }
		 public void setApplyReason(String applyReason){ this.applyReason=applyReason; }
		 public String getApproveUserId(){ return this.approveUserId; }
		 public void setApproveUserId(String approveUserId){ this.approveUserId=approveUserId; }
		 public String getApproveDate(){ return this.approveDate; }
		 public void setApproveDate(String approveDate){ this.approveDate=approveDate; }
		 public String getApproveQuantity(){ return this.approveQuantity; }
		 public void setApproveQuantity(String approveQuantity){ this.approveQuantity=approveQuantity; }
		 public String getApproveIdea(){ return this.approveIdea; }
		 public void setApproveIdea(String approveIdea){ this.approveIdea=approveIdea; }
		 public String getStatus(){ return this.status; }
		 public void setStatus(String status){ this.status=status; }
		 public String getPro_type(){ return this.pro_type; }
		 public void setPro_type(String pro_type){ this.pro_type=pro_type; }
		 public String getReturn_state(){ return this.return_state; }
		 public void setReturn_state(String return_state){ this.return_state=return_state; }
		 public String getReturn_real_time(){ return this.return_real_time; }
		 public void setReturn_real_time(String return_real_time){ this.return_real_time=return_real_time; }
		 public String getReturn_expect_time(){ return this.return_expect_time; }
		 public void setReturn_expect_time(String return_expect_time){ this.return_expect_time=return_expect_time; }
		 public String getProject_name(){ return this.project_name; }
		 public void setProject_name(String project_name){ this.project_name=project_name; }
		 public String getProject_owner_name(){ return this.project_owner_name; }
		 public void setProject_owner_name(String project_owner_name){ this.project_owner_name=project_owner_name; }
		 public String getCheck_state(){ return this.check_state; }
		 public void setCheck_state(String check_state){ this.check_state=check_state; }
		 public String getWare_name(){ return this.ware_name; }
		 public void setWare_name(String ware_name){ this.ware_name=ware_name; }
		 public String getReceive_state(){ return this.receive_state; }
		 public void setReceive_state(String receive_state){ this.receive_state=receive_state; }
		 public String getReceive_time(){ return this.receive_time; }
		 public void setReceive_time(String receive_time){ this.receive_time=receive_time; }
		 public String getProject_id(){ return this.project_id; }
		 public void setProject_id(String project_id){ this.project_id=project_id; }
		 public String getProject_owner_id(){ return this.project_owner_id; }
		 public void setProject_owner_id(String project_owner_id){ this.project_owner_id=project_owner_id; }
		 public String getDep_name(){ return this.dep_name; }
		 public void setDep_name(String dep_name){ this.dep_name=dep_name; }
		 public String getUser_name(){ return this.user_name; }
		 public void setUser_name(String user_name){ this.user_name=user_name; }
		 public Integer getDep_id(){ return this.dep_id; }
		 public void setDep_id(Integer dep_id){ this.dep_id=dep_id; }
		 public String getWare_code(){ return this.ware_code; }
		 public void setWare_code(String ware_code){ this.ware_code=ware_code; }
		 public String getMainformid(){ return this.mainformid; }
		 public void setMainformid(String mainformid){ this.mainformid=mainformid; }
		 public String getUser_type(){ return this.user_type; }
		 public void setUser_type(String user_type){ this.user_type=user_type; }
		 public String getRemark(){ return this.remark; }
		 public void setRemark(String remark){ this.remark=remark; }


}

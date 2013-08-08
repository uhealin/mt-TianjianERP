package com.matech.audit.service.waresStock.model;

import com.matech.framework.pub.db.Table;

@Table(name="k_waresstockdetails",pk="uuid")
public class WarestockdetailsVO {
	
	 protected String uuid ;
	 protected String waresStockId ;
	 protected String userId ;
	 protected String date ;
	 protected String ctype ;
	 protected String quantity ;
	 protected String price ;
	 protected String suppliers ;
	 protected String status ;
	 protected String check_state ;
	 protected String grant_state ;
	 protected String check_time ;
	 protected String grant_time ;
	 protected String receiver_id ;
	 protected String receiver_name ;
	 protected String checker_name ;
	 protected String checker_id ;
	 protected String remark ;
	 protected String scrap_time ;
	 protected String scrap_msg ;


	 public String getUuid(){ return this.uuid; }
	 public void setUuid(String uuid){ this.uuid=uuid; }
	 public String getWaresStockId(){ return this.waresStockId; }
	 public void setWaresStockId(String waresStockId){ this.waresStockId=waresStockId; }
	 public String getUserId(){ return this.userId; }
	 public void setUserId(String userId){ this.userId=userId; }
	 public String getDate(){ return this.date; }
	 public void setDate(String date){ this.date=date; }
	 public String getCtype(){ return this.ctype; }
	 public void setCtype(String ctype){ this.ctype=ctype; }
	 public String getQuantity(){ return this.quantity; }
	 public void setQuantity(String quantity){ this.quantity=quantity; }
	 public String getPrice(){ return this.price; }
	 public void setPrice(String price){ this.price=price; }
	 public String getSuppliers(){ return this.suppliers; }
	 public void setSuppliers(String suppliers){ this.suppliers=suppliers; }
	 public String getStatus(){ return this.status; }
	 public void setStatus(String status){ this.status=status; }
	 public String getCheck_state(){ return this.check_state; }
	 public void setCheck_state(String check_state){ this.check_state=check_state; }
	 public String getGrant_state(){ return this.grant_state; }
	 public void setGrant_state(String grant_state){ this.grant_state=grant_state; }
	 public String getCheck_time(){ return this.check_time; }
	 public void setCheck_time(String check_time){ this.check_time=check_time; }
	 public String getGrant_time(){ return this.grant_time; }
	 public void setGrant_time(String grant_time){ this.grant_time=grant_time; }
	 public String getReceiver_id(){ return this.receiver_id; }
	 public void setReceiver_id(String receiver_id){ this.receiver_id=receiver_id; }
	 public String getReceiver_name(){ return this.receiver_name; }
	 public void setReceiver_name(String receiver_name){ this.receiver_name=receiver_name; }
	 public String getChecker_name(){ return this.checker_name; }
	 public void setChecker_name(String checker_name){ this.checker_name=checker_name; }
	 public String getChecker_id(){ return this.checker_id; }
	 public void setChecker_id(String checker_id){ this.checker_id=checker_id; }
	 public String getRemark(){ return this.remark; }
	 public void setRemark(String remark){ this.remark=remark; }
	 public String getScrap_time(){ return this.scrap_time; }
	 public void setScrap_time(String scrap_time){ this.scrap_time=scrap_time; }
	 public String getScrap_msg(){ return this.scrap_msg; }
	 public void setScrap_msg(String scrap_msg){ this.scrap_msg=scrap_msg; }


}

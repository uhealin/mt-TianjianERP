package com.matech.audit.service.waresStock.model;

import com.matech.framework.pub.db.Table;

@Table(name="k_waresstock",pk="uuid")
public class WaresStockVO {
	
	
		 protected String uuid ;
		 protected String name ;
		 protected String remark ;
		 protected String type ;
		 protected String coding ;
		 protected String unitUnit ;
		 protected String lowestStock ;
		 protected String lowestWarnStock ;
		 protected String highestWarnStock ;
		 protected String usableStock ;
		 protected String scrappedStock ;
		 protected String departmentId ;
		 protected String photo ;
		 protected String photoTemp ;
		 protected String pro_type ;
		 protected String need_check_ind ;
		 protected Double unit_price ;
		 protected String local_code ;
		 protected String local_address ;
		 protected String putin_time ;


		 public String getUuid(){ return this.uuid; }
		 public void setUuid(String uuid){ this.uuid=uuid; }
		 public String getName(){ return this.name; }
		 public void setName(String name){ this.name=name; }
		 public String getRemark(){ return this.remark; }
		 public void setRemark(String remark){ this.remark=remark; }
		 public String getType(){ return this.type; }
		 public void setType(String type){ this.type=type; }
		 public String getCoding(){ return this.coding; }
		 public void setCoding(String coding){ this.coding=coding; }
		 public String getUnitUnit(){ return this.unitUnit; }
		 public void setUnitUnit(String unitUnit){ this.unitUnit=unitUnit; }
		 public String getLowestStock(){ return this.lowestStock; }
		 public void setLowestStock(String lowestStock){ this.lowestStock=lowestStock; }
		 public String getLowestWarnStock(){ return this.lowestWarnStock; }
		 public void setLowestWarnStock(String lowestWarnStock){ this.lowestWarnStock=lowestWarnStock; }
		 public String getHighestWarnStock(){ return this.highestWarnStock; }
		 public void setHighestWarnStock(String highestWarnStock){ this.highestWarnStock=highestWarnStock; }
		 public String getUsableStock(){ return this.usableStock; }
		 public void setUsableStock(String usableStock){ this.usableStock=usableStock; }
		 public String getScrappedStock(){ return this.scrappedStock; }
		 public void setScrappedStock(String scrappedStock){ this.scrappedStock=scrappedStock; }
		 public String getDepartmentId(){ return this.departmentId; }
		 public void setDepartmentId(String departmentId){ this.departmentId=departmentId; }
		 public String getPhoto(){ return this.photo; }
		 public void setPhoto(String photo){ this.photo=photo; }
		 public String getPhotoTemp(){ return this.photoTemp; }
		 public void setPhotoTemp(String photoTemp){ this.photoTemp=photoTemp; }
		 public String getPro_type(){ return this.pro_type; }
		 public void setPro_type(String pro_type){ this.pro_type=pro_type; }
		 public String getNeed_check_ind(){ return this.need_check_ind; }
		 public void setNeed_check_ind(String need_check_ind){ this.need_check_ind=need_check_ind; }
		 public Double getUnit_price(){ return this.unit_price; }
		 public void setUnit_price(Double unit_price){ this.unit_price=unit_price; }
		 public String getLocal_code(){ return this.local_code; }
		 public void setLocal_code(String local_code){ this.local_code=local_code; }
		 public String getLocal_address(){ return this.local_address; }
		 public void setLocal_address(String local_address){ this.local_address=local_address; }
		 public String getPutin_time(){ return this.putin_time; }
		 public void setPutin_time(String putin_time){ this.putin_time=putin_time; }


}

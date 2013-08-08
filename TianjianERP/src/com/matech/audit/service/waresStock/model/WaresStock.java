package com.matech.audit.service.waresStock.model;

import com.matech.framework.pub.db.Table;

/**
 * @author YMM
 * 物品库存
 *
 */

@Table(name="k_waresstock",pk="uuid")
public class WaresStock {
	
	
	private String uuid ;    //;    //varchar(50) NOT NULLuuid
	private String name ;    //;    //varchar(50) NULL物品名称
	private String remark;// text NULL描述
	private String type ;    //varchar(30) NULL类别
	private String coding ;    //;    //varchar(50) NULL编码
	private String unitUnit ;    //varchar(30) NULL计量单位
	private String lowestStock ;    //varchar(20) NULL最低库存
	private String lowestWarnStock ;    //varchar(20) NULL最低警告库存
	private String highestWarnStock ;    //varchar(20) NULL最搞警告库存
	private String usableStock ;    //varchar(20) NULL当前可用的库存
	private String scrappedStock ;    //varchar(20) NULL当前报废的库存
	private String departmentId ;    //varchar(30) NULL所属部门
	private String photo;
	private String photoTemp;
	 protected String pro_type ;
	 protected Double unit_price ;
	 protected String local_code ;
	 protected String local_address ;
	 protected String putin_time ;
	 protected String need_check_ind ;
	
	public String getPhoto() {
		return photo;
	}
	public void setPhoto(String photo) {
		this.photo = photo;
	}
	public String getPhotoTemp() {
		return photoTemp;
	}
	public void setPhotoTemp(String photoTemp) {
		this.photoTemp = photoTemp;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getCoding() {
		return coding;
	}
	public void setCoding(String coding) {
		this.coding = coding;
	}
	public String getUnitUnit() {
		return unitUnit;
	}
	public void setUnitUnit(String unitUnit) {
		this.unitUnit = unitUnit;
	}
	public String getLowestStock() {
		return lowestStock;
	}
	public void setLowestStock(String lowestStock) {
		this.lowestStock = lowestStock;
	}
	public String getLowestWarnStock() {
		return lowestWarnStock;
	}
	public void setLowestWarnStock(String lowestWarnStock) {
		this.lowestWarnStock = lowestWarnStock;
	}
	public String getHighestWarnStock() {
		return highestWarnStock;
	}
	public void setHighestWarnStock(String highestWarnStock) {
		this.highestWarnStock = highestWarnStock;
	}
	public String getUsableStock() {
		return usableStock;
	}
	public void setUsableStock(String usableStock) {
		this.usableStock = usableStock;
	}
	public String getScrappedStock() {
		return scrappedStock;
	}
	public void setScrappedStock(String scrappedStock) {
		this.scrappedStock = scrappedStock;
	}
	public String getDepartmentId() {
		return departmentId;
	}
	public void setDepartmentId(String departmentId) {
		this.departmentId = departmentId;
	}

	public String getPro_type() {
		return pro_type;
	}
	public void setPro_type(String pro_type) {
		this.pro_type = pro_type;
	}
	public Double getUnit_price() {
		return unit_price;
	}
	public void setUnit_price(Double price) {
		this.unit_price = price;
	}
	
	public String getPutin_time() {
		return putin_time;
	}
	public void setPutin_time(String putin_time) {
		this.putin_time = putin_time;
	}
	public String getNeed_check_ind() {
		return need_check_ind;
	}
	public void setNeed_check_ind(String neek_check_ind) {
		this.need_check_ind = neek_check_ind;
	}
	public String getLocal_code() {
		return local_code;
	}
	public void setLocal_code(String local_code) {
		this.local_code = local_code;
	}
	public String getLocal_address() {
		return local_address;
	}
	public void setLocal_address(String local_address) {
		this.local_address = local_address;
	}

	
}

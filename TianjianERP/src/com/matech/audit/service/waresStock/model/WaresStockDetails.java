package com.matech.audit.service.waresStock.model;

import com.matech.framework.pub.db.Table;

/**
 * @author YMM
 * 物品库存流水对象
 *
 */
@Table(name="k_waresstockdetails",pk="uuid")
public class WaresStockDetails {

	private String uuid; // int(11) NOT NULLautoId
	private String waresStockId ;  //(50) NULL物品库存ID
	private String userId ;  //(20) NULL操作人
	private String date ;  //(30) NULL操作日期
	private String ctype ;  //(20) NULL单据类型：（入库、归还、报废、）
	private String quantity ;  //(20) NULL数量
	private String price ;  //(15) NULL单价
	private String suppliers ;  //(50) NULL供应商
	private String status ;  //状态
	 protected String check_state ;
	 protected String grant_state ;
	 protected String check_time ;
	 protected String grant_time ;
	 protected String receiver_id ;
	 protected String checker_id ;
	 protected String receiver_name ;
	 protected String checker_name ;
	 protected String remark ;
	protected String scrap_time;
	protected String scrap_msg;
	 
	public String getGrant_state() {
		return grant_state;
	}
	public void setGrant_state(String grant_state) {
		this.grant_state = grant_state;
	}
	public String getCheck_time() {
		return check_time;
	}
	public void setCheck_time(String check_time) {
		this.check_time = check_time;
	}
	public String getGrant_time() {
		return grant_time;
	}
	public void setGrant_time(String grant_time) {
		this.grant_time = grant_time;
	}
	public String getReceiver_id() {
		return receiver_id;
	}
	public void setReceiver_id(String receiver_id) {
		this.receiver_id = receiver_id;
	}
	public String getChecker_id() {
		return checker_id;
	}
	public void setChecker_id(String checker_id) {
		this.checker_id = checker_id;
	}
	public String getReceiver_name() {
		return receiver_name;
	}
	public void setReceiver_name(String receiver_name) {
		this.receiver_name = receiver_name;
	}
	public String getChecker_name() {
		return checker_name;
	}
	public void setChecker_name(String check_name) {
		this.checker_name = check_name;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getCheck_state() {
		return check_state;
	}
	public void setCheck_state(String check_state) {
		this.check_state = check_state;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getWaresStockId() {
		return waresStockId;
	}
	public void setWaresStockId(String waresStockId) {
		this.waresStockId = waresStockId;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getCtype() {
		return ctype;
	}
	public void setCtype(String ctype) {
		this.ctype = ctype;
	}
	public String getQuantity() {
		return quantity;
	}
	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}
	public String getSuppliers() {
		return suppliers;
	}
	public void setSuppliers(String suppliers) {
		this.suppliers = suppliers;
	}
	public String getScrap_time() {
		return scrap_time;
	}
	public void setScrap_time(String scrap_time) {
		this.scrap_time = scrap_time;
	}
	public String getScrap_msg() {
		return scrap_msg;
	}
	public void setScrap_msg(String scrap_msg) {
		this.scrap_msg = scrap_msg;
	}
	
	
}

package com.matech.audit.service.waresStock.model;

import com.matech.framework.pub.db.Table;

/**
 * @author YMM
 * 物品进出流水表
 *
 */
@Table(name="k_waresstream",pk="uuid")
public class WaresStream {
	private String uuid;  //varchar(50) NOT NULLuuid
	private String waresStockId;  //varchar(30) NULL物品名称
	private String userId;  //varchar(20) NULL申请人
	private String quantity;  //varchar(20) NULL申请数量
	private String applyDate;  //varchar(30) NULL申请日期
	private String applyReason;  //mediumtext NULL申请理由
	private String approveUserId;  //varchar(20) NULL审批人
	private String approveDate;  //varchar(30) NULL审批时间
	private String approveQuantity;  //varchar(20) NULL审批数量
	private String approveIdea;  //mediumtext NULL审批意见
	private String status;  //varchar(20) NULL状态()
	 protected String pro_type ;
	 protected String return_state ;
	 protected String return_real_time ;
	 protected String return_expect_time ;
	 protected String project_name ;
	 protected String project_ower_name ;
	 protected String check_state ;
	 protected String ware_name;
	 protected String receive_state;
	 public String getReceive_state() {
		return receive_state;
	}
	public void setReceive_state(String receive_state) {
		this.receive_state = receive_state;
	}
	public String getReceive_time() {
		return receive_time;
	}
	public void setReceive_time(String receive_time) {
		this.receive_time = receive_time;
	}
	protected String receive_time;
	 
	public String getWare_name() {
		return ware_name;
	}
	public void setWare_name(String ware_name) {
		this.ware_name = ware_name;
	}
	public String getWaresStockId() {
		return waresStockId;
	}
	public void setWaresStockId(String waresStockId) {
		this.waresStockId = waresStockId;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getQuantity() {
		return quantity;
	}
	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}
	public String getApplyDate() {
		return applyDate;
	}
	public void setApplyDate(String applyDate) {
		this.applyDate = applyDate;
	}
	public String getApplyReason() {
		return applyReason;
	}
	public void setApplyReason(String applyReason) {
		this.applyReason = applyReason;
	}
	public String getApproveUserId() {
		return approveUserId;
	}
	public void setApproveUserId(String approveUserId) {
		this.approveUserId = approveUserId;
	}
	public String getApproveDate() {
		return approveDate;
	}
	public void setApproveDate(String approveDate) {
		this.approveDate = approveDate;
	}
	public String getApproveQuantity() {
		return approveQuantity;
	}
	public void setApproveQuantity(String approveQuantity) {
		this.approveQuantity = approveQuantity;
	}
	public String getApproveIdea() {
		return approveIdea;
	}
	public void setApproveIdea(String approveIdea) {
		this.approveIdea = approveIdea;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getPro_type() {
		return pro_type;
	}
	public void setPro_type(String pro_type) {
		this.pro_type = pro_type;
	}
	public String getReturn_state() {
		return return_state;
	}
	public void setReturn_state(String return_state) {
		this.return_state = return_state;
	}
	public String getReturn_real_time() {
		return return_real_time;
	}
	public void setReturn_real_time(String return_real_time) {
		this.return_real_time = return_real_time;
	}
	public String getReturn_expect_time() {
		return return_expect_time;
	}
	public void setReturn_expect_time(String return_expect_time) {
		this.return_expect_time = return_expect_time;
	}
	public String getProject_name() {
		return project_name;
	}
	public void setProject_name(String project_name) {
		this.project_name = project_name;
	}
	public String getProject_ower_name() {
		return project_ower_name;
	}
	public void setProject_ower_name(String project_ower_name) {
		this.project_ower_name = project_ower_name;
	}
	public String getCheck_state() {
		return check_state;
	}
	public void setCheck_state(String check_state) {
		this.check_state = check_state;
	}
}

package com.matech.audit.service.inbox.model;

/**
 * @author YMM
 * 发件
 *
 */
public class Inbox {
		
		private String  uuid;                 // 'uuid',
		private String  packageName;          // '包裹名称',
		private String  packageCode;          // '包裹条形码',
		private String  logisticsCompany ;      // '物流公司',
		private String  floorNumber;         // '送到楼层',
		private String  inboxUserId;         // '收件人',
		private String  arrivalDate;        // '送达时间',
		private String  remindMode;         // '提醒方式',
		private String  receiveUserId;      // '领取人',
		private String  receiveDate;       // '领取时间',
		private String  status;            // '状态(待领取|已领取)',
		private String  mphoneRemind; //手机短信提醒
		private String addUserId;		//增加记录人的id
		
		
		public String getAddUserId() {
			return addUserId;
		}
		public void setAddUserId(String addUserId) {
			this.addUserId = addUserId;
		}
		public String getMphoneRemind() {
			return mphoneRemind;
		}
		public void setMphoneRemind(String mphoneRemind) {
			this.mphoneRemind = mphoneRemind;
		}
		public String getUuid() {
			return uuid;
		}
		public void setUuid(String uuid) {
			this.uuid = uuid;
		}
		public String getPackageName() {
			return packageName;
		}
		public void setPackageName(String packageName) {
			this.packageName = packageName;
		}
		public String getPackageCode() {
			return packageCode;
		}
		public void setPackageCode(String packageCode) {
			this.packageCode = packageCode;
		}
		public String getLogisticsCompany() {
			return logisticsCompany;
		}
		public void setLogisticsCompany(String logisticsCompany) {
			this.logisticsCompany = logisticsCompany;
		}
		public String getFloorNumber() {
			return floorNumber;
		}
		public void setFloorNumber(String floorNumber) {
			this.floorNumber = floorNumber;
		}
		public String getInboxUserId() {
			return inboxUserId;
		}
		public void setInboxUserId(String inboxUserId) {
			this.inboxUserId = inboxUserId;
		}
		public String getArrivalDate() {
			return arrivalDate;
		}
		public void setArrivalDate(String arrivalDate) {
			this.arrivalDate = arrivalDate;
		}
		public String getRemindMode() {
			return remindMode;
		}
		public void setRemindMode(String remindMode) {
			this.remindMode = remindMode;
		}
		public String getReceiveUserId() {
			return receiveUserId;
		}
		public void setReceiveUserId(String receiveUserId) {
			this.receiveUserId = receiveUserId;
		}
		public String getReceiveDate() {
			return receiveDate;
		}
		public void setReceiveDate(String receiveDate) {
			this.receiveDate = receiveDate;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
}

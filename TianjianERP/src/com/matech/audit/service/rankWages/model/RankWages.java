package com.matech.audit.service.rankWages.model;

/**
 * @author YMM
 *工资组成
 */
public class RankWages {
	private String uuid ; //; //VARCHAR(100) NOT NULL,
	private String interiorId ; //; //VARCHAR(30) DEFAULT NULL COMMENT '内部编号',
	private String rankId ; //; //VARCHAR(100) DEFAULT NULL COMMENT '对应职级类型',
	private String wagesName ; //; //VARCHAR(150) DEFAULT NULL COMMENT '工资项名称',
	private String getValue;// TEXT COMMENT '取值',
	private String updateTache;  //; //VARCHAR(100) DEFAULT NULL COMMENT '有权修改的环节',
	private String orderId ; //; //VARCHAR(20) DEFAULT NULL COMMENT '排序编号',
	private String remark;  // TEXT COMMENT '备注',
	private String propenty ; //; //VARCHAR(200) DEFAULT NULL COMMENT '工资项拼音简写',
	private String valueType;//VARCHAR(100) DEFAULT NULL COMMENT '取值类型'
	private String groupFlag = "";
 
	 
	public String getGroupFlag() {
		return groupFlag;
	}
	public void setGroupFlag(String groupFlag) {
		this.groupFlag = groupFlag;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getInteriorId() {
		return interiorId;
	}
	public void setInteriorId(String interiorId) {
		this.interiorId = interiorId;
	}
	public String getRankId() {
		return rankId;
	}
	public void setRankId(String rankId) {
		this.rankId = rankId;
	}
	public String getWagesName() {
		return wagesName;
	}
	public void setWagesName(String wagesName) {
		this.wagesName = wagesName;
	}
	public String getGetValue() {
		return getValue;
	}
	public void setGetValue(String getValue) {
		this.getValue = getValue;
	}
	public String getUpdateTache() {
		return updateTache;
	}
	public void setUpdateTache(String updateTache) {
		this.updateTache = updateTache;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getPropenty() {
		return propenty;
	}
	public void setPropenty(String propenty) {
		this.propenty = propenty;
	}
	public String getValueType() {
		return valueType;
	}
	public void setValueType(String valueType) {
		this.valueType = valueType;
	}
	  
}

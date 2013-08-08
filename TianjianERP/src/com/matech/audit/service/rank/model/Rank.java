package com.matech.audit.service.rank.model;

/**
 * @author YMM
 *职级
 */
public class Rank {
	private String autoId ;  //  VARCHAR; (100) NOT NULL COMMENT 'uuid',
	private String name ;  //  VARCHAR; (50) DEFAULT NULL COMMENT '职级名称',
	private String ctype ;  //  VARCHAR; (20) DEFAULT NULL COMMENT '类型(试用职级、正式职级)',
	private String sequenceNumber ;  //  VARCHAR; (20) DEFAULT NULL COMMENT '权限号、排序号',
	private String group ;  //  VARCHAR; (25) DEFAULT NULL COMMENT '组',
	private String explain;  // TEXT COMMENT '职级说明',
	private String propenty ;  //  VARCHAR; (200) DEFAULT NULL COMMENT '备用',
	private String baseSalary; // 基本工资
	private String timeSalary; // 用时工资
	
	 
	public String getBaseSalary() {
		return baseSalary;
	}
	public void setBaseSalary(String baseSalary) {
		this.baseSalary = baseSalary;
	}
	public String getTimeSalary() {
		return timeSalary;
	}
	public void setTimeSalary(String timeSalary) {
		this.timeSalary = timeSalary;
	}
	public String getAutoId() {
		return autoId;
	}
	public void setAutoId(String autoId) {
		this.autoId = autoId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCtype() {
		return ctype;
	}
	public void setCtype(String ctype) {
		this.ctype = ctype;
	}
	public String getSequenceNumber() {
		return sequenceNumber;
	}
	public void setSequenceNumber(String sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}
	public String getGroup() {
		return group;
	}
	public void setGroup(String group) {
		this.group = group;
	}
	public String getExplain() {
		return explain;
	}
	public void setExplain(String explain) {
		this.explain = explain;
	}
	public String getPropenty() {
		return propenty;
	}
	public void setPropenty(String propenty) {
		this.propenty = propenty;
	}
	  
	  
}

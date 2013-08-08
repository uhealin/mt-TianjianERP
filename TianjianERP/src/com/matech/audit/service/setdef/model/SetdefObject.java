package com.matech.audit.service.setdef.model;


/**
 * 
 * <p>Title: TODO</p>
 * <p>Description: TODO</p>
 * <p>Copyright: Copyright (c) 2006, 2008 MaTech Corporation.All rights reserved. </p>
 * <p>Company: Matech  广州铭太信息科技有限公司</p>
 * 
 * 本程序及其相关的所有资源均为铭太科技公司研发中心审计开发组所有，
 * 版本发行及解释权归研发中心，公司网站为：http://www.matech.cn
 *
 * 贡献者团队:铭太科技 - 研发中心，审计开发组
 *          
 * @author Phoenix
 * 2007-8-31
 */

public class SetdefObject {
	private String autoid;  //自定义编号
	private String defName; //自定义字段名称
	private String defValue;//自定义字段值
	private String defType; //自定义所属类别
	private String dicType;
	public String getDicType() {
		return dicType;
	}
	public void setDicType(String dicType) {
		this.dicType = dicType;
	}
	public String getAutoid() {
		return autoid;
	}
	public void setAutoid(String autoid) {
		this.autoid = autoid;
	}
	public String getDefName() {
		return defName;
	}
	public void setDefName(String defName) {
		this.defName = defName;
	}
	public String getDefValue() {
		return defValue;
	}
	public void setDefValue(String defValue) {
		this.defValue = defValue;
	}
	public String getDefType() {
		return defType;
	}
	public void setDefType(String defType) {
		this.defType = defType;
	}
		
}

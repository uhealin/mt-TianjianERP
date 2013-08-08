/*******************************************************************************
 * Copyright (c) 2006, 2008 MaTech Corporation.
 * All rights reserved.
 * 本程序及其相关的所有资源均为铭太科技公司研发中心审计开发组所有，
 * 版本发行及解释权归研发中心，公司网站为：
 * http://www.matech.cn
 *
 * 贡献者团队:
 *     铭太科技 - 研发中心，审计开发组
 *******************************************************************************/
package com.matech.audit.service.checkInfo;

import java.util.*;
/**
 * 类<code>CheckInfo</code>包含了打印控件的属性
 * <p>用于保存打印的属性</p>
 *
 * @see
 * @since 1.0
 */
public class PrintModel {
	
	
  /*
   * 指定数据连接。
   */	
  private String databaseDepartID="";	
  /**
   * 是否当前用户选择库。
   */
	
  private boolean isCurUseChoosing=true;	
	
  /**
   * 表头之上的标题
   * @since 1.0
   */
  private String[] strTitles;

  /**
   * 需要打印的内容的sql语句
   * @since 1.0
   */
  private String[] strQuerySqls;

  /**
   * 表头
   * @since 1.0
   */
  private String[] strChineseTitles;


  /**
   * 用于储存参数的(varMap)
   * @since 1.0
   */
  private Map varMap=new java.util.HashMap();

  
  /**
   * 字符列设置，格式：new String[]{"1`2`3","1`2`3","1`2`3"};
   * 功能：将某列转化成非数字列，如帐套、科目号列不用转化
   */
  private String[] charColumn;
  
  /**
   * 设置列宽度
   */
  private int[] iColumnWidths;

  
  private boolean vertical=true;
  /**
   * 指定底稿模型的名字
   * @since 1.0
   */
  private String excelTemplateFileName;
  public PrintModel() {
  }

  public void setVarMap(Map VarMap){
    this.varMap=VarMap;
  }

  public Map getVarMap(){
    return this.varMap;
  }

  public void setStrTitles(String[] strTitles) {
    this.strTitles = strTitles;
  }

  public void setStrQuerySqls(String[] strQuerySqls) {
    this.strQuerySqls = strQuerySqls;
  }

  public void setStrChineseTitles(String[] strChineseTitles) {
    this.strChineseTitles = strChineseTitles;
  }

  public void setExcelTemplateFileName(String excelTemplateFileName) {
    this.excelTemplateFileName = excelTemplateFileName;
  }

  public String[] getStrTitles() {
    return strTitles;
  }

  public String[] getStrQuerySqls() {
    return strQuerySqls;
  }

  public String[] getStrChineseTitles() {
    return strChineseTitles;
  }

  public String getExcelTemplateFileName() {
    return excelTemplateFileName;
  }
  /**
   * 为了方便本类的varMap成员调用put方法
   *
   * @param key Object
   * @param value Object
   */
  public void putVarMap(Object key,Object value){
    this.varMap.put(key,value);
  }

public String[] getCharColumn() {
	return charColumn;
}

public void setCharColumn(String[] charColumn) {
	this.charColumn = charColumn;
}

public int[] getIColumnWidths() {
	return iColumnWidths;
}

public void setIColumnWidths(int[] columnWidths) {
	iColumnWidths = columnWidths;
}

public boolean getVertical() {
	return vertical;
}

public void setVertical(boolean vertical) {
	this.vertical = vertical;
}

public boolean isCurUseChoosing() {
	return isCurUseChoosing;
}

public void setCurUseChoosing(boolean isCurUseChoosing) {
	this.isCurUseChoosing = isCurUseChoosing;
}

public String getDatabaseDepartID() {
	return databaseDepartID;
}

public void setDatabaseDepartID(String databaseDepartID) {
	this.databaseDepartID = databaseDepartID;
}
}

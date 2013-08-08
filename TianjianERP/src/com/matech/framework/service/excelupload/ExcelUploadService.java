package com.matech.framework.service.excelupload;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFName;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.exception.MatechException;
import com.matech.framework.pub.util.UTILString;
import com.sun.org.apache.xerces.internal.impl.dv.DVFactoryException;

/**
 * 
 * <p>
 * Title: TODO
 * </p>
 * <p>
 * Description: TODO
 * </p>
 * <p>
 * Copyright: Copyright (c) 2006, 2008 MaTech Corporation.All rights reserved.
 * </p>
 * <p>
 * Company: Matech 广州铭太信息科技有限公司
 * </p>
 * 
 * 本程序及其相关的所有资源均为铭太科技公司研发中心审计开发组所有， 版本发行及解释权归研发中心，公司网站为：http://www.matech.cn
 * 
 * <p>
 * 贡献者团队: 铭太科技 - 研发中心，审计开发组
 * 
 * @author LuckyStar 2007-6-28
 */
public class ExcelUploadService {

	private Connection conn = null;

	private String strFullName = "";

	private HSSFWorkbook workbook = null;

	private String STabNum;

	private String[] SExcelName;

	private String STabStr;
	
	private String STabDate;

	/**
	 * 
	 * @param conn
	 * @param strFullName
	 * @throws MatechException
	 */
	public ExcelUploadService(Connection conn, String strFullName) throws Exception {
		if (strFullName == null || strFullName.equals("")) {
			throw new MatechException("访问失败：请先设置filePath属性");
		}
		if (conn == null) {
			throw new MatechException("访问失败：数据库联结不能为空");
		}
		this.conn=conn;
		this.strFullName =strFullName;
	}

	public void setExcelNum(String num){
		this.STabNum = num;
	}
	public void setExcelString(String str){
		this.STabStr = str;
	}
	
	public void setExcelDate(String str){
		this.STabDate = str;
	}
		  
	/**
	 * 获得指定单元格的值，以字符串形式返回
	 * 
	 * @param cell
	 *            HSSFCell
	 * @return String
	 */
	private String getCellStrValue(HSSFCell cell) {
		if (cell == null) {
			return "";
		}
		try {
			// 按照字符串格式读取
			return cell.getStringCellValue();
		} catch (Exception e) {
			// 读取出错，说明是数字格式
			return String.valueOf(cell.getNumericCellValue());
		}
	}
	
	private String getCellNumValue(HSSFCell cell) {
		if (cell == null) {
			return "";
		}
		try {
//			 读取出错，说明是数字格式
			
			return String.valueOf(cell.getNumericCellValue()).equals("0.0")?"":String.valueOf(cell.getNumericCellValue());
		} catch (Exception e) {
//			 按照字符串格式读取
			return cell.getStringCellValue();
		}
	}

	private String getCellStrValue(HSSFCell cell, String ExcelFields) {
		if (cell == null) {
			return "";
		}
		try { 
			// 按照字符串格式读取
//			System.out.println("getCellStrValue:= |"
//					+ cell.getStringCellValue());
			return cell.getStringCellValue();
		} catch (Exception e) {
			// 读取出错，说明是数字格式
			String s = String.valueOf(cell.getNumericCellValue());
//			System.out.println("getCellStrValue:=" + s);
			if (this.STabStr.indexOf(ExcelFields) > -1) {
				s = (String) getExcelString(s);
//				System.out.println("getCellStrValue:=||" + s);
			}
			return s;
		}
	}

	private String getExcelString(String snum) {
		String result = "";
		int s = snum.indexOf("E");
		if (s > -1) {
			String[] st = snum.split("E");
			String[] stt = st[0].split("\\.");
			if (stt[1].length() == Integer.parseInt(st[1])) {
				result = stt[0] + stt[1];
			} else {
				String ss = "";
				for (int i = 0; i < Integer.parseInt(st[1]) - stt[1].length(); i++) {
					ss += "0";
				}
				result = stt[0] + stt[1] + ss;
			}
		} else {
			if (snum.indexOf(".") > -1) {
				result = snum.substring(0, snum.indexOf("."));
			} else {
				result = snum;
			}
		}
		return result;
	}

	/**
	 * 为避免重复打开EXCEL，首先调用INIT来打开一次，以后都不需要了
	 * 
	 * @throws Exception
	 */
	public void init() throws Exception {
		if (new File(strFullName).exists() == false) {
			throw new MatechException("文件[" + strFullName + "]不存在，装载失败！");
		}
		workbook = new HSSFWorkbook(new FileInputStream(strFullName));
	}

	/**
	 * 给出当前sheet的指定行的所有单元格，以数组形式返回
	 * 
	 * @param strSheetName
	 *            String EXCEL表页名字
	 * @param iRowNum
	 *            int 指定行
	 * @return String[] 单元格数组，第一个元素下标是0开始
	 */
	public String[] getOneRowCells(String strSheetName, int iRowNum) throws Exception {
		if (workbook == null) {
			throw new MatechException("请先调用init函数，装载失败！");
		}

		if (strSheetName == null) {
			throw new MatechException("请先设置表strSheetName属性，函数调用失败！");
		}

		HSSFSheet sheet = workbook.getSheet(strSheetName);
		if (sheet == null) {
			throw new MatechException("表页[" + strSheetName + "]不存在，请修改！");
		}

		if (iRowNum > sheet.getLastRowNum() || iRowNum < sheet.getFirstRowNum()) {
			throw new MatechException("指定行数[" + iRowNum + "超出指定表页"
					+ strSheetName + "]行范围，必须在" + sheet.getFirstRowNum() + "与"
					+ sheet.getLastRowNum() + "之间，请检查！");
		}

		// 也可用getSheetAt(int index)按索引引用，
		// 在Excel文档中，第一张工作表的缺省索引是0，
		// 其语句为：HSSFSheet sheet = workbook.getSheetAt(0);
		// 读取左上端单元
		HSSFRow row = sheet.getRow((short) iRowNum);
		short iStart = row.getFirstCellNum(), iEnd = row.getLastCellNum();
		String[] strHeads = new String[iEnd - iStart + 1];
		int t = 0;
		for (short i = iStart; i < iEnd; i++) {
			strHeads[t++] = getCellStrValue(row.getCell((short) i));
		}
		return strHeads;
	}
	
	/**
	 * 得到第一个sheet的名称
	 * @return
	 * @throws Exception
	 */
	public String getOneSheetName() throws Exception {
		if (workbook == null) {
			throw new MatechException("请先调用init函数，装载失败！");
		}
		return workbook.getSheetName(0);
	}

	/**
	 * 检查制定行是否存在指定内容的单元格， 返回如果为负数，则表示不存在， 返回为整数或者0，则表示这个格子的位置
	 * 
	 * @param strSheetName
	 *            String 表页名称，支持中文
	 * @param iRowNum
	 *            int 指定行
	 * @return int
	 * @throws Exception
	 */
	public int checkCellExist(String strSheetName, int iRowNum, String CellValue) throws Exception {
		if (workbook == null) {
			throw new MatechException("请先调用init函数，装载失败！");
		}

		if (strSheetName == null) {
			throw new MatechException("请先设置表strSheetName属性，函数调用失败！");
		}

		HSSFSheet sheet = workbook.getSheet(strSheetName);
		if (sheet == null) {
			throw new MatechException("表页[" + strSheetName + "]不存在，请修改！");
		}

		if (iRowNum > sheet.getLastRowNum() || iRowNum < sheet.getFirstRowNum()) {
			throw new MatechException("指定行数[" + iRowNum + "超出指定表页"
					+ strSheetName + "]行范围，必须在" + sheet.getFirstRowNum() + "与"
					+ sheet.getLastRowNum() + "之间，请检查！");
		}

		// 也可用getSheetAt(int index)按索引引用，
		// 在Excel文档中，第一张工作表的缺省索引是0，
		// 其语句为：HSSFSheet sheet = workbook.getSheetAt(0);
		// 读取左上端单元
		HSSFRow row = sheet.getRow((short) iRowNum);
		short iStart = row.getFirstCellNum(), iEnd = row.getLastCellNum();
		String[] strHeads = new String[iEnd - iStart + 1];
		int t = 0;
		for (short i = iStart; i < iEnd; i++) {
			if (getCellStrValue(row.getCell((short) i)).equals(CellValue)) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * 检查表头是否存在指定内容的单元格
	 * 
	 * @param strSheetName
	 *            String
	 * @param iRowNum
	 *            int
	 * @param CellValue
	 *            String
	 * @return boolean
	 * @throws Exception
	 */
	public int checkHeadCellExist(String strSheetName, String CellValue) throws Exception {
		return checkCellExist(strSheetName, 0, CellValue);
	}

	/**
	 * 读取指定SHEET的第一行的单元格数据，以字符串数组的形式返回
	 * 
	 * @param strSheetName
	 *            String
	 * @return String[]
	 * @throws Exception
	 */
	public String[] getAllHeads(String strSheetName) throws Exception {
		return (getOneRowCells(strSheetName, 0));
	}

	
	public String LoadFromExcel(String strSheetName, String strTable,
			String[] strExcelFields, String[] strTablefields,
			String[] strFixFields, String[] strFixFieldValues,boolean genUUID) throws Exception {
		return LoadFromExcel(strSheetName, strTable, strExcelFields,
				strTablefields, null, null, strFixFields, strFixFieldValues,genUUID);
	}
	
	/**
	 * 从EXCEL文件中装载指定SHEET到表格中
	 * 
	 * @param strSheetName
	 *            String EXCEL表页名字
	 * @param strTable
	 *            String 目标表名字，注意本函数不会事先清空目标表
	 * @param strExcelFields
	 *            String[] EXCEL中表列
	 * @param strTablefields
	 *            String[] 目标表的字段列，顺序要和strExcelFields的顺序对应
	 * @param strOptionalExcelFields
	 *            String[] 设置EXCEL表内一些可选字段，比如外币、数量帐等, 如果没有，请用null赋值
	 * @param strOptionalTablefields
	 *            String[] 设置表内一些可选字段的值，比如currency的 如果没有，请用null赋值
	 * @param strFixFields
	 *            String[] 设置表内一些固定字段，比如AccPackageID, 如果没有，请用null赋值
	 * @param strFixFieldValues
	 *            String[] 设置表内一些固定字段的值，比如AccPackageID的值 如果没有，请用null赋值
	 * @return result 用于记录装载过程中的一些异常情况，比如忽略了哪些合计行等；
	 * @throws Exception
	 */
	public String LoadFromExcel(String strSheetName, String strTable,
			String[] strExcelFields, String[] strTablefields,
			String[] strFixFields, String[] strFixFieldValues) throws Exception {
		return LoadFromExcel(strSheetName, strTable, strExcelFields,
				strTablefields, null, null, strFixFields, strFixFieldValues);
	}
 /**
  * 主要针对薪酬模块的多页面批量导入
  * @param sheetName
  * @return
  */
 public List getOptionTable(String sheetName){
	 String rank = "";
	 List list = new ArrayList();
	 if(!sheetName.equals("")){
		 rank = sheetName.split(",")[0];
	 }
	 PreparedStatement ps = null;
	 ResultSet rs = null;
	 String wagesList  = "";
	 String optionList ="";
	 String sql ="select group_concat(wagesName ORDER BY orderid) from k_rankwages a inner join k_rank b on a.rankId = b.autoId where name =?";
	 try{
		 ps = conn.prepareStatement(sql);
		 ps.setString(1, rank);
		 rs = ps.executeQuery();
		 if(rs.next()){
			 wagesList = rs.getString(1); 
			 if(wagesList == null){
				 wagesList = "";
			 }
		 }
		 optionList=wagesList+",姓名,部门";
		String[] optionExcel = optionList.split(",");
		 
		String[] optionTable = new String[optionExcel.length];
		String[] fixExcel = new String[wagesList.split(",").length];
		String[] fixTable = new String[wagesList.split(",").length];
		 for(int i = 0,j=0;i<optionExcel.length;i++,j++){
			   if(optionExcel[i].equals("姓名")){
				   optionTable[i]="userId";
			   }else if(optionExcel[i].equals("部门")){
				   optionTable[i]="departmentid";
			   }else{
				   optionTable[i]= "v"+(j+1);
				   fixExcel[i]   = optionExcel[i];
				   fixTable[i]   = "n"+(i+1);
//				   fixExcel[j]
			   }
		 }
		   list.add(0, optionExcel);
		   list.add(1,optionTable);
		   list.add(2,fixExcel);
		   list.add(3, fixTable);
		   
		 
	 }catch(Exception e){
		 e.printStackTrace();
	 }finally{
		 DbUtil.close(ps);
	 }
	 //optionTable ="" ;
   
      
	 return list;
 }	

 
 
 /**
  * 载入多页面的Excel
  * @param strTable
  * @param strExcelFields
  * @param strTablefields
  * @param strOptionalExcelFields
  * @param strOptionalTablefields
  * @param strFixFields
  * @param strFixFieldValues
  * @throws Exception
  */	
  public void LoadFromExcelAllSheet(String strTable,
    String[] strExcelFields, String[] strTablefields)throws Exception {
    String sheetName = "";	
		if (workbook == null) {
			throw new Exception("请先调用init函数，装载失败！");
		}
		
		for(int i=0;i<workbook.getNumberOfSheets();i++){
			sheetName = workbook.getSheetName(i);
		   List params = getOptionTable(sheetName);
		   String[] strOptionalExcelFields = (String[])params.get(0);    //excel
		   String[] strOptionalTablefields = (String[])params.get(1);    //数据库字段
		   String[] strFixFieldValues      =(String[])params.get(2);	         //excel
		   String[] strFixFields           =(String[])params.get(3); 		 //数据库字段
		   //System.out.println(strOptionalExcelFields.length); 
		    LoadFromExcel(sheetName, strTable,
		              strExcelFields, strTablefields,
		              strOptionalExcelFields,
		             strOptionalTablefields,
		             strFixFields, strFixFieldValues);
		}
    }
	
  public String LoadFromExcel(String strSheetName, String strTable,
          String[] strExcelFields, String[] strTablefields,
          String[] strOptionalExcelFields,
          String[] strOptionalTablefields,
          String[] strFixFields, String[] strFixFieldValues
	) throws Exception {
	  return LoadFromExcel(strSheetName, strTable, strExcelFields, strTablefields, strOptionalExcelFields, strOptionalTablefields, strFixFields, strFixFieldValues, false);
  }
	
	 public String LoadFromExcel(String strSheetName, String strTable,
             String[] strExcelFields, String[] strTablefields,
             String[] strOptionalExcelFields,
             String[] strOptionalTablefields,
             String[] strFixFields, String[] strFixFieldValues,boolean genUUID
	) throws Exception {
	if (workbook == null) {
		throw new Exception("请先调用init函数，装载失败！");
	}
	
	if (strExcelFields == null || strExcelFields.length == 0 ||
			strTablefields == null || strTablefields.length == 0) {
		throw new Exception("请先设置表strExcelFields和strTablefields属性，装载失败！");
	}
	
	if (conn == null) {
		new Exception("数据库联结不能为空");
	}
	else {
		conn.setAutoCommit(false);
	}
	String result = "";
	
	// 创建对工作表的引用。
	//本例是按名引用（让我们假定那张表有着缺省名"Sheet1"）
	HSSFSheet sheet = workbook.getSheet(strSheetName);
	if (sheet == null) {
		throw new Exception("表页[" + strSheetName + "]不存在，请修改！");
	}
	
	// 也可用getSheetAt(int index)按索引引用，
	// 在Excel文档中，第一张工作表的缺省索引是0，
	// 其语句为：HSSFSheet sheet = workbook.getSheetAt(0);
	// 读取左上端单元
	int i, j,iTitleRow;
	int Num = workbook.getNumberOfSheets();
	HSSFRow row=null;
	HSSFCell cell1 = null;
//	row = sheet.getRow(3);
	iTitleRow=0;
	for (j=0;j<100;j++){
		row = sheet.getRow(j);
		if(row!=null){
              cell1 = row.getCell(row.getFirstCellNum());
              if(cell1!=null && (!"".equals(cell1.getStringCellValue()))){
            	  iTitleRow=j;
            	  break;
              }

		}
//		if (!"".equals(row.getCell(row.getFirstCellNum()).getStringCellValue())){
//			iTitleRow=j;
//			break;
//		}
	}
	if (iTitleRow<0){
		throw new Exception("无法定位标题行！");
	}
	
	int opt = strOptionalExcelFields != null ? strOptionalExcelFields.length : 0;
	
	//使用必有EXCEL列总数和可选列总数来创建位置数组
	int[] iFieldLocate = new int[strExcelFields.length +opt];
	
	/**
	* 先判断必填列所在的位置
	*/
	//设置为-1
	for (j = 0; j < iFieldLocate.length; j++) {
	//位置的初始值清零
		iFieldLocate[j] = -1;
	}
	//遍历一遍,把所有的值填上
	SExcelName = new String[strExcelFields.length + opt];
	for (i = row.getFirstCellNum(); i < row.getLastCellNum(); i++) {
		HSSFCell cell = row.getCell( (short) i);
	
		if (cell == null)
			continue;
		//看看是不是必填列
	
		for (j = 0; j < strExcelFields.length; j++) {
			if (cell.getStringCellValue().equalsIgnoreCase(strExcelFields[j])) {
			//相等，就记录对应的值
			iFieldLocate[j] = i;
			SExcelName[j] = new String();
			SExcelName[j] = cell.getStringCellValue();
			}
		}
		//看看是不是可选列
		
		for (; j < strExcelFields.length + opt; j++) {
			if (cell.getStringCellValue().equalsIgnoreCase(strOptionalExcelFields[j-strExcelFields.length])) {
			//相等，就记录对应的值
			iFieldLocate[j] = i;
			SExcelName[j] = new String();
			SExcelName[j] = cell.getStringCellValue();
			}
		}
	} //for
	//最后检查所有必填列是不是都填了，可选列倒是可有可无（没有的值还是保留为-1）
	for (i = 0; i < strExcelFields.length; i++) {
		if (iFieldLocate[i] == -1) {
		throw new Exception("EXCEL的[" + strSheetName + "]表页中缺少必须的[" +
		           strExcelFields[i] + "]列，装载失败");
		}
	}
	
	/**
	* 构造SQL语句，合并了必填和可选列
	*/
	String sql = UTILString.killEndToken(
		UTILString.getStringFromArray1(strTablefields, ",")
		+ UTILString.getStringFromArray1(strOptionalTablefields, ",")
		+ UTILString.getStringFromArray1(strFixFields, ",")
		, ",");
	System.out.println("sql=" + sql);
	String sql1 = "select " + sql + " from " + strTable + " where 1=2";
	if (strFixFieldValues != null && strFixFieldValues.length > 0) {
		
		sql = "insert into " + strTable + "("+(genUUID? "uuid,":"") + sql + ") values ("+(genUUID?"UUID(),":"")
		+ UTILString.killEndToken(
		UTILString.nCharToString("?,",
		                      strTablefields.length +
		                      (strOptionalTablefields!=null?strOptionalTablefields.length:0))
		+ "'" + UTILString.getStringFromArray1(strFixFieldValues, "','")
		, ",'") + ");";
	}
	else {
		sql = "insert into " + strTable + "("+(genUUID? "uuid,":"") + sql + ") values ("+(genUUID?"UUID(),":"")
		+ UTILString.killEndToken(
		UTILString.nCharToString("?,",
		                      strTablefields.length +
		                      (strOptionalTablefields!=null?strOptionalTablefields.length:0))
		, ",") + ");";
	}
	
	System.out.println("sql=" + sql);
	
	//获取表结构
	ResultSetMetaData rsmd = null;
	PreparedStatement ps = null;
	ResultSet rs = null;
	try {
		ps = conn.prepareStatement(sql1);
		rs = ps.executeQuery();
		if (rs == null) {
			throw new Exception("无法获取" + strTable + "表信息");
		}
		rsmd = rs.getMetaData();
		if (rsmd == null) {
			throw new Exception("无法获取" + strTable + "表的字段信息");
		}
	}
	catch (Exception e) {
		throw new Exception(e.getMessage());
	}
	finally {
	//if (rs != null)
	//rs.close();
	//if (ps != null)
	//ps.close();
	}
	
	//开始装载数据
	ps = conn.prepareStatement(sql);
	int count = 0, cancel;
	
	HSSFCell cell = null;
	try {
	//逐行装载，但是会去掉其中的"有合计字样"的科目（从第二行装载，第一行是标题，跳过）
		for (i = iTitleRow+1; i <= sheet.getLastRowNum(); i++) {
			cancel = 0;
			row = sheet.getRow(i);
			//逐位置数组遍历，而不是单元格遍历
			for (j = 0; j < iFieldLocate.length; j++) {
				String value = "";
				//如果有对应的单元格列号，则取出对应值，否则置为0			
				String sEx = "";
				if (iFieldLocate[j] >= 0) {
					cell=null;
					try{
						cell = row.getCell( (short) iFieldLocate[j]);
					}catch(Exception e){
						cancel=1;
						System.out.println("奇怪的报错了："+e.getMessage());
						//e.printStackTrace(); 
					}
					if (cell!=null){
						if(this.STabStr.indexOf(SExcelName[j])>-1){
							value = getCellStrValue(cell,SExcelName[j]);
						}else if(this.STabNum.indexOf(SExcelName[j])>-1){
							value = getCellNumValue(cell);
						}else{
							value = getCellStrValue(cell);
						}
						if(this.STabDate != null && this.STabDate.indexOf(SExcelName[j])>-1){
							java.util.Date dateValue = cell.getDateCellValue();
							java.text.SimpleDateFormat dateformat = new java.text.SimpleDateFormat("yyyy-MM-dd");
							value = dateformat.format(dateValue);
						}
					}else{
						value="";
					}
					
					sEx = SExcelName[j];
				}
				else {
					value = "";
				}
				System.out.println(j+"|"+iFieldLocate[j]+"=:|"+value);
				//根据数据库表的字段属性来对VALUE进行修饰
				switch (rsmd.getColumnType(j+1)) {
					case java.sql.Types.CHAR:
					case java.sql.Types.VARCHAR:
						String t1 = value.trim();
						if(sEx !=null && !"".equals(sEx)){
							if(this.STabNum.indexOf(sEx)>-1){
								try{
									t1 = new java.text.DecimalFormat("#0.00").format(
					                     Double.
					                     parseDouble(t1.replaceAll(",", "")));
								}catch(Exception e){
					             result+=this.STabNum+"列遇到非法字符串:"+t1+",按照0处理！<br/>";
					             t1="0.00";
								}
							}
						}
		
						ps.setString(j+1, t1.replaceAll("\n",""));
						break;
					case java.sql.Types.INTEGER:
						if (value == null || value.equals(""))
							ps.setInt(j+1, 0);
						else
							ps.setString(j+1, value);
						break;
					case java.sql.Types.DECIMAL:
						if (value == null || value.equals(""))
							ps.setInt(j+1, 0);
						else {
						//转换精度
							String t = new java.text.DecimalFormat("#0.00").format(Double.
									parseDouble(value.replaceAll(",","")));
							ps.setString(j+1, t);
						}
						break;
					default:
						ps.setString(j+1, value);
				} //遍历单元格结束
			} //遍历行结束
			if (cancel == 0) {
				ps.addBatch();
				count++;
				if (count % 200 == 0) {
					ps.executeBatch();
					conn.commit();
				}
			}
		}
		if (count % 200 != 0) {
			ps.executeBatch();
			conn.commit();
		}
	}
	catch (Exception e) {
		e.printStackTrace();
		result = e.getMessage();
		result.replaceAll("entry", "");
		result.replaceAll("Duplicate", "唯一编号出现了重复！");
		throw new Exception("执行失败:" + result, e);
	}
	finally {
		if (ps != null)
			ps.close();
	}
	
		return result;
	}
	 
	 /**
	   * 清除指定临时表的数据,避免冲突;
	   * @param table String
	   * @return boolean
	   */
	  public boolean DropTable(String table) throws Exception {
	    if (conn == null) {
	      new Exception("数据库联结不能为空");
	    }
	    PreparedStatement ps = null;
	    try {
	      ps = conn.prepareStatement("drop table  " + table );
	      ps.execute();

	      if (conn.getAutoCommit() == false) {
	        conn.commit();
	      }
	      return true;
	    }
	    catch (Exception e) {
	      e.printStackTrace();
	      throw new Exception("执行失败" + e.getMessage(), e);
	    }
	    finally {
	      if (ps != null) {
	        ps.close();
	      }
	    }
	  }	 
	  

		/**
		 * 从EXCEL文件中装载指定SHEET到表格中
		 * 
		 * @param strSheetName
		 *            String EXCEL表页名字
		 * @param strTable
		 *            String 目标表名字，注意本函数不会事先清空目标表
		 * @param strExcelFields
		 *            String[] EXCEL中表列
		 * @param strTablefields
		 *            String[] 目标表的字段列，顺序要和strExcelFields的顺序对应
		 * @param strOptionalExcelFields
		 *            String[] 设置EXCEL表内一些可选字段，比如外币、数量帐等, 如果没有，请用null赋值
		 * @param strOptionalTablefields
		 *            String[] 设置表内一些可选字段的值，比如currency的 如果没有，请用null赋值
		 * @param strFixFields
		 *            String[] 设置表内一些固定字段，比如AccPackageID, 如果没有，请用null赋值
		 * @param strFixFieldValues
		 *            String[] 设置表内一些固定字段的值，比如AccPackageID的值 如果没有，请用null赋值
		 * @return result 用于记录装载过程中的一些异常情况，比如忽略了哪些合计行等；
		 * @throws Exception
		 */
		public String LoadAllSheetFromExcel(String[] strTable0,
				String[][] strExcelFields0, String[][] strTablefields0,
				String[][] strFixFields0, String[][] strFixFieldValues0) throws Exception {
			return LoadAllSheetFromExcel(strTable0, strExcelFields0,
					strTablefields0, null, null, strFixFields0, strFixFieldValues0);
		}

		 public String LoadAllSheetFromExcel(String[] strTable0,
	             String[][] strExcelFields0, String[][] strTablefields0,
	             String[][] strOptionalExcelFields0,
	             String[][] strOptionalTablefields0,
	             String[][] strFixFields0, String[][] strFixFieldValues0
		) throws Exception {
		if (workbook == null) {
			throw new Exception("请先调用init函数，装载失败！");
		}
		
		if (strExcelFields0 == null || strExcelFields0.length == 0 ||
				strTablefields0 == null || strTablefields0.length == 0) {
			throw new Exception("请先设置表strExcelFields和strTablefields属性，装载失败！");
		}
		
		if (conn == null) {
			new Exception("数据库联结不能为空");
		}
		else {
			conn.setAutoCommit(false);
		}
		String result = "";
		

		// 也可用getSheetAt(int index)按索引引用，
		// 在Excel文档中，第一张工作表的缺省索引是0，
		// 其语句为：
		// 读取左上端单元
		int Num = workbook.getNumberOfSheets();
		String strTable = "";
		String[] strExcelFields = null;
		String[] strTablefields = null;
		String[] strOptionalExcelFields = null;
		String[] strOptionalTablefields = null;
		String[] strFixFields = null;
		String[] strFixFieldValues = null;
			for(int z=0;z<Num;z++){
				HSSFSheet sheet = workbook.getSheetAt(z);
				int ii=0;
				if(workbook.getSheetName(z).indexOf("科目余额表")>-1){
					ii = 1;
				}else if(workbook.getSheetName(z).indexOf("月度余额表")>-1){
					ii = 2;
				}else if("目录".equals(workbook.getSheetName(z))){
					continue;
				}
				strTable = strTable0[ii];
				strExcelFields = strExcelFields0[ii];
				strTablefields = strTablefields0[ii];
				strOptionalExcelFields = strOptionalExcelFields0[ii];
				strOptionalTablefields = strOptionalTablefields0[ii];
				strFixFields = strFixFields0[ii];
				strFixFieldValues = strFixFieldValues0[ii];
	//				continue;
	//			
				HSSFRow row = sheet.getRow(0);
				
				int opt = strOptionalExcelFields != null ? strOptionalExcelFields.length : 0;
				int i, j;
				//使用必有EXCEL列总数和可选列总数来创建位置数组
				int[] iFieldLocate = new int[strExcelFields.length +opt];
				
				/**
				* 先判断必填列所在的位置
				*/
				//设置为-1
				for (j = 0; j < iFieldLocate.length; j++) {
				//位置的初始值清零
					iFieldLocate[j] = -1;
				}
				//遍历一遍,把所有的值填上
				SExcelName = new String[strExcelFields.length + opt];
				for (i = row.getFirstCellNum(); i < row.getLastCellNum(); i++) {
					HSSFCell cell = row.getCell( (short) i);
				
					if (cell == null)
						continue;
					//看看是不是必填列
					
					for (j = 0; j < strExcelFields.length; j++) {
						if (cell.getStringCellValue().indexOf(strExcelFields[j]) >= 0) {
						//相等，就记录对应的值
							iFieldLocate[j] = i;
							SExcelName[j] = new String();
							
							
							SExcelName[j] = cell.getStringCellValue();
							
						}
					}
					//看看是不是可选列
					
					for (; j < strExcelFields.length + opt; j++) {
						if (cell.getStringCellValue().indexOf(strOptionalExcelFields[j-strExcelFields.length]) >= 0) {
						//相等，就记录对应的值
							iFieldLocate[j] = i;
							SExcelName[j] = new String();
							
							SExcelName[j] = cell.getStringCellValue();
							
						}
					}
				} //for
				//最后检查所有必填列是不是都填了，可选列倒是可有可无（没有的值还是保留为-1）
				for (i = 0; i < strExcelFields.length; i++) {
					if (iFieldLocate[i] == -1) {
					throw new Exception("EXCEL的["+workbook.getSheetName(z)+"]表页中缺少必须的[" +
					           strExcelFields[i] + "]列，装载失败");
					}
				}
				
				/**
				* 构造SQL语句，合并了必填和可选列
				*/
				String sql = UTILString.killEndToken(
					UTILString.getStringFromArray1(strTablefields, ",")
					+ UTILString.getStringFromArray1(strOptionalTablefields, ",")
					+ UTILString.getStringFromArray1(strFixFields, ",")
					, ",");
				System.out.println("sql=" + sql);
				String sql1 = "select " + sql + " from " + strTable + " where 1=2";
				if (strFixFieldValues != null && strFixFieldValues.length > 0) {
					sql = "insert into " + strTable + "(" + sql + ") values ("
					+ UTILString.killEndToken(
					UTILString.nCharToString("?,",
					                      strTablefields.length +
					                      (strOptionalTablefields!=null?strOptionalTablefields.length:0))
					+ "'" + UTILString.getStringFromArray1(strFixFieldValues, "','").replaceAll("'\\$\\{rownum\\}'", "?")
					, ",'") + ");";
				}
				else {
					sql = "insert into " + strTable + "(" + sql + ") values ("
					+ UTILString.killEndToken(
					UTILString.nCharToString("?,",
					                      strTablefields.length +
					                      (strOptionalTablefields!=null?strOptionalTablefields.length:0))
					, ",") + ");";
				}
				
				System.out.println("sql=" + sql);
				
				//获取表结构
				ResultSetMetaData rsmd = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				try {
					ps = conn.prepareStatement(sql1);
					rs = ps.executeQuery();
					if (rs == null) {
						throw new Exception("无法获取" + strTable + "表信息");
					}
					rsmd = rs.getMetaData();
					if (rsmd == null) {
						throw new Exception("无法获取" + strTable + "表的字段信息");
					}
				}
				catch (Exception e) {
					throw new Exception(e.getMessage());
				}
				finally {
				//if (rs != null)
				//rs.close();
				//if (ps != null)
				//ps.close();
				}
				
				//开始装载数据
				ps = conn.prepareStatement(sql);
				int count = 0, cancel;
				String value = "";
				HSSFCell cell = null;
				try {
				//逐行装载，但是会去掉其中的"有合计字样"的科目（从第二行装载，第一行是标题，跳过）
					for (i = 1; i <= sheet.getLastRowNum(); i++) {
						String values = "";
						cancel = 0;
						row = sheet.getRow(i);
						//逐位置数组遍历，而不是单元格遍历
						for (j = 0; j < iFieldLocate.length; j++) {
							//如果有对应的单元格列号，则取出对应值，否则置为0			
							String sEx = "";
							if (iFieldLocate[j] >= 0) {
								cell=null;
								try{
									cell = row.getCell( (short) iFieldLocate[j]);
								}catch(Exception e){
									cancel=1;
								//	System.out.println("我是空行");
								}
								if (cell!=null){
									if(this.STabStr.indexOf(SExcelName[j].trim())>-1){
										value = getCellStrValue(cell,SExcelName[j].trim());
										if("".equals(value)){
											values += "#";
										}
									}else if(this.STabNum.indexOf(SExcelName[j])>-1){
										value = getCellNumValue(cell);
									}else{
										value = getCellStrValue(cell);
									}
									if(this.STabDate != null && this.STabDate.indexOf(SExcelName[j].trim())>-1){
										try{
											java.util.Date dateValue = cell.getDateCellValue();
											java.text.SimpleDateFormat dateformat = new java.text.SimpleDateFormat("yyyy-MM-dd");
											value = dateformat.format(dateValue);
										}catch (Exception e) {
											value = getCellStrValue(cell);
										}
									}
								}else{
									value="";
								}
								
								sEx = SExcelName[j].trim();
							}
							else {
								value = "";
							}
							
							values += value; 
							//根据数据库表的字段属性来对VALUE进行修饰
							switch (rsmd.getColumnType(j+1)) {
								case java.sql.Types.CHAR:
								case java.sql.Types.VARCHAR:
									String t1 = value.trim();
									if(sEx !=null && !"".equals(sEx)){
										if(this.STabNum.indexOf(sEx)>-1){
											try{
												t1 = new java.text.DecimalFormat("#0.00").format(
								                     Double.
								                     parseDouble(t1.replaceAll(",", "")));
											}catch(Exception e){
								             result+=this.STabNum+"列遇到非法字符串:"+t1+",按照0处理！<br/>";
								             t1="0.00";
											}
										}
									}
					
									ps.setString(j+1, t1);
									break;
								case java.sql.Types.INTEGER:
									if (value == null || value.equals(""))
										ps.setInt(j+1, 0);
									else
										ps.setString(j+1, value);
									break;
								case java.sql.Types.DECIMAL:
									if (value == null || value.equals(""))
										ps.setInt(j+1, 0);
									else {
									//转换精度
										String t = new java.text.DecimalFormat("#0.00").format(Double.
												parseDouble(value.replaceAll(",","")));
										ps.setString(j+1, t);
									}
									break;
								default:
									ps.setString(j+1, value);
							} //遍历单元格结束
						
							
						} //遍历行结束
						if(ii==0){
							ps.setInt(j+1, i);
						}
						if (cancel == 0) {
							if(values.indexOf("#")<0&!"".equals(values.trim())){
								ps.addBatch();
								count++;
							}
							if (count % 200 == 0) {
								ps.executeBatch();
								conn.commit();
							}
						}
					}
					if (count % 200 != 0) {
						ps.executeBatch();
						conn.commit();
					}
				}
				catch (Exception e) {
					e.printStackTrace();
					result = e.getMessage();
					result.replaceAll("entry", "");
					result.replaceAll("Duplicate", "唯一编号出现了重复！");
					throw new Exception("执行失败:" + result, e);
				}
				finally {
					if (ps != null)
						ps.close();
				}
			}
			
			return result;
		}
		 
		public String [] getExcelRows(String [] tableRows,String [] excelAllRows ){
//				String []excelRows = new String [excelAllRows.length-1];
			int [] ii = new int [excelAllRows.length];
			for(int i=0;i<ii.length;i++){
				ii[i]=-1;
			}
			int num=0;
			for (int i = 0; i < excelAllRows.length-1; i++) {
				for (int j = 0; j < tableRows.length; j++) {
					if(excelAllRows[i].trim().equals(tableRows[j].trim())){
//							ii[num++]=i;
						break;
					}
					if(j==tableRows.length-1){
//							excelRows[num] = new String();
//							excelRows[num]=excelAllRows[i];
//							num++;
						ii[num]=i;
						num++;
					}
				}
			}
			String []excelRows = new String [num];
			for (int i = 0; i < ii.length; i++) {
				for (int j = 0; j < excelAllRows.length-1; j++) {
					if(ii[i]==j && ii[i]!=-1){
						excelRows[i]= new String();
						excelRows[i]= excelAllRows[j].trim();
						break;
					}
				}
			}
			
			org.util.Debug.prtOut("excelRows:="+excelRows.length);
			return excelRows;
		}	 
		 
}

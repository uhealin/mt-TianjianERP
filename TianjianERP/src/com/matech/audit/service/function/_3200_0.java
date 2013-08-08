package com.matech.audit.service.function;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.matech.audit.service.dataupload.DisposeTableService;
import com.matech.audit.service.dataupload.UploadItemService;

/**
 * 
 * @author Administrator
 *根据固定资产查询的逻辑取数，
 *支持：
 *类别 固定资产名称 期初原值 预计使用期间 残值率 残值 月折旧率（%） 年折旧率 本年折旧 累计折旧 净值
 *如果有某一列无法对照，则直接显示类似：未提供【资产原值】列对照...
 *提供输出列为:
 *代码,名称,类别,预计使用期间(工作总量),累计折旧,年折旧率,预计净残值,原值,残值率,月折旧率,本年折旧,净值
 *
 *如:=取列公式覆盖(3201, "1111", "年折旧率")
 *
 */

public class _3200_0 extends AbstractAreaFunction {


	public ResultSet process(HttpSession session, HttpServletRequest request, HttpServletResponse response, Connection conn, Map args) throws Exception {
		
		
		String accpackageid=(String)args.get("curAccPackageID");

		String resultSql="";
		String sql = "";
		PreparedStatement ps = null;
		Statement st=null;
		ResultSet rs=null;
		
		String colName = "";
		String col[] = new String[8];
		String itemName[] = new String[]{"代码", "名称", "类别", "预计使用期间(工作总量)", "累计折旧", "年折旧率", "预计净残值","原值"};
		try{
			org.util.Debug.prtErr(resultSql);
			st = conn.createStatement();
			
			UploadItemService ui = new UploadItemService(conn,accpackageid);
			if(ui.ItemExist()<=0) {
//				没有此账套的固定资产!
				throw new Exception("没有此账套["+accpackageid+"]的固定资产!");
			}

			
			sql = " select itemcode,standupname from f_item where accpackageid="+accpackageid+"";
			ps = conn.prepareStatement(sql);
			
			for(int i=0;i<itemName.length;i++){
				int j=0;
				rs = ps.executeQuery();
				while(rs.next()) {
					if(itemName[i].equals(rs.getString("standupname"))) {//根据对应的标准列构造Sql
						colName += ""+rs.getString("itemcode")+" `"+itemName[i]+"`,";
						col[i] = rs.getString("itemcode");
						j++;
					}
				}
				if(j==0) {//如果有没对照的列,则显示未提供信息.
					colName +="'未提供【"+itemName[i]+"】列对照' `"+itemName[i]+"`,";
				}
				
			}
			if(col[6]==null || col[7]==null) {//如果组成"残值率"计算结果的值为Null,则显示该列未对照
				if(col[6]==null) {
					colName += "'未提供【"+itemName[6]+"】列对照' `残值率`,";
				} else {
					colName += "'未提供【"+itemName[7]+"】列对照' `残值率`,";
				}
				
			} else {//如果值都存在,则计算出该值;残值率=预计净残值/原值
				colName += col[6]+"/"+col[7]+" `残值率`,";
			}
			
			if(col[5]==null) {//如果组成"月折旧率"计算结果的值为Null,则显示该列未对照
				colName += "'未提供【"+itemName[5]+"】列对照' `月折旧率`,";
			} else {//月折旧率=年折旧率/12*100
				colName += col[5]+"/12*100"+" `月折旧率`,";
			}
			
			if(col[7]==null || col[6]==null || col[5]==null) {//如果组成"本年折旧"计算结果的值为Null,则显示该列未对照
				if(col[7]==null) {
					colName += "'未提供【"+itemName[7]+"】列对照' `本年折旧`,";
				} else if(col[6]==null) {
					colName += "'未提供【"+itemName[6]+"】列对照' `本年折旧`,";
				} else {
					colName += "'未提供【"+itemName[5]+"】列对照' `本年折旧`,";
				}
			} else {//本年折旧=(原值-预计净残值)*年折旧率
				colName += "("+col[7]+"-"+col[6]+")*"+col[5]+" `本年折旧`,";
			}
			
			if(col[7]==null || col[4]==null) {//如果组成"净值"计算结果的值为Null,则显示该列未对照
				if(col[7]==null) {
					colName += "'未提供【"+itemName[7]+"】列对照' `净值`,";
				} else {
					colName += "'未提供【"+itemName[4]+"】列对照' `净值`,";
				}
			} else {//净值=原值-累计折旧
				colName += col[7]+"-"+col[4]+" `净值` ";
			}
			
			colName = colName.substring(0,colName.length()-1);
		
			boolean bool = new DisposeTableService(conn).checkTableExist("fa_" + accpackageid);
			String str = accpackageid.substring(6);
			if(bool) str = accpackageid;
			//根据条件构造取数Sql
			resultSql = "select "+colName+"  from FA_"+str+" order by ItemNO";

			System.out.println("resultSql1="+resultSql);	
			//最终查询结果
			resultSql=this.setSqlArguments(resultSql, args);
			
			st.executeQuery("set   charset   gbk;");
			rs=st.executeQuery(resultSql);
		
			return rs;
		}catch(Exception e){
			e.printStackTrace();
			throw  e;
		}
	}

}
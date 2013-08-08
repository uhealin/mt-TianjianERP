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
 *
 *根据类别(group by 类别)，刷出：资产原值、本年折旧、累计折旧、净值的SUM值；
 *类别 资产原值 本年折旧 累计折旧 净值
 *如果有某一列无法对照，则直接显示类似：未提供【资产原值】列对照...
 *
 *例:
 *=取行公式覆盖(3201, "1111", "年折旧率")
 *输入参数为中文:类别,原值,年折旧率,累计折旧,预计净残值
 */

public class _3201_0 extends AbstractAreaFunction {


	public ResultSet process(HttpSession session, HttpServletRequest request, HttpServletResponse response, Connection conn, Map args) throws Exception {
		
		
		String accpackageid=(String)args.get("curAccPackageID");

		String resultSql="";
		String sql= "";
		PreparedStatement ps = null;
		Statement st=null;
		ResultSet rs=null;
		
		String colName = " select ItemClass `类别`,";
		String itemName[] = new String[]{"原值", "年折旧率", "累计折旧", "预计净残值"};
		try{
			st = conn.createStatement();
			
			UploadItemService ui = new UploadItemService(conn,accpackageid);

			if(ui.ItemExist()<=0) {
//				没有此账套[<%=accpackageid%>]的固定资产!
				throw new Exception("没有此账套["+accpackageid+"]的固定资产!");
			}
			sql = " select itemcode,standupname from f_item where accpackageid="+accpackageid+"";
			ps = conn.prepareStatement(sql);
			
			for(int i=0;i<itemName.length;i++){
				int j=0;
				rs = ps.executeQuery();
				while(rs.next()) {
					if(itemName[i].equals(rs.getString("standupname"))) {//根据对应的标准列构造Sql
						colName += "sum("+rs.getString("itemcode")+") `"+itemName[i]+"`,";
						j++;
					}
				}
				if(j==0) {//如果有没对照的列,则显示未提供信息.
					colName +="'未提供【"+itemName[i]+"】列对照' `"+itemName[i]+"`,";
				}
				
			}
			colName = colName.substring(0,colName.length()-1);
			
			boolean bool = new DisposeTableService(conn).checkTableExist("fa_" + accpackageid);
			String str = accpackageid.substring(6);
			if(bool) str = accpackageid;

			resultSql = colName +" from FA_"+str+" group by ItemClass order by ItemNO ";
			System.out.println("resultSql1="+resultSql);	
			//最终查询结果
			resultSql=this.setSqlArguments(resultSql, args);
			st.executeQuery("set   charset   gbk;");
			rs=st.executeQuery(resultSql);
		
			return rs;
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}
	}

}
package com.matech.audit.service.function;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.matech.audit.service.dataupload.UploadItemService;
import com.matech.audit.service.project.ProjectService;

/**
 * 
 * @author yzm
 *
 *需要刷出固定资产每个一个分类的原值、累计折旧、减值准备，及其期初、借发生、贷发生、期末余额。
 *
 *输入参数：无
 *输出参数：期初原值，原值增加，原值减少，原值期末，期初累计折旧，累计折旧增加，累计折旧减少，累计折旧期末，期初减值准备，减值准备增加，减值准备减少，期末减值准备
 *例:
 *=取行公式覆盖(3202, "", "期初原值")
 *
 */

public class _3202_0 extends AbstractAreaFunction {


	public ResultSet process(HttpSession session, HttpServletRequest request, HttpServletResponse response, Connection conn, Map args) throws Exception {
		
		
		String projectid = (String) args.get("curProjectid");
		String accpackageid = (String) args.get("curAccPackageID");


		ProjectService projectService = new ProjectService(conn);
		
		Statement st=null;
		ResultSet rs=null;
		PreparedStatement ps = null;
		String sql = "";
		int[] ProjectAuditArea = projectService
				.getProjectAuditAreaByProjectid(projectid);
	
		
		
		String colName = " select a.accpackageid `帐套编号`,a.itemname `类别`,";
		String itemName[] = new String[]{"原值期初","原值增加","原值减少", "原值期末", "期初累计折旧","累计折旧增加","累计折旧减少","累计折旧期末","期初减值准备","减值准备增加","减值准备减少","期末减值准备" };
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
						colName += rs.getString("itemcode")+" `"+itemName[i]+"`,";
						j++;
					}
				}
				if(j==0) {//如果有没对照的列,则显示未提供信息.
					colName +="'未提供【"+itemName[i]+"】列对照' `"+itemName[i]+"`,";
				}
				
			}
			colName = colName.substring(0,colName.length()-1);
			st = conn.createStatement();
		String  resultSql = ""
			+colName+" \n"
			+" from  \n"
			+" ( \n"
			+" select  \n"
			+" 	accpackageid,itemname, \n"
			+" 	sum(initadd) as mt_initadd,sum(initMinus) as mt_initMinus, \n"
			+" 	sum(DepreAdd) as mt_DepreAdd,sum(DepreMinus) as mt_DepreMinus, \n"
			+" 	sum(ReservedAdd) as  mt_ReservedAdd,sum(ReservedMinus) as mt_ReservedMinus \n"
			+" from fa_account \n"
			+" where level1 = 1 and accpackageid = '"+accpackageid+"' and submonth<='"+ProjectAuditArea[3]+"' \n"
			+" group by itemname  \n"
			+" )a  \n"
			+" inner join  \n"
			+" ( \n"
			+" select  \n"
			+" 	accpackageid,itemname, \n"
			+" 	initremain as OpenningBalance, \n"
			+" 	Depreremain as OpenningTotalDep, \n"
			+" 	ReservedRemain as OpenningReserved\n"
			+" from fa_account \n"
			+" where level1 = 1 and accpackageid = '"+accpackageid+"' and submonth = '"+ProjectAuditArea[1]+"' \n"
			+"  \n"
			+" )b  \n"
			+" on a.itemname = b.itemname \n"
			+" inner join  \n"
			+" ( \n"
			+" select  \n"
			+" 	accpackageid,itemname, \n"
			+" 	initbalance as mt_initbalance, \n"
			+" 	DepreBalance as mt_DepreBalance, \n"
			+" 	ReservedBalance as mt_ReservedBalance \n"
			+" from fa_account \n"
			+" where level1 = 1 and accpackageid = '"+accpackageid+"' and submonth = '"+ProjectAuditArea[3]+"' \n"
			+"  \n"
			+" )c  \n"
			+" on a.itemname = c.itemname \n";
			
			System.out.println("yzm:3202="+resultSql);	
			//最终查询结果
			st.executeQuery("set   charset   gbk;");
			rs=st.executeQuery(resultSql);
		
			return rs;
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}
	}

}
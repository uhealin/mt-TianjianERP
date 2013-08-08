package com.matech.audit.service.function;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

//import com.matech.framework.pub.util.ASFuntion;

/**
 * 现金流量取数
 * 参数：itemfullname 为现金流量的全路径	
 * 注：itemfullname为可选，不填表示为批量刷新，填表示只取指定的现金流量
 * 
 * 返回：
 * itemid ：现金流量的编号
 * itemname ：现金流量的名称
 * fullname ：现金流量的全路径
 * oribalance ：现金流量的未审数
 * flowin ：现金流量的调整增加
 * flowout ：现金流量的调整减少
 * sdbalance ：现金流量的审定数
 * 
 * 例：
 * =取列公式插入(4000, "1111", "itemname")	//批量刷新
 * =取自定义函数(4000,"oribalance","&itemfullname=经营活动产生的现金流量/经营活动现金流入/销售商品、提供劳务收到的现金")  //指定
 */
public class _4000_0 extends AbstractAreaFunction {

	public ResultSet process(HttpSession session, HttpServletRequest request,
			HttpServletResponse response, Connection conn, Map args)
			throws Exception {
//		ASFuntion asf = new ASFuntion();
//
//		String accpackageid = (String) args.get("curAccPackageID");
//		String projectid = (String) args.get("curProjectid");

		Statement st=null;
		ResultSet rs=null;
		try {
			st = conn.createStatement();
			
			String sql="";
			String name = (String) args.get("itemfullname");
			if(!"".equals(name)){
				name = " and fullname = '"+name+"'";
			}else{
				name = "  ";
			}
			args.put("name",name);
			
			sql = "select * from z_cashflowaccount where 1=1 and projectid = '${curProjectid}' ${name} order by itemid";
			
			sql = this.setSqlArguments(sql, args);
			
//			System.out.println(sql);
			rs = st.executeQuery(sql);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} 
		
		return rs;
	}

}

package com.matech.audit.service.OAexamine;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.OAexamine.model.CustomerExamineTable;
import com.matech.framework.pub.db.DbUtil;

public class ExamineService {
		
	public Connection conn;
	
	public ExamineService(Connection conn) throws Exception {
		DbUtil.checkConn(conn);
		this.conn = conn;
	}
	
	/***
	 * 把考核类别全部计算一次
	 * 
	 * @param ctype 考核的类别
	 * @param useridOrCustomerId 用户或者客户ID
	 * @param vlaue 用来判断是客户还是用户：用户，客户
	 * @return
	 */
	
	public ArrayList Calculate(String ctype,String useridOrCustomerId,String vlaue){

	
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList arrayList = new ArrayList(); 
		int count=0;
		try{
			
			String sql = "select cname,originalsql,ccal,cformula,memo from asdb.oa_examinelibrary where ctype = ? and isenable='有效' and property='定量' order by orderid";
			
			ps = conn.prepareStatement(sql);
			ps.setString(1, ctype);
			
			rs = ps.executeQuery();
			while(rs.next()){
				CustomerExamineTable customerExamineTable=new CustomerExamineTable();
		
				customerExamineTable.setExamineName(rs.getString("cname"));
				customerExamineTable.setObjectiveValue(getResultSqlValue(rs.getString("originalsql"),useridOrCustomerId));
				customerExamineTable.setSystemScore(getResultSqlValue(rs.getString("cformula"),useridOrCustomerId));
				customerExamineTable.setExamineMome(rs.getString("memo"));
						
				arrayList.add(customerExamineTable);
				count++;
			}
			
			rs.close();
			ps.close();
			
			String sql1 = "select cname,originalsql,ccal,cformula,memo from asdb.oa_examinelibrary where ctype = ? and isenable='有效' and property='定性' order by orderid";
			
			ps = conn.prepareStatement(sql1);
			ps.setString(1, ctype);
			
			rs = ps.executeQuery();
			while(rs.next()){
				CustomerExamineTable customerExamineTable=new CustomerExamineTable();
			
				customerExamineTable.setExamineName(rs.getString("cname"));
				customerExamineTable.setObjectiveValue(createHTML(rs.getString("ccal"),rs.getString("cformula"),++count));
				customerExamineTable.setSystemScore("0");
				customerExamineTable.setExamineMome(rs.getString("memo"));
				
				
				arrayList.add(customerExamineTable);
			}
			
			rs.close();
			ps.close();
			
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		
		return arrayList;
	}
	
	/***
	 * 计算单个考核题目的值
	 * 
	 * @param ctype 考核的类别
	 * @param useridOrCustomerId 用户或者客户ID
	 * @param vlaue  用来判断是客户还是用户：用户，客户
	 * @param cname  考核的题目名称
	 * @return
	
	public LookOutPlanTable Calculate(String ctype,String useridOrCustomerId,String vlaue,String cname){

		LookOutPlanTable table = new LookOutPlanTable();
		

		PreparedStatement ps = null;
		ResultSet rs = null;
		try{
			int id = Integer.parseInt(useridOrCustomerId);
			String sql = "select * from asdb.k_lookoutplan where autoid = ?";
			
			ps = conn.prepareStatement(sql);
			ps.setInt(1, id);
			
			rs = ps.executeQuery();
			if(rs.next()){
				table.setAutoid(id);
				table.setTitle(rs.getString("title"));
				table.setMemo(rs.getString("memo"));
				table.setLastmodifydate(rs.getString("lastmodifydate"));
				table.setLookoutids(rs.getString("lookoutids"));
				table.setRundetail(rs.getString("rundetail"));
				table.setRuntype(rs.getString("runtype"));
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		
		return table;
	}
	 */
	
	/***
	 * 替换客户或者用户id
	 * 
	 * @param sql  sql语句
	 * @param useridOrCustomerId  客户或者用户ID
	 * @return
	 */
	public String repalceCustomerId(String sql,String useridOrCustomerId){

		try{
			sql = sql.replaceAll("\\$\\{customerid\\}", " and customerid = '"+useridOrCustomerId+"'");
			sql = sql.replaceAll("\\$\\{departid\\}", " and departid = '"+useridOrCustomerId+"'");
			sql = sql.replaceAll("\\$\\{userid\\}", " and loginid = '"+useridOrCustomerId+"'");
		}catch(Exception e){
			e.printStackTrace();
		}
		return sql;
	}
	
	
	/***
	 * 替换掉参数并得到传入sql的结果集
	 * 
	 * @param sqlValue  传入的sql
	 * @param useridOrCustomerId  客户或者用户的ID
	 * @return
	 */
	public String getResultSqlValue(String sqlValue,String useridOrCustomerId){

		PreparedStatement ps = null;
		ResultSet rs = null;
		String value="";
		try{
			String tempSql = repalceCustomerId(sqlValue,useridOrCustomerId);
			
			rs = new DbUtil(conn).getResultSet(tempSql);
			
			if(rs.next()){
				value = rs.getString(1);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		
		return value;
	}
	
	/***
	 * 构建定量的下拉HTML
	 * 
	 * @param ccal  用户定义的规则
	 * @param cformula  和规则对应的分数值
	 * @return
	 */
	public String createHTML(String ccal,String cformula,int count){

		
		String selectFull="";
		try{
			
		String[] ccals = ccal.split("~");
		String[] cformulas = cformula.split("~");
		
		String selectHead="<select id=\"objvalue\" name=\"objvalue\" onchange=FF1(this,"+count+")> \n"
			+ "	<option selected value='0'>未选</option>\n";
		
	    String selectEnd =  "</select> \n";

	    String selectbody="";
	    
		for(int i=0;i<ccals.length;i++){
			
			selectbody = selectbody+" <option value=\""+cformulas[i]+"\">"+ccals[i]+"</option> \n";
			 		
		}
		
		selectFull = selectHead + selectbody + selectEnd;
	
		
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return selectFull;
	}

	
public static void main(String[] args) throws Exception{
		
		Connection conn = new DBConnect().getConnect("");
		
		ExamineService examineService = new ExamineService(conn);
		
		List list = examineService.Calculate("客户考核","555555","1");
			
		for (Iterator iter = list.iterator(); iter.hasNext();) {
			CustomerExamineTable customerExamineTable = (CustomerExamineTable) iter.next();
			
			System.out.print(customerExamineTable.getExamineName()+"\n");
			System.out.print(customerExamineTable.getObjectiveValue()+"\n");
			System.out.print(customerExamineTable.getSystemScore()+"\n");
			System.out.print(customerExamineTable.getExamineMome()+"\n");
			
		}
	}

}

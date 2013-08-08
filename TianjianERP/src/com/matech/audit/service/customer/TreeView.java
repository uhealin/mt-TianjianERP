package com.matech.audit.service.customer;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.jsp.JspWriter;


import com.matech.framework.pub.db.DBConnect;
import com.matech.framework.pub.util.ASFuntion;

public class TreeView {
	
	ASFuntion CHF = new ASFuntion();
	Connection conn = null;
	PreparedStatement ps = null;
	ResultSet rs = null;
	StringBuffer sb = null;
	String sql = "";
	String slq = "select * from k_customerrelation where customerid=?";
	public TreeView() {
		
	}

	 
	/*
	 * initId����֤�Ŀͻ����
	 * id�ӿͻ����
	 * ʹ�õݹ鷽��4��֤initId�Ƿ�Ϊ���ȣ���Ϊ�����򷵻�true,���򷵻�false
	 * */
	public boolean isForefather(Connection con,String initId,String id){

		try{
			PreparedStatement ps = con.prepareStatement(slq);
			ps.setString(1,id);
			ResultSet rs = ps.executeQuery();
			rs.next();
			if(rs.getString("parentcustomerid").equals(initId)){
				return true;
			}else{
				return isForefather(con,initId,rs.getString("parentcustomerid"));
			}
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	/*
	 * ��bһ���¸��ͻ�֮ǰ�������飬�Ƿ���Ϊ���ͻ����ǣ��򷵻�true,��,�򷵻�false��
	 */
	public boolean isForefather(Connection con,String id){

		try{
			PreparedStatement ps = con.prepareStatement("select count(*) from k_customerrelation where customerid=?");
			ps.setString(1,id);
			ResultSet rs = ps.executeQuery();
			rs.next();
			if(rs.getInt(1)>0){
				return true;
			}else{
				return false;
			}
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	
	public String getParentNode(String id)throws Exception {
		if(id == null)	return "";
		try{

			conn = new DBConnect().getConnect();
			sql = "select departid,departname from k_customer where departid=?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, id);
			rs = ps.executeQuery();
			sb = new StringBuffer("");
			
			if(rs.next()){
				String customerId = rs.getString("departid");
				String customerName = rs.getString("departname");
				

				
				sb.append("<tr style=\"cursor: hand;\" onclick=\"gotoLocation(this,'" + customerId + "')\" selfId=\"" + customerId + "\">");
				sb.append("<td align=\"left\" nowrap >");
				sb.append(customerId);
				sb.append("</td>");
				sb.append("<td align=\"left\" valign=\"bottom\" nowrap>"
						 + "&nbsp;"	+ customerName + "</td>");				
				sb.append("</tr>");				

				
				sql="select a.customerid,b.departname from k_customerrelation a " +
						"inner join k_customer b on a.customerid=b.departid where parentcustomerid=?";
				getParentNodeRecursion(conn,sql,sb,id,id);	

			}
				
			
			return sb.toString();
			
		}catch(Exception e){
			e.printStackTrace();
			return "";
		}finally{
			if (rs != null)	rs.close();
			if (ps != null)	ps.close();
			if (conn != null)conn.close();
		}
	}
	
	
	
	public void getParentNodeRecursion(Connection conn,String sql,StringBuffer sb,String departmentid,String fullPathName) throws Exception{
		
		
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setString(1, departmentid);
		ResultSet rs = ps.executeQuery();
		
		
		while(rs.next()){
			
			String customerId = rs.getString("customerid");
			String customerName = rs.getString("departname");
			
			if(!(fullPathName.indexOf(customerId)>=0)){
				String newFullPathName=new String(fullPathName+"-"+customerId);
				
				sb.append("<tr style=\"cursor: hand;\" onclick=\"gotoLocation(this,'" + customerId + "')\" selfId=\"" + customerId + "\">");
				sb.append("<td align=\"left\" nowrap >");
				sb.append(newFullPathName);
				sb.append("</td>");
				sb.append("<td align=\"left\" valign=\"bottom\" nowrap>"
						 + "&nbsp;"	+ customerName + "</td>");				
				sb.append("</tr>");	
				
				getParentNodeRecursion(conn,sql,sb,customerId,newFullPathName);				
			}
		}
	}

	
	public String getSubTree(String pid) throws Exception {
		


		if (pid == null) {
			return "";
		}
		try {
			//��ȡ���ͻ����
			int begin = pid.lastIndexOf("-") + 1;
			String parentId = pid.substring(begin);
			
			conn = new DBConnect().getConnect();
			//ͨ����j��4��֤�ӿͻ��Ƿ�Ϊ��
			sql = "select * from ( " +
				        //��k_customerrelation���ȡָ���ͻ��������ӿͻ���ż������ 
				        "select departid,parentcustomerid,departname " +
				        "from k_customerrelation t1,k_customer t2 " +
				        "where parentcustomerid='"  + parentId + "'" +
				              "and t2.property=1 " +
				              "and t1.customerid=t2.departid " +
				   ") t1 left join ( " +
				        //��k_customerrelation���ȡ���еĸ��ͻ����
				        "select distinct parentcustomerid as chkparentcustomerid " +
				        "from k_customerrelation " +
				        "where 1=1 " +
				   ") t2 on t1.departid=t2.chkparentcustomerid ";
			
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			sb = new StringBuffer("");
			
			while(rs.next()){
				
				String customerId = rs.getString("departid");
				String parentCustomerId = rs.getString("parentcustomerid");
				String customerName = rs.getString("departname");
				String chkParentCustomerId =rs.getString("chkparentcustomerid");
				String fullPath = pid + "-" + customerId;
				
				

				
				
				sb.append(fullPath+"`o`"+customerName+"^o^");
			}
			
			String result=sb.toString();
			if(result.length()>3){
				result=result.substring(0,result.length()-3);
			}
			return result;
		}catch (Exception e) {
			e.printStackTrace();
			return "";
		} finally {
			if (rs != null)	rs.close();
			if (ps != null)	ps.close();
			if (conn != null)conn.close();
		}
	}
	
	


	public static void main(String[] args) {
		System.out.println("\\|");
	}
}
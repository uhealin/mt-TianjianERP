package com.matech.audit.service.customer;

import java.sql.*;
import java.text.*;
public class connectcompanyExcelData {

	private Connection conn = null;

	public connectcompanyExcelData(Connection conn1) 
			throws Exception {
		this.conn = conn1;
	}
/**
 * 创建临时表
 * @throws Exception
 */
	public void newTable() throws Exception {
		delTable();
		String sql = "CREATE TABLE `tt_k_connectcompanys` (`connectcompanysname` varchar(30) default NULL) ENGINE=MyISAM DEFAULT CHARSET=gbk";
		
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(sql);
			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("�½��û���ʱ���SQLִ��ʧ��" + e.getMessage(), e);
		} finally {
			if (ps != null)
				ps.close();
		}

	}
/**
 * 删除临时表
 * @throws Exception
 */
	public void delTable() throws Exception {
		String sql = "DROP TABLE IF EXISTS `tt_k_connectcompanys`";
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(sql);
			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("ZYQSQLִ��ʧ��" + e.getMessage(), e);
		} finally {
			if (ps != null)
				ps.close();
		}

	}
/**
 * 将临时表中的数据插入到指定的数据表中
 * @param costomerid
 * @throws Exception
 */
	public void insertData(String costomerid)throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "";
		try {
			sql = "insert into k_connectcompanys(customerid,connectcompanysname) "
				+	"select distinct '"+costomerid+"', a.connectcompanysname "
				+" from tt_k_connectcompanys a "
				+" where a.connectcompanysname  not in "
				+" ( "
				+"		select b.connectcompanysname from  k_connectcompanys b " 
				+"		 where  b.customerid = '"+costomerid+"' "
				+" ) and  a.connectcompanysname !='' ";
			ps = conn.prepareStatement(sql);
			ps.execute();
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			//使用完后无条件删除临时表
			delTable();
			if( rs != null)
				rs.close();
			if (ps != null)
				ps.close();
		}
	}

	public static void main(String[] args) {
		long b=(long)33946.0;
		Date tt = new Date(b);
		Format formatter = new SimpleDateFormat("yyyy-MM-dd");
		System.out.println("date:"+tt);
		System.out.println("date:"+formatter.format(tt));
	}
}


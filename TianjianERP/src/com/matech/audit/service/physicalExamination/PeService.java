package com.matech.audit.service.physicalExamination;

import java.sql.Connection;
import java.sql.DriverManager;

import com.matech.framework.pub.db.DbUtil;
import com.mysql.jdbc.Driver;

public class PeService {

	Connection conn;
	
	public PeService(Connection conn){
		this.conn=conn;
	}
	
	public static void main(String[] args){
		try {
			Class.forName(Driver.class.getName());
			Connection conn=DriverManager.getConnection("jdbc:mysql://192.168.1.203:5188/asdb?autoReconnect=true&amp;useUnicode=true&amp;characterEncoding=utf-8", "xoops_root", "654321");
			DbUtil dbUtil=new DbUtil(conn);
			String context=dbUtil.tableRefClassConext("pe_appointment");
			System.err.println(context);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}

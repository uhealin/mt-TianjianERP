package com.matech.framework.pub.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Scanner;

import com.mysql.jdbc.Driver;

public class GenTableApp {

	
	public static void main(String[] args){

		try {
			Class.forName(Driver.class.getName());
		//matech-sd2.eicp.net 172.19.7.121172.19.7.121 172.19.7.123
			String connStr="jdbc:mysql://localhost:5188/asdb?autoReconnect=true&amp;useUnicode=true&amp;characterEncoding=utf-8";
			Connection conn=DriverManager.getConnection(connStr,"xoops_root","654321");
			DbUtil dbUtil=new DbUtil(conn);
			//String context=dbUtil.tableRefClassConext("oa_doc_post");
			
			//PreparedStatement ps=conn.prepareStatement("select * from oa_doc_sign");
			//ResultSet rs=ps.executeQuery();
			//DocRecVO rec=dbUtil.load(DocRecVO.class, "451f4b2d-cdd3-43ca-8882-6ba625e1e6bb");
			//rec.setFile_name("鬼爷");
			//int i=dbUtil.update(rec);
			//rec.setUuid(UUID.randomUUID().toString());
			//i+=dbUtil.insert(rec);
			//i+=dbUtil.delete(rec);
			System.out.println("当前连接:"+connStr);
			System.out.println("请输入表名：");
			Scanner scanner=new Scanner(System.in);
			System.out.println(dbUtil.tableRefClassConext(scanner.next()));
			//System.out.println(i);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

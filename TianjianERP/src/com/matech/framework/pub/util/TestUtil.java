package com.matech.framework.pub.util;

import java.sql.Connection;
import java.sql.DriverManager;

import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.doc.model.DocPostVO;
import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.db.DbUtil;

public class TestUtil {

	protected  Connection conn;
	protected  UserSession userSession;
	protected  DbUtil dbUtil;
	protected  Connection conn1;
	public TestUtil() {
		try{
			//matech-sd2.eicp.net 192.168.1.9 172.19.7.123
			//conn=DriverManager.getConnection("jdbc:mysql://172.19.7.123:5188/asdb?autoReconnect=true&amp;useUnicode=true&amp;characterEncoding=utf-8","xoops_root","654321");
            //conn=new DBConnect().getConnect();
		    //conn=DriverManager.getConnection("jdbc:mysql://localhost:5188/asdb?autoReconnect=true&amp;useUnicode=true&amp;characterEncoding=utf-8","xoops_root","654321");
		    //conn.setAutoCommit(false); 
			String str="jdbc:mysql://localhost:5188/asdb?autoReconnect=true&useUnicode=true&characterEncoding=utf-8";
		    conn=DriverManager.getConnection(str,"xoops_root","654321");
		    System.err.println("当前连接:"+str);
		    //conn=new DBConnect().getConnect();
			dbUtil=new DbUtil(conn);
		    userSession=new UserSession();
		    userSession.setUserId("9802");
		    userSession.setUserName("林海晖");
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
}

package com.matech.audit.service.function;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class _1102_0 extends AbstractAreaFunction {


	public ResultSet process(HttpSession session, HttpServletRequest request, HttpServletResponse response, Connection conn, Map args) throws Exception {
		
		
//		&subjectname=Ӧ���ʿ�&starmonth=1&endmonth=1&ptype=1
		
		//ASFuntion asf=new ASFuntion();
		
		//String apkID=(String)args.get("curAccPackageID");
		//String prjID=(String)args.get("curProjectid");
		String taskCode=(String)args.get("taskCode");
		String sheetName=(String)args.get("sheetName");
		
		String sql="";
		
		Statement st=null;
		ResultSet rs=null;
		

		try{
			
			st=conn.createStatement();
			
			sql= " SELECT rowindex FROM asdb.k_reportconfig \n";
			sql+=" where reporttype='"+sheetName+"' \n";
			sql+=" order by rowindex  \n";
			
			rs=st.executeQuery(sql);
			
			if(rs.next()){
				sql="select * from "
				   +" ( select  '=getcellvalue(\""+taskCode+"\",\""+sheetName+"\",\"B"+rs.getString("rowIndex")+"\")' as getCell ,0 as orderid \n";
			}
			for(int i=1;rs.next();i++){
				sql+=" union \n"
				    +" select  '=getcellvalue(\""+taskCode+"\",\""+sheetName+"\",\"B"+rs.getString("rowIndex")+"\")' as getCell ,"+i+" as orderid \n";
			}
			
			sql+=") a order by orderid ";

			rs=st.executeQuery(sql);
			return rs;
		}catch(Exception e){
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}
	
	
}

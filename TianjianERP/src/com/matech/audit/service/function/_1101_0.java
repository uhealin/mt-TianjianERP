package com.matech.audit.service.function;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.matech.framework.pub.util.ASFuntion;

public class _1101_0 extends AbstractAreaFunction {


	public ResultSet process(HttpSession session, HttpServletRequest request, HttpServletResponse response, Connection conn, Map args) throws Exception {
		

//		=取行公式覆盖(1101,"s","qh","")
		
		ASFuntion asf=new ASFuntion();
		
		String apkID=(String)args.get("curAccPackageID");
		String prjID=(String)args.get("curProjectid");
		String curTaskCode=(String)args.get("curTaskCode");
		String sheetName=(String)args.get("sheetName");
		String sql="";
		
		PreparedStatement ps=null;
		Statement st=null;
		ResultSet rs=null;
		try{
			
			st=conn.createStatement();
			
			String taskID="";
			sql="select taskID from z_task where projectid=? and taskcode=?";
			ps=conn.prepareStatement(sql);
			ps.setString(1, prjID);
			ps.setString(2, curTaskCode);
			
			rs=ps.executeQuery();
			
			if(rs.next()){
				taskID=rs.getString("taskid");
			}else{
				throw new Exception("找不到taskCode［"+curTaskCode+"］projectid［"+prjID+"］的taskid");
			}
			
			
			
			String parenttaskid="";
			sql="select parenttaskid from z_task where projectid=? and taskid=?";
			ps=conn.prepareStatement(sql);
			ps.setString(1, prjID);
			ps.setString(2, taskID);
			
			rs=ps.executeQuery();
//			当前底稿的上级底稿
			if(rs.next()){
				parenttaskid=rs.getString("parenttaskid");
			}else{
				throw new Exception("找不到taskid［"+taskID+"］projectid［"+prjID+"］的上级");
			}
			
			ps.setString(1, prjID);
			ps.setString(2, parenttaskid);
			
			rs=ps.executeQuery();
//			当前底稿的上上级底稿
			if(rs.next()){
				parenttaskid=rs.getString("parenttaskid");
			}else{
				throw new Exception("找不到taskid［"+parenttaskid+"］projectid［"+prjID+"］的上级");
			}
			
//			当前底稿的对应参加报表结点
			String joinConpany="";
			sql="select taskid from z_task where projectid="+prjID+" and parenttaskid="+parenttaskid+" and property='18'";
			
			rs=st.executeQuery(sql);
			if(rs.next()){
				joinConpany=rs.getString("taskid");
			}else{
				throw new Exception("找不到taskid［"+taskID+"］projectid［"+prjID+"］的对应的参加公司结点");
			}
			 
//			当前底稿的对应参加报表结点的参加报表。
			sql=" select concat( '=取合并公式(1102, \"',REPLACE(taskName,'.xls',''),'\", \"getCell\", \"&taskCode=',taskCode,'&sheetName="+sheetName+"\",,,true)' ) as qh \n"
			+"from (\n"
			+"  select * from z_task where projectid="+prjID+" and parenttaskid="+joinConpany+" and isleaf=1 and property=15 \n" 
			+"  union  \n" 
			+"  select b.* from  \n" 
			+"  (  \n" 
			+"  	select b.taskid,b.projectid from   \n" 
			+"  	(  \n" 
			+"  		select taskid,projectid from z_task where projectid="+prjID+" and parenttaskid="+joinConpany+" and isleaf=0  \n" 
			+"  	) a  \n" 
			+"  	inner join   \n" 
			+"  	z_task b  \n" 
			+"  	on a.projectid=b.projectid and b.parenttaskid=a.taskid  \n" 
			+"  	where b.property='16'   \n" 
			+"  ) a  \n" 
			+"  inner join  \n" 
			+"  z_task b  \n" 
			+"  on a.projectid=b.projectid and b.parenttaskid=a.taskid  \n" 
			+"  where b.property='15'  \n" 
			+") a left join \n"
			+"  asdb.k_customerrelation b \n"
			+"  on a.description = b.autoid  \n"
			+"  order by substring(b.property,1,1) ,b.customerid,b.nodename \n"
			   ;
			rs=st.executeQuery(sql);
			return rs;
		}catch(Exception e){
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}
	
	
}


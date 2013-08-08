package com.matech.audit.service.checkInfo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.matech.framework.pub.util.ASFuntion;
import com.matech.framework.pub.db.DbUtil;

public class SVS {
	private Connection conn;
    
	public SVS(Connection conn) throws Exception {
		DbUtil.checkConn(conn);
		this.conn = conn;
	}
	
	
	  public String getKeysHTML()throws Exception{
		   ASFuntion asf = new ASFuntion();
		   DbUtil.checkConn(conn);
		   PreparedStatement ps = null;
		   ResultSet rs=null;
		   String keys="";

		   try {
		    
		     String str;
		     str = "select name,Autoid from k_dic where `ctype`='SVS'";
		     ps = conn.prepareStatement(str);
		     rs=ps.executeQuery();
		     keys="<table id=\"RVSKeys\" name=\"RVSKeys\"><tr>";
		     for(int i=0;rs.next();i++){
		       keys=keys+"<td><input type='checkbox' name='cbx' value='"+rs.getString("name")+"'>"+rs.getString("name")+"<img style=\"CURSOR: hand\" onclick=\"goDel('"+rs.getString("Autoid")+"');\" alt=\"按此删除\" src=\"../images/close.gif\"></td>";
		       if((i+1)%3==0)keys=keys+"</tr><tr>";
		     }
		     keys=keys+"</tr></table>";

		   }
		   catch (Exception e) { e.printStackTrace(); }
		   finally {

		      try {
		        if (rs != null)
		          rs.close();
		        if (ps != null)
		          ps.close();
		      
		      }
		      catch (SQLException ex) {ex.printStackTrace(); }
		  }

		    return keys;
		 }

		 /*
		  *input:args1,args2,args3,...
		  *operation:
		  *insert into k_dic set name=args1,`ctype`= 'SVS'
		  *insert into k_dic set name=args2,`ctype`= 'SVS'
		  *insert into k_dic set name=args3,`ctype`= 'SVS'
		  *...
		  */
		 public void insertKeys(String args)throws Exception{

			 DbUtil.checkConn(conn);
		  PreparedStatement psQuery = null;
		  PreparedStatement psInsert = null;
		  String[] keys=args.split(",");
		  try {
		
		    String strQuery;
		    String strInsert;
		    strQuery = " select name from k_dic where `ctype`='SVS' and name=? ";
		    strInsert = " insert into k_dic set name=?,`ctype`= 'SVS' ";
		    psQuery = conn.prepareStatement(strQuery);
		    psInsert = conn.prepareStatement(strInsert);

		    for(int i=0;i<keys.length;i++){
		      psQuery.setString(1,keys[i]);
		      if(!keys[i].equals("") &&!psQuery.executeQuery().next()){
		        psInsert.setString(1,keys[i]);
		        psInsert.addBatch();
		      }
		    }
		    psInsert.executeBatch();

		  }catch (Exception e) { e.printStackTrace(); }
		  finally {

		     try {
		       if (psQuery != null)
		         psQuery.close();
		       if (psInsert != null)
		         psInsert.close();
		    
		     }
		     catch (SQLException ex) {ex.printStackTrace(); }
		  }
		 }


		 /*
		  * operation:
		  * delete from k_dic where `ctype`='RVS'
		  */
		 public void deleteKeys(String autoid)throws Exception{

			 DbUtil.checkConn(conn);
		  PreparedStatement ps = null;
		  try {
		  
		    String strQuery;
		    strQuery = " delete from k_dic where `ctype`='SVS' and autoid="+autoid;
		    ps = conn.prepareStatement(strQuery);
		    ps.execute();

		  }catch (Exception e) { e.printStackTrace(); }
		  finally {

		     try {
		       if (ps != null)
		         ps.close();

		     }
		     catch (SQLException ex) {ex.printStackTrace(); }
		  }
		 }
		 
	
	
	
}

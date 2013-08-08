package com.matech.audit.work.subjectentry;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;



import com.matech.audit.pub.db.DBConnect;

import com.matech.framework.pub.datagrid.DataGridFieldProcess;
import com.matech.framework.pub.datagrid.DataGridProperty;
import com.matech.framework.pub.db.DbUtil;

import com.matech.framework.pub.util.ASFuntion;
import com.matech.framework.pub.util.UTILString;

public class BatchValueProcess extends DataGridFieldProcess { 

	public String fieldProcess(DataGridProperty pp, int rowIndex, int colIndex,
			int length, ResultSet rs, String value) throws Exception {
		String subjectIDs = rs.getString("subjects");

//		PreparedStatement ps = null;
//	    ResultSet rs1 = null;
//	    Connection conn = null ;

//	    String[] subjectIDsArr = subjectIDs.split("<br>") ;
	   	    
	    String subjectNames = "" ;
	    try {
	    	ASFuntion CHF=new ASFuntion();
	    	subjectNames =CHF.showNull( rs.getString("subjectnames"));
	    	
	    	subjectNames = CHF.replaceStr(subjectNames, ",", "<br>");
	    	
//	    	yzm出错
	    	
//	    	String acc = rs.getString("acc");
//	    	if(subjectIDs!=null&&!"".equals(subjectIDs)){
//	    		
//	    		String departid = acc.substring(0,6);
//	    		conn = new DBConnect().getConnect(departid);
//	    
//	    		for(int i=0;i<subjectIDsArr.length;i++) {
//
//	    			String sql = "select SubjectName from c_accpkgsubject where SubjectID = '" +subjectIDsArr[i] + "'";
//	    			ps = conn.prepareStatement(sql);
//	    			rs1 = ps.executeQuery();
//		        
//	    			if(rs1.next()) {
//	    				subjectNames += rs1.getString("SubjectName") + "<br>" ;
//	    			}
//	    		}
//	    		subjectNames = UTILString.killEndToken(subjectNames, "<br>") ;
//	    	}
	 
		StringBuffer sb = new StringBuffer();

		sb.append("<td style=\"\" ononselectstart=\"return false\" onmouseover=\"onMouseOverA('"+subjectIDs+"');\" onmouseout=\"onMouseOutf();\" onmousemove=\"return selectMultiCell(event);\" onmousedown=\"return selectCell(event)\"> "
					+ subjectNames + "</td>");
		return sb.toString();
	    } catch (Exception e) {
			e.printStackTrace();
		} finally {
//			DbUtil.close(rs1);
//			DbUtil.close(ps);
//			DbUtil.close(conn) ;
		}
		return "";
	}



}

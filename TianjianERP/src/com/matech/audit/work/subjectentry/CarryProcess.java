package com.matech.audit.work.subjectentry;

import java.sql.ResultSet;

import com.matech.framework.pub.datagrid.DataGridFieldProcess;
import com.matech.framework.pub.datagrid.DataGridProperty;
import com.matech.framework.pub.util.ASFuntion;

public class CarryProcess extends DataGridFieldProcess { 

	public String fieldProcess(DataGridProperty pp, int rowIndex, int colIndex,
			int length, ResultSet rs, String value) throws Exception {
		String property = rs.getString("property");
	    try {
	    	ASFuntion CHF=new ASFuntion();
	    	
	    	if(property != null && property.indexOf("2")==-1) {
	    		//非结转凭证
	    		property = "" ;
	    	}
	    	
	    	String sb = CHF.showCarry(property,value); //结字
	    	return sb;
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

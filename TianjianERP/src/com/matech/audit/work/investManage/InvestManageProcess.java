package com.matech.audit.work.investManage;

import java.sql.ResultSet;

import com.matech.framework.pub.datagrid.DataGridProperty;

public class InvestManageProcess extends com.matech.framework.pub.datagrid.DataGridFieldProcess{
	
	public String fieldProcess(DataGridProperty pp, int rowIndex, int colIndex,
			int length, ResultSet rs, String value) throws Exception {
		String result = "";
		if("3违规".equals(value)){
			result = "<td>" 
				   + "<span ><font color='red'>不合规格！</font></span>" 
				   + "</td>";
		}else{
			result = "<td>" 
				   + "<span >合规格</span>" 
				   + "</td>";
		}
		return result;
	}
}

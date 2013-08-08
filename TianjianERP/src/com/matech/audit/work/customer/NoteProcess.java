package com.matech.audit.work.customer;

import java.sql.ResultSet;
import com.matech.framework.pub.datagrid.DataGridFieldProcess;
import com.matech.framework.pub.datagrid.DataGridProperty;

public class NoteProcess extends DataGridFieldProcess{

	public String fieldProcess(DataGridProperty pp, int rowIndex, int colIndex, int length, ResultSet rs, String value) throws Exception {
		
		String filename = rs.getString("filename");
		String filepath = rs.getString("filepath");
	
		StringBuffer sb = new StringBuffer();

		sb.append("<td align=\"center\" valign=\"middle\">");
		sb.append("<a href=\"#\" onclick=\"openFile('"+filepath+"','"+filename+"')\">"+filename+"</a>");
		sb.append("</td>");

		return sb.toString();
	}
}

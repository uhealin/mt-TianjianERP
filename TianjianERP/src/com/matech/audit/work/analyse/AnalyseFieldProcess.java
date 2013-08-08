package com.matech.audit.work.analyse;

import java.sql.ResultSet;
import com.matech.framework.pub.datagrid.DataGridProperty;
import com.matech.framework.pub.util.ASFuntion;

public class AnalyseFieldProcess extends com.matech.framework.pub.datagrid.DataGridFieldProcess {
	public String fieldProcess(DataGridProperty pp, int rowIndex, int colIndex,
			int length, ResultSet rs, String value) throws Exception {
		ASFuntion CHF=new ASFuntion();
		String result = "";
		result = "<td align='center'>" +
		"<input name='subjects' type='hidden' id='subjects' value='"+rs.getString("subjectID")+"'>" +
		
		"<select id=\"creditPeriod\"  name=\"creditPeriod\">" +
		"<option value=''>请选择</option>" ;
		for(int i=1;i<=12;i++){
			if("".equals(value)){
				result += "<option value='"+i+"'>"+i+"个月</option>";	
			}else{
				if(i == Integer.parseInt(value)){
					result += "<option value='"+i+"' selected >"+i+"个月</option>";	
				}else{
					result += "<option value='"+i+"'>"+i+"个月</option>";		
				}
			}
			
			
		}
		result += "</select>" +
//		"<input type=\"text\" name=\"creditPeriod\"  id=\"creditPeriod\" value='"+CHF.showNull(value)+"' onkeydown=\"onKeyDownEvent();\" onkeyup=\"onKeyUpEvent();\" onclick=\"onPopDivClick(this);\"   valuemustexist=true hideresult='true' noinput=\"true\" autoid=24   >月" +
		"</td>";
		return result;
		
	}
}

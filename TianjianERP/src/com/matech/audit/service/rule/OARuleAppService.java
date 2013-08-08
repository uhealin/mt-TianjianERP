package com.matech.audit.service.rule;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import com.matech.audit.pub.db.DBConnect;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.exception.MatechException;
import com.matech.framework.pub.util.ASFuntion;
import com.matech.framework.pub.util.Debug;
import com.matech.framework.pub.util.UTILString;
import com.matech.rule.Project;
import com.matech.rule.ProjectUtil;
import com.matech.rule.RulePO;

public class OARuleAppService {

	
	public OARuleAppService() {
		
	}
/***
 * 
 * @param expression  要计算的表达式
 * @param map         表达式中${}之间的值
 * @return
 * @throws Exception
 */
	public String getRulePo(String XML) throws Exception {
		
		ASFuntion CHF=new ASFuntion();
		
		String expression = CHF.getXMLData(XML, "exp","0");//得到表达式
	
		String strRule="";
		
		try {
			 
			String tempExpression =expression.replaceAll("\\$\\{", "").replaceAll("\\}", "");
			
			String t1[] = UTILString.getVaribles(expression);//得到每个变量名
			
			strRule = strRule+"输出(\"结果\"); \n";
			
			for (int i = 0; i < t1.length; i++) {
			
				t1[i] = t1[i].replaceAll("\\$\\{", "").replaceAll("\\}", "");
				strRule = strRule+""+t1[i]+"="+CHF.getXMLData(XML, t1[i],"0")+"; \n";
				
			}
			strRule += "结果="+tempExpression+";";
			
//		System.out.println("yzmn:strRule="+strRule);
		RulePO rpo=new RulePO();
		rpo.setId("1");		
		rpo.setRule(strRule);
		rpo.setName("内部临时规则");
		
		Project project = ProjectUtil.runRuleByPO(rpo, new HashMap());
		Map outputs = project.getOutputs();

		return outputs.get("结果")+"";
		

		} catch (Exception e) {
			Debug.print(Debug.iError, "返回规则失败！", e);
			throw new MatechException("返回规则失败！" + e.getMessage(), e);
		} 
	}
	
	
	public static void main(String[] args) throws Exception{

		OARuleAppService ruleAppService = new OARuleAppService();
		
		String xml = "<exp>${val1}+${val2}*${val3}-${val1}</exp>"
					+"	<val1>21</val1> "
					+"	<val2>33</val2> "
					+"	<val3>52</val3> ";
		
		System.out.println(ruleAppService.getRulePo(xml));
	}
}

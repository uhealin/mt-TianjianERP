package com.matech.audit.service.form.buttonImpl;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.matech.audit.service.form.FormButtonExtInterface;
import com.matech.audit.service.form.model.FormButton;
import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.log.Log;
import com.matech.framework.pub.util.StringUtil;
import com.matech.framework.pub.util.WebUtil;

public class ButtonSqlImpl implements FormButtonExtInterface {
	
	Log log = new Log(ButtonSqlImpl.class); 

	public String handle(Connection conn, FormButton formButton, HttpServletRequest request,HttpServletResponse response) throws Exception {
		
		try {
			if(formButton == null || "".equals(formButton)) {
				throw new Exception("按钮对象为空!!");
			}
			
			String sql = formButton.getSql();
			
			if(sql == null || "".equals(sql)) {
				throw new Exception("SQL为空!!");
			}
			
			//sql = sql.toLowerCase();
			
			log.debug("按钮接口SQL:" + sql);
			
			String[] varibles = StringUtil.getVaribles(sql);
			String value = "";
			String key = "";
			WebUtil webUtil=new WebUtil(request, response);
			UserSession userSession=webUtil.getUserSession();
			for (int i = 0; i < varibles.length; i++) {
				key = varibles[i];
				
				if(key.startsWith("userSession.")){
				   String skey=key.replace("userSession.", "");
				   String methodName="get"+skey.substring(0,1).toUpperCase()+skey.substring(1);
				   Method method=UserSession.class.getDeclaredMethod(methodName);
				   Object val=method.invoke(userSession);
				   if(val==null)continue;
				   value=(String)val;
				}else{
				key = varibles[i].toLowerCase();
				value = request.getParameter(key);
				
				value = value.replaceAll(",", "','");
				
				System.out.println(key + ":" + value);
				}
				sql = sql.replaceAll("\\$\\{" + key + "\\}", value);
			}
			
			log.debug("按钮接口SQL_替换后:" + sql);
			
			String[] sqls = sql.split(";");
			
			DbUtil dbUtil = new DbUtil(conn);
			
			for (int i = 0; i < sqls.length; i++) {
				if(sqls[i] != null && !"".equals(sqls[i].trim())) { 
					System.out.println("SQL：" + sqls[i].trim());
					dbUtil.execute(sqls[i].trim());
				}
			}
			return "ok";
			
		} catch (Exception e) {
			e.printStackTrace();
			log.exception("调用按钮SQL接口出错", e);
			throw e;
		} 
	}

}

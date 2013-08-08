package com.matech.audit.service.form.impl.form;

import java.sql.Connection;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tools.ant.taskdefs.condition.Http;
import org.springframework.web.servlet.ModelAndView;

import com.matech.audit.service.form.FormExtInterface;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.StringUtil;



public class DemoImpl implements FormExtInterface {

	private static void print(HttpServletRequest req ){
		Map map=req.getParameterMap();
        for(Iterator<String> it=map.keySet().iterator();it.hasNext();){
        	String key=it.next();
        	String value=req.getParameter(key);
        	System.out.println(MessageFormat.format("key:{0},value:{1}",key,value));
        }
	}
	
	
	public String beforeAdd(Connection conn,String formId, HttpServletRequest req,HttpServletResponse res) throws Exception {
		System.out.println("beforeAdd");
        print(req);
		return null;  
	}
	


	public String afterAdd(Connection conn,String formId, String dataUUID, HttpServletRequest req,HttpServletResponse res) throws Exception {
	
		System.out.println("afterAdd");
        print(req);
		return null;
	}

	public String beforeUpdate(Connection conn,String formId, String dataUUID, HttpServletRequest req,HttpServletResponse res) throws Exception {
		System.out.println("beforeUpdate1111");
        print(req);
        System.out.println("beforeUpdate2222");
		return null;
	}

	public String afterUpdate(Connection conn,String formId, String dataUUID, HttpServletRequest req,HttpServletResponse res) throws Exception {
		System.out.println("afterUpdate");
        print(req);
		return null;
	}

	public String beforeDelete(Connection conn,String formId, String dataUUID, HttpServletRequest req,HttpServletResponse res) throws Exception {
		System.out.println("beforeDelete");
        print(req);
		return null;
	}

	public String afterDelete(Connection conn,String formId, String dataUUID, HttpServletRequest req,HttpServletResponse res) throws Exception {
		System.out.println("afterDelete");
        print(req);
		return null;
	}


	@Override
	public void beforeView(Connection conn, String formId, String dataUUID,
			HttpServletRequest req, HttpServletResponse res,
			ModelAndView modelAndView) {
		// TODO Auto-generated method stub
		
	}


	



	

}

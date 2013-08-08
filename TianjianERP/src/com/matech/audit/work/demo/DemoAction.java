package com.matech.audit.work.demo;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.matech.audit.pub.datagrid.DataGrid;
import com.matech.audit.pub.datagrid.DataGridProperty;
import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.inbox.InboxService;
import com.matech.audit.service.morality.MoralityService;
import com.matech.audit.service.morality.model.Morality;
import com.matech.audit.service.userpopedom.UserPopedomService;
import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.autocode.DELUnid;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;
import com.matech.framework.pub.util.StringUtil;

public class DemoAction extends MultiActionController {
	
	private static final String list =  "demo/list.jsp"; 

	//声明：年度独立、项目独立
	public ModelAndView list(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView(list);
		ASFuntion CHF = new ASFuntion();
		
		System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
		
		DataGridProperty pp = new DataGridProperty();

		pp.setTableID("demolist");
		//pp.setInputType("radio");
		//pp.setWhichFieldIsValue(1);

		pp.setSQL("select * from k_user where 1=1 ${username} ");
		
		
		pp.setOrderBy_CH("loginid,name");
		pp.setDirection("desc,desc");
		
		//pp.setTrActionProperty(true);
		//pp.setTrAction("style=\"cursor:hand;\" onDBLclick=\"goSort('${id}','${name}');\" ");

		pp.addColumn("人员姓名", "name");
		pp.addColumn("登录ID", "loginid");
		pp.addColumn("密码", "password");
		
		pp.addSqlWhere("username", " and loginid= '${username}'");
		//pp.addSqlWhere("loginid", " and loginid like '%${loginid}%'");
	
		request.getSession().setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);
		
		
		return modelAndView;
	}


}

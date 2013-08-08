package com.matech.audit.work.customer;

import java.io.IOException;
import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.matech.audit.pub.datagrid.DataGrid;
import com.matech.audit.pub.datagrid.DataGridProperty;
import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.customer.CustomerService;
import com.matech.audit.service.customer.FollowService;
import com.matech.audit.service.customer.model.Business;
import com.matech.audit.service.customer.model.Follow;
import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;

public class FollowAction extends MultiActionController {
	 private final String list ="follow/List.jsp";
	 private final String andAndEdit ="follow/AddandEdit.jsp";
	
	 public ModelAndView list(HttpServletRequest request, HttpServletResponse response) throws Exception{
		 ModelAndView modelAndView = new ModelAndView(list);
		 DataGridProperty pp = new DataGridProperty();
	   
		 UserSession userSession = (UserSession) request.getSession().getAttribute("userSession"); 
	     Connection conn = new DBConnect().getConnect("");
		
		String bcRoleString =new ASFuntion().showNull(new DbUtil(conn).queryForString("SELECT a.rid FROM k_userrole a LEFT JOIN k_role b ON a.`rid` = b.`id` WHERE a.userid='"+userSession.getUserId()+"' AND b.`rolename` = '市场部负责人'"));
		if(!"".equals(bcRoleString)){
			bcRoleString = " OR 1=1 ";
		}
		DbUtil.close(conn);
		 
		 String sql= "SELECT a.autoId,b.customerName as customer,a.linkpeople,a.followtime,a.followcontent,a.followstatus,a.nexttime,a.nextcontent,a.nextstatus,c.name AS disman," 
			 		+" d.`Name` AS followman FROM k_follow a "
			 		+" LEFT JOIN oa_business b ON a.customer =b.CustomerID" 
			 		+" LEFT JOIN k_user c ON a.`disman` = c.`id`"
			 		+" LEFT JOIN k_user d ON a.`followman` = d.`id`" 
			 		+" where 1=1 and (a.disman="+userSession.getUserId()+" or a.followman = "+userSession.getUserId()+" or '19' ='"+userSession.getUserId()+"' "+bcRoleString+" ) ${customer} ${linkpeople} ${followtime} ${followstatus} ${nexttime} ${nextstatus} ${disman} ${followman}";
		 	pp.setTableID("follow");
			pp.setPageSize_CH(20);
			pp.setCustomerId("");
			pp.setWhichFieldIsValue(1);
			pp.setInputType("radio");
			pp.setOrderBy_CH("autoId");
			pp.setDirection("asc");
			pp.setPrintEnable(true);
		    pp.setPrintVerTical(false);
		    
		    pp.setSQL(sql);
		    pp.addColumn("客户名称", "customer");
		    pp.addColumn("联系人", "linkpeople");
		    pp.addColumn("本次跟进时间", "followtime");
		    pp.addColumn("本次跟进内容", "followcontent");
		    pp.addColumn("本次跟进状态", "followstatus");
		    pp.addColumn("下次跟进时间", "nexttime");
		    pp.addColumn("下次跟进内容", "nextcontent");
		    pp.addColumn("下次跟进状态", "nextstatus");
		    pp.addColumn("分配人", "disman");
		    pp.addColumn("跟进人", "followman");
		    
		    pp.addSqlWhere("customer", " and customer = '${customer}'");
		    pp.addSqlWhere("linkpeople", " and linkpeople like '%${linkpeople}%'");
		    pp.addSqlWhere("followtime", " and followtime = '${followtime}'");
		    pp.addSqlWhere("followstatus", " and followstatus = '${followstatus}'");
		    pp.addSqlWhere("nexttime"," and nexttime = '${nexttime}'");
		    pp.addSqlWhere("nextstatus", " and nextstatus = '${nextstatus}'");
		    pp.addSqlWhere("disman", " and disman = '${disman}'");
		    pp.addSqlWhere("followman", " and followman = ${followman}");
		    
		    request.getSession().setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);
		    return modelAndView;
	 }
	 public ModelAndView edit(HttpServletRequest request, HttpServletResponse response) throws Exception{
		 ModelAndView modelAndView = new ModelAndView(andAndEdit);
		 ASFuntion asf = new ASFuntion();
		 String autoId = asf.showNull(request.getParameter("id"));
		 Connection conn = null;
		 Follow follow = null;
		 try{
			 conn = new DBConnect().getConnect("");
			 FollowService followService = new FollowService(conn);
			 if(!autoId.equals("")){
				 follow = followService.getFollow(autoId);
			 }
			 modelAndView.addObject("follow", follow);
		 }catch(Exception e){
			 e.printStackTrace();
		 }finally{
			 DbUtil.close(conn);
		 }
		 return modelAndView;
	 }
	 
	 public ModelAndView add(HttpServletRequest request, HttpServletResponse response) throws IOException{
		 ModelAndView modelAndView = new ModelAndView(list);
		 UserSession userSession = (UserSession) request.getSession().getAttribute("userSession"); 
		 ASFuntion asf = new ASFuntion();
		 String customer = asf.showNull(request.getParameter("customer"));
		 String linkpeople = asf.showNull(request.getParameter("linkpeople"));
		 String followtime = asf.showNull(request.getParameter("followtime"));
		 String followcontent =asf.showNull(request.getParameter("followcontent"));
		 String followstatus = asf.showNull(request.getParameter("followstatus"));
		 String nexttime = asf.showNull(request.getParameter("nexttime"));
		 String nextcontent = asf.showNull(request.getParameter("nextcontent"));
		 String nextstatus = asf.showNull(request.getParameter("nextstatus"));
		 String disman = asf.showNull(request.getParameter("disman"));
		 String followman = asf.showNull(request.getParameter("followman"));
		 String createtTime = asf.getCurrentDate()+" "+asf.getCurrentTime();
		 String createDepartment = userSession.getUserAuditDepartmentId();
		 String createUser = userSession.getUserId();
		 
		 Follow follow = new Follow();
		 follow.setCustomer(customer);
		 follow.setLinkpeople(linkpeople);
		 follow.setFollowtime(followtime);
		 follow.setFollowcontent(followcontent);
		 follow.setFollowstatus(followstatus);
		 follow.setNexttime(nexttime);
		 follow.setNextcontent(nextcontent);
		 follow.setNextstatus(nextstatus);
		 follow.setDisman(disman);
		 follow.setFollowman(followman);
		 follow.setCreateUser(createUser);
		 follow.setCreateDepartment(createDepartment);
		 follow.setCreateTime(createtTime);
		 Connection conn = null;
		 try{
			 conn = new DBConnect().getConnect("");
			 FollowService followService = new FollowService(conn);
			 followService.add(follow);
		 }catch(Exception e){
			 e.printStackTrace();
		 }finally{
			 response.sendRedirect(request.getContextPath()+"/follow.do?method=list");
			 DbUtil.close(conn);
		 }
		 
		 return modelAndView;
	 }
	 public ModelAndView update(HttpServletRequest request, HttpServletResponse response) throws IOException{
		   ModelAndView modelAndView = new ModelAndView(andAndEdit);
		   ASFuntion asf = new ASFuntion();
		   String autoId = asf.showNull(request.getParameter("autoId"));
		   String customer = asf.showNull(request.getParameter("customer"));
		   String linkpeople = asf.showNull(request.getParameter("linkpeople"));
		   String followtime = asf.showNull(request.getParameter("followtime"));
		   String followcontent =asf.showNull(request.getParameter("followcontent"));
		   String followstatus = asf.showNull(request.getParameter("followstatus"));
		   String nexttime = asf.showNull(request.getParameter("nexttime"));
		   String nextcontent = asf.showNull(request.getParameter("nextcontent"));
		   String nextstatus = asf.showNull(request.getParameter("nextstatus"));
		   String disman = asf.showNull(request.getParameter("disman"));
		   String followman = asf.showNull(request.getParameter("followman"));
		   
		   Follow follow = new Follow();
		   follow.setAutoId(autoId);
		   follow.setCustomer(customer);
		   follow.setLinkpeople(linkpeople);
		   follow.setFollowtime(followtime);
		   follow.setFollowcontent(followcontent);
		   follow.setFollowstatus(followstatus);
		   follow.setNexttime(nexttime);
		   follow.setNextcontent(nextcontent);
		   follow.setNextstatus(nextstatus);
		   follow.setDisman(disman);
		   follow.setFollowman(followman);
		   Connection conn = null;
		   try{
			   conn = new DBConnect().getConnect("");
			   FollowService followService = new FollowService(conn);
			   followService.update(follow);
		   }catch(Exception e){
			   e.printStackTrace();
		   }finally{
			   response.sendRedirect(request.getContextPath()+"/follow.do?method=list");
			   DbUtil.close(conn);
		   }
		   return modelAndView;
	 }
	 public ModelAndView delete(HttpServletRequest request, HttpServletResponse response) throws IOException{
		 ModelAndView modelAndView = new ModelAndView(list);
		 ASFuntion asf = new ASFuntion();
		 String autoId = asf.showNull(request.getParameter("id"));
		 Connection conn = null;
		 try{
			 conn = new DBConnect().getConnect("");
			 FollowService followService = new FollowService(conn);
			 followService.delete(autoId);
		 }catch(Exception e){
			 e.printStackTrace();
		 }finally{
			 response.sendRedirect(request.getContextPath()+"/follow.do?method=list");
			 DbUtil.close(conn);
		 }
		 return modelAndView;
	 }
	 
	 public ModelAndView goFollowCondition(HttpServletRequest request, HttpServletResponse response) throws Exception{
		 ModelAndView modelAndView = new ModelAndView(andAndEdit);
		 ASFuntion asf = new ASFuntion();
		 String id = asf.showNull(request.getParameter("id"));
		 Connection conn = null;
		 Business business = null;
		 try{
			 conn = new DBConnect().getConnect("");
			 CustomerService customerService = new CustomerService(conn); 
				if(!id.equals("")){
					business = customerService.getBusiness(id);
					Follow fw = new Follow();
					fw.setCustomer(business.getCustomername());
					fw.setFollowman(business.getDistriman());
					fw.setDisman(business.getFollow());
					fw.setLinkpeople(business.getContact());
					modelAndView.addObject("follow",fw);
				}	
		 }catch(Exception e){
			 e.printStackTrace();
		 }finally{
			 DbUtil.close(conn);
		 }
		 return modelAndView;
	 }

}

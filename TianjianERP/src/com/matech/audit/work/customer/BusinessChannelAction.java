package com.matech.audit.work.customer;

import java.io.IOException;
import java.sql.Connection;

import javax.jws.WebParam.Mode;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.matech.audit.pub.datagrid.DataGrid;
import com.matech.audit.pub.datagrid.DataGridProperty;
import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.customer.BusinessChannelService;
import com.matech.audit.service.customer.model.BusinessChannel;
import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;

public class BusinessChannelAction extends MultiActionController {
	private final String list ="businesschannel/List.jsp";
	private final String addAndEdit ="businesschannel/AddandEdit.jsp";
	
	public ModelAndView list(HttpServletRequest request, HttpServletResponse response) throws Exception{
		ModelAndView modelAndView = new ModelAndView(list);
		UserSession userSession = (UserSession) request.getSession().getAttribute("userSession"); 
		DataGridProperty pp = new DataGridProperty(); 
		String ppSql  = "";
		
		Connection conn = new DBConnect().getConnect("");
		
		String bcRoleString =new ASFuntion().showNull(new DbUtil(conn).queryForString("SELECT a.rid FROM k_userrole a LEFT JOIN k_role b ON a.`rid` = b.`id` WHERE a.userid='"+userSession.getUserId()+"' AND b.`rolename` = '品牌部负责人'"));
		if(!"".equals(bcRoleString)){
			bcRoleString = " OR 1=1 ";
		}
		DbUtil.close(conn);
		
		ppSql="SELECT a.autoId,a.company,a.channel,a.ifpartner,a.memberCount,a.linkman,a.linkrank,a.phone,a.email,"+
				"a.QQorMSN,a.headuser,a.headrank, a.headphone, b.Name as manager,a.memo FROM k_businesschanel  a "+
				"left join k_user b on b.id = a.manager " +
			    " where 1=1 and (createUser="+userSession.getUserId()+" or manager = "+userSession.getUserId()+" or '19' ='"+userSession.getUserId()+"' "+bcRoleString+" ) ${company} ${channel} ${ifpartner} ${membercount} ${linkman} ${headuser} ${manager}";

		pp.setTableID("businessChannel");
		pp.setPageSize_CH(20);
		pp.setCustomerId("");
		pp.setWhichFieldIsValue(1);
		pp.setInputType("radio");
		pp.setOrderBy_CH("autoId");
		pp.setDirection("asc");
		pp.setPrintEnable(true);
	    pp.setPrintVerTical(false);

		pp.setSQL(ppSql);
		pp.addColumn("单位名称","company");
		pp.addColumn("渠道名称", "channel");
		pp.addColumn("是否战略合作伙伴","ifpartner");
		pp.addColumn("会员单位数", "memberCount");
		pp.addColumn("联系人", "linkman");
		pp.addColumn("职位", "linkrank");
		pp.addColumn("电话", "phone");
		pp.addColumn("邮箱", "email");
		pp.addColumn("QQ/MSN", "QQorMSN");
		pp.addColumn("负责人", "headuser");
		pp.addColumn("负责人职位", "headrank");
		pp.addColumn("负责人联系方式","headphone");
		pp.addColumn("内部管理人","manager");
		
		pp.addSqlWhere("company"," and company like '%${company}%'");
		pp.addSqlWhere("channel", " and channel = '${channel}'");
		pp.addSqlWhere("ifpartner", " and ifpartner = '${ifpartner}'");
		pp.addSqlWhere("membercount", " and membercount = '${membercount}'");
		pp.addSqlWhere("linkman", " and linkman like '%${linkman}'%");
		pp.addSqlWhere("headuser", "and headuser like '%${headuser}%'");
		pp.addSqlWhere("manager", " and manager like '${manager}'");

 
		request.getSession().setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);
		return modelAndView;
		
	}
	
	public ModelAndView edit(HttpServletRequest request, HttpServletResponse response){
		ModelAndView modelAndView= new ModelAndView(addAndEdit);
		ASFuntion asf = new ASFuntion();
		String id = asf.showNull(request.getParameter("id"));
		Connection conn = null;
		BusinessChannel businessChannel = null;
		try{
			conn = new DBConnect().getConnect("");
			BusinessChannelService businessChannelService = new BusinessChannelService(conn);
			if(!id.equals("")){
				businessChannel = businessChannelService.getBusinessChannel(id);	 
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		modelAndView.addObject("businessChannel",businessChannel);
		return modelAndView;
	}
	public ModelAndView save(HttpServletRequest request, HttpServletResponse response) throws IOException{
		ASFuntion asf = new ASFuntion();
		UserSession userSession = (UserSession) request.getSession().getAttribute("userSession"); 
		String userId = userSession.getUserId();
		String departmentId = userSession.getUserAuditDepartmentId();
		String createTime = asf.getCurrentDate()+" "+asf.getCurrentTime();
		String company = asf.showNull(request.getParameter("company")); 
		String channel = asf.showNull(request.getParameter("channel"));
		String ifpartner = asf.showNull(request.getParameter("ifpartner"));
		String membercount = asf.showNull(request.getParameter("membercount"));
		String linkman = asf.showNull(request.getParameter("linkman"));
		String linkrank = asf.showNull(request.getParameter("linkrank"));
		String phone = asf.showNull(request.getParameter("phone"));
		String email = asf.showNull(request.getParameter("email"));
		String qqormsn = asf.showNull(request.getParameter("qqormsn"));
		String headuser = asf.showNull(request.getParameter("headuser"));
		String headrank = asf.showNull(request.getParameter("headrank"));
		String headphone = asf.showNull(request.getParameter("headphone"));
		String manager = asf.showNull(request.getParameter("manager"));
		String memo = asf.showNull(request.getParameter("memo"));
		
		BusinessChannel businessChannel = new BusinessChannel();
		businessChannel.setCreateUser(userId);
		businessChannel.setCreateDepartment(departmentId);
		businessChannel.setCreatTime(createTime);
		businessChannel.setCompany(company);
		businessChannel.setChannel(channel);
		businessChannel.setIfpartner(ifpartner);
		businessChannel.setMenberCount(membercount);
		businessChannel.setLinkMan(linkman);
		businessChannel.setLinkrank(linkrank);
		businessChannel.setPhone(phone);
		businessChannel.setEmail(email);
		businessChannel.setQQorMSN(qqormsn);
		businessChannel.setHeadUser(headuser);
		businessChannel.setHeadrank(headrank);
		businessChannel.setHeadphone(headphone);
		businessChannel.setManager(manager);
		businessChannel.setMemo(memo);
		Connection conn = null;
		try{
			conn = new DBConnect().getConnect("");
			BusinessChannelService businessChannelService = new BusinessChannelService(conn);
			businessChannelService.save(businessChannel);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			response.sendRedirect(request.getContextPath()+"/businessChannel.do");
			DbUtil.close(conn);
		}
	
		
		return null;
	}
	
	public ModelAndView update(HttpServletRequest request, HttpServletResponse response) throws IOException{
		 ModelAndView modelAndView = new ModelAndView(list);
		 ASFuntion asf = new ASFuntion();
		 String autoId = asf.showNull(request.getParameter("autoId"));
		 String company = asf.showNull(request.getParameter("company")); 
		 String channel = asf.showNull(request.getParameter("channel"));
		 String ifpartner = asf.showNull(request.getParameter("ifpartner"));
		 String membercount = asf.showNull(request.getParameter("membercount"));
		 String linkman = asf.showNull(request.getParameter("linkman"));
		 String linkrank = asf.showNull(request.getParameter("linkrank"));
		 String phone = asf.showNull(request.getParameter("phone"));
		 String email = asf.showNull(request.getParameter("email"));
		 String qqormsn = asf.showNull(request.getParameter("qqormsn"));
		 String headuser = asf.showNull(request.getParameter("headuser"));
		 String headrank = asf.showNull(request.getParameter("headrank"));
		 String headphone = asf.showNull(request.getParameter("headphone"));
		 String manager = asf.showNull(request.getParameter("manager"));
		 String memo = asf.showNull(request.getParameter("memo"));
		 
		 BusinessChannel businessChannel = new BusinessChannel();
		 businessChannel.setAutoId(autoId);
		 businessChannel.setCompany(company);
		 businessChannel.setChannel(channel);
		 businessChannel.setIfpartner(ifpartner);
		 businessChannel.setMenberCount(membercount);
		 businessChannel.setLinkMan(linkman);
		 businessChannel.setLinkrank(linkrank);
		 businessChannel.setPhone(phone);
		 businessChannel.setEmail(email);
		 businessChannel.setQQorMSN(qqormsn);
		 businessChannel.setHeadUser(headuser);
		 businessChannel.setHeadrank(headrank);
		 businessChannel.setHeadphone(headphone);
		 businessChannel.setManager(manager);
		 businessChannel.setMemo(memo);
		 Connection conn = null;
		 try{
			 conn = new DBConnect().getConnect("");
			 BusinessChannelService businessChannelService = new BusinessChannelService(conn);
			 businessChannelService.update(businessChannel);
		 }catch(Exception e){
			 e.printStackTrace();
		 }finally{
			 response.sendRedirect(request.getContextPath()+"/businessChannel.do");
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
			 BusinessChannelService businessChannelService = new BusinessChannelService(conn);
			 businessChannelService.delete(autoId);
		 }catch(Exception e){
			 e.printStackTrace();
		 }finally{
			 response.sendRedirect(request.getContextPath()+"/businessChannel.do");
			 DbUtil.close(conn);
		 }
		 
		 return modelAndView;
	}

}

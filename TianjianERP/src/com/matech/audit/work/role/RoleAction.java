package com.matech.audit.work.role;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.matech.audit.pub.datagrid.DataGrid;
import com.matech.audit.pub.datagrid.DataGridProperty;
import com.matech.audit.pub.db.DBConnect;

import com.matech.audit.service.accright.AccRightService;
import com.matech.audit.service.log.LogService;
import com.matech.audit.service.popedom.PopedomService;
import com.matech.audit.service.role.ApplyService;
import com.matech.audit.service.role.AuditService;
import com.matech.audit.service.role.RoleService;
import com.matech.audit.service.role.model.RoleTable;
import com.matech.audit.service.userpopedom.UserPopedomService;
import com.matech.framework.listener.UserSession;

import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;
import com.matech.framework.pub.util.Debug;
import com.matech.framework.service.print.PrintSetup;

public class RoleAction extends MultiActionController {
	
	private final String _strSuccess = "role.do";

	private final String _strList = "Role/List.jsp";

	private final String _strAddaddEdit = "Role/AddandEdit.jsp";

	private final String _strRolePopedom = "role.do?method=UpdatePopedom";

	
	private final String _strApply = "Role/Apply.jsp";
	
	private final String _strApplyList = "Role/ApplyList.jsp";
	
	private final String _strApplyListAction = "role.do?method=applyAlist";
	
	private final String _strAudit = "Role/Audit.jsp";
	
	private final String _strAuditList = "Role/AuditList.jsp"; 
	
	private final String _strAuditListAction = "role.do?method=auditAlist";
	
	/**
	 * 个人权限申请
	 */
	public ModelAndView applyAdd(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView(_strApply);
		ASFuntion CHF = new ASFuntion();
			UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");
			String ppm = userSession.getUserPopedom(); 	//人员已有权限
			String system = userSession.getUserAuditOfficePopedom();	//系统总权限
			
			system = CHF.replaceStr(system, "','", ".");
			system = CHF.replaceStr(system, "'", ".");
			String [] ppms = ppm.split("\\.");
			
			String ppmng = "";
			for(int i = 0; i<ppms.length; i++){
				if(ppms[i] != null && !"".equals(ppms[i])){
					if(system.indexOf("." + ppms[i] + ".")> -1){
						system = CHF.replaceStr(system, "." + ppms[i] + ".", ".");
						
					}
				}
			}
			
			ppmng = system;
			
			ppmng = CHF.replaceStr(ppmng, ".", "','");
			
			if(ppmng.length()>6) {	
				ppmng = ppmng.substring(2,ppmng.length()-2);
			} else {
				ppmng = ".";
			}
			
			DataGridProperty pp = new DataGridProperty(){
			};
						
						

					//必要设置
					pp.setTableID("rightapply");
					//基本设置
					
					pp.setCustomerId("");
					
					pp.setPageSize_CH(50);
				
					//sql设置
				
					String sql = "select a.id,a.menu_id,b.name as sname,a.name from s_sysmenu a left join s_sysmenu b on a.parentid=b.menu_id where  a.id in  (" + ppmng + ")   and a.parentid!='000' and b.parentid!='000' and b.depth!='0'  ";

					//查询设置

					

					pp.setSQL(sql);
					pp.setDirection_CH("menu_id");
				
					pp.setInputType("checkbox");
					pp.addColumn("对应菜单编号", "menu_id");
					pp.addColumn("上级菜单名称", "sname");
					pp.addColumn("对应菜单名称", "name");
					

					pp.setWhichFieldIsValue(1);
					
					pp.setPrintEnable(true);
					pp.setPrintVerTical(false);
					pp.setPrintTitle("申请权限");

					request.getSession()
							.setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);
			
		return modelAndView;
	}
	
	public ModelAndView applySave(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		ASFuntion CHF=new ASFuntion();
		
		Connection conn =null;
		    
		try{
			UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");
			String userid = userSession.getUserId();
			String currentDate = CHF.getCurrentDate();
			
		     conn = new DBConnect().getConnect("");
		     
		     String chooseValues = request.getParameter("chooseValues");
		     chooseValues ="."+CHF.replaceStr(chooseValues, ",", ".")+".";
		     
		     String applymemo = request.getParameter("applymemo");
		     
		     ApplyService applyService=new ApplyService(conn);
		     
		     applyService.saveApply(userid,chooseValues,currentDate,applymemo);
		     
		     response.sendRedirect(_strApplyListAction);
		     return null;
		     
		}catch(Exception e){
		     Debug.print(Debug.iError,"访问失败",e);
		}finally{
		     DbUtil.close(conn); 
		}
		
		return null;
	}
	
	public ModelAndView applyAlist(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		ModelAndView modelAndView = new ModelAndView(_strApplyList);
			ASFuntion CHF = new ASFuntion();
			
			
			DataGridProperty pp = new DataGridProperty(){
				
				public void onSearch (
	    				javax.servlet.http.HttpSession session,
	    				javax.servlet.http.HttpServletRequest request,
	    				javax.servlet.http.HttpServletResponse response) throws Exception{
					
					UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");
					
					String userid = userSession.getUserId();

	
					//sql设置
					
					String sql ="" 	
						+" select a.autoid,a.applydate, \n"
						+" if(length(a.applymemo)<=6,a.applymemo,CONCAT(SUBSTRING(a.applymemo,1,6),'...')) as applymemo, \n"
						+" e.auditPopedomok,f.auditPopedomng,a.auditdate, \n"
						+" if(length(a.auditmemo)<=6,a.auditmemo,CONCAT(SUBSTRING(a.auditmemo,1,6),'...')) as auditmemo,b.name as bname,c.name as cname, \n"
						+" d.applyPopedom \n"
						+" from k_rightapply a  \n"
						+" left join k_user b  \n"
						+" on a.applyuserid=b.id  \n"
						+" left join k_user c on a.audituserid=c.id \n"
						+" inner join ( \n"
						+" 		select a.autoid,group_concat(d.name )as applyPopedom \n"
						+" 		from k_rightapply a,s_sysmenu d \n"
						+" 		where a.applyPopedom like concat('%.',d.id,'.%') \n"
						+" 		group by a.autoid \n"
						+" ) d \n"
						+" on  \n"
						+" a.autoid=d.autoid \n"
						+" left join ( \n"
						+" 		select a.autoid,group_concat(d.name )as auditPopedomok \n"
						+" 		from k_rightapply a,s_sysmenu d \n"
						+" 		where a.auditPopedomok like concat('%.',d.id,'.%') \n"
						+" 		group by a.autoid \n"
						+" ) e \n"
						+" on  \n"
						+" a.autoid=e.autoid \n"
						+" left join ( \n"
						+" 		select a.autoid,group_concat(d.name )as auditPopedomng \n"
						+" 		from k_rightapply a,s_sysmenu d \n"
						+" 		where a.auditPopedomng like concat('%.',d.id,'.%') \n"
						+" 		group by a.autoid \n"
						+" ) f \n"
						+" on  \n"
						+" a.autoid=f.autoid \n"
						+"where 1=1 and a.applyuserid='"+userid+"' \n"
						+"${applyuserid} \n"
						+"${applyPopedom} \n"
						+"${applydate} \n"
						+"${applymemo} \n"
						+"${audituserid} \n"
						+"${auditPopedomok} \n"
						+"${auditPopedomng} \n"
						+"${auditdate} \n"
						+"${auditmemo}";
						 
					

					
					this.setSQL(sql);
				
				}
				
			};
						
						

					//必要设置
					pp.setTableID("rightapplyList");
					//基本设置
					
					pp.setCustomerId("");
					
					pp.setPageSize_CH(50);
				
		
					pp.setDirection_CH("autoid");
				
					pp.setInputType("radio");
					pp.addColumn("申请人", "bname");
				
					pp.addColumn("申请权限", "applyPopedom");
					pp.addColumn("申请时间", "applydate");
					pp.addColumn("申请备注", "applymemo");
					pp.addColumn("审批人", "cname");
					pp.addColumn("审批通过权限", "auditPopedomok");
					pp.addColumn("审批未通过权限", "auditPopedomng");
					pp.addColumn("审批时间", "auditdate");
					pp.addColumn("审批备注", "auditmemo");
					
					pp.setWhichFieldIsValue(1);
					
					//设置过滤条件
					pp.addSqlWhere("applyuserid", " and b.name like '%${applyuserid}%' ");
					pp.addSqlWhere("applyPopedom", " and a.applyPopedom like '%.${applyPopedom}.%' ");
					
					pp.addSqlWhere("applydate", " and a.applydate = '${applydate}' ");
					pp.addSqlWhere("applymemo", " and a.applymemo like '%${applymemo}%' ");
					
					pp.addSqlWhere("audituserid", " and c.name like '%${audituserid}%' ");
					pp.addSqlWhere("auditPopedomok", " and a.auditPopedomok like '%.${auditPopedomok}.%' ");
					pp.addSqlWhere("auditPopedomng", " and a.auditPopedomng like '%.${auditPopedomng}.%' ");
					pp.addSqlWhere("auditdate", " and a.auditdate = '${auditdate}' ");

					pp.addSqlWhere("auditmemo", " and a.auditmemo like '%${auditmemo}%' ");
					
					pp.setPrintEnable(true);
					pp.setPrintVerTical(false);
					pp.setPrintTitle("权限申请");

					request.getSession()
							.setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);
			
			
		return modelAndView;
	}
	
	public ModelAndView applyUpdate(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return null;
	}
	


	/**
	 * 个人权限审批
	 */
	public ModelAndView auditAdd(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView(_strAudit);
	    ASFuntion CHF=new ASFuntion();
			
	    Connection conn =null;
		    
		try {
		       conn = new DBConnect().getConnect("");
		
		       String id = request.getParameter("chooseValue");
		      
		       
		       ApplyService applyService=new ApplyService(conn);
		       
		       String userId = applyService.getUserIdByRightApplyId(id);
		       
		       String  ppm= applyService.getApplyPopedom(id);
		       
		       ppm = CHF.replaceStr(ppm, ".", "','");
		       
		       ppm = ppm.substring(2,ppm.length()-2);
		       
		       DataGridProperty pp = new DataGridProperty(){
		       };
						
						

					//必要设置
					pp.setTableID("rightaudit");
					//基本设置
					
					pp.setCustomerId("");
					
					pp.setPageSize_CH(50);
				
					//sql设置
				
					String sql = "select a.id,a.menu_id,b.name as sname,a.name from s_sysmenu a left join s_sysmenu b on a.parentid=b.menu_id where  a.id in  (" + ppm + ")  ";

					//查询设置

					

					pp.setSQL(sql);
					pp.setDirection_CH("menu_id");
				
					pp.setInputType("checkbox");
					pp.addColumn("对应菜单编号", "menu_id");
					pp.addColumn("上级菜单名称", "sname");
					pp.addColumn("对应菜单名称", "name");
					

					pp.setWhichFieldIsValue(1);
					
					pp.setPrintEnable(true);
					pp.setPrintVerTical(false);
					pp.setPrintTitle("申请权限");
					
					
					modelAndView.addObject("id", id);
					modelAndView.addObject("userId", userId);

					request.getSession()
							.setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);
					
		}catch(Exception e){
		     Debug.print(Debug.iError,"访问失败",e);
		}finally{
		     DbUtil.close(conn); 
		}
		return modelAndView;
	}
	
	public ModelAndView auditSave(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		ASFuntion CHF=new ASFuntion();
		
		Connection conn =null;
		    
		try{
			UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");
			String userid = userSession.getUserId();
			String currentDate = CHF.getCurrentDate();
			
		     conn = new DBConnect().getConnect("");
		     
		     String chooseValues = CHF.showNull(request.getParameter("chooseValues"));
		     chooseValues ="."+CHF.replaceStr(chooseValues, ",", ".")+".";
		     
		     String noChooseValues = CHF.showNull(request.getParameter("noChooseValues"));
		     
		     if(!"".equals(noChooseValues)){ 
		    	 
		    	 noChooseValues ="."+CHF.replaceStr(noChooseValues, ",", ".")+".";
		    	 
		     }
		    
		     String applyId = CHF.showNull(request.getParameter("applyId"));
		     
		     String userId = CHF.showNull(request.getParameter("userId"));
		     
		     String auditmemo = CHF.showNull(request.getParameter("auditmemo"));
		     
		     AuditService auditService=new AuditService(conn);
		     
		     auditService.saveAudit(userid,chooseValues,noChooseValues,currentDate,auditmemo,applyId,userId);
		     
		     response.sendRedirect(_strAuditListAction);
		     return null;
		     
		}catch(Exception e){
		     Debug.print(Debug.iError,"访问失败",e);
		}finally{
		     DbUtil.close(conn); 
		}
		
		return null;
	}
	
	public ModelAndView auditAlist(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		ModelAndView modelAndView = new ModelAndView(_strAuditList);
		ASFuntion CHF = new ASFuntion();
			UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");
			
			String userid = userSession.getUserId();

			
			DataGridProperty pp = new DataGridProperty(){
				public void onSearch (
	    				javax.servlet.http.HttpSession session,
	    				javax.servlet.http.HttpServletRequest request,
	    				javax.servlet.http.HttpServletResponse response) throws Exception{
					
					
	
//					sql设置
					
					String sql = ""
						+" select a.autoid,a.applydate, \n"
						+" if(length(a.applymemo)<=6,a.applymemo,CONCAT(SUBSTRING(a.applymemo,1,6),'...')) as applymemo, \n"
						+" e.auditPopedomok,f.auditPopedomng,a.auditdate, \n"
						+" if(length(a.auditmemo)<=6,a.auditmemo,CONCAT(SUBSTRING(a.auditmemo,1,6),'...')) as auditmemo,b.name as bname,c.name as cname, \n"
						+" d.applyPopedom \n"
						+" from k_rightapply a  \n"
						+" left join k_user b  \n"
						+" on a.applyuserid=b.id  \n"
						+" left join k_user c on a.audituserid=c.id \n"
						+" inner join ( \n"
						+" 		select a.autoid,group_concat(d.name )as applyPopedom \n"
						+" 		from k_rightapply a,s_sysmenu d \n"
						+" 		where a.applyPopedom like concat('%.',d.id,'.%') \n"
						+" 		group by a.autoid \n"
						+" ) d \n"
						+" on  \n"
						+" a.autoid=d.autoid \n"
						+" left join ( \n"
						+" 		select a.autoid,group_concat(d.name )as auditPopedomok \n"
						+" 		from k_rightapply a,s_sysmenu d \n"
						+" 		where a.auditPopedomok like concat('%.',d.id,'.%') \n"
						+" 		group by a.autoid \n"
						+" ) e \n"
						+" on  \n"
						+" a.autoid=e.autoid \n"
						+" left join ( \n"
						+" 		select a.autoid,group_concat(d.name )as auditPopedomng \n"
						+" 		from k_rightapply a,s_sysmenu d \n"
						+" 		where a.auditPopedomng like concat('%.',d.id,'.%') \n"
						+" 		group by a.autoid \n"
						+" ) f \n"
						+" on  \n"
						+" a.autoid=f.autoid \n"
						+ " where 1=1 "
						+ "${applyuserid} \n"
						+ "${applyPopedom} \n"
						+ "${applydate} \n"
						+ "${applymemo} \n"
						+"${audituserid} \n"
						+"${auditPopedomok} \n"
						+ "${auditPopedomng} \n"
						+"${auditdate} \n"
						+ "${auditmemo}";
						 
					//System.out.println("yzm:sbSql="+sql);
					
					this.setSQL(sql);
				
				}
			};
						
						

					//必要设置
					pp.setTableID("rightauditList");
					//基本设置
					
					pp.setCustomerId("");
					
					pp.setPageSize_CH(50);
				
					

					//查询设置

					

				
					pp.setDirection_CH("autoid");
				
					pp.setInputType("radio");
					pp.addColumn("申请人", "bname");
					pp.addColumn("申请权限", "applyPopedom");
					pp.addColumn("申请时间", "applydate");
					pp.addColumn("申请备注", "applymemo");
					pp.addColumn("审批人", "cname");
					pp.addColumn("审批通过权限", "auditPopedomok");
					pp.addColumn("审批未通过权限", "auditPopedomng");
					pp.addColumn("审批时间", "auditdate");
					pp.addColumn("审批备注", "auditmemo");
					
					pp.setWhichFieldIsValue(1);
					
					//设置过滤条件
					pp.addSqlWhere("applyuserid", " and b.name like '%${applyuserid}%' ");
					pp.addSqlWhere("applyPopedom", " and a.applyPopedom like '%.${applyPopedom}.%' ");
					
					pp.addSqlWhere("applydate", " and a.applydate = '${applydate}' ");
					pp.addSqlWhere("applymemo", " and a.applymemo like '%${applymemo}%' ");
					
					pp.addSqlWhere("audituserid", " and c.name like '%${audituserid}%' ");
					pp.addSqlWhere("auditPopedomok", " and a.auditPopedomok like '%.${auditPopedomok}.%' ");
					pp.addSqlWhere("auditPopedomng", " and a.auditPopedomng like '%.${auditPopedomng}.%' ");
					pp.addSqlWhere("auditdate", " and a.auditdate = '${auditdate}' ");

					pp.addSqlWhere("auditmemo", " and a.auditmemo like '%${auditmemo}%' ");
					

					
//					//获取页面上的值，一般用来构造复杂过滤条件时用
//					pp.addInputValue("curChoiceCustomerId");

					

					
					
					pp.setPrintEnable(true);
					pp.setPrintVerTical(false);
					pp.setPrintTitle("权限申请");

					request.getSession()
							.setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);
			
			
		return modelAndView;
	}
	
	public ModelAndView auditUpdate(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return null;
	}
	

	
	
	
	/**
	 * 跳转到角色列表
	 * @param request
	 * @param Response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView list(HttpServletRequest req, HttpServletResponse res)
			throws Exception {


		DataGridProperty pp = new DataGridProperty(){
};
			
			

		//必要设置
		pp.setTableID("role");
		//基本设置
		
		pp.setCustomerId("");
		
		pp.setPageSize_CH(50);
	
		//sql设置
	
		String sql = "select a.id,a.rolename,a.rolevalue,if(group_concat(c.name) is null, '', group_concat(c.name)) name,a.property,if(ifnull(a.popedom,'') = '','','已授权') as pop \n"
					+ "from asdb.k_role a left join asdb.k_userrole b \n"
					+ "on a.id = b.rid \n"
					+ "left join asdb.k_user c \n"
					+ "on b.userid = c.id \n"
					+ "where 1=1 "
					+ "${departmentid}"
					+ "${rolename}"
					+ "${name}"
					+ " group by a.id"; 

		//查询设置

		pp.setSQL(sql);
		pp.setOrderBy_CH("property,id");
		pp.setDirection("desc,asc");
//		pp.setDirection_CH("id,rolename,rolevalue");
	
		pp.setColumnWidth("10,15,15,10,10");
		
		pp.setValueMaxLength(1000);
		
		pp.setInputType("radio");
		pp.addColumn("角色名称", "rolename");
		pp.addColumn("角色功能", "rolevalue");
		pp.addColumn("角色人员", "name");
		pp.addColumn("优先级", "property");
		pp.addColumn("状态", "pop");
		
		pp.addSqlWhere("departmentid", " and c.departmentid = '${departmentid}' ");
		pp.addSqlWhere("rolename", " and a.rolename = '${rolename}' ");
		pp.addSqlWhere("name", "and c.name='${name}'");
		
		pp.setWhichFieldIsValue(1);
		
		pp.setPrintEnable(true);
		pp.setPrintVerTical(false);
		pp.setPrintTitle("角色管理");

		req.getSession()
				.setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);
	
		return new ModelAndView(_strList);
	}

	 public ModelAndView del(HttpServletRequest request, HttpServletResponse response)throws Exception{
			
		 response.setContentType("text/html;charset=utf-8");
		 ASFuntion CHF=new ASFuntion();
			PrintWriter out=response.getWriter();
		    Connection conn =null;
		    
		        try {
		            conn = new DBConnect().getConnect("");
		          
		            //这里是你的处理代码，以及调用SERVICE的
		            String id = CHF.showNull(request.getParameter("chooseValue"));
		            String act = CHF.showNull(request.getParameter("act"));
		            RoleService rm = new RoleService (conn);
		            RoleTable rt = new RoleTable();
		            //判断是否点选了一项
		            if("".equals(id)){
		            	String adored = CHF.showNull(request.getParameter("adored"));
		            	
		            	System.out.println("adored:"+adored);
		            	
		            	//判断判断增加还是修改的变量是否为空字符
		            	if(!"".equals(adored)){
		            		
		            		System.out.println("11111:"+adored);
		            		
		            		rt.setId(CHF.showNull(request.getParameter("id")));
		            		rt.setRolename(CHF.showNull(request.getParameter("rolename")));
		            		rt.setRolevalue(CHF.showNull(request.getParameter("rolevalue")));
		            		rt.setProperty(CHF.showNull(request.getParameter("property")));
		            		rt.setLtype(CHF.showNull(request.getParameter("ltype")));
		            		rt.setInnername(CHF.showNull(request.getParameter("innername")));
		            		rm.add(rt,adored);
		            		
		            		if("ad".equals(adored)){
		            			System.out.println("222:"+_strSuccess);
		            			response.sendRedirect(_strSuccess);
		            		}else{
		            			String URL =_strRolePopedom + "&chooseValue="+rt.getId(); 
		            			
		            			System.out.println("333:"+URL);
			            		response.sendRedirect(URL);
		            		}
		            		
		            		return null;
		            	}else{
		            		System.out.println("444:"+_strAddaddEdit);
		            		return new ModelAndView(_strAddaddEdit);
		            		
		            	}
		            }else{
		            	//判断是否是删除
		            	if(act.equals("del")){
		            		//判断是否删除成
		            		if(rm.del(id)){
		            			response.sendRedirect(_strSuccess);
		            			return null;
		            		}else{
		            			out.println("<script language='javascript'>");
		            			out.println("alert('该角色内存在人员,不能删除!');");
		            			out.println("window.location='"+_strSuccess+"';");
		            			out.println("</script>");
		            			return null;
		            		}
		            	}else{
		            		rt = rm.get(id);
			            	
			            	//request.getSession().setAttribute("rt", rt);
			            	
			            	return new ModelAndView(_strAddaddEdit,"rt",rt);
			            
		            	}   	
		            }
		        }catch(Exception e){
		            Debug.print(Debug.iError,"访问失败",e);
		        }finally{
		            DbUtil.close(conn); 
		        }

	

		        return null;
		    }
	 
	 /**
		 * 设置打印属性
		 * @param request
		 * @param response
		 * @return
		 * @throws Exception
		 */
		public ModelAndView print(HttpServletRequest request, HttpServletResponse response) throws Exception{
			Connection conn=null;
			HashMap mapResult = new HashMap();
			try {						
				//String temp = com.matech.framework.pub.sys.UTILSysProperty.SysProperty.getProperty("clientDog");
				conn= new DBConnect().getConnect("");
				String tableid = request.getParameter("tableid");
				
				DataGridProperty pp = (DataGridProperty)request.getSession().getAttribute(DataGrid.sessionPre+tableid);
				
				PrintSetup printSetup = new PrintSetup(conn);	
				
				printSetup.setStrTitles(new String[]{"角色列表"});
				
				printSetup.setStrQuerySqls(new String[]{pp.getFinishSQL()});
				printSetup.setStrChineseTitles(new String[]{"编号`角色名称`角色功能`角色人员"});
				printSetup.setCharColumn(new String[]{"1`2`3`4"});
	
				
				printSetup.setIColumnWidths(new int[]{20,20,60,30});
				
				String filename = printSetup.getExcelFile();
				
				
				//vpage strPrintTitleRows
				mapResult.put("refresh","");
				
				mapResult.put("saveasfilename","角色列表");			
				mapResult.put("vpage","false");
				mapResult.put("strPrintTitleRows","$2:$4");
				mapResult.put("filename", filename);	
				
				
				
				//curProjectid curProjectState curAccPackageID user
				
			}catch (Exception e) {
				Debug.print(Debug.iError, "查询个人项目项目权限失败！", e);
				e.printStackTrace();
				throw e;
			} finally {
				DbUtil.close(conn);
			}
			
			return new ModelAndView("/Excel/tempdata/PrintandSave.jsp",mapResult);
		}
		
		 /**
		 * 
		 * 设置人员角色关系
		 * @param request
		 * @param response
		 * @return
		 * @throws Exception
		 */
		public ModelAndView UserRole(HttpServletRequest request, HttpServletResponse response) throws Exception{
			Connection conn=null;
			String radio="";
			try{
				conn=new DBConnect().getConnect("");
				radio=new RoleService(conn).getRole("radio");
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				if(conn!=null)
					conn.close();
			}
			
			
			return new ModelAndView("Role/UserRole.jsp","radio",radio);
		}
		
		
		 /**
		 * 角色授权
		 * @param request
		 * @param response
		 * @return
		 * @throws Exception
		 */
		public ModelAndView RolePopedom(HttpServletRequest request, HttpServletResponse response) throws Exception{
			
			ASFuntion CHF=new ASFuntion();
			String stAll = CHF.showNull(request.getParameter("stAll"));
			String stRole = CHF.showNull(request.getParameter("stRole"));
			Map map=new HashMap();
			String string="";
			String SysPpm="";
			//String aUsr="";
			String string2="";
			Connection conn=null;
			try{
			conn=new DBConnect().getConnect("");
			if(!"".equals(stRole)){
				
				new RoleService(conn).updateRolePopedom(stAll,stRole);
			}
			UserSession us=(UserSession)request.getSession().getAttribute("userSession");
			SysPpm = us.getUserAuditOfficePopedom();
			
			//UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");
		//	aUsr = us.getUserLoginId();
			string2=new PopedomService(conn,SysPpm).getSubTree("00",".92.",".92.");
			string=new RoleService(conn).getRole("checkbox");

			}catch(Exception e)
			{
				//out.println(e);
			}finally{
				if(conn!=null)conn.close();
			}
			map.put("string", string);
			map.put("string2", string2);
			return new ModelAndView("Role/RolePopedom.jsp",map);
		}
		
		
		
		/**
		 * 调用BccUser.jsp页面
		 * @param request
		 * @param response
		 * @return
		 * @throws Exception
		 */
		public ModelAndView BccUser(HttpServletRequest request, HttpServletResponse response) throws Exception{
			
			ASFuntion CHF=new ASFuntion();
			Connection conn = null;

			response.setContentType("text/html;charset=utf-8");
			PrintWriter out=response.getWriter();
			String rid = CHF.showNull(request.getParameter("rid"));
			String usr = CHF.showNull(request.getParameter("usr"));
		
			try{
				conn=new DBConnect().getConnect("");
			RoleService rm = new RoleService(conn);
			out.println(rm.getUserTable(rid,usr));		
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				if(conn!=null)
					conn.close();
			}
			
			return null;
		}
		
		/**
		 * 调用AccUser.jsp页面
		 * @param request
		 * @param response
		 * @return
		 * @throws Exception
		 */
		public ModelAndView AccUser(HttpServletRequest request, HttpServletResponse response) throws Exception{
			
			Connection conn = null;
			AccRightService arm =new AccRightService();
//			String UserID = CHF.showNull(request.getParameter("UserID"));

			response.setContentType("text/html;charset=utf-8");
			PrintWriter out=response.getWriter();
	
			try{
				conn = new DBConnect().getConnect("");
				out.print(arm.getAUserTable(conn));		
				
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				if(conn!=null)conn.close();
			}
			return null;
		}
		
		/**
		 * 保存
		 * @param request
		 * @param response
		 * @return
		 * @throws Exception
		 */
		public ModelAndView SaveRole(HttpServletRequest request, HttpServletResponse response) throws Exception{
			
			UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
			
			ASFuntion CHF=new ASFuntion();
			Connection conn = null;
				
			String rid = CHF.showNull(request.getParameter("rid"));
			String sUsr = CHF.showNull(request.getParameter("sUsr"));
			
			//System.out.println("yzm:rid="+rid);
			//System.out.println("yzm:sUsr="+sUsr);
		
			try{
				conn=new DBConnect().getConnect("");
				RoleService rm = new RoleService(conn);
				String result = rm.saveUserRole(rid,sUsr);
				
				//2.人员的角色权限变动都记录日志；
				LogService.addTOLog(userSession, conn, null, result);
				
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				if(conn!=null)
					conn.close();
			}
			
			return null;
		}
		
		
		/**
		 * 更新权限
		 * @param request
		 * @param response
		 * @return
		 * @throws Exception
		 */
		public ModelAndView UpdatePopedom(HttpServletRequest request, HttpServletResponse response) throws Exception{
			
			response.setContentType("text/html;charset=utf-8");
			PrintWriter out=response.getWriter();
			ASFuntion CHF=new ASFuntion();
			String id = CHF.showNull(request.getParameter("chooseValue"));
		//	System.out.println("id=sdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasd"+id);
			Connection conn = null;
			RoleTable rt=null;
			PopedomService pcm =null;
			String string="";
			String string2="";
			
			Map map=new HashMap();
			map.put("id", id);
			try
			{
			conn = new DBConnect().getConnect("");
			RoleService rm = new RoleService (conn);
			rt = new RoleTable();
			String stAll = CHF.showNull(request.getParameter("stAll"));
			String stRole = CHF.showNull(request.getParameter("stRole"));
			if("".equals(id)){
				if(!"".equals(stRole)){
					new RoleService(conn).updateRolePopedom(stAll,stRole);
					
					UserPopedomService ups = new UserPopedomService(conn);
					ups.setProperty("role");
					//保存人员与部门的权限
					String [] menuid = request.getParameterValues("menuid"); //菜单List
					String [] departmentid = request.getParameterValues("departmentid"); //部门授权
					
					String [] omenuid = request.getParameterValues("omenuid");
					String [] reading = request.getParameterValues("reading"); //字段可读
					String [] editing = request.getParameterValues("editing"); //字段可编辑
					
					String [] role = stRole.split("\\.");
		        	for (int i = 0; i < role.length; i++) {
						if(role[i]!=null && !"".equals(role[i])){
							ups.saveUserIdPopedom(role[i], menuid, departmentid); 
							ups.saveFieldPopedom(role[i], omenuid, reading, editing); 
						}
					}
					response.sendRedirect("role.do");
					return null;
				}
			}else{
				rt = rm.get(id);
				string2=rt.getRolename();
			}
			UserSession us=(UserSession)request.getSession().getAttribute("userSession");
			String SysPpm = us.getUserAuditOfficePopedom();
			
			int userRoleOptimization = us.getUserRoleOptimization(); //当前人最大角色的优化级
			
			//property && roleid && UserPopedom
			pcm = new PopedomService(conn,SysPpm);
			try{
				pcm.setUserPopedom(true); //显示用户所有的部门权限
				pcm.setRoleid(id);
				pcm.setProperty("role");
				pcm.setUserRoleOptimization(userRoleOptimization); 
			}catch (Exception e) {}
			string=pcm.getSubTree("00",pcm.getRolePpm(id),".92.");
			
			}catch(Exception e)
			{
				out.println(e);
			}finally{
				if(conn!=null)conn.close();
			}
			map.put("string2", string2);
			map.put("string", string);
			
			//显示字段授权的dataGrid
			DataGridProperty pp = new DataGridProperty();
			//必要设置
			pp.setTableID("FieldPopedom");
			//基本设置
			pp.setCustomerId("");
			pp.setPageSize_CH(50);
				
			//sql设置
			String sql = "select a.*,b.reading,b.editing," +
			"	if(ifnull(b.reading,'') = '是','checked','') as readValue,if(ifnull(b.editing,'') = '是','checked','') as editValue " +
			"	from k_Field a  " +
			"	left join k_FieldPopedom b on 1=1 " +
			"	${roleid} " +
			"	and a.menuid = b.menuid " +
			"	and a.autoid = b.FieldId " +
			"	where 1=1 ${menuid} "; 

			//查询设置
			pp.setSQL(sql);
			pp.setOrderBy_CH("a.autoid");
			pp.setDirection("asc");
//			pp.setDirection_CH("id,rolename,rolevalue");
				
			pp.setColumnWidth("13,5,5");
			pp.setCancelPage(true);		
			pp.setValueMaxLength(1000);
					
			pp.addColumn("字段名称", "FieldName");
			pp.addColumn("是否可读", "reading" ,null,null,"<input ${readValue} type='checkbox' fieldid='${autoid}' menuid='${menuid}' id='r_${menuid}_${autoid}'  name='reads' value='是' onclick='getObj(this)' >可读");
			pp.addColumn("是否编辑", "editing" ,null,null,"<input ${editValue} type='checkbox' fieldid='${autoid}' menuid='${menuid}' id='e_${menuid}_${autoid}'  name='edits' value='是' onclick='getObj(this)' >编辑");
			
			pp.addSqlWhere("roleid", " and b.roleid = '${roleid}'  ");
			pp.addSqlWhere("menuid", " and a.menuid = '${menuid}'  ");
			
			pp.setWhichFieldIsValue(1);
			

			request.getSession().setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);
			
			return new ModelAndView("Role/UpdatePopedom.jsp",map);
		}
		
		
		/**
		 * 根据菜单权限ID得到菜单名称
		 * @param request
		 * @param response
		 * @return
		 * @throws Exception
		 */
		public ModelAndView getNameByPopedomPowerId(HttpServletRequest request, HttpServletResponse response) throws Exception{
			
			response.setContentType("text/html;charset=utf-8");
			PrintWriter out=response.getWriter();
			ASFuntion CHF=new ASFuntion();
			
			PreparedStatement ps = null;
			ResultSet rs = null;
			Connection conn = null;
			
	 
			try
			{
				conn = new DBConnect().getConnect("");
				
				String PopedomPowerId=CHF.showNull(request.getParameter("PopedomPowerId"));
				
				PopedomPowerId = CHF.replaceStr(PopedomPowerId, ".", "','");
				
				if(!"".equals(PopedomPowerId)){
					
					PopedomPowerId = PopedomPowerId.substring(2,PopedomPowerId.length()-2);
					
				}else{
					
					PopedomPowerId="-1";
					
				}
				String sql="select group_concat(name) from s_sysmenu  where id in ("+PopedomPowerId+")";
			
				
				
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				String names = ""; 
				 
				if(rs.next()){
					
					names = rs.getString(1);
					
				}
				
				if(names!=null){
					
					names = names.replaceAll(",", "<br>");
					
				}else{
					
					names="";
					
				}
				out.print(names);
				
				return null;
				
			}catch(Exception e)
			{
				out.println(e);
			}finally{
				if(rs!=null)rs.close();
				if(ps!=null)ps.close();
				if(conn!=null)conn.close();
			}
			return null;
		
			//return new ModelAndView("Role/UpdatePopedom.jsp",map);
		}
		
		
		
}

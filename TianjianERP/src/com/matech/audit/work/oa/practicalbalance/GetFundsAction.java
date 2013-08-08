package com.matech.audit.work.oa.practicalbalance;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.matech.audit.pub.datagrid.DataGrid;
import com.matech.audit.pub.datagrid.DataGridProperty;
import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.auditPlaform.AuditPlaformService;
import com.matech.audit.service.datamanage.BackupUtil;
import com.matech.audit.service.invoiceentry.InvoiceService;
import com.matech.audit.service.invoiceentry.model.InvoiceTable;
import com.matech.audit.service.oa.practicalbalance.GetFundService;
import com.matech.audit.service.oa.practicalbalance.model.GetFundsTable;
import com.matech.audit.service.project.BusinessProjectService;
import com.matech.audit.service.project.ProjectService;
import com.matech.audit.service.project.model.BusinessProject;
import com.matech.audit.service.project.model.Project;
import com.matech.audit.service.userpopedom.UserPopedomService;

import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.sys.UTILSysProperty;
import com.matech.framework.pub.util.ASFuntion;


 
public class GetFundsAction extends MultiActionController {
	 
	private static final String LIST = "oa/practicalbalance/GetFundsList.jsp";  
	private static final String DHLIST = "oa/practicalbalance/dhGetFundsList.jsp";  
	private static final String RDLIST = "oa/practicalbalance/rdGetFundsList.jsp";  
	private static final String IncomeCountList = "oa/practicalbalance/IncomeCountList.jsp";  
	private static final String IncomeOverrallList = "oa/practicalbalance/IncomeOverrallList.jsp";  
	private static final String AddAndEdit = "oa/practicalbalance/GetFundsEdit.jsp";
	private static final String DHAddAndEdit = "oa/practicalbalance/dhGetFundsEdit.jsp";
	private static final String RDAddAndEdit = "oa/practicalbalance/rdGetFundsEdit.jsp";
	private static final String businessEdit = "oa/practicalbalance/businessEdit.jsp"; 
	private static final String LIST_VIEW = "oa/practicalbalance/businessList.jsp"; 
	private static final String DHLIST_VIEW = "oa/practicalbalance/dhbusinessList.jsp"; 
	private final static String PROCESSATTATCHFILE = "processAttatchFile" ;
	private static final String TODETAIL="oa/practicalbalance/GetFundsAndInvoiceDetail.jsp";
	
	public ModelAndView list(HttpServletRequest request, HttpServletResponse response) throws Exception  {
		ASFuntion CHF = new ASFuntion() ;
		String office = CHF.showNull(UTILSysProperty.SysProperty.getProperty("系统应用事务所")).trim();
		System.out.println("office="+ office);
		if("众华".equals(office)) {
			response.sendRedirect(request.getContextPath()+"/getFunds.do?method=zhList") ;
		}else if("仁德".equals(office)) {
			response.sendRedirect(request.getContextPath()+"/getFunds.do?method=rdList") ;
		}else if("大华".equals(office) || "立信大华".equals(office)){
			response.sendRedirect(request.getContextPath()+"/getFunds.do?method=dhList") ;
		}else {
			response.sendRedirect(request.getContextPath()+"/practicalbalance.do") ;
		}
		return null ;
	} 
   
	public ModelAndView zhList(HttpServletRequest request, HttpServletResponse response) throws Exception  {
		 
			DataGridProperty pp = new DataGridProperty() {
				public void onSearch(javax.servlet.http.HttpSession session,
	                javax.servlet.http.HttpServletRequest request,
	                javax.servlet.http.HttpServletResponse response)
				   throws Exception {
					
						String receicedate1 = this.getRequestValue("receicedate1");
						String receicedate2 = this.getRequestValue("receicedate2");
						String projectid = this.getRequestValue("projectid");
						String ctype = this.getRequestValue("ctype");
						String receiceMoney1 = this.getRequestValue("receiceMoney1");
						String receiceMoney2 = this.getRequestValue("receiceMoney2");
						String customerId = this.getRequestValue("customerId");
						String entrustNumber = this.getRequestValue("entrustNumber");
						String reportNumber = this.getRequestValue("reportNumber");
						String departname = this.getRequestValue("departname");
						String accounttype = this.getRequestValue("accounttype");
						String ctypenumber = this.getRequestValue("ctypenumber");
						String receicedate = "";
						String receiceMoney = "";
						
						if (projectid != null && !"".equals(projectid)) {
							projectid = " and (p.projectid like '%" + projectid + "%' or p.projectname like '%"+projectid+"%') \n" ;
						}
						if (ctype != null && !"".equals(ctype)) {
							ctype = " and g.ctype = '" + ctype + "' \n" ;
						}
						
						if(receiceMoney1!=null && !"".equals(receiceMoney1) && receiceMoney2!=null && !"".equals(receiceMoney2)){
							receiceMoney = "  and ( g.receiceMoney between "+receiceMoney1+" and "+receiceMoney2+" ) ";
						}else{
							if(receiceMoney1!=null && !"".equals(receiceMoney1)){
								receiceMoney = "  and g.receiceMoney = "+receiceMoney1;
							}
							if(receiceMoney2!=null && !"".equals(receiceMoney2)){
								receiceMoney = "  and g.receiceMoney = "+receiceMoney2;
							}
						}
						
						if(receicedate1!=null && !"".equals(receicedate1) && receicedate2!=null && !"".equals(receicedate2)){
							receicedate = "  and ( g.receicedate between '"+receicedate1+"' and '"+receicedate2+" 24:00:00' ) ";
						}else{
							if(receicedate1!=null && !"".equals(receicedate1)){
								receicedate = "  and g.receicedate like '%"+receicedate1+"%'";
							}
							if(receicedate2!=null && !"".equals(receicedate2)){
								receicedate = "  and g.receicedate like '%"+receicedate2+"%'";
							}
						}
						
						if (customerId != null && !"".equals(customerId)) {
							customerId = " and (c.departname = '" + customerId + "' or c.departid = '"+customerId+"') \n";
						}
						if (entrustNumber != null && !"".equals(entrustNumber)) {
							entrustNumber = " and p.entrustNumber like '%" + entrustNumber + "%' \n";
						}
						if (reportNumber != null && !"".equals(reportNumber)) {
							reportNumber = " and p.reportNumber like '%" + reportNumber + "%' \n";
						}
						if (departname != null && !"".equals(departname)) {
							departname = " and (d.departname = '" + departname + "' or d.autoid = '"+departname+"') \n";
						}
						if (accounttype != null && !"".equals(accounttype)) {
							accounttype = " and (g.accounttype = '" + accounttype +"') \n";
						}
						if (ctypenumber != null && !"".equals(ctypenumber)) {
							ctypenumber = " and (g.ctypenumber = '" + ctypenumber +"') \n";
						}
						
						this.setOrAddRequestValue("projectid", projectid);
						this.setOrAddRequestValue("ctype", ctype);
						this.setOrAddRequestValue("receiceMoney", receiceMoney);
						this.setOrAddRequestValue("receicedate",receicedate);
						this.setOrAddRequestValue("customerId", customerId);
						this.setOrAddRequestValue("entrustNumber", entrustNumber);
						this.setOrAddRequestValue("reportNumber", reportNumber);
						this.setOrAddRequestValue("departname", departname);
						this.setOrAddRequestValue("accounttype", accounttype);
						this.setOrAddRequestValue("ctypenumber", ctypenumber);
				}
			};
 
			 
			// 必要设置
			pp.setTableID("GetFundsList");

			// 基本设置
			pp.setTrActionProperty(true);
			pp.setTrAction("style=\"cursor:hand;\" autoid='${autoid}'\" ");
			pp.setPageSize_CH(50);
		    pp.setPrintEnable(true);
		    pp.setPrintVerTical(false);
		    pp.setPrintTitle("收款列表");

			// sql设置 			
			String sql = " select g.*,p.projectname,p.entrustNumber,p.reportNumber,p.isStock,c.departname as customername,d.departname, " 
					   + " 		u1.name as name1,u2.name as name2 from k_getFunds g "
					   + " left join z_projectbusiness p on g.projectid = p.projectid "
					   + " left join k_customer c on p.payCustomerId = c.departid "
					   + " left join k_department d on p.departmentid = d.autoid "
					   + " left join k_user u1 on u1.id = p.signedCpa1 "
					   + " left join k_user u2 on u2.id = p.signedCpa2 "
					   + " where 1=1 and ctype!='手动' ${projectid} ${ctype} ${receicedate} ${receiceMoney} ${customerId} ${entrustNumber} ${reportNumber} ${departname} ${accounttype} ${ctypenumber} "; 
			
			pp.setSQL(sql); 
			pp.setOrderBy_CH("receicedate,autoid") ;
			pp.setDirection("desc,desc");
			pp.setInputType("radio");
			
			pp.addInputValue("receicedate1");
			pp.addInputValue("receicedate2");
			pp.addInputValue("receiceMoney1");
			pp.addInputValue("receiceMoney2");
			
			pp.addColumn("项目", "projectname");
			pp.addColumn("付款单位", "customername");
			pp.addColumn("收款金额", "receiceMoney","showMoney");
			pp.addColumn("收款日期", "receicedate");
			
			pp.addColumn("委托号", "entrustNumber");
			pp.addColumn("报告号", "reportNumber");
			pp.addColumn("账面分类", "accounttype");
			pp.addColumn("收款形式", "ctype");
			pp.addColumn("凭证号", "ctypenumber");
			pp.addColumn("签字cpa1", "name1");
			pp.addColumn("签字cpa2", "name2");
			pp.addColumn("企业是否具有证劵业务", "isStock");
			pp.setColumnWidth("20,10,10") ;
			pp.setWhichFieldIsValue(1);
			
			pp.addSqlWhere("projectid", "${projectid}");
			pp.addSqlWhere("ctype", "${ctype}");
			pp.addSqlWhere("receiceMoney", "${receiceMoney}");
			pp.addSqlWhere("receicedate", "${receicedate}");
			pp.addSqlWhere("customerId", "${customerId}");
			pp.addSqlWhere("entrustNumber", "${entrustNumber}");
			pp.addSqlWhere("reportNumber", "${reportNumber}");
			pp.addSqlWhere("departname", "${departname}");
			pp.addSqlWhere("accounttype", "${accounttype}");
			pp.addSqlWhere("ctypenumber", "${ctypenumber}");
			
			System.out.println(this.getClass()+"  |  sql = "+sql);

			request.getSession().setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);
			
		
			return new ModelAndView(LIST);
	}
	
	

	public ModelAndView rdList(HttpServletRequest request, HttpServletResponse response) throws Exception  {
		 
			DataGridProperty pp = new DataGridProperty() {
				public void onSearch(javax.servlet.http.HttpSession session,
	                javax.servlet.http.HttpServletRequest request,
	                javax.servlet.http.HttpServletResponse response)
				   throws Exception {
					
						String receicedate = this.getRequestValue("receicedate");
						String projectid = this.getRequestValue("projectid");
						String ctype = this.getRequestValue("ctype");
						String receiceMoney = this.getRequestValue("receiceMoney");
						String departname = this.getRequestValue("departname");
						
						if (projectid != null && !"".equals(projectid)) {
							projectid = " and (p.projectid like '%" + projectid + "%' or p.projectname like '%"+projectid+"%') \n" ;
						}
						if (ctype != null && !"".equals(ctype)) {
							ctype = " and g.ctype = '" + ctype + "' \n" ;
						}
						if (receiceMoney != null && !"".equals(receiceMoney)) {
							String[] receiceMoneys = receiceMoney.split("!");
							System.out.println(receiceMoneys[0]+"     | "+receiceMoneys[1]);
							receiceMoney = " and (g.receiceMoney between '" + receiceMoneys[0] + "' and '" + receiceMoneys[1] + "') \n";
						}
						if (receicedate != null && !"".equals(receicedate)) {
							String[] receicedates = receicedate.split("!");
							System.out.println(receicedates[0]+"     | "+receicedates[1]);
							receicedate = " and (g.receicedate between '" + receicedates[0] +"' and '" + receicedates[1] +"') \n";
						}
					
						if (departname != null && !"".equals(departname)) {
							departname = " and (d.departname = '" + departname + "' or d.autoid = '"+departname+"') \n";
						}
						
						this.setOrAddRequestValue("projectid", projectid);
						this.setOrAddRequestValue("ctype", ctype);
						this.setOrAddRequestValue("receiceMoney", receiceMoney);
						this.setOrAddRequestValue("receicedate",receicedate);
						this.setOrAddRequestValue("departname", departname);
				}
			};
 
			 
			// 必要设置
			pp.setTableID("GetFundsList");

			// 基本设置
			pp.setTrActionProperty(true);
			pp.setTrAction("style=\"cursor:hand;\" autoid='${autoid}'\" ");
			pp.setPageSize_CH(50);
		    pp.setPrintEnable(true);
		    pp.setPrintVerTical(false);
		    pp.setPrintTitle("收款列表");
		    pp.setColumnWidth("20,10,10") ;

			// sql设置 			
			String sql = " select g.*,p.projectname,d.departname " 
					   + " from k_getFunds g "
					   + " left join z_project p on g.projectid = p.projectid "
					   + " left join k_department d on p.departmentid = d.autoid "
					   + " where 1=1 ${projectid} ${ctype} ${receicedate} ${receiceMoney} ${departname} "; 
			
			pp.setSQL(sql); 
			pp.setOrderBy_CH("receicedate,autoid") ;
			pp.setDirection("desc,desc");
			pp.setInputType("radio");
			pp.addColumn("项目", "projectname");
			pp.addColumn("收款金额", "receiceMoney","showMoney");
			pp.addColumn("收款日期", "receicedate");
			pp.addColumn("收款形式", "ctype");
			
			pp.setWhichFieldIsValue(1);
			
			pp.addSqlWhere("projectid", "${projectid}");
			pp.addSqlWhere("ctype", "${ctype}");
			pp.addSqlWhere("receiceMoney", "${receiceMoney}");
			pp.addSqlWhere("receicedate", "${receicedate}");
			pp.addSqlWhere("departname", "${departname}");
			
			request.getSession().setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);
		
			return new ModelAndView(RDLIST);
	}
	
	
	/**
	 * 大华
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView dhList(HttpServletRequest request, HttpServletResponse response) throws Exception  {
		ASFuntion asf = new ASFuntion();
		Connection conn = null;
		String menuId = asf.showNull(request.getParameter("menuid"));
		UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
		DataGridProperty pp = new DataGridProperty() {
			public void onSearch(javax.servlet.http.HttpSession session,
                javax.servlet.http.HttpServletRequest request,
                javax.servlet.http.HttpServletResponse response)
			   throws Exception {
				
					String receicedate1 = this.getRequestValue("receicedate1");
					String receicedate2 = this.getRequestValue("receicedate2");
					String projectid = this.getRequestValue("projectid");
					String ctype = this.getRequestValue("ctype");
					String receiceMoney1 = this.getRequestValue("receiceMoney1");
					String receiceMoney2 = this.getRequestValue("receiceMoney2");
					String customerId = this.getRequestValue("customerId");
					String entrustNumber = this.getRequestValue("entrustNumber");
					String reportNumber = this.getRequestValue("reportNumber");
					String departname = this.getRequestValue("departname");
					String accounttype = this.getRequestValue("accounttype");
					String ctypenumber = this.getRequestValue("ctypenumber");
					String receicedate = "";
					String receiceMoney = "";
					
					if (projectid != null && !"".equals(projectid)) {
						projectid = " and (p.projectid like '%" + projectid + "%' or p.projectname like '%"+projectid+"%') \n" ;
					}
					if (ctype != null && !"".equals(ctype)) {
						ctype = " and g.ctype = '" + ctype + "' \n" ;
					}
					
					if(receiceMoney1!=null && !"".equals(receiceMoney1) && receiceMoney2!=null && !"".equals(receiceMoney2)){
						receiceMoney = "  and ( g.receiceMoney between "+receiceMoney1+" and "+receiceMoney2+" ) ";
					}else{
						if(receiceMoney1!=null && !"".equals(receiceMoney1)){
							receiceMoney = "  and g.receiceMoney = "+receiceMoney1;
						}
						if(receiceMoney2!=null && !"".equals(receiceMoney2)){
							receiceMoney = "  and g.receiceMoney = "+receiceMoney2;
						}
					}
					
					if(receicedate1!=null && !"".equals(receicedate1) && receicedate2!=null && !"".equals(receicedate2)){
						receicedate = "  and ( g.receicedate between '"+receicedate1+"' and '"+receicedate2+" 24:00:00' ) ";
					}else{
						if(receicedate1!=null && !"".equals(receicedate1)){
							receicedate = "  and g.receicedate like '%"+receicedate1+"%'";
						}
						if(receicedate2!=null && !"".equals(receicedate2)){
							receicedate = "  and g.receicedate like '%"+receicedate2+"%'";
						}
					}
					
					if (customerId != null && !"".equals(customerId)) {
						customerId = " and (c.departname = '" + customerId + "' or c.departid = '"+customerId+"') \n";
					}
					if (entrustNumber != null && !"".equals(entrustNumber)) {
						entrustNumber = " and p.entrustNumber like '%" + entrustNumber + "%' \n";
					}
					if (reportNumber != null && !"".equals(reportNumber)) {
						reportNumber = " and p.reportNumber like '%" + reportNumber + "%' \n";
					}
					if (departname != null && !"".equals(departname)) {
						departname = " and (d.departname = '" + departname + "' or d.autoid = '"+departname+"') \n";
					}
					if (accounttype != null && !"".equals(accounttype)) {
						accounttype = " and (g.accounttype = '" + accounttype +"') \n";
					}
					if (ctypenumber != null && !"".equals(ctypenumber)) {
						ctypenumber = " and (g.ctypenumber = '" + ctypenumber +"') \n";
					}
					
					this.setOrAddRequestValue("projectid", projectid);
					this.setOrAddRequestValue("ctype", ctype);
					this.setOrAddRequestValue("receiceMoney", receiceMoney);
					this.setOrAddRequestValue("receicedate",receicedate);
					this.setOrAddRequestValue("customerId", customerId);
					this.setOrAddRequestValue("entrustNumber", entrustNumber);
					this.setOrAddRequestValue("reportNumber", reportNumber);
					this.setOrAddRequestValue("departname", departname);
					this.setOrAddRequestValue("accounttype", accounttype);
					this.setOrAddRequestValue("ctypenumber", ctypenumber);
			}
		};

		try{
		conn = new DBConnect().getConnect("");
		String departments = asf.showNull(new UserPopedomService(conn).getUserPopedom(userSession.getUserId(), "GetFundsList"));
		// 必要设置
		pp.setTableID("GetFundsList");

		// 基本设置
		pp.setTrActionProperty(true);
		pp.setTrAction("style=\"cursor:hand;\" autoid='${autoid}'\" ");
		pp.setPageSize_CH(50);
	    pp.setPrintEnable(true);
	    pp.setPrintVerTical(false);
	    pp.setPrintTitle("收款列表");

		// sql设置 			
		String sql = " select g.*,p.projectname,p.entrustNumber,p.reportNumber,p.isStock,c.departname as customername,d.departname, " 
				   + " 		u1.name as name1,u2.name as name2 from k_getFunds g "
				   + " left join z_projectbusiness p on g.projectid = p.projectid "
				   + " left join k_customer c on p.payCustomerId = c.departid "
				   + " left join k_department d on p.departmentid = d.autoid "
				   + " left join k_user u1 on u1.id = p.signedCpa1 "
				   + " left join k_user u2 on u2.id = p.signedCpa2 "
				   + " where 1=1 and ctype!='手动' and (d.autoId in("+departments+") or g.createUser='"+userSession.getUserId()+"') ${projectid} ${ctype} ${receicedate} ${receiceMoney} ${customerId} ${entrustNumber} ${reportNumber} ${departname} ${accounttype} ${ctypenumber} "; 
		
		pp.setSQL(sql); 
		pp.setOrderBy_CH("receicedate,autoid") ;
		pp.setDirection("desc,desc");
		pp.setInputType("radio");
		
		pp.addInputValue("receicedate1");
		pp.addInputValue("receicedate2");
		pp.addInputValue("receiceMoney1");
		pp.addInputValue("receiceMoney2");
		
		pp.addColumn("项目", "projectname");
		pp.addColumn("付款单位", "customername");
		pp.addColumn("收款金额", "receiceMoney","showMoney");
		pp.addColumn("收款日期", "receicedate");
		
		pp.addColumn("委托号", "entrustNumber");
		pp.addColumn("报告号", "reportNumber");
		pp.addColumn("账面分类", "accounttype");
		pp.addColumn("收款形式", "ctype");
		pp.addColumn("凭证号", "ctypenumber");
		pp.addColumn("签字cpa1", "name1");
		pp.addColumn("签字cpa2", "name2");
		pp.addColumn("企业是否具有证劵业务", "isStock");
		pp.setColumnWidth("20,10,10") ;
		pp.setWhichFieldIsValue(1);
		
		pp.addSqlWhere("projectid", "${projectid}");
		pp.addSqlWhere("ctype", "${ctype}");
		pp.addSqlWhere("receiceMoney", "${receiceMoney}");
		pp.addSqlWhere("receicedate", "${receicedate}");
		pp.addSqlWhere("customerId", "${customerId}");
		pp.addSqlWhere("entrustNumber", "${entrustNumber}");
		pp.addSqlWhere("reportNumber", "${reportNumber}");
		pp.addSqlWhere("departname", "${departname}");
		pp.addSqlWhere("accounttype", "${accounttype}");
		pp.addSqlWhere("ctypenumber", "${ctypenumber}");
		
		System.out.println(this.getClass()+"  |  sql = "+sql);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
			request.getSession().setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);
		}
	
		return new ModelAndView(DHLIST);
	}
	

	/**
	 * 大华： 收款新增
	 */
	public ModelAndView goDH(HttpServletRequest request,HttpServletResponse response) throws Exception{
		
		ModelAndView model = new ModelAndView(DHAddAndEdit);
		ASFuntion CHF = new ASFuntion();
		
		String opt = CHF.showNull(request.getParameter("opt"));
		String autoid = CHF.showNull(request.getParameter("id"));
		
		System.out.println("opt="+opt+"   autoid="+autoid);
		
		Connection conn = null;
		try {
			conn=new DBConnect().getConnect("");
			
			GetFundsTable gt = null;
			
			if("update".equalsIgnoreCase(opt)){
				GetFundService gfs = new GetFundService(conn);
				gt = gfs.getGetFundsTableByAutoid(autoid);
				
				BusinessProjectService bps = new BusinessProjectService(conn) ;
				if(!"".equals(gt.getProjectid())){
					String projectName = bps.get(gt.getProjectid()).getProjectName() ;
					model.addObject("projectName", projectName);
				}
				model.addObject("getFunds", gt);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		model.addObject("now", CHF.getCurrentDate());
		model.addObject("opt", opt);
		return model;
	}
	
	
	/**
	 * 添加
	 */
	public ModelAndView add(HttpServletRequest request,HttpServletResponse response) throws Exception{
		response.setContentType("text/html;charset=utf-8");
		ASFuntion CHF = new ASFuntion();
		
		UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
		String userid = userSession.getUserId();
		
		//基础信息
		String projectid = CHF.showNull(request.getParameter("projectid"));
		String ctype = CHF.showNull(request.getParameter("ctype"));
		String ctypenumber = CHF.showNull(request.getParameter("ctypenumber"));
		String receiceMoney = CHF.showNull(request.getParameter("receiceMoney"));
		String receicedate = CHF.showNull(request.getParameter("receicedate"));
		String accounttype = CHF.showNull(request.getParameter("accounttype"));
		String remark = CHF.showNull(request.getParameter("remark"));
		
		String date = CHF.getCurrentDate();
		String time = CHF.getCurrentTime();
		
		Connection conn = null;
		try {
			conn=new DBConnect().getConnect("");
			GetFundsTable gt = new GetFundsTable();
			gt.setProjectid(projectid);
			gt.setCtype(ctype);
			gt.setCtypenumber(ctypenumber);
			gt.setReceiceMoney(receiceMoney);
			gt.setReceicedate(receicedate);
			gt.setAccounttype(accounttype);
			gt.setRemark(remark);
			gt.setProperty(userid+","+date+" "+time);
			
			// 新增收款
			GetFundService gs = new GetFundService(conn);
			gs.addGetFunds(gt);
			
			// 修改 已收款金额
			BusinessProjectService bs = new BusinessProjectService(conn);
			bs.updateReceiveMoneyByProjectId(projectid,receicedate);
			
			//得到z_projectext收款金额
			try{
				DbUtil db = new DbUtil(conn);
				String sql = "";
				AuditPlaformService aps = new AuditPlaformService(conn);
				if(!projectid.equals(aps.getProjectext(projectid,"projectid"))){
					//没有这个项目,就新增这个项目
					sql = "insert into z_projectext (projectid) value (?) ";
					Object[] args = new Object[] {projectid};
					db.execute(sql,args);
				}
				sql = "select receicemoney from z_projectbusiness where projectid = ? ";
				String receicemoney = db.queryForString(sql, new String []{projectid});
				aps.updateProjectext(projectid, "price", receicemoney);	
			}catch(Exception e) {}
			
			response.sendRedirect(request.getContextPath()+"/getFunds.do") ;
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		return null;
	}
	
	
	/**
	 * 修改
	 */
	public ModelAndView update(HttpServletRequest request,HttpServletResponse response) throws Exception{
		response.setContentType("text/html;charset=utf-8");
		ASFuntion CHF = new ASFuntion();
		
		//基础信息
		
		String autoid = CHF.showNull(request.getParameter("autoid"));
		String projectid = CHF.showNull(request.getParameter("projectid"));
		String ctype = CHF.showNull(request.getParameter("ctype"));
		String ctypenumber = CHF.showNull(request.getParameter("ctypenumber"));
		String receiceMoney = CHF.showNull(request.getParameter("receiceMoney"));
		String receicedate = CHF.showNull(request.getParameter("receicedate"));
		String accounttype = CHF.showNull(request.getParameter("accounttype"));
		String remark = CHF.showNull(request.getParameter("remark"));
		
		Connection conn = null;
		try {
			conn=new DBConnect().getConnect("");
			GetFundsTable gt = new GetFundsTable();
			gt.setAutoid(autoid);
			gt.setProjectid(projectid);
			gt.setCtype(ctype);
			gt.setCtypenumber(ctypenumber);
			gt.setReceiceMoney(receiceMoney);
			gt.setReceicedate(receicedate);
			gt.setAccounttype(accounttype);
			gt.setRemark(remark);
			GetFundService gs = new GetFundService(conn);
			
			// 修改收款
			gs.updateGetFunds(gt); 
			
			// 修改已收款金额
			BusinessProjectService bs = new BusinessProjectService(conn);
			bs.updateReceiveMoneyByProjectId(projectid,receicedate);
			
			//得到z_projectext收款金额
			try{
				DbUtil db = new DbUtil(conn);
				String sql = "";
				AuditPlaformService aps = new AuditPlaformService(conn);
				if(!projectid.equals(aps.getProjectext(projectid,"projectid"))){
					//没有这个项目,就新增这个项目
					sql = "insert into z_projectext (projectid) value (?) ";
					Object[] args = new Object[] {projectid};
					db.execute(sql,args);
				}
				sql = "select receicemoney from z_projectbusiness where projectid = ? ";
				String receicemoney = db.queryForString(sql, new String []{projectid});
				aps.updateProjectext(projectid, "price", receicemoney);	
			}catch(Exception e) {}
			
			response.sendRedirect(request.getContextPath()+"/getFunds.do") ;
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		return null;
	}
	
	
	
	/**
	 * 大华 保存
	 */
	public ModelAndView save(HttpServletRequest request,HttpServletResponse response) throws Exception{
		response.setContentType("text/html;charset=utf-8");
		ASFuntion CHF = new ASFuntion();
		
		UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
		String userid = userSession.getUserId();
		
		String opt = CHF.showNull(request.getParameter("opt"));
		//基础信息
		String autoid = CHF.showNull(request.getParameter("autoid"));
		String projectid = CHF.showNull(request.getParameter("projectid"));
		//String departname = CHF.showNull(request.getParameter("departname"));
		String[] ctype = request.getParameterValues("ctype");
		String ctypenumber = CHF.showNull(request.getParameter("ctypenumber"));
		String[] receiceMoney = request.getParameterValues("receiceMoney");
		String[] receicedate = request.getParameterValues("receicedate");
		String[] accounttype = request.getParameterValues("accounttype");
		String remark = CHF.showNull(request.getParameter("remark"));
		String[] invoicenumber = request.getParameterValues("invoicenumber");
		String[] customerCode = request.getParameterValues("customerCode"); //开票客户代码
		String[] certificateNumber = request.getParameterValues("certificateNumber");
		
		String date = CHF.getCurrentDate();
		String time = CHF.getCurrentTime();
		
		Connection conn = null;
		try {
			conn=new DBConnect().getConnect("");
			
			GetFundService gs = new GetFundService(conn);
			
			if(!"".equals(autoid)){
				gs.delGetFunds(autoid);
			}
			for (int i = 0; i < receiceMoney.length; i++) {
				GetFundsTable gt = new GetFundsTable();
				gt.setProjectid(projectid);
				//gt.setContinueDepartId(departname);
				if( ctype !=null){
					gt.setCtype(ctype[i]);
				}
				if( ctypenumber !=null){
					gt.setCtypenumber(ctypenumber);
				}
				if( receiceMoney !=null){
					gt.setReceiceMoney(receiceMoney[i]);
				}
				if( receicedate !=null){
					gt.setReceicedate(receicedate[i]);
				}
				if( accounttype !=null){
					gt.setAccounttype(accounttype[i]);
				}
				gt.setRemark(remark);
				if( customerCode !=null){
					gt.setCustomerCode(customerCode[i]);
				}
				gt.setCreateUser(userid);
				gt.setProperty(userid+","+date+" "+time);
				if(invoicenumber !=null){
					gt.setInvoicenumber(invoicenumber[i]);
				}
				if(certificateNumber !=null){
					gt.setCertificateNumber(certificateNumber[i]);
				}
				// 新增收款
				gs.addGetFunds(gt);
				
				// 修改 已收款金额
				BusinessProjectService bs = new BusinessProjectService(conn);
				
				bs.updateReceiveMoneyByProjectId(projectid,receicedate[i]);
				
				//得到z_projectext收款金额
				try{
					DbUtil db = new DbUtil(conn);
					String sql = "";
					AuditPlaformService aps = new AuditPlaformService(conn);
					if(!projectid.equals(aps.getProjectext(projectid,"projectid"))){
						//没有这个项目,就新增这个项目
						sql = "insert into z_projectext (projectid) value (?) ";
						Object[] args = new Object[] {projectid};
						db.execute(sql,args);
					}
					sql = "select receicemoney from z_projectbusiness where projectid = ? ";
					String receicemoney = db.queryForString(sql, new String []{projectid});
					aps.updateProjectext(projectid, "price", receicemoney);	
				}catch(Exception e) {}
			}
			
			if("saveAndInvoice".equalsIgnoreCase(opt)){
				response.sendRedirect(request.getContextPath()+"/invoice.do?method=saveGetFundsToInvoice&projectid="+projectid) ;
			}else{
				response.sendRedirect(request.getContextPath()+"/getFunds.do") ;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		return null;
	}
	

	/**
	 * 根据编号得到对象的方法
	 */
	public ModelAndView getGetFundsById(HttpServletRequest request,HttpServletResponse response){
		ModelAndView model=new ModelAndView(AddAndEdit);
		String autoid=request.getParameter("autoid");
		Connection conn=null;
		GetFundsTable gt=null;
		try {
			conn=new DBConnect().getConnect("");
			GetFundService gs = new GetFundService(conn);
			gt = gs.getGetFundsTableByAutoid(autoid);
			
			BusinessProjectService bps = new BusinessProjectService(conn) ;
			String projectName = bps.get(gt.getProjectid()).getProjectName() ;
			model.addObject("projectName", projectName);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		model.addObject("getFunds", gt);
		return model;
	}
	
	/**
	 * 根据编号得到对象的方法
	 */
	public ModelAndView rdGetGetFundsById(HttpServletRequest request,HttpServletResponse response){
		ModelAndView model=new ModelAndView(RDAddAndEdit);
		String autoid=request.getParameter("autoid");
		Connection conn=null;
		GetFundsTable gt=null;
		ASFuntion CHF = new ASFuntion() ;
		try {
			conn=new DBConnect().getConnect("");
			GetFundService gs = new GetFundService(conn);
			gt = gs.getGetFundsTableByAutoid(autoid);

			// 得到已收款金额
			String strSql = "select price from z_projectext where projectid = '"+gt.getProjectid()+"'";
			String price = CHF.showNull(new DbUtil(conn).queryForString(strSql));
			if("".equals(price)) {
				price = "0" ;
			}
			
			// 得到已收款金额
			strSql = "select sum(receiceMoney) from k_getfunds where projectid = '"+gt.getProjectid()+"'";
			String receiceMoney = CHF.showNull(new DbUtil(conn).queryForString(strSql));
			if("".equals(receiceMoney)) {
				receiceMoney = "0" ;
			}
			
			model.addObject("price", price);
			model.addObject("receiceMoney", receiceMoney);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		model.addObject("getFunds", gt);
		return model;
	}
	
	
	/**
	 * 根据项目编号得到项目信息
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView getMoney(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		response.setContentType("text/html;charset=utf-8");
		Connection conn = null;
		ASFuntion CHF = new ASFuntion();
		PrintWriter out  = response.getWriter() ;
		try {
			conn = new DBConnect().getConnect("");
			String projectid = CHF.showNull(request.getParameter("projectid")) ;
			
			// 得到已收款金额
			String strSql = "select price from z_projectext where projectid = '"+projectid+"'";
			String price = CHF.showNull(new DbUtil(conn).queryForString(strSql));
			if("".equals(price)) {
				price = "0" ;
			}
			
			// 得到已收款金额
			strSql = "select sum(receiceMoney) from k_getfunds where projectid = '"+projectid+"'";
			String receiceMoney = CHF.showNull(new DbUtil(conn).queryForString(strSql));
			if("".equals(receiceMoney)) {
				receiceMoney = "0" ;
			}
			
			out.write("{price:"+price+",receiceMoney:"+receiceMoney+"}") ;
			out.close() ;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}
		
		return null;
	}
	
	/**
	 * 根据项目编号得到项目信息
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView getProjectInfoJson(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		response.setContentType("text/html;charset=utf-8");
		Connection conn = null;
		ASFuntion CHF = new ASFuntion();
		PrintWriter out  = response.getWriter() ;
		try {
			conn = new DBConnect().getConnect("");
			GetFundService gs = new GetFundService(conn);
			String projectid = CHF.showNull(request.getParameter("projectid")) ;
			String json = gs.getProjectInfoJson(projectid);
			out.write(json) ;
			out.close() ;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}
		
		return null;
	}
	
	
	
	/**
	 * 业务收入统计 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView incomeCount(HttpServletRequest request, HttpServletResponse response) throws Exception  {
		ModelAndView modelAndView=new ModelAndView(IncomeCountList);
		
		Connection conn = null;
		UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
		DataGridProperty pp = new DataGridProperty() {
				public void onSearch(javax.servlet.http.HttpSession session,
	                javax.servlet.http.HttpServletRequest request,
	                javax.servlet.http.HttpServletResponse response)
				   throws Exception {
						
						// 项目
						String projectid = this.getRequestValue("projectid");
						// 收款日期
						String receicedate = this.getRequestValue("receicedate");
						// 付款单位
						String customername = this.getRequestValue("customername");
						// 委托内容
						String auditpara = this.getRequestValue("auditpara");
						// 项目负责人
						String name = this.getRequestValue("name");
						// 承接部门
						String departname = this.getRequestValue("departname");
						// 委托号
						String entrustNumber = this.getRequestValue("entrustNumber");
						// 报告号
						String reportNumber = this.getRequestValue("reportNumber");
						
						String accounttype = this.getRequestValue("accounttype");
						String ctypenumber = this.getRequestValue("ctypenumber");
						
						String receiceMoney1 = this.getRequestValue("receiceMoney1");
						String receiceMoney2 = this.getRequestValue("receiceMoney2");
						
						String ctype = this.getRequestValue("ctype");
						
						String receiceMoney = "" ;
						
						String receicedate1=this.getRequestValue("receicedate1");
						String receicedate2=this.getRequestValue("receicedate2");
						
						if (projectid != null && !"".equals(projectid)) {
							projectid = " and (p.projectid like '%" + projectid + "%' or p.projectname like '%"+projectid+"%') \n" ;
						}
						
						if (receicedate != null && !"".equals(receicedate)) {
							String[] receicedates = receicedate.split("!");
							receicedate = " and (g.receicedate between '" + receicedates[0] +"' and '" + receicedates[1] +"') \n";
						}
						
						if (customername != null && !"".equals(customername)) {
							customername = " and (c.departid = '"+customername+"' or c.departname = '" + customername + "') \n" ;
						}
						 
						if (auditpara != null && !"".equals(auditpara)) {
							auditpara = " and p.auditpara = '" + auditpara + "' \n";
						}
						
						if (name != null && !"".equals(name)) {
							name = " and (u.id = '"+name+"' or u.name = '" + name + "') \n";
						}
						
						if (departname != null && !"".equals(departname)) {
							departname = " and (d.autoid = '"+departname+"' or d.departname = '" + departname + "') \n";
						}
						
						if(receiceMoney1!=null && !"".equals(receiceMoney1) && receiceMoney2!=null && !"".equals(receiceMoney2)){
							receiceMoney = "  and g.receiceMoney between "+receiceMoney1+" and "+receiceMoney2+" ";
						}else{
							if(receiceMoney1!=null && !"".equals(receiceMoney1)){
								receiceMoney = "  and g.receiceMoney = "+receiceMoney1;
							}
							if(receiceMoney2!=null && !"".equals(receiceMoney2)){
								receiceMoney = "  and g.receiceMoney = "+receiceMoney2;
							}
						}
						
						if (entrustNumber != null && !"".equals(entrustNumber)) {
							entrustNumber = " and p.entrustNumber like '%" + entrustNumber + "%' \n";
						}
						if (reportNumber != null && !"".equals(reportNumber)) {
							reportNumber = " and p.reportNumber like '%" + reportNumber + "%' \n";
						}
						if (accounttype != null && !"".equals(accounttype)) {
							accounttype = " and (g.accounttype = '" + accounttype +"') \n";
						}
						if (ctypenumber != null && !"".equals(ctypenumber)) {
							ctypenumber = " and (g.ctypenumber = '" + ctypenumber +"') \n";
						}
						
						if (ctype != null && !"".equals(ctype)) {
							ctype = " and g.ctype = '" + ctype + "' \n" ;
						}
						
						this.setOrAddRequestValue("projectid", projectid);
						this.setOrAddRequestValue("receicedate", receicedate);
						this.setOrAddRequestValue("customername", customername);
						this.setOrAddRequestValue("auditpara", auditpara);
						this.setOrAddRequestValue("name", name); 
						this.setOrAddRequestValue("departname", departname); 
						
						this.setOrAddRequestValue("ctype", ctype);
						this.setOrAddRequestValue("receiceMoney", receiceMoney);
						this.setOrAddRequestValue("entrustNumber", entrustNumber);
						this.setOrAddRequestValue("reportNumber", reportNumber);
						this.setOrAddRequestValue("accounttype", accounttype);
						this.setOrAddRequestValue("ctypenumber", ctypenumber);
						
						this.setOrAddRequestValue("receicedate1", receicedate1);
						this.setOrAddRequestValue("receicedate2", receicedate2);
				}
			};
			try{
			 conn = new DBConnect().getConnect("");
			 ASFuntion asf = new ASFuntion();
			 String departments = asf.showNull(new UserPopedomService(conn).getUserPopedom(userSession.getUserId(), "IncomCountList"));
			// 必要设置
			pp.setTableID("IncomCountList");

			// 基本设置
			pp.setTrActionProperty(true);
			pp.setTrAction("style=\"cursor:hand;\" autoid='${autoid}'\" ");
			pp.setPageSize_CH(50);
		    pp.setPrintEnable(true);
		    pp.setPrintVerTical(false);
		    pp.setPrintTitle("业务收入统计");

		 // sql设置 			
			String sql = " select g.autoid,p.projectname,c.departname as customername,p.auditpara,p.isStock,ifnull(g.receicemoney,0) as receicemoney,g.receicedate," 
					   + " u.name,p.entrustNumber,p.reportNumber,p.isNewTakeProject,d.departname,g.accounttype,g.ctype,g.ctypenumber,"
					   + " reporttype,e.name as signedCpa1Nmae,f.name as signedCpa2Name,h.departname as beiCustomerName,g.customerCode "
					   + " from k_getfunds g "
					   + " left join z_projectbusiness p on g.projectid = p.projectid "
					   + " left join k_customer c on p.payCustomerId = c.departid "
					   + " left join k_department d on p.departmentid = d.autoid "
					   + " left join k_user u on p.managerUserId = u.id "
					   + " left join k_user e on p.signedCpa1 = e.id "
					   + " left join k_user f on p.signedCpa2 = f.id "
					   + " left join k_customer h on p.customerId =h.departid "
					   + " where 1=1 and d.autoId in ("+departments+") ${projectid} ${receicedate} ${customername} ${auditpara} ${name} ${departname} ${receiceMoney} ${entrustNumber} ${reportNumber} ${accounttype} ${ctypenumber} ${ctype} "; 
			
			String newSql = " select * from ( "+sql+" union select 0,'','','','',sum(a.receicemoney),'合计','','','','','','','','','','','','','' from ("+sql+")a )a ";
			
			pp.setSQL(newSql); 
			pp.setOrderBy_CH("autoid,receicedate") ;
			pp.setDirection("asc,desc");
			//pp.setInputType("radio");
			//pp.addColumn("项目", "projectname");
			pp.addColumn("收款日期", "receicedate");
			pp.addColumn("付款单位", "customername");
			pp.addColumn("被审单位", "beiCustomerName");
			pp.addColumn("客户代码", "customerCode");
			
			pp.addColumn("业务类型", "auditpara");
			pp.addColumn("收款金额", "receicemoney","showMoney");
			pp.addColumn("项目负责人", "name");
			pp.addColumn("委托号", "entrustNumber");
			pp.addColumn("报告号", "reportNumber");
			pp.addColumn("是否新项目", "isNewTakeProject");
			pp.addColumn("承接部门", "departname");
			pp.addColumn("账面分类", "accounttype");
			pp.addColumn("收款方式", "ctype");
			pp.addColumn("凭证号", "ctypenumber");
			pp.addColumn("出具报告类型", "reporttype");
			pp.addColumn("签字CPA1", "signedCpa1Nmae");
			pp.addColumn("签字CPA2", "signedCpa2Name");
			pp.addColumn("企业是否具有证劵业务", "isStock");
			
			pp.setWhichFieldIsValue(1);

			pp.addSqlWhere("projectid", "${projectid}");
			pp.addSqlWhere("receicedate", "${receicedate}");
			pp.addSqlWhere("customername", "${customername}");
			pp.addSqlWhere("auditpara", "${auditpara}");
			pp.addSqlWhere("name", "${name}");
			pp.addSqlWhere("departname", "${departname}");
			pp.addSqlWhere("ctype", "${ctype}");
			pp.addSqlWhere("receiceMoney", "${receiceMoney}");
			pp.addSqlWhere("entrustNumber", "${entrustNumber}");
			pp.addSqlWhere("reportNumber", "${reportNumber}");
			pp.addSqlWhere("accounttype", "${accounttype}");
			pp.addSqlWhere("ctypenumber", "${ctypenumber}");
			pp.addInputValue("receiceMoney1");
			pp.addInputValue("receiceMoney2");
			pp.addInputValue("receicedate1");
			pp.addInputValue("receicedate2");
				
			
			System.out.println(this.getClass()+"  |  sql = "+sql);
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				DbUtil.close(conn);
				request.getSession().setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);
			}
		
//			Connection conn = null;
//			try {
//				conn=new DBConnect().getConnect("");
//				GetFundService gf=new GetFundService(conn);
//				String countMoney=gf.getCount("${receicedate1}", "${receicedate2}");
//				modelAndView.addObject("countMoney", countMoney);
//			} catch (Exception e) {
//				e.printStackTrace();
//			} finally{
//				DbUtil.close(conn);
//			}
			return modelAndView;
	}
	
	

	
	/**
	 * 收款总揽 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView incomeOverrall(HttpServletRequest request, HttpServletResponse response) throws Exception  {
		ModelAndView model = new ModelAndView(IncomeOverrallList);
		UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
		Connection conn = null;
			DataGridProperty pp = new DataGridProperty() {
				public void onSearch(javax.servlet.http.HttpSession session,
	                javax.servlet.http.HttpServletRequest request,
	                javax.servlet.http.HttpServletResponse response)
				   throws Exception {
						// 项目
						String projectid = this.getRequestValue("projectid");
						// 项目负责人
						String managerUserId = this.getRequestValue("managerUserId");
						// 付款单位
						String customername = this.getRequestValue("customername");
						// 委托内容
						String auditpara = this.getRequestValue("auditpara");
						// 委托号
						String entrustNumber = this.getRequestValue("entrustNumber");
						// 报告号
						String reportNumber = this.getRequestValue("reportNumber");
						// 承接部门
						String departname = this.getRequestValue("departname");
						// 业务费用
						String businesscost1 = this.getRequestValue("businesscost1");
						String businesscost2 = this.getRequestValue("businesscost2");
						// 业务费用
						String createDate1 = this.getRequestValue("createDate1");
						String createDate2 = this.getRequestValue("createDate2");
						
						String ctypenumber = this.getRequestValue("ctypenumber");
						
						String businesscost = "";
						String createDate = "";
						
						if (projectid != null && !"".equals(projectid)) {
							projectid = " and (a.projectid like '%" + projectid + "%' or a.projectname like '%"+projectid+"%') \n" ;
						}
						if (managerUserId != null && !"".equals(managerUserId)) {
							managerUserId = " and (a.managerUserId like '%" + managerUserId + "%' or u.name like '%"+managerUserId+"%')  \n" ;
						}
						
						if (entrustNumber != null && !"".equals(entrustNumber)) {
							entrustNumber = " and a.entrustNumber like '%" + entrustNumber +"%' \n";
						}
						
						if (customername != null && !"".equals(customername)) {
							customername = " and (c.departid = '"+customername+"' or c.departname = '" + customername + "') \n" ;
						}
						 
						if (auditpara != null && !"".equals(auditpara)) {
							auditpara = " and a.auditpara = '" + auditpara + "' \n";
						}
						
						if (reportNumber != null && !"".equals(reportNumber)) {
							reportNumber = " and a.reportNumber like '%" + reportNumber + "%' \n";
						}
						
						
						if (departname != null && !"".equals(departname)) {
							departname = " and  (d.autoid = '"+departname+"' or d.departname = '" + departname + "')\n";
						}
						
						if (businesscost1 != null && !"".equals(businesscost1) && businesscost2 != null && !"".equals(businesscost2)) {
							businesscost = " and (replace(a.businesscost,',','') >= "+businesscost1+" and replace(a.businesscost,',','') <= " + businesscost2 + ") \n";
						}
						if ((businesscost1 != null && !"".equals(businesscost1)) && (businesscost2 == null || "".equals(businesscost2))) {
							businesscost = " and (replace(a.businesscost,',','') = '"+businesscost1+"') \n";
						}
						if ((businesscost1 == null || "".equals(businesscost1)) && (businesscost2 != null && !"".equals(businesscost2))) {
							businesscost = " and (replace(a.businesscost,',','') = '"+businesscost2+"') \n";
						}
						
						if (createDate1 != null && !"".equals(createDate1) && createDate2 != null && !"".equals(createDate2)) {
							createDate = " and (a.createTime >= '"+createDate1+"' and a.createTime <= '" + createDate2 + " 99:99:99') \n";
						}
						if ((createDate1 != null && !"".equals(createDate1)) && (createDate2 == null || "".equals(createDate2))) {
							createDate = " and (a.createTime = '"+createDate1+"') \n";
						}
						if ((createDate1 == null || "".equals(createDate1)) && (createDate2 != null && !"".equals(createDate2))) {
							createDate = " and (a.createTime = '"+createDate2+"') \n";
						}
						
						if ((ctypenumber != null && !"".equals(ctypenumber))) {
							ctypenumber = " and b.ctypenumber like '%"+ctypenumber+"%' \n";
						}
						
						
						
						this.setOrAddRequestValue("projectid", projectid);
						this.setOrAddRequestValue("entrustNumber", entrustNumber);
						this.setOrAddRequestValue("customername", customername);
						this.setOrAddRequestValue("auditpara", auditpara);
						this.setOrAddRequestValue("reportNumber", reportNumber); 
						this.setOrAddRequestValue("departname", departname); 
						this.setOrAddRequestValue("businesscost", businesscost); 
						this.setOrAddRequestValue("createDate", createDate); 
						this.setOrAddRequestValue("managerUserId", managerUserId); 
						this.setOrAddRequestValue("ctypenumber", ctypenumber); 
						
				}
			};
 
			try{ 
			conn = new DBConnect().getConnect("");
			ASFuntion asf = new ASFuntion();
			String departments = asf.showNull(new UserPopedomService(conn).getUserPopedom(userSession.getUserId(), "IncomeOverrallList"));
			// 必要设置
			pp.setTableID("IncomeOverrallList");

			// 基本设置
			pp.setTrActionProperty(true);
			pp.setTrAction("style=\"cursor:hand;\" projectname='${projectname}' projectid='${projectid}'\" ");
			pp.setPageSize_CH(50);
		    pp.setPrintEnable(true);
		    pp.setPrintVerTical(false);
		    pp.setPrintTitle("收款总览");
		    //pp.setLimitByOwnEnable(true);
		    
		    
		    StringBuffer sql = new StringBuffer();
		     
	    	sql.append( "  SELECT a.*,c.DepartName as customername,d.departname,g.name,b.ctype,b.ctypenumber,b.receicedate,b.accounttype,e.name as signedCpa1Nmae,f.name as signedCpa2Name,u.name as username, ");
		    //sql.append( " (SELECT a.*,");
		    sql.append( " CASE replace(a.businessCost,',','') WHEN NULL THEN 0 WHEN '' THEN 0 ELSE replace(a.businessCost,',','') END AS businessCosts, \n") ;
		    sql.append( " (replace(a.businessCost,',','')*1-a.receicemoney*1) AS receicemoneys,  \n") ;
		    sql.append( " (replace(a.businessCost,',','')*1-a.money*1) AS moneys  \n") ;
		    //sql.append( " FROM z_projectbusiness a ${LIMIT} )a \n") ;
		    sql.append( " FROM z_projectbusiness a \n") ;
		    sql.append( " left JOIN ( \n") ;
		    sql.append( " SELECT max(receicedate) as receicedate,GROUP_CONCAT(accounttype) as  accounttype,createuser,GROUP_CONCAT(ctype) AS ctype,GROUP_CONCAT(ctypenumber) AS ctypenumber,projectid FROM k_getfunds GROUP BY projectid \n") ;
		    sql.append( " ) b ON a.projectid = b.projectid \n") ;
		    sql.append( " LEFT JOIN k_customer c ON a.payCustomerId = c.departid \n") ;
		    sql.append( " LEFT JOIN k_department d ON a.departmentid = d.autoid \n") ;
		    sql.append( " left join k_user u on a.managerUserId = u.id \n");
		    sql.append( " left join k_user e on a.signedCpa1 = e.id \n");
		    sql.append( " left join k_user f on a.signedCpa2 = f.id \n");
		    sql.append( " left join k_user g on b.createuser = g.id \n ");
		    sql.append( " WHERE 1=1 and d.autoId in ("+departments+")\n") ;
		    sql.append( " ${projectid} ${entrustNumber} ${customername} ${auditpara} ${reportNumber} ${departname} ${businesscost} ${createDate} ${managerUserId} ${ctypenumber} \n") ;
	    
		     
		    pp.setSQL(sql.toString()); 
			pp.setOrderBy_CH("lastshoukuandate") ;
			pp.setDirection("desc");
			//pp.setInputType("radio");
			pp.addColumn("项目名称", "projectname");
			pp.addColumn("付款单位", "customername");
			pp.addColumn("承接部门", "departname");
			pp.addColumn("经办人","name");
			pp.addColumn("项目负责人", "username");
			pp.addColumn("委托号", "entrustNumber");
			pp.addColumn("报告号", "reportNumber");
			pp.addColumn("建项时间", "createtime");
			pp.addColumn("业务约定收费金额", "businessCosts","showMoney");
			pp.addColumn("已收款(元)", "receicemoney","showMoney");
			pp.addColumn("未收款(元)", "receicemoneys","showMoney");
			pp.addColumn("已开票(元)", "money","showMoney");
			pp.addColumn("未开票(元)", "moneys","showMoney");
			pp.addColumn("委托内容", "auditpara");
			pp.addColumn("出具报告类型", "reportType");
			pp.addColumn("账面分类", "accounttype");
			pp.addColumn("收款方式", "ctype");
			pp.addColumn("凭证号", "ctypenumber");
			pp.addColumn("签字CPA1", "signedCpa1Nmae");
			pp.addColumn("签字CPA2", "signedCpa2Name");
			pp.addColumn("企业性质", "companytype");
			pp.addColumn("企业是否具有证劵业务", "isStock");
			
			pp.setWhichFieldIsValue(1);
			
			pp.addSqlWhere("projectid", "${projectid}");
			pp.addSqlWhere("entrustNumber", "${entrustNumber}");
			pp.addSqlWhere("customername", "${customername}");
			pp.addSqlWhere("auditpara", "${auditpara}");
			pp.addSqlWhere("reportNumber", "${reportNumber}");
			pp.addSqlWhere("departname", "${departname}");
			pp.addSqlWhere("businesscost", "${businesscost}");
			pp.addSqlWhere("createDate", "${createDate}");
			pp.addSqlWhere("managerUserId", "${managerUserId}");
			pp.addSqlWhere("ctypenumber", "${ctypenumber}");
			
			pp.addInputValue("businesscost1");
			pp.addInputValue("businesscost2");
			
			pp.addInputValue("createDate1");
			pp.addInputValue("createDate2");
			
			
			System.out.println(this.getClass()+"       　　#### 222222222222222222233344 ｓｑｌ　＝　"+sql);
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				DbUtil.close(conn);
				request.getSession().setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);
			}
			return model;
	}
	
	
	/**
	 * 得到项目信息
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView goProject(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView(businessEdit);
		Connection conn = null;
		ASFuntion CHF = new ASFuntion();
		try {
			conn = new DBConnect().getConnect("");
			BusinessProjectService bps = new BusinessProjectService(conn) ;
			ProjectService ps = new ProjectService(conn) ;
			String projectId = CHF.showNull(request.getParameter("projectid")) ;
			System.out.println(this.getClass()+  "         |projectid="+projectId);
			BusinessProject bp = bps.get(projectId) ;
			Project project = ps.getProjectById(projectId) ;
			modelAndView.addObject("bp", bp) ;
			modelAndView.addObject("project",project) ;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}
		
		return modelAndView;
	}
	 
	
	/**
	 * 得到项目信息
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView getProject(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView(businessEdit);
		Connection conn = null;
		ASFuntion CHF = new ASFuntion();
		try {
			conn = new DBConnect().getConnect("");
			BusinessProjectService bps = new BusinessProjectService(conn) ;
			ProjectService ps = new ProjectService(conn) ;
			String projectId = CHF.showNull(request.getParameter("projectid")) ;
			System.out.println(this.getClass()+  "         |projectid="+projectId);
			BusinessProject bp = bps.get(projectId) ;
			Project project = ps.getProjectById(projectId) ;
			modelAndView.addObject("bp", bp) ;
			modelAndView.addObject("project",project) ;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}
		
		return modelAndView;
	}
	 
	
	/**
	 * 查看金额
	 * @throws IOException 
	 */
	public ModelAndView seeMoney(HttpServletRequest request,HttpServletResponse response) throws IOException{
		response.setContentType("text/html;charset=utf-8");  //设置编码
		PrintWriter out = response.getWriter();
		
		String autoid = request.getParameter("autoid");
		
		Connection conn=null; 
		try {
			conn=new DBConnect().getConnect("");
			String strSql = "select receiceMoney from k_getFunds where autoid = "+autoid;
			String receiceMoney = new DbUtil(conn).queryForString(strSql);
			System.out.println(this.getClass()+"     |receiceMoney="+receiceMoney+"       |autoid="+autoid);
			out.print(receiceMoney);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		return null;
	}
	
	
	/**
	 * 修改金额为0
	 * @throws IOException 
	 */
	public ModelAndView updateMoney(HttpServletRequest request,HttpServletResponse response) throws IOException{
		response.setContentType("text/html;charset=utf-8");  //设置编码
		PrintWriter out = response.getWriter();
		
		String autoid = request.getParameter("autoid");
		
		Connection conn=null; 
		try {
			conn=new DBConnect().getConnect("");
			String strSql = "update k_getFunds set receiceMoney = '0' where autoid = "+autoid;
			int count = new DbUtil(conn).executeUpdate(strSql);
			System.out.println(this.getClass()+"     |count="+count+"       |autoid="+autoid);
			
			
			if(count>0){
				// 得到 该条收款 的收款金额
				GetFundService gs = new GetFundService(conn);
				GetFundsTable gt = gs.getGetFundsTableByAutoid(autoid);
				BusinessProjectService bs = new BusinessProjectService(conn);
				bs.updateReceiveMoneyByProjectId(gt.getProjectid(),gt.getReceicedate());
				
				out.print("Y");
			}else{
				out.print("N");
			}
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		return null;
	}
	
	
	/**
	 * 项目选择列表
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView list2(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView(LIST_VIEW);
		DataGridProperty pp = new DataGridProperty() {
			public void onSearch(javax.servlet.http.HttpSession session,
                javax.servlet.http.HttpServletRequest request,
                javax.servlet.http.HttpServletResponse response)
			   throws Exception {
				
					String projectid = this.getRequestValue("projectid");
					String customerId = this.getRequestValue("customerId");
					String entrustNumber = this.getRequestValue("entrustNumber");
					String reportNumber = this.getRequestValue("reportNumber");
					String departname = this.getRequestValue("departname");
					
					if (projectid != null && !"".equals(projectid)) {
						projectid = " and (a.projectid like '%" + projectid + "%' or a.projectname like '%"+projectid+"%') \n" ;
					}
					if (customerId != null && !"".equals(customerId)) {
						customerId = " and (cr.departname = '" + customerId + "' or cr.departid = '"+customerId+"') \n";
					}
					if (entrustNumber != null && !"".equals(entrustNumber)) {
						entrustNumber = " and a.entrustNumber like '%" + entrustNumber + "%' \n";
					}
					if (reportNumber != null && !"".equals(reportNumber)) {
						reportNumber = " and a.reportNumber like '%" + reportNumber + "%' \n";
					}
					if (departname != null && !"".equals(departname)) {
						departname = " and (c.departname = '" + departname + "' or c.autoid = '"+departname+"') \n";
					}
					
					
					this.setOrAddRequestValue("projectid", projectid);
					this.setOrAddRequestValue("customerId", customerId);
					this.setOrAddRequestValue("entrustNumber", entrustNumber);
					this.setOrAddRequestValue("reportNumber", reportNumber);
					this.setOrAddRequestValue("departname", departname);
			}
		};


		pp.setTableID("showModelGetFundsProject");
		pp.setTrActionProperty(true);
		pp.setTrAction("style=\"cursor:hand;\" projectname='${projectname}' projectid='${projectid}' \" ");
		pp.setPageSize_CH(50);
		pp.setPageSize_CH(50);

		pp.setOrderBy_CH("state,signedDate");
		pp.setDirection("asc,desc");
		

		StringBuffer sql = new StringBuffer();
		
		sql.append(" SELECT DISTINCT a.projectid,createtime,projectname,entrustNumber,reportNumber,d.Name,c.departname,auditpara \n");
		sql.append(" cr.departname AS customername,replace(a.businesscost,',','') as businesscost FROM  \n");
		sql.append(" z_projectbusiness a  \n");
		
		//sql.append("LEFT JOIN k_invoice d ON a.projectID  = d.projectid\n");
		
		sql.append(" LEFT JOIN k_customer cr ON a.payCustomerId = cr.departid \n");
		sql.append(" LEFT JOIN k_department c ON a.departmentid = c.autoid  \n");
		sql.append(" LEFT JOIN k_user d ON a.managerUserId = d.id  \n");	
		sql.append(" WHERE entrustNumber <>''  AND reportNumber <>'' \n");
		sql.append(" and (CASE IFNULL(replace(a.businesscost,',',''),0) WHEN '' THEN 0 ELSE replace(a.businesscost,',','')*1 END)> receicemoney*1 \n");
		
		sql.append(" ${projectid} ${customerId} ${entrustNumber} ${reportNumber} ${departname} \n");

		
		pp.addColumn("项目编号", "projectid");
		pp.addColumn("项目名称", "projectname");
		pp.addColumn("委托内容", "auditpara");
		pp.addColumn("委托号", "entrustNumber");
		pp.addColumn("报告文号", "reportNumber");
		pp.addColumn("业务费用", "businesscost","showMoney");
		pp.addColumn("承接部门", "departname");
		pp.addColumn("项目负责人", "Name");
		pp.addColumn("付款单位", "customername");
		
		pp.addSqlWhere("projectid", "${projectid}");
		pp.addSqlWhere("customerId", "${customerId}");
		pp.addSqlWhere("entrustNumber", "${entrustNumber}");
		pp.addSqlWhere("reportNumber", "${reportNumber}");
		pp.addSqlWhere("departname", "${departname}");
		
		
		pp.setColumnWidth("10,25");

		pp.setSQL(sql.toString());
		pp.setOrderBy_CH("createTime");
		pp.setDirection("desc");

		System.out.println(this.getClass()+"         sql="+sql);
		
		request.getSession().setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);

		return modelAndView;
	}
	
	

	/**
	 * 大华 项目选择列表
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView dhlist2(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
		Connection conn = null;
		ModelAndView modelAndView = new ModelAndView(DHLIST_VIEW);
		try {
			conn=new DBConnect().getConnect("");
			DataGridProperty pp = new DataGridProperty() {
				public void onSearch(javax.servlet.http.HttpSession session,
	                javax.servlet.http.HttpServletRequest request,
	                javax.servlet.http.HttpServletResponse response)
				   throws Exception {
					
						String projectid = this.getRequestValue("projectid");
						String customerId = this.getRequestValue("customerId");
						String entrustNumber = this.getRequestValue("entrustNumber");
						String reportNumber = this.getRequestValue("reportNumber");
						String departname = this.getRequestValue("departname");
						String invoicenumber = this.getRequestValue("invoicenumber");
																	 
						
						if (projectid != null && !"".equals(projectid)) {
							projectid = " and (a.projectid like '%" + projectid + "%' or a.projectname like '%"+projectid+"%') \n" ;
						}
						if (customerId != null && !"".equals(customerId)) {
							customerId = " and (cr.departname = '" + customerId + "' or cr.departid = '"+customerId+"') \n";
						}
						if (entrustNumber != null && !"".equals(entrustNumber)) {
							entrustNumber = " and a.entrustNumber like '%" + entrustNumber + "%' \n";
						}
						if (reportNumber != null && !"".equals(reportNumber)) {
							reportNumber = " and a.reportNumber like '%" + reportNumber + "%' \n";
						}
						if (departname != null && !"".equals(departname)) {
							departname = " and (c.departname = '" + departname + "' or c.autoid = '"+departname+"') \n";
						}
						if(invoicenumber!=null && !"".equals(invoicenumber)){
							invoicenumber =" and invoicenumber like '%"+invoicenumber+"%'";
						}
						
						
						this.setOrAddRequestValue("projectid", projectid);
						this.setOrAddRequestValue("customerId", customerId);
						this.setOrAddRequestValue("entrustNumber", entrustNumber);
						this.setOrAddRequestValue("reportNumber", reportNumber);
						this.setOrAddRequestValue("departname", departname);
						this.setOrAddRequestValue("invoicenumber",invoicenumber);
				}
			};


			pp.setTableID("showModelGetFundsProject");
			pp.setTrActionProperty(true);
			pp.setTrAction("style=\"cursor:hand;\" projectname='${projectname}' projectid='${projectid}' invoicenumber='${invoicenumber}'\" ");
			pp.setPageSize_CH(50);
			pp.setPageSize_CH(50);

			pp.setOrderBy_CH("state,signedDate");
			pp.setDirection("asc,desc");
			

			StringBuffer sql = new StringBuffer();
			
			String menuid = request.getParameter("menuid"); //菜单ID
			String departments = new UserPopedomService(conn).getUserIdPopedom(userSession.getUserId(), menuid); //部门授权
			System.out.println("menuid="+menuid+"|departments="+departments);
			
			
			sql.append(" SELECT DISTINCT a.projectid,createtime,b.invoicenumber,projectname,entrustNumber,reportNumber,d.Name,c.departname,auditpara,  \n");
			sql.append(" cr.departname AS customername,replace(a.businesscost,',','') as businesscost FROM  \n");
			sql.append(" z_projectbusiness a  \n");
			sql.append(" LEFT JOIN k_customer cr ON a.payCustomerId = cr.departid \n");
			sql.append(" LEFT JOIN k_department c ON a.departmentid = c.autoid  \n");
			sql.append(" LEFT JOIN k_user d ON a.managerUserId = d.id INNER JOIN k_invoice b ON a.projectId= b.projectId  \n");	
			sql.append(" WHERE entrustNumber <>''  AND reportNumber <>'' \n");
			sql.append(" and d.departmentId IN ("+departments+")  \n");
			sql.append(" and (CASE IFNULL(replace(a.businesscost,',',''),0) WHEN '' THEN 0 ELSE replace(a.businesscost,',','')*1 END)> receicemoney*1 \n");
			
			sql.append(" ${projectid} ${customerId} ${entrustNumber} ${reportNumber} ${departname} ${invoicenumber} \n");

			
			pp.addColumn("项目编号", "projectid");
			pp.addColumn("项目名称", "projectname");
			pp.addColumn("委托内容", "auditpara");
			pp.addColumn("委托号", "entrustNumber");
			pp.addColumn("报告文号", "reportNumber");
			pp.addColumn("业务费用", "businesscost","showMoney");
			pp.addColumn("承接部门", "departname");
			pp.addColumn("项目负责人", "Name");
			pp.addColumn("付款单位", "customername");
			pp.addColumn("发票号", "invoicenumber");
			
			pp.addSqlWhere("projectid", "${projectid}");
			pp.addSqlWhere("customerId", "${customerId}");
			pp.addSqlWhere("entrustNumber", "${entrustNumber}");
			pp.addSqlWhere("reportNumber", "${reportNumber}");
			pp.addSqlWhere("departname", "${departname}");
			pp.addSqlWhere("invoicenumber", "${invoicenumber}");
			                
			
			pp.setColumnWidth("10,25");

			pp.setSQL(sql.toString());
			pp.setOrderBy_CH("createTime");
			pp.setDirection("desc");

			System.out.println(this.getClass()+"         sql="+sql);
			
			request.getSession().setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(conn);
		}		
		return modelAndView;
	}
	
	private static final String dhInvoiceList = "oa/practicalbalance/dhInvoiceList.jsp"; 
	public ModelAndView newdhlist2(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		Connection conn = null;
		ModelAndView modelAndView = new ModelAndView(dhInvoiceList);
		try {
			conn=new DBConnect().getConnect("");
			DataGridProperty pp = new DataGridProperty();

			pp.setTableID("showModelGetFundsProject");
			pp.setTrActionProperty(true);
			pp.setTrAction(" projectname='${projectname}' projectid='${projectid}' ");
			pp.setPageSize_CH(50);

			StringBuffer sql = new StringBuffer();
			sql.append("SELECT a.`projectid`,c.`DepartName` AS payCustomerName,a.customerCode,d.`DepartName` AS customerName, \n"); 
			sql.append("e.value as companyProperties,f.value as invoiceItem,g.value as incomeItem,a.money,a.`times`,b.`projectName` FROM k_invoice a \n");
			sql.append("LEFT JOIN `z_projectbusiness` b ON a.`projectid` = b.`projectID` \n");
			sql.append("LEFT JOIN k_customer c ON b.`payCustomerId`  = c.`DepartID`  \n  ");
			sql.append("LEFT JOIN k_customer d ON b.`customerId`  = d.`DepartID` \n "); 
			sql.append("LEFT JOIN  k_dic e ON a.`companyProperties`  = e.`name` and e.ctype='开票单位性质' \n "); 
			sql.append("LEFT JOIN  k_dic f ON a.`invoiceItem`  = f.`name` and f.ctype='开票项目' \n "); 
			sql.append("LEFT JOIN  k_dic g ON a.`incomeItem`  = g.`name` and g.ctype='开票收入类项目' \n "); 
			sql.append(" where 1=1 ");
			sql.append(" ${payCustomerId} ${customerCode} ${companyProperties} ${invoiceItem} \n");

			
			pp.addColumn("项目编号", "projectid");
			pp.addColumn("付款客户", "payCustomerName");
			pp.addColumn("开票客户代码", "customerCode");
			pp.addColumn("客户单位名称", "customerName");
			pp.addColumn("单位性质", "companyProperties");
			pp.addColumn("开票项目", "invoiceItem");
			pp.addColumn("收入类项目", "incomeItem");
			pp.addColumn("开票金额", "money","showMoney");
			
			pp.addSqlWhere("payCustomerId", " and b.payCustomerId = '${payCustomerId}'");
			pp.addSqlWhere("customerCode", " and  a.customerCode like '%${customerCode}%'");
			pp.addSqlWhere("companyProperties", " and a.companyProperties = '${companyProperties}'");
			pp.addSqlWhere("invoiceItem", " and a.invoiceItem = '${invoiceItem}'");
			                
			
			pp.setColumnWidth("0,10,15,25,10,10,10");

			pp.setSQL(sql.toString());
			pp.setOrderBy_CH("times");
			pp.setDirection("desc");

			request.getSession().setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(conn);
		}		
		return modelAndView;
	}
	
	
	/**
	 * 删除
	 */
	public ModelAndView deleteGetFunds(HttpServletRequest request,HttpServletResponse response) throws Exception{
		response.setContentType("text/html;charset=utf-8");
		ASFuntion CHF = new ASFuntion();
		String autoid = CHF.showNull(request.getParameter("autoid"));
		
		Connection conn = null;
		try {
			conn=new DBConnect().getConnect("");
			GetFundService gs = new GetFundService(conn);
			gs.delGetFunds(autoid);
			response.sendRedirect(request.getContextPath()+"/getFunds.do") ;
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		return null;
	}
	
	

	public ModelAndView download(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Connection conn = null;
		try {
			response.setContentType("text/html;charset=utf-8");
			conn = new DBConnect().getConnect("");
			ASFuntion CHF = new ASFuntion();
			
			String fileName =CHF.showNull(request.getParameter("fileName"));
			String fileTempName =CHF.showNull(request.getParameter("fileTempName"));
			
			String path = BackupUtil.getDATABASE_PATH()+"../"+PROCESSATTATCHFILE+"/";
			
			String filePath = path+fileTempName;

			if (!new File(filePath).exists()) {
				PrintWriter out = response.getWriter();
				out.println("找不到文件，请联系管理员。");
				return null;
			} else {
				fileName = URLEncoder.encode(fileName, "UTF-8");
				response.setContentType("application/x-msdownload");
				response.setHeader("Content-disposition",
						"attachment; filename=" + fileName);
				
				OutputStream os = response.getOutputStream();
				BufferedInputStream bis = new BufferedInputStream(
						new FileInputStream(new File(filePath)));

				byte b[] = new byte[512];
				int len;

				while ((len = bis.read(b)) != -1) {
					os.write(b, 0, len);
				}

				os.flush();
				bis.close();
				os.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}

		return null;
	}
	

	/**
	 * 查询历史收款票信息
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView getGetFundsHistoryInfo(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		response.setContentType("text/html;charset=utf-8");
		Connection conn = null;
		ASFuntion CHF = new ASFuntion();
		PrintWriter out  = response.getWriter() ;
		try {
			conn = new DBConnect().getConnect("");

			GetFundService fs = new GetFundService(conn);
			String projectid = CHF.showNull(request.getParameter("projectid")) ;
			String autoid = CHF.showNull(request.getParameter("autoid")) ;
			String json = fs.getHistoryInfo(projectid,autoid);
			out.write(json) ;
			out.close() ;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}
		
		return null;
	}
	
	public ModelAndView goDetail(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView(TODETAIL);
		String projectid=request.getParameter("projectid");
		Connection conn = null;
		List<GetFundsTable> gt = new ArrayList<GetFundsTable>();
		List<InvoiceTable> it=new ArrayList<InvoiceTable>();
		try {
			conn=new DBConnect().getConnect("");
			GetFundService gfs = new GetFundService(conn);
			gt = gfs.getGetFundsTableByProjectId(projectid);
			//根据收款登记获取项目资料
//			BusinessProjectService bps = new BusinessProjectService(conn) ;
//			String projectName = bps.get(gt.getProjectid()).getProjectName() ;
//			modelAndView.addObject("projectName", projectName);
			modelAndView.addObject("getFunds", gt);
			//根据发票获取项目资料
			InvoiceService is = new InvoiceService(conn);
			it = is.getInvoiceTableByProjectId(projectid);
			
//			String projectName2 = bps.get(it.getProjectid()).getProjectName() ;
//			modelAndView.addObject("projectName2", projectName2);
			
			modelAndView.addObject("invoice", it);
			//收款登记信息
			String projectInfo=gfs.getProjectInfoJson(projectid);
			projectInfo=projectInfo.replace("[{", "");
			projectInfo=projectInfo.replace("}]", "");
			projectInfo=projectInfo.replace("\"", "");
			String[] proArry = projectInfo.split(",");
			Map map = new HashMap();
			for (int i = 0; i < proArry.length; i++) {
				String uName = proArry[i];
				map.put(uName.substring(0, uName.indexOf(":")),uName.substring( uName.indexOf(":")+1,uName.length()));				
			}
			
			modelAndView.addObject("projectInfo", map);
			double ymoney=Double.valueOf((map.get("businessCost").toString()))-Double.valueOf((map.get("getFundsMoney").toString()));
			modelAndView.addObject("ymoney", ymoney);
			//发票信息
			String projectInfoInv=is.getProjectInfoJson(projectid);
			projectInfoInv=projectInfoInv.replace("[{", "");
			projectInfoInv=projectInfoInv.replace("}]", "");
			projectInfoInv=projectInfoInv.replace("\"", "");
			String[] proInvArry = projectInfoInv.split(",");
			Map mapInv = new HashMap();
			for (int i = 0; i < proInvArry.length; i++) {
				String uName = proInvArry[i];
				mapInv.put(uName.substring(0, uName.indexOf(":")),uName.substring( uName.indexOf(":")+1,uName.length()));				
			}
			modelAndView.addObject("projectInfoInv", mapInv);
			double ymoneyInv=Double.valueOf((mapInv.get("businessCost").toString()))-Double.valueOf((mapInv.get("invoicemoney").toString()));
			modelAndView.addObject("ymoneyInv", ymoneyInv);
			//历史收款明细
			List<Map> listMapHgf = new ArrayList<Map>();
			for(int i=0;i<gt.size();i++){
				Map historyGetFunsMap=null;
				if(historyGetFunsMap==null){
					historyGetFunsMap=new HashMap();
				}
				String autoId=gt.get(i).getAutoid();
				String historyGetFuns=gfs.getHistoryInfoNew(projectid, autoId);
				historyGetFuns=historyGetFuns.replace("[{", "");
				historyGetFuns=historyGetFuns.replace("}]", "");
				historyGetFuns=historyGetFuns.replace("\"", "");
				String[] historyGetFunsArry = historyGetFuns.split(",");
				for(int j=0;j<historyGetFunsArry.length;j++){
					String historyName=historyGetFunsArry[j];
					historyGetFunsMap.put(historyName.substring(0, historyName.indexOf(":")),historyName.substring( historyName.indexOf(":")+1,historyName.length()));
				}
				listMapHgf.add(historyGetFunsMap);
			}
			modelAndView.addObject("historyGetFunsMap", listMapHgf);
			//历史开票明细
			List<Map> listMapHin = new ArrayList<Map>();
			for(int i=0;i<it.size();i++){
				Map historyInv=null;
				if(historyInv==null){
					historyInv=new HashMap();
				}
				String autoId=it.get(i).getAutoid();
				String historyInvoice=is.getHistoryInfoNew(projectid, autoId);
				historyInvoice=historyInvoice.replace("[{", "");
				historyInvoice=historyInvoice.replace("}]", "");
				historyInvoice=historyInvoice.replace("\"", "");
				String[] historyInvArry = historyInvoice.split(",");
				for(int j=0;j<historyInvArry.length;j++){
					String historyName=historyInvArry[j];
					System.out.println("historyName=="+historyName);
					String value = historyName.substring( historyName.indexOf(":")+1,historyName.length());
					System.out.println("value=="+value);
					historyInv.put(historyName.substring(0, historyName.indexOf(":")),value);
				}
				listMapHin.add(historyInv);
			}
			System.out.println(listMapHin);
			modelAndView.addObject("historyInv", listMapHin);
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(conn);
		}
		return modelAndView;
	}
	
}	 
	
	
	
	
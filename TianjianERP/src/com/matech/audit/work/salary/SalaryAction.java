package com.matech.audit.work.salary;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.matech.audit.pub.datagrid.DataGrid;
import com.matech.audit.pub.datagrid.DataGridProperty;
import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.fileupload.MyFileUpload;
import com.matech.audit.service.salary.SalaryService;
import com.matech.audit.service.salary.model.Salary;
import com.matech.audit.service.userpopedom.UserPopedomService;
import com.matech.audit.work.oa.interiorEmail.InteriorEmailAction;
import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.single.Single;
import com.matech.framework.pub.util.ASFuntion;
import com.matech.framework.pub.util.Debug;
import com.matech.framework.pub.util.StringUtil;
import com.matech.framework.service.excelupload.ExcelUploadService;

/**
 * @author Administrator
 *工资
 */
public class SalaryAction extends MultiActionController{

	
	private String List = "salary/List.jsp";
	private String BUSINESSLIST = "salary/businessList.jsp";
	private String PERSONNELIST = "salary/personnelList.jsp";
	private String AUDIT = "salary/audit.jsp";
	private String MYSQLARYLIST = "salary/mySalaryList.jsp";
	private String listSalayByDepart = "salary/listSalaryByDepart.jsp";
	private String SOFTSALARYLIST = "salary/softSalaryList.jsp";

	public ModelAndView list(HttpServletRequest request, HttpServletResponse response)  throws Exception  {
		
		ModelAndView modelAndView = new ModelAndView( List);
		UserSession userSession = (UserSession) request.getSession().getAttribute("userSession"); 
		String departmentid = userSession.getUserAuditDepartmentId();   //得到当前部门
		DataGridProperty pp = new DataGridProperty(); 
		DataGridProperty pp2 = new DataGridProperty(); 
		ASFuntion asf = new ASFuntion();
		String ppSql  = "";
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {	
			conn = new DBConnect().getConnect("");
			String userid = userSession.getUserId(); 
			String menuid = asf.showNull(request.getParameter("menuid")); //菜单ID
			if("".equals(menuid)) menuid = "10000767"; 
	    	String departmentIds = new UserPopedomService(conn).getUserIdPopedom(userid, menuid);
			//String departmentIds = asf.showNull(new UserPopedomService(conn).getUserPopedom(userSession.getUserId(), "giveList"));
			
			/**
			 * 取出紧挨着已发放的倒数一个STATUS
			 */
			String t1="SELECT NAME FROM k_dic WHERE ctype='工资进度' AND NAME<>'已发放' ORDER BY VALUE DESC LIMIT 1";
			
			ps = conn.prepareStatement(t1);
			rs = ps.executeQuery();
			String status="";
			if(rs.next()){
				status=rs.getString(1);
			}
			modelAndView.addObject("status",status);
			
			ppSql="SELECT pch, CONCAT(`nowYear`,'年',`nowMonth`,'月') AS nowYearMoneth, \n"+
					 " b.departname as departName,COUNT(1) AS RS,a.status as status,a.pchname \n "+
                     " FROM K_SALARY A,K_DEPARTMENT B \n" +
                     " ,(SELECT NAME FROM k_dic WHERE ctype='工资进度' AND NAME<>'已发放' ORDER BY VALUE DESC LIMIT 1)c "+
                     " WHERE B.AUTOID=A.DEPARTMENTID \n" +
                     " and (a.departmentId ='"+departmentid+"'  or departmentId in ("+departmentIds+"))" +
                     " and a.status = c.name \n" +
                     " ${year} ${month} ${departName}  ${departmentId}"+
                     " GROUP BY pch ";
			
			pp.setTableID("giveList");
			pp.setPageSize_CH(50);
			pp.setCustomerId("");
			pp.setWhichFieldIsValue(1);
			pp.setInputType("radio");
			pp.setOrderBy_CH("nowYear,nowMonth");
			pp.setDirection("desc,desc");
			
			pp.setColumnWidth("10,10,10,10");
			pp.setSQL(ppSql);
			
			pp.setTrActionProperty(true); // 设置 table可双击
			pp.setTrAction("style=\"cursor:hand;\" NY=\"${NY}\" ");
			pp.setUseBufferGrid(false) ;//全选x`
			pp.addColumn("项目", "pchname");
			pp.addColumn("工资年月", "nowYearMoneth");
			pp.addColumn("部门", "departName");
			pp.addColumn("人数", "RS");
			pp.addColumn("状态", "status");
			
			pp.addSqlWhere("year"," and a.nowYear = '${year}' ");
			pp.addSqlWhere("month"," and a.nowMonth = '${month}' ");
			pp.addSqlWhere("departName"," and c.departName like '%${departName}%' ");
			pp.addSqlWhere("departmentId"," and a.departmentId = '${departmentId}'");
			
			String ppSql2 = "";
			if (departmentid != null && !"".equals(departmentid)) {
				ppSql2="SELECT pch, CONCAT(`nowYear`,'年',`nowMonth`,'月') AS nowYearMoneth, \n"+
								 " b.departname as departName,COUNT(1) AS RS,a.status as status,a.pchname \n "+
	                             " FROM K_SALARY A,K_DEPARTMENT B \n" +
	                             " WHERE B.AUTOID=A.DEPARTMENTID \n" +
	                             " and (a.departmentId ='"+departmentid+"' or a.departmentId in (" +departmentIds +
	                             " )) and a.status = '已发放' \n" +
	                             " ${year} ${month} ${departName} ${departmentId}"+
	                             " GROUP BY pch ";
			} else {
				ppSql2="SELECT pch, CONCAT(`nowYear`,'年',`nowMonth`,'月') AS nowYearMoneth, \n"+
								 " b.departname as departName,COUNT(1) AS RS,a.status as status,a.pchname \n "+
	                             " FROM K_SALARY A,K_DEPARTMENT B \n" +
	                             " WHERE B.AUTOID=A.DEPARTMENTID \n" +
	                             " and a.status = '已发放' \n" +
	                             " ${year} ${month} ${departName} ${departmentId} "+
	                             " GROUP BY pch";
			}
			
			pp2.setTableID("giveYetList");
			pp2.setPageSize_CH(50);
			pp2.setCustomerId("");
			pp2.setWhichFieldIsValue(1);
			pp2.setInputType("radio");
			pp2.setOrderBy_CH("nowYear,nowMonth");
			pp2.setDirection("desc,desc");
			
			pp2.setColumnWidth("10,10,10,10");
			pp2.setSQL(ppSql2);

			
			pp2.setTrActionProperty(true); // 设置 table可双击
			pp2.setTrAction("style=\"cursor:hand;\" NY=\"${NY}\" ");
			pp2.setUseBufferGrid(false) ;//全选x`
			pp2.addColumn("项目", "pchname");
			pp2.addColumn("工资年月", "nowYearMoneth");
			pp2.addColumn("部门", "departName");
			pp2.addColumn("人数", "RS");
			pp2.addColumn("状态", "status");
			
			pp2.addSqlWhere("year"," and a.nowYear = '${year}' ");
			pp2.addSqlWhere("month"," and a.nowMonth = '${month}' ");
			pp2.addSqlWhere("departName"," and c.departName like '%${departName}%' ");
			pp2.addSqlWhere("departmentId"," and a.departmentId = '${departmentId}' ");
 			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{			
			DbUtil.close(rs);
			DbUtil.close(ps);
			
			DbUtil.close(conn);
			request.getSession().setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);
			request.getSession().setAttribute(DataGrid.sessionPre + pp2.getTableID(), pp2);
		}
		
		return modelAndView;
	}
	
	
	/**
	 * 弹性工资录入
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView softSalaryList(HttpServletRequest request, HttpServletResponse response)  throws Exception  {
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			
			ModelAndView modelAndView = new ModelAndView(SOFTSALARYLIST);					
			DataGridProperty pp = new DataGridProperty();
			pp.setTableID("softSalaryList");
			pp.setCustomerId("");
			pp.setPageSize_CH(50);
			pp.setWhichFieldIsValue(1);
			
			
			pp.setInputType("checkbox");
			
			// 获取当前年月
			Calendar cal = Calendar.getInstance();
			int year = cal.get(Calendar.YEAR);
			int month = cal.get(Calendar.MONTH );
			 if(month == 0) {
				 year = year - 1;
				 month = 12;
			 }
			 //得到user
			UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");
			conn = new DBConnect().getConnect("");
			ASFuntion asf = new ASFuntion();
			String strSql = "",sql = "";
			
			//得到当前部门
			String departmentid = userSession.getUserAuditDepartmentId();
			String paraYear =(asf.showNull(request.getParameter("paraYear"))) ;
			String paraMonth =(asf.showNull(request.getParameter("paraMonth"))) ;
			int YY = 0;
			int MM = 0;
			String te = request.getParameter("te");
			SalaryService salaryService = new SalaryService(conn);
			if ( !"".equals(te) && null != te  ) {
				if (te.equals("1")) {
				salaryService.initSoftSalary(departmentid, year, month);
				YY=year;
				MM=month;
				} else {
					if (!"".equals(paraYear) && null != paraYear && !"".equals(paraMonth) && null != paraMonth) {
						salaryService.initSoftSalary(departmentid,Integer.parseInt(paraYear) , Integer.parseInt(paraMonth));
						YY=Integer.parseInt(paraYear);
						MM=Integer.parseInt(paraMonth);
					}
				}
			} 
			DbUtil.close(rs);
			DbUtil.close(ps);
			DbUtil.close(conn);
			
			modelAndView.addObject("YY",YY);
			modelAndView.addObject("MM",MM);
			
			//
			if(departmentid != null && !"".equals(departmentid)){
				//有部门,只显示本部门和授权部门
				strSql = "select ID,Name,case when Sex='M' or Sex='男' then '男' else '女' end Sex," +
				"	a.rank,d.rankid,d.status,d.v17,d.v18,d.v19,d.v20 \n" +
				"	from k_User a \n" +
				"	left join k_salary d on a.id=d.userid \n"+
				"	where d.departmentid='"+departmentid+"' AND nowYear='"+YY+"' AND nowMonth='"+MM+"' ${nowMonth}  ${nowYear} ${name} ${rankId} " ;
			} else {
				//无部门,显示全部部门和授权部门
				strSql = "select ID,Name,case when Sex='M' or Sex='男' then '男' else '女' end Sex," +
				"	a.rank,d.rankid,d.status,d.v17,d.v18,d.v19,d.v20 \n" +
				"	from k_User a \n" +
				"	left join k_salary d on a.id=d.userid \n"+
				"	where 1=1 AND nowYear='"+YY+"' AND nowMonth='"+MM+"' ${nowMonth}  ${nowYear} ${name} ${rankId} " ;
			}

				
  		    pp.setOrderBy_CH("Name");
			pp.setDirection("asc");
			pp.setColumnWidth("10,10,5,10,10,10,10,10") ;
			
			//编号,姓名,登录名,性别,学历,所属部门,岗位
			//pp.addColumn("编号", "id");
			
			pp.addColumn("姓名", "Name");
			pp.addColumn("薪酬级别", "rank");
			pp.addColumn("性别", "Sex","showCenter");
			pp.addColumn("本月工时", "v17");
			pp.addColumn("本月外勤天数", "v18");
			pp.addColumn("绩效工资", "v19");
			pp.addColumn("其他补助", "v20");
			pp.addColumn("状态", "status");
			
			pp.setSQL(strSql);
		    pp.addSqlWhere("name", " and name like '%${name}%'");
			pp.addSqlWhere("rankId", " and d.rankid like '%${rankid}%'");
		    pp.addSqlWhere("nowMonth", " and d.nowMonth = '${todayMonth}'");
			pp.addSqlWhere("nowYear", " and d.nowYear = '${todayYear}'");
			pp.setCancelPage(true); //不分页  显示全部
			pp.setUseBufferGrid(false) ;//全选x`
			request.getSession().setAttribute(DataGrid.sessionPre+pp.getTableID(),pp);			
									
			
		} catch (Exception e) {
			Debug.print(Debug.iError, "查询用户列表失败！", e);
			e.printStackTrace();
			throw e;
		}finally{
		
		} 
		
	 	
		return new ModelAndView(SOFTSALARYLIST);			
	}
	
	/**
	 * 初始化 工资 (增加)
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView init(HttpServletRequest request, HttpServletResponse response){
		
		ASFuntion asf = new ASFuntion();
		String paraYear = asf.showNull(request.getParameter("paraYear"));
		String paraMonth = asf.showNull(request.getParameter("paraMonth"));
		String[] departmentIds=asf.showNull(request.getParameter("departmentIds")).split(",");
		
		
		UserSession userSession = (UserSession) request.getSession().getAttribute("userSession"); 
		Connection conn = null;
		
		response.setContentType("text/html;charset=utf-8");
		response.setCharacterEncoding("utf-8");
		PrintWriter out = null;
		
		if(!"".equals(paraYear)&&null!=paraYear&&!"".equals(paraMonth)&&null!=paraMonth){
			try {
				out=response.getWriter();
				
				
				conn = new DBConnect().getConnect("");
				SalaryService salaryService = new SalaryService(conn);

				/**
				 * 发放逻辑：先删除同一年月。同一部门，同一项目的,再新增
				 */
				int iOk=0,iNg=0;
				for(int i=0;i<departmentIds.length;i++){
					
					String pch = UUID.randomUUID().toString();
					//发放逻辑：先删除同一年月。同一部门，同一项目的
					salaryService.deleteByPch(paraYear, paraMonth, "月度工资", departmentIds[i]);
					//初始化人员
					System.out.println("初始化："+departmentIds[i]+"开始：" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
					if (salaryService.init(pch,departmentIds[i],paraYear,paraMonth)){
						iOk++;
					}else{
						iNg++;
					}
					
					System.out.println("初始化："+departmentIds[i]+"结束：" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
				}
				
				out.println("工资初始化"+iOk+"个部门成功，"+iNg+"个部门失败！");
			} catch (Exception e) {
				e.printStackTrace();
				out.println("失败:"+e.getMessage());
			}finally{
				DbUtil.close(conn);
			}
		}
		return null;
	}
	
	/**
	 * 初始化 工资 (修改)
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView updateInit(HttpServletRequest request, HttpServletResponse response){
		Connection conn = null;
		ASFuntion asf = new ASFuntion();
		List<String> tableList = new ArrayList<String>();
		UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");
		
		String pch =asf.showNull(request.getParameter("p_ch")) ;
		String editRight = asf.showNull(request.getParameter("e_ditRight"));
		
			try {
				response.setContentType("text/html;charset=utf-8") ;
				response.setCharacterEncoding("utf-8");
				PrintWriter out = response.getWriter();
				conn = new DBConnect().getConnect("");
				SalaryService salaryService = new SalaryService(conn);
				//初始化人员
				   SimpleDateFormat d1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 格式化时间   
				     
				   String nowtime1 = d1.format(new Date());// 按以上格式 将当前时间转换成字符串   
				     
				   System.out.println("1当前时间：" + nowtime1); 
				   tableList = salaryService.updateInit(pch,editRight);
				//初始化人员
				   SimpleDateFormat d2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 格式化时间   
				     
				    String nowtime2 = d2.format(new Date());// 按以上格式 将当前时间转换成字符串   
				     
				    System.out.println("2当前时间：" + nowtime2); 
				String newTable = "";
				for (int i = 0; i < tableList.size(); i++) {
					newTable+=tableList.get(i);
				}
				  SimpleDateFormat d3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 格式化时间   
				     
		           String nowtime3 = d3.format(new Date());// 按以上格式 将当前时间转换成字符串   
		     
		           System.out.println("3当前时间：" + nowtime3); 
				   out.write(newTable);
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				DbUtil.close(conn);
			}
		return null;
	}
	
	/**
	 * 删除工资 (业务部门）
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView deleteByPch(HttpServletRequest request, HttpServletResponse response){
		Connection conn = null;
		ASFuntion asf = new ASFuntion();
		String pch =asf.showNull(request.getParameter("p_ch")) ;
		String flag = asf.showNull(request.getParameter("flag"));
		try {
				response.setContentType("text/html;charset=utf-8") ;
				response.setCharacterEncoding("utf-8");
				conn = new DBConnect().getConnect("");
				SalaryService salaryService = new SalaryService(conn);
                boolean result=false;
				result = salaryService.deleteByPch(pch);
				if (result) {
					if(!flag.equals("")){
						response.sendRedirect(request.getContextPath()+"/salary.do?method=list");
					}else{
						response.sendRedirect(request.getContextPath()+"/salary.do?method=businessList");
					}
			  }
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				DbUtil.close(conn);
			}
		return null;
	}
	
	/**
	 * 删除工资 (业务部门）,根据年月
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView deleteByNY(HttpServletRequest request, HttpServletResponse response){
		Connection conn = null;
		ASFuntion asf = new ASFuntion();
		String yearNow =asf.showNull(request.getParameter("yearNows")) ;
		String monthNow =asf.showNull(request.getParameter("monthNows")) ;
		String departmentId = asf.showNull(request.getParameter("departmentId"));
			try {
				response.setContentType("text/html;charset=utf-8") ;
				response.setCharacterEncoding("utf-8");
				conn = new DBConnect().getConnect("");
				SalaryService salaryService = new SalaryService(conn);
                boolean result=false;
				result = salaryService.deleteByNY(yearNow,monthNow,departmentId);
				if (result) {
					response.sendRedirect(request.getContextPath()+"/salary.do?method=businessList");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				DbUtil.close(conn);
			}
		return null;
	}
	
	/**
	 * 审批 工资 (初始化)
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView AuditInit(HttpServletRequest request, HttpServletResponse response){
		Connection conn = null;
		ASFuntion asf = new ASFuntion();
		List<String> tableList = new ArrayList<String>();
		String pch =asf.showNull(request.getParameter("p_ch")) ;
			try {
				response.setContentType("text/html;charset=utf-8") ;
				response.setCharacterEncoding("utf-8");
				PrintWriter out = response.getWriter();
				conn = new DBConnect().getConnect("");
				SalaryService salaryService = new SalaryService(conn);

				tableList = salaryService.updateInit(pch,"人事部");
			
				String newTable = "";
				for (int i = 0; i < tableList.size(); i++) {
					newTable+=tableList.get(i);
				}
				out.write(newTable + "<br>");
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				DbUtil.close(conn);
			}
		return null;
	}
	/**
	 * 录入弹性工资，点击弹性工资录入按钮
	 * @param request
	 * @param response
	 * @return
	 */
	public void softSalary(HttpServletRequest request, HttpServletResponse response){
		ASFuntion asf = new ASFuntion();
		String userIds = asf.showNull(request.getParameter("userIds"));
		Connection conn = null;
		List<String> tableList = new ArrayList<String>();
		if(!"".equals(userIds)){
			try {
				response.setContentType("text/html;charset=utf-8") ;
				response.setCharacterEncoding("utf-8");
				PrintWriter out = response.getWriter();
				String pch = UUID.randomUUID().toString();
				conn = new DBConnect().getConnect("");
				SalaryService salaryService = new SalaryService(conn);
				
				//初始化人员
				//boolean result = salaryService.initPch(userIds,pch);

//				if(result){
//					
//					tableList = salaryService.updateInit(pch);
//				}
			
				String newTable = "";
				for (int i = 0; i < tableList.size(); i++) {
					newTable+=tableList.get(i);
				}
				out.write(newTable);
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				DbUtil.close(conn);
			}
		}
	}
	
	/**
	 * 修改 工资
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView ajaxEditSaray(HttpServletRequest request, HttpServletResponse response){
		
		ASFuntion asf = new ASFuntion();
		Connection conn = null;
		String pch = asf.showNull(request.getParameter("pch").trim());
		String userId = asf.showNull(request.getParameter("userId").trim());
		String name = asf.showNull(request.getParameter("nName").trim());
		String value = asf.showNull(request.getParameter("vValue").trim());
		
		try {
			PrintWriter out = response.getWriter() ; 
			conn = new DBConnect().getConnect("");
			SalaryService salaryService = new SalaryService(conn);
			
			String result = salaryService.updateSalary(pch, userId, name, value);		
			
			out.write(result);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		return null;
		
	}
	
	/**
	 * 修改 工资
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView ajaxEditSarayByRs(HttpServletRequest request, HttpServletResponse response){
		
		ASFuntion asf = new ASFuntion();
		Connection conn = null;
		String pch = asf.showNull(request.getParameter("pch").trim());
		String userId = asf.showNull(request.getParameter("userId").trim());
		String name = asf.showNull(request.getParameter("nName").trim());
		String value = asf.showNull(request.getParameter("vValue").trim());
		
		try {
			PrintWriter out = response.getWriter() ; 
			conn = new DBConnect().getConnect("");
			SalaryService salaryService = new SalaryService(conn);
			
			String result = salaryService.updateSalaryByRs(pch, userId, name, value);		
			result =userId+"-"+result;
			out.write(result);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		return null;
		
	}
	
	/**
	 * 修改 工资、扣税
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView ajaxEditSarayKouSui(HttpServletRequest request, HttpServletResponse response){
		
		ASFuntion asf = new ASFuntion();
		Connection conn = null;
		String pch = asf.showNull(request.getParameter("pch").trim());
		String userId = asf.showNull(request.getParameter("userId").trim());
		String name = asf.showNull(request.getParameter("nName").trim());
		String value = asf.showNull(request.getParameter("vValue").trim());
		String value2 = asf.showNull(request.getParameter("vValue2").trim());
		
		try {
			PrintWriter out = response.getWriter() ; 
			conn = new DBConnect().getConnect("");
			SalaryService salaryService = new SalaryService(conn);
			
			String result = salaryService.updateSalaryKouSui(pch, userId, name, value,value2);		
			result =userId+"-"+result;			
			out.write(result);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		return null;
		
	}
	
	/**
	 * 修改 工资、计算实发工资
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView ajaxEditSarayShiFa(HttpServletRequest request, HttpServletResponse response){
		
		ASFuntion asf = new ASFuntion();
		Connection conn = null;
		String pch = asf.showNull(request.getParameter("pch").trim());
		String userId = asf.showNull(request.getParameter("userId").trim());
		String name = asf.showNull(request.getParameter("nName").trim());
		String value = asf.showNull(request.getParameter("vValue").trim());
		String value2 = asf.showNull(request.getParameter("vValue2").trim());
		String value3 = asf.showNull(request.getParameter("vValue3").trim());
		
		try {
			PrintWriter out = response.getWriter() ; 
			conn = new DBConnect().getConnect("");
			SalaryService salaryService = new SalaryService(conn);
			
			String result = salaryService.updateSalaryShiFa(pch, userId, name, value,value2,value3);		
			result =userId+"-"+result;			
			out.write(result);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		return null;
		
	}
	
	/**
	 * 查询当前年月是否初始化了 工资条
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView getPch(HttpServletRequest request, HttpServletResponse response){
		
		Connection conn = null;
		 
		
		try {
			PrintWriter out = response.getWriter() ; 
			conn = new DBConnect().getConnect("");
			SalaryService salaryService = new SalaryService(conn);
			
			String result = salaryService.getPch();
			
			out.write(result);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		return null;
		
	}
	
	/**
	 * 初始化
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView businessList(HttpServletRequest request, HttpServletResponse response){
		
		ModelAndView modelAndView = new ModelAndView( BUSINESSLIST);
		Connection conn = null;
		//未发放
		try {	
			ASFuntion asf = new ASFuntion();
			UserSession userSession = (UserSession) request.getSession().getAttribute("userSession"); 
			String departmentid = userSession.getUserAuditDepartmentId();   //得到当前部门
			String userid = userSession.getUserId(); 
			
			conn = new DBConnect().getConnect("");
			String menuid = asf.showNull(request.getParameter("menuid")); //菜单ID
			if("".equals(menuid)) menuid = "10000765";
	    	String departments = new UserPopedomService(conn).getUserIdPopedom(userid, menuid);
	    	
			System.out.println("qwh:departmentid="+departmentid);
			DataGridProperty pp = new DataGridProperty(); 
			DataGridProperty pp1 = new DataGridProperty(); 
			String ppSql  = "";
			ppSql="SELECT pch,pchname, CONCAT(CONVERT(nowYear,CHAR),'年',CONVERT(nowMonth,CHAR),'月') AS nowYearMoneth, \n"
				+" b.departname AS departName,COUNT(DISTINCT a.autoid) AS RS,a.status AS STATUS  \n"
				+" FROM K_SALARY A,k_department B  \n"
				+" WHERE (a.departmentId='"+departmentid+"' or a.departmentId in ("+departments+"))  \n"
				+" AND a.departmentId=b.autoid  \n"
				+" AND a.status = '暂存'   \n"
				+" ${year} ${month} ${departName} "
				+" GROUP BY pch,a.departmentId";
	                            
			pp.setTableID("businessList");
			pp.setPageSize_CH(50);
			pp.setCustomerId("");
			pp.setWhichFieldIsValue(1);
			pp.setInputType("radio");
			pp.setOrderBy_CH("nowYear,nowMonth");
			pp.setDirection("desc,desc");
			
			pp.setColumnWidth("10,10,10,10");
			pp.setSQL(ppSql);
			pp.setPrintEnable(true);
		    pp.setPrintVerTical(false);
		    pp.setPrintTitle("未发放工资列表");
			
			//pp.setTrActionProperty(true); // 设置 table可双击
			//pp.setTrAction("style=\"cursor:hand;\" NY=\"${NY}\" ");
			pp.setUseBufferGrid(false) ;//全选x`
			pp.addColumn("项目", "pchname");
			pp.addColumn("工资年月", "nowYearMoneth");
			pp.addColumn("部门", "departName");
			pp.addColumn("人数", "RS");
			pp.addColumn("状态", "status");
			
			pp.addSqlWhere("year"," and a.nowYear = '${year}' ");
			pp.addSqlWhere("month"," and a.nowMonth = '${month}' ");
			pp.addSqlWhere("departName"," and c.departName like '%${departName}%' ");
			
			
			String ppSql1 = "";
			//已发放
			ppSql1="SELECT pch,pchname, CONCAT(CONVERT(nowYear,CHAR),'年',CONVERT(nowMonth,CHAR),'月') AS nowYearMoneth, \n"
					+" b.departname AS departName,COUNT(DISTINCT a.autoid) AS RS,a.status AS STATUS  \n"
					+" FROM K_SALARY A,k_department B  \n"
					+" WHERE (a.departmentId='"+departmentid+"' or a.departmentId in ("+departments+"))  \n"
					+" AND a.departmentId=b.autoid  \n"
					+" AND a.status <> '暂存'   \n"
					+" ${year} ${month} ${departName} "
					+" GROUP BY pch,a.departmentId";
			
			pp1.setTableID("businessYetList");
			pp1.setPageSize_CH(50);
			pp1.setCustomerId("");
			pp1.setWhichFieldIsValue(1);
			pp1.setInputType("radio");
			pp1.setOrderBy_CH("nowYear,nowMonth");
			pp1.setDirection("desc,desc");
			
			pp1.setColumnWidth("10,10,10,10");
			pp1.setSQL(ppSql1);
			
			//pp1.setTrActionProperty(true); // 设置 table可双击
			//pp1.setTrAction("style=\"cursor:hand;\" NY=\"${NY}\" ");
			pp1.setUseBufferGrid(false) ;//全选x`
			pp1.addColumn("项目", "pchname");
			pp1.addColumn("工资年月", "nowYearMoneth");
			pp1.addColumn("部门", "departName");
			pp1.addColumn("人数", "RS");
			pp1.addColumn("状态", "status");
			
			pp1.addSqlWhere("year"," and a.nowYear = '${year}' ");
			pp1.addSqlWhere("month"," and a.nowMonth = '${month}' ");
			pp1.addSqlWhere("departName"," and c.departName like '%${departName}%' ");
			
			modelAndView.addObject("departmentId",departmentid);
			
			request.getSession().setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);
			request.getSession().setAttribute(DataGrid.sessionPre + pp1.getTableID(), pp1);

		} catch (Exception e) {
			e.printStackTrace();
		}finally{	
			DbUtil.close(conn);
		}
		
		return modelAndView;
	}
	
	/**
	 * 中间审核
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView personnelList(HttpServletRequest request, HttpServletResponse response){
		
		ASFuntion asf = new ASFuntion();
		ModelAndView modelAndView = new ModelAndView( PERSONNELIST);
		
		//显示进度
		String status = asf.showNull(request.getParameter("status"));
		String view = asf.showNull(request.getParameter("view"));
		
		DataGridProperty pp = new DataGridProperty(); 
		Connection conn = null;
		String ppSql  = "";
		try {	
			conn = new DBConnect().getConnect("");
			
			//获取当前人可以访问的部门列表
			UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
			String departmentid=userSession.getUserAuditDepartmentId();
			String userid =userSession.getUserId();
			
			String menuid = asf.showNull(request.getParameter("menuid")); //菜单ID
			if("".equals(menuid)) menuid = "10000766";
	    	String departmentIds = new UserPopedomService(conn).getUserIdPopedom(userid, menuid);
			//String departmentIds = asf.showNull(new UserPopedomService(conn).getUserPopedom(userSession.getUserId(), "personnelList"));
			
			ppSql="SELECT pch, CONCAT(`nowYear`,'年',`nowMonth`,'月') AS nowYearMoneth, \n"+
							 " b.departname as departName,COUNT(1) AS RS,a.status as status,a.pchname \n "+
                             " FROM K_SALARY A,K_DEPARTMENT B,k_dic c  \n" +
                             " WHERE B.AUTOID=A.DEPARTMENTID \n" +
                             " and ( a.departmentId='"+departmentid+"' or a.departmentId in ("+departmentIds+"))" +
                             " and c.ctype='工资进度' and c.value='"+status+"' and c.name=a.status \n" +
                             " ${year} ${month} ${departName} "+
                             " GROUP BY pch ";
			
			pp.setTableID("personnelList");
			pp.setPageSize_CH(50);
			pp.setCustomerId("");
			pp.setWhichFieldIsValue(1);
			pp.setInputType("radio");
			pp.setOrderBy_CH("nowYear,nowMonth");
			pp.setDirection("desc,desc");
			
			pp.setColumnWidth("10,10,10,10");
			pp.setSQL(ppSql);
			
			pp.setTrActionProperty(false); // 设置 table可双击
			pp.setTrAction("style=\"cursor:hand;\" NY=\"${NY}\" ");
			pp.setUseBufferGrid(false) ;//全选x`
			pp.addColumn("项目", "pchname");
			pp.addColumn("工资年月", "nowYearMoneth");
			pp.addColumn("部门", "departName");
			pp.addColumn("人数", "RS");
			pp.addColumn("状态", "status");
			
			pp.addSqlWhere("year"," and a.nowYear = '${year}' ");
			pp.addSqlWhere("month"," and a.nowMonth = '${month}' ");
			pp.addSqlWhere("departName"," and a.departmentId ='${departName}' ");
 			
			
			modelAndView.addObject("status",status);
			modelAndView.addObject("view",view);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{			
			request.getSession().setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);
		}
		
		return modelAndView;
	}
	
	/**
	 * 业务与人事 审批跳转
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView auditSkip(HttpServletRequest request, HttpServletResponse response){
		
		String pch = "";
		ASFuntion asf = new ASFuntion();
		Connection conn = null;
		String NY = asf.showNull(request.getParameter("NY").trim());
		String ctype = asf.showNull(request.getParameter("ctype").trim());
		ModelAndView modelAndView = new ModelAndView(AUDIT);
		String year1 = NY.substring(0, 4);
		String month1 = NY.substring(4);
		try {
			conn = new DBConnect().getConnect("");
			SalaryService salaryService = new SalaryService(conn);
			if("1".endsWith(ctype)){
				ctype="业务部门暂存";
			}else{
				ctype="人事部门确认";
			}
			
			//List<Map> salaryList = (List<Map>)request.getSession().getAttribute("salaryList");
			//List<String> tableList = salaryService.getTables(pch,ctype);
			
			String sql_year = "select nowYear from k_salary where pch='"+pch+"'";
			String sql_month = "select nowMonth from k_salary where pch='"+pch+"'";
			String sql_departmentId = "select departmentId from k_salary where pch='"+pch+"'";
			String sql_userIds = "select userid from k_salary where pch='"+pch+"'";
			
			String year = salaryService.getValueBySql(sql_year);
			String month = salaryService.getValueBySql(sql_month);
			String departmentId = salaryService.getValueBySql(sql_departmentId);
			String userIds = salaryService.getUserIdsBySql(sql_userIds);
			List<Map> salaryList_te = salaryService.getSelaryList(year, month, departmentId, userIds);  //执行getSelaryList（）方法里的隐式update
			List<Map> salaryList = salaryService.getSelaryList(year, month, departmentId, userIds);     //执行真正查询，获取salaryList
			
			
//			String userIds ="8612,8614,8615";
//			List<Map> salaryList_te = salaryService.getSelaryList("2011", "12", "621", userIds);  //执行getSelaryList（）方法里的隐式update
//			List<Map> salaryList = salaryService.getSelaryList("2011", "12", "621", userIds);     //执行真正查询，获取salaryList
//			
			
			
//			String newTable = "";
//			for (int i = 0; i < tableList.size(); i++) {
//				newTable+=tableList.get(i);
//			}
			
//			modelAndView.addObject("newTable",newTable);
			modelAndView.addObject("ctype",ctype);
			modelAndView.addObject("pch",pch);
			modelAndView.addObject("salaryList",salaryList);
			
			//request.getSession().setAttribute("salaryList", salaryList);
			//modelAndView.addObject("salaryList",salaryList);
			
			//response.sendRedirect(request.getContextPath()+"/"+AUDIT);
		
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		return modelAndView;
		
	}
	
	/**
	 * 工资审批前进
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView handleNext(HttpServletRequest request, HttpServletResponse response){
		ASFuntion asf = new ASFuntion();
		Connection conn = null;
		String pch = asf.showNull(request.getParameter("pch").trim());

		response.setCharacterEncoding("utf-8");
		
		try {
			
			PrintWriter out = response.getWriter();
			
			conn = new DBConnect().getConnect("");
			SalaryService salaryService = new SalaryService(conn);
			String result=salaryService.handleNext(pch);
			
			out.println(result);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		
		return null;
	}
	
	/**
	 * 工资审批前进
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView handlePre(HttpServletRequest request, HttpServletResponse response){
		ASFuntion asf = new ASFuntion();
		Connection conn = null;
		String pch = asf.showNull(request.getParameter("pch").trim());
		String status = asf.showNull(request.getParameter("status"));

		response.setCharacterEncoding("utf-8");
		
		try {
			
			PrintWriter out = response.getWriter();
			
			conn = new DBConnect().getConnect("");
			SalaryService salaryService = new SalaryService(conn);
			String result=salaryService.handlePre(pch,status);
			
			out.println(result);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		
		return null;
	}
	
	/**
	 * 导出
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView export(HttpServletRequest request, HttpServletResponse response){
		Map mapResult = new HashMap();
		ASFuntion asf = new ASFuntion();
		String file=request.getParameter("filename");
		Connection conn = null;
		String filenames ="";
		String pch = asf.showNull(request.getParameter("pch").trim());
		
		boolean bExists =false;
		try {
			conn = new DBConnect().getConnect("");
			SalaryService salaryService = new SalaryService(conn);
			
			String filename=salaryService.getExeclDir()+"/"+pch+".zip";
			
			if (new File(filename).exists()){
				//文件已经存在,直接返回
				filenames = pch+".zip";
				bExists=true;
			}else{
				filenames = salaryService.ExploreExcel(pch);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		
		mapResult.put("filenames", filenames);
		mapResult.put("pch", pch);
		mapResult.put("bExists", bExists);
		
		
		//是否只读
		String mode = asf.showNull(request.getParameter("mode"));
		mapResult.put("mode", mode);
		
		return new ModelAndView("salary/EditExcel.jsp",mapResult);
	}
	
	/**
	 * 审核通过
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView audit(HttpServletRequest request, HttpServletResponse response){
		
		ASFuntion asf = new ASFuntion();
		Connection conn = null;
		String pch = asf.showNull(request.getParameter("pch").trim());
		String ctype = asf.showNull(request.getParameter("ctype").trim());
		try {
			conn = new DBConnect().getConnect("");
			SalaryService salaryService = new SalaryService(conn);
				
			if(!"".equals(pch) && !"".equals(ctype)){
				 
				salaryService.updateStatus(ctype, pch);		
			}
			if("业务部门审核".endsWith(ctype)){
				response.sendRedirect(request.getContextPath()+"/salary.do?method=businessList");
			}else{
				response.sendRedirect(request.getContextPath()+"/salary.do?method=personnelList");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		return null;
		
	}
	
	/**
	 * 我的工资单查询
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView mySalaryList(HttpServletRequest request, HttpServletResponse response){
		
		ModelAndView modelAndView = new ModelAndView(MYSQLARYLIST);
		UserSession userSession = (UserSession) request.getSession().getAttribute("userSession"); 
		Connection conn = null;
		try {
			
			ASFuntion asf = new ASFuntion();
			String year = asf.showNull(request.getParameter("year"));
			String month = asf.showNull(request.getParameter("month"));
			String pchname = asf.showNull(request.getParameter("pchname"));
			String userid = userSession.getUserId();
			String departmentid = userSession.getUserAuditDepartmentId();  
			
			if(!"".equals(year) && !"".equals(month) && !"".equals(pchname)){
				conn = new DBConnect().getConnect("");
				DbUtil db = new DbUtil(conn);
				String sql = "select * from k_dic where ctype = '工资分组' order by property,autoid";
				List dicList = db.getList(sql);
				
				//同一部门，同一人，同一年月，同一项目
				sql = "select * from k_salary where status ='已发放' and userId ='"+userid+"' and departmentId = '"+departmentid+"' and nowYear = '"+year+"' and nowMonth*1 = '"+month+"' and pchname ='"+pchname+"'  ";
				List list = db.getList(sql);
				
				StringBuffer sb = new StringBuffer("");
				for (int i = 0; i < list.size(); i++) {
					Map map = (Map)list.get(i);
					String groupSet = asf.showNull((String)map.get("groupset")); //分组
					
					sb.append("<span class='formTitle' >"+map.get("nowyear")+"年"+map.get("nowmonth")+"月&nbsp;&nbsp;【"+userSession.getUserName()+"】"+map.get("pchname")+"</span>");
					sb.append("<table class='formTable' style='width:80%;height: 10px;'>");
					
					int iSize = 0;
					double d1 = 0.00,d2 = 0.00,d3=0.00;
					
					StringBuffer sb2 = new StringBuffer(""); //合计
					
					for (int j = 0; j < dicList.size(); j++) {
						String dicValue = StringUtil.showNull(((Map)dicList.get(j)).get("value"));
						
						StringBuffer sb1 = new StringBuffer("");
						sb1.append("<tr><th colspan=4 style='width:20%;line-height: 10px;text-align: center;'>").append(dicValue).append("</th></tr>");
						String tr = "<tr>";
						int iTr = 0;
						for (int ii = 0; ii < 70; ii++) {
							String value = (String) map.get("n" + (ii+1));
							if(!"".equals(asf.showNull(value)) && (","+groupSet+",").indexOf(","+dicValue+"."+value+",")>-1){
								
								sb1.append(tr);
								sb1.append("<th  style='width:20%;line-height: 10px;text-align: left;'>"+value+"</th>");
								//sb1.append(asf.showMoney((String)map.get("c" + (ii+1))));
								sb1.append("<td style='text-align: left;'>"+asf.showMoney3((String)map.get("c" + (ii+1)))+"</td>");
								if(iTr % 2 == 1){
									tr = "</tr><tr >";
								}else{
									tr = "";
								}
								iTr ++;
								iSize ++;
								
								if("应发一".equals(dicValue)){
									if(value.indexOf("调整后")==-1){
										if(value.indexOf("调整")>-1){
											d1 -= Double.parseDouble((String)map.get("c" + (ii+1)));
										}else{
											d1 += Double.parseDouble((String)map.get("c" + (ii+1)));	
										}
									}
								}else if("应发二".equals(dicValue)){
									if(value.indexOf("调整后")==-1){
										if(value.indexOf("调整")>-1){
											d2 -= Double.parseDouble((String)map.get("c" + (ii+1)));
										}else{
											d2 += Double.parseDouble((String)map.get("c" + (ii+1)));	
										}
									}
								}else if("应扣".equals(dicValue)){
									if(value.indexOf("调整后")==-1){
										if(value.indexOf("调整")>-1){
											d3 -= Double.parseDouble((String)map.get("c" + (ii+1)));
										}else{
											d3 += Double.parseDouble((String)map.get("c" + (ii+1)));	
										}
									}
								}
								
							}
						}
						if(iSize != 0){
							sb.append(sb1);
						}
						
					}
					
					if(iSize == 0){
						String tr = "<tr>";
						int iTr = 0;
						for (int ii = 0; ii < 70; ii++) {
							String value = (String) map.get("n" + (ii+1));
							if(!"".equals(asf.showNull(value))){
								sb.append(tr);
								sb.append("<th  style='width:20%;line-height: 10px;text-align: left;'>"+value+"</th>");
								//sb.append(asf.showMoney((String)map.get("c" + (ii+1))));
								sb.append("<td style='text-align: left;'>"+asf.showMoney3((String)map.get("c" + (ii+1)))+"</td>");
								if(iTr % 2 == 1){
									tr = "</tr><tr >";
								}else{
									tr = "";
								}
								iTr ++;
								
								//合计
								if(value.indexOf("调整后")==-1){
									if(value.indexOf("调整")>-1){
										d1 -= Double.parseDouble((String)map.get("c" + (ii+1)));
									}else{
										d1 += Double.parseDouble((String)map.get("c" + (ii+1)));	
									}
								}
							}
						}
					}
					
					sb.append("<tr><th colspan=4 style='width:20%;line-height: 10px;text-align: center;'>合计</th></tr>");
					String tr = "<tr>";
					int iTr = 0;
					for (int j = 0; j < dicList.size(); j++) {
						String dicValue = StringUtil.showNull(((Map)dicList.get(j)).get("value"));
						sb.append(tr);
						sb.append("<th  style='width:20%;line-height: 10px;text-align: left;' >"+dicValue+"总额</th>");
						if("应发一".equals(dicValue)){
							sb.append("<td  style='text-align: left;'>"+asf.showMoney3(String.valueOf(d1))+"</td>");	
						}else if("应发二".equals(dicValue)){
							sb.append("<td style='text-align: left;'>"+asf.showMoney3(String.valueOf(d2))+"</td>");
						}else if("应扣".equals(dicValue)){
							sb.append("<td  style='text-align: left;'>"+asf.showMoney3(String.valueOf(d3))+"</td>");
						}
						if(iTr % 2 == 1){
							tr = "</tr><tr >";
						}else{
							tr = "";
						}
						iTr ++;
					}
					sb.append("<th  style='width:20%;line-height: 10px;text-align: left;' >实发总额</th>");
					sb.append("<td  style='text-align: left;'>"+asf.showMoney3(String.valueOf(d1+d2-d3))+"</td>");
					sb.append("</tr>");
					
					sb.append("<tr>");
					sb.append("<th  style='width:20%;line-height: 10px;' >备注</th>");
					sb.append("<td colspan=3 >"+map.get("memo")+"</td>");
					sb.append("</tr>");
					sb.append("</table>");

					
				}
				if(list == null || list.size() == 0){
					sb.append("<span class='formTitle' >该发放项目本月还没有发放工资，请重新查询</span>");
				}

				//数据list
				modelAndView.addObject("salary", sb.toString());
				
			}
			modelAndView.addObject("year", year);
			modelAndView.addObject("month", month);
			modelAndView.addObject("pchname", pchname);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{			
			DbUtil.close(conn);
		}
		
		return modelAndView;
	}

	public ModelAndView mySalaryList1(HttpServletRequest request, HttpServletResponse response){
		
		/*//需要加密狗登录
		if (JRockey2Opp.getUserLic() <= 0) {
			final String TRY_URL = "/AuditSystem/AS_SYSTEM/error_page.jsp?tip=999";
			try {
				response.sendRedirect(TRY_URL);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}*/
		ModelAndView modelAndView = new ModelAndView(MYSQLARYLIST);
		UserSession userSession = (UserSession) request.getSession().getAttribute("userSession"); 
		DataGridProperty pp = new DataGridProperty(); 
		ASFuntion asf = new ASFuntion();
		String year = asf.showNull(request.getParameter("year"));
		String month = asf.showNull(request.getParameter("month"));
		String pchname = asf.showNull(request.getParameter("pchname"));
		
		try {	
			
			String ppSql="SELECT concat(a.nowYear,'年',a.nowMonth,'月') as nowYearMoneth,b.name as userName,a.* \n" +
						"FROM `k_salary` a " +
						" left join k_user b on a.userid= b.id\n"+
						"where userId='"+userSession.getUserId()+"' and a.`status` ='已发放' "; 
			if(!"".equals(year) && !"".equals(month)){
				ppSql += " and nowYear='"+year+"' and nowMonth='"+month+"'";
				modelAndView.addObject("ifOne","one");      //有值时不弹框
				modelAndView.addObject("outputData","true"); 
			}else{
				modelAndView.addObject("outputData","false"); 
			}
			pp.setTableID("mySalaryList");
			pp.setCustomerId("");
			pp.setWhichFieldIsValue(1);
			pp.setInputType("radio");
			pp.setOrderBy_CH("autoId");
			pp.setDirection("desc");
			
			pp.setColumnWidth("8,8,8,8");
			pp.setSQL(ppSql);
			
			pp.addColumn("发放年月", "nowYearMoneth");
			pp.addColumn("发放员工", "userName");
			//pp.addColumn("总工资", "countValue","showMoney");
			
			pp.addSqlWhere("year"," and a.nowYear like '%${year}%' ");
			pp.addSqlWhere("month"," and a.nowMonth like '%${month}%' ");
			
			String sql = "SELECT * FROM `k_salary` where userId='"+userSession.getUserId()+"' and `status` ='已发放' ";
			
			if(!"".equals(year) && !"".equals(month)){
				sql += " and nowYear='"+year+"' and nowMonth='"+month+"'";
			}
			PreparedStatement ps = null;
			ResultSet rs = null;
			Connection conn = null;
			try {
				conn = new DBConnect().getConnect("");
				
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				ResultSetMetaData RSMD = rs.getMetaData();          //得到所有的列
				int j = 1;
				while (rs.next()) {
					
					for (int i = 1; i <= RSMD.getColumnCount(); i++) {
						String lineName = RSMD.getColumnLabel(i).toLowerCase();          //得到列名
						if(!"autoid".endsWith(lineName) && !"userid".endsWith(lineName) && 
							!"rankid".endsWith(lineName) && !"nowyear".endsWith(lineName) &&
							!"nowmonth".endsWith(lineName) && !"countvalue".endsWith(lineName)&& 
							!"departmentid".endsWith(lineName) && !"pch".endsWith(lineName) && 
							!"status".endsWith(lineName)){
							
								
							//判断得到的里面
							if(lineName.indexOf("n")>-1){
							//if(lineName.equals(asf.showNull("n"+j))){
								if(!"".equals(asf.showNull(rs.getString(lineName)))){
									String wagenames = asf.showNull(rs.getString(lineName));
									if(!wagenames.equals("工时工资标准") && !wagenames.equals("外勤补助标准") && !wagenames.equals("本月工时") && !wagenames.equals("本月外勤天数")){
										pp.addColumn(rs.getString(lineName),"c"+j,"showMoney");
									}
									j++;
								}
							}
						}
					}
					 
				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				DbUtil.close(ps);
				DbUtil.close(rs);
				DbUtil.close(conn);
			}
			
 			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{			
			request.getSession().setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);
		}
		
		return modelAndView;
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView listSalarybyDepart(HttpServletRequest request, HttpServletResponse response){
	 
		ModelAndView modelAndView = new ModelAndView(listSalayByDepart);
	//	UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");
		DataGridProperty pp = new DataGridProperty(); 
		ASFuntion asf = new ASFuntion();
		String year = asf.showNull(request.getParameter("year"));
		String month = asf.showNull(request.getParameter("month"));
		String departmentId = asf.showNull(request.getParameter("departmentId"));
		String pch = asf.showNull(request.getParameter("p_ch"));
		

		try {	
			
			String ppSql="SELECT concat(a.nowYear,'年',a.nowMonth,'月') as nowYearMoneth,b.name as userName,c.departname,a.* \n" +
						"FROM `k_salary` a " +
						" left join k_user b on a.userid= b.id\n"+
						" left join k_department c on a.departmentId = c.autoId \n"+
						"where   a.`status` ='已发放' "; 
			if(year.equals("") && month.equals("") && pch.equals("")){
				  ppSql +=" and nowyear = YEAR(now()) and nowmonth = (MONTH(now())-1)";
			}
			if(!"".equals(year) && !"".equals(month)){
				ppSql += " and nowYear='"+year+"' and nowMonth='"+month+"'";
				
			}
			if(!departmentId.equals("")){
				ppSql +=" and a.departmentId = "+departmentId;
			}
			if(!pch.equals("")){
				ppSql += " and pch='"+pch+"'";
			}
			pp.setTableID("listSaraly");
			pp.setCustomerId("");
			pp.setWhichFieldIsValue(1);
			pp.setInputType("radio");
		    pp.setPrintColumnWidth("8,8,8,8");
			
		    
		    
			pp.setColumnWidth("8,8,8,8");
			pp.setSQL(ppSql);
			pp.setOrderBy_CH("n1");
			pp.setDirection("desc");
			pp.setPrintEnable(true);
			pp.setPrintVerTical(false);
			pp.setPrintTitle("工资详情列表");
		
			
			pp.addColumn("发放年月", "nowYearMoneth");
			pp.addColumn("发放员工", "userName");
			pp.addColumn("所属部门", "departname");
			//pp.addColumn("总工资", "countValue","showMoney");
			
			pp.addSqlWhere("year"," and a.nowYear like '%${year}%' ");
			pp.addSqlWhere("month"," and a.nowMonth like '%${month}%' ");
			
			String sql = "SELECT * FROM `k_salary` where `status` ='已发放'  and n1<> 'null' ";
			
			if(!"".equals(year) && !"".equals(month)){
				sql += " and nowYear='"+year+"' and nowMonth='"+month+"' and departmentId='"+departmentId+"'" ;
			}
			sql +=" limit 1";
			PreparedStatement ps = null;
			ResultSet rs = null;
			Connection conn = null;
			try {
				conn = new DBConnect().getConnect("");
				
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				ResultSetMetaData RSMD = rs.getMetaData();          //得到所有的列
				int j = 1;
				while (rs.next()) {
					
					for (int i = 1; i <= RSMD.getColumnCount(); i++) {
						String lineName = RSMD.getColumnLabel(i).toLowerCase();          //得到列名
						if(!"autoid".endsWith(lineName) && !"userid".endsWith(lineName) && 
							!"rankid".endsWith(lineName) && !"nowyear".endsWith(lineName) &&
							!"nowmonth".endsWith(lineName) && !"countvalue".endsWith(lineName)&& 
							!"departmentid".endsWith(lineName) && !"pch".endsWith(lineName) && 
							!"status".endsWith(lineName)){
							
								
							//判断得到的里面
							if(lineName.indexOf("n")>-1){
							//if(lineName.equals(asf.showNull("n"+j))){
								if(!"".equals(asf.showNull(rs.getString(lineName)))){
									pp.addColumn(rs.getString(lineName),"v"+j,"showMoney");
									j++;
								}
							}
						}
					}
					 
				}
				modelAndView.addObject("pch", pch);
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				DbUtil.close(ps);
				DbUtil.close(rs);
				DbUtil.close(conn);
			}
			
 			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{			
			request.getSession().setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);
		}
		
		return modelAndView;
	}
	public ModelAndView detailWageAll(HttpServletRequest request, HttpServletResponse response){
		ModelAndView modelAndView = new ModelAndView("salary/listSalary.jsp");
        ASFuntion asf = new ASFuntion();
        List dataGridList = new ArrayList();
        String pch = asf.showNull(request.getParameter("pch"));
        String year = asf.showNull(request.getParameter("year"));
        String month = asf.showNull(request.getParameter("month"));
        String departmentId = asf.showNull(request.getParameter("departmentId"));
        
		Connection conn = null;
		Connection conn1 = null;
        try{
        	conn = new DBConnect().getConnect("");
        	SalaryService salaryService = new SalaryService(conn);
        	Map wageMap = salaryService.wageAllMap();
        	List wageList = salaryService.wageAllList();
        	String json = "{}";
			if(wageList != null){
				json = JSONArray.fromObject(wageList).toString();
			}
			modelAndView.addObject("json",json);
        	Set keySet  = wageMap.keySet();
        	int m =1;
        	for(Iterator iter = keySet.iterator();iter.hasNext();){
        		String key= (String)iter.next();
        		String value = (String)wageMap.get(key);
        		DataGridProperty pp = new DataGridProperty();
        		String ppSql ="SELECT concat(CONVERT(nowYear,char),'年',CONVERT(nowMonth,char),'月') as nowYearMoneth,b.name as userName,c.departname,d.name as rankName,a.* \n" +
							"FROM `k_salary` a " +
							" inner join k_user b on a.userid= b.id\n"+
							" inner join k_department c on a.departmentId = c.autoId " +
							" inner join k_rank d on a.rankId = d.autoId "+
							" and a.rankId in ("+key+")\n"+
							"  where   a.`status` ='已发放' ${year} ${month} ${departmentid} "; 
        	
        		pp.setTableID("listSaraly"+(m++));
    			pp.setCustomerId("");
    			pp.setWhichFieldIsValue(1);
    			pp.setInputType("radio");
    		    pp.setPrintColumnWidth("8,8,8,8");
    			
    		    
    		    
    			pp.setColumnWidth("8,8,8,8");
    			pp.setSQL(ppSql);
    			pp.setOrderBy_CH("nowYear,nowMonth");
    			pp.setDirection("desc,desc");
    			
    			pp.setPrintEnable(true);
    			pp.setPrintVerTical(false);
    			pp.setPrintTitle("工资详情列表");
    		
    			
    			pp.addColumn("发放年月", "nowYearMoneth");
    			pp.addColumn("发放员工", "userName");
    			pp.addColumn("所属部门", "departname");
    			pp.addColumn("职级", "rankName");
    			
    			
    			PreparedStatement ps = null;
    			ResultSet rs = null;
    			Connection conn2 = null;
    			String sql="SELECT * FROM `k_salary` where status ='已发放' "; 
        		sql+=" and rankId in("+key+") ORDER BY nowYear DESC,nowMonth DESC limit 1";
    			try {
    				conn2 = new DBConnect().getConnect("");
    				ps = conn2.prepareStatement(sql);
    				rs = ps.executeQuery();
    				ResultSetMetaData RSMD = rs.getMetaData();          //得到所有的列
    				int j = 1;
    				while (rs.next()) {
    					
    					for (int i = 1; i <= RSMD.getColumnCount(); i++) {
    						String lineName = RSMD.getColumnLabel(i).toLowerCase();          //得到列名
    						if(!"autoid".endsWith(lineName) && !"userid".endsWith(lineName) && 
    							!"rankid".endsWith(lineName) && !"nowyear".endsWith(lineName) &&
    							!"nowmonth".endsWith(lineName) && !"countvalue".endsWith(lineName)&& 
    							!"departmentid".endsWith(lineName) && !"pch".endsWith(lineName) && 
    							!"status".endsWith(lineName)){
    							
    								
    							//判断得到的里面
    							if(lineName.indexOf("n")>-1){
    							//if(lineName.equals(asf.showNull("n"+j))){
    								if(!"".equals(asf.showNull(rs.getString(lineName)))){
    									pp.addColumn(rs.getString(lineName),"v"+j,"showMoney");
    									j++;
    								}
    							}
    						}
    					}
    					 
    				}
    			} catch (Exception e) {
    				e.printStackTrace();
    			}finally{
    				DbUtil.close(ps);
    				DbUtil.close(rs);
    				DbUtil.close(conn);
    			}
    			pp.addSqlWhere("year", " and ifnull(a.nowYear,'') = '${year}' ");
    			pp.addSqlWhere("month", " and ifnull(a.nowMonth,'') = '${month}' ");
    			pp.addSqlWhere("departmentid", " and ifnull(a.departmentid,'') = '${departmentid}' ");
    			request.getSession().setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);
    			dataGridList.add(pp);
        
        	}
        	modelAndView.addObject("gridList",dataGridList);
        }catch(Exception e){
        	e.printStackTrace();
        }finally{
        	DbUtil.close(conn);
        }
        return modelAndView;
	}
	
	public ModelAndView detailWagesList(HttpServletRequest request, HttpServletResponse response){
		ModelAndView modelAndView = new ModelAndView("salary/listSalaryByDepart2.jsp");
        ASFuntion asf = new ASFuntion();
        List dataGridList = new ArrayList();
        String pch = asf.showNull(request.getParameter("pch"));
		Connection conn = null;
        try{
        	conn = new DBConnect().getConnect("");
        	SalaryService salaryService = new SalaryService(conn);
        	Map wageMap = salaryService.detailWageMap(pch);
        	List wageList = salaryService.detailWageList(pch);
        	String json = "{}";
			if(wageList != null){
				json = JSONArray.fromObject(wageList).toString();
			}
			modelAndView.addObject("json",json);
        	Set keySet  = wageMap.keySet();
        	int m =1;
        	for(Iterator iter = keySet.iterator();iter.hasNext();){
        		String key= (String)iter.next();
        		String value = (String)wageMap.get(key);
        		DataGridProperty pp = new DataGridProperty();
        		String ppSql="SELECT concat(CONVERT(nowYear,char),'年',CONVERT(nowMonth,char),'月') as nowYearMoneth,b.name as userName,c.departname,d.name as rankName,a.* \n" +
				"FROM `k_salary` a " +
				" inner join k_user b on a.userid= b.id\n"+
				" inner join k_department c on a.departmentId = c.autoId " +
				" inner join k_rank d on a.rankId = d.autoId "+
				" and a.rankId in ("+key+")\n"+
				"  where   a.`status` ='已发放' "; 
        		if(!pch.equals("")){
    				ppSql += " and pch='"+pch+"'";
    			}
        		pp.setTableID("listSaraly"+(m++));
    			pp.setCustomerId("");
    			pp.setWhichFieldIsValue(1);
    			pp.setInputType("radio");
    		    pp.setPrintColumnWidth("8,8,8,8");
    			
    		    
    		    
    			pp.setColumnWidth("8,8,8,8");
    			pp.setSQL(ppSql);
    			pp.setOrderBy_CH("nowYearMoneth");
    			pp.setDirection("desc");
    			
    			pp.setPrintEnable(true);
    			pp.setPrintVerTical(false);
    			pp.setPrintTitle("工资详情列表");
    		
    			
    			pp.addColumn("发放年月", "nowYearMoneth");
    			pp.addColumn("发放员工", "userName");
    			pp.addColumn("所属部门", "departname");
    			pp.addColumn("职级", "rankName");
    			
    			
    			PreparedStatement ps = null;
    			ResultSet rs = null;
    			Connection conn2 = null;
    			String sql="SELECT * FROM `k_salary` where status ='已发放'"; 
        		if(!pch.equals("")){
    				sql += " and pch='"+pch+"'";
    			}
        		sql+=" and rankId in("+key+") limit 1";
    			try {
    				conn2 = new DBConnect().getConnect("");
    				ps = conn2.prepareStatement(sql);
    				rs = ps.executeQuery();
    				ResultSetMetaData RSMD = rs.getMetaData();          //得到所有的列
    				int j = 1;
    				while (rs.next()) {
    					
    					for (int i = 1; i <= RSMD.getColumnCount(); i++) {
    						String lineName = RSMD.getColumnLabel(i).toLowerCase();          //得到列名
    						if(!"autoid".endsWith(lineName) && !"userid".endsWith(lineName) && 
    							!"rankid".endsWith(lineName) && !"nowyear".endsWith(lineName) &&
    							!"nowmonth".endsWith(lineName) && !"countvalue".endsWith(lineName)&& 
    							!"departmentid".endsWith(lineName) && !"pch".endsWith(lineName) && 
    							!"status".endsWith(lineName)){
    							
    								
    							//判断得到的里面
    							if(lineName.indexOf("n")>-1){
    							//if(lineName.equals(asf.showNull("n"+j))){
    								if(!"".equals(asf.showNull(rs.getString(lineName)))){
    									pp.addColumn(rs.getString(lineName),"v"+j,"showMoney");
    									j++;
    								}
    							}
    						}
    					}
    					 
    				}
    			} catch (Exception e) {
    				e.printStackTrace();
    			}finally{
    				DbUtil.close(ps);
    				DbUtil.close(rs);
    				DbUtil.close(conn);
    			}
    			request.getSession().setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);
    			dataGridList.add(pp);
    			
        	}
        	modelAndView.addObject("gridList",dataGridList);
        }catch(Exception e){
        	e.printStackTrace();
        }finally{
        	DbUtil.close(conn);
        }
        return modelAndView;
		 
	}
	
	/**
	 * 验证密码与身份证是否存在
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	public ModelAndView queryUser(HttpServletRequest request, HttpServletResponse response) throws IOException{
		// 获取登录的用户
		Connection conn = null;
		
		ASFuntion asf = new ASFuntion();
		String password = asf.showNull(request.getParameter("passowrd"));
		String identityCard = asf.showNull(request.getParameter("identityCard"));
		
		response.setCharacterEncoding("utf-8");
		PrintWriter out = response.getWriter();
		try {
				conn = new DBConnect().getConnect("");
				UserSession userSession = (UserSession) request.getSession().getAttribute("userSession"); 

			if(!"".equals(password) && !"".equals(password)){
				
				SalaryService salaryService = new SalaryService(conn);
				String sql = " SELECT COUNT(*) FROM k_user WHERE id='"+userSession.getUserId()+"' " +
							 "AND PASSWORD=MD5('"+password+"') AND identityCard='"+identityCard+"' ";
				String ifExist = asf.showNull(salaryService.getValueBySql(sql));
				
				out.write(ifExist);
			}else{
				out.write("");
			}	
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		return null;

	}
	
	/**
	 * 添加验证码
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	public ModelAndView addAuthCode(HttpServletRequest request, HttpServletResponse response) throws IOException{
		// 获取登录的用户
		Connection conn = null;
		
		ASFuntion asf = new ASFuntion();
		String module = asf.showNull(request.getParameter("module"));
		String aparttime = asf.showNull(request.getParameter("aparttime"));
		InteriorEmailAction interiorEmailAction = new InteriorEmailAction();
		response.setCharacterEncoding("utf-8");
		PrintWriter out = response.getWriter();
		try {
				conn = new DBConnect().getConnect("");
				UserSession userSession = (UserSession) request.getSession().getAttribute("userSession"); 
				Date dt= new Date();
				Long time= dt.getTime();//这就是距离1970年1月1日0点0分0秒的毫秒数
				String yzm = time.toString().substring(time.toString().length()-6, time.toString().length());

				Map<Object, String> map = new HashMap<Object, String>();
				map.put("module", module);
				map.put("yzm", yzm);
				map.put("currentdate", asf.getCurrentDate()+" "+asf.getCurrentTime());
				map.put("aparttime", aparttime);
				map.put("userid", userSession.getUserId());
				map.put("isuse", "否");
				//添加验证码表
				new DbUtil(conn).add("k_authcode", "autoId", map);
				String content =  "欢迎使用ERP工资查询功能，您的手机密钥为："+yzm+"，感谢您的使用！";
				String msg = interiorEmailAction.mobilePhoneInfo(request, response, "", userSession.getUserId(),content);
				
				
				out.write("短信发送成功");
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		return null;

	}
	
	
	/**
	 * 得到验证码
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	public void getYzm(HttpServletRequest request, HttpServletResponse response) throws IOException{
		// 获取登录的用户
		Connection conn = null;
		
		ASFuntion asf = new ASFuntion();
		String module = asf.showNull(request.getParameter("module"));
		String yzm = asf.showNull(request.getParameter("yzm"));
		response.setCharacterEncoding("utf-8");
		PrintWriter out = response.getWriter();
		try {
				conn = new DBConnect().getConnect("");
				UserSession userSession = (UserSession) request.getSession().getAttribute("userSession"); 

				String sql = "SELECT IF(ADDDATE(currentDate,INTERVAL apartTime MINUTE)<NOW(),'false',yzm) \n"
							+"FROM k_authcode  \n"
							+"WHERE userid='"+userSession.getUserId()+"' and yzm='"+yzm+"' AND isuse='否' AND module='"+module+"' ORDER BY autoId DESC LIMIT 1";
					
				//添加验证码表
				yzm = new DbUtil(conn).queryForString(sql);
				
				out.write(yzm);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}

	}
	
	/**
	 * 作废验证码
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	public void updateYzmIsUse(HttpServletRequest request, HttpServletResponse response) throws IOException{
		// 获取登录的用户
		Connection conn = null;
		
		ASFuntion asf = new ASFuntion();
		String module = asf.showNull(request.getParameter("module"));
		String yzm = asf.showNull(request.getParameter("yzm"));
		response.setCharacterEncoding("utf-8");
		//PrintWriter out = response.getWriter();
		try {
				conn = new DBConnect().getConnect("");
				UserSession userSession = (UserSession) request.getSession().getAttribute("userSession"); 

				String sql = "UPDATE k_authcode SET isuse='是' " +
							"WHERE module='"+module+"' AND userId='"+userSession.getUserId()+"' " +
							"AND yzm='"+yzm+"'  AND currentdate LIKE '"+asf.getCurrentDate()+"%' ORDER BY autoid DESC LIMIT 1 ";
					
				//添加验证码表
				 new DbUtil(conn).execute(sql);
				
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}

	}
	
	
	/**
	 * 弹性工资录入、修改
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView departSalaryEdit(HttpServletRequest request, HttpServletResponse response){
		
		//ModelAndView modelAndView = new ModelAndView(SOFTSALARYLIST);
		
		ASFuntion asf = new ASFuntion();
		Connection conn = null;
		try {
			response.setCharacterEncoding("utf-8");
			request.setCharacterEncoding("utf-8");
			String pch = UUID.randomUUID().toString();
			String year = asf.showNull(request.getParameter("todayYear"));
			String month = asf.showNull(request.getParameter("todayMonth"));
			String departmentId = asf.showNull(request.getParameter("departmentId"));
			String userIds = asf.showNull(request.getParameter("userIds"));
			PrintWriter out = response.getWriter() ; 
			conn = new DBConnect().getConnect("");
			SalaryService salaryService = new SalaryService(conn);
			salaryService.initPch(year, month, departmentId, userIds,pch);   //录入批次码
			//salaryService.updateSoftSalary(pch);
			List<Map> salaryList = salaryService.getSelaryList(year, month, departmentId, userIds);
			
			String numOnly = " onkeyup=\" value=value.replace(/[^\\d]/g,\'\') \"  onbeforepaste=\"clipboardData.setData(\'text\',clipboardData.getData(\'text\').replace(/[^\\d]/g,\'\'))\" ";  //只能输入数字
			String newTable = "<table class=\"data_tb\" >";
			
				newTable +="<tr> ";
				newTable +="<td class=\"data_tb_alignright\">姓名</td> ";
				newTable +="<td class=\"data_tb_alignright\">薪酬级别</td> ";
				newTable +="<td class=\"data_tb_alignright\">性别</td> ";
				newTable +="<td class=\"data_tb_alignright\">本月工时</td> ";
				newTable +="<td class=\"data_tb_alignright\">本月外勤天数</td> ";
				newTable +="<td class=\"data_tb_alignright\">绩效工资</td> ";
				newTable +="<td class=\"data_tb_alignright\">其他补助</td> ";
				newTable +="</tr>  ";
			
			
			for (int i = 0; i < salaryList.size(); i++) {
				Map map = salaryList.get(i);
				newTable +="<tr><td><input type=\"hidden\" name=\"k_autoId\" value=\""+map.get("autoId")+"\" ></td></tr>";
				newTable +="<tr>";
				newTable +="<td class=\"data_tb_content\">";
				newTable +=map.get("name");
				newTable +="</td>";
				newTable +="<td class=\"data_tb_content\">";
				newTable +=map.get("rank");
				newTable +="</td>";
				newTable +="<td class=\"data_tb_content\">";
				String sex = "";
				if("M".equals(map.get("sex").toString().trim())){
					sex = "男";
				}else{
					sex = "女";
				}
				newTable +=sex;
				newTable +="</td>";
			
				newTable +="<td class=\"data_tb_content\">";
				String v17 =map.get("v17") == null?"0": map.get("v17").toString();
				newTable +="<input type=\"text\" name=\"autoId\" "+numOnly+" onpropertychange=updateSalary('"+map.get("autoId")+"','v17',this.value) value="+v17+"  size=\"10\" >";
				newTable +="</td>";
				
				newTable +="<td class=\"data_tb_content\">";
				String v18 =map.get("v18") == null?"0": map.get("v18").toString();
				newTable +="<input type=\"text\" name=\"autoId\" "+numOnly+" onpropertychange=updateSalary('"+map.get("autoId")+"','v18',this.value) value="+v18+" size=\"10\">";
				newTable +="</td>";
				
				newTable +="<td class=\"data_tb_content\">";
				String v19 =map.get("v19") == null?"0": map.get("v19").toString();
				newTable +="<input type=\"text\" name=\"autoId\" "+numOnly+" onpropertychange=updateSalary('"+map.get("autoId")+"','v19',this.value) value="+v19+" size=\"10\">";
				newTable +="</td>";
				
				newTable +="<td class=\"data_tb_content\">";
				String v20 =map.get("v20") == null?"0": map.get("v20").toString();
				newTable +="<input type=\"text\" name=\"autoId\" "+numOnly+" onpropertychange=updateSalary('"+map.get("autoId")+"','v20',this.value) value="+v20+" size=\"10\">";
				newTable +="</td>";
				newTable +="</tr>";
				 
			}
			newTable +="</table>";
			out.write(newTable);
		
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		return null;
		
	}
	
	/**
	 * 工资发放
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView giveSalaryEdit(HttpServletRequest request, HttpServletResponse response){
		
		//ModelAndView modelAndView = new ModelAndView(SOFTSALARYLIST);
		
		ASFuntion asf = new ASFuntion();
		Connection conn = null;
		try {
			response.setCharacterEncoding("utf-8");
			request.setCharacterEncoding("utf-8");
			String year = asf.showNull(request.getParameter("todayYear"));
			String month = asf.showNull(request.getParameter("todayMonth"));
			String departmentId = asf.showNull(request.getParameter("departmentId"));
			String userIds = asf.showNull(request.getParameter("userIds"));
			PrintWriter out = response.getWriter() ; 
			conn = new DBConnect().getConnect("");
			SalaryService salaryService = new SalaryService(conn);
			List<Map> salaryList = salaryService.getSelaryList(year, month, departmentId, userIds);
			
			String numOnly = " onkeyup=\" value=value.replace(/[^\\d]/g,\'\') \"  onbeforepaste=\"clipboardData.setData(\'text\',clipboardData.getData(\'text\').replace(/[^\\d]/g,\'\'))\" ";  //只能输入数字
			String newTable = "<table class=\"data_tb\" >";
			
				newTable +="<tr> ";
				newTable +="<td class=\"data_tb_alignright\">&nbsp;姓名&nbsp;</td> ";
				newTable +="<td class=\"data_tb_alignright\">部门</td> ";
				newTable +="<td class=\"data_tb_alignright\">薪酬级别</td> ";
				newTable +="<td class=\"data_tb_alignright\">基础工资</td> ";
				newTable +="<td class=\"data_tb_alignright\">工时<br/>标准</td> ";
				newTable +="<td class=\"data_tb_alignright\">本月<br/>工时</td> ";
				newTable +="<td class=\"data_tb_alignright\">工时工资</td> ";
				newTable +="<td class=\"data_tb_alignright\">外勤<br/>补助标准</td> ";
				newTable +="<td class=\"data_tb_alignright\">本月<br/>外勤天数</td> ";
				newTable +="<td class=\"data_tb_alignright\">外勤<br/>补助</td> ";
				newTable +="<td class=\"data_tb_alignright\">绩效工资</td> ";
				newTable +="<td class=\"data_tb_alignright\">其他补助</td> ";
				newTable +="<td class=\"data_tb_alignright\">缺勤减薪</td> ";
				newTable +="<td class=\"data_tb_alignright\">其他减薪</td> ";
				newTable +="<td class=\"data_tb_alignright\">收入总和</td> ";
				newTable +="<td class=\"data_tb_alignright\">社保计提</td> ";
				newTable +="<td class=\"data_tb_alignright\">社保调整</td> ";
				newTable +="<td class=\"data_tb_alignright\">计税收入</td> ";
				newTable +="<td class=\"data_tb_alignright\">代扣个税</td> ";
				newTable +="<td class=\"data_tb_alignright\">财务扣款</td> ";
				newTable +="<td class=\"data_tb_alignright\">其他扣款</td> ";
				newTable +="<td class=\"data_tb_alignright\">实发工资</td>  ";
				newTable +="<td class=\"data_tb_alignright\">备注</td>  ";
				newTable +="</tr>  ";
			
			
			for (int i = 0; i < salaryList.size(); i++) {
				Map map = salaryList.get(i);
				newTable +="<tr><td><input type=\"hidden\" name=\"k_autoId\" value=\""+map.get("autoId")+"\" ></td></tr>";
				newTable +="<tr>";
				newTable +="<td class=\"data_tb_content\">";
				newTable +=map.get("name");
				newTable +="</td>";
				newTable +="<td class=\"data_tb_content\">";
				newTable +=map.get("departname");
				newTable +="</td>";
				newTable +="<td class=\"data_tb_content\">";
				newTable +=map.get("rank");
				newTable +="</td>";
				newTable +="<td class=\"data_tb_content\">";
				newTable +=map.get("countValue");
				newTable +="</td>";
				newTable +="<td class=\"data_tb_content\">";
				newTable +=map.get("v2");
				newTable +="</td>";
				newTable +="<td class=\"data_tb_content\">";
				newTable +=map.get("gs");
				newTable +="</td>";
				newTable +="<td class=\"data_tb_content\">";
				newTable +=map.get("gsgz");
				newTable +="</td>";
				newTable +="<td class=\"data_tb_content\">";
				newTable +=map.get("v9");
				newTable +="</td>";
				newTable +="<td class=\"data_tb_content\">";
				newTable +=map.get("wqDays");
				newTable +="</td>";
				newTable +="<td class=\"data_tb_content\">";
				newTable +=map.get("wqbz");
				newTable +="</td>";
				newTable +="<td class=\"data_tb_content\">";
				newTable +=map.get("v19");
				newTable +="</td>";
			
				newTable +="<td class=\"data_tb_content\">";
				String v20 =map.get("v20") == null?"0": map.get("v20").toString();
				newTable +="<input  id=\"qtbz_"+map.get("userId")+"\" type=\"text\" "+numOnly+" onpropertychange=ajaxEditSarayByRs('"+map.get("pch")+"','"+map.get("userId")+"','v20','qtbz_"+map.get("userId")+"') value="+v20+" size=\"10\" >";
				newTable +="</td>";
				
				newTable +="<td class=\"data_tb_content\">";
				String v21 =map.get("v21") == null?"0": map.get("v21").toString();
				newTable +="<input  id=\"qqjx_"+map.get("userId")+"\" type=\"text\" "+numOnly+" onpropertychange=ajaxEditSarayByRs('"+map.get("pch")+"','"+map.get("userId")+"','v21','qqjx_"+map.get("userId")+"') value="+v21+" size=\"10\" >";
				newTable +="</td>";
				
				newTable +="<td class=\"data_tb_content\">";
				String v22 =map.get("v22") == null?"0": map.get("v22").toString();
				newTable +="<input  id=\"qtjx_"+map.get("userId")+"\" type=\"text\" "+numOnly+" onpropertychange=ajaxEditSarayByRs('"+map.get("pch")+"','"+map.get("userId")+"','v22','qtjx_"+map.get("userId")+"') value="+v22+" size=\"10\" >";
				newTable +="</td>";

				newTable +="<td class=\"data_tb_content\" ";
				String v23 =map.get("v23") == null?"0": map.get("v23").toString();
				newTable +=" id=\"totalSalary_"+map.get("userId")+"\" > "+v23;
				newTable +="</td>";
				
				newTable +="<td class=\"data_tb_content\">";
				String v24 =map.get("v24") == null?"0": map.get("v24").toString();
				newTable +="<input  id=\"sbjt_"+map.get("userId")+"\" type=\"text\" "+numOnly+" onpropertychange=ajaxEditSarayKouSui('"+map.get("pch")+"','"+map.get("userId")+"','v24','sbjt_"+map.get("userId")+"','sbtz_"+map.get("userId")+"') value="+v24+" size=\"10\" >";
				newTable +="</td>";
				
				newTable +="<td class=\"data_tb_content\">";
				String v25 =map.get("v25") == null?"0": map.get("v25").toString();
				newTable +="<input  id=\"sbtz_"+map.get("userId")+"\" type=\"text\" "+numOnly+" onpropertychange=ajaxEditSarayKouSui('"+map.get("pch")+"','"+map.get("userId")+"','v25','sbtz_"+map.get("userId")+"','sbjt_"+map.get("userId")+"') value="+v25+" size=\"10\" >";
				newTable +="</td>";
				
				newTable +="<td class=\"data_tb_content\" ";
				String v26 =map.get("v26") == null?"0": map.get("v26").toString();
				newTable +=" id=\"jssrSalary_"+map.get("userId")+"\" > "+v26;
				newTable +="</td>";
				
				newTable +="<td class=\"data_tb_content\">";
				String v27 =map.get("v27") == null?"0": map.get("v27").toString();
				newTable +="<input  id=\"dkgsSalary_"+map.get("userId")+"\" type=\"text\"  disabled=\"true\" value="+v27+" size=\"10\" >";
				newTable +="</td>";
				
				newTable +="<td class=\"data_tb_content\">";
				String v28 =map.get("v28") == null?"0": map.get("v28").toString();
				newTable +="<input  id=\"cwkk_"+map.get("userId")+"\" type=\"text\" "+numOnly+" onpropertychange=ajaxEditSarayShiFa('"+map.get("pch")+"','"+map.get("userId")+"','v28','cwkk_"+map.get("userId")+"','dkgsSalary_"+map.get("userId")+"','qtkk_"+map.get("userId")+"') value="+v28+" size=\"10\" >";
				newTable +="</td>";
				
				newTable +="<td class=\"data_tb_content\">";
				String v29 =map.get("v29") == null?"0": map.get("v29").toString();
				newTable +="<input  id=\"qtkk_"+map.get("userId")+"\" type=\"text\" "+numOnly+" onpropertychange=ajaxEditSarayShiFa('"+map.get("pch")+"','"+map.get("userId")+"','v29','qtkk_"+map.get("userId")+"','dkgsSalary_"+map.get("userId")+"','cwkk_"+map.get("userId")+"') value="+v29+" size=\"10\" >";
				newTable +="</td>";
				
				newTable +="<td class=\"data_tb_content\" ";
				String v30 =map.get("v30") == null?"0": map.get("v30").toString();
				newTable +=" id=\"sfgzSalary_"+map.get("userId")+"\" > "+v30;
				newTable +="</td>";
				
				newTable +="<td class=\"data_tb_content\">";
				String c20 =map.get("c20") == null?"0": map.get("c20").toString();
				newTable +="<input type=\"text\"  onpropertychange=updateSalary('"+map.get("autoId")+"','c20',this.value) value="+c20+" size=\"10\">";
				newTable +="</td>";
				
				
				 
			}
			newTable +="</table>";
			out.write(newTable);
		
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		return null;
		
	}
	
	/**
	 * 部门修改工资
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	public void updateSalary(HttpServletRequest request, HttpServletResponse response) throws IOException{
		// 获取登录的用户
		Connection conn = null;
		
		ASFuntion asf = new ASFuntion();
		String autoId = asf.showNull(request.getParameter("autoId"));
		String line = asf.showNull(request.getParameter("line"));
		String lineValue = asf.showNull(request.getParameter("lineValue"));
		response.setCharacterEncoding("utf-8");
		PrintWriter out = response.getWriter();
		try {
				conn = new DBConnect().getConnect("");

				String sql = " update k_salary set "+line+" = '"+lineValue+"' where autoid="+autoId+"";
					
				//添加验证码表
				out.write(new DbUtil(conn).executeUpdate(sql)+"");
				
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}

	}
	
	/**
	 * 提交或者暂存方法
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	public void saveAndTemp(HttpServletRequest request, HttpServletResponse response) throws IOException{
		// 获取登录的用户
		Connection conn = null;
		
		ASFuntion asf = new ASFuntion();
		String autoIds = asf.showNull(request.getParameter("autoids"));
		String ctype = asf.showNull(request.getParameter("ctype")); //暂存还是提交
		PrintWriter out = response.getWriter();
		try {
				conn = new DBConnect().getConnect("");
				String status = "";
				if("提交".equals(ctype)){
					status = "已录弹性工资"; 
				}else if("暂存".equals(ctype)){
					status  = "暂存";
				}else if("确定发放".equals(ctype)){
					status  = "已发放";
				}
				
				String sql = " update k_salary set status = '"+status+"' where autoid in ("+autoIds+") ";
					
				out.write(new DbUtil(conn).executeUpdate(sql)+"");
				
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}

	}
	public void checkUpdate(HttpServletRequest request, HttpServletResponse response){
		Connection conn = null;
		ASFuntion asf = new ASFuntion();
		String pch = asf.showNull(request.getParameter("p_ch"));
			try {
				PrintWriter out = response.getWriter();
				conn = new DBConnect().getConnect("");
				SalaryService salaryService = new SalaryService(conn);
				String result = salaryService.checkUpdate(pch);
				out.write(result);
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				DbUtil.close(conn);
			}
			
	}
	/**
	 * 检查k_user表是否存在选中年月的数据
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	public void checkNY(HttpServletRequest request, HttpServletResponse response) throws IOException{
		// 获取登录的用户
		Connection conn = null;
			
		ASFuntion asf = new ASFuntion();
		String yearNow = asf.showNull(request.getParameter("yearNow"));
		String monthNow = asf.showNull(request.getParameter("monthNow")); 
		String departmentId = asf.showNull(request.getParameter("departmentId")); 
		PrintWriter out = response.getWriter();
		try {
				conn = new DBConnect().getConnect("");
				String sql = " select count(*) from k_salary where nowYear='"+yearNow+"' and nowMonth='"+monthNow+"' "+" and departmentId in ('"+departmentId+"') and pchname = '月度工资' ";
				String result = new DbUtil(conn).queryForString(sql);
				
				if(result.equals("0")){
					out.write("yes");
				}else{
					out.write("no");
				}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
	}
	
	
	/**
	 *检查状态
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException 
	 */
	public void checkStatus(HttpServletRequest request, HttpServletResponse response) throws IOException{
		ASFuntion asf = new ASFuntion();
		response.setCharacterEncoding("utf-8");
		PrintWriter out = response.getWriter();
		Connection conn = null;
		try{
			conn = new DBConnect().getConnect("");
			String pch = asf.showNull(request.getParameter("pch"));
			SalaryService salaryService = new SalaryService(conn);
			String result = salaryService.checkStatus(pch);
			if(result.equals("暂存")){
				result ="yes";
			}else{
				result ="no";
			}
			out.write(result);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
	}
	/**
	 * 修改方法
	 * @param request
	 * @param response
	 */
	public ModelAndView update(HttpServletRequest request, HttpServletResponse response){
		
		UserSession userSession = (UserSession) request.getSession().getAttribute("userSession"); 
		String departmentid = userSession.getUserAuditDepartmentId();   //得到当前部门
		Connection conn = null;
		
		String pch = request.getParameter("pch");
		String YY = request.getParameter("Nian");
		String MM = request.getParameter("Yue");
		List<Salary> list = new ArrayList<Salary>();
		String[] ranks = request.getParameterValues("rank");
		String flag = request.getParameter("flag");
		try {	
			for(int i = 0;i<ranks.length;i++){
				String[] userIds = request.getParameterValues("userId"+ranks[i]);
			   if(userIds!=null){	
				for(int j=0;j<userIds.length;j++){
					Salary salary = new Salary();
			    	salary.setUserId(userIds[j]);
			    	salary.setRankId(ranks[i]);
			    	salary.setCountValue((request.getParameter(userIds[j]+"-countValue")==null)?"0":(request.getParameter(userIds[j]+"-countValue")));
			    	if(flag.equals("1")){
			    		salary.setStatus("暂存");
			    	}else{
			    		salary.setStatus("等待人事处理");
			    	}
			    	//salary.setStatus("部门暂存");
			    	
					salary.setNowYear(YY);
					salary.setNowMonth(MM);
					salary.setDepartmentId(departmentid);
					salary.setPch(pch);
					salary.setN1(request.getParameter(userIds[j]+"-n1"));
					salary.setN2(request.getParameter(userIds[j]+"-n2"));
					salary.setN3(request.getParameter(userIds[j]+"-n3"));
					salary.setN4(request.getParameter(userIds[j]+"-n4"));
					salary.setN5(request.getParameter(userIds[j]+"-n5"));
					
					salary.setN6(request.getParameter(userIds[j]+"-n6"));
					salary.setN7(request.getParameter(userIds[j]+"-n7"));
					salary.setN8(request.getParameter(userIds[j]+"-n8"));
					salary.setN9(request.getParameter(userIds[j]+"-n9"));
					salary.setN10(request.getParameter(userIds[j]+"-n10"));
					
					salary.setN11(request.getParameter(userIds[j]+"-n11"));
					salary.setN12(request.getParameter(userIds[j]+"-n12"));
					salary.setN13(request.getParameter(userIds[j]+"-n13"));
					salary.setN14(request.getParameter(userIds[j]+"-n14"));
					salary.setN15(request.getParameter(userIds[j]+"-n15"));
					
					salary.setN16(request.getParameter(userIds[j]+"-n16"));
					salary.setN17(request.getParameter(userIds[j]+"-n17"));
					salary.setN18(request.getParameter(userIds[j]+"-n18"));
					salary.setN19(request.getParameter(userIds[j]+"-n19"));
					salary.setN20(request.getParameter(userIds[j]+"-n20"));
					
					salary.setN21(request.getParameter(userIds[j]+"-n21"));
					salary.setN22(request.getParameter(userIds[j]+"-n22"));
					salary.setN23(request.getParameter(userIds[j]+"-n23"));
					salary.setN24(request.getParameter(userIds[j]+"-n24"));
					salary.setN25(request.getParameter(userIds[j]+"-n25"));
					
					salary.setN26(request.getParameter(userIds[j]+"-n26"));
					salary.setN27(request.getParameter(userIds[j]+"-n27"));
					salary.setN28(request.getParameter(userIds[j]+"-n28"));
					salary.setN29(request.getParameter(userIds[j]+"-n29"));
					salary.setN30(request.getParameter(userIds[j]+"-n30"));
					
					salary.setC1(request.getParameter(userIds[j]+"-c1"));
					salary.setC2(request.getParameter(userIds[j]+"-c2"));
					salary.setC3(request.getParameter(userIds[j]+"-c3"));
					salary.setC4(request.getParameter(userIds[j]+"-c4"));
					salary.setC5(request.getParameter(userIds[j]+"-c5"));
					
					salary.setC6(request.getParameter(userIds[j]+"-c6"));
					salary.setC7(request.getParameter(userIds[j]+"-c7"));
					salary.setC8(request.getParameter(userIds[j]+"-c8"));
					salary.setC9(request.getParameter(userIds[j]+"-c9"));
					salary.setC10(request.getParameter(userIds[j]+"-c10"));
					
					salary.setC11(request.getParameter(userIds[j]+"-c11"));
					salary.setC12(request.getParameter(userIds[j]+"-c12"));
					salary.setC13(request.getParameter(userIds[j]+"-c13"));
					salary.setC14(request.getParameter(userIds[j]+"-c14"));
					salary.setC15(request.getParameter(userIds[j]+"-c15"));
					
					salary.setC16(request.getParameter(userIds[j]+"-c16"));
					salary.setC17(request.getParameter(userIds[j]+"-c17"));
					salary.setC18(request.getParameter(userIds[j]+"-c18"));
					salary.setC19(request.getParameter(userIds[j]+"-c19"));
					salary.setC20(request.getParameter(userIds[j]+"-c20"));
					
					salary.setC21(request.getParameter(userIds[j]+"-c21"));
					salary.setC22(request.getParameter(userIds[j]+"-c22"));
					salary.setC23(request.getParameter(userIds[j]+"-c23"));
					salary.setC24(request.getParameter(userIds[j]+"-c24"));
					salary.setC25(request.getParameter(userIds[j]+"-c25"));
					
					salary.setC26(request.getParameter(userIds[j]+"-c26"));
					salary.setC27(request.getParameter(userIds[j]+"-c27"));
					salary.setC28(request.getParameter(userIds[j]+"-c28"));
					salary.setC29(request.getParameter(userIds[j]+"-c29"));
					salary.setC30(request.getParameter(userIds[j]+"-c30"));
					
					if(request.getParameter(userIds[j]+"-c1")!=null){
						String[] wagesValue = request.getParameterValues(request.getParameter(userIds[j]+"-c1")+ranks[i]);
						salary.setV1(wagesValue[j]);
					}
					if(request.getParameter(userIds[j]+"-c2")!=null){
						String[] wagesValue = request.getParameterValues(request.getParameter(userIds[j]+"-c2")+ranks[i]);
						salary.setV2(wagesValue[j]);
					}
					if(request.getParameter(userIds[j]+"-c3")!=null){
						String[] wagesValue = request.getParameterValues(request.getParameter(userIds[j]+"-c3")+ranks[i]);
						salary.setV3(wagesValue[j]);
					}
					if(request.getParameter(userIds[j]+"-c4")!=null){
						String[] wagesValue = request.getParameterValues(request.getParameter(userIds[j]+"-c4")+ranks[i]);
						salary.setV4(wagesValue[j]);
					}
					if(request.getParameter(userIds[j]+"-c5")!=null){
						String[] wagesValue = request.getParameterValues(request.getParameter(userIds[j]+"-c5")+ranks[i]);
						salary.setV5(wagesValue[j]);
					}
					if(request.getParameter(userIds[j]+"-c6")!=null){
						String[] wagesValue = request.getParameterValues(request.getParameter(userIds[j]+"-c6")+ranks[i]);
						salary.setV6(wagesValue[j]);
					}
					if(request.getParameter(userIds[j]+"-c7")!=null){
						String[] wagesValue = request.getParameterValues(request.getParameter(userIds[j]+"-c7")+ranks[i]);
						salary.setV7(wagesValue[j]);
					}
					if(request.getParameter(userIds[j]+"-c8")!=null){
						String[] wagesValue = request.getParameterValues(request.getParameter(userIds[j]+"-c8")+ranks[i]);
						salary.setV8(wagesValue[j]);
					}
					if(request.getParameter(userIds[j]+"-c9")!=null){
						String[] wagesValue = request.getParameterValues(request.getParameter(userIds[j]+"-c9")+ranks[i]);
						salary.setV9(wagesValue[j]);
					}
					if(request.getParameter(userIds[j]+"-c10")!=null){
						String[] wagesValue = request.getParameterValues(request.getParameter(userIds[j]+"-c10")+ranks[i]);
						salary.setV10(wagesValue[j]);
					}
					if(request.getParameter(userIds[j]+"-c11")!=null){
						String[] wagesValue = request.getParameterValues(request.getParameter(userIds[j]+"-c11")+ranks[i]);
						salary.setV11(wagesValue[j]);
					}
					if(request.getParameter(userIds[j]+"-c12")!=null){
						String[] wagesValue = request.getParameterValues(request.getParameter(userIds[j]+"-c12")+ranks[i]);
						salary.setV12(wagesValue[j]);
					}
					if(request.getParameter(userIds[j]+"-c13")!=null){
						String[] wagesValue = request.getParameterValues(request.getParameter(userIds[j]+"-c13")+ranks[i]);
						salary.setV13(wagesValue[j]);
					}
					if(request.getParameter(userIds[j]+"-c14")!=null){
						String[] wagesValue = request.getParameterValues(request.getParameter(userIds[j]+"-c14")+ranks[i]);
						salary.setV14(wagesValue[j]);
					}
					if(request.getParameter(userIds[j]+"-c15")!=null){
						String[] wagesValue = request.getParameterValues(request.getParameter(userIds[j]+"-c15")+ranks[i]);
						salary.setV15(wagesValue[j]);
					}
					if(request.getParameter(userIds[j]+"-c16")!=null){
						String[] wagesValue = request.getParameterValues(request.getParameter(userIds[j]+"-c16")+ranks[i]);
						salary.setV16(wagesValue[j]);
					}
					if(request.getParameter(userIds[j]+"-c17")!=null){
						String[] wagesValue = request.getParameterValues(request.getParameter(userIds[j]+"-c17")+ranks[i]);
						salary.setV17(wagesValue[j]);
					}
					if(request.getParameter(userIds[j]+"-c18")!=null){
						String[] wagesValue = request.getParameterValues(request.getParameter(userIds[j]+"-c18")+ranks[i]);
						salary.setV18(wagesValue[j]);
					}
					if(request.getParameter(userIds[j]+"-c19")!=null){
						String[] wagesValue = request.getParameterValues(request.getParameter(userIds[j]+"-c19")+ranks[i]);
						salary.setV19(wagesValue[j]);
					}
					if(request.getParameter(userIds[j]+"-c20")!=null){
						String[] wagesValue = request.getParameterValues(request.getParameter(userIds[j]+"-c20")+ranks[i]);
						salary.setV20(wagesValue[j]);
					}
					if(request.getParameter(userIds[j]+"-c21")!=null){
						String[] wagesValue = request.getParameterValues(request.getParameter(userIds[j]+"-c21")+ranks[i]);
						salary.setV21(wagesValue[j]);
					}
					if(request.getParameter(userIds[j]+"-c22")!=null){
						String[] wagesValue = request.getParameterValues(request.getParameter(userIds[j]+"-c22")+ranks[i]);
						salary.setV22(wagesValue[j]);
					}
					if(request.getParameter(userIds[j]+"-c23")!=null){
						String[] wagesValue = request.getParameterValues(request.getParameter(userIds[j]+"-c23")+ranks[i]);
						salary.setV23(wagesValue[j]);
					}
					if(request.getParameter(userIds[j]+"-c24")!=null){
						String[] wagesValue = request.getParameterValues(request.getParameter(userIds[j]+"-c24")+ranks[i]);
						salary.setV24(wagesValue[j]);
					}
					if(request.getParameter(userIds[j]+"-c25")!=null){
						String[] wagesValue = request.getParameterValues(request.getParameter(userIds[j]+"-c25")+ranks[i]);
						salary.setV25(wagesValue[j]);
					}
					if(request.getParameter(userIds[j]+"-c26")!=null){
						String[] wagesValue = request.getParameterValues(request.getParameter(userIds[j]+"-c26")+ranks[i]);
						salary.setV26(wagesValue[j]);
					}
					if(request.getParameter(userIds[j]+"-c27")!=null){
						String[] wagesValue = request.getParameterValues(request.getParameter(userIds[j]+"-c27")+ranks[i]);
						salary.setV27(wagesValue[j]);
					}
					if(request.getParameter(userIds[j]+"-c28")!=null){
						String[] wagesValue = request.getParameterValues(request.getParameter(userIds[j]+"-c28")+ranks[i]);
						salary.setV28(wagesValue[j]);
					}
					if(request.getParameter(userIds[j]+"-c29")!=null){
						String[] wagesValue = request.getParameterValues(request.getParameter(userIds[j]+"-c29")+ranks[i]);
						salary.setV29(wagesValue[j]);
					}
					if(request.getParameter(userIds[j]+"-c30")!=null){
						String[] wagesValue = request.getParameterValues(request.getParameter(userIds[j]+"-c30")+ranks[i]);
						salary.setV30(wagesValue[j]);
					}
					list.add(salary);
				}
			 }	
				
			}
			conn = new DBConnect().getConnect("");
			SalaryService salaryService = new SalaryService(conn);
			boolean result = salaryService.updateSalary(list, YY, MM, departmentid);
			if (result) {
				response.sendRedirect(request.getContextPath()+"/salary.do?method=businessList");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		
		return null;
	}
	
	/**
	 * 确认审批方法
	 * @param request
	 * @param response
	 */
	public ModelAndView updateAudit(HttpServletRequest request, HttpServletResponse response){
		
		UserSession userSession = (UserSession) request.getSession().getAttribute("userSession"); 
		//String departmentid = userSession.getUserAuditDepartmentId();   //得到当前部门
		Connection conn = null;
		ASFuntion asf = new ASFuntion();
		String pch = UUID.randomUUID().toString();
		String YY = request.getParameter("Nian");
		String MM = request.getParameter("Yue");
		String departmentid  = asf.showNull(request.getParameter("departmentId"));
		String flag = asf.showNull(request.getParameter("flag"));
		if(departmentid.equals("")){
			departmentid = userSession.getUserAuditDepartmentId();
		}
		String[] ranks = request.getParameterValues("rank");
		List<Salary> list = new ArrayList<Salary>();
		ASFuntion ASF = new ASFuntion();
		try {
			conn = new DBConnect().getConnect("");	
			for(int i =0;i<ranks.length;i++){
				String[] userIds = request.getParameterValues("userId"+ranks[i]);
				for(int j=0;j<userIds.length;j++){
					Salary salary = new Salary();
			    	salary.setUserId(userIds[j]);
			    	salary.setRankId(ranks[i]);
			    	salary.setCountValue((request.getParameter(userIds[j]+"-countValue")==null)?"0":(request.getParameter(userIds[j]+"-countValue")));
			    	if(flag.equals("1")){
			    		salary.setStatus("人事暂存");
			    	}else if(flag.equals("2")){
			    		salary.setStatus("暂存");
			    	}else{
			    		salary.setStatus("等待发放");
			    	}
			    	//salary.setStatus("人事部门确认");
			    	
					salary.setNowYear(YY);
					salary.setNowMonth(MM);
					salary.setDepartmentId(departmentid);
					salary.setPch(pch);
					salary.setN1(request.getParameter(userIds[j]+"-n1"));
					salary.setN2(request.getParameter(userIds[j]+"-n2"));
					salary.setN3(request.getParameter(userIds[j]+"-n3"));
					salary.setN4(request.getParameter(userIds[j]+"-n4"));
					salary.setN5(request.getParameter(userIds[j]+"-n5"));
					
					salary.setN6(request.getParameter(userIds[j]+"-n6"));
					salary.setN7(request.getParameter(userIds[j]+"-n7"));
					salary.setN8(request.getParameter(userIds[j]+"-n8"));
					salary.setN9(request.getParameter(userIds[j]+"-n9"));
					salary.setN10(request.getParameter(userIds[j]+"-n10"));
					
					salary.setN11(request.getParameter(userIds[j]+"-n11"));
					salary.setN12(request.getParameter(userIds[j]+"-n12"));
					salary.setN13(request.getParameter(userIds[j]+"-n13"));
					salary.setN14(request.getParameter(userIds[j]+"-n14"));
					salary.setN15(request.getParameter(userIds[j]+"-n15"));
					
					salary.setN16(request.getParameter(userIds[j]+"-n16"));
					salary.setN17(request.getParameter(userIds[j]+"-n17"));
					salary.setN18(request.getParameter(userIds[j]+"-n18"));
					salary.setN19(request.getParameter(userIds[j]+"-n19"));
					salary.setN20(request.getParameter(userIds[j]+"-n20"));
					
					salary.setN21(request.getParameter(userIds[j]+"-n21"));
					salary.setN22(request.getParameter(userIds[j]+"-n22"));
					salary.setN23(request.getParameter(userIds[j]+"-n23"));
					salary.setN24(request.getParameter(userIds[j]+"-n24"));
					salary.setN25(request.getParameter(userIds[j]+"-n25"));
					
					salary.setN26(request.getParameter(userIds[j]+"-n26"));
					salary.setN27(request.getParameter(userIds[j]+"-n27"));
					salary.setN28(request.getParameter(userIds[j]+"-n28"));
					salary.setN29(request.getParameter(userIds[j]+"-n29"));
					salary.setN30(request.getParameter(userIds[j]+"-n30"));
					
					salary.setC1(request.getParameter(userIds[j]+"-c1"));
					salary.setC2(request.getParameter(userIds[j]+"-c2"));
					salary.setC3(request.getParameter(userIds[j]+"-c3"));
					salary.setC4(request.getParameter(userIds[j]+"-c4"));
					salary.setC5(request.getParameter(userIds[j]+"-c5"));
					
					salary.setC6(request.getParameter(userIds[j]+"-c6"));
					salary.setC7(request.getParameter(userIds[j]+"-c7"));
					salary.setC8(request.getParameter(userIds[j]+"-c8"));
					salary.setC9(request.getParameter(userIds[j]+"-c9"));
					salary.setC10(request.getParameter(userIds[j]+"-c10"));
					
					salary.setC11(request.getParameter(userIds[j]+"-c11"));
					salary.setC12(request.getParameter(userIds[j]+"-c12"));
					salary.setC13(request.getParameter(userIds[j]+"-c13"));
					salary.setC14(request.getParameter(userIds[j]+"-c14"));
					salary.setC15(request.getParameter(userIds[j]+"-c15"));
					
					salary.setC16(request.getParameter(userIds[j]+"-c16"));
					salary.setC17(request.getParameter(userIds[j]+"-c17"));
					salary.setC18(request.getParameter(userIds[j]+"-c18"));
					salary.setC19(request.getParameter(userIds[j]+"-c19"));
					salary.setC20(request.getParameter(userIds[j]+"-c20"));
					
					salary.setC21(request.getParameter(userIds[j]+"-c21"));
					salary.setC22(request.getParameter(userIds[j]+"-c22"));
					salary.setC23(request.getParameter(userIds[j]+"-c23"));
					salary.setC24(request.getParameter(userIds[j]+"-c24"));
					salary.setC25(request.getParameter(userIds[j]+"-c25"));
					
					salary.setC26(request.getParameter(userIds[j]+"-c26"));
					salary.setC27(request.getParameter(userIds[j]+"-c27"));
					salary.setC28(request.getParameter(userIds[j]+"-c28"));
					salary.setC29(request.getParameter(userIds[j]+"-c29"));
					salary.setC30(request.getParameter(userIds[j]+"-c30"));
					
					if(request.getParameter(userIds[j]+"-c1")!=null){
						String[] wagesValue = request.getParameterValues(request.getParameter(userIds[j]+"-c1")+ranks[i]);
						salary.setV1(wagesValue[j]);
					}
					if(request.getParameter(userIds[j]+"-c2")!=null){
						String[] wagesValue = request.getParameterValues(request.getParameter(userIds[j]+"-c2")+ranks[i]);
						salary.setV2(wagesValue[j]);
					}
					if(request.getParameter(userIds[j]+"-c3")!=null){
						String[] wagesValue = request.getParameterValues(request.getParameter(userIds[j]+"-c3")+ranks[i]);
						salary.setV3(wagesValue[j]);
					}
					if(request.getParameter(userIds[j]+"-c4")!=null){
						String[] wagesValue = request.getParameterValues(request.getParameter(userIds[j]+"-c4")+ranks[i]);
						salary.setV4(wagesValue[j]);
					}
					if(request.getParameter(userIds[j]+"-c5")!=null){
						String[] wagesValue = request.getParameterValues(request.getParameter(userIds[j]+"-c5")+ranks[i]);
						salary.setV5(wagesValue[j]);
					}
					if(request.getParameter(userIds[j]+"-c6")!=null){
						String[] wagesValue = request.getParameterValues(request.getParameter(userIds[j]+"-c6")+ranks[i]);
						salary.setV6(wagesValue[j]);
					}
					if(request.getParameter(userIds[j]+"-c7")!=null){
						String[] wagesValue = request.getParameterValues(request.getParameter(userIds[j]+"-c7")+ranks[i]);
						salary.setV7(wagesValue[j]);
					}
					if(request.getParameter(userIds[j]+"-c8")!=null){
						String[] wagesValue = request.getParameterValues(request.getParameter(userIds[j]+"-c8")+ranks[i]);
						salary.setV8(wagesValue[j]);
					}
					if(request.getParameter(userIds[j]+"-c9")!=null){
						String[] wagesValue = request.getParameterValues(request.getParameter(userIds[j]+"-c9")+ranks[i]);
						salary.setV9(wagesValue[j]);
					}
					if(request.getParameter(userIds[j]+"-c10")!=null){
						String[] wagesValue = request.getParameterValues(request.getParameter(userIds[j]+"-c10")+ranks[i]);
						salary.setV10(wagesValue[j]);
					}
					if(request.getParameter(userIds[j]+"-c11")!=null){
						String[] wagesValue = request.getParameterValues(request.getParameter(userIds[j]+"-c11")+ranks[i]);
						salary.setV11(wagesValue[j]);
					}
					if(request.getParameter(userIds[j]+"-c12")!=null){
						String[] wagesValue = request.getParameterValues(request.getParameter(userIds[j]+"-c12")+ranks[i]);
						salary.setV12(wagesValue[j]);
					}
					if(request.getParameter(userIds[j]+"-c13")!=null){
						String[] wagesValue = request.getParameterValues(request.getParameter(userIds[j]+"-c13")+ranks[i]);
						salary.setV13(wagesValue[j]);
					}
					if(request.getParameter(userIds[j]+"-c14")!=null){
						String[] wagesValue = request.getParameterValues(request.getParameter(userIds[j]+"-c14")+ranks[i]);
						salary.setV14(wagesValue[j]);
					}
					if(request.getParameter(userIds[j]+"-c15")!=null){
						String[] wagesValue = request.getParameterValues(request.getParameter(userIds[j]+"-c15")+ranks[i]);
						salary.setV15(wagesValue[j]);
					}
					if(request.getParameter(userIds[j]+"-c16")!=null){
						String[] wagesValue = request.getParameterValues(request.getParameter(userIds[j]+"-c16")+ranks[i]);
						salary.setV16(wagesValue[j]);
					}
					if(request.getParameter(userIds[j]+"-c17")!=null){
						String[] wagesValue = request.getParameterValues(request.getParameter(userIds[j]+"-c17")+ranks[i]);
						salary.setV17(wagesValue[j]);
					}
					if(request.getParameter(userIds[j]+"-c18")!=null){
						String[] wagesValue = request.getParameterValues(request.getParameter(userIds[j]+"-c18")+ranks[i]);
						salary.setV18(wagesValue[j]);
					}
					if(request.getParameter(userIds[j]+"-c19")!=null){
						String[] wagesValue = request.getParameterValues(request.getParameter(userIds[j]+"-c19")+ranks[i]);
						salary.setV19(wagesValue[j]);
					}
					if(request.getParameter(userIds[j]+"-c20")!=null){
						String[] wagesValue = request.getParameterValues(request.getParameter(userIds[j]+"-c20")+ranks[i]);
						salary.setV20(wagesValue[j]);
					}
					if(request.getParameter(userIds[j]+"-c21")!=null){
						String[] wagesValue = request.getParameterValues(request.getParameter(userIds[j]+"-c21")+ranks[i]);
						salary.setV21(wagesValue[j]);
					}
					if(request.getParameter(userIds[j]+"-c22")!=null){
						String[] wagesValue = request.getParameterValues(request.getParameter(userIds[j]+"-c22")+ranks[i]);
						salary.setV22(wagesValue[j]);
					}
					if(request.getParameter(userIds[j]+"-c23")!=null){
						String[] wagesValue = request.getParameterValues(request.getParameter(userIds[j]+"-c23")+ranks[i]);
						salary.setV23(wagesValue[j]);
					}
					if(request.getParameter(userIds[j]+"-c24")!=null){
						String[] wagesValue = request.getParameterValues(request.getParameter(userIds[j]+"-c24")+ranks[i]);
						salary.setV24(wagesValue[j]);
					}
					if(request.getParameter(userIds[j]+"-c25")!=null){
						String[] wagesValue = request.getParameterValues(request.getParameter(userIds[j]+"-c25")+ranks[i]);
						salary.setV25(wagesValue[j]);
					}
					if(request.getParameter(userIds[j]+"-c26")!=null){
						String[] wagesValue = request.getParameterValues(request.getParameter(userIds[j]+"-c26")+ranks[i]);
						salary.setV26(wagesValue[j]);
					}
					if(request.getParameter(userIds[j]+"-c27")!=null){
						String[] wagesValue = request.getParameterValues(request.getParameter(userIds[j]+"-c27")+ranks[i]);
						salary.setV27(wagesValue[j]);
					}
					if(request.getParameter(userIds[j]+"-c28")!=null){
						String[] wagesValue = request.getParameterValues(request.getParameter(userIds[j]+"-c28")+ranks[i]);
						salary.setV28(wagesValue[j]);
					}
					if(request.getParameter(userIds[j]+"-c29")!=null){
						String[] wagesValue = request.getParameterValues(request.getParameter(userIds[j]+"-c29")+ranks[i]);
						salary.setV29(wagesValue[j]);
					}
					if(request.getParameter(userIds[j]+"-c30")!=null){
						String[] wagesValue = request.getParameterValues(request.getParameter(userIds[j]+"-c30")+ranks[i]);
						salary.setV30(wagesValue[j]);
					}
					list.add(salary);
				}
			}
			
			SalaryService salaryService = new SalaryService(conn);
			boolean result = salaryService.updateAudit(list, YY, MM, departmentid);
			if (result) {
				response.sendRedirect(request.getContextPath()+"/salary.do?method=personnelList");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		
		return null;
	}
	
	/**
	 * 确认发放方法
	 * @param request
	 * @param response
	 */
	public ModelAndView updateGive(HttpServletRequest request, HttpServletResponse response){
		
		UserSession userSession = (UserSession) request.getSession().getAttribute("userSession"); 
		String departmentid = userSession.getUserAuditDepartmentId();   //得到当前部门
		Connection conn = null;
		ASFuntion asf = new ASFuntion();
		String pch = UUID.randomUUID().toString();
		String YY = request.getParameter("Nian");
		String MM = request.getParameter("Yue");
		String flag = asf.showNull(request.getParameter("flag"));
		List<Salary> list = new ArrayList<Salary>();
		
		String[] ranks = request.getParameterValues("rank");
		for(int i =0;i<ranks.length;i++){
			String[] userIds = request.getParameterValues("userId"+ranks[i]);
			for(int j=0;j<userIds.length;j++){
				Salary salary = new Salary();
		    	salary.setUserId(userIds[j]);
		    	salary.setRankId(ranks[i]);
		    	salary.setCountValue((request.getParameter(userIds[j]+"-countValue")==null)?"0":(request.getParameter(userIds[j]+"-countValue")));
		    	if(flag.equals("2")){
		    		salary.setStatus("等待人事处理");
		    	}else{
		    		salary.setStatus("已发放");
		    	}
				salary.setNowYear(YY);
				salary.setNowMonth(MM);
				salary.setDepartmentId(departmentid);
				salary.setPch(pch);
				salary.setN1(request.getParameter(userIds[j]+"-n1"));
				salary.setN2(request.getParameter(userIds[j]+"-n2"));
				salary.setN3(request.getParameter(userIds[j]+"-n3"));
				salary.setN4(request.getParameter(userIds[j]+"-n4"));
				salary.setN5(request.getParameter(userIds[j]+"-n5"));
				
				salary.setN6(request.getParameter(userIds[j]+"-n6"));
				salary.setN7(request.getParameter(userIds[j]+"-n7"));
				salary.setN8(request.getParameter(userIds[j]+"-n8"));
				salary.setN9(request.getParameter(userIds[j]+"-n9"));
				salary.setN10(request.getParameter(userIds[j]+"-n10"));
				
				salary.setN11(request.getParameter(userIds[j]+"-n11"));
				salary.setN12(request.getParameter(userIds[j]+"-n12"));
				salary.setN13(request.getParameter(userIds[j]+"-n13"));
				salary.setN14(request.getParameter(userIds[j]+"-n14"));
				salary.setN15(request.getParameter(userIds[j]+"-n15"));
				
				salary.setN16(request.getParameter(userIds[j]+"-n16"));
				salary.setN17(request.getParameter(userIds[j]+"-n17"));
				salary.setN18(request.getParameter(userIds[j]+"-n18"));
				salary.setN19(request.getParameter(userIds[j]+"-n19"));
				salary.setN20(request.getParameter(userIds[j]+"-n20"));
				
				salary.setN21(request.getParameter(userIds[j]+"-n21"));
				salary.setN22(request.getParameter(userIds[j]+"-n22"));
				salary.setN23(request.getParameter(userIds[j]+"-n23"));
				salary.setN24(request.getParameter(userIds[j]+"-n24"));
				salary.setN25(request.getParameter(userIds[j]+"-n25"));
				
				salary.setN26(request.getParameter(userIds[j]+"-n26"));
				salary.setN27(request.getParameter(userIds[j]+"-n27"));
				salary.setN28(request.getParameter(userIds[j]+"-n28"));
				salary.setN29(request.getParameter(userIds[j]+"-n29"));
				salary.setN30(request.getParameter(userIds[j]+"-n30"));
				
				salary.setC1(request.getParameter(userIds[j]+"-c1"));
				salary.setC2(request.getParameter(userIds[j]+"-c2"));
				salary.setC3(request.getParameter(userIds[j]+"-c3"));
				salary.setC4(request.getParameter(userIds[j]+"-c4"));
				salary.setC5(request.getParameter(userIds[j]+"-c5"));
				
				salary.setC6(request.getParameter(userIds[j]+"-c6"));
				salary.setC7(request.getParameter(userIds[j]+"-c7"));
				salary.setC8(request.getParameter(userIds[j]+"-c8"));
				salary.setC9(request.getParameter(userIds[j]+"-c9"));
				salary.setC10(request.getParameter(userIds[j]+"-c10"));
				
				salary.setC11(request.getParameter(userIds[j]+"-c11"));
				salary.setC12(request.getParameter(userIds[j]+"-c12"));
				salary.setC13(request.getParameter(userIds[j]+"-c13"));
				salary.setC14(request.getParameter(userIds[j]+"-c14"));
				salary.setC15(request.getParameter(userIds[j]+"-c15"));
				
				salary.setC16(request.getParameter(userIds[j]+"-c16"));
				salary.setC17(request.getParameter(userIds[j]+"-c17"));
				salary.setC18(request.getParameter(userIds[j]+"-c18"));
				salary.setC19(request.getParameter(userIds[j]+"-c19"));
				salary.setC20(request.getParameter(userIds[j]+"-c20"));
				
				salary.setC21(request.getParameter(userIds[j]+"-c21"));
				salary.setC22(request.getParameter(userIds[j]+"-c22"));
				salary.setC23(request.getParameter(userIds[j]+"-c23"));
				salary.setC24(request.getParameter(userIds[j]+"-c24"));
				salary.setC25(request.getParameter(userIds[j]+"-c25"));
				
				salary.setC26(request.getParameter(userIds[j]+"-c26"));
				salary.setC27(request.getParameter(userIds[j]+"-c27"));
				salary.setC28(request.getParameter(userIds[j]+"-c28"));
				salary.setC29(request.getParameter(userIds[j]+"-c29"));
				salary.setC30(request.getParameter(userIds[j]+"-c30"));
				
				if(request.getParameter(userIds[j]+"-c1")!=null){
					String[] wagesValue = request.getParameterValues(request.getParameter(userIds[j]+"-c1")+ranks[i]);
					salary.setV1(wagesValue[j]);
				}
				if(request.getParameter(userIds[j]+"-c2")!=null){
					String[] wagesValue = request.getParameterValues(request.getParameter(userIds[j]+"-c2")+ranks[i]);
					salary.setV2(wagesValue[j]);
				}
				if(request.getParameter(userIds[j]+"-c3")!=null){
					String[] wagesValue = request.getParameterValues(request.getParameter(userIds[j]+"-c3")+ranks[i]);
					salary.setV3(wagesValue[j]);
				}
				if(request.getParameter(userIds[j]+"-c4")!=null){
					String[] wagesValue = request.getParameterValues(request.getParameter(userIds[j]+"-c4")+ranks[i]);
					salary.setV4(wagesValue[j]);
				}
				if(request.getParameter(userIds[j]+"-c5")!=null){
					String[] wagesValue = request.getParameterValues(request.getParameter(userIds[j]+"-c5")+ranks[i]);
					salary.setV5(wagesValue[j]);
				}
				if(request.getParameter(userIds[j]+"-c6")!=null){
					String[] wagesValue = request.getParameterValues(request.getParameter(userIds[j]+"-c6")+ranks[i]);
					salary.setV6(wagesValue[j]);
				}
				if(request.getParameter(userIds[j]+"-c7")!=null){
					String[] wagesValue = request.getParameterValues(request.getParameter(userIds[j]+"-c7")+ranks[i]);
					salary.setV7(wagesValue[j]);
				}
				if(request.getParameter(userIds[j]+"-c8")!=null){
					String[] wagesValue = request.getParameterValues(request.getParameter(userIds[j]+"-c8")+ranks[i]);
					salary.setV8(wagesValue[j]);
				}
				if(request.getParameter(userIds[j]+"-c9")!=null){
					String[] wagesValue = request.getParameterValues(request.getParameter(userIds[j]+"-c9")+ranks[i]);
					salary.setV9(wagesValue[j]);
				}
				if(request.getParameter(userIds[j]+"-c10")!=null){
					String[] wagesValue = request.getParameterValues(request.getParameter(userIds[j]+"-c10")+ranks[i]);
					salary.setV10(wagesValue[j]);
				}
				if(request.getParameter(userIds[j]+"-c11")!=null){
					String[] wagesValue = request.getParameterValues(request.getParameter(userIds[j]+"-c11")+ranks[i]);
					salary.setV11(wagesValue[j]);
				}
				if(request.getParameter(userIds[j]+"-c12")!=null){
					String[] wagesValue = request.getParameterValues(request.getParameter(userIds[j]+"-c12")+ranks[i]);
					salary.setV12(wagesValue[j]);
				}
				if(request.getParameter(userIds[j]+"-c13")!=null){
					String[] wagesValue = request.getParameterValues(request.getParameter(userIds[j]+"-c13")+ranks[i]);
					salary.setV13(wagesValue[j]);
				}
				if(request.getParameter(userIds[j]+"-c14")!=null){
					String[] wagesValue = request.getParameterValues(request.getParameter(userIds[j]+"-c14")+ranks[i]);
					salary.setV14(wagesValue[j]);
				}
				if(request.getParameter(userIds[j]+"-c15")!=null){
					String[] wagesValue = request.getParameterValues(request.getParameter(userIds[j]+"-c15")+ranks[i]);
					salary.setV15(wagesValue[j]);
				}
				if(request.getParameter(userIds[j]+"-c16")!=null){
					String[] wagesValue = request.getParameterValues(request.getParameter(userIds[j]+"-c16")+ranks[i]);
					salary.setV16(wagesValue[j]);
				}
				if(request.getParameter(userIds[j]+"-c17")!=null){
					String[] wagesValue = request.getParameterValues(request.getParameter(userIds[j]+"-c17")+ranks[i]);
					salary.setV17(wagesValue[j]);
				}
				if(request.getParameter(userIds[j]+"-c18")!=null){
					String[] wagesValue = request.getParameterValues(request.getParameter(userIds[j]+"-c18")+ranks[i]);
					salary.setV18(wagesValue[j]);
				}
				if(request.getParameter(userIds[j]+"-c19")!=null){
					String[] wagesValue = request.getParameterValues(request.getParameter(userIds[j]+"-c19")+ranks[i]);
					salary.setV19(wagesValue[j]);
				}
				if(request.getParameter(userIds[j]+"-c20")!=null){
					String[] wagesValue = request.getParameterValues(request.getParameter(userIds[j]+"-c20")+ranks[i]);
					salary.setV20(wagesValue[j]);
				}
				if(request.getParameter(userIds[j]+"-c21")!=null){
					String[] wagesValue = request.getParameterValues(request.getParameter(userIds[j]+"-c21")+ranks[i]);
					salary.setV21(wagesValue[j]);
				}
				if(request.getParameter(userIds[j]+"-c22")!=null){
					String[] wagesValue = request.getParameterValues(request.getParameter(userIds[j]+"-c22")+ranks[i]);
					salary.setV22(wagesValue[j]);
				}
				if(request.getParameter(userIds[j]+"-c23")!=null){
					String[] wagesValue = request.getParameterValues(request.getParameter(userIds[j]+"-c23")+ranks[i]);
					salary.setV23(wagesValue[j]);
				}
				if(request.getParameter(userIds[j]+"-c24")!=null){
					String[] wagesValue = request.getParameterValues(request.getParameter(userIds[j]+"-c24")+ranks[i]);
					salary.setV24(wagesValue[j]);
				}
				if(request.getParameter(userIds[j]+"-c25")!=null){
					String[] wagesValue = request.getParameterValues(request.getParameter(userIds[j]+"-c25")+ranks[i]);
					salary.setV25(wagesValue[j]);
				}
				if(request.getParameter(userIds[j]+"-c26")!=null){
					String[] wagesValue = request.getParameterValues(request.getParameter(userIds[j]+"-c26")+ranks[i]);
					salary.setV26(wagesValue[j]);
				}
				if(request.getParameter(userIds[j]+"-c27")!=null){
					String[] wagesValue = request.getParameterValues(request.getParameter(userIds[j]+"-c27")+ranks[i]);
					salary.setV27(wagesValue[j]);
				}
				if(request.getParameter(userIds[j]+"-c28")!=null){
					String[] wagesValue = request.getParameterValues(request.getParameter(userIds[j]+"-c28")+ranks[i]);
					salary.setV28(wagesValue[j]);
				}
				if(request.getParameter(userIds[j]+"-c29")!=null){
					String[] wagesValue = request.getParameterValues(request.getParameter(userIds[j]+"-c29")+ranks[i]);
					salary.setV29(wagesValue[j]);
				}
				if(request.getParameter(userIds[j]+"-c30")!=null){
					String[] wagesValue = request.getParameterValues(request.getParameter(userIds[j]+"-c30")+ranks[i]);
					salary.setV30(wagesValue[j]);
				}
				list.add(salary);
			}
		}
		
//		for (int i = 0; i <aryUserIds.length; i++) {
//			Salary salary = new Salary();
//			salary.setUserId(aryUserIds[i]);
//			salary.setRankId((request.getParameter(aryUserIds[i]+"-rankId")==null)?"":(request.getParameter(aryUserIds[i]+"-rankId")));
//			salary.setCountValue((request.getParameter(aryUserIds[i]+"-countValue")==null)?"0":(request.getParameter(aryUserIds[i]+"-countValue")));
//			
//			salary.setStatus("已发放");
//			salary.setNowYear(YY);
//			salary.setNowMonth(MM);
//			salary.setDepartmentId(departmentid);
//			salary.setPch(pch);
//			
//			salary.setN1(request.getParameter(aryUserIds[i]+"-n1"));
//			salary.setN2(request.getParameter(aryUserIds[i]+"-n2"));
//			salary.setN3(request.getParameter(aryUserIds[i]+"-n3"));
//			salary.setN4(request.getParameter(aryUserIds[i]+"-n4"));
//			salary.setN5(request.getParameter(aryUserIds[i]+"-n5"));
//			
//			salary.setN6(request.getParameter(aryUserIds[i]+"-n6"));
//			salary.setN7(request.getParameter(aryUserIds[i]+"-n7"));
//			salary.setN8(request.getParameter(aryUserIds[i]+"-n8"));
//			salary.setN9(request.getParameter(aryUserIds[i]+"-n9"));
//			salary.setN10(request.getParameter(aryUserIds[i]+"-n10"));
//			
//			salary.setN11(request.getParameter(aryUserIds[i]+"-n11"));
//			salary.setN12(request.getParameter(aryUserIds[i]+"-n12"));
//			salary.setN13(request.getParameter(aryUserIds[i]+"-n13"));
//			salary.setN14(request.getParameter(aryUserIds[i]+"-n14"));
//			salary.setN15(request.getParameter(aryUserIds[i]+"-n15"));
//			
//			salary.setN16(request.getParameter(aryUserIds[i]+"-n16"));
//			salary.setN17(request.getParameter(aryUserIds[i]+"-n17"));
//			salary.setN18(request.getParameter(aryUserIds[i]+"-n18"));
//			salary.setN19(request.getParameter(aryUserIds[i]+"-n19"));
//			salary.setN20(request.getParameter(aryUserIds[i]+"-n20"));
//			
//			salary.setN21(request.getParameter(aryUserIds[i]+"-n21"));
//			salary.setN22(request.getParameter(aryUserIds[i]+"-n22"));
//			salary.setN23(request.getParameter(aryUserIds[i]+"-n23"));
//			salary.setN24(request.getParameter(aryUserIds[i]+"-n24"));
//			salary.setN25(request.getParameter(aryUserIds[i]+"-n25"));
//			
//			salary.setN26(request.getParameter(aryUserIds[i]+"-n26"));
//			salary.setN27(request.getParameter(aryUserIds[i]+"-n27"));
//			salary.setN28(request.getParameter(aryUserIds[i]+"-n28"));
//			salary.setN29(request.getParameter(aryUserIds[i]+"-n29"));
//			salary.setN30(request.getParameter(aryUserIds[i]+"-n30"));
//			
//			salary.setV1(request.getParameter(aryUserIds[i]+"-v1"));
//			salary.setV2(request.getParameter(aryUserIds[i]+"-v2"));
//			salary.setV3(request.getParameter(aryUserIds[i]+"-v3"));
//			salary.setV4(request.getParameter(aryUserIds[i]+"-v4"));
//			salary.setV5(request.getParameter(aryUserIds[i]+"-v5"));
//			
//			salary.setV6(request.getParameter(aryUserIds[i]+"-v6"));
//			salary.setV7(request.getParameter(aryUserIds[i]+"-v7"));
//			salary.setV8(request.getParameter(aryUserIds[i]+"-v8"));
//			salary.setV9(request.getParameter(aryUserIds[i]+"-v9"));
//			salary.setV10(request.getParameter(aryUserIds[i]+"-v10"));
//			
//			salary.setV11(request.getParameter(aryUserIds[i]+"-v11"));
//			salary.setV12(request.getParameter(aryUserIds[i]+"-v12"));
//			salary.setV13(request.getParameter(aryUserIds[i]+"-v13"));
//			salary.setV14(request.getParameter(aryUserIds[i]+"-v14"));
//			salary.setV15(request.getParameter(aryUserIds[i]+"-v15"));
//			
//			salary.setV16(request.getParameter(aryUserIds[i]+"-v16"));
//			salary.setV17(request.getParameter(aryUserIds[i]+"-v17"));
//			salary.setV18(request.getParameter(aryUserIds[i]+"-v18"));
//			salary.setV19(request.getParameter(aryUserIds[i]+"-v19"));
//			salary.setV20(request.getParameter(aryUserIds[i]+"-v20"));
//			
//			salary.setV21(request.getParameter(aryUserIds[i]+"-v21"));
//			salary.setV22(request.getParameter(aryUserIds[i]+"-v22"));
//			salary.setV23(request.getParameter(aryUserIds[i]+"-v23"));
//			salary.setV24(request.getParameter(aryUserIds[i]+"-v24"));
//			salary.setV25(request.getParameter(aryUserIds[i]+"-v25"));
//			
//			salary.setV26(request.getParameter(aryUserIds[i]+"-v26"));
//			salary.setV27(request.getParameter(aryUserIds[i]+"-v27"));
//			salary.setV28(request.getParameter(aryUserIds[i]+"-v28"));
//			salary.setV29(request.getParameter(aryUserIds[i]+"-v29"));
//			salary.setV30(request.getParameter(aryUserIds[i]+"-v30"));
//			
//			salary.setC1(request.getParameter(aryUserIds[i]+"-c1"));
//			salary.setC2(request.getParameter(aryUserIds[i]+"-c2"));
//			salary.setC3(request.getParameter(aryUserIds[i]+"-c3"));
//			salary.setC4(request.getParameter(aryUserIds[i]+"-c4"));
//			salary.setC5(request.getParameter(aryUserIds[i]+"-c5"));
//			
//			salary.setC6(request.getParameter(aryUserIds[i]+"-c6"));
//			salary.setC7(request.getParameter(aryUserIds[i]+"-c7"));
//			salary.setC8(request.getParameter(aryUserIds[i]+"-c8"));
//			salary.setC9(request.getParameter(aryUserIds[i]+"-c9"));
//			salary.setC10(request.getParameter(aryUserIds[i]+"-c10"));
//			
//			salary.setC11(request.getParameter(aryUserIds[i]+"-c11"));
//			salary.setC12(request.getParameter(aryUserIds[i]+"-c12"));
//			salary.setC13(request.getParameter(aryUserIds[i]+"-c13"));
//			salary.setC14(request.getParameter(aryUserIds[i]+"-c14"));
//			salary.setC15(request.getParameter(aryUserIds[i]+"-c15"));
//			
//			salary.setC16(request.getParameter(aryUserIds[i]+"-c16"));
//			salary.setC17(request.getParameter(aryUserIds[i]+"-c17"));
//			salary.setC18(request.getParameter(aryUserIds[i]+"-c18"));
//			salary.setC19(request.getParameter(aryUserIds[i]+"-c19"));
//			salary.setC20(request.getParameter(aryUserIds[i]+"-c20"));
//			
//			salary.setC21(request.getParameter(aryUserIds[i]+"-c21"));
//			salary.setC22(request.getParameter(aryUserIds[i]+"-c22"));
//			salary.setC23(request.getParameter(aryUserIds[i]+"-c23"));
//			salary.setC24(request.getParameter(aryUserIds[i]+"-c24"));
//			salary.setC25(request.getParameter(aryUserIds[i]+"-c25"));
//			
//			salary.setC26(request.getParameter(aryUserIds[i]+"-c26"));
//			salary.setC27(request.getParameter(aryUserIds[i]+"-c27"));
//			salary.setC28(request.getParameter(aryUserIds[i]+"-c28"));
//			salary.setC29(request.getParameter(aryUserIds[i]+"-c29"));
//			salary.setC30(request.getParameter(aryUserIds[i]+"-c30"));
//			
//			list.add(salary);
//		}
		
		try {
			conn = new DBConnect().getConnect("");
			SalaryService salaryService = new SalaryService(conn);
			boolean result = salaryService.updateGive(list, YY, MM, departmentid);
			if (result) {
				response.sendRedirect(request.getContextPath()+"/salary.do?method=list");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		
		return null;
	}
	
	/**
	 * 数据库计算
	 * @param request
	 * @param response
	 * @throws IOException 
	 */
	public void excuteSql(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// 获取登录的用户
		Connection conn = null;
		
		ASFuntion asf = new ASFuntion();
		String autoId = asf.showNull(request.getParameter("id"));
		String line = asf.showNull(request.getParameter("line"));
		String lineValue = asf.showNull(request.getParameter("lineValue"));
		response.setCharacterEncoding("utf-8");
		PrintWriter out = response.getWriter();
		try {
				conn = new DBConnect().getConnect("");

				String sql = " update k_salary set "+line+" = '"+lineValue+"' where autoid="+autoId+"";
					
				//添加验证码表
				out.write(new DbUtil(conn).executeUpdate(sql)+"");
				
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
	}
    public void createExcel(HttpServletRequest request, HttpServletResponse response) throws FileNotFoundException, IOException,Exception{
    	//POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(request.getContextPath()+"/salary/test.xls"));
        String address = request.getRealPath("");

    	FileOutputStream fos = new FileOutputStream(address+"/salary/initSalary.xls");
    	HSSFWorkbook workbook = new HSSFWorkbook();
    	HSSFSheet sheet = workbook.createSheet();
    	workbook.setSheetName(0, "工资表");
    	HSSFRow row = sheet.createRow((short)0);
    	row.setHeight((short)400);
        HSSFCell cell1 = row.createCell(0);
        //cell1.setEncoding(HSSFCell.ENCODING_UTF_16);
        cell1.setCellValue("部门");
        
        HSSFCell cell2 = row.createCell(1);
        //cell2.setEncoding(HSSFCell.ENCODING_UTF_16);
        cell2.setCellValue("姓名");
       
        HSSFCell cell3 = row.createCell(2);
        //cell3.setEncoding(HSSFCell.ENCODING_UTF_16);
        cell3.setCellValue("职务级别");
        
        HSSFCell cell4 = row.createCell(3);
        //cell4.setEncoding(HSSFCell.ENCODING_UTF_16);
        cell4.setCellValue("年份");
        
        
        HSSFCell cell5 = row.createCell(4);
        //cell5.setEncoding(HSSFCell.ENCODING_UTF_16);
        cell5.setCellValue("月份");
  
    	Connection conn = new DBConnect().getConnect("");
    	SalaryService salaryService = new SalaryService(conn);
    	List<String> list = salaryService.getExcelValue();
    	int j = 5;
    	for(int i = 0;i<list.size();i++){
    		HSSFCell cell = row.createCell(j);
    		//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
    		cell.setCellValue(list.get(i));
    		j++;
    	}
        workbook.write(fos);
        fos.close();
    	DbUtil.close(conn);
    	//打包成zip文件
    	FileOutputStream fos2 = null;
        ZipOutputStream zos = null;
        try {
        	   new File(address+"/salary/salary_template.zip").delete();
               fos = new FileOutputStream(address+"/salary/salary_template.zip");
               zos = new ZipOutputStream(fos);
               this.writeZip(new File(address+"/salary/initSalary.xls"), "", zos);
        } catch (FileNotFoundException e) {
               e.printStackTrace();
        } finally {
               try {
                   if (zos != null) {
                       zos.close();
                   }
               } catch (IOException e) {
                   e.printStackTrace();
               }

           }
         new File(address+"/salary/initSalary.xls").delete(); 
    	
    }
    private void writeZip(File file, String parentPath, ZipOutputStream zos) {
        if(file.exists()){
            if(file.isDirectory()){//处理文件夹
                parentPath+=file.getName()+File.separator;
                File [] files=file.listFiles();
                for(File f:files){
                    this.writeZip(f, parentPath, zos);
                }
            }else{
                FileInputStream fis=null;
                DataInputStream dis=null;
                try {
                    fis=new FileInputStream(file);
                    dis=new DataInputStream(new BufferedInputStream(fis));
                    ZipEntry ze = new ZipEntry(parentPath + file.getName());
                    zos.putNextEntry(ze);
                    byte [] content=new byte[1024];
                    int len;
                    while((len=fis.read(content))!=-1){
                        zos.write(content,0,len);
                        zos.flush();
                    }
                    
                
                } catch (FileNotFoundException e) {
                	e.printStackTrace();
                } catch (IOException e) {
                	e.printStackTrace();
                }finally{
                    try {
                        if(dis!=null){
                            dis.close();
                        }
                    }catch(IOException e){
                        e.printStackTrace();
                    }
                }
            }
        }
    }

	
	//跳转批量导入页面
	public void Upload(String year,String month) {
//		try{
//			createExcel(request,response);
//		}catch(Exception e){
//			e.printStackTrace();
//		}
		Connection conn = null;
		ASFuntion asf = new ASFuntion();

		try{
			conn = new DBConnect().getConnect("");
			SalaryService salaryService = new SalaryService(conn);
		//	salaryService.initDelete(departmentId, month);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
	}
	
	/**
	 * 保存批量导入
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView SaveUpload(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		ASFuntion asf = new ASFuntion();
		
		String pch = asf.showNull(request.getParameter("pch"));
		if("".equals(pch)) pch = UUID.randomUUID().toString();
		
		PrintWriter out = null;
		Connection conn=null;
		Single sl = new Single();
		
		UserSession us=(UserSession)request.getSession().getAttribute("userSession");
		String areaid = us.getAreaid();
		String departmentId = us.getUserAuditDepartmentId();
		String lockmsg = "装载工资数据";
		
		try {
			conn = new DBConnect().getDirectConnect("");
			
			//定义单一，避免其他用户干扰；
			try {
				sl.locked(lockmsg, us.getUserLoginId());
			} catch (Exception e) {
				throw new Exception("其他同事正在保存工资，请稍等："+e.getMessage());
			}
			
			response.setContentType("text/html;charset=utf-8");  //设置编码
			out = response.getWriter();
			
			
			/**
			 * 上传文件处理段
			 */
			Map parameters = null;

			String uploadtemppath = "";
			String strFullFileName = "";

			MyFileUpload myfileUpload = new MyFileUpload(request);
			uploadtemppath = myfileUpload.UploadFile(null, null);
			parameters = myfileUpload.getMap();
			System.out.println(parameters);

			//如果不存在,说明是第一次提交,这个时候从MAP中取值
			uploadtemppath = (String) parameters.get("tempdir");

			strFullFileName = uploadtemppath
					+ (String) parameters.get("filename");
			org.util.Debug.prtOut("strFullFileName=" + strFullFileName);
			
			uploadtemppath = (String) parameters.get("tempdir");
			if (uploadtemppath.equals("")){
				throw new Exception("工资文件上传失败");
			}

			/**
			 * 加载EXCEL到临时表处理段
			 */
			ExcelUploadService upload = null;
			try {
				upload = new ExcelUploadService(conn,strFullFileName);
			} catch (Exception e) {
				e.printStackTrace();
				throw new Exception("临时路径错误："+e.getMessage());
			}
			
			org.util.Debug.prtOut("装载的临时目录为:" + uploadtemppath);
			upload.init();

				
			SalaryService salaryService = new SalaryService(conn);
			
			String[] tableRows = { "项目","姓名","部门","年份","月份","备注"};
			
			upload.setExcelNum("");
			upload.setExcelString("项目,姓名,部门,年份,月份,备注");
			String[] exexlKmye = { "项目","姓名","部门","年份","月份","备注"};
			String[] tableKmye = { "pchname","username","departmentname","nowYear","nowMonth","memo"};
			
//			List<String> list = salaryService.getExcelValue();
			//String[] exexlPzmxOpt = new String[list.size()];   //可选列Excel
			//String[] tablePzmxOpt = new String[list.size()];   //可选列数据库
			//String[] exexlKmyeFixFields = new String[list.size()];
			//String[] excelKmyeFixFieldValues = new String[list.size()];
			//for(int i = 0;i<list.size();i++){
			//	exexlPzmxOpt[i]=list.get(i);
			//	tablePzmxOpt[i]="v"+(i+1);
			//	exexlKmyeFixFields[i]="n"+(i+1);
			//	excelKmyeFixFieldValues[i]=list.get(i);
			//}
			String[] excelAllRows = upload.getAllHeads("工资表");
			String[] excelRows = this.getExcelRows(tableRows, excelAllRows);
			//excelRows = {国庆过节费	中秋过节费};
			//国庆过节费,中秋过节费 = v1,v2; n1,n2 =国庆过节费,中秋过节费 
			
			String groupset1 = "",groupset2 = "",groupset3 = "",groupset4 = "",groupset = "";
			String exexlPzmx = "",tablePzmx = "",Fields = "",FieldValues = "";
			for (int i = 0; i < excelRows.length; i++) {
				exexlPzmx += "," + excelRows[i] + "," + excelRows[i];
				tablePzmx += ",v" + (i+1) + ",c"+ (i+1);
				Fields += ",n" + (i+1);
				FieldValues += "," + excelRows[i];
				
				if(i>=0 && i<=11){ //应收一
					if(!"".equals(excelRows[i])){
						groupset1 += ",应发一." + excelRows[i];
					}
				}
				if(i>=23 && i<=34){ //应收二
					if(!"".equals(excelRows[i])){
						groupset2 += ",应发二." + excelRows[i];
					}
				}
				if(i>=13 && i<=20){ //应扣
					if(!"".equals(excelRows[i])){
						groupset3 += ",应扣." + excelRows[i];
					}
				}
				if(i==12 || i==21 || i==22 || i==35){ 
					if(!"".equals(excelRows[i])){
						groupset4 += ",合计." + excelRows[i];
					}
				}
			}
			groupset = groupset1 + groupset2 + groupset3 + groupset4;
			if(!"".equals(groupset)) groupset = groupset.substring(1);
			
			String[] exexlPzmxOpt = (exexlPzmx.substring(1)).split(",");
			String[] tablePzmxOpt= (tablePzmx.substring(1)).split(",");
			String[] exexlKmyeFixFields=("pch" + Fields).split(",");
			String[] excelKmyeFixFieldValues=(pch + FieldValues).split(",");
//				upload.LoadFromExcelAllSheet("tt_k_salary",
//				exexlKmye, tableKmye, exexlPzmxOpt, tablePzmxOpt,
//				exexlKmyeFixFields, excelKmyeFixFieldValues);

				upload.LoadFromExcel("工资表","tt_k_salary",  
						exexlKmye,tableKmye, exexlPzmxOpt, tablePzmxOpt, 
						exexlKmyeFixFields, excelKmyeFixFieldValues);
//				upload.LoadFromExcel("人员列表", "tt_k_user",
//						exexlKmye, tableKmye, exexlPzmxOpt, tablePzmxOpt,
//						exexlKmyeFixFields, excelKmyeFixFieldValues);
//			upload.LoadFromExcelAllSheet("tt_k_salary", exexlKmye, tableKmye);
			org.util.Debug.prtOut("装载用户内容完毕!<BR>");

			/**
			 * 处理工资更新代码段
			 */
			org.util.Debug.prtOut("开始更新工资信息!......");
			
			//更新分组groupset
			new DbUtil(conn).update("tt_k_salary", "pch", pch, "groupset", groupset);
			
			salaryService.updateData(pch,areaid,"已发放");
			//salaryService.deleteByPch(pch);
			//salaryService.insertData(pch);
			
			//删除临时表
			//salaryService.delTable(); 
			out.println("<script language=\"javascript\">");
			out.println("alert(\"工资数据装载成功!\");");
			out.println("document.location.href=\""+request.getContextPath() + "/salary.do?method=businessList\";");
//			out.println("</font><br>");
			out.println("</script>");
			
				
		} catch (Exception e) {
			e.printStackTrace();
			out.println("工资数据装载失败:"+e.getMessage());
		} finally {
			//解除并发锁定
			try {
				sl.unlocked(lockmsg, us.getUserLoginId());
			} catch (Exception e) {
				out.println("撤销并发锁失败：" + e.getMessage() + "<br/>");
			}
			DbUtil.close(conn);
		}

		return null;
	}
	
	public String [] getExcelRows(String [] tableRows,String [] excelAllRows ){
//		String []excelRows = new String [excelAllRows.length-1];
		int [] ii = new int [excelAllRows.length];
		for(int i=0;i<ii.length;i++){
			ii[i]=-1;
		}
		int num=0;
		for (int i = 0; i < excelAllRows.length-1; i++) {
			for (int j = 0; j < tableRows.length; j++) {
				if(excelAllRows[i].trim().equals(tableRows[j].trim())){
//					ii[num++]=i;
					break;
				}
				if(j==tableRows.length-1){
//					excelRows[num] = new String();
//					excelRows[num]=excelAllRows[i];
//					num++;
					ii[num]=i;
					num++;
				}
			}
		}
		String []excelRows = new String [num];
		for (int i = 0; i < ii.length; i++) {
			for (int j = 0; j < excelAllRows.length-1; j++) {
				if(ii[i]==j && ii[i]!=-1){
					excelRows[i]= new String();
					excelRows[i]= excelAllRows[j].trim();
					break;
				}
			}
		}
		
		org.util.Debug.prtOut("excelRows:="+excelRows.length);
		return excelRows;
	}
	
	public static void main(String[] args) {
		String sql = "sadfasdfasdf ${year} ${month} ${userid}";
//		String temp = "year";
//		 sql=sql.replaceAll("\\$\\{|\\}", "yy");
//		String[] ssss = UTILString.getVaribles(sql);
//		for (int i = 0; i < ssss.length; i++) {
//			System.out.println(ssss[i]);
//		}
		
		//ASFuntion asf = new ASFuntion();
		
		//System.out.println(asf.getCurrentDate());
		String[]  aa =new String[]{"1","2","3"};
		for(int i = 0;i<aa.length;i++){
			System.out.println(aa[i]);
		}
	}
}

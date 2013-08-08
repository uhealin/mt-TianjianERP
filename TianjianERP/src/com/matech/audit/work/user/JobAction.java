package com.matech.audit.work.user;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.text.MessageFormat;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;

import org.jbpm.api.ProcessInstance;
import org.jbpm.api.TaskQuery;
import org.jbpm.api.task.Task;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.matech.audit.pub.datagrid.DataGrid;
import com.matech.audit.pub.datagrid.DataGridProperty;
import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.pub.db.PinyingUtil;
import com.matech.audit.service.attachFileUploadService.AttachService;
import com.matech.audit.service.datamanage.DataZip;
import com.matech.audit.service.employment.EmploymentService;
import com.matech.audit.service.fileupload.MyFileUpload;
import com.matech.audit.service.hr.model.ResumeVO;
import com.matech.audit.service.leave.LeaveService;
import com.matech.audit.service.log.LogService;
import com.matech.audit.service.oa.labor.LaborBargainService;
import com.matech.audit.service.oa.labor.model.LaborBargain;
import com.matech.audit.service.process.JbpmTemplate;
import com.matech.audit.service.process.ProcessFormService;
import com.matech.audit.service.process.model.ProcessForm;
import com.matech.audit.service.user.JobService;
import com.matech.audit.service.user.StaffRegisterService;
import com.matech.audit.service.user.UserService;
import com.matech.audit.service.user.model.JobVO;
import com.matech.audit.service.user.model.ReportDutFlow;
import com.matech.audit.service.user.model.StaffJobIntro;
import com.matech.audit.service.user.model.StaffLiaison;
import com.matech.audit.service.user.model.StaffLiaisonFamily;
import com.matech.audit.service.user.model.StaffPost;
import com.matech.audit.service.user.model.StaffPractice;
import com.matech.audit.service.user.model.StaffRegister;
import com.matech.audit.service.user.model.StaffSocialseCurity;
import com.matech.audit.service.user.model.StaffSocialseCurityHp;
import com.matech.audit.service.user.model.User;
import com.matech.audit.service.userpopedom.UserPopedomService;
import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.autocode.DELAutocode;
import com.matech.framework.pub.autocode.DELUnid;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.single.Single;
import com.matech.framework.pub.util.ASFuntion;
import com.matech.framework.pub.util.WebUtil;
import com.matech.framework.service.excelupload.ExcelUploadService;
import com.matech.sms.SmsOpt;

public class JobAction extends MultiActionController {

	private final String list = "user/jobList.jsp"; 
	private final String planList = "user/planJobList.jsp"; 
	private final String jobCountList = "user/jobCountList.jsp"; 
	
	
	private final String jobEdit = "user/jobEdit.jsp";
	private final String alJobEdit = "user/alJobEdit.jsp";
	
	private final String resume = "user/resumeList.jsp";
	private final String resumeEdit = "user/resumeEdit.jsp";
	private final String jobList = "AS_SYSTEM/resumeList.jsp";
	private final String jobresumeEdit = "AS_SYSTEM/resumeEdit.jsp";
	private final String reportDutAuditList ="user/reportDutAuditList.jsp";
	private final String reportDutAudit ="user/reportDutAudit.jsp";
	
	private JbpmTemplate jbpmTemplate;
	
	public JbpmTemplate getJbpmTemplate() {
		return jbpmTemplate;
	}

	public void setJbpmTemplate(JbpmTemplate jbpmTemplate) {
		this.jbpmTemplate = jbpmTemplate;
	}
	
	
	public ModelAndView planList(HttpServletRequest request, HttpServletResponse response)   throws Exception {
		ModelAndView modelAndView = new ModelAndView(planList);
		UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
		
		ASFuntion CHF = new ASFuntion();
		
		String menuid = CHF.showNull(request.getParameter("menuid")); //菜单ID
		if("".equals(menuid)) menuid = "10000756";
		Connection conn = null;
		try {
			conn = new DBConnect().getConnect("");
			String departments = new UserPopedomService(conn).getUserIdPopedom(userSession.getUserId(), menuid);
	    	
			String strSql = "";
			
			strSql = " and (a.departmentid in ("+departments+")  or a.departmentid ='"+userSession.getUserAuditDepartmentId()+"')";
			
			DataGridProperty pp = new DataGridProperty();
			String table = "tt_" + DELUnid.getNumUnid();
			
			String sql = "select a.*,b.departname " +
			"	from k_job a,k_department b " +
			"	where a.departmentid =b.autoid " + strSql +
			"	${departmentid} ${jobname} ${city} ${toworktime} ${working} ${state} and  who = 'planList'";
			
			pp.setOrderBy_CH("a.departmentid,a.lasttime");
			pp.setDirection("asc,desc");
			pp.setPrintEnable(true);
			pp.setPrintVerTical(false);
			pp.setPrintTitle("招聘计划");
			
			//pp.addColumn("机构名称", "");
			pp.addColumn("部门", "departname");
			pp.addColumn("岗位名称", "jobname");
			pp.addColumn("人数", "peoplecount");
			pp.addColumn("工作城市", "city");
			pp.addColumn("到岗时间", "toworktime");
			pp.addColumn("工时要求", "working"); 
			pp.addColumn("招聘类型", "type"); 
			pp.addColumn("状态", "state");
			pp.addColumn("最后修改人", "lastuser");
			pp.addColumn("最后修改时间", "lasttime");
			pp.setTableID(table);
			
			pp.addSqlWhere("departmentid", " and a.departmentid = '${departmentid}' ");
			pp.addSqlWhere("jobname", " and a.jobname like '%${jobname}%' ");
			pp.addSqlWhere("city", " and a.city = '${city}' ");
			pp.addSqlWhere("toworktime", " and a.toworktime = '${toworktime}' ");
			pp.addSqlWhere("working", " and a.working = '${working}' ");
			pp.addSqlWhere("state", " and a.state = '${state}' ");
			
			pp.setPageSize_CH(50);
			
			pp.setInputType("radio");
			pp.setWhichFieldIsValue(1);
			
			pp.setCustomerId("") ;

			pp.setSQL(sql);

			request.getSession().setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);
			
			modelAndView.addObject("tableid", pp.getTableID());
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(conn);
		}
    	
		return modelAndView;
	}
	
	/**
	 * 统计部门的招聘计划
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView bmzpjhtj(HttpServletRequest request, HttpServletResponse response)   throws Exception {
		ModelAndView modelAndView = new ModelAndView(jobCountList);
		UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
		
		ASFuntion CHF = new ASFuntion();
		
		Connection conn = null;
		try {
			conn = new DBConnect().getConnect("");
			String departments = new UserPopedomService(conn).getUserIdPopedom(userSession.getUserId(), "BMZPJH");
	    	
			String strSql = "";
			
			strSql = " and (a.departmentid in ("+departments+")  or a.departmentid ='"+userSession.getUserAuditDepartmentId()+"')";
			
			DataGridProperty pp = new DataGridProperty();
			
			String sql = "select a.*,b.departname " +
			"	from k_job a,k_department b " +
			"	where a.departmentid =b.autoid " + strSql +
			"	${departmentid} ${jobname} ${city} ${lasttime1} ${lasttime2} ${working} and  who = 'planList'";
			
			sql="SELECT jobname,SUM(peoplecount) AS peoplecount,GROUP_CONCAT(DISTINCT departmentname) AS departmentname,city FROM ("
				+sql +" )t WHERE state='有效' GROUP BY jobname,TYPE ";
			
			pp.setOrderBy_CH("jobname");
			pp.setDirection("asc");
			pp.setPrintEnable(true);
			pp.setPrintVerTical(false);
			
			pp.setPrintTitle("部门招聘计划统计");
			
			//pp.addColumn("机构名称", "");
			pp.addColumn("岗位名称", "jobname");
			pp.addColumn("人数", "peoplecount");
			pp.addColumn("发布部门", "departmentname");
			pp.addColumn("工作城市", "city");
			
			pp.setTableID("bmzpjhtj");
			
			pp.addSqlWhere("departmentid", " and a.departmentid = '${departmentid}' ");
			pp.addSqlWhere("jobname", " and a.jobname like '%${jobname}%' ");
			pp.addSqlWhere("city", " and a.city = '${city}' ");
			pp.addSqlWhere("working", " and a.working = '${working}' ");
			pp.addSqlWhere("lasttime1", " and a.lasttime>= '${lasttime1}' ");
			pp.addSqlWhere("lasttime2", " and a.lasttime<= '${lasttime2}' ");
			
			pp.setPageSize_CH(50);
			
			pp.setInputType("");
			pp.setWhichFieldIsValue(1);
			
			pp.setCustomerId("") ;

			pp.setSQL(sql);

			request.getSession().setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);
			
			modelAndView.addObject("tableid", pp.getTableID());
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(conn);
		}
    	
		return modelAndView;
	}
	
	//表 k_job
	/**
	 * 人事的表格
	 */
	public ModelAndView list(HttpServletRequest request, HttpServletResponse response)   throws Exception {
		ModelAndView modelAndView = new ModelAndView(list);
		UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
		
		ASFuntion CHF = new ASFuntion();
		
		String menuid = CHF.showNull(request.getParameter("menuid")); //菜单ID
		if("".equals(menuid)) menuid = "10000757";
		Connection conn = null;
		try {
			conn = new DBConnect().getConnect("");
			String departments = new UserPopedomService(conn).getUserIdPopedom(userSession.getUserId(), menuid);
	    	
			String strSql = " and (a.departmentid in ("+departments+")  or a.departmentid ='"+userSession.getUserAuditDepartmentId()+"')";
			
			DataGridProperty pp = new DataGridProperty();
			String table = "tt_" + DELUnid.getNumUnid();
			
			String sql = "select a.*,b.departname " 
			+",(SELECT COUNT(UUID) FROM k_resume WHERE apply_job_id=a.unid) AS r_count "
			+",(SELECT COUNT(UUID) FROM k_resume WHERE apply_job_id=a.unid AND state='已审核') AS rc_count  "
			+",(SELECT COUNT(UUID) FROM k_resume WHERE apply_job_id=a.unid AND idcard IN (SELECT identityCard FROM k_user )) AS ri_count "
			+"	from k_job a,k_department b " +
			"	where a.departmentid =b.autoid " + strSql +
			"	${departmentid} ${jobname} ${city} ${toworktime} ${working} ${state} and  who = 'list'";
			
			pp.setOrderBy_CH("a.departmentid,a.lasttime");
			pp.setDirection("asc,desc");
			pp.setPrintEnable(true);
			pp.setPrintVerTical(false);
			pp.setPrintTitle("招聘计划");
			
			pp.addColumn("招聘批次号", "xulienumber","hide");
			pp.addColumn("岗位名称", "jobname");
			
			pp.addColumn("投历人数", "rotenumber","hide");
			pp.addColumn("工作城市", "city");
			pp.addColumn("到岗时间", "toworktime");
			pp.addColumn("工时要求", "working");
			pp.addColumn("状态", "state");
			pp.addColumn("招聘类型", "type"); 
			pp.addColumn("最后修改人", "lastuser");
			pp.addColumn("最后修改时间", "lasttime");
			pp.addColumn("拟招人数", "peoplecount");
			pp.addColumn("简历人数", "r_count");
			pp.addColumn("入选人数", "rc_count");
			pp.addColumn("入职人数", "ri_count");
			pp.setTableID(table);
			
			pp.addSqlWhere("departmentid", " and a.departmentid = '${departmentid}' ");
			pp.addSqlWhere("jobname", " and a.jobname like '%${jobname}%' ");
			pp.addSqlWhere("city", " and a.city = '${city}' ");
			pp.addSqlWhere("toworktime", " and a.toworktime = '${toworktime}' ");
			pp.addSqlWhere("working", " and a.working = '${working}' ");
			pp.addSqlWhere("state", " and a.state = '${state}' ");
			
			pp.setPageSize_CH(50);
			
			pp.setInputType("radio");
			pp.setWhichFieldIsValue(1);
			
			pp.setCustomerId("") ;
			
			// pp.setTrActionProperty(true);
			pp.setTrAction("style='cursor:hand;' lastuser='${lastuser}' ");

			pp.setSQL(sql);

			request.getSession().setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);
			
			modelAndView.addObject("tableid", pp.getTableID());
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(conn);
		}
    	
		return modelAndView;
	}
	
	public ModelAndView alEdit(HttpServletRequest request, HttpServletResponse response)   throws Exception {
		ModelAndView modelAndView = new ModelAndView(alJobEdit);
		Connection conn=null;
		DbUtil dbUtil=null;
		try {
			ASFuntion CHF = new ASFuntion();
			
			UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
			
			//所属事务所
			String departid = CHF.showNull(userSession.getUserAuditDepartId());
			String flag = CHF.showNull(request.getParameter("flag")); //区分查看的权限
			String unid = CHF.showNull(request.getParameter("unid")); //有就是修改，无就是新增 
			String table = CHF.showNull(request.getParameter("table")); //表名
			
			conn= new DBConnect().getConnect("");
			String  departname =new ASFuntion().showNull(new DbUtil(conn).queryForString(" SELECT b.departname FROM k_user a INNER JOIN `k_department` b ON a.departID = b.autoid WHERE a.departmentid = "+ departid) );
			
			dbUtil=new DbUtil(conn);
			JobService js = new JobService(conn);
			Map edit = new HashMap();
			if(!"".equals(unid)){
				edit = js.get(table, "unid", unid);
			}else{
				if("k_job".toLowerCase().equals(table.toLowerCase())){
					//招聘
					edit.put("departmentname", departname);
					edit.put("departmentid", userSession.getUserAuditDepartmentId());
					edit.put("state", "有效");
				}else if("k_resume".toLowerCase().equals(table.toLowerCase())){
					//简历
					edit.put("resumeid",js.getAutoCode(""));
					edit.put("state", "候选");
					edit.put("sex", "男");
					edit.put("attachname", "");
					edit.put("attachid", UUID.randomUUID().toString());
				}
			}
			
			if("k_job".toLowerCase().equals(table.toLowerCase())){
				//招聘
				modelAndView.setViewName(alJobEdit);
			}else if("k_resume".toLowerCase().equals(table.toLowerCase())){
				//简历
				String attachid = CHF.showNull((String)edit.get("attachid"));
				edit.put("attachid", "".equals(attachid) ? UUID.randomUUID().toString() : attachid);
				modelAndView.setViewName(resumeEdit);
			}
			
			modelAndView.addObject("edit", edit);
			modelAndView.addObject("flag", flag);
			modelAndView.addObject("table", table);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}
		return modelAndView;
	}
	
	
	
	//新增或修改
	public ModelAndView edit(HttpServletRequest request, HttpServletResponse response)   throws Exception {
		ModelAndView modelAndView = new ModelAndView(jobEdit);
		Connection conn=null;
		try {
			ASFuntion CHF = new ASFuntion();
			 
			UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
			
			String flag = CHF.showNull(request.getParameter("flag")); //区分查看的权限
			String unid = CHF.showNull(request.getParameter("unid")); //有就是修改，无就是新增 
			String table = CHF.showNull(request.getParameter("table")); //表名
			String who = CHF.showNull(request.getParameter("who"));
			//所属事务所
			
			
			conn= new DBConnect().getConnect("");
			
			JobService js = new JobService(conn);
			Map edit = new HashMap();
			if(!"".equals(unid)){
				edit = js.get(table, "unid", unid);
			}else{
				if("k_job".toLowerCase().equals(table.toLowerCase())){
					//部门招聘计划和岗位
					edit.put("departmentname", userSession.getUserAuditDepartmentName());
					edit.put("departmentid", userSession.getUserAuditDepartmentId());
					
					edit.put("areaid", userSession.getAreaid());
					edit.put("areaname", userSession.getAreaname());
					
					
					edit.put("state", "有效");
				}else if("k_resume".toLowerCase().equals(table.toLowerCase())){
					//简历
					edit.put("resumeid",js.getAutoCode(""));
					edit.put("state", "候选");
					edit.put("sex", "男");
					edit.put("attachname", "");
					edit.put("attachid", UUID.randomUUID().toString());
				}
			}
			
			if("k_job".toLowerCase().equals(table.toLowerCase())){
				//招聘
				modelAndView.setViewName(jobEdit);
			}else if("k_resume".toLowerCase().equals(table.toLowerCase())){
				//简历
				String attachid = CHF.showNull((String)edit.get("attachid"));
				edit.put("attachid", "".equals(attachid) ? UUID.randomUUID().toString() : attachid);
				modelAndView.setViewName(resumeEdit);
			}
			if(who == "role"){
				modelAndView.addObject("who", who);
			}else{
				modelAndView.addObject("who", who);
			}
			modelAndView.addObject("edit", edit);
			modelAndView.addObject("flag", flag);
			modelAndView.addObject("table", table);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}
		return modelAndView;
	}
	
	//保存
	public void save(HttpServletRequest request, HttpServletResponse response)   throws Exception {
		Connection conn=null;
		try {
			
			UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
			
			ASFuntion CHF = new ASFuntion();
			String typeid = CHF.showNull(request.getParameter("typeid")); //
			String flag = CHF.showNull(request.getParameter("flag")); //
			
			Map parameters = new HashMap();
			Enumeration enum1 = request.getParameterNames();
			DELAutocode t=new DELAutocode();

			while (enum1.hasMoreElements()) {
				String paramName = (String) enum1.nextElement();
				String [] paramValue = request.getParameterValues(paramName);
				if(paramValue.length == 1 ){
					parameters.put(paramName, paramValue[0]);	
					System.out.println(paramName+"="+paramValue[0]);
				}else{
					parameters.put(paramName, paramValue);
					System.out.println(paramName+"="+paramValue);
				}
				
			}
			parameters.put("typeid", typeid);
			parameters.put("lastuser", userSession.getUserName());
			parameters.put("lasttime", CHF.getCurrentDate() + " " + CHF.getCurrentTime());
			parameters.put("who", flag);
			
			String table = CHF.showNull((String)parameters.get("table"));
			
			conn= new DBConnect().getConnect("");
			
			JobService js = new JobService(conn);
			String unid = CHF.showNull((String)parameters.get("unid"));
			if("".equals(unid)){
				//新增
				unid = UUID.randomUUID().toString(); //用来生成数据库的主键id非常不错。。
				parameters.put("unid", unid);
				js.add(table, null, parameters);
			}else{
				//修改
				js.update(table, "unid", parameters);
			}
			
			if("k_job".toLowerCase().equals(table.toLowerCase())){
				//招聘
				response.sendRedirect(request.getContextPath() + "/job.do?method="+flag);
			}else if("k_resume".toLowerCase().equals(table.toLowerCase())){
				//简历
				response.sendRedirect(request.getContextPath() + "/job.do?method=resume");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}
	}
	
	//删除
	public void delete(HttpServletRequest request, HttpServletResponse response)   throws Exception {
		Connection conn=null;
		try {
			
			UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
			
			ASFuntion CHF = new ASFuntion();
			String table = CHF.showNull(request.getParameter("table"));
			String unid = CHF.showNull(request.getParameter("unid"));
			conn= new DBConnect().getConnect("");
			
			JobService js = new JobService(conn);
			
			if("k_resume".toLowerCase().equals(table.toLowerCase())){
				//简历的unid，改为多条
				String []uuid = unid.split(",");
				for (int i = 0; i < uuid.length; i++) {
					//k_resume 1、删除对应附件
					if(!"".equals(CHF.showNull(uuid[i]))){
						String indexId = js.get(table, "unid", uuid[i], "attachid").toString(); //求出附件的索引
						AttachService attachService = new AttachService(conn);
						attachService.remove(table, indexId);	
						
						js.del(table, "unid", uuid[i]);
					}
				}
			}else{
				//删除
				js.del(table, "unid", unid);				
			}

			
			response.setContentType("text/html;charset=utf-8");
			PrintWriter out = response.getWriter();
			out.write("OK");
			out.close();
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}
			
	}
	
	//修改状态
	public void updateJob(HttpServletRequest request, HttpServletResponse response)   throws Exception {
		Connection conn=null;
		try {
			
			UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
			
			ASFuntion CHF = new ASFuntion();
			String table = CHF.showNull(request.getParameter("table"));
			String unid = CHF.showNull(request.getParameter("unid"));
			String state = CHF.showNull(request.getParameter("state"));
			
			if("1".equals(state)) {
				state = "有效";
			} else {
				state = "无效";
			}
			
			System.out.println("状态："+state);
			
			
			conn= new DBConnect().getConnect("");
			
			JobService js = new JobService(conn);
			if("k_resume".toLowerCase().equals(table.toLowerCase())){
				//简历
				//unid 是多条的
				String []uuid = unid.split(",");
				for (int i = 0; i < uuid.length; i++) {
					//k_resume 1、删除对应附件
					if(!"".equals(CHF.showNull(uuid[i]))){
						Map parameters = js.get(table, "unid", uuid[i]);
						parameters.put("state", state);
						parameters.put("lastuser", userSession.getUserName());
						parameters.put("lasttime", CHF.getCurrentDate() + " " + CHF.getCurrentTime());
						String oldState = js.getValueBySql("SELECT state FROM `k_resume` WHERE unid='"+unid+"'");
						String name = js.getValueBySql("SELECT name FROM `k_resume` WHERE unid='"+unid+"'");
						LogService.addTOLog(userSession, conn, "简历库", "<font color=blue>"+name+"</font> 的简历状态为：【"+oldState+"】修改为：【"+state+"】","简历库状态批量修改");
						js.update(table, "unid", parameters);
					}
				}
			}else{
				//招聘
				Map parameters = js.get(table, "unid", unid);
				parameters.put("state", state);
				parameters.put("lastuser", userSession.getUserName());
				parameters.put("lasttime", CHF.getCurrentDate() + " " + CHF.getCurrentTime());
				js.update(table, "unid", parameters);
			}
			
			
			response.setContentType("text/html;charset=utf-8");
			PrintWriter out = response.getWriter();
			out.write("OK");
			out.close();
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}
			
	}
	
	//简历库(resume)
	public ModelAndView resume(HttpServletRequest request, HttpServletResponse response)   throws Exception {
		ModelAndView modelAndView = new ModelAndView(resume);
		
		DataGridProperty pp = new DataGridProperty();
		String table = "tt_" + DELUnid.getNumUnid();
		
		String sql = "select * from k_resume a where 1=1 ${jobname} ${name} ${paperstype} ${papersnumber} ${state} ";
		
		pp.setOrderBy_CH("a.resumeid,a.lasttime");
		pp.setDirection("desc,desc");
		
		pp.addColumn("自动编号", "resumeid");
		//pp.addColumn("附件", "attachname");
		pp.addColumn("人力资源建议岗位", "jobname");
		pp.addColumn("姓名", "Name");
		pp.addColumn("性别", "Sex");
		pp.addColumn("学历", "educational");
		pp.addColumn("证件类型", "paperstype");
		pp.addColumn("证件号码", "papersnumber");
		pp.addColumn("执业资质", "cpano");
		pp.addColumn("手机", "mobilePhone");
		pp.addColumn("邮箱", "email");
		pp.addColumn("状态", "state");
		pp.addColumn("最后修改人", "lastuser");
		pp.addColumn("最后修改时间", "lasttime");
		pp.setTableID(table);
		pp.setPrintEnable(true);
		pp.setPrintVerTical(false);
		pp.setPrintTitle("简历库与招聘状态管理");
		
		pp.addSqlWhere("jobname", " and a.jobname like '%${jobname}%' ");
		pp.addSqlWhere("name", " and a.name like '%${name}%' ");
		pp.addSqlWhere("paperstype", " and a.paperstype like '%${paperstype}%' ");
		pp.addSqlWhere("papersnumber", " and a.papersnumber like '%${papersnumber}%' ");
		pp.addSqlWhere("state", " and a.state = '${state}' ");
		
		pp.setPageSize_CH(50);
		
		pp.setInputType("checkbox");
		pp.setWhichFieldIsValue(1);
		
		pp.setCustomerId("") ;
//		pp.setPrintEnable(true) ;
//		pp.setPrintTitle("报表列表") ;
//		pp.setPrintColumnWidth("10,10,10,10,10,10,10,10,10,10");
//		pp.setTrActionProperty(true) ;
//		pp.setTrAction("projectId=${projectid}") ;
//		pp.setColumnWidth("10,10,10,10,10,10,10,10,10,10");

		pp.setSQL(sql);

		request.getSession().setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);
		
		modelAndView.addObject("tableid", pp.getTableID());
		
		return modelAndView;
	}
	
	//批量导入简历库
	public ModelAndView Upload(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return new ModelAndView("/user/resumeUpload.jsp");
	}
	
	//批量导入保存
	public void SaveUpload(HttpServletRequest request, HttpServletResponse response) throws Exception {
		PrintWriter out = null;
		Connection conn=null;
		UserSession us=(UserSession)request.getSession().getAttribute("userSession");
		String lockmsg = "批量导入简历库";
		Single sl = new Single();
		try {
			ASFuntion CHF = new ASFuntion();
			response.setContentType("text/html;charset=utf-8");  //设置编码
			response.setHeader("title", "批量导入简历库");
			out = response.getWriter();
			
			Map parameters = null;

			String uploadtemppath = "";
			String strFullFileName = "";

			MyFileUpload myfileUpload = new MyFileUpload(request);
			uploadtemppath = myfileUpload.UploadFile(null, null);
			parameters = myfileUpload.getMap();

			//如果不存在,说明是第一次提交,这个时候从MAP中取值
			uploadtemppath = (String) parameters.get("tempdir");

			strFullFileName = uploadtemppath + (String) parameters.get("filename");
			System.out.println("strFullFileName=" + strFullFileName);
			uploadtemppath = (String) parameters.get("tempdir");

			if (uploadtemppath.equals(""))
				out.print("Error\n简历库数据上传及预处理失败");
			else
				out.println("简历库数据上传并分析成功!<br>正在加载，请等待<br><br><br>");

			int error = 0; //用于标记程序是否出错,出错了后面就不会再继续执行了

			//分析帐套文件,取出帐套年份;
			out.println("预处理分析简历库文件<br/>");
			out.flush();
			
			//解压
			DataZip zip = new DataZip();
			zip.unZipCHN(strFullFileName, uploadtemppath, false);
			
			//处理EXCEL文件	
			String strExcelFileName = uploadtemppath + "resume.xls";
			System.out.println("strFullFileName=" + strExcelFileName);
			
			conn= new DBConnect().getConnect("");
			
			//初始化业务对象
			ExcelUploadService upload = null; 
			try {
				upload = new ExcelUploadService(conn,strExcelFileName);
			} catch (Exception e) {
				e.printStackTrace();
				out.println("ZIP压缩包中不能没有【resume.xls】文件,请重新导入<br>");
				error = 1;
			}
			
			try {
				sl.locked(lockmsg, us.getUserLoginId());
			} catch (Exception e) {
				out.println(e.getMessage() + "<br/>");
				error = 1;
			}
			
			if (error > 0) {
				out.println("批量导入简历库遇到错误,已经中止!<br>请解决错误后重新导入");
			} else {
				
				JobService job = new JobService(conn);
				
				out.println("继续处理装载<br>");
				out.flush();
				
				out.println("正在分析EXCEL文件......");
				out.flush();
				upload.init();
				out.println("分析EXCEL文件完毕!<BR>");
				
				out.println("正在装载用户简历信息!......");
				out.flush();
				
				upload.setExcelNum("");
				upload.setExcelString("证件编号,CPA编号,出生年月,手机");
				String[] exexlKmye = { "应聘岗位","姓名","证件类型","证件编号" };
				String[] tableKmye = { "jobname", "name", "paperstype","papersnumber" };
				String[] exexlPzmxOpt = {"CPA编号",	"性别",	"出生年月",	"学历",	"毕业院校及专业","手机",	"邮箱",	"工作简历"};
				String[] tablePzmxOpt = {"cpano","sex","borndate","educational","diploma","mobilephone","email","specialty"};
				String[] exexlKmyeFixFields = { "state","lastuser","lasttime"};
				String[] excelKmyeFixFieldValues = { "候选",us.getUserName(),CHF.getCurrentDate() + " " + CHF.getCurrentTime()};
				
				job.newTable("k_resume");
				String result = "";

				result = upload.LoadFromExcel("简历库", "tt_k_resume",exexlKmye, tableKmye, exexlPzmxOpt, tablePzmxOpt,exexlKmyeFixFields, excelKmyeFixFieldValues);
				
				out.println("装载用户简历信息完毕!<BR>");
				out.flush();
				
				//1、处理附件文件，2、保存简历
				job.checkUpData(out,us.getUserId(),uploadtemppath);
				
				if (result != null && result.length() > 0) {
					out.println("<br><br>装载非正常结果报告：<BR><font color='red'>");
					out.println(result);
					out.println("</font><br>");
				}

				out.println("<hr>数据装载成功 <a href=\""+request.getContextPath() + "/job.do?method=resume\">返回查询页面</a>\"</font>");
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
			out.println("<font style=\"color:red\">装载处理出现错误:<br/>"+ e.getMessage());
			out.println("<a href=\""+request.getContextPath() + "/job.do?method=Upload\">返回装载页面</a>\"</font>");
			throw e;
		} finally {
			try {
				sl.unlocked(lockmsg, us.getUserLoginId());
			} catch (Exception e) {
				out.println("撤销并发锁失败：" + e.getMessage() + "<br/>");
			}
			DbUtil.close(conn);
		}
	}
	
	//发送短信
	public ModelAndView note(HttpServletRequest request, HttpServletResponse response) {
		return new ModelAndView("/user/resumeNote.jsp");
	}
	
	/**
	 * 发送手机短信
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	public void userMessager(HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		response.setCharacterEncoding("utf-8");
		
		Connection conn = null;
		try {
			ASFuntion asf = new ASFuntion();
			PrintWriter out = response.getWriter();
			String msg = "";
			conn = new DBConnect().getConnect("");
			String names = asf.showNull(request.getParameter("notename"));
			String conter = asf.showNull(request.getParameter("specialty"));
			JobService jobService = new JobService(conn);
			
			
			if(!"".equals(names) && !"".equals(conter)){
				names = names.substring(0, names.length()-1);
				String[] name =names.split(",");
				
				String mobilePhone = "";
				for (int i = 0; i < name.length; i++) {
					
					String sql = "SELECT mobilePhone FROM k_resume WHERE name='"+name[i]+"'";
					
					String phone = asf.showNull(jobService.getValueBySql(sql));
					
					System.out.println("phone:"+phone);
					
					if(!"".equals(phone)){
						
						//下班
						if(phone.length() == 11){
							
							mobilePhone +=phone+",";
						}
					}
				}
				
				System.out.println("mobilePhone:"+mobilePhone);
				if(!"".equals(mobilePhone)){
					
					mobilePhone = mobilePhone.substring(0, mobilePhone.length()-1);
					System.out.println("mobilePhone2:"+mobilePhone);
					msg = SmsOpt.sendSMS(mobilePhone, conter);
				}
			}
			
			out.write(msg);
			
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}

	}
	
	//简历树
	public void tree(HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.setContentType("text/html;charset=utf-8");
		Connection conn=null;
		try {
			ASFuntion CHF=new ASFuntion();
			
			conn= new DBConnect().getConnect("");
			JobService job = new JobService(conn);
			
			String jobname = CHF.showNull(request.getParameter("jobname"));
			String id = CHF.showNull(request.getParameter("id"));
			List list = null;
			if("".equals(id) || "undefined".equals(id) || "0".equals(id)) {
				list = job.getJobTree();
			}else{
				list = job.getResumeTree(jobname);
			}
			String json = "{}";
			if(list != null){
				json = JSONArray.fromObject(list).toString();
			}
			System.out.println("json="+json);
			response.getWriter().write(json);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}
		
	}
	
	/**
	 * 新员工入职了列表
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView joblist(HttpServletRequest request, HttpServletResponse response)   throws Exception {
		ModelAndView modelAndView = new ModelAndView(jobList);
		
		ASFuntion CHF = new ASFuntion();
		
		String unid = CHF.showNull(request.getParameter("unid"));
		
		DataGridProperty pp = new DataGridProperty();
		
		String sql = "select id,rank,departmentid,createdate,state from k_staffregister a " +
					 " where id='"+unid+"'";
		
		pp.setOrderBy_CH("id");
		pp.setDirection("desc");
		
		pp.addColumn("应聘岗位", "rank");
		pp.addColumn("应聘部门", "departmentid");
		pp.addColumn("提交时间", "createdate");
		pp.addColumn("状态", "state");
		pp.setTableID("jobResumeList");
		
		pp.setColumnWidth("10,15,15,10");
		
		pp.setPageSize_CH(50);
		
		pp.setWhichFieldIsValue(1);
		pp.setCancelPage(true); //不分页  显示全部
		pp.setCustomerId("") ;
		//pp.setcou
		pp.setSQL(sql);

		request.getSession().setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);
		
		return modelAndView;
	}

	/**
	 * ajax入职人是否存在
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView getAjaxIfJob(HttpServletRequest request, HttpServletResponse response)   throws Exception {
		Connection conn=null;
		try {
			ASFuntion CHF = new ASFuntion();
			
			//response.setContentType("text/html;charset=utf-8") ;
			response.setCharacterEncoding("utf-8");
			PrintWriter out = response.getWriter();
			
			String name = CHF.showNull(request.getParameter("name")); //区分查看的权限
			String paperstype = CHF.showNull(request.getParameter("paperstype")); //有就是修改，无就是新增 
			String papersnumber = CHF.showNull(request.getParameter("papersnumber")); //表名
			
			conn= new DBConnect().getConnect("");
			
			JobService js = new JobService(conn);
			String unid = "";
			String jobUnidAndDate = "";
			if(!"".equals(name) && !"".equals(paperstype) && !"".equals(papersnumber)){
				
				String sql = "SELECT unid FROM k_resume WHERE `name` LIKE '%"+name+"%' AND paperstype ='"+paperstype+"' AND papersnumber='"+papersnumber+"'";
				unid = js.getValueBySql(sql);
				
				//查询状态
				if(!"".equals(unid)){
					
					sql = "SELECT state FROM k_resume WHERE unid='"+unid+"' ";
					jobUnidAndDate =unid +"`@`"+js.getValueBySql(sql);
					sql = "SELECT submitDate FROM k_resume WHERE unid='"+unid+"' ";
					String submitDate = CHF.showNull(js.getValueBySql(sql));
					if("".equals(submitDate)){
						submitDate = "job";
					} 
					jobUnidAndDate = jobUnidAndDate+"`@`"+submitDate;
				}
			}
			 
			out.write(jobUnidAndDate) ;
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}
		return null;
	}
	
	/**
	 * 用ajax得到简历信息
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView getAjaxJob(HttpServletRequest request, HttpServletResponse response)   throws Exception {
		Connection conn=null;
		try {
			ASFuntion CHF = new ASFuntion();
			
			UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
			
			response.setContentType("text/html;charset=utf-8") ;
			PrintWriter out = response.getWriter();
			
			String unid = CHF.showNull(request.getParameter("unid")); //表名
			
			conn= new DBConnect().getConnect("");
			
			JobService js = new JobService(conn);
			Map edit = new HashMap();
			 
			if(!"".equals(unid)){
				
				edit = js.get("k_resume", "unid", unid);
			}
			
			String jsonStr = JSONArray.fromObject(edit).toString() ;
			out.write(jsonStr) ;
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}
		return null;
	}
	
	/**
	 * 新员工入职 保存
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView jboSave(HttpServletRequest request, HttpServletResponse response)   throws Exception {
		Connection conn=null;
		try {
			UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
			
			ASFuntion CHF = new ASFuntion();
			
			Map parameters = new HashMap();
			Enumeration enum1 = request.getParameterNames();
			while (enum1.hasMoreElements()) {
				String paramName = (String) enum1.nextElement();
				String [] paramValue = request.getParameterValues(paramName);
				if(paramValue.length == 1 ){
					parameters.put(paramName, paramValue[0]);	
				}else{
					parameters.put(paramName, paramValue);
				}
			}
			
			parameters.put("lasttime", CHF.getCurrentDate() + " " + CHF.getCurrentTime());
			parameters.put("submitdate", CHF.getCurrentDate() + " " + CHF.getCurrentTime());
			
			parameters.remove("name");
			parameters.remove("paperstype");
			parameters.remove("papersnumber");
			conn= new DBConnect().getConnect("");
			Set<String> key = parameters.keySet();
	        
			for (Iterator it = key.iterator(); it.hasNext();) {
	            String s = (String) it.next();
	            System.out.println(parameters.get(s));
	        }
			JobService js = new JobService(conn);
			Map map = js.get("k_resume", "unid", (String)parameters.get("unid"));
			map.putAll(parameters);
			System.out.println(map);
			//String unid = CHF.showNull((String)parameters.get("unid"));
			//修改
			js.update("k_resume", "unid", map);
			
			//启动流程
			this.startFlow(request, response, (String)parameters.get("unid"));
			
			response.sendRedirect(request.getContextPath()+"/"+jobresumeEdit);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}
		return null;
	}

	/**
	 * 新员工 入职
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView jobSkip(HttpServletRequest request, HttpServletResponse response){
		
		return new ModelAndView(jobresumeEdit);
	}
	
	/**
	 * 启动入职流程
	 * @param request
	 * @param response
	 * @param uuid
	 * @return
	 * @throws IOException
	 */
	public ModelAndView startFlow(HttpServletRequest request, HttpServletResponse response,String unid) throws IOException{
		// 获取登录的用户
		Connection conn = null;
		
		ASFuntion asf = new ASFuntion();
		try {
				conn = new DBConnect().getConnect("");

				JobService jobService = new JobService(conn);
				Map<String, String> startMap = new HashMap<String, String>();
		
				String sql = "select processDefinitionId from j_processdeploy where processKey='reportDutyFlow' " ;
			 
				String processDefinitionId = jobService.getValueBySql(sql);
				
				// 启动流程
				ProcessInstance pi =jbpmTemplate.startProcessById(processDefinitionId, startMap);
		
				// 获取节点任务
				TaskQuery tq = jbpmTemplate.getTaskService().createTaskQuery();
				List<Task> taskList2 = tq.processInstanceId(pi.getId()).list();
				Task myTask = taskList2.get(0);
		  
				ReportDutFlow reportDutFlow = new ReportDutFlow();
				reportDutFlow.setProcessInstanceId(pi.getId());
				reportDutFlow.setUnid(unid);
				reportDutFlow.setApplyDate(asf.getCurrentDate()+" "+asf.getCurrentTime());
				reportDutFlow.setState(myTask.getName());
				reportDutFlow.setCtype("");
		
				// 根据节点 ID 把下个节点的执行人 传到下一个节点
				jbpmTemplate.setTaskVariables(myTask.getId(), startMap);
				
				ProcessFormService processFormService = new ProcessFormService(conn);
				
				String activeName = jbpmTemplate.getActivityName(myTask.getId()); // 获取当前节点
				ProcessForm processForm = new ProcessForm();
				processForm.setProcessInstanseId(processDefinitionId);
				processForm.setKey("发起状态");
				processForm.setValue("发起成功");
				processForm.setNodeName(activeName);
				processForm.setDealTime(new ASFuntion().getCurrentDate()+" "+new ASFuntion().getCurrentTime());
				processForm.setProperty("");
				processFormService.add(processForm);// 添加记录(流程表)
				
				// 完成节点
				jbpmTemplate.completeTask(myTask.getId());
		
				jobService.addProcss(reportDutFlow);// 添加流程 (自己的表)
				
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		return null;

	}
	
	/**
	 * 入职审批
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView reportDutAuditList(HttpServletRequest request, HttpServletResponse response){
		
		ModelAndView modelAndView = new ModelAndView(reportDutAuditList);
		UserSession userSession =(UserSession) request.getSession().getAttribute("userSession");
		DataGridProperty pp = new DataGridProperty(); 
		try {	
			
			
//			String sql ="SELECT e.*,a.`unid`,a.jobname,a.name,a.paperstype,a.papersnumber,a.educational,a.mobilePhone,a.email,a.borndate,f.departname \n"
//				       +" FROM k_resume a "
//				       +" INNER JOIN `j_reportDutProcss` d ON a.unid  = d.`unid` \n"
//				       +" inner JOIN (" 
//				       +"SELECT DISTINCT a.DBID_ AS taskId,b.ID_, a.EXECUTION_ID_,b.ACTIVITYNAME_ AS auditStatus, \n" 
//					   +"GROUP_CONCAT(c.userID_ ) AS auditUserId  \n"
//					   +"FROM jbpm4_task a   \n"
//					   +"INNER JOIN jbpm4_execution b ON a.EXECUTION_ID_ = b.ID_   \n"
//					   +"LEFT JOIN  jbpm4_participation c ON a.DBID_ = c.TASK_  AND c.type_ = 'candidate'  \n"
//					   +" where 1=1 " 
//					   +" AND "+userSession.getUserId()+" like concat(c.userID_,'%') "
//					   +" GROUP BY a.EXECUTION_ID_  \n"
//				       +") e on e.ID_  = d.`ProcessInstanceId` "
//				       +" left join k_department f on a.departmentid = f.autoid"
//				       +" where 1=1 and (a.submitDate !='' or a.submitDate is not null) " +
//				       " ${userName} ${leaveTypeId} ${department} ${educationl} ";
				
			String sql ="select e.*,a.*,b.departname as departmentName " +
						" from k_staffregister a " +
						" left join k_department b on a.departmentid=b.autoid"
				       +" left JOIN `j_reportDutProcss` d ON a.Id  = d.`unid` \n"
				       +" left JOIN (" 
				       +"SELECT DISTINCT a.DBID_ AS taskId,b.ID_, a.EXECUTION_ID_,b.ACTIVITYNAME_ AS auditStatus, \n" 
					   +"GROUP_CONCAT(c.userID_ ) AS auditUserId  \n"
					   +"FROM jbpm4_task a   \n"
					   +"INNER JOIN jbpm4_execution b ON a.EXECUTION_ID_ = b.ID_   \n"
					   +"LEFT JOIN  jbpm4_participation c ON a.DBID_ = c.TASK_  AND c.type_ = 'candidate'  \n"
					   +" where 1=1 " 
					  // +" AND "+userSession.getUserId()+" like concat(c.userID_,'%') "
					   +" GROUP BY a.EXECUTION_ID_  \n"
				       +") e on e.ID_  = d.`ProcessInstanceId` "
				        +" where 1=1 ${userName} ${leaveTypeId} ${department} ${educationl}";
			
			pp.setTableID("reportDutAuditList");
			pp.setCustomerId(""); 
			pp.setWhichFieldIsValue(1);
			pp.setInputType("radio");
			pp.setOrderBy_CH("entrytime");
			pp.setDirection("desc");
			
			pp.setPrintEnable(true);
			pp.setPrintVerTical(false);
			pp.setPrintTitle("入职审批");
			
//			pp.setColumnWidth("10,10,10,13,13,8,10,10,10,13");
			pp.setSQL(sql);
			
			pp.addColumn("申请入职人员", "name");
			pp.addColumn("申请职位", "Rank");
			pp.addColumn("申请部门", "departmentName");
			pp.addColumn("婚姻状况", "marriage");
			pp.addColumn("性别", "Sex");
			pp.addColumn("学历", "Educational");
			pp.addColumn("手机", "mobilePhone");
			pp.addColumn("专业", "profession");
			pp.addColumn("户口性质", "category");
			pp.addColumn("政治面貌", "politics");
			pp.addColumn("入所时间", "entrytime");
			
			pp.addSqlWhere("userName"," and a.Name like '%${userName}%' ");
			pp.addSqlWhere("leaveTypeId"," and a.rank like '%${leaveTypeId}%' ");
			pp.addSqlWhere("department"," and a.departmentid like '%${department}%' ");
			pp.addSqlWhere("educationl"," and a.educationl like '%${educationl}%' ");
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{			
			request.getSession().setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);
		}
		return modelAndView;
	}

	/**
	 * 入职审批跳转
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView auditSkip(HttpServletRequest request, HttpServletResponse response){
		
		ModelAndView modelAndView = new ModelAndView(reportDutAudit);
		ASFuntion asf = new ASFuntion();
		Connection conn = null;
		try {
			
			conn = new DBConnect().getConnect("");
			
			JobService jobService = new JobService(conn);
			ProcessFormService pfs = new ProcessFormService(conn);
			
			String  taskId = asf.showNull(request.getParameter("taskId"));
			
			if(!"".equals(taskId)){
				
				String pdid = jbpmTemplate.getProcessInstanceId(taskId);
				
				String sql = "select unid from j_reportDutProcss where ProcessInstanceId='"+pdid+"'"; 
				String autoId = jobService.getValueBySql(sql);
				String activityName = jbpmTemplate.getActivityName(taskId);
				
				if(!"".equals(autoId)){
					
					modelAndView.addObject("taskId",taskId);
					modelAndView.addObject("autoId",autoId);
					modelAndView.addObject("curstate",activityName);

					List nodeList = pfs.getNodeList(pdid);
					modelAndView.addObject("nodeList",nodeList);
					
						
					StaffRegisterService ss = new StaffRegisterService(conn);
					StaffRegister staffRegister = new StaffRegister();
					staffRegister=ss.getStaffRegister(autoId);
					
					List<StaffJobIntro> staffJobIntro = new ArrayList<StaffJobIntro>();
					staffJobIntro=ss.getStaffJobIntroList(autoId);
					
					StaffLiaison staffLiaison = new StaffLiaison();
					staffLiaison = ss.getStaffLiaison(autoId);
					
					List<StaffLiaisonFamily> staffLiaisonFamily = new ArrayList<StaffLiaisonFamily>();
					staffLiaisonFamily = ss.getStaffLiaisonFamily(autoId);
					
					List<StaffPost> staffPost = new ArrayList<StaffPost>();
					staffPost = ss.getStaffPost(autoId);
					
					List<StaffPractice> staffPractice = new ArrayList<StaffPractice>();
					staffPractice = ss.getStaffPractice(autoId);
					
					StaffSocialseCurity staffSocialseCurity = new StaffSocialseCurity();
					staffSocialseCurity = ss.getStaffSocialseCurity(autoId);
					
					List<StaffSocialseCurityHp> staffSocialseCurityHp = new ArrayList<StaffSocialseCurityHp>();
					staffSocialseCurityHp = ss.getStaffSocialseCurityHp(autoId);
					
					modelAndView.addObject("staffRegister",staffRegister);
					modelAndView.addObject("staffJobIntro",staffJobIntro);
					modelAndView.addObject("staffLiaison",staffLiaison);
					modelAndView.addObject("staffLiaisonFamily",staffLiaisonFamily);
					modelAndView.addObject("staffPost",staffPost);
					modelAndView.addObject("staffPractice",staffPractice);
					modelAndView.addObject("staffSocialseCurity",staffSocialseCurity);
					modelAndView.addObject("staffSocialseCurityHp",staffSocialseCurityHp);
					 
					
				}
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		
		return modelAndView;
	}
	
	/**
	 * 离职审批 完成
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView auditAgree(HttpServletRequest request, HttpServletResponse response){
		
		
		ASFuntion asf = new ASFuntion();
		Connection conn = null;
		try {
			
			conn = new DBConnect().getConnect("");
			response.setCharacterEncoding("UTF-8");
			request.setCharacterEncoding("UTF-8");
			UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
			Map<String, String> startMap = new HashMap<String, String>();
			
			LeaveService leaveService = new LeaveService(conn);
			String  unid = asf.showNull(request.getParameter("unid"));
			String taskId = asf.showNull(request.getParameter("taskId"));
			
			String trialRank = asf.showNull(request.getParameter("trialRank"));
			String fullRank = asf.showNull(request.getParameter("fullRank"));
			String timeDate = asf.showNull(request.getParameter("timeDate"));
			
			String dataComplete = asf.showNull(request.getParameter("dataComplete"));
			String signContract = asf.showNull(request.getParameter("signContract"));
			String contractExpire = asf.showNull(request.getParameter("contractExpire"));
			String rid = asf.showNull(request.getParameter("rid"));
			
			
			if(!"".equals(taskId) ){
				
				String sqlSet = "";
				
				if(!"".equals(trialRank)){
					sqlSet = "trialRank = '"+trialRank+"',";
				}
				if(!"".equals(trialRank)){
					sqlSet = "rid = '"+rid+"',";
				}
				
				if(!"".equals(fullRank)){
					sqlSet += "fullRank = '"+fullRank+"',";
				}
				
				if(!"".equals(timeDate)){
					sqlSet += "timeDate = '"+timeDate+"'";
				}
				
				if(!"".equals(signContract)){
					sqlSet += "dataComplete = '"+dataComplete+"',";
				}
				
				if(!"".equals(signContract)){
					sqlSet += "signContract = '"+signContract+"',";
				}
				
				if(!"".equals(contractExpire)){
					sqlSet += "contractExpire = '"+contractExpire+"'";
				}
				String sql = "";
				if(!"".equals(sqlSet)){
					
					sql =  " update k_resume set "+sqlSet+" where unid='"+unid+"'";
					leaveService.UpdateValueBySql(sql); //修改简历信息
				}
				ProcessFormService processFormService = new ProcessFormService(conn);
				JobService jobService = new JobService(conn);
				
				String pdid = jbpmTemplate.getProcessInstanceId(taskId);
				String activeName = jbpmTemplate.getActiveName(pdid);
				
				ProcessForm processForm = new ProcessForm();
				processForm.setProcessInstanseId(pdid);
				processForm.setKey("审批状态");
				processForm.setValue("已通过");
				processForm.setNodeName(activeName);
				processForm.setDealUserId(userSession.getUserId());
				processForm.setDealTime(new ASFuntion().getCurrentDate()+" "+new ASFuntion().getCurrentTime());
				processForm.setProperty("");
				processFormService.add(processForm);// 添加记录(流程表)
				
				
				// 根据节点 ID 把下个节点的执行人 传到下一个节点
				//jbpmTemplate.setTaskVariables(taskId, startMap);
				
				//jbpmTemplate.completeTask(taskId); //完成节点
				
				//修改 简历审批状态
				sql = "UPDATE `j_reportDutProcss` SET `state` = '"+activeName+"' WHERE `unid` = '"+unid+"'"; 
				leaveService.UpdateValueBySql(sql); 
				
				
				if("人事部审阅资料".equals(activeName) ){
					
					//简历库状态 修改为 录取
					sql = "UPDATE `k_resume` SET `state` = '录取' WHERE `unid` = '"+unid+"'"; 
					leaveService.UpdateValueBySql(sql); 
					
					User user = new User();
					UserService userService = new UserService(conn);
					Map edit = new HashMap();
					 
					if(!"".equals(unid)){
						
						edit = jobService.get("k_resume", "unid", unid);
					}
					
					String name = (String)edit.get("name");
					String loginId ="";
					if(!"".equals(name)){
						loginId = PinyingUtil.stringArrayToString(PinyingUtil.getHeadByString(name));
						//检查登录名是否存在
						sql="SELECT  loginid FROM k_user WHERE loginid ='"+loginId+"'";
						String loginId2 = leaveService.getValueBySql(sql); 
						
						if(!"".equals(loginId2)){
							
							loginId = "";
							//使用全拼音
							String[] loginIds = PinyingUtil.stringToPinyin(name);
							for (int i = 0; i < loginIds.length; i++) {
								loginId+=loginIds[i];
							}
							
							sql="SELECT  loginid FROM k_user WHERE loginid ='"+loginId+"'";
							loginId2 = leaveService.getValueBySql(sql); 
							
							if(!"".equals(loginId2)){
								loginId = getAZ()+loginId2;
							} 
							
							user.setLoginid(loginId);
						}else{
							
							user.setLoginid(loginId);
						}
						
					}
					
					//新增 用户
					user.setName(name);
					user.setBorndate((String)edit.get("borndate"));
					user.setEmail((String)edit.get("email"));
					user.setPassword(loginId);
					user.setSex((String)edit.get("sex"));
					user.setEducational((String)edit.get("educational"));
					user.setDiploma((String)edit.get("diploma"));
					user.setDepartmentid((String)edit.get("departmentid")); //待补
					user.setRank((String)edit.get("trialrank"));
					user.setSpecialty((String)edit.get("specialty"));
					user.setMobilePhone((String)edit.get("mobilephone"));
					user.setCpano((String)edit.get("cpano"));
					user.setRoles((String)edit.get("rid"));
					userService.addUser(user);
					
					sql = " update k_user set password=md5('"+loginId+"') where loginid='"+loginId+"' LIMIT 1 ";
					new DbUtil(conn).executeUpdate(sql); //修改密码
					
					sql="SELECT  mobilePhone FROM k_resume WHERE unid ='"+unid+"'";
					String mobilePhone = leaveService.getValueBySql(sql); 
					if("".equals(mobilePhone)){
						
						if(mobilePhone.length() ==11 ){
							sql="SELECT  departname FROM k_resume a" +
								" left join k_department b on a.departmentid = b.autoid"+
								" WHERE name ='"+name+"'";
							String departname = asf.showNull(leaveService.getValueBySql(sql));  //应聘部门
								
							String msg = "您的简历已通过审核。请您尽快到“人力资源部”填写相关资料 进行报到。" +
										  "大华ERP登录名为："+loginId+" 密码为："+loginId+ "。"+
										  "公司网址是：http://www.bdo-lxdh.com。" +
										  "公司总部地址：北京海淀区西四环中路十六号院7号楼12层";
							SmsOpt.sendSMS(mobilePhone, msg);
						}
					}
					
					
					if("是".equals(signContract)){
						//添加劳动合同
						LaborBargainService ls = new LaborBargainService(conn);
						LaborBargain laborbargain = new LaborBargain() ;
						
						laborbargain.setEndorsedate((String)edit.get("submitDate")) ;
						laborbargain.setIneffecttime((String)edit.get("contractExpire")) ;
						laborbargain.setBargainID(DELUnid.getNumUnid()) ;
						//laborbargain.setOther((String)edit.get("other")) ;
						
						sql="SELECT baseSalary FROM k_rank WHERE `name` ='"+(String)edit.get("trialrank")+"'";
						String baseSalary = asf.showNull(leaveService.getValueBySql(sql)); 
						laborbargain.setEmolument(baseSalary) ;
						
						//附件
						//laborbargain.setFileNames(myfileUpload.getFileNames()) ;
						//laborbargain.setFileRondomNames(myfileUpload.getFileRondomNames()) ;
						sql="SELECT  id FROM k_user WHERE loginid ='"+loginId+"'";
						String userid = leaveService.getValueBySql(sql); 
						laborbargain.setUserid(userid);
						
						laborbargain.setCheckinperson(name);
						ls.addlabor(laborbargain);
					}
				}
				
			}
			response.sendRedirect(request.getContextPath()+"/job.do?method=reportDutAuditList");
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		return null;
	}
	
	public ModelAndView newAuditAgree(HttpServletRequest request, HttpServletResponse response){
		
		
		ASFuntion asf = new ASFuntion();
		Connection conn = null;
		try {
			
			conn = new DBConnect().getConnect("");
			response.setCharacterEncoding("UTF-8");
			request.setCharacterEncoding("UTF-8");
			UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
			Map<String, String> startMap = new HashMap<String, String>();
			
			LeaveService leaveService = new LeaveService(conn);
			String autoId = asf.showNull(request.getParameter("autoId")); //主键ID，用来更新状态的(自己传过来)
			String taskId = asf.showNull(request.getParameter("taskId")); //任务ID，用来结束流程的
			
			
			
			if(!"".equals(taskId) ){
				
				JobService jobService = new JobService(conn);
				
				String pdid = jbpmTemplate.getProcessInstanceId(taskId);
				String activeName = jbpmTemplate.getActiveName(pdid);
				
				//1:结束流程
				jbpmTemplate.completeTask(taskId); //完成节点
				
				if("人事部审阅资料".equals(activeName)){
					
					//2:更新入职表状态，改为，已通过
					String sql = "";
					if(!"".equals(autoId)){
						
						sql =  " update k_staffregister set state = '已通过' where autoId='"+autoId+"'";
						leaveService.UpdateValueBySql(sql); //修改简历信息
					}
					
					//3:插入人员表
					
					/*User user = new User();
					UserService userService = new UserService(conn);
					Map edit = new HashMap();
					 
					if(!"".equals(autoId)){
						
						//得到所有人员入职信息(自己重新写)
						edit = jobService.get("k_resume", "unid", unid);
					}
					
					String name = (String)edit.get("name");
					String loginId ="";
					if(!"".equals(name)){
						loginId = PinyingUtil.stringArrayToString(PinyingUtil.getHeadByString(name));
						//检查登录名是否存在
						sql="SELECT  loginid FROM k_user WHERE loginid ='"+loginId+"'";
						String loginId2 = leaveService.getValueBySql(sql); 
						
						if(!"".equals(loginId2)){
							
							loginId = "";
							//使用全拼音
							String[] loginIds = PinyingUtil.stringToPinyin(name);
							for (int i = 0; i < loginIds.length; i++) {
								loginId+=loginIds[i];
							}
							
							sql="SELECT  loginid FROM k_user WHERE loginid ='"+loginId+"'";
							loginId2 = leaveService.getValueBySql(sql); 
							
							if(!"".equals(loginId2)){
								loginId = getAZ()+loginId2;
							} 
							
							user.setLoginid(loginId);
						}else{
							
							user.setLoginid(loginId);
						}
						
					}
					
					//新增 用户
					user.setName(name);
					user.setBorndate((String)edit.get("borndate"));
					user.setEmail((String)edit.get("email"));
					user.setPassword(loginId);
					user.setSex((String)edit.get("sex"));
					user.setEducational((String)edit.get("educational"));
					user.setDiploma((String)edit.get("diploma"));
					user.setDepartmentid((String)edit.get("departmentid")); //待补
					user.setRank((String)edit.get("trialrank"));
					user.setSpecialty((String)edit.get("specialty"));
					user.setMobilePhone((String)edit.get("mobilephone"));
					user.setCpano((String)edit.get("cpano"));
					user.setRoles((String)edit.get("rid"));
					userService.addUser(user);
					
					sql = " update k_user set password=md5('"+loginId+"') where loginid='"+loginId+"' LIMIT 1 ";
					new DbUtil(conn).executeUpdate(sql); //修改密码
					
					sql="SELECT  mobilePhone FROM k_resume WHERE unid ='"+unid+"'";
					String mobilePhone = leaveService.getValueBySql(sql); 
					if("".equals(mobilePhone)){
						
						if(mobilePhone.length() ==11 ){
							sql="SELECT  departname FROM k_resume a" +
								" left join k_department b on a.departmentid = b.autoid"+
								" WHERE name ='"+name+"'";
							String departname = asf.showNull(leaveService.getValueBySql(sql));  //应聘部门
								
							String msg = "您的简历已通过审核。请您尽快到“人力资源部”填写相关资料 进行报到。" +
										  "大华ERP登录名为："+loginId+" 密码为："+loginId+ "。"+
										  "公司网址是：http://www.bdo-lxdh.com。" +
										  "公司总部地址：北京海淀区西四环中路十六号院7号楼12层";
							SmsOpt.sendSMS(mobilePhone, msg);
						}
					}
					
					
					if("是".equals(signContract)){
						//添加劳动合同
						LaborBargainService ls = new LaborBargainService(conn);
						LaborBargain laborbargain = new LaborBargain() ;
						
						laborbargain.setEndorsedate((String)edit.get("submitDate")) ;
						laborbargain.setIneffecttime((String)edit.get("contractExpire")) ;
						laborbargain.setBargainID(DELUnid.getNumUnid()) ;
						//laborbargain.setOther((String)edit.get("other")) ;
						
						sql="SELECT baseSalary FROM k_rank WHERE `name` ='"+(String)edit.get("trialrank")+"'";
						String baseSalary = asf.showNull(leaveService.getValueBySql(sql)); 
						laborbargain.setEmolument(baseSalary) ;
						
						//附件
						//laborbargain.setFileNames(myfileUpload.getFileNames()) ;
						//laborbargain.setFileRondomNames(myfileUpload.getFileRondomNames()) ;
						sql="SELECT  id FROM k_user WHERE loginid ='"+loginId+"'";
						String userid = leaveService.getValueBySql(sql); 
						laborbargain.setUserid(userid);
						
						laborbargain.setCheckinperson(name);
						ls.addlabor(laborbargain);
					}*/
					
					String userName = asf.showNull(request.getParameter("name"));
					String loginId ="";
					if(!"".equals(userName)){
						loginId = PinyingUtil.stringArrayToString(PinyingUtil.getHeadByString(userName));
						//检查登录名是否存在
						sql="SELECT  loginid FROM k_user WHERE loginid ='"+loginId+"'";
						String loginId2 = leaveService.getValueBySql(sql); 
						
						if(!"".equals(loginId2)){
							
							loginId = "";
							//使用全拼音
							String[] loginIds = PinyingUtil.stringToPinyin(userName);
							for (int i = 0; i < loginIds.length; i++) {
								loginId+=loginIds[i];
							}
							
							sql="SELECT  loginid FROM k_user WHERE loginid ='"+loginId+"'";
							loginId2 = leaveService.getValueBySql(sql); 
							
							if(!"".equals(loginId2)){
								loginId = getAZ()+loginId2;
							} 
						}
					}
					jobService.insertToUser(autoId, loginId);
				}
			}
			response.sendRedirect(request.getContextPath()+"/job.do?method=reportDutAuditList");
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		return null;
	}
	
	/**
	 * 随机产生a-z
	 * @param count
	 * @return
	 */
	public static String getAZ(){
		char ch = 0;
		for (int i = 0; i < 28; i++) {
			
			ch = (char)(Math.random()*26 + 'a');
		}
		System.out.println(ch);
		
		return ch+"";
	}
	 public static void main(String[] args) {
	}
	 
	/**
	 * 保存 员工信息
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView addStaffRegister(HttpServletRequest request, HttpServletResponse response){
		Connection conn = null;
		try{
			 ASFuntion asf = new ASFuntion(); 
			 String id = asf.showNull(request.getParameter("id"));
			 request.setCharacterEncoding("utf-8");
			 response.setCharacterEncoding("utf-8");
			 response.setContentType("text/html;charset=utf-8") ;
			 //得到信息
			 StaffRegister sr = this.getStaffRegister(request, response);  // 员工报到信息
			 StaffSocialseCurity ssc = this.getStaffSocialseCurity(request, response); // 得到  员工报到(社保信息表)
			 List<StaffSocialseCurityHp> sschList = this.getStaffSocialseCurityHp(request, response); //得到 社保医疗信息
			 List<StaffPractice> spList = this.getStaffPractice(request, response); //得到  员工报到( 执 业 信 息)
			 List<StaffPost> sfList = this.getStaffPost(request, response); //得到  员工报到(执业信息 职称表)
			 StaffLiaison sl = this.getStaffLiaison(request, response); // 得到 员工报到(员工联络卡)
			 List<StaffLiaisonFamily> slfList = this.getStaffLiaisonFamily(request, response); //得到 员工报到(员工亲人联系方式)
			 List<StaffJobIntro> sjiList = this.getStaffJobIntro(request, response); //  得到 员工报到(学习、工作简历介绍)
			 
			 //插入表
			 conn = new DBConnect().getConnect("");
			 
			 String linkUserId = asf.showNull(request.getParameter("linkUserId"));
			 EmploymentService es = new EmploymentService(conn);
			 es.updateSubmitDate(linkUserId);
			 
			 StaffRegisterService srs = new StaffRegisterService(conn);
			 
			if("".equals(id)){
				 id = srs.addStaffRegister(sr);
			 }else {
				 srs.updteStaffRegister(sr);
			 }
			 if(!"".equals(id)){
				 	try {
				 		
				 		srs.del("k_staffjobintro", id);
				 		srs.del("k_staffliaisonfamily", id);
				 		srs.del("k_staffpost", id);
				 		srs.del("k_staffpractice", id);
				 		srs.del("k_staffsocialsecurityhp", id);
				 		srs.del("k_staffliaison", id);
				 		srs.del("k_staffsocialsecurity", id);
				 		
					} catch (Exception e) {
						 e.getStackTrace();
					}
					
					 
				 	 ssc.setId(id);
					 srs.addStaffSocialseCurity(ssc);
					 
					 sl.setId(id);
					 srs.addStaffLiaison(sl);
					 
					 for (int i = 0; i < sschList.size(); i++) {
						 StaffSocialseCurityHp ssch = sschList.get(i);
						 ssch.setId(id);
						 srs.addStaffSocialseCurityHp(ssch);
					 }
					 
					 for (int i = 0; i < spList.size(); i++) {
						 StaffPractice sp = spList.get(i);
						 sp.setId(id);
						 srs.addStaffPractice(sp);
					 }
					 
					 for (int i = 0; i < sfList.size(); i++) {
						 StaffPost sp = sfList.get(i);
						 sp.setId(id);
						 srs.addStaffPost(sp);
					 }
					 
					 for (int i = 0; i < slfList.size(); i++) {
						 StaffLiaisonFamily sp = slfList.get(i);
						 sp.setId(id);
						 srs.addStaffLiaisonFamily(sp);
					 }
					 
					 for (int i = 0; i < sjiList.size(); i++) {
						 StaffJobIntro sji = sjiList.get(i);
						 sji.setId(id);
						 srs.addStaffJobIntro(sji);
					 }
			 }
			 String autoId = srs.getId(sr.getName(), sr.getIdentityCard());
			 this.startFlow(request, response, autoId);
			response.getWriter().print("<script> alert('保存成功!');window.close();</script>");
		 }catch (Exception e) {
			e.getStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		 return null;
	 }
	 
	 /**
	  * 得到  员工报到信息
	 * @param request
	 * @param response
	 * @return
	 */
	public StaffRegister getStaffRegister(HttpServletRequest request, HttpServletResponse response){
		 
		 StaffRegister sr = new StaffRegister();
		 try{
			 
				 ASFuntion  asf = new ASFuntion();
				 
				 String createDate = asf.getCurrentDate()+" "+asf.getCurrentTime();
				 
				 String name = asf.showNull(request.getParameter("name"));
				 String educational = asf.showNull(request.getParameter("educational")); //文化程度
				 String bornDate = asf.showNull(request.getParameter("bornDate"));//出生年月
				 String marriage = asf.showNull(request.getParameter("marriage"));//婚姻状态
				 String sex = asf.showNull(request.getParameter("sex"));
				 String category = asf.showNull(request.getParameter("category")); //户口性质
				 String profession = asf.showNull(request.getParameter("profession")); //专业
				 String politics = asf.showNull(request.getParameter("politics")); //面貌
				 String nation = asf.showNull(request.getParameter("nation")); //民族
				 String mobilePhone = asf.showNull(request.getParameter("mobilePhone")); //移动电话
				 String diploma = asf.showNull(request.getParameter("diploma")); // 毕业学校
				 String place = asf.showNull(request.getParameter("place")); // 籍贯
				 String entrytime = asf.showNull(request.getParameter("entrytime")); //入所时间
				 String identityCard = asf.showNull(request.getParameter("identityCard")); //身份证
				 String employWay = asf.showNull(request.getParameter("employWay")); //应聘途径
				 String referrer = asf.showNull(request.getParameter("referrer")); //推荐人
				 String contactWay = asf.showNull(request.getParameter("contactWay")); //推荐人联系方式
				 String cpa = asf.showNull(request.getParameter("cpa")); //是否参加全国CPA考试
				 String examSubject = asf.showNull(request.getParameter("examSubject")); //通过考试科目
				 String departmentId = asf.showNull(request.getParameter("departmentId")); //应聘部门
				 String rank = asf.showNull(request.getParameter("rank")); //应聘职位
				 
				 sr.setName(name);
				 sr.setEducational(educational);
				 sr.setBornDate(bornDate);
				 sr.setMarriage(marriage);
				 sr.setSex(sex);
				 sr.setCategory(category);
				 sr.setProfession(profession);
				 sr.setPolitics(politics);
				 sr.setNation(nation);
				 sr.setMobilePhone(mobilePhone);
				 sr.setDiploma(diploma);
				 sr.setPlace(place);
				 sr.setEntrytime(entrytime);
				 sr.setIdentityCard(identityCard);
				 sr.setEmployWay(employWay);
				 sr.setReferrer(referrer);
				 sr.setContactWay(contactWay);
				 sr.setCpa(cpa);
				 sr.setExamSubject(examSubject);
				 sr.setState("审核中");//明明设计
				 
				 sr.setCreateDate(createDate);
				 sr.setDepartmentid(departmentId);
				 sr.setRank(rank);
				 
				 try{
					 
					 UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
					 sr.setCreateDepartment(userSession.getUserAuditDepartmentId());
					 sr.setCreateUser(userSession.getUserId());
				
				 }catch(Exception e){
					 e.getStackTrace();
				 }
				 
		 }catch(Exception e){
			 e.getStackTrace();
		 }finally{
			 
		 }
		 return sr;
	 }
	
	/**
	 * 得到  员工报到(社保信息表)
	 * @param request
	 * @param response
	 * @return
	 */
	public StaffSocialseCurity getStaffSocialseCurity(HttpServletRequest request, HttpServletResponse response){
		 
		StaffSocialseCurity ssc = new StaffSocialseCurity();
		 try{
			 
				 ASFuntion  asf = new ASFuntion();
				 
				 String createDate = asf.getCurrentDate()+" "+asf.getCurrentTime();
				 
				 String paySort = asf.showNull(request.getParameter("paySort"));//缴费人员类别
				 String insuredSort = asf.showNull(request.getParameter("insuredSort")); //参保类别
				 String firstJobTime = asf.showNull(request.getParameter("firstJobTime"));//首次参加工作日期
				 String baseNumber = asf.showNull(request.getParameter("baseNumber"));//上一单位医疗基数
				 String residencePermit = asf.showNull(request.getParameter("residencePermit"));//是否有工作居住证
				 String property = asf.showNull(request.getParameter("property")); //备用
				 
				 ssc.setPaySort(paySort);
				 ssc.setInsuredSort(insuredSort);
				 ssc.setFirstJobTime(firstJobTime);
				 ssc.setBaseNumber(baseNumber);
				 ssc.setResidencePermit(residencePermit);
				 ssc.setProperty(property);
				 
				 ssc.setCreateDate(createDate);
				 
				 try{
					 
					 UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
					 ssc.setCreateDepartment(userSession.getUserAuditDepartmentId());
					 ssc.setCreateUser(userSession.getUserId());
				
				 }catch(Exception e){
					 e.getStackTrace();
				 }
				 
		 }catch(Exception e){
			 e.getStackTrace();
		 }finally{
			 
		 }
		 return ssc;
	 }
	
	/**
	 * 得到 社保医疗信息 
	 * @param request
	 * @param response
	 * @return
	 */
	public List<StaffSocialseCurityHp> getStaffSocialseCurityHp(HttpServletRequest request, HttpServletResponse response){
		
		List<StaffSocialseCurityHp> sschList = new ArrayList<StaffSocialseCurityHp>();
		
		 try{
				 ASFuntion  asf = new ASFuntion();
				 
				 String createDate = asf.getCurrentDate()+" "+asf.getCurrentTime();
				 
				 String[] organizationNames = request.getParameterValues("organizationNames");//L医疗定点机构名称
				 String[] coding = request.getParameterValues("coding"); //编码
				 String[] hospitalName = request.getParameterValues("hospitalName");//医院名称
				 String stsoseId = asf.showNull(request.getParameter("stsoseId")); //社保信息外键
				 //String[] property = request.getParameterValues("property"); //备用
				 
				 if(organizationNames !=null){
					 
					 for (int i = 0; i < organizationNames.length; i++) {
						 
						 StaffSocialseCurityHp ssch = new StaffSocialseCurityHp();
						 
						 ssch.setStsoseId(stsoseId);
						 
						 ssch.setCreateDate(createDate);
						 
						 if(organizationNames[i] != null){
							 ssch.setOrganizationNames(organizationNames[i]);
						 }
						 if(coding[i] != null){
							 ssch.setCoding(coding[i]);
						 }
						 if(hospitalName[i] != null){
							 ssch.setHospitalName(hospitalName[i]);
						 }
						 /*if(property[i] != null){
							 ssch.setProperty(property[i]);
						 }*/
						 try{
							 
							 UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
							 ssch.setCreateDepartment(userSession.getUserAuditDepartmentId());
							 ssch.setCreateUser(userSession.getUserId());
						
						 }catch(Exception e){
							 e.getStackTrace();
						 }
						 sschList.add(ssch);
					 }
					
				 }
				 
		 }catch(Exception e){
			 e.getStackTrace();
		 }finally{
			 
		 }
		 return sschList;
	 }
	
	/**
	 * 得到  员工报到( 执 业 信 息)
	 * @param request
	 * @param response
	 * @return
	 */
	public List<StaffPractice> getStaffPractice(HttpServletRequest request, HttpServletResponse response){
		 
		List<StaffPractice> spList = new ArrayList<StaffPractice>();
		
		 try{
				 ASFuntion  asf = new ASFuntion();
				 
				 String createDate = asf.getCurrentDate()+" "+asf.getCurrentTime();
				 
				 String[] spname = request.getParameterValues("spname");//资质名称
				 String[] rank = request.getParameterValues("rank"); //资质等级
				 String[] cNumber = request.getParameterValues("cNumber");//证书编号
				 String[] ratifyOrgan = request.getParameterValues("ratifyOrgan");//认可机关
				 String[] yearMax = request.getParameterValues("yearMax");//批准年限
				 String[] referenceNumber = request.getParameterValues("referenceNumber");//批准文号
				 String[] qualifiedCertificate = request.getParameterValues("qualifiedCertificate");//合格证号
				 String[] property = request.getParameterValues("propertySp"); //备用
				 
				 if(spname !=null){
					 
					 for (int i = 0; i < spname.length; i++) {
						 
						 StaffPractice sp = new StaffPractice();
						 
						 sp.setCreateDate(createDate);
						 
						 if(spname[i] != null){
							 sp.setSpname(spname[i]);
						 }
						 if(rank[i] != null){
							 sp.setRank(rank[i]);
						 }
						 if(cNumber[i] != null){
							 sp.setcNumber(cNumber[i]);
						 }
						 if(ratifyOrgan[i] != null){
							 sp.setRatifyOrgan(ratifyOrgan[i]);
						 }
						 
						 if(yearMax[i] != null){
							 sp.setYearMax(yearMax[i]);
						 }
						 if(referenceNumber[i] != null){
							 sp.setReferenceNumber(referenceNumber[i]);
						 }
						 if(qualifiedCertificate[i] != null){
							 sp.setQualifiedCertificate(qualifiedCertificate[i]);
						 }
						 if(property[i] != null){
							 sp.setProperty(property[i]);
						 }
						 try{
							 
							 UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
							 sp.setCreateDepartment(userSession.getUserAuditDepartmentId());
							 sp.setCreateUser(userSession.getUserId());
						
						 }catch(Exception e){
							 e.getStackTrace();
						 }
						 spList.add(sp);
					 }
					
				 }
				 
		 }catch(Exception e){
			 e.getStackTrace();
		 }finally{
			 
		 }
		 return spList;
	 }
	
	/**
	 * 得到  员工报到(执业信息 职称表)
	 * @author Ymm
	 * k_staffpost
	 */
	public List<StaffPost> getStaffPost(HttpServletRequest request, HttpServletResponse response){
		 
		List<StaffPost> spList = new ArrayList<StaffPost>();
		
		 try{
				 ASFuntion  asf = new ASFuntion();
				 
				 String createDate = asf.getCurrentDate()+" "+asf.getCurrentTime();
				 
				 String[] series = request.getParameterValues("series");//职称系列
				 String[] rankName = request.getParameterValues("rankName"); //职称
				 String[] rankGrade = request.getParameterValues("rankGrade");//职称等级
				 String[] getDate = request.getParameterValues("getDate");//获取时间

				 String[] property = request.getParameterValues("propertySf"); //备用
				 
				 if(rankName !=null){
					 
					
					 for (int i = 0; i < rankName.length; i++) {

						 StaffPost sp = new StaffPost();
						 
						 sp.setCreateDate(createDate);
						 
						 
						 if(rankName[i] != null){
							 sp.setRankName(rankName[i]);
						 }
						 if(series[i] != null){
							 sp.setSeries(series[i]);
						 }
						 if(rankGrade[i] != null){
							 sp.setRankGrade(rankGrade[i]);
						 }
						 if(getDate[i] != null){
							 sp.setGetDate(getDate[i]);
						 }
						 
						 if(property[i] != null){
							 sp.setProperty(property[i]);
						 }
						 
						 try{
							 
							 UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
							 sp.setCreateDepartment(userSession.getUserAuditDepartmentId());
							 sp.setCreateUser(userSession.getUserId());
						
						 }catch(Exception e){
							 e.getStackTrace();
						 }
						 spList.add(sp);
					 }
					
				 }
				 
		 }catch(Exception e){
			 e.getStackTrace();
		 }finally{
			 
		 }
		 return spList;
	 }

	/**
	 * 得到 员工报到(员工联络卡)
	 * @param request
	 * @param response
	 * @return
	 */
	public StaffLiaison getStaffLiaison(HttpServletRequest request, HttpServletResponse response){
		 
		StaffLiaison sl = new StaffLiaison();
		 try{
			 
				 ASFuntion  asf = new ASFuntion();
				 
				 String createDate = asf.getCurrentDate()+" "+asf.getCurrentTime();
				 
				 String residence = asf.showNull(request.getParameter("residence"));//户籍所在地
				 String policeSubstation = asf.showNull(request.getParameter("policeSubstation")); //所辖派出所
				 String homeAddress = asf.showNull(request.getParameter("homeAddress"));//家庭住址
				 String homePostcode = asf.showNull(request.getParameter("homePostcode"));//家庭住址的邮编
				 String mailAddress = asf.showNull(request.getParameter("mailAddress"));//邮寄地址
				 String mailPostcode = asf.showNull(request.getParameter("mailPostcode"));//邮寄的邮编
				 String homeTel = asf.showNull(request.getParameter("homeTel"));//家庭电话
				 String urgencyTel = asf.showNull(request.getParameter("urgencyTel"));//紧急电话
				 String msn = asf.showNull(request.getParameter("msn"));//是否有工作居住证
				 String qq = asf.showNull(request.getParameter("qq"));//是否有工作居住证
				 String archivesPlace = asf.showNull(request.getParameter("archivesPlace"));//档案存放地点
				 String archivesId = asf.showNull(request.getParameter("archivesId"));//存档编号
				 String property = asf.showNull(request.getParameter("property")); //备用
				 
				 sl.setResidence(residence);
				 sl.setPoliceSubstation(policeSubstation);
				 sl.setHomeAddress(homeAddress);
				 sl.setHomePostcode(homePostcode);
				 sl.setMailAddress(mailAddress);
				 sl.setMailPostcode(mailPostcode);
				 sl.setHomeTel(homeTel);
				 sl.setUrgencyTel(urgencyTel);
				 sl.setMsn(msn);
				 sl.setQq(qq);
				 sl.setArchivesPlace(archivesPlace);
				 sl.setArchivesId(archivesId);

				 sl.setProperty(property);
				 
				 sl.setCreateDate(createDate);
				 
				 try{
					 
					 UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
					 sl.setCreateDepartment(userSession.getUserAuditDepartmentId());
					 sl.setCreateUser(userSession.getUserId());
				
				 }catch(Exception e){
					 e.getStackTrace();
				 }
				 
		 }catch(Exception e){
			 e.getStackTrace();
		 }finally{
			 
		 }
		 return sl;
	 }

	/**
	 * 得到 员工报到(员工亲人联系方式)
	 * @param request
	 * @param response
	 * @return
	 */
	public List<StaffLiaisonFamily> getStaffLiaisonFamily(HttpServletRequest request, HttpServletResponse response){
		 
		List<StaffLiaisonFamily> slfList = new ArrayList<StaffLiaisonFamily>();
		
		 try{
			 
				 ASFuntion  asf = new ASFuntion();
				 
				 String createDate = asf.getCurrentDate()+" "+asf.getCurrentTime();
				 
				 String[] familyName = request.getParameterValues("familyName");//姓名
				 String[] relation = request.getParameterValues("relation"); //关系
				 String[] jobPlace = request.getParameterValues("jobPlace");//工作/学习单位
				 String[] tel = request.getParameterValues("telSlf");//电话
				 String[] identityCard = request.getParameterValues("identityCardSlf");//身份证
				 //String[] property = request.getParameterValues("property"); //备用
				 
				 if(familyName !=null){
					 
					 for (int i = 0; i < familyName.length; i++) {
						 
						 StaffLiaisonFamily slf = new StaffLiaisonFamily();
						 
						 slf.setCreateDate(createDate);
						 
						 if(familyName[i] != null){
							 slf.setFamilyName(familyName[i]);
						 }
						 if(relation[i] != null){
							 slf.setRelation(relation[i]);
						 }
						 if(jobPlace[i] != null){
							 slf.setJobPlace(jobPlace[i]);
						 }
						 if(tel[i] != null){
							 slf.setTel(tel[i]);
						 }
						 if(identityCard[i] != null){
							 slf.setIdentityCard(identityCard[i]);
						 }
						 if(familyName[i] != null){
							 slf.setFamilyName(familyName[i]);
						 }
						 
						/* if(property[i] != null){
							 slf.setProperty(property[i]);
						 }*/
						 try{
							 
							 UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
							 slf.setCreateDepartment(userSession.getUserAuditDepartmentId());
							 slf.setCreateUser(userSession.getUserId());
						
						 }catch(Exception e){
							 e.getStackTrace();
						 }
						 slfList.add(slf);
					 }
					
				 }
		 }catch(Exception e){
			 e.getStackTrace();
		 }finally{
			 
		 }
		 return slfList;
	 }
	
	/**
	 * 得到 员工报到(学习、工作简历介绍)
	 * @param request
	 * @param response
	 * @return
	 */
	public List<StaffJobIntro> getStaffJobIntro(HttpServletRequest request, HttpServletResponse response){
		 
		List<StaffJobIntro> sjiList = new ArrayList<StaffJobIntro>();
		
		 try{
				 ASFuntion  asf = new ASFuntion();
				 
				 String createDate = asf.getCurrentDate()+" "+asf.getCurrentTime();
				 
				 String[] startDate = request.getParameterValues("startDate");//开始时间
				 String[] endDate = request.getParameterValues("endDate"); //结束时间
				 String[] content = request.getParameterValues("content");//学习、工作单位及职位描述
				 String[] ctype = request.getParameterValues("ctype");//类型
				 //String[] property = request.getParameterValues("property"); //备用
				 
				 if(startDate !=null){
					 
					 
					 for (int i = 0; i < startDate.length; i++) {
						 
						 StaffJobIntro sji = new StaffJobIntro();
						 
						 sji.setCreateDate(createDate);
						 
						 if(startDate[i] != null){
							 sji.setStartDate(startDate[i]);
						 }
						 if(endDate[i] != null){
							 sji.setEndDate(endDate[i]);
						 }
						 if(content[i] != null){
							 sji.setContent(content[i]);
						 }
						 if(ctype[i] != null){
							 sji.setCtype(ctype[i]);
						 }
						 /*if(property[i] != null){
							 sji.setProperty(property[i]);
						 }*/
						 

						 try{
							 
							 UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
							 sji.setCreateDepartment(userSession.getUserAuditDepartmentId());
							 sji.setCreateUser(userSession.getUserId());
						
						 }catch(Exception e){
							 e.getStackTrace();
						 }
						 
						 sjiList.add(sji);
					 }
					
				 }
		 }catch(Exception e){
			 e.getStackTrace();
		 }finally{
			 
		 }
		 return sjiList;
	 }
	
	public ModelAndView jobView(HttpServletRequest request, HttpServletResponse response)   throws Exception {
		ModelAndView modelAndView = new ModelAndView("hr/layout/jobView.jsp");
		Connection conn=null;
		try {
			ASFuntion CHF = new ASFuntion();
			 
			UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
			
			String flag = CHF.showNull(request.getParameter("flag")); //区分查看的权限
			String unid = CHF.showNull(request.getParameter("unid")); //有就是修改，无就是新增 
			String table = CHF.showNull(request.getParameter("table")); //表名
			String who = CHF.showNull(request.getParameter("who"));
			//所属事务所
			
			
			conn= new DBConnect().getConnect("");
			
			JobService js = new JobService(conn);
			Map edit = new HashMap();
			if(!"".equals(unid)){
				edit = js.get(table, "unid", unid);
			}else{
				if("k_job".toLowerCase().equals(table.toLowerCase())){
					//部门招聘计划和岗位
					edit.put("departmentname", userSession.getUserAuditDepartmentName());
					edit.put("departmentid", userSession.getUserAuditDepartmentId());
					
					edit.put("areaid", userSession.getAreaid());
					edit.put("areaname", userSession.getAreaname());
					
					
					edit.put("state", "有效");
				}else if("k_resume".toLowerCase().equals(table.toLowerCase())){
					//简历
					edit.put("resumeid",js.getAutoCode(""));
					edit.put("state", "候选");
					edit.put("sex", "男");
					edit.put("attachname", "");
					edit.put("attachid", UUID.randomUUID().toString());
				}
			}
			
			if("k_job".toLowerCase().equals(table.toLowerCase())){
				//招聘
				modelAndView.setViewName("hr/layout/jobView.jsp");
			}else if("k_resume".toLowerCase().equals(table.toLowerCase())){
				//简历
				String attachid = CHF.showNull((String)edit.get("attachid"));
				edit.put("attachid", "".equals(attachid) ? UUID.randomUUID().toString() : attachid);
				modelAndView.setViewName(resumeEdit);
			}
			if(who == "role"){
				modelAndView.addObject("who", who);
			}else{
				modelAndView.addObject("who", who);
			}
			modelAndView.addObject("edit", edit);
			modelAndView.addObject("flag", flag);
			modelAndView.addObject("table", table);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}
		return modelAndView;
	}
	
	public ModelAndView resumeList(HttpServletRequest request,HttpServletResponse response) throws Exception{
		Connection conn=null;
		DbUtil dbUtil=null;
		WebUtil webUtil=new WebUtil(request, response);
		String unid=request.getParameter("unid");
		UserSession userSession=webUtil.getUserSession();
		int eff=0;
		String re="";
	    JobVO jobVO=null;
		response.setContentType(WebUtil.CONTENT_TYPE_UTF8);
		String urlPattern="formDefine.do?method=formListView&uuid={0}&jobname={1}",url="";
		try{
			conn=new DBConnect().getConnect();
			dbUtil=new DbUtil(conn);
			jobVO=dbUtil.load(JobVO.class, unid);
			if("0".equals(jobVO.getTypeid())){
				//8252d0e7-6f0d-4ae1-b34d-97327b6270d5
				url=MessageFormat.format(urlPattern, "8252d0e7-6f0d-4ae1-b34d-97327b6270d5",unid);
			}else{
				url=MessageFormat.format(urlPattern, "13916b9b-2f1b-47d6-9b00-66fddc927903",unid);
			}
				
		}catch(Exception ex){
			re=ex.getLocalizedMessage();
		}finally{
			DbUtil.close(conn);
		}
		//response.getWriter().write(re);
		response.sendRedirect(url);
		return null;
	}

	
}

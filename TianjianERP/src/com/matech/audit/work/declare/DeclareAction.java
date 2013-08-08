package com.matech.audit.work.declare;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.UUID;

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
import com.matech.audit.service.promises.PromisesVO;
import com.matech.audit.service.userpopedom.UserPopedomService;
import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.autocode.DELUnid;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;
import com.matech.framework.pub.util.StringUtil;
import com.matech.framework.pub.util.WebUtil;

public class DeclareAction extends MultiActionController {
	
	private static final String list =  "declare/list.jsp"; 
	private static final String project =  "declare/project.jsp";
	private static final String year =  "declare/year.jsp";
	private static final String declareList =  "declare/declareList.jsp";
	private static final String moralNormList = "moralNorm/list.jsp";
	private static final String moralNormYear = "moralNorm/year.jsp";
	private static final String moralityEdit = "moralNorm/moralityEdit.jsp";
	private static final String yearEdit = "moralNorm/yearEdit.jsp";
	private static final String promisesEdit = "moralNorm/promisesEdit.jsp";
	private static final String secrecyPromisesEdit = "moralNorm/secrecyPromisesEdit.jsp";
	//声明：年度独立、项目独立
	public ModelAndView list(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView(list);
		ASFuntion CHF = new ASFuntion();
		Connection conn = null;
		try {
			UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
			
			conn = new DBConnect().getConnect("");
			
			String userid = CHF.showNull(request.getParameter("userid")); 
			if("".equals(userid)) userid = userSession.getUserId();
			
			String flag = CHF.showNull(request.getParameter("flag")); //区分是年度还是项目
			String opt = CHF.showNull(request.getParameter("opt")); //区分是显示全部还是本人
			String menuid = CHF.showNull(request.getParameter("menuid")); //菜单ID
			
			String strSql = "";
			if("project".equals(flag)){
				//项目独立
				strSql += "	and a.ctype = 'project' ";
				if("".equals(menuid)) menuid = "10000736";
			}else{
				//年度独立
				strSql += "	and a.ctype = 'year' ";
				if("".equals(menuid)) menuid = "10000737";
			}
//			if("all".equals(opt)){	
//				//显示全部
//				strSql += "	";
//			}else{
//				//显示本人
//				strSql += "	and a.userid = '"+userid+"' ";
//			}
			
			String departments = new UserPopedomService(conn).getUserIdPopedom(userid, menuid);
			strSql += "	and (a.userid = '"+userid+"' or c.departmentid in ("+departments+"))";
			
			String tableid = "tt_"+DELUnid.getNumUnid();
			
			DataGridProperty pp = new DataGridProperty();
			String sql = "select a.*, " +
			"	b.projectid,b.projectname,  " +
			"	c.loginid,c.name as cname, " +
			"	d.departname " +
			"	from k_declare a " +
			"	left join z_project b on a.ctypeid = b.projectid " +
			"	left join k_user c on a.userid = c.id " +
			"	left join k_department d on c.departmentid = d.autoid " +
			"	where 1=1 " 
			+ strSql;
			
			pp.setOrderBy_CH("a.userdate");
			pp.setDirection("desc");
			
			if("project".equals(flag)){
				pp.addColumn("项目名称", "projectname");
				pp.setColumnWidth("30,15,15,20");
			}else{
				pp.addColumn("年度", "ctypeid");
				pp.setColumnWidth("15,15,15,20");
			}
			pp.addColumn("签署人", "cname");
			pp.addColumn("所属部门", "departname");
			pp.addColumn("签署日期", "userdate");
			
			pp.setPrintEnable(true);
		    pp.setPrintVerTical(false);
		    pp.setPrintTitle("年度独立性声明列表");
		    pp.setPrintColumnWidth("30,20,30,30");
		    pp.setPrintSqlColumn("ctypeid,cname,departname,userdate");
			pp.setPrintColumn("年度`签署人`所属部门`签署日期");
			
			pp.setTableID(tableid);
			pp.setPageSize_CH(50);
			pp.setWhichFieldIsValue(1);
			
			pp.setCustomerId("") ;
//			pp.setPrintEnable(true) ;
//			pp.setPrintTitle("报表列表") ;
//			pp.setPrintColumnWidth("10,10,10,10,10,10,10,10,10,10");
//			pp.setTrActionProperty(true) ;
//			pp.setTrAction("projectId=${projectid}") ;
//			pp.setColumnWidth("10,10,10,10,10,10,10,10,10,10");

			pp.setSQL(sql);

			request.getSession().setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);
			
			modelAndView.addObject("flag", flag);
			modelAndView.addObject("opt", opt);
			modelAndView.addObject("userid", userid);
			
			modelAndView.addObject("tableid", pp.getTableID());
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}
		
		
		return modelAndView;
	}

	//新增 增加检查是否已签
	public ModelAndView add(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		Connection conn = null;
		try {
			ASFuntion CHF = new ASFuntion();

			UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
			
			String userid = CHF.showNull(request.getParameter("userid")); 
			if("".equals(userid)) userid = userSession.getUserId();
			
			String flag = CHF.showNull(request.getParameter("flag")); //区分是年度还是项目
			String opt = CHF.showNull(request.getParameter("opt")); //区分是显示全部还是本人
			
			String projectid = CHF.showNull(request.getParameter("projectid")); //项目编号
			String changeTitle = CHF.showNull(request.getParameter("changeTitle")); //项目编号
			
			conn = new DBConnect().getConnect("");
			
			String sql = "";
			DbUtil db = new DbUtil(conn);
			sql = "select 1 from k_declare where ctype = ? and ctypeid = ? and userid = ? ";
			if("project".equals(flag)){
				//项目独立
				String t = db.queryForString(sql, new String[]{flag,projectid,userid});
				if("1".equals(t)){
					//已签，登录项目
					response.sendRedirect(request.getContextPath() + "/AuditProject.do?method=login&pid="+projectid+"&changeTitle=" + changeTitle);
					return null;
				}
				modelAndView.addObject("projectid", projectid);
				modelAndView.addObject("changeTitle", changeTitle);
				modelAndView.setViewName(project);
			}else{
				//年度独立
				String t = db.queryForString(sql, new String[]{flag,CHF.getCurrentDate().substring(0, 4),userid});
				if("1".equals(t)){
					//已签标志
					modelAndView.addObject("isOk", t);
				}
				modelAndView.setViewName(year);
				//把显示内容修改为动态版本后的处理
				MoralityService ms= new MoralityService(conn);
				Morality moral =new Morality();
				moral=ms.getYearMorality();
				modelAndView.addObject("moral", moral);
			}
			
			modelAndView.addObject("flag", flag);
			modelAndView.addObject("opt", opt);
			modelAndView.addObject("userid", userid);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}
		return modelAndView;
	}
	
	//保存
	public ModelAndView save(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Connection conn = null;
		try {
			
			ASFuntion CHF = new ASFuntion();
			UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
			
			String userid = CHF.showNull(request.getParameter("userid")); 
			String username = userSession.getUserName();
			if("".equals(userid)) userid = userSession.getUserId();
			
			String flag = CHF.showNull(request.getParameter("flag")); //区分是年度还是项目
			String opt = CHF.showNull(request.getParameter("opt")); //区分是显示全部还是本人
			
			String projectid = CHF.showNull(request.getParameter("projectid")); //项目编号
			String changeTitle = CHF.showNull(request.getParameter("changeTitle")); //项目编号
			
			String userdate = CHF.getCurrentDate() + " " + CHF.getCurrentTime();
			
			conn = new DBConnect().getConnect("");
			
			String sql = "insert into k_declare (ctype, ctypeid, userid, username, userdate) values (?,?,?,?,?)";
			DbUtil db = new DbUtil(conn);
			if("project".equals(flag)){
				//项目独立
				db.execute(sql, new String[]{
						flag,
						projectid,
						userid,
						username,
						userdate
					});
				
				//登录项目
				response.sendRedirect(request.getContextPath() + "/AuditProject.do?method=login&pid="+projectid+"&changeTitle=" + changeTitle);
			}else{
				//年度独立
				db.execute(sql, new String[]{
						flag,
						userdate.substring(0, 4),
						userid,
						username,
						userdate
					});
				
				//返回list
				response.sendRedirect(request.getContextPath() + "/declare.do?flag="+flag+"&opt="+opt);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}
		
		
		return null;
	}
	
	
	//独立性检查
	public ModelAndView declareList(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView(declareList);
		ASFuntion CHF = new ASFuntion();
		
		UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
		
		String userid = userSession.getUserId();
		Connection conn = null ;
		conn=new DBConnect().getConnect("");
		String strSql = "";
		System.out.println(request.getContextPath());
		System.out.println(request.getServletPath());
		System.out.println(request.getRequestURI());
		System.out.println(request.getQueryString());
		
		String menuid = StringUtil.showNull(request.getParameter("menuid")); //菜单ID
		if("".equals(menuid)) menuid = "10000738";
    	String departments = new UserPopedomService(conn).getUserIdPopedom(userid, menuid);
    	DbUtil.close(conn);
    	
    	strSql += " and (a.id = '"+userid+"' or a.departmentid in ("+departments+")) ";
		String tableid = "tt_"+DELUnid.getNumUnid();
		
		DataGridProperty pp = new DataGridProperty();
		String sql = "select * from (" +
				"\n	select 0 as opt ,a.id,a.loginid,a.name,a.mobilephone,b.departname, " +
				"\n	if(c.userid is null,'年度独立性申明违规','') AS atype, " +
				"\n	if(c.userid is null,concat('还未签署',year(CURDATE()),'年度独立性申明'),'') as memo,a.`departmentid` " +
				"\n	from k_user a " +
				"\n	left join k_department b on a.departmentid = b.autoid " +
				"\n	left join k_declare c on c.ctype = 'year' and c.ctypeid = year(CURDATE()) and a.id = c.userid " +
				"\n	where a.state = 0 and c.userid is null " +
				strSql + 
				"\n	union all " +
//				"	#还未进行投资合规性申报的人员
				"\n	select 1 as opt ,a.id,a.loginid,a.name,a.mobilephone,b.departname, " +
				"\n	if(c.loginid is null,'投资合规性违规','') AS atype, " +
				"\n	if(c.loginid is null,'还未进行任何本人及直属亲属投资情况申报','') as memo,a.`departmentid` " +
				"\n	from k_user a " +
				"\n	left join k_department b on a.departmentid = b.autoid " +
				"\n	left join k_investManage c on a.id = c.loginid " +
				"\n	where a.state = 0  and c.loginid is null " +
				strSql + 
				"\n	union all " +
				"\n	select 2 as opt ,a.id,a.loginid,a.name,a.mobilephone,b.departname, " +
				"\n	'投资合规性违规' as atype, " +
				"\n	CONCAT(c.loginname,'的',c.relations,c.username,'买入',d.departname,'(',c.stockcode,')','还未卖出') as memo,a.`departmentid` " +
				"\n	from k_user a " +
				"\n	left join k_department b on a.departmentid = b.autoid " +
				"\n	inner join k_investManage c on a.id = c.loginid " +
				"\n	inner join k_customer d on  ifnull(c.stockOutDate,'') = '' and d.sockcode=c.stockcode " +
				"\n	where a.state = 0  " +
				strSql + 
				"\n	union all " +
				"\n	select 3 as opt ,a.id,a.loginid,a.name,a.mobilephone,b.departname, " +
				"\n	'5年轮换制提醒' as atype, " +
				"\n	CONCAT(a.name , GROUP_CONCAT(DISTINCT c.projectyear ORDER BY c.projectyear ASC),'担任[',b.departname,']签字注师') as memo,a.`departmentid` " +
				"\n	FROM k_user a,k_customer b ,z_project c,z_auditpeople d " +
				"\n	WHERE  " +
				"\n	c.projectyear >= (year(CURDATE())  -4) AND c.projectyear <= (year(CURDATE())  -1) " +  
				"\n	AND d.role >= '签字会计师' AND d.role<'签字会计师9'   " +
				"\n	AND d.projectid=c.projectid AND userid=a.id  AND c.customerid=b.departid " +
				strSql +
				"\n	GROUP BY c.customerid,b.departname,a.name  " +
				"\n	HAVING COUNT(DISTINCT c.projectyear)>4  " +
				"\n	UNION ALL " +
				"\n	select 3 as opt ,a.id,a.loginid,a.name,a.mobilephone,b.departname, " +
				"\n	'5年轮换制提醒' as atype, " +
				"\n	CONCAT(a.name , GROUP_CONCAT(DISTINCT c.projectyear ORDER BY c.projectyear ASC),'担任[',b.departname,']签字注师') as memo,a.`departmentid` " +
				"\n	FROM k_user a,k_customer b ,z_project c,z_auditpeople d " +
				"\n	WHERE  " +
				"\n	c.projectyear >= (year(CURDATE())-5) AND c.projectyear <= (year(CURDATE())-2) " +  
				"\n	AND d.role >= '签字会计师' AND d.role<'签字会计师9'   " +
				"\n	AND d.projectid=c.projectid AND userid=a.id  AND c.customerid=b.departid " +
				strSql +
				"\n	GROUP BY c.customerid,b.departname,a.name  " +
				"\n	HAVING COUNT(DISTINCT c.projectyear)>4  " +
				") a where 1=1 ${atype} ${name} ${department} "  ;
		
		pp.setColumnWidth("15,15,15,20,25");
		pp.setOrderBy_CH("a.departname,a.loginid,opt");
		pp.setDirection("asc,asc,asc");
		pp.setInputType("checkbox");
		
		pp.addColumn("部门", "departname");
		pp.addColumn("人员姓名", "name");
		pp.addColumn("联系电话", "mobilephone");
		pp.addColumn("违规类型", "atype");
		pp.addColumn("详细", "memo");
		
		pp.setPrintEnable(true);
	    pp.setPrintVerTical(false);
	    pp.setPrintTitle("独立性检查列表");
	    pp.setUseBufferGrid(false) ;//全选x`
	    pp.setPrintColumnWidth("30,20,30,30,50");
	    pp.setPrintSqlColumn("departname,name,mobilephone,atype,memo");
		pp.setPrintColumn("部门`人员姓名`联系电话`违规类型`详细");
		
		pp.setTableID(tableid);
		pp.setPageSize_CH(100);
		pp.setWhichFieldIsValue(2);
		
		pp.addSqlWhere("atype", " and a.opt in (${atype}) ");
		pp.addSqlWhere("name", " and a.name like '%${name}%' ");
		pp.addSqlWhere("department", " and a.departmentid = '${departName}' ");
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
	
	/**
	 * 是否允许做 年度独立性声明
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView judgeAllow(HttpServletRequest request, HttpServletResponse response) throws Exception{
		Connection conn = null;
		
		UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");

		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		try {
			conn = new DBConnect().getConnect("");
			String userId =  userSession.getUserId();
			PrintWriter out = response.getWriter();
			
			String allowMsg = "succeed";
			//是否 做了 投资合规性 核查
			int investmentCount = new DbUtil(conn).queryForInt("SELECT COUNT(*) FROM K_investManage WHERE loginid= '"+userId+"' AND setTime LIKE CONCAT(YEAR(NOW()),'%')");
			 
			//是否声明了 道德声明
			int moralNormCount = new DbUtil(conn).queryForInt("SELECT COUNT(*) FROM k_moralNorm WHERE userid= '"+userId+"' AND ctype = YEAR(NOW())");

			if(investmentCount<=0){
				allowMsg = "";
				allowMsg = "您尚未录入投资合规申报，不允许做年度独立性声明! \n";
			}
			if(moralNormCount<=0){
				if("succeed".equals(allowMsg)){
					allowMsg = "";
				}
				allowMsg += "您尚未做职业道德声明，不允许做年度独立性声明!\n";
			}
			out.write(allowMsg);
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		return null;
	}
	/**
	 * 职业道德说明
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView moralNormList(HttpServletRequest request, HttpServletResponse response) throws Exception{
		Connection conn = null;
		ModelAndView modelAndView = new ModelAndView(moralNormList);
		try {
			DataGridProperty pp = new DataGridProperty();
			UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
			String userid = userSession.getUserId(); 
			
			conn = new DBConnect().getConnect("");
			String menuid = StringUtil.showNull(request.getParameter("menuid")); //菜单ID
			if("".equals(menuid)) menuid = "10000739";
	    	String departments = new UserPopedomService(conn).getUserIdPopedom(userid, menuid);
	    	
			
			String sql =" SELECT m.*,d.`departname` FROM `k_moralnorm` m LEFT JOIN k_user u ON m.`userid` =  u.`id` "
						+" LEFT JOIN k_department d ON u.`departmentid`=d.`autoid` "
						+"	where 1=1 and (m.userid = '"+userid +"' or u.departmentid in ("+departments+"))";
			pp.setSQL(sql);
			pp.addColumn("年度","ctype");
			pp.addColumn("签署人","username");
			pp.addColumn("所属部门", "departname");
			pp.addColumn("签署日期","userdate");
	        pp.setOrderBy_CH("userdate");
	        
	        pp.setPrintEnable(true);
		    pp.setPrintVerTical(false);
		    pp.setPrintTitle("年度独立性声明列表");

			
			pp.setTableID("moralNorm");
			pp.setPageSize_CH(50);
			pp.setWhichFieldIsValue(1);
			pp.setCustomerId("") ;
			
			request.getSession().setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}
		//modelAndView.addObject("tableid", pp.getTableID());
		return modelAndView;
	}

	
	public ModelAndView addMoralNorm(HttpServletRequest request, HttpServletResponse response) throws Exception{
		ModelAndView modelAndView = new ModelAndView(moralNormYear);
		Connection conn = null;
		try {
			ASFuntion CHF = new ASFuntion();
			UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
			
			String userid  = userSession.getUserId();
			conn = new DBConnect().getConnect("");	
			String sql = "";
			DbUtil db = new DbUtil(conn);
			sql = "select 1 from k_moralNorm where ctype = ? and userid =? ";
			String t = db.queryForString(sql,new String[]{CHF.getCurrentDate().substring(0, 4),userid});
			if("1".equals(t)){
				//已签标志
				modelAndView.addObject("isOk", t);
			}
			//把显示内容修改为动态版本后的处理
			MoralityService ms= new MoralityService(conn);
			Morality moral =new Morality();
			moral=ms.getMorality();
			modelAndView.addObject("moral", moral);
		
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}
		return modelAndView;
	}
	
	
	public void saveMoralNorm(HttpServletRequest request, HttpServletResponse response)  throws Exception{
	
		Connection conn = null;
		try {
			
			ASFuntion CHF = new ASFuntion();
			UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
			String userid = userSession.getUserId();
            String username = userSession.getUserName();
			String agreeornot = CHF.showNull(request.getParameter("flag"));
            String property = "";
            if(agreeornot.equals("agree")){
            	property ="同意";
            }
			String userdate = CHF.getCurrentDate() + " " + CHF.getCurrentTime();
			
			conn = new DBConnect().getConnect("");
			
			String sql = "insert into k_moralnorm (ctype,ctypeid, userid, username, userdate,property) values (?,?,?,?,?,?)";
			DbUtil db = new DbUtil(conn);
			db.execute(sql, new String[]{userdate.substring(0,4),"1",userid,username,userdate,property});
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}
		
		response.sendRedirect(request.getContextPath() + "/declare.do?method=moralNormList");
	}
	/*
	 * 跳转到职业道德说明页面
	 */
	public ModelAndView moralityEdit(HttpServletRequest request, HttpServletResponse response) throws Exception{
		ModelAndView modelAndView = new ModelAndView(moralityEdit);
		Connection conn = null;
		try {
			ASFuntion CHF = new ASFuntion();
			String prompt=CHF.showNull(request.getParameter("prompt"));
			modelAndView.addObject("prompt", prompt);
			//上面3行是标识是不是成功保存，要在页面提示信息的
			conn = new DBConnect().getConnect("");
			MoralityService ms =new MoralityService(conn);
			Morality morality=new Morality();
			morality=ms.getMorality();
			modelAndView.addObject("morality", morality);
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(conn);
		}
		return modelAndView;
	}
	/*
	 * 跳转到年度独立性说明页面
	 */
	public ModelAndView yearEdit(HttpServletRequest request, HttpServletResponse response) throws Exception{
		ModelAndView modelAndView = new ModelAndView(yearEdit);
		Connection conn = null;
		try {
			ASFuntion CHF = new ASFuntion();
			String prompt=CHF.showNull(request.getParameter("prompt"));
			modelAndView.addObject("prompt", prompt);
			//上面3行是标识是不是成功保存，要在页面提示信息的
			conn = new DBConnect().getConnect("");
			MoralityService ms =new MoralityService(conn);
			Morality morality=new Morality();
			morality=ms.getYearMorality();
			modelAndView.addObject("morality", morality);
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(conn);
		}
		return modelAndView;
	}
	/*
	 * 保存职业道德说明
	 */
	public void saveMorality(HttpServletRequest request, HttpServletResponse response) throws Exception{
		ASFuntion CHF = new ASFuntion();
		String content=CHF.showNull(request.getParameter("content"));
		Connection conn = null;
		boolean result=false;
		String prompt="";
		try {
			conn = new DBConnect().getConnect("");
			MoralityService ms=new MoralityService(conn);
			result=ms.saveMorality(content);
			if(result){
				prompt="success";
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(conn);
		}
		response.sendRedirect(request.getContextPath() + "/declare.do?method=moralityEdit&prompt="+prompt);
	}
	/*
	 * 保存年度独立性说明
	 */
	public void saveYear(HttpServletRequest request, HttpServletResponse response) throws Exception{
		ASFuntion CHF = new ASFuntion();
		String content=CHF.showNull(request.getParameter("content"));
		Connection conn = null;
		boolean result=false;
		String prompt="";
		try {
			conn = new DBConnect().getConnect("");
			MoralityService ms=new MoralityService(conn);
			result=ms.saveYear(content);
			if(result){
				prompt="success";
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(conn);
		}
		response.sendRedirect(request.getContextPath() + "/declare.do?method=yearEdit&prompt="+prompt);
	}
	
	
	/*
	 * 跳转到离职承诺函页面
	 */
	public ModelAndView promisesEdit(HttpServletRequest request, HttpServletResponse response){
		ModelAndView modelAndView = new ModelAndView(promisesEdit);
		
		WebUtil webUtil=new WebUtil(request, response);
		UserSession userSession=webUtil.getUserSession();
		response.setContentType(WebUtil.CONTENT_TYPE_UTF8);
		String rank = userSession.getUserRank();
		
		DbUtil dbUtil = null;
		Connection conn = null;
		
		try {
			conn = new DBConnect().getConnect();
			dbUtil = new DbUtil(conn);
			String sql = "select autoId from k_rank where name = '" + rank + "'";
			String autoid = dbUtil.queryForString(sql);
			
			if(autoid==null || "".equals(autoid)) {
				webUtil.alert("对不起，此功能暂未对您开放！");
				response.getWriter().write("<script>parent.tab.remove(parent.tab.getActiveTab());</script>");
				return null;
			}
			
			int j = Integer.parseInt(autoid);

			if(j>2000) {
				webUtil.alert("对不起，此功能暂未对您开放！");
				response.getWriter().write("<script>parent.tab.remove(parent.tab.getActiveTab());</script>");
				return null;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		java.util.Calendar c=java.util.Calendar.getInstance();    
		java.text.SimpleDateFormat f=new java.text.SimpleDateFormat("yyyy年MM月dd日");

		modelAndView.addObject("apply_date", f.format(c.getTime()));
		modelAndView.addObject("userid", userSession.getUserId());
		modelAndView.addObject("username", userSession.getUserName());
		modelAndView.addObject("departname", userSession.getUserAuditDepartmentName());
		modelAndView.addObject("departmentid", userSession.getUserAuditDepartmentId());
		return modelAndView;
	}
	
	
	/*
	 * 离职承诺函保存
	 */
	public ModelAndView savePromises(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView modelAndView = new ModelAndView(promisesEdit);
		
		String username = request.getParameter("username");
		String apply_date = request.getParameter("apply_date");
		
		WebUtil webUtil=new WebUtil(request, response);
		response.setContentType(WebUtil.CONTENT_TYPE_UTF8);
		DbUtil dbUtil = null;
		Connection conn = null;
		PromisesVO promisesVO = null;
		int i = 0;
		String uuid=UUID.randomUUID().toString();
		
		try {
			conn = new DBConnect().getConnect();
			dbUtil = new DbUtil(conn);
			promisesVO = webUtil.evalObject(PromisesVO.class);
			promisesVO.setUuid(uuid);
			i = dbUtil.insert(promisesVO);
			
			if(i<1) {
				webUtil.alert("保存失败！");
				response.getWriter().write("<script>parent.tab.remove(parent.tab.getActiveTab());</script>");
				return null;
			}
			webUtil.alert("保存成功！");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		modelAndView.addObject("mode", "save");
		modelAndView.addObject("username", username);
		modelAndView.addObject("apply_date", apply_date);
		return modelAndView;
	}
	
	
	/*
	 * 离职承诺函查看页面
	 */
	public ModelAndView viewPromises(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView modelAndView = null;
		
		String uuid = request.getParameter("uuid");
		String mode = request.getParameter("mode");
		
		DbUtil dbUtil = null;
		Connection conn = null;
		PromisesVO promisesVO = null;
		
		try {
			conn = new DBConnect().getConnect();
			dbUtil = new DbUtil(conn);
			promisesVO = dbUtil.load(PromisesVO.class, uuid);
			
			if("L".equals(promisesVO.getType())) {
				modelAndView = new ModelAndView(promisesEdit);
			} else if("B".equals(promisesVO.getType())) {
				modelAndView = new ModelAndView(secrecyPromisesEdit);
			}
			
			modelAndView.addObject("mode", mode);
			modelAndView.addObject("username", promisesVO.getUsername());
			modelAndView.addObject("apply_date", promisesVO.getApply_date());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return modelAndView;
	}
	
	/*
	 * 跳转到保密承诺函页面
	 */
	public ModelAndView secrecyPromisesEdit(HttpServletRequest request, HttpServletResponse response){
		ModelAndView modelAndView = new ModelAndView(secrecyPromisesEdit);
		
		WebUtil webUtil=new WebUtil(request, response);
		UserSession userSession=webUtil.getUserSession();

		java.util.Calendar c=java.util.Calendar.getInstance();    
		java.text.SimpleDateFormat f=new java.text.SimpleDateFormat("yyyy年MM月dd日");

		modelAndView.addObject("apply_date", f.format(c.getTime()));
		modelAndView.addObject("userid", userSession.getUserId());
		modelAndView.addObject("username", userSession.getUserName());
		modelAndView.addObject("departname", userSession.getUserAuditDepartmentName());
		modelAndView.addObject("departmentid", userSession.getUserAuditDepartmentId());
		return modelAndView;
	}
	
	
}

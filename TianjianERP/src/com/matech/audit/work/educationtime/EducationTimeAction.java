package com.matech.audit.work.educationtime;

import java.io.PrintWriter;
import java.sql.Connection;
import java.util.Calendar;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.matech.audit.pub.datagrid.DataGrid;
import com.matech.audit.pub.datagrid.DataGridProperty;
import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.educationSetTime.EducationSetTimeService;
import com.matech.audit.service.educationSetTime.model.EducationSetTime;
import com.matech.audit.service.educationtime.model.EducationTime;
import com.matech.audit.service.educationtime.EducationTimeService;
import com.matech.audit.service.fileupload.MyFileUpload;
import com.matech.audit.service.rank.RankService;
import com.matech.audit.service.user.UserService;
import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.single.Single;
import com.matech.framework.pub.util.ASFuntion;
import com.matech.framework.pub.util.Debug;
import com.matech.framework.service.excelupload.ExcelUploadService;

public class EducationTimeAction extends MultiActionController{
	private final String _timeList="/educationtime/educationManager.jsp";
	private final String _timeEdit ="/educationtime/educatuionEdit.jsp";
	private final String _myEducationTime="/educationtime/MyeducationTime.jsp";
	/*
	 * 学时
	 * 
	 */
	public ModelAndView list(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView(_timeList);
		ASFuntion CHF = new ASFuntion();
		request.setCharacterEncoding("utf-8");
		
		String username = CHF.showNull(request.getParameter("username"));
		String hoursType = CHF.showNull(request.getParameter("hoursType"));
		String beginTime = CHF.showNull(request.getParameter("beginTime"));
		String endTime = CHF.showNull(request.getParameter("endTime"));
		
		String sql ="select a.id,educationtime,username,hoursNum,b.type,classNum,graduationNum,className,teacherName " +
				    " from  k_educationtime a " +
				    " left join k_classtype b on a.hoursType = b.id " +
				    "where 1=1 ";
		
		
		if(!username.equals("")){
			sql+=" and username like '%"+username+"%'";
		}
		if(!hoursType.equals("")){
			sql+="and hoursType = '"+hoursType+"'";
		}
		if(!beginTime.equals("") && endTime.equals("")){
			sql+="and educationtime > '"+ beginTime+"'";
		}
		if(!endTime.equals("") && !beginTime.equals("")){
			sql+="and educationtime between '" +beginTime+"' and '"+endTime+"'" ;
		}
		if(!endTime.equals("") && beginTime.equals("")){
			sql+="and educationtime < '"+ endTime +"'";
		}
		
		DataGridProperty pp =new DataGridProperty();
	
		pp.setSQL(sql);
		pp.setCustomerId("");
		pp.setPageSize_CH(50);
		pp.setOrderBy_CH("educationtime");
		pp.setTableID("timelist");
    	pp.setWhichFieldIsValue(1);
		pp.setInputType("radio");
		
		pp.addColumn("培训日期", "educationtime");
		pp.addColumn("学员姓名", "username");
		pp.addColumn("学时数", "hoursNum");
		pp.addColumn("学时形式","type"); 
		pp.addColumn("培训班编号", "classNum");
		pp.addColumn("培训班名称","className");
		pp.addColumn("证书编号","graduationNum");
		pp.addColumn("讲师","teacherName");
		pp.setColumnWidth("10,10,10,10,12,12,10,15") ;
		
	    request.getSession().setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);
		return modelAndView;
	}
	/*
	 * 编辑学时
	 */
	public ModelAndView timeEdit(HttpServletRequest request, HttpServletResponse response) throws Exception{
		Connection conn = null;
		ModelAndView modelAndView = new ModelAndView(_timeEdit);
		
		try{
			conn = new DBConnect().getConnect("");
			ASFuntion CHF=new ASFuntion();
			String id = CHF.showNull(request.getParameter("id"));
			EducationTimeService educationTimeService = new EducationTimeService(conn);
			EducationTime educationtime = educationTimeService.getEducationTime(id);
			modelAndView.addObject("educationtime", educationtime);
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return modelAndView;
	}
	//更改学时
	public ModelAndView updateTime(HttpServletRequest request, HttpServletResponse response) throws Exception{
	    Connection conn = null; 
		
	    ModelAndView moelAndView = new ModelAndView(_timeList);
		request.setCharacterEncoding("utf-8");
	    
	    try{
	    	conn = new DBConnect().getConnect("");
	    	ASFuntion CHF = new ASFuntion();
	    	int id = Integer.parseInt(CHF.showNull(request.getParameter("id")));
	    	
	    	EducationTime educationTime = new EducationTime();
	    	
	    	educationTime.setId(CHF.showNull(request.getParameter("id")));
	    	educationTime.setEducationtime(CHF.showNull(request.getParameter("educationtime")));
	    	educationTime.setClassNum(CHF.showNull(request.getParameter("classNum")));
	        educationTime.setUsername(CHF.showNull(request.getParameter("username")));
	        educationTime.setHoursNum(CHF.showNull(request.getParameter("hoursNum")));
	        educationTime.setHoursType(CHF.showNull(request.getParameter("hoursType")));
	        educationTime.setGraduationNum(CHF.showNull(request.getParameter("graduationNum")));
	        
	    	EducationTimeService edutimeSer = new EducationTimeService(conn);
	    	edutimeSer.updateTime(educationTime,id);
	    	
	    }catch(Exception e){
	    	e.printStackTrace();
	    }finally{
	    	DbUtil.close(conn);
	    }
		response.sendRedirect("educationTime.do");  
		return null;
	}

	/*
	 * 删除学时
	 */
	public ModelAndView Remove(HttpServletRequest request, HttpServletResponse response)  throws Exception {
		Connection conn=null;
		PrintWriter out = null;
		try {
			conn= new DBConnect().getConnect("");
			
			int id = Integer.parseInt(request.getParameter("id"));
			EducationTimeService educationService = new EducationTimeService(conn);
			educationService.remove(id);

			response.setContentType("text/html;charset=utf-8");  //设置编码
			out = response.getWriter();
			out.print("删除成功！");
		} catch (Exception e) {
			out.print("删除失败！");
			Debug.print(Debug.iError, "删除学时失败！", e);
			e.printStackTrace();
			throw e;
		} finally {
			out.close(); 
			DbUtil.close(conn);
		}	
		
		return null;
	}
	/*
	 * 批量导入
	 */
	public ModelAndView Upload(HttpServletRequest request, HttpServletResponse response) throws Exception{	
		
		return new ModelAndView("/educationtime/educationUpload.jsp");
	}
	/**
	 * 保存批量导入
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView SaveUpload(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		PrintWriter out = null;
		Connection conn=null;
		Single sl = new Single();
		UserSession us=(UserSession)request.getSession().getAttribute("userSession");
		String lockmsg = "装载帐套数据";
		try {
			
			response.setContentType("text/html;charset=utf-8");  //设置编码
//			response.setHeader("title", "EXCEL导入");
			out = response.getWriter();
			
			Map parameters = null;

			String uploadtemppath = "";

			String strFullFileName = "";
//			String nf = "";
			//获取前台指定的客户ID

			String User = "";

			//String popedom = ".22.2205.2210.2214.221405.2215.2216.2217.221701.221702.221703.221704.221725.221730.221735.32.3205.3210.3215.3216.3225.42.4201.4203.4204.4206.4208.4212.4215.421505.421510.4226.4230.45.4505.4510.4515.4520.4525.4530.4535.52.5204.5205.520505.520510.520515.5210.5215.5217.5220.5225.5226.522605.522610.522615.5230.523005.523010.5232.451003.5236.5250.525005.525010.525025.525070.525080.62.6205.620535.620537.620540.620545.620560.620565.6210.6215.6220.622005.622010.622015.622020.6225.622505.622510.72.7205.720505.720530.7207.720705.720710.7209.720905.720910.7215.7220.722005.752001.92.";		

			MyFileUpload myfileUpload = new MyFileUpload(request);
			uploadtemppath = myfileUpload.UploadFile(null, null);
			parameters = myfileUpload.getMap();
			User = (String) parameters.get("User");

			//如果不存在,说明是第一次提交,这个时候从MAP中取值
			uploadtemppath = (String) parameters.get("tempdir");

			strFullFileName = uploadtemppath
					+ (String) parameters.get("filename");
			org.util.Debug.prtOut("strFullFileName=" + strFullFileName);
			uploadtemppath = (String) parameters.get("tempdir");

			if (uploadtemppath.equals(""))
				out.print("Error\n帐套数据上传及预处理失败");
			else
				out.println("帐套数据上传并分析成功!<br>正在加载，请等待<br><br><br>");

			int error = 0; //用于标记程序是否出错,出错了后面就不会再继续执行了


			//分析帐套文件,取出帐套年份;

			out.println("预处理分析帐套文件<br/>");
			out.flush();
			

			conn = new DBConnect().getDirectConnect("");

//			初始化业务对象
			ExcelUploadService upload = null;
			try {
				upload = new ExcelUploadService(conn,strFullFileName);
			} catch (Exception e) {
				e.printStackTrace();
				out.println("临时路径或者客户编号设置有误,请与系统管理员联系<br>");
				error = 1;
			}
			
//			检查用户指定年份的帐套是否存在;

			//定义单一，避免其他用户干扰；
			
			
			try {
				sl.locked(lockmsg, us.getUserLoginId());
			} catch (Exception e) {
				out.println(e.getMessage() + "<br/>");
				error = 1;
			}

			if (error > 0) {
				out.println("装载活动遇到错误,已经中止!<br>请解决错误后重新装载");
			} else {
				org.util.Debug.prtOut("装载的临时目录为:" + uploadtemppath);
				out.println("继续处理装载<br>");
				out.flush();
				
				EducationTimeService edu = new EducationTimeService(conn);
			//	UserService ued = new UserService(conn);
				out.println("正在分析EXCEL文件......");
				out.flush();
				upload.init();
				out.println("分析EXCEL文件完毕!<BR>");

				//开始装载科目余额表信息
				//首先清空指定表的指定帐套的数据;
				out.println("正在装载学时内容!......");
				out.flush();

				edu.newTable(); 	 	 	

				upload.setExcelNum("");
				upload.setExcelString("培训日期,人员姓名");
				String[] exexlKmye = { "培训班编号","培训日期", "人员姓名", "学时数","学时形式","讲师","培训班名称"};
				String[] tableKmye = { "classNum","educationtime", "username", "hoursNum","hoursType","teacherName","className"};
				String[] exexlPzmxOpt = {"培训班编号"};    
				String[] tablePzmxOpt = {"graduationNum"};

				String result = "";

				result = upload.LoadFromExcel("学时列表", "tt_k_educationtime",
				exexlKmye, tableKmye, exexlPzmxOpt, tablePzmxOpt,null,null);

				out.println("装载学时内容完毕!<BR>");

				out.flush();
				out.println("开始更新学时列表!......");
				out.flush();
				result = edu.CheckUpData();
				edu.insertData();
				out.println("更新学时列表完毕!<BR>");
				
				if (result != null && result.length() > 0) {
					out.println("<br><br>装载非正常结果报告：<BR><font color='red'>");
					out.println(result);
					out.println("</font><br>");
				}

				out.println("<hr>数据装载成功 <a href=\"educationTime.do\">返回查询页面</a>\"</font>");
				
				
			}
		} catch (Exception e) {
			e.printStackTrace();
			out.println("<font style=\"color:red\">装载处理出现错误:<br/>"+ e.getMessage());
			out.println("<a href=\"user.do?method=Upload\">返回装载页面</a>\"</font>");
			
			Debug.print(Debug.iError, "查询个人项目项目权限失败！", e);
			e.printStackTrace();
		} finally {
			try {
				sl.unlocked(lockmsg, us.getUserLoginId());
			} catch (Exception e) {
				out.println("撤销并发锁失败：" + e.getMessage() + "<br/>");
			}
			DbUtil.close(conn);
		}

		return null;
	}
	/**
	 * 我的学时
	 */
	public ModelAndView myEducationTime(HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.setContentType("text/html;charset=utf-8");
		request.setCharacterEncoding("utf-8");
		ModelAndView modelAndView = new ModelAndView(_myEducationTime);
		DataGridProperty pp = new DataGridProperty();
		DataGridProperty pp2 = new DataGridProperty();
		ASFuntion CHF = new ASFuntion();
		String selectYear=CHF.showNull(request.getParameter("selectYear"));
		UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
		Connection conn=null;
		String year=null;
		try {
			conn = new DBConnect().getConnect("");
			UserService us=new UserService(conn);
			EducationTimeService ets=new EducationTimeService(conn);
			EducationSetTimeService ests=new EducationSetTimeService(conn);
			String username = userSession.getUserName();
			String userId=userSession.getUserId();

			//获取职级
			String rank=us.getRank(userId);
			RankService rs=new RankService(conn);
			String rankId=rs.getRankId(rank);
			Calendar c=Calendar.getInstance();
			if(selectYear==null||selectYear.equals("")){
				year=c.get(Calendar.YEAR)+"";
			}
			else{
				year=selectYear;
			}
			EducationSetTime edu=new EducationSetTime();
			edu=ests.findByIdYear(rankId, year);
			String yearOne=edu.getYearOne();
			String yearTwo=edu.getYearTwo();
			//获取某年(相当于2年)的学时
			String educationTime=ets.getEducationTime(yearTwo, username);
			//获取某年(相当于2年)的必修学时
			String educationSetTime=edu.getTimeTwo();
			//获取某年的上一期(相当于1年)的学时
			String lastEducationTime=ets.getEducationTime(yearOne, username);
			//获取某年的上一期(相当于1年)的必修学时
			String lastEducationSetTime=edu.getTimeOne();
			String isPassOne="(不通过)";
			String isPassTwo="(不通过)";
			if(educationTime!=null && educationSetTime!=null && Integer.valueOf(educationTime)>=Integer.valueOf(educationSetTime)){
				isPassTwo="(通过)";
			}
			if(lastEducationTime!=null && lastEducationSetTime!=null && Integer.valueOf(lastEducationTime)>=Integer.valueOf(lastEducationSetTime)){
				isPassOne="(通过)";
			}
			modelAndView.addObject("year", year);
			modelAndView.addObject("username", username);
			modelAndView.addObject("rank", rank);
			modelAndView.addObject("educationTime", educationTime);
			modelAndView.addObject("lastEducationTime", lastEducationTime);
			modelAndView.addObject("educationSetTime", educationSetTime);
			modelAndView.addObject("lastEducationSetTime", lastEducationSetTime);
			modelAndView.addObject("isPassTwo", isPassTwo);
			modelAndView.addObject("isPassOne", isPassOne);
			modelAndView.addObject("yearOne", yearOne);
			modelAndView.addObject("yearTwo", yearTwo);
			
			//汇总查询
			String sql  ="select id,time,username,sum(hoursNum) as hoursNum from k_educationtime group by time,username";
			pp.setCustomerId("");
			pp.setPageSize_CH(50);
			pp.setOrderBy_CH("time");
			pp.setTableID("timeEducation");
	    	pp.setWhichFieldIsValue(1);
			pp.setInputType("radio");
			pp.setSQL(sql);
			
			pp.addColumn("年份","time");
			pp.addColumn("学员姓名","username");
			pp.addColumn("学时数", "hoursNum");
			
			sql ="select a.id,educationtime,username,hoursNum,c.type,classNum,graduationNum,a.className,a.teacherName from k_educationtime a " +
					" left join k_classType c on c.id=a.hoursType where username='"+username+"'";
			pp2.setCustomerId("");
			pp2.setPageSize_CH(50);
			pp2.setOrderBy_CH("educationtime");
			pp2.setTableID("myEducationtime");
	    	pp2.setWhichFieldIsValue(1);
			pp2.setInputType("radio");
			pp2.setSQL(sql);
			
			pp2.addColumn("培训日期", "educationtime");
			pp2.addColumn("人员姓名", "username");
			pp2.addColumn("学时数", "hoursNum");
			pp2.addColumn("学时形式","type");
			pp2.addColumn("培训班编号", "classNum");
			pp2.addColumn("讲师","teacherName");
			pp2.addColumn("培训班名称","className");
			pp2.addColumn("证书编号", "graduationNum");
			
			pp2.setColumnWidth("10,10,8,12,12,12,12,15");
			
		    request.getSession().setAttribute(DataGrid.sessionPre + pp2.getTableID(), pp2);
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(conn);
		}
		
		
		
		
		return modelAndView;
		
	}

}

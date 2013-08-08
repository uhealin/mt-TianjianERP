package com.matech.audit.work.hr;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
import com.matech.audit.pub.datagrid.DataGrid;
import com.matech.audit.pub.datagrid.DataGridProperty;
import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.fileupload.MyFileUpload;
import com.matech.audit.service.hr.ResumeService;
import com.matech.audit.service.hr.model.ResumeVO;
import com.matech.audit.service.user.model.JobVO;
import com.matech.audit.work.form.FormDefineAction;
import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.single.Single;
import com.matech.framework.pub.util.ASFuntion;
import com.matech.framework.pub.util.Debug;
import com.matech.framework.pub.util.StringUtil;
import com.matech.framework.pub.util.WebUtil;
import com.matech.framework.service.excelupload.ExcelUploadService;

public class ResumeAction extends MultiActionController{
	
	protected enum Jsp{
		yj_table,sz_table;
		
		public String getPath(){
			return MessageFormat.format("hr/{0}.jsp", this.name());
		} 
	}
	
	private final String yjList = "hr/yingjieList.jsp";
	private final String yjEdit = "hr/test.jsp";
	private final String uploadExcelYJ = "hr/resumeUploadYJ.jsp";
	private final String uploadExcelSZ = "hr/resumeUploadSZ.jsp";
	public ModelAndView yjList (HttpServletRequest request, HttpServletResponse response)throws Exception {
		
		ModelAndView modelAndView = new ModelAndView(yjList);
		String sql = "select uuid,name,gender, graduation,degrees,tel,email,wantjob,b.departmentname as depart ,applytime from k_resume2 a "+
		"left join k_job b on b.jobname=a.wantjob where type=1 ";
		
		DataGridProperty pp = new DataGridProperty();
		pp.setCustomerId("");
		pp.setWhichFieldIsValue(1);
		pp.setTableID("yingjieList");
		pp.setCheckColumn("checkbox");
		pp.setOrderBy_CH("a.uuid,a.applytime");
		pp.setDirection("asc,desc");
		pp.setPrintEnable(true);
		pp.setPrintVerTical(false);
		pp.setPrintTitle("应届生招聘表单");
		
		pp.addColumn("姓名", "name");
		pp.addColumn("性别", "gender");
		pp.addColumn("拟招人数", "graduation");
		pp.addColumn("学历", "degrees");
		pp.addColumn("手机", "tel");
		pp.addColumn("邮箱", "email");
		pp.addColumn("求职岗位", "wantjob");
		pp.addColumn("所属总/分所", "depart");
		pp.addColumn("投历时间", "applytime"); 
		
		pp.setSQL(sql);
		request.getSession().setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);
		
		
		return modelAndView;
	}
	public ModelAndView yjEdit(HttpServletRequest request, HttpServletResponse response)throws Exception {
		ModelAndView modelAndView = new ModelAndView(yjEdit);
		return modelAndView;
		
	}
	public ModelAndView add(HttpServletRequest request, HttpServletResponse response)throws Exception {
      
		Connection conn=null;
		DbUtil dbUtil=null;
		WebUtil webUtil=new WebUtil(request,response);
		ResumeVO resumeVO=null;
		ASFuntion CHF = new ASFuntion();
		//获取投简历的时间
		String applytime = CHF.getCurrentDate();
		int eff=0;
		try{
			conn=new DBConnect().getConnect();
			dbUtil=new DbUtil(conn);
			resumeVO=webUtil.evalObject(ResumeVO.class);
			resumeVO.setUuid(UUID.randomUUID().toString());
			resumeVO.setApply_time(applytime);
			eff+=dbUtil.insert(resumeVO);
		}catch(Exception ex){
			
		}finally{
			dbUtil.close(conn);
		}
		response.sendRedirect(request.getContextPath() + "/resume.do?method=yjList");
		return null;
	}
	public ModelAndView look(HttpServletRequest request, HttpServletResponse response)throws Exception {
		
		ModelAndView modelAndView = new ModelAndView(yjEdit);
	      Connection conn = null;
	      DbUtil dbUtil = null;
	      WebUtil webUtil = new WebUtil(request,response);
	      ResumeVO resumeVO = null;
	      ASFuntion CHF = new ASFuntion();
	      try {
	    	  //获得连接
	    	  conn = new DBConnect().getConnect();
	    	  //获得数据库处理类
	    	  dbUtil = new DbUtil(conn);
	    	  //用反射的方法获得实体类
	    	  resumeVO = webUtil.evalObject(ResumeVO.class);
	    	  //根据id获得实体类的内容，set好，然后返回给实体类
	    	  resumeVO =  dbUtil.load(ResumeVO.class,resumeVO.getUuid());
	    	  modelAndView.addObject("vo", resumeVO);
	    	  
		} catch (Exception e) {
			
		}finally{
			dbUtil.close(conn);
		}
		return modelAndView;
	}
	public void delect (HttpServletRequest request, HttpServletResponse response)throws Exception {
		Connection conn = null;
	      DbUtil dbUtil = null;
	      WebUtil webUtil = new WebUtil(request,response);
	      int num = 0;
	      ResumeVO resumeVO = null;
	      ASFuntion CHF = new ASFuntion();
	      try {
	    	  //获得连接
	    	  conn = new DBConnect().getConnect();
	    	  //获得数据库处理类
	    	  dbUtil = new DbUtil(conn);
	    	  //用反射的方法获得实体类
	    	  resumeVO = webUtil.evalObject(ResumeVO.class);
	    	  //根据id获得实体类的内容，set好，然后返回给实体类
	    	  //删除
	    	  num =  dbUtil.delete(resumeVO);
	    	  
		} catch (Exception e) {
			
		}finally{
			dbUtil.close(conn);
		}
	}
	public ModelAndView sendSms (HttpServletRequest request, HttpServletResponse response)throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		ASFuntion asf = new ASFuntion();
		//获得所选的uuid
		String[]uuids = request.getParameterValues("uuid");
		//String uuid = "";
		
		//int num = uuid.length;
		for(String uuid: uuids){
			
		}
		
		return modelAndView;
	}
	//批量导入
	public ModelAndView yjUpload(HttpServletRequest request, HttpServletResponse response){
		return new ModelAndView(uploadExcelYJ);
	}
	public ModelAndView szUpload(HttpServletRequest request, HttpServletResponse response){
		return new ModelAndView(uploadExcelSZ);
	}
	public ModelAndView saveUpLoad(HttpServletRequest request, HttpServletResponse response) throws Exception{

		PrintWriter out = null;
		Connection conn=null;
		Single sl = new Single();
		UserSession us=(UserSession)request.getSession().getAttribute("userSession");
		String lockmsg = "装载帐套数据";
		DbUtil dbUtil=null;
		try {
			UserSession userSession = (UserSession) request.getSession().getAttribute("userSession"); 
			response.setContentType("text/html;charset=utf-8");  //设置编码
			out = response.getWriter();
			
			Map parameters = null;

			String uploadtemppath = "";

			String strFullFileName = "";
			String User = "";


			MyFileUpload myfileUpload = new MyFileUpload(request);
			uploadtemppath = myfileUpload.UploadFile(null, null);
			parameters = myfileUpload.getMap();
			System.out.println(parameters);
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
             dbUtil=new DbUtil(conn);
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
				
				//ActiveService activeService = new ActiveService(conn);
				ResumeService resumeService = new ResumeService(conn);
				out.println("正在分析EXCEL文件......");
				out.flush();
				upload.init();
				out.println("分析EXCEL文件完毕!<BR>");

				//开始装载科目余额表信息
				//首先清空指定表的指定帐套的数据;
				out.println("正在装载用户内容!......");
				out.flush();

				resumeService.newTable();
				upload.setExcelNum("");
				upload.setExcelString("身份证号,手机,出生日期");
				//必填设置
				String[] exexlKmye = { "身份证号","姓名","毕业院校","学历","专业","手机","邮箱"};
				//必填对应表字段设置
				//String[] tableKmye = { "id","name", "graduation", "degrees","profession","tel","email" };
				String[] tableKmye = { "idcard","name_cn", "grud_uni", "grud_level","grud_pro_master","mobile","email" };
				//不必填
				String[] exexlPzmxOpt = { "性别","出生日期","籍贯","住址","高考录取批次","辅修专业","英语等级"};
				//String[] tablePzmxOpt = { "gender", "birthday","nativeplace", "address", "receivetype", "otherprofession","englishlevel"};
				String[] tablePzmxOpt = { "sex", "birthday","nativeplace", "address", "cee_batch", "grud_pro_slave","cet_level"};
				
				//婚姻状态，籍贯，户口所在地，政治面貌，入党时间，组织关系所在单位，专业，英语能力，CPA号，合同类型,特长
				String[] exexlKmyeFixFields = { ""};
				String[] excelKmyeFixFieldValues = { ""};

				String result = "";

				result = upload.LoadFromExcel("应届简历库", "tt_k_resume2",
				exexlKmye, tableKmye, exexlPzmxOpt, tablePzmxOpt,
				null, null,true);
				//获取投简历的时间
				
				out.println("装载用户内容完毕!<BR>");

				out.flush();
				out.println("应届简历表!......");
				out.flush();
				
				
				//临时表里面已经有数据了，首先要将临时表与已有的表进行比较
				PreparedStatement ps = null;
				ResultSet rs = null;
				String sql1 = null;
				ASFuntion CHF = new ASFuntion();
				String applytime = CHF.getCurrentDate();
				//获得要投的岗位
				String wantjob = (String) parameters.get("wantjob");
				//String wantjob = CHF.showNull(request.getParameter("wantjob"));
				String type1 = CHF.showNull(request.getParameter("type"));
				int type = Integer.parseInt(type1);
				try {
					//String sql ="update tt_k_resume2 set type="+type+", applytime='"+applytime+"' , wantjob='"+wantjob+"' ";
					String sql ="update tt_k_resume2 set type="+type+", apply_time='"+applytime+"' , apply_job_id='"+wantjob+"',departmentid='"+userSession.getUserAuditDepartmentId()+"' ";
					ps = conn.prepareStatement(sql);
					ps.execute(sql);
				} catch (Exception e) {
					throw e;
				}
				
				String ids="";
				try {
					sql1 = "SELECT a.id FROM tt_k_resume2 a ,k_resume2 b WHERE a.id = b.id ";
					sql1 = "SELECT a.idcard FROM tt_k_resume2 a ,k_resume b WHERE a.idcard = b.idcard ";
					ps = conn.prepareStatement(sql1);
					rs = ps.executeQuery();
					
					while(rs.next()){
						ids+="'"+rs.getString(1)+"',";
						//ids = rs.getString(1);
					}
					//ids=ids.substring(1);
					ids=StringUtil.trim(ids, ",");
					if(ids!=null&&!ids.isEmpty()){
							String sql2 = "DELETE from tt_k_resume2 where id in ("+ids+") ";
							sql2 = "DELETE from tt_k_resume2 where idcard in ("+ids+") ";
							ps.execute(sql2);
					}
					String sql3 = "insert into k_resume2 (SELECT * from tt_k_resume2 )";
				    sql3 = "insert into k_resume (SELECT * from tt_k_resume2 )";
					ps.execute(sql3);
				} catch (Exception e) {
					throw e;
				}finally{
					
			}
				/*List<ResumeVO> resumeList = dbUtil.select(ResumeVO.class, "select * from tt_k_resume2");
				
					System.out.println(resumeList.size());
					for(int i =0;i< resumeList.size(); i++){
						
						ResumeVO resumeVO = resumeList.get(i);
						//Connection conn=null;
						//DbUtil dbUtil=null;
						WebUtil webUtil=new WebUtil(request,response);
						
						ASFuntion CHF = new ASFuntion();
						//获取投简历的时间
						String applytime = CHF.getCurrentDate();
						//获得要投的岗位
						String wantjob = CHF.showNull(request.getParameter("wantjob"));
						String type1 = CHF.showNull(request.getParameter("type"));
						int type = Integer.parseInt(type1);
						int eff=0;
						try{
							//conn=new DBConnect().getConnect();
							//dbUtil=new DbUtil(conn);
							resumeVO=webUtil.evalObject(ResumeVO.class);
							resumeVO.setUuid(UUID.randomUUID().toString());
							resumeVO.setApplytime(applytime);
							resumeVO.setWantjob(wantjob);
							resumeVO.setType(type);
							eff+=dbUtil.insert(resumeVO);
						}catch(Exception e){
							e.printStackTrace();
						}finally{
							
						}
					}*/
					
				
				
				out.println("更新用户列表完毕!<BR>");
				
				if (result != null && result.length() > 0) {
					out.println("<br><br>装载非正常结果报告：<BR><font color='red'>");
					out.println(result);
					out.println("</font><br>");
				}

				out.println("<hr>数据装载成功 <a href=\"formDefine.do?method=formListView&formTypeId=a3703544-e921-4749-8d6b-0efd5c45c9bb&uuid=13916b9b-2f1b-47d6-9b00-66fddc927903\">返回查询页面</a>\"</font>");
				
				
			}
		} catch (Exception e) {
			e.printStackTrace();
			out.println("<font style=\"color:red\">装载处理出现错误:<br/>"+ e.getMessage());
			out.println("<a href=\"resume.do?method=yjUpload\">返回装载页面</a>\"</font>");
			
			Debug.print(Debug.iError, "查询个人项目项目权限失败！", e);
			e.printStackTrace();
		} finally {
			try {
				sl.unlocked(lockmsg, us.getUserLoginId());
				new DbUtil(conn).executeUpdate("drop table tt_k_resume2");
			} catch (Exception e) {
				out.println("撤销并发锁失败：" + e.getMessage() + "<br/>");
			}
			DbUtil.close(conn);
		}

		return null;
		
	
	}
	
	
	public ModelAndView toEdit(HttpServletRequest request,HttpServletResponse response){
		String uuid=request.getParameter("uuid");
	    Connection conn=null;
	    DbUtil dbUtil=null;
	    WebUtil webUtil=new WebUtil(request, response);
	    ResumeVO resumeVO=new ResumeVO();
	    ModelAndView modelAndView=null;
	    String type=request.getParameter("type");
	    try {
	    	conn=new DBConnect().getConnect();
			dbUtil=new DbUtil(conn);
			if(uuid!=null&&!uuid.isEmpty()){
				resumeVO=dbUtil.load(ResumeVO.class,uuid);
				if(type==null||type.isEmpty()){
					type=resumeVO.getType();
				}
			}
			
			if("0".equals(type)){
				modelAndView=new ModelAndView(Jsp.sz_table.getPath());
			}else if("1".equals(type)){
				modelAndView=new ModelAndView(Jsp.yj_table.getPath());
			}
			modelAndView.addObject("vo", resumeVO);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
	    return modelAndView;
		
	}
	
	public ModelAndView doSave(HttpServletRequest request,HttpServletResponse response) throws Exception{
	
		 Connection conn=null;
		    DbUtil dbUtil=null;
		    WebUtil webUtil=new WebUtil(request, response);
		    ResumeVO resumeVO=webUtil.evalObject(ResumeVO.class);
		    ModelAndView modelAndView=null;
		    String formid=request.getParameter("formid");
		    String menuid=request.getParameter("menuid");
		    UserSession userSession=webUtil.getUserSession();
		    try {
		    	conn=new DBConnect().getConnect();
				dbUtil=new DbUtil(conn);
				if(resumeVO.getUuid()!=null&&!resumeVO.getUuid().isEmpty()){
					resumeVO=dbUtil.load(ResumeVO.class, resumeVO.getUuid());
					resumeVO=webUtil.evalObject(resumeVO);
					dbUtil.update(resumeVO);
				}else{
                    resumeVO.setUuid(UUID.randomUUID().toString());
                    resumeVO.setApply_time(StringUtil.getCurDateTime());
                    resumeVO.setDepartmentid(userSession.getUserAuditDepartmentId());
                    resumeVO.setHr_state("未审核");
                    resumeVO.setHr_interview_state("未面试");
                    
                    dbUtil.insert(resumeVO);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				DbUtil.close(conn);
			}
		    
		    response.sendRedirect(FormDefineAction.pathFormListView(formid)+"&menuid="+menuid);
		    return null;
	
	}
	
	
	
	public ModelAndView doUpLoad(HttpServletRequest request, HttpServletResponse response) throws Exception{

		PrintWriter out = null;
		Connection conn=null;
		Single sl = new Single();
		UserSession us=(UserSession)request.getSession().getAttribute("userSession");
		String lockmsg = "装载帐套数据";
		DbUtil dbUtil=null;
		try {
			UserSession userSession = (UserSession) request.getSession().getAttribute("userSession"); 
			response.setContentType("text/html;charset=utf-8");  //设置编码
			out = response.getWriter();
			
			Map parameters = null;

			String uploadtemppath = "";

			String strFullFileName = "";
			String User = "";


			MyFileUpload myfileUpload = new MyFileUpload(request);
			uploadtemppath = myfileUpload.UploadFile(null, null);
			parameters = myfileUpload.getMap();
			System.out.println(parameters);
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
             dbUtil=new DbUtil(conn);
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
				
				//ActiveService activeService = new ActiveService(conn);
				ResumeService resumeService = new ResumeService(conn);
				out.println("正在分析EXCEL文件......");
				out.flush();
				upload.init();
				out.println("分析EXCEL文件完毕!<BR>");

				//开始装载科目余额表信息
				//首先清空指定表的指定帐套的数据;
				out.println("正在装载用户内容!......");
				out.flush();

				resumeService.newTable();
				upload.setExcelNum("");
				upload.setExcelString("身份证号,手机,出生日期");
				//必填设置
				String[] exexlKmye = { "身份证号","姓名","毕业院校","学历","专业"};
				//必填对应表字段设置
				//String[] tableKmye = { "id","name", "graduation", "degrees","profession","tel","email" };
				String[] tableKmye = { "idcard","name_cn", "grud_uni", "grud_level","grud_pro_master"};
				//不必填
				//String[] exexlPzmxOpt = { "性别","出生日期","籍贯","住址","高考录取批次","辅修专业","英语等级"};
				//String[] tablePzmxOpt = { "gender", "birthday","nativeplace", "address", "receivetype", "otherprofession","englishlevel"};
                String[] exexlPzmxOpt = {"性别","出生日期","籍贯","家庭地址","高校录取批次",
                		"辅修专业","班级名次","年级名次","英语等级","会计-已考","审计-已考","财务成本管理-已考","经济法-已考",
                		"税法-已考","战略与风险管理-已考","综合税法-已考","会计-已过","审计-已过","财务成本管理-已过","经济法-已过",
                		"税法-已过","战略与风险管理-已过","综合税法-已过","其他资格","简历备注","公务员研究生考试情况记录","实习时间",
                		"面试时间","面试地点","面试情况","面试意见"};

				String[] tablePzmxOpt = { "sex", "birthday","nativeplace", "address", "cee_batch",
						"grud_pro_slave","class_postion","grade_postion","cet_level","cpa_account_attend_ind","cpa_audit_attend_ind","cpa_fcm_attend_ind","cpa_eclaw_attend_ind"
						,"cpa_taxlaw_attend_ind","cpa_srm_attend_ind","cpa_sp_attend_ind","cpa_account_pass_ind","cpa_audit_pass_ind","cpa_fcm_pass_ind","cpa_eclaw_pass_ind"
						,"cpa_taxlaw_pass_ind","cpa_srm_pass_ind","cpa_sp_pass_ind","other_pr_info","remark","plan_ext_study","prac_real_start_date"
						,"hr_interview_time","hr_interview_address","hr_interview_state","remark_interview"
				};
				
				//婚姻状态，籍贯，户口所在地，政治面貌，入党时间，组织关系所在单位，专业，英语能力，CPA号，合同类型,特长
				String[] exexlKmyeFixFields = { ""};
				String[] excelKmyeFixFieldValues = { ""};

				String result = "";

				result = upload.LoadFromExcel("通用简历库", "tt_k_resume2",
				exexlKmye, tableKmye, exexlPzmxOpt, tablePzmxOpt,
				null, null,true);
				//获取投简历的时间
				
				out.println("装载用户内容完毕!<BR>");

				out.flush();
				out.println("应届简历表!......");
				out.flush();
				
				
				//临时表里面已经有数据了，首先要将临时表与已有的表进行比较
				PreparedStatement ps = null;
				ResultSet rs = null;
				String sql1 = null;
				ASFuntion CHF = new ASFuntion();
				String applytime = CHF.getCurrentDate();
				//获得要投的岗位
				String wantjob = (String) parameters.get("wantjob");
				
				//String wantjob = CHF.showNull(request.getParameter("wantjob"));
				//String type1 = CHF.showNull(request.getParameter("type"));
				//int type = Integer.parseInt(type1);
				/*
				try {
					String sql ="update tt_k_resume2 set type="+type+", applytime='"+applytime+"' , wantjob='"+wantjob+"' ";
					String sql ="update tt_k_resume2 set type="+type+", apply_time='"+applytime+"' , apply_job_id='"+wantjob+"',departmentid='"+userSession.getUserAuditDepartmentId()+"' ";
					ps = conn.prepareStatement(sql);
					ps.execute(sql);
				} catch (Exception e) {
					throw e;
				}
				
				String ids="";
				try {
					sql1 = "SELECT a.id FROM tt_k_resume2 a ,k_resume2 b WHERE a.id = b.id ";
					sql1 = "SELECT a.idcard FROM tt_k_resume2 a ,k_resume b WHERE a.idcard = b.idcard ";
					ps = conn.prepareStatement(sql1);
					rs = ps.executeQuery();
					
					while(rs.next()){
						ids+="'"+rs.getString(1)+"',";
						//ids = rs.getString(1);
					}
					//ids=ids.substring(1);
					ids=StringUtil.trim(ids, ",");
					if(ids!=null&&!ids.isEmpty()){
							String sql2 = "DELETE from tt_k_resume2 where id in ("+ids+") ";
							sql2 = "DELETE from tt_k_resume2 where idcard in ("+ids+") ";
							ps.execute(sql2);
					}
					String sql3 = "insert into k_resume2 (SELECT * from tt_k_resume2 )";
				    sql3 = "insert into k_resume (SELECT * from tt_k_resume2 )";
					ps.execute(sql3);
				} catch (Exception e) {
					throw e;
				}finally{
					
			}*/
				JobVO jobVO=dbUtil.load(JobVO.class, wantjob);
				List<ResumeVO> resumeList = dbUtil.select(ResumeVO.class, "select * from tt_k_resume2");
				
					System.out.println(resumeList.size());
					for(int i =0;i< resumeList.size(); i++){
						ResumeVO resumeVO = resumeList.get(i);
						//Connection conn=null;
						//DbUtil dbUtil=null;
						WebUtil webUtil=new WebUtil(request,response);
					
						int eff=0;
						
						try{
							//conn=new DBConnect().getConnect();
							//dbUtil=new DbUtil(conn);
							List<ResumeVO> tempResumeVOs=dbUtil.select(ResumeVO.class, "select * from {0} where name_cn=? and idcard=?", resumeVO.getName_cn(),resumeVO.getIdcard());
							//resumeVO=webUtil.evalObject(ResumeVO.class);
							if(tempResumeVOs.size()==0){
								resumeVO.setUuid(UUID.randomUUID().toString());
								resumeVO.setApply_time(applytime);
								resumeVO.setApply_job_id(wantjob);
								resumeVO.setType(jobVO.getType()=="社会招聘"?"0":"1");
								eff+=dbUtil.insert(resumeVO);
							}else{
								ResumeVO temResumeVO=tempResumeVOs.get(0);
						        temResumeVO.setHr_interview_state(resumeVO.getHr_interview_state());
						        temResumeVO.setHr_interview_address(resumeVO.getHr_interview_address());
						        eff+=dbUtil.update(temResumeVO);
							}
					
						}catch(Exception e){
							e.printStackTrace();
						}finally{
							
						}
					}
					
				
				
				out.println("更新用户列表完毕!<BR>");
				
				if (result != null && result.length() > 0) {
					out.println("<br><br>装载非正常结果报告：<BR><font color='red'>");
					out.println(result);
					out.println("</font><br>");
				}

				out.println("<hr>数据装载成功 <a href='formDefine.do?method=formListView&uuid=52d67802-2c6b-48c9-ab5b-5f6b149e0d19&jobid="+wantjob+"'>返回审核页面</a>\"</font>");
				
				
			}
		} catch (Exception e) {
			e.printStackTrace();
			out.println("<font style=\"color:red\">装载处理出现错误:<br/>"+ e.getMessage());
			out.println("<a href=\"resume.do?method=yjUpload\">返回装载页面</a>\"</font>");
			
			Debug.print(Debug.iError, "查询个人项目项目权限失败！", e);
			e.printStackTrace();
		} finally {
			try {
				sl.unlocked(lockmsg, us.getUserLoginId());
				new DbUtil(conn).executeUpdate("drop table tt_k_resume2");
			} catch (Exception e) {
				out.println("撤销并发锁失败：" + e.getMessage() + "<br/>");
			}
			DbUtil.close(conn);
		}

		return null;
		
	
	}
	
}








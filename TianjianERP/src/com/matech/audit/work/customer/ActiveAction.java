package com.matech.audit.work.customer;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.matech.audit.pub.datagrid.DataGrid;
import com.matech.audit.pub.datagrid.DataGridProperty;
import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.customer.ActiveService;
import com.matech.audit.service.customer.CustomerService;
import com.matech.audit.service.customer.model.Active;
import com.matech.audit.service.customer.model.ActiveCompany;
import com.matech.audit.service.customer.model.Business;
import com.matech.audit.service.customer.model.Customer;
import com.matech.audit.service.fileupload.MyFileUpload;
import com.matech.audit.service.placard.PlacardService;
import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.autocode.DELAutocode;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.single.Single;
import com.matech.framework.pub.util.ASFuntion;
import com.matech.framework.pub.util.Debug;
import com.matech.framework.service.excelupload.ExcelUploadService;

public class ActiveAction extends MultiActionController {
	private final String list ="active/List.jsp";
	private final String addAndEdit ="active/AddandEdit.jsp";
	private final String companyList="active/companyList.jsp";
	private final String newCompanyList="active/newCompanyList.jsp";
	private final String companyAddAndEdit ="active/companyAddandEdit.jsp";
	private final String companyUpload="active/companyUpload.jsp";
	
	public ModelAndView list(HttpServletRequest request, HttpServletResponse response) throws Exception{
		ModelAndView modelAndView = new ModelAndView(list);
		
		DataGridProperty pp = new DataGridProperty(); 
		String ppSql  = "";
		ppSql="SELECT AutoId,activeName,activeType,startDate,EndDate,Address,partners FROM k_active " +
				"where 1=1 ${activeName} ${activeType} ${startDate} ${EndDate} ${partners}" ;

		pp.setTableID("active");
		pp.setPageSize_CH(20);
		pp.setCustomerId("");
		pp.setWhichFieldIsValue(1);
		pp.setInputType("radio");
		pp.setOrderBy_CH("autoId");
		pp.setDirection("asc");
		pp.setPrintEnable(true);
	    pp.setPrintVerTical(false);
	    pp.setTrActionProperty(true);
		pp.setTrAction("autoId=${AutoId}");

		pp.setSQL(ppSql);
		pp.addColumn("活动名称","activeName");
		pp.addColumn("活动类型", "activeType");
		pp.addColumn("开始日期","startDate");
		pp.addColumn("结束日期", "EndDate");
		pp.addColumn("地址", "Address");
		pp.addColumn("合作方", "partners");
		
		pp.addSqlWhere("activeName", " and activeName like '%${activeName}%'");
		pp.addSqlWhere("activeType", " and activeType = '${activeType}'");
	 	pp.addSqlWhere("startDate", " and startDate = '${startDate}'");
	 	pp.addSqlWhere("EndDate", " and EndDate ='${EndDate}'");
	 	pp.addSqlWhere("partners", " and partners = '${partners}'");
	
		

 
		request.getSession().setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);
		return modelAndView;
		
	}
	
	public ModelAndView edit(HttpServletRequest request, HttpServletResponse response){
		ModelAndView modelAndView= new ModelAndView(addAndEdit);
		ASFuntion asf = new ASFuntion();
		String id = asf.showNull(request.getParameter("id"));
		Connection conn = null;
		Active active = null;
		try{
			conn = new DBConnect().getConnect("");
			ActiveService activeService = new ActiveService(conn);
			
			if(!id.equals("")){
				 active =  activeService.getActive(id);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		modelAndView.addObject("active",active);
		return modelAndView;
	}
	public ModelAndView add(HttpServletRequest request, HttpServletResponse response) throws IOException{
		ModelAndView modelAndView = new ModelAndView(list);
		ASFuntion asf = new ASFuntion();
		UserSession userSession = (UserSession) request.getSession().getAttribute("userSession"); 
		Active active = new Active();
		String userId = userSession.getUserId();
		String createDepartment = userSession.getUserAuditDepartmentId();
		String createTime = asf.getCurrentDate()+" "+asf.getCurrentTime();
		String activeName = asf.showNull(request.getParameter("activeName"));
		String activeType = asf.showNull(request.getParameter("activeType"));
		String startdate = asf.showNull(request.getParameter("startdate"));
		String enddate = asf.showNull(request.getParameter("enddate"));
		String address = asf.showNull(request.getParameter("address"));
		String partners = asf.showNull(request.getParameter("partners"));
		
		active.setCreateUser(userId);
		active.setCreateDepartmentId(createDepartment);
		active.setCreateTime(createTime);
		active.setActiveName(activeName);
		active.setActiveType(activeType);
		active.setStartDate(startdate);
		active.setEndDate(enddate);
		active.setAddress(address);
		active.setPartners(partners);
		
		Connection conn = null;
		try{
			conn = new DBConnect().getConnect("");
			ActiveService activeService = new ActiveService(conn);
			activeService.save(active);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			response.sendRedirect(request.getContextPath()+"/active.do");
			DbUtil.close(conn);
		}
	
		
		return modelAndView;
	}
	
	public ModelAndView update(HttpServletRequest request, HttpServletResponse response) throws IOException{
		 ModelAndView modelAndView = new ModelAndView(list);
		 ASFuntion asf = new ASFuntion();
		 String autoId = asf.showNull(request.getParameter("autoId"));
		 Connection conn= null;
		 Active active = new Active();
		 String activeName = asf.showNull(request.getParameter("activeName"));
		 String activeType = asf.showNull(request.getParameter("activeType"));
		 String startdate = asf.showNull(request.getParameter("startdate"));
		 String enddate = asf.showNull(request.getParameter("enddate"));
		 String address = asf.showNull(request.getParameter("address"));
		 String partners = asf.showNull(request.getParameter("partners"));
		 
		 active.setAutoId(autoId);
		 active.setActiveName(activeName);
		 active.setActiveType(activeType);
		 active.setStartDate(startdate);
		 active.setEndDate(enddate);
		 active.setAddress(address);
		 active.setPartners(partners);

		 try{
			 conn = new DBConnect().getConnect("");
			 ActiveService activeService = new ActiveService(conn);
			 activeService.update(active);
		 }catch(Exception e){
			 e.printStackTrace();
		 }finally{
			 response.sendRedirect(request.getContextPath()+"/active.do");
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
			 ActiveService activeService = new ActiveService(conn);
			 activeService.delete(autoId);
		 }catch(Exception e){
			 e.printStackTrace();
		 }finally{
			 response.sendRedirect(request.getContextPath()+"/active.do");
			 DbUtil.close(conn);
		 }
		 
		 return modelAndView;
	}
	
	public ModelAndView newCompanyList(HttpServletRequest request, HttpServletResponse response) throws Exception{
		ModelAndView modelAndView = new ModelAndView(newCompanyList);
		ASFuntion asf = new ASFuntion();
		String joinActive = asf.showNull(request.getParameter("active"));
		String id = asf.showNull(request.getParameter("id"));
		if(!"".equals(id)){
			id =" and a.active= '"+id+"'";
		}
		
		String sql="select a.autoId,company, b.activeName as active,custlevel,custsource,indistry,companytype,demandtype,demand,linkman,linkrank," +
				   "linkphone,email,QQorMSN,memo from k_activecompany a left join k_active b on a.active=b.autoId  where 1=1 "+id+" "+joinActive+" ${company} ${active} ${custlevel} ${custsource}"+
					"${indistry} ${companytype} ${demandtype}";
		DataGridProperty pp = new DataGridProperty();
		pp.setTableID("activecompany");
		pp.setPageSize_CH(20);
		pp.setCustomerId("");
		pp.setWhichFieldIsValue(1);
		pp.setInputType("checkbox");
		pp.setOrderBy_CH("autoId");
		pp.setDirection("asc");
		pp.setPrintEnable(true);
	    pp.setPrintVerTical(false);
	    
	    pp.setSQL(sql);
	    pp.addColumn("公司名称", "company");
	    pp.addColumn("参与活动", "active");
	    pp.addColumn("客户级别", "custlevel");
	    pp.addColumn("客户来源", "custsource");
	    pp.addColumn("行业类型", "indistry");
	    pp.addColumn("公司性质", "companytype");
	    pp.addColumn("需求类型", "demandtype");
	    pp.addColumn("具体需求", "demand");
	    pp.addColumn("联系人", "linkman");
	    pp.addColumn("职位", "linkrank");
	    pp.addColumn("电话", "linkphone");
	    pp.addColumn("邮箱", "email");
	    pp.addColumn("QQ/MSN", "QQorMSN");
	    pp.addColumn("备注", "memo");
	    
	    pp.addSqlWhere("company", " and company like '%${company}%'");
	    pp.addSqlWhere("active", " and active = '${active}'");
	    pp.addSqlWhere("custlevel", " and custlevel = '${custlevel}'");
	    pp.addSqlWhere("custsource", " and custsource = '${custsource}'");
	    pp.addSqlWhere("indistry", " and indistry = '${indistry}'");
	    pp.addSqlWhere("companytype", " and companytype = '${companytype}'");
	    pp.addSqlWhere("demandtype", " and demandtype = '${demandtype}'");
	    request.getSession().setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);
		return modelAndView;
	}
	public ModelAndView companyList(HttpServletRequest request, HttpServletResponse response) throws Exception{
		ModelAndView modelAndView = new ModelAndView(companyList);
		ASFuntion asf = new ASFuntion();
		String joinActive = asf.showNull(request.getParameter("active"));
		String id = asf.showNull(request.getParameter("id"));
		if(!"".equals(id)){
			id =" and a.active= '"+id+"'";
		}
		
		String sql="select a.autoId,company, b.activeName as active,custlevel,custsource,indistry,companytype,demandtype,demand,linkman,linkrank," +
				   "linkphone,email,QQorMSN,memo from k_activecompany a left join k_active b on a.active=b.autoId  where 1=1 "+id+" "+joinActive+" ${company} ${active} ${custlevel} ${custsource}"+
					"${indistry} ${companytype} ${demandtype}";
		DataGridProperty pp = new DataGridProperty();
		pp.setTableID("activecompany");
		pp.setPageSize_CH(20);
		pp.setCustomerId("");
		pp.setWhichFieldIsValue(1);
		pp.setInputType("radio");
		pp.setOrderBy_CH("autoId");
		pp.setDirection("asc");
		pp.setPrintEnable(true);
	    pp.setPrintVerTical(false);
	    
	    pp.setSQL(sql);
	    pp.addColumn("公司名称", "company");
	    pp.addColumn("参与活动", "active");
	    pp.addColumn("客户级别", "custlevel");
	    pp.addColumn("客户来源", "custsource");
	    pp.addColumn("行业类型", "indistry");
	    pp.addColumn("公司性质", "companytype");
	    pp.addColumn("需求类型", "demandtype");
	    pp.addColumn("具体需求", "demand");
	    pp.addColumn("联系人", "linkman");
	    pp.addColumn("职位", "linkrank");
	    pp.addColumn("电话", "linkphone");
	    pp.addColumn("邮箱", "email");
	    pp.addColumn("QQ/MSN", "QQorMSN");
	    pp.addColumn("备注", "memo");
	    
	    pp.addSqlWhere("company", " and company like '%${company}%'");
	    pp.addSqlWhere("active", " and active = '${active}'");
	    pp.addSqlWhere("custlevel", " and custlevel = '${custlevel}'");
	    pp.addSqlWhere("custsource", " and custsource = '${custsource}'");
	    pp.addSqlWhere("indistry", " and indistry = '${indistry}'");
	    pp.addSqlWhere("companytype", " and companytype = '${companytype}'");
	    pp.addSqlWhere("demandtype", " and demandtype = '${demandtype}'");
	    request.getSession().setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);
		return modelAndView;
	}
	public ModelAndView companyEdit(HttpServletRequest request, HttpServletResponse response) throws Exception{
		ModelAndView modelAndView = new ModelAndView(companyAddAndEdit);
		ASFuntion asf= new ASFuntion();
		String autoId = asf.showNull(request.getParameter("id"));
		Connection conn = null;
		ActiveCompany activeCompany = null;
		try{
			conn = new DBConnect().getConnect("");
			ActiveService activeService = new ActiveService(conn);
			if(!autoId.equals("")){
				activeCompany = activeService.getCompany(autoId);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			modelAndView.addObject("activeCompany", activeCompany);
			DbUtil.close(conn);
		}
		
		return modelAndView;
	}
	public ModelAndView addcompany(HttpServletRequest request, HttpServletResponse response) throws IOException{
		 ModelAndView modelAndView = new ModelAndView(companyList);
		 ASFuntion asf = new ASFuntion();
		 UserSession userSession = (UserSession) request.getSession().getAttribute("userSession"); 
		 Connection conn = null;
		 
		 ActiveCompany activeCompany = new ActiveCompany();
		 String company = asf.showNull(request.getParameter("company"));
		 String active = asf.showNull(request.getParameter("active"));
		 String custlevel = asf.showNull(request.getParameter("custlevel"));
		 String custsource = asf.showNull(request.getParameter("custsource"));
		 String indistry = asf.showNull(request.getParameter("indistry"));
		 String companytype = asf.showNull(request.getParameter("companytype"));
		 String linkman = asf.showNull(request.getParameter("linkman"));
		 String linkrank = asf.showNull(request.getParameter("linkrank"));
		 String linkphone= asf.showNull(request.getParameter("linkphone"));
		 String email  = asf.showNull(request.getParameter("email"));
		 String demandtype = asf.showNull(request.getParameter("demandtype"));
		 String QQorMSN = asf.showNull(request.getParameter("QQorMSN"));
		 String demand = asf.showNull(request.getParameter("demand"));
		 String memo = asf.showNull(request.getParameter("memo"));
		 
		 activeCompany.setCompany(company);
		 activeCompany.setActive(active);
		 activeCompany.setCustLevel(custlevel);
		 activeCompany.setCustSource(custsource);
		 activeCompany.setIndistry(indistry);
		 activeCompany.setCompanytype(companytype);
		 activeCompany.setLinkman(linkman);
		 activeCompany.setLinkrank(linkrank);
		 activeCompany.setLinkphone(linkphone);
		 activeCompany.setEmail(email);
		 activeCompany.setDemandtype(demandtype);
		 activeCompany.setQQorMSN(QQorMSN);
		 activeCompany.setDemand(demand);
		 activeCompany.setMemo(memo);
		 
		 try{
			 conn = new DBConnect().getConnect("");
			 ActiveService activeService =  new ActiveService(conn);
			 activeService.saveCompany(activeCompany);
			 
			 //查询一下当前公司是否存在客户表
			 String isCut = asf.showNull(new DbUtil(conn).queryForString("SELECT DEPARTiD FROM k_customer WHERE DEPARTnAME = '"+company+"' LIMIT 1 "));

			 if(!"E".equals(custlevel) && !"".equals(isCut)){
				 
				 //新增客户
				 CustomerService customerService =  new CustomerService(conn);
				 DELAutocode t = new DELAutocode();
				String area = "";
				try{
					String departmentid = userSession.getUserAuditDepartmentId();
					String sql = "select a.enname from k_department a,k_department b where b.autoid = ? and a.level0 = 1 and b.fullpath like concat(a.fullpath,'%')";
					area = new DbUtil(conn).queryForString(sql,new String[]{departmentid});
				} catch (Exception e) {
					area = "";
				} 
				String time = asf.replaceStr(asf.replaceStr(asf.getCurrentDate()+asf.getCurrentTime(), "-", ""), ":", "");
				String did2 = t.getAutoCode("ZDBH","",new String[]{area,time}); //单位编号(外部编号)
				
				 Customer customer = new Customer();
				 String departid = t.getAutoCode("KHDH", "");
				 
				 customer.setDepartName(company);
				 customer.setCustdepartid(did2);
				 customer.setDepartId(departid);
				 customer.setLinkMan(linkman);
				 customer.setPhone(linkphone);
				 customer.setDepartmentid(userSession.getUserAuditDepartmentId());
				 customer.setVocationId("1");
				 customer.setProperty("1");
				 customer.setEmail(email);
				 customerService.addCustomer(customer);
				 
				 //加一条市场机会管理记录addCustomer
				 Business business = new Business();
				 
				 String iuser = userSession.getUserId();
				 business.setCustomerId(departid);
				 business.setCustomername(company);
				 business.setCustomerlevel(custlevel);
				 business.setSource(custsource);
				 business.setIndistry(indistry);
				 business.setCompanyType(companytype);
				 business.setDemandType(demandtype);
				 business.setDemand(demand);
				 business.setContact(linkman);
				 business.setRank(linkrank);
				 business.setContactway(linkphone);
				 business.setEmail(email);
				 business.setQQorMSN(QQorMSN);
				 business.setIuser(iuser);
				 
				 //business.setDistriman(distriman);
				 //business.setFollow(follow);
				 customerService.annianSave(business);
			 }
			 
		 }catch(Exception e){
			 e.printStackTrace();
		 }finally{
			 response.sendRedirect(request.getContextPath()+"/active.do?method=companyList");
			 DbUtil.close(conn);
		 }
		 return  modelAndView;
	}
	
	public ModelAndView updateCompany(HttpServletRequest request, HttpServletResponse response) throws IOException{
		   ModelAndView modelAndView = new ModelAndView(companyList);
		   ASFuntion asf = new ASFuntion();
		   String autoId = asf.showNull(request.getParameter("autoId"));
		   Connection conn = null;
		     ActiveCompany activeCompany = new ActiveCompany();
			 String company = asf.showNull(request.getParameter("company"));
			 String active = asf.showNull(request.getParameter("active"));
			 String custlevel = asf.showNull(request.getParameter("custlevel"));
			 String custsource = asf.showNull(request.getParameter("custsource"));
			 String indistry = asf.showNull(request.getParameter("indistry"));
			 String companytype = asf.showNull(request.getParameter("companytype"));
			 String linkman = asf.showNull(request.getParameter("linkman"));
			 String linkrank = asf.showNull(request.getParameter("linkrank"));
			 String linkphone= asf.showNull(request.getParameter("linkphone"));
			 String email  = asf.showNull(request.getParameter("email"));
			 String demandtype = asf.showNull(request.getParameter("demandtype"));
			 String QQorMSN = asf.showNull(request.getParameter("QQorMSN"));
			 String demand = asf.showNull(request.getParameter("demand"));
			 String memo = asf.showNull(request.getParameter("memo"));
			 
			 activeCompany.setAutoId(autoId);
			 activeCompany.setCompany(company);
			 activeCompany.setActive(active);
			 activeCompany.setCustLevel(custlevel);
			 activeCompany.setCustSource(custsource);
			 activeCompany.setIndistry(indistry);
			 activeCompany.setCompanytype(companytype);
			 activeCompany.setLinkman(linkman);
			 activeCompany.setLinkrank(linkrank);
			 activeCompany.setLinkphone(linkphone);
			 activeCompany.setEmail(email);
			 activeCompany.setDemandtype(demandtype);
			 activeCompany.setQQorMSN(QQorMSN);
			 activeCompany.setDemand(demand);
			 activeCompany.setMemo(memo);
			 
		   try{
			   conn = new DBConnect().getConnect("");
			   ActiveService activeService = new ActiveService(conn);
			   activeService.updateCompany(activeCompany);
		   }catch(Exception e){
			   e.printStackTrace();
		   }finally{
			   response.sendRedirect(request.getContextPath()+"/active.do?method=companyList");
			   DbUtil.close(conn);
		   }
		   return modelAndView;
	}
	
	public ModelAndView deleleCompany(HttpServletRequest request, HttpServletResponse response) throws IOException{
		ModelAndView modelAndView = new ModelAndView(companyList);
		ASFuntion asf = new ASFuntion();
		String autoId = asf.showNull(request.getParameter("id"));
		Connection conn = null;
		try{
			 conn = new DBConnect().getConnect("");
			 ActiveService activeService = new ActiveService(conn);
			 activeService.deleteCompany(autoId);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			response.sendRedirect(request.getContextPath()+"/active.do?method=companyList");
			DbUtil.close(conn);
		}
		return modelAndView;
	}
	public ModelAndView upload(HttpServletRequest request, HttpServletResponse response){
		return new ModelAndView(companyUpload);
	}
	public ModelAndView saveUpload(HttpServletRequest request, HttpServletResponse response){
		PrintWriter out = null;
		Connection conn=null;
		Single sl = new Single();
		UserSession us=(UserSession)request.getSession().getAttribute("userSession");
		String lockmsg = "装载帐套数据";
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
				
				ActiveService activeService = new ActiveService(conn);
				out.println("正在分析EXCEL文件......");
				out.flush();
				upload.init();
				out.println("分析EXCEL文件完毕!<BR>");

				//开始装载科目余额表信息
				//首先清空指定表的指定帐套的数据;
				out.println("正在装载用户内容!......");
				out.flush();

				activeService.newTable();
				upload.setExcelNum("");
				upload.setExcelString("公司名称,电话,QQ/MSN");
				//必填设置
				String[] exexlKmye = { "公司名称", "参与活动", "客户级别","客户来源","行业类型","公司性质"};
				//必填对应表字段设置
				String[] tableKmye = { "company", "active", "custlevel","custsource","indistry","companytype" };
				//不必填
				String[] exexlPzmxOpt = { "需求类型", "具体需求", "联系人", "职位","电话", "邮箱","QQ/MSN","备注"};
				String[] tablePzmxOpt = { "demandtype", "demand","linkman", "linkrank", "linkphone", "email","QQorMSN","memo"};
				
				//婚姻状态，籍贯，户口所在地，政治面貌，入党时间，组织关系所在单位，专业，英语能力，CPA号，合同类型,特长
				String[] exexlKmyeFixFields = { ""};
				String[] excelKmyeFixFieldValues = { ""};

				String result = "";

				result = upload.LoadFromExcel("市场活动参会公司", "tt_k_activecompany",
				exexlKmye, tableKmye, exexlPzmxOpt, tablePzmxOpt,
				null, null);

				out.println("装载用户内容完毕!<BR>");

				out.flush();
				out.println("市场活动参会公司列表!......");
				out.flush();
				ASFuntion asf = new ASFuntion();
				String createTime = asf.getCurrentDate()+" "+asf.getCurrentTime();
				activeService.updateDate(us.getUserId(),us.getUserAuditDepartmentId(),createTime);
								activeService.insertData();
				//新增客户
				CustomerService customerService =  new CustomerService(conn);
								
				//查询一下当前公司是否存在客户表
				List<ActiveCompany> listCompany = activeService.getListCompany();
				if(listCompany.size()>0){
					
					try {
						for (int i = 0; i < listCompany.size(); i++) {
							
							ActiveCompany activeCompany = listCompany.get(i);
							
							//在页面打印出要过滤的列
							String customerSource = activeCompany.getCustSource();
							String isCuts = asf.showNull(new DbUtil(conn).queryForString("select name from k_dic where ctype='客户来源' and name = '"+customerSource+"'"));
							if("".equals(isCuts)){
								out.println("非字典里的数据被过滤，客户名称为：【"+activeCompany.getCompany()+"】");
								out.flush();
							}
							String customerLeve = activeCompany.getCustLevel();
							String isCutt = asf.showNull(new DbUtil(conn).queryForString("select name from k_dic where ctype='客户级别' and name = '"+customerLeve + "'"));
							if("".equals(isCutt)){
								out.println("客户级别外的数据被过滤，客户名称为：【"+activeCompany.getCompany()+"】");
								out.flush();
							}
							
							//参与活动
							String  active = activeCompany.getActive();
							String cut = asf.showNull(new DbUtil(conn).queryForString("select autoId from k_active where autoId = '"+active+"'"));
							if("".equals(cut)){
								out.println("参与活动外的数据被过滤，客户名称为：【"+activeCompany.getCompany()+"】");
								out.flush();
							}
							String isCut = asf.showNull(new DbUtil(conn).queryForString("SELECT DEPARTiD FROM k_customer WHERE DEPARTnAME = '"+activeCompany.getCompany()+"' LIMIT 1 "));
							if(!"E".equals(activeCompany.getCustLevel()) && "".equals(isCut)){
								
								//同步更新
								//activeService.insertOA_buseness();
								
								DELAutocode t = new DELAutocode();
								String area = "";
								try{
									String departmentid = userSession.getUserAuditDepartmentId();
									String sql = "select a.enname from k_department a,k_department b where b.autoid = ? and a.level0 = 1 and b.fullpath like concat(a.fullpath,'%')";
									area = new DbUtil(conn).queryForString(sql,new String[]{departmentid});
								} catch (Exception e) {
									area = "";
								} 
								String time = asf.replaceStr(asf.replaceStr(asf.getCurrentDate()+asf.getCurrentTime(), "-", ""), ":", "");
								String did2 = t.getAutoCode("ZDBH","",new String[]{area,time}); //单位编号(外部编号)
								String departid = t.getAutoCode("KHDH", "");
								
									Customer customer = new Customer();
									customer.setCustdepartid(did2);
									customer.setDepartName(activeCompany.getCompany());
									customer.setDepartId(departid);
									customer.setLinkMan(activeCompany.getLinkman());
									customer.setPhone(activeCompany.getLinkphone());
									customer.setDepartmentid(userSession.getUserAuditDepartmentId());
									customer.setVocationId("1");
									customer.setProperty("1");
									customer.setEmail(activeCompany.getEmail());
									customerService.addCustomer(customer);
									
								
								
							}
							
							if(!"E".equals(activeCompany.getCustLevel())){
								//加一条市场机会管理记录addCustomer
								String iuser = userSession.getUserId();
								
								Business business = new Business();
								business.setCustomerId(isCut);
								business.setCustomername(activeCompany.getCompany());
								business.setCustomerlevel(activeCompany.getCustLevel());
								business.setSource(activeCompany.getCustSource());
								business.setIndistry(activeCompany.getIndistry());
								business.setCompanyType(activeCompany.getCompanytype());
								business.setDemandType(activeCompany.getDemandtype());
								business.setDemand(activeCompany.getDemand());
								business.setContact(activeCompany.getLinkman());
								business.setRank(activeCompany.getLinkrank());
								business.setContactway(activeCompany.getLinkphone());
								business.setEmail(activeCompany.getEmail());
								business.setQQorMSN(activeCompany.getQQorMSN());
								business.setIuser(iuser);
								//business.setDistriman(distriman);
								//business.setFollow(follow);
								business.setIuser(us.getUserId());
								if(!"".equals(isCuts) && !"".equals(isCutt) && !"".equals(cut)){
									customerService.annianSave(business);
								}
								
							}
						}
					} catch (Exception e) {
						out.println("错误"+e.getStackTrace());
					}
					
				}
				out.println("更新用户列表完毕!<BR>");
				
				if (result != null && result.length() > 0) {
					out.println("<br><br>装载非正常结果报告：<BR><font color='red'>");
					out.println(result);
					out.println("</font><br>");
				}

				out.println("<hr>数据装载成功 <a href=\"active.do?method=companyList\">返回查询页面</a>\"</font>");
				
				
			}
		} catch (Exception e) {
			e.printStackTrace();
			out.println("<font style=\"color:red\">装载处理出现错误:<br/>"+ e.getMessage());
			out.println("<a href=\"active.do?method=upload\">返回装载页面</a>\"</font>");
			
			Debug.print(Debug.iError, "查询个人项目项目权限失败！", e);
			e.printStackTrace();
		} finally {
			try {
				sl.unlocked(lockmsg, us.getUserLoginId());
				new DbUtil(conn).executeUpdate("drop table tt_k_activecompany");
			} catch (Exception e) {
				out.println("撤销并发锁失败：" + e.getMessage() + "<br/>");
			}
			DbUtil.close(conn);
		}

		return null;
		
	}
	public ModelAndView piLiangDel(HttpServletRequest request, HttpServletResponse response) throws IOException{
		String result = "false";
		Connection conn = null;
		PrintWriter out = null;
		try {
		    out = response.getWriter();
		    response.setCharacterEncoding("utf-8");
		    request.setCharacterEncoding("utf-8");
			UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");
			ASFuntion asf = new ASFuntion();
			String ids = asf.showNull(request.getParameter("ids"));
		
			conn = new DBConnect().getConnect("");
			
			ActiveService activeService = new ActiveService(conn);
			
			//批量删除
				String[] id = ids.split(",");
				
				for (int i = 0; i < id.length; i++) {
					activeService.delAPlacard(id[i]);
				}
				result = "true";
			out.write(result);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		
		return null;
	}
	
	
}

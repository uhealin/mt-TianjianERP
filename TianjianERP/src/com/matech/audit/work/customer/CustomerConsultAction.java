package com.matech.audit.work.customer;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
import com.matech.audit.pub.datagrid.DataGrid;
import com.matech.audit.pub.datagrid.DataGridProperty;
import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.customer.CustomerConsultService;
import com.matech.audit.service.customer.CustomerService;
import com.matech.audit.service.customer.ManagerService1;
import com.matech.audit.service.customer.model.ConsultTxt;
import com.matech.audit.service.customer.model.Customer;
import com.matech.audit.service.customer.model.CustomerConsult;
import com.matech.audit.service.customer.model.Manager1;
import com.matech.audit.service.placard.PlacardService;
import com.matech.audit.service.placard.model.PlacardTable;
import com.matech.audit.service.userpopedom.UserPopedomService;
import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;

public class CustomerConsultAction extends MultiActionController {
	private final String _customerConsultList = "/Customer/CustomerConsultList.jsp";
	private final String _customerConsultEdit = "/Customer/AddcustomerConsult.jsp";
	private final String _consultCount = "/Customer/ConsultCount.jsp";
	private final String _consultCountLook = "/Customer/CustomerConsultLook.jsp";
	ASFuntion CHF = new ASFuntion();

	/**
	 * 显示客户接洽追踪记录
	 * 
	 * @param request
	 * @param response
	 * @return 
	 * @throws Exception
	 */
	public ModelAndView list(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		ModelAndView modelAndView = new ModelAndView(_customerConsultList);
		HttpSession session = request.getSession();

		// 客户编号
		String customerId = request.getParameter("customerid");

		String frameTree = request.getParameter("frameTree");
		String flag = CHF.showNull(request.getParameter("isSolve"));
		String state = "";
		if(flag.equals("no")){
			state ="and a.state<> '已解决' ";
		}
		Connection conn = new DBConnect().getConnect("");
		
		String consultSkip = request.getParameter("consultSkip"); //新增业务咨询记录的跳转
		
		DataGridProperty dgProperty = new DataGridProperty() {
			public void onSearch(javax.servlet.http.HttpSession session,
					javax.servlet.http.HttpServletRequest request,
					javax.servlet.http.HttpServletResponse response)
					throws Exception {

				String customerName = this.getRequestValue("customerName");
				String recorder = this.getRequestValue("recorder");
				String visitTime = this.getRequestValue("visitTime");

				if (customerName != null && !customerName.equals("")) {
					customerName = "and customername like '%" + customerName
							+ "%' ";
				}

				if (recorder != null && !recorder.equals("")) {
					recorder = "and linkman like '%" + recorder + "%' ";
				}

				if (visitTime != null && !visitTime.equals("")) {
					visitTime = "and visitTime like '%" + visitTime + "%' ";
				}

				this.setOrAddRequestValue("customerName", customerName);
				this.setOrAddRequestValue("recorder", recorder);
				this.setOrAddRequestValue("visitTime", visitTime);

			}
		};

		// 客户接洽追踪记录
		dgProperty.setTableID("customerTrackList");
		dgProperty.setInputType("radio");

		dgProperty.setWhichFieldIsValue(1);

		dgProperty.setPrintEnable(true);
		dgProperty.setPrintVerTical(false);

		// 打印的列宽
		dgProperty.setPrintColumnWidth("50,14,17,16,60,15,17,17");
		// 打印的表名
		dgProperty.setPrintTitle("受理咨询列表");

		dgProperty.addColumn("单位名称", "customerName");
		dgProperty.addColumn("联系人", "linkMan");
		//dgProperty.addColumn("QQ/MSN", "QQ");
		//dgProperty.addColumn("电话", "phone");
		//dgProperty.addColumn("EMAIL", "EMAIL");
		dgProperty.addColumn("通讯类型", "ctype");
		dgProperty.addColumn("通讯号", "number");
		dgProperty.addColumn("来访时间", "visitTime");
		dgProperty.addColumn("来访事由", "problem");
		dgProperty.addColumn("责任人", "unfinishManName");
		dgProperty.addColumn("责任人所属部门", "unfinishDepartName");
		dgProperty.addColumn("问题状态", "state");
		dgProperty.addColumn("记录人", "recoderName");
		dgProperty.addColumn("记录时间", "recodeTime");
		UserSession userSession = (UserSession)session.getAttribute("userSession");

		String sql = "";
		if ("".equals(customerId) || customerId == null) {
			sql = "select a.*,IF(a.QQ <>'',a.QQ,IF(a.PHONE <>'',a.PHONE,IF(a.EMAIL <>'',a.EMAIL,'')))  as number, " +
					"IF(a.QQ <>'','QQ/MSN',IF(a.PHONE <>'','电话',IF(a.EMAIL <>'','邮箱',''))) as ctype," +
				   "d.name unfinishManName,e.departname as unfinishDepartName,f.name as recoderName " +
				   "from oa_customerconsult a " +
				   "left join k_customer b on a.customerId = b.departId \n"+
				   "left join k_department c on b.departmentid =c.autoid \n"+
				   "left join k_user d on a.unfinishMan = d.id \n"+
				   "left join k_department e on a.unfinishDepart = e.autoID \n"+
				   "left join k_user f on a.recoder = f.id "+
				   "where 1=1 ${customerName} ${recorder} ${visitTime} ";
			
			String menuid = request.getParameter("menuid"); //菜单ID
			String departments = new UserPopedomService(conn).getUserIdPopedom(userSession.getUserId(), menuid); //部门授权

			sql+=" and (a.unfinishMan='"+userSession.getUserId()+"' or  a.recoder ='"+userSession.getUserId()+"' or  d.departmentId IN ("+departments+")) ";
	        sql+=state;
		} else {
			sql = "select a.*,IF(a.QQ <>'',a.QQ,IF(a.PHONE <>'',a.PHONE,IF(a.EMAIL <>'',a.EMAIL,'')))  as number, " +
					"IF(a.QQ <>'','QQ/MSN',IF(a.PHONE <>'','电话',IF(a.EMAIL <>'','邮箱',''))) as ctype," +
					   "d.name unfinishManName,e.departname as unfinishDepartName,f.name as recoderName " +
					   "from oa_customerconsult a " +
					   "left join k_customer b on a.customerId = b.departId \n"+
					   "left join k_department c on b.departmentid =c.autoid \n"+
					   "left join k_user d on a.unfinishMan = d.id \n"+
					   "left join k_department e on a.unfinishDepart = e.autoID \n"+
					   "left join k_user f on a.recoder = f.id "+
					   "where 1=1  and a.customerid='"+
					   customerId + "' ${customerName} ${recorder} ${visitTime}";
			
			
			String menuid = request.getParameter("menuid"); //菜单ID
			String departments = new UserPopedomService(conn).getUserIdPopedom(userSession.getUserId(), menuid); //部门授权

			sql+=" and (a.unfinishMan='"+userSession.getUserId()+"' or  a.recoder ='"+userSession.getUserId()+"' or  d.departmentId IN ("+departments+")) ";
			sql+=state;

			try{
				CustomerService customerService = new CustomerService(conn);
				String customerName = 	customerService.getCustomerName(customerId);
				request.setAttribute("customerName", customerName);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				DbUtil.close(conn);
			}

			
			request.setAttribute("customerid", customerId);
		}

		dgProperty.setSQL(sql.toString());

		dgProperty.setOrderBy_CH("visitTime");
		dgProperty.setDirection("desc");

		dgProperty.addSqlWhere("customerName", " ${customerName} ");
		dgProperty.addSqlWhere("recorder", " ${recorder} ");
		dgProperty.addSqlWhere("visitTime", " ${visitTime} ");

		
		session.setAttribute(DataGrid.sessionPre + dgProperty.getTableID(),
				dgProperty);
		modelAndView.addObject("flag", flag);
		modelAndView.addObject("frameTree", frameTree);
		modelAndView.addObject("consultSkip", consultSkip);
		modelAndView.addObject("customerId", customerId); //新增业务咨询记录 然后跳转到 发起商机的参数 
		return modelAndView;
	}

	/**
	 * 增加客户接洽追踪记录
	 * 
	 * @param request
	 * @param response
	 * @param customerTrack
	 * @return
	 * @throws Exception
	 */
	public ModelAndView addCustomerTrack(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Connection conn = null;

		// 客户编号
		String customerId = "";
		boolean flag = false;
		ASFuntion asf = new ASFuntion();
		String CustomerID = asf.showNull(request.getParameter("CustomerNumber"));

		String opt = request.getParameter("opt");//跳转 1为会list ,2为发起商机登记
		UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
		
		if ("".equals(CustomerID)) {
			customerId = request.getParameter("customerid");
		} else {
			customerId = CustomerID;
			flag = true;
		}

		try {
			conn = new DBConnect().getConnect("");

			CustomerConsultService customerConsultService = new CustomerConsultService(conn);
					
            PlacardService placardService=new PlacardService(conn); 
			CustomerConsult customerConsult = new CustomerConsult();
			CustomerService customerService = new CustomerService(conn);
			
			
			String customerName = request.getParameter("customerName");
			String linkMan = request.getParameter("linkMan");
			//String QQ = request.getParameter("QQ");
			//String PHONE = CHF.showNull(request.getParameter("PHONE"));
			//String EMAIL = CHF.showNull(request.getParameter("EMAIL"));
			//String linkManTel = CHF.showNull(request.getParameter("linkManTel"));
			String number = CHF.showNull(request.getParameter("number"));
			String linkManEmail = CHF.showNull(request.getParameter("linkManEmail"));
			String ctype = CHF.showNull(request.getParameter("ctype"));
			String visitTime = request.getParameter("visitTime");
			String visitProblem = request.getParameter("visitProblem");
			String pstate = request.getParameter("pstate");
			String finishedTime = request.getParameter("finishedTime");
			String finishMan = request.getParameter("fixPerson");
			String finishedRec = request.getParameter("finishedRec");
			String unfixQuestion = request.getParameter("unfixQuestion");
			String unfixDepartment = request.getParameter("unfixDepartment");
			String customerMainPerson = request.getParameter("customerMainPerson");  //客户承做人
			
			String Department = request.getParameter("Department");
			String Person = request.getParameter("Person");
			
			String unfixPerson = request.getParameter("unfixPerson");
			String untillTime = request.getParameter("untillTime");
			String recoder = request.getParameter("recoder");
			String recodeTime = request.getParameter("recodeTime");
			if(!"".equals(CustomerID)){//隐藏域
				customerConsult.setCustomerName(customerName);
			}else{
				customerId = customerName;
				Customer customer = customerService.getCustomer(customerName);
			    if(customer==null){
			    	 customerConsult.setCustomerName(customerName);
			    }else{
			    	 customerConsult.setCustomerName(customer.getDepartName());
			    }  
				
			}
			customerConsult.setLinkMan(linkMan);
			
			if("邮箱".equals(ctype)){
				customerConsult.setEMAIL(number);
			}else if("电话".equals(ctype)){
				customerConsult.setPHONE(number);
			} else if("QQ/MSN".equals(ctype)){
				customerConsult.setQQ(number);
			}
			
			
			customerConsult.setVisitTime(visitTime);
			customerConsult.setProblem(visitProblem);
			customerConsult.setState(pstate);
			customerConsult.setFinishMan(finishMan);
			customerConsult.setDealTime(finishedTime);
			customerConsult.setFinishRecode(finishedRec);
			customerConsult.setUnfinishProblem(unfixQuestion);
			//customerConsult.setUnfinishDepart(unfixDepartment);
			
			customerConsult.setUnfinishDepart(Department);
			
			customerConsult.setUnfinishMan(Person);
			customerConsult.setUntillTime(untillTime);
			customerConsult.setRecoder(recoder);
			customerConsult.setRecodeTime(recodeTime);
			String nameString=customerConsult.getUnfinishMan();

			// 增加客户接洽追踪记录
			 boolean isSuccess=customerConsultService.addCustomerConsult(customerConsult,customerId);
					

			 //如果不是加载出来的联系人 ，就新增一条联系人
			 if("".equals(customerMainPerson)){
				 ManagerService1 managerService1  = new ManagerService1(conn);
				 Manager1 manager1 = new Manager1();
				 manager1.setCustomerid(customerId);
				 manager1.setName(linkMan);
				 
				 if("邮箱".equals(ctype)){
					 manager1.setEmail(number);
					}else if("电话".equals(ctype)){
						manager1.setMobilephone(number);
						manager1.setFixedphone(number);
					} else if("QQ/MSN".equals(ctype)){
						manager1.setContact1(number);
					}				 
				 	
				 	managerService1.Manageradd(manager1); //新增联系人
			 }
			 
			String recordId = customerConsultService.getMaxAutoId();
			String userId = customerConsultService.getUserByName(recoder);
			String mostlyId = customerConsultService.getCustomerBydepartName(CustomerID);
			String DepartName = customerConsultService.getCustomerIdTransformDepartName(CustomerID);
		
			customerConsultService.getUserByName(recoder);

			ConsultTxt consultTxt = new ConsultTxt();

			consultTxt.setCustomerId(customerId);
			consultTxt.setRecordId(recordId);
			consultTxt.setState(pstate);

			if (!"".equals(finishedRec)) {
				consultTxt.setRecordContent(finishedRec);
			} else {
				consultTxt.setRecordContent(unfixQuestion);
			}

			consultTxt.setDepartment(Department);

			if (!"".equals(finishMan)) {
				consultTxt.setPerson(finishMan);
			} else {
				consultTxt.setPerson(unfixPerson);
			}

			if (!"".equals(finishedTime)) {
				consultTxt.setUntillTime(finishedTime);
			} else {
				consultTxt.setUntillTime(untillTime);
			}

			consultTxt.setManager(recoder);

			customerConsultService.addCousultTxt(consultTxt);
			
			//给老大发信息
			PlacardTable placardTable=new PlacardTable(); 
			placardTable.setAddresser(userId);//发起
			placardTable.setAddresserTime(CHF.getCurrentDate()+" "+CHF.getCurrentTime());
			placardTable.setCaption("客户接洽记录");
			String sbString="客户名称："+DepartName+"<br>单位名称："+customerName+"<br>" +
					        "来访时间："+visitTime+"<br>来访事由："+visitProblem+"<br>" +
					        "问题状态："+pstate;				      
			placardTable.setMatter("'"+sbString+"'");
			placardTable.setAddressee(mostlyId); //接收的老大UserId
			placardTable.setIsRead(0);
			placardTable.setIsReversion(0);
			placardTable.setIsNotReversion(0);
			if(isSuccess==true){
				if(mostlyId!=""){
					placardService.AddPlacard(placardTable); //记录人发消息
					
					placardTable.setAddresser(customerMainPerson);//客户承做人 发消息
					sbString =userSession.getUserName()+"已经分配您处理"+customerName+"" +
								" 咨询的事宜，请于"+untillTime+"之前处理完毕!"+"如有问题请联系："+linkMan+"联系方式："+ctype+" 联系号码："+number;
					placardTable.setMatter("'"+sbString+"'");
					placardService.AddPlacard(placardTable);
				}
				
			}
			

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}

		if("2".equals(opt)){
			//发起商机登记
			response.sendRedirect("/AuditSystem/customerConsult.do?method=list&customerid="+customerId+"&consultSkip=add");
		}else{
			//if (flag) {
				response.sendRedirect("/AuditSystem/customerConsult.do");
				//} else {
				//response.sendRedirect("/AuditSystem/customerConsult.do?customerid="+ customerId);
				//}
		}
		return null;

	} 

	/**
	 * 修改客户追踪记录
	 * 
	 * @param request
	 * @param response
	 * @param customerTrack
	 * @return
	 * @throws Exception
	 */
	public ModelAndView updateCustomerTrack(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Connection conn = null;
		ASFuntion asf= new ASFuntion();

		String flag = asf.showNull(request.getParameter("flag"));
		String customerId = request.getParameter("customerid");
		String setcustomerid = request.getParameter("setcustomerid");
		String opt = request.getParameter("opt");//跳转 1为会list ,2为发起商机登记
		try {
			conn = new DBConnect().getConnect("");
			// 要修改的客户追踪记录的autoid
			String autoid = request.getParameter("autoid");

			CustomerService customerService = new CustomerService(conn);
			CustomerConsult customerConsult = new CustomerConsult();

			String customerName = request.getParameter("customerName");
			String linkMan = request.getParameter("linkMan");
			//String QQ = request.getParameter("QQ");
			//String PHONE = CHF.showNull(request.getParameter("PHONE"));
			//String EMAIL = CHF.showNull(request.getParameter("EMAIL"));
			String linkManTel = CHF.showNull(request.getParameter("linkManTel"));
			String number = CHF.showNull(request.getParameter("number"));
			String ctype = CHF.showNull(request.getParameter("ctype"));
			
			String linkManEmail = CHF.showNull(request.getParameter("linkManEmail"));
			String visitTime = request.getParameter("visitTime");
			String visitProblem = request.getParameter("visitProblem");
			String pstate = request.getParameter("pstate");
			String finishedMan = request.getParameter("fixPerson");
			String finishedTime = request.getParameter("finishedTime");
			String finishedRec = request.getParameter("finishedRec");
			String unfixQuestion = request.getParameter("unfixQuestion");
			String unfixDepartment = request.getParameter("unfixDepartment");
			
			String Department = request.getParameter("Department");
			String Person = request.getParameter("Person");
			
			String unfixPerson = request.getParameter("unfixPerson");
			String untillTime = request.getParameter("untillTime");
			String recoder = request.getParameter("recoder");
			String recodeTime = request.getParameter("recodeTime");

			Customer customer = customerService.getCustomer(customerName);
		    if(customer==null){
		    	 customerConsult.setCustomerName(customerName);
		    }else{
		    	 customerConsult.setCustomerName(customer.getDepartName());
		    }  
			customerConsult.setLinkMan(linkMan);
			if("邮箱".equals(ctype)){
				customerConsult.setEMAIL(number);
			}else if("电话".equals(ctype)){
				customerConsult.setPHONE(number);
			} else if("QQ/MSN".equals(ctype)){
				customerConsult.setQQ(number);
			}
		

			customerConsult.setState(pstate);
			if (pstate.equals("已解决")) {
				customerConsult.setFinishMan(finishedMan);
				customerConsult.setDealTime(finishedTime);
				customerConsult.setFinishRecode(finishedRec);

				customerConsult.setUnfinishProblem("");
				customerConsult.setUnfinishDepart("");
				customerConsult.setUnfinishMan("");
				customerConsult.setUntillTime("");

			} else {

				customerConsult.setFinishMan("");
				customerConsult.setDealTime("");
				customerConsult.setFinishRecode("");

				customerConsult.setUnfinishProblem(unfixQuestion);
				//customerConsult.setUnfinishDepart(unfixDepartment);
				//customerConsult.setUnfinishMan(unfixPerson);
				
				customerConsult.setUnfinishDepart(Department);
				customerConsult.setUnfinishMan(Person);
				
				customerConsult.setUntillTime(untillTime);

			}

			customerConsult.setVisitTime(visitTime);
			customerConsult.setProblem(visitProblem);
			customerConsult.setRecoder(recoder);
			customerConsult.setRecodeTime(recodeTime);

			CustomerConsultService customerConsultService = new CustomerConsultService(
					conn);
			// 修改客户追踪记录
			customerConsultService.updateCustomerTrack(customerConsult, autoid);

			ConsultTxt consultTxt = new ConsultTxt();

			consultTxt.setCustomerId(request.getParameter("setcustomerid"));
			consultTxt.setRecordId(autoid);
			consultTxt.setState(pstate);

			if (!"".equals(finishedRec)) {
				consultTxt.setRecordContent(finishedRec);
			} else {
				consultTxt.setRecordContent(unfixQuestion);
			}

			consultTxt.setDepartment(Department);

			if (!"".equals(finishedMan)) {
				consultTxt.setPerson(finishedMan);
			} else {
				consultTxt.setPerson(unfixPerson);
			}

			if (!"".equals(finishedTime)) {
				consultTxt.setUntillTime(finishedTime);
			} else {
				consultTxt.setUntillTime(untillTime);
			}

			consultTxt.setManager(recoder);

			customerConsultService.addCousultTxt(consultTxt);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}
         System.out.println(flag);
		if("2".equals(opt)){
			//发起商机登记
			response.sendRedirect("/AuditSystem/customer.do?method=businessAdd&isSolve="+flag+"&customerId="+setcustomerid);
		}else{
			if ("".equals(customerId) || customerId == null) {
				response.sendRedirect("/AuditSystem/customerConsult.do?isSolve="+flag);
			} else {
				response.sendRedirect("/AuditSystem/customerConsult.do?isSolve"+flag+"&customerid="+ customerId);
			}
		}
		return null;

	}

	/**
	 * 删除客户追踪记录
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView removeCustomerTrack(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Connection conn = null;

		String customerId = request.getParameter("customerid");
		try {
			conn = new DBConnect().getConnect("");
			// 要删除的客户追踪记录的autoid
			String autoid = request.getParameter("autoid");

			CustomerConsultService customerConsultService = new CustomerConsultService(
					conn);
			// 修改客户追踪记录
			customerConsultService.removeCustomerTrack(autoid);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}

		if ("".equals(customerId) || customerId == null) {
			response.sendRedirect("/AuditSystem/customerConsult.do");
		} else {
			response.sendRedirect("/AuditSystem/customerConsult.do?customerid="
					+ customerId);
		}

		return null;

	}

	/**
	 * 编辑客户追踪记录
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView exitCustomerTrack(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		ModelAndView modelAndView = new ModelAndView(_customerConsultEdit);

		CustomerConsult customerconsult = new CustomerConsult();

		List ConsultTxtList = new ArrayList();
          
		// 要修改的客户追踪记录的autoid
		String autoid = request.getParameter("autoid");
		String flag = request.getParameter("flag");	
		
		Connection conn = null;
		try {
			conn = new DBConnect().getConnect("");

			CustomerConsultService customerConsultService = new CustomerConsultService(
					conn);
			// 根据autoid获得客户追踪记录
			customerconsult = customerConsultService.getCustomerTrack(autoid);

			ConsultTxtList = customerConsultService.getConsultTxts(
					customerconsult.getCustomerid(), autoid);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}
        
		modelAndView.addObject("flag",flag );
		modelAndView.addObject("autoid", autoid);
		modelAndView.addObject("CustomerConsult", customerconsult);
		modelAndView.addObject("ConsultTxtList", ConsultTxtList);

		return modelAndView;
	}
	
	/**
	 * 查看
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView look(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		ModelAndView modelAndView = new ModelAndView(_consultCountLook);

		CustomerConsult customerconsult = new CustomerConsult();

		List ConsultTxtList = new ArrayList();

		// 要修改的客户追踪记录的autoid
		String autoid = request.getParameter("autoid");

		Connection conn = null;
		try {
			conn = new DBConnect().getConnect("");

			CustomerConsultService customerConsultService = new CustomerConsultService(
					conn);
			// 根据autoid获得客户追踪记录
			customerconsult = customerConsultService.getCustomerTrack(autoid);

			ConsultTxtList = customerConsultService.getConsultTxts(
					customerconsult.getCustomerid(), autoid);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}

		modelAndView.addObject("autoid", autoid);
		modelAndView.addObject("CustomerConsult", customerconsult);
		modelAndView.addObject("ConsultTxtList", ConsultTxtList);

		return modelAndView;
	}

	/**
	 * 获得联系方式
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView getLink(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		String linkWay = "";

		try {

			String customerid = request.getParameter("customerid");

			conn = new DBConnect().getConnect("");

			String sql = "select ifnull(linkMan,'')as linkman , ifnull(phone,'')as phone,ifnull(email,'')as email from k_customer where departid = '"
					+ customerid + "'";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();

			while (rs.next()) {
				linkWay = rs.getString("linkman") + "," + rs.getString("phone")
						+ "," + rs.getString("email");
			}

			PrintWriter out = response.getWriter();

			out.println(linkWay);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
			DbUtil.close(conn);
		}

		return null;

	}

	/**
	 * 跟踪记录统计
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView consultCount(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		ModelAndView modelAndView = new ModelAndView(_consultCount);
		HttpSession session = request.getSession();

		String startTime = request.getParameter("startTime");
		String endTime = request.getParameter("endTime");

		//客户接洽次数统计
		DataGridProperty PP = new DataGridProperty();

		PP.setTableID("customerVisitList");

		PP.addColumn("客户编号", "customerid");
		PP.addColumn("单位名称", "customerName");
		PP.addColumn("接洽次数", "countnumber");

		String sql = "";

		if (startTime != null && endTime != null) {
			sql = "select customerid,customerName,count(customerid)as countnumber "
					+ "from oa_customerconsult where visittime>='"
					+ startTime
					+ "' and visittime<='"
					+ endTime
					+ "' "
					+ "group by customerid";
		} else {
			sql = "select customerid,customerName,count(customerid)as countnumber "
					+ "from oa_customerconsult group by customerid";
		}

		PP.setSQL(sql);

		PP.setOrderBy_CH("countnumber");
		PP.setDirection("desc");

		session.setAttribute(DataGrid.sessionPre + PP.getTableID(), PP);

		//人员解决问题统计
		DataGridProperty PP2 = new DataGridProperty();

		PP2.setTableID("personFinishList");

		PP2.addColumn("人员名称", "person");
		PP2.addColumn("所属部门", "departname");
		PP2.addColumn("解决问题数", "finish");
		PP2.addColumn("待解决问题数", "unfinish");

		if (startTime != null && endTime != null) {
			sql = "select a.person,a.countnumber as finish,ifnull(b.countnumber,'0') as unfinish," 
				+ "ifnull(c.departname,'')as departname from ( select person,count(person)as countnumber "
				+ "from  oa_consulttxt  where state = '已解决' "
				+ "and untilltime>='"+startTime+"' and untilltime<='"+endTime+"'"
				+ "group by person) a "
				+ "left join (select person,count(person)as countnumber " 
				+ "from  oa_consulttxt where state = '待处理' group by person) b " 
				+ "on a.person = b.person "
				+ "left join ( select u.name,d.departname from k_user u,k_department d " 
				+ "where d.autoid = u.departmentid) c "
				+ "on a.person=c.name";
		} else {
			sql = "select a.person,a.countnumber as finish,ifnull(b.countnumber,'0') as unfinish," 
				+ "ifnull(c.departname,'')as departname from ( select person,count(person)as countnumber "
				+ "from  oa_consulttxt  where state = '已解决'  group by person) a "
				+ "left join (select person,count(person)as countnumber " 
				+ "from  oa_consulttxt where state = '待处理' group by person) b " 
				+ "on a.person = b.person "
				+ "left join ( select u.name,d.departname from k_user u,k_department d " 
				+ "where d.autoid = u.departmentid) c "
				+ "on a.person=c.name";
		}

		PP2.setSQL(sql);

		PP2.setOrderBy_CH("finish");
		PP2.setDirection("desc");

		session.setAttribute(DataGrid.sessionPre + PP2.getTableID(), PP2);
		
		//部门解决问题统计
		DataGridProperty PP3 = new DataGridProperty();

		PP3.setTableID("departFinishList");

		PP3.addColumn("部门名称", "department");
		PP3.addColumn("解决问题数", "finish");
		PP3.addColumn("待解决问题数", "unfinish");

		if (startTime != null && endTime != null) {
			sql = "select a.*,b.unfinish from(select department,count(department)as finish "
				+ "from oa_consulttxt " 
				+ "where state = '已解决' " 
				+ "and untilltime>='"+startTime+"' and untilltime<='"+endTime+"'"
				+ "group by department)a " 
				+ "inner join(select department,count(department)as unfinish " 
				+ "from oa_consulttxt " 
				+ "where state = '待处理' " 
				+ "group by department)b " 
				+ "on a.department = b.department";
		} else {
			sql = "select a.*,b.unfinish from(select department,count(department)as finish "
				+ "from oa_consulttxt " 
				+ "where state = '已解决' " 
				+ "group by department)a " 
				+ "inner join(select department,count(department)as unfinish " 
				+ "from oa_consulttxt " 
				+ "where state = '待处理' " 
				+ "group by department)b " 
				+ "on a.department = b.department";
		}

		PP3.setSQL(sql);

		PP3.setOrderBy_CH("finish");
		PP3.setDirection("desc");

		session.setAttribute(DataGrid.sessionPre + PP3.getTableID(), PP3);
		
		//解决时间最长问题统计
		DataGridProperty PP4 = new DataGridProperty();

		PP4.setTableID("finishTimeList");

		PP4.addColumn("问题事由", "recordconent");
		PP4.addColumn("记录人", "person");
		PP4.addColumn("解决人", "manager");
		PP4.addColumn("解决时间", "untilltime");
		PP4.addColumn("总共用时(天)", "totaltime");
		
		if (startTime != null && endTime != null) {
			sql = "select a. recordconent,a.person,a.untilltime,a.manager,a.untilltime-b.recodetime as totaltime from " 
				+ "(select recordconent,person,max(untilltime)as untilltime,manager,recordid "
				+ "from oa_consulttxt "
				+ "where state = '已解决' "
				+ "and untilltime>='"+startTime+"' and untilltime<='"+endTime+"' "
				+ "group by recordid)a "
				+ "inner join "
				+ "(select autoid,recodetime from oa_customerconsult)b "
				+ "on a.recordid = b.autoid ";
		} else {
			sql = "select a. recordconent,a.person,a.untilltime,a.manager,a.untilltime-b.recodetime as totaltime from " 
				+ "(select recordconent,person,max(untilltime)as untilltime,manager,recordid "
				+ "from oa_consulttxt "
				+ "where state = '已解决' "
				+ "group by recordid)a "
				+ "inner join "
				+ "(select autoid,recodetime from oa_customerconsult)b "
				+ "on a.recordid = b.autoid ";

		}

		PP4.setSQL(sql);

		PP4.setOrderBy_CH("totaltime");
		PP4.setDirection("desc");

		session.setAttribute(DataGrid.sessionPre + PP4.getTableID(), PP4);
		
		//未解决时间最长问题统计
		DataGridProperty PP5 = new DataGridProperty();

		PP5.setTableID("unFinishTimeList");

		PP5.addColumn("问题事由", "recordconent");
		PP5.addColumn("记录人", "person");
		PP5.addColumn("分工人", "manager");
		PP5.addColumn("累积时间(天)", "totaltime");
		
		if (startTime != null && endTime != null) {
			sql = "select a. recordconent,a.person,a.untilltime,a.manager,a.untilltime-b.recodetime as totaltime from" 
				+ " (select recordconent,person,max(untilltime)as untilltime,manager,recordid"
				+ " from oa_consulttxt"
				+ " where state = '待处理'"
				+ " and untilltime>='"+startTime+"' and untilltime<='"+endTime+"' "
				+ " group by recordid)a"
				+ " inner join"
				+ " (select autoid,recodetime from oa_customerconsult)b"
				+ " on a.recordid = b.autoid";
		} else {
			sql = "select a. recordconent,a.person,a.untilltime,a.manager,a.untilltime-b.recodetime as totaltime from" 
				+ " (select recordconent,person,max(untilltime)as untilltime,manager,recordid"
				+ " from oa_consulttxt"
				+ " where state = '待处理'"
				+ " group by recordid)a"
				+ " inner join"
				+ " (select autoid,recodetime from oa_customerconsult)b"
				+ " on a.recordid = b.autoid";
		}

		PP5.setSQL(sql);

		PP5.setOrderBy_CH("totaltime");
		PP5.setDirection("desc");

		session.setAttribute(DataGrid.sessionPre + PP5.getTableID(), PP5);

		return modelAndView;
	}
	
	/**
	 * 自动填充客户信息
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView autoFill(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Connection conn = null;

		ASFuntion CHF = new ASFuntion();
		
		PrintWriter out = null;
		
		try {
			
			String linkManTel = CHF.showNull(request.getParameter("linkManTel"));
			String ctype = CHF.showNull(request.getParameter("ctype"));
			
			conn = new DBConnect().getConnect("");
			
			CustomerConsultService ccs = new CustomerConsultService(conn);
			
			String customerInfo = ccs.autoFill(linkManTel);
			
			response.setContentType("text/html;charset=utf-8");
			
			out = response.getWriter();
			
			if(customerInfo!=null) {
				out.write(customerInfo);
			} else {
				out.write("false");
			}

			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			out.close();
			DbUtil.close(conn);
		}

		return null;

	}
	
	/**
	 * 根据 同学类型 得到 客户信息
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView customerFill(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Connection conn = null;
		ASFuntion CHF = new ASFuntion();
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = null;
		
		try {
			
			String number = CHF.showNull(request.getParameter("number"));
			String ctype = CHF.showNull(request.getParameter("ctype"));
			
			conn = new DBConnect().getConnect("");
			CustomerConsultService ccs = new CustomerConsultService(conn);
			
			if("邮箱".equals(ctype)){
				ctype = "email";
			}else if("电话".equals(ctype)){
				ctype = "fixedphone";
			}else if("QQ/MSN".equals(ctype)){
				ctype = "contact1";
			}
			
			String customerInfo = ccs.customerFill(ctype, number);
			
			out = response.getWriter();
			
			if(customerInfo!=null) {
				out.write(customerInfo);
			} else {
				out.write("false");
			}

			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			out.close();
			DbUtil.close(conn);
		}

		return null;

	}
	
	
}

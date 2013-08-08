package com.matech.audit.work.customer;

import java.io.PrintWriter;
import java.sql.Connection;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.matech.audit.pub.datagrid.DataGrid;
import com.matech.audit.pub.datagrid.DataGridProperty;
import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.customer.CustomerNoteService;
import com.matech.audit.service.customer.model.CustomerNote;
import com.matech.audit.service.fileupload.MyFileUpload;
import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.autocode.DELUnid;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;

public class CustomerNoteAction extends MultiActionController {
	private static final String LIST_VIEW = "Customer/CustomerNoteList.jsp";

	private static final String EDIT_VIEW = "Customer/CustomerNoteEdit.jsp";
	
	private static final String _noteCount = "/Customer/NoteCount.jsp";
	
	private static final String _noteCountLook = "/Customer/CustomerNoteLook.jsp";

	/**
	 * 客户走访记录列表
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView list(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		response.setContentType("text/html;charset=utf-8");

		HttpSession session = request.getSession();
		ModelAndView modelAndView = new ModelAndView();
		String departid = new ASFuntion().showNull(request.getParameter("departid"));
		DataGridProperty dgProperty = new DataGridProperty() {
			public void onSearch(javax.servlet.http.HttpSession session,
					javax.servlet.http.HttpServletRequest request,
					javax.servlet.http.HttpServletResponse response)
					throws Exception {

				String customerName = this.getRequestValue("customerName");
				String recorder = this.getRequestValue("recorder");
				String visitTime = this.getRequestValue("visitTime");

				if (customerName != null && !customerName.equals("")) {
					customerName = "and departname like '%" + customerName + "%' ";
				}

				if (recorder != null && !recorder.equals("")) {
					recorder = "and a.linkman like '%" + recorder + "%' ";
				}

				if (visitTime != null && !visitTime.equals("")) {
					visitTime = "and notetime like '%" + visitTime + "%' ";
				}

				this.setOrAddRequestValue("customerName", customerName);
				this.setOrAddRequestValue("recorder", recorder);
				this.setOrAddRequestValue("visitTime", visitTime);

			}
		};
		
		dgProperty.setTableID("customerNoteList");
		dgProperty.setCurProjectDatabase(true);
		dgProperty.setInputType("radio");
		dgProperty.setWhichFieldIsValue(1);
		dgProperty.setPrintEnable(true);
		dgProperty.setPrintTitle("客户走访记录");
		
		dgProperty.addColumn("客户名称", "departname");
		dgProperty.addColumn("客户联系人", "linkman");
		dgProperty.addColumn("服务类型", "type0");
		dgProperty.addColumn("服务时间", "notetime");
		dgProperty.addColumn("服务主题", "title");
		//dgProperty.addColumn("服务内容", "content");
//		dgProperty.addColumn("附件名称", "filepath",null,"com.matech.audit.work.customer.NoteProcess",null);
		dgProperty.addColumn("记录人", "username");
		dgProperty.addColumn("记录时间", "udate");

//		dgProperty.setTrActionProperty(true);
//		dgProperty.setTrAction("autoId='${autoId}' style='cursor:hand;' onDBLclick=\"viewNote(this);\"");

		StringBuffer sql = new StringBuffer();
		sql.append(" select autoid,customerid,a.linkman,notetime,title,content,filename,filepath,username,udate,departname,a.type0 ");
		sql.append(" from oa_customernote a ");
		sql.append(" left join k_customer b on a.customerId=b.departid where 1=1 ${customerName} ${recorder} ${visitTime}");
		if(!"".equals(departid)){
			sql.append("  and a.customerid='"+departid+"' \n ");
		}
		dgProperty.setSQL(sql.toString());
		dgProperty.setOrderBy_CH("autoid");
		dgProperty.setDirection("asc");
			
		dgProperty.addSqlWhere("customerName", " ${customerName} ");
		dgProperty.addSqlWhere("recorder", " ${recorder} ");
		dgProperty.addSqlWhere("visitTime", " ${visitTime} ");
		
		session.setAttribute(DataGrid.sessionPre + dgProperty.getTableID(),dgProperty);

		modelAndView.setViewName(LIST_VIEW);
		String fileDirPath = CustomerNoteService.NOTE_FILE_PATH;
		
		modelAndView.addObject("fileDirPath", fileDirPath);
		
		return modelAndView;
	}

	/**
	 * 编辑走访记录
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView edit(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		ModelAndView modelAndView = new ModelAndView(EDIT_VIEW);

		HttpSession session = request.getSession();
		UserSession userSession = (UserSession) session
				.getAttribute("userSession");

		ASFuntion funtion = new ASFuntion();

		String autoId = funtion.showNull(request.getParameter("autoId"));

		if (!"".equals(autoId)) {
			Connection conn = null;

			try {
				conn = new DBConnect().getConnect("");
				CustomerNoteService customerNoteService = new CustomerNoteService(conn);
				CustomerNote customerNote = customerNoteService.getCustomerNote(autoId);

				modelAndView.addObject("autoId", customerNote.getAutoId());// 自动编号
				modelAndView.addObject("customerId", customerNote.getCustomerId());// 客户编号
				modelAndView.addObject("linkMan", customerNote.getLinkMan());// 联系人
				modelAndView.addObject("title", customerNote.getTitle());// 标题
				modelAndView.addObject("content", customerNote.getContent());// 内容
				modelAndView.addObject("userName", customerNote.getUserName());// 记录人
				modelAndView.addObject("noteTime", customerNote.getNoteTime());// 走访时间
				modelAndView.addObject("udate", customerNote.getUdate());// 记录时间
				modelAndView.addObject("fileName", customerNote.getFileName());// 记录时间
				modelAndView.addObject("type0", customerNote.getType0());// 记录时间
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				DbUtil.close(conn);
			}

		} else {
			modelAndView.addObject("userName", userSession.getUserName());
		}

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

		ModelAndView modelAndView = new ModelAndView(_noteCountLook);

		HttpSession session = request.getSession();
		UserSession userSession = (UserSession) session
				.getAttribute("userSession");

		ASFuntion funtion = new ASFuntion();

		String autoId = funtion.showNull(request.getParameter("autoId"));

		if (!"".equals(autoId)) {
			Connection conn = null;

			try {
				conn = new DBConnect().getConnect("");
				CustomerNoteService customerNoteService = new CustomerNoteService(conn);
				CustomerNote customerNote = customerNoteService.getCustomerNote(autoId);

				modelAndView.addObject("autoId", customerNote.getAutoId());// 自动编号
				modelAndView.addObject("customerId", customerNote.getCustomerId());// 客户编号
				modelAndView.addObject("linkMan", customerNote.getLinkMan());// 联系人
				modelAndView.addObject("title", customerNote.getTitle());// 标题
				modelAndView.addObject("content", customerNote.getContent());// 内容
				modelAndView.addObject("userName", customerNote.getUserName());// 记录人
				modelAndView.addObject("noteTime", customerNote.getNoteTime());// 走访时间
				modelAndView.addObject("udate", customerNote.getUdate());// 记录时间
				modelAndView.addObject("fileName", customerNote.getFileName());// 记录时间
				modelAndView.addObject("type0", customerNote.getType0());// 记录时间
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				DbUtil.close(conn);
			}

		} else {
			modelAndView.addObject("userName", userSession.getUserName());
		}

		return modelAndView;
	}

	/**
	 * 保存走访记录
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView save(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		response.setContentType("text/html;charset=utf-8");

		response.setHeader("Pragma","No-cache");
		response.setHeader("Cache-Control","no-cache");
		response.setDateHeader("Expires", 0);

		MyFileUpload myfileUpload = new MyFileUpload(request);

		String randomFileName = DELUnid.getNumUnid();
		
		String opt = request.getParameter("opt");
		System.out.println("opt="+opt);
		
		myfileUpload.UploadFile(randomFileName, CustomerNoteService.NOTE_FILE_PATH);

		Map parmsMap = myfileUpload.getMap();

		String fileName = (String)parmsMap.get("fileName");

		Connection conn = null;
		PrintWriter printWriter = response.getWriter();
		String message = "";

		try {
			conn = new DBConnect().getConnect("");
			CustomerNote customerNote = new CustomerNote();
			customerNote.setFileName(fileName);
			customerNote.setFilePath(randomFileName);
			customerNote.setCustomerId((String)parmsMap.get("customerId"));
			customerNote.setContent((String)parmsMap.get("content"));
			customerNote.setTitle((String)parmsMap.get("title"));
			customerNote.setLinkMan((String)parmsMap.get("linkMan"));
			customerNote.setNoteTime((String)parmsMap.get("noteTime"));
			customerNote.setUserName((String)parmsMap.get("userName"));
			
			customerNote.setType0((String)parmsMap.get("type0"));
			
			CustomerNoteService customerNoteService = new CustomerNoteService(conn);
			customerNoteService.saveCustomerNote(customerNote);

			message = "保存走访记录成功!!";

		} catch (Exception e) {
			message = "保存走访记录失败:" + e.getMessage();
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}
		if(opt !=null && "2".equals(opt)){
			printWriter.write(getJavaScript(message,(String)parmsMap.get("customerId")));
		}else{
			printWriter.write(getJavaScript(message));
		}
		return null;
	}

	/**
	 * 更新走访记录
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView update(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		response.setContentType("text/html;charset=utf-8");

		response.setHeader("Pragma","No-cache");
		response.setHeader("Cache-Control","no-cache");
		response.setDateHeader("Expires", 0);

		String autoId = request.getParameter("autoId");
		
		String opt = request.getParameter("opt");//跳转 1为会list ,2为发起商机登记
		
		Connection conn = null;
		PrintWriter printWriter = response.getWriter();
		String message = "";

		try {
			conn = new DBConnect().getConnect("");
			CustomerNoteService customerNoteService = new CustomerNoteService(conn);

			CustomerNote customerNote = customerNoteService.getCustomerNote(autoId);

			customerNote.setCustomerId(request.getParameter("customerId"));
			customerNote.setContent(request.getParameter("content"));
			customerNote.setTitle(request.getParameter("title"));
			customerNote.setLinkMan(request.getParameter("linkMan"));
			customerNote.setNoteTime(request.getParameter("noteTime"));
			customerNote.setFileName(request.getParameter("fileName"));

			customerNote.setType0(request.getParameter("type0"));
			
			customerNoteService.updateCustomerNote(customerNote);

			message = "更新走访记录成功!!";

		} catch (Exception e) {
			message = "更新走访记录失败:" + e.getMessage();
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}
		if("2".equals(opt)){
			printWriter.write(getJavaScript(message,request.getParameter("customerId")));
		}else{
			printWriter.write(getJavaScript(message));
		}
		return null;
	}

	/**
	 * 删除走访记录
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView remove(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		response.setContentType("text/html;charset=utf-8");

		response.setHeader("Pragma","No-cache");
		response.setHeader("Cache-Control","no-cache");
		response.setDateHeader("Expires", 0);

		String autoId = request.getParameter("autoId");

		Connection conn = null;
		PrintWriter printWriter = response.getWriter();
		String message = "";

		try {
			conn = new DBConnect().getConnect("");
			CustomerNoteService customerNoteService = new CustomerNoteService(conn);

			customerNoteService.removeCustomerNote(autoId);

			message = "删除走访记录成功!!";

		} catch (Exception e) {
			message = "删除走访记录失败:" + e.getMessage();
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}

		printWriter.write(getJavaScript(message));
		return null;
	}

	/**
	 * 弹出信息,并返回到客户走访列表
	 * @param message
	 * @return
	 */
	private String getJavaScript(String message) {
		StringBuffer stringBuffer = new StringBuffer();

		stringBuffer.append("<script>");
		stringBuffer.append("alert('" + message +"');");
		stringBuffer.append("window.location='/AuditSystem/customerNote.do';");
		stringBuffer.append("</script>");

		return stringBuffer.toString();
	}
	
	private String getJavaScript(String message,String customerId) {
		StringBuffer stringBuffer = new StringBuffer();

		stringBuffer.append("<script>");
		stringBuffer.append("alert('" + message +"');");
		if(!"".equals(customerId)){
			stringBuffer.append("window.location='/AuditSystem/customer.do?method=businessAdd&customerId="+customerId+"';");
		}else{
			stringBuffer.append("window.location='/AuditSystem/customerNote.do';");
		}
		stringBuffer.append("</script>");

		return stringBuffer.toString();
	}
	/**
	 * 走访记录统计
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView noteCount(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		ModelAndView modelAndView = new ModelAndView(_noteCount);
		HttpSession session = request.getSession();

		String startTime = request.getParameter("startTime");
		String endTime = request.getParameter("endTime");

		//客户接洽次数统计
		DataGridProperty PP = new DataGridProperty();
	
		PP.setTableID("customerVisitList");
		
		PP.setPrintColumnWidth("15,50,20,30,30,35");
		// 打印的表名
		PP.setPrintTitle("客户走访统计");
		
		PP.setPrintEnable(true);
		PP.setPrintVerTical(false);

		PP.addColumn("客户编号", "customerid");
		PP.addColumn("单位名称", "departname");
		PP.addColumn("客户等级", "customerlevel");
		PP.addColumn("客户服务次数", "visitcount");
		PP.addColumn("客户接洽次数", "countnumber");
		PP.addColumn("客户未解决问题数", "countunfinish");

		String sql = "";

		if (startTime != null && endTime != null) {
			sql = " select distinct a.customerid as customerid,a.notetime,departname ,ifnull(customerlevel,'')as customerlevel,"
				+ " ifnull(c.visitcount,'0')as visitcount,ifnull(d.countnumber,'0')as countnumber,"
				+ " ifnull(e.countunfinish,'0')as countunfinish "
				+ " from oa_customernote a "
				+ " left join k_customer b "
				+ " on a.customerId=b.departid "
				+ " left join " 
				+ " ("
				+ "  select customerid,count(customerid) as visitcount from oa_customernote "
				+ "  where notetime>='"+startTime+"' and notetime<='"+endTime+"'"
				+ "  group by customerid "	
				+ " )c "
				+ " on c.customerid = a.customerid "
				+ " left join " 
				+ " ("
				+ "  select customerid,count(customerid)as countnumber " 
				+ "  from oa_customerconsult "
				+ "  where untilltime>='"+startTime+"' and untilltime<='"+endTime+"'"
				+ "  group by customerid " 
				+ " )d "
				+ " on d.customerid = a.customerid "
				+ " left join " 
				+ " ("
				+ "  select customerid,count(customerid)as countunfinish " 
				+ "  from  oa_consulttxt where state = '待处理' " 
				+ "  and untilltime>='"+startTime+"' and untilltime<='"+endTime+"'"
				+ "  group by customerid " 
				+ " )e "
				+ " on e.customerid = a.customerid"
			    + " where a.notetime>='"+startTime+"' and a.notetime<='"+endTime+"'";
		} else {
			sql = " select distinct a.customerid as customerid,departname ,ifnull(customerlevel,'')as customerlevel,"
				+ " ifnull(c.visitcount,'0')as visitcount,ifnull(d.countnumber,'0')as countnumber,"
				+ " ifnull(e.countunfinish,'0')as countunfinish "
				+ " from oa_customernote a "
				+ " left join k_customer b "
				+ " on a.customerId=b.departid "
				+ " left join " 
				+ " ("
				+ "  select customerid,count(customerid) as visitcount from oa_customernote "
				+ "  group by customerid "	
				+ " )c "
				+ " on c.customerid = a.customerid "
				+ " left join " 
				+ " ("
				+ "  select customerid,count(customerid)as countnumber " 
				+ "  from oa_customerconsult group by customerid " 
				+ " )d "
				+ " on d.customerid = a.customerid "
				+ " left join " 
				+ " ("
				+ "  select customerid,count(customerid)as countunfinish " 
				+ "  from  oa_consulttxt where state = '待处理' group by customerid " 
				+ " )e "
				+ " on e.customerid = a.customerid";
		}

		PP.setSQL(sql);

		PP.setOrderBy_CH("visitcount");
		PP.setDirection("desc");

		session.setAttribute(DataGrid.sessionPre + PP.getTableID(), PP);

		return modelAndView;
	}
	
}

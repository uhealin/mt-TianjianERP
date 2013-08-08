package com.matech.audit.work.form;

import java.io.PrintWriter;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.matech.audit.pub.datagrid.DataGridProperty;
import com.matech.audit.pub.datagrid.ExtGrid;
import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.form.FormButtonExtInterface;
import com.matech.audit.service.form.FormButtonExtService;
import com.matech.audit.service.form.FormDefineService;
import com.matech.audit.service.form.FormQueryConfigService;
import com.matech.audit.service.form.GenFormService;
import com.matech.audit.service.form.model.FormButton;
import com.matech.audit.service.form.model.FormDefine;
import com.matech.audit.service.form.model.FormQuery;
import com.matech.audit.service.form.model.FormVO;
import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ClassUtil;
import com.matech.framework.pub.util.StringUtil;
import com.matech.framework.pub.util.WebUtil;

public class FormQueryConfigAction extends MultiActionController {

	private String QUERY_EDIT_VIEW = "form/queryEdit.jsp";
	private String BUTTON_EDIT_VIEW = "form/buttonEdit.jsp";
	private String BUTTON_LIST_VIEW = "form/buttonList.jsp";

	/**
	 * 修改表单字段页面
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView queryConfigEdit(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Connection conn = null;
		ModelAndView modelAndView = new ModelAndView(QUERY_EDIT_VIEW);
		List<FormQuery> formQueryFeildList = null;
		List<String> list = new ArrayList<String>();
		String formId = request.getParameter("formId");
		//标识是否点击页面“执行sql更新字段名”按钮，listSql不为空表示已点击，否则照常执行
		String listSql = StringUtil.showNull(request.getParameter("listSql"));
		String listHtml = StringUtil.showNull(request.getParameter("listHtml"));
		String tempSql=listSql.replace("${userPopedom}", "''");
		try {
			conn = new DBConnect().getConnect();
			FormQueryConfigService updateFormService = new FormQueryConfigService(conn);
			if (!listSql.equals("")) {
				formQueryFeildList = updateFormService.updateNameBySql(formId, tempSql);
			} else {
				formQueryFeildList = updateFormService.getformQuery(formId);
				list = updateFormService.findListSql(formId);
				listSql = list.get(0);
				listHtml = list.get(1);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}
		
		modelAndView.addObject("formQueryFeildList", formQueryFeildList);
		modelAndView.addObject("listSql", listSql);
		modelAndView.addObject("formId", formId);
		modelAndView.addObject("listHtml", listHtml);
		
		return modelAndView;
	}

	/**
	 * 保存表单字段
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView saveField(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Connection conn = null;
		
		String formId = request.getParameter("formId");
		String listSql = StringUtil.showNull(request.getParameter("listSql"));
		String listHtml = StringUtil.showNull(request.getParameter("listHtml"));
		String thead =StringUtil.showNull(request.getParameter("thead"));
		FormDefine formDefine = null;
		
		try {
			
			conn = new DBConnect().getConnect();
			formDefine = new FormDefineService(conn).getFormDefine(formId);
			FormQueryConfigService updateFormService = new FormQueryConfigService(
					conn);
			updateFormService.removeAllField(formId);
			
			String[] enname = request.getParameterValues("enname");
			String[] uuid = request.getParameterValues("uuid");
			String[] name = request.getParameterValues("name");
			String[] bshow = request.getParameterValues("bshow");
			String[] bhiddenrow = request.getParameterValues("bhiddenrow");
			String[] border = request.getParameterValues("border");
			String[] orderid = request.getParameterValues("orderid");
			String[] btype = request.getParameterValues("btype");
			String[] rowFlag = request.getParameterValues("rowFlag");
			String[] width = request.getParameterValues("width");
			
			for (int i = 0; enname !=null && i < enname.length; i++) {
				FormQuery formQuery = new FormQuery();
				formQuery.setUuid(uuid[i]);
				formQuery.setEnname(enname[i]);//数据库字段名
				formQuery.setFormid(formId);
				formQuery.setName(name[i]); // 字段显示名
				formQuery.setBshow(Integer.parseInt(bshow[i])); // 是否在列表显示
				formQuery.setBhiddenrow(Integer.parseInt(bhiddenrow[i])); // 是否放到trproperty（隐藏域）
				formQuery.setOrderid(Integer.parseInt(orderid[i]));//字段间排序
				formQuery.setBorder(Integer.parseInt(border[i])); // 字段内排序，大于0升序，小于0降序
				formQuery.setBtype(btype[i]); // 字段类型
				formQuery.setRowFlag(rowFlag[i]); // 标识值
				formQuery.setWidth(width[i]) ;
				
				if(!"".equals(enname[i])) {
					//更新
					updateFormService.addFormField(formQuery);
				}
			}
			//更新Form表的listSQL字段、listHtml字段，插入sql
			updateFormService.updateListSql(formId,listSql,listHtml);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}


		response.sendRedirect(request.getContextPath()
				+ "/formDefine.do?method=formList&formTypeId=" + formDefine.getFormType());

		return null;
	}

	/**
	 * 修改表单按钮页面
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView updateButtonsList(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Connection conn = null;
		ModelAndView modelAndView = new ModelAndView(BUTTON_LIST_VIEW);
		String formId = request.getParameter("formId");
		try {
			String sql = " SELECT uuid,name,enname,formid,orderid,icon, \n"
					+ "if(aftergroup = 0, '不追加', '追加') aftergroup, \n"
					+ "if(buttonType = '0', '列表按钮', '表单按钮') buttonType, \n"
					+ "onclick,extjs FROM MT_COM_FORM_BUTTON where formid ='"
					+ formId + "'";
			DataGridProperty pp = new DataGridProperty();
			pp.setTableID("formButton");
			pp.setCustomerId("");// 暂时没有用，但是一定要写
			pp.setColumnWidth("10,10,11,12,15");// 设置列宽
			pp.setPageSize_CH(50); // 设置每页显示的条数
			pp.setWhichFieldIsValue(1);// 设置第几行为获取项,对应的是sql语句的第几个字段,这列会以隐藏表单域的形式传过去
			pp.setInputType("radio");// 是用复选框显示还是用单选框显示radio checkBox
			// 向datagvid中添加列
			pp.addColumn("按钮名", "name");
			pp.addColumn("英文名", "enname");
			pp.addColumn("按钮类型", "buttonType");
			pp.addColumn("图标", "icon");
			pp.addColumn("是否追加|分隔", "aftergroup");
			pp.addColumn("点击事件", "onclick");
			pp.addColumn("顺序", "orderid");

			// 显示数据时按照orderid列降序排列
			pp.setOrderBy_CH("orderid");
			pp.setDirection("asc");

			pp.setSQL(sql);
			request.getSession().setAttribute(
					ExtGrid.sessionPre + pp.getTableID(), pp);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}
		request.setAttribute("formId", formId);
		return modelAndView;
	}

	/**
	 * 
	 * 保存表单按钮
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView saveButtons(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Connection conn = null;
		String uuid = request.getParameter("uuid");// uuid
		String name = request.getParameter("name");// 按钮中文名
		String enname = request.getParameter("enname");// 按钮英文名
		String formId = request.getParameter("formId");// formId
		String orderId = request.getParameter("orderId");// 顺序
		String icon = request.getParameter("icon");// 图标
		String aftergroup = request.getParameter("afterGroup");// 是否追加 |
		String onClick = request.getParameter("onClick");// 点击事件
		String extJs = request.getParameter("extJs");// 扩展函数
		
		String className = request.getParameter("className");// 扩展函数
		String sql = request.getParameter("sql");// 扩展函数
		String handleType = request.getParameter("handleType");
		String buttonType = request.getParameter("buttonType");
		
		String beforeClick = request.getParameter("beforeClick");
		String beforeClickJs = request.getParameter("beforeClickJs");
		
		String afterClick = request.getParameter("afterClick");
		String afterClickJs = request.getParameter("afterClickJs");
		
		try {
			conn = new DBConnect().getConnect();

			FormQueryConfigService updateFormService = new FormQueryConfigService(conn);

			FormButton formButton = new FormButton();
			formButton.setName(name);
			formButton.setFormid(formId);
			
			if(orderId != null && !"".equals(orderId)) {
				formButton.setOrderid(Integer.parseInt(orderId));
			}
			
			formButton.setIcon(icon);
			formButton.setAftergroup(Integer.parseInt(aftergroup));
			formButton.setOnclick(onClick);
			formButton.setExtjs(extJs);
			formButton.setClassName(className);
			formButton.setHandleType(handleType);
			formButton.setSql(sql);
			formButton.setEnname(enname);
			formButton.setButtonType(buttonType);
			
			formButton.setBeforeClick(beforeClick);
			formButton.setBeforeClickJs(beforeClickJs);
			
			formButton.setAfterClick(afterClick);
			formButton.setAfterClickJs(afterClickJs);
			
			// 如果是新增按钮
			if (null == uuid || "".equals(uuid)) {
				formButton.setUuid(UUID.randomUUID().toString());
				updateFormService.addFormButton(formButton);
			} else {
				formButton.setUuid(uuid);
				updateFormService.updateFormButton(formButton);
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}
		response.sendRedirect(request.getContextPath() + "/formQueryConfig.do?method=updateButtonsList&formId=" + formId);

		return null;
	}

	/**
	 * 
	 * 新增、修改表单按钮
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView addOrEdit(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Connection conn = null;
		ModelAndView modelAndView = new ModelAndView(BUTTON_EDIT_VIEW);
		
		String uuid = request.getParameter("uuid");
		String formId = request.getParameter("formId");
		FormButton formButton = null;
		
		modelAndView.addObject("classList", ClassUtil.getClassListByInterface(FormButtonExtInterface.class));
		
		if (null == uuid || "".equals(uuid)) {
			//将formid传回页面，标志同一表单
			formButton = new FormButton();
			formButton.setFormid(formId);
			modelAndView.addObject("formButton", formButton);
			return modelAndView;
		} else {
			try {

				conn = new DBConnect().getConnect();

				FormQueryConfigService updateFormService = new FormQueryConfigService(
						conn);

				formButton = updateFormService.getFormButtonByUuid(uuid);

			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			} finally {
				DbUtil.close(conn);
			}
			modelAndView.addObject("formButton", formButton);

			return modelAndView;
		}

	}

	/**
	 * 
	 * 删除表单按钮
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView delButton(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Connection conn = null;
		String uuid = request.getParameter("uuid");
		String formId = request.getParameter("formId");
		if (null != uuid && !"".equals(uuid)) {
			try {

				conn = new DBConnect().getConnect();

				FormQueryConfigService updateFormService = new FormQueryConfigService(
						conn);

				updateFormService.delFormButton(uuid);

			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			} finally {
				DbUtil.close(conn);
			}
			response.sendRedirect(request.getContextPath()
					+ "/formQueryConfig.do?method=updateButtonsList&formId="
					+ formId);
		}
		return null;
	}
	
	/**
	 * 按钮JAVA类接口
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView buttonExtHandle(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		response.setContentType("text/html;charset=utf-8");
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);
		
		PrintWriter out = response.getWriter();
		try {
			String btnId = request.getParameter("btnId");
			/*
			Map parameters = new HashMap();
			Enumeration e = request.getParameterNames();
			while (e.hasMoreElements()) {
				String paramName = String.valueOf(e.nextElement());
				String[] values = request.getParameterValues(paramName);
				
				String value = "";
				for (int i = 0; i < values.length; i++) {
					value += values[i];
					
					if (i != values.length - 1) {
						value += ",";
					}
				}
				
				parameters.put(paramName, value);	
			}
			UserSession userSession = (UserSession)request.getSession().getAttribute("userSession") ;
			parameters.put("userSession", userSession) ;
			*/
			String re= new FormButtonExtService().handler(btnId, request,response);
			
			out.write(re);
		} catch (Exception e) {
			e.printStackTrace();
			out.write("执行失败，出错 原因:" + e.getMessage()); 
		}
		
		return null;
	}
	
	//doGenForm
	public ModelAndView doGenForm(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		response.setContentType("text/html;charset=utf-8");
		WebUtil webUtil=new WebUtil(request, response);
		String formid=request.getParameter("formid");
		String cols=request.getParameter("cols");
		boolean noempty="y".equals(request.getParameter("noempty"));
		Connection conn=null;
	    GenFormService genFormService=null;
	    DbUtil dbUtil=null;
	    FormVO formVO=null;
	    int eff=0;
		try{
			conn=new DBConnect().getConnect();
			genFormService=new GenFormService(conn);
			dbUtil=new DbUtil(conn);
			formVO=dbUtil.load(FormVO.class, formid);
			eff=genFormService.genTable(formVO,Integer.parseInt(cols),noempty);
			if(eff>0){
				response.getWriter().write("保单生成成功");
			}else{
				response.getWriter().write("保单生成失败");
			}
		}catch(Exception ex){
			response.getWriter().write(ex.getLocalizedMessage());
		}finally{
			DbUtil.close(conn);
		}
		return null;
	}
}

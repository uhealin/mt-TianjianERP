package com.matech.audit.work.form;

import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.matech.audit.pub.datagrid.DataGridProperty;
import com.matech.audit.pub.datagrid.ExtGrid;
import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.form.FormButtonExtService;
import com.matech.audit.service.form.FormDefineService;
import com.matech.audit.service.form.FormExtInterface;
import com.matech.audit.service.form.FormQueryConfigService;
import com.matech.audit.service.form.FormTypeService;
import com.matech.audit.service.form.model.FormDefine;
import com.matech.audit.service.form.model.FormQuery;
import com.matech.audit.service.form.model.FormType;
import com.matech.audit.service.form.model.FormVO;
import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.log.Log;
import com.matech.framework.pub.util.ClassUtil;
import com.matech.framework.pub.util.StringUtil;
import com.matech.framework.pub.util.WebUtil;

public class FormDefineAction extends MultiActionController {

	private static final String FORM_HTML_VIEW = "form/formView.jsp";
	private static final String FORM_LIST_VIEW = "form/formList.jsp";
	private static final String FORM_EDIT_VIEW = "form/formEdit.jsp";
	private static final String SHOW_LIST_VIEW = "form/formListView.jsp";
	
	private static final String MAIN_VIEW = "form/main.jsp";
	private static final String FORM_TYPE_EDIT_VIEW = "form/formTypeEdit.jsp";
	private static final String FORM_TYPE_LIST_VIEW = "form/formTypeList.jsp";
	
	private static final String DISIGNER_VIEW = "form/formDesigner.jsp";
	private static final String PREVIEW_VIEW = "form/formPreView.jsp";
	public static final String[] WHERE_IDS={"00","01","02","03","04","05","06","07","08","09"}; 
	Log log = new Log(FormDefineAction.class) ;

	public static void setCurr(HttpServletRequest request){
		request.setAttribute("dt",StringUtil.getCurDateTime());
		request.setAttribute("d",StringUtil.getCurrentDate());
		request.setAttribute("t",StringUtil.getCurTime());
	}
	
	public static final String pathFormView(String formId,String uuid,boolean view){
		
		return MessageFormat.format("formDefine.do?method=formView&uuid={1}&formId={0}"+(view?"&view=true":""),
		formId,uuid);
	}
	
public static final String pathFormListExtView(String uuid,String where_id){
		
	return MessageFormat.format("formDefine.do?method=formListExtView&uuid={0}&where_id={1}",uuid,where_id);
	}
	
	public static final String pathFormListView(String uuid){
		
		return MessageFormat.format("formDefine.do?method=formListView&uuid={0}",uuid);
	}
	
	/**
	 * 表单HTML
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView formView(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
        this.setCurr(request);
		ModelAndView modelAndView = new ModelAndView(FORM_HTML_VIEW);
		Connection conn = null;
		try {

			String formId = StringUtil.showNull(request.getParameter("formId")); // 表单的unid
			String uuid = StringUtil.showNull(request.getParameter("uuid")); // 数据表的unid
			String srcFormId = StringUtil.showNull(request.getParameter("srcFormId")); // 数据表的unid
			
			conn = new DBConnect().getConnect();
			
			FormDefineService formService = new FormDefineService(conn);
			formService.setContextPath(request.getContextPath()); // 应用路径
			formService.beforeView(request, response, formId,uuid, modelAndView);
			String html = formService.getFormDataHTML(request, formId, uuid);

			modelAndView.addObject("mt_formid", formId);
			modelAndView.addObject("uuid", uuid);
			modelAndView.addObject("html", html);
			
			String buttonFormId = formId;
			
			if(srcFormId != null && !"".equals(srcFormId)) {
				buttonFormId = srcFormId;
			}
			String extjs = FormButtonExtService.createButtonExtjs(buttonFormId, request.getContextPath(), "1").toString();	
			String extjsSupportFunction = FormButtonExtService.createButtonExtjsExt(buttonFormId, "1").toString();
			
			modelAndView.addObject("extjs", extjs);
			modelAndView.addObject("extjsSupportFunction", extjsSupportFunction); //扩展方法
			

			return modelAndView;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}

	}

	public String getQueryString(HttpServletRequest request){
		String[] arrParams=request.getQueryString().split("&");
		String param="";
		for(String par:arrParams){
			if(par.startsWith("uuid=")||par.startsWith("mt_formid=")
					||par.startsWith("view=")||par.startsWith("formId=")
					||par.startsWith("method="))continue;
			param+="&"+par;
		}
		return param;
	}
	
	/**
	 * 保存表单
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	public void formDataSave(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Connection conn = null;
		try {
			
			conn = new DBConnect().getConnect();
			DbUtil dbUtil=new DbUtil(conn);
			//System.out.println(request.getQueryString());
			
			
			String formId = StringUtil.showNull(request.getParameter("mt_formid"));
			String uuid = StringUtil.showNull(request.getParameter("uuid"));
			
			WebUtil webUtil=new WebUtil(request, response);
			UserSession userSession=webUtil.getUserSession();
		    String id= new FormDefineService(conn).saveFormData(request,response, formId, uuid);
			boolean isAdd=uuid.isEmpty();
		   
		    String afterAdd=request.getParameter("afterAdd");
		    String afterEdit=request.getParameter("afterEdit");
		    if(isAdd&&"toEdit".equals(afterAdd)){
			 response.sendRedirect(pathFormView(formId, id, false)+getQueryString(request));
			}else if(isAdd&&"toView".equals(afterAdd)){
				response.sendRedirect(pathFormView(formId, id, true)+getQueryString(request));
			}else if(!isAdd&&"toEdit".equals(afterEdit)){
				 response.sendRedirect(pathFormView(formId, uuid, false)+getQueryString(request));
		    }else if(!isAdd&&"toView".equals(afterEdit)){
					response.sendRedirect(pathFormView(formId, uuid, true)+getQueryString(request));
			}else{
			   if(request.getParameter("where_id")==null){
			   response.sendRedirect("formDefine.do?method=formListView&uuid=" + formId+getQueryString(request));
			    // response.sendRedirect(pathFormListView(formId)+getQueryString(request));
			   }else {
				response.sendRedirect("formDefine.do?method=formListExtView&uuid=" + formId+getQueryString(request));
				}
			
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}
	}

	/**
	 * 显示列表
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView formListView(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		ModelAndView modelAndView = new ModelAndView(SHOW_LIST_VIEW);
		String srcFormId = request.getParameter("srcFormId");
		String formId = request.getParameter("uuid");
		String context = request.getContextPath();
        String menuid=request.getParameter("menuid");
		menuid=menuid==null?"":menuid;
        Connection conn = null;

		try {
			conn = new DBConnect().getConnect();
			FormDefineService formDefineService = new FormDefineService(conn);
			
			FormDefine formDefine = formDefineService.getFormDefine(formId);
			
			FormQueryConfigService updateFormService = new FormQueryConfigService(conn);
			List<FormQuery> list = updateFormService.getformQuery(formId);
			String datagridId = FormDefineService.getDataGridId(formId);
			DataGridProperty pp = new DataGridProperty();
			pp.setTableID(datagridId+"_"+menuid);
			pp.setCustomerId("");// 暂时没有用，但是一定要写
			pp.setPageSize_CH(50); // 设置每页显示的条数
			pp.setWhichFieldIsValue(1);// 设置第几行为获取项,对应的是sql语句的第几个字段,这列会以隐藏表单域的形式传过去
			if(formDefine.getThead()!=null&&!formDefine.getThead().isEmpty()){
				pp.setTableHead(formDefine.getThead());
			}
			// 是用复选框显示还是用单选框显示radio checkBox
			if("多选".equals(formDefine.getSelecttype())) {
				pp.setInputType("checkbox");
			} else {
				pp.setInputType("radio");
			}
			
			String setStrAction = "";
			
			boolean isOrderby = false;
			
			String orderByField = "";
			String orderByDirection = "";
			String width = "" ;
			
			for (FormQuery bean : list) {
				// 在list里面显示的列，0不显示1显示在列表
				if (bean.getBshow() == 1) {
					if (bean.getBtype() != null) {
						
						if("showAttach".equals(bean.getBtype())) {
							
							pp.addColumn(bean.getName(), bean.getEnname(), null, "cn.gov.shunde.sdcs.action.common.AttachColumnProcess", null, formId);
							
						}else if("showProcess".equals(bean.getBtype())){
							pp.addColumn(bean.getName(), bean.getEnname(), null, "cn.gov.shunde.sdcs.action.common.ProcessColumnProcess", null, formId);
							//pp.addColumn(bean.getName(), bean.getEnname(), null, "cn.gov.shunde.sdcs.action.common.AttachColumnProcess", null, formId);
						} else {
							pp.addColumn(bean.getName(), bean.getEnname(),
									bean.getBtype());
						}
						
					} else {
						pp.addColumn(bean.getName(), bean.getEnname());
					}
					
					//列宽
					String colWidth = StringUtil.showNull(bean.getWidth()) ;
					if(!"".equals(colWidth)) {
						width += colWidth + "," ;
					}else {
						width += "10," ;
					}
				}
				
				
				//特殊行标识值
				if(bean.getRowFlag() != null && !"".equals(bean.getRowFlag())) {
					pp.setTrBgColor(bean.getEnname().toLowerCase() ,bean.getRowFlag(),"#FF9966") ;
				}
				
				// 是否放到trproperty,0不放1放 双击的时候用
				if (bean.getBhiddenrow() == 1) {
					setStrAction += bean.getEnname().toLowerCase() + "=\"${" + bean.getEnname().toLowerCase() + "}\" ";

				}
				// 设置排序方式，其中大于零正序小于零降序
				if (bean.getBorder() != 0) {
					orderByField += bean.getEnname() + ",";
					orderByDirection += ( bean.getBorder() > 0 ? "asc," : "desc,");
					isOrderby = true;
				}
				
			}
			
			if(!"".equals(setStrAction)) {
				// 设置双击时传递的参数
				pp.setTrActionProperty(true);// 设置表格可以双击穿透
				pp.setTrAction(setStrAction);
				System.out.println(setStrAction);
			}
			
			
			if(!isOrderby) {
				pp.setOrderBy_CH("1");
				pp.setDirection("asc");
			} else {
				orderByField = orderByField.substring(0, orderByField.lastIndexOf(","));
				orderByDirection = orderByDirection.substring(0, orderByDirection.lastIndexOf(","));
				
				pp.setOrderBy_CH(orderByField);
				pp.setDirection(orderByDirection);
			}
			
			// 设置列宽
			if (width.indexOf(",")>0) {
				width = width.substring(0, width.lastIndexOf(","));
			} 
			pp.setColumnWidth(width);// 设置列宽
			String sql = "";
			sql = StringUtil.showNull(formDefineService.getListSql(formId));//获取自定义的sql
			if ("".equals(sql)) { //如果自定义的sql为空，则构造默认sql
				sql = StringUtil.showNull(formDefineService.getShowSql(formId));
			} 
			
			sql = StringUtil.transRequestValue(request, sql);
			
			sql = StringUtil.transSessionValue(request.getSession(), sql);
			
			sql = StringUtil.transUserPopedomValue(request, sql);
			
			log.debug(sql);
			
			pp.setSQL(sql);
			
			request.getSession().setAttribute(
					ExtGrid.sessionPre + pp.getTableID(), pp);
			modelAndView.addObject("tableid", pp.getTableID());
			modelAndView.addObject("formId", formId);
			
			String buttonFormId = formId;
			if(srcFormId != null && !"".equals(srcFormId)) {
				buttonFormId = srcFormId;
			}
			
			String extjs = FormButtonExtService.createButtonExtjs(buttonFormId, context, "0",request).toString();	
			String extjsSupportFunction = FormButtonExtService.createButtonExtjsExt(buttonFormId, "0").toString();
			
			modelAndView.addObject("extHtml", formDefine.getListHtml());
			modelAndView.addObject("extjs", extjs);
			modelAndView.addObject("extjsSupportFunction", extjsSupportFunction); //扩展方法
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}
		return modelAndView;
	}

	/**
	 * 表单管理
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView formList(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		String formTypeId = StringUtil.showNull(request.getParameter("formTypeId"));
		
		if("".equals(formTypeId)) {
			formTypeId = "0";
		}
		
		ModelAndView modelAndView = new ModelAndView(FORM_LIST_VIEW);

		DataGridProperty pp = new DataGridProperty();
		pp.setTableID("sform_list");
		pp.setCustomerId("");// 暂时没有用，但是一定要写
		pp.setColumnWidth("25,10,11,12,15,10,10");// 设置列宽
		pp.setPageSize_CH(50); // 设置每页显示的条数
		pp.setWhichFieldIsValue(1);// 设置第几行为获取项,对应的是sql语句的第几个字段,这列会以隐藏表单域的形式传过去
		pp.setInputType("radio");// 是用复选框显示还是用单选框显示radio checkBox
		// 向datagvid中添加列
		pp.addColumn("中文名", "name");
		pp.addColumn("英文名", "enname");
		pp.addColumn("数据库表名", "tablename");
		pp.addColumn("建表方式", "tabletype");
		//pp.addColumn("扩展类", "extclass");
		pp.addColumn("最后修改时间", "udate");
		pp.addColumn("最后修改人", "uname");
		//pp.addColumn("单选/多选", "selecttype");
		//pp.addColumn("备用", "property");

		pp.addSqlWhere("name", " and name like '%${cname}%' ");
		pp.addSqlWhere("enname", " and enname like '%${enname}%' ");
		pp.addSqlWhere("lastUTime", " and udate like '%${lastUTime}%' ");
		pp.addSqlWhere("lastUName", " and uname like '%${lastUName}%' ");
		pp.addSqlWhere("uuidfind", " and uuid like '%${uuidfind}%' ");

		String sql = " SELECT uuid,name,enname,tablename,case tabletype when '0' then '自动建表' else '手工建表' end as tabletype,extclass,udate,uname,selecttype,property "
					+ " FROM MT_COM_FORM "
					+ " where (parentformid='' or parentformid is null) "
					+ " and form_type='" + formTypeId + "' "
					+ " ${name} ${enname} ${lastUTime} ${lastUName} ${uuidfind}";
		// 显示数据时按照loginid列降序排列
		pp.setOrderBy_CH("name");
		pp.setDirection("asc");

		pp.setSQL(sql);
		request.getSession().setAttribute(
				ExtGrid.sessionPre + pp.getTableID(), pp);
		
		return modelAndView;
	}

	/**
	 * 重定向到添加页面
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView formAdd(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		this.setCurr(request);
		ModelAndView modelAndView = new ModelAndView(FORM_EDIT_VIEW);
		String formTypeId = request.getParameter("formTypeId");
		FormDefine formDefine = new FormDefine();
		formDefine.setFormType(formTypeId);
		
		modelAndView.addObject("classList", ClassUtil.getClassListByInterface(FormExtInterface.class));
		modelAndView.addObject("formDefine", formDefine);
		modelAndView.addObject("formTablePre", FormDefineService.FORM_TABLE_PRE);

		return modelAndView;
	}

	/**
	 * 添加记录
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView formSave(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		UserSession userSession = (UserSession) request.getSession()
				.getAttribute("userSession");

		Connection conn = null;

		String formType = request.getParameter("formType");//表单类型
		
		try {
			conn = new DBConnect().getConnect();
			//conn.setAutoCommit(false) ;
			
			String userName = userSession.getUserName();// 最后修改人
			String udate = StringUtil.getCurDateTime();
			String uuid = request.getParameter("uuid");
			String name = request.getParameter("name");// 表单中文名
			String enName = request.getParameter("enName");// 表单英文名
			String oldEnName = request.getParameter("oldEnName");// 旧的表单英文名
			String tableName = request.getParameter("tableName");// 表名
			String defineStr = request.getParameter("defineStr");// 自定义字符串
			String extClass = request.getParameter("extClass");// 扩展类
			String selectType = request.getParameter("selectType");// 选择类型
			String property = request.getParameter("property");// 备用tableType
			String tableType = request.getParameter("tableType");// 建表类型

			FormDefine formDefine = new FormDefine();
			formDefine.setName(name);
			formDefine.setDefinestr(defineStr);
			formDefine.setEnname(enName);
			formDefine.setTableName(tableName);
			formDefine.setTableType(tableType);
			formDefine.setExtclass(extClass);
			formDefine.setProperty(property);
			formDefine.setUdate(udate);
			formDefine.setSelecttype(selectType);
			formDefine.setUname(userName);
			formDefine.setFormType(formType);
			
			FormDefineService formDefineService = new FormDefineService(conn);
			
			if(uuid == null || "".equals(uuid)) {
				formDefine.setUuid(UUID.randomUUID().toString());
				formDefineService.saveFormDefine(formDefine);
			} else {
				formDefine.setUuid(uuid);
				formDefineService.updateFormDefine(oldEnName, formDefine);
			}
			//conn.commit(); // 提交事务
		} catch (Exception e) {
			//DbUtil.rollback(conn);
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}

		response.sendRedirect("formDefine.do?method=formList&formTypeId=" + formType);
		return null;
	}

	/**
	 * 
	 * 根据uuid查询记录
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView formEdit(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		this.setCurr(request);
		String uuid = request.getParameter("uuid");

		ModelAndView modelAndView = new ModelAndView(FORM_EDIT_VIEW);
		Connection conn = null;

		try {

			conn = new DBConnect().getConnect();

			FormDefineService service = new FormDefineService(conn);
			FormDefine formDefine = service.getFormDefine(uuid);
			modelAndView.addObject("formDefine", formDefine);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}
		modelAndView.addObject("classList", ClassUtil.getClassListByInterface(FormExtInterface.class));
		
		modelAndView.addObject("formTablePre", FormDefineService.FORM_TABLE_PRE);


		return modelAndView;
	}

	/**
	 * 根据uuid删除form表单
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView removeForm(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String uuid = request.getParameter("uuid");
		Connection conn = null;
		try {
			conn = new DBConnect().getConnect();
			conn.setAutoCommit(false); // 关闭自动提交
			
			FormDefineService service = new FormDefineService(conn);
			service.removeFormDefine(uuid);

			conn.commit(); // 提交事务
		} catch (Exception e) {
			DbUtil.rollback(conn);
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}

		response.sendRedirect(request.getContextPath()
				+ "/formDefine.do?method=formList");
		return null;
	}
	
	/**
	 * 根据uuid删除form表单的记录
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView removeFormData(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String uuid = request.getParameter("uuid");
		String formId = request.getParameter("formId");
		Connection conn = null;
		try {
			conn = new DBConnect().getConnect();
			conn.setAutoCommit(false); // 关闭自动提交
			
			FormDefineService service = new FormDefineService(conn);
			service.removeFormData(formId, uuid); //删除表单数据
			
			conn.commit(); // 提交事务
		} catch (Exception e) {
			DbUtil.rollback(conn);
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}
        if(request.getParameter("where_id")==null){
		response.sendRedirect(request.getContextPath() + "/formDefine.do?method=formListView&uuid="+formId+getQueryString(request));
        }else{
    		response.sendRedirect(request.getContextPath() + "/formDefine.do?method=formListExtView&uuid="+formId+getQueryString(request));
    		 
        }
		return null;
	}

	/**
	 * 根据uuid删除form表单的记录
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView removeFormDataNotReal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String uuid = request.getParameter("uuid");
		String formId = request.getParameter("formId");
		
		String uuids =uuid.contains(",")?uuid.replaceAll(",", "','"):"'"+uuid+"'";
		uuids=StringUtil.trim(uuids, ",");
		Connection conn = null;
		DbUtil dbUtil=null;
		try {
			conn = new DBConnect().getConnect();
			conn.setAutoCommit(false); // 关闭自动提交
			dbUtil=new DbUtil(conn);
			FormDefineService service = new FormDefineService(conn);
			//service.removeFormData(formId, uuid); //删除表单数据
			FormDefine formDefine=service.getFormDefine(formId);
			String sql="update "+formDefine.getTableName()+" set del_ind=1 where uuid in ("+uuids+")";
			
			dbUtil.executeUpdate(sql);
			conn.commit(); // 提交事务
		} catch (Exception e) {
			DbUtil.rollback(conn);
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}
        if(request.getParameter("where_id")==null){
		response.sendRedirect(request.getContextPath() + "/formDefine.do?method=formListView&uuid="+formId+getQueryString(request));
        }else{
    		response.sendRedirect(request.getContextPath() + "/formDefine.do?method=formListExtView&uuid="+formId+getQueryString(request));
    		 
        }
		return null;
	}
	
	/**
	 * 新增分类
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView formTypeAdd(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView(FORM_TYPE_EDIT_VIEW);
		
		String parentId = request.getParameter("parentId");
		
		FormType formType = new FormType();
		formType.setParentId(parentId);
		modelAndView.addObject("formType", formType);
		
		return modelAndView;
	}
	
	/**
	 * 编辑分类
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView formTypeEdit(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String formTypeId = request.getParameter("formTypeId");
				
		ModelAndView modelAndView = new ModelAndView(FORM_TYPE_EDIT_VIEW);

		Connection conn = null;
		
		try {
			conn = new DBConnect().getConnect();
			
			FormType formType = new FormTypeService(conn).getFormTypeByTypeId(formTypeId);
			modelAndView.addObject("formType", formType);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}
		
		return modelAndView;
	}
	
	/**
	 * 获取类型树JSON
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView getFormTypeJSONTree(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		response.setContentType("text/html;charset=utf-8");

		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);
		
		String parentId = StringUtil.showNull(request.getParameter("id"));
		Connection conn = null;
		
		try {
			
			conn = new DBConnect().getConnect();
			
			if("".equals(parentId)) {
				parentId = "0";
			}
			
			FormTypeService formTypeService = new FormTypeService(conn) ;
			List list = formTypeService.getFormTypeJSONList(parentId);
			String treeJson = JSONArray.fromObject(list).toString() ;
			
			response.getWriter().write(treeJson);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}

		return null;
	}
	
	/**
	 * 删除分类
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView removeFormType(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		String parentId = StringUtil.showNull(request.getParameter("parentId"));
		String formTypeId = StringUtil.showNull(request.getParameter("formTypeId"));
		
		Connection conn = null;
		
		try {
			
			conn = new DBConnect().getConnect();
			
			new FormTypeService(conn).remove(formTypeId);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}
		
		response.sendRedirect("formDefine.do?method=formTypeList&parentId=" + parentId);
		return null;
	}
	
	/**
	 * 保存分类
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView formTypeSave(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String formTypeId = StringUtil.showNull(request.getParameter("formTypeId"));
		String formTypeName = StringUtil.showNull( request.getParameter("formTypeName"));
		String parentId = StringUtil.showNull(request.getParameter("parentId"));
		String property = StringUtil.showNull(request.getParameter("property"));
		
		if("".equals(parentId)) {
			parentId = "0";
		}

		Connection conn = null;
		FormType formType = new FormType();
		
		try {
			conn = new DBConnect().getConnect();

			formType.setFormTypeName(formTypeName);
			formType.setParentId(parentId);
			formType.setProperty(property);

			FormTypeService formTypeService = new FormTypeService(conn);

			if ("".equals(formTypeId)) {
				//新增
				formType.setFormTypeId(StringUtil.getUUID());
				formTypeService.save(formType);
			} else {
				//更新
				formType.setFormTypeId(formTypeId);
				formTypeService.update(formType);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}

		response.sendRedirect("formDefine.do?method=formTypeEdit&formTypeId=" + formType.getFormTypeId() + "&parentId=" + parentId);

		return null;
	}
	
	/**
	 * 分类列表
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView formTypeList(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		String parentId = StringUtil.showNull(request.getParameter("parentId"));
	 
		if("".equals(parentId)) {
			parentId = "0";
		}
		
		ModelAndView modelAndView = new ModelAndView(FORM_TYPE_LIST_VIEW);

		DataGridProperty pp = new DataGridProperty();

		pp.setTableID("formTypeList");

		String sql = " select FORM_TYPE_ID, FORM_TYPE_NAME, PARENTID, PROPERTY "
					+ " from MT_COM_FORM_TYPE "
					+ " where '1'='1' "
					+ " and parentId='" + parentId + "' ${formTypeName}";
		
		pp.setSQL(sql);
		pp.setOrderBy_CH("FORM_TYPE_NAME");
		pp.setDirection_CH("desc");
		
		pp.setInputType("radio");
		pp.setWhichFieldIsValue(1);
		
		pp.setTrActionProperty(true); 
		pp.setTrAction("style=\"cursor:hand;\" formTypeId=\"${formTypeId}\" ");
	
		pp.addColumn("分类名称", "FORM_TYPE_NAME");
		pp.addColumn("分类说明", "PROPERTY");
		
		pp.addSqlWhere("formTypeName", " and FORM_TYPE_NAME like '%${formTypeName}%'");
		
		request.getSession().setAttribute(ExtGrid.sessionPre + pp.getTableID(),pp);
		
		modelAndView.addObject("parentId", parentId);
		
		return modelAndView;
	}
	
	/**
	 * 主页面
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView main(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView(MAIN_VIEW);

		return modelAndView;
	}
	
	
	/**表单智能设计器
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView design(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView(DISIGNER_VIEW);
		
		Connection conn = null ;
		try {
			conn = new DBConnect().getConnect();
			String formId = StringUtil.showNull(request.getParameter("formId")) ;
			
			DbUtil db = new DbUtil(conn);
			Map form = db.get("MT_COM_FORM", "uuid", formId);
			String tableName = (String)form.get("tablename") ;
			  
			String html = StringUtil.showNull(form.get("definestr"));
			
			modelAndView.addObject("html", html) ;
			modelAndView.addObject("formId", formId) ;
			modelAndView.addObject("tableName", tableName) ;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}
		return modelAndView;
	}
	
	
	/**
	 * 更新表单的html
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView updateFormHtml(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		Connection conn = null ;
		String script = "",formId = "";
		PrintWriter out = null ;
		
		response.setContentType("text/html;charset=utf-8");
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);
		
		try {
			UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");
			
			conn = new DBConnect().getConnect();
			out = response.getWriter() ;
			formId = StringUtil.showNull(request.getParameter("formId")) ;
			String formHtml = StringUtil.showNull(request.getParameter("formHtml")) ;
			
			//conn.setAutoCommit(false) ;
			
			DbUtil db = new DbUtil(conn);

			FormDefineService fds = new FormDefineService(conn) ;
			
			//备份
			fds.backupFormDefine(formId);
			
			
			Map map = new HashMap();
			map.put("uuid", formId);
			map.put("definestr", formHtml);
			map.put("udate", StringUtil.getCurDateTime());
			map.put("uname", userSession.getUserName());
			
			//更新表单html
			db.update("MT_COM_FORM", "uuid", map) ;
			
			//更新表单字段MT_COM_FORM_FIELD
			fds.saveFormField(formId, formHtml, false) ;
			//conn.commit() ;
			
			script = "<script>"
				   + " 	alert('表单保存成功!');" 
				   + " 	window.location = '"+request.getContextPath()+"/formDefine.do?method=design&formId="+formId+"';" 
				   + "</script>" ;  
			out.write(script) ;
		} catch (Exception e) {
			//DbUtil.rollback(conn);
			log.exception("表单设计器保存出错", e) ;
			script = "<script>"
				   + " 	alert('后台发生异常,表单保存失败!');" 
				   + " 	window.location = '"+request.getContextPath()+"/formDefine.do?method=design&formId="+formId+"';" 
				   + "</script>" ;  
			out.write(script) ;
		} finally {
			DbUtil.close(conn);
			if(out != null) out.close() ;
		}
		return null;
	}
	
	/**
	 * 表单保存并预览
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView formPreView(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		this.setCurr(request);
		ModelAndView modelAndView = new ModelAndView(PREVIEW_VIEW);
		Connection conn = null ;
		
		try {
			conn = new DBConnect().getConnect();
			String formId = StringUtil.showNull(request.getParameter("formId")) ;
			String formHtml = StringUtil.showNull(request.getParameter("formHtml")) ;
			
			conn.setAutoCommit(false) ;
			DbUtil db = new DbUtil(conn);
			FormDefineService fds = new FormDefineService(conn) ;
			fds.beforeView(request, response, formId,null, modelAndView);
			//更新表单html
			db.update("MT_COM_FORM", "uuid", formId, "definestr", formHtml) ;
			//更新表单字段MT_COM_FORM_FIELD
			fds.saveFormField(formId, formHtml, false) ;
			conn.commit() ;
			conn.setAutoCommit(true) ;
			
			FormDefineService formService = new FormDefineService(conn);
			formService.setContextPath(request.getContextPath()); // 应用路径
			String html = formService.getFormDataHTML(request,formId,"");
			
			modelAndView.addObject("html",html) ;
		} catch (Exception e) {
			log.exception("表单设计器预览发生错误!", e) ;
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}
		return modelAndView;
	}
	
	public static ModelAndView saveUrl(HttpServletRequest request,
			HttpServletResponse response){
		String url=request.getParameter("url");
		
		request.getSession().setAttribute("url",url);
		return null;
	}
	
	
	/**
	 *  分状态显示列表
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView formListExtView(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		ModelAndView modelAndView = new ModelAndView(SHOW_LIST_VIEW);
		String srcFormId = request.getParameter("srcFormId");
		String formId = request.getParameter("uuid");
		String context = request.getContextPath();
        String menuid=request.getParameter("menuid");
        String where_id=request.getParameter("where_id");
		where_id=where_id==null?"00":where_id;
        menuid=menuid==null?"":menuid;
        String where_sql="";
        String where_hiddenbtn="",where_hiddenCol="";
        Connection conn = null;
        DbUtil dbUtil=null;
		try {
			conn = new DBConnect().getConnect();
			dbUtil=new DbUtil(conn);
			FormDefineService formDefineService = new FormDefineService(conn);
			
			//FormDefine formDefine = formDefineService.getFormDefine(formId);
			
			FormVO formDefine=dbUtil.load(FormVO.class, formId);
			
			String whereSqlGetterName="getWhere_"+where_id+"_sql";
			Method whereSqlGetter=FormVO.class.getDeclaredMethod(whereSqlGetterName);
			String whereHiddenBtnGetterName="getWhere_"+where_id+"_hiddenbtn";
			Method whereHiddenBtnGetter=FormVO.class.getDeclaredMethod(whereHiddenBtnGetterName);
			String whereHiddenColName="getWhere_"+where_id+"_hiddencol";
			Method whereHiddenColGetter=FormVO.class.getDeclaredMethod(whereHiddenColName);
			try{
			where_sql=(String)whereSqlGetter.invoke(formDefine);
			where_hiddenbtn=(String)whereHiddenBtnGetter.invoke(formDefine);
			where_hiddenCol=(String)whereHiddenColGetter.invoke(formDefine);
			}catch(Exception ex){}
			FormQueryConfigService updateFormService = new FormQueryConfigService(conn);
			List<FormQuery> list = updateFormService.getformQuery(formId);
			String datagridId = FormDefineService.getDataGridId(formId);
			DataGridProperty pp = new DataGridProperty();
			pp.setTableID(datagridId+"_"+menuid);
			pp.setCustomerId("");// 暂时没有用，但是一定要写
			pp.setPageSize_CH(50); // 设置每页显示的条数
			pp.setWhichFieldIsValue(1);// 设置第几行为获取项,对应的是sql语句的第几个字段,这列会以隐藏表单域的形式传过去
			if(formDefine.getThead()!=null&&!formDefine.getThead().isEmpty()){
				pp.setTableHead(formDefine.getThead());
			}
			// 是用复选框显示还是用单选框显示radio checkBox
			if("多选".equals(formDefine.getSELECTTYPE())) {
				pp.setInputType("checkbox");
			} else {
				pp.setInputType("radio");
			}
			
			String setStrAction = "";
			
			boolean isOrderby = false;
			
			String orderByField = "";
			String orderByDirection = "";
			String width = "" ;
			where_hiddenCol=StringUtil.isBlank(where_hiddenCol)?"":where_hiddenCol;
			String[] arrHiddenCols=where_hiddenCol.split(",");
		    Set<String> setHiddenCol=new HashSet<String>();
		    for (String string : arrHiddenCols) {
		    	if(StringUtil.isBlank(string)) continue;
		    	setHiddenCol.add(string);
		    		
		    	
			}
			for (FormQuery bean : list) {
				if (setHiddenCol.contains(bean.getEnname())) {
					continue;
				}
				// 在list里面显示的列，0不显示1显示在列表
				if (bean.getBshow() == 1) {
					if (bean.getBtype() != null) {
						
						if("showAttach".equals(bean.getBtype())) {
							
							pp.addColumn(bean.getName(), bean.getEnname(), null, "cn.gov.shunde.sdcs.action.common.AttachColumnProcess", null, formId);
							
						}else if("showProcess".equals(bean.getBtype())){
							pp.addColumn(bean.getName(), bean.getEnname(), null, "cn.gov.shunde.sdcs.action.common.ProcessColumnProcess", null, formId);
							//pp.addColumn(bean.getName(), bean.getEnname(), null, "cn.gov.shunde.sdcs.action.common.AttachColumnProcess", null, formId);
						} else {
							pp.addColumn(bean.getName(), bean.getEnname(),
									bean.getBtype());
						}
						
					} else {
						pp.addColumn(bean.getName(), bean.getEnname());
					}
					
					//列宽
					String colWidth = StringUtil.showNull(bean.getWidth()) ;
					if(!"".equals(colWidth)) {
						width += colWidth + "," ;
					}else {
						width += "10," ;
					}
				}
				
				
				//特殊行标识值
				if(bean.getRowFlag() != null && !"".equals(bean.getRowFlag())) {
					pp.setTrBgColor(bean.getEnname().toLowerCase() ,bean.getRowFlag(),"#FF9966") ;
				}
				
				// 是否放到trproperty,0不放1放 双击的时候用
				if (bean.getBhiddenrow() == 1) {
					setStrAction += bean.getEnname().toLowerCase() + "=\"${" + bean.getEnname().toLowerCase() + "}\" ";

				}
				// 设置排序方式，其中大于零正序小于零降序
				if (bean.getBorder() != 0) {
					orderByField += bean.getEnname() + ",";
					orderByDirection += ( bean.getBorder() > 0 ? "asc," : "desc,");
					isOrderby = true;
				}
				
			}
			
			if(!"".equals(setStrAction)) {
				// 设置双击时传递的参数
				pp.setTrActionProperty(true);// 设置表格可以双击穿透
				pp.setTrAction(setStrAction);
				System.out.println(setStrAction);
			}
			
			
			if(!isOrderby) {
				pp.setOrderBy_CH("1");
				pp.setDirection("asc");
			} else {
				orderByField = orderByField.substring(0, orderByField.lastIndexOf(","));
				orderByDirection = orderByDirection.substring(0, orderByDirection.lastIndexOf(","));
				
				pp.setOrderBy_CH(orderByField);
				pp.setDirection(orderByDirection);
			}
			
			// 设置列宽
			if (width.indexOf(",")>0) {
				width = width.substring(0, width.lastIndexOf(","));
			} 
			pp.setColumnWidth(width);// 设置列宽
			String sql = "";
			sql = StringUtil.showNull(formDefineService.getListSql(formId));//获取自定义的sql
			sql = sql+" where "+where_sql;
			if ("".equals(sql)) { //如果自定义的sql为空，则构造默认sql
				sql = StringUtil.showNull(formDefineService.getShowSql(formId));
			} 
			
			sql = StringUtil.transRequestValue(request, sql);
			
			sql = StringUtil.transSessionValue(request.getSession(), sql);
			
			sql = StringUtil.transUserPopedomValue(request, sql);
			
			log.debug(sql);
			
			pp.setSQL(sql);
			
			request.getSession().setAttribute(
					ExtGrid.sessionPre + pp.getTableID(), pp);
			modelAndView.addObject("tableid", pp.getTableID());
			modelAndView.addObject("formId", formId);
			
			String buttonFormId = formId;
			if(srcFormId != null && !"".equals(srcFormId)) {
				buttonFormId = srcFormId;
			}
			
			String extjs = FormButtonExtService.createButtonExtjs(buttonFormId, context, "0",request,where_hiddenbtn).toString();	
			String extjsSupportFunction = FormButtonExtService.createButtonExtjsExt(buttonFormId, "0").toString();
			
			modelAndView.addObject("extHtml", formDefine.getLISTHTML());
			modelAndView.addObject("extjs", extjs);
			modelAndView.addObject("extjsSupportFunction", extjsSupportFunction); //扩展方法
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}
		return modelAndView;
	}
	
	
	public ModelAndView updateFormWhere(HttpServletRequest request,HttpServletResponse response) throws Exception{
		Connection conn=null;
		DbUtil dbUtil=null;
		WebUtil webUtil=new WebUtil(request, response);
		UserSession userSession=webUtil.getUserSession();
		String formTypeId=request.getParameter("formTypeId");
		int eff=0;
		String re="";
		String uuid=request.getParameter("uuid");
		response.setContentType(WebUtil.CONTENT_TYPE_UTF8);
		try{
			conn=new DBConnect().getConnect();
			dbUtil=new DbUtil(conn);
		    FormVO formVO=dbUtil.load(FormVO.class, uuid);
		    formVO=webUtil.evalObject(formVO);
		    dbUtil.update(formVO);
		}catch(Exception ex){
			re=ex.getLocalizedMessage();
		}finally{
			DbUtil.close(conn);
		}
		response.sendRedirect(MessageFormat.format("formDefine.do?method=formList&formTypeId={0}", formTypeId));
		return null;
	}
	
	public ModelAndView checkWhere(HttpServletRequest request,HttpServletResponse response) throws Exception{
		Connection conn=null;
		DbUtil dbUtil=null;
		WebUtil webUtil=new WebUtil(request, response);
		UserSession userSession=webUtil.getUserSession();
		int eff=0;
		String re="";
		String formid=request.getParameter("formid");
		String where_sql =request.getParameter("where_sql");
		response.setContentType(WebUtil.CONTENT_TYPE_UTF8);
		String testSql="";
		FormVO formVO=null;
		
		JSONObject jsonRe=new JSONObject();
		try{
			conn=new DBConnect().getConnect();
			dbUtil=new DbUtil(conn);
			formVO=dbUtil.load(FormVO.class, formid);
			testSql=formVO.getLISTSQL()+" where "+where_sql;
			String testSql2=testSql.replace("${userPopedom}", "''");
			dbUtil.execute(testSql2);
		    jsonRe.put("re", 0);
		}catch(Exception ex){
			jsonRe.put("re", -1);
			re=ex.getLocalizedMessage();
		}finally{
			DbUtil.close(conn);
		}
		jsonRe.put("sql", testSql);
		response.getWriter().write(jsonRe.toString());
		return null;
	}
	
	public ModelAndView jsonFormDefine(HttpServletRequest request,HttpServletResponse response) throws Exception{
		Connection conn=null;
		DbUtil dbUtil=null;
		WebUtil webUtil=new WebUtil(request, response);
		UserSession userSession=webUtil.getUserSession();
		int eff=0;
		String re="";
		String uuid=request.getParameter("uuid");
		FormVO formVO=null;
		response.setContentType(WebUtil.CONTENT_TYPE_UTF8);
		JSONObject json=new JSONObject();
		try{
			conn=new DBConnect().getConnect();
			dbUtil=new DbUtil(conn);
			formVO=dbUtil.load(FormVO.class, uuid);
			json=JSONObject.fromObject(formVO);
			List<JSONObject> listExtViews=new ArrayList<JSONObject>();
			for(String where_id:WHERE_IDS){
			      Object temp=null;
			      temp=FormVO.class.getDeclaredMethod("getWhere_"+where_id+"_name").invoke(formVO);
			      String where_name=temp==null?"":temp.toString();
			      temp=FormVO.class.getDeclaredMethod("getWhere_"+where_id+"_sql").invoke(formVO);
			      String where_sql=temp==null?"":temp.toString();
			      temp=FormVO.class.getDeclaredMethod("getWhere_"+where_id+"_hiddenbtn").invoke(formVO);
			      String where_hiddenbtn=temp==null?"":temp.toString();
			      JSONObject listExtView=new JSONObject();
			      listExtView.put("where_name", where_name);
			      listExtView.put("where_id",where_id);
			      listExtView.put("url",pathFormListExtView(uuid, where_id));
			      listExtViews.add(listExtView);
			}
			json.put("listExtViews", listExtViews);
			json.put("listViewUrl",pathFormListView(uuid));
		}catch(Exception ex){
			re=ex.getLocalizedMessage();
		}finally{
			DbUtil.close(conn);
		}
		response.getWriter().write(json.toString());
		return null;
	}
}

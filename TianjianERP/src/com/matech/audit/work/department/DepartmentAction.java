package com.matech.audit.work.department;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.matech.audit.pub.datagrid.DataGrid;
import com.matech.audit.pub.datagrid.DataGridProperty;
import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.department.DepartmentService;
import com.matech.audit.service.department.model.DepartmentVO;
import com.matech.audit.service.enterpriseQualification.EnterpriseQualificationService;
import com.matech.audit.service.setdef.SetdefObjectService;
import com.matech.audit.service.userdef.Userdef;
import com.matech.audit.service.userdef.UserdefService;
import com.matech.audit.service.userpopedom.UserPopedomService;
import com.matech.framework.listener.OnlineListListener;
import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.autocode.DELUnid;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;
import com.matech.framework.pub.util.Debug;
import com.matech.framework.service.print.PrintSetup;

public class DepartmentAction extends MultiActionController {

	private final String _strSuccess = "department/List.jsp";

	private final String _strAddaddEdit = "department/AddandEdit.jsp";

	private final String _strPopdem = "department/departmentUpdatePopdem.jsp";
	
	private final String SET_ORDER_BY_VIEW = "department/setOrderBy.jsp";
	
	private final String STATICLIST = "department/usageStaticList.jsp";
	
	private final String USERLIST = "department/userList.jsp";
	
	private final String PROJECTLIST = "department/projectList.jsp";
	
	private final String CUSTOMERLIST = "department/customerList.jsp";
	
	private final String SCHEDULELIST = "department/scheduleList.jsp";

	private final String RECEIVENUMPROJECTLIST = "department/receiveNumProjectList.jsp";
	
	private final String ISSUEPROJECTLIST = "department/issueProjectList.jsp";
	
	private final String nlist = "department/nlist.jsp";
	private final String norgan = "department/norgan.jsp";
	private final String ndepart = "department/ndepart.jsp";
	private final String narea = "department/narea.jsp";
	//单位/部门树
	public void getTree(HttpServletRequest request, HttpServletResponse response)throws Exception {
		response.setContentType("text/html;charset=utf-8");
		Connection conn = null;
		try {
			ASFuntion CHF=new ASFuntion();
			
			//部门树：checked && departid && areaid && departname && isSubject
			String checked = CHF.showNull(request.getParameter("checked"));
			String departid = CHF.showNull(request.getParameter("departid"));//单位/区域/部门
			String areaid = CHF.showNull(request.getParameter("areaid"));//单位/区域/部门
			String departname = CHF.showNull(request.getParameter("departname"));	
			String isSubject = CHF.showNull(request.getParameter("isSubject"));	//判断是哪个节目
			
			//部门树加人：addUser
			String addUser = CHF.showNull(request.getParameter("addUser")); //用于追加一个人员树

			//部门授权  ： property && omenuid && (loginid || roleid) && userpopedom
			String userpopedom = CHF.showNull(request.getParameter("userpopedom"));	 //用于判断部门是否要加上选择框
			String menuid = CHF.showNull(request.getParameter("omenuid")); //菜单ID	
			String property = CHF.showNull(request.getParameter("property")); //UserPopedomService
			String loginid = CHF.showNull(request.getParameter("loginid")); //人员loginid
			String roleid = CHF.showNull(request.getParameter("roleid"));  //角色id
			
			if("".equals(property)) property = "user";
			
			System.out.println(userpopedom +"|" +addUser+"|"+checked+"|"+departid+"|"+areaid+"|"+departname+"|"+isSubject);
			
			conn = new DBConnect().getConnect("");
			
			//部门授权
			UserPopedomService up = new UserPopedomService(conn);
			//up.setProperty(property); //类型：user,role
			String departments = "";
			if("role".equals(property)){
				departments = up.getDepartment(roleid, menuid,property);
			}else{
				departments = up.getLoginIdDepartment(loginid, menuid,property);	
			}
			System.out.println("departments="+departments);
			
			DepartmentService ds = new DepartmentService(conn);
			ds.setAddUser(addUser); //追加人员树 addUser = "addUser"; 
			List list = null;
			if("".equals(isSubject) || "undefined".equals(isSubject)) {
				if("userpopedom".equals(userpopedom)){
					ds.setUserpopedom(departments);
					ds.setUserbool(true); 
				}
				list = ds.getOrgan(checked);	
				
				if(list == null){
					//区域表无值，直接展开部门表
					if("userpopedom".equals(userpopedom)){
						checked = "false";
						ds.setUserpopedom(departments);
					}
					departid = "555555";
					list = ds.getDepartment(departid, areaid, checked);
					
				}
			}else{
				if("1".equals(isSubject)){ 
					//如果是1，就表示当前节目是单位，要展开区域
					//1、区域表有值，要展开
					//2、区域表无值，直接展开部门表
					list = ds.getArea(departid,checked);
					if(list == null){
						//区域表无值，直接展开部门表
						if("userpopedom".equals(userpopedom)){
							checked = "false";
							ds.setUserpopedom(departments);
						}
						list = ds.getDepartment(departid, areaid, checked);
						if("true".equals(addUser)){
							List list1 = ds.getUser(departid, checked);
							if(list1 != null){
								if(list == null) list = new ArrayList();
								for(int i = 0;i<list1.size(); i++){
									list.add(list1.get(i));
								}
							}
						}
					}
				}else{
					//都是展开部门
					if("userpopedom".equals(userpopedom)){
						checked = "false";
						ds.setUserpopedom(departments);
					}
					list = ds.getDepartment(departid, areaid, checked);
					if("true".equals(addUser)){
						List list1 = ds.getUser(departid, checked);
						if(list1 != null){
							if(list == null) list = new ArrayList();
							for(int i = 0;i<list1.size(); i++){
								System.out.println(list1.get(i));
								list.add(list1.get(i));
							}
						}
					}
				}
			}
			
			String json = "{}";
			if(list != null){
				json = JSONArray.fromObject(list).toString();
			}
			System.out.println("json="+json);
			response.getWriter().write(json);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}
	}
	
	public void del(HttpServletRequest request, HttpServletResponse response)throws Exception {
		Connection conn = null;
		response.setContentType("text/html;charset=utf-8");
		try{
			ASFuntion CHF = new ASFuntion();
			
			String departid = CHF.showNull(request.getParameter("departid"));//单位/区域/部门
			String areaid = CHF.showNull(request.getParameter("areaid"));//单位/区域/部门
			String subject = CHF.showNull(request.getParameter("issubject")); //用来区分单位/区域/部门
			int issubject = Integer.parseInt(subject);
			
			conn = new DBConnect().getConnect("");
			DbUtil db = new DbUtil(conn);
			
			//删除单位->把单位的所以区域|部门放在555555下
			//删除区域->把区域下的部门areaid=''
			//删除部门->叶子部门直接删除，非叶子部门不能删除
			//部门下还有人员不能删除
			String result = "";
			
			String sql = "";
			switch (issubject) {
			case 1: //单位，
				//1、修改区域、部门放在555555下
				String [] args = new String[]{departid};
				sql = "update k_area set organid = '555555' where organid=? ";
				db.executeUpdate(sql, args);
				sql = "update k_department set parentid = '555555' where parentid = ? ";
				db.executeUpdate(sql, args);
				//2、删除单位
				sql = "delete from k_organ where departid = ? ";
				db.executeUpdate(sql, args);
				
				result = "OK|单位下的区域和部门都移到授权机构下，删除本单位成功！";
				args = null;
				break;
			case 2: //区域
				args = new String[]{areaid};
				//1、修改区域下的部门areaid=''
				sql = "update k_department set areaid = '' where areaid = ? ";
				db.executeUpdate(sql, args);
				//2、删除区域
				sql = "delete from k_area where autoid = ? ";
				db.executeUpdate(sql, args);
				
				result = "OK|区域下的部门都设置为无区域，删除本区域成功！";
				args = null;
				break;
			case 3: //部门
				args = new String[]{departid};
				boolean bool = true;
				String str = "";
				//1、判断部门下是否还有人员
				sql = "select 1 from k_user where departmentid = ? and state = 0 limit 1";
				str = CHF.showNull(db.queryForString(sql, args));
				if(bool && "1".equals(str)){
					//还有人员
					bool = false;
					result = "NO|该部门下面还有人员，不允许删除该部门！";
				}
				//2、判断部门是否叶子部门
				sql = "select isleaf from k_department where autoid = ? ";
				str = CHF.showNull(db.queryForString(sql, args));
				if(bool && "0".equals(str)){
					//非叶子部门
					bool = false;
					result = "NO|该部门还有下级部门，不允许删除该部门！";
				}
				//3、删除部门
				if(bool){
					sql = "delete from k_department where autoid = ?";
					db.executeUpdate(sql, args);
					
					//重算level0,fullpath,isleaf
					new DepartmentService(conn).updateDepartmentPath();
					result = "OK|删除本部门成功！";
				}
				break;
			}
			
			PrintWriter out = response.getWriter();
			out.write(result);
			out.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}	
	}
	
	//保存单位/区域/部门 信息
	public void save(HttpServletRequest request, HttpServletResponse response)throws Exception {
		Connection conn = null;
		response.setContentType("text/html;charset=utf-8");
		try{
			ASFuntion CHF = new ASFuntion();
			
			Map parameters = new HashMap();
			Enumeration enum1 = request.getParameterNames();
			while (enum1.hasMoreElements()) {
				String paramName = (String) enum1.nextElement();
				String [] paramValue = request.getParameterValues(paramName);
				if(paramValue.length == 1 
					&& !"defname".equals(paramName)
					&& !"defvalue".equals(paramName)
				){
					parameters.put(paramName, paramValue[0]);	
				}else{
					parameters.put(paramName, paramValue);
				}
			}
			
			String tableid = CHF.showNull((String)parameters.get("tableid")); //保存的表名
			String subject = CHF.showNull((String)parameters.get("issubject")); //用来区分单位/区域/部门
			int issubject = Integer.parseInt(subject);
			
			conn = new DBConnect().getConnect("");
			DepartmentService ds = new DepartmentService(conn);
			ds.save(tableid, issubject, parameters);
			
			PrintWriter out = response.getWriter();
			
			switch (issubject) {
			case 1: //单位，返回本页面
				String departid = CHF.showNull((String)parameters.get("departid")); 
				out.write("<script>");
				out.write("alert('保存单位信息成功');");
				out.write("parent.refreshTree();"); //刷新单位树
				out.write("window.location='"+request.getContextPath()+"/department.do?method=organ&departid="+departid+"&issubject="+issubject+"&rand="+Math.random()+"';");
				out.write("</script>");
				
				break;
			case 0: //区域
				issubject = 2;
			case 2: //区域
				String autoid = CHF.showNull((String)parameters.get("autoid")); 
				out.write("<script>");
				out.write("alert('保存区域信息成功');");
				out.write("parent.refreshTree();"); //刷新单位树
				out.write("window.location='"+request.getContextPath()+"/department.do?method=area&departid="+autoid+"&issubject="+issubject+"&rand="+Math.random()+"';");
				out.write("</script>");
				
				break;
			case 3: //部门
				autoid = CHF.showNull((String)parameters.get("autoid")); 
				out.write("<script>");
				out.write("alert('保存部门信息成功');");
				out.write("parent.refreshTree();"); //刷新单位树
				out.write("window.location='"+request.getContextPath()+"/department.do?method=depart&departid="+autoid+"&issubject="+issubject+"&rand="+Math.random()+"';");
				out.write("</script>");
				break;				
			default:
				break;
			}
			
			out.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}
	}
	
	//新的部门管理
	public ModelAndView list(HttpServletRequest request, HttpServletResponse response)throws Exception {
		ModelAndView modelAndView = new ModelAndView(nlist);
		return modelAndView;
	}
	
	//显示单位修改页面
	public ModelAndView organ(HttpServletRequest request, HttpServletResponse response)throws Exception {
		ModelAndView modelAndView = new ModelAndView(norgan);
		Connection conn = null;
		try {
			ASFuntion CHF=new ASFuntion();
			
			conn = new DBConnect().getConnect("");
			String departid = CHF.showNull(request.getParameter("departid"));//单位/区域/部门
			String issubject = CHF.showNull(request.getParameter("issubject"));//单位/区域/部门
			DepartmentService ds = new DepartmentService(conn);
			Map organ = ds.get("k_organ", "departid", departid);
			
			modelAndView.addObject("organ", organ);
			modelAndView.addObject("issubject", issubject);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}
		
		return modelAndView;
	}
	//显示区域修改页面
	public ModelAndView area(HttpServletRequest request, HttpServletResponse response)throws Exception {
		ModelAndView modelAndView = new ModelAndView(narea);
		Connection conn = null;
		try {
			ASFuntion CHF=new ASFuntion();
			
			conn = new DBConnect().getConnect("");
			String departid = CHF.showNull(request.getParameter("departid"));//单位/区域/部门
			String issubject = CHF.showNull(request.getParameter("issubject"));//单位/区域/部门
			
			String organid = CHF.showNull(request.getParameter("organid"));
			
			DepartmentService ds = new DepartmentService(conn);
			Map area = new HashMap();
			
			if(!"".equals(departid)){
				area = ds.get("k_area", "autoid", departid);
			}else{
				area.put("property", DELUnid.getNumUnid());
				area.put("organid", organid);
			}
			modelAndView.addObject("area", area);
			modelAndView.addObject("issubject", issubject);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}
		
		return modelAndView;
	}	
	
	
	//显示部门修改页面
	public ModelAndView depart(HttpServletRequest request, HttpServletResponse response)throws Exception {
		ModelAndView modelAndView = new ModelAndView(ndepart);
		Connection conn = null;
		try {
			ASFuntion CHF=new ASFuntion();
			
			conn = new DBConnect().getConnect("");
			String departid = CHF.showNull(request.getParameter("departid"));//单位/区域/部门
			String issubject = CHF.showNull(request.getParameter("issubject"));//单位/区域/部门
			
			String parentid = CHF.showNull(request.getParameter("parentid"));
			String areaid = CHF.showNull(request.getParameter("areaid"));
			
			DepartmentService ds = new DepartmentService(conn);
			
			Map depart = new HashMap();
			
			if(!"".equals(departid)){
				depart = ds.get("k_department", "autoid", departid);
				
				modelAndView.addObject("autoid1", departid);
			}else{
				depart.put("parentid", parentid);
				depart.put("areaid", areaid);
				depart.put("level0", "0");
				depart.put("ltype", "0");
				
				modelAndView.addObject("autoid1", "0");
			}
			
			depart.put("rand0", DELUnid.getNumUnid());
			
			modelAndView.addObject("depart", depart);
			modelAndView.addObject("issubject", issubject);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}
		
		return modelAndView;
	}
	
	//================================================================================================
	/**
	 * ��½
	 * 
	 * @param req
	 *            HttpServletRequest
	 * @param res
	 *            HttpServletResponse
	 * @return ModelAndView
	 */
	public ModelAndView list1(HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		// HashMap mapResult = new HashMap();
		DataGridProperty pp = new DataGridProperty();

		pp.setPrintEnable(true);
		pp.setPrintVerTical(false);
		pp.setPrintColumnWidth("34,26,90,52");
		// 必要设置
		pp.setTableID("department");
		// 基本设置
		// pp.setDatabaseDepartID("100900");
		pp.setPageSize_CH(50);
		// pp.setWhichFieldIsValue(0);
		ASFuntion CHF = new ASFuntion();
		String subSearchID = CHF.showNull(req.getParameter("menu_id"));
		String subSearchName = CHF.showNull(req.getParameter("name"));

		String sql = "select a.autoid,a.departname,a.parentid,a.Typeid, d.TypeName,CONCAT('[ ',a.Typeid,' ]',d.TypeName) AS tname," +
				"	group_concat(c.name) as member, ifnull(b.departname,(select departname from  k_customer where departid='555555' and property = '2'))as parentdeparment,a.url as url "
				+ "	from asdb.k_department a " +
				"	left join asdb.k_department b on a.parentid = b.autoid " +
				"	left join asdb.k_user c on a.autoid = c.departmentid " +
				"	left join asdb.k_AuditTypeTemplate d on a.Typeid = d.TypeId " +
				"	where 1=1 ";

		// sql设置

		if (!subSearchID.equals(""))
			sql = sql + "and a.autoid like '%" + subSearchID + "%'";
		if (!subSearchName.equals(""))
			sql = sql + "and a.departname like '%" + subSearchName + "%'";

		sql = sql + " group by a.departname";

		System.out.println(sql);

		// sql设置

		pp.setSQL(sql);

		pp.setOrderBy_CH("abs(a.property),autoid");
		pp.setDirection("asc,asc");

		// pp.setDirection_CH("autoId,departname,parentid,property,url");
		// pp.setDirection("desc");
		// pp.addSqlWhere("summary", "where keyvalue like '%${name}%'");

		pp.setInputType("radio");
		pp.addColumn("部门名称", "departname");
		pp.addColumn("上级部门", "parentdeparment");
		pp.addColumn("部门成员", "member");
		pp.addColumn("部门默认底稿模板", "TName");
		pp.addColumn("部门服务器地址", "url");

		// pp.setTableHead("部门号,部门名称,上级部门,部门成员,部门服务器地址");
		pp.setWhichFieldIsValue(1);

		req.getSession()
				.setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);

		return new ModelAndView(_strSuccess);
	}

	/**
	 * 判断部门是否还有人员
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	/*
	 * public ModelAndView isNullDepartment(HttpServletRequest request,
	 * HttpServletResponse response) throws Exception { ASFuntion asf = new
	 * ASFuntion(); Connection conn = null; String result = "ok"; try { conn =
	 * new DBConnect().getConnect(""); String autoId =
	 * asf.showNull(request.getParameter("autoId"));
	 * 
	 * result = new DepartmentService(conn).isNullDepartment(autoId);
	 * 
	 * response.setContentType("text/html;charset=utf-8"); //设置编码 PrintWriter
	 * out = response.getWriter(); out.print(result); out.close();
	 *  } catch (Exception e) { e.printStackTrace(); } finally { conn.close(); }
	 * 
	 * return null; }
	 */

	public ModelAndView isUpDepartment(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		ASFuntion asf = new ASFuntion();
		Connection conn = null;
		String result = "ko";
		try {
			conn = new DBConnect().getConnect("");
			String autoId = asf.showNull(request.getParameter("autoId"));

			result = new DepartmentService(conn).isUpDepartment(autoId);

			response.setContentType("text/html;charset=utf-8"); // 设置编码
			PrintWriter out = response.getWriter();
			out.print(result);
			out.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			conn.close();
		}

		return null;

	}

	public ModelAndView login(HttpServletRequest req, HttpServletResponse res) {
		ASFuntion CHF = new ASFuntion();
		// DataGridProperty
		// pp=(DataGridProperty)req.getSession().getAttribute(DataGrid.sessionPre+"department");
		// req.getSession().setAttribute(DataGrid.sessionPre+pp.getTableID(),pp);
		Connection conn = null;

		// String act= pp.getRequestValue("act");

		try {

			String act = req.getParameter("act");

			conn = new DBConnect().getConnect("");

			DepartmentService departmnetService = new DepartmentService(conn);
			UserdefService userDefService = new UserdefService(conn);

			DepartmentVO departmentVO = new DepartmentVO();
			String autoId = CHF.showNull(req.getParameter("chooseValue"));

			if (!autoId.equals("")) {
				departmentVO.setAutoId(Integer.parseInt(autoId));

			} else {
				departmentVO.setAutoId(0);

			}

			Map map = new HashMap();

			SetdefObjectService setdefObjectService = new SetdefObjectService(
					conn);
			List setValueList = setdefObjectService.getSetValueList("depart",
					autoId, "com_depart");
			map.put("setValueList", setValueList);

			if (act.equals("del")) {

				try {
					departmnetService.remove("2", departmentVO.getAutoId());
				} catch (Exception e) {

					String url = "department.do";
					res.setContentType("text/html;charset=utf-8"); // 设置编码
					PrintWriter out = res.getWriter();
					out.print("<script>");
					out.print(" alert('" + e.getMessage() + "'); ");
					out.print(" window.location=\"" + url + "\";");
					out.print("</script>");
					out.close();
					return null;
				}

				return new ModelAndView(_strSuccess);

			} else if (act.equals("update")) {

				DepartmentVO departmentVO1 = departmnetService
						.getVo(departmentVO.getAutoId());
				String property = "depart";
				Userdef[] userdef = userDefService.getUserdef(autoId, property);

				map.put("departmentVO", departmentVO1);
				map.put("userdef", userdef);
				req.setAttribute("act", act);
				return new ModelAndView(_strAddaddEdit, map);

			} else if (act.equals("add")) {
				req.setAttribute("act", act);
				req.setAttribute("setValueList", setValueList);
				return new ModelAndView(_strAddaddEdit, "departmentVO",
						departmentVO);

			} else if (act.equals("popdemUpdate")) {
				return new ModelAndView(_strPopdem);
			}

		} catch (Exception e) {
			Debug.print(Debug.iError, "����ʧ��", e);
		} finally {
			DbUtil.close(conn);
		}

		return null;
	}

	public ModelAndView addAndEdit(HttpServletRequest req,
			HttpServletResponse res) throws Exception {
		ASFuntion CHF = new ASFuntion();
		// DataGridProperty
		// pp=(DataGridProperty)req.getSession().getAttribute(DataGrid.sessionPre+"department");
		// req.getSession().setAttribute(DataGrid.sessionPre+pp.getTableID(),pp);
		Connection conn = null;
		try {

			conn = new DBConnect().getConnect("");

			DepartmentVO departmentVO = new DepartmentVO();

			String autoId = CHF.showNull(req.getParameter("chooseValue"));
			if (!autoId.equals("")) {
				departmentVO.setAutoId(Integer.parseInt(autoId));
			} else {
				departmentVO.setAutoId(0);
			}
			String property = "depart";
			DepartmentService departmnetService = new DepartmentService(conn);
			// UserdefService userDefService=new UserdefService(conn);
			// userDefService.removeUserdef(autoId, property);

			departmentVO.setAutoId(departmentVO.getAutoId());
			departmentVO.setUrl(CHF.showNull(req.getParameter("url")));
			departmentVO.setParentId(Integer.parseInt(req
					.getParameter("parentID")));
			departmentVO.setDepartmentName(req.getParameter("departName"));
			departmentVO.setTypeid(req.getParameter("typeid"));
			departmentVO.setPostalcode(req.getParameter("postalcode"));
			departmentVO.setAddress(req.getParameter("address"));

			// userDefVO.setContrastid(req.getParameter("chooseValue"));
			// ArrayList arrayList=new ArrayList();

			String[] userDefName = req.getParameterValues("UserDefName");
			String[] userDefValue = req.getParameterValues("UserDefValue");

			Userdef[] userdefs = null;
			// 判断有无自定义信息
			int lengh = userDefName == null ? 0 : userDefName.length;
			userdefs = new Userdef[lengh];
			Userdef userdef = null;
			for (int i = 0; i < userdefs.length; i++) {
				userdef = new Userdef();
				userdef.setName(userDefName[i]);
				userdef.setValue(userDefValue[i]);

				userdefs[i] = userdef;
			}

			String act = req.getParameter("act");
			departmnetService
					.AddOrUpdate(departmentVO, userdefs, act, property);

			// System.out.println("==============================================================");
			UserdefService udm = new UserdefService(conn);
			String commondefNames = CHF.showNull(req
					.getParameter("commondefNames"));
			String commondefValues = CHF.showNull(req
					.getParameter("commondefValues"));

			if (!commondefNames.equals("") && !commondefValues.equals("")) {

				String commondefnames[] = commondefNames.substring(0,
						commondefNames.length() - 1).split("-");
				String commondefvalues[] = commondefValues.substring(0,
						commondefValues.length() - 1).split("-");

				Userdef[] commonsetdef = new Userdef[commondefnames.length];

				for (int i = 0; i < commondefnames.length; i++) {
					if (commondefvalues[i].equals("NaN")) {
						commondefvalues[i] = "";
					}
					String name = commondefnames[i];
					String value = commondefvalues[i];

					// System.out.println(name);
					// System.out.println(value);

					commonsetdef[i] = new Userdef();
					commonsetdef[i].setName(name);
					commonsetdef[i].setValue(value);
					commonsetdef[i].setProperty("com_depart");

				}

				String contrastid = departmnetService.getDepartId(departmentVO
						.getDepartmentName());
				udm.addOrupdateUserdef(commonsetdef, contrastid, "com_depart");

			}

			// System.out.println("==============================================================");

			res.sendRedirect("department.do");
			return null;
		} catch (Exception e) {
			Debug.print(Debug.iError, "����ʧ��", e);
			throw e;
		} finally {
			DbUtil.close(conn);
		}
		// return new ModelAndView(_strSuccess);
	}

	/**
	 * 设置打印属性
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView print(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Connection conn = null;
		HashMap mapResult = new HashMap();
		try {
			// String temp =
			// com.matech.framework.pub.sys.UTILSysProperty.SysProperty.getProperty("clientDog");
			conn = new DBConnect().getConnect("");
			String tableid = request.getParameter("tableid");

			DataGridProperty pp = (DataGridProperty) request.getSession()
					.getAttribute(DataGrid.sessionPre + tableid);

			PrintSetup printSetup = new PrintSetup(conn);

			printSetup.setStrTitles(new String[] { "部门列表" });

			printSetup.setStrQuerySqls(new String[] { pp.getFinishSQL() });
			printSetup
					.setStrChineseTitles(new String[] { "部门号`部门名称`上级部门`属性`全名`部门服务期地址" });
			printSetup.setCharColumn(new String[] { "1`2`3`4`5`6" });

			printSetup.setIColumnWidths(new int[] { 20, 20, 20, 20, 20, 20 });

			String filename = printSetup.getExcelFile();

			// vpage strPrintTitleRows
			mapResult.put("refresh", "");

			mapResult.put("saveasfilename", "部门列表");
			mapResult.put("vpage", "false");
			mapResult.put("strPrintTitleRows", "$2:$4");
			mapResult.put("filename", filename);

		} catch (Exception e) {
			Debug.print(Debug.iError, "查询个人项目项目权限失败！", e);
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}

		return new ModelAndView("/Excel/tempdata/PrintandSave.jsp", mapResult);
	}

	public ModelAndView checkExists(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		ASFuntion asf = new ASFuntion();
		Connection conn = null;
		PrintWriter out = null;
		try {
			conn = new DBConnect().getConnect("");
			String departName = asf.showNull(request.getParameter("departName"));

			boolean flag = false;
			
			flag = new DepartmentService(conn).checkExists(departName);

			response.setContentType("text/html;charset=utf-8"); // 设置编码
			out = response.getWriter();
			
			if(flag) {
				out.print("yes");
			} else {
				out.print("no");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			out.close();
			conn.close();
		}
		return null;
	}
	
	/**
	 * 设置部门排序
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView setOrderBy(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Connection conn = null;
		
		ModelAndView modelAndView = new ModelAndView(SET_ORDER_BY_VIEW);
		try {
			conn = new DBConnect().getConnect("");
			
			List<DepartmentVO> departmentList = new DepartmentService(conn).getDepartmentList();
			
			modelAndView.addObject("departmentList", departmentList);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			conn.close();
		}
		return modelAndView;
	}
	
	/**
	 * 保存排序
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView saveOrderBy(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		ASFuntion asf = new ASFuntion();
		Connection conn = null;
		PrintWriter out = null;
		try {
			conn = new DBConnect().getConnect("");
			String values = asf.showNull(request.getParameter("values"));

			int result = new DepartmentService(conn).saveOrderBy(values);

			response.setContentType("text/html;charset=utf-8"); // 设置编码
			out = response.getWriter();
			
			out.print(result);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			out.close();
			conn.close();
		}
		return null;
	}
	
	
	/**
	 * 部门人员情况一览表
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView usageStaticList(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		ModelAndView modelAndView = new ModelAndView(STATICLIST) ;
		UserSession userSession = (UserSession)request.getSession().getAttribute("userSession") ;
		Connection conn = null ;
		ASFuntion CHF = new ASFuntion() ;
		try {
			
			conn = new DBConnect().getConnect("") ;
			//String sumSql = sbSql.toString() + groupSql ;
			

			
			StringBuffer sbSql = new StringBuffer() ;
			
			String departname = CHF.showNull(request.getParameter("departname"));
			String level = CHF.showNull(request.getParameter("level"));
			String isCurLevel = CHF.showNull(request.getParameter("isCurLevel"));
			String beginDate = CHF.showNull(request.getParameter("beginDate"));
			String endDate = CHF.showNull(request.getParameter("endDate"));
			
			
			String oralLevel = level ;
			
			if(!"".equals(departname)) {
				departname = "and a.departname like '%"+departname+"%' " ;
			}else {
				departname = "" ;
			}
			
			if("".equals(level)) {
				//初始值，1级
				level = " and a.level0 = 1 " ; //默认显示1级
			}else if("all".equals(level)) {
				//全部
				level = " " ; 
			}else {
				if(!"".equals(isCurLevel)) {
					level = "and a.level0 = '"+level+"' " ;
				}else {
					level = "and a.level0 >= '"+level+"' " ;
				}
			}
			
			if("".equals(isCurLevel)) {
				//初始值 是
				isCurLevel = "是" ;
			}
			
			
			//void:计算初始日期,默认区间为本周，周日开始
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd") ;
			
			Calendar calendar = new GregorianCalendar();
			calendar.setTime(new Date());
			calendar.setFirstDayOfWeek(Calendar.SUNDAY);	//周的第一天为：周日
			
			//本周开始日
			
			if("".equals(beginDate)) {
				calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
				beginDate = sdf.format(calendar.getTime()) ;
				
				//本周结束日
				calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek() + 6);
				endDate = sdf.format(calendar.getTime()) ;
			}
			 
			modelAndView.addObject("departname",departname) ;
			modelAndView.addObject("level",oralLevel) ;
			modelAndView.addObject("isCurLevel",isCurLevel) ;
			modelAndView.addObject("endDate",endDate) ;
			modelAndView.addObject("beginDate",beginDate) ;
			
					
			sbSql.append(" SELECT a.departmentId,a.departname,property \n") ;
			sbSql.append(" ,SUM(rs) AS rs   #累计部门人数 \n") ;
			sbSql.append(" ,SUM(khs) AS khs #累计客户数  \n") ;
			sbSql.append(" ,SUM(xms) AS xms #累计项目数 \n") ;
			sbSql.append(" ,SUM(dls) AS dls #期间登录数  \n") ;
			sbSql.append(" ,SUM(pbs) AS pbs #期间排班数 \n") ;
			sbSql.append(" ,SUM(sbs) AS sbs #期间申报数 \n") ;
			sbSql.append(" ,SUM(jxrs) AS jxrs #建项人数 \n") ;
			sbSql.append(" ,SUM(wdls) AS wdls #未登陆数 \n") ;
			sbSql.append(" ,SUM(wpbs) AS wpbs #未排班人数 \n") ;
			sbSql.append(" ,SUM(wsbs) AS wsbs #未申报人数 \n") ;
			sbSql.append(" ,SUM(kpbs) AS kpbs #有权排班人数 \n") ;
			sbSql.append(" ,SUM(lhs) AS lhs #二审领号数 \n") ;    
			sbSql.append(" ,SUM(qzs) AS qzs #报告签发数  \n") ;
			
			sbSql.append(" FROM ( \n") ;
			
			sbSql.append("  SELECT a.departmentid,a.departname,a.property,a.level0 \n") ;
			sbSql.append(" ,ifnull(b.rs,0) as rs  #累计部门人数	\n") ;
			sbSql.append(" ,ifnull(c.khs,0) as khs #累计客户数  \n") ; 
			sbSql.append(" ,ifnull(d.xms,0) as xms #累计项目数 \n") ;
			sbSql.append(" ,ifnull(dls,0) as dls   #期间登录数 \n") ;
			sbSql.append(" ,ifnull(f.pbs,0) as pbs #期间排班数 \n") ;
			sbSql.append(" ,ifnull(g.sbs,0) as sbs #期间申报数 \n") ;
			sbSql.append(" ,IFNULL(h.jxrs,0) AS jxrs #建项人数  \n") ;
			sbSql.append(" ,IFNULL(i.wdls,0) AS wdls #未登陆数  \n") ;
			sbSql.append(" ,IFNULL(j.wpbs,0) AS wpbs #未排班人数 \n") ;
			sbSql.append(" ,IFNULL(k.wsbs,0) AS wsbs #未申报人数 \n") ;
			sbSql.append(" ,IFNULL(l.kpbs,0) AS kpbs #有权排班人数  \n") ;
			sbSql.append(" ,IFNULL(m.lhs,0) AS lhs   #二审领号数  \n") ;
			sbSql.append(" ,IFNULL(n.qzs,0) AS qzs 	 #报告签发数  \n") ;
			sbSql.append(" FROM (  \n") ;
			sbSql.append(" 	SELECT a.autoid AS departmentId,a.departname,a.`property`,a.level0,b.autoid,b.`departname` AS bdeparname   \n") ;
			sbSql.append(" 	FROM k_department a  \n") ;
			sbSql.append(" 	LEFT JOIN k_department b  \n") ;
			sbSql.append(" 	ON b.`fullPath` LIKE CONCAT(a.`fullPath`,'%') \n") ;
			sbSql.append(" ) a  \n") ;
			sbSql.append(" LEFT JOIN ( \n") ;
			sbSql.append(" 		SELECT departmentid,COUNT(*) AS rs \n") ;
			sbSql.append(" 		FROM k_user b \n") ;
			sbSql.append(" 		WHERE state=0 \n") ;
			sbSql.append(" 		GROUP BY departmentid \n") ;
			sbSql.append(" )b ON a.autoid=b.departmentid \n") ;
			sbSql.append(" LEFT JOIN (	 \n") ;
			sbSql.append(" 		SELECT departmentid,COUNT(*) AS khs \n") ;
			sbSql.append(" 		FROM k_customer b \n") ;
			sbSql.append(" 		GROUP BY departmentid \n") ;
			sbSql.append(" )c ON a.autoid=c.departmentid \n") ;
			sbSql.append(" LEFT JOIN (	\n") ;
			sbSql.append(" 		SELECT departmentid,COUNT(*) AS xms \n") ;
			sbSql.append(" 		FROM z_project b \n") ;
			sbSql.append(" 		GROUP BY departmentid \n") ;
			sbSql.append(" )d ON a.autoid=d.departmentid \n") ;
			sbSql.append(" LEFT JOIN (	\n") ;
			sbSql.append(" 		SELECT b.departmentid,COUNT(*) AS dls\n") ;
			sbSql.append(" 		FROM t_log a,k_user b \n") ;
			sbSql.append(" 		WHERE udate>='"+beginDate+"' \n") ;
			sbSql.append(" 		AND udate<='"+endDate+"' \n") ;
			sbSql.append(" 		AND a.cmdname IN ('用户登录','排班系统') \n") ;
			sbSql.append(" 		AND a.loginid=b.loginid \n") ;
			sbSql.append(" 		AND b.state=0 \n") ;
			sbSql.append(" 		GROUP BY b.departmentid \n") ;
			sbSql.append(" )e ON a.autoid=e.departmentid\n") ;
			sbSql.append(" LEFT JOIN ( \n") ;
			sbSql.append(" 		SELECT c.departmentid,COUNT(workdate) AS pbs\n") ;
			sbSql.append(" 		FROM oa_timesschedular b,k_user c \n") ;
			sbSql.append(" 		WHERE b.userId = c.loginid \n") ;
			sbSql.append(" 		AND workdate>='"+beginDate+"' \n") ;
			sbSql.append(" 		AND workdate<='"+endDate+"' \n") ;
			sbSql.append(" 		AND c.state=0 \n") ;
			sbSql.append(" 		GROUP BY c.departmentid \n") ;
			sbSql.append(" )f ON a.autoid=f.departmentid \n") ;
			sbSql.append(" LEFT JOIN (	\n") ;
			sbSql.append(" 		SELECT c.departmentid,COUNT(DISTINCT workdate) AS sbs \n") ;
			sbSql.append(" 		FROM oa_timesreport b,k_user c \n") ;
			sbSql.append(" 		WHERE b.userId = c.loginid \n") ;
			sbSql.append(" 		and workdate>='"+beginDate+"' \n") ;
			sbSql.append(" 		AND workdate<='"+endDate+"' \n") ;
			sbSql.append(" 		AND c.state=0 \n") ;
			sbSql.append(" 		GROUP BY c.departmentid \n") ;
			sbSql.append(" )g ON a.autoid=g.departmentid \n") ;
			
			sbSql.append(" LEFT JOIN ( \n") ;
			sbSql.append(" 	SELECT departmentid,COUNT(1) AS jxrs \n") ;
			sbSql.append(" 	FROM ( \n") ;
			sbSql.append(" 		SELECT DISTINCT b.userid,b.departmentid,b.name \n") ;
			sbSql.append(" 		FROM  ( \n") ;
			sbSql.append(" 			SELECT   c.id,c.menu_id,c.name \n") ;
			sbSql.append(" 			FROM s_sysmenu a, k_menuversion b ,s_sysmenu c,k_menuversion d  \n") ;
			sbSql.append(" 			WHERE 1 = 1 AND b.menuversion = '项目管控' AND a.id = b.menuid AND d.menuversion = '项目管控'  \n") ;
			sbSql.append(" 			AND c.id = d.menuid  AND a.name='项目维护' AND c.name='新建' \n") ;
			sbSql.append(" 			AND c.parentid=a.menu_id \n") ;
			sbSql.append(" 		) a , ( \n") ;
			sbSql.append(" 			SELECT id AS userid,popedom ,departmentid,NAME,state \n") ;
			sbSql.append(" 			FROM k_user  \n") ;
			sbSql.append(" 			UNION ALL \n") ;
			sbSql.append(" 			SELECT a.userid,b.popedom ,c.departmentid,c.name,state \n") ;
			sbSql.append(" 			FROM k_user c,k_userrole a,k_role b  \n") ;
			sbSql.append(" 			WHERE c.id=a.userid AND a.rid = b.id  \n") ;
			sbSql.append(" 		) b  \n") ;
			sbSql.append(" 		WHERE b.popedom LIKE CONCAT('%.', a.id, '.%') \n") ;
			sbSql.append(" 		and b.state = 0 \n") ;
			sbSql.append(" 	)t GROUP BY departmentid\n") ;
			sbSql.append(" ) h ON a.autoid = h.departmentid \n") ;
			
			sbSql.append(" LEFT JOIN ( \n") ;
			sbSql.append("  SELECT a.departmentid,COUNT(*) AS wdls \n") ;
			sbSql.append("  FROM k_user a \n") ;
			sbSql.append("  LEFT JOIN ( \n") ;
			sbSql.append("  	SELECT DISTINCT loginid FROM t_log b \n") ;
			sbSql.append("  	WHERE udate>='"+beginDate+"' \n") ;
			sbSql.append("  	AND udate<='"+endDate+"' \n") ;
			sbSql.append("  	AND b.cmdname IN ('用户登录','排班系统') \n") ;
			sbSql.append("  ) b ON a.loginid=b.loginid  \n") ;
			sbSql.append("  WHERE b.loginid IS NULL \n") ;
			sbSql.append("  AND a.state=0  \n") ;
			sbSql.append("  GROUP BY a.departmentid \n") ;
			sbSql.append(" ) i ON a.autoid=i.departmentid\n") ;
			sbSql.append(" LEFT JOIN ( \n") ;
			sbSql.append("  SELECT departmentid,COUNT(*) AS wpbs FROM \n") ;
			sbSql.append("  k_user a \n") ;
			sbSql.append("  LEFT JOIN (  \n") ;
			sbSql.append("  	SELECT DISTINCT userid FROM \n") ;
			sbSql.append("  	oa_timesschedular \n") ;
			sbSql.append("  	WHERE workdate>='"+beginDate+"' \n") ;
			sbSql.append("  	AND workdate<='"+endDate+"'  \n") ;
			sbSql.append("  ) b ON a.loginid = b.userid \n") ;
			sbSql.append("  WHERE b.userid IS NULL \n") ;
			sbSql.append("  AND a.state=0  \n") ;
			sbSql.append("  GROUP BY departmentid \n") ;
			sbSql.append(" ) j ON  a.autoid=j.departmentid \n") ;
			sbSql.append(" LEFT JOIN ( \n") ;
			sbSql.append("  SELECT departmentid,COUNT(*) AS wsbs FROM \n") ;
			sbSql.append("  k_user a \n") ;
			sbSql.append("  LEFT JOIN ( \n") ;
			sbSql.append("  	SELECT DISTINCT userid FROM \n") ;
			sbSql.append("  	oa_timesreport \n") ;
			sbSql.append("  	WHERE workdate>='"+beginDate+"' \n") ;
			sbSql.append("  	AND workdate<='"+endDate+"' \n") ;
			sbSql.append("  ) b ON a.loginid = b.userid \n") ;
			sbSql.append("  WHERE b.userid IS NULL  \n") ;
			sbSql.append("  AND a.state=0  \n") ;
			sbSql.append("  GROUP BY departmentid \n") ;
			sbSql.append("  ) k ON  a.autoid=k.departmentid \n") ;
			sbSql.append("  LEFT JOIN ( \n") ;
			sbSql.append("  	SELECT departmentid,COUNT(distinct c.id) AS kpbs \n") ;
			sbSql.append("  	FROM k_role a,k_userrole b,k_user c \n") ;
			sbSql.append("  	WHERE a.id = b.rid \n") ;
			sbSql.append("  	AND a.rolename LIKE '%合伙人%' \n") ;
			sbSql.append("  	AND c.id = b.userid \n") ;
			sbSql.append("  	GROUP BY c.departmentid \n") ;
			sbSql.append(" 	) l ON a.autoid=l.departmentid \n") ;
			
			sbSql.append(" 	LEFT JOIN ( \n") ;
			
			sbSql.append(" 		SELECT COUNT(DISTINCT a.projectid) as lhs,departmentid FROM  \n") ;
			sbSql.append(" 		z_project a \n") ;
			sbSql.append(" 		INNER JOIN z_auditstep b ON a.projectid = b.projectid \n") ;
			sbSql.append(" 		AND b.property > '' AND taskArriveTime > '' \n") ;
			sbSql.append("  	and taskArriveTime>='"+beginDate+"' \n") ;
			sbSql.append("  	AND taskArriveTime<='"+endDate+"' \n") ;
			sbSql.append(" 		GROUP BY a.departmentid  \n") ;
		
			sbSql.append(" 	) m ON a.autoid = m.departmentid \n") ;
			sbSql.append(" 	LEFT JOIN ( \n") ;
			sbSql.append(" 		SELECT b.departmentid,COUNT(DISTINCT b.projectid) AS qzs \n") ;
			sbSql.append(" 		FROM z_auditpeople a,z_project b \n") ;
			sbSql.append(" 		WHERE a.role='签字合伙人' \n") ;
			sbSql.append(" 		AND a.projectid = b.projectid \n") ;
			sbSql.append("  	and appointdate>='"+beginDate+"' \n") ;
			sbSql.append("  	AND appointdate<='"+endDate+"' \n") ;
			sbSql.append(" 		GROUP BY b.departmentid \n") ;
			sbSql.append(" 	) n ON a.autoid = n.departmentid\n") ;
			
			sbSql.append(" ) a \n") ;
			sbSql.append(" where 1=1 "+departname+" "+level+"\n") ;
			
			String groupSql = " GROUP BY a.departmentId,a.departname \n" ;
			
			
			String curDepartname = userSession.getUserAuditDepartmentName() ;
			String curDepartId = userSession.getUserAuditDepartmentId() ;
			
			if(!"主任室".equals(curDepartname) && !"19".equals(userSession.getUserId()) && !"480".equals(curDepartId)) {
				
				PreparedStatement ps = null ;
				ResultSet rs = null ;
				try {
					// 找出项目权限
					String departmentIds = "" ;
					String sql = " select concat(ifnull(group_concat(a.autoid),-1),',',b.departmentid) from "
							+ " k_department a,k_user b "
							+ " where b.ProjectPopedom like concat('%.',a.autoid,'.%') "
							+ "	and b.id=? ";
					ps = conn.prepareStatement(sql);
					ps.setString(1, userSession.getUserId());
					rs = ps.executeQuery();

					if (rs.next()) {
						departmentIds = rs.getString(1);
					}
					
					if(!"".equals(departmentIds)) {
						sbSql.append(" and a.departmentId in("+departmentIds+") \n") ;
					}

				} catch (Exception e) {
					e.printStackTrace();
					throw new Exception("项目列表获得连接失败", e);
				} finally {
					DbUtil.close(rs);
					DbUtil.close(ps);
				}
				modelAndView.addObject("limit","true") ;
			}
			//合计sql ;
			PreparedStatement ps = null ;
			ResultSet rs = null ;
			String rs1 = "",khs = "",xms = "",dls = "",pbs = "",sbs = "",jxrs = "",wdls = "",wpbs = "",wsbs = "",kpbs = "",lhs="",qzs="" ;
			
			try {

				String sumSql =  " SELECT \n "
					  +  " SUM(rs) AS rs,SUM(khs) AS khs,SUM(xms) AS xms, \n" 
					  +  " SUM(dls) AS dls,SUM(pbs) AS pbs,SUM(sbs) AS sbs, \n" 
					  +  " SUM(jxrs) AS jxrs,SUM(wdls) AS wdls,SUM(wpbs) AS wpbs, \n" 
					  +  " SUM(wsbs) AS wsbs,SUM(kpbs) AS kpbs,sum(qzs) as qzs,sum(lhs) as lhs FROM ( \n" 
					  +  sbSql.toString() + " \n" + groupSql 
					  +  " ) a \n"  ;
				
				ps = conn.prepareStatement(sumSql) ;
				rs = ps.executeQuery() ;
				
				
				if(rs.next()) {
					rs1 = rs.getString(1) ;
					khs = rs.getString(2) ;
					xms = rs.getString(3) ;
					dls = rs.getString(4) ;
					pbs = rs.getString(5) ;
					sbs = rs.getString(6) ;
					jxrs = rs.getString(7) ;
					wdls = rs.getString(8) ;
					wpbs = rs.getString(9) ;
					wsbs = rs.getString(10) ;
					kpbs = rs.getString(11) ;
					qzs = rs.getString(12) ;
					lhs = rs.getString(13) ;
				}

			} catch (Exception e) {
				e.printStackTrace();
				throw new Exception("合计sql执行出错啦!!", e);
			} finally {
				DbUtil.close(rs);
				DbUtil.close(ps);
			}
			
			/*
			String sumSql =  " SELECT '' AS departmentid,'合计' AS departname,0 AS property,\n "
						  +  " SUM(rs) AS rs,SUM(khs) AS khs,SUM(xms) AS xms, \n" 
						  +  " SUM(dls) AS dls,SUM(pbs) AS pbs,SUM(sbs) AS sbs, \n" 
						  +  " SUM(jxrs) AS jxrs,SUM(wdls) AS wdls,SUM(wpbs) AS wpbs, \n" 
						  +  " SUM(wsbs) AS wsbs,SUM(kpbs) AS kpbs FROM ( \n" 
						  +  sbSql.toString() + " and level0 = 1 \n" + groupSql
						  +  " ) a \n"  
						  +  " union all\n"
						  +  sbSql.toString() + groupSql ; 
			*/
			//合起来报错 只能先执行完合计的sql再union
			
			String sumSql =  " SELECT '' AS departmentid,'<font color=blue>合计</font>' AS departname,0 AS property,\n "
				  +  " "+rs1+" AS rs,"+khs+" AS khs,"+xms+" AS xms, \n" 
				  +  " "+dls+" AS dls,"+pbs+" AS pbs,"+sbs+" AS sbs, \n" 
				  +  " "+jxrs+" AS jxrs,"+wdls+" AS wdls,"+wpbs+" AS wpbs, \n" 
				  +  " "+wsbs+" AS wsbs,"+kpbs+" AS kpbs,  \n" 
				  +  " "+lhs+" AS lhs,"+qzs+" AS qzs  \n" 
				  +  " union all\n"
				  +  sbSql.toString() + departname + groupSql+ " order by abs(property) asc " ; 
			
			DataGridProperty pp = new DataGridProperty();
			
			pp.setSQL(sumSql);
			pp.setOrderBy_CH("abs(property)");
			pp.setDirection("asc");
			
			
			pp.setTableID("usageStaticList") ;
			pp.setWhichFieldIsValue(1);
			pp.setColumnWidth("13,7,7,7,7,7,7,7,7,7") ;
			pp.setCustomerId("") ;
			pp.setCancelPage(true) ;
			
			pp.setCancelOrderby(true) ;
			
			pp.setTrActionProperty(true) ;
			pp.setTrAction(" departmentid=${departmentid} ") ;
			
			pp.addSqlWhere("beginDate", "${beginDate}");
			pp.addSqlWhere("endDate", "${endDate}");
			pp.addSqlWhere("departname", "${departname}");
			pp.addSqlWhere("level", "${level}");
			pp.addSqlWhere("isCurLevel", "${isCurLevel}");
			
			pp.addColumn("部门", "departname");
			pp.addColumn("累计部门人数", "rs","showCenter",null,"<div style='cursor:pointer;'>${value}</div>");
			pp.addColumn("可建项人数","jxrs","showCenter",null,"<div style='cursor:pointer;'>${value}</div>");
			pp.addColumn("可排班人数", "kpbs","showCenter",null,"<div style='cursor:pointer;'>${value}</div>");
			pp.addColumn("未登陆人数", "wdls","showCenter",null,"<div style='cursor:pointer;'>${value}</div>");
			pp.addColumn("累计客户数", "khs","showCenter",null,"<div style='cursor:pointer;'>${value}</div>");
			pp.addColumn("累计项目数", "xms","showCenter",null,"<div style='cursor:pointer;'>${value}</div>");
			pp.addColumn("期间登录次数", "dls","showCenter",null,"<div style='cursor:pointer;'>${value}</div>");
			pp.addColumn("期间排班天数", "pbs","showCenter",null,"<div style='cursor:pointer;'>${value}</div>");
			pp.addColumn("期间未排班人数", "wpbs","showCenter");
			pp.addColumn("期间申报天数", "sbs","showCenter");
			pp.addColumn("期间未申报人数", "wsbs","showCenter");
			pp.addColumn("二审领号数", "lhs","showCenter");
			pp.addColumn("报告签发数", "qzs","showCenter");
			
			
			pp.setTitle("<center>部门工作情况一览表</center>") ;
			
			pp.setPrintEnable(true) ;
			pp.setPrintTitle("部门工作情况一览表") ;
			
			
			request.getSession().setAttribute(DataGrid.sessionPre+pp.getTableID(),pp);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn) ;
		}
		return modelAndView;
	}
	
	
	
	/**
	 * 部门人员情况一览表穿透到人员
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView userList(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		ModelAndView modelAndView = new ModelAndView(USERLIST) ;
		ASFuntion CHF = new ASFuntion();
		Connection conn = null ;
		try {
			
			StringBuffer sbSql = new StringBuffer() ;
			conn = new DBConnect().getConnect("") ;
			
			String departmentid = CHF.showNull(request.getParameter("departmentid")) ;
			String col = CHF.showNull(request.getParameter("col")) ;
			
			
			if(!"".equals(departmentid)){
				departmentid = " and  departmentid in (SELECT a.autoid FROM k_department a ,k_department b "
							 + " WHERE a.fullpath LIKE CONCAT(b.fullpath,'%') AND b.autoid = '"+departmentid+"')" ;
			}else{
				departmentid  = "";
			}
			
			String beginDate = CHF.showNull(request.getParameter("beginDate")) ;
			String endDate = CHF.showNull(request.getParameter("endDate")) ;
			
			modelAndView.addObject("beginDate",beginDate) ;
			modelAndView.addObject("endDate", endDate) ;
			
			if(!"".equals(beginDate)) {
				beginDate = " and udate>='"+beginDate+"'" ;
			}
			
			if(!"".equals(endDate)) {
				endDate = " and udate<='"+endDate+"'" ;
			}
			
			//统计人数
			String userids = "" ;
			String userSql = "" ;
				
			if("4".equals(col)) {
				//可建项人数
				
				userSql = " 		SELECT ifnull(group_concat(b.userid),-1) \n"
						+ " 		FROM  ( \n"
						+ " 			SELECT   c.id,c.menu_id,c.name \n"
						+ " 			FROM s_sysmenu a, k_menuversion b ,s_sysmenu c,k_menuversion d  \n"
						+ " 			WHERE 1 = 1 AND b.menuversion = '项目管控' AND a.id = b.menuid AND d.menuversion = '项目管控'  \n"
						+ " 			AND c.id = d.menuid  AND a.name='项目维护' AND c.name='新建' \n"
						+ " 			AND c.parentid=a.menu_id \n"
						+ " 		) a , ( \n"
						+ " 			SELECT id AS userid,popedom ,departmentid,NAME \n"
						+ " 			FROM k_user  \n"
						+ " 			UNION ALL \n"
						+ " 			SELECT a.userid,b.popedom ,c.departmentid,c.name \n"
						+ " 			FROM k_user c,k_userrole a,k_role b  \n"
						+ " 			WHERE c.id=a.userid AND a.rid = b.id  \n"
						+ " 		) b  \n"
						+ " 		WHERE b.popedom LIKE CONCAT('%.', a.id, '.%') \n"
						+ departmentid ;
				   
			}else if("5".equals(col)) {
				//可排班人数
				userSql = "select ifnull(group_concat(b.userid),-1) "
					+ "FROM k_role a,k_userrole b,k_user c " 
					+ "WHERE a.id = b.rid " 
					+ "AND b.userid = c.id " 
					+ "AND a.rolename LIKE '%合伙人%' " 
					+ departmentid ;
				  
			}else if("6".equals(col)) {
				userSql = "select ifnull(group_concat(a.id),-1) "
					+ "FROM k_user a " 
					+ "	LEFT JOIN ( " 
					+ "		SELECT DISTINCT loginid FROM t_log b  " 
					+ "		WHERE 1=1 " +beginDate+endDate
					+ "		AND b.cmdname IN ('用户登录','排班系统') " 
					+ "	) b ON a.loginid=b.loginid " 
					+ "	WHERE b.loginid IS NULL " 
					+ departmentid ;
			}else if("9".equals(col)) {
				//期间登陆次数
				userSql = "select ifnull(group_concat(b.id),-1) "
						+ "FROM t_log a,k_user b " 
						+ "where 1=1 " 
						+ beginDate+endDate
						+ "	AND a.cmdname IN ('用户登录','排班系统')  " 
						+ "	AND a.loginid=b.loginid   " 
						+ departmentid ;
				
			}  
			if(!"".equals(userSql)) {
				DbUtil dbUtil = new DbUtil(conn);
				userids = CHF.showNull(dbUtil.queryForString(userSql)) ;
			}
			
			if(!"".equals(userids)) {
				userids = " and userid in(" + userids + ")" ;
			}
			
			sbSql.append(" select distinct a.id,name,loginid,IF(sex = 'M' OR sex='男','男','女') AS sex,a.rank,roles,ifnull(loginTimes,0) as loginTimes,d.departname from \n") ;
			sbSql.append(" k_user a\n") ;
			sbSql.append(" LEFT JOIN ( \n") ;
			sbSql.append("  SELECT userid,GROUP_CONCAT(DISTINCT rolename) AS roles FROM k_userrole a,k_role b WHERE a.rid=b.id GROUP BY userid \n") ;
			sbSql.append(" ) b ON a.id=b.userid \n") ;
			sbSql.append(" LEFT JOIN  ( \n") ; 
			sbSql.append("  SELECT b.id,COUNT(*) AS loginTimes \n") ;
			sbSql.append("  FROM t_log a,k_user b \n") ;
			sbSql.append("  WHERE 1=1 \n") ;
			sbSql.append("  "+ beginDate + endDate +" \n") ;
			sbSql.append("  AND a.cmdname IN ('用户登录','排班系统') \n") ;
			sbSql.append("  AND a.loginid=b.loginid \n") ;
			sbSql.append("  GROUP BY b.id \n") ;
			sbSql.append(" ) c ON a.id = c.id \n") ;
			sbSql.append(" left join k_department d on a.departmentid = d.autoid \n") ;
			sbSql.append(" where a.state=0 "+departmentid+" \n") ;
			sbSql.append(" ${userName} ${loginId} ${rank} \n") ;
			sbSql.append(userids) ;

			DataGridProperty pp = new DataGridProperty() ;
			
			pp.setTableID("staticUserList") ;
			pp.setWhichFieldIsValue(1);
			pp.setColumnWidth("10,7,7,7,7,20") ;
			pp.setCustomerId("") ;
			pp.setPageSize_CH(100) ;
			
			pp.setTrActionProperty(true) ;
			pp.setTrAction("userid=${loginid}") ;
			
			pp.addSqlWhere("userName", " and a.name like '%${userName}%'");
			pp.addSqlWhere("loginId", " and a.loginId like '%${loginId}%'");
			pp.addSqlWhere("rank", " and a.rank like '%${rank}%'");
			
			pp.addColumn("姓名", "name");
			pp.addColumn("登录名", "loginid");
			pp.addColumn("性别", "sex","showCenter");
			pp.addColumn("所属部门", "departname");
			pp.addColumn("薪酬级别", "rank");
			pp.addColumn("权限", "roles");
			pp.addColumn("期间登陆数","loginTimes","showCenter",null,"<div style='cursor:pointer;'>${value}</div>");
			
			pp.setOrderBy_CH("a.id");
			pp.setDirection("asc");
			
			pp.setSQL(sbSql.toString());
			
			
			
			request.getSession().setAttribute(DataGrid.sessionPre+pp.getTableID(),pp);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn) ;
		}
		return modelAndView;
	}
	
	/**
	 * 部门人员情况一览表穿透到项目
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView projectList(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		ModelAndView modelAndView = new ModelAndView(PROJECTLIST) ;
		ASFuntion CHF = new ASFuntion();
		try {
			
			StringBuffer sbSql = new StringBuffer() ;
			
			String departmentid = CHF.showNull(request.getParameter("departmentid")) ;
			
			if(!"".equals(departmentid)){
				departmentid = " and  a.departmentid in (SELECT a.autoid FROM k_department a ,k_department b "
					 + " WHERE a.fullpath LIKE CONCAT(b.fullpath,'%') AND b.autoid = '"+departmentid+"')" ;
			}else{
				departmentid  = "";
			}
			
			sbSql.append("  SELECT a.projectid,c.groupName,a.projectName,e.name AS projectManager, \n") ;
			sbSql.append("  a.shortName,a.auditpara,a.projectcreated,f.stepname AS state, \n") ;
			sbSql.append("  g2.name as fAuditUser,h2.name as signedParner,i.s1 as special \n") ;
			sbSql.append("  FROM z_project a \n") ;
			sbSql.append("  LEFT JOIN k_customer b ON a.customerid = b.departid \n") ;
			sbSql.append("  LEFT JOIN k_group c ON b.groupname = c.groupid \n") ;
			sbSql.append("  LEFT JOIN z_auditpeople d ON a.projectid = d.projectid AND d.Role = '项目负责人'  \n") ;
			sbSql.append("  LEFT JOIN k_user e ON d.userid = e.id \n") ;
			sbSql.append("  LEFT JOIN k_auditconfig f ON a.state = f.id \n") ;
			sbSql.append("  LEFT JOIN z_auditpeople g1 ON a.projectid = g1.projectid AND g1.Role = '部门一审'  \n") ;
			sbSql.append("  LEFT JOIN k_user g2 ON g1.userid = g2.id \n") ;
			sbSql.append("  LEFT JOIN z_auditpeople h1 ON a.projectid = h1.projectid AND h1.Role = '签字合伙人'  \n") ;
			sbSql.append("  LEFT JOIN k_user h2 ON h1.userid = h2.id \n") ;
			sbSql.append("  LEFT JOIN z_projectext i ON a.projectid = i.projectid \n") ;
			sbSql.append("  where 1=1 \n") ;  
			sbSql.append("  "+departmentid+" \n") ;
			sbSql.append(" ${groupName} ${customerName} ${projectName} ${projectManager} \n") ; 

			DataGridProperty pp = new DataGridProperty() ;
			
			pp.setTableID("staticProjectList") ;
			pp.setWhichFieldIsValue(1);
			pp.setColumnWidth("15,20,7,7,7,20,7,7") ;
			pp.setCustomerId("") ;
			
			pp.setPageSize_CH(50) ;
			
			pp.addSqlWhere("groupName", " and c.groupName like '%${groupName}%'");
			pp.addSqlWhere("customerName", " and b.departName like '%${customerName}%'");
			pp.addSqlWhere("projectName", " and b.projectName like '%${projectName}%'");
			pp.addSqlWhere("projectManager", " and e.name like '%${projectManager}%'");
			
			pp.addColumn("集团", "groupName");
			pp.addColumn("项目名称", "projectName");
			pp.addColumn("负责人", "projectManager","showCenter");
			pp.addColumn("部门一审", "fAuditUser","showCenter");
			pp.addColumn("签字合伙人", "signedParner","showCenter");
			pp.addColumn("项目简称", "shortName");
			pp.addColumn("业务类型", "auditpara");
			pp.addColumn("重大/非重大", "special");
			pp.addColumn("建项日期", "projectcreated");
			pp.addColumn("状态", "state");
			
			pp.setOrderBy_CH("a.projectcreated,projectid");
			pp.setDirection("desc,asc");
			
			pp.setSQL(sbSql.toString());
			
			request.getSession().setAttribute(DataGrid.sessionPre+pp.getTableID(),pp);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
		return modelAndView;
	}
	
	
	/**
	 * 部门人员情况一览表穿透到客户
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView customerList(HttpServletRequest request,
			HttpServletResponse response) throws Exception { 

		ModelAndView modelAndView = new ModelAndView(CUSTOMERLIST) ;
		ASFuntion CHF = new ASFuntion();
		try {
			
			StringBuffer sbSql = new StringBuffer() ;
			
			String departmentid = CHF.showNull(request.getParameter("departmentid")) ;
			if(!"".equals(departmentid)){
				departmentid = " and a.departmentid in (SELECT a.autoid FROM k_department a ,k_department b "
					 + " WHERE a.fullpath LIKE CONCAT(b.fullpath,'%') AND b.autoid = '"+departmentid+"')" ;
			}else{
				departmentid  = "";
			}
			
			sbSql.append("  SELECT departid,departName,projectCount,companyProperty \n") ;
			sbSql.append("  FROM k_customer a \n") ;
			sbSql.append("  LEFT JOIN ( \n") ;
			sbSql.append("  	SELECT COUNT(*) AS projectCount,customerid FROM z_project b GROUP BY b.customerid  \n") ;  
			sbSql.append(" ) b ON a.departid = b.customerid \n") ;
			sbSql.append(" WHERE 1=1  \n") ;
			sbSql.append("  "+departmentid+" \n") ;
			sbSql.append(" ${property} ${customerName} \n") ; 
			
			DataGridProperty pp = new DataGridProperty() ;
			
			pp.setTableID("staticCustomerList") ;
			pp.setWhichFieldIsValue(1);
			pp.setColumnWidth("30,7,15") ;
			pp.setCustomerId("") ;
			
			pp.setTrActionProperty(true) ;
			pp.setTrAction("departid=${departid}") ;
			
			pp.setPageSize_CH(50) ;
			
			pp.addSqlWhere("customerName", " and a.departName like '%${customerName}%'");
			pp.addSqlWhere("property", " and a.property like '%${property}%'");
			
			pp.addColumn("客户名称", "departName");
			pp.addColumn("客户项目数", "projectCount");
			pp.addColumn("性质", "companyProperty");
			
			pp.setOrderBy_CH("a.departid");
			pp.setDirection("desc");
			
			pp.setSQL(sbSql.toString());
			
			request.getSession().setAttribute(DataGrid.sessionPre+pp.getTableID(),pp);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
		return modelAndView;
	}
	
	
	public ModelAndView receiveNumProjectList(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		Connection conn = null ;
		ModelAndView modelAndView = new ModelAndView(RECEIVENUMPROJECTLIST) ;
		ASFuntion CHF = new ASFuntion() ;
		
		try {
			conn = new DBConnect().getConnect("") ;
			
			StringBuffer sbSql = new StringBuffer() ;
			
			String departmentid = CHF.showNull(request.getParameter("departmentid")) ;
			
			String beginDate = CHF.showNull(request.getParameter("beginDate")) ;
			String endDate = CHF.showNull(request.getParameter("endDate")) ;
			
			if(!"".equals(beginDate) && !"".equals(endDate)) {
				beginDate = " and b.taskArriveTime between '"+beginDate+"' and '"+endDate+"'" ;
			}
			
			if(!"".equals(departmentid)){
				departmentid = " and  a.departmentid in (SELECT a.autoid FROM k_department a ,k_department b "
					 + " WHERE a.fullpath LIKE CONCAT(b.fullpath,'%') AND b.autoid = '"+departmentid+"')" ;
			}else{
				departmentid  = "";
			}
			
			sbSql.append(" SELECT DISTINCT a.projectid,projectname,e.groupName,a.auditpara,c.departname,b.taskArriveTime \n") ;
			sbSql.append(" FROM z_project a \n") ;
			sbSql.append(" INNER JOIN z_auditstep b ON a.projectid = b.projectid \n") ;
			sbSql.append(" AND b.property > '' AND taskArriveTime > '' \n") ;
			sbSql.append(beginDate) ;
			sbSql.append(departmentid) ;
			sbSql.append(" left join k_department c on a.departmentid = c.autoid \n") ;
			sbSql.append(" left join k_customer d on a.customerid = d.departid \n") ;
			sbSql.append(" left join k_group e on d.groupName = e.groupId \n") ;
			  
			
			DataGridProperty pp = new DataGridProperty() ;
			pp.setTableID("receiveNumProjectList") ;
			pp.setWhichFieldIsValue(1);
			pp.setColumnWidth("20,15,10,10,10") ;
			pp.setCustomerId("") ;
			
			pp.addColumn("项目名称", "projectname");
			pp.addColumn("所属集团","groupName");
			pp.addColumn("业务类型","auditpara");
			pp.addColumn("所属部门","departname");
			pp.addColumn("领号时间","taskArriveTime");
			
			pp.setOrderBy_CH("taskArriveTime");
			pp.setDirection("desc");
			
			pp.setSQL(sbSql.toString());
			
			request.getSession().setAttribute(DataGrid.sessionPre+pp.getTableID(),pp);
		}catch(Exception e) {
			e.printStackTrace() ;
		}finally{
			DbUtil.close(conn) ;
		}
		return modelAndView ;
	}
	
	/**
	 * 部门人员情况一览表穿透到签发项目数
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView issueProjectList(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		ModelAndView modelAndView = new ModelAndView(ISSUEPROJECTLIST) ;
		ASFuntion CHF = new ASFuntion();
		try {
			
			StringBuffer sbSql = new StringBuffer() ;
			
			String departmentid = CHF.showNull(request.getParameter("departmentid")) ;
			
			if(!"".equals(departmentid)){
				departmentid = " and  a.departmentid in (SELECT a.autoid FROM k_department a ,k_department b "
					 + " WHERE a.fullpath LIKE CONCAT(b.fullpath,'%') AND b.autoid = '"+departmentid+"')" ;
			}else{
				departmentid  = "";
			}
			
			String beginDate = CHF.showNull(request.getParameter("beginDate")) ;
			String endDate = CHF.showNull(request.getParameter("endDate")) ;
			
			if(!"".equals(beginDate) && !"".equals(endDate)) {
				beginDate = " and h1.appointdate between '"+beginDate+"' and '"+endDate+"'" ;
			}
			
			sbSql.append("  SELECT a.projectid,c.groupName,a.projectName,e.name AS projectManager, \n") ;
			sbSql.append("  a.shortName,a.auditpara,a.projectcreated,f.stepname AS state, \n") ;
			sbSql.append("  g2.name as fAuditUser,h2.name as signedParner,i.s1 as special \n") ;
			sbSql.append("  FROM z_project a \n") ;
			sbSql.append("  LEFT JOIN k_customer b ON a.customerid = b.departid \n") ;
			sbSql.append("  LEFT JOIN k_group c ON b.groupname = c.groupid \n") ;
			sbSql.append("  LEFT JOIN z_auditpeople d ON a.projectid = d.projectid AND d.Role = '项目负责人'  \n") ;
			sbSql.append("  LEFT JOIN k_user e ON d.userid = e.id \n") ;
			sbSql.append("  LEFT JOIN k_auditconfig f ON a.state = f.id \n") ;
			sbSql.append("  LEFT JOIN z_auditpeople g1 ON a.projectid = g1.projectid AND g1.Role = '部门一审'  \n") ;
			sbSql.append("  LEFT JOIN k_user g2 ON g1.userid = g2.id \n") ;
			sbSql.append("  INNER JOIN z_auditpeople h1 ON a.projectid = h1.projectid AND h1.Role = '签字合伙人'  \n") ;
			sbSql.append("  "+beginDate+"  \n") ;
			sbSql.append("  LEFT JOIN k_user h2 ON h1.userid = h2.id \n") ;
			sbSql.append("  LEFT JOIN z_projectext i ON a.projectid = i.projectid \n") ;
			sbSql.append("  where 1=1 \n") ;  
			sbSql.append("  "+departmentid+" \n") ;
			sbSql.append(" ${groupName} ${customerName} ${projectName} ${projectManager} \n") ; 

			DataGridProperty pp = new DataGridProperty() ;
			
			pp.setTableID("issueProjectList") ;
			pp.setWhichFieldIsValue(1);
			pp.setColumnWidth("15,20,7,7,7,20,7,7") ;
			pp.setCustomerId("") ;
			
			pp.setPageSize_CH(50) ;
			
			pp.addSqlWhere("groupName", " and c.groupName like '%${groupName}%'");
			pp.addSqlWhere("customerName", " and b.departName like '%${customerName}%'");
			pp.addSqlWhere("projectName", " and b.projectName like '%${projectName}%'");
			pp.addSqlWhere("projectManager", " and e.name like '%${projectManager}%'");
			
			pp.addColumn("集团", "groupName");
			pp.addColumn("项目名称", "projectName");
			pp.addColumn("负责人", "projectManager","showCenter");
			pp.addColumn("部门一审", "fAuditUser","showCenter");
			pp.addColumn("签字合伙人", "signedParner","showCenter");
			pp.addColumn("项目简称", "shortName");
			pp.addColumn("业务类型", "auditpara");
			pp.addColumn("重大/非重大", "special");
			pp.addColumn("建项日期", "projectcreated");
			pp.addColumn("状态", "state");
			
			pp.setOrderBy_CH("a.projectcreated,projectid");
			pp.setDirection("desc,asc");
			
			pp.setSQL(sbSql.toString());
			
			request.getSession().setAttribute(DataGrid.sessionPre+pp.getTableID(),pp);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
		return modelAndView;
	}
	
	
	/*
	 * 人员树，可以根据部门id查找
	 */
	public void getUserTree(HttpServletRequest request, HttpServletResponse response)throws Exception {
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		
		ASFuntion asf = new ASFuntion();
		
		String autoid = asf.showNull(request.getParameter("autoid")); //部门编号
		String keywords = asf.showNull(request.getParameter("keywords")); //关键字
		String ids = asf.showNull(request.getParameter("ids"));  //人员ID
		String roleId = asf.showNull(request.getParameter("roleId"));  //角色ID
		
		Connection conn  = null ;
		try {
			UserSession userSession = (UserSession)request.getSession().getAttribute("userSession") ;
			conn=new DBConnect().getConnect("") ;
			DepartmentService ds=new DepartmentService(conn);
			List<Map> list = null;
			if(!"".equals(autoid)){
				if("onLineUser".equals(autoid)){ //在线用户
					list = this.getOnLine(userSession);
				}else if("commonlyUsed".equals(autoid)){ //常用联系人
					 list = ds.getListMapUser(autoid,userSession.getUserId());
				}else{
					list=ds.getUserById(autoid);
				}
			}else if(!"".equals(keywords)){  
				list=ds.getUserByKeywords(keywords);
			}else if(!"".equals(ids)){ 
				if (ids.substring(ids.length()-1, ids.length()).equals(",")) {
					ids = ids.substring(0, ids.length()-1);
				}
				if(!"".equals(ids)){
					list=ds.getUserByIds(ids);
				}
			}else if(!"".equals(roleId)){
				list = ds.getUserByRoleId(roleId);
			}
			String json = "{}";
			if(list != null){
				json = JSONArray.fromObject(list).toString();
			}
			response.getWriter().write(json);
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(conn);
		}
	}
	
	
	/**
	 * 获取在线人员列表
	 * @param userSession
	 * @return
	 */
	public List<Map>  getOnLine(UserSession userSession){
		//无条件刷新用户数
		try {
			OnlineListListener.refresh();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		List<Map> list=null;
		List userList = OnlineListListener.getList();

		try {
			String userId = "";
			String allUserId = "";
			ASFuntion asf = new ASFuntion();

			if(userList != null && !userList.isEmpty()) {
				for(int i=0; i < userList.size(); i++){
					userSession = (UserSession)userList.get(i);

					if(userSession == null) {
						continue;
					}

					userId = asf.showNull(userSession.getUserId());

					if(allUserId.indexOf(userId) == -1){
						allUserId += userId + ",";
					}
					Map map=new HashMap();
					map.put("id", userId);
					map.put("name", userSession.getUserName());
					map.put("departmentId", userSession.getUserAuditDepartmentId());
					map.put("departName", userSession.getUserAuditDepartmentName());
					
					if(list==null){
						list=new ArrayList<Map>();
					}
					
					list.add(map);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	/**
	 * 异步加载 角色树
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView getRoleTree(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		response.setContentType("text/html;charset=utf-8");
		
		ASFuntion af = new ASFuntion();
		
		String roleId = af.showNull(request.getParameter("id"));
		Connection conn = null;
		
		PreparedStatement ps = null;
		ResultSet rs = null;

		List treeList = new ArrayList() ;
		try {
			
			conn = new DBConnect().getConnect("");
		
			List roleList = new EnterpriseQualificationService(conn).getRoleList();

			boolean check = false;
			if("0".equals(roleId)){
				StringBuffer sql = new StringBuffer();
				sql.append("SELECT id,rolename FROM k_role ORDER BY id " );
				
				ps = conn.prepareStatement(sql.toString()) ;
				rs = ps.executeQuery() ;
				while(rs.next()) {
					Map map = new HashMap();
					map.put("text", rs.getString("rolename"));
					map.put("id", rs.getString("id"));
					map.put("leaf", true);
					treeList.add(map) ;	

				}
				
			}			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
			DbUtil.close(ps);
			DbUtil.close(rs);
		}
		String jsonStr = JSONArray.fromObject(treeList).toString() ;
		response.getWriter().write(jsonStr) ;
		return null;
	}
	
}

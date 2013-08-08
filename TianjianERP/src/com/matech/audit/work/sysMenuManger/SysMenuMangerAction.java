package com.matech.audit.work.sysMenuManger;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONArray;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.matech.audit.pub.datagrid.DataGrid;
import com.matech.audit.pub.datagrid.DataGridProperty;
import com.matech.audit.service.popedom.PopedomService;
import com.matech.audit.service.sysMenuManger.sysMenuMangerService;
import com.matech.audit.service.sysMenuManger.model.MenuVO;
import com.matech.audit.service.sysMenuManger.model.SysMenuMangerVO;
import com.matech.audit.service.user.UserService;
import com.matech.audit.service.userpopedom.UserPopedomService;
import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.db.DBConnect;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;
import com.matech.framework.pub.util.Debug;
import com.matech.framework.service.print.PrintSetup;
import com.matech.framework.service.sysmenu.SysMenuCustomer;
import com.matech.framework.servlet.extmenu.Menu;

public class SysMenuMangerAction extends MultiActionController {
	
	protected enum Jsp{
		tabView;
		
		public String getPath(){
			return MessageFormat.format("sysMenuManger/{0}.jsp", this.name());
		}
	}
	private final String _strList = "sysMenuManger/List.jsp";
	private final String _strSuccess = "sysMenuManger.do";
	private final String _strAddaddEdit = "sysMenuManger/AddandEdit.jsp";
	private final String _strPopdem = "department/departmentUpdatePopdem.jsp";

	public ModelAndView list(HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		//HashMap mapResult = new HashMap();
		ASFuntion CHF = new ASFuntion();
		String subSearchID = CHF.showNull(req.getParameter("menu_id"));
		String subSearchName = CHF.showNull(req.getParameter("name"));
		String sql = " select ID,menu_id,parentid,depth,ctype,name,act,helpact,target,rolename,a.power from ( "
					+ " select a.ID,menu_id,parentid,depth,ctype,name,act,helpact,target,group_concat(b.rolename) as rolename,a.power "				
					+ " from s_sysmenu a left join k_role b "
					+ " on concat(',',a.isvalidate,',') like concat('%,',b.id,',%') "
					+ " group by a.ID) a  "
					+ " where 1=1 ";
		DataGridProperty pp = new DataGridProperty();
		//String Str = (String)req.getSession().getAttribute("SysMenu_ppm");

		pp.setTableID("sysMenuManger");
		//基本设置
		//pp.setDatabaseDepartID("100900");
		pp.setPageSize_CH(50);
		pp.setWhichFieldIsValue(0);

		pp.setPrintEnable(true);
		pp.setPrintVerTical(false);

		pp.setPrintTitle("菜单列表");
		pp.setPrintColumnWidth("11,9,7,6,20,56,72,15");

		//sql设置

		if (!subSearchID.equals(""))
			sql = sql + "and menu_id like '%" + subSearchID + "%'";
		if (!subSearchName.equals(""))
			sql = sql + "and name like '%" + subSearchName + "%'";

		System.out.println(sql);
		//pp.setSQL("select ID,menu_id,parentid,depth,ctype,name,act,target,0 from s_sysmenu where 1=1 and menu_id in ("+Str+")");
		pp.setSQL(sql);
		pp.setDirection_CH("ID,menu_id,parentid,depth,ctype,name,act,target");
		//pp.setDirection("desc");
		//pp.addSqlWhere("summary", "where keyvalue like '%${name}%'");

		pp.setInputType("radio");
		pp.addColumn("菜单编号", "menu_id");
		pp.addColumn("父菜单编号", "parentid");
		pp.addColumn("深度", "depth");
		pp.addColumn("类型", "ctype");
		pp.addColumn("名称", "name");
		pp.addColumn("行为", "act");
		pp.addColumn("权限ID", "power");
		pp.addColumn("帮助行为", "helpact");
		pp.addColumn("目标", "target");
		pp.addColumn("二次登录角色", "rolename");


		//pp.addColumn("其它", "url");

		//		pp.setTableHead("菜单ID,菜单父ID,深度,类型,名称,行为,帮助行为,目标");
		pp.setWhichFieldIsValue(1);

		req.getSession()
				.setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);

		return new ModelAndView(_strList);
	}

	public ModelAndView del(HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		ASFuntion CHF = new ASFuntion();
		//DataGridProperty pp=(DataGridProperty)req.getSession().getAttribute(DataGrid.sessionPre+"sysMenuManger");
		//req.getSession().setAttribute(DataGrid.sessionPre+pp.getTableID(),pp);
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String dogVersion = "";
		try {
			conn = new DBConnect().getConnect();
			//  RowSetDynaClass rsdc=null;
			String act = req.getParameter("act");
			SysMenuMangerVO sysMenuMangerVO = new SysMenuMangerVO();

			String autoId = CHF.showNull(req.getParameter("chooseValue"));
			if (!autoId.equals("")) {
				sysMenuMangerVO.setID(Integer.parseInt(autoId));
			} else {
				sysMenuMangerVO.setID(0);
			}

			// departmentVO.setPopedom(req.getParameter("popdem"));

			//这里是你的处理代码，以及调用SERVICE的代码

			String string = "";
			Map map = new HashMap();

			sysMenuMangerService menuMangerService = new sysMenuMangerService(
					conn);
			Vector vector = menuMangerService.getParent();
			map.put("vector", vector);
			//UserDefService userDefService=new UserDefService(conn);
			if (act.equals("del")) {
				menuMangerService.delAMenu(sysMenuMangerVO.getID());
				res.sendRedirect(_strSuccess);
				return null;
			} else if (act.equals("update")) {

				//String string =glossaryService.getAMenuDetail(CHF.showNull(req.getParameter("chooseValue")));
				//ArrayList arrayList = userDefService.getAMenuDetail(req.getParameter("chooseValue"));

				string = menuMangerService.getAMenuDetail(sysMenuMangerVO
						.getID());
				
				
				map.put("string", string);

				try {
					String sql = "select group_concat(menuversion)as dogversions from k_menuversion where menuid=? and menuversion not like '%副狗%'";
					ps = conn.prepareStatement(sql);
					ps.setString(1, autoId);

					rs = ps.executeQuery();

					while (rs.next()) {
						dogVersion = rs.getString(1);
					}

				} catch (Exception e) {
					rs.close();
					ps.close();
				}

				req.setAttribute("dogVersion", dogVersion);

				return new ModelAndView(_strAddaddEdit, map);
				//return new ModelAndView(_strAddaddEdit,"hashMap",hashMap);
			} else if (act.equals("add")) {

				try {
					String sql = "select group_concat(distinct `name`)as dogversions from k_dic where ctype='dogversion'";
					ps = conn.prepareStatement(sql);
					rs = ps.executeQuery();

					while (rs.next()) {
						dogVersion = rs.getString(1);
					}

				} catch (Exception e) {
					rs.close();
					ps.close();
				}

				req.setAttribute("dogVersion", dogVersion);

				return new ModelAndView(_strAddaddEdit, map);
			} else if (act.equals("popdemUpdate")) {
				return new ModelAndView(_strPopdem);
			} else {

			}
			/**
			 * 这里演示如何返回国际化信息给JSP
			 */
			//tip = "系统维护中!";
			//     tip = "serverState3";
			//      tip = UTILSysProperty.context.getMessage(tip, null,
			//                                            new MatechLocale(req).getLocale());
			/**
			 * 这里演示如何操作session
			 */
			//       req.getSession().removeAttribute("user");
			//      req.getSession().removeAttribute("username");

			/**
			 * 这里演示如何给最终的ROWSET
			 */
			//         DbUtil db=new DbUtil(conn);
			//         rsdc=db.getRowSet("select '1' as tt");
		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
		} finally {
			DbUtil.close(conn);
		}

		//返回结果,这个给页面的提示信息
		//    mapResult.put("serverinfo",tip);
		//    mapResult.put("result",rsdc);

		//返回结果，这个是给页面的数据库游标

		return null;
	}

	public ModelAndView addAndEdit(HttpServletRequest req,
			HttpServletResponse res) throws Exception {

		Connection conn = null;

		try {
			conn = new DBConnect().getConnect();

			String dogVersion = req.getParameter("dogversion");

			sysMenuMangerService mangerService = new sysMenuMangerService(conn);

			mangerService.AddOrModifyAMenu(req.getParameter("submitStr"), req
					.getParameter("adored"));

			ASFuntion asf = new ASFuntion();
			String menuid = asf.getXMLData(req.getParameter("submitStr"),
					"menu_id");
			String id = mangerService.getIdByMenuId(menuid);

			mangerService.setDogversions(id, dogVersion);

			req.getSession().setAttribute("SysMenu_ppm",
					new SysMenuCustomer(conn).SysMenuPpm());

			res.sendRedirect(_strSuccess);
			return null;
		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
		} finally {
			DbUtil.close(conn);
		}
		return new ModelAndView();
	}

	public ModelAndView isExist(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);

		request.setCharacterEncoding("utf-8");
		PrintWriter out = response.getWriter();
		ASFuntion CHF = new ASFuntion();

		//菜单编号
		String menuid = CHF.showNull(request.getParameter("menuid"));

		Connection conn = null;
		Statement stmt = null;
		java.sql.ResultSet rs = null;
		String sql = "";
		int count = 0;

		try {
			conn = new DBConnect().getConnect();
			stmt = conn.createStatement();
			sql = "select count(*) from s_sysmenu a, s_sysmenu b where a.id="
					+ menuid + " and a.menu_id=b.parentid";
			rs = stmt.executeQuery(sql);
			rs.next();
			count = rs.getInt(1);
			if (count > 0) {
				out.print("exist");
			} else {
				out.print("noExist");
			}
		} catch (Exception e) {
			e.printStackTrace();
			out.print("exist");
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (stmt != null)
					stmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	/**
	 * 设置打印属性
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
			//String temp = com.matech.framework.pub.sys.UTILSysProperty.SysProperty.getProperty("clientDog");
			conn = new DBConnect().getConnect();
			String tableid = request.getParameter("tableid");

			DataGridProperty pp = (DataGridProperty) request.getSession()
					.getAttribute(DataGrid.sessionPre + tableid);

			PrintSetup printSetup = new PrintSetup(conn);

			printSetup.setStrTitles(new String[] { "菜单列表" });

			printSetup.setStrQuerySqls(new String[] { pp.getFinishSQL() });
			printSetup
					.setStrChineseTitles(new String[] { "编号`菜单ID`菜单父ID`深度`类型`名称`行为`帮助行为`目标" });
			printSetup.setCharColumn(new String[] { "1`2`3`4`5`6`7`8" });

			printSetup.setIColumnWidths(new int[] { 7, 9, 8, 7, 6, 20, 50, 50,
					15 });

			String filename = printSetup.getExcelFile();

			//vpage strPrintTitleRows
			mapResult.put("refresh", "");

			mapResult.put("saveasfilename", "菜单列表");
			mapResult.put("vpage", "true");
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

	/**
	 * 高级查找
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView search(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		return new ModelAndView("sysMenuManger/search5Grid.jsp");

	}
	
	public ModelAndView recordMenuState(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		Connection conn = null;
		ASFuntion CHF = new ASFuntion();
		
		HttpSession session = request.getSession();
		
		UserSession userSession = (UserSession)session.getAttribute("userSession");
		
		String userId = userSession.getUserId();
		
		String menuId = CHF.showNull(request.getParameter("menuId"));
		
		if(!"".equals(menuId)) {
			menuId = menuId.substring(1);
		}
		
		String state = CHF.showNull(request.getParameter("state"));
		
		try {
			
			conn = new DBConnect().getConnect();
			sysMenuMangerService smms = new sysMenuMangerService(conn);
			smms.recordMenuState(userId, menuId, state);
			
			System.out.println(userId + "==" + menuId + "==" + state);
			
		} catch (Exception e) {
			Debug.print(Debug.iError, "修改菜单状态！", e);
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}
		return null;
	}
	
	
	public ModelAndView tabView(HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		ModelAndView mv=new ModelAndView(Jsp.tabView.getPath());
		String menuid=req.getParameter("menuid");
		Connection conn=new DBConnect().getConnect();
		DbUtil dbUtil=new DbUtil(conn);
		MenuVO menu=dbUtil.load(MenuVO.class, menuid);
		List<Menu> menus=getChildLeafs(menu.getMenu_id(), req, conn);
		req.setAttribute("menus", menus);
		return mv;
	}
	
	
public List<Menu> getChildLeafs(String menuid,HttpServletRequest requset,Connection conn){
		
	    List<Menu> menus=new ArrayList<Menu>();
		JSONArray jsonArr = null ;
		ArrayList treeNodeList = new ArrayList() ;
		
		Statement stmt = null;
		ResultSet rs = null;
		
		Statement stmt2 = null;
		ResultSet rs2 = null;
		
		try {
			
			  conn.createStatement();
              UserSession userSession=(UserSession)requset.getSession().getAttribute("userSession");
              ASFuntion ASF = new ASFuntion() ;

              
              String ppm = userSession.getUserPopedom();
              String userId = userSession.getUserId();

              String centerId = requset.getParameter("centerId") ;
              
              String centerName = "" ;
              if(!"".equals(centerId)) {
            	  int cid ;
            	  try {
            		  cid = Integer.parseInt(centerId) ;
            	  }catch(Exception e) {
            		  cid = 0 ;
            	  }
            
	              switch(cid) {
	              	case 1:
	            	  centerName = "审计作业中心" ;
	            	  break ;
	              	case 2:
		            	  centerName = "项目管理中心" ;
		            	  break ;
	              	case 3:
		            	  centerName = "质量管理中心" ;
		            	  break ;
	              	case 4:
		            	  centerName = "客户管理中心" ;
		            	  break ;
	              	case 6:
		            	  centerName = "档案管理中心" ;
		            	  break ; 
	              	case 7:
		            	  centerName = "erp中心" ;
		            	  break ;
	              	default :
		            	  centerName = "审计作业中心" ;
		            	  break ;
	              
	              }
              }else { 
            	  centerName = "审计作业中心" ;
              }
              
              String sqlWhere = "" ;
              if(!"".equals(centerName)) {
            	  sqlWhere = " where menuversion like '%" + centerName + "%'" ;
              }
              

              String sql = "";
              if (ppm.equals("all")) {
					sql = "select distinct a.id,a.menu_id,a.name,depth,act,ActiveX_method,group_concat(distinct c.dogid) as dogid from (select * from s_sysmenu where parentID='" + menuid
							+ "' and ctype='01') a "
							+ " left join k_menuversion b on a.id = b.menuid "
							+ " left join k_dogpopedom c on c.menuid like concat('%.',a.id,'.%') "
							//+sqlWhere 
							+ " group by a.id order by menu_id ";
              } else {
					ppm = "'" + new ASFuntion().replaceStr(ppm, ".", "','")
							+ "'";
					sql = "select distinct a.id,a.menu_id,a.name,depth,act,ActiveX_method,group_concat(distinct c.dogid) as dogid from ( select * from s_sysmenu where parentID='" + menuid
							+ "' and ctype='01' and id in (" + ppm
							+ ")) a"
							+ " left join k_menuversion b on a.id = b.menuid "
							+ " left join k_dogpopedom c on c.menuid like concat('%.',a.id,'.%') "
							//+sqlWhere 
							+ " group by a.id order by menu_id ";
              }
				
			System.out.println("##"+sql);
              stmt = conn.createStatement();
              stmt2 = conn.createStatement();
              rs = stmt.executeQuery(sql);
              
              
              String act = "error_page.jsp";
              String parentID = "";
              String str_temp = "";
              String curmenuid="";
              
              /*
               * @todo
               * 
              boolean isValidate2 = "允许".equals(UTILSysProperty.SysProperty
						.getProperty("是否允许二次登录验证"));
              String memory = UTILSysProperty.SysProperty
						.getProperty("是否记忆系统菜单");
				*/
              
              while (rs.next()) {
            	  
            	  Menu menu=new Menu();
            	  
            	  curmenuid=rs.getString("id");
            	  menu.setId(curmenuid); 
            	  menu.setText(rs.getString("name"));
            	  menu.setActiveXMethod(rs.getString("ActiveX_method"));
            	  menu.setDogid(rs.getString("dogid"));
            	  
            	  if (rs.getInt("depth") != 1) {
            		  
            		  //System.out.println("aaa:"+rs.getString("name"));
            		  
            		  //如果是叶子
            		  menu.setLeaf(true);
            		  
            		  str_temp = ASF.showNull(rs.getString("act"));
            		  
            		  if (!str_temp.equals("")) {
	  						act = str_temp;
	  					} else {
	  						act = "error_page.jsp";
	  					}
            		  
            		  if (act.indexOf("?") >= 0) {
							act = act + "&menuid="+curmenuid;
						} else {
							act = act + "?menuid="+curmenuid;
						}
            		  
            		  menu.setHref(act);
            		  
            	  }
            	  //treeNodeList.add(menu);
            	  menus.add(menu);
              }//while
              
	          //jsonArr = JSONArray.fromObject(treeNodeList) ;
	          //System.out.println("#####"+jsonArr.toString());
	          //return jsonArr.toString();
		} catch (Exception e) {
			e.printStackTrace();
			
		} finally {
			DbUtil.close(stmt);
			DbUtil.close(stmt2);
			DbUtil.close(rs);
			DbUtil.close(rs2);
		}
		return menus;
	}
	


	//自定义菜单 
	private final String custom = "sysMenuManger/custom.jsp";
	public ModelAndView custom(HttpServletRequest request,HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView(custom); 
		Connection conn=null;
		ASFuntion CHF = new ASFuntion();
		try {
			conn= new DBConnect().getConnect();
			UserSession userSession=(UserSession)request.getSession().getAttribute("userSession");
			String id = userSession.getUserId();
			String loginid = userSession.getUserLoginId();
			String name = userSession.getUserName();
			
			String SysPpm = CHF.showNull(userSession.getUserPopedom()); //用户菜单
			if("".equals(SysPpm)){
				SysPpm = "-1";
			}else{
				SysPpm = CHF.replaceStr(SysPpm, ".", "','");
              	SysPpm = SysPpm.substring(2,SysPpm.length()-2);
			}
			
			DbUtil db = new DbUtil(conn);
			PopedomService pps = new PopedomService(conn,SysPpm);
			String opt = CHF.showNull(request.getParameter("opt"));
			
			if("save".equals(opt)){
				String stAll = CHF.showNull(request.getParameter("stAll"));
				String stAllName = CHF.showNull(request.getParameter("stAllName"));
				String userid = CHF.showNull(request.getParameter("userid"));
				if(!"".equals(stAll) ){
					db.del("k_usersysmenu", "userid", userid); //删除
					String [] menus = stAll.split(",");
					String [] names = stAllName.split(",");
					for (int i = 0; i < menus.length; i++) {
						if(!"".equals(CHF.showNull(menus[i])) && !"-1".equals(CHF.showNull(menus[i])) ){
							Map map = new HashMap();
							map.put("userid", userid);
							map.put("sysmenu", menus[i]);
							map.put("menuname", names[i]);
							map.put("orderid", i);
							
							db.add("k_usersysmenu", "autoid", map); //新增
						}
					}
					
				}
			}
			
			List menuList = db.getList("k_usersysmenu", "userid", id);
			
			String sTable = pps.getSubTree("00", "","");
			
			modelAndView.addObject("name", name);
			modelAndView.addObject("loginid", loginid);
			modelAndView.addObject("userid", id);
			modelAndView.addObject("sTable", sTable);
			modelAndView.addObject("menuList", menuList);
			
		} catch (Exception e) {
			Debug.print(Debug.iError, "查询个人项目项目权限失败！", e);
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}
		
		
		return modelAndView;
	}	





}

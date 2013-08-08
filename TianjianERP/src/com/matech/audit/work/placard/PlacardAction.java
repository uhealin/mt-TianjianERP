package com.matech.audit.work.placard;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;	
import javax.servlet.http.HttpSession;

import net.sf.json.JSONArray;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.matech.audit.pub.datagrid.DataGrid;
import com.matech.audit.pub.datagrid.DataGridProperty;
import com.matech.audit.service.department.DepartmentService;
import com.matech.audit.service.enterpriseQualification.EnterpriseQualificationService;
import com.matech.audit.service.placard.PlacardService;
import com.matech.audit.service.placard.model.TreeNode;
import com.matech.audit.service.userpopedom.UserPopedomService;
import com.matech.audit.work.cadet.CadetAction;
import com.matech.framework.listener.OnlineListListener;
import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.db.DBConnect;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;
import com.matech.framework.service.print.PrintSetup;

public class PlacardAction extends MultiActionController{
	
	
	private static final String placardList = "placard/newList.jsp";
	
	private static final String placardAdd ="placard/AddPlacard.jsp";
	
	public ModelAndView print(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ASFuntion CHF=new ASFuntion();
		HashMap mapResult = new HashMap();
		HttpSession session = request.getSession();
		UserSession us = (UserSession) session.getAttribute("userSession");		
		String placardTitle = CHF.showNull(request.getParameter("placardTitle"));
		String placardSender = CHF.showNull(request.getParameter("placardSender"));
		String placardTime = CHF.showNull(request.getParameter("placardTime"));
		
		Connection conn = null;

		try {
			String sql = "";
			
			sql = "select a.caption,a.AddresserTime,  \n" 
				+" replace(replace(replace(replace(replace(a.Matter,'<P>',''),'</P>',''),'<br>',''),'<br />','\n'),'&nbsp;','') as Matter,  \n" 
				+"a.ctype,b.name from k_placard a,k_user b   \n" 
				+" where Addressee='"+ us.getUserId()+"'  \n" 
				+" and a.Addresser = b.id  \n" ;
					
			if(!"".equals(placardTitle)){
				sql += " and caption like '%" + placardTitle +"%'"; 
			}
			
			if(!"".equals(placardSender)){
				sql += " and name like '%" + placardSender +"%'"; 
			}
			
			if(!"".equals(placardTime)){
				sql += " and AddresserTime like'" + placardTime +"%'"; 
			}
				
			sql += " order by AddresserTime desc";
			
			System.out.println("sk:公告查询sql：\n"+sql+"\n");
			
			conn = new DBConnect().getConnect();
			
			PrintSetup printSetup = new PrintSetup(conn);

			printSetup.setStrTitles(new String[] { "公告查询" });

			printSetup.setStrQuerySqls(new String[] { sql });
			printSetup.setStrChineseTitles(new String[] { "公告标题`发布时间`公告内容`公告类型`发信人" });
			printSetup.setCharColumn(new String[] { "1`2`3`4`5" });

			printSetup.setIColumnWidths(new int[] { 20, 20, 40, 20, 20 });

			String filename = printSetup.getExcelFile();

			//vpage strPrintTitleRows
			
			
			mapResult.put("refresh", "");

			mapResult.put("saveasfilename", "公告查询");
			mapResult.put("vpage", "false");
			mapResult.put("strPrintTitleRows", "$2:$4");
			mapResult.put("filename", filename);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}
		
		return new ModelAndView("/Excel/tempdata/PrintandSave.jsp",mapResult);
	}
	
	
	public void getUserJsonTree(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
//		ASFuntion CHF = new ASFuntion();
//		Connection conn = null;
//		PreparedStatement ps = null;
//		ResultSet rs = null;
//		ResultSet rs2 = null ;
//		try {
//			conn=new DBConnect().getConnect() ;
//			response.setContentType("text/html;charset=utf-8") ;
//			PrintWriter out = response.getWriter();
//			
//			UserSession userSession = (UserSession)request.getSession().getAttribute(("userSession"));
//			String userId = userSession.getUserId() ;
//			
//			//求出当前在线的人员
//			List list = new ArrayList();
//			Iterator it = OnlineListListener.getList().iterator();
//			while(it.hasNext()){
//				String id = ((UserSession)it.next()).getUserId();
//				list.add(id);
//			}
//			
//			String sql = "select * from (select autoid,departname from k_department order by abs(property), autoid) a  union select -1,'无部门人员' from k_department ";
//			ps = conn.prepareStatement(sql);
//			rs = ps.executeQuery();
//			List treeList = new ArrayList() ;
//			while(rs.next()) {
//				String departId = rs.getString(1) ;
//				TreeNode treeNode = new TreeNode() ;
//				treeNode.setId(departId) ;
//				treeNode.setText(rs.getString(2)) ;
//				treeNode.setLeaf(false) ;
//				
//				sql = "select id,name ,DepartName from k_user a,k_department b where a.departmentid=b.autoid and a.state=0 and a.departmentid='" + departId + "' order by b.DepartName " ;
//				if("-1".equals(departId)) {
//					//无部门人员
//					sql = "select id,name from k_user a where a.state=0 and (a.departmentid = '' or departmentid is null) " ;
//				}
//				ps = conn.prepareStatement(sql) ;
//				rs2 = ps.executeQuery() ;
//				List childList = new ArrayList() ;
//				while(rs2.next()) {
//					TreeNode childTreeNode = new TreeNode() ;
//					childTreeNode.setId(rs2.getString(1)) ;
//					
//					if(list.contains(rs2.getString(1))) {
//						//在线用户
//						childTreeNode.setText(rs2.getString(2)+"&nbsp;<font color='#FF0000'>(在线)</font>") ;
//					}else {
//						childTreeNode.setText(rs2.getString(2)+"&nbsp;<font color='#0000FF'>(离线)</font>") ;
//					}
//					String addresser=request.getParameter("addresser");
//					childTreeNode.setLeaf(true) ;					
//					if(rs2.getString(1).equals(addresser)){
//						childTreeNode.setChecked(true) ;
//					}else{
//						childTreeNode.setChecked(false) ;
//					}
//					childList.add(childTreeNode) ;
//				}
//				
//				treeNode.setChildren(childList) ;
//				treeNode.setChecked(false) ;
//				
//				treeList.add(treeNode) ;
//			}
//			
//			String jsonStr = JSONArray.fromObject(treeList).toString() ;
//			out.write(jsonStr) ;
//		}catch(Exception e){
//			e.printStackTrace();
//			throw e;
//		}finally{
//			DbUtil.close(rs);
//			DbUtil.close(ps);
//			DbUtil.close(conn);
//		}
		response.setContentType("text/html;charset=utf-8");
		Connection conn = null;
		try {
			ASFuntion CHF=new ASFuntion();
			
			String checked = CHF.showNull(request.getParameter("checked"));
			
			String departid = CHF.showNull(request.getParameter("departid"));//单位/区域/部门
			String areaid = CHF.showNull(request.getParameter("areaid"));//单位/区域/部门
			String departname = CHF.showNull(request.getParameter("departname"));	
			String isSubject = CHF.showNull(request.getParameter("isSubject"));	//判断是哪个节目
			
			
			String userpopedom = CHF.showNull(request.getParameter("userpopedom"));	 //用于判断部门是否要加上选择框
			
			String loginid = CHF.showNull(request.getParameter("loginid")); //人员loginid
			String menuid = CHF.showNull(request.getParameter("omenuid")); //菜单ID	
			String joinUser = CHF.showNull(request.getParameter("joinUser")); //菜单ID	
			
			String addUser = CHF.showNull(request.getParameter("addUser")); //用于追加一个人员树
			
			System.out.println(addUser+"|"+checked+"|"+departid+"|"+areaid+"|"+departname+"|"+isSubject);
			
			conn = new DBConnect().getConnect();
			DepartmentService ds = new DepartmentService(conn);
			UserPopedomService up = new UserPopedomService(conn);
			String departments = up.getLoginIdPopedom(loginid, menuid);
			EnterpriseQualificationService eqs = new EnterpriseQualificationService(conn);
			
			ds.setAddUser(addUser); //追加人员树 addUser = "addUser"; 
			
			List list = null;
			if("".equals(isSubject) || "undefined".equals(isSubject)) {
				list = ds.getOrgan(checked);	
				if(list == null){
					//区域表无值，直接展开部门表
					if("userpopedom".equals(userpopedom)){
						checked = "false";
						ds.setUserpopedom(departments);
					}
					departid = "555555";
					list = ds.getDepartment(departid, areaid, checked);
					list=depFielter(list);
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
						list=depFielter(list);
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
					list=depFielter(list);
					if("true".equals(addUser)){
						List list1 = eqs.getUser(departid, checked,","+joinUser);
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
	
	/**
	 * 内部短息
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView placarList(HttpServletRequest request,HttpServletResponse response){
		
		ModelAndView modelAndView = new ModelAndView(placardList);
		DataGridProperty waitReadPP = new DataGridProperty(); //等待阅读
		DataGridProperty alreadyReadPP = new DataGridProperty(); //已阅读
		DataGridProperty alreadySendPP = new DataGridProperty(); //已发送
		DataGridProperty allNotePP = new DataGridProperty(); //全部短信查询
		try {
			
			UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");
			
			//等待阅读
			String waitReadSql="SELECT a.id,b.`Name` AS addresser,d.`departname` AS addresserDepartName, a.`Matter`," 
								+"a.`AddresserTime`,a.`Caption`,a.`myimage` \n"
								+",c.`Name` AS addressee \n"
								+"FROM k_placard a \n"
								+"LEFT JOIN k_user b ON a.`Addresser` =b.`id`  \n"
								+"LEFT JOIN k_user c ON a.`Addressee` =c.`id`  \n"
								+"LEFT JOIN k_department d ON b.`departmentid` = d.`autoid` \n"
							+"where a.isRead =0 and addressee ='"+userSession.getUserId()+"' ${waitReadCaption} ${waitReadDate} ${waitReadDepartName} ${waitReadAddressee} ";
			
			waitReadPP.setTableID("waitReadList");
			waitReadPP.setCustomerId("");
			waitReadPP.setWhichFieldIsValue(1);
			waitReadPP.setInputType("checkbox");
			waitReadPP.setOrderBy_CH("AddresserTime");
			waitReadPP.setDirection("desc");
			System.out.println("userId="+userSession.getUserId());
			waitReadPP.setColumnWidth("10,10,15,15");
			waitReadPP.setSQL(waitReadSql);
			
			waitReadPP.setPrintEnable(true);	//关闭dg打印

			waitReadPP.setPrintTitle("未查看信息列表");

			waitReadPP.setTrActionProperty(true); // 设置 table可双击
			waitReadPP.setTrAction("style=\"cursor:hand;\" seadId=\"${id}\"  ");
			
			waitReadPP.addColumn("发信人", "addresser");
			waitReadPP.addColumn("发信人所在部门", "addresserDepartName");
			waitReadPP.addColumn("发信时间", "AddresserTime");
			waitReadPP.addColumn("标题", "Caption");
			//waitReadPP.addColumn("内容", "Matter");
			
			waitReadPP.addSqlWhere("waitReadAddressee"," and b.name like '%${waitReadAddressee}%' ");
			waitReadPP.addSqlWhere("waitReadDepartName"," and d.departname like '%${waitReadDepartName}%' ");
			waitReadPP.addSqlWhere("waitReadDate"," and a.AddresserTime like '%${waitReadDate}%' ");
			waitReadPP.addSqlWhere("waitReadCaption"," and a.Caption like '%${waitReadCaption}%' ");
		

			

			//已阅读
			String alreadyReadSql="SELECT a.id,b.`Name` AS addresser,d.`departname` AS addresserDepartName, " 
							+"a.`AddresserTime`,a.`Caption`,a.`Matter` \n"
							+",c.`Name` AS addressee \n"
							+"FROM k_placard a \n"
							+"LEFT JOIN k_user b ON a.`Addresser` =b.`id`  \n"
							+"LEFT JOIN k_user c ON a.`Addressee` =c.`id`  \n"
							+"LEFT JOIN k_department d ON b.`departmentid` = d.`autoid` \n"
							+"where a.isRead =1 and addressee ='"+userSession.getUserId()+"' ${alreadyReadAddressee} ${alreadyReadDepartName} ${alreadyReadDate} ${alreadyReadCaption} ";

			alreadyReadPP.setTableID("alreadyReadList");
			alreadyReadPP.setCustomerId("");
			alreadyReadPP.setWhichFieldIsValue(1);
			//alreadyReadPP.setInputType("radio");
			alreadyReadPP.setOrderBy_CH("AddresserTime");
			alreadyReadPP.setDirection("desc");
			System.out.println("userId="+userSession.getUserId());
			alreadyReadPP.setColumnWidth("10,10,15,15");
			alreadyReadPP.setSQL(alreadyReadSql);
			
			alreadyReadPP.setPrintEnable(true);	//关闭dg打印
			alreadyReadPP.setPrintTitle("已查看信息列表");
			
			alreadyReadPP.setTrActionProperty(true); // 设置 table可双击
			alreadyReadPP.setTrAction("style=\"cursor:hand;\" seadId=\"${id}\"  ");
			
			alreadyReadPP.addColumn("发信人", "addresser");
			alreadyReadPP.addColumn("发信人所在部门", "addresserDepartName");
			alreadyReadPP.addColumn("发信时间", "AddresserTime");
			alreadyReadPP.addColumn("标题", "Caption");
			//alreadyReadPP.addColumn("内容", "Matter");
			
			alreadyReadPP.addSqlWhere("alreadyReadAddressee"," and b.name like '%${alreadyReadAddressee}%' ");
			alreadyReadPP.addSqlWhere("alreadyReadDepartName"," and d.departname like '%${alreadyReadDepartName}%' ");
			alreadyReadPP.addSqlWhere("alreadyReadDate"," and a.AddresserTime like '%${alreadyReadDate}%' ");
			alreadyReadPP.addSqlWhere("alreadyReadCaption"," and a.Caption like '%${alreadyReadCaption}%' ");
			
			
			//已发送
			String alreadySendSql="SELECT a.id,a.`AddresserTime`,a.`Caption`,a.`Matter` \n"
								+",b.`Name` AS addressee,d.`departname` \n"
								+"FROM k_placard a \n"
								+"inner JOIN k_user b ON a.`Addressee` =b.`id`  \n"
								+"LEFT JOIN k_department d ON b.`departmentid` = d.`autoid` \n"
								+" where 1=1 and a.Addresser = "+userSession.getUserId()+" ${addressee} ${AddresserTime} ${caption}";

			alreadySendPP.setTableID("alreadySendList");
			alreadySendPP.setCustomerId("");
			alreadySendPP.setWhichFieldIsValue(1);
			//alreadySendPP.setInputType("radio");
			alreadySendPP.setOrderBy_CH("AddresserTime");
			alreadySendPP.setDirection("desc");
			System.out.println("userId="+userSession.getUserId());
			alreadySendPP.setColumnWidth("10,10,15,15");
			alreadySendPP.setSQL(alreadySendSql);
			
			alreadySendPP.setPrintEnable(true);	//关闭dg打印
			alreadySendPP.setPrintTitle("已发送信息列表");
			
			alreadySendPP.setTrActionProperty(true); // 设置 table可双击
			alreadySendPP.setTrAction("style=\"cursor:hand;\" seadId=\"${id}\"  ");
			
			alreadySendPP.addColumn("收信人", "addressee");
			alreadySendPP.addColumn("收信人所在部门", "departname");
			alreadySendPP.addColumn("发信时间", "AddresserTime");
			alreadySendPP.addColumn("标题", "Caption");
			//alreadySendPP.addColumn("内容", "Matter");
			
			alreadySendPP.addSqlWhere("addressee"," and b.name like '%${addressee}%' ");
			alreadySendPP.addSqlWhere("AddresserTime"," and a.AddresserTime like '%${sendDate}%' ");
			alreadySendPP.addSqlWhere("caption"," and a.caption like '%${caption}%' ");
		
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			request.getSession().setAttribute(DataGrid.sessionPre + waitReadPP.getTableID(), waitReadPP);
			request.getSession().setAttribute(DataGrid.sessionPre + alreadyReadPP.getTableID(), alreadyReadPP);
			request.getSession().setAttribute(DataGrid.sessionPre + alreadySendPP.getTableID(), alreadySendPP);
		}
		return modelAndView;
	}

	/**
	 * 标记已阅读
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView signRead(HttpServletRequest request,HttpServletResponse response){
		String result = "false";
		Connection conn = null;
		PrintWriter out = null;
		try {
		    out = response.getWriter();
		    response.setCharacterEncoding("utf-8");
		    request.setCharacterEncoding("utf-8");
			ASFuntion asf = new ASFuntion();
			String ids = asf.showNull(request.getParameter("ids"));
			UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");
			conn = new DBConnect().getConnect();
			
			PlacardService placardService = new PlacardService(conn);
			
			if(!"".equals(ids)){ //批量标记
				String[] id = ids.split(",");
				
				for (int i = 0; i < id.length; i++) {
					placardService.updateIsReadById(id[i]);
				}
				
				result = "true";
			}else{//全部标记
				placardService.updateIsRead(userSession.getUserId());
			}
			out.write(result);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		return null;
	}
	
	/**
	 * 删除短信
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView del(HttpServletRequest request,HttpServletResponse response){
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
		
			conn = new DBConnect().getConnect();
			
			PlacardService placardService = new PlacardService(conn);
			
			if(!"".equals(ids)){ //批量删除
				String[] id = ids.split(",");
				
				for (int i = 0; i < id.length; i++) {
					placardService.delAPlacard(id[i]);
				}
				
				result = "true";
			}else{//全部删除
				placardService.delAllAPlacardByUser(userSession.getUserId());
				result = "true";
			}
			
			out.write(result);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		return null;
	}
	
	/**
	 * 查找发件人发给自己的最新的1条信息
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView findOnePlacard(HttpServletRequest request,HttpServletResponse response){
		ModelAndView modelAndView = new ModelAndView(placardAdd);
		Connection conn = null;
		UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
		String addressee=userSession.getUserId();
		try {
			String sql="select addresser"
						+" from k_placard"
						+" where addressee='"+addressee+"'"
						+" and isread=0"
						+" order by AddresserTime desc"
						+" limit 0,1 ";
			conn=new DBConnect().getConnect();
			String addresser= new DbUtil(conn).queryForString(sql);
			modelAndView.addObject("addresser", addresser);
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(conn);
		}
		return modelAndView;
	}
	
	/**
	 * 把没阅读的update为已阅读
	 * @param request
	 * @param response
	 * @return
	 */
	public void updateIsRead(HttpServletRequest request,
			HttpServletResponse response) throws Exception{
		Connection conn = null;
		UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
		String addressee=userSession.getUserId();		
		try {
			String sql="update k_placard set isread=1 where addressee='"+addressee+"'";
			conn=new DBConnect().getConnect();
			int result = new DbUtil(conn).executeUpdate(sql);
			
			response.getWriter().write(result+"");
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(conn);
		}
	}
	
	//过渡部门
    protected List depFielter(List deps){
    	if(deps==null)return deps;
		List tempList=new ArrayList();
		for(Object dep: deps) {
			Map map=(Map)dep;
			String depid=map.get("departid").toString();
			if("1269".equals(depid)||"1267".equals(depid)||"127506".equals(depid)||CadetAction.CADET_DEPARTMENID.equals(depid))continue;
			tempList.add(map);
		}
	    return	 tempList;
    }
	
//	/**
//	 * 验证是不是邮件
//	 * @param request
//	 * @param response
//	 * @return
//	 */
//	public void isMail(HttpServletRequest request,
//			HttpServletResponse response) throws Exception{
//		Connection conn = null;
//		UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
//		String addressee=userSession.getUserId();
//		try {
//			conn=new DBConnect().getConnect();
//			String SqlFindMailId="select id"
//						+" from k_placard"
//						+" where addressee='"+addressee+"'"
//						+" and isread=0"
//						+" order by AddresserTime desc"
//						+" limit 0,1 ";
//			String mailId=new DbUtil(conn).queryForString(SqlFindMailId);
//			String sql="select left(url,16) from k_placard where id='"+mailId+"'";
//			String urlStr=new DbUtil(conn).queryForString(sql);
//			System.out.println(urlStr+"*********************");
//			response.getWriter().write(urlStr);
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally{
//			DbUtil.close(conn);
//		}
//	}
}

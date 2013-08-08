package com.matech.audit.work.content;

import java.sql.Connection;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.matech.audit.pub.datagrid.DataGrid;
import com.matech.audit.pub.datagrid.DataGridProperty;
import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.attachFileUploadService.AttachService;
import com.matech.audit.service.attachFileUploadService.model.Attach;
import com.matech.audit.service.content.ContentService;
import com.matech.audit.service.placard.PlacardService;
import com.matech.audit.service.placard.model.PlacardTable;
import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.autocode.DELUnid;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;
import com.matech.framework.pub.util.StringUtil;
import com.matech.framework.pub.util.WebUtil;

public class ContentAction extends MultiActionController {

	
	private static final String list = "content/list.jsp";
	private static final String edit = "content/edit.jsp";
	private static final String answer = "content/answer.jsp";
	//咨询树
	public ModelAndView tree(HttpServletRequest request, HttpServletResponse response) throws Exception{
		response.setContentType(WebUtil.CONTENT_TYPE_UTF8);
		Connection conn=null;
		try {
			conn=new DBConnect().getConnect();
			DbUtil db=new DbUtil(conn);
			
			List list = new ArrayList();
			List l = db.getList("k_dic", "ctype", "咨询类型", "property");
			for (int i = 0; i < l.size(); i++) {
				Map m = (Map)l.get(i);
				Map map = new HashMap();
				map.put("isSubject","0");//用于标志：当前节目的类型
				map.put("cls","folder");
				map.put("leaf",true);	
				map.put("id","dic_"+StringUtil.showNull(m.get("value")) +"_"+DELUnid.getNumUnid()) ;
				map.put("classid",StringUtil.showNull(m.get("value")));
				map.put("text",StringUtil.showNull(m.get("name")));
				list.add(map);
			}
			
			String json = "{}";
			if(list != null){
				json = JSONArray.fromObject(list).toString();
			}
			response.getWriter().write(json);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}
		return null;
	}
	
	//问题列表
	public ModelAndView list(HttpServletRequest request, HttpServletResponse response) throws Exception{
		ModelAndView modelAndView = new ModelAndView(list);
		UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
		String userid = userSession.getUserId();
		
		ASFuntion asf = new ASFuntion();
		String op = asf.showNull(request.getParameter("op"));
		String classid = asf.showNull(request.getParameter("classid"));
		
		String strWhere = "";
		if("0".equals(op)){
			strWhere += " and a.EAdvmen = '"+userid+"' ";
		}
		if(!"".equals(classid)){
			strWhere += " and a.ClassID = '"+classid+"' ";
		}
		
		String sql = "select a.*,if(ifnull(isfb,'0')='0','否','是') as fb,if(ifnull(ishf,'0')='0','否','是') as hf,b.name as classname,c.name as username " +
		"	from k_adv_content a,k_dic b,k_user c " +
		"	where b.ctype = '咨询类型' and a.ClassID = b.value  and a.EAdvmen = c.id " +
		strWhere;
		
		DataGridProperty pp = new DataGridProperty();
		
		pp.setColumnWidth("8,8,30");
		
		pp.addColumn("咨询编号", "AdvID");
		pp.addColumn("咨询类型", "classname");
		pp.addColumn("咨询事项", "AdvTitle");
		pp.addColumn("咨询人", "Advmen");
		pp.addColumn("提出时间", "AdvTime");
		//pp.addColumn("是否发布", "fb");
		pp.addColumn("是否回复", "hf");
		
		pp.setTableID("tt_"+DELUnid.getNumUnid());
		pp.setPageSize_CH(50);
		
		pp.setOrderBy_CH("advtime");
		pp.setDirection_CH("desc");
		
		pp.setInputType("radio");
		pp.setWhichFieldIsValue(1);
		
		pp.setCustomerId("") ;
		
		//pp.setTrActionProperty(true) ;
		//pp.setTrAction(" uuid=${uuid} ") ;
		
		pp.setSQL(sql);
		
		request.getSession().setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);
		
		modelAndView.addObject("op", op);
		modelAndView.addObject("classid", classid);
		modelAndView.addObject("tableid", pp.getTableID());
		
		return modelAndView;
	}
	
	//新增、修改
	public ModelAndView edit(HttpServletRequest request, HttpServletResponse response) throws Exception{
		ModelAndView modelAndView = new ModelAndView(edit);
		Connection conn=null;
		try {
			conn=new DBConnect().getConnect();
			DbUtil db=new DbUtil(conn);
			
			ASFuntion asf = new ASFuntion();
			String op = asf.showNull(request.getParameter("op"));
			String classid = asf.showNull(request.getParameter("classid"));
			String autoid = asf.showNull(request.getParameter("autoid"));
			
			String classname = db.queryForString("select name from k_dic where ctype = '咨询类型' and value = ? ", new String[]{classid});
			
			Map content = new HashMap();
			if("".equals(autoid)){
				//新增 
				content.put("classid", classid);
			}else{
				//修改
				content = db.get("k_adv_content", "autoid", autoid);
			}
			
			
			modelAndView.addObject("content", content);
			modelAndView.addObject("op", op);
			modelAndView.addObject("classname", classname);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}	
		return modelAndView;
	}
	
	//删除
	public void del(HttpServletRequest request, HttpServletResponse response) throws Exception{
		response.setContentType(WebUtil.CONTENT_TYPE_UTF8);
		Connection conn=null;
		try {
			conn=new DBConnect().getConnect();
			DbUtil db=new DbUtil(conn);
			
			ASFuntion asf = new ASFuntion();
			String autoid = asf.showNull(request.getParameter("autoid"));
			
			//删除同时要删除回复记录
			String advid = String.valueOf(db.get("k_adv_content", "autoid", autoid, "advid"));
			String attachid = String.valueOf(db.get("k_adv_content", "autoid", autoid, "attachid"));
			
			db.del("k_adv_content", "autoid", autoid);
			db.del("k_adv_content_sub", "advid", advid);
			
			//删除附件
			try {
				AttachService attachService = new AttachService(conn);
				attachService.remove(attachid, attachid);
			} catch (Exception e) {
				
			}
			
			response.getWriter().write("删除成功！");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}	
	}
	
	//回复
	public ModelAndView answer(HttpServletRequest request, HttpServletResponse response) throws Exception{
		ModelAndView modelAndView = new ModelAndView(answer);
		Connection conn=null;
		try {
			conn=new DBConnect().getConnect();
			DbUtil db=new DbUtil(conn);
			
			ASFuntion asf = new ASFuntion();
			String autoid = asf.showNull(request.getParameter("autoid"));
			String op = asf.showNull(request.getParameter("op"));
			String classid = asf.showNull(request.getParameter("classid"));
			String classname = db.queryForString("select name from k_dic where ctype = '咨询类型' and value = ? ", new String[]{classid});
			String advid = String.valueOf(db.get("k_adv_content", "autoid", autoid, "advid"));
			
			Map content = db.get("k_adv_content", "autoid", autoid);
			List subList = db.getList("k_adv_content_sub", "advid", advid,"jsb_time");
			
			modelAndView.addObject("content", content);
			modelAndView.addObject("subList", subList);
			modelAndView.addObject("op", op);
			modelAndView.addObject("classname", classname);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}		
		return modelAndView;
	}
	
	//保存：问题保存、回复保存
	public void save(HttpServletRequest request, HttpServletResponse response) throws Exception{
		Connection conn = null;
		try {
			Map parameters = new HashMap();
			Enumeration enum1 = request.getParameterNames();
			while (enum1.hasMoreElements()) {
				String paramName = (String) enum1.nextElement();
				String [] paramValue = request.getParameterValues(paramName);
				if(paramValue.length == 1 ){
					parameters.put(paramName.toLowerCase(), paramValue[0]);	
				}else{
					parameters.put(paramName.toLowerCase(), paramValue);
				}
			}
			
			conn = new DBConnect().getConnect();
			DbUtil db = new DbUtil(conn);
			ContentService cs = new ContentService(conn);
			
			UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
			
			String op = StringUtil.showNull(parameters.get("op"));
			String classid = StringUtil.showNull(parameters.get("classid"));
			
			String autoid = StringUtil.showNull(parameters.get("autoid"));
			if("".equals(autoid)){
				parameters.put("advid", cs.getAutoCode("CONTENT", "k_adv_content", "advid"));
				parameters.put("advtime", StringUtil.getCurDateTime());
				parameters.put("advmen", userSession.getUserName());
				parameters.put("eadvmen", userSession.getUserId());
				parameters.put("isfb", "0");
				parameters.put("ishf", "0");
				
				db.add("k_adv_content", "autoid", parameters);
				
				//角色与分类名一样
				String classname = db.queryForString("select ext_str1 from k_dic where ctype = '咨询类型' and value = ? ", new String[]{classid});
				try {
					//站内短信（补）
					PlacardService placardService=new PlacardService(conn); 
					PlacardTable placardTable=new PlacardTable(); 
					placardTable.setAddresser(userSession.getUserId());//发起
					placardTable.setAddresserTime(StringUtil.getCurDateTime());
					placardTable.setIsRead(0);
					placardTable.setIsReversion(0);
					placardTable.setIsNotReversion(0);
					placardTable.setCaption(classname + "的咨询问题");
					placardTable.setMatter(StringUtil.showNull(parameters.get("advtitle")));
					
					List l = db.getList("select b.* from k_role a,k_userrole b where a.id = b.rid and rolename = '"+classname+"' ");
					for (int i = 0; i < l.size(); i++) {
						Map m = (Map)l.get(i);
						System.out.println("=====发给"+m.get("userid"));
						placardTable.setAddressee(StringUtil.showNull(m.get("userid"))); //接收的老大UserId
						placardService.AddPlacard(placardTable);	
					}
				} catch (Exception e) {
					
				}
				
			}else{
				db.update("k_adv_content", "autoid", parameters);
			}
			
			response.sendRedirect(request.getContextPath() + "/content.do?op="+op+"&classid="+classid);	

			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}
		
	}
	
	//回复保存
	public void answerSave(HttpServletRequest request, HttpServletResponse response) throws Exception{
		Connection conn = null;
		try {
			Map parameters = new HashMap();
			Enumeration enum1 = request.getParameterNames();
			while (enum1.hasMoreElements()) {
				String paramName = (String) enum1.nextElement();
				String [] paramValue = request.getParameterValues(paramName);
				if(paramValue.length == 1 ){
					parameters.put(paramName.toLowerCase(), paramValue[0]);	
				}else{
					parameters.put(paramName.toLowerCase(), paramValue);
				}
			}
			
			conn = new DBConnect().getConnect();
			DbUtil db = new DbUtil(conn);
			
			UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
			
			String op = StringUtil.showNull(parameters.get("op"));
			String classid = StringUtil.showNull(parameters.get("classid"));
			String advid = StringUtil.showNull(parameters.get("advid"));
			String autoid = StringUtil.showNull(parameters.get("autoid"));
			
			String orderid = StringUtil.showNull(db.queryForString("select max(orderid) from k_adv_content_sub where AdvID=? and classid = ? ",new String[]{advid,classid}));
			
			
			parameters.put("jsb_time",  StringUtil.getCurDateTime());
			parameters.put("jsb_men", userSession.getUserName());
			parameters.put("ejsb_men", userSession.getUserId());
			parameters.put("orderid", ("".equals(orderid) ? 1 : Integer.valueOf(orderid) + 1));
			
			db.add("k_adv_content_sub", "autoid", parameters);
			db.update("k_adv_content", "autoid", autoid, "ishf", "1"); //修改回复
			
			response.sendRedirect(request.getContextPath() + "/content.do?op="+op+"&classid="+classid);	

			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}
		
		
	}
	
}

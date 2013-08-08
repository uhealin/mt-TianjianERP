package com.matech.audit.work.rank;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;

import org.del.JRockey2Opp;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.matech.audit.pub.datagrid.DataGrid;
import com.matech.audit.pub.datagrid.DataGridProperty;
import com.matech.audit.service.newCustomer.model.NewCustomer;
import com.matech.audit.service.rank.RankService;
import com.matech.audit.service.rank.model.Rank;
import com.matech.framework.listener.OnlineListListener;
import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.db.DBConnect;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;

/**
 * @author ymm
 *职级
 */
public class RankAction extends MultiActionController{
	
	private String LIST = "rank/list.jsp";
	private String ADDSKIP = "rank/addAndEdit.jsp";
	private String UPDATESKIP = "rank/addAndEdit.jsp";
	
	
	/**
	 * LIST
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView list(HttpServletRequest request, HttpServletResponse response){
		
		//需要加密狗登录
		/*
		if (JRockey2Opp.getUserLic() <= 0) {
			final String TRY_URL = "/AuditSystem/AS_SYSTEM/error_page.jsp?tip=999";
			try {
				response.sendRedirect(TRY_URL);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}*/
		ModelAndView modelAndView = new ModelAndView(LIST);
		//UserSession userSession = (UserSession) request.getSession().getAttribute("userSession"); 
		DataGridProperty pp = new DataGridProperty(); 
		try {	
			
			
			String ppSql=" SELECT `autoId`,`name`,`ctype`,`sequenceNumber`,`group` as grou,`explain` as explai ,baseSalary,timeSalary,`propenty`" +
						 " FROM `k_rank` a  where 1=1 ${name} ${ctype} ${group}  ${explain}"; 
			
			pp.setTableID("rankList");
			pp.setCustomerId("");
			pp.setWhichFieldIsValue(1);
			pp.setInputType("radio");
			pp.setOrderBy_CH("sequenceNumber");
			pp.setDirection("desc");
			
			pp.setSQL(ppSql);

			pp.setPrintEnable(true);
			pp.setPrintVerTical(false);
			pp.setPrintTitle("晋升审批列表");
			//pp.setPrintVerTical(false);
			//pp.setColumnWidth("10,10,15,8,8,8,20");
			
			//pp.setTrActionProperty(true); // 设置 table可双击
			//pp.setTrAction("style=\"cursor:hand;\" taskId=\"${taskId}\"  ");
			
			pp.addColumn("职级名称", "name");
			pp.addColumn("职级类型", "ctype");
			pp.addColumn("权限号", "sequenceNumber");
			pp.addColumn("基本工资", "baseSalary");
			pp.addColumn("工时工资", "timeSalary");
			pp.addColumn("所属组", "grou");
			pp.addColumn("职级说明", "explai");
			
			
			pp.addSqlWhere("name"," and `name` like '%${name}%' ");
			pp.addSqlWhere("ctype"," and `ctype` like '%${ctype}%' ");
			pp.addSqlWhere("group"," and `group` like '%${group}%' ");
			pp.addSqlWhere("explain"," and `explain` like '%${explain}%' ");
			
 			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{			
			request.getSession().setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);
		}
		
		return modelAndView;
	}

	/**
	 * 判断职级名称是否存在
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView getIfCustomerName(HttpServletRequest request,HttpServletResponse response) throws Exception{
		
		Connection conn = null;
		
		try {
			ASFuntion asf = new ASFuntion() ;
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
			conn = new DBConnect().getConnect();
			PrintWriter out = response.getWriter();
			
			RankService rankService =new RankService(conn);
			
			String name = asf.showNull(request.getParameter("name"));
			String ctype = asf.showNull(request.getParameter("ctype"));
			
			String result  = rankService.getRankName(name, ctype);
			
			out.write(result);
			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		return null;
	}
	
	/**
	 * 新增跳转
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView addSkip (HttpServletRequest request, HttpServletResponse response){
		
		//需要加密狗登录
		/*
		if (JRockey2Opp.getUserLic() <= 0) {
			final String TRY_URL = "/AuditSystem/AS_SYSTEM/error_page.jsp?tip=999";
			try {
				response.sendRedirect(TRY_URL);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
		*/
		return new ModelAndView(ADDSKIP);
	}
	
	/**
	 * 新增
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView add (HttpServletRequest request, HttpServletResponse response){
		
		Connection conn = null;
		try {
			
			conn = new DBConnect().getConnect();
			
			RankService rankService =new RankService(conn);
			 
			String  name = request.getParameter("name"); 
			String  ctype = request.getParameter("ctype"); 
			String  group = request.getParameter("group"); 
			String  baseSalary = request.getParameter("baseSalary"); 
			String  timeSalary = request.getParameter("timeSalary"); 
			String  sequenceNumber = request.getParameter("sequenceNumber"); 
			String  explain = request.getParameter("explain"); 
			String  propenty = request.getParameter("propenty"); 
			
			Rank rank = new Rank();
			rank.setName(name);
			rank.setCtype(ctype);
			rank.setGroup(group);
			rank.setExplain(explain);
			rank.setPropenty(propenty);
			rank.setBaseSalary(baseSalary);
			rank.setTimeSalary(timeSalary);
			rank.setSequenceNumber(sequenceNumber);
			
			rankService.add(rank);
			
			response.sendRedirect(request.getContextPath()+"/rank.do?method=list");
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		return null;
	}
	
	/**
	 * 修改跳转
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView updateSkip (HttpServletRequest request, HttpServletResponse response){
		
		//需要加密狗登录
		/*
		if (JRockey2Opp.getUserLic() <= 0) {
			final String TRY_URL = "/AuditSystem/AS_SYSTEM/error_page.jsp?tip=999";
			try {
				response.sendRedirect(TRY_URL);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
		*/
		ModelAndView modelAndView = new ModelAndView(UPDATESKIP);
		ASFuntion asf = new ASFuntion();
		Connection conn = null;
		try {
			
			conn = new DBConnect().getConnect();
			
			RankService rankService =new RankService(conn);
			String  autoId = asf.showNull(request.getParameter("autoId"));
			 
			Rank rank = rankService.getRank(autoId);
			
			modelAndView.addObject("rank",rank);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		
		return modelAndView;
	}
	
	/**
	 * ajax修改跳转
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView ajaxSkip (HttpServletRequest request, HttpServletResponse response){
		
		ASFuntion asf = new ASFuntion();
		Connection conn = null;
		try {
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
			PrintWriter out = response.getWriter();
			conn = new DBConnect().getConnect();
			
			RankService rankService =new RankService(conn);
			String  autoId = asf.showNull(request.getParameter("autoId"));
			 
			Rank rank = rankService.getRank(autoId);
			
			out.write( JSONArray.fromObject(rank).toString());
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		
		return null;
	}
	
	/**
	 * 修改
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView update (HttpServletRequest request, HttpServletResponse response){
		Connection conn = null;
		try {
			ASFuntion asf = new ASFuntion();
			conn = new DBConnect().getConnect();
			RankService rankService =new RankService(conn);
			String  name = request.getParameter("name"); 
			
			String  ctype = request.getParameter("ctype"); 
			String  autoId = asf.showNull(request.getParameter("autoId")); 
			String  group = request.getParameter("group"); 
			String  baseSalary = request.getParameter("baseSalary"); 
			String  timeSalary = request.getParameter("timeSalary"); 
			String  sequenceNumber = request.getParameter("sequenceNumber"); 
			String  explain = request.getParameter("explain"); 
			String  propenty = request.getParameter("propenty"); 
			if(!"".equals(autoId)){
				Rank rank = new Rank();
				
				rank.setAutoId(autoId);
				rank.setName(name);
				rank.setCtype(ctype);
				rank.setGroup(group);
				rank.setExplain(explain);
				rank.setPropenty(propenty);
				rank.setBaseSalary(baseSalary);
				rank.setTimeSalary(timeSalary);
				rank.setSequenceNumber(sequenceNumber);
				
				rankService.update(rank);
				
			}
			response.sendRedirect(request.getContextPath()+"/rank.do?method=list");
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		return null;
	}
	
	/**
	 * 删除
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView del (HttpServletRequest request, HttpServletResponse response){
		
		ASFuntion asf = new ASFuntion();
		Connection conn = null;
		try {
			
			conn = new DBConnect().getConnect();
			
			RankService rankService =new RankService(conn);
			String  uuid = asf.showNull(request.getParameter("autoId"));
			 
			rankService.del(uuid);
			
			response.sendRedirect(request.getContextPath()+"/rank.do?method=list");
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		
		return null;
	}
	
	public void getRankJsonTree(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		ASFuntion CHF = new ASFuntion();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ResultSet rs2 = null ;
		try {
			conn=new DBConnect().getConnect() ;
			response.setContentType("text/html;charset=utf-8") ;
			PrintWriter out = response.getWriter();
			
			UserSession userSession = (UserSession)request.getSession().getAttribute(("userSession"));
			String userId = userSession.getUserId() ;
			
			//求出当前在线的人员
			List list = new ArrayList();
			Iterator it = OnlineListListener.getList().iterator();
			while(it.hasNext()){
				String id = ((UserSession)it.next()).getUserId();
				list.add(id);
			}
			
			String sql = "SELECT DISTINCT `GROUP` as group1,`GROUP` FROM k_rank  GROUP BY `name` ORDER BY autoId ";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			List treeList = new ArrayList() ;
			while(rs.next()) {
				String departId = rs.getString(1) ;
				Map map = new HashMap();
				map.put("id", rs.getString("group1"));
				map.put("text",rs.getString("GROUP"));
				map.put("leaf", false);
				
				sql = "SELECT autoId,name FROM k_rank WHERE `group` ='" + rs.getString("GROUP") + "' order by sequenceNumber " ;
				ps = conn.prepareStatement(sql) ;
				rs2 = ps.executeQuery() ;
				List childList = new ArrayList() ;
				while(rs2.next()) {
					Map childTreeNode = new HashMap();
					childTreeNode.put("id",rs2.getString("autoId")) ;
					childTreeNode.put("text",rs2.getString("name")) ;
					childTreeNode.put("userName",rs2.getString("name")) ;
					childTreeNode.put("leaf",true) ;
					childList.add(childTreeNode) ;
				}
				map.put("name", departId);
				map.put("children",childList) ;
				treeList.add(map) ;
			}
			
			String jsonStr = JSONArray.fromObject(treeList).toString() ;
			out.write(jsonStr) ;
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}finally{
			DbUtil.close(rs);
			DbUtil.close(ps);
			DbUtil.close(conn);
		}
	}
}

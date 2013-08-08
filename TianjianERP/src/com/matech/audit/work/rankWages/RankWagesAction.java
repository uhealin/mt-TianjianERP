package com.matech.audit.work.rankWages;

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
import com.matech.audit.service.rankWages.*;
import com.matech.audit.service.rank.model.Rank;
import com.matech.audit.service.rankWages.model.RankWages;
import com.matech.framework.listener.OnlineListListener;
import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.db.DBConnect;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;

/**
 * @author ymm
 *职级
 */
public class RankWagesAction extends MultiActionController{
	
	private String LIST = "rankWages/list.jsp";
	private String ADDSKIP = "rankWages/addAndEdit.jsp";
	private String UPDATESKIP = "rankWages/addAndEdit.jsp";
	
	
	/**
	 * LIST
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView list(HttpServletRequest request, HttpServletResponse response){
		/*
		//需要加密狗登录
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
		ModelAndView modelAndView = new ModelAndView(LIST);
		//UserSession userSession = (UserSession) request.getSession().getAttribute("userSession"); 
		DataGridProperty pp = new DataGridProperty(); 
		try {	
			ASFuntion asf = new ASFuntion();
			String rankId = asf.showNull(request.getParameter("rankId"));
			
			String ppSql="SELECT a.`uuid`,a.`interiorId`,b.name as `rankId`,a.`wagesName`,a.`getValue`,a.`updateTache`,a.`orderId`,a.`remark`,a.`propenty`,a.`valueType`,a.groupFlag \n"
						+"FROM `k_rankwages` a \n" 
						+"left join k_rank b on a.rankId =b.autoId \n"
						+" where 1=1 "; 
			if(!"".equals(rankId)){
				
				ppSql +=" and a.rankId='"+rankId+"' \n";
				modelAndView.addObject("rankId",rankId);
			}
			ppSql +="${wagesName} ${rankId} ${getValue}  ${remark}";
			pp.setTableID("rankWagesList");
			pp.setCustomerId("");
			pp.setWhichFieldIsValue(1);
			pp.setInputType("radio");
			pp.setOrderBy_CH("orderId");
			pp.setDirection("asc");
			
			pp.setColumnWidth("13,13,10,13,10");
			pp.setSQL(ppSql);
			
			//pp.setTrActionProperty(true); // 设置 table可双击
			//pp.setTrAction("style=\"cursor:hand;\" taskId=\"${taskId}\"  ");
			
			pp.addColumn("工资项名称", "wagesName");
			pp.addColumn("分组", "groupFlag");
			pp.addColumn("取值类型","valueType");
			pp.addColumn("值", "getValue");
			pp.addColumn("有权修改的环节", "updateTache");
			
			
			pp.addSqlWhere("wagesName"," and a.wagesName like '%${wagesName}%' ");
			pp.addSqlWhere("rankId"," and a.rankId like '%${rankId}%' ");
			pp.addSqlWhere("getValue"," and a.getValue like '%${getValue}%' ");
			pp.addSqlWhere("remark"," and a.remark like '%${remark}%' ");
			
 			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{			
			request.getSession().setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);
		}
		
		return modelAndView;
	}
	 
	/**
	 * 新增跳转
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView addSkip (HttpServletRequest request, HttpServletResponse response){
		/*
		//需要加密狗登录
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
		String rankId = request.getParameter("rankId");
		ModelAndView modelAndView = new ModelAndView(ADDSKIP);
		modelAndView.addObject("rankId",rankId);
		return modelAndView;
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
			
			RankWargesService rankWargesService =new RankWargesService(conn);
			 
			String  wagesName = request.getParameter("wagesName"); 
			String  propenty = request.getParameter("propenty"); 
			String  rankId = request.getParameter("rankId"); 
			String  interiorId = request.getParameter("interiorId"); 
			String  getValue = request.getParameter("getValue"); 
			String  updateTache = request.getParameter("updateTache"); 
			String  orderId = request.getParameter("orderId"); 
			String  remark = request.getParameter("remark"); 
			String  valueType = request.getParameter("valueType");
			String  groupFlag = request.getParameter("groupFlag");
			
			RankWages rankWages = new RankWages();
			String uuid = UUID.randomUUID().toString();
			
			rankWages.setUuid(uuid);
			rankWages.setWagesName(wagesName);
			rankWages.setPropenty(propenty);
			rankWages.setRankId(rankId);
			rankWages.setInteriorId(interiorId);
			rankWages.setGetValue(getValue);
			rankWages.setUpdateTache(updateTache);
			rankWages.setOrderId(orderId);
			rankWages.setRemark(remark);
			rankWages.setValueType(valueType);
			rankWages.setGroupFlag(groupFlag);
			
			rankWargesService.add(rankWages);
			
			response.sendRedirect(request.getContextPath()+"/rankWages.do?method=list&rankId="+rankId);
			
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
		/*
		//需要加密狗登录
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
			
			RankWargesService RankWargesService =new RankWargesService(conn);
			String  uuid = asf.showNull(request.getParameter("uuid"));
			 
			RankWages rankWages = RankWargesService.getRankWages(uuid);
			
			modelAndView.addObject("rankWages",rankWages);
			modelAndView.addObject("uuid",uuid);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		
		return modelAndView;
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
			RankWargesService rankWargesService =new RankWargesService(conn);
			 
			String  uuid = asf.showNull(request.getParameter("uuid")); 
			String  wagesName = request.getParameter("wagesName"); 
			String  propenty = request.getParameter("propenty"); 
			String  rankId = request.getParameter("rankId"); 
			String  interiorId = request.getParameter("interiorId"); 
			String  getValue = request.getParameter("getValue"); 
			String  updateTache = request.getParameter("updateTache"); 
			String  orderId = request.getParameter("orderId"); 
			String  remark = request.getParameter("remark"); 
			String  valueType = request.getParameter("valueType");
			String  groupFlag = request.getParameter("groupFlag");
			
			if(!"".equals(uuid)){
				
				RankWages rankWages = new RankWages();
				rankWages.setUuid(uuid);
				rankWages.setWagesName(wagesName);
				rankWages.setPropenty(propenty);
				rankWages.setRankId(rankId);
				rankWages.setInteriorId(interiorId);
				rankWages.setGetValue(getValue);
				rankWages.setUpdateTache(updateTache);
				rankWages.setOrderId(orderId);
				rankWages.setRemark(remark);
				rankWages.setValueType(valueType);
				rankWages.setGroupFlag(groupFlag);
				
				rankWargesService.update(rankWages);
			}
			
			response.sendRedirect(request.getContextPath()+"/rankWages.do?method=list&rankId="+rankId);
			
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
			
			RankWargesService RankWargesService =new RankWargesService(conn);
			String  uuid = asf.showNull(request.getParameter("uuid"));
			String  rankId = request.getParameter("rankId"); 
			 
			RankWargesService.del(uuid);
			
			response.sendRedirect(request.getContextPath()+"/rankWages.do?method=list&rankId="+rankId);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		
		return null;
	}
	
}

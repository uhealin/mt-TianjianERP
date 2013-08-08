package com.matech.audit.work.leaveType;

import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.matech.audit.pub.datagrid.DataGrid;
import com.matech.audit.pub.datagrid.DataGridProperty;
import com.matech.audit.service.leaveType.model.LeaveType;
import com.matech.audit.service.leaveType.LeaveTypeService;
import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.db.DBConnect;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;

public class LeaveTypeAction extends MultiActionController{
	private static final String LIST = "leaveType/list.jsp";
	private static final String ADDSKIP = "leaveType/AddandEdit.jsp";
	private static final String UPDATESKIP = "leaveType/AddandEdit.jsp";
	
	/**
	 * list
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView list(HttpServletRequest request, HttpServletResponse response){
		
		ModelAndView modelAndView = new ModelAndView(LIST);
		
		DataGridProperty pp = new DataGridProperty(); //待领取
		try {	
			
			
			String sql ="SELECT`autoId`,`name`,`applyLimit`," +
								"`yearDayLimit`,`yearCountLimit`," +
								"`monthDayLimit`,`monthCountLimit`," +
								"`deductMoney`,`minTime`,`memo` "+
						"FROM `k_leavetypeSetUp` where 1=1 ${name} ${applyLimit}";
			
			pp.setTableID("leavTypeList");
			pp.setCustomerId(""); //
			pp.setWhichFieldIsValue(1);
			pp.setInputType("radio");
			pp.setOrderBy_CH("name");
			pp.setDirection("desc");
			pp.setPrintEnable(true);	//关闭dg打印

			pp.setPrintTitle("请假类型列表");

			pp.setColumnWidth("10");
			pp.setSQL(sql);
			
			pp.addColumn("请假名称", "name");
			pp.addColumn("提前申请限制", "applyLimit");
			pp.addColumn("每年天数上限", "yearDayLimit");
			pp.addColumn("每年累计上限", "yearCountLimit");
			pp.addColumn("每月天数上限", "monthDayLimit");
			pp.addColumn("每月累计上限", "monthCountLimit");
			pp.addColumn("扣工资金额", "deductMoney");
			pp.addColumn("最小起扣小时", "minTime");
			pp.addColumn("备注", "memo");
			
			pp.addSqlWhere("name"," and name like '%${name}%' ");
			pp.addSqlWhere("applyLimit"," and applyLimit like '%${applyLimit}%' ");
	

		} catch (Exception e) {
			e.printStackTrace();
		}finally{			
			request.getSession().setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);
		}
		
		return modelAndView;
	}
	
	/**
	 * 判断类型是否存在
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView getName(HttpServletRequest request,HttpServletResponse response) throws Exception{
		
		Connection conn = null;
		
		try {
			ASFuntion asf = new ASFuntion() ;
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
			conn = new DBConnect().getConnect();
			PrintWriter out = response.getWriter();
			
			LeaveTypeService leaveTypeService =new LeaveTypeService(conn);
			
			String name = asf.showNull(request.getParameter("name"));
			String sql = " select name from k_leavetypesetup where name='"+name+"'";
			name = leaveTypeService.getValueBySql(sql);
			
			out.write(name);
			
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
		
		ModelAndView modelAndView = new ModelAndView(ADDSKIP) ;
		
		return modelAndView;
	}
	
	/**
	 * 新增与修改
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView add (HttpServletRequest request, HttpServletResponse response){
		
		ASFuntion asf = new ASFuntion();
		Connection conn = null;
		try {
			
			conn = new DBConnect().getConnect();
			
			LeaveTypeService leaveTypeService = new LeaveTypeService(conn);
			
			String  name= asf.showNull(request.getParameter("name"));
			String  autoId= asf.showNull(request.getParameter("autoId"));
			String  applyLimit= asf.showNull(request.getParameter("applyLimit"));
			String  yearDayLimit= asf.showNull(request.getParameter("yearDayLimit"));
			String  yearCountLimit= asf.showNull(request.getParameter("yearCountLimit"));
			String  monthDayLimit= asf.showNull(request.getParameter("monthDayLimit"));
			String  monthCountLimit= asf.showNull(request.getParameter("monthCountLimit"));
			String  deductMoney= asf.showNull(request.getParameter("deductMoney"));
			String  minTime= asf.showNull(request.getParameter("minTime"));
			String memo = asf.showNull(request.getParameter("memo"));
			
			LeaveType leaveType = new LeaveType();
			
			leaveType.setName(name);
			leaveType.setApplyLimit(applyLimit);
			leaveType.setYearDayLimit(yearDayLimit);
			leaveType.setYearCountLimit(yearCountLimit);
			leaveType.setMonthDayLimit(monthDayLimit);
			leaveType.setMonthCountLimit(monthCountLimit);
			leaveType.setDeductMoney(deductMoney);
			leaveType.setMinTime(minTime);
			leaveType.setMemo(memo);
			
			if("".equals(autoId)){
				
				leaveTypeService.add(leaveType);
			}else{
				leaveType.setAutoId(autoId);
				leaveTypeService.update(leaveType);

			}
			
			response.sendRedirect(request.getContextPath()+"/leaveType.do?method=list");
			
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
		
		ModelAndView modelAndView = new ModelAndView(UPDATESKIP);
		ASFuntion asf = new ASFuntion();
		Connection conn = null;
		try {
			
			conn = new DBConnect().getConnect();
			
			LeaveTypeService leaveTypeService = new LeaveTypeService(conn);
			String  autoId = asf.showNull(request.getParameter("autoId"));
			 
			LeaveType leaveType = leaveTypeService.getLeaveType(autoId);
			
			modelAndView.addObject("leaveType",leaveType);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		
		return modelAndView;
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
			
			LeaveTypeService leaveTypeService = new LeaveTypeService(conn);
			String  autoId = asf.showNull(request.getParameter("autoId"));
			 
			leaveTypeService.delete(autoId);
			
			response.sendRedirect(request.getContextPath()+"/leaveType.do?method=list");
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		
		return null;
	}
	
}

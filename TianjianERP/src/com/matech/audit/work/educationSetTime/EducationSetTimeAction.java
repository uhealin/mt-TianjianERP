package com.matech.audit.work.educationSetTime;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.matech.audit.pub.datagrid.DataGrid;
import com.matech.audit.pub.datagrid.DataGridProperty;
import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.educationSetTime.EducationSetTimeService;
import com.matech.audit.service.educationSetTime.model.EducationSetTime;
import com.matech.audit.service.rank.RankService;
import com.matech.audit.service.user.UserService;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;

public class EducationSetTimeAction extends MultiActionController {
	private final String _list="educationSetTime/list.jsp";
	private final String _addAndEdit="educationSetTime/addAndEdit.jsp";
	/*
	 * 获取所有人员的必修学时数据
	 */
	public ModelAndView list(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView(_list);
		String sql="select e.*,r.name as rankName from k_educationsettime e "
					+" inner join k_rank r on e.rankid=r.autoid"
					+" where 1=1 ${userName} ${rankId}";
		DataGridProperty pp =new DataGridProperty();
		pp.setSQL(sql);
		pp.setCustomerId("");
		pp.setPageSize_CH(50);
		pp.setOrderBy_CH("id");
		pp.setTableID("educationSetTime");
    	pp.setWhichFieldIsValue(1);
		pp.setInputType("radio");
		
		pp.setPrintEnable(true);
	    pp.setPrintVerTical(false);
	    pp.setPrintTitle("必修学时管理");
		
//		pp.addColumn("姓名", "userName");
		pp.addColumn("职级", "rankName");
		pp.addColumn("一年学时年份", "yearone");
		pp.addColumn("二年学时年份", "yeartwo");
		pp.addColumn("一年必修学时","timeone"); 
		pp.addColumn("二年必修学时", "timetwo");
//		pp.addColumn("一年必修学时状态","stateone");
//		pp.addColumn("二年必修学时状态","statetwo");
		pp.setColumnWidth("10,10,10,10,10") ;
		
		pp.addSqlWhere("userName", "and u.name like '%${userName}%'");
		pp.addSqlWhere("rankId", "and r.autoid=${rankId}");

		request.getSession().setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);
		return modelAndView;
	}
	/*
	 * 增加或修改
	 */
	public void saveOrUpdate(HttpServletRequest request, HttpServletResponse response) throws Exception{
		Connection conn = null;
		try {
			conn = new DBConnect().getConnect("");
			ASFuntion CHF = new ASFuntion();
			String act=CHF.showNull(request.getParameter("act"));
//			String userId=CHF.showNull(request.getParameter("userId"));
			String rankId=CHF.showNull(request.getParameter("rankId"));
			String yearOne=CHF.showNull(request.getParameter("selectYearOne"));
			String yearTwo=CHF.showNull(request.getParameter("selectYearTwo"));
			String timeOne=CHF.showNull(request.getParameter("timeOne"));
			String timeTwo=CHF.showNull(request.getParameter("timeTwo"));
			EducationSetTime es=new EducationSetTime();
//			RankService rs=new RankService(conn);
//			String rankName=rs.getRankById(rankId).getName();
//			UserService us=new UserService(conn);
//			List<String> list=new ArrayList<String>();
//			list=us.getUserByRank(rankName);
			EducationSetTimeService est=new EducationSetTimeService(conn);
//			String groupNum=(Integer.valueOf(est.getMaxGroupNum())+1)+"";
//			for(int i=0;i<list.size();i++){
//				es.setUserId(userId);
//				es.setUserId(list.get(i));
				es.setRankId(rankId);
				es.setYearOne(yearOne);
				es.setYearTwo(yearTwo);
				es.setTimeOne(timeOne);
				es.setTimeTwo(timeTwo);
//				es.setGroupNum(groupNum);
				if(act.equals("add")){
					est.insertOne(es);
				}
//			}
			if(act.equals("edit")){
				String id=CHF.showNull(request.getParameter("id"));
				es.setId(id);
				est.updateOne(es);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(conn);
		}
		response.sendRedirect(request.getContextPath() + "/educationSetTime.do?method=list");
	}
	/*
	 * 删除
	 */
	public ModelAndView del(HttpServletRequest request, HttpServletResponse response) throws Exception{
		Connection conn = null;
		ModelAndView modelAndView = new ModelAndView(_list);
		try {
			conn = new DBConnect().getConnect("");
			ASFuntion CHF = new ASFuntion();
			String id=CHF.showNull(request.getParameter("id"));
			EducationSetTimeService est=new EducationSetTimeService(conn);
			est.del(id);
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(conn);
		}
		return modelAndView;
	}
	/*
	 * 跳转到增加或修改页面
	 */
	public ModelAndView add(HttpServletRequest request, HttpServletResponse response) throws Exception{
		ModelAndView modelAndView = new ModelAndView(_addAndEdit);
		Connection conn = null;
		ASFuntion CHF = new ASFuntion();
		String act=CHF.showNull(request.getParameter("act"));
		String id=CHF.showNull(request.getParameter("id"));
		try {
			if(act.equals("edit")){
				conn = new DBConnect().getConnect("");
				EducationSetTime educationSetTime=new EducationSetTime();
				EducationSetTimeService ests=new EducationSetTimeService(conn);
				educationSetTime=ests.findById(id);
				modelAndView.addObject("educationSetTime", educationSetTime);
				modelAndView.addObject("id", id);
			}
			modelAndView.addObject("act", act);
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(conn);
		}
		return modelAndView;
	}
}

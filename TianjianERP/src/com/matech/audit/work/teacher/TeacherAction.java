package com.matech.audit.work.teacher;

import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.matech.audit.pub.datagrid.DataGrid;
import com.matech.audit.pub.datagrid.DataGridProperty;
import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.teacher.TeacherService;
import com.matech.audit.service.teacher.model.Teacher;
import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;
/**
 * 培训师资管理业务控制器
 * @author LiuHaijun
 *
 */
public class TeacherAction extends MultiActionController {
	private final String _addAndEdit="/teacher/addAndEdit.jsp";
	private final String _list="/teacher/List.jsp";
	private final String _historyList="/teacher/historyList.jsp";
	
	/*
	 * 读出所有老师记录
	 */
	public ModelAndView list(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView(_list);
		
		DataGridProperty pp =new DataGridProperty();
		
		pp.setTableID("teacher");
		pp.setCustomerId("");
		pp.setPageSize_CH(50);
		
		pp.setPrintEnable(true);
	    pp.setPrintVerTical(false);
	    pp.setPrintTitle("培训教师库维护");
	    
	    String sql = "select t.*,u.name as userName from k_teacher t left join k_user u on t.ugg=u.id "
	    			+"where 1=1 ${name} ${teacherNum} ${title} ${position} ${professional} ${sex} ${company}";
	    
	    pp.setSQL(sql);
	    pp.setOrderBy_CH("teacherNum") ;
	    pp.setInputType("radio");
	    pp.setTrActionProperty(true);
		pp.setTrAction(" teacherId='${id}' style='cursor:hand;'");
	    pp.setWhichFieldIsValue(1);
	    pp.addColumn("教师编号", "teacherNum");
	    pp.addColumn("姓名", "name");
	    pp.addColumn("授课专业", "professional");
	    pp.addColumn("工作单位", "company");
	    pp.addColumn("职称", "title");
	    pp.addColumn("职位", "position");
	    pp.addColumn("性别", "sex");
	    pp.addColumn("发表人", "userName");
	    pp.addColumn("讲师归属", "state");
	    
	    pp.addSqlWhere("name", "and t.name='${name}'");
	    pp.addSqlWhere("teacherNum", "and teacherNum='${teacherNum}'");
	    pp.addSqlWhere("title", "and title='${title}'");
	    pp.addSqlWhere("position", "and position='${position}'");
	    pp.addSqlWhere("professional", "and professional='${professional}'");
	    pp.addSqlWhere("sex", "and t.sex='${sex}'");
	    pp.addSqlWhere("company", "and company='${company}'");
	    
	    pp.setPrintEnable(true);
	    pp.setPrintTitle("教师列表");
	    request.getSession().setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);
		return modelAndView;
	}
	
	/*
	 * 跳转到增加或者编辑老师页面
	 */
	public ModelAndView add(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView(_addAndEdit);
		Connection conn = null;
		try {
			conn = new DBConnect().getConnect("");
			ASFuntion CHF=new ASFuntion();
			String act=CHF.showNull(request.getParameter("act"));
			modelAndView.addObject("act", act);
			if(act.equals("edit")){
				int id=Integer.valueOf(CHF.showNull(request.getParameter("id")));
				Teacher teacher=new Teacher();
				TeacherService ts=new TeacherService(conn);
				teacher=ts.findById(id);
				modelAndView.addObject("teacher", teacher);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		System.out.println("-----------------------TeacherAction类的add()方法----------------------");
		return modelAndView;
	}
	
	/*
	 * 保存老师资料
	 */
	public void saveOrUpdate(HttpServletRequest request, HttpServletResponse response) throws Exception{
		Connection conn = null;
		UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
		String userId=userSession.getUserId();
		try {
			conn = new DBConnect().getConnect("");
			ASFuntion CHF=new ASFuntion();
			String teacherNum=CHF.showNull(request.getParameter("teacherNum"));
			String name=CHF.showNull(request.getParameter("name"));
			String title=CHF.showNull(request.getParameter("title"));
			String position=CHF.showNull(request.getParameter("position"));
			String sex=CHF.showNull(request.getParameter("sex"));
			String ugg=CHF.showNull(request.getParameter("ugg"));
			String professional=CHF.showNull(request.getParameter("professional"));
			String company=CHF.showNull(request.getParameter("company"));
			String act=CHF.showNull(request.getParameter("act"));
			String state =CHF.showNull(request.getParameter("state"));
			Teacher teacher=new Teacher();
			if(request.getParameter("id")!="" && request.getParameter("id")!=null){
				int id=Integer.valueOf(request.getParameter("id"));
				teacher.setId(id);
			}
			teacher.setName(name);
			teacher.setCompany(company);
			teacher.setPosition(position);
			teacher.setProfessional(professional);
			teacher.setSex(sex);
			teacher.setTeacherNum(teacherNum);
			teacher.setTitle(title);
			teacher.setUgg(userId);
			teacher.setState(state);
			TeacherService ts=new TeacherService(conn);
			if(act.equals("add")){
				ts.add(teacher);
			}
			if(act.equals("edit")){
				ts.update(teacher);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		response.sendRedirect(request.getContextPath() + "/"+ "teacher.do");	
		//response.sendRedirect(request.getContextPath() + "/teacher.do?method=list");
	}
	
	/*
	 * 删除选中的数据
	 */
	public ModelAndView del(HttpServletRequest request, HttpServletResponse response) throws Exception{
		ModelAndView modelAndView = new ModelAndView(_list);
		Connection conn = null;
		try {
			conn = new DBConnect().getConnect("");
			ASFuntion CHF=new ASFuntion();
			int id=Integer.valueOf(CHF.showNull(request.getParameter("id")));
			TeacherService ts=new TeacherService(conn);
			ts.del(id);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		return modelAndView;
	}
	
	/*
	 * 查训老师所教过的培训班
	 */
	public ModelAndView getHistory(HttpServletRequest request, HttpServletResponse response) throws Exception{
		ModelAndView modelAndView = new ModelAndView(_historyList);
		DataGridProperty pp =new DataGridProperty();
		int id=Integer.valueOf(request.getParameter("id"));
		
		pp.setTableID("teacherHistory");
		pp.setCustomerId("");
		pp.setPageSize_CH(50);
		
		pp.setPrintEnable(true);
	    pp.setPrintVerTical(false);
	    pp.setPrintTitle("培训历史");
	    
	    String sql="select e.id as educationId,trainendtime,e.name,v.averageScore " +
	    		    "from k_teacher t " +
	    		    "inner join k_education e on t.id=e.teacherid "
	    			+" left join (" 
	    			+" SELECT *,ROUND(AVG(votevalue),2) as averageScore FROM k_evaluate GROUP BY educationId "
	    			+") v on v.educationid=e.id "
	    			+" where t.id="+id
	    			+" group by e.id";
	    pp.setSQL(sql);
	    pp.setOrderBy_CH("trainendtime") ;
	    pp.setDirection_CH("desc");
	    pp.setWhichFieldIsValue(1);
	    
	    pp.setTrActionProperty(true); // 设置 table可双击
		pp.setTrAction("style=\"cursor:hand;\" educationId=\"${educationId}\"  ");
	    
	    pp.addColumn("培训班结束日期", "trainendtime");
	    pp.addColumn("培训班名称", "name");
	    pp.addColumn("评价得分", "averageScore",null,null," <a href=\"javascript:void(0)\" onclick='openPage(${educationId})'></img>${value}</a>");
	    request.getSession().setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);
		return modelAndView;
	}
}

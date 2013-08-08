package com.matech.audit.work.user;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jbpm.api.ProcessInstance;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.matech.audit.pub.datagrid.DataGrid;
import com.matech.audit.pub.datagrid.DataGridProperty;
import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.employment.subset.SubsetFamilyTempVO;
import com.matech.audit.service.employment.subset.SubsetFamilyVO;

import com.matech.audit.service.hr.model.ResumeVO;
import com.matech.audit.service.process.JbpmServicce;
import com.matech.audit.service.process.JbpmTemplate;
import com.matech.audit.service.process.ProcessService;
import com.matech.audit.service.process.model.ProcessApply;
import com.matech.audit.service.process.model.ProcessDeploy;
import com.matech.audit.service.process.model.ProcessForm;
import com.matech.audit.service.user.model.ResumMassage;
import com.matech.audit.service.user.ResumMassageService;
import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;
import com.matech.framework.pub.util.StringUtil;
import com.matech.framework.pub.util.WebUtil;

public class ResumAction extends MultiActionController {
	private final String list = "user/resum/list.jsp";
	private final String edit = "user/view.jsp";
	private final String goLook = "user/resum/look.jsp";
	

	public void view (HttpServletRequest request, HttpServletResponse response)   throws Exception{
		
		response.sendRedirect(request.getContextPath()+"/user/view.jsp");
	}
	public ModelAndView list (HttpServletRequest request, HttpServletResponse response)   throws Exception{
		ModelAndView modelAndView = new ModelAndView(list);
		DataGridProperty pp = new DataGridProperty();
		String sql = "select autoid,title,type,content,updateTime from k_resummessage";
		pp.setOrderBy_CH("updateTime");
		pp.setWhichFieldIsValue(1);
		pp.setCustomerId("") ;
		pp.setInputType("radio");
		pp.setDirection("desc");
		pp.setPageSize_CH(50);
		pp.setPrintTitle("招聘计划");
		pp.setTableID("resumList");
		
		pp.addColumn("标题","title");
		pp.addColumn("招聘类型", "type");
		//pp.addColumn("详细内容", "content");
		pp.addColumn("发布时间", "updateTime");
		
		pp.setSQL(sql);
		
		request.getSession().setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);
		return modelAndView;
		
	}
	public void save (HttpServletRequest request, HttpServletResponse response)   throws Exception{
		
		Connection conn = null;
		ResumMassage resumMassage = null;
		try {
			resumMassage = new ResumMassage();
			ASFuntion CHF = new ASFuntion();
			conn = new DBConnect().getConnect("");
			// 获取当前时间
			String updateTime = CHF.getCurrentDate();
			String title = CHF.showNull(request.getParameter("title"));
			String content = CHF.showNull(request.getParameter("contents"));
			String type = CHF.showNull(request.getParameter("type"));
			resumMassage.setTitle(title);
			resumMassage.setContent(content);
			resumMassage.setType(type);
			resumMassage.setUpdateTime(updateTime);

			ResumMassageService rms = new ResumMassageService(conn);
			rms.addNews(resumMassage);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}

		response.sendRedirect(request.getContextPath() + "/resum.do?method=list");
		
	}
public void update (HttpServletRequest request, HttpServletResponse response)   throws Exception{
		
		Connection conn = null;
		ResumMassage resumMassage = null;
		try {
			resumMassage = new ResumMassage();
			ASFuntion CHF = new ASFuntion();
			conn = new DBConnect().getConnect("");
			// 获取当前时间
			String updateTime = CHF.getCurrentDate();
			String autoid = CHF.showNull(request.getParameter("autoid"));
			int id = Integer.parseInt(autoid);
			String title = CHF.showNull(request.getParameter("title"));
			String content = CHF.showNull(request.getParameter("contents"));
			String type = CHF.showNull(request.getParameter("type"));
			//resumMassage.setTitle(id);
			resumMassage.setTitle(title);
			resumMassage.setContent(content);
			resumMassage.setType(type);
			resumMassage.setUpdateTime(updateTime);

			ResumMassageService rms = new ResumMassageService(conn);
			rms.update(resumMassage,id);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}

		response.sendRedirect(request.getContextPath() + "/resum.do?method=list");
		
	}
	public void del (HttpServletRequest request, HttpServletResponse response)   throws Exception{
		Connection conn = null;
		try {
			ASFuntion CHF = new ASFuntion();
			String autoid = CHF.showNull(request.getParameter("autoid"));
			
			conn= new DBConnect().getConnect("");
			ResumMassageService rms = new ResumMassageService(conn);
			rms.del(autoid);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}
		response.sendRedirect(request.getContextPath() + "/resum.do?method=list");
	}
	//public ModelAndView goEdit
	
	public ModelAndView edit (HttpServletRequest request, HttpServletResponse response)   throws Exception{
		ModelAndView modelAndView = new ModelAndView(edit);
		
		Connection conn=null;
		try {
			ASFuntion CHF = new ASFuntion();
			
			//UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
			
			String autoid = CHF.showNull(request.getParameter("autoid"));
			conn = new DBConnect().getConnect("");
			ResumMassageService rms = new ResumMassageService(conn);
			ResumMassage  resum = new ResumMassage();
			//PersonalInfo personal=new PersonalInfo();
			resum=rms.get(autoid);
			modelAndView.addObject("resum", resum);

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}
		return modelAndView;
	}
	public ModelAndView goLook (HttpServletRequest request, HttpServletResponse response)   throws Exception{
		ModelAndView modelAndView = new ModelAndView(goLook);
		Connection conn = null;
		try {
			ASFuntion CHF = new ASFuntion();
			String autoid = CHF.showNull(request.getParameter("autoid")); //
			conn = new DBConnect().getConnect("");
			ResumMassageService rms = new ResumMassageService(conn);
			ResumMassage  resum = new ResumMassage();
			//PersonalInfo personal=new PersonalInfo();
			resum=rms.get(autoid);
			modelAndView.addObject("resum", resum);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}
		return modelAndView;
		
	}
	

	
	
}
	
	
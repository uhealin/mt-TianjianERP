package com.matech.audit.work.hr;

import java.sql.Connection;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
import net.sf.json.JSONArray;
import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.hr.TreeService;
import com.matech.framework.pub.db.DbUtil;

public class TreeAction extends MultiActionController{
	private final String tree = "hr/tree.jsp";
	private final String yjTree = "hr/yjtree.jsp";
	public ModelAndView list(HttpServletRequest request, HttpServletResponse response)throws Exception {
		ModelAndView modelAndView = new ModelAndView(tree);
		return modelAndView;
	}
	public ModelAndView yjList(HttpServletRequest request, HttpServletResponse response)throws Exception {
		ModelAndView modelAndView = new ModelAndView(yjTree);
		return modelAndView;
	}
	public ModelAndView getTree (HttpServletRequest request, HttpServletResponse response)throws Exception {
		response.setContentType("text/html;charset=utf-8");
		Connection conn = null;
		JSONArray jary = new JSONArray();
		try {
			conn = new DBConnect().getConnect("");
			TreeService ts = new TreeService(conn);
			//获得岗位名称，
			jary = ts.get();
			
			String json ="{}";
			
				response.getWriter().write(jary.toString());
			
		} catch (Exception e) {
			
		}finally{
			DbUtil.close(conn);
		}
		return null;
	}
}

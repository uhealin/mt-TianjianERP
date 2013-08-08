package com.matech.audit.work.scan;

import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.scan.ScanService;
import com.matech.audit.service.scan.model.Scan;
import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;

public class ScanAction extends MultiActionController {
	private final String _upload="/user/upload.jsp";
	
	/*
	 * 保存扫描件上传
	 */
	public ModelAndView saveUpload(HttpServletRequest request, HttpServletResponse response)  throws Exception{
		ModelAndView modelAndView = new ModelAndView(_upload);
		ASFuntion asf = new ASFuntion();
		String scanName = asf.showNull(request.getParameter("scanName"));
		String remark = asf.showNull(request.getParameter("remark"));
		String attachment = asf.showNull(request.getParameter("attachment"));
		UserSession userSession = (UserSession)request.getSession().getAttribute("userSession") ;
		String userId=userSession.getUserId();
		Connection conn = null;
		try {
			conn = new DBConnect().getConnect("");
			Scan scan=new Scan();
			scan.setScanName(scanName);
			scan.setRemark(remark);
			scan.setAttachment(attachment);
			ScanService ss=new ScanService(conn);
			ss.add(scan,userId);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}
		return modelAndView;
	}
	
	/*
	 * 跳转到扫描件上传
	 */
	public ModelAndView upload(HttpServletRequest request, HttpServletResponse response)  throws Exception{
		ModelAndView modelAndView = new ModelAndView(_upload);
		return modelAndView;
	}
}

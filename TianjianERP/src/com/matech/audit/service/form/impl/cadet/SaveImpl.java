package com.matech.audit.service.form.impl.cadet;

import java.sql.Connection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.springframework.web.servlet.ModelAndView;

import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.form.FormExtInterface;
import com.matech.audit.service.hr.model.ResumeVO;
import com.matech.audit.work.form.FormDefineAction;
import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.WebUtil;

public class SaveImpl implements FormExtInterface {

	@Override
	public String beforeAdd(Connection conn, String formId,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		// TODO Auto-generated method stub
		DbUtil dbUtil=null;
		WebUtil webUtil=new WebUtil(req, res);
		UserSession userSession=webUtil.getUserSession();
		String idcard=req.getParameter("id");
		int eff=0;
		String re="";
		JSONObject json=new JSONObject();
		res.setContentType(WebUtil.CONTENT_TYPE_UTF8);
		try{
			conn=new DBConnect().getConnect();
			dbUtil=new DbUtil(conn);
			List<ResumeVO> list=dbUtil.select(ResumeVO.class,"select * from {0} where id=?", idcard);
            if(list.size()>0){
                //json.put("re", 1);
                //json.put("uuid", list.get(0).getUuid());
                res.sendRedirect(FormDefineAction.pathFormView(formId, list.get(0).getUuid(), true));
            }else{
            	json.put("re", 1);
            }
            //re=json.toString();
		}catch(Exception ex){
			re=ex.getLocalizedMessage();
		}finally{
			DbUtil.close(conn);
		}
		return re;
	}

	@Override
	public String afterAdd(Connection conn, String formId, String dataUUID,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String beforeUpdate(Connection conn, String formId, String dataUUID,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String afterUpdate(Connection conn, String formId, String dataUUID,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String beforeDelete(Connection conn, String formId, String dataUUID,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String afterDelete(Connection conn, String formId, String dataUUID,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void beforeView(Connection conn, String formId, String dataUUID,
			HttpServletRequest req, HttpServletResponse res,
			ModelAndView modelAndView) {
		// TODO Auto-generated method stub
		
	}

}

package com.matech.audit.service.form.impl.club;

import java.sql.Connection;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.matech.audit.service.club.model.ClubVO;
import com.matech.audit.service.form.FormExtInterface;
import com.matech.audit.service.placard.PlacardService;
import com.matech.audit.service.placard.model.PlacardTable;
import com.matech.audit.service.user.model.UserVO;
import com.matech.audit.work.form.FormDefineAction;
import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.StringUtil;
import com.matech.framework.pub.util.WebUtil;

public class ClubSaveImpl implements FormExtInterface {

	@Override
	public String beforeAdd(Connection conn, String formId,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String afterAdd(Connection conn, String formId, String dataUUID,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		// TODO Auto-generated method stub
		
		DbUtil dbUtil=null;
		WebUtil webUtil=new WebUtil(req, res);
		UserSession userSession=webUtil.getUserSession();
		int eff=0;
		String re="";
		res.setContentType(WebUtil.CONTENT_TYPE_UTF8);
		ClubVO clubVO=null;
		List<UserVO> userVOs=new ArrayList<UserVO>();
		PlacardService placardService=new PlacardService(conn);
		try{
			
			dbUtil=new DbUtil(conn);
			clubVO=dbUtil.load(ClubVO.class, dataUUID);
			userVOs=dbUtil.select(UserVO.class, "select * from {0} where id not in (?)",Integer.valueOf(userSession.getUserId()));
			String caption=MessageFormat.format("{0} 俱乐部成立", clubVO.getName());
			String context=MessageFormat.format("{0} 于 {1} 成立 {2} 俱乐部，敬请留意", userSession.getUserName(),StringUtil.getCurDate(),clubVO.getName());
			//conn.setAutoCommit(false);
		
			for(UserVO userVO:userVOs){
				  PlacardTable placardTable=new PlacardTable();
		    		placardTable.setIsReversion(0);
		    		placardTable.setAddresserTime(StringUtil.getCurDateTime());
		    		placardTable.setCaption(caption);
		    		placardTable.setMatter(context);
		    		placardTable.setIsRead(0);
		    		placardTable.setIsNotReversion(0);
		    		placardTable.setUuid(UUID.randomUUID().toString());
		    		placardTable.setUrl(FormDefineAction.pathFormListExtView("5e85b78b-51b2-45a7-bdda-a86919f3e0c9", "01"));
		    		placardTable.setUuidName("uuid");
		    		placardTable.setModel("查看俱乐部");
		    		placardTable.setAddresser( userSession.getUserId());//发起
		    		placardTable.setAddressee(String.valueOf(userVO.getId()));//发起
		    		placardService.AddPlacard(placardTable);
			}
			//conn.commit();
		}catch(Exception ex){
			re=ex.getLocalizedMessage();
		}
		//response.getWriter().write(re);
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

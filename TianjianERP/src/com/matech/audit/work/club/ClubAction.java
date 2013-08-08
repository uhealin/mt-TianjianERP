package com.matech.audit.work.club;

import java.sql.Connection;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.club.model.ClubApplyVO;
import com.matech.audit.service.club.model.ClubEventVO;
import com.matech.audit.service.club.model.ClubFinanceVO;
import com.matech.audit.service.club.model.ClubVO;
import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.StringUtil;
import com.matech.framework.pub.util.WebUtil;

public class ClubAction extends MultiActionController {

	
	public ModelAndView doApply(HttpServletRequest request,HttpServletResponse response) throws Exception{
		Connection conn=null;
		DbUtil dbUtil=null;
		WebUtil webUtil=new WebUtil(request, response);
		UserSession userSession=webUtil.getUserSession();
		int eff=0;
		String re="";
		response.setContentType(WebUtil.CONTENT_TYPE_UTF8);
		String uuid=request.getParameter("uuid");
		ClubVO clubVO=null; 
		List<ClubApplyVO> clubApplyVOs=new ArrayList<ClubApplyVO>(),clubApplyVOs2=new ArrayList<ClubApplyVO>();
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		Date ndate=null,tdate=null;
		try{
			conn=new DBConnect().getConnect();
			dbUtil=new DbUtil(conn);
			clubVO=dbUtil.load(ClubVO.class, uuid);
			clubApplyVOs=dbUtil.select(ClubApplyVO.class, "select * from {0} where userid=?",
			  userSession.getUserId()
			  //,uuid
			);
			clubApplyVOs2=dbUtil.select(ClubApplyVO.class, "select * from {0} where club_id=?"
					  ,uuid
					);
			ndate=sdf.parse(StringUtil.getCurDate());
			tdate=sdf.parse(clubVO.getTimeout_time());
			if(clubApplyVOs.size()>=3){
				re="你已申请加入3个俱乐部,不能再发起申请";
			}else if(ndate.compareTo(tdate)>0){
				re=MessageFormat.format("今天已经超过 {0} 俱乐部过期日期 {1}，不能发起申请", clubVO.getName(),clubVO.getTimeout_time());
			}else if(clubApplyVOs2.size()>=clubVO.getMember_count()){
				re=MessageFormat.format("报名人数已超过 {0} 俱乐部 报名人数{1}，不能发起申请", clubVO.getName(),clubVO.getMember_count());				
			}
			else{
				ClubApplyVO clubApplyVO=new ClubApplyVO();
				clubApplyVO.setUserid(userSession.getUserId());
				clubApplyVO.setDepartmentid(userSession.getUserAuditDepartmentId());
				clubApplyVO.setState("未审核");
				clubApplyVO.setClub_id(uuid);
				clubApplyVO.setUuid(UUID.randomUUID().toString());
				clubApplyVO.setCreate_date(StringUtil.getCurDateTime());
				eff+=dbUtil.insert(clubApplyVO);
				if(eff>0){
					re=MessageFormat.format("你已成功申请加入{0}俱乐部,请等待管理员审核", clubVO.getName());
				}else{
					re=MessageFormat.format("申请加入{0}俱乐部失败", clubVO.getName());
				}
			}
		}catch(Exception ex){
			re=ex.getLocalizedMessage();
		}finally{
			DbUtil.close(conn);
		}
		response.getWriter().write(re);
		return null;
	}
	
	
	public ModelAndView doFinanceReg(HttpServletRequest request,HttpServletResponse response) throws Exception{
		Connection conn=null;
		DbUtil dbUtil=null;
		WebUtil webUtil=new WebUtil(request, response);
		UserSession userSession=webUtil.getUserSession();
		int eff=0;
		String re="";
		response.setContentType(WebUtil.CONTENT_TYPE_UTF8);
		ClubFinanceVO clubFinanceVO=null;
		ClubVO clubVO=null;
		List<ClubFinanceVO> clubFinanceVOs=new ArrayList<ClubFinanceVO>();
		double rest_amout=0;
		try{
			conn=new DBConnect().getConnect();
			dbUtil=new DbUtil(conn);
			clubFinanceVO=webUtil.evalObject(ClubFinanceVO.class);
			clubVO=dbUtil.load(ClubVO.class, clubFinanceVO.getClub_id());
			clubFinanceVOs=dbUtil.select(ClubFinanceVO.class, "select * from {0} where club_id=? order by create_date desc,uuid desc", clubFinanceVO.getClub_id());
			
			if(clubFinanceVOs.size()==0){
				if("in".equals(clubFinanceVO.getFtype())){
					rest_amout+=clubFinanceVO.getAmount();
				}else if("out".equals(clubFinanceVO.getFtype())){
					rest_amout-=clubFinanceVO.getAmount();
				}
			}else{
				if("in".equals(clubFinanceVO.getFtype())){
					rest_amout=clubFinanceVOs.get(0).getRest_amount()+clubFinanceVO.getAmount();
				}else if("out".equals(clubFinanceVO.getFtype())){
					rest_amout=clubFinanceVOs.get(0).getRest_amount()-clubFinanceVO.getAmount();

				}
			}
			clubFinanceVO.setUuid(UUID.randomUUID().toString());
			clubFinanceVO.setUserid(userSession.getUserId());
			clubFinanceVO.setDepartment(userSession.getUserAuditDepartmentId());
			clubFinanceVO.setRest_amount(rest_amout);
			eff+=dbUtil.insert(clubFinanceVO);
			if(eff>0){
				re="财务记录登记成功";
			}else{
				re="财务记录登记失败";
			}
		}catch(Exception ex){
			re=ex.getLocalizedMessage();
		}finally{
			DbUtil.close(conn);
		}
		response.getWriter().write(re);
		//webUtil.alert(re);
	    //response.getWriter().write("<script>window.opener.location.reload();window.close();</script>");
		return null;
	}
	

	public ModelAndView jsonEvent(HttpServletRequest request,HttpServletResponse response) throws Exception{
		Connection conn=null;
		DbUtil dbUtil=null;
		WebUtil webUtil=new WebUtil(request, response);
		UserSession userSession=webUtil.getUserSession();
		int eff=0;
		String re="";
		response.setContentType(WebUtil.CONTENT_TYPE_UTF8);
		String uuid=request.getParameter("uuid");
		ClubEventVO clubEventVO=null;
		try{
			conn=new DBConnect().getConnect();
			dbUtil=new DbUtil(conn);
			clubEventVO=dbUtil.load(ClubEventVO.class, uuid);
			re=JSONObject.fromObject(clubEventVO).toString();
		}catch(Exception ex){
			re=ex.getLocalizedMessage();
		}finally{
			DbUtil.close(conn);
		}
		response.getWriter().write(re);
		return null;
	}
	
	
}

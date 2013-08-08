package com.matech.audit.work.user;

import java.sql.Connection;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.user.model.UserMenuGroupVO;
import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.StringUtil;
import com.matech.framework.pub.util.WebUtil;

public class UserMenuGroupAction extends MultiActionController{

	
	public enum Jsp{
		userMenuGroup;
		
		public String getPath(){
			return MessageFormat.format("/user/{0}.jsp", this.name());
		}
	}
	
	public ModelAndView doSaveMenuGroup(HttpServletRequest request,HttpServletResponse response) throws Exception{
		Connection conn=null;
		DbUtil dbUtil=null;
		WebUtil webUtil=new WebUtil(request, response);
		UserSession userSession=webUtil.getUserSession();
		int eff=0;
		String re="";
		UserMenuGroupVO userMenuGroupVO=webUtil.evalObject(UserMenuGroupVO.class);
		response.setContentType(WebUtil.CONTENT_TYPE_UTF8);
		try{
			conn=new DBConnect().getConnect();
			dbUtil=new DbUtil(conn);
		    if(StringUtil.isBlank(userMenuGroupVO.getUuid())) {
		    userMenuGroupVO.setUuid(UUID.randomUUID().toString());
		    userMenuGroupVO.setUserid(userSession.getUserId());
		     eff+=dbUtil.insert(userMenuGroupVO);
		    }else{
		      userMenuGroupVO=dbUtil.load(UserMenuGroupVO.class,userMenuGroupVO.getUuid());
		      userMenuGroupVO=webUtil.evalObject(userMenuGroupVO);
		      eff+= dbUtil.update(userMenuGroupVO);
		    }
		    if(eff>0){
		    	re="保存成功";
		    }else{
		    	re="保存失败";
		    }
		}catch(Exception ex){
			re=ex.getLocalizedMessage();
		}finally{
			DbUtil.close(conn);
		}
		response.getWriter().write(re);
		return null;
	}
	
	public ModelAndView doDeleteMenuGroup(HttpServletRequest request,HttpServletResponse response) throws Exception{
		Connection conn=null;
		DbUtil dbUtil=null;
		WebUtil webUtil=new WebUtil(request, response);
		UserSession userSession=webUtil.getUserSession();
		int eff=0;
		String re="";
		String uuid=request.getParameter("uuid");
		response.setContentType(WebUtil.CONTENT_TYPE_UTF8);
		try{
			conn=new DBConnect().getConnect();
			dbUtil=new DbUtil(conn);
			UserMenuGroupVO userMenuGroupVO=dbUtil.load(UserMenuGroupVO.class,uuid);
			eff+=dbUtil.delete(userMenuGroupVO);
			if(eff>0){
		    	re="删除成功";
		    }else{
		    	re="删除失败";
		    }
		}catch(Exception ex){
			re=ex.getLocalizedMessage();
		}finally{
			DbUtil.close(conn);
		}
		response.getWriter().write(re);
		return null;
	}
	
	
	public ModelAndView treeMenuGroup(HttpServletRequest request,HttpServletResponse response) throws Exception{
		Connection conn=null;
		DbUtil dbUtil=null;
		WebUtil webUtil=new WebUtil(request, response);
		UserSession userSession=webUtil.getUserSession();
		int eff=0;
		String re="";
		List<UserMenuGroupVO> userMenuGroupVOs=new ArrayList<UserMenuGroupVO>();
		response.setContentType(WebUtil.CONTENT_TYPE_UTF8); 
		JSONArray jarr=new JSONArray();
		try{
			conn=new DBConnect().getConnect();
			dbUtil=new DbUtil(conn);
			userMenuGroupVOs=dbUtil.select(UserMenuGroupVO.class, "select * from {0} where userid=?", userSession.getUserId());
		    jarr=toMenuTree(userMenuGroupVOs, "root");
		}catch(Exception ex){
			re=ex.getLocalizedMessage();
		}finally{
			DbUtil.close(conn);
		}
		response.getWriter().write(jarr.toString());
		return null;
	}

	
	
	  public static JSONArray toMenuTree(List<UserMenuGroupVO> list,String rootid){
		   JSONArray treeList=new JSONArray();
		   for(UserMenuGroupVO userMenuGroupVO:list){
			   if(!userMenuGroupVO.getParent_id().equals(rootid))continue;
			   JSONObject jsonUserMenu=new JSONObject();
			   jsonUserMenu.put("id", userMenuGroupVO.getUuid());
			   jsonUserMenu.put("text",userMenuGroupVO.getName());
			   jsonUserMenu.put("leaf",false);
			   jsonUserMenu.put("children",toMenuTree(list,userMenuGroupVO.getUuid() ));
			   
		       treeList.add(userMenuGroupVO);
		   }
		   return treeList;
	    }
	
}

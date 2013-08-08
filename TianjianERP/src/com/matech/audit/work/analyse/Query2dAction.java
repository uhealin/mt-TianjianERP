package com.matech.audit.work.analyse;

import java.sql.Connection;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.analyse.Query2dService;
import com.matech.audit.service.analyse.model.ConditionVO;
import com.matech.audit.service.analyse.model.TableColVO;
import com.matech.audit.service.analyse.model.TableResultVO;
import com.matech.audit.service.analyse.model.TableRowVO;
import com.matech.audit.service.analyse.model.TableVO;
import com.matech.audit.service.employment.EmploymentService;
import com.matech.audit.service.form.model.FormVO;
import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.StringUtil;
import com.matech.framework.pub.util.WebUtil;

public class Query2dAction extends MultiActionController{

	
	
	protected enum Jsp{
		query2d;
		
		public String getPath(){
			return MessageFormat.format("/analyse/{0}.jsp", this.name());
		}
	}
	
	protected enum Layout{
		table,resultCal,resultDisplay,condition;
		
		public String getPath(){
			return MessageFormat.format("/analyse/layout/{0}.jsp", this.name());
		}
	}
	
	
	public ModelAndView list(HttpServletRequest request,HttpServletResponse response) throws Exception{
		
		return new ModelAndView(Jsp.query2d.getPath());
		
	}
	
	public ModelAndView tree(HttpServletRequest request,HttpServletResponse response) throws Exception{
	
		Connection conn=null;
		DbUtil dbUtil=null;
		JSONArray jarr=new JSONArray();
		String formId=request.getParameter("formid");
		String ntype=request.getParameter("ntype");
		response.setContentType(WebUtil.CONTENT_TYPE_UTF8);
	    try{
	    	conn=new DBConnect().getConnect();
	    	dbUtil=new DbUtil(conn);
	    	if(!Layout.table.name().equals(ntype)){
	    		List<TableVO> tableVOs=dbUtil.select(TableVO.class, "select * from {0} ");
	    		
	    		for(TableVO tableVO:tableVOs){
	    			JSONObject json=JSONObject.fromObject(tableVO);
	    			json.put("id", tableVO.getUuid());
	    			json.put("text", tableVO.getCaption());
	    			json.put("ntype", Layout.table.name());
	    			json.put("leaf", false);
	    			json.put("formid", tableVO.getUuid());
	    			jarr.add(json);
	    		}
	    	
	    	}else{
	    		List<TableResultVO> tableResultVOs=dbUtil.select(TableResultVO.class, "select * from {0} where tableid=?", formId);
	    		for(TableResultVO tableResultVO:tableResultVOs){
	    			JSONObject json=JSONObject.fromObject(tableResultVO);
	    			json.put("id", tableResultVO.getUuid());
	    			json.put("text", tableResultVO.getCaption());
	    			json.put("ntype", Layout.resultDisplay.name());
	    			json.put("leaf", true);
	    			jarr.add(json);
	    		}
	    	}
	    	response.getWriter().write(jarr.toString());
	    }catch(Exception ex){
	    	ex.printStackTrace();
	    }finally{
	    	DbUtil.close(conn);
	    }
		return null;
	}
	
	public ModelAndView doSaveTable(HttpServletRequest request,HttpServletResponse response) throws Exception{
		Connection conn=null;
		DbUtil dbUtil=null;
		WebUtil webUtil=new WebUtil(request, response);
		TableVO tableVO=null;
		String uuid=request.getParameter("uuid");
		response.setContentType(WebUtil.CONTENT_TYPE_UTF8);
		int eff=0;
		String result="";
		try{
			conn=new DBConnect().getConnect();
			dbUtil=new DbUtil(conn);
			
			if(StringUtil.isBlank(uuid)){
			  tableVO=webUtil.evalObject(TableVO.class);
			  tableVO.setUuid(UUID.randomUUID().toString());
			  eff+=dbUtil.insert(tableVO);
			}else{
			  tableVO=dbUtil.load(TableVO.class, uuid);
			  tableVO=webUtil.evalObject(tableVO);
			  eff+=dbUtil.update(tableVO);
			}
			if(eff==1){
				result=MessageFormat.format("查询统计 {0} 保存成功", tableVO.getCaption());
			}else{
				result=MessageFormat.format("查询统计 {0} 保存失败", tableVO.getCaption());
			}
		}catch(Exception ex){
			ex.printStackTrace();
			result=ex.getLocalizedMessage();
		}finally{
			DbUtil.close(conn);
		}
		response.getWriter().write(result);
		return null;
	}
	
	
	public ModelAndView doDeleteTable(HttpServletRequest request,HttpServletResponse response) throws Exception{
		Connection conn=null;
		DbUtil dbUtil=null;
		WebUtil webUtil=new WebUtil(request, response);
		response.setContentType(WebUtil.CONTENT_TYPE_UTF8);
		int eff=0;
		String re="";
		TableVO tableVO=webUtil.evalObject(TableVO.class);
		try{
			conn=new DBConnect().getConnect();
			dbUtil=new DbUtil(conn);
			eff+=dbUtil.delete(tableVO);
			if(eff==1){
				re="删除成功";
			}else{
				re="删除失败";
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		response.getWriter().write(re);
		return null;
	}
	
	public ModelAndView doSaveResult(HttpServletRequest request,HttpServletResponse response) throws Exception{
		Connection conn=null;
		DbUtil dbUtil=null;
		WebUtil webUtil=new WebUtil(request, response);
		TableResultVO resultVO=webUtil.evalObject(TableResultVO.class);
		int eff=0;
		String re="";
		response.setContentType(WebUtil.CONTENT_TYPE_UTF8);
		try{
			conn=new DBConnect().getConnect();
			dbUtil=new DbUtil(conn);
			if(StringUtil.isBlank(resultVO.getUuid())){
				resultVO.setUuid(UUID.randomUUID().toString());
				eff+=dbUtil.insert(resultVO);
			}else{
				resultVO=dbUtil.load(TableResultVO.class, resultVO.getUuid());
				resultVO=webUtil.evalObject(resultVO);
				eff+=dbUtil.update(resultVO);
			}
			re=eff==1?"保存成功":"保存失败";
		}catch(Exception ex){
			re=ex.getLocalizedMessage();
			ex.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		response.getWriter().write(re);
		return null;
	}
	
	public ModelAndView doDeleteResult(HttpServletRequest request,HttpServletResponse response) throws Exception{
		Connection conn=null;
		DbUtil dbUtil=null;
		WebUtil webUtil=new WebUtil(request, response);
		try{
			conn=new DBConnect().getConnect();
			dbUtil=new DbUtil(conn);
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		return null;
	}
	
	
	public ModelAndView layoutTable(HttpServletRequest request,HttpServletResponse response) throws Exception{
		String uuid=request.getParameter("uuid");
		ModelAndView mv= new ModelAndView(Layout.table.getPath());
		TableVO tableVO=new TableVO();
		List<TableRowVO> tableRowVOs=new ArrayList<TableRowVO>();
		List<TableColVO> tableColVOs=new ArrayList<TableColVO>();
		Connection conn=null;
		DbUtil dbUtil=new DbUtil(conn);
		try{
			conn=new DBConnect().getConnect();
			dbUtil=new DbUtil(conn);
			
		if(!StringUtil.isBlank(uuid)){
			tableVO=dbUtil.load(TableVO.class, uuid);
			String sqlPattern="select t1.*,t2.caption from {0} t1 left join an_condition t2 on t1.conid=t2.uuid where tableid=?";
			tableRowVOs=dbUtil.select(TableRowVO.class, sqlPattern, uuid);
			tableColVOs=dbUtil.select(TableColVO.class, sqlPattern, uuid);
		}
		mv.addObject("table",tableVO);
		request.setAttribute(TableRowVO.class.getName(), tableRowVOs);
		request.setAttribute(TableColVO.class.getName(), tableColVOs);
		mv.addObject("cols",tableColVOs);
		}catch(Exception ex){
			
		} finally{
			DbUtil.close(conn);
		}
		return mv;
	}
	
	public ModelAndView layoutResult(HttpServletRequest request,HttpServletResponse response) throws Exception{
		
		ModelAndView mv= new ModelAndView(Layout.resultDisplay.getPath());
		Connection conn=null;
		DbUtil dbUtil=null;
		TableResultVO tableResultVO=new TableResultVO();
		String uuid=request.getParameter("uuid");
		try{
			conn=new DBConnect().getConnect();
			dbUtil=new DbUtil(conn);
			tableResultVO=dbUtil.load(TableResultVO.class, uuid);
			mv.addObject("vo", tableResultVO);
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		return mv;
	}
	
	public ModelAndView layoutCondition(HttpServletRequest request,HttpServletResponse response) throws Exception{
		
		Connection conn=null;
		DbUtil dbUtil=null;
		ModelAndView mv=new ModelAndView(Layout.condition.getPath());
		ConditionVO vo=new ConditionVO();
		String uuid=request.getParameter("uuid");
		FormVO formVO=new FormVO();
		JSONArray jarr=new JSONArray();
		try{
			conn=new DBConnect().getConnect();
			dbUtil=new DbUtil(conn);
			vo=dbUtil.load(ConditionVO.class,uuid);
			formVO=dbUtil.load(FormVO.class, vo.getFormid());
			jarr=JSONArray.fromObject(vo.getJsonstr());
			
			mv.addObject("vo",vo);
			mv.addObject("formVo",formVO);
		    request.setAttribute("jarrQuerys", jarr);
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		return mv;
	}
	
	public ModelAndView doSaveRow(HttpServletRequest request,HttpServletResponse response) throws Exception{
		Connection conn=null;
		DbUtil dbUtil=null;
		WebUtil webUtil=new WebUtil(request, response);
		response.setContentType(WebUtil.CONTENT_TYPE_UTF8);
		UserSession userSession=webUtil.getUserSession();
		int eff=0;
		String re="";
	    TableRowVO vo=webUtil.evalObject(TableRowVO.class);
		try{
	        conn=new DBConnect().getConnect();
	        dbUtil=new DbUtil(conn);
	        if(StringUtil.isBlank(vo.getUuid())){
	        	vo.setUuid(UUID.randomUUID().toString());
	        	eff+=dbUtil.insert(vo);
	        }else{
	        	vo=dbUtil.load(TableRowVO.class, vo.getUuid());
	        	vo=webUtil.evalObject(vo);
	        	eff+=dbUtil.update(vo);
	        }
	        if(eff==1){
	        	re="保存成功";
	        }else {
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
	
	public ModelAndView doSaveCol(HttpServletRequest request,HttpServletResponse response) throws Exception{
		Connection conn=null;
		DbUtil dbUtil=null;
		WebUtil webUtil=new WebUtil(request, response);
		UserSession userSession=webUtil.getUserSession();
		response.setContentType(WebUtil.CONTENT_TYPE_UTF8);
		int eff=0;
		String re="";
	    TableColVO vo=webUtil.evalObject(TableColVO.class);
		try{
	        conn=new DBConnect().getConnect();
	        dbUtil=new DbUtil(conn);
	        if(StringUtil.isBlank(vo.getUuid())){
	        	vo.setUuid(UUID.randomUUID().toString());
	        	eff+=dbUtil.insert(vo);
	        }else{
	        	vo=dbUtil.load(TableColVO.class, vo.getUuid());
	        	vo=webUtil.evalObject(vo);
	        	eff+=dbUtil.update(vo);
	        }
	        if(eff==1){
	        	re="保存成功";
	        }else {
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
	
	public ModelAndView doSaveCondition(HttpServletRequest request,HttpServletResponse response) throws Exception{
		Connection conn=null;
		DbUtil dbUtil=null;
		WebUtil webUtil=new WebUtil(request, response);
		response.setContentType(WebUtil.CONTENT_TYPE_UTF8);
		UserSession userSession=webUtil.getUserSession();
		int eff=0;
		String re="";
	    ConditionVO vo=webUtil.evalObject(ConditionVO.class);
	    vo.setLastuser(userSession.getUserId());
	    vo.setLasttime(StringUtil.getCurDateTime());
		try{
	        conn=new DBConnect().getConnect();
	        dbUtil=new DbUtil(conn);
	        if(StringUtil.isBlank(vo.getUuid())){
	        	vo.setUuid(UUID.randomUUID().toString());
	        	eff+=dbUtil.insert(vo);
	        }else{
	        	vo=dbUtil.load(ConditionVO.class, vo.getUuid());
	        	vo=webUtil.evalObject(vo);
	        	eff+=dbUtil.update(vo);
	        }
	        if(eff==1){
	        	re="保存成功";
	        }else {
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
	
	public ModelAndView doDeleteCol(HttpServletRequest request,HttpServletResponse response) throws Exception{
		Connection conn=null;
		DbUtil dbUtil=null;
		WebUtil webUtil=new WebUtil(request, response);
		UserSession userSession=webUtil.getUserSession();
		int eff=0;
		String re="";
		TableColVO vo=webUtil.evalObject(TableColVO.class);
		response.setContentType(WebUtil.CONTENT_TYPE_UTF8);
		try{
	        conn=new DBConnect().getConnect();
	        dbUtil=new DbUtil(conn);
	        eff=dbUtil.delete(vo);
	        if(eff==1){
	        	re="操作成功";
	        }else {
	        	re="操作失败";
	        }
		}catch(Exception ex){
			re=ex.getLocalizedMessage();
		}finally{
			DbUtil.close(conn);
		}
		response.getWriter().write(re);
		return null;
	}
	
	public ModelAndView doDeleteRow(HttpServletRequest request,HttpServletResponse response) throws Exception{
		Connection conn=null;
		DbUtil dbUtil=null;
		WebUtil webUtil=new WebUtil(request, response);
		UserSession userSession=webUtil.getUserSession();
		int eff=0;
		String re="";
		TableRowVO vo=webUtil.evalObject(TableRowVO.class);
		response.setContentType(WebUtil.CONTENT_TYPE_UTF8);
		try{
	        conn=new DBConnect().getConnect();
	        dbUtil=new DbUtil(conn);
	        eff=dbUtil.delete(vo);
	        if(eff==1){
	        	re="操作成功";
	        }else {
	        	re="操作失败";
	        }
		}catch(Exception ex){
			re=ex.getLocalizedMessage();
		}finally{
			DbUtil.close(conn);
		}
		response.getWriter().write(re);
		return null;
	}
	
	
	public ModelAndView treeCondition(HttpServletRequest request,HttpServletResponse response) throws Exception{
		
		Connection conn=null;
		DbUtil dbUtil=null;
		JSONArray jarr=new JSONArray();
		String formId=request.getParameter("formid");
		String ntype=request.getParameter("ntype");
		response.setContentType(WebUtil.CONTENT_TYPE_UTF8);
		try{
	    	conn=new DBConnect().getConnect();
	        dbUtil=new DbUtil(conn);
	        if(StringUtil.isBlank(ntype)){
	           List<FormVO> subsets=dbUtil.select(FormVO.class, "select * from {0} where form_type=?", EmploymentService.UUID_FORM_TYPE_SUBSET);    	
	           for(FormVO subset:subsets){
	        	   JSONObject json=JSONObject.fromObject(subset);
	        	   json.put("id", subset.getUUID());
	        	   json.put("text", subset.getNAME());
	        	   json.put("ntype", "subset");
	        	   json.put("formid", subset.getUUID());
	        	   json.put("leaf",false);
	        	   jarr.add(json);
	           }
	        }else if("subset".equals(ntype)){
	        	List<ConditionVO> conditionVOs=dbUtil.select(ConditionVO.class, "select * from {0} where formid=?", formId);
	        	for(ConditionVO conditionVO:conditionVOs){
	        		JSONObject json=JSONObject.fromObject(conditionVO);
	        		json.put("id", conditionVO.getUuid());
	        		json.put("text", conditionVO.getCaption());
	        		json.put("leaf", true);
	        		json.put("ntype", "condition");
	        		jarr.add(json);
	        	}
	        	JSONObject add=new JSONObject();
	        	add.put("text", "<span style='color:red'>新增</span>");
	        	add.put("ntype", "addCondition");
	        	add.put("formid", formId);
	        	add.put("leaf", true);
	        	jarr.add(add);
	        }
	    }catch(Exception ex){
	    	ex.printStackTrace();
	    }finally{
	    	DbUtil.close(conn);
	    }
		response.getWriter().write(jarr.toString());
	    return null;
	}
	
	public ModelAndView doCal2d(HttpServletRequest request,HttpServletResponse response) throws Exception{
        String uuid=request.getParameter("uuid");
        Connection conn=null;
        DbUtil dbUtil=null;
        int[][] int2d=null;
        TableVO tableVO=null;
        List<ConditionVO> condRowVOs=new ArrayList<ConditionVO>();
        List<ConditionVO> condColVOs=new ArrayList<ConditionVO>();
        Query2dService query2dService=null;
        try{
        	conn=new DBConnect().getConnect();
        	dbUtil=new DbUtil(conn);
        	tableVO=dbUtil.load(TableVO.class, uuid);
        	query2dService=new Query2dService(conn);
        	condRowVOs=query2dService.condRows(tableVO);
        	condColVOs=query2dService.condCols(tableVO);
        	int2d=query2dService.cal(tableVO);
        	request.setAttribute("int2d", int2d);
        	request.setAttribute("rows", condRowVOs);
        	request.setAttribute("cols", condColVOs);
        	
        }catch(Exception ex){
        	ex.printStackTrace();
        }finally{
        	DbUtil.close(conn);
        }
		return new ModelAndView(Layout.resultCal.getPath());                                                		
	}
}

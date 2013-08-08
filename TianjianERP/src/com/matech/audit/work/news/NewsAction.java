package com.matech.audit.work.news;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.jms.Session;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.catalina.tribes.util.UUIDGenerator;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;


import com.matech.audit.pub.datagrid.DataGrid;
import com.matech.audit.pub.datagrid.DataGridProperty;
import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.attach.AttachService;
import com.matech.audit.service.attach.model.Attach;
import com.matech.audit.service.datamanage.BackupUtil;
import com.matech.audit.service.department.DepartmentService;
import com.matech.audit.service.department.model.DepartmentVO;
import com.matech.audit.service.fileupload.MyFileUpload;
import com.matech.audit.service.news.NewsService;
import com.matech.audit.service.news.model.News;
import com.matech.audit.service.sysMenuManger.model.MenuVO;
import com.matech.audit.work.department.DepartmentAction;
import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.autocode.DELUnid;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;
import com.matech.framework.pub.util.StringUtil;
import com.matech.framework.pub.util.WebUtil;

public class NewsAction extends MultiActionController {
	private final String MAIN = "news/main.jsp";
	private final String LIST = "news/list.jsp";
	private final String ADDANDEDIT = "news/addAndEdit.jsp";
	private final String VIEW = "news/view.jsp";
	private final String MORE = "news/more.jsp";

	/**
	 * 列表
	 * 
	 * @param request
	 * @param Response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView list(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		DataGridProperty pp = new DataGridProperty() {
		};
		String menuid=request.getParameter("menuid");
		String hasclass=request.getParameter("hasclass");
		// 必要设置
		pp.setTableID("newsList"+"_"+menuid);
		pp.setTrActionProperty(true);
		pp.setTrAction("style='cursor:hand;' onDBLclick='goSort(this);' myUserid='${autoId}' ");

		// 基本设置

		pp.setCustomerId("");

		pp.setPageSize_CH(50);

		String opt = request.getParameter("opt");
		Connection conn=new DBConnect().getConnect();
		DbUtil dbUtil=new DbUtil(conn);
		WebUtil webUtil=new WebUtil(request, response);
		UserSession userSession=webUtil.getUserSession();
		int re=dbUtil.queryForInt("SELECT COUNT(*) FROM s_sysmenu WHERE parentid IN (SELECT menu_id FROM s_sysmenu WHERE NAME = '区域资讯') AND id="+menuid);
		String areaSql=re>0?" and area='"+userSession.getAreaid()+"'":" ";
		// sql设置
		//UserSession userSession = (UserSession)request.getSession().getAttribute("userSession") ;
		String user = userSession.getUserId();
	//	String sql = " select n.autoId,n.title,subString(n.contents,1,30) as contents,n.updateTime,n.publishUserId,n.attachmentId,n.memo,n.property,u.name "
			///	+ " from oa_news n left join k_user u on n.publishUserId=u.id " +
			//			" where 1=1 and (n.publishUserId = '"+userSession.getUserId()+"' or '19'='"+userSession.getUserId()+"') ${title} ${publishUserId} ${updateTime}";
		String sql = " select n.autoId,n.title,subString(n.contents,1,30) as contents,n.updateTime,n.publishUserId,n.attachmentId,n.memo,n.property,u.name,n.type,n.big_type,sm.name as menu_name "
				//+ " from oa_news n left join k_user u on n.publishUserId=u.id left join s_sysmenu sm on sm.id=n.menuid WHERE 1=1 and (n.publishUserId = '"+userSession.getUserId()+"' or '19'='"+userSession.getUserId()+"') "
				+ " from oa_news n left join k_user u on n.publishUserId=u.id left join s_sysmenu sm on sm.id=n.menuid WHERE 1=1 "+areaSql
				//+(opt==null?" ":" and big_type = '"+opt+"' ")
				+(menuid==null?" ":" and n.menuid='"+menuid+"' ")
				+" ${title} ${publishUserId} ${type} ${updateTime}";
       
		ASFuntion af = new ASFuntion();
		String title = af.showNull(request.getParameter("title"));
		String publishUserId = af.showNull(request
				.getParameter("publishUserId"));
		String type= af.showNull(request.getParameter("type"));
		String updateTime = af.showNull(request.getParameter("updateTime"));


		// 查询设置
		if (!title.equals(""))
			sql = sql + " and title like '%" + title + "%'";
		if (!publishUserId.equals(""))
			sql = sql + " and publishUserId = '" + publishUserId + "'";
		
		if(!type.equals("")){
			sql = sql + "and type = '" +type+"'";
		}
		
		if (!updateTime.equals(""))
			sql = sql + " and updateTime = '" + updateTime + "'";
		
		pp.addSqlWhere("title"," and n.title like '%${title}%'");
		pp.addSqlWhere("publishUserId","  and n.publishUserId like '%${publishUserId}%'");
		pp.addSqlWhere("type","  and n.type like '%${type}%'");
		pp.addSqlWhere("updateTime"," and n.updateTime like '%${updateTime}%'");
		
		pp.setSQL(sql);
		pp.setOrderBy_CH("updateTime");
		pp.setDirection("desc");

		pp.setColumnWidth("30,10,15,10,10");

		pp.setInputType("checkbox");
		pp.addColumn("标题", "title");
		//pp.addColumn("发布人", "name");
		//pp.addColumn("类别","sm_name");
		pp.addColumn("类别","type");
		pp.addColumn("发布时间", "updateTime");

		pp.setTrActionProperty(true);
		pp.setTrAction(" autoId='${autoId}' style='cursor:hand;'");
		
		pp.setWhichFieldIsValue(1);

		pp.setPrintEnable(true);
		pp.setPrintVerTical(false);
		pp.setPrintTitle("新闻列表");
		
System.out.println("sql = "+sql);
		request.getSession().setAttribute(
				DataGrid.sessionPre + pp.getTableID(), pp);
      
		return new ModelAndView(LIST);
	}

	/**
	 * 得到 news list 集合对象
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView getListNews(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView model = new ModelAndView(MAIN);
		List<News> list = null;
		Connection conn = null;

		try {
			conn = new DBConnect().getConnect("");
			NewsService ns = new NewsService(conn);
			list = ns.getListNews();
			model.addObject("newsList", list);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}

		return model;
	}

	/**
	 * 添加跳转
	 * 
	 * @return
	 */
	public ModelAndView goAdd(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView model = new ModelAndView(ADDANDEDIT);
		String opt = request.getParameter("opt");
		request.setAttribute("opt", opt);
		WebUtil web = new WebUtil(request, response);
		Connection conn = null;
		boolean b = false;
		
		try {
			conn = new DBConnect().getConnect();
			DepartmentService ds = new DepartmentService(conn);
			 b = ds.isTotalDep(web.getUserSession());
			 model.addObject("b",b);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		
		
		
		return model;
	}

	/**
	 * 修改跳转
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView goUpdate(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView model = new ModelAndView(ADDANDEDIT);
		String autoId = request.getParameter("autoId");
		Connection conn = null;
		News news = null;
		String opt = request.getParameter("opt");
		String hasclass=request.getParameter("hasclass");
		WebUtil web = new WebUtil(request, response);
		boolean b = false;
		DbUtil dbUtil=null;
		try {
			conn = new DBConnect().getConnect("");
			dbUtil=new DbUtil(conn);
			NewsService ns = new NewsService(conn);
			//news = ns.getNewsByAutoId(autoId);
			news=dbUtil.load(News.class, Integer.parseInt(autoId));
	
			DepartmentService ds = new DepartmentService(conn);
			 b = ds.isTotalDep(web.getUserSession());
			 model.addObject("b",b);
			model.addObject("autoId", autoId);
			model.addObject("news", news);
	       
//			// 文件名称
//			String fileName = "";
//			String sql = "select filename from asdb.k_attach where unid = "+news.getAttachmentId();
//			String temp = new DbUtil(conn).queryForString(sql);
			
			/*
			List<Attach> list = ns.getAttach(news.getAttachmentId());
			System.out.println(list.size());
			if(list.size()>0){
				List lists = new ArrayList();
				for (Attach attach : list) {
					Map map = new HashMap();
					// 文件路径
					String filePath = BackupUtil.getDATABASE_PATH() + "../ManuScriptData/"
					+ "news/" + attach.getMime();
					map.put("fileName",attach.getMime());
					map.put("filePath",filePath);
					map.put("unid", attach.getUnid());
					lists.add(map);
				}
				
				model.addObject("list", lists);
			}*/
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}
		return model;
	}

	/**
	 * 新增保存
	 * 
	 * @param request
	 * @param response
	 * @param news
	 * @return
	 * @throws IOException
	 */
	public ModelAndView addSave(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		Connection conn = null;
		String opt = request.getParameter("opt");
		String menuid=request.getParameter("menuid");
		String hasclass=request.getParameter("hasclass");
		DbUtil dbUtil=null;
		News news=new News();
		try {
			
			ASFuntion CHF = new ASFuntion();
			conn = new DBConnect().getConnect("");
			dbUtil=new DbUtil(conn);
			/*
			MyFileUpload myfileUpload = new MyFileUpload(request);
			myfileUpload.setUploadProcess(true,"") ;
			
			response.setContentType("text/html;charset=UTF-8") ;
			
			//文件上传的路径 
			String filePath = BackupUtil.getDATABASE_PATH() + "../ManuScriptData/news/";
			File file = new File(filePath) ; 
			if(!file.exists()) {
				file.mkdir() ;
			}
			
			String uploadtemppath = myfileUpload.UploadFile(null, filePath);

			System.out.println(this.getClass()+"   |  uploadtemppath="+uploadtemppath+"  | filePath="+filePath);

			Map parameters = myfileUpload.getMap();
			String unid = DELUnid.getNumUnid(); // 唯一编号
			*/
			
			
			String title = CHF.showNull(request.getParameter("title"));
			String contents = CHF.showNull(request.getParameter("contents"));
			String memo = CHF.showNull(request.getParameter("memo"));
			String attachmentId = CHF.showNull(request.getParameter("attachmentId"));
			String updateTime = CHF.showNull(request.getParameter("updateTime"));
			String type = request.getParameter("type");
			if(type==null||type.isEmpty()){
				MenuVO menuVO=dbUtil.load(MenuVO.class, Integer.parseInt(menuid));
				news.setType(menuVO.getName().replace("维护",""));
			}else{
			news.setType(type);
			}
			news.setAttachmentId(attachmentId);
			news.setTitle(title);
			news.setContents( contents);
			news.setMemo(memo);
			if(updateTime==null||updateTime.isEmpty()){
			 news.setUpdateTime(StringUtil.getCurDate());
			}else{
			 news.setUpdateTime(updateTime);
			}
            news.setMenuid(menuid);
			news.setDoc_no(request.getParameter("doc_no"));
			news.setSub_title(request.getParameter("sub_title"));

			
			NewsService ns = new NewsService(conn);
			ASFuntion as = new ASFuntion();
			// 获取当前时间
			//String updateTime = as.getCurrentDate();
		//	news.setUpdateTime(updateTime);

			// 当前登录用户
			HttpSession session = request.getSession();
			UserSession userSession = (UserSession) session
					.getAttribute("userSession");
			news.setArea(userSession.getAreaid());
			news.setPublishUserId(userSession.getUserId());
			news.setBig_type(opt);

			// 保存
			//ns.addNews(news);
            dbUtil.insert(news);
			// 附件名称 input="file" 类型
			
		/*	String filename = (String) news.getAttachmentId();
			if (filename != null && !"".equals(filename)) {

				// 上传附件
				Attach attach = new Attach();

				String unid = attach.setUnid(UUID);

				attach.setUdate(CHF.getCurrentDate());
				String departid = userSession.getUserAuditDepartmentId();
				departid = "111"; // 暂时无条件设置 int 类型插入值的时候不能为空
				attach.setDepartid(departid);

				attach.setTitle(userSession.getCurCustomerId());
				attach.setTypeId("8");
				attach.setProperty(unid);
				attach.setOrderId("");
				attach.setContent("新闻附件");
				attach.setEdate(CHF.getCurrentDate());
				attach.setReleasedate(CHF.getCurrentDate());
				attach.setLastDate(CHF.getCurrentDate());
				attach.setLastPerson(userSession.getUserId());

				attach.setFilename(filename);
				attach.setMime(filename.substring(filename.lastIndexOf("\\")+1,filename.length()));

				 
				AttachService attachService = new AttachService(conn);
				attachService.save(attach, "ad");
			}*/

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}

		response.sendRedirect("news.do?method=list&opt="+opt+"&menuid="+menuid+"&hasclass="+hasclass);
		return null;
	}

	/**
	 * 修改保存
	 * 
	 * @param request
	 * @param response
	 * @param news
	 * @return
	 * @throws IOException
	 */
	public ModelAndView updateSave(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		Connection conn = null;
		String opt = request.getParameter("opt"); 
		String hasclass=request.getParameter("hasclass");
		
		String autoid=request.getParameter("autoId");
		DbUtil dbUtil=null;
		News news=null;
		try {
			
			ASFuntion CHF = new ASFuntion();
			/*
			MyFileUpload myfileUpload = new MyFileUpload(request);
			myfileUpload.setUploadProcess(true,"") ;
			
			response.setContentType("text/html;charset=UTF-8") ;
			
			//文件上传的路径 
			String filePath = BackupUtil.getDATABASE_PATH() + "../ManuScriptData/news/";
			File file = new File(filePath) ; 
			if(!file.exists()) {
				file.mkdir() ;
			}
			
			String uploadtemppath = myfileUpload.UploadFile(null, filePath);

			System.out.println(this.getClass()+"   |  uploadtemppath="+uploadtemppath+"  | filePath="+filePath);

			Map parameters = myfileUpload.getMap();

			news.setAutoId((String) parameters.get("autoId"));


			news.setTitle((String) parameters.get("title"));
			news.setContents((String) parameters.get("contents"));
			news.setMemo((String) parameters.get("memo"));
			*/
			String title = CHF.showNull(request.getParameter("title"));
			String contents = CHF.showNull(request.getParameter("contents"));
			String memo = CHF.showNull(request.getParameter("memo"));
			String attachmentId = CHF.showNull(request.getParameter("attachmentId"));
			String updateTime = CHF.showNull(request.getParameter("updateTime"));
            String big_type = CHF.showNull(request.getParameter("big_type"));
            String type = CHF.showNull(request.getParameter("type"));
            String dept_type = CHF.showNull(request.getParameter("dept_type"));
    		conn = new DBConnect().getConnect("");
			dbUtil=new DbUtil(conn);
			 news=dbUtil.load(News.class, Integer.parseInt(autoid));
			news.setAttachmentId(attachmentId);
			news.setTitle(title);
			news.setContents( contents);
			news.setMemo(memo);
			//news.setBig_type(big_type);
			if(type!=null&&!type.isEmpty()){
			news.setType(type);
			}
			news.setUpdateTime(updateTime);
			//news.setDept_type(dept_type);
			news.setDoc_no(request.getParameter("doc_no"));
			news.setSub_title(request.getParameter("sub_title"));

	
			NewsService ns = new NewsService(conn);
			ASFuntion as = new ASFuntion();
			// 获取当前时间
			//String updateTime = as.getCurrentDate();
			//news.setUpdateTime(updateTime);

			// 当前登录用户
			HttpSession session = request.getSession();
			UserSession userSession = (UserSession) session
					.getAttribute("userSession");
			news.setPublishUserId(userSession.getUserId());

			// 修改
			//ns.updateNewsByAutoId(news);
            dbUtil.update(news);
			/*
			// 附件名称 input="file" 类型
			String filename = (String) parameters.get("fileName");
			if (filename != null && !"".equals(filename)) {

				// 上传附件
				Attach attach = new Attach();
				String unid = DELUnid.getNumUnid(); // 唯一编号
				attach.setUnid(unid);
	
				attach.setUdate(CHF.getCurrentDate());
				String departid = userSession.getUserAuditDepartmentId();
				departid = "111"; // 暂时无条件设置 int 类型插入值的时候不能为空   [ 新闻附件 ]
				attach.setDepartid(departid);
	
				attach.setTitle(userSession.getCurCustomerId());
				attach.setTypeId("8");
				attach.setProperty(ns.getNewsByAutoId((String) parameters.get("autoId")).getAttachmentId());
				attach.setOrderId("");
				attach.setContent("新闻附件");
				attach.setEdate(CHF.getCurrentDate());
				attach.setReleasedate(CHF.getCurrentDate());
				attach.setLastDate(CHF.getCurrentDate());
				attach.setLastPerson(userSession.getUserId());
	
				attach.setFilename(filename);
				attach.setMime(filename.substring(filename.lastIndexOf("\\")+1,filename.length()));

				AttachService attachService = new AttachService(conn);
				attachService.save(attach, "ad");
		}*/

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}

		response.sendRedirect("news.do?method=list&menuid="+news.getMenuid()+"&hasclass="+hasclass);
		return null;
	}

	/**
	 * 查看
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView viewNews(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView model = new ModelAndView(VIEW);
		String autoId = request.getParameter("autoId");
		Connection conn = null;
		News news = null;
        DbUtil dbUtil= null;
        WebUtil webUtil=new WebUtil(request, response);
        UserSession userSession=webUtil.getUserSession();
		try {
			conn = new DBConnect().getConnect("");
			dbUtil= new DbUtil(conn);
			NewsService ns = new NewsService(conn);
			String opt = request.getParameter("opt");
			//news = ns.getNewsByAutoId(autoId);
			news=dbUtil.load(News.class, Integer.parseInt(autoId));
			model.addObject("news", news);
			model.addObject("opt", opt);
			String sql="select name from k_user where id='"+news.getPublishUserId()+"'";  //查找用户名
			String userName=new DbUtil(conn).queryForString(sql);
			String sql1="select departmentid from k_user where id='"+news.getPublishUserId()+"'";  //查找部门id
			String departmentid=new DbUtil(conn).queryForString(sql1);
			String sql2="select departname from k_department where autoid='"+departmentid+"'";
			String departName=new DbUtil(conn).queryForString(sql2);
			String sql3="select name from k_area where autoid="+news.getArea()+"";
			String areaName=new DbUtil(conn).queryForString(sql3);
			model.addObject("userName", userName);
			model.addObject("departName", departName);
			
		    List<MenuVO> localMenus=dbUtil.select(MenuVO.class, "SELECT * FROM {0} WHERE parentid IN (SELECT menu_id FROM {0} WHERE {1}=?) and {1}=?", 10001324,news.getMenuid());
		    
			if(localMenus.size()>0){
					model.addObject("areaName",areaName);
			}else{
			    List<MenuVO> tjMenus=dbUtil.select(MenuVO.class, "SELECT * FROM {0} WHERE parentid IN (SELECT menu_id FROM {0} WHERE {1}=?) and {1}=?", 10000820,news.getMenuid());
				if(tjMenus.size()>0){
			    model.addObject("areaName","天健资讯");
				}
		   }
			
			//model.addObject("areaName", "天健".equals(news.getDept_type())?"天健":areaName);
			System.out.println(news.getAttachmentId());
			List<Attach> list = ns.getAttach(news.getAttachmentId());
			
			if(list.size()>0){
				List lists = new ArrayList();
				for (Attach attach : list) {
					Map map = new HashMap();
					// 文件路径
					String filePath = BackupUtil.getDATABASE_PATH() + "../ManuScriptData/"
					+ "news/" + attach.getMime();
					map.put("fileName",attach.getMime());
					map.put("filePath",filePath);
					map.put("unid", attach.getUnid());
					lists.add(map);
				}
				
				model.addObject("list", lists);
			}
			
			News newsVo=dbUtil.load(News.class, Integer.parseInt(autoId));
			if(newsVo.getNameId()==null)newsVo.setNameId("");
			if(!newsVo.getNameId().contains(userSession.getUserId())){
				String str=StringUtil.trim(newsVo.getNameId(),",");
				newsVo.setNameId(str+","+userSession.getUserId());
				dbUtil.update(newsVo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}
		return model;
	}

	/**
	 * 删除
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	public ModelAndView deleteNews(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		String autoId = request.getParameter("autoId");
		Connection conn = null;
        String opt = request.getParameter("opt");
        String menuid=request.getParameter("menuid");
        String hasclass=request.getParameter("hasclass");
		try {
			conn = new DBConnect().getConnect("");
			
			String[] ids = autoId.split(",");
			
			for (int i = 0; i < ids.length; i++) {
				
				NewsService ns = new NewsService(conn);
				
				// 删除附件表中对应 的记录
				String sql = "delete from asdb.k_attach where UNID = '"+ns.getNewsByAutoId(ids[i]).getAttachmentId()+"'";
				new DbUtil(conn).execute(sql);
				
				//删除记录
				ns.deleteNewsByAutoId(ids[i]);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}
		response.sendRedirect("news.do?method=list&opt="+opt+"&menuid="+menuid+"&hasclass="+hasclass);
		return null;
	}

	/**
	 * 更多
	 * 
	 * @param request
	 * @param Response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView more(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		DataGridProperty pp = new DataGridProperty() {
		};

		// 必要设置
		pp.setTableID("newsList");
		// 基本设置

		pp.setCustomerId("");

		pp.setPageSize_CH(50);

		// sql设置

		String sql = " select n.autoId,n.title,n.contents,n.updateTime,n.publishUserId,n.attachmentId,n.memo,n.property,u.name "
				+ " from oa_news n left join k_user u on n.publishUserId=u.id where 1=1 ";

		
		
		ASFuntion af = new ASFuntion();
		String title = af.showNull(request.getParameter("title"));
		String publishUserId = af.showNull(request
				.getParameter("publishUserId"));
		String updateTime = af.showNull(request.getParameter("updateTime"));


		// 查询设置
		if (!title.equals(""))
			sql = sql + " and title like '%" + title + "%'";
		if (!publishUserId.equals(""))
			sql = sql + " and publishUserId = '" + publishUserId + "'";
		if (!updateTime.equals(""))
			sql = sql + " and updateTime = '" + updateTime + "'";

		
		
		pp.setSQL(sql);
		pp.setOrderBy_CH("updateTime");
		pp.setDirection("desc");
		
		pp.setColumnWidth("15,60");
		
		pp.addColumn("时间", "updateTime");
		pp.addColumn("新闻标题", "title");
//		pp.addColumn("新闻内容", "contents");
//		pp.addColumn("发布人", "name");

		
		pp.setTrActionProperty(true);
		pp.setTrAction(" autoId='${autoId}' style='cursor:hand;'");
		
		pp.setWhichFieldIsValue(1);
		
		pp.setPrintEnable(true);
		pp.setPrintVerTical(false);
		pp.setPrintTitle("新闻列表");

		request.getSession().setAttribute(
				DataGrid.sessionPre + pp.getTableID(), pp);

		return new ModelAndView(MORE);
	}
	
	
	/**
	 * 下载文件
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView download(HttpServletRequest request, HttpServletResponse response) throws Exception{
		response.setContentType("text/html;charset=utf-8");

		java.io.BufferedInputStream bis = null;
		java.io.BufferedOutputStream bos = null;

		String filePath = request.getParameter("filePath");
		String fileName = request.getParameter("fileName");
		
		
		if(fileName==null || "".equals(fileName )){
			MyFileUpload myfileUpload = new MyFileUpload(request);
			String uploadtemppath = myfileUpload.UploadFile(null, null);
			Map parameters = myfileUpload.getMap();
			filePath = (String) parameters.get("filePath");
			fileName = (String) parameters.get("fileName");
		}


		try {
			if (!new File(filePath).exists()) {
				PrintWriter out = response.getWriter();
				out.println("<script language=javascript>");
				out.println("	window.parent.alert(\"下载文件失败，找不到对应文件，请联系管理员！\");");
				out.println("</script>");
				out.close();
				System.out.println("error:下载文件出错了,找不到对应的文件!");
				return null;
			} else {
				long fileLength = new File(filePath).length();
	
				fileName = URLEncoder.encode(fileName, "UTF-8");
				response.setContentType("application/x-msdownload");
				response.setHeader("Content-disposition","attachment; filename=" + fileName);
				response.setHeader("Content-Length", String.valueOf(fileLength));
	
				bis = new BufferedInputStream(new FileInputStream(filePath));
				bos = new BufferedOutputStream(response.getOutputStream());
				byte[] buff = new byte[2048];
				int bytesRead;
				while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
					bos.write(buff, 0, bytesRead);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (bis != null)
				bis.close();
			if (bos != null)
				bos.close();
		}


		return null;
	}
	
	
	/**
	 * 删除文件
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView deleteFile(HttpServletRequest request, HttpServletResponse response) throws Exception{

		response.setContentType("text/html;charset=utf-8");  //设置编码
		PrintWriter out = response.getWriter();
		
		String filePath = request.getParameter("filePath");
		String attachmentId = request.getParameter("attachmentId");
		Connection conn = null;
		try{
			 
			conn =  new DBConnect().getConnect("");
			// 删除附件表中对应 的记录
			String sql = "delete from asdb.k_attach where UNID = '"+attachmentId+"'";
		
			new DbUtil(conn).execute(sql);
	        
	
			File file = new File(filePath);
			
			if(file.exists()) {
				if(file.delete()){     // 删除文件
					out.print("Y");
				}else{
					out.print("N");
				}
			}else{
				out.print("noexit");
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
			out.close();
		}
		return null;
	}
	
	public ModelAndView go(HttpServletRequest request, HttpServletResponse response) throws Exception{
		System.out.println("test2.jsp");
		return new ModelAndView("test2.jsp");
	}
	
}

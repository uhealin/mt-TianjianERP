package com.matech.audit.work.oa.worknote;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.matech.audit.pub.datagrid.DataGrid;
import com.matech.audit.pub.datagrid.DataGridProperty;
import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.attachFileUploadService.AttachService;
import com.matech.audit.service.fileupload.MyFileUpload;
import com.matech.audit.service.oa.family.FamilyService;
import com.matech.audit.service.oa.family.model.Family;
import com.matech.audit.service.oa.foder.Foder;
import com.matech.audit.service.oa.specialitycompetence.specialityCompetenceService;
import com.matech.audit.service.oa.specialitycompetence.model.SpecialityCompetenceTable;
import com.matech.audit.service.oa.worknote.worknoteService;
import com.matech.audit.service.oa.worknote.model.worknoteTable;
import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;
import com.matech.framework.pub.util.Debug;

public class worknoteAction extends MultiActionController {
	private final String _strList = "oa/worknote/List.jsp";

	private final String _strListDo = "/AuditSystem/worknote.do";

	private final String _AddandEdit = "oa/worknote/AddandEdit.jsp";

	/**
	 * 跳转到人事档案缴费管理列表
	 * 
	 * @param request
	 * @param Response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView list(HttpServletRequest req, HttpServletResponse res)
			throws Exception {

		DataGridProperty pp = new DataGridProperty() {
		};

		// 必要设置
		pp.setTableID("worknote");
		// 基本设置

		pp.setCustomerId("");

		pp.setPageSize_CH(50);

		// sql设置

		String myUserid = (String) req.getSession().getAttribute("myUserid");
		String sql = " select autoid, starttime, endtime, workunit, \n"
				+ " job,  \n" + " proveman, \n" + " workcircs,  \n"
				+ " userid,  \n" + " property \n" + " from  \n"
				+ " oa_worknote where userid=" + myUserid;

		// 查询设置

		pp.setPrintEnable(true);
		pp.setPrintTitle("工作记录登记");

		pp.setSQL(sql);
		pp.setDirection_CH("autoid");

		pp.setInputType("radio");
		pp.setWhichFieldIsValue(1);

		pp.addColumn("工作单位", "workunit");
		pp.addColumn("职位", "job");
		pp.addColumn("起始时间", "starttime");
		pp.addColumn("结束时间", "endtime");
		pp.addColumn("证明人", "proveman");
		pp.addColumn("工作情况简介", "workcircs");

		req.getSession()
				.setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);

		return new ModelAndView(_strList);
	}

	/**
	 * 添加信息
	 * 
	 * @param req
	 * @param res
	 * @param wt
	 * @return
	 * @throws Exception
	 */
	public ModelAndView add(HttpServletRequest req, HttpServletResponse res) throws Exception {

		Connection conn = null;
		String myUserid = "";
		try {
			conn = new DBConnect().getConnect("");
			
			ASFuntion asf = new ASFuntion() ;
			
			MyFileUpload myfileUpload = new MyFileUpload(req);
			
			
			Foder foder  = new Foder("worknoteFoder",req);
			
			String uploadtemppath = myfileUpload.UploadFiles(false, foder.getDirPath());

			Map parameters = myfileUpload.getMap();
			worknoteTable wt = new worknoteTable() ;
			
			wt.setWorkunit((String)parameters.get("workunit")) ;
			wt.setJob((String)parameters.get("job")) ;
			wt.setProveman((String)parameters.get("proveman")) ;
			wt.setStarttime((String)parameters.get("starttime")) ;
			wt.setEndtime((String)parameters.get("endtime")) ;
			wt.setWorkcircs((String)parameters.get("workcircs")) ;
			wt.setFileNames(myfileUpload.getFileNames()) ;
			wt.setFileRondomNames((String)parameters.get("fileRondomNames")) ;
			
			worknoteService ws = new worknoteService(conn);
			myUserid = (String) req.getSession().getAttribute("myUserid");
			if (myUserid != null) {// 如果Session中有值，就取Session中的值
				wt.setUserid(myUserid);
			}

			ws.add(wt);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);

		}
		res.sendRedirect(_strListDo);
		return new ModelAndView(_strList);
	}

	/**
	 * 删除信息
	 * 
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ModelAndView del(HttpServletRequest req, HttpServletResponse res)
			throws Exception {

		ASFuntion CHF = new ASFuntion();
		Connection conn = null;
		try {
			conn = new DBConnect().getConnect("");
			String autoid = CHF.showNull(req.getParameter("autoid"));
			worknoteService ws = new worknoteService(conn);
			
			worknoteTable wt = ws.getWorknote(autoid);
			AttachService attachService = new AttachService(conn);
			attachService.remove("oa/worknoteFoder", wt.getFileRondomNames());
			
			ws.del(autoid);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);

		}

		return new ModelAndView(_strList);
	}

	/**
	 * 显示修改信息
	 * 
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ModelAndView edit(HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		ModelAndView modelAndView = new ModelAndView(_AddandEdit);
		
		
		ASFuntion CHF = new ASFuntion();
		Connection conn = null;
		String autoid = "";
		worknoteTable wt = new worknoteTable();
		try {
			conn = new DBConnect().getConnect("");
			autoid = CHF.showNull(req.getParameter("autoid"));
			if(!"".equals(autoid)){
				worknoteService ws = new worknoteService(conn);
				wt = ws.getWorknote(autoid);
				
				String fileNames = wt.getFileNames() ;
				String fileTempNames = wt.getFileRondomNames() ;

				
				String[] fileArr = fileNames.split(",") ;
				String[] fileTempArr = fileTempNames.split(",") ;
				
				modelAndView.addObject("fileArr",fileArr) ;
				modelAndView.addObject("fileTempArr",fileTempArr) ;
			}else{
				wt.setFileRondomNames(UUID.randomUUID().toString());
			}
			
			
			modelAndView.addObject("wt", wt);
			modelAndView.addObject("autoid", autoid);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);

		}
	
		return modelAndView;
	}

	/**
	 * 更新信息
	 * 
	 * @param req
	 * @param res
	 * @param wt
	 * @return
	 * @throws Exception
	 */
	public ModelAndView update(HttpServletRequest req, HttpServletResponse res) throws Exception {

		Connection conn = null;
		try {
			conn = new DBConnect().getConnect("");
			worknoteService ws = new worknoteService(conn);
			ASFuntion asf = new ASFuntion() ;
			
			MyFileUpload myfileUpload = new MyFileUpload(req);
			
			
			Foder foder  = new Foder("worknoteFoder",req);
			
			String uploadtemppath = myfileUpload.UploadFiles(false, foder.getDirPath());

			Map parameters = myfileUpload.getMap();
			worknoteTable wt = new worknoteTable();
			
			String autoId = asf.showNull(req.getParameter("autoid")) ;
			worknoteTable wtTemp = ws.getWorknote(autoId) ;
			
			String fileNames = "" ;
			String setFileRondomNames = "" ;
			if(!"".equals(wtTemp.getFileNames()) && !"".equals(wtTemp.getFileRondomNames())) {
				 fileNames = wtTemp.getFileNames()+","+myfileUpload.getFileNames() ;
				 setFileRondomNames = wtTemp.getFileRondomNames()+","+myfileUpload.getFileRondomNames() ;
			}else {
				 fileNames = myfileUpload.getFileNames() ;
				 setFileRondomNames = myfileUpload.getFileRondomNames() ;
			}
			
			wt.setAutoid(Integer.parseInt(autoId)) ;
			wt.setWorkunit((String)parameters.get("workunit")) ;
			wt.setJob((String)parameters.get("job")) ;
			wt.setProveman((String)parameters.get("proveman")) ;
			wt.setStarttime((String)parameters.get("starttime")) ;
			wt.setEndtime((String)parameters.get("endtime")) ;
			wt.setWorkcircs((String)parameters.get("workcircs")) ;
			wt.setFileNames(fileNames) ;
			wt.setFileRondomNames((String)parameters.get("fileRondomNames")) ;
			
			
			ws.update(wt);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);

		}
		res.sendRedirect(_strListDo);
		return new ModelAndView(_strList);
	}
	
	/**
	 * 下载附件
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView download(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Connection conn = null;
		try {
			response.setContentType("text/html;charset=utf-8");
			conn = new DBConnect().getConnect("");
			ASFuntion CHF = new ASFuntion();
			
			String fileName =CHF.showNull(request.getParameter("fileName_CH"));
			String autoId = CHF.showNull(request.getParameter("autoid")) ;
	
			worknoteService ws = new worknoteService(conn);
			worknoteTable wt = ws.getWorknote(autoId) ;
			
			String fileNames = wt.getFileNames() ;
			String fileTempNames = wt.getFileRondomNames() ;

			String[] fileArr = fileNames.split(",") ;
			String[] fileTempArr = fileTempNames.split(",") ;
			
			String fileTempName = "" ;
			for(int i=0;i<fileArr.length;i++) {
				if(fileName.equals(fileArr[i])) {
					fileTempName = fileTempArr[i] ;
				}
			}
	
			fileName = URLEncoder.encode(fileName, "UTF-8");
			Foder foder  = new Foder("worknoteFoder",request);
			
			String filePath = foder.getDirPath()+fileTempName;
			

			if (!new File(filePath).exists()) {
				PrintWriter out = response.getWriter();
				out.println("找不到文件，请联系管理员。<input type=\"button\" value=\"返回\" onclick=\"history.back();\" >");
				return null;
			} else {
				response.setContentType("application/x-msdownload");
				response.setHeader("Content-disposition",
						"attachment; filename=" + fileName);
				//更新下载次数
				OutputStream os = response.getOutputStream();
				BufferedInputStream bis = new BufferedInputStream(
						new FileInputStream(new File(filePath)));

				byte b[] = new byte[512];
				int len;

				while ((len = bis.read(b)) != -1) {
					os.write(b, 0, len);
				}

				os.flush();
				bis.close();
				os.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}

		return null;
	}
	
	
	public void deleteUpload(HttpServletRequest request,
			HttpServletResponse response) {
		
		Connection conn = null ;
		PreparedStatement ps = null ;
		PreparedStatement ps2 = null ;
		ResultSet rs = null ;
		PrintWriter out = null ;
		ASFuntion asf = new ASFuntion();
		String fileNames = "" ;
		String fileTempNames = "" ;
		
		try {
			 out = response.getWriter() ;
			 
			 String fileName = request.getParameter("fileName") ;
			 String autoId = asf.showNull(request.getParameter("autoId")) ;
			 
			 
			conn = new DBConnect().getConnect("") ;
			String sql = "select uploadFileName,uploadTempName \n" 
						+"from \n"
						+"oa_worknote  where autoid = '" + autoId + "' ";
			ps = conn.prepareStatement(sql) ;
			rs = ps.executeQuery() ;
			
			if(rs.next()) {
				
				 fileNames = rs.getString(1) ;
				 fileTempNames = rs.getString(2) ;
				
				String[] fileArr = fileNames.split(",") ;
				String[] fileTempArr = fileTempNames.split(",") ;
				
				
				String fileTempName = "" ;
				 fileNames = "" ;
				 fileTempNames = "" ;
				for(int i=0;i<fileArr.length;i++) {
					
					if(fileName.equals(fileArr[i])) {
						fileTempName = fileTempArr[i] ;
					}else {
						fileNames += fileArr[i] +",";
						fileTempNames += fileTempArr[i] +",";
					}
				}
				
				
				if(!"".equals(fileNames) && !"".equals(fileTempNames)) {
					fileNames = fileNames.substring(0,fileNames.length()-1) ;
					fileTempNames = fileTempNames.substring(0,fileTempNames.length()-1) ;
				}
	
				sql = "update oa_worknote set uploadFileName=?,uploadTempName=? where autoId=?" ;
				 ps2 = conn.prepareStatement(sql) ;
				 ps2.setString(1, fileNames) ;
				 ps2.setString(2, fileTempNames) ;
				 ps2.setString(3, autoId) ;
				 ps2.executeUpdate() ;
				
				fileName = URLEncoder.encode(fileName, "UTF-8");
				Foder foder  = new Foder("worknoteFoder",request);
				
				String filePath = foder.getDirPath()+fileTempName;
				

				if (!new File(filePath).exists()) {
						out.write("notFound") ;
				} else {
					if(new File(filePath).delete()) {
						out.write("suc") ;
					}else {
						out.write("fail") ;
					}
				}
				
				
			}
		
			
		}catch (Exception e) {
			out.write("fail") ;
			Debug.print(Debug.iError, "删除附件失败！", e);
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
			out.close();
		}
		
	}
	
	/**
	 * ---------------------------------------------------------------------------------------------
	 * 增加社会职务 
	 * ---------------------------------------------------------------------------------------------
	 */
	private final String societyList = "oa/society/list.jsp";
	private final String societyEdit = "oa/society/edit.jsp";
	public ModelAndView societyList(HttpServletRequest req, HttpServletResponse res) throws Exception {
		ModelAndView modelAndView = new ModelAndView(societyList);
		ASFuntion CHF = new ASFuntion() ;
		String all = CHF.showNull(req.getParameter("all"));
		
		DataGridProperty pp = new DataGridProperty(); 

		// 必要设置
		pp.setTableID("societyList");
		// 基本设置
		pp.setCustomerId("");
		pp.setPageSize_CH(50);

		// sql设置
		String myUserid = CHF.showNull(req.getParameter("userid"));
		String sql = " select a.*,b.name,c.departname " +
				"	from oa_society a " +
				"	left join k_user b on a.userid =b.id " +
				"	left join k_department c on b.departmentid =c.autoid " +
				"	where 1=1 " ;
		if(!"all".equals(all)){
			sql += "	and a.userid = '" + myUserid+"' "; 
		}
				

		// 查询设置

		pp.setPrintEnable(true);
		pp.setPrintTitle("社会职务");

		pp.setSQL(sql);
		pp.setDirection_CH("autoid");

		pp.setInputType("radio");
		pp.setWhichFieldIsValue(1);

		if("all".equals(all)){
			pp.addColumn("所属部门", "departname");
			pp.addColumn("姓名", "name");
		}
		pp.addColumn("社会职务", "postname");
		pp.addColumn("建议或提案年度", "pyear");
		pp.addColumn("建议或提案数量", "pcount");
		pp.addColumn("备注", "memo");

		req.getSession().setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);
		//modelAndView.addObject("myUserid", myUserid);
		
		return modelAndView;
	}

	public ModelAndView societyEdit(HttpServletRequest req, HttpServletResponse res)throws Exception {
		ModelAndView modelAndView = new ModelAndView(societyEdit);
		
		ASFuntion CHF = new ASFuntion();
		Connection conn = null;
		String autoid = "",userid="";
		try {
			
			autoid = CHF.showNull(req.getParameter("autoid"));
			userid = CHF.showNull(req.getParameter("userid"));
			Map map = new HashMap();
			if(!"".equals(autoid)){
				conn = new DBConnect().getConnect("");
				
				DbUtil db = new DbUtil(conn);
				map = db.get("oa_society", "autoid", autoid);
			}else{
				map.put("userid", userid);
			}
			modelAndView.addObject("society", map);
			modelAndView.addObject("autoid", autoid);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);

		}
	
		return modelAndView;
	}
	
	public void societySave(HttpServletRequest req, HttpServletResponse res)throws Exception {
		ASFuntion CHF = new ASFuntion();
		Connection conn = null;
		try {
			
			UserSession userSession = (UserSession) req.getSession().getAttribute("userSession");
			
			Map parameters = new HashMap();
			Enumeration enum1 = req.getParameterNames();
			while (enum1.hasMoreElements()) {
				String paramName = (String) enum1.nextElement();
				String [] paramValue = req.getParameterValues(paramName);
				if(paramValue.length == 1 ){
					parameters.put(paramName, paramValue[0]);	
				}else{
					parameters.put(paramName, paramValue);
				}
			}
			
			String flag = CHF.showNull((String)parameters.get("flag"));
			
			String autoid = CHF.showNull((String)parameters.get("autoid"));
			String userid = CHF.showNull((String)parameters.get("userid"));
			if("".equals(userid)){
				parameters.put("userid", userSession.getUserId());
			}
			conn = new DBConnect().getConnect("");
			DbUtil db = new DbUtil(conn);
			if("del".equals(flag)){
				//删除
				db.del("oa_society", "autoid", autoid);
			}else{
				if(!"".equals(autoid)){
					//修改
					db.update("oa_society", "autoid", parameters);
				}else{
					//新增
					db.add("oa_society", "autoid", parameters);
				}
			}
			res.sendRedirect(req.getContextPath() + "/worknote.do?method=societyList&userid="+userid);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);

		}
	
	}
	
}
